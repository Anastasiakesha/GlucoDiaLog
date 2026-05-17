package com.example.glucodialog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.glucodialog.data.AppDatabase
import com.example.glucodialog.domain.model.ActivityEntryWithTypeDomain
import com.example.glucodialog.domain.model.FoodEntryWithTypeDomain
import com.example.glucodialog.domain.model.InsulinEntryWithTypeDomain
import com.example.glucodialog.domain.model.MedicationEntryWithTypeDomain
import com.example.glucodialog.domain.model.UserProfile
import com.example.glucodialog.domain.repository.ActivityRepositoryImpl
import com.example.glucodialog.domain.repository.BloodPressureRepositoryImpl
import com.example.glucodialog.domain.repository.FoodRepositoryImpl
import com.example.glucodialog.domain.repository.GlucoseRepositoryImpl
import com.example.glucodialog.domain.repository.InsulinRepositoryImpl
import com.example.glucodialog.domain.repository.MedicationRepositoryImpl
import com.example.glucodialog.domain.usecase.activity.*
import com.example.glucodialog.domain.usecase.bloodpressure.DeleteBloodPressureEntryUseCase
import com.example.glucodialog.domain.usecase.bloodpressure.GetAllBloodPressureUseCase
import com.example.glucodialog.domain.usecase.bloodpressure.GetBloodPressureEntryByIdUseCase
import com.example.glucodialog.domain.usecase.bloodpressure.InsertBloodPressureEntryUseCase
import com.example.glucodialog.domain.usecase.bloodpressure.UpdateBloodPressureUseCase
import com.example.glucodialog.domain.usecase.food.*
import com.example.glucodialog.domain.usecase.medication.*
import com.example.glucodialog.domain.usecase.glucose.*
import com.example.glucodialog.domain.usecase.insulin.*
import com.example.glucodialog.ui.components.BottomNavigationBar
import com.example.glucodialog.ui.screen.*
import com.example.glucodialog.ui.screen.Dashboard
import com.example.glucodialog.ui.theme.GlucoDialogTheme
import com.example.glucodialog.ui.viewmodel.ActivityEntryViewModel
import com.example.glucodialog.ui.viewmodel.ActivityEntryViewModelFactory
import com.example.glucodialog.ui.viewmodel.BloodPressureViewModel
import com.example.glucodialog.ui.viewmodel.BloodPressureViewModelFactory
import com.example.glucodialog.ui.viewmodel.FoodViewModel
import com.example.glucodialog.ui.viewmodel.FoodViewModelFactory
import com.example.glucodialog.ui.viewmodel.GlucoseViewModel
import com.example.glucodialog.ui.viewmodel.GlucoseViewModelFactory
import com.example.glucodialog.ui.viewmodel.InsulinViewModel
import com.example.glucodialog.ui.viewmodel.InsulinViewModelFactory
import com.example.glucodialog.ui.viewmodel.MainViewModel
import com.example.glucodialog.ui.viewmodel.MainViewModel.MainViewModelFactory
import com.example.glucodialog.ui.viewmodel.MedicationViewModel
import com.example.glucodialog.ui.viewmodel.MedicationViewModelFactory
import com.example.glucodialog.ui.viewmodel.TherapyPlanViewModel
import com.example.glucodialog.ui.viewmodel.TherapyPlanViewModelFactory


