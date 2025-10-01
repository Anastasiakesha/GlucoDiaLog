package com.example.glucodialog.data.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.glucodialog.data.local.ActivityEntry
import com.example.glucodialog.data.local.ActivityType
import com.example.glucodialog.data.relations.ActivityEntryWithType
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {

    @Query("SELECT * FROM activity_types")
    fun getAllActivityTypes(): Flow<List<ActivityType>>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAllActivityTypes(types: List<ActivityType>)

    @Insert
    suspend fun insertActivityEntry(entry: ActivityEntry)

    @Query("SELECT * FROM activity_entries ORDER BY timestamp DESC")
    fun getAllActivityEntries(): Flow<List<ActivityEntry>>

    @Query("SELECT * FROM activity_types WHERE id = :id LIMIT 1")
    suspend fun getActivityById(id: Int): ActivityType?

    @Query("SELECT * FROM activity_entries ORDER BY timestamp DESC")
    suspend fun getAllActivityEntriesOnce(): List<ActivityEntry>

    @Transaction
    @Query("SELECT * FROM activity_entries ORDER BY timestamp DESC")
    suspend fun getAllActivityEntriesOnceWithTypes(): List<ActivityEntryWithType>

    @Query("SELECT * FROM activity_types WHERE LOWER(name) = LOWER(:name) LIMIT 1")
    suspend fun getActivityTypeByName(name: String): ActivityType?

    @Query("SELECT * FROM activity_entries WHERE timestamp BETWEEN :startTimestamp AND :endTimestamp ORDER BY timestamp DESC")
    suspend fun getActivitiesBetween(startTimestamp: Long, endTimestamp: Long): List<ActivityEntry>

    @Transaction
    @Query("SELECT * FROM activity_entries ORDER BY timestamp DESC")
    fun getAllActivityEntriesWithTypesFlow(): Flow<List<ActivityEntryWithType>>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertActivityType(type: ActivityType)

    @Update
    suspend fun updateActivityEntry(entry: ActivityEntry)

    @Delete
    suspend fun deleteActivityEntry(entry: ActivityEntry)

}