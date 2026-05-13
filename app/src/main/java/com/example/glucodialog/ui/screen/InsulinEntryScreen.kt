package com.example.glucodialog.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessAlarms
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.glucodialog.domain.model.InsulinEntry
import com.example.glucodialog.domain.model.InsulinType
import com.example.glucodialog.domain.model.UserProfile
import com.example.glucodialog.ui.components.DateTimePickerButton
import com.example.glucodialog.ui.constants.Labels.DURATION_OPTIONS
import com.example.glucodialog.ui.viewmodel.InsulinViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsulinEntryScreen(
    viewModel: InsulinViewModel,
    userProfile: UserProfile?,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val insulinTypes by viewModel.insulinTypes.collectAsState()

    var selectedType by remember { mutableStateOf<InsulinType?>(null) }
    var addingNewType by remember { mutableStateOf(false) }
    var newTypeName by remember { mutableStateOf("") }
    var newTypeDuration by remember { mutableStateOf("") }
    var selectedDuration by remember { mutableStateOf<String?>(null) }

    var dosage by remember { mutableStateOf("") }
    var dosageFocusedOnce by remember { mutableStateOf(false) }
    var typeFocusedOnce by remember { mutableStateOf(false) }
    var attemptedSave by remember { mutableStateOf(false) }

    var calendar by remember { mutableStateOf(Calendar.getInstance()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var expandedType by remember { mutableStateOf(false) }
    var expandedDuration by remember { mutableStateOf(false) }

    LaunchedEffect(insulinTypes) {
        if (insulinTypes.isNotEmpty() && selectedType == null) selectedType = insulinTypes[0]
    }

    val doseValue = dosage.toDoubleOrNull()
    val canSave = selectedType != null && doseValue != null && doseValue > 0
    val showDosageError = (dosageFocusedOnce || attemptedSave) && dosage.isNotBlank() && (doseValue == null || doseValue <= 0)
    val showTypeError = (typeFocusedOnce || attemptedSave) && selectedType == null && !addingNewType

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(
                imageVector = Icons.Filled.MedicalServices,
                contentDescription = "Инсулин",
                tint = MaterialTheme.colorScheme.primary
            )
            Text(" Ввод инсулина", style = MaterialTheme.typography.titleLarge)
        }


        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

                if (!addingNewType) {
                    ExposedDropdownMenuBox(expanded = expandedType, onExpandedChange = { expandedType = it }) {
                        OutlinedTextField(
                            value = selectedType?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Название инсулина") },
                            isError = showTypeError,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .onFocusChanged { if (!it.isFocused) typeFocusedOnce = true }
                        )
                        ExposedDropdownMenu(expanded = expandedType, onDismissRequest = { expandedType = false }) {
                            insulinTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type.name) },
                                    onClick = { selectedType = type; expandedType = false }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("➕ Добавить новый тип") },
                                onClick = { addingNewType = true; selectedType = null; expandedType = false }
                            )
                        }
                    }
                    if (showTypeError) {
                        Text("Выберите тип инсулина", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                } else {
                    OutlinedTextField(newTypeName, { newTypeName = it }, label = { Text("Название нового типа") }, modifier = Modifier.fillMaxWidth())
                    ExposedDropdownMenuBox(expanded = expandedDuration, onExpandedChange = { expandedDuration = it }) {
                        OutlinedTextField(
                            value = selectedDuration ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Продолжительность действия") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDuration) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(expanded = expandedDuration, onDismissRequest = { expandedDuration = false }) {
                            DURATION_OPTIONS.forEach { option ->
                                DropdownMenuItem(text = { Text(option) }, onClick = { selectedDuration = option; expandedDuration = false })
                            }
                        }
                    }
                    OutlinedTextField(newTypeDuration, { newTypeDuration = it }, label = { Text("Длительность действия (часы)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())

                    Button(
                        onClick = {
                            val durationHours = newTypeDuration.toIntOrNull()
                            if (newTypeName.isBlank() || durationHours == null || durationHours <= 0) {
                                errorMessage = "Введите корректные данные нового типа"
                                return@Button
                            }

                            scope.launch {
                                viewModel.addInsulinType(
                                    InsulinType(
                                        name = newTypeName,
                                        type = "default",
                                        durationHours = durationHours
                                    )
                                ) { insertedId ->

                                    selectedType = InsulinType(
                                        id = insertedId,
                                        name = newTypeName,
                                        type = "default",
                                        durationHours = durationHours
                                    )

                                    addingNewType = false

                                    newTypeName = ""
                                    newTypeDuration = ""
                                    selectedDuration = null
                                    errorMessage = null
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Сохранить новый тип", color = Color.White)
                    }

                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = dosage,
                    onValueChange = { dosage = it; errorMessage = null },
                    label = { Text("Дозировка (единицы)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = showDosageError,
                    modifier = Modifier.fillMaxWidth().onFocusChanged { if (!it.isFocused) dosageFocusedOnce = true }
                )
                if (showDosageError) {
                    Text("Введите корректную дозировку", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Filled.AccessAlarms,
                        contentDescription = "Дата и время",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text("Дата и время: ${dateFormat.format(calendar.time)}")
                }
                DateTimePickerButton(calendar = calendar, onDateTimeSelected = { calendar = it })
            }
        }

        errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Button(
            onClick = {
                attemptedSave = true
                val doseValueNonNull = dosage.toDoubleOrNull()
                if (!canSave) {
                    errorMessage = "Заполните все поля корректно"
                    dosageFocusedOnce = true
                    typeFocusedOnce = true
                    return@Button
                }
                val entry = InsulinEntry(
                    insulinTypeId = selectedType!!.id,
                    doseUnits = doseValueNonNull!!,
                    unit = "Ед",
                    timestamp = calendar.timeInMillis
                )
                scope.launch { viewModel.addInsulinEntry(entry); onBack() }
                dosage = ""
                dosageFocusedOnce = false
                typeFocusedOnce = false
                attemptedSave = false
                errorMessage = null
            },
            enabled = canSave,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                imageVector = Icons.Filled.Save,
                contentDescription = "Сохранить запись",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("Добавить запись", color = Color.White)
        }
    }
}