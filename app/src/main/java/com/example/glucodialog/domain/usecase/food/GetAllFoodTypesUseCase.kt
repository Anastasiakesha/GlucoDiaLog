package com.example.glucodialog.domain.usecase.food

import com.example.glucodialog.domain.model.FoodType
import com.example.glucodialog.domain.repository.FoodRepository
import kotlinx.coroutines.flow.Flow

class GetAllFoodTypesUseCase(private val repository: FoodRepository) {
    operator fun invoke(): Flow<List<FoodType>> = repository.getAllFoodTypes()
}