package com.example.glucodialog.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "glucose_entries")
data class GlucoseEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val glucoseLevel: Double,
    val unit: String,
    val timestamp: Long
)