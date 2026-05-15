package com.example.glucodialog.ui.screen


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import com.example.glucodialog.domain.model.BloodPressureEntry
import com.example.glucodialog.ui.components.DateTimePickerButton
import com.example.glucodialog.ui.viewmodel.BloodPressureViewModel
import java.util.Calendar

@Composable
fun BloodPressureEntryScreen(
    viewModel: BloodPressureViewModel,
    entryId: Int = -1,
    onBack: () -> Unit
) {
    var systolic by remember { mutableStateOf("") }
    var diastolic by remember { mutableStateOf("") }
    var pulse by remember { mutableStateOf("") }
    var calendar by remember { mutableStateOf(Calendar.getInstance()) }

    val sysVal = systolic.toIntOrNull()
    val diaVal = diastolic.toIntOrNull()
    val pulseVal = pulse.toIntOrNull()

    val canSave = sysVal != null && sysVal in 50..250 &&
            diaVal != null && diaVal in 30..150 &&
            pulseVal != null && pulseVal in 30..200

    LaunchedEffect(entryId) {
        if (entryId != -1) {
            val entryToEdit = viewModel.getEntryById(entryId)
            entryToEdit?.let { entry ->
                systolic = entry.systolic.toString()
                diastolic = entry.diastolic.toString()
                pulse = entry.pulse.toString()
                calendar = Calendar.getInstance().apply { timeInMillis = entry.timestamp }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.FavoriteBorder, contentDescription = "Давление", tint = MaterialTheme.colorScheme.primary)
            Text("Ввод давления", style = MaterialTheme.typography.titleLarge)
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = systolic, onValueChange = { systolic = it },
                        label = { Text("Систол. (верх)") }, placeholder = { Text("120") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f), singleLine = true
                    )
                    OutlinedTextField(
                        value = diastolic, onValueChange = { diastolic = it },
                        label = { Text("Диастол. (ниж)") }, placeholder = { Text("80") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f), singleLine = true
                    )
                }
                OutlinedTextField(
                    value = pulse, onValueChange = { pulse = it },
                    label = { Text("Пульс (ЧСС)") }, placeholder = { Text("70") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                DateTimePickerButton(calendar = calendar, onDateTimeSelected = { calendar = it })
            }
        }

        Button(
            onClick = {
                if (canSave) {
                    val entry = BloodPressureEntry(
                        id = if (entryId != -1) entryId else 0,
                        systolic = sysVal!!,
                        diastolic = diaVal!!,
                        pulse = pulseVal!!,
                        timestamp = calendar.timeInMillis
                    )
                    if (entryId != -1) {
                        viewModel.updateBloodPressureEntry(entry, onComplete = onBack)
                    } else {
                        viewModel.addBloodPressureEntry(entry, onComplete = onBack)
                    }
                }
            },
            enabled = canSave,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Save, contentDescription = "Сохранить", tint = Color.White)
            Spacer(Modifier.width(8.dp))
            Text(if (entryId != -1) "Сохранить изменения" else "Добавить запись", color = Color.White)
        }
    }
}