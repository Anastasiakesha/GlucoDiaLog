package com.example.glucodialog.domain.usecase.food

import com.example.glucodialog.domain.model.FoodEntry
import com.example.glucodialog.domain.repository.FoodRepository
import kotlinx.coroutines.flow.Flow

class GetAllFoodEntriesUseCase(private val repository: FoodRepository) {
    operator fun invoke(): Flow<List<FoodEntry>> = repository.getAllFoodEntries()
}