package com.example.glucodialog.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.glucodialog.data.*
import com.example.glucodialog.ui.components.DateTimePickerButton
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import com.example.glucodialog.ui.constants.Labels.MEDICATION_UNITS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationEntryScreen(
    medicationDao: MedicationDao,
    onBack: () -> Unit,
    userProfile: UserProfile?,
) {
    val scope = rememberCoroutineScope()

    var medicationTypes by remember { mutableStateOf<List<MedicationType>>(emptyList()) }
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

    LaunchedEffect(Unit) {
        medicationTypes = medicationDao.getAllMedicationTypes().firstOrNull() ?: emptyList()
        if (medicationTypes.isNotEmpty()) selectedType = medicationTypes[0]
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
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2FE))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("💊 Препарат", style = MaterialTheme.typography.titleLarge)

                if (!addingNewType) {
                    ExposedDropdownMenuBox(
                        expanded = expandedType,
                        onExpandedChange = { expandedType = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedType?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Название препарата") },
                            isError = showTypeError,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                            modifier = Modifier
                                .menuAnchor()
                                .onFocusChanged { state ->
                                    if (!state.isFocused) typeTouched = true
                                }
                        )
                        ExposedDropdownMenu(
                            expanded = expandedType,
                            onDismissRequest = { expandedType = false }
                        ) {
                            medicationTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type.name) },
                                    onClick = {
                                        selectedType = type
                                        expandedType = false
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("➕ Добавить новый препарат") },
                                onClick = {
                                    addingNewType = true
                                    selectedType = null
                                    expandedType = false
                                }
                            )
                        }
                    }
                    if (showTypeError) {
                        Text("Выберите препарат", color = Color.Red, style = MaterialTheme.typography.bodySmall)
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
                            val type = MedicationType(name = newTypeName)
                            scope.launch {
                                medicationDao.insertMedicationType(type)
                                medicationTypes = medicationDao.getAllMedicationTypes().firstOrNull() ?: emptyList()
                                selectedType = medicationTypes.last()
                                addingNewType = false
                                newTypeName = ""
                                typeTouched = false
                                errorMessage = null
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Сохранить новый препарат", color = Color.White)
                    }
                }
            }
        }


        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = dosage,
                    onValueChange = { newValue ->
                        dosage = newValue
                        errorMessage = null
                    },
                    label = { Text("Дозировка") },
                    isError = showDosageError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged { state -> if (!state.isFocused) dosageTouched = true }
                )
                ExposedDropdownMenuBox(
                    expanded = expandedUnit,
                    onExpandedChange = { expandedUnit = it },
                    modifier = Modifier.weight(1f)
                ) {
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
                                onClick = {
                                    unit = key
                                    expandedUnit = false
                                }
                            )
                        }
                    }
                }
            }
            if (showDosageError) {
                Text(
                    "Введите корректную дозировку",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7E6))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                val dateFormat = remember { java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }
                Text("⏰ Дата и время приема: ${dateFormat.format(calendar.time)}")
                DateTimePickerButton(
                    calendar = calendar,
                    onDateTimeSelected = { updatedCalendar ->
                        calendar = updatedCalendar
                    }
                )
            }
        }

        errorMessage?.let { Text(it, color = Color.Red) }

        Button(
            onClick = {
                attemptedSave = true
                if (!canSave) {
                    errorMessage = "Заполните все поля корректно"
                    return@Button
                }
                val entry = MedicationEntry(
                    medicationTypeId = selectedType!!.id,
                    dose = dosage.text,
                    unit = unit,
                    timestamp = calendar.timeInMillis
                )
                scope.launch {
                    medicationDao.insertMedicationEntry(entry)
                    onBack()
                }
                dosage = TextFieldValue("")
                dosageTouched = false
                typeTouched = false
                attemptedSave = false
                errorMessage = null
            },
            enabled = canSave,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("💾 Добавить запись", color = Color.White)
        }
    }
}