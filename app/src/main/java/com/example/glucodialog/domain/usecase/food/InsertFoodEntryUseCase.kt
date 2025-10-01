package com.example.glucodialog.domain.usecase.food

import com.example.glucodialog.domain.model.FoodEntry
import com.example.glucodialog.domain.repository.FoodRepository


class InsertFoodEntryUseCase(private val repository: FoodRepository) {
    suspend operator fun invoke(entry: FoodEntry) = repository.insertFoodEntry(entry)
}