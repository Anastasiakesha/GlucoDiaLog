package com.example.glucodialog.domain.usecase.glucose

import com.example.glucodialog.domain.model.GlucoseEntry
import com.example.glucodialog.domain.repository.GlucoseRepository

class DeleteGlucoseEntryUseCase(
    private val repository: GlucoseRepository
) {
    suspend operator fun invoke(entry: GlucoseEntry) {
        repository.deleteGlucoseEntry(entry)
    }
}