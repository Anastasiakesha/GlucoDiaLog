package com.example.glucodialog.domain.usecase.insulin

import com.example.glucodialog.domain.model.InsulinEntryWithTypeDomain
import com.example.glucodialog.domain.repository.InsulinRepository

class GetAllInsulinEntriesWithTypesOnceUseCase(private val repository: InsulinRepository) {
    suspend operator fun invoke(): List<InsulinEntryWithTypeDomain> = repository.getAllInsulinEntriesWithTypesOnce()
}