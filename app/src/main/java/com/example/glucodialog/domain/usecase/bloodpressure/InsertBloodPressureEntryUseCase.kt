package com.example.glucodialog.domain.usecase.bloodpressure

import com.example.glucodialog.domain.model.BloodPressureEntry
import com.example.glucodialog.domain.repository.BloodPressureRepository

class InsertBloodPressureEntryUseCase(private val repo: BloodPressureRepository)
{
    suspend operator fun invoke(entry: BloodPressureEntry) = repo.insertBloodPressureEntry(entry)
}