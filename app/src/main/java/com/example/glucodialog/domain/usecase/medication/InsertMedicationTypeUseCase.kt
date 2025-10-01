package com.example.glucodialog.domain.usecase.medication

import com.example.glucodialog.domain.model.MedicationEntry
import com.example.glucodialog.domain.model.MedicationEntryWithTypeDomain
import com.example.glucodialog.domain.model.MedicationType
import com.example.glucodialog.domain.repository.MedicationRepository
import kotlinx.coroutines.flow.Flow

class InsertMedicationTypeUseCase(private val repository: MedicationRepository) {
    suspend operator fun invoke(type: MedicationType) = repository.insertMedicationType(type)
}
