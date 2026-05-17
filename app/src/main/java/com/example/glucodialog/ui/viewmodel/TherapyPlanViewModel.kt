package com.example.glucodialog.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.glucodialog.data.local.*
import com.example.glucodialog.data.relations.TherapyPlanWithDetails
import com.example.glucodialog.data.repository.InsulinDao
import com.example.glucodialog.data.repository.MedicationDao
import com.example.glucodialog.data.repository.TherapyPlanDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TherapyPlanViewModel(
    private val therapyPlanDao: TherapyPlanDao,
    private val insulinDao: InsulinDao,
    private val medicationDao: MedicationDao
) : ViewModel() {

    private val _userId = MutableStateFlow<Int?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val activePlan: StateFlow<TherapyPlanWithDetails?> = _userId
        .filterNotNull()
        .flatMapLatest { id -> therapyPlanDao.getActivePlanWithDetailsFlow(id) }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val insulinTypes = insulinDao.getAllInsulinTypes()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val medicationTypes = medicationDao.getAllMedicationTypes()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setUserId(id: Int) {
        _userId.value = id
    }

    fun createNewPlan(userId: Int) = viewModelScope.launch {
        val newPlan = TherapyPlan(userId = userId, startDate = System.currentTimeMillis())
        therapyPlanDao.insertTherapyPlan(newPlan)
    }

    fun finishCurrentPlan(planId: Int) = viewModelScope.launch {
        therapyPlanDao.finishPlan(planId, System.currentTimeMillis())
    }

    fun addInsulinToPlan(planId: Int, typeId: Int, dose: Double, timeMinutes: Int?) = viewModelScope.launch {
        val entry = InsulinTherapyPlan(
            therapyPlanId = planId, insulinTypeId = typeId, dose = dose, reminderTimeMinutes = timeMinutes
        )
        therapyPlanDao.insertInsulinPlan(entry)
    }

    fun addMedicationToPlan(planId: Int, typeId: Int, dose: String, timeMinutes: Int?) = viewModelScope.launch {
        val entry = MedicationTherapyPlan(
            therapyPlanId = planId, medicationTypeId = typeId, dose = dose, reminderTimeMinutes = timeMinutes
        )
        therapyPlanDao.insertMedicationPlan(entry)
    }
}

class TherapyPlanViewModelFactory(
    private val therapyPlanDao: TherapyPlanDao,
    private val insulinDao: InsulinDao,
    private val medicationDao: MedicationDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TherapyPlanViewModel(therapyPlanDao, insulinDao, medicationDao) as T
    }
}