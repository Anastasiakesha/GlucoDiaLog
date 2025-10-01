package com.example.glucodialog.domain.usecase.activity


import com.example.glucodialog.domain.repository.ActivityRepository

class GetAllActivitiesOnceUseCase(
    private val repository: ActivityRepository
) {
    suspend operator fun invoke() = repository.getAllActivityEntriesOnce()
}