package com.example.glucodialog.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.glucodialog.domain.model.InsulinEntry
import com.example.glucodialog.domain.model.InsulinEntryWithTypeDomain
import com.example.glucodialog.domain.model.InsulinType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun InsulinCard(
    record: InsulinEntryWithTypeDomain,
    onDelete: (InsulinEntry) -> Unit,
    valueColor: Color = Color.Black
) {
    val insulinName = record.type?.name?: "Неизвестно"
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFDCEDC8)
        ),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = insulinName,
                    style = MaterialTheme.typography.titleMedium,
                    color = valueColor
                )
                Text(
                    text = "Доза: ${record.entry.doseUnits} ${record.entry.unit}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Дата: ${dateFormat.format(Date(record.entry.timestamp))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = { showDialog = true },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Удаление инсулина") },
            text = { Text("Вы уверены, что хотите удалить запись \"$insulinName\"?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(record.entry)
                    showDialog = false
                }) {
                    Text("Удалить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}