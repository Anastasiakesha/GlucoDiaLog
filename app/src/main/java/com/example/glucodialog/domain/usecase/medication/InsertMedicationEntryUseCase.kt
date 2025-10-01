package com.example.glucodialog.domain.usecase.medication

import com.example.glucodialog.domain.model.MedicationEntry
import com.example.glucodialog.domain.repository.MedicationRepository

class InsertMedicationEntryUseCase(private val repository: MedicationRepository) {
    suspend operator fun invoke(entry: MedicationEntry) = repository.insertMedicationEntry(entry)
}