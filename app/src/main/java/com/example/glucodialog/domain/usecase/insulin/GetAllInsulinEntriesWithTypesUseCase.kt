package com.example.glucodialog.domain.usecase.insulin

import com.example.glucodialog.domain.model.InsulinEntryWithTypeDomain
import com.example.glucodialog.domain.repository.InsulinRepository
import kotlinx.coroutines.flow.Flow

class GetAllInsulinEntriesWithTypesUseCase(private val repository: InsulinRepository) {
    operator fun invoke(): Flow<List<InsulinEntryWithTypeDomain>> = repository.getAllInsulinEntriesWithTypesFlow()
}