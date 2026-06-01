package com.example.glucodialog.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.glucodialog.utils.FetalWeightCalculator
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FetalGrowthScreen(onBack: () -> Unit) {
    var bpd by remember { mutableStateOf("") }
    var hc by remember { mutableStateOf("") }
    var ac by remember { mutableStateOf("") }
    var fl by remember { mutableStateOf("") }
    var resultWeight by remember { mutableStateOf<Double?>(null) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Вес плода (УЗИ)") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Назад") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFCE4EC))) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ChildCare, null, tint = Color(0xFFE91E63))
                    Spacer(Modifier.width(8.dp))
                    Text("Введите данные из протокола УЗИ для оценки веса плода")
                }
            }

            OutlinedTextField(
                value = bpd, onValueChange = { bpd = it },
                label = { Text("BPD (Размер головы), мм") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = hc, onValueChange = { hc = it },
                label = { Text("HC (Окружность головы), мм") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = ac, onValueChange = { ac = it },
                label = { Text("AC (Окружность живота), мм") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = fl, onValueChange = { fl = it },
                label = { Text("FL (Длина бедра), мм") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val b = bpd.toDoubleOrNull() ?: 0.0
                    val h = hc.toDoubleOrNull() ?: 0.0
                    val a = ac.toDoubleOrNull() ?: 0.0
                    val f = fl.toDoubleOrNull() ?: 0.0
                    if (b > 0 && h > 0 && a > 0 && f > 0) {
                        resultWeight = FetalWeightCalculator.calculateEFW(b, h, a, f)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Рассчитать вес")
            }

            resultWeight?.let { weight ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Предполагаемый вес плода:",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            "${String.format("%.0f", weight)} грамм",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (weight > 4000) {
                            Text(
                                "Внимание: Вес выше 4000г может указывать на макросомию.",
                                color = Color.Red,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }

            Text(
                "Примечание: Данный расчет является оценочным. Погрешность составляет ±15%. Всегда консультируйтесь с врачом.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}