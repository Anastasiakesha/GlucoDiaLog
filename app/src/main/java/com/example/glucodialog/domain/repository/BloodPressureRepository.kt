package com.example.glucodialog.domain.repository

import com.example.glucodialog.domain.model.BloodPressureEntry
import kotlinx.coroutines.flow.Flow

interface BloodPressureRepository {
    suspend fun insertBloodPressureEntry(entry: BloodPressureEntry)
    fun getAllEntries(): Flow<List<BloodPressureEntry>>
    suspend fun deleteBloodPressureEntry(entry: BloodPressureEntry)
}