package com.example.glucodialog.domain.usecase.bloodpressure

import com.example.glucodialog.domain.repository.BloodPressureRepository

class GetAllBloodPressureUseCase(private val repo: BloodPressureRepository)
{
    operator fun invoke() = repo.getAllEntries()
}