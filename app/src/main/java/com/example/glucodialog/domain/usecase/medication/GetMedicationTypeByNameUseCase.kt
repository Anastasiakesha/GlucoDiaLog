package com.example.glucodialog.domain.usecase.medication

import com.example.glucodialog.domain.model.MedicationType
import com.example.glucodialog.domain.repository.MedicationRepository

class GetMedicationTypeByNameUseCase(private val repository: MedicationRepository) {
    suspend operator fun invoke(name: String): MedicationType? = repository.getMedicationTypeByName(name)
}