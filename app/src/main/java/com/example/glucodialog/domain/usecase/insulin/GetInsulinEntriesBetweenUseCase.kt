package com.example.glucodialog.domain.usecase.insulin

import com.example.glucodialog.domain.repository.InsulinRepository

class GetInsulinEntriesBetweenUseCase(private val repository: InsulinRepository) {
    suspend operator fun invoke(start: Long, end: Long) = repository.getInsulinEntriesBetween(start, end)
}
