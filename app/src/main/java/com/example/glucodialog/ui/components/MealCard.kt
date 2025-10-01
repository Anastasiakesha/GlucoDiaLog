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
import com.example.glucodialog.domain.model.FoodEntry
import com.example.glucodialog.domain.model.FoodType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MealCard(
    record: FoodEntry,
    foodItems: List<FoodType>,
    onDelete: (FoodEntry) -> Unit,
    valueColor: Color = Color.Black
) {
    val food = foodItems.find { it.id == record.foodTypeId }
    val foodName = food?.name ?: "Неизвестно"
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

    val multiplier = when (food?.allowedUnits) {
        "г", "мл" -> record.quantity / 100.0
        "шт" -> record.quantity
        else -> 0.0
    }

    val calories = (food?.calories ?: 0) * multiplier
    val proteins = (food?.proteins ?: 0.0) * multiplier
    val fats = (food?.fats ?: 0.0) * multiplier
    val carbs = (food?.carbs ?: 0.0) * multiplier

    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFFDE7)
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
                    text = foodName,
                    style = MaterialTheme.typography.titleMedium,
                    color = valueColor
                )
                Text(
                    text = "Количество: ${record.quantity} ${record.unit}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Дата: ${dateFormat.format(Date(record.timestamp))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (food != null) {
                    Text(
                        text = "КБЖУ: ${"%.1f".format(calories)} ккал, Б: ${"%.1f".format(proteins)} г, Ж: ${"%.1f".format(fats)} г, У: ${"%.1f".format(carbs)} г",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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
            title = { Text("Удаление продукта") },
            text = { Text("Вы уверены, что хотите удалить \"$foodName\"?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(record)
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