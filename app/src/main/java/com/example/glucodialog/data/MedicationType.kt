package com.example.glucodialog.data


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medication_types")
data class MedicationType(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)