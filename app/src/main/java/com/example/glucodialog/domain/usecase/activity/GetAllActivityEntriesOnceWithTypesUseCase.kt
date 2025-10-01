package com.example.glucodialog.domain.usecase.activity

import com.example.glucodialog.domain.model.ActivityEntryWithTypeDomain
import com.example.glucodialog.domain.repository.ActivityRepository

class GetAllActivityEntriesOnceWithTypesUseCase(
    private val repository: ActivityRepository
) {
    suspend operator fun invoke(): List<ActivityEntryWithTypeDomain> =
        repository.getAllActivityEntriesOnceWithTypes()
}