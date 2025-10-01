package com.example.glucodialog.domain.usecase.food

import com.example.glucodialog.domain.model.FoodEntry
import com.example.glucodialog.domain.repository.FoodRepository

class GetFoodEntriesBetweenUseCase(private val repository: FoodRepository) {
    suspend operator fun invoke(startTimestamp: Long, endTimestamp: Long): List<FoodEntry> =
        repository.getFoodEntriesBetween(startTimestamp, endTimestamp)
}