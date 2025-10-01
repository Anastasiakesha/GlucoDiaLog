package com.example.glucodialog.domain.usecase.food

import com.example.glucodialog.domain.model.FoodEntry
import com.example.glucodialog.domain.repository.FoodRepository

class UpdateFoodEntryUseCase(private val repository: FoodRepository) {
    suspend operator fun invoke(entry: FoodEntry) = repository.updateFoodEntry(entry)
}