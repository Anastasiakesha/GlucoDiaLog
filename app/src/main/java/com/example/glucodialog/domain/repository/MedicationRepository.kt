package com.example.glucodialog.domain.repository

import com.example.glucodialog.domain.model.MedicationEntry
import com.example.glucodialog.domain.model.MedicationEntryWithTypeDomain
import com.example.glucodialog.domain.model.MedicationType
import kotlinx.coroutines.flow.Flow

interface MedicationRepository {
    fun getAllMedicationTypes(): Flow<List<MedicationType>>
    suspend fun insertAllMedicationTypes(types: List<MedicationType>)
    suspend fun insertMedicationEntry(entry: MedicationEntry)
    fun getAllMedicationEntries(): Flow<List<MedicationEntry>>
    suspend fun getMedicationById(id: Int): MedicationType?
    suspend fun getAllMedicationEntriesOnce(): List<MedicationEntry>
    suspend fun insertMedicationType(type: MedicationType): Long
    fun getAllMedicationEntriesWithTypesFlow(): Flow<List<MedicationEntryWithTypeDomain>>
    suspend fun getAllMedicationEntriesOnceWithTypes(): List<MedicationEntryWithTypeDomain>
    suspend fun getMedicationTypeByName(name: String): MedicationType?
    suspend fun getMedicationEntriesBetween(startTimestamp: Long, endTimestamp: Long): List<MedicationEntry>
    suspend fun updateMedicationEntry(entry: MedicationEntry)
    suspend fun deleteMedicationEntry(entry: MedicationEntry)
}