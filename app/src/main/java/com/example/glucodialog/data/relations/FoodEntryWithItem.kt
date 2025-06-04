package com.example.glucodialog.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.glucodialog.data.FoodEntry
import com.example.glucodialog.data.FoodItem

data class FoodEntryWithItem(
    @Embedded val entry: FoodEntry,
    @Relation(
        parentColumn = "foodItemId",
        entityColumn = "id"
    )
    val foodItem: FoodItem
)