class MainActivity : ComponentActivity() {

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
        const val BLOOD_PRESSURE = "blood_pressure"
        const val THERAPY_PLAN = "therapy_plan"
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getDatabase(this)
        setContent{
            GlucoDialogTheme {
                val viewModel: MainViewModel = viewModel(
                    factory = MainViewModelFactory(db.userProfileDao())
                )

                val userProfile by viewModel.userProfile.collectAsState()
                val isLoading by viewModel.isLoading.collectAsState()

                val foodRepository = FoodRepositoryImpl(db.foodDao())
                val foodViewModel: FoodViewModel = viewModel(
                    factory = FoodViewModelFactory(
                        insertFoodEntryUseCase = InsertFoodEntryUseCase(
                            foodRepository
                        ),
                        updateFoodEntryUseCase = UpdateFoodEntryUseCase(
                            foodRepository
                        ),
                        deleteFoodEntryUseCase = DeleteFoodEntryUseCase(
                            foodRepository
                        ),
                        getAllFoodEntriesUseCase = GetAllFoodEntriesUseCase(
                            foodRepository
                        ),
                        getAllFoodEntriesOnceUseCase = GetAllFoodEntriesOnceUseCase(
                            foodRepository
                        ),
                        getAllFoodEntriesWithTypesUseCase = GetAllFoodEntriesWithTypesUseCase(
                            foodRepository
                        ),
                        getAllFoodEntriesWithTypesOnceUseCase = GetAllFoodEntriesWithTypesOnceUseCase(
                            foodRepository
                        ),
                        getFoodEntriesBetweenUseCase = GetFoodEntriesBetweenUseCase(
                            foodRepository
                        ),
                        getAllFoodTypesUseCase = GetAllFoodTypesUseCase(
                            foodRepository
                        ),
                        insertFoodTypeUseCase = InsertFoodTypeUseCase(
                            foodRepository
                        ),
                        insertAllFoodTypesUseCase = InsertAllFoodTypesUseCase(
                            foodRepository
                        ),
                        getFoodTypeByIdUseCase = GetFoodTypeByIdUseCase(
                            foodRepository
                        ),
                        getFoodTypeByNameUseCase = GetFoodTypeByNameUseCase(
                            foodRepository
                        ),
                        getFoodEntryByIdUseCase = GetFoodEntryByIdUseCase(
                            foodRepository
                        )
                    )
                )
                val meals by foodViewModel.foodEntries.collectAsState()
                val foodItems by foodViewModel.foodTypes.collectAsState()

                val glucoseRepository =
                    GlucoseRepositoryImpl(db.glucoseDao())
                val glucoseViewModel: GlucoseViewModel = viewModel(
                    factory = GlucoseViewModelFactory(
                        getAllGlucoseEntriesUseCase = GetAllGlucoseEntriesUseCase(
                            glucoseRepository
                        ),
                        getAllGlucoseEntriesOnceUseCase = GetAllGlucoseEntriesOnceUseCase(
                            glucoseRepository
                        ),
                        getGlucoseEntriesBetweenUseCase = GetGlucoseEntriesBetweenUseCase(
                            glucoseRepository
                        ),
                        insertGlucoseUseCase = InsertGlucoseUseCase(
                            glucoseRepository
                        ),
                        updateGlucoseEntryUseCase = UpdateGlucoseEntryUseCase(
                            glucoseRepository
                        ),
                        deleteGlucoseEntryUseCase = DeleteGlucoseEntryUseCase(
                            glucoseRepository
                        ),
                        updateNoteForEntryUseCase = UpdateNoteForEntryUseCase(
                            glucoseRepository
                        ),
                        getGlucoseEntryByIdUseCase = GetGlucoseEntryByIdUseCase(
                            glucoseRepository
                        )
                    )
                )
                val glucoseReadings by glucoseViewModel.glucoseEntries.collectAsState()

                val insulinRepository =
                    InsulinRepositoryImpl(db.insulinDao())
                val insulinViewModel: InsulinViewModel = viewModel(
                    factory = InsulinViewModelFactory(
                        getAllInsulinEntriesUseCase = GetAllInsulinEntriesUseCase(
                            insulinRepository
                        ),
                        getAllInsulinEntriesOnceUseCase = GetAllInsulinEntriesOnceUseCase(
                            insulinRepository
                        ),
                        getInsulinEntriesBetweenUseCase = GetInsulinEntriesBetweenUseCase(
                            insulinRepository
                        ),
                        insertInsulinEntryUseCase = InsertInsulinEntryUseCase(
                            insulinRepository
                        ),
                        updateInsulinEntryUseCase = UpdateInsulinEntryUseCase(
                            insulinRepository
                        ),
                        deleteInsulinEntryUseCase = DeleteInsulinEntryUseCase(
                            insulinRepository
                        ),
                        insertInsulinTypeUseCase = InsertInsulinTypeUseCase(
                            insulinRepository
                        ),
                        getAllInsulinTypesUseCase = GetAllInsulinTypesUseCase(
                            insulinRepository
                        ),
                        getInsulinTypeByIdUseCase = GetInsulinTypeByIdUseCase(
                            insulinRepository
                        ),
                        getInsulinTypeByNameUseCase = GetInsulinTypeByNameUseCase(
                            insulinRepository
                        ),
                        insertAllInsulinTypesUseCase = InsertAllInsulinTypesUseCase(
                            insulinRepository
                        ),
                        getAllInsulinEntriesWithTypesUseCase = GetAllInsulinEntriesWithTypesUseCase(
                            insulinRepository
                        ),
                        getAllInsulinEntriesWithTypesOnceUseCase = GetAllInsulinEntriesWithTypesOnceUseCase(
                            insulinRepository
                        ),
                        getInsulinEntryByIdUseCase = GetInsulinEntryByIdUseCase(
                            insulinRepository
                        )
                    )
                )
                val insulinRecords by insulinViewModel.insulinEntries.collectAsState()
                val insulinTypes by insulinViewModel.insulinTypes.collectAsState()
                val insulinEntriesWithTypesCard = insulinViewModel.insulinEntriesWithTypes.collectAsState().value

                val activityRepository =
                    ActivityRepositoryImpl(db.activityDao())
                val activityViewModel: ActivityEntryViewModel = viewModel(
                    factory = ActivityEntryViewModelFactory(
                        getAllActivityTypesUseCase = GetAllActivityTypesUseCase(
                            activityRepository
                        ),
                        insertActivityTypeUseCase = InsertActivityTypeUseCase(
                            activityRepository
                        ),
                        insertAllActivityTypesUseCase = InsertAllActivityTypesUseCase(
                            activityRepository
                        ),
                        getAllActivityEntriesUseCase = GetAllActivityEntriesUseCase(
                            activityRepository
                        ),
                        getAllActivityEntriesWithTypesFlowUseCase = GetAllActivityEntriesWithTypesFlowUseCase(
                            activityRepository
                        ),
                        getAllActivityEntriesOnceWithTypesUseCase = GetAllActivityEntriesOnceWithTypesUseCase(
                            activityRepository
                        ),
                        getActivityByIdUseCase = GetActivityByIdUseCase(
                            activityRepository
                        ),
                        getAllActivityEntriesOnceUseCase = GetAllActivityEntriesOnceUseCase(
                            activityRepository
                        ),
                        getActivityTypeByNameUseCase = GetActivityTypeByNameUseCase(
                            activityRepository
                        ),
                        getActivityEntriesBetweenUseCase = GetActivitiesBetweenUseCase(
                            activityRepository
                        ),
                        insertActivityEntryUseCase = InsertActivityEntryUseCase(
                            activityRepository
                        ),
                        updateActivityEntryUseCase = UpdateActivityEntryUseCase(
                            activityRepository
                        ),
                        deleteActivityEntryUseCase = DeleteActivityEntryUseCase(
                            activityRepository
                        ),
                        getActivityEntryByIdUseCase = GetActivityEntryByIdUseCase(
                            activityRepository
                        )
                    )
                )
                val activityRecords by activityViewModel.activityEntries.collectAsState()
                val activityTypes by activityViewModel.activityTypes.collectAsState()

                val medicationRepository =
                    MedicationRepositoryImpl(db.medicationDao())
                val medicationViewModel: MedicationViewModel = viewModel(
                    factory = MedicationViewModelFactory(
                        getAllMedicationTypesUseCase = GetAllMedicationTypesUseCase(
                            medicationRepository
                        ),
                        insertMedicationTypeUseCase = InsertMedicationTypeUseCase(
                            medicationRepository
                        ),
                        insertAllMedicationTypesUseCase = InsertAllMedicationTypesUseCase(
                            medicationRepository
                        ),
                        getAllMedicationEntriesUseCase = GetAllMedicationEntriesUseCase(
                            medicationRepository
                        ),
                        getAllMedicationEntriesWithTypesUseCase = GetAllMedicationEntriesWithTypesUseCase(
                            medicationRepository
                        ),
                        getAllMedicationEntriesOnceWithTypesUseCase = GetAllMedicationEntriesOnceWithTypesUseCase(
                            medicationRepository
                        ),
                        getMedicationByIdUseCase = GetMedicationByIdUseCase(
                            medicationRepository
                        ),
                        getAllMedicationEntriesOnceUseCase = GetAllMedicationEntriesOnceUseCase(
                            medicationRepository
                        ),
                        getMedicationTypeByNameUseCase = GetMedicationTypeByNameUseCase(
                            medicationRepository
                        ),
                        getMedicationEntriesBetweenUseCase = GetMedicationEntriesBetweenUseCase(
                            medicationRepository
                        ),
                        insertMedicationEntryUseCase = InsertMedicationEntryUseCase(
                            medicationRepository
                        ),
                        updateMedicationEntryUseCase = UpdateMedicationEntryUseCase(
                            medicationRepository
                        ),
                        deleteMedicationEntryUseCase = DeleteMedicationEntryUseCase(
                            medicationRepository
                        ),
                        getMedicationEntryByIdUseCase = GetMedicationEntryByIdUseCase(
                            medicationRepository
                        )
                    )
                )
                val medicationRecords by medicationViewModel.medicationEntries.collectAsState()
                val medicationTypes by medicationViewModel.medicationTypes.collectAsState()

                val bpRepository = BloodPressureRepositoryImpl(db.bloodPressureDao())
                val bpViewModel: BloodPressureViewModel = viewModel(
                    factory = BloodPressureViewModelFactory(
                        InsertBloodPressureEntryUseCase(bpRepository),
                        GetAllBloodPressureUseCase(bpRepository),
                        DeleteBloodPressureEntryUseCase(bpRepository),
                        UpdateBloodPressureUseCase(bpRepository),
                        GetBloodPressureEntryByIdUseCase(bpRepository)
                    )
                )
                val bpRecords by bpViewModel.entries.collectAsState()

                val therapyPlanViewModel: TherapyPlanViewModel = viewModel(
                    factory = TherapyPlanViewModelFactory(
                        therapyPlanDao = db.therapyPlanDao(),
                        insulinDao = db.insulinDao(),
                        medicationDao = db.medicationDao()
                    )
                )

                val activePlan by therapyPlanViewModel.activePlan.collectAsState()

                LaunchedEffect(userProfile?.id) {
                    userProfile?.id?.let { therapyPlanViewModel.setUserId(it) }
                }

                val glucoseEntries by db.glucoseDao().getAllGlucoseEntries()
                    .collectAsState(initial = emptyList())
                val foodEntriesWithItems by db.foodDao().getAllFoodEntriesWithItemsFlow()
                    .collectAsState(initial = emptyList())
                val insulinEntriesWithTypes by db.insulinDao().getAllInsulinEntriesWithTypesFlow()
                    .collectAsState(initial = emptyList())
                val activityEntriesWithTypes by db.activityDao()
                    .getAllActivityEntriesWithTypesFlow().collectAsState(initial = emptyList())
                val medicationEntriesWithTypes by db.medicationDao()
                    .getAllMedicationEntriesWithTypesFlow().collectAsState(initial = emptyList())

                val navController = rememberNavController()

                Scaffold(
                    topBar = {
                        val currentRoute =
                            navController.currentBackStackEntryAsState().value?.destination?.route
                        TopAppBar(
                            title = {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                                        val icon = when (currentRoute) {
                                            Routes.DASHBOARD -> Icons.Filled.ShowChart
                                            Routes.PROFILE -> Icons.Filled.Person
                                            Routes.PROFILE_FORM -> Icons.Filled.Edit
                                            Routes.RECORD_SELECTOR -> Icons.Filled.Add
                                            Routes.RECORD_HISTORY -> Icons.Filled.History
                                            else -> Icons.Filled.Apps
                                        }
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = currentRoute,
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                        )

                                        Text(
                                            when (currentRoute) {
                                                Routes.DASHBOARD -> "Панель управления"
                                                Routes.PROFILE -> "Профиль"
                                                Routes.PROFILE_FORM -> "Заполнение профиля"
                                                Routes.RECORD_SELECTOR -> "Добавить запись"
                                                Routes.RECORD_HISTORY -> "История записей"
                                                else -> "GlucoDiaLog"
                                            },
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }

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
                        when {
                            isLoading -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }

                            userProfile == null -> {
                                var currentProfile by remember {
                                    mutableStateOf(
                                        UserProfile(
                                            id = 0,
                                            email = "",
                                            name = "",
                                            gender = "",
                                            weight = 0.0,
                                            height = 0.0,
                                            diabetesType = "",
                                            targetGlucoseLow = 0.0,
                                            targetGlucoseHigh = 0.0,
                                            glucoseUnit = ""
                                        )
                                    )
                                }

                                ProfileFormScreen(
                                    profile = currentProfile,
                                    onUpdateProfile = { updatedProfile ->
                                        viewModel.insertUserProfile(updatedProfile)
                                    },
                                    onBack = { finish() }
                                )
                            }

                            else -> {
                                NavHost(
                                    navController = navController,
                                    startDestination = Routes.DASHBOARD
                                ) {
                                    composable(Routes.DASHBOARD) {
                                        val context = LocalContext.current
                                        val db = AppDatabase.getDatabase(context)

                                        Dashboard(
                                            glucoseEntries = glucoseReadings,
                                            foodEntriesWithItems = meals.map { foodEntry ->
                                                FoodEntryWithTypeDomain(
                                                    entry = foodEntry,
                                                    type = foodItems.find { it.id == foodEntry.foodTypeId }
                                                )
                                            },
                                            insulinEntriesWithTypes = insulinRecords.map { insulinEntry ->
                                                InsulinEntryWithTypeDomain(
                                                    entry = insulinEntry,
                                                    type = insulinTypes.find { it.id == insulinEntry.insulinTypeId }
                                                )
                                            },
                                            activityEntriesWithTypes = activityRecords.map { activityEntry ->
                                                ActivityEntryWithTypeDomain(
                                                    entry = activityEntry,
                                                    type = activityTypes.find { it.id == activityEntry.activityTypeId }
                                                )
                                            },
                                            medicationEntriesWithTypes = medicationRecords.map { medicationEntry ->
                                                MedicationEntryWithTypeDomain(
                                                    entry = medicationEntry,
                                                    type = medicationTypes.find { it.id == medicationEntry.medicationTypeId }
                                                )
                                            }
                                        )
                                    }

                                    composable(Routes.PROFILE) {
                                        ProfileViewScreen(
                                            profile = userProfile!!,
                                            onEdit = { navController.navigate(Routes.PROFILE_FORM) },
                                            onTherapyPlanClick = { navController.navigate(Routes.THERAPY_PLAN) }
                                        )
                                    }

                                    composable(Routes.THERAPY_PLAN) {
                                        TherapyPlanScreen(
                                            viewModel = therapyPlanViewModel,
                                            userProfile = userProfile!!,
                                            onBack = { navController.popBackStack() }
                                        )
                                    }

                                    composable(Routes.PROFILE_FORM) {
                                        ProfileFormScreen(
                                            profile = userProfile!!,
                                            onUpdateProfile = { updatedProfile ->
                                                viewModel.updateUserProfile(updatedProfile)
                                                navController.popBackStack()
                                            },
                                            onBack = { navController.popBackStack() }
                                        )
                                    }

                                    composable(Routes.RECORD_SELECTOR) {
                                        RecordTypeSelector(
                                            onSelectScreen = { route -> navController.navigate(route) }
                                        )
                                    }

                                    composable(
                                        route = "${Routes.GLUCOSE}?id={id}",
                                        arguments = listOf(navArgument("id") { type = NavType.IntType; defaultValue = -1 })
                                    ) { backStackEntry ->
                                        val id = backStackEntry.arguments?.getInt("id") ?: -1

                                        GlucoseEntryScreen(
                                            viewModel = glucoseViewModel,
                                            userProfile = userProfile,
                                            entryId = id,
                                            activePlan = activePlan,
                                            onBack = { navController.popBackStack() }
                                        )
                                    }

                                    composable(
                                        route = "${Routes.INSULIN}?id={id}",
                                        arguments = listOf(navArgument("id") { type = NavType.IntType; defaultValue = -1 })
                                    ) { backStackEntry ->
                                        val id = backStackEntry.arguments?.getInt("id") ?: -1

                                        InsulinEntryScreen(
                                            viewModel = insulinViewModel,
                                            userProfile = userProfile,
                                            entryId = id,
                                            onBack = { navController.popBackStack() }
                                        )
                                    }

                                    composable(
                                        route = "${Routes.MEDICATION}?id={id}",
                                        arguments = listOf(navArgument("id") { type = NavType.IntType; defaultValue = -1 })
                                    ) { backStackEntry ->
                                        val id = backStackEntry.arguments?.getInt("id") ?: -1

                                        MedicationEntryScreen(
                                            viewModel = medicationViewModel,
                                            entryId = id,
                                            onBack = { navController.popBackStack() }
                                        )
                                    }

                                    composable(
                                        route = "${Routes.MEAL}?id={id}",
                                        arguments = listOf(navArgument("id") { type = NavType.IntType; defaultValue = -1 })
                                    ) { backStackEntry ->
                                        val id = backStackEntry.arguments?.getInt("id") ?: -1

                                        MealEntryScreen(
                                            viewModel = foodViewModel,
                                            userProfile = userProfile,
                                            entryId = id,
                                            activePlan = activePlan,
                                            onBack = { navController.popBackStack() }
                                        )
                                    }

                                    composable(
                                        route = "${Routes.ACTIVITY}?id={id}",
                                        arguments = listOf(navArgument("id") { type = NavType.IntType; defaultValue = -1 })
                                    ) { backStackEntry ->
                                        val id = backStackEntry.arguments?.getInt("id") ?: -1

                                        ActivityEntryScreen(
                                            viewModel = activityViewModel,
                                            entryId = id,
                                            onBack = { navController.popBackStack() }
                                        )
                                    }

                                    composable(
                                        route = "${Routes.BLOOD_PRESSURE}?id={id}",
                                        arguments = listOf(navArgument("id") { type = NavType.IntType; defaultValue = -1 })
                                    ) { backStackEntry ->
                                        val id = backStackEntry.arguments?.getInt("id") ?: -1

                                        BloodPressureEntryScreen(
                                            viewModel = bpViewModel,
                                            entryId = id,
                                            onBack = { navController.popBackStack() }
                                        )
                                    }

                                    composable(Routes.RECORD_HISTORY) {
                                        val context = LocalContext.current
                                        val db = AppDatabase.getDatabase(context)

                                        RecordHistoryScreen(
                                            onSelectScreen = { route -> navController.navigate(route) },
                                            onEditRecord = { route, id -> navController.navigate("$route?id=$id") },
                                            userProfile = userProfile,
                                            glucoseReadings = glucoseReadings,
                                            meals = meals,
                                            insulinRecords = insulinEntriesWithTypesCard,
                                            activityRecords = activityRecords,
                                            medicationRecords = medicationRecords,
                                            activityTypes = activityTypes,
                                            foodItems = foodItems,
                                            insulinTypes = insulinTypes,
                                            medicationTypes = medicationTypes,
                                            foodViewModel = foodViewModel,
                                            insulinViewModel = insulinViewModel,
                                            activityViewModel = activityViewModel,
                                            glucoseViewModel = glucoseViewModel,
                                            medicationViewModel = medicationViewModel,
                                            bloodPressureRecords = bpRecords,
                                            bloodPressureViewModel = bpViewModel
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
}
