package com.example.glucodialog.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessAlarms
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.glucodialog.ui.components.DateTimePickerButton
import java.util.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import com.example.glucodialog.domain.model.ActivityEntry
import com.example.glucodialog.domain.model.ActivityType
import com.example.glucodialog.ui.viewmodel.ActivityEntryViewModel
import java.text.SimpleDateFormat
import androidx.compose.ui.text.input.KeyboardType
import com.example.glucodialog.domain.model.MedicationType
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityEntryScreen(
    viewModel: ActivityEntryViewModel,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val activityTypes by viewModel.activityTypes.collectAsState()
    var typeTouched by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf<ActivityType?>(activityTypes.firstOrNull()) }
    var durationMinutes by remember { mutableStateOf("") }
    var calendar by remember { mutableStateOf(Calendar.getInstance()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var addingNewType by remember { mutableStateOf(false) }
    var newTypeName by remember { mutableStateOf("") }
    var expandedType by remember { mutableStateOf(false) }
    var attemptedSave by remember { mutableStateOf(false) }

    val canSave = selectedType != null && durationMinutes.toIntOrNull()?.let { it > 0 } == true
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
                imageVector = Icons.Filled.DirectionsRun,
                contentDescription = "Активность",
                tint = MaterialTheme.colorScheme.primary
            )
            Text("Ввод активности", style = MaterialTheme.typography.titleLarge)
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

                if (!addingNewType) {
                    ExposedDropdownMenuBox(
                        expanded = expandedType,
                        onExpandedChange = { expandedType = !expandedType },
                        modifier = Modifier.fillMaxWidth()
                            .onFocusChanged { if (!it.isFocused) typeTouched = true }
                    ) {
                        OutlinedTextField(
                            value = selectedType?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Тип активности") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = expandedType,
                            onDismissRequest = { expandedType = false },
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
                                errorMessage = "Введите название физической активности"
                                return@Button
                            }
                            scope.launch {
                                viewModel.addActivityType(
                                    ActivityType(name = newTypeName)
                                ) { insertedId ->

                                    selectedType = ActivityType(
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
            OutlinedTextField(
                value = durationMinutes,
                onValueChange = { durationMinutes = it; errorMessage = null },
                label = { Text("Длительность (минуты)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
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
                DateTimePickerButton(
                    calendar = calendar,
                    onDateTimeSelected = { calendar = it }
                )
            }
        }

        errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Button(
            onClick = {
                val minutes = durationMinutes.toIntOrNull()
                if (selectedType == null || minutes == null || minutes <= 0) {
                    errorMessage = "Заполните все поля корректно"
                    return@Button
                }
                val entry = ActivityEntry(
                    activityTypeId = selectedType!!.id,
                    durationMinutes = minutes,
                    timestamp = calendar.timeInMillis
                )
                viewModel.addActivityEntry(entry)
                onBack()
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