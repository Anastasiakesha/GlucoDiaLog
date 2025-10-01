package com.example.glucodialog.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.glucodialog.domain.model.ActivityEntry
import com.example.glucodialog.domain.model.ActivityEntryWithTypeDomain
import com.example.glucodialog.domain.model.ActivityType
import com.example.glucodialog.domain.usecase.activity.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ActivityEntryViewModel(
    private val getAllActivityTypesUseCase: GetAllActivityTypesUseCase,
    private val insertActivityTypeUseCase: InsertActivityTypeUseCase,
    private val insertAllActivityTypesUseCase: InsertAllActivityTypesUseCase,
    private val getAllActivityEntriesUseCase: GetAllActivityEntriesUseCase,
    private val getAllActivityEntriesWithTypesFlowUseCase: GetAllActivityEntriesWithTypesFlowUseCase,
    private val getAllActivityEntriesOnceWithTypesUseCase: GetAllActivityEntriesOnceWithTypesUseCase,
    private val getActivityByIdUseCase: GetActivityByIdUseCase,
    private val getAllActivityEntriesOnceUseCase: GetAllActivityEntriesOnceUseCase,
    private val getActivityTypeByNameUseCase: GetActivityTypeByNameUseCase,
    private val getActivityEntriesBetweenUseCase: GetActivitiesBetweenUseCase,
    private val insertActivityEntryUseCase: InsertActivityEntryUseCase,
    private val updateActivityEntryUseCase: UpdateActivityEntryUseCase,
    private val deleteActivityEntryUseCase: DeleteActivityEntryUseCase
) : ViewModel() {

    private val _activityTypes = MutableStateFlow<List<ActivityType>>(emptyList())
    val activityTypes: StateFlow<List<ActivityType>> = _activityTypes

    private val _activityEntries = MutableStateFlow<List<ActivityEntry>>(emptyList())
    val activityEntries: StateFlow<List<ActivityEntry>> = _activityEntries

    init {
        viewModelScope.launch {
            getAllActivityTypesUseCase().collectLatest { types ->
                _activityTypes.value = types
            }
        }

        viewModelScope.launch {
            getAllActivityEntriesWithTypesFlowUseCase().collectLatest { entries ->
                _activityEntries.value = entries.map { it.entry }
            }
        }
    }

    fun addActivityType(type: ActivityType) = viewModelScope.launch {
        insertActivityTypeUseCase(type)
    }

    fun addAllActivityTypes(types: List<ActivityType>) = viewModelScope.launch {
        insertAllActivityTypesUseCase(types)
    }

    fun addActivityEntry(entry: ActivityEntry) = viewModelScope.launch {
        insertActivityEntryUseCase(entry)
    }

    fun updateActivityEntry(entry: ActivityEntry) = viewModelScope.launch {
        updateActivityEntryUseCase(entry)
    }

    fun deleteActivityEntry(entry: ActivityEntry) = viewModelScope.launch {
        deleteActivityEntryUseCase(entry)
    }

    suspend fun getActivityById(id: Int): ActivityType? = getActivityByIdUseCase(id)

    suspend fun getAllActivityEntriesOnce(): List<ActivityEntry> = getAllActivityEntriesOnceUseCase()

    suspend fun getAllActivityEntriesOnceWithTypes(): List<ActivityEntryWithTypeDomain> =
        getAllActivityEntriesOnceWithTypesUseCase()

    suspend fun getActivityTypeByName(name: String): ActivityType? = getActivityTypeByNameUseCase(name)

    suspend fun getActivityEntriesBetween(start: Long, end: Long): List<ActivityEntry> =
        getActivityEntriesBetweenUseCase(start, end)

    fun getAllActivityEntriesWithTypesFlow() = getAllActivityEntriesWithTypesFlowUseCase()
}

class ActivityEntryViewModelFactory(
    private val getAllActivityTypesUseCase: GetAllActivityTypesUseCase,
    private val insertActivityTypeUseCase: InsertActivityTypeUseCase,
    private val insertAllActivityTypesUseCase: InsertAllActivityTypesUseCase,
    private val getAllActivityEntriesUseCase: GetAllActivityEntriesUseCase,
    private val getAllActivityEntriesWithTypesFlowUseCase: GetAllActivityEntriesWithTypesFlowUseCase,
    private val getAllActivityEntriesOnceWithTypesUseCase: GetAllActivityEntriesOnceWithTypesUseCase,
    private val getActivityByIdUseCase: GetActivityByIdUseCase,
    private val getAllActivityEntriesOnceUseCase: GetAllActivityEntriesOnceUseCase,
    private val getActivityTypeByNameUseCase: GetActivityTypeByNameUseCase,
    private val getActivityEntriesBetweenUseCase: GetActivitiesBetweenUseCase,
    private val insertActivityEntryUseCase: InsertActivityEntryUseCase,
    private val updateActivityEntryUseCase: UpdateActivityEntryUseCase,
    private val deleteActivityEntryUseCase: DeleteActivityEntryUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActivityEntryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ActivityEntryViewModel(
                getAllActivityTypesUseCase,
                insertActivityTypeUseCase,
                insertAllActivityTypesUseCase,
                getAllActivityEntriesUseCase,
                getAllActivityEntriesWithTypesFlowUseCase,
                getAllActivityEntriesOnceWithTypesUseCase,
                getActivityByIdUseCase,
                getAllActivityEntriesOnceUseCase,
                getActivityTypeByNameUseCase,
                getActivityEntriesBetweenUseCase,
                insertActivityEntryUseCase,
                updateActivityEntryUseCase,
                deleteActivityEntryUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}