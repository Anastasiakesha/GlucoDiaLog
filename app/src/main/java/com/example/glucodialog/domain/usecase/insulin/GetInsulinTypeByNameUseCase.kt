package com.example.glucodialog.domain.usecase.insulin

import com.example.glucodialog.domain.model.InsulinType
import com.example.glucodialog.domain.repository.InsulinRepository

class GetInsulinTypeByNameUseCase(private val repository: InsulinRepository) {
    suspend operator fun invoke(name: String): InsulinType? = repository.getInsulinTypeByName(name)
}