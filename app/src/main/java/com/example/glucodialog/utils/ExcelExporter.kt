package com.example.glucodialog.utils

import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.example.glucodialog.data.AppDatabase
import jxl.Workbook
import jxl.write.Label
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object ExcelExporter {

    suspend fun exportAllData(context: Context, db: AppDatabase) = withContext(Dispatchers.IO) {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        val fileName = "glucodialog_export_${System.currentTimeMillis()}.xls"
        val fileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        if (!fileDir.exists() && !fileDir.mkdirs()) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Не удалось создать папку для сохранения.", Toast.LENGTH_LONG).show()
            }
            return@withContext
        }

        val file = File(fileDir, fileName)

        try {
            val allEntries = mutableListOf<List<String>>()

            // === Глюкоза ===
            db.glucoseDao().getAllGlucoseEntriesOnce().forEach {
                allEntries.add(
                    listOf(
                        "Глюкоза",
                        dateFormat.format(Date(it.timestamp)),
                        "",
                        it.glucoseLevel.toString(),
                        it.unit,
                        it.note.toString()
                    )
                )
            }

            // === Инсулин ===
            db.insulinDao().getAllInsulinEntriesOnceWithTypes().forEach {
                allEntries.add(
                    listOf(
                        "Инсулин",
                        dateFormat.format(Date(it.entry.timestamp)),
                        it.type.name,
                        it.entry.doseUnits.toString(),
                        it.entry.unit
                    )
                )
            }

            // === Лекарства ===
            db.medicationDao().getAllMedicationEntriesOnceWithTypes().forEach {
                allEntries.add(
                    listOf(
                        "Лекарство",
                        dateFormat.format(Date(it.entry.timestamp)),
                        it.type.name,
                        it.entry.dose,
                        it.entry.unit
                    )
                )
            }

            // === Активность ===
            db.activityDao().getAllActivityEntriesOnceWithTypes().forEach {
                allEntries.add(
                    listOf(
                        "Активность",
                        dateFormat.format(Date(it.entry.timestamp)),
                        it.type.name,
                        it.entry.durationMinutes.toString(),
                        "мин"
                    )
                )
            }

            // === Питание ===
            db.foodDao().getAllFoodEntriesOnceWithItems().forEach {
                allEntries.add(
                    listOf(
                        "Питание",
                        dateFormat.format(Date(it.entry.timestamp)),
                        it.foodItem.name,
                        it.entry.quantity.toString(),
                        it.entry.unit,
                        "",
                        it.foodItem.carbs.toString(),
                        it.foodItem.calories.toString(),
                        it.foodItem.proteins.toString(),
                        it.foodItem.fats.toString()
                    )
                )
            }

            // Сортировка от нового к старому
            allEntries.sortByDescending {
                try {
                    dateFormat.parse(it[1])?.time ?: 0L
                } catch (e: Exception) {
                    0L
                }
            }

            val workbook = Workbook.createWorkbook(file)
            val sheet = workbook.createSheet("Данные", 0)

            // Заголовки
            val headers = listOf(
                "Тип записи",        // 0
                "Дата и время",      // 1
                "Название / Тип",    // 2
                "Кол-во / Доза",     // 3
                "Единицы измерения",  // 4
                "Предупреждения",
                "Углеводы",
                "Калории",
                "Белки",
                "Жиры"

            )

            headers.forEachIndexed { col, name ->
                sheet.addCell(Label(col, 0, name))
            }

            // Данные
            allEntries.forEachIndexed { rowIndex, row ->
                row.forEachIndexed { colIndex, value ->
                    sheet.addCell(Label(colIndex, rowIndex + 1, value))
                }
            }

            workbook.write()
            workbook.close()

            MediaScannerConnection.scanFile(
                context,
                arrayOf(file.absolutePath),
                null
            ) { path, uri ->
                Log.d("Scan", "Файл доступен: $path - $uri")
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Файл экспортирован:\n${file.absolutePath}", Toast.LENGTH_LONG).show()
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Ошибка экспорта: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
