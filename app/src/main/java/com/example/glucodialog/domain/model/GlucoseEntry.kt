package com.example.glucodialog.domain.model

data class GlucoseEntry(
    val id: Int = 0,
    val glucoseLevel: Double,
    val unit: String,
    val timestamp: Long,
    val note: String? = null
)