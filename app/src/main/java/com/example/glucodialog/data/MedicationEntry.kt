package com.example.glucodialog.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medication_entries")
data class MedicationEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val medicationTypeId: Int,
    val dose: String,
    val unit: String,
    val timestamp: Long
)