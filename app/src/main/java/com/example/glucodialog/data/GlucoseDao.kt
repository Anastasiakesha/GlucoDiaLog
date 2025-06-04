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
}