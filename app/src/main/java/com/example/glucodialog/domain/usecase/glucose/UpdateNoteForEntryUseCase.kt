package com.example.glucodialog.domain.usecase.glucose


import com.example.glucodialog.domain.repository.GlucoseRepository

class UpdateNoteForEntryUseCase(
    private val repository: GlucoseRepository
) {
    suspend operator fun invoke(entryId: Int, note: String) {
        repository.updateNoteForEntry(entryId, note)
    }
}