package com.example.glucodialog.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessAlarms
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.glucodialog.data.relations.TherapyPlanWithDetails
import com.example.glucodialog.domain.model.FoodEntry
import com.example.glucodialog.domain.model.FoodType
import com.example.glucodialog.domain.model.UserProfile
import com.example.glucodialog.ui.components.DateTimePickerButton
import com.example.glucodialog.ui.viewmodel.FoodViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealEntryScreen(
    viewModel: FoodViewModel,
    onBack: () -> Unit,
    userProfile: UserProfile?,
    activePlan: TherapyPlanWithDetails?,
    entryId: Int = -1
) {
    val scope = rememberCoroutineScope()
    val foodTypes by viewModel.foodTypes.collectAsState()

    var selectedFood by remember { mutableStateOf<FoodType?>(null) }
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
    var currentGlucose by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val expandedFood = remember { mutableStateOf(false) }
    val expandedUnit = remember { mutableStateOf(false) }

    val carbRatio = remember(activePlan) {
        val totalBolus = activePlan?.insulinPlans?.filter { it.type?.type == "Болюсный" }?.sumOf { it.plan.dose } ?: 0.0
        val totalBasal = activePlan?.insulinPlans?.filter { it.type?.type == "Базальный" }?.sumOf { it.plan.dose } ?: 0.0
        val tdd = totalBolus + totalBasal
        if (tdd > 0) 500 / tdd else null
    }

    val isf = remember(activePlan) {
        val totalBolus = activePlan?.insulinPlans?.filter { it.type?.type == "Болюсный" }?.sumOf { it.plan.dose } ?: 0.0
        val totalBasal = activePlan?.insulinPlans?.filter { it.type?.type == "Базальный" }?.sumOf { it.plan.dose } ?: 0.0
        val tdd = totalBolus + totalBasal
        if (tdd > 0) 100 / tdd else null
    }

    val quantityMeal = quantityText.toDoubleOrNull() ?: 0.0

    val bolusDose: Double? = run {
        val icr = carbRatio
        val target = userProfile?.targetGlucoseHigh
        val current = currentGlucose.toDoubleOrNull()

        if (icr == null || icr <= 0.0 || current == null) null
        else {
            val mealInsulin = if (quantityMeal > 0.0) ((selectedFood?.carbs ?: 0.0) * (quantityMeal / 100.0)) / icr else 0.0

            val sensitivity = isf ?: 2.0
            val correction = if (target != null && current > target) (current - target) / sensitivity else 0.0

            (mealInsulin + correction).coerceAtLeast(0.0)
        }
    }
    val isFormValid by derivedStateOf {
        selectedFood != null &&
                !selectedUnit.isNullOrBlank() &&
                quantityText.toDoubleOrNull()?.let { it > 0 } == true &&
                (entryId != -1 || currentGlucose.toDoubleOrNull() != null)
    }

    LaunchedEffect(entryId, foodTypes) {
        if (foodTypes.isNotEmpty()) {
            if (entryId != -1) {
                val entryToEdit = viewModel.getEntryById(entryId)
                entryToEdit?.let { entry ->
                    selectedFood = foodTypes.find { it.id == entry.foodTypeId }
                    selectedUnit = entry.unit
                    quantityText = entry.quantity.toString()
                    calendar = Calendar.getInstance().apply { timeInMillis = entry.timestamp }
                }
            } else if (selectedFood == null) {
                selectedFood = foodTypes.first()
                selectedUnit = selectedFood?.allowedUnits?.split(",")?.map { it.trim() }?.firstOrNull()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(
                imageVector = Icons.Filled.Restaurant,
                contentDescription = "Продукт",
                tint = MaterialTheme.colorScheme.primary
            )
            Text(" Добавление продукта", style = MaterialTheme.typography.titleLarge)
        }


        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (!addingNewFood) {
                    ExposedDropdownMenuBox(expanded = expandedFood.value, onExpandedChange = { expandedFood.value = it }) {
                        OutlinedTextField(
                            value = selectedFood?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Название продукта") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFood.value) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(expanded = expandedFood.value, onDismissRequest = { expandedFood.value = false }) {
                            foodTypes.forEach { food ->
                                DropdownMenuItem(
                                    text = { Text(food.name) },
                                    onClick = {
                                        selectedFood = food
                                        selectedUnit = food.allowedUnits.split(",").map { it.trim() }.firstOrNull()
                                        quantityText = ""
                                        expandedFood.value = false
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("➕ Добавить новый продукт") },
                                onClick = {
                                    addingNewFood = true
                                    selectedFood = null
                                    expandedFood.value = false
                                }
                            )
                        }
                    }
                } else {
                    OutlinedTextField(newFoodName, { newFoodName = it }, label = { Text("Название продукта") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(newCalories, { newCalories = it }, label = { Text("Калории") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(newProteins, { newProteins = it }, label = { Text("Белки (г)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(newFats, { newFats = it }, label = { Text("Жиры (г)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(newCarbs, { newCarbs = it }, label = { Text("Углеводы (г)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(newUnits, { newUnits = it }, label = { Text("Единицы (через запятую)") }, modifier = Modifier.fillMaxWidth())

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
                            scope.launch {
                                viewModel.addFoodType(food)
                                addingNewFood = false
                                newFoodName = ""; newCalories = ""; newProteins = ""; newFats = ""; newCarbs = ""; newUnits = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) { Text("Сохранить новый продукт", color = Color.White) }
                }
            }
        }

        selectedFood?.let { food ->
            val units = food.allowedUnits.split(",").map { it.trim() }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ExposedDropdownMenuBox(expanded = expandedUnit.value, onExpandedChange = { expandedUnit.value = it }) {
                        OutlinedTextField(
                            value = selectedUnit ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Единица") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUnit.value) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(expanded = expandedUnit.value, onDismissRequest = { expandedUnit.value = false }) {
                            units.forEach { unit ->
                                DropdownMenuItem(text = { Text(unit) }, onClick = {
                                    selectedUnit = unit
                                    expandedUnit.value = false
                                })
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
        if (selectedFood != null && quantity > 0) {
            val carbs = (selectedFood?.carbs ?: 0.0) * factor
            val proteins = (selectedFood?.proteins ?: 0.0) * factor
            val fats = (selectedFood?.fats ?: 0.0) * factor
            val calories = (selectedFood?.calories ?: 0) * factor
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
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
            val target = userProfile?.targetGlucoseHigh
            val current = currentGlucose.toDoubleOrNull()
            if (icr == null || icr <= 0.0 || current == null) null
            else {
                val mealInsulin = if (quantity > 0.0) (selectedFood?.carbs ?: 0.0) * (quantity/100.0) / icr else 0.0
                val correction = if (target != null) (current - target) / 2.0 else 0.0
                (mealInsulin + correction).coerceAtLeast(0.0)
            }
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(bolusDose?.let { "Рассчитанная доза: ${"%.1f".format(it)} ЕД (болюсного)" } ?: "Недостаточно данных для расчёта дозы")
            }
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
                DateTimePickerButton(calendar = calendar, onDateTimeSelected = { calendar = it })
            }
        }

        Button(
            onClick = {
                val quantityVal = quantityText.toDoubleOrNull()
                val glucoseVal = currentGlucose.toDoubleOrNull()
                if (selectedFood == null || selectedUnit.isNullOrBlank() || quantityVal == null || quantityVal <= 0 || (entryId == -1 && glucoseVal == null)) {
                    errorMessage = "Заполните все поля корректно"
                    return@Button
                }
                val entry = FoodEntry(
                    id = if (entryId != -1) entryId else 0,
                    foodTypeId = selectedFood!!.id,
                    quantity = quantityVal,
                    unit = selectedUnit!!,
                    timestamp = calendar.timeInMillis
                )
                scope.launch {
                    if (entryId != -1) {
                        viewModel.updateEntry(entry)
                    } else {
                        viewModel.addFoodEntry(entry)
                    }
                    onBack()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            enabled = isFormValid
        ) {
            Icon(
            imageVector = Icons.Filled.Save,
            contentDescription = "Сохранить запись",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
            Spacer(Modifier.width(8.dp))
            Text(if (entryId != -1) "Сохранить изменения" else "Добавить запись", color = Color.White)
        }

        errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}