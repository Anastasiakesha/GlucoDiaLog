package com.example.glucodialog


import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.glucodialog.data.AppDatabase
import com.example.glucodialog.data.InsulinEntry
import com.example.glucodialog.data.InsulinType
import com.example.glucodialog.utils.DateTimeHelper
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*

class AddInsulinActivity : AppCompatActivity() {

    private lateinit var spinnerInsulinType: Spinner
    private lateinit var etInsulinDose: EditText
    private lateinit var tvSelectedTime: TextView
    private lateinit var btnPickTime: Button
    private lateinit var btnSave: Button

    private val calendar = Calendar.getInstance()
    private val scope = MainScope()
    private var isDateTimeSelected = false

    private var insulinTypes: List<InsulinType> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_insulin)

        // Инициализация UI
        spinnerInsulinType = findViewById(R.id.spinnerInsulinType)
        etInsulinDose = findViewById(R.id.etInsulinDose)
        tvSelectedTime = findViewById(R.id.tvSelectedTime)
        btnPickTime = findViewById(R.id.btnPickInsulinTime)
        btnSave = findViewById(R.id.btnSaveInsulin)

        updateDateTimeDisplay()

        btnPickTime.setOnClickListener {
            showDateTimePicker()
        }

        btnSave.setOnClickListener {
            saveInsulinEntryToDatabase()
        }

        loadInsulinTypes()
    }

    private fun loadInsulinTypes() {
        val db = AppDatabase.getDatabase(this)

        scope.launch {
            insulinTypes = db.insulinDao().getAllInsulinTypes().first()

            runOnUiThread {
                if (insulinTypes.isEmpty()) {
                    Toast.makeText(this@AddInsulinActivity, "Нет доступных типов инсулина", Toast.LENGTH_LONG).show()
                    finish()
                    return@runOnUiThread
                }

                val adapter = ArrayAdapter(
                    this@AddInsulinActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    insulinTypes.map { it.name }
                )
                spinnerInsulinType.adapter = adapter
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

    private fun saveInsulinEntryToDatabase() {
        val doseText = etInsulinDose.text.toString()

        if (doseText.isBlank()) {
            Toast.makeText(this, "Введите дозу инсулина", Toast.LENGTH_SHORT).show()
            return
        }

        val dose = doseText.toDoubleOrNull()
        if (dose == null || dose <= 0.0) {
            Toast.makeText(this, "Некорректное значение дозы", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isDateTimeSelected) {
            Toast.makeText(this, "Выберите дату и время", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedIndex = spinnerInsulinType.selectedItemPosition
        if (selectedIndex !in insulinTypes.indices) {
            Toast.makeText(this, "Некорректный выбор типа инсулина", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedType = insulinTypes[selectedIndex]

        val entry = InsulinEntry(
            insulinTypeId = selectedType.id,
            doseUnits = dose,
            timestamp = calendar.timeInMillis
        )

        val db = AppDatabase.getDatabase(this)
        scope.launch {
            db.insulinDao().insertInsulinEntry(entry)
            runOnUiThread {
                Toast.makeText(this@AddInsulinActivity, "Инсулин сохранён", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
