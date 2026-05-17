package com.example.glucodialog.domain.model

data class UserProfile(
    val id: Int = 0,
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