package com.example.glucodialog.domain.mappers

import com.example.glucodialog.data.local.FoodEntry as LocalFoodEntry
import com.example.glucodialog.data.local.FoodItem as LocalFoodType
import com.example.glucodialog.data.relations.FoodEntryWithItem as LocalFoodEntryWithType
import com.example.glucodialog.domain.model.FoodEntry
import com.example.glucodialog.domain.model.FoodType
import com.example.glucodialog.domain.model.FoodEntryWithTypeDomain

fun LocalFoodType.toDomain() = FoodType(
    id = id,
    name = name,
    calories = calories,
    proteins = proteins,
    fats = fats,
    carbs = carbs,
    allowedUnits = allowedUnits
)

fun FoodType.toEntity() = LocalFoodType(
    id = id,
    name = name,
    calories = calories,
    proteins = proteins,
    fats = fats,
    carbs = carbs,
    allowedUnits = allowedUnits
)

fun LocalFoodEntry.toDomain() = FoodEntry(
    id = id,
    foodTypeId = foodItemId,
    quantity = quantity,
    unit = unit,
    timestamp = timestamp
)

fun FoodEntry.toEntity() = LocalFoodEntry(
    id = id,
    foodItemId = foodTypeId,
    quantity = quantity,
    unit = unit,
    timestamp = timestamp
)

fun LocalFoodEntryWithType.toDomain() = FoodEntryWithTypeDomain(
    entry = entry.toDomain(),
    type = foodItem?.toDomain()
)

//fun LocalFoodEntryWithType.toDomain(): FoodEntryWithTypeDomain {
//    val safeType = foodItem ?: FoodType(
//        id = -1,
//        name = "Неизвестная еда",
//        calories = 0,
//        proteins = 0.0,
//        fats = 0.0,
//        carbs = 0.0,
//        allowedUnits = ""
//    )
//
//    return FoodEntryWithTypeDomain(
//        entry = entry.toDomain(),
//        type = safeType as FoodType
//    )
//}