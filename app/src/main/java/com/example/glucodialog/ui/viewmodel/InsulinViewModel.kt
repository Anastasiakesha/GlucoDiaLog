package com.example.glucodialog.ui.viewmodel


import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.glucodialog.domain.model.InsulinEntry
import com.example.glucodialog.domain.model.InsulinType
import com.example.glucodialog.domain.model.InsulinEntryWithTypeDomain
import com.example.glucodialog.domain.usecase.insulin.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class InsulinViewModel(
    private val getAllInsulinTypesUseCase: GetAllInsulinTypesUseCase,
    private val insertInsulinTypeUseCase: InsertInsulinTypeUseCase,
    private val insertAllInsulinTypesUseCase: InsertAllInsulinTypesUseCase,
    private val getInsulinTypeByIdUseCase: GetInsulinTypeByIdUseCase,
    private val getInsulinTypeByNameUseCase: GetInsulinTypeByNameUseCase,
    private val insertInsulinEntryUseCase: InsertInsulinEntryUseCase,
    private val getAllInsulinEntriesUseCase: GetAllInsulinEntriesUseCase,
    private val getAllInsulinEntriesOnceUseCase: GetAllInsulinEntriesOnceUseCase,
    private val getAllInsulinEntriesWithTypesUseCase: GetAllInsulinEntriesWithTypesUseCase,
    private val getAllInsulinEntriesWithTypesOnceUseCase: GetAllInsulinEntriesWithTypesOnceUseCase,
    private val getInsulinEntriesBetweenUseCase: GetInsulinEntriesBetweenUseCase,
    private val updateInsulinEntryUseCase: UpdateInsulinEntryUseCase,
    private val deleteInsulinEntryUseCase: DeleteInsulinEntryUseCase
) : ViewModel() {

    private val _insulinTypes = MutableStateFlow<List<InsulinType>>(emptyList())
    val insulinTypes: StateFlow<List<InsulinType>> = _insulinTypes

    private val _insulinEntries = MutableStateFlow<List<InsulinEntry>>(emptyList())
    val insulinEntries: StateFlow<List<InsulinEntry>> = _insulinEntries

    private val _insulinEntriesWithTypes = MutableStateFlow<List<InsulinEntryWithTypeDomain>>(emptyList())
    val insulinEntriesWithTypes: StateFlow<List<InsulinEntryWithTypeDomain>> = _insulinEntriesWithTypes

    init {
        viewModelScope.launch {
            getAllInsulinTypesUseCase().collectLatest { types ->
                _insulinTypes.value = types
            }
        }

        viewModelScope.launch {
            getAllInsulinEntriesUseCase().collectLatest { entries ->
                _insulinEntries.value = entries
            }
        }

        viewModelScope.launch {
            getAllInsulinEntriesWithTypesUseCase().collectLatest { entriesWithTypes ->
                _insulinEntriesWithTypes.value = entriesWithTypes
            }
        }

    }

    fun addInsulinType(type: InsulinType, onComplete: (Int) -> Unit = {}) = viewModelScope.launch {
        val insertedId = insertInsulinTypeUseCase(type).toInt()
        onComplete(insertedId)
    }

    fun addAllInsulinTypes(types: List<InsulinType>) = viewModelScope.launch {
        insertAllInsulinTypesUseCase(types)
    }

    fun addInsulinEntry(entry: InsulinEntry, onComplete: () -> Unit = {}) = viewModelScope.launch {
        insertInsulinEntryUseCase(entry)
        onComplete()
    }

    fun updateInsulinEntry(entry: InsulinEntry) = viewModelScope.launch {
        updateInsulinEntryUseCase(entry)
    }

    fun deleteInsulinEntry(entry: InsulinEntry) = viewModelScope.launch {
        deleteInsulinEntryUseCase(entry)
    }
    fun addInsulinEntryWithNewType(
        typeName: String,
        typeCategory: String, // можно задать дефолт
        durationHours: Int,            // дефолтная длительность
        doseUnits: Double,
        unit: String,
        onComplete: () -> Unit = {}
    ) = viewModelScope.launch {
        // Вставляем новый тип
        val newTypeId = insertInsulinTypeUseCase(
            InsulinType(
                name = typeName,
                type = typeCategory,
                durationHours = durationHours
            )
        ).toInt() // Room вернёт новый ID

        // Создаём запись с этим типом
        val newEntry = InsulinEntry(
            insulinTypeId = newTypeId,
            doseUnits = doseUnits,
            unit = unit,
            timestamp = System.currentTimeMillis()
        )

        insertInsulinEntryUseCase(newEntry)

        // Callback
        onComplete()
    }
    suspend fun getAllEntriesOnce(): List<InsulinEntry> = getAllInsulinEntriesOnceUseCase()

    suspend fun getEntriesWithTypesOnce(): List<InsulinEntryWithTypeDomain> = getAllInsulinEntriesWithTypesOnceUseCase()

    suspend fun getEntriesBetween(start: Long, end: Long): List<InsulinEntry> =
        getInsulinEntriesBetweenUseCase(start, end)

    suspend fun getTypeById(id: Int): InsulinType? = getInsulinTypeByIdUseCase(id)

    suspend fun getTypeByName(name: String): InsulinType? = getInsulinTypeByNameUseCase(name)
}

class InsulinViewModelFactory(
    private val getAllInsulinTypesUseCase: GetAllInsulinTypesUseCase,
    private val insertInsulinTypeUseCase: InsertInsulinTypeUseCase,
    private val insertAllInsulinTypesUseCase: InsertAllInsulinTypesUseCase,
    private val getInsulinTypeByIdUseCase: GetInsulinTypeByIdUseCase,
    private val getInsulinTypeByNameUseCase: GetInsulinTypeByNameUseCase,
    private val insertInsulinEntryUseCase: InsertInsulinEntryUseCase,
    private val getAllInsulinEntriesUseCase: GetAllInsulinEntriesUseCase,
    private val getAllInsulinEntriesOnceUseCase: GetAllInsulinEntriesOnceUseCase,
    private val getAllInsulinEntriesWithTypesUseCase: GetAllInsulinEntriesWithTypesUseCase,
    private val getAllInsulinEntriesWithTypesOnceUseCase: GetAllInsulinEntriesWithTypesOnceUseCase,
    private val getInsulinEntriesBetweenUseCase: GetInsulinEntriesBetweenUseCase,
    private val updateInsulinEntryUseCase: UpdateInsulinEntryUseCase,
    private val deleteInsulinEntryUseCase: DeleteInsulinEntryUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InsulinViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InsulinViewModel(
                getAllInsulinTypesUseCase,
                insertInsulinTypeUseCase,
                insertAllInsulinTypesUseCase,
                getInsulinTypeByIdUseCase,
                getInsulinTypeByNameUseCase,
                insertInsulinEntryUseCase,
                getAllInsulinEntriesUseCase,
                getAllInsulinEntriesOnceUseCase,
                getAllInsulinEntriesWithTypesUseCase,
                getAllInsulinEntriesWithTypesOnceUseCase,
                getInsulinEntriesBetweenUseCase,
                updateInsulinEntryUseCase,
                deleteInsulinEntryUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}