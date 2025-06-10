package com.example.glucodialog

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.glucodialog.data.AppDatabase
import com.example.glucodialog.data.GlucoseEntry
import com.example.glucodialog.utils.DateTimeHelper
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.*

class AddGlucoseActivity : AppCompatActivity() {

    private lateinit var etGlucoseValue: EditText
    private lateinit var tvSelectedTime: TextView
    private lateinit var btnPickTime: Button
    private lateinit var btnSave: Button
    private lateinit var spinnerUnit: Spinner
    private val units = listOf("ммоль/л", "мг/дл")


    private val calendar = Calendar.getInstance()
    private val scope = MainScope()
    private var isDateTimeSelected = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_glucose)

        etGlucoseValue = findViewById(R.id.etGlucoseValue)
        tvSelectedTime = findViewById(R.id.tvSelectedTime)
        btnPickTime = findViewById(R.id.btnPickGlucoseTime)
        btnSave = findViewById(R.id.btnSaveGlucose)

        spinnerUnit = findViewById(R.id.spinnerUnit)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, units)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUnit.adapter = adapter


        updateDateTimeDisplay()

        btnPickTime.setOnClickListener {
            showDateTimePicker()
        }

        btnSave.setOnClickListener {
            saveGlucoseToDatabase()
        }
    }

    private fun showDateTimePicker() {
        DateTimeHelper.showDateTimePicker(this, calendar) { selectedCalendar ->
            calendar.timeInMillis = selectedCalendar.timeInMillis
            isDateTimeSelected = true
            updateDateTimeDisplay()
        }
    }

    private fun updateDateTimeDisplay() {
        tvSelectedTime.text = DateTimeHelper.formatDateTime(calendar)
    }

    private fun saveGlucoseToDatabase() {
        val glucoseText = etGlucoseValue.text.toString()
        val selectedUnit = spinnerUnit.selectedItem.toString()

        if (glucoseText.isBlank()) {
            Toast.makeText(this, "Введите уровень глюкозы", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isDateTimeSelected) {
            Toast.makeText(this, "Выберите дату и время", Toast.LENGTH_SHORT).show()
            return
        }

        val glucoseLevel = glucoseText.toDoubleOrNull()
        if (glucoseLevel == null) {
            Toast.makeText(this, "Некорректное значение", Toast.LENGTH_SHORT).show()
            return
        }


        val valid = when (selectedUnit) {
            "ммоль/л" -> glucoseLevel in 2.0..33.3
            "мг/дл" -> glucoseLevel in 36.0..600.0
            else -> false
        }

        if (!valid) {
            Toast.makeText(this, "Значение вне допустимого диапазона для $selectedUnit", Toast.LENGTH_SHORT).show()
            return
        }

        val timestamp = calendar.timeInMillis
        val entry = GlucoseEntry(
            glucoseLevel = glucoseLevel,
            unit = selectedUnit,
            timestamp = timestamp
        )

        val db = AppDatabase.getDatabase(this)
        scope.launch {
            db.glucoseDao().insertGlucoseEntry(entry)
            runOnUiThread {
                Toast.makeText(this@AddGlucoseActivity, "Запись сохранена", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

}
