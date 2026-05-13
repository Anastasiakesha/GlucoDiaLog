package com.example.glucodialog.domain.repository


import com.example.glucodialog.domain.model.ActivityEntry
import com.example.glucodialog.domain.model.ActivityEntryWithTypeDomain
import com.example.glucodialog.domain.model.ActivityType
import kotlinx.coroutines.flow.Flow

interface ActivityRepository {
    fun getAllActivityTypes(): Flow<List<ActivityType>>
    suspend fun insertAllActivityTypes(types: List<ActivityType>)
    suspend fun insertActivityEntry(entry: ActivityEntry)
    fun getAllActivityEntries(): Flow<List<ActivityEntry>>
    suspend fun getAllActivityEntriesOnce(): List<ActivityEntry>
    suspend fun getActivityById(id: Int): ActivityType?
    suspend fun getAllActivityEntriesOnceWithTypes(): List<ActivityEntryWithTypeDomain>
    suspend fun getActivityTypeByName(name: String): ActivityType?
    suspend fun getActivitiesBetween(startTimestamp: Long, endTimestamp: Long): List<ActivityEntry>
    fun getAllActivityEntriesWithTypesFlow(): Flow<List<ActivityEntryWithTypeDomain>>
    suspend fun insertActivityType(type: ActivityType): Long
    suspend fun updateActivityEntry(entry: ActivityEntry)
    suspend fun deleteActivityEntry(entry: ActivityEntry)
}