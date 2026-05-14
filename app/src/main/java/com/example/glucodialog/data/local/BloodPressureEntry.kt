package com.example.glucodialog.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blood_pressure_entries")
data class BloodPressureEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val systolic: Int,
    val diastolic: Int,
    val pulse: Int,
    val timestamp: Long
)