package com.example.glucodialog.domain.usecase.insulin

import com.example.glucodialog.domain.model.InsulinType
import com.example.glucodialog.domain.repository.InsulinRepository

class GetInsulinTypeByIdUseCase(private val repository: InsulinRepository) {
    suspend operator fun invoke(id: Int): InsulinType? = repository.getInsulinTypeById(id)
}