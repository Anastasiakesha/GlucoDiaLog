package com.example.glucodialog.domain.model

data class InsulinEntry(
    val id: Int = 0,
    val insulinTypeId: Int,
    val doseUnits: Double,
    val unit: String,
    val timestamp: Long
)