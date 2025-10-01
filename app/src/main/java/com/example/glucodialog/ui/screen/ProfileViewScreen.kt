package com.example.glucodialog.ui.screen

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.glucodialog.ExportPDFActivity
import com.example.glucodialog.ExportReportActivity
import com.example.glucodialog.ImportActivity
import com.example.glucodialog.domain.model.UserProfile
import com.example.glucodialog.ui.constants.Labels.DIABETES_TYPE_LABELS


@Composable
fun ProfileViewScreen(
    profile: UserProfile,
    onEdit: () -> Unit
) {
    val height = profile.height
    val weight = profile.weight
    val bmi = if (height > 0 && weight > 0) weight / ((height / 100) * (height / 100)) else null

    val colorScheme = MaterialTheme.colorScheme

    fun getBMIColor(bmi: Double) = when {
        bmi < 18.5 -> colorScheme.secondary
        bmi < 25 -> colorScheme.primary
        bmi < 30 -> colorScheme.tertiary
        else -> colorScheme.error
    }

    fun getBMIStatus(bmi: Double) = when {
        bmi < 18.5 -> "Недостаточный"
        bmi < 25 -> "Нормальный"
        bmi < 30 -> "Избыточный"
        else -> "Ожирение"
    }

    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(colorScheme.primaryContainer, shape = MaterialTheme.shapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "User",
                        tint = colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(profile.name, style = MaterialTheme.typography.headlineSmall)
                    Text(
                        "${profile.gender} • ${DIABETES_TYPE_LABELS[profile.diabetesType] ?: ""}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                    bmi?.let {
                        Text(
                            "ИМТ: ${String.format("%.1f", it)} (${getBMIStatus(it)})",
                            color = getBMIColor(it),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Straighten, contentDescription = null, tint = colorScheme.secondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Физические параметры", style = MaterialTheme.typography.titleMedium)
                }

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Рост:", style = MaterialTheme.typography.bodyMedium)
                    Text("${profile.height} см", style = MaterialTheme.typography.bodyMedium)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Вес:", style = MaterialTheme.typography.bodyMedium)
                    Text("${profile.weight} кг", style = MaterialTheme.typography.bodyMedium)
                }
                bmi?.let {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("ИМТ:", style = MaterialTheme.typography.bodyMedium)
                        Text(String.format("%.1f", it), color = getBMIColor(it))
                    }
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.HealthAndSafety, contentDescription = null, tint = colorScheme.tertiary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Инсулинотерапия", style = MaterialTheme.typography.titleMedium)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Болюсный:", style = MaterialTheme.typography.bodyMedium)
                    Text("${profile.bolusInsulin} (${profile.bolusDose} ед)", style = MaterialTheme.typography.bodyMedium)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Базальный:", style = MaterialTheme.typography.bodyMedium)
                    Text("${profile.basalInsulin} (${profile.basalDose} ед)", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.TrackChanges, contentDescription = null, tint = colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Целевой диапазон глюкозы", style = MaterialTheme.typography.titleMedium)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Минимум", style = MaterialTheme.typography.bodyMedium)
                        Text("${profile.targetGlucoseLow}", style = MaterialTheme.typography.titleLarge, color = colorScheme.primary)
                        Text(profile.glucoseUnit, style = MaterialTheme.typography.bodySmall)
                    }
                    Text("—", color = colorScheme.onSurfaceVariant)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Максимум", style = MaterialTheme.typography.bodyMedium)
                        Text("${profile.targetGlucoseHigh}", style = MaterialTheme.typography.titleLarge, color = colorScheme.primary)
                        Text(profile.glucoseUnit, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Medication, contentDescription = null, tint = colorScheme.error)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Медикаменты", style = MaterialTheme.typography.titleMedium)
                }

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(profile.medication, style = MaterialTheme.typography.bodyMedium)
                    Text("${profile.medicationDose} ${profile.medicationUnit}", style = MaterialTheme.typography.bodyMedium)
                }
                profile.medicationTimeMinutesFromMidnight?.let { minutes ->
                    val hours = minutes / 60
                    val mins = minutes % 60
                    Text("Время приема: %02d:%02d".format(hours, mins), style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Folder, contentDescription = null, tint = colorScheme.secondaryContainer)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Управление данными", style = MaterialTheme.typography.titleMedium)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(onClick = {
                        val intent = Intent(context, ImportActivity::class.java)
                        context.startActivity(intent)
                    }) { Text("Импорт") }

                    Button(onClick = {
                        val intent = Intent(context, ExportReportActivity::class.java)
                        context.startActivity(intent)
                    }) { Text("Экспорт XLS") }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(onClick = {
                        val intent = Intent(context, ExportPDFActivity::class.java)
                        context.startActivity(intent)
                    }) { Text("Экспорт PDF") }
                }
            }
        }
    }
}