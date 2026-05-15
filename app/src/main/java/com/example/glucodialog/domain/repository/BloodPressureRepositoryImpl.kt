package com.example.glucodialog.domain.repository

import com.example.glucodialog.data.repository.BloodPressureDao
import com.example.glucodialog.domain.mappers.toDomain
import com.example.glucodialog.domain.mappers.toLocal
import com.example.glucodialog.domain.model.BloodPressureEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BloodPressureRepositoryImpl(private val dao: BloodPressureDao) : BloodPressureRepository {
    override suspend fun insertBloodPressureEntry(entry: BloodPressureEntry) = dao.insertBloodPressureEntry(entry.toLocal())

    override suspend fun getBloodPressureEntryById(id: Int): BloodPressureEntry? =
        dao.getBloodPressureEntryById(id)?.toDomain()

    override suspend fun updateBloodPressureEntry(entry: BloodPressureEntry) =
        dao.updateBloodPressureEntry(entry.toLocal())

    override fun getAllEntries(): Flow<List<BloodPressureEntry>> = dao.getAllEntries().map { list -> list.map { it.toDomain() } }

    override suspend fun deleteBloodPressureEntry(entry: BloodPressureEntry) = dao.deleteBloodPressureEntry(entry.toLocal())
}