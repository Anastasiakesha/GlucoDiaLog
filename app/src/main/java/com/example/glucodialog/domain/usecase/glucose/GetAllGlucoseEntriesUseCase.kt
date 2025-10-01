package com.example.glucodialog.domain.usecase.glucose

import com.example.glucodialog.domain.model.GlucoseEntry
import com.example.glucodialog.domain.repository.GlucoseRepository
import kotlinx.coroutines.flow.Flow

class GetAllGlucoseEntriesUseCase(
    private val repository: GlucoseRepository
) {
    operator fun invoke(): Flow<List<GlucoseEntry>> {
        return repository.getAllGlucoseEntries()
    }
}