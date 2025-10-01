package com.example.glucodialog.domain.usecase.activity


import com.example.glucodialog.domain.model.ActivityType
import com.example.glucodialog.domain.repository.ActivityRepository

class GetActivityByIdUseCase(
    private val repository: ActivityRepository
) {
    suspend operator fun invoke(id: Int): ActivityType? = repository.getActivityById(id)
}