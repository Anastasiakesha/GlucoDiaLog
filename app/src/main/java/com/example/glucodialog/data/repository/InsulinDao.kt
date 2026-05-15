package com.example.glucodialog.data.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.glucodialog.data.local.InsulinEntry
import com.example.glucodialog.data.local.InsulinType
import com.example.glucodialog.data.relations.InsulinEntryWithType
import kotlinx.coroutines.flow.Flow

@Dao
interface InsulinDao {

    @Query("SELECT * FROM insulin_types")
    fun getAllInsulinTypes(): Flow<List<InsulinType>>

    @Query("SELECT * FROM insulin_entries WHERE id = :id LIMIT 1")
    suspend fun getInsulinEntryById(id: Int): InsulinEntry?

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
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

    @Query("SELECT * FROM insulin_entries WHERE timestamp BETWEEN :startTimestamp AND :endTimestamp ORDER BY timestamp DESC")
    suspend fun getInsulinEntriesBetween(startTimestamp: Long, endTimestamp: Long): List<InsulinEntry>

    @Transaction
    @Query("SELECT * FROM insulin_entries ORDER BY timestamp DESC")
    fun getAllInsulinEntriesWithTypesFlow(): Flow<List<InsulinEntryWithType>>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertInsulinType(type: InsulinType): Long

    @Update
    suspend fun updateInsulinEntry(entry: InsulinEntry)

    @Delete
    suspend fun deleteInsulinEntry(entry: InsulinEntry)

}