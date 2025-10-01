package com.example.glucodialog

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.glucodialog.data.AppDatabase
import com.example.glucodialog.data.repository.UserProfileRepositoryImpl
import com.example.glucodialog.domain.repository.ActivityRepositoryImpl
import com.example.glucodialog.domain.usecase.activity.*
import com.example.glucodialog.ui.screen.ActivityEntryScreen
import com.example.glucodialog.ui.viewmodel.ActivityEntryViewModel
import com.example.glucodialog.ui.viewmodel.ActivityEntryViewModelFactory

class AddPhysicalActivity : AppCompatActivity() {

    private val viewModel: ActivityEntryViewModel by viewModels {
        val db = AppDatabase.getDatabase(this)
        val activityRepository = ActivityRepositoryImpl(db.activityDao())
        val userProfileRepository = UserProfileRepositoryImpl(db.userProfileDao())

        val getAllActivityTypesUseCase = GetAllActivityTypesUseCase(activityRepository)
        val insertActivityTypeUseCase = InsertActivityTypeUseCase(activityRepository)
        val insertAllActivityTypesUseCase = InsertAllActivityTypesUseCase(activityRepository)
        val getAllActivityEntriesUseCase = GetAllActivityEntriesUseCase(activityRepository)
        val getAllActivityEntriesWithTypesFlowUseCase = GetAllActivityEntriesWithTypesFlowUseCase(activityRepository)
        val getAllActivityEntriesOnceWithTypesUseCase = GetAllActivityEntriesOnceWithTypesUseCase(activityRepository)
        val getActivityByIdUseCase = GetActivityByIdUseCase(activityRepository)
        val getAllActivityEntriesOnceUseCase = GetAllActivityEntriesOnceUseCase(activityRepository)
        val getActivityTypeByNameUseCase = GetActivityTypeByNameUseCase(activityRepository)
        val getActivityEntriesBetweenUseCase = GetActivitiesBetweenUseCase(activityRepository)
        val insertActivityEntryUseCase = InsertActivityEntryUseCase(activityRepository)
        val updateActivityEntryUseCase = UpdateActivityEntryUseCase(activityRepository)
        val deleteActivityEntryUseCase = DeleteActivityEntryUseCase(activityRepository)

        ActivityEntryViewModelFactory(
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
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ActivityEntryScreen(
                viewModel = viewModel,
                onBack = { finish() }
            )
        }
    }
}