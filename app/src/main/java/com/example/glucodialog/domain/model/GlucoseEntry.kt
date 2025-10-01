package com.example.glucodialog.domain.model

data class Glucose(
    val id: Int = 0,
    val level: Double,
    val unit: String,
    val timestamp: Long,
    val note: String? = null
)