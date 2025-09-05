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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityEntryScreen(
    activityDao: ActivityDao,
    onBack: () -> Unit,
    userProfile: UserProfile?,
) {
    val scope = rememberCoroutineScope()

    var activityTypes by remember { mutableStateOf<List<ActivityType>>(emptyList()) }
    var selectedType by remember { mutableStateOf<ActivityType?>(null) }
    var addingNewType by remember { mutableStateOf(false) }
    var newTypeName by remember { mutableStateOf("") }

    var durationMinutes by remember { mutableStateOf("") }
    var durationTouched by remember { mutableStateOf(false) }
    var typeTouched by remember { mutableStateOf(false) }
    var attemptedSave by remember { mutableStateOf(false) }

    var calendar by remember { mutableStateOf(Calendar.getInstance()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var expandedType by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        activityTypes = activityDao.getAllActivityTypes().firstOrNull() ?: emptyList()
        if (activityTypes.isNotEmpty()) selectedType = activityTypes[0]
    }

    val durationValue = durationMinutes.toIntOrNull()
    val canSave = selectedType != null && durationValue != null && durationValue > 0

    val showTypeError = (typeTouched || attemptedSave) && selectedType == null && !addingNewType
    val showDurationError = (durationTouched || attemptedSave) && durationMinutes.isNotBlank() && (durationValue == null || durationValue <= 0)

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
                Text("🏃 Тип активности", style = MaterialTheme.typography.titleLarge)

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
                            label = { Text("Название активности") },
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
                            activityTypes.forEach { type ->
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
                        Text("Выберите тип активности", color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    }
                } else {
                    OutlinedTextField(
                        value = newTypeName,
                        onValueChange = { newTypeName = it },
                        label = { Text("Название новой активности") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            if (newTypeName.isBlank()) {
                                errorMessage = "Введите название новой активности"
                                return@Button
                            }
                            val type = ActivityType(name = newTypeName)
                            scope.launch {
                                activityDao.insertActivityType(type)
                                activityTypes = activityDao.getAllActivityTypes().firstOrNull() ?: emptyList()
                                selectedType = activityTypes.last()
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
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = durationMinutes,
                    onValueChange = { value ->
                        durationMinutes = value
                        errorMessage = null
                    },
                    label = { Text("Длительность (минуты)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = showDurationError,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { state ->
                            if (!state.isFocused) durationTouched = true
                        }
                )
                if (showDurationError) {
                    Text("Введите корректную длительность", color = Color.Red, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7E6))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }
                Text("⏰ Дата и время активности: ${dateFormat.format(calendar.time)}")
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
                val minutes = durationMinutes.toIntOrNull()
                if (!canSave) {
                    errorMessage = "Заполните все поля корректно"
                    durationTouched = true
                    typeTouched = true
                    return@Button
                }
                val entry = ActivityEntry(
                    activityTypeId = selectedType!!.id,
                    durationMinutes = minutes!!,
                    timestamp = calendar.timeInMillis
                )
                scope.launch {
                    activityDao.insertActivityEntry(entry)
                    onBack()
                }
                durationMinutes = ""
                durationTouched = false
                typeTouched = false
                attemptedSave = false
                errorMessage = null
            },
            enabled = canSave,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (canSave) Color(0xFF0288D1) else Color.Gray
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("💾 Добавить запись", color = Color.White)
        }
    }
}