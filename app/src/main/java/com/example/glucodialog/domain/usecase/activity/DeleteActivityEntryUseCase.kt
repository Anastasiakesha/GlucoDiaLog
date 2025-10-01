package com.example.glucodialog.domain.usecase.activity


import com.example.glucodialog.domain.model.ActivityEntry
import com.example.glucodialog.domain.repository.ActivityRepository

class DeleteActivityEntry(
    private val repository: ActivityRepository
) {
    suspend operator fun invoke(entry: ActivityEntry) {
        repository.deleteActivityEntry(entry)
    }
}