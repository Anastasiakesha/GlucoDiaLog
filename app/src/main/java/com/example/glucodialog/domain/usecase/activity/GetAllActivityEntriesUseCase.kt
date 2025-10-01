package com.example.glucodialog.domain.usecase.activity

import com.example.glucodialog.domain.model.ActivityEntry
import com.example.glucodialog.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow
class GetAllActivityEntriesUseCase(
    private val repository: ActivityRepository
) {
    operator fun invoke(): Flow<List<ActivityEntry>> = repository.getAllActivityEntries()
}