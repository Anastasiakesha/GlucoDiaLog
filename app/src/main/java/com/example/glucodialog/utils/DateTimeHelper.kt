package com.example.glucodialog.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

object DateTimeHelper {

    fun showDateTimePicker(
        context: Context,
        initialCalendar: Calendar,
        onValidDateTimeSelected: (Calendar) -> Unit
    ) {
        val calendar = initialCalendar.clone() as Calendar

        val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            showTimePicker(context, calendar, onValidDateTimeSelected)
        }

        DatePickerDialog(
            context, dateListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker(
        context: Context,
        calendar: Calendar,
        onValidDateTimeSelected: (Calendar) -> Unit
    ) {
        val timeListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)

            if (calendar.after(Calendar.getInstance())) {
                Toast.makeText(context, "Нельзя выбрать будущее время", Toast.LENGTH_SHORT).show()
            } else {
                onValidDateTimeSelected(calendar)
            }
        }

        TimePickerDialog(
            context, timeListener,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    fun formatDateTime(calendar: Calendar): String {
        return SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(calendar.time)
    }
}
