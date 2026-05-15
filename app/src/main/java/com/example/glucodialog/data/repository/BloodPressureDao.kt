package com.example.glucodialog.data.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.glucodialog.data.local.BloodPressureEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface BloodPressureDao {
    @Insert
    suspend fun insertBloodPressureEntry(entry: BloodPressureEntry)

    @Query("SELECT * FROM blood_pressure_entries WHERE id = :id LIMIT 1")
    suspend fun getBloodPressureEntryById(id: Int): BloodPressureEntry?

    @Query("SELECT * FROM blood_pressure_entries ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<BloodPressureEntry>>

    @Update
    suspend fun updateBloodPressureEntry(entry: BloodPressureEntry)

    @Delete
    suspend fun deleteBloodPressureEntry(entry: BloodPressureEntry)
}