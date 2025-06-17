package com.example.glucodialog.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val email: String, // email как уникальный ключ
    val name: String,
    val gender: String,
    val weight: Double,
    val height: Double,
    val diabetesType: String,
    val targetGlucoseLow: Double,
    val targetGlucoseHigh: Double,
    val glucoseUnit: String,
    val bolusInsulin: String,
    val bolusDose: Double,
    val basalInsulin: String,
    val basalDose: Double,
    val medication: String,
    val medicationDose: Double,
    val medicationUnit: String,
    val medicationTimeMinutesFromMidnight: Int? = null
)
