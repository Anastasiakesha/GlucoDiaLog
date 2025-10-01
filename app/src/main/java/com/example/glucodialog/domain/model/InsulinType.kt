package com.example.glucodialog.domain.model

data class InsulinType(
    val id: Int = 0,
    val name: String,
    val type: String,
    val durationHours: Int
)