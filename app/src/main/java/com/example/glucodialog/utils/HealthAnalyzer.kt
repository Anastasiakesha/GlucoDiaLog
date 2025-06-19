package com.example.glucodialog.utils

import android.content.Context
import com.example.glucodialog.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.*

object HealthAnalyzer {

    suspend fun analyzeGlucoseEntry(
        context: Context,
        db: AppDatabase,
        newEntry: GlucoseEntry
    ) = withContext(Dispatchers.IO) {

        val glucoseDao = db.glucoseDao()
        val insulinDao = db.insulinDao()
        val foodDao = db.foodDao()
        val activityDao = db.activityDao()

        val twoHoursMillis = 2 * 60 * 60 * 1000L
        val threeHoursMillis = 3 * 60 * 60 * 1000L

        val nowTimestamp = newEntry.timestamp

        // Получаем предыдущую запись глюкозы до текущей
        val prevEntry = glucoseDao.getGlucoseEntriesBetween(0L, nowTimestamp - 1)
            .maxByOrNull { it.timestamp }

        // Инсулин за 2–3 часа до текущей записи
        val insulinBefore = insulinDao.getInsulinEntriesBetween(
            nowTimestamp - threeHoursMillis,
            nowTimestamp - twoHoursMillis
        )

        // Еда за 2–3 часа до текущей записи
        val foodBefore = foodDao.getFoodEntriesBetween(
            nowTimestamp - threeHoursMillis,
            nowTimestamp - twoHoursMillis
        )

        // Активность за последние 2 часа
        val activityBefore = activityDao.getActivitiesBetween(
            nowTimestamp - twoHoursMillis,
            nowTimestamp
        )

        val warnings = mutableListOf<String>()

        // Анализ повышения глюкозы при наличии еды и инсулина
        if (insulinBefore.isNotEmpty() && foodBefore.isNotEmpty() && prevEntry != null) {
            if (newEntry.glucoseLevel - prevEntry.glucoseLevel >= 2.0) {
                warnings.add("Возможна недостаточная доза инсулина")
            }
        }

        // Анализ снижения глюкозы после инсулина
        if (insulinBefore.isNotEmpty() && prevEntry != null) {
            if (prevEntry.glucoseLevel - newEntry.glucoseLevel >= 2.0) {
                warnings.add("Риск гипогликемии после введения инсулина")
            }
        }

        // Анализ снижения глюкозы после физической активности
        if (activityBefore.isNotEmpty() && prevEntry != null) {
            if (prevEntry.glucoseLevel - newEntry.glucoseLevel >= 3.0) {
                warnings.add("Рекомендуется приём быстрых углеводов после физической активности")
            }
        }

        // Сохраняем предупреждение в note и отображаем уведомление
        if (warnings.isNotEmpty()) {
            val message = warnings.joinToString("\n")
            glucoseDao.updateNoteForEntry(newEntry.id, message)
            Notifier.showNotification(context, "Анализ показателей", message)
        }
    }

    suspend fun checkMedicationCompliance(context: Context, db: AppDatabase) = withContext(Dispatchers.IO) {
        val medicationDao = db.medicationDao()
        val userProfileDao = db.userProfileDao()

        val userProfile = userProfileDao.getUserProfile().firstOrNull() ?: return@withContext

        val now = Calendar.getInstance()
        val todayStart = now.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val nowMillis = System.currentTimeMillis()

        // Получаем записи лекарства за сегодня
        val todayEntries = medicationDao.getMedicationEntriesBetween(todayStart, nowMillis)

        val warnings = mutableListOf<String>()

        if (todayEntries.isEmpty()) {
            warnings.add("Сегодня не зафиксирован прием лекарства.")
        } else {
            val totalDose = todayEntries.sumOf { it.dose.toDoubleOrNull() ?: 0.0 }
            if (totalDose < userProfile.medicationDose) {
                warnings.add("Принятая доза лекарства сегодня меньше рекомендованной: $totalDose из ${userProfile.medicationDose}")
            }
        }

        if (warnings.isNotEmpty()) {
            val message = warnings.joinToString("\n")
            Notifier.showNotification(context, "Прием лекарства", message)
        }
    }
}
