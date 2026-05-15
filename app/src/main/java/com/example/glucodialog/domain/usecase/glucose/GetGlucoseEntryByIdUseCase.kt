package com.example.glucodialog.domain.usecase.glucose

import com.example.glucodialog.domain.model.GlucoseEntry
import com.example.glucodialog.domain.repository.GlucoseRepository

class GetGlucoseEntryByIdUseCase(private val repository: GlucoseRepository) {
    suspend operator fun invoke(id: Int): GlucoseEntry? = repository.getGlucoseEntryById(id)
}