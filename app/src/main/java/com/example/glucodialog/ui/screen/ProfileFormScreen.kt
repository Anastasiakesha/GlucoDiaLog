package com.example.glucodialog.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.glucodialog.data.local.UserProfile
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

    fun handleChange(field: String, value: String) {
        formData = when (field) {
            "email" -> formData.copy(email = value)
            "name" -> formData.copy(name = value)
            "gender" -> formData.copy(gender = value)
            "height" -> formData.copy(height = value.toDoubleOrNull() ?: 0.0)
            "weight" -> formData.copy(weight = value.toDoubleOrNull() ?: 0.0)
            "diabetesType" -> formData.copy(diabetesType = value)
            "targetGlucoseLow" -> formData.copy(targetGlucoseLow = value.toDoubleOrNull() ?: 0.0)
            "targetGlucoseHigh" -> formData.copy(targetGlucoseHigh = value.toDoubleOrNull() ?: 0.0)
            "glucoseUnit" -> formData.copy(glucoseUnit = value)
            "bolusInsulin" -> formData.copy(bolusInsulin = value)
            "bolusDose" -> formData.copy(bolusDose = value.toDoubleOrNull() ?: 0.0)
            "basalInsulin" -> formData.copy(basalInsulin = value)
            "basalDose" -> formData.copy(basalDose = value.toDoubleOrNull() ?: 0.0)
            "medication" -> formData.copy(medication = value)
            "medicationDose" -> formData.copy(medicationDose = value.toDoubleOrNull() ?: 0.0)
            "medicationUnit" -> formData.copy(medicationUnit = value)
            else -> formData
        }
    }

    var weightText by remember { mutableStateOf(formData.weight.toString()) }
    var heightText by remember { mutableStateOf(formData.height.toString()) }
    var targetGlucoseLowText by remember { mutableStateOf(formData.targetGlucoseLow.toString()) }
    var targetGlucoseHighText by remember { mutableStateOf(formData.targetGlucoseHigh.toString()) }
    var bolusDoseText by remember { mutableStateOf(formData.bolusDose.toString()) }
    var basalDoseText by remember { mutableStateOf(formData.basalDose.toString()) }
    var medicationDoseText by remember { mutableStateOf(formData.medicationDose.toString()) }

    val isFormValid by derivedStateOf {
        emailError == null &&
                formData.email.isNotBlank() &&
                formData.name.isNotBlank() &&
                formData.gender.isNotBlank() &&
                formData.weight > 0 &&
                formData.height > 0 &&
                formData.diabetesType.isNotBlank() &&
                formData.targetGlucoseLow > 0 &&
                formData.targetGlucoseHigh > 0 &&
                formData.glucoseUnit.isNotBlank() &&
                formData.bolusInsulin.isNotBlank() &&
                formData.bolusDose > 0 &&
                formData.basalInsulin.isNotBlank() &&
                formData.basalDose > 0 &&
                formData.medication.isNotBlank() &&
                formData.medicationDose > 0 &&
                formData.medicationUnit.isNotBlank() &&
                formData.medicationTimeMinutesFromMidnight != null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {


        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "👋 Добро пожаловать! Пожалуйста, введите информацию о себе для дальнейшей работы приложения.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }


        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("👤 Личная информация", style = MaterialTheme.typography.titleMedium)

                OutlinedTextField(
                    value = formData.name,
                    onValueChange = { handleChange("name", it) },
                    label = { Text("Имя") },
                    modifier = Modifier.fillMaxWidth()
                )


                OutlinedTextField(
                    value = formData.email,
                    onValueChange = { input ->

                        val filtered = input.filter { it.isLetterOrDigit() || it in listOf('@', '.', '-', '_') }
                        handleChange("email", filtered)

                        emailError = when {
                            !filtered.contains("@") -> "Email должен содержать символ @"
                            else -> null
                        }
                    },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = emailError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
                )

                emailError?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                DropdownSelector(
                    label = "Пол",
                    options = mapOf("female" to "Женский", "male" to "Мужской"),
                    selected = formData.gender,
                    onSelect = { handleChange("gender", it) }
                )

                OutlinedTextField(
                    value = weightText,
                    onValueChange = { input ->
                        if (input.matches(Regex("^\\d*\\.?\\d*\$"))) {
                            weightText = input
                            formData = formData.copy(weight = input.toDoubleOrNull() ?: 0.0)
                        }
                    },
                    label = { Text("Вес (кг)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused && weightText == "0.0") weightText = ""
                        }
                )

                OutlinedTextField(
                    value = heightText,
                    onValueChange = { input ->
                        if (input.matches(Regex("^\\d*\\.?\\d*\$"))) {
                            heightText = input
                            formData = formData.copy(height = input.toDoubleOrNull() ?: 0.0)
                        }
                    },
                    label = { Text("Рост (см)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused && heightText == "0.0") heightText = ""
                        }
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("🏥 Медицинская информация", style = MaterialTheme.typography.titleMedium)

                DropdownSelector(
                    label = "Тип диабета",
                    options = DIABETES_TYPE_LABELS,
                    selected = formData.diabetesType,
                    onSelect = { handleChange("diabetesType", it) }
                )

                OutlinedTextField(
                    value = targetGlucoseLowText,
                    onValueChange = { input ->
                        if (input.matches(Regex("^\\d*\\.?\\d*\$"))) {
                            targetGlucoseLowText = input
                            formData = formData.copy(targetGlucoseLow = input.toDoubleOrNull() ?: 0.0)
                        }
                    },
                    label = { Text("Целевой диапазон глюкозы (мин)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused && targetGlucoseLowText == "0.0") targetGlucoseLowText = ""
                        }
                )

                OutlinedTextField(
                    value = targetGlucoseHighText,
                    onValueChange = { input ->
                        if (input.matches(Regex("^\\d*\\.?\\d*\$"))) {
                            targetGlucoseHighText = input
                            formData = formData.copy(targetGlucoseHigh = input.toDoubleOrNull() ?: 0.0)
                        }
                    },
                    label = { Text("Целевой диапазон глюкозы (макс)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused && targetGlucoseHighText == "0.0") targetGlucoseHighText = ""
                        }
                )

                DropdownSelector(
                    label = "Единица измерения глюкозы",
                    options = GLUCOSE_UNITS_PROFILE,
                    selected = formData.glucoseUnit,
                    onSelect = { handleChange("glucoseUnit", it) }
                )
            }
        }


        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("💉 Инсулинотерапия", style = MaterialTheme.typography.titleMedium)

                OutlinedTextField(
                    value = formData.bolusInsulin,
                    onValueChange = { handleChange("bolusInsulin", it) },
                    label = { Text("Болюсный (название)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = bolusDoseText,
                    onValueChange = { input ->
                        if (input.matches(Regex("^\\d*\\.?\\d*\$"))) {
                            bolusDoseText = input
                            formData = formData.copy(bolusDose = input.toDoubleOrNull() ?: 0.0)
                        }
                    },
                    label = { Text("Доза болюсного (ед) в сутки") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused && bolusDoseText == "0.0") bolusDoseText = ""
                        }
                )

                OutlinedTextField(
                    value = formData.basalInsulin,
                    onValueChange = { handleChange("basalInsulin", it) },
                    label = { Text("Базальный (название)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = basalDoseText,
                    onValueChange = { input ->
                        if (input.matches(Regex("^\\d*\\.?\\d*\$"))) {
                            basalDoseText = input
                            formData = formData.copy(basalDose = input.toDoubleOrNull() ?: 0.0)
                        }
                    },
                    label = { Text("Доза базального (ед) в сутки") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused && basalDoseText == "0.0") basalDoseText = ""
                        }
                )
            }
        }


        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("💊 Медикаменты", style = MaterialTheme.typography.titleMedium)

                OutlinedTextField(
                    value = formData.medication,
                    onValueChange = { handleChange("medication", it) },
                    label = { Text("Лекарственный препарат (название)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = medicationDoseText,
                    onValueChange = { input ->
                        if (input.matches(Regex("^\\d*\\.?\\d*\$"))) {
                            medicationDoseText = input
                            formData = formData.copy(medicationDose = input.toDoubleOrNull() ?: 0.0)
                        }
                    },
                    label = { Text("Доза медикаментов") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused && medicationDoseText == "0.0") medicationDoseText = ""
                        }
                )

                DropdownSelector(
                    label = "Единица",
                    options = MEDICATION_UNITS,
                    selected = formData.medicationUnit,
                    onSelect = { handleChange("medicationUnit", it) }
                )

                Column {
                    Text("⏰ Время приема медикаментов", style = MaterialTheme.typography.bodyMedium)
                    val timeText = formData.medicationTimeMinutesFromMidnight?.let {
                        val hours = it / 60
                        val mins = it % 60
                        "%02d:%02d".format(hours, mins)
                    } ?: "Выбрать время"

                    Button(onClick = { showTimePicker = true }) {
                        Text(timeText)
                    }
                }
            }
        }

        Button(
            onClick = { onUpdateProfile(formData) },
            modifier = Modifier.fillMaxWidth(),
            enabled = isFormValid
        ) {
            Text("💾 Сохранить профиль")
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