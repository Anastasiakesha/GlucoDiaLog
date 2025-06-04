package com.example.glucodialog.data


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_entries")
data class ActivityEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val activityTypeId: Int,
    val durationMinutes: Int,
    val timestamp: Long
)