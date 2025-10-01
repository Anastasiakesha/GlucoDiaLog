package com.example.glucodialog.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "insulin_types")
data class InsulinType(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String,
    val durationHours: Int
)