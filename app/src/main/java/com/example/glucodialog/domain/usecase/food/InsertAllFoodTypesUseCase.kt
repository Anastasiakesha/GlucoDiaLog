package com.example.glucodialog.domain.usecase.food

import com.example.glucodialog.domain.model.FoodType
import com.example.glucodialog.domain.repository.FoodRepository

class InsertAllFoodTypesUseCase(private val repository: FoodRepository) {
    suspend operator fun invoke(types: List<FoodType>) = repository.insertAllFoodTypes(types)
}