package com.example.glucodialog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.glucodialog.data.AppDatabase
import com.example.glucodialog.domain.repository.InsulinRepositoryImpl
import com.example.glucodialog.domain.usecase.insulin.*
import com.example.glucodialog.ui.screen.InsulinEntryScreen
import com.example.glucodialog.ui.viewmodel.InsulinViewModel
import com.example.glucodialog.ui.viewmodel.InsulinViewModelFactory
import com.example.glucodialog.ui.viewmodel.MainViewModel

class AddInsulinActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory(
            AppDatabase.getDatabase(this).userProfileDao()
        )
    }

    private val insulinViewModel: InsulinViewModel by viewModels {
        val db = AppDatabase.getDatabase(this)
        val repository = InsulinRepositoryImpl(db.insulinDao())

        InsulinViewModelFactory(
            getAllInsulinTypesUseCase = GetAllInsulinTypesUseCase(repository),
            insertInsulinTypeUseCase = InsertInsulinTypeUseCase(repository),
            insertAllInsulinTypesUseCase = InsertAllInsulinTypesUseCase(repository),
            getInsulinTypeByIdUseCase = GetInsulinTypeByIdUseCase(repository),
            getInsulinTypeByNameUseCase = GetInsulinTypeByNameUseCase(repository),
            insertInsulinEntryUseCase = InsertInsulinEntryUseCase(repository),
            getAllInsulinEntriesUseCase = GetAllInsulinEntriesUseCase(repository),
            getAllInsulinEntriesOnceUseCase = GetAllInsulinEntriesOnceUseCase(repository),
            getAllInsulinEntriesWithTypesUseCase = GetAllInsulinEntriesWithTypesUseCase(repository),
            getAllInsulinEntriesWithTypesOnceUseCase = GetAllInsulinEntriesWithTypesOnceUseCase(repository),
            getInsulinEntriesBetweenUseCase = GetInsulinEntriesBetweenUseCase(repository),
            updateInsulinEntryUseCase = UpdateInsulinEntryUseCase(repository),
            deleteInsulinEntryUseCase = DeleteInsulinEntryUseCase(repository)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val userProfile by mainViewModel.userProfile.collectAsState(initial = null)

            InsulinEntryScreen(
                viewModel = insulinViewModel,
                userProfile = userProfile,
                onBack = { finish() }
            )
        }
    }
}