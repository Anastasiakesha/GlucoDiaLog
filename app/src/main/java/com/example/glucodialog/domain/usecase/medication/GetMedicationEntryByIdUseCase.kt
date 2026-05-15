package com.example.glucodialog.domain.usecase.medication

import com.example.glucodialog.domain.model.MedicationEntry
import com.example.glucodialog.domain.repository.MedicationRepository

class GetMedicationEntryByIdUseCase(private val repository: MedicationRepository) {
    suspend operator fun invoke(id: Int): MedicationEntry? = repository.getMedicationEntryById(id)
}