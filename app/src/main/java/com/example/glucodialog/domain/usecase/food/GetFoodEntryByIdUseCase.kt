package com.example.glucodialog.domain.usecase.food

import com.example.glucodialog.domain.model.FoodEntry
import com.example.glucodialog.domain.repository.FoodRepository

class GetFoodEntryByIdUseCase(private val repository: FoodRepository) {
    suspend operator fun invoke(id: Int): FoodEntry? = repository.getFoodEntryById(id)
}