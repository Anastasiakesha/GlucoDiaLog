package com.example.glucodialog.domain.repository


import com.example.glucodialog.data.repository.InsulinDao
import com.example.glucodialog.domain.mappers.toDomain
import com.example.glucodialog.domain.mappers.toLocal
import com.example.glucodialog.domain.model.InsulinEntry
import com.example.glucodialog.domain.model.InsulinType
import com.example.glucodialog.domain.model.InsulinEntryWithTypeDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class InsulinRepositoryImpl(
    private val dao: InsulinDao
) : InsulinRepository {

    override fun getAllInsulinTypes(): Flow<List<InsulinType>> =
        dao.getAllInsulinTypes().map { it.map { it.toDomain() } }

    override suspend fun getInsulinEntryById(id: Int): InsulinEntry? =
        dao.getInsulinEntryById(id)?.toDomain()

    override suspend fun insertInsulinType(type: InsulinType): Long {
        return dao.insertInsulinType(type.toLocal())
    }

    override suspend fun insertAllInsulinTypes(types: List<InsulinType>) {
        dao.insertAllInsulinTypes(types.map { it.toLocal() })
    }

    override suspend fun insertInsulinEntry(entry: InsulinEntry) {
        dao.insertInsulinEntry(entry.toLocal())
    }

    override fun getAllInsulinEntries(): Flow<List<InsulinEntry>> =
        dao.getAllInsulinEntries().map { it.map { it.toDomain() } }

    override suspend fun getAllInsulinEntriesOnce(): List<InsulinEntry> =
        dao.getAllInsulinEntriesOnce().map { it.toDomain() }

    override suspend fun getAllInsulinEntriesWithTypesOnce(): List<InsulinEntryWithTypeDomain> =
        dao.getAllInsulinEntriesOnceWithTypes().map { it.toDomain() }

    override fun getAllInsulinEntriesWithTypesFlow(): Flow<List<InsulinEntryWithTypeDomain>> =
        dao.getAllInsulinEntriesWithTypesFlow().map { it.map { it.toDomain() } }

    override suspend fun getInsulinTypeById(id: Int): InsulinType? =
        dao.getInsulinById(id)?.toDomain()

    override suspend fun getInsulinTypeByName(name: String): InsulinType? =
        dao.getInsulinTypeByName(name)?.toDomain()

    override suspend fun getInsulinEntriesBetween(startTimestamp: Long, endTimestamp: Long): List<InsulinEntry> =
        dao.getInsulinEntriesBetween(startTimestamp, endTimestamp).map { it.toDomain() }

    override suspend fun updateInsulinEntry(entry: InsulinEntry) {
        dao.updateInsulinEntry(entry.toLocal())
    }

    override suspend fun deleteInsulinEntry(entry: InsulinEntry) {
        dao.deleteInsulinEntry(entry.toLocal())
    }
}