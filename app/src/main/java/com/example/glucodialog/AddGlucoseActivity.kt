package com.example.glucodialog

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.glucodialog.data.AppDatabase
import com.example.glucodialog.data.GlucoseEntry
import com.example.glucodialog.data.UserProfile
import com.example.glucodialog.utils.DateTimeHelper
import com.example.glucodialog.utils.HealthAnalyzer
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.*

class AddGlucoseActivity : AppCompatActivity() {

    private lateinit var etGlucoseValue: EditText
    private lateinit var tvSelectedTime: TextView
    private lateinit var btnPickTime: Button
    private lateinit var btnSave: Button
    private lateinit var spinnerUnit: Spinner
    private lateinit var tvCorrectionDose: TextView

    private val calendar = Calendar.getInstance()
    private val scope = MainScope()
    private var isDateTimeSelected = true
    private val units = listOf("ммоль/л", "мг/дл")

    private var userProfile: UserProfile? = null

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_glucose)

        etGlucoseValue = findViewById(R.id.etGlucoseValue)
        tvSelectedTime = findViewById(R.id.tvSelectedTime)
        btnPickTime = findViewById(R.id.btnPickGlucoseTime)
        btnSave = findViewById(R.id.btnSaveGlucose)
        tvCorrectionDose = findViewById(R.id.tvCorrectionDose)
        spinnerUnit = findViewById(R.id.spinnerUnit)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, units)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUnit.adapter = adapter

        updateDateTimeDisplay()
        loadUserProfileForISF()

        btnPickTime.setOnClickListener {
            showDateTimePicker()
        }

        btnSave.setOnClickListener {
            saveGlucoseToDatabase()
        }

        etGlucoseValue.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                calculateCorrectionDose()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        spinnerUnit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                calculateCorrectionDose()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
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

    private fun loadUserProfileForISF() {
        val db = AppDatabase.getDatabase(this)
        scope.launch {
            userProfile = db.userProfileDao().getUserProfile().firstOrNull()
            runOnUiThread {
                calculateCorrectionDose()
            }
        }
    }

    private fun getNormalizedGlucoseInMmolL(): Double? {
        val text = etGlucoseValue.text.toString()
        val value = text.toDoubleOrNull() ?: return null
        val unit = spinnerUnit.selectedItem.toString()
        return if (unit == "мг/дл") value / 18.0 else value
    }

    private fun calculateCorrectionDose() {
        val glucoseMmolL = getNormalizedGlucoseInMmolL()
        val profile = userProfile

        if (glucoseMmolL == null || profile == null) {
            tvCorrectionDose.text = "Корректирующая доза: —"
            return
        }

        val tdd = profile.bolusDose + profile.basalDose
        val target = profile.targetGlucoseHigh

        if (tdd > 0 && glucoseMmolL > target) {
            val isf = 100 / tdd
            val excess = glucoseMmolL - target
            val correctionDose = excess / isf
            tvCorrectionDose.text = "Корректирующая доза: %.1f ед.".format(correctionDose)
        } else {
            tvCorrectionDose.text = "Корректирующая доза: 0 ед."
        }
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

        requestNotificationPermission()

        val db = AppDatabase.getDatabase(this)
        scope.launch {
            db.glucoseDao().insertGlucoseEntry(entry)
            HealthAnalyzer.analyzeGlucoseEntry(this@AddGlucoseActivity, db, entry)

            runOnUiThread {
                Toast.makeText(this@AddGlucoseActivity, "Запись сохранена", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
