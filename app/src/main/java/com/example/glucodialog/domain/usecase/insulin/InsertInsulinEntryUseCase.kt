package com.example.glucodialog.domain.usecase.insulin

import com.example.glucodialog.domain.model.InsulinEntry
import com.example.glucodialog.domain.repository.InsulinRepository

class InsertInsulinEntryUseCase(private val repository: InsulinRepository) {
    suspend operator fun invoke(entry: InsulinEntry) = repository.insertInsulinEntry(entry)
}
