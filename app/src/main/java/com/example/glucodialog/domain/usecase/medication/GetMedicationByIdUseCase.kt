package com.example.glucodialog.domain.usecase.medication

import com.example.glucodialog.domain.model.MedicationType
import com.example.glucodialog.domain.repository.MedicationRepository


class GetMedicationByIdUseCase(private val repository: MedicationRepository) {
    suspend operator fun invoke(id: Int): MedicationType? = repository.getMedicationById(id)
}