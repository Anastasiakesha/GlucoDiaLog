package com.example.glucodialog.domain.usecase.medication

import com.example.glucodialog.domain.model.MedicationEntryWithTypeDomain
import com.example.glucodialog.domain.repository.MedicationRepository

class GetAllMedicationEntriesOnceWithTypesUseCase(private val repository: MedicationRepository) {
    suspend operator fun invoke(): List<MedicationEntryWithTypeDomain> =
        repository.getAllMedicationEntriesOnceWithTypes()
}