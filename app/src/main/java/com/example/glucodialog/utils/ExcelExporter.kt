package com.example.glucodialog.utils

import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.example.glucodialog.data.AppDatabase
import jxl.Workbook
import jxl.write.Label
import jxl.write.Number
import jxl.write.WritableWorkbook
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

        if (!fileDir.exists()) {
            val created = fileDir.mkdirs()
            if (!created) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Ошибка: не удалось создать директорию для сохранения файла.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                return@withContext
            }
        }

        val file = File(fileDir, fileName)

        try {
            val workbook: WritableWorkbook = Workbook.createWorkbook(file)

            // === 1. Глюкоза ===
            val glucoseSheet = workbook.createSheet("Глюкоза", 0)
            glucoseSheet.addCell(Label(0, 0, "Уровень"))
            glucoseSheet.addCell(Label(1, 0, "Единица измерения"))
            glucoseSheet.addCell(Label(2, 0, "Дата и время"))

            val glucoseEntries = db.glucoseDao().getAllGlucoseEntriesOnce()
            glucoseEntries.forEachIndexed { i, entry ->
                glucoseSheet.addCell(Number(0, i + 1, entry.glucoseLevel))
                glucoseSheet.addCell(Label(1, i + 1, entry.unit))
                glucoseSheet.addCell(Label(2, i + 1, dateFormat.format(Date(entry.timestamp))))
            }

            // === 2. Инсулин ===
            val insulinSheet = workbook.createSheet("Инсулин", 1)
            insulinSheet.addCell(Label(0, 0, "Тип инсулина"))
            insulinSheet.addCell(Label(1, 0, "Доза"))
            insulinSheet.addCell(Label(2, 0, "Единица"))
            insulinSheet.addCell(Label(3, 0, "Дата и время"))

            val insulinEntries = db.insulinDao().getAllInsulinEntriesOnceWithTypes()
            insulinEntries.forEachIndexed { i, entry ->
                insulinSheet.addCell(Label(0, i + 1, entry.type.name))
                insulinSheet.addCell(Number(1, i + 1, entry.entry.doseUnits))
                insulinSheet.addCell(Label(2, i + 1, entry.entry.unit))
                insulinSheet.addCell(Label(3, i + 1, dateFormat.format(Date(entry.entry.timestamp))))
            }

            // === 3. Лекарства ===
            val medsSheet = workbook.createSheet("Лекарства", 2)
            medsSheet.addCell(Label(0, 0, "Препарат"))
            medsSheet.addCell(Label(1, 0, "Единица измерения"))
            medsSheet.addCell(Label(2, 0, "Доза"))
            medsSheet.addCell(Label(3, 0, "Дата и время"))

            val medsEntries = db.medicationDao().getAllMedicationEntriesOnceWithTypes()
            medsEntries.forEachIndexed { i, entry ->
                medsSheet.addCell(Label(0, i + 1, entry.type.name))
                medsSheet.addCell(Label(1, i + 1, entry.entry.unit))
                medsSheet.addCell(Label(2, i + 1, entry.entry.dose))
                medsSheet.addCell(Label(3, i + 1, dateFormat.format(Date(entry.entry.timestamp))))
            }

            // === 4. Активность ===
            val activitySheet = workbook.createSheet("Активность", 3)
            activitySheet.addCell(Label(0, 0, "Тип"))
            activitySheet.addCell(Label(1, 0, "Длительность (мин)"))
            activitySheet.addCell(Label(2, 0, "Дата и время"))

            val activityEntries = db.activityDao().getAllActivityEntriesOnceWithTypes()
            activityEntries.forEachIndexed { i, entry ->
                activitySheet.addCell(Label(0, i + 1, entry.type.name))
                activitySheet.addCell(Number(1, i + 1, entry.entry.durationMinutes.toDouble()))
                activitySheet.addCell(Label(2, i + 1, dateFormat.format(Date(entry.entry.timestamp))))
            }

            // === 5. Питание ===
            val foodSheet = workbook.createSheet("Питание", 4)
            foodSheet.addCell(Label(0, 0, "Продукт"))
            foodSheet.addCell(Label(1, 0, "Единица измерения"))
            foodSheet.addCell(Label(2, 0, "Углеводы"))
            foodSheet.addCell(Label(3, 0, "Дата и время"))

            val foodEntries = db.foodDao().getAllFoodEntriesOnceWithItems()
            foodEntries.forEachIndexed { i, entry ->
                foodSheet.addCell(Label(0, i + 1, entry.foodItem.name))
                foodSheet.addCell(Label(1, i + 1, entry.entry.unit))
                foodSheet.addCell(Number(2, i + 1, entry.foodItem.carbs))
                foodSheet.addCell(Label(3, i + 1, dateFormat.format(Date(entry.entry.timestamp))))
            }

            workbook.write()
            workbook.close()

            // === Обновление MediaStore ===
            MediaScannerConnection.scanFile(
                context,
                arrayOf(file.absolutePath),
                null
            ) { path, uri ->
                Log.d("Scan", "Файл доступен: $path - $uri")
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Файл экспортирован в:\n${file.absolutePath}", Toast.LENGTH_LONG).show()
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Ошибка экспорта: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
