package com.example.glucodialog.domain.usecase.activity

import com.example.glucodialog.domain.model.ActivityEntry
import com.example.glucodialog.domain.repository.ActivityRepository

class GetActivityEntryByIdUseCase(private val repository: ActivityRepository) {
    suspend operator fun invoke(id: Int): ActivityEntry? = repository.getActivityEntryById(id)
}