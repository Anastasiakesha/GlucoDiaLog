package com.example.glucodialog.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

object Routes {
    const val GLUCOSE = "glucose"
    const val INSULIN = "insulin"
    const val ACTIVITY = "activity"
    const val MEAL = "meal"
    const val MEDICATION = "medication"
}

data class RecordType(
    val route: String,
    val title: String,
    val description: String,
    val icon: String,
    val color: Color
)

@Composable
fun RecordTypeScreen(navController: NavHostController) {
    RecordTypeSelector(
        onSelectScreen = { route ->
            navController.navigate(route)
        }
    )
}

@Composable
fun RecordTypeSelector(
    recordTypes: List<RecordType> = listOf(
        RecordType(
            route = Routes.GLUCOSE,
            title = "Глюкоза",
            description = "Добавить запись глюкозы",
            icon = "🩸",
            color = Color(0xFFEF4444)
        ),
        RecordType(
            route = Routes.INSULIN,
            title = "Инсулин",
            description = "Добавить запись инсулина",
            icon = "💉",
            color = Color(0xFF3B82F6)
        ),
        RecordType(
            route = Routes.ACTIVITY,
            title = "Активность",
            description = "Добавить запись активности",
            icon = "🏃‍♂️",
            color = Color(0xFF10B981)
        ),
        RecordType(
            route = Routes.MEAL,
            title = "Прием пищи",
            description = "Добавить запись еды",
            icon = "🍽️",
            color = Color(0xFFF59E0B)
        ),
        RecordType(
            route = Routes.MEDICATION,
            title = "Лекарство",
            description = "Добавить запись медикамента",
            icon = "💊",
            color = Color(0xFF8B5CF6)
        ),

    ),
    onSelectScreen: (String) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            maxItemsInEachRow = 3
        ) {
            recordTypes.forEach { record ->
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelectScreen(record.route) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = record.color.copy(alpha = 0.1f)),
                    border = BorderStroke(2.dp, record.color.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(record.icon, style = MaterialTheme.typography.headlineSmall)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(record.title, style = MaterialTheme.typography.titleMedium)
                            Text(record.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}