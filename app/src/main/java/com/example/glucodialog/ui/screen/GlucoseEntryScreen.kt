package com.example.glucodialog.ui

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.glucodialog.data.repository.GlucoseDao
import com.example.glucodialog.data.local.GlucoseEntry
import com.example.glucodialog.data.local.UserProfile
import com.example.glucodialog.ui.components.DateTimePickerButton
import com.example.glucodialog.ui.constants.Labels.GLUCOSE_UNITS
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun GlucoseEntryScreen(
    userProfile: UserProfile?,
    glucoseDao: GlucoseDao,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var value by remember { mutableStateOf(TextFieldValue("")) }
    var notes by remember { mutableStateOf(TextFieldValue("")) }
    var selectedUnit by remember { mutableStateOf("ммоль/л") }

    var correctionDose by remember { mutableStateOf("Корректирующая доза: —") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var wasTouched by remember { mutableStateOf(false) }
    var calendar by remember { mutableStateOf(Calendar.getInstance()) }

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
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2FE))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("💧 Уровень глюкозы", style = MaterialTheme.typography.titleLarge)

                val placeholderText =
                    if (selectedUnit == "ммоль/л") "Введите от 1.5 до 50 ммоль/л"
                    else "Введите от 27 до 900 мг/дл"

                OutlinedTextField(
                    value = value,
                    onValueChange = { newValue ->
                        wasTouched = true
                        val filtered = newValue.text.filter { it.isDigit() || it == '.' }
                        val oneDot = buildString {
                            var dotUsed = false
                            for (ch in filtered) {
                                if (ch == '.') {
                                    if (!dotUsed) {
                                        append(ch)
                                        dotUsed = true
                                    }
                                } else append(ch)
                            }
                        }
                        value = TextFieldValue(
                            text = oneDot,
                            selection = TextRange(oneDot.length)
                        )
                        errorMessage = null
                        updateCorrectionDose()
                    },
                    label = { Text("Значение ($selectedUnit)") },
                    placeholder = { Text(placeholderText) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            if (!focusState.isFocused && wasTouched) {
                                val numeric = value.text.toDoubleOrNull()
                                errorMessage = when {
                                    numeric == null -> "Введите корректное число"
                                    selectedUnit == "ммоль/л" && numeric !in 1.5..50.0 ->
                                        "Значение должно быть от 1.5 до 50 ммоль/л"
                                    selectedUnit == "мг/дл" && numeric !in 27.0..900.0 ->
                                        "Значение должно быть от 27 до 900 мг/дл"
                                    else -> null
                                }
                            }
                        },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = wasTouched && errorMessage != null
                )

                if (wasTouched && errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                var expandedUnit by remember { mutableStateOf(false) }
                Box {
                    OutlinedButton(
                        onClick = { expandedUnit = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(selectedUnit)
                    }
                    DropdownMenu(
                        expanded = expandedUnit,
                        onDismissRequest = { expandedUnit = false }) {
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
                            val v = if (selectedUnit == "ммоль/л") numericValue * 18.0 else numericValue
                            Triple(v, userProfile.targetGlucoseLow, userProfile.targetGlucoseHigh)
                        }
                        else -> {
                            val v = if (selectedUnit == "мг/дл") numericValue / 18.0 else numericValue
                            Triple(v, userProfile.targetGlucoseLow, userProfile.targetGlucoseHigh)
                        }
                    }

                    val (statusColor, statusText) = when {
                        valueToCompare < low -> Color(0xFF3B82F6) to "Низкий"
                        valueToCompare in low..high -> Color(0xFF16A34A) to "Норма"
                        valueToCompare <= high + (if (userProfile.glucoseUnit == "мг/дл") 54 else 3) ->
                            Color(0xFFF59E0B) to "Выше нормы"
                        else -> Color(0xFFEF4444) to "Очень высокий"
                    }

                    Box(
                        modifier = Modifier
                            .background(statusColor, shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(statusText, color = Color.White)
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
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
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7E6))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val dateFormat =
                    remember { java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }
                Text("⏰ Дата и время замера: ${dateFormat.format(calendar.time)}")
                DateTimePickerButton(
                    calendar = calendar,
                    onDateTimeSelected = { updatedCalendar ->
                        calendar = updatedCalendar
                    }
                )
            }
        }

        Button(
            onClick = {
                val numeric = value.text.toDoubleOrNull() ?: return@Button
                val entry = GlucoseEntry(
                    glucoseLevel = numeric,
                    unit = selectedUnit,
                    timestamp = calendar.timeInMillis,
                    note = notes.text.ifBlank { null }
                )
                scope.launch {
                    glucoseDao.insertGlucoseEntry(entry)
                    onBack()
                }
                value = TextFieldValue("")
                notes = TextFieldValue("")
                wasTouched = false
            },
            enabled = canSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("💾 Добавить запись", color = Color.White)
        }
    }
}