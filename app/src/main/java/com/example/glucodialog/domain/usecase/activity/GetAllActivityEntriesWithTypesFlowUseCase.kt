package com.example.glucodialog.domain.usecase.activity

import com.example.glucodialog.domain.model.ActivityEntryWithTypeDomain
import com.example.glucodialog.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow

class GetAllActivityEntriesWithTypesFlowUseCase(
    private val repository: ActivityRepository
) {
    operator fun invoke(): Flow<List<ActivityEntryWithTypeDomain>> =
        repository.getAllActivityEntriesWithTypesFlow()
}