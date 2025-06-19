package com.example.glucodialog.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GlucoseDao {

    @Insert
    suspend fun insertGlucoseEntry(entry: GlucoseEntry)

    @Query("SELECT * FROM glucose_entries ORDER BY timestamp DESC")
    fun getAllGlucoseEntries(): Flow<List<GlucoseEntry>>

    @Query("SELECT * FROM glucose_entries ORDER BY timestamp DESC")
    suspend fun getAllGlucoseEntriesOnce(): List<GlucoseEntry>

    @Query("UPDATE glucose_entries SET note = :note WHERE id = :entryId")
    suspend fun updateNoteForEntry(entryId: Int, note: String)

    @Query("SELECT * FROM glucose_entries WHERE timestamp BETWEEN :startTimestamp AND :endTimestamp ORDER BY timestamp DESC")
    suspend fun getGlucoseEntriesBetween(startTimestamp: Long, endTimestamp: Long): List<GlucoseEntry>


}