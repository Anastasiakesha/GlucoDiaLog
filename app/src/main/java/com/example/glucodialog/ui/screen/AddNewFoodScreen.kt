package com.example.glucodialog.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.glucodialog.domain.model.FoodType

@Composable
fun AddNewFoodScreen(
    initialName: String,
    onBack: () -> Unit,
    onSave: (FoodType) -> Unit
) {
    var newFoodName by remember { mutableStateOf(initialName) }
    var newCalories by remember { mutableStateOf("") }
    var newProteins by remember { mutableStateOf("") }
    var newFats by remember { mutableStateOf("") }
    var newCarbs by remember { mutableStateOf("") }
    var newUnits by remember { mutableStateOf("г,шт") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
            }
            Text("Новый продукт", style = MaterialTheme.typography.titleLarge)
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(newFoodName, { newFoodName = it }, label = { Text("Название продукта") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(newCalories, { newCalories = it }, label = { Text("Калории (на 100г/шт)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(newProteins, { newProteins = it }, label = { Text("Белки (г)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(newFats, { newFats = it }, label = { Text("Жиры (г)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(newCarbs, { newCarbs = it }, label = { Text("Углеводы (г)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(newUnits, { newUnits = it }, label = { Text("Единицы (через запятую, напр: г,шт)") }, modifier = Modifier.fillMaxWidth())

                Button(
                    onClick = {
                        val calories = newCalories.toIntOrNull()
                        val proteins = newProteins.toDoubleOrNull()
                        val fats = newFats.toDoubleOrNull()
                        val carbs = newCarbs.toDoubleOrNull()
                        if (newFoodName.isBlank() || calories == null || proteins == null || fats == null || carbs == null || newUnits.isBlank()) {
                            errorMessage = "Введите корректные данные нового продукта"
                            return@Button
                        }
                        val food = FoodType(
                            name = newFoodName,
                            calories = calories,
                            proteins = proteins,
                            fats = fats,
                            carbs = carbs,
                            allowedUnits = newUnits
                        )
                        onSave(food)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) { Text("Сохранить", color = Color.White) }

                errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        }
    }
}