package com.example.glucodialog.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String,
    val name: String,
    val gender: String,
    val weight: Double,
    val height: Double,
    val diabetesType: String,
    val targetGlucoseLow: Double,
    val targetGlucoseHigh: Double,
    val glucoseUnit: String
)