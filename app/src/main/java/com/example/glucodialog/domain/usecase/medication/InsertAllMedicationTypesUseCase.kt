package com.example.glucodialog.domain.usecase.medication

import com.example.glucodialog.domain.model.MedicationType
import com.example.glucodialog.domain.repository.MedicationRepository


class InsertAllMedicationTypesUseCase(private val repository: MedicationRepository) {
    suspend operator fun invoke(types: List<MedicationType>) = repository.insertAllMedicationTypes(types)
}