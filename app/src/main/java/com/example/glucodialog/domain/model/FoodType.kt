package com.example.glucodialog.domain.model


data class FoodType(
    val id: Int = 0,
    val name: String,
    val calories: Int,
    val proteins: Double,
    val fats: Double,
    val carbs: Double,
    val allowedUnits: String
)
