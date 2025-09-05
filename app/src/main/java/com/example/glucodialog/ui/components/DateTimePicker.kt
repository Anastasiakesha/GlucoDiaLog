package com.example.glucodialog.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DateTimePickerButton(
    calendar: Calendar,
    onDateTimeSelected: (Calendar) -> Unit
) {
    val context = LocalContext.current
    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }

    Button(
        onClick = {
            val now = Calendar.getInstance()

            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    val newCalendar = Calendar.getInstance().apply {
                        time = calendar.time
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month)
                        set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    }

                    val maxHour: Int
                    val maxMinute: Int
                    // Проверяем, выбрана ли сегодняшняя дата
                    if (newCalendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                        newCalendar.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
                        newCalendar.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH)
                    ) {
                        maxHour = now.get(Calendar.HOUR_OF_DAY)
                        maxMinute = now.get(Calendar.MINUTE)
                    } else {
                        maxHour = 23
                        maxMinute = 59
                    }

                    TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            // Если пользователь выбрал слишком позднее время для сегодняшнего дня
                            val safeHour = if (hourOfDay > maxHour) maxHour else hourOfDay
                            val safeMinute = if (hourOfDay == maxHour && minute > maxMinute) maxMinute else minute

                            newCalendar.set(Calendar.HOUR_OF_DAY, safeHour)
                            newCalendar.set(Calendar.MINUTE, safeMinute)
                            newCalendar.set(Calendar.SECOND, 0)
                            newCalendar.set(Calendar.MILLISECOND, 0)

                            onDateTimeSelected(newCalendar)
                        },
                        newCalendar.get(Calendar.HOUR_OF_DAY),
                        newCalendar.get(Calendar.MINUTE),
                        true
                    ).show()

                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.maxDate = now.timeInMillis
            }.show()
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBBDEFB)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text(
            text = "⏰ ${dateFormat.format(calendar.time)}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF0D47A1)
        )
    }
}