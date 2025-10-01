package com.example.glucodialog.domain.usecase.insulin

import com.example.glucodialog.domain.model.InsulinEntry
import com.example.glucodialog.domain.repository.InsulinRepository

class GetAllInsulinEntriesOnceUseCase(private val repository: InsulinRepository) {
    suspend operator fun invoke(): List<InsulinEntry> = repository.getAllInsulinEntriesOnce()
}