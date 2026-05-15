package com.example.glucodialog.ui.screen


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.glucodialog.domain.model.*
import com.example.glucodialog.ui.components.*
import com.example.glucodialog.ui.viewmodel.*
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log
import androidx.compose.material.icons.filled.FavoriteBorder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordHistoryScreen(
    onSelectScreen: (String) -> Unit,
    onEditRecord: (String, Int) -> Unit,
    userProfile: UserProfile?,
    glucoseReadings: List<GlucoseEntry>,
    meals: List<FoodEntry>,
    insulinRecords: List<InsulinEntryWithTypeDomain>,
    activityRecords: List<ActivityEntry>,
    medicationRecords: List<MedicationEntry>,
    activityTypes: List<ActivityType>,
    foodItems: List<FoodType>,
    insulinTypes: List<InsulinType>,
    medicationTypes: List<MedicationType>,
    foodViewModel: FoodViewModel,
    insulinViewModel: InsulinViewModel,
    activityViewModel: ActivityEntryViewModel,
    glucoseViewModel: GlucoseViewModel,
    medicationViewModel: MedicationViewModel,
    bloodPressureRecords: List<BloodPressureEntry>,
    bloodPressureViewModel: BloodPressureViewModel
) {
    var searchTerm by remember { mutableStateOf("") }
    var dateFilter by remember { mutableStateOf("all") }
    var sortOrder by remember { mutableStateOf("newest") }
    var activeTab by remember { mutableStateOf("all") }
    var selectedDate by remember { mutableStateOf<Calendar?>(null) }


    val typeTextColors = mapOf(
        "glucose" to Color(0xFFD32F2F),
        "meal" to Color(0xFFFF9800),
        "insulin" to Color(0xFF2E7D32),
        "activity" to Color(0xFF7B1FA2),
        "medication" to Color(0xFF1976D2),
        "blood_pressure" to Color(0xFFE91E63)
    )

    val typeIcons = mapOf(
        "glucose" to Icons.Default.Favorite,
        "meal" to Icons.Default.Restaurant,
        "insulin" to Icons.Default.LocalHospital,
        "activity" to Icons.Default.DirectionsRun,
        "medication" to Icons.Default.Medication,
        "blood_pressure" to Icons.Default.FavoriteBorder
    )

    val filteredRecords by remember(
        searchTerm,
        dateFilter,
        sortOrder,
        activeTab,
        selectedDate,
        glucoseReadings,
        meals,
        insulinRecords,
        activityRecords,
        medicationRecords
    ) {
        derivedStateOf {
            val now = Date()
            val calendar = Calendar.getInstance().apply { time = now }

            fun filterByDate(timestamp: Long): Boolean {
                selectedDate?.let { sel ->
                    val recordCal = Calendar.getInstance().apply { timeInMillis = timestamp }
                    return recordCal.get(Calendar.YEAR) == sel.get(Calendar.YEAR) &&
                            recordCal.get(Calendar.MONTH) == sel.get(Calendar.MONTH) &&
                            recordCal.get(Calendar.DAY_OF_MONTH) == sel.get(Calendar.DAY_OF_MONTH)
                }
                return when (dateFilter) {
                    "today" -> SimpleDateFormat("yyyyMMdd").format(Date(timestamp)) ==
                            SimpleDateFormat("yyyyMMdd").format(now)
                    "week" -> { calendar.add(Calendar.DAY_OF_YEAR, -7); timestamp >= calendar.timeInMillis }
                    "month" -> { calendar.add(Calendar.DAY_OF_YEAR, -30); timestamp >= calendar.timeInMillis }
                    else -> true
                }
            }


            val allRecords = glucoseReadings.map { RecordWrapperDomain(it, "glucose") } +
                    meals.map { RecordWrapperDomain(it, "meal") } +
                    insulinRecords.map { RecordWrapperDomain(it, "insulin") } +
                    activityRecords.map { RecordWrapperDomain(it, "activity") } +
                    medicationRecords.map { RecordWrapperDomain(it, "medication") } +
                    bloodPressureRecords.map { RecordWrapperDomain(it, "blood_pressure") }

            allRecords.filter { record ->
                filterByDate(record.timestamp) &&
                        (activeTab == "all" || record.type == activeTab) &&
                        (searchTerm.isEmpty() || record.matchesSearch(searchTerm.lowercase()))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = searchTerm,
            onValueChange = { searchTerm = it },
            label = { Text("Поиск по записям") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("newest" to "Сначала новые", "oldest" to "Сначала старые").forEach { (key, label) ->
                FilterChip(
                    selected = sortOrder == key,
                    onClick = { sortOrder = key },
                    label = { Text(label, style = MaterialTheme.typography.bodySmall) }
                )
            }
        }

        val tabs = listOf("all", "glucose", "meal", "insulin", "activity", "medication", "blood_pressure")
        val tabLabels = mapOf(
            "all" to "Все",
            "glucose" to "Глюкоза",
            "meal" to "Еда",
            "insulin" to "Инсулин",
            "activity" to "Активность",
            "medication" to "Лекарства",
            "blood_pressure" to "Давление"
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            ScrollableTabsWithArrows(
                tabs = tabs,
                tabLabels = tabLabels,
                activeTab = activeTab,
                onTabSelected = { activeTab = it }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (filteredRecords.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Записи не найдены", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        "Попробуйте изменить фильтры",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val grouped = filteredRecords.groupBy { sdf.format(Date(it.timestamp)) }
            val sortedDates = grouped.keys.sortedWith { d1, d2 ->
                val date1 = sdf.parse(d1)!!
                val date2 = sdf.parse(d2)!!
                if (sortOrder == "newest") date2.compareTo(date1) else date1.compareTo(date2)
            }

            sortedDates.forEach { dateStr ->
                val records = grouped[dateStr] ?: emptyList()
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD1C4E9)), // яркий фон для даты
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        dateStr,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color(0xFF512DA8),
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(8.dp)
                    )
                }

                val sortedRecords = if (sortOrder == "newest") records.sortedByDescending { it.timestamp }
                else records.sortedBy { it.timestamp }

                sortedRecords.forEach { record ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            Icon(
                                imageVector = typeIcons[record.type] ?: Icons.Default.Info,
                                contentDescription = record.type,
                                tint = typeTextColors[record.type] ?: MaterialTheme.colorScheme.onSurface
                            )

                            when (record.type) {
                                "glucose" -> GlucoseCard(
                                    record = record.data as GlucoseEntry,
                                    onDelete = { glucoseViewModel.deleteEntry(record.data) },
                                    userProfile = userProfile,
                                    valueColor = typeTextColors["glucose"] ?: MaterialTheme.colorScheme.error
                                )
                                "meal" -> MealCard(
                                    record = record.data as FoodEntry,
                                    foodItems = foodItems,
                                    onEdit = { onEditRecord("meal", (record.data as FoodEntry).id) },
                                    onDelete = { foodViewModel.deleteEntry(record.data) },
                                    valueColor = typeTextColors["meal"] ?: MaterialTheme.colorScheme.secondary
                                )

                                "insulin" -> if (record.data is InsulinEntryWithTypeDomain) {
                                    InsulinCard(
                                        record = record.data,
                                        onEdit = { onEditRecord("insulin", record.data.entry.id) },
                                        onDelete = { insulinViewModel.deleteInsulinEntry(record.data.entry) },
                                        valueColor = typeTextColors["insulin"] ?: MaterialTheme.colorScheme.tertiary
                                    )
                                } else {
                                    Text(
                                        text = "Ошибка: неверная запись инсулина",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                                "activity" -> ActivityCard(
                                    record = record.data as ActivityEntry,
                                    activityTypes = activityTypes,
                                    onEdit = { onEditRecord("activity", (record.data as ActivityEntry).id) },
                                    onDelete = { activityViewModel.deleteActivityEntry(record.data) },
                                    valueColor = typeTextColors["activity"] ?: MaterialTheme.colorScheme.primary
                                )
                                "medication" -> MedicationCard(
                                    record = record.data as MedicationEntry,
                                    medicationTypes = medicationTypes,
                                    onEdit = { onEditRecord("medication", (record.data as MedicationEntry).id) }, // <--- ДОБАВИЛИ
                                    onDelete = { medicationViewModel.deleteMedicationEntry(record.data) },
                                    valueColor = typeTextColors["medication"] ?: MaterialTheme.colorScheme.primaryContainer
                                )
                                "blood_pressure" -> BloodPressureCard(
                                    record = record.data as BloodPressureEntry,
                                    onDelete = { bloodPressureViewModel.deleteBloodPressureEntry(record.data) },
                                    valueColor = typeTextColors["blood_pressure"] ?: MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class RecordWrapperDomain(val data: Any, val type: String) {
    val timestamp: Long
        get() = when (data) {
            is GlucoseEntry -> data.timestamp
            is FoodEntry -> data.timestamp
            is InsulinEntryWithTypeDomain -> data.entry.timestamp
            is ActivityEntry -> data.timestamp
            is MedicationEntry -> data.timestamp
            is BloodPressureEntry -> data.timestamp
            else -> 0L
        }

    fun matchesSearch(term: String): Boolean {
        return when (data) {
            is GlucoseEntry -> data.note?.lowercase()?.contains(term) ?: false
            is FoodEntry -> data.quantity.toString().contains(term) || data.unit.lowercase().contains(term)
            is InsulinEntryWithTypeDomain ->
                (data.entry.doseUnits.toString().contains(term)) ||
                        (data.entry.unit.lowercase().contains(term)) ||
                        (data.type?.name?.lowercase()?.contains(term) ?: false)
            is ActivityEntry -> data.durationMinutes.toString().contains(term)
            is MedicationEntry -> data.dose.lowercase().contains(term) || data.unit.lowercase().contains(term)
            is BloodPressureEntry -> data.systolic.toString().contains(term) || data.diastolic.toString().contains(term)
            else -> false
        }
    }
}