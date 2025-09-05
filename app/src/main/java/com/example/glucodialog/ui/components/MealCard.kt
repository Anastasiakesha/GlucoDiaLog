package com.example.glucodialog.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.glucodialog.data.FoodEntry
import com.example.glucodialog.data.FoodItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MealCard(record: FoodEntry, foodItems: List<FoodItem>) {
    val foodName = foodItems.find { it.id == record.foodItemId }?.name ?: "Неизвестно"
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Продукт: $foodName, Количество: ${record.quantity} ${record.unit}", color = Color.Black)
        Text("Дата: ${dateFormat.format(Date(record.timestamp))}", color = Color.Black)
    }
}