package com.example.glucodialog.domain.usecase.food

import com.example.glucodialog.domain.model.FoodType
import com.example.glucodialog.domain.repository.FoodRepository

class InsertFoodTypeUseCase(private val repository: FoodRepository) {
    suspend operator fun invoke(type: FoodType) = repository.insertFoodType(type)
}
