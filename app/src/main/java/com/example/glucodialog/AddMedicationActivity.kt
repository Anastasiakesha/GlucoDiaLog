package com.example.glucodialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.glucodialog.data.AppDatabase
import com.example.glucodialog.data.MedicationEntry
import com.example.glucodialog.data.MedicationType
import com.example.glucodialog.utils.DateTimeHelper
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*

class AddMedicationActivity : AppCompatActivity() {

    private lateinit var spinnerMedication: Spinner
    private lateinit var etDosage: EditText
    private lateinit var tvSelectedTime: TextView
    private lateinit var spinnerUnit: Spinner
    private lateinit var btnPickTime: Button
    private lateinit var btnSave: Button

    private val calendar = Calendar.getInstance()
    private val scope = MainScope()
    private var isDateTimeSelected = true

    private var medicationTypes: List<MedicationType> = emptyList()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_medication)

        spinnerMedication = findViewById(R.id.spinnerMedication)
        etDosage = findViewById(R.id.etDosage)
        tvSelectedTime = findViewById(R.id.tvSelectedTime)
        spinnerUnit = findViewById(R.id.spinnerUnit)
        btnPickTime = findViewById(R.id.btnPickMedicationTime)
        btnSave = findViewById(R.id.btnSaveMedication)

        updateDateTimeDisplay()

        btnPickTime.setOnClickListener {
            showDateTimePicker()
        }

        btnSave.setOnClickListener {
            saveMedicationToDatabase()
        }

        setupUnitSpinner()
        loadMedicationTypes()
    }

    private fun setupUnitSpinner() {
        val units = listOf("мг", "мкг", "мл", "таб")
        val unitAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            units
        )
        spinnerUnit.adapter = unitAdapter
    }

    private fun loadMedicationTypes() {
        val db = AppDatabase.getDatabase(this)

        scope.launch {
            medicationTypes = db.medicationDao().getAllMedicationTypes().first()

            runOnUiThread {
                if (medicationTypes.isEmpty()) {
                    Toast.makeText(this@AddMedicationActivity, "Нет доступных препаратов", Toast.LENGTH_LONG).show()
                    finish()
                    return@runOnUiThread
                }

                val adapter = ArrayAdapter(
                    this@AddMedicationActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    medicationTypes.map { it.name }
                )
                spinnerMedication.adapter = adapter
            }
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

    private fun saveMedicationToDatabase() {
        val dosageText = etDosage.text.toString()

        if (dosageText.isBlank()) {
            Toast.makeText(this, "Введите дозировку", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isDateTimeSelected) {
            Toast.makeText(this, "Выберите дату и время", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedIndex = spinnerMedication.selectedItemPosition
        if (selectedIndex !in medicationTypes.indices) {
            Toast.makeText(this, "Некорректный выбор препарата", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedMedicationType = medicationTypes[selectedIndex]
        val unit = spinnerUnit.selectedItem?.toString() ?: ""
        val timestamp = calendar.timeInMillis

        val doseValue = dosageText.toDoubleOrNull()
        if (doseValue == null || doseValue <= 0.0) {
            Toast.makeText(this, "Введите корректную числовую дозу", Toast.LENGTH_SHORT).show()
            return
        }

        // Валидация по единице измерения
        when (unit) {
            "мг" -> {
                if (doseValue > 3000) {
                    Toast.makeText(this, "Слишком большая доза в мг", Toast.LENGTH_SHORT).show()
                    return
                }
            }
            "мкг" -> {
                if (doseValue > 1000) {
                    Toast.makeText(this, "Слишком большая доза в мкг", Toast.LENGTH_SHORT).show()
                    return
                }
            }
            "мл" -> {
                if (doseValue > 100) {
                    Toast.makeText(this, "Слишком большой объем в мл", Toast.LENGTH_SHORT).show()
                    return
                }
            }
            "таб" -> {
                if (doseValue > 10) {
                    Toast.makeText(this, "Слишком много таблеток", Toast.LENGTH_SHORT).show()
                    return
                }
            }
            else -> {
                Toast.makeText(this, "Выберите единицу измерения", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val entry = MedicationEntry(
            medicationTypeId = selectedMedicationType.id,
            dose = dosageText,
            unit = unit,
            timestamp = timestamp
        )

        val db = AppDatabase.getDatabase(this)
        scope.launch {
            db.medicationDao().insertMedicationEntry(entry)
            runOnUiThread {
                Toast.makeText(this@AddMedicationActivity, "Запись сохранена", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
