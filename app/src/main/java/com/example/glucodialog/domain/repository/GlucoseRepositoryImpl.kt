package com.example.glucodialog.domain.repository

import com.example.glucodialog.data.repository.GlucoseDao
import com.example.glucodialog.domain.mappers.toDomain
import com.example.glucodialog.domain.mappers.toLocal
import com.example.glucodialog.domain.model.GlucoseEntry
import com.example.glucodialog.domain.model.MedicationEntry
import com.example.glucodialog.domain.repository.GlucoseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class GlucoseRepositoryImpl(
    private val dao: GlucoseDao
) : GlucoseRepository {

    override suspend fun insertGlucoseEntry(entry: GlucoseEntry) {
        dao.insertGlucoseEntry(entry.toLocal())
    }

    override suspend fun getGlucoseEntryById(id: Int): GlucoseEntry? =
        dao.getGlucoseEntryById(id)?.toDomain()

    override fun getAllGlucoseEntries(): Flow<List<GlucoseEntry>> {
        return dao.getAllGlucoseEntries().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getAllGlucoseEntriesOnce(): List<GlucoseEntry> {
        return dao.getAllGlucoseEntriesOnce().map { it.toDomain() }
    }

    override suspend fun updateGlucoseEntry(entry: GlucoseEntry) {
        dao.updateGlucoseEntry(entry.toLocal())
    }

    override suspend fun updateNoteForEntry(entryId: Int, note: String) {
        dao.updateNoteForEntry(entryId, note)
    }

    override suspend fun deleteGlucoseEntry(entry: GlucoseEntry) {
        dao.deleteGlucoseEntry(entry.toLocal())
    }

    override suspend fun getGlucoseEntriesBetween(
        startTimestamp: Long,
        endTimestamp: Long
    ): List<GlucoseEntry> {
        return dao.getGlucoseEntriesBetween(startTimestamp, endTimestamp).map { it.toDomain() }
    }
}