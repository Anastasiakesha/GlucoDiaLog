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
import androidx.compose.ui.unit.dp
import com.example.glucodialog.data.*
import com.example.glucodialog.ui.components.DateTimePickerButton
import com.example.glucodialog.ui.constants.Labels.DURATION_OPTIONS
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsulinEntryScreen(
    insulinDao: InsulinDao,
    onBack: () -> Unit,
    userProfile: UserProfile?,
) {
    val scope = rememberCoroutineScope()

    var insulinTypes by remember { mutableStateOf<List<InsulinType>>(emptyList()) }
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

    LaunchedEffect(Unit) {
        insulinTypes = insulinDao.getAllInsulinTypes().firstOrNull() ?: emptyList()
        if (insulinTypes.isNotEmpty()) selectedType = insulinTypes[0]
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
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2FE))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("💉 Тип инсулина", style = MaterialTheme.typography.titleLarge)

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
                            label = { Text("Название инсулина") },
                            isError = showTypeError,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                            modifier = Modifier
                                .menuAnchor()
                                .onFocusChanged { state ->
                                    if (!state.isFocused) typeFocusedOnce = true
                                }
                        )
                        ExposedDropdownMenu(
                            expanded = expandedType,
                            onDismissRequest = { expandedType = false }
                        ) {
                            insulinTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type.name) },
                                    onClick = {
                                        selectedType = type
                                        expandedType = false
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("➕ Добавить новый тип") },
                                onClick = {
                                    addingNewType = true
                                    selectedType = null
                                    expandedType = false
                                }
                            )
                        }
                    }
                    if (showTypeError) {
                        Text("Выберите тип инсулина", color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    }
                } else {
                    // Новый тип
                    OutlinedTextField(
                        value = newTypeName,
                        onValueChange = { newTypeName = it },
                        label = { Text("Название нового типа") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    ExposedDropdownMenuBox(
                        expanded = expandedDuration,
                        onExpandedChange = { expandedDuration = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedDuration ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Продолжительность действия") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDuration) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedDuration,
                            onDismissRequest = { expandedDuration = false }
                        ) {
                            DURATION_OPTIONS.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type) },
                                    onClick = {
                                        selectedDuration = type
                                        expandedDuration = false
                                    }
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = newTypeDuration,
                        onValueChange = { newTypeDuration = it },
                        label = { Text("Длительность действия (часы)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            val durationHours = newTypeDuration.toIntOrNull()
                            if (newTypeName.isBlank() || durationHours == null || durationHours <= 0) {
                                errorMessage = "Введите корректные данные нового типа"
                                return@Button
                            }
                            val type = InsulinType(
                                name = newTypeName,
                                type = selectedDuration ?: "обычный",
                                durationHours = durationHours
                            )
                            scope.launch {
                                insulinDao.insertInsulinType(type)
                                insulinTypes = insulinDao.getAllInsulinTypes().firstOrNull() ?: emptyList()
                                selectedType = insulinTypes.last()
                                addingNewType = false
                                newTypeName = ""
                                newTypeDuration = ""
                                selectedDuration = null
                                errorMessage = null
                                typeFocusedOnce = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Сохранить новый тип", color = Color.White)
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = dosage,
                    onValueChange = {
                        dosage = it
                        errorMessage = null
                    },
                    label = { Text("Дозировка (единицы)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = showDosageError,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { state ->
                            if (!state.isFocused) dosageFocusedOnce = true
                        }
                )
                if (showDosageError) {
                    Text("Введите корректную дозировку", color = Color.Red, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7E6))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                scope.launch {
                    insulinDao.insertInsulinEntry(entry)
                    onBack()
                }
                dosage = ""
                dosageFocusedOnce = false
                typeFocusedOnce = false
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