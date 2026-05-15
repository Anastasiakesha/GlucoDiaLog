package com.example.glucodialog.domain.usecase.bloodpressure

import com.example.glucodialog.domain.model.BloodPressureEntry
import com.example.glucodialog.domain.repository.BloodPressureRepository

class GetBloodPressureEntryByIdUseCase(private val repository: BloodPressureRepository) {
    suspend operator fun invoke(id: Int): BloodPressureEntry? = repository.getBloodPressureEntryById(id)
}