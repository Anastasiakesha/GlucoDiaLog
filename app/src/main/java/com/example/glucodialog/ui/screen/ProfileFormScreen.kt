package com.example.glucodialog.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import java.util.Locale
import java.text.SimpleDateFormat
import androidx.compose.ui.unit.dp
import androidx.compose.material3.OutlinedButton
import com.example.glucodialog.domain.model.UserProfile
import com.example.glucodialog.ui.components.DropdownSelector
import com.example.glucodialog.ui.constants.Labels
import com.example.glucodialog.ui.constants.Labels.DIABETES_TYPE_LABELS
import com.example.glucodialog.ui.constants.Labels.GLUCOSE_UNITS_PROFILE
import java.util.Calendar
import java.util.Date

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileFormScreen(
    profile: UserProfile,
    onUpdateProfile: (UserProfile) -> Unit,
    onBack: () -> Unit,
) {
    var formData by remember { mutableStateOf(profile) }
    var emailError by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()
    val colorScheme = MaterialTheme.colorScheme

    var isPregnancyActive by remember {
        mutableStateOf(formData.pregnancyLmpTimestamp != null)
    }

    LaunchedEffect(formData.gender) {
        if (formData.gender != "female") {
            isPregnancyActive = false
            formData = formData.copy(pregnancyLmpTimestamp = null)
        }
    }

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
                    options = Labels.GENDER_LABELS,
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

        if (profile.gender == "female") {
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Активная беременность", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "Включает расчет срока и макросомии плода",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = isPregnancyActive,
                    onCheckedChange = {
                        isPregnancyActive = it
                        if (!it) formData = formData.copy(pregnancyLmpTimestamp = null)
                    }
                )
            }

            if (isPregnancyActive) {
                val context = LocalContext.current
                val sdf = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }

                OutlinedButton(
                    onClick = {
                        val calendar = Calendar.getInstance()
                        formData.pregnancyLmpTimestamp?.let { calendar.timeInMillis = it }

                        android.app.DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                val selectedCal = Calendar.getInstance()
                                selectedCal.set(year, month, day)
                                formData = formData.copy(pregnancyLmpTimestamp = selectedCal.timeInMillis)
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).apply {
                            datePicker.maxDate = System.currentTimeMillis()
                        }.show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    val dateText = formData.pregnancyLmpTimestamp?.let { sdf.format(Date(it)) }
                        ?: "Дата последней менструации (LMP)"
                    Icon(Icons.Default.DateRange, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(dateText)
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

    }
}