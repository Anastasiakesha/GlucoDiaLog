package com.example.glucodialog.domain.usecase.medication

import com.example.glucodialog.domain.model.MedicationType
import com.example.glucodialog.domain.repository.MedicationRepository
import kotlinx.coroutines.flow.Flow

class GetAllMedicationTypesUseCase(private val repository: MedicationRepository) {
    operator fun invoke(): Flow<List<MedicationType>> = repository.getAllMedicationTypes()
}