package com.example.glucodialog.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.glucodialog.domain.model.GlucoseEntry
import com.example.glucodialog.domain.usecase.glucose.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GlucoseViewModel(
    private val getAllGlucoseEntriesUseCase: GetAllGlucoseEntriesUseCase,
    private val getAllGlucoseEntriesOnceUseCase: GetAllGlucoseEntriesOnceUseCase,
    private val getGlucoseEntriesBetweenUseCase: GetGlucoseEntriesBetweenUseCase,
    private val insertGlucoseUseCase: InsertGlucoseUseCase,
    private val updateGlucoseEntryUseCase: UpdateGlucoseEntryUseCase,
    private val deleteGlucoseEntryUseCase: DeleteGlucoseEntryUseCase,
    private val updateNoteForEntryUseCase: UpdateNoteForEntryUseCase
) : ViewModel() {

    private val _glucoseEntries = MutableStateFlow<List<GlucoseEntry>>(emptyList())
    val glucoseEntries: StateFlow<List<GlucoseEntry>> = _glucoseEntries

    init {
        viewModelScope.launch {
            getAllGlucoseEntriesUseCase().collectLatest { entries ->
                _glucoseEntries.value = entries
            }
        }
    }

    fun addEntry(entry: GlucoseEntry, onComplete: () -> Unit = {}) = viewModelScope.launch {
        insertGlucoseUseCase(entry)
        onComplete()
    }

    fun updateEntry(entry: GlucoseEntry) = viewModelScope.launch {
        updateGlucoseEntryUseCase(entry)
    }

    fun deleteEntry(entry: GlucoseEntry) = viewModelScope.launch {
        deleteGlucoseEntryUseCase(entry)
    }

    fun updateNote(entryId: Int, note: String) = viewModelScope.launch {
        updateNoteForEntryUseCase(entryId, note)
    }

    suspend fun getAllEntriesOnce(): List<GlucoseEntry> = getAllGlucoseEntriesOnceUseCase()

    suspend fun getEntriesBetween(start: Long, end: Long): List<GlucoseEntry> =
        getGlucoseEntriesBetweenUseCase(start, end)
}

class GlucoseViewModelFactory(
    private val getAllGlucoseEntriesUseCase: GetAllGlucoseEntriesUseCase,
    private val getAllGlucoseEntriesOnceUseCase: GetAllGlucoseEntriesOnceUseCase,
    private val getGlucoseEntriesBetweenUseCase: GetGlucoseEntriesBetweenUseCase,
    private val insertGlucoseUseCase: InsertGlucoseUseCase,
    private val updateGlucoseEntryUseCase: UpdateGlucoseEntryUseCase,
    private val deleteGlucoseEntryUseCase: DeleteGlucoseEntryUseCase,
    private val updateNoteForEntryUseCase: UpdateNoteForEntryUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GlucoseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GlucoseViewModel(
                getAllGlucoseEntriesUseCase,
                getAllGlucoseEntriesOnceUseCase,
                getGlucoseEntriesBetweenUseCase,
                insertGlucoseUseCase,
                updateGlucoseEntryUseCase,
                deleteGlucoseEntryUseCase,
                updateNoteForEntryUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}