package com.example.glucodialog.domain.usecase.insulin

import com.example.glucodialog.domain.model.InsulinType
import com.example.glucodialog.domain.repository.InsulinRepository
import kotlinx.coroutines.flow.Flow

class GetAllInsulinTypesUseCase(private val repository: InsulinRepository) {
    operator fun invoke(): Flow<List<InsulinType>> = repository.getAllInsulinTypes()
}