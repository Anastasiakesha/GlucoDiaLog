package com.example.glucodialog.domain.usecase.bloodpressure

import com.example.glucodialog.domain.model.BloodPressureEntry
import com.example.glucodialog.domain.repository.BloodPressureRepository

class UpdateBloodPressureUseCase(private val repository: BloodPressureRepository) {
    suspend operator fun invoke(entry: BloodPressureEntry) = repository.updateBloodPressureEntry(entry)
}