package com.example.glucodialog.ui.screens

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.glucodialog.data.*
import com.example.glucodialog.data.relations.*
import com.example.glucodialog.ui.components.AnimatedLineChart
import com.example.glucodialog.ui.components.DashboardStatCard
import java.util.*

@Composable
fun Dashboard(
    glucoseEntries: List<GlucoseEntry>,
    foodEntriesWithItems: List<FoodEntryWithItem>,
    insulinEntriesWithTypes: List<InsulinEntryWithType>,
    activityEntriesWithTypes: List<ActivityEntryWithType>,
    medicationEntriesWithTypes: List<MedicationEntryWithType>
) {

    var animationProgress by remember { mutableStateOf(0f) }
    LaunchedEffect(glucoseEntries, foodEntriesWithItems, insulinEntriesWithTypes, activityEntriesWithTypes, medicationEntriesWithTypes) {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200, easing = LinearOutSlowInEasing)
        ) { value, _ ->
            animationProgress = value
        }
    }

    val alpha = animationProgress
    val offsetY = (1 - animationProgress) * 50f


    val recentGlucose = glucoseEntries.takeLast(10)
    val averageGlucose = if (recentGlucose.isNotEmpty())
        recentGlucose.map { it.glucoseLevel }.average().toFloat()
    else 0f

    val inRangeCount = recentGlucose.count { it.glucoseLevel in 4.0..7.0 }
    val timeInRange = if (recentGlucose.isNotEmpty())
        inRangeCount.toFloat() / recentGlucose.size * 100
    else 0f

    val today = Calendar.getInstance()

    val todayMeals = foodEntriesWithItems.filter {
        val cal = Calendar.getInstance().apply { timeInMillis = it.entry.timestamp }
        cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
    }
    val todayInsulin = insulinEntriesWithTypes.filter {
        val cal = Calendar.getInstance().apply { timeInMillis = it.entry.timestamp }
        cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
    }
    val todayActivities = activityEntriesWithTypes.filter {
        val cal = Calendar.getInstance().apply { timeInMillis = it.entry.timestamp }
        cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
    }
    val todayMedications = medicationEntriesWithTypes.filter {
        val cal = Calendar.getInstance().apply { timeInMillis = it.entry.timestamp }
        cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
    }

    val totalTodayCarbs = todayMeals.sumOf { it.foodItem.carbs * it.entry.quantity }
    val totalTodayInsulin = todayInsulin.sumOf { it.entry.doseUnits }.toFloat()
    val totalActivityMinutes = todayActivities.sumOf { it.entry.durationMinutes }
    val totalMedications = todayMedications.size

    val mmolEntries = glucoseEntries.map { entry ->
        if (entry.unit == "мг/дл") entry.glucoseLevel / 18.0 else entry.glucoseLevel
    }
    val avgGlucose = mmolEntries.average()
    val minGlucose = mmolEntries.minOrNull() ?: 0.0
    val maxGlucose = mmolEntries.maxOrNull() ?: 0.0
    val measurementsPerDay = glucoseEntries.groupBy {
        val cal = Calendar.getInstance().apply { timeInMillis = it.timestamp }
        "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.DAY_OF_YEAR)}"
    }.mapValues { it.value.size }
    val avgDailyMeasurements = measurementsPerDay.values.average()
    val estimatedHbA1c = (avgGlucose + 2.59) / 1.59

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = offsetY.dp)
                .alpha(alpha),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardStatCard(
                icon = "🩸",
                title = "Текущий уровень",
                value = recentGlucose.lastOrNull()?.let { "${it.glucoseLevel} ${it.unit}" } ?: "Нет данных",
                subtitle = glucoseStatus(recentGlucose.lastOrNull()?.glucoseLevel),
                color = when (glucoseStatus(recentGlucose.lastOrNull()?.glucoseLevel)) {
                    "Низкий" -> Color.Blue
                    "В норме" -> Color.Green
                    "Высокий" -> Color(0xFFFFA500)
                    "Очень высокий" -> Color.Red
                    else -> Color.Gray
                },
                modifier = Modifier.weight(1f)
            )

            DashboardStatCard(
                icon = "📊",
                title = "Средний (последние 10)",
                value = if (averageGlucose > 0) "%.1f".format(averageGlucose) else "Нет данных",
                subtitle = null,
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = offsetY.dp)
                .alpha(alpha),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardStatCard(
                icon = "⏱️",
                title = "Время в диапазоне",
                value = "${timeInRange.toInt()}%",
                subtitle = "4.0-7.0 ммоль/л",
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            )

            DashboardStatCard(
                icon = "🍽️",
                title = "Сегодня",
                value = "Углеводы: ${"%.1f".format(totalTodayCarbs)} г\nИнсулин: ${"%.1f".format(totalTodayInsulin)} ед",
                subtitle = null,
                color = Color(0xFF03A9F4),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = offsetY.dp)
                .alpha(alpha),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardStatCard(
                icon = "🏃‍♂️",
                title = "Активность",
                value = "$totalActivityMinutes мин",
                subtitle = "Сегодня",
                color = Color(0xFFFFC107),
                modifier = Modifier.weight(1f)
            )

            DashboardStatCard(
                icon = "💊",
                title = "Лекарства",
                value = "$totalMedications записей",
                subtitle = "Сегодня",
                color = Color(0xFFFF5722),
                modifier = Modifier.weight(1f)
            )
        }

        if (glucoseEntries.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🩸", style = MaterialTheme.typography.headlineLarge)
                    Text(
                        "Нет данных о глюкозе",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black
                    )
                    Text(
                        "Добавьте первую запись, чтобы видеть статистику и график.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        } else {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = offsetY.dp)
                    .alpha(alpha),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Глюкоза (ммоль/л)", style = MaterialTheme.typography.titleMedium)
                    Text("Средняя: %.2f".format(avgGlucose))
                    Text("Мин/Макс: %.1f / %.1f".format(minGlucose, maxGlucose))
                    Text("Измерений в день: %.1f".format(avgDailyMeasurements))
                    Text("HbA1c (≈): %.2f %%".format(estimatedHbA1c))
                }
            }


            if (glucoseEntries.size < 2) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("📉", style = MaterialTheme.typography.headlineLarge)
                        Text(
                            "Недостаточно данных для графика",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black
                        )
                        Text(
                            "Добавьте ещё хотя бы одну запись глюкозы, чтобы видеть динамику.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = offsetY.dp)
                        .alpha(alpha),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("📈 Динамика уровня глюкозы", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(12.dp))
                        val sortedGlucoseEntries = glucoseEntries.sortedBy { it.timestamp }

                        AnimatedLineChart(
                            values = sortedGlucoseEntries.map { entry ->
                                if (entry.unit == "мг/дл") entry.glucoseLevel.toFloat() / 18f else entry.glucoseLevel.toFloat()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }
                }
            }
        }
    }
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