package com.example.glucodialog.domain.usecase.food

import com.example.glucodialog.domain.model.FoodType
import com.example.glucodialog.domain.repository.FoodRepository

class GetFoodTypeByIdUseCase(private val repository: FoodRepository) {
    suspend operator fun invoke(id: Int): FoodType? = repository.getFoodTypeById(id)
}