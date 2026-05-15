package com.example.glucodialog.domain.usecase.insulin

import com.example.glucodialog.domain.model.InsulinEntry
import com.example.glucodialog.domain.repository.InsulinRepository

class GetInsulinEntryByIdUseCase(private val repository: InsulinRepository) {
    suspend operator fun invoke(id: Int): InsulinEntry? = repository.getInsulinEntryById(id)
}