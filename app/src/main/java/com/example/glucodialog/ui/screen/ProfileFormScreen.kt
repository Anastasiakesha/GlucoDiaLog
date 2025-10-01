package com.example.glucodialog.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.glucodialog.domain.model.UserProfile
import com.example.glucodialog.ui.components.DropdownSelector
import com.example.glucodialog.ui.components.TimePickerDialog
import com.example.glucodialog.ui.constants.Labels.DIABETES_TYPE_LABELS
import com.example.glucodialog.ui.constants.Labels.GLUCOSE_UNITS_PROFILE
import com.example.glucodialog.ui.constants.Labels.MEDICATION_UNITS

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileFormScreen(
    profile: UserProfile,
    onUpdateProfile: (UserProfile) -> Unit,
    onBack: () -> Unit
) {
    var formData by remember { mutableStateOf(profile) }
    var showTimePicker by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = colorScheme.secondaryContainer),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "👋 Добро пожаловать! Введите информацию о себе для корректной работы приложения.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp),
                color = colorScheme.onSecondaryContainer
            )
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        " Личная информация",
                        style = MaterialTheme.typography.titleMedium,
                        color = colorScheme.primary
                    )
                }

                OutlinedTextField(
                    value = formData.name,
                    onValueChange = { formData = formData.copy(name = it) },
                    label = { Text("Имя") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = formData.email,
                    onValueChange = { input ->
                        val filtered = input.filter {
                            it.isLetterOrDigit() || it in listOf(
                                '@',
                                '.',
                                '-',
                                '_'
                            )
                        }
                        formData = formData.copy(email = filtered)
                        emailError =
                            if (!filtered.contains("@")) "Email должен содержать @" else null
                    },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = emailError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
                )

                emailError?.let {
                    Text(
                        text = it,
                        color = colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                DropdownSelector(
                    label = "Пол",
                    options = mapOf("female" to "Женский", "male" to "Мужской"),
                    selected = formData.gender,
                    onSelect = { formData = formData.copy(gender = it) }
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = if (formData.weight == 0.0) "" else formData.weight.toString(),
                        onValueChange = { newValue ->
                            formData = formData.copy(weight = newValue.toDoubleOrNull() ?: 0.0)
                        },
                        label = { Text("Вес (кг)") },
                        placeholder = { Text("0.0") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    OutlinedTextField(
                        value = if (formData.height == 0.0) "" else formData.height.toString(),
                        onValueChange = { newValue ->
                            formData = formData.copy(height = newValue.toDoubleOrNull() ?: 0.0)
                        },
                        label = { Text("Рост (см)") },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("0.0") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.HealthAndSafety,
                        contentDescription = null,
                        tint = colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        " Медицинская информация",
                        style = MaterialTheme.typography.titleMedium,
                        color = colorScheme.secondary
                    )
                }

                DropdownSelector(
                    label = "Тип диабета",
                    options = DIABETES_TYPE_LABELS,
                    selected = formData.diabetesType,
                    onSelect = { formData = formData.copy(diabetesType = it) }
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = if (formData.targetGlucoseLow == 0.0) "" else formData.targetGlucoseLow.toString(),
                        onValueChange = { newValue ->
                            formData = formData.copy(targetGlucoseLow = newValue.toDoubleOrNull() ?: 0.0)
                        },
                        label = { Text("Глюкоза (мин)") },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("0.0") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    OutlinedTextField(
                        value = if (formData.targetGlucoseHigh == 0.0) "" else formData.targetGlucoseHigh.toString(),
                        onValueChange = { newValue ->
                            formData = formData.copy(targetGlucoseHigh = newValue.toDoubleOrNull() ?: 0.0)
                        },
                        label = { Text("Глюкоза (макс)") },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("0.0") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                DropdownSelector(
                    label = "Единица измерения глюкозы",
                    options = GLUCOSE_UNITS_PROFILE,
                    selected = formData.glucoseUnit,
                    onSelect = { formData = formData.copy(glucoseUnit = it) }
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocalHospital,
                        contentDescription = null,
                        tint = colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        " Инсулинотерапия",
                        style = MaterialTheme.typography.titleMedium,
                        color = colorScheme.tertiary
                    )
                }
                OutlinedTextField(
                    value = formData.bolusInsulin,
                    onValueChange = { formData = formData.copy(bolusInsulin = it) },
                    label = { Text("Болюсный (название)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = if (formData.bolusDose == 0.0) "" else formData.bolusDose.toString(),
                    onValueChange = { newValue ->
                        formData = formData.copy(bolusDose = newValue.toDoubleOrNull() ?: 0.0)
                    },
                    label = { Text("Доза болюсного (ед)") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("0.0") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = formData.basalInsulin,
                    onValueChange = { formData = formData.copy(basalInsulin = it) },
                    label = { Text("Базальный (название)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = if (formData.basalDose == 0.0) "" else formData.basalDose.toString(),
                    onValueChange = { newValue ->
                        formData = formData.copy(basalDose = newValue.toDoubleOrNull() ?: 0.0)
                    },
                    label = { Text("Доза базального (ед)") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("0.0") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Medication,
                        contentDescription = null,
                        tint = colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        " Медикаменты",
                        style = MaterialTheme.typography.titleMedium,
                        color = colorScheme.primary
                    )
                }

                OutlinedTextField(
                    value = formData.medication,
                    onValueChange = { formData = formData.copy(medication = it) },
                    label = { Text("Название") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = if (formData.medicationDose == 0.0) "" else formData.medicationDose.toString(),
                    onValueChange = { newValue ->
                        formData = formData.copy(medicationDose = newValue.toDoubleOrNull() ?: 0.0)
                    },
                    label = { Text("Доза") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("0.0") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )


                DropdownSelector(
                    label = "Единица",
                    options = MEDICATION_UNITS,
                    selected = formData.medicationUnit,
                    onSelect = { formData = formData.copy(medicationUnit = it) }
                )

                Button(onClick = { showTimePicker = true }) {
                    Text(formData.medicationTimeMinutesFromMidnight?.let {
                        "%02d:%02d".format(it / 60, it % 60)
                    } ?: "Выбрать время")
                }
            }
        }

        FilledTonalButton(
            onClick = { onUpdateProfile(formData) },
            enabled = emailError == null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Save,
                    contentDescription = "Сохранить профиль",
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(" Сохранить профиль", style = MaterialTheme.typography.titleLarge)
            }
        }

        if (showTimePicker) {
            val initialHour = (formData.medicationTimeMinutesFromMidnight ?: 8 * 60) / 60
            val initialMinute = (formData.medicationTimeMinutesFromMidnight ?: 8 * 60) % 60

            TimePickerDialog(
                initialHour = initialHour,
                initialMinute = initialMinute,
                onDismiss = { showTimePicker = false },
                onConfirm = { h, m ->
                    formData = formData.copy(medicationTimeMinutesFromMidnight = h * 60 + m)
                    showTimePicker = false
                }
            )
        }
    }
}