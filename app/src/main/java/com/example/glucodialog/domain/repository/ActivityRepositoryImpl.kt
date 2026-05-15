package com.example.glucodialog.domain.repository

import com.example.glucodialog.data.repository.ActivityDao
import com.example.glucodialog.domain.model.ActivityEntry
import com.example.glucodialog.domain.model.ActivityType
import com.example.glucodialog.domain.mappers.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.glucodialog.domain.model.ActivityEntryWithTypeDomain

class ActivityRepositoryImpl(
    private val dao: ActivityDao
) : ActivityRepository {

    override fun getAllActivityTypes(): Flow<List<ActivityType>> {
        return dao.getAllActivityTypes().map { list -> list.map { it.toDomain() } }
    }
    override suspend fun getActivityEntryById(id: Int): ActivityEntry? =
        dao.getActivityEntryById(id)?.toDomain()

    override suspend fun insertActivityType(type: ActivityType): Long {
        return dao.insertActivityType(type.toLocal())
    }

    override suspend fun insertAllActivityTypes(types: List<ActivityType>) {
        dao.insertAllActivityTypes(types.map { it.toLocal() })
    }

    override fun getAllActivityEntries(): Flow<List<ActivityEntry>> {
        return dao.getAllActivityEntries().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getActivityById(id: Int): ActivityType? {
        return dao.getActivityById(id)?.toDomain()
    }
    override suspend fun getAllActivityEntriesOnceWithTypes(): List<ActivityEntryWithTypeDomain> {
        return dao.getAllActivityEntriesOnceWithTypes().map { it.toDomain() }
    }

    override suspend fun getAllActivityEntriesOnce(): List<ActivityEntry> {
        return dao.getAllActivityEntriesOnce().map { it.toDomain() }
    }

    override suspend fun getActivityTypeByName(name: String): ActivityType? {
        return dao.getActivityTypeByName(name)?.toDomain()
    }

    override suspend fun getActivitiesBetween(startTimestamp: Long, endTimestamp: Long): List<ActivityEntry> {
        return dao.getActivitiesBetween(startTimestamp, endTimestamp).map { it.toDomain() }
    }

    override fun getAllActivityEntriesWithTypesFlow(): Flow<List<ActivityEntryWithTypeDomain>> {
        return dao.getAllActivityEntriesWithTypesFlow().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun insertActivityEntry(entry: ActivityEntry) {
        dao.insertActivityEntry(entry.toLocal())
    }

    override suspend fun updateActivityEntry(entry: ActivityEntry) {
        dao.updateActivityEntry(entry.toLocal())
    }

    override suspend fun deleteActivityEntry(entry: ActivityEntry) {
        dao.deleteActivityEntry(entry.toLocal())
    }
}