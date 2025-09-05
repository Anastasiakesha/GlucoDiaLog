package com.example.glucodialog.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import com.example.glucodialog.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PdfExporter {

    private val typeColors = mapOf(
        "Глюкоза" to Color.parseColor("#BBDEFB"),    // пастельный голубой
        "Питание" to Color.parseColor("#FFF9C4"),    // пастельный желтый
        "Инсулин" to Color.parseColor("#C8E6C9"),    // пастельный зеленый
        "Активность" to Color.parseColor("#FFCCBC"), // пастельный оранжевый
        "Лекарство" to Color.parseColor("#D1C4E9")   // пастельный фиолетовый
    )

    suspend fun exportAllData(context: Context, db: AppDatabase) = withContext(Dispatchers.IO) {
        val dateTimeFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        val dateOnlyFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val fileName = "glucodialog_export_${System.currentTimeMillis()}.pdf"
        val fileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        if (!fileDir.exists() && !fileDir.mkdirs()) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Не удалось создать папку для сохранения.", Toast.LENGTH_LONG).show()
            }
            return@withContext
        }

        val file = File(fileDir, fileName)
        val pdfDocument = PdfDocument()
        val defaultPaint = Paint().apply { color = Color.BLACK; textSize = 12f }
        val headerPaint = Paint().apply { color = Color.BLACK; textSize = 16f; isFakeBoldText = true }
        val backgroundPaint = Paint()

        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
        var page = pdfDocument.startPage(pageInfo)
        var canvas: Canvas = page.canvas
        var yPosition = 50f
        val rowHeight = 25f
        val xStart = 20f

        try {
            fun checkPageSpace(): Canvas {
                if (yPosition + rowHeight > pageInfo.pageHeight - 50) {
                    pdfDocument.finishPage(page)
                    page = pdfDocument.startPage(pageInfo)
                    yPosition = 50f
                    canvas = page.canvas
                }
                return canvas
            }

            fun drawRow(row: List<String>, type: String? = null, isHeader: Boolean = false) {
                canvas = checkPageSpace()
                var x = xStart

                // Фон для строки
                if (!isHeader && type != null) {
                    backgroundPaint.color = typeColors[type] ?: Color.WHITE
                    canvas.drawRect(xStart - 5f, yPosition - 15f, xStart + row.size * 100f, yPosition + 5f, backgroundPaint)
                }

                // Текст
                val paintToUse = if (isHeader) headerPaint else defaultPaint
                row.forEach { cell ->
                    canvas.drawText(cell, x, yPosition, paintToUse)
                    x += 100f
                }

                yPosition += rowHeight
            }

            // Заголовки колонок
            val headers = listOf(
                "Тип записи", "Дата", "Название",
                "Кол-во", "Ед", "Предупреждения",
                "Углеводы", "Калории", "Белки", "Жиры"
            )
            drawRow(headers, isHeader = true)

            // === Сбор всех записей с датой ===
            val allEntries = mutableListOf<Triple<Long, List<String>, String>>() // Triple<timestamp, row, type>

            db.glucoseDao().getAllGlucoseEntriesOnce().forEach {
                allEntries.add(Triple(it.timestamp, listOf("Глюкоза", dateTimeFormat.format(Date(it.timestamp)), "", it.glucoseLevel.toString(), it.unit, it.note.toString(), "", "", "", ""), "Глюкоза"))
            }
            db.insulinDao().getAllInsulinEntriesOnceWithTypes().forEach {
                allEntries.add(Triple(it.entry.timestamp, listOf("Инсулин", dateTimeFormat.format(Date(it.entry.timestamp)), it.type.name, it.entry.doseUnits.toString(), it.entry.unit, "", "", "", "", ""), "Инсулин"))
            }
            db.medicationDao().getAllMedicationEntriesOnceWithTypes().forEach {
                allEntries.add(Triple(it.entry.timestamp, listOf("Лекарство", dateTimeFormat.format(Date(it.entry.timestamp)), it.type.name, it.entry.dose, it.entry.unit, "", "", "", "", ""), "Лекарство"))
            }
            db.activityDao().getAllActivityEntriesOnceWithTypes().forEach {
                allEntries.add(Triple(it.entry.timestamp, listOf("Активность", dateTimeFormat.format(Date(it.entry.timestamp)), it.type.name, it.entry.durationMinutes.toString(), "мин", "", "", "", "", ""), "Активность"))
            }
            db.foodDao().getAllFoodEntriesOnceWithItems().forEach {
                allEntries.add(Triple(it.entry.timestamp, listOf("Питание", dateTimeFormat.format(Date(it.entry.timestamp)), it.foodItem.name, it.entry.quantity.toString(), it.entry.unit, "", it.foodItem.carbs.toString(), it.foodItem.calories.toString(), it.foodItem.proteins.toString(), it.foodItem.fats.toString()), "Питание"))
            }

            // Сортировка по дате (новые сверху)
            allEntries.sortByDescending { it.first }

            // Рисуем записи с разделением по дате
            var currentDate = ""
            allEntries.forEach { (timestamp, row, type) ->
                val rowDate = dateOnlyFormat.format(Date(timestamp))
                if (rowDate != currentDate) {
                    currentDate = rowDate
                    drawRow(listOf("=== $currentDate ==="), isHeader = true)
                }
                drawRow(row, type)
            }

            pdfDocument.finishPage(page)

            FileOutputStream(file).use { output ->
                pdfDocument.writeTo(output)
            }
            pdfDocument.close()

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Файл экспортирован:\n${file.absolutePath}", Toast.LENGTH_LONG).show()
            }

        } catch (e: Exception) {
            pdfDocument.close()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Ошибка экспорта: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }
}