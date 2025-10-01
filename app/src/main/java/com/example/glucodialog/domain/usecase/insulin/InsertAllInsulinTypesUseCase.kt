package com.example.glucodialog.domain.usecase.insulin

import com.example.glucodialog.domain.model.InsulinType
import com.example.glucodialog.domain.repository.InsulinRepository

class InsertAllInsulinTypesUseCase(private val repository: InsulinRepository) {
    suspend operator fun invoke(types: List<InsulinType>) = repository.insertAllInsulinTypes(types)
}