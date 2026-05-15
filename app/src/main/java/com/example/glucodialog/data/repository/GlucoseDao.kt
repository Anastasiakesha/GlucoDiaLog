package com.example.glucodialog.data.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.glucodialog.data.local.FoodEntry
import com.example.glucodialog.data.local.GlucoseEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface GlucoseDao {

    @Insert
    suspend fun insertGlucoseEntry(entry: GlucoseEntry)

    @Query("SELECT * FROM glucose_entries ORDER BY timestamp DESC")
    fun getAllGlucoseEntries(): Flow<List<GlucoseEntry>>

    @Query("SELECT * FROM glucose_entries WHERE id = :id LIMIT 1")
    suspend fun getGlucoseEntryById(id: Int): GlucoseEntry?

    @Query("SELECT * FROM glucose_entries ORDER BY timestamp DESC")
    suspend fun getAllGlucoseEntriesOnce(): List<GlucoseEntry>

    @Query("UPDATE glucose_entries SET note = :note WHERE id = :entryId")
    suspend fun updateNoteForEntry(entryId: Int, note: String)

    @Query("SELECT * FROM glucose_entries WHERE timestamp BETWEEN :startTimestamp AND :endTimestamp ORDER BY timestamp DESC")
    suspend fun getGlucoseEntriesBetween(startTimestamp: Long, endTimestamp: Long): List<GlucoseEntry>

    @Update
    suspend fun updateGlucoseEntry(entry: GlucoseEntry)

    @Delete
    suspend fun deleteGlucoseEntry(entry: GlucoseEntry)


}