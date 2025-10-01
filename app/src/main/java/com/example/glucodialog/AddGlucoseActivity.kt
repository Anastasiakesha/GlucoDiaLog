package com.example.glucodialog

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.glucodialog.data.AppDatabase
import com.example.glucodialog.domain.repository.GlucoseRepositoryImpl
import com.example.glucodialog.domain.usecase.glucose.*
import com.example.glucodialog.ui.screen.GlucoseEntryScreen
import com.example.glucodialog.ui.viewmodel.GlucoseViewModel
import com.example.glucodialog.ui.viewmodel.GlucoseViewModelFactory
import com.example.glucodialog.ui.viewmodel.MainViewModel

class AddGlucoseActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory(
            AppDatabase.getDatabase(this).userProfileDao()
        )
    }

    private val glucoseViewModel: GlucoseViewModel by viewModels {
        val db = AppDatabase.getDatabase(this)
        val repository = GlucoseRepositoryImpl(db.glucoseDao())

        GlucoseViewModelFactory(
            getAllGlucoseEntriesUseCase = GetAllGlucoseEntriesUseCase(repository),
            getAllGlucoseEntriesOnceUseCase = GetAllGlucoseEntriesOnceUseCase(repository),
            getGlucoseEntriesBetweenUseCase = GetGlucoseEntriesBetweenUseCase(repository),
            insertGlucoseUseCase = InsertGlucoseUseCase(repository),
            updateGlucoseEntryUseCase = UpdateGlucoseEntryUseCase(repository),
            deleteGlucoseEntryUseCase = DeleteGlucoseEntryUseCase(repository),
            updateNoteForEntryUseCase = UpdateNoteForEntryUseCase(repository)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val userProfile by mainViewModel.userProfile.collectAsState(initial = null)

            GlucoseEntryScreen(
                viewModel = glucoseViewModel,
                userProfile = userProfile,
                onBack = { finish() }
            )
        }
    }
}