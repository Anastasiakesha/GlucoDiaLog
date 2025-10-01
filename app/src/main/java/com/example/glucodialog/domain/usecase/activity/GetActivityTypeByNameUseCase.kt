package com.example.glucodialog.domain.usecase.activity

import com.example.glucodialog.domain.model.ActivityType
import com.example.glucodialog.domain.repository.ActivityRepository

class GetActivityTypeByNameUseCase(
    private val repository: ActivityRepository
) {
    suspend operator fun invoke(name: String): ActivityType? = repository.getActivityTypeByName(name)
}