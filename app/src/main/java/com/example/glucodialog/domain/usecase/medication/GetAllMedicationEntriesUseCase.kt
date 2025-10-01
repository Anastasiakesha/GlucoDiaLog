package com.example.glucodialog.domain.usecase.medication

import com.example.glucodialog.domain.model.MedicationEntry
import com.example.glucodialog.domain.model.MedicationEntryWithTypeDomain
import com.example.glucodialog.domain.model.MedicationType
import com.example.glucodialog.domain.repository.MedicationRepository
import kotlinx.coroutines.flow.Flow

class GetAllMedicationEntriesUseCase(private val repository: MedicationRepository) {
    operator fun invoke(): Flow<List<MedicationEntry>> = repository.getAllMedicationEntries()
}
