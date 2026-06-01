package com.example.glucodialog.ui.screen

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessAlarms
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.glucodialog.data.relations.TherapyPlanWithDetails
import com.example.glucodialog.domain.model.FoodEntry
import com.example.glucodialog.domain.model.FoodType
import com.example.glucodialog.domain.model.FoodEntryWithTypeDomain
import com.example.glucodialog.domain.model.UserProfile
import com.example.glucodialog.ui.components.DateTimePickerButton
import com.example.glucodialog.ui.viewmodel.FoodViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class MealScreenState { MAIN, SEARCH, ADD_NEW }

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
    val context = LocalContext.current
    val foodTypes by viewModel.foodTypes.collectAsState()

    // Состояние текущего отображаемого экрана
    var screenState by remember { mutableStateOf(MealScreenState.MAIN) }

    // Список добавленных продуктов в текущий прием пищи
    val mealItems = remember { mutableStateListOf<FoodEntryWithTypeDomain>() }

    var searchQuery by remember { mutableStateOf("") }
    var selectedFood by remember { mutableStateOf<FoodType?>(null) }
    var selectedUnit by remember { mutableStateOf<String?>(null) }
    var quantityText by remember { mutableStateOf("") }
    var calendar by remember { mutableStateOf(Calendar.getInstance()) }
    var currentGlucose by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var expandedUnit by remember { mutableStateOf(false) }
    var hasLoadedEdit by remember { mutableStateOf(false) }

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

    val currentValidQuantity = quantityText.toDoubleOrNull() ?: 0.0
    val currentValidItem = if (selectedFood != null && currentValidQuantity > 0 && selectedUnit != null) {
        FoodEntryWithTypeDomain(
            entry = FoodEntry(
                id = if (entryId != -1 && mealItems.isEmpty()) entryId else 0,
                foodTypeId = selectedFood!!.id,
                quantity = currentValidQuantity,
                unit = selectedUnit!!,
                timestamp = 0L
            ),
            type = selectedFood
        )
    } else null

    val allItems = mealItems + listOfNotNull(currentValidItem)

    val isFormValid by derivedStateOf {
        val hasItems = allItems.isNotEmpty()
        val hasGlucoseIfNeeded = entryId != -1 || currentGlucose.toDoubleOrNull() != null
        hasItems && hasGlucoseIfNeeded
    }

    LaunchedEffect(entryId, foodTypes) {
        if (foodTypes.isNotEmpty() && entryId != -1 && !hasLoadedEdit) {
            val entryToEdit = viewModel.getEntryById(entryId)
            entryToEdit?.let { entry ->
                val food = foodTypes.find { it.id == entry.foodTypeId }
                if (food != null) {
                    selectedFood = food
                    selectedUnit = entry.unit
                    quantityText = entry.quantity.toString()
                    calendar = Calendar.getInstance().apply { timeInMillis = entry.timestamp }
                    hasLoadedEdit = true
                }
            }
        }
    }

    when (screenState) {
        MealScreenState.SEARCH -> {
            SearchFoodScreen(
                foodTypes = foodTypes,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onBack = { screenState = MealScreenState.MAIN },
                onAddNewFood = { screenState = MealScreenState.ADD_NEW },
                onFoodSelected = { food ->
                    selectedFood = food
                    selectedUnit = food.allowedUnits.split(",").firstOrNull()?.trim()
                    screenState = MealScreenState.MAIN
                }
            )
        }
        MealScreenState.ADD_NEW -> {
            AddNewFoodScreen(
                initialName = searchQuery,
                onBack = { screenState = MealScreenState.SEARCH },
                onSave = { newFood ->
                    scope.launch {
                        viewModel.addFoodType(newFood)
                        searchQuery = newFood.name
                        Toast.makeText(context, "Продукт сохранен. Выберите его в списке.", Toast.LENGTH_LONG).show()
                        screenState = MealScreenState.SEARCH
                    }
                }
            )
        }
        MealScreenState.MAIN -> {
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
                    Text(" Добавление продуктов", style = MaterialTheme.typography.titleLarge)
                }

                if (mealItems.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Список продуктов", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                            mealItems.forEachIndexed { index, item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.type?.name ?: "Неизвестно", style = MaterialTheme.typography.bodyMedium)
                                        Text("${item.entry.quantity} ${item.entry.unit}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    IconButton(onClick = { mealItems.removeAt(index) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                                if (index < mealItems.lastIndex) {
                                    HorizontalDivider()
                                }
                            }
                        }
                    }
                }

                if (selectedFood == null) {
                    Button(
                        onClick = {
                            searchQuery = ""
                            screenState = MealScreenState.SEARCH
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Найти и добавить продукт")
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedFood!!.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = {
                                    selectedFood = null
                                    quantityText = ""
                                }) {
                                    Icon(Icons.Default.Close, contentDescription = "Отменить выбор")
                                }
                            }

                            val units = selectedFood!!.allowedUnits.split(",").map { it.trim() }

                            ExposedDropdownMenuBox(expanded = expandedUnit, onExpandedChange = { expandedUnit = it }) {
                                OutlinedTextField(
                                    value = selectedUnit ?: "",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Единица измерения") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUnit) },
                                    modifier = Modifier.menuAnchor().fillMaxWidth()
                                )
                                ExposedDropdownMenu(expanded = expandedUnit, onDismissRequest = { expandedUnit = false }) {
                                    units.forEach { unit ->
                                        DropdownMenuItem(text = { Text(unit) }, onClick = {
                                            selectedUnit = unit
                                            expandedUnit = false
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

                            if (currentValidQuantity > 0) {
                                val multiplier = when (selectedUnit) {
                                    "г", "мл" -> currentValidQuantity / 100.0
                                    "шт" -> currentValidQuantity
                                    else -> 0.0
                                }
                                val curCarbs = selectedFood!!.carbs * multiplier
                                val curProteins = selectedFood!!.proteins * multiplier
                                val curFats = selectedFood!!.fats * multiplier
                                val curCalories = selectedFood!!.calories * multiplier

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text("КБЖУ для этого продукта:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                        Text("Кал: ${"%.1f".format(curCalories)} | Б: ${"%.1f".format(curProteins)}г | Ж: ${"%.1f".format(curFats)}г | У: ${"%.1f".format(curCarbs)}г",
                                            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                    }
                                }

                                Button(
                                    onClick = {
                                        mealItems.add(currentValidItem!!)
                                        selectedFood = null
                                        quantityText = ""
                                        selectedUnit = null
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Добавить к приему пищи")
                                }
                            }
                        }
                    }
                }

                var totalCarbs = 0.0
                var totalProteins = 0.0
                var totalFats = 0.0
                var totalCalories = 0.0

                allItems.forEach { item ->
                    val food = item.type
                    if (food != null) {
                        val multiplier = when (item.entry.unit) {
                            "г", "мл" -> item.entry.quantity / 100.0
                            "шт" -> item.entry.quantity
                            else -> 0.0
                        }
                        totalCarbs += food.carbs * multiplier
                        totalProteins += food.proteins * multiplier
                        totalFats += food.fats * multiplier
                        totalCalories += food.calories * multiplier
                    }
                }

                if (allItems.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Итого за прием пищи:", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                            Text("Калории: ${"%.1f".format(totalCalories)}")
                            Text("Белки: ${"%.1f".format(totalProteins)} г")
                            Text("Жиры: ${"%.1f".format(totalFats)} г")
                            Text("Углеводы: ${"%.1f".format(totalCarbs)} г")
                        }
                    }
                }

                OutlinedTextField(
                    value = currentGlucose,
                    onValueChange = { currentGlucose = it },
                    label = { Text("Текущая глюкоза (ммоль/л) перед едой") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                val bolusDose: Double? = run {
                    val icr = carbRatio
                    val target = userProfile?.targetGlucoseHigh
                    val current = currentGlucose.toDoubleOrNull()
                    if (icr == null || icr <= 0.0 || current == null) null
                    else {
                        val mealInsulin = totalCarbs / icr
                        val correction = if (target != null && current > target) (current - target) / (isf ?: 2.0) else 0.0
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
                        if (!isFormValid) {
                            errorMessage = "Заполните все поля корректно"
                            return@Button
                        }
                        scope.launch {
                            val finalTimestamp = calendar.timeInMillis
                            val itemsToSave = allItems.map { it.entry.copy(timestamp = finalTimestamp) }

                            itemsToSave.forEach { entry ->
                                if (entry.id != 0) {
                                    viewModel.updateEntry(entry)
                                } else {
                                    viewModel.addFoodEntry(entry)
                                }
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
                    Text(if (entryId != -1) "Сохранить изменения" else "Сохранить приём пищи", color = Color.White)
                }

                errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        }
    }
}
