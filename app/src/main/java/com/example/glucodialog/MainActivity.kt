package com.example.glucodialog

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import com.example.glucodialog.data.*
import com.example.glucodialog.data.relations.*
import com.example.glucodialog.ui.ProfileForm
import com.example.glucodialog.ui.RecordHistory
import com.example.glucodialog.ui.RecordTypeSelector
import com.example.glucodialog.ui.screens.Dashboard
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.navigation.compose.*
import com.example.glucodialog.ui.GlucoseEntryScreen
import com.example.glucodialog.ui.ProfileView
import com.example.glucodialog.ui.Routes
import com.example.glucodialog.ui.components.BottomNavigationBar

import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.glucodialog.data.*
import com.example.glucodialog.data.relations.*
import com.example.glucodialog.ui.*
import kotlinx.coroutines.flow.firstOrNull

class MainActivity : AppCompatActivity() {

    private val db by lazy { AppDatabase.getDatabase(this) }
    private val scope = MainScope()

    object Routes {
        const val DASHBOARD = "dashboard"
        const val PROFILE = "profile"
        const val PROFILE_FORM = "profile_form"
        const val RECORD_SELECTOR = "record_selector"
        const val RECORD_HISTORY = "record_history"
        const val GLUCOSE = "glucose"
        const val INSULIN = "insulin"
        const val ACTIVITY = "activity"
        const val MEAL = "meal"
        const val MEDICATION = "medication"
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var userProfile by remember { mutableStateOf<UserProfile?>(null) }
            var isLoading by remember { mutableStateOf(true) }

            val glucoseEntries by db.glucoseDao().getAllGlucoseEntries().collectAsState(initial = emptyList())
            val foodEntriesWithItems by db.foodDao().getAllFoodEntriesWithItemsFlow().collectAsState(initial = emptyList())
            val insulinEntriesWithTypes by db.insulinDao().getAllInsulinEntriesWithTypesFlow().collectAsState(initial = emptyList())
            val activityEntriesWithTypes by db.activityDao().getAllActivityEntriesWithTypesFlow().collectAsState(initial = emptyList())
            val medicationEntriesWithTypes by db.medicationDao().getAllMedicationEntriesWithTypesFlow().collectAsState(initial = emptyList())

            LaunchedEffect(Unit) {
                userProfile = db.userProfileDao().getUserProfile().firstOrNull()
                isLoading = false
            }

            val navController = rememberNavController()

