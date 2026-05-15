package com.example.glucodialog.domain.repository

import com.example.glucodialog.domain.model.GlucoseEntry
import com.example.glucodialog.domain.model.MedicationEntry
import kotlinx.coroutines.flow.Flow

interface GlucoseRepository {
    suspend fun insertGlucoseEntry(entry: GlucoseEntry)

    suspend fun getGlucoseEntryById(id: Int): GlucoseEntry?
    fun getAllGlucoseEntries(): Flow<List<GlucoseEntry>>
    suspend fun getAllGlucoseEntriesOnce(): List<GlucoseEntry>
    suspend fun updateGlucoseEntry(entry: GlucoseEntry)
    suspend fun updateNoteForEntry(entryId: Int, note: String)
    suspend fun deleteGlucoseEntry(entry: GlucoseEntry)
    suspend fun getGlucoseEntriesBetween(startTimestamp: Long, endTimestamp: Long): List<GlucoseEntry>
}