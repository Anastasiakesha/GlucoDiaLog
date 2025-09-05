package com.example.glucodialog.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.glucodialog.data.*
import java.text.SimpleDateFormat
import java.util.*
import com.example.glucodialog.ui.components.GlucoseCard
import com.example.glucodialog.ui.components.MealCard
import com.example.glucodialog.ui.components.ActivityCard
import com.example.glucodialog.ui.components.InsulinCard
import com.example.glucodialog.ui.components.MedicationCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordHistory(
    onSelectScreen: (String) -> Unit,
    userProfile: UserProfile?,
    glucoseReadings: List<GlucoseEntry>,
    meals: List<FoodEntry>,
    insulinRecords: List<InsulinEntry>,
    activityRecords: List<ActivityEntry>,
    medicationRecords: List<MedicationEntry>,
    activityTypes: List<ActivityType>,
    foodItems: List<FoodItem>,
    insulinTypes: List<InsulinType>,
    medicationTypes: List<MedicationType>
) {
    var searchTerm by remember { mutableStateOf("") }
    var dateFilter by remember { mutableStateOf("all") }
    var sortOrder by remember { mutableStateOf("newest") }
    var activeTab by remember { mutableStateOf("all") }
    var selectedDate by remember { mutableStateOf<Calendar?>(null) }



    val typeColors = mapOf(
        "glucose" to Color(0xFFBBDEFB),   // пастельный голубой
        "meal" to Color(0xFFFFF9C4),      // пастельный желтый
        "insulin" to Color(0xFFC8E6C9),   // пастельный зеленый
        "activity" to Color(0xFFFFCCBC),  // пастельный оранжевый
        "medication" to Color(0xFFD1C4E9) // пастельный фиолетовый
    )

    val filteredRecords by remember(searchTerm, dateFilter, sortOrder, activeTab, selectedDate) {
        derivedStateOf {
            val now = Date()
            val calendar = Calendar.getInstance().apply { time = now }

            fun filterByDate(timestamp: Date): Boolean {
                selectedDate?.let { sel ->
                    return timestamp.year == sel.time.year &&
                            timestamp.month == sel.time.month &&
                            timestamp.date == sel.time.date
                }

                return when (dateFilter) {
                    "today" -> SimpleDateFormat("yyyyMMdd").format(timestamp) ==
                            SimpleDateFormat("yyyyMMdd").format(now)
                    "week" -> { calendar.add(Calendar.DAY_OF_YEAR, -7); timestamp.after(calendar.time) }
                    "month" -> { calendar.add(Calendar.DAY_OF_YEAR, -30); timestamp.after(calendar.time) }
                    else -> true
                }
            }

            val allRecords = glucoseReadings.map { RecordWrapper(it, "glucose") } +
                    meals.map { RecordWrapper(it, "meal") } +
                    insulinRecords.map { RecordWrapper(it, "insulin") } +
                    activityRecords.map { RecordWrapper(it, "activity") } +
                    medicationRecords.map { RecordWrapper(it, "medication") }

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
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        OutlinedTextField(
            value = searchTerm,
            onValueChange = { searchTerm = it },
            label = { Text("Поиск по записям") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("newest" to "Сначала новые", "oldest" to "Сначала старые").forEach { (key, label) ->
                FilterChip(
                    selected = sortOrder == key,
                    onClick = { sortOrder = key },
                    label = { Text(label) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        /** --- Tabs --- */
        val tabs = listOf("all", "glucose", "meal", "insulin", "activity", "medication")
        val tabLabels = mapOf(
            "all" to "Все",
            "glucose" to "Глюкоза",
            "meal" to "Еда",
            "insulin" to "Инсулин",
            "activity" to "Активность",
            "medication" to "Лекарства"
        )

        TabRow(selectedTabIndex = tabs.indexOf(activeTab)) {
            tabs.forEach { tab ->
                Tab(
                    selected = activeTab == tab,
                    onClick = { activeTab = tab }
                ) {
                    Text(tabLabels[tab] ?: tab, modifier = Modifier.padding(vertical = 8.dp), fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredRecords.isEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Записи не найдены", color = Color.Gray)
                    Text("Попробуйте изменить фильтры", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
        } else {
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val grouped = filteredRecords.groupBy { sdf.format(it.timestamp) }
            val sortedDates = grouped.keys.sortedWith { d1, d2 ->
                val date1 = sdf.parse(d1)!!
                val date2 = sdf.parse(d2)!!
                if (sortOrder == "newest") date2.compareTo(date1) else date1.compareTo(date2)
            }

            sortedDates.forEach { dateStr ->
                val records = grouped[dateStr] ?: emptyList()

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(Color.LightGray.copy(alpha = 0.3f))
                ) {
                    Text(dateStr, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(8.dp))
                }

                val sortedRecords = if (sortOrder == "newest") records.sortedByDescending { it.timestamp.time }
                else records.sortedBy { it.timestamp.time }

                sortedRecords.forEach { record ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(typeColors[record.type] ?: Color.White)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(Color.White)
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    when (record.type) {
                                        "glucose" -> GlucoseCard(
                                            record.data as GlucoseEntry, userProfile)
                                        "meal" -> MealCard(record.data as FoodEntry, foodItems)
                                        "insulin" -> InsulinCard(record.data as InsulinEntry, insulinTypes)
                                        "activity" -> ActivityCard(record.data as ActivityEntry, activityTypes)
                                        "medication" -> MedicationCard(record.data as MedicationEntry, medicationTypes)
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

private data class RecordWrapper(val data: Any, val type: String) {
    val timestamp: Date
        get() = when (data) {
            is GlucoseEntry -> Date(data.timestamp)
            is FoodEntry -> Date(data.timestamp)
            is InsulinEntry -> Date(data.timestamp)
            is ActivityEntry -> Date(data.timestamp)
            is MedicationEntry -> Date(data.timestamp)
            else -> Date(0)
        }

    fun matchesSearch(term: String): Boolean {
        return when (data) {
            is GlucoseEntry -> (data.note?.lowercase()?.contains(term) ?: false)
            is FoodEntry -> data.quantity.toString().contains(term) || data.unit.lowercase().contains(term)
            is InsulinEntry -> data.doseUnits.toString().contains(term) || data.unit.lowercase().contains(term)
            is ActivityEntry -> data.durationMinutes.toString().contains(term)
            is MedicationEntry -> data.dose.lowercase().contains(term) || data.unit.lowercase().contains(term)
            else -> false
        }
    }
}
