package com.example.glucodialog.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_entries")
data class FoodEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val foodItemId: Int,
    val quantity: Double,
    val unit: String,// Добавленное поле
    val timestamp: Long
)