package com.example.glucodialog.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.glucodialog.data.MedicationEntry
import com.example.glucodialog.data.MedicationType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MedicationCard(record: MedicationEntry, medicationTypes: List<MedicationType>) {
    val medName = medicationTypes.find { it.id == record.medicationTypeId }?.name ?: "Неизвестно"
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Препарат: $medName, ${record.dose} ${record.unit}", color = Color.Black)
        Text("Дата: ${dateFormat.format(Date(record.timestamp))}", color = Color.Black)
    }
}