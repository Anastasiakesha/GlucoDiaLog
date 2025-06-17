package com.example.glucodialog.data

import androidx.room.*
import com.example.glucodialog.data.relations.ActivityEntryWithType
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {

    @Query("SELECT * FROM activity_types")
    fun getAllActivityTypes(): Flow<List<ActivityType>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
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

}
