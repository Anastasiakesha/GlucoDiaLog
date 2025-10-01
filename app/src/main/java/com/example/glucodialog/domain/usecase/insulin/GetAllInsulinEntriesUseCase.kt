package com.example.glucodialog.domain.usecase.insulin

import com.example.glucodialog.domain.model.InsulinEntry
import com.example.glucodialog.domain.repository.InsulinRepository
import kotlinx.coroutines.flow.Flow

class GetAllInsulinEntriesUseCase(private val repository: InsulinRepository) {
    operator fun invoke(): Flow<List<InsulinEntry>> = repository.getAllInsulinEntries()
}
