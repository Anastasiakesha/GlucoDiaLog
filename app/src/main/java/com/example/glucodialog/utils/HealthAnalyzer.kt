package com.example.glucodialog.utils

import android.content.Context
import com.example.glucodialog.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.firstOrNull
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
        val medicationDao = db.medicationDao()
        val userProfileDao = db.userProfileDao()

        val twoHoursMillis = 2 * 60 * 60 * 1000L
        val threeHoursMillis = 3 * 60 * 60 * 1000L

        val oldEntries = glucoseDao.getAllGlucoseEntriesOnce().filter { it.timestamp < newEntry.timestamp }

        val prevEntry = oldEntries.maxByOrNull { it.timestamp }

        val insulinBefore = insulinDao.getAllInsulinEntriesOnce()
            .filter { it.timestamp in (newEntry.timestamp - threeHoursMillis)..(newEntry.timestamp - twoHoursMillis) }

        val foodBefore = foodDao.getAllFoodEntriesOnce()
            .filter { it.timestamp in (newEntry.timestamp - threeHoursMillis)..(newEntry.timestamp - twoHoursMillis) }

        val activityBefore = activityDao.getAllActivityEntriesOnce()
            .filter { it.timestamp in (newEntry.timestamp - twoHoursMillis)..newEntry.timestamp }

        val warnings = mutableListOf<String>()

        // Анализ изменения глюкозы относительно еды и инсулина
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

        // Сохраняем предупреждения в note (комментарии) глюкозы
        if (warnings.isNotEmpty()) {
            val message = warnings.joinToString("\n")
            val updatedEntry = newEntry.copy(note = message)
            glucoseDao.updateNoteForEntry(updatedEntry.id, message)

            // Показываем уведомления
            Notifier.showNotification(context, "Анализ показателей", message)
        }
    }

    suspend fun checkMedicationCompliance(context: Context, db: AppDatabase) = withContext(Dispatchers.IO) {
        val medicationDao = db.medicationDao()
        val userProfileDao = db.userProfileDao()

        val userProfile = userProfileDao.getUserProfile().firstOrNull() ?: return@withContext

        val medicationTypes = medicationDao.getAllMedicationTypes().firstOrNull() ?: emptyList()
        val medicationTypeMap = medicationTypes.associateBy { it.id }

        val now = Calendar.getInstance()
        val todayStart = now.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val todayEntries = medicationDao.getAllMedicationEntriesOnce()
            .filter { it.timestamp >= todayStart }

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
