package com.example.glucodialog

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.glucodialog.data.AppDatabase
import com.example.glucodialog.data.UserProfile
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class UserSetupActivity : AppCompatActivity() {

    companion object {
        const val MODE_CREATE = "create"
        const val MODE_EDIT = "edit"
    }

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var spinnerGender: Spinner
    private lateinit var etWeight: EditText
    private lateinit var etHeight: EditText
    private lateinit var spinnerDiabetesType: Spinner
    private lateinit var etTargetGlucoseLow: EditText
    private lateinit var etTargetGlucoseHigh: EditText
    private lateinit var spinnerGlucoseUnit: Spinner
    private lateinit var spinnerBolus: Spinner
    private lateinit var etBolusDose: EditText
    private lateinit var spinnerBasal: Spinner
    private lateinit var etBasalDose: EditText
    private lateinit var spinnerMedication: Spinner
    private lateinit var etMedicationDose: EditText
    private lateinit var spinnerMedicationUnit: Spinner
    private lateinit var btnSave: Button
    private lateinit var tvMedicationTime: TextView

    private val scope = MainScope()
    private var currentMode: String = MODE_CREATE
    private var existingProfile: UserProfile? = null

    // Вместо двух отдельных полей — одно целочисленное для хранения минут с полуночи
    private var medicationTimeMinutesFromMidnight: Int = 8 * 60  // 08:00 по умолчанию

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_setup)

        initViews()
        setupStaticSpinners()

        currentMode = intent.getStringExtra("mode") ?: MODE_CREATE

        if (currentMode == MODE_EDIT) {
            title = "Редактировать профиль"
            btnSave.text = "Сохранить изменения"
        } else {
            title = "Создать профиль"
        }

        loadDynamicSpinners()

        btnSave.setOnClickListener {
            saveUserData()
        }

        tvMedicationTime.setOnClickListener {
            showTimePickerDialog()
        }
    }

    private fun initViews() {
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        spinnerGender = findViewById(R.id.spinnerGender)
        etWeight = findViewById(R.id.etWeight)
        etHeight = findViewById(R.id.etHeight)
        spinnerDiabetesType = findViewById(R.id.spinnerDiabetesType)
        etTargetGlucoseLow = findViewById(R.id.etTargetGlucoseLow)
        etTargetGlucoseHigh = findViewById(R.id.etTargetGlucoseHigh)
        spinnerGlucoseUnit = findViewById(R.id.spinnerGlucoseUnit)
        spinnerBolus = findViewById(R.id.spinnerBolus)
        etBolusDose = findViewById(R.id.etBolusDose)
        spinnerBasal = findViewById(R.id.spinnerBasal)
        etBasalDose = findViewById(R.id.etBasalDose)
        spinnerMedication = findViewById(R.id.spinnerMedication)
        etMedicationDose = findViewById(R.id.etMedicationDose)
        spinnerMedicationUnit = findViewById(R.id.spinnerMedicationUnit)
        btnSave = findViewById(R.id.btnSaveUserSetup)
        tvMedicationTime = findViewById(R.id.tvMedicationTime)
    }

    private fun setupStaticSpinners() {
        spinnerGender.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, listOf("Мужской", "Женский")
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        spinnerDiabetesType.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, listOf("СД 1 типа", "СД 2 типа", "Гестационный СД")
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        spinnerGlucoseUnit.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, listOf("ммоль/л", "мг/дл")
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
    }

    private fun loadDynamicSpinners() {
        val db = AppDatabase.getDatabase(this)

        scope.launch {
            val insulinTypes = db.insulinDao().getAllInsulinTypes().firstOrNull() ?: emptyList()
            val medicationTypes = db.medicationDao().getAllMedicationTypes().firstOrNull() ?: emptyList()

            runOnUiThread {
                if (insulinTypes.isEmpty()) {
                    Toast.makeText(this@UserSetupActivity, "Нет данных по инсулину", Toast.LENGTH_LONG).show()
                }
                if (medicationTypes.isEmpty()) {
                    Toast.makeText(this@UserSetupActivity, "Нет данных по препаратам", Toast.LENGTH_LONG).show()
                }

                val bolusList = insulinTypes.filter { it.type == "Болюсный" }.map { it.name }
                val basalList = insulinTypes.filter { it.type == "Базальный" }.map { it.name }
                val medicationList = medicationTypes.map { it.name }

                spinnerBolus.adapter = ArrayAdapter(
                    this@UserSetupActivity,
                    android.R.layout.simple_spinner_item,
                    if (bolusList.isNotEmpty()) bolusList else listOf("Нет данных")
                ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

                spinnerBasal.adapter = ArrayAdapter(
                    this@UserSetupActivity,
                    android.R.layout.simple_spinner_item,
                    if (basalList.isNotEmpty()) basalList else listOf("Нет данных")
                ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

                spinnerMedication.adapter = ArrayAdapter(
                    this@UserSetupActivity,
                    android.R.layout.simple_spinner_item,
                    if (medicationList.isNotEmpty()) medicationList else listOf("Нет данных")
                ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

                spinnerMedicationUnit.adapter = ArrayAdapter(
                    this@UserSetupActivity,
                    android.R.layout.simple_spinner_item,
                    listOf("мг", "мкг", "мл", "таб")
                ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

                if (currentMode == MODE_EDIT) {
                    loadUserProfile()
                } else {
                    updateMedicationTimeText()
                }
            }
        }
    }

    private fun loadUserProfile() {
        val db = AppDatabase.getDatabase(this)
        scope.launch {
            val user = db.userProfileDao().getUserProfile().firstOrNull()
            if (user != null) {
                existingProfile = user
                runOnUiThread {
                    etName.setText(user.name)
                    etEmail.setText(user.email)
                    etEmail.isEnabled = false
                    etWeight.setText(user.weight.toString())
                    etHeight.setText(user.height.toString())
                    etTargetGlucoseLow.setText(user.targetGlucoseLow.toString())
                    etTargetGlucoseHigh.setText(user.targetGlucoseHigh.toString())
                    etBolusDose.setText(user.bolusDose.toString())
                    etBasalDose.setText(user.basalDose.toString())
                    etMedicationDose.setText(user.medicationDose.toString())

                    spinnerGender.setSelection(getSpinnerIndex(spinnerGender, user.gender))
                    spinnerDiabetesType.setSelection(getSpinnerIndex(spinnerDiabetesType, user.diabetesType))
                    spinnerGlucoseUnit.setSelection(getSpinnerIndex(spinnerGlucoseUnit, user.glucoseUnit))
                    spinnerBolus.setSelection(getSpinnerIndex(spinnerBolus, user.bolusInsulin))
                    spinnerBasal.setSelection(getSpinnerIndex(spinnerBasal, user.basalInsulin))
                    spinnerMedication.setSelection(getSpinnerIndex(spinnerMedication, user.medication))
                    spinnerMedicationUnit.setSelection(getSpinnerIndex(spinnerMedicationUnit, user.medicationUnit))

                    medicationTimeMinutesFromMidnight = user.medicationTimeMinutesFromMidnight ?: (8 * 60)
                    updateMedicationTimeText()
                }
            }
        }
    }

    private fun getSpinnerIndex(spinner: Spinner, value: String): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString() == value) return i
        }
        return 0
    }

    private fun saveUserData() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val gender = spinnerGender.selectedItem?.toString() ?: ""
        val weight = etWeight.text.toString().toDoubleOrNull()
        val height = etHeight.text.toString().toDoubleOrNull()
        val diabetesType = spinnerDiabetesType.selectedItem?.toString() ?: ""
        val targetLow = etTargetGlucoseLow.text.toString().toDoubleOrNull()
        val targetHigh = etTargetGlucoseHigh.text.toString().toDoubleOrNull()
        val glucoseUnit = spinnerGlucoseUnit.selectedItem?.toString() ?: ""
        val bolusInsulin = spinnerBolus.selectedItem?.toString() ?: ""
        val bolusDose = etBolusDose.text.toString().toDoubleOrNull()
        val basalInsulin = spinnerBasal.selectedItem?.toString() ?: ""
        val basalDose = etBasalDose.text.toString().toDoubleOrNull()
        val medication = spinnerMedication.selectedItem?.toString() ?: ""
        val medicationDose = etMedicationDose.text.toString().toDoubleOrNull()
        val medicationUnit = spinnerMedicationUnit.selectedItem?.toString() ?: ""

        if (name.isEmpty() || email.isEmpty() || weight == null || height == null ||
            targetLow == null || targetHigh == null || bolusDose == null ||
            basalDose == null || medicationDose == null
        ) {
            Toast.makeText(this, "Пожалуйста, заполните все поля корректно", Toast.LENGTH_SHORT).show()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Введите корректный email", Toast.LENGTH_SHORT).show()
            return
        }

        if (weight <= 0) {
            Toast.makeText(this, "Вес должен быть положительным числом", Toast.LENGTH_SHORT).show()
            return
        }
        if (height <= 0) {
            Toast.makeText(this, "Рост должен быть положительным числом", Toast.LENGTH_SHORT).show()
            return
        }

        if (targetLow >= targetHigh) {
            Toast.makeText(this, "Нижняя цель глюкозы должна быть меньше верхней", Toast.LENGTH_SHORT).show()
            return
        }

        fun validateInsulinDose(dose: Double): Boolean = dose > 0 && dose <= 100

        if (!validateInsulinDose(bolusDose)) {
            Toast.makeText(this, "Введите корректную дозу болюсного инсулина (0-100)", Toast.LENGTH_SHORT).show()
            return
        }
        if (!validateInsulinDose(basalDose)) {
            Toast.makeText(this, "Введите корректную дозу базального инсулина (0-100)", Toast.LENGTH_SHORT).show()
            return
        }

        fun validateMedicationDose(dose: Double, unit: String): Boolean {
            return when (unit) {
                "мг" -> dose in 0.1..1000.0
                "мкг" -> dose in 1.0..100000.0
                "мл" -> dose in 0.1..100.0
                "таб" -> dose in 1.0..100.0
                else -> false
            }
        }

        if (!validateMedicationDose(medicationDose, medicationUnit)) {
            Toast.makeText(this, "Введите корректную дозу лекарственного препарата", Toast.LENGTH_SHORT).show()
            return
        }

        val profile = UserProfile(
            email = existingProfile?.email ?: email,
            name = name,
            gender = gender,
            weight = weight,
            height = height,
            diabetesType = diabetesType,
            targetGlucoseLow = targetLow,
            targetGlucoseHigh = targetHigh,
            glucoseUnit = glucoseUnit,
            bolusInsulin = bolusInsulin,
            bolusDose = bolusDose,
            basalInsulin = basalInsulin,
            basalDose = basalDose,
            medication = medication,
            medicationDose = medicationDose,
            medicationUnit = medicationUnit,
            medicationTimeMinutesFromMidnight = medicationTimeMinutesFromMidnight
        )

        val db = AppDatabase.getDatabase(this)
        scope.launch {
            if (currentMode == MODE_EDIT && existingProfile != null) {
                db.userProfileDao().updateUserProfile(profile)
            } else {
                db.userProfileDao().insertUserProfile(profile)
            }
            runOnUiThread {
                Toast.makeText(this@UserSetupActivity, "Профиль сохранён", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun showTimePickerDialog() {
        val hour = medicationTimeMinutesFromMidnight / 60
        val minute = medicationTimeMinutesFromMidnight % 60

        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, h, m ->
            medicationTimeMinutesFromMidnight = h * 60 + m
            updateMedicationTimeText()
        }
        TimePickerDialog(this, timeSetListener, hour, minute, true).show()
    }

    private fun updateMedicationTimeText() {
        val hour = medicationTimeMinutesFromMidnight / 60
        val minute = medicationTimeMinutesFromMidnight % 60
        tvMedicationTime.text = String.format("%02d:%02d", hour, minute)
    }
}
