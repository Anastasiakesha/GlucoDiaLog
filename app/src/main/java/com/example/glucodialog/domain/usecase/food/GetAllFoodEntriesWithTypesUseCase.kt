package com.example.glucodialog.domain.usecase.food

import com.example.glucodialog.domain.model.FoodEntryWithTypeDomain
import com.example.glucodialog.domain.repository.FoodRepository
import kotlinx.coroutines.flow.Flow

class GetAllFoodEntriesWithTypesUseCase(private val repository: FoodRepository) {
    operator fun invoke(): Flow<List<FoodEntryWithTypeDomain>> =
        repository.getAllFoodEntriesWithTypesFlow()
}