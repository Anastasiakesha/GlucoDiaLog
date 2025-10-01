package com.example.glucodialog.domain.usecase.activity


import com.example.glucodialog.domain.repository.ActivityRepository

class GetAllActivityEntriesOnceUseCase(
    private val repository: ActivityRepository
) {
    suspend operator fun invoke() = repository.getAllActivityEntriesOnce()
}