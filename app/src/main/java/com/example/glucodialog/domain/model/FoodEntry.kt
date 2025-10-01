package com.example.glucodialog.domain.model

data class FoodEntry(
    val id: Int = 0,
    val foodTypeId: Int,
    val quantity: Double,
    val unit: String,
    val timestamp: Long
)