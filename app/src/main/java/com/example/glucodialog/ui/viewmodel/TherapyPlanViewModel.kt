package com.example.glucodialog.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.glucodialog.data.local.*
import com.example.glucodialog.data.relations.TherapyPlanWithDetails
import com.example.glucodialog.data.repository.InsulinDao
import com.example.glucodialog.data.repository.MedicationDao
import com.example.glucodialog.data.repository.TherapyPlanDao
import com.example.glucodialog.utils.ReminderScheduler
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

    fun finishCurrentPlan(context: Context, planDetails: TherapyPlanWithDetails) = viewModelScope.launch {
        planDetails.insulinPlans.forEach { ip ->
            ReminderScheduler.cancelReminder(context, ip.plan.id)
        }

        planDetails.medicationPlans.forEach { mp ->
            ReminderScheduler.cancelReminder(context, mp.plan.id + 10000)
        }

        therapyPlanDao.finishPlan(planDetails.plan.id, System.currentTimeMillis())
    }

    fun addInsulinToPlan(context: Context, planId: Int, typeId: Int, dose: Double, timeMinutes: Int?) = viewModelScope.launch {
        val entry = InsulinTherapyPlan(
            therapyPlanId = planId, insulinTypeId = typeId, dose = dose, reminderTimeMinutes = timeMinutes
        )
        val insertedId = therapyPlanDao.insertInsulinPlan(entry).toInt()

        if (timeMinutes != null) {
            val type = insulinDao.getInsulinById(typeId)
            ReminderScheduler.scheduleReminder(
                context = context,
                id = insertedId,
                timeMinutes = timeMinutes,
                title = "Время приема инсулина",
                message = "Пора ввести инсулин ${type?.name ?: ""}, дозировка: $dose ед."
            )
        }
    }

    fun addMedicationToPlan(context: Context, planId: Int, typeId: Int, dose: String, timeMinutes: Int?) = viewModelScope.launch {
        val entry = MedicationTherapyPlan(
            therapyPlanId = planId, medicationTypeId = typeId, dose = dose, reminderTimeMinutes = timeMinutes
        )
        val insertedId = therapyPlanDao.insertMedicationPlan(entry).toInt()

        if (timeMinutes != null) {
            val type = medicationDao.getMedicationById(typeId)
            ReminderScheduler.scheduleReminder(
                context = context,
                id = insertedId + 10000,
                timeMinutes = timeMinutes,
                title = "Прием препарата",
                message = "Пора принять лекарство ${type?.name ?: ""}, доза: $dose"
            )
        }
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