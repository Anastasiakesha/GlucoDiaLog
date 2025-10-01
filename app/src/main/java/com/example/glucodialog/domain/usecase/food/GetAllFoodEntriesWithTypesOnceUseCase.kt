package com.example.glucodialog.domain.usecase.food

import com.example.glucodialog.domain.model.FoodEntryWithTypeDomain
import com.example.glucodialog.domain.repository.FoodRepository

class GetAllFoodEntriesWithTypesOnceUseCase(private val repository: FoodRepository) {
    suspend operator fun invoke(): List<FoodEntryWithTypeDomain> =
        repository.getAllFoodEntriesWithTypesOnce()
}
