package com.example.glucodialog.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.glucodialog.data.local.InsulinType
import com.example.glucodialog.data.local.MedicationType
import com.example.glucodialog.domain.model.UserProfile
import com.example.glucodialog.ui.components.TimePickerDialog
import com.example.glucodialog.ui.viewmodel.TherapyPlanViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TherapyPlanScreen(
    viewModel: TherapyPlanViewModel,
    userProfile: UserProfile,
    onBack: () -> Unit
) {

    LaunchedEffect(userProfile.id) { viewModel.setUserId(userProfile.id) }

    val activePlan by viewModel.activePlan.collectAsState()
    val insulinTypes by viewModel.insulinTypes.collectAsState()
    val medicationTypes by viewModel.medicationTypes.collectAsState()

    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }

    var showInsulinDialog by remember { mutableStateOf(false) }
    var showMedDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("План лечения") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Назад") }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (activePlan == null) {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.EventNote, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                        Text("У вас нет активного плана лечения", style = MaterialTheme.typography.titleMedium)
                        Button(onClick = { viewModel.createNewPlan(userProfile.id) }) {
                            Text("Создать новый план")
                        }
                    }
                }
            } else {
                val planDetails = activePlan!!

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Текущий план", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        Text("Начат: ${dateFormat.format(Date(planDetails.plan.startDate))}", style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalHospital, null, tint = MaterialTheme.colorScheme.tertiary)
                            Spacer(Modifier.width(8.dp))
                            Text("Инсулинотерапия", style = MaterialTheme.typography.titleMedium)
                        }

                        val totalBolus = planDetails.insulinPlans.filter { it.type?.type == "Болюсный" }.sumOf { it.plan.dose }
                        val totalBasal = planDetails.insulinPlans.filter { it.type?.type == "Базальный" }.sumOf { it.plan.dose }

                        Text("Болюсный: $totalBolus ед / сутки", style = MaterialTheme.typography.bodyMedium)
                        Text("Базальный: $totalBasal ед / сутки", style = MaterialTheme.typography.bodyMedium)
                        Text("Общая суточная доза: ${totalBolus + totalBasal} ед", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(4.dp))

                        planDetails.insulinPlans.forEach { ip ->
                            Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp)).padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("${ip.type?.name ?: "Неизвестно"} (${ip.type?.type ?: ""})", style = MaterialTheme.typography.bodyMedium)
                                    ip.plan.reminderTimeMinutes?.let { t ->
                                        Text("Напоминание: %02d:%02d".format(t / 60, t % 60), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Text("${ip.plan.dose} ед", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        TextButton(onClick = { showInsulinDialog = true }) { Text("➕ Добавить инсулин") }
                    }
                }

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Medication, null, tint = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.width(8.dp))
                            Text("Медикаменты", style = MaterialTheme.typography.titleMedium)
                        }
                        planDetails.medicationPlans.forEach { mp ->
                            Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp)).padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text(mp.type?.name ?: "Неизвестно", style = MaterialTheme.typography.bodyMedium)
                                    mp.plan.reminderTimeMinutes?.let { t ->
                                        Text("Напоминание: %02d:%02d".format(t / 60, t % 60), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Text(mp.plan.dose, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        TextButton(onClick = { showMedDialog = true }) { Text("➕ Добавить лекарство") }
                    }
                }

                Button(
                    onClick = { viewModel.finishCurrentPlan(planDetails.plan.id) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Завершить этот план лечения")
                }
            }
        }
    }

    if (showInsulinDialog) {
        var dose by remember { mutableStateOf("") }
        var type by remember { mutableStateOf<InsulinType?>(null) } // <-- Теперь null
        var time by remember { mutableStateOf<Int?>(null) }
        var showTime by remember { mutableStateOf(false) }
        var expanded by remember { mutableStateOf(false) } // Для DropdownMenu

        AlertDialog(
            onDismissRequest = { showInsulinDialog = false },
            title = { Text("Добавить инсулин") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = type?.name ?: "Выберите тип инсулина",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Тип инсулина") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            insulinTypes.forEach { t ->
                                DropdownMenuItem(
                                    text = { Text("${t.name} (${t.type})") },
                                    onClick = { type = t; expanded = false }
                                )
                            }
                        }
                    }

                    OutlinedTextField(value = dose, onValueChange = { dose = it }, label = { Text("Доза") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                    Button(onClick = { showTime = true }, modifier = Modifier.fillMaxWidth()) { Text(time?.let { "%02d:%02d".format(it/60, it%60) } ?: "Выбрать время") }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (type != null && dose.isNotBlank()) {
                            viewModel.addInsulinToPlan(activePlan!!.plan.id, type!!.id, dose.toDoubleOrNull() ?: 0.0, time)
                            showInsulinDialog = false
                        }
                    },
                    enabled = type != null && dose.isNotBlank() // Кнопка неактивна, пока не выбран тип
                ) { Text("Сохранить") }
            },
            dismissButton = { TextButton(onClick = { showInsulinDialog = false }) { Text("Отмена") } }
        )

        if (showTime) {
            TimePickerDialog(8, 0, { showTime = false }, { h, m -> time = h * 60 + m; showTime = false })
        }
    }

    if (showMedDialog) {
        var dose by remember { mutableStateOf("") }
        var type by remember { mutableStateOf<MedicationType?>(null) } // <-- Теперь null
        var time by remember { mutableStateOf<Int?>(null) }
        var showTime by remember { mutableStateOf(false) }
        var expanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showMedDialog = false },
            title = { Text("Добавить лекарство") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = type?.name ?: "Выберите препарат",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Препарат") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            medicationTypes.forEach { t ->
                                DropdownMenuItem(
                                    text = { Text(t.name) },
                                    onClick = { type = t; expanded = false }
                                )
                            }
                        }
                    }

                    OutlinedTextField(value = dose, onValueChange = { dose = it }, label = { Text("Доза") }, modifier = Modifier.fillMaxWidth())
                    Button(onClick = { showTime = true }, modifier = Modifier.fillMaxWidth()) { Text(time?.let { "%02d:%02d".format(it/60, it%60) } ?: "Выбрать время") }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (type != null && dose.isNotBlank()) {
                            viewModel.addMedicationToPlan(activePlan!!.plan.id, type!!.id, dose, time)
                            showMedDialog = false
                        }
                    },
                    enabled = type != null && dose.isNotBlank()
                ) { Text("Сохранить") }
            },
            dismissButton = { TextButton(onClick = { showMedDialog = false }) { Text("Отмена") } }
        )

        if (showTime) {
            TimePickerDialog(8, 0, { showTime = false }, { h, m -> time = h * 60 + m; showTime = false })
        }
    }

}