            Scaffold(
                topBar = {
                    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                    TopAppBar(
                        title = {
                            Column {
                                Text(
                                    when (currentRoute) {
                                        Routes.DASHBOARD -> "📊 Панель управления"
                                        Routes.PROFILE -> "👤 Профиль"
                                        Routes.PROFILE_FORM -> "✏️ Редактирование профиля"
                                        Routes.RECORD_SELECTOR -> "➕ Добавить запись"
                                        Routes.RECORD_HISTORY -> "📜 История записей"
                                        else -> "💊 GlucoDiaLog"
                                    },
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    when (currentRoute) {
                                        Routes.DASHBOARD -> "Обзор ваших показателей"
                                        Routes.PROFILE -> "Просмотр информации о пользователе"
                                        Routes.PROFILE_FORM -> "Заполните или измените данные профиля"
                                        Routes.RECORD_SELECTOR -> "Выберите тип записи для добавления"
                                        Routes.RECORD_HISTORY -> "История всех записей"
                                        else -> ""
                                    },
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        },
                        colors = TopAppBarDefaults.smallTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                },
                bottomBar = {
                    if (userProfile != null) {
                        BottomNavigationBar(navController = navController)
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {

                    if (isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {

                        if (userProfile == null) {
                            var currentProfile by remember {
                                mutableStateOf(
                                    UserProfile(
                                        email = "",
                                        name = "",
                                        gender = "",
                                        weight = 0.0,
                                        height = 0.0,
                                        diabetesType = "",
                                        targetGlucoseLow = 0.0,
                                        targetGlucoseHigh = 0.0,
                                        glucoseUnit = "",
                                        bolusInsulin = "",
                                        bolusDose = 0.0,
                                        basalInsulin = "",
                                        basalDose = 0.0,
                                        medication = "",
                                        medicationDose = 0.0,
                                        medicationUnit = "",
                                        medicationTimeMinutesFromMidnight = 0
                                    )
                                )
                            }

                            ProfileForm(
                                profile = currentProfile,
                                onUpdateProfile = { updatedProfile ->
                                    scope.launch {
                                        db.userProfileDao().insertUserProfile(updatedProfile)
                                        userProfile = updatedProfile
                                    }
                                },
                                onBack = { finish() }
                            )

                        } else {
                            NavHost(
                                navController = navController,
                                startDestination = Routes.DASHBOARD
                            ) {

                                composable(Routes.DASHBOARD) {
                                    Dashboard(
                                        glucoseEntries = glucoseEntries,
                                        foodEntriesWithItems = foodEntriesWithItems,
                                        insulinEntriesWithTypes = insulinEntriesWithTypes,
                                        activityEntriesWithTypes = activityEntriesWithTypes,
                                        medicationEntriesWithTypes = medicationEntriesWithTypes
                                    )
                                }

                                composable(Routes.PROFILE) {
                                    ProfileView(
                                        profile = userProfile!!,
                                        onEdit = { navController.navigate(Routes.PROFILE_FORM) }
                                    )
                                }

                                composable(Routes.PROFILE_FORM) {
                                    ProfileForm(
                                        profile = userProfile!!,
                                        onUpdateProfile = { updatedProfile ->
                                            scope.launch {
                                                db.userProfileDao().insertUserProfile(updatedProfile)
                                                userProfile = updatedProfile
                                                navController.popBackStack()
                                            }
                                        },
                                        onBack = { navController.popBackStack() }
                                    )
                                }

                                composable(Routes.RECORD_SELECTOR) {
                                    RecordTypeSelector(
                                        onSelectScreen = { route -> navController.navigate(route) }
                                    )
                                }

                                composable(Routes.GLUCOSE) {
                                    val context = LocalContext.current
                                    val db = AppDatabase.getDatabase(context)
                                    val glucoseDao = db.glucoseDao()
                                    val userProfileDao = db.userProfileDao()
                                    val userProfile by userProfileDao.getUserProfile().collectAsState(initial = null)

                                    GlucoseEntryScreen(
                                        userProfile = userProfile,
                                        glucoseDao = glucoseDao,
                                        onBack = { navController.popBackStack() }
                                    )
                                }

                                composable(Routes.INSULIN) {
                                    val context = LocalContext.current
                                    val db = AppDatabase.getDatabase(context)
                                    val insulinDao = db.insulinDao()
                                    val userProfileDao = db.userProfileDao()
                                    val userProfile by userProfileDao.getUserProfile().collectAsState(initial = null)

                                    InsulinEntryScreen(
                                        userProfile = userProfile,
                                        insulinDao = insulinDao,
                                        onBack = { navController.popBackStack() }
                                    )
                                }

                                composable(Routes.MEDICATION) {
                                    val context = LocalContext.current
                                    val db = AppDatabase.getDatabase(context)
                                    val medicationDao = db.medicationDao()
                                    val userProfileDao = db.userProfileDao()
                                    val userProfile by userProfileDao.getUserProfile().collectAsState(initial = null)

                                    MedicationEntryScreen(
                                        userProfile = userProfile,
                                        medicationDao = medicationDao,
                                        onBack = { navController.popBackStack() }
                                    )
                                }

                                composable(Routes.MEAL) {
                                    val context = LocalContext.current
                                    val db = AppDatabase.getDatabase(context)
                                    val foodDao = db.foodDao()
                                    val userProfileDao = db.userProfileDao()
                                    val userProfile by userProfileDao.getUserProfile().collectAsState(initial = null)

                                    MealEntryScreen(
                                        userProfile = userProfile,
                                        foodDao = foodDao,
                                        onBack = { navController.popBackStack() }
                                    )
                                }

                                composable(Routes.ACTIVITY) {
                                    val context = LocalContext.current
                                    val db = AppDatabase.getDatabase(context)
                                    val activityDao = db.activityDao()
                                    val userProfileDao = db.userProfileDao()
                                    val userProfile by userProfileDao.getUserProfile().collectAsState(initial = null)

                                    ActivityEntryScreen(
                                        userProfile = userProfile,
                                        activityDao = activityDao,
                                        onBack = { navController.popBackStack() }
                                    )
                                }

                                composable(Routes.RECORD_HISTORY) {
                                    val context = LocalContext.current
                                    val db = AppDatabase.getDatabase(context)

                                    val activityTypes by db.activityDao().getAllActivityTypes().collectAsState(initial = emptyList())
                                    val foodItems by db.foodDao().getAllFoodItems().collectAsState(initial = emptyList())
                                    val insulinTypes by db.insulinDao().getAllInsulinTypes().collectAsState(initial = emptyList())
                                    val medicationTypes by db.medicationDao().getAllMedicationTypes().collectAsState(initial = emptyList())

                                    RecordHistory(
                                        onSelectScreen = { route -> navController.navigate(route) },
                                        glucoseReadings = glucoseEntries,
                                        meals = foodEntriesWithItems.map { it.entry },
                                        insulinRecords = insulinEntriesWithTypes.map { it.entry },
                                        activityRecords = activityEntriesWithTypes.map { it.entry },
                                        medicationRecords = medicationEntriesWithTypes.map { it.entry },
                                        userProfile = userProfile,
                                        activityTypes = activityTypes,
                                        foodItems = foodItems,
                                        insulinTypes = insulinTypes,
                                        medicationTypes = medicationTypes
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}