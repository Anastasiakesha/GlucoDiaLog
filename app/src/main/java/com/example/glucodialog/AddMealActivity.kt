package com.example.glucodialog

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.glucodialog.data.AppDatabase
import com.example.glucodialog.domain.repository.FoodRepositoryImpl
import com.example.glucodialog.domain.usecase.food.*
import com.example.glucodialog.ui.screen.MealEntryScreen
import com.example.glucodialog.ui.viewmodel.FoodViewModel
import com.example.glucodialog.ui.viewmodel.FoodViewModelFactory
import  com.example.glucodialog.ui.viewmodel.MainViewModel

class AddMealActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory(
            AppDatabase.getDatabase(this).userProfileDao()
        )
    }
    private val viewModel: FoodViewModel by viewModels {
        val db = AppDatabase.getDatabase(this)

        val foodRepository = FoodRepositoryImpl(db.foodDao())


        FoodViewModelFactory(
            insertFoodEntryUseCase = InsertFoodEntryUseCase(foodRepository),
            updateFoodEntryUseCase = UpdateFoodEntryUseCase(foodRepository),
            deleteFoodEntryUseCase = DeleteFoodEntryUseCase(foodRepository),
            getAllFoodEntriesUseCase = GetAllFoodEntriesUseCase(foodRepository),
            getAllFoodEntriesOnceUseCase = GetAllFoodEntriesOnceUseCase(foodRepository),
            getAllFoodEntriesWithTypesUseCase = GetAllFoodEntriesWithTypesUseCase(foodRepository),
            getAllFoodEntriesWithTypesOnceUseCase = GetAllFoodEntriesWithTypesOnceUseCase(foodRepository),
            getFoodEntriesBetweenUseCase = GetFoodEntriesBetweenUseCase(foodRepository),
            getAllFoodTypesUseCase = GetAllFoodTypesUseCase(foodRepository),
            insertFoodTypeUseCase = InsertFoodTypeUseCase(foodRepository),
            insertAllFoodTypesUseCase = InsertAllFoodTypesUseCase(foodRepository),
            getFoodTypeByIdUseCase = GetFoodTypeByIdUseCase(foodRepository),
            getFoodTypeByNameUseCase = GetFoodTypeByNameUseCase(foodRepository),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val userProfile by mainViewModel.userProfile.collectAsState(initial = null)

            MealEntryScreen(
                viewModel = viewModel,
                onBack = { finish() },
                userProfile = userProfile
            )
        }
    }
}