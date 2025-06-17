package com.example.glucodialog.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.glucodialog.data.*
import jxl.Workbook
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

object ExcelImporter {

    private const val TAG = "ExcelImporter"

    suspend fun importFromExcel(context: Context, fileUri: Uri?, db: AppDatabase) =
        withContext(Dispatchers.IO) {
            if (fileUri == null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Файл не выбран", Toast.LENGTH_LONG).show()
                }
                Log.d(TAG, "Файл не выбран (fileUri == null)")
                return@withContext
            }

            val mime = context.contentResolver.getType(fileUri)
            if (mime != "application/vnd.ms-excel" && !fileUri.toString().endsWith(".xls")) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Поддерживаются только файлы .xls", Toast.LENGTH_LONG).show()
                }
                Log.d(TAG, "Неподдерживаемый MIME тип: $mime")
                return@withContext
            }

            try {
                val inputStream = context.contentResolver.openInputStream(fileUri)
                    ?: throw IllegalArgumentException("Не удалось открыть файл")

                inputStream.use {
                    val workbook = Workbook.getWorkbook(it)
                    val sheet = workbook.getSheet(0)

                    if (sheet.rows <= 1) {
                        throw IllegalArgumentException("Файл пуст или не содержит данных")
                    }

                    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

                    for (i in 1 until sheet.rows) {
                        val row = sheet.getRow(i)
                        if (row.size < 5) {
                            Log.d(TAG, "Строка $i пропущена: менее 5 ячеек")
                            continue
                        }

                        val type = row[0].contents.trim()
                        val timestampStr = row[1].contents.trim()
                        val label = row.getOrNull(2)?.contents?.trim() ?: ""
                        val valueStr = row.getOrNull(3)?.contents?.trim() ?: ""
                        val unit = row.getOrNull(4)?.contents?.trim() ?: ""
                        val note = row.getOrNull(5)?.contents?.trim() ?: ""

                        val timestamp = try {
                            val parsed = dateFormat.parse(timestampStr)
                            if (parsed == null) {
                                Log.d(TAG, "Строка $i пропущена: неверный формат даты '$timestampStr'")
                                continue
                            } else {
                                parsed.time
                            }
                        } catch (e: Exception) {
                            Log.d(TAG, "Строка $i пропущена: ошибка парсинга даты '$timestampStr' - ${e.localizedMessage}")
                            continue
                        }


                        when (type) {
                            "Глюкоза" -> {
                                val glucose = valueStr.toDoubleOrNull()
                                if (glucose == null) {
                                    Log.d(TAG, "Строка $i пропущена: неверное число для Глюкозы '$valueStr'")
                                    continue
                                }
                                db.glucoseDao().insertGlucoseEntry(
                                    GlucoseEntry(
                                        timestamp = timestamp,
                                        glucoseLevel = glucose,
                                        unit = unit,
                                        note = note
                                    )
                                )
                                Log.d(TAG, "Строка $i импортирована: Глюкоза")
                            }

                            "Инсулин" -> {
                                val dose = valueStr.toDoubleOrNull()
                                if (dose == null) {
                                    Log.d(TAG, "Строка $i пропущена: неверное число для Инсулина '$valueStr'")
                                    continue
                                }
                                val insulinType = db.insulinDao().getInsulinTypeByName(label)
                                if (insulinType == null) {
                                    Log.d(TAG, "Строка $i пропущена: тип Инсулина '$label' не найден")
                                    continue
                                }
                                db.insulinDao().insertInsulinEntry(
                                    InsulinEntry(
                                        timestamp = timestamp,
                                        doseUnits = dose,
                                        unit = unit,
                                        insulinTypeId = insulinType.id
                                    )
                                )
                                Log.d(TAG, "Строка $i импортирована: Инсулин")
                            }

                            "Лекарство" -> {
                                val medicationType = db.medicationDao().getMedicationTypeByName(label)
                                if (medicationType == null) {
                                    Log.d(TAG, "Строка $i пропущена: тип Лекарства '$label' не найден")
                                    continue
                                }
                                db.medicationDao().insertMedicationEntry(
                                    MedicationEntry(
                                        timestamp = timestamp,
                                        dose = valueStr,
                                        unit = unit,
                                        medicationTypeId = medicationType.id
                                    )
                                )
                                Log.d(TAG, "Строка $i импортирована: Лекарство")
                            }

                            "Активность" -> {
                                val duration = valueStr.toIntOrNull()
                                if (duration == null) {
                                    Log.d(TAG, "Строка $i пропущена: неверное число для Активности '$valueStr'")
                                    continue
                                }
                                val activityType = db.activityDao().getActivityTypeByName(label)
                                if (activityType == null) {
                                    Log.d(TAG, "Строка $i пропущена: тип Активности '$label' не найден")
                                    continue
                                }
                                db.activityDao().insertActivityEntry(
                                    ActivityEntry(
                                        timestamp = timestamp,
                                        durationMinutes = duration,
                                        activityTypeId = activityType.id
                                    )
                                )
                                Log.d(TAG, "Строка $i импортирована: Активность")
                            }

                            "Питание" -> {
                                val quantity = valueStr.toDoubleOrNull()
                                if (quantity == null) {
                                    Log.d(TAG, "Строка $i пропущена: неверное число для Питания '$valueStr'")
                                    continue
                                }
                                val foodItem = db.foodDao().getFoodItemByName(label)
                                if (foodItem == null) {
                                    Log.d(TAG, "Строка $i пропущена: тип Питания '$label' не найден")
                                    continue
                                }
                                db.foodDao().insertFoodEntry(
                                    FoodEntry(
                                        timestamp = timestamp,
                                        quantity = quantity,
                                        unit = unit,
                                        foodItemId = foodItem.id
                                    )
                                )
                                Log.d(TAG, "Строка $i импортирована: Питание")
                            }

                            else -> {
                                Log.d(TAG, "Строка $i пропущена: неизвестный тип '$type'")
                            }
                        }
                    }

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Импорт завершён успешно", Toast.LENGTH_LONG).show()
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Ошибка импорта: ${e.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                Log.e(TAG, "Ошибка импорта", e)
            }
        }
}
