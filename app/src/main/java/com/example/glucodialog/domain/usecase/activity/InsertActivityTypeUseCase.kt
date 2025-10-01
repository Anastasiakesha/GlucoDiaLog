package com.example.glucodialog.domain.usecase.activity


import com.example.glucodialog.domain.model.ActivityType
import com.example.glucodialog.domain.repository.ActivityRepository

class InsertActivityTypeUseCase(
    private val repository: ActivityRepository
) {
    suspend operator fun invoke(type: ActivityType) {
        repository.insertActivityType(type)
    }
}