package com.example.glucodialog.data

import androidx.room.*
import com.example.glucodialog.data.relations.InsulinEntryWithType
import kotlinx.coroutines.flow.Flow

@Dao
interface InsulinDao {

    @Query("SELECT * FROM insulin_types")
    fun getAllInsulinTypes(): Flow<List<InsulinType>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllInsulinTypes(types: List<InsulinType>)

    @Insert
    suspend fun insertInsulinEntry(entry: InsulinEntry)

    @Query("SELECT * FROM insulin_entries ORDER BY timestamp DESC")
    fun getAllInsulinEntries(): Flow<List<InsulinEntry>>

    @Query("SELECT * FROM insulin_types WHERE id = :id LIMIT 1")
    suspend fun getInsulinById(id: Int): InsulinType?

    @Query("SELECT * FROM insulin_entries ORDER BY timestamp DESC")
    suspend fun getAllInsulinEntriesOnce(): List<InsulinEntry>

    @Transaction
    @Query("SELECT * FROM insulin_entries ORDER BY timestamp DESC")
    suspend fun getAllInsulinEntriesOnceWithTypes(): List<InsulinEntryWithType>

    @Query("SELECT * FROM insulin_types WHERE LOWER(name) = LOWER(:name) LIMIT 1")
    suspend fun getInsulinTypeByName(name: String): InsulinType?

}