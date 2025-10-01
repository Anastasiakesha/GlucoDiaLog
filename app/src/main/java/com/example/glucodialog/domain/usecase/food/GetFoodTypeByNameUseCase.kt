package com.example.glucodialog.domain.usecase.food

import com.example.glucodialog.domain.model.FoodType
import com.example.glucodialog.domain.repository.FoodRepository

class GetFoodTypeByNameUseCase(private val repository: FoodRepository) {
    suspend operator fun invoke(name: String): FoodType? = repository.getFoodTypeByName(name)
}