package com.example.glucodialog.ui


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.glucodialog.data.FoodEntry
import com.example.glucodialog.data.FoodItem
import kotlinx.coroutines.launch
import java.util.*
import com.example.glucodialog.data.*
import com.example.glucodialog.ui.components.DateTimePickerButton
import kotlinx.coroutines.flow.firstOrNull
import com.example.glucodialog.data.UserProfile
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.Color


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealEntryScreen(
    foodDao: FoodDao,
    onBack: () -> Unit,
    userProfile: UserProfile?,
) {
    val scope = rememberCoroutineScope()

    var foodItems by remember { mutableStateOf<List<FoodItem>>(emptyList()) }
    var selectedFood by remember { mutableStateOf<FoodItem?>(null) }
    var addingNewFood by remember { mutableStateOf(false) }
    var newFoodName by remember { mutableStateOf("") }
    var newCalories by remember { mutableStateOf("") }
    var newProteins by remember { mutableStateOf("") }
    var newFats by remember { mutableStateOf("") }
    var newCarbs by remember { mutableStateOf("") }
    var newUnits by remember { mutableStateOf("") }

    var selectedUnit by remember { mutableStateOf<String?>(null) }
    var quantityText by remember { mutableStateOf("") }
    var calendar by remember { mutableStateOf(Calendar.getInstance()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val carbRatio = remember(userProfile) {
        userProfile?.let { profile ->
            val tdd = profile.basalDose + profile.bolusDose
            if (tdd > 0) 500 / tdd else null
        }
    }
    val targetGlucose = remember(userProfile) { userProfile?.targetGlucoseHigh }
    var currentGlucose by remember { mutableStateOf("") }

    var expandedFood by remember { mutableStateOf(false) }
    var expandedUnit by remember { mutableStateOf(false) }

    val isFormValid by derivedStateOf {
        selectedFood != null &&
                !selectedUnit.isNullOrBlank() &&
                quantityText.toDoubleOrNull()?.let { it > 0 } == true &&
                currentGlucose.toDoubleOrNull() != null
    }

    LaunchedEffect(Unit) {
        foodItems = foodDao.getAllFoodItems().firstOrNull() ?: emptyList()
        if (foodItems.isNotEmpty()) selectedFood = foodItems[0]
    }

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
                Text("🍽 Продукт", style = MaterialTheme.typography.titleLarge)

                if (!addingNewFood) {
                    ExposedDropdownMenuBox(
                        expanded = expandedFood,
                        onExpandedChange = { expandedFood = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedFood?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Название продукта") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFood) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedFood,
                            onDismissRequest = { expandedFood = false }
                        ) {
                            foodItems.forEach { food ->
                                DropdownMenuItem(
                                    text = { Text(food.name) },
                                    onClick = {
                                        selectedFood = food
                                        selectedUnit = food.allowedUnits.split(",").map { it.trim() }.firstOrNull()
                                        quantityText = ""
                                        expandedFood = false
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("➕ Добавить новый продукт") },
                                onClick = {
                                    addingNewFood = true
                                    selectedFood = null
                                    expandedFood = false
                                }
                            )
                        }
                    }
                } else {
                    // Ввод нового продукта
                    OutlinedTextField(
                        value = newFoodName,
                        onValueChange = { newFoodName = it },
                        label = { Text("Название продукта") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newCalories,
                        onValueChange = { newCalories = it },
                        label = { Text("Калории") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newProteins,
                        onValueChange = { newProteins = it },
                        label = { Text("Белки (г)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newFats,
                        onValueChange = { newFats = it },
                        label = { Text("Жиры (г)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newCarbs,
                        onValueChange = { newCarbs = it },
                        label = { Text("Углеводы (г)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newUnits,
                        onValueChange = { newUnits = it },
                        label = { Text("Единицы (через запятую)") },
                        modifier = Modifier.fillMaxWidth()
                    )

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

                            val food = FoodItem(
                                name = newFoodName,
                                calories = calories,
                                proteins = proteins,
                                fats = fats,
                                carbs = carbs,
                                allowedUnits = newUnits
                            )
                            scope.launch {
                                foodDao.insertFoodItem(food)
                                foodItems = foodDao.getAllFoodItems().firstOrNull() ?: emptyList()
                                selectedFood = foodItems.last()
                                selectedUnit = selectedFood?.allowedUnits?.split(",")?.map { it.trim() }?.firstOrNull()
                                addingNewFood = false
                                newFoodName = ""
                                newCalories = ""
                                newProteins = ""
                                newFats = ""
                                newCarbs = ""
                                newUnits = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Сохранить новый продукт", color = Color.White)
                    }
                }
            }
        }

        if (selectedFood != null) {
            val units = selectedFood!!.allowedUnits.split(",").map { it.trim() }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ExposedDropdownMenuBox(
                        expanded = expandedUnit,
                        onExpandedChange = { expandedUnit = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedUnit ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Единица") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUnit) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedUnit,
                            onDismissRequest = { expandedUnit = false }
                        ) {
                            units.forEach { unit ->
                                DropdownMenuItem(
                                    text = { Text(unit) },
                                    onClick = {
                                        selectedUnit = unit
                                        expandedUnit = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it },
                        label = { Text("Количество") },
                        placeholder = { Text("100 ${selectedUnit ?: ""}") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        val quantity = quantityText.toDoubleOrNull() ?: 0.0
        val factor = quantity / 100.0
        val carbs = (selectedFood?.carbs ?: 0.0) * factor
        val proteins = (selectedFood?.proteins ?: 0.0) * factor
        val fats = (selectedFood?.fats ?: 0.0) * factor
        val calories = (selectedFood?.calories ?: 0) * factor

        if (selectedFood != null && quantity > 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7E6))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Калории: ${"%.1f".format(calories)}")
                    Text("Белки: ${"%.1f".format(proteins)} г")
                    Text("Жиры: ${"%.1f".format(fats)} г")
                    Text("Углеводы: ${"%.1f".format(carbs)} г")
                }
            }
        }

        OutlinedTextField(
            value = currentGlucose,
            onValueChange = { currentGlucose = it },
            label = { Text("Текущая глюкоза (ммоль/л)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        val bolusDose: Double? = run {
            val icr = carbRatio
            val target = targetGlucose
            val current = currentGlucose.toDoubleOrNull()
            if (icr == null || icr <= 0.0 || current == null) null
            else {
                val mealInsulin = if (carbs > 0.0) carbs / icr else 0.0
                val correction = if (target != null) (current - target) / 2.0 else 0.0
                (mealInsulin + correction).coerceAtLeast(0.0)
            }
        }

        bolusDose?.let {
            Text("Рассчитанная доза: ${"%.1f".format(it)} ЕД")
        } ?: Text("Недостаточно данных для расчёта дозы")

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7E6))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val dateFormat = remember { java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }
                Text("⏰ Дата и время приема: ${dateFormat.format(calendar.time)}")
                DateTimePickerButton(
                    calendar = calendar,
                    onDateTimeSelected = { updatedCalendar ->
                        calendar = updatedCalendar
                    }
                )
            }
        }

        Button(
            onClick = {
                val quantityVal = quantityText.toDoubleOrNull()
                val glucoseVal = currentGlucose.toDoubleOrNull()
                if (selectedFood == null || selectedUnit.isNullOrBlank() || quantityVal == null || quantityVal <= 0 || glucoseVal == null) {
                    errorMessage = "Заполните все поля корректно"
                    return@Button
                }

                val entry = FoodEntry(
                    foodItemId = selectedFood!!.id,
                    quantity = quantityVal,
                    unit = selectedUnit!!,
                    timestamp = calendar.timeInMillis
                )

                scope.launch {
                    foodDao.insertFoodEntry(entry)
                    onBack()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0288D1),
            ),
            shape = RoundedCornerShape(12.dp),
            enabled = isFormValid
        ) {
            Text("💾 Добавить запись", color = Color.White)
        }

        errorMessage?.let { Text(it, color = Color.Red) }
    }
}