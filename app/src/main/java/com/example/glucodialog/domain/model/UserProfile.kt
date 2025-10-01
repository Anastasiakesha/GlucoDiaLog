package com.example.glucodialog.domain.model

data class UserProfile(
    val email: String,
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