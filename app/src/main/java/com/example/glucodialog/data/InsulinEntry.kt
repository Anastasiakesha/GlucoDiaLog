package com.example.glucodialog.data


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "insulin_entries")
data class InsulinEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val insulinTypeId: Int,
    val doseUnits: Double,
    val unit: String,
    val timestamp: Long
)