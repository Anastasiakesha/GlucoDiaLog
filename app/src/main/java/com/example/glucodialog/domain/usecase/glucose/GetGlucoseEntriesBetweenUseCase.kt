package com.example.glucodialog.domain.usecase.glucose

import com.example.glucodialog.domain.model.GlucoseEntry
import com.example.glucodialog.domain.repository.GlucoseRepository

class GetGlucoseEntriesBetweenUseCase(
    private val repository: GlucoseRepository
) {
    suspend operator fun invoke(startTimestamp: Long, endTimestamp: Long): List<GlucoseEntry> {
        return repository.getGlucoseEntriesBetween(startTimestamp, endTimestamp)
    }
}