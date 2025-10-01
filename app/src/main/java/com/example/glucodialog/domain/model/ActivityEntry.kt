package com.example.glucodialog.domain.model


data class ActivityEntry(
    val id: Int = 0,
    val activityTypeId: Int,
    val durationMinutes: Int,
    val timestamp: Long
)