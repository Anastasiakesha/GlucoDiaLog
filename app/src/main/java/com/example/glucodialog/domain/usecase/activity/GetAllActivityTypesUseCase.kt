package com.example.glucodialog.domain.usecase.activity


import com.example.glucodialog.domain.repository.ActivityRepository

class GetAllActivityTypesUseCase(
    private val repository: ActivityRepository
) {
    operator fun invoke() = repository.getAllActivityTypes()
}