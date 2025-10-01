package com.example.glucodialog.domain.usecase.medication

import com.example.glucodialog.domain.model.MedicationEntry
import com.example.glucodialog.domain.repository.MedicationRepository

class GetMedicationEntriesBetweenUseCase(private val repository: MedicationRepository) {
    suspend operator fun invoke(startTimestamp: Long, endTimestamp: Long): List<MedicationEntry> =
        repository.getMedicationEntriesBetween(startTimestamp, endTimestamp)
}