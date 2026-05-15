package com.example.glucodialog.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessAlarms
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.glucodialog.domain.model.MedicationType
import com.example.glucodialog.domain.model.MedicationEntry
import com.example.glucodialog.ui.components.DateTimePickerButton
import com.example.glucodialog.ui.constants.Labels.MEDICATION_UNITS
import com.example.glucodialog.ui.viewmodel.MedicationViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationEntryScreen(
    viewModel: MedicationViewModel,
    entryId: Int = -1,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()

    val medicationTypes by viewModel.medicationTypes.collectAsState()
    var selectedType by remember { mutableStateOf<MedicationType?>(null) }
    var addingNewType by remember { mutableStateOf(false) }
    var newTypeName by remember { mutableStateOf("") }

    var dosage by remember { mutableStateOf(TextFieldValue("")) }
    var dosageTouched by remember { mutableStateOf(false) }
    var typeTouched by remember { mutableStateOf(false) }
    var attemptedSave by remember { mutableStateOf(false) }

    var unit by remember { mutableStateOf(MEDICATION_UNITS.keys.first()) }
    var calendar by remember { mutableStateOf(Calendar.getInstance()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var expandedType by remember { mutableStateOf(false) }
    var expandedUnit by remember { mutableStateOf(false) }

    LaunchedEffect(entryId, medicationTypes) {
        if (medicationTypes.isNotEmpty()) {
            if (entryId != -1) {
                val entryToEdit = viewModel.getEntryById(entryId)
                entryToEdit?.let { entry ->
                    selectedType = medicationTypes.find { it.id == entry.medicationTypeId }
                    dosage = TextFieldValue(entry.dose)
                    unit = entry.unit
                    calendar = Calendar.getInstance().apply { timeInMillis = entry.timestamp }
                }
            } else if (selectedType == null) {
                selectedType = medicationTypes.first()
            }
        }
    }

    val doseValue = dosage.text.toDoubleOrNull()
    val canSave = selectedType != null && doseValue != null && doseValue > 0
    val showDosageError = (dosageTouched || attemptedSave) && !dosage.text.isBlank() && (doseValue == null || doseValue <= 0)
    val showTypeError = (typeTouched || attemptedSave) && selectedType == null && !addingNewType

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(
                imageVector = Icons.Filled.LocalPharmacy,
                contentDescription = "Ввод лекарства",
                tint = MaterialTheme.colorScheme.primary
            )
            Text(" Ввод лекарства", style = MaterialTheme.typography.titleLarge)
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
                            label = { Text("Название препарата") },
                            isError = showTypeError,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .onFocusChanged { if (!it.isFocused) typeTouched = true }
                        )
                        ExposedDropdownMenu(expanded = expandedType, onDismissRequest = { expandedType = false }) {
                            medicationTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type.name) },
                                    onClick = { selectedType = type; expandedType = false }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("➕ Добавить новый препарат") },
                                onClick = { addingNewType = true; selectedType = null; expandedType = false }
                            )
                        }
                    }
                    if (showTypeError) {
                        Text("Выберите препарат", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                } else {
                    OutlinedTextField(
                        value = newTypeName,
                        onValueChange = { newTypeName = it },
                        label = { Text("Название нового препарата") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            if (newTypeName.isBlank()) {
                                errorMessage = "Введите название препарата"
                                return@Button
                            }

                            scope.launch {
                                viewModel.addMedicationType(
                                    MedicationType(name = newTypeName)
                                ) { insertedId ->

                                    selectedType = MedicationType(
                                        id = insertedId,
                                        name = newTypeName
                                    )

                                    addingNewType = false
                                    newTypeName = ""
                                    typeTouched = false
                                    errorMessage = null
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Сохранить новый препарат", color = Color.White)
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
            Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = dosage,
                    onValueChange = { dosage = it; errorMessage = null },
                    label = { Text("Дозировка") },
                    isError = showDosageError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged { if (!it.isFocused) dosageTouched = true }
                )

                ExposedDropdownMenuBox(expanded = expandedUnit, onExpandedChange = { expandedUnit = it }, modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = unit,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Ед. изм.") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUnit) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedUnit,
                        onDismissRequest = { expandedUnit = false }
                    ) {
                        MEDICATION_UNITS.forEach { (key, value) ->
                            DropdownMenuItem(
                                text = { Text(value) },
                                onClick = { unit = key; expandedUnit = false }
                            )
                        }
                    }
                }
            }
            if (showDosageError) {
                Text("Введите корректную дозировку", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp, top = 4.dp))
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
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
                if (!canSave) {
                    errorMessage = "Заполните все поля корректно"
                    return@Button
                }
                val entry = MedicationEntry(
                    id = if (entryId != -1) entryId else 0,
                    medicationTypeId = selectedType!!.id,
                    dose = dosage.text,
                    unit = unit,
                    timestamp = calendar.timeInMillis
                )
                scope.launch {
                    if (entryId != -1) {
                        viewModel.updateMedicationEntry(entry)
                    } else {
                        viewModel.addMedicationEntry(entry)
                    }
                    onBack()
                }
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
            Text(if (entryId != -1) "Сохранить изменения" else "Добавить запись", color = Color.White)
        }
    }
}