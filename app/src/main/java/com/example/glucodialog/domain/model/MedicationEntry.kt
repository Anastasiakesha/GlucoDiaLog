package com.example.glucodialog.domain.model


data class MedicationEntry(
    val id: Int = 0,
    val medicationTypeId: Int,
    val dose: String,
    val unit: String,
    val timestamp: Long
)
