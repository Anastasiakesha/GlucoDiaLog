package com.example.glucodialog.domain.repository


import com.example.glucodialog.data.repository.MedicationDao
import com.example.glucodialog.domain.model.MedicationEntry
import com.example.glucodialog.domain.model.MedicationEntryWithTypeDomain
import com.example.glucodialog.domain.model.MedicationType
import com.example.glucodialog.domain.mappers.toDomain
import com.example.glucodialog.domain.mappers.toLocal
import com.example.glucodialog.domain.repository.MedicationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MedicationRepositoryImpl(
    private val dao: MedicationDao
) : MedicationRepository {

    override fun getAllMedicationTypes(): Flow<List<MedicationType>> =
        dao.getAllMedicationTypes().map { list -> list.map { it.toDomain() } }

    override suspend fun insertMedicationType(type: MedicationType): Long {
        return dao.insertMedicationType(type.toLocal())
    }

    override suspend fun insertAllMedicationTypes(types: List<MedicationType>) {
        dao.insertAllMedicationTypes(types.map { it.toLocal() })
    }

    override fun getAllMedicationEntries(): Flow<List<MedicationEntry>> =
        dao.getAllMedicationEntries().map { list -> list.map { it.toDomain() } }

    override fun getAllMedicationEntriesWithTypesFlow(): Flow<List<MedicationEntryWithTypeDomain>> =
        dao.getAllMedicationEntriesWithTypesFlow().map { list -> list.map { it.toDomain() } }

    override suspend fun getAllMedicationEntriesOnceWithTypes(): List<MedicationEntryWithTypeDomain> =
        dao.getAllMedicationEntriesOnceWithTypes().map { it.toDomain() }

    override suspend fun getMedicationById(id: Int): MedicationType? =
        dao.getMedicationById(id)?.toDomain()

    override suspend fun getAllMedicationEntriesOnce(): List<MedicationEntry> =
        dao.getAllMedicationEntriesOnce().map { it.toDomain() }

    override suspend fun getMedicationTypeByName(name: String): MedicationType? =
        dao.getMedicationTypeByName(name)?.toDomain()

    override suspend fun getMedicationEntriesBetween(startTimestamp: Long, endTimestamp: Long): List<MedicationEntry> =
        dao.getMedicationEntriesBetween(startTimestamp, endTimestamp).map { it.toDomain() }

    override suspend fun insertMedicationEntry(entry: MedicationEntry) {
        dao.insertMedicationEntry(entry.toLocal())
    }

    override suspend fun updateMedicationEntry(entry: MedicationEntry) {
        dao.updateMedicationEntry(entry.toLocal())
    }

    override suspend fun deleteMedicationEntry(entry: MedicationEntry) {
        dao.deleteMedicationEntry(entry.toLocal())
    }
}