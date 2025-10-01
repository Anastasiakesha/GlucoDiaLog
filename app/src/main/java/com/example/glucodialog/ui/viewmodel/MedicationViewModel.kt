package com.example.glucodialog.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.glucodialog.domain.model.MedicationEntry
import com.example.glucodialog.domain.model.MedicationEntryWithTypeDomain
import com.example.glucodialog.domain.model.MedicationType
import com.example.glucodialog.domain.usecase.medication.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MedicationViewModel(
    private val getAllMedicationTypesUseCase: GetAllMedicationTypesUseCase,
    private val insertMedicationTypeUseCase: InsertMedicationTypeUseCase,
    private val insertAllMedicationTypesUseCase: InsertAllMedicationTypesUseCase,
    private val getAllMedicationEntriesUseCase: GetAllMedicationEntriesUseCase,
    private val getAllMedicationEntriesWithTypesUseCase: GetAllMedicationEntriesWithTypesUseCase,
    private val getAllMedicationEntriesOnceWithTypesUseCase: GetAllMedicationEntriesOnceWithTypesUseCase,
    private val getMedicationByIdUseCase: GetMedicationByIdUseCase,
    private val getAllMedicationEntriesOnceUseCase: GetAllMedicationEntriesOnceUseCase,
    private val getMedicationTypeByNameUseCase: GetMedicationTypeByNameUseCase,
    private val getMedicationEntriesBetweenUseCase: GetMedicationEntriesBetweenUseCase,
    private val insertMedicationEntryUseCase: InsertMedicationEntryUseCase,
    private val updateMedicationEntryUseCase: UpdateMedicationEntryUseCase,
    private val deleteMedicationEntryUseCase: DeleteMedicationEntryUseCase
) : ViewModel() {

    private val _medicationTypes = MutableStateFlow<List<MedicationType>>(emptyList())
    val medicationTypes: StateFlow<List<MedicationType>> = _medicationTypes

    private val _medicationEntries = MutableStateFlow<List<MedicationEntry>>(emptyList())
    val medicationEntries: StateFlow<List<MedicationEntry>> = _medicationEntries

    init {
        viewModelScope.launch {
            getAllMedicationTypesUseCase().collectLatest { types ->
                _medicationTypes.value = types
            }
        }

        viewModelScope.launch {
            getAllMedicationEntriesUseCase().collectLatest { entries ->
                _medicationEntries.value = entries
            }
        }
    }

    fun addMedicationType(type: MedicationType) = viewModelScope.launch {
        insertMedicationTypeUseCase(type)
    }

    fun addAllMedicationTypes(types: List<MedicationType>) = viewModelScope.launch {
        insertAllMedicationTypesUseCase(types)
    }

    fun addMedicationEntry(entry: MedicationEntry) = viewModelScope.launch {
        insertMedicationEntryUseCase(entry)
    }

    fun updateMedicationEntry(entry: MedicationEntry) = viewModelScope.launch {
        updateMedicationEntryUseCase(entry)
    }

    fun deleteMedicationEntry(entry: MedicationEntry) = viewModelScope.launch {
        deleteMedicationEntryUseCase(entry)
    }

    suspend fun getMedicationById(id: Int): MedicationType? =
        getMedicationByIdUseCase(id)

    suspend fun getAllMedicationEntriesOnce(): List<MedicationEntry> =
        getAllMedicationEntriesOnceUseCase()

    suspend fun getAllMedicationEntriesOnceWithTypes(): List<MedicationEntryWithTypeDomain> =
        getAllMedicationEntriesOnceWithTypesUseCase()

    suspend fun getMedicationTypeByName(name: String): MedicationType? =
        getMedicationTypeByNameUseCase(name)

    suspend fun getMedicationEntriesBetween(start: Long, end: Long): List<MedicationEntry> =
        getMedicationEntriesBetweenUseCase(start, end)

    fun getAllMedicationEntriesWithTypes() = getAllMedicationEntriesWithTypesUseCase()
}

class MedicationViewModelFactory(
    private val getAllMedicationTypesUseCase: GetAllMedicationTypesUseCase,
    private val insertMedicationTypeUseCase: InsertMedicationTypeUseCase,
    private val insertAllMedicationTypesUseCase: InsertAllMedicationTypesUseCase,
    private val getAllMedicationEntriesUseCase: GetAllMedicationEntriesUseCase,
    private val getAllMedicationEntriesWithTypesUseCase: GetAllMedicationEntriesWithTypesUseCase,
    private val getAllMedicationEntriesOnceWithTypesUseCase: GetAllMedicationEntriesOnceWithTypesUseCase,
    private val getMedicationByIdUseCase: GetMedicationByIdUseCase,
    private val getAllMedicationEntriesOnceUseCase: GetAllMedicationEntriesOnceUseCase,
    private val getMedicationTypeByNameUseCase: GetMedicationTypeByNameUseCase,
    private val getMedicationEntriesBetweenUseCase: GetMedicationEntriesBetweenUseCase,
    private val insertMedicationEntryUseCase: InsertMedicationEntryUseCase,
    private val updateMedicationEntryUseCase: UpdateMedicationEntryUseCase,
    private val deleteMedicationEntryUseCase: DeleteMedicationEntryUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MedicationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MedicationViewModel(
                getAllMedicationTypesUseCase,
                insertMedicationTypeUseCase,
                insertAllMedicationTypesUseCase,
                getAllMedicationEntriesUseCase,
                getAllMedicationEntriesWithTypesUseCase,
                getAllMedicationEntriesOnceWithTypesUseCase,
                getMedicationByIdUseCase,
                getAllMedicationEntriesOnceUseCase,
                getMedicationTypeByNameUseCase,
                getMedicationEntriesBetweenUseCase,
                insertMedicationEntryUseCase,
                updateMedicationEntryUseCase,
                deleteMedicationEntryUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}