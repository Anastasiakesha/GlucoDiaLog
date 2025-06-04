package com.example.glucodialog

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.glucodialog.data.ActivityEntry
import com.example.glucodialog.data.ActivityType
import com.example.glucodialog.data.AppDatabase
import com.example.glucodialog.utils.DateTimeHelper
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*

class AddPhysicalActivity : AppCompatActivity() {

    private lateinit var spinnerActivityType: Spinner
    private lateinit var etDuration: EditText
    private lateinit var tvSelectedTime: TextView
    private lateinit var btnPickTime: Button
    private lateinit var btnSave: Button

    private val calendar = Calendar.getInstance()
    private val scope = MainScope()
    private var isDateTimeSelected = false

    private var activityTypes: List<ActivityType> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_activity)

        // Инициализация UI
        spinnerActivityType = findViewById(R.id.spinnerActivityType)
        etDuration = findViewById(R.id.etDuration)
        tvSelectedTime = findViewById(R.id.tvSelectedTime)
        btnPickTime = findViewById(R.id.btnPickActivityTime)
        btnSave = findViewById(R.id.btnSaveActivity)

        updateDateTimeDisplay()

        btnPickTime.setOnClickListener {
            showDateTimePicker()
        }

        btnSave.setOnClickListener {
            saveActivityToDatabase()
        }

        loadActivityTypes()
    }

    private fun loadActivityTypes() {
        val db = AppDatabase.getDatabase(this)

        scope.launch {
            activityTypes = db.activityDao().getAllActivityTypes().first()

            runOnUiThread {
                if (activityTypes.isEmpty()) {
                    Toast.makeText(this@AddPhysicalActivity, "Нет доступных видов активности", Toast.LENGTH_LONG).show()
                    finish()
                    return@runOnUiThread
                }

                val adapter = ArrayAdapter(
                    this@AddPhysicalActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    activityTypes.map { it.name }
                )
                spinnerActivityType.adapter = adapter
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

    private fun saveActivityToDatabase() {
        val durationText = etDuration.text.toString()

        if (durationText.isBlank()) {
            Toast.makeText(this, "Введите длительность", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isDateTimeSelected) {
            Toast.makeText(this, "Выберите дату и время", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedIndex = spinnerActivityType.selectedItemPosition
        if (selectedIndex !in activityTypes.indices) {
            Toast.makeText(this, "Некорректный выбор активности", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedActivityType = activityTypes[selectedIndex]
        val duration = durationText.toIntOrNull()
        if (duration == null || duration <= 0) {
            Toast.makeText(this, "Неверное значение длительности", Toast.LENGTH_SHORT).show()
            return
        }

        val timestamp = calendar.timeInMillis

        val entry = ActivityEntry(
            activityTypeId = selectedActivityType.id,
            durationMinutes = duration,
            timestamp = timestamp
        )

        val db = AppDatabase.getDatabase(this)
        scope.launch {
            db.activityDao().insertActivityEntry(entry)
            runOnUiThread {
                Toast.makeText(this@AddPhysicalActivity, "Активность добавлена", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
