package com.example.glucodialog.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

data class RecordType(
    val route: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color
)

object Routes {
    const val GLUCOSE = "glucose"
    const val INSULIN = "insulin"
    const val ACTIVITY = "activity"
    const val MEAL = "meal"
    const val MEDICATION = "medication"
    const val BLOOD_PRESSURE = "blood_pressure"
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun RecordTypeSelector(
    recordTypes: List<RecordType> = listOf(
        RecordType(
            route = Routes.GLUCOSE,
            title = "Глюкоза",
            description = "Добавить запись глюкозы",
            icon = Icons.Default.Bloodtype,
            color = Color(0xFFEF4444)
        ),
        RecordType(
            route = Routes.INSULIN,
            title = "Инсулин",
            description = "Добавить запись инсулина",
            icon = Icons.Default.MedicalServices,
            color = Color(0xFF3B82F6)
        ),
        RecordType(
            route = Routes.ACTIVITY,
            title = "Активность",
            description = "Добавить запись активности",
            icon = Icons.Default.DirectionsRun,
            color = Color(0xFF10B981)
        ),
        RecordType(
            route = Routes.MEAL,
            title = "Прием пищи",
            description = "Добавить запись еды",
            icon = Icons.Default.Restaurant,
            color = Color(0xFFF59E0B)
        ),
        RecordType(
            route = Routes.MEDICATION,
            title = "Лекарство",
            description = "Добавить запись медикамента",
            icon = Icons.Default.LocalPharmacy,
            color = Color(0xFF8B5CF6)
        ),
        RecordType(
            route = Routes.BLOOD_PRESSURE,
            title = "Давление",
            description = "Добавить запись давления",
            icon = Icons.Default.FavoriteBorder,
            color = Color(0xFFE91E63)
        )
    ),
    onSelectScreen: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 120.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(recordTypes) { record ->
                Card(
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = LocalIndication.current,
                        onClick = { onSelectScreen(record.route) }
                    ),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = record.color.copy(alpha = 0.1f),
                        contentColor = record.color
                    ),
                    border = BorderStroke(1.dp, record.color.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = record.icon,
                            contentDescription = record.title,
                            tint = record.color,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            record.title,
                            style = MaterialTheme.typography.titleMedium,
                            softWrap = false,
                            maxLines = 1,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            record.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            softWrap = true,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}