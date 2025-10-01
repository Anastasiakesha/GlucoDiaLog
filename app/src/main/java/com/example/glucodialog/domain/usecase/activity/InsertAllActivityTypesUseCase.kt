package com.example.glucodialog.domain.usecase.activity


import com.example.glucodialog.domain.model.ActivityType
import com.example.glucodialog.domain.repository.ActivityRepository

class InsertAllActivityTypesUseCase(
    private val repository: ActivityRepository
) {
    suspend operator fun invoke(types: List<ActivityType>) {
        repository.insertAllActivityTypes(types)
    }
}