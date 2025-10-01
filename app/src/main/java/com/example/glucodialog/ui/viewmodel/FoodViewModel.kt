package com.example.glucodialog.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.glucodialog.domain.model.FoodEntry
import com.example.glucodialog.domain.model.FoodType
import com.example.glucodialog.domain.model.FoodEntryWithTypeDomain
import com.example.glucodialog.domain.usecase.food.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FoodViewModel(
    private val insertFoodEntryUseCase: InsertFoodEntryUseCase,
    private val updateFoodEntryUseCase: UpdateFoodEntryUseCase,
    private val deleteFoodEntryUseCase: DeleteFoodEntryUseCase,
    private val getAllFoodEntriesUseCase: GetAllFoodEntriesUseCase,
    private val getAllFoodEntriesOnceUseCase: GetAllFoodEntriesOnceUseCase,
    private val getAllFoodEntriesWithTypesUseCase: GetAllFoodEntriesWithTypesUseCase,
    private val getAllFoodEntriesWithTypesOnceUseCase: GetAllFoodEntriesWithTypesOnceUseCase,
    private val getFoodEntriesBetweenUseCase: GetFoodEntriesBetweenUseCase,
    private val getAllFoodTypesUseCase: GetAllFoodTypesUseCase,
    private val insertFoodTypeUseCase: InsertFoodTypeUseCase,
    private val insertAllFoodTypesUseCase: InsertAllFoodTypesUseCase,
    private val getFoodTypeByIdUseCase: GetFoodTypeByIdUseCase,
    private val getFoodTypeByNameUseCase: GetFoodTypeByNameUseCase
) : ViewModel() {

    private val _foodEntries = MutableStateFlow<List<FoodEntry>>(emptyList())
    val foodEntries: StateFlow<List<FoodEntry>> = _foodEntries

    private val _foodEntriesWithTypes = MutableStateFlow<List<FoodEntryWithTypeDomain>>(emptyList())
    val foodEntriesWithTypes: StateFlow<List<FoodEntryWithTypeDomain>> = _foodEntriesWithTypes

    private val _foodTypes = MutableStateFlow<List<FoodType>>(emptyList())
    val foodTypes: StateFlow<List<FoodType>> = _foodTypes

    init {
        viewModelScope.launch {
            getAllFoodEntriesUseCase().collectLatest { entries ->
                _foodEntries.value = entries
            }
        }

        viewModelScope.launch {
            getAllFoodEntriesWithTypesUseCase().collectLatest { entries ->
                _foodEntriesWithTypes.value = entries
            }
        }

        viewModelScope.launch {
            getAllFoodTypesUseCase().collectLatest { types ->
                _foodTypes.value = types
            }
        }
    }

    fun addFoodEntry(entry: FoodEntry, onComplete: () -> Unit = {}) = viewModelScope.launch {
        insertFoodEntryUseCase(entry)
        onComplete()
    }

    fun updateEntry(entry: FoodEntry) = viewModelScope.launch {
        updateFoodEntryUseCase(entry)
    }

    fun deleteEntry(entry: FoodEntry) = viewModelScope.launch {
        deleteFoodEntryUseCase(entry)
    }

    suspend fun getAllEntriesOnce(): List<FoodEntry> = getAllFoodEntriesOnceUseCase()

    suspend fun getEntriesBetween(start: Long, end: Long): List<FoodEntry> =
        getFoodEntriesBetweenUseCase(start, end)

    suspend fun getAllEntriesWithTypesOnce(): List<FoodEntryWithTypeDomain> =
        getAllFoodEntriesWithTypesOnceUseCase()

    fun addFoodType(type: FoodType) = viewModelScope.launch {
        insertFoodTypeUseCase(type)
    }

    fun addAllFoodTypes(types: List<FoodType>) = viewModelScope.launch {
        insertAllFoodTypesUseCase(types)
    }

    suspend fun getFoodTypeById(id: Int): FoodType? = getFoodTypeByIdUseCase(id)

    suspend fun getFoodTypeByName(name: String): FoodType? = getFoodTypeByNameUseCase(name)
}

class FoodViewModelFactory(
    private val insertFoodEntryUseCase: InsertFoodEntryUseCase,
    private val updateFoodEntryUseCase: UpdateFoodEntryUseCase,
    private val deleteFoodEntryUseCase: DeleteFoodEntryUseCase,
    private val getAllFoodEntriesUseCase: GetAllFoodEntriesUseCase,
    private val getAllFoodEntriesOnceUseCase: GetAllFoodEntriesOnceUseCase,
    private val getAllFoodEntriesWithTypesUseCase: GetAllFoodEntriesWithTypesUseCase,
    private val getAllFoodEntriesWithTypesOnceUseCase: GetAllFoodEntriesWithTypesOnceUseCase,
    private val getFoodEntriesBetweenUseCase: GetFoodEntriesBetweenUseCase,
    private val getAllFoodTypesUseCase: GetAllFoodTypesUseCase,
    private val insertFoodTypeUseCase: InsertFoodTypeUseCase,
    private val insertAllFoodTypesUseCase: InsertAllFoodTypesUseCase,
    private val getFoodTypeByIdUseCase: GetFoodTypeByIdUseCase,
    private val getFoodTypeByNameUseCase: GetFoodTypeByNameUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoodViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FoodViewModel(
                insertFoodEntryUseCase,
                updateFoodEntryUseCase,
                deleteFoodEntryUseCase,
                getAllFoodEntriesUseCase,
                getAllFoodEntriesOnceUseCase,
                getAllFoodEntriesWithTypesUseCase,
                getAllFoodEntriesWithTypesOnceUseCase,
                getFoodEntriesBetweenUseCase,
                getAllFoodTypesUseCase,
                insertFoodTypeUseCase,
                insertAllFoodTypesUseCase,
                getFoodTypeByIdUseCase,
                getFoodTypeByNameUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}