package com.example.glucodialog.domain.model

data class BloodPressureEntry(
    val id: Int = 0,
    val systolic: Int,
    val diastolic: Int,
    val pulse: Int,
    val timestamp: Long
)