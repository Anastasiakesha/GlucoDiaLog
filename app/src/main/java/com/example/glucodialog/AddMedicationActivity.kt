package com.example.glucodialog

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.glucodialog.data.AppDatabase
import com.example.glucodialog.domain.repository.MedicationRepositoryImpl
import com.example.glucodialog.domain.usecase.medication.DeleteMedicationEntryUseCase
import com.example.glucodialog.domain.usecase.medication.GetAllMedicationEntriesOnceUseCase
import com.example.glucodialog.domain.usecase.medication.GetAllMedicationEntriesOnceWithTypesUseCase
import com.example.glucodialog.domain.usecase.medication.GetAllMedicationEntriesUseCase
import com.example.glucodialog.domain.usecase.medication.GetAllMedicationEntriesWithTypesUseCase
import com.example.glucodialog.domain.usecase.medication.GetAllMedicationTypesUseCase
import com.example.glucodialog.domain.usecase.medication.GetMedicationByIdUseCase
import com.example.glucodialog.domain.usecase.medication.GetMedicationEntriesBetweenUseCase
import com.example.glucodialog.domain.usecase.medication.GetMedicationTypeByNameUseCase
import com.example.glucodialog.domain.usecase.medication.InsertAllMedicationTypesUseCase
import com.example.glucodialog.domain.usecase.medication.InsertMedicationEntryUseCase
import com.example.glucodialog.domain.usecase.medication.InsertMedicationTypeUseCase
import com.example.glucodialog.domain.usecase.medication.UpdateMedicationEntryUseCase
import com.example.glucodialog.ui.screen.MedicationEntryScreen
import com.example.glucodialog.ui.viewmodel.MedicationViewModel
import com.example.glucodialog.ui.viewmodel.MedicationViewModelFactory
import kotlin.getValue

class AddMedicationActivity : AppCompatActivity() {

    private val viewModel: MedicationViewModel by viewModels {
        val db = AppDatabase.getDatabase(this)
        val medicationRepository = MedicationRepositoryImpl(db.medicationDao())

        MedicationViewModelFactory(
            getAllMedicationTypesUseCase = GetAllMedicationTypesUseCase(medicationRepository),
            insertMedicationTypeUseCase = InsertMedicationTypeUseCase(medicationRepository),
            insertAllMedicationTypesUseCase = InsertAllMedicationTypesUseCase(medicationRepository),
            getAllMedicationEntriesUseCase = GetAllMedicationEntriesUseCase(medicationRepository),
            getAllMedicationEntriesWithTypesUseCase = GetAllMedicationEntriesWithTypesUseCase(
                medicationRepository
            ),
            getAllMedicationEntriesOnceWithTypesUseCase = GetAllMedicationEntriesOnceWithTypesUseCase(
                medicationRepository
            ),
            getMedicationByIdUseCase = GetMedicationByIdUseCase(medicationRepository),
            getAllMedicationEntriesOnceUseCase = GetAllMedicationEntriesOnceUseCase(
                medicationRepository
            ),
            getMedicationTypeByNameUseCase = GetMedicationTypeByNameUseCase(medicationRepository),
            getMedicationEntriesBetweenUseCase = GetMedicationEntriesBetweenUseCase(
                medicationRepository
            ),
            insertMedicationEntryUseCase = InsertMedicationEntryUseCase(medicationRepository),
            updateMedicationEntryUseCase = UpdateMedicationEntryUseCase(medicationRepository),
            deleteMedicationEntryUseCase = DeleteMedicationEntryUseCase(medicationRepository)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MedicationEntryScreen(
                viewModel = viewModel,
                onBack = { finish() }
            )
        }
    }
}