package com.example.glucodialog.ui.screen

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.glucodialog.domain.model.*
import com.example.glucodialog.ui.components.AnimatedLineChart
import com.example.glucodialog.ui.constants.TimeRange
import com.example.glucodialog.ui.components.DashboardStatCard
import com.example.glucodialog.ui.components.filterByRange
import com.example.glucodialog.ui.constants.ChartType
import androidx.compose.foundation.BorderStroke
import java.util.*

@Composable
fun Dashboard(
    glucoseEntries: List<GlucoseEntry>,
    foodEntriesWithItems: List<FoodEntryWithTypeDomain>,
    insulinEntriesWithTypes: List<InsulinEntryWithTypeDomain>,
    activityEntriesWithTypes: List<ActivityEntryWithTypeDomain>,
    medicationEntriesWithTypes: List<MedicationEntryWithTypeDomain>
) {

    var animationProgress by remember { mutableStateOf(0f) }
    LaunchedEffect(glucoseEntries, foodEntriesWithItems, insulinEntriesWithTypes, activityEntriesWithTypes, medicationEntriesWithTypes) {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200, easing = LinearOutSlowInEasing)
        ) { value, _ -> animationProgress = value }
    }

    val alpha = animationProgress
    val offsetY = (1 - animationProgress) * 50f

    val recentGlucose = glucoseEntries.sortedBy { it.timestamp }.takeLast(10)
    val latestGlucose = glucoseEntries.maxByOrNull { it.timestamp }
    val averageGlucose = if (recentGlucose.isNotEmpty()) recentGlucose.map { it.glucoseLevel }.average().toFloat() else 0f
    val inRangeCount = recentGlucose.count { it.glucoseLevel in 4.0..7.0 }
    val timeInRange = if (recentGlucose.isNotEmpty()) inRangeCount.toFloat() / recentGlucose.size * 100 else 0f

    val today = Calendar.getInstance()
    val todayMeals = foodEntriesWithItems.filter { meal ->
        val cal = Calendar.getInstance().apply { timeInMillis = meal.entry.timestamp }
        cal.get(Calendar.YEAR) == today.get(Calendar.YEAR) && cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
    }
    val todayInsulin = insulinEntriesWithTypes.filter { Calendar.getInstance().apply { timeInMillis = it.entry.timestamp }.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) }
    val todayActivities = activityEntriesWithTypes.filter { Calendar.getInstance().apply { timeInMillis = it.entry.timestamp }.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) }
    val todayMedications = medicationEntriesWithTypes.filter { Calendar.getInstance().apply { timeInMillis = it.entry.timestamp }.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) }

    val totalTodayCarbs = todayMeals.sumOf { meal ->
        val carbs = meal.type?.carbs ?: 0.0
        val quantity = meal.entry.quantity
        when (meal.type?.allowedUnits) {
            "г", "мл" -> carbs * (quantity / 100.0)
            "шт" -> carbs * quantity
            else -> 0.0
        }
    }

    val totalTodayInsulin = todayInsulin.sumOf { it.entry.doseUnits }.toFloat()
    val totalActivityMinutes = todayActivities.sumOf { it.entry.durationMinutes }
    val totalMedications = todayMedications.size

    val mmolEntries = glucoseEntries.map { if (it.unit == "мг/дл") it.glucoseLevel / 18.0 else it.glucoseLevel }
    val avgGlucose = mmolEntries.average()
    val minGlucose = mmolEntries.minOrNull() ?: 0.0
    val maxGlucose = mmolEntries.maxOrNull() ?: 0.0
    val measurementsPerDay = glucoseEntries.groupBy {
        val cal = Calendar.getInstance().apply { timeInMillis = it.timestamp }
        "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.DAY_OF_YEAR)}"
    }.mapValues { it.value.size }
    val avgDailyMeasurements = measurementsPerDay.values.average()
    val estimatedHbA1c = (avgGlucose + 2.59) / 1.59

    val lowColor = Color(0xFF1976D2)
    val normalColor = Color(0xFF388E3C)
    val highColor = Color(0xFFFFA000)
    val veryHighColor = Color(0xFFD32F2F)
    val carbsColor = Color(0xFFFFC107)
    val insulinColor = Color(0xFF0288D1)
    val activityColor = Color(0xFF7B1FA2)
    val medicationColor = Color(0xFFE64A19)
    val chartBackgroundColor = Color(0xFFEDE7F6)
    val mediumColor = Color(0xFFFF7043)
    val timeInRangeColor = Color(0xFFF06292)
    val glucoseStatsCardColor = MaterialTheme.colorScheme.primaryContainer

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().offset(y = offsetY.dp).alpha(alpha),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardStatCard(
                icon = Icons.Filled.Bloodtype,
                title = "Последняя запись",
                value = latestGlucose?.let { "${it.glucoseLevel} ${it.unit}" } ?: "Нет данных",
                subtitle = glucoseStatus(recentGlucose.lastOrNull()?.glucoseLevel),
                color = when (glucoseStatus(recentGlucose.lastOrNull()?.glucoseLevel)) {
                    "Низкий" -> lowColor
                    "В норме" -> normalColor
                    "Высокий" -> highColor
                    "Очень высокий" -> veryHighColor
                    else -> Color.Gray
                },
                modifier = Modifier.weight(1f)
            )

            DashboardStatCard(
                icon = Icons.Filled.ShowChart,
                title = "Среднее значение глюкозы",
                value = if (averageGlucose > 0) "%.1f".format(averageGlucose) else "Нет данных",
                subtitle = "Последние записи",
                color = mediumColor,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().offset(y = offsetY.dp).alpha(alpha),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardStatCard(
                icon = Icons.Filled.Timer,
                title = "Время в диапазоне",
                value = "${timeInRange.toInt()}%",
                subtitle = "4.0-7.0 ммоль/л",
                color = timeInRangeColor,
                modifier = Modifier.weight(1f)
            )
            DashboardStatCard(
                icon = Icons.Filled.FitnessCenter,
                title = "Сегодня",
                value = "Углеводы: ${"%.1f".format(totalTodayCarbs)} г\nИнсулин: ${"%.1f".format(totalTodayInsulin)} ед",
                subtitle = null,
                color = carbsColor,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().offset(y = offsetY.dp).alpha(alpha),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardStatCard(
                icon = Icons.Filled.FitnessCenter,
                title = "Активность",
                value = "$totalActivityMinutes мин",
                subtitle = "Сегодня",
                color = activityColor,
                modifier = Modifier.weight(1f)
            )
            DashboardStatCard(
                icon = Icons.Filled.MedicalServices,
                title = "Лекарства",
                value = "$totalMedications записей",
                subtitle = "Сегодня",
                color = medicationColor,
                modifier = Modifier.weight(1f)
            )
        }

        if (glucoseEntries.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = chartBackgroundColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Bloodtype,
                        contentDescription = "Глюкоза",
                        tint = Color.Red,
                        modifier = Modifier.size(48.dp)
                    )
                    Text("Нет данных о глюкозе", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Добавьте первую запись, чтобы видеть статистику и график.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth().offset(y = offsetY.dp).alpha(alpha),
                colors = CardDefaults.cardColors(containerColor = glucoseStatsCardColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        "Глюкоза (ммоль/л)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                    )
                    Text(
                        "Мин/Макс: %.1f / %.1f".format(minGlucose, maxGlucose),
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onPrimaryContainer)
                    )
                    Text(
                        "Измерений в день: %.1f".format(avgDailyMeasurements),
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onPrimaryContainer)
                    )
                    Text(
                        "HbA1c (≈): %.2f %%".format(estimatedHbA1c),
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onPrimaryContainer)
                    )
                }
            }


            if (glucoseEntries.size >= 2) {

                var selectedRange by remember { mutableStateOf<TimeRange>(TimeRange.Week) }
                var selectedChart by remember { mutableStateOf<ChartType>(ChartType.Glucose) }

                var isDetailDialogOpen by remember { mutableStateOf(false) }

                val filteredGlucose = filterByRange(glucoseEntries.map {
                    ChartPoint(
                        it.glucoseLevel.toFloat(),
                        it.timestamp
                    )
                }, selectedRange)
                val filteredInsulin = insulinEntriesWithTypes.filter {
                    it.entry.timestamp >= getTimestampForRange(selectedRange)
                }
                val filteredFood = foodEntriesWithItems.filter {
                    it.entry.timestamp >= getTimestampForRange(selectedRange)
                }

                val chartData = when (selectedChart) {
                    ChartType.Glucose -> ChartData(
                        listOf(
                            ChartLine(
                                filteredGlucose,
                                Color.Green,
                                "Глюкоза"
                            )
                        )
                    )

                    ChartType.Insulin -> insulinChartFromEntries(filteredInsulin)
                    ChartType.Food -> foodChartFromEntries(filteredFood)
                    else -> ChartData(emptyList())
                }

                if (isDetailDialogOpen) {
                    DetailedChartDialog(
                        data = chartData,
                        onDismiss = { isDetailDialogOpen = false }
                    )
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = offsetY.dp)
                        .alpha(alpha),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = chartBackgroundColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ShowChart,
                                contentDescription = "График",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Динамика", style = MaterialTheme.typography.titleMedium)

                            Spacer(Modifier.weight(1f))

                            TextButton(onClick = { isDetailDialogOpen = true }) {
                                Text("Подробнее")
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                ChartType.Glucose to "Глюкоза",
                                ChartType.Insulin to "Инсулин",
                                ChartType.Food to "КБЖУ"
                            ).forEach { (type, label) ->
                                FilterChip(
                                    selected = selectedChart == type,
                                    onClick = { selectedChart = type },
                                    label = { Text(label) }
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                TimeRange.Day to "День",
                                TimeRange.Week to "Неделя",
                                TimeRange.Month to "Месяц"
                            ).forEach { (range, label) ->
                                AssistChip(
                                    onClick = { selectedRange = range },
                                    label = { Text(label) },
                                    border = if (selectedRange == range)
                                        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                                    else
                                        BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f))
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        AnimatedLineChart(
                            data = chartData,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

fun getTimestampForRange(range: TimeRange): Long {
    val now = System.currentTimeMillis()
    return when (range) {
        TimeRange.Day -> now - 24 * 60 * 60 * 1000
        TimeRange.Week -> now - 7 * 24 * 60 * 60 * 1000
        TimeRange.Month -> now - 30L * 24 * 60 * 60 * 1000
    }
}

fun insulinChartFromEntries(entries: List<InsulinEntryWithTypeDomain>): ChartData {
    return ChartData(
        listOf(
            ChartLine(
                points = entries.sortedBy { it.entry.timestamp }.map {
                    ChartPoint(it.entry.doseUnits.toFloat(), it.entry.timestamp)
                },
                color = Color(0xFF0288D1),
                label = "Инсулин (ед)"
            )
        )
    )
}

fun foodChartFromEntries(entries: List<FoodEntryWithTypeDomain>): ChartData {
    val sorted = entries.sortedBy { it.entry.timestamp }

    fun calculateNutrient(entry: FoodEntryWithTypeDomain, type: String): Float {
        val nutrientPer100: Double = when(type) {
            "calories" -> entry.type?.calories ?: 0.0
            "protein" -> entry.type?.proteins ?: 0.0
            "fat" -> entry.type?.fats ?: 0.0
            "carbs" -> entry.type?.carbs ?: 0.0
            else -> 0.0
        }.toDouble()
        val quantity = entry.entry.quantity
        return (if (entry.type?.allowedUnits == "шт") nutrientPer100 * quantity
        else (nutrientPer100 * quantity / 100.0)).toFloat()
    }

    return ChartData(
        listOf(
            ChartLine(sorted.map { ChartPoint(calculateNutrient(it, "carbs"), it.entry.timestamp) }, Color(0xFFFFC107), "Углеводы"),
            ChartLine(sorted.map { ChartPoint(calculateNutrient(it, "protein"), it.entry.timestamp) }, Color(0xFF4CAF50), "Белки"),
            ChartLine(sorted.map { ChartPoint(calculateNutrient(it, "fat"), it.entry.timestamp) }, Color(0xFF2196F3), "Жиры"),
            ChartLine(sorted.map { ChartPoint(calculateNutrient(it, "calories") / 10f, it.entry.timestamp) }, Color(0xFFE91E63), "Кал/10")
            // Калории делим на 10, чтобы график не "улетел" слишком высоко относительно БЖУ
        )
    )
}


fun glucoseStatus(value: Double?): String {
    return when {
        value == null -> "Нет данных"
        value < 4.0 -> "Низкий"
        value <= 7.0 -> "В норме"
        value <= 10.0 -> "Высокий"
        else -> "Очень высокий"
    }
}