package com.example.glucodialog.domain.usecase.insulin

import com.example.glucodialog.domain.model.InsulinType
import com.example.glucodialog.domain.repository.InsulinRepository

class InsertInsulinTypeUseCase(private val repository: InsulinRepository) {
    suspend operator fun invoke(type: InsulinType): Long {
        return repository.insertInsulinType(type)
    }
}