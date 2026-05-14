package com.example.glucodialog.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.glucodialog.domain.model.BloodPressureEntry
import com.example.glucodialog.domain.usecase.bloodpressure.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BloodPressureViewModel(
    private val insertBloodPressureEntryUseCase: InsertBloodPressureEntryUseCase,
    private val getAllUseCase: GetAllBloodPressureUseCase,
    private val deleteBloodPressureEntryUseCase: DeleteBloodPressureEntryUseCase
) : ViewModel() {

    private val _entries = MutableStateFlow<List<BloodPressureEntry>>(emptyList())
    val entries: StateFlow<List<BloodPressureEntry>> = _entries

    init {
        viewModelScope.launch {
            getAllUseCase().collectLatest { _entries.value = it }
        }
    }

    fun addBloodPressureEntry(entry: BloodPressureEntry, onComplete: () -> Unit = {}) = viewModelScope.launch {
        insertBloodPressureEntryUseCase(entry)
        onComplete()
    }

    fun deleteBloodPressureEntry(entry: BloodPressureEntry) = viewModelScope.launch {
        deleteBloodPressureEntryUseCase(entry)
    }
}

class BloodPressureViewModelFactory(
    private val insertBloodPressureEntryUseCase: InsertBloodPressureEntryUseCase,
    private val getAllUseCase: GetAllBloodPressureUseCase,
    private val deleteBloodPressureEntryUseCase: DeleteBloodPressureEntryUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BloodPressureViewModel(insertBloodPressureEntryUseCase, getAllUseCase, deleteBloodPressureEntryUseCase) as T
    }
}