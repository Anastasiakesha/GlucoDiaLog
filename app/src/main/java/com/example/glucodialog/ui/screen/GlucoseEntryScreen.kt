package com.example.glucodialog.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessAlarms
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.room.util.copy
import com.example.glucodialog.domain.model.GlucoseEntry
import com.example.glucodialog.domain.model.UserProfile
import com.example.glucodialog.ui.components.DateTimePickerButton
import com.example.glucodialog.ui.constants.Labels.GLUCOSE_UNITS
import com.example.glucodialog.ui.viewmodel.GlucoseViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.text.iterator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlucoseEntryScreen(
    viewModel: GlucoseViewModel,
    userProfile: UserProfile?,
    entryId: Int = -1,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var value by remember { mutableStateOf(TextFieldValue("")) }
    var notes by remember { mutableStateOf(TextFieldValue("")) }
    var selectedUnit by remember { mutableStateOf("ммоль/л") }
    var calendar by remember { mutableStateOf(Calendar.getInstance()) }
    var wasTouched by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var correctionDose by remember { mutableStateOf("Корректирующая доза: —") }

    fun updateCorrectionDose() {
        val glucoseMmolL = value.text.toDoubleOrNull()?.let { v ->
            if (selectedUnit == "мг/дл") v / 18.0 else v
        }
        if (glucoseMmolL == null || userProfile == null) {
            correctionDose = "Корректирующая доза: —"
            return
        }
        val tdd = userProfile.bolusDose + userProfile.basalDose
        val target = userProfile.targetGlucoseHigh
        correctionDose = if (tdd > 0 && glucoseMmolL > target) {
            val isf = 100 / tdd
            val excess = glucoseMmolL - target
            val dose = excess / isf
            "Корректирующая доза: %.1f ед.".format(dose)
        } else {
            "Корректирующая доза: 0 ед."
        }
    }

    LaunchedEffect(entryId) {
        if (entryId != -1) {
            val entryToEdit = viewModel.getEntryById(entryId)
            entryToEdit?.let { entry ->
                value = TextFieldValue(entry.glucoseLevel.toString())
                notes = TextFieldValue(entry.note ?: "")
                selectedUnit = entry.unit
                calendar = Calendar.getInstance().apply { timeInMillis = entry.timestamp }

                updateCorrectionDose()
            }
        }
    }

    val canSave = errorMessage == null &&
            value.text.isNotBlank() &&
            value.text.toDoubleOrNull()?.let { numeric ->
                when (selectedUnit) {
                    "ммоль/л" -> numeric in 1.5..50.0
                    "мг/дл" -> numeric in 27.0..900.0
                    else -> false
                }
            } == true

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(
                imageVector = Icons.Filled.Bloodtype,
                contentDescription = "Уровень глюкозы",
                tint = MaterialTheme.colorScheme.primary
            )
            Text(" Уровень глюкозы", style = MaterialTheme.typography.titleLarge)
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                val placeholderText =
                    if (selectedUnit == "ммоль/л") "Введите от 1.5 до 50 ммоль/л"
                    else "Введите от 27 до 900 мг/дл"

                        OutlinedTextField(
                            value = value,
                            onValueChange = {
                                wasTouched = true
                                val filtered = it.text.filter { ch -> ch.isDigit() || ch == '.' }
                                val oneDot = buildString {
                                    var dotUsed = false
                                    for (ch in filtered) {
                                        if (ch == '.' && !dotUsed) {
                                            append(ch)
                                            dotUsed = true
                                        } else if (ch != '.') append(ch)
                                    }
                                }

                                val cursor = it.selection
                                val newSelection = TextRange(
                                    start = oneDot.length.coerceAtMost(cursor.start),
                                    end = oneDot.length.coerceAtMost(cursor.end)
                                )

                                value = TextFieldValue(
                                    text = oneDot,
                                    selection = newSelection
                                )

                                errorMessage = null
                                updateCorrectionDose()
                            },
                            label = { Text("Значение ($selectedUnit)") },
                            placeholder = { Text(placeholderText) },
                            singleLine = true,
                            isError = wasTouched && errorMessage != null,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                if (wasTouched && errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                var expandedUnit by remember { mutableStateOf(false) }
                Box {
                    OutlinedButton(onClick = { expandedUnit = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(selectedUnit)
                    }
                    DropdownMenu(expanded = expandedUnit, onDismissRequest = { expandedUnit = false }) {
                        GLUCOSE_UNITS.forEach { unit ->
                            DropdownMenuItem(
                                text = { Text(unit) },
                                onClick = {
                                    selectedUnit = unit
                                    expandedUnit = false
                                    updateCorrectionDose()
                                }
                            )
                        }
                    }
                }

                val numericValue = value.text.toDoubleOrNull()
                if (numericValue != null && userProfile != null) {
                    val (valueToCompare, low, high) = when (userProfile.glucoseUnit) {
                        "мг/дл" -> {
                            val v = if (selectedUnit == "ммоль/л") numericValue * 18 else numericValue
                            Triple(v, userProfile.targetGlucoseLow, userProfile.targetGlucoseHigh)
                        }
                        else -> {
                            val v = if (selectedUnit == "мг/дл") numericValue / 18 else numericValue
                            Triple(v, userProfile.targetGlucoseLow, userProfile.targetGlucoseHigh)
                        }
                    }

                    val (statusColor, statusText) = when {
                        valueToCompare < low -> MaterialTheme.colorScheme.primary to "Низкий"
                        valueToCompare in low..high -> MaterialTheme.colorScheme.secondary to "Норма"
                        valueToCompare <= high + (if (userProfile.glucoseUnit == "мг/дл") 54 else 3) ->
                            MaterialTheme.colorScheme.tertiary to "Выше нормы"
                        else -> MaterialTheme.colorScheme.error to "Очень высокий"
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(statusColor.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp))
                            .padding(8.dp)
                    ) {
                        Text(statusText, color = statusColor, style = MaterialTheme.typography.bodyMedium)
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
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Заметки (необязательно)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                Text(correctionDose, style = MaterialTheme.typography.bodyMedium)
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

        Button(
            onClick = {
                val numeric = value.text.toDoubleOrNull() ?: return@Button
                val entry = GlucoseEntry(
                    id = if (entryId != -1) entryId else 0, // <--- ВАЖНО!
                    glucoseLevel = numeric,
                    unit = selectedUnit,
                    timestamp = calendar.timeInMillis,
                    note = notes.text.ifBlank { null }
                )
                scope.launch {
                    if (entryId != -1) {
                        viewModel.updateEntry(entry)
                    } else {
                        viewModel.addEntry(entry)
                    }
                    onBack()
                }
                value = TextFieldValue("")
                notes = TextFieldValue("")
                wasTouched = false
            },
            enabled = canSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
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