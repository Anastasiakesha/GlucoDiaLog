package com.example.glucodialog.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.glucodialog.ExportPDFActivity
import com.example.glucodialog.ExportReportActivity
import com.example.glucodialog.ImportActivity
import com.example.glucodialog.data.local.UserProfile
import com.example.glucodialog.ui.constants.Labels.DIABETES_TYPE_LABELS

@Composable
fun ProfileView(
    profile: UserProfile,
    onEdit: () -> Unit
) {
    val height = profile.height
    val weight = profile.weight
    val bmi = if (height > 0 && weight > 0) weight / ((height / 100) * (height / 100)) else null

    fun getBMIColor(bmi: Double) = when {
        bmi < 18.5 -> Color.Blue
        bmi < 25 -> Color.Green
        bmi < 30 -> Color.Yellow
        else -> Color.Red
    }

    fun getBMIStatus(bmi: Double) = when {
        bmi < 18.5 -> "Недостаточный"
        bmi < 25 -> "Нормальный"
        bmi < 30 -> "Избыточный"
        else -> "Ожирение"
    }

    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFBBDEFB), shape = MaterialTheme.shapes.medium),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👤")
                    }
                    Column {
                        Text(profile.name, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "${profile.gender} • ${DIABETES_TYPE_LABELS[profile.diabetesType] ?: ""}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        bmi?.let {
                            Text(
                                "ИМТ: ${String.format("%.1f", it)} (${getBMIStatus(it)})",
                                color = getBMIColor(it),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(onClick = onEdit) {
                    Text("✏️", fontSize = 12.sp)
                }
            }
        }


        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("📏 Физические параметры", style = MaterialTheme.typography.titleMedium)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Рост:")
                    Text("${profile.height} см", style = MaterialTheme.typography.bodyMedium)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Вес:")
                    Text("${profile.weight} кг", style = MaterialTheme.typography.bodyMedium)
                }
                bmi?.let {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("ИМТ:")
                        Text(String.format("%.1f", it), color = getBMIColor(it))
                    }
                }
            }
        }


        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("💉 Инсулинотерапия", style = MaterialTheme.typography.titleMedium)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Болюсный:")
                    Text("${profile.bolusInsulin} (${profile.bolusDose} ед)")
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Базальный:")
                    Text("${profile.basalInsulin} (${profile.basalDose} ед)")
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🎯 Целевой диапазон глюкозы", style = MaterialTheme.typography.titleMedium)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Минимум")
                        Text("${profile.targetGlucoseLow}", style = MaterialTheme.typography.titleLarge, color = Color.Blue)
                        Text("ммоль/л", style = MaterialTheme.typography.bodySmall)
                    }
                    Text("—", color = Color.Gray)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Максимум")
                        Text("${profile.targetGlucoseHigh}", style = MaterialTheme.typography.titleLarge, color = Color.Blue)
                        Text("ммоль/л", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }


        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("💊 Медикаменты", style = MaterialTheme.typography.titleMedium)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(profile.medication)
                    Text("${profile.medicationDose} ${profile.medicationUnit}")
                }
                profile.medicationTimeMinutesFromMidnight?.let { minutes ->
                    val hours = minutes / 60
                    val mins = minutes % 60
                    Text("Время приема: %02d:%02d".format(hours, mins))
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("📂 Управление данными", style = MaterialTheme.typography.titleMedium)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        val intent = Intent(context, ImportActivity::class.java)
                        context.startActivity(intent)
                    }) {
                        Text("⬇️ Импорт")
                    }

                    Button(onClick = {
                        val intent = Intent(context, ExportReportActivity::class.java)
                        context.startActivity(intent)
                    }) {
                        Text("⬆️ Экспорт в xls")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        val intent = Intent(context, ExportPDFActivity::class.java)
                        context.startActivity(intent)
                    }) {
                        Text("✨ Экспорт отчета в PDF")
                    }
                }
            }
        }
    }
}