package com.example.glucodialog

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.glucodialog.data.AppDatabase
import com.example.glucodialog.data.FoodEntry
import com.example.glucodialog.data.FoodItem
import com.example.glucodialog.utils.DateTimeHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddMealActivity : AppCompatActivity() {

    private lateinit var spinnerFoodItem: Spinner
    private lateinit var etQuantity: EditText
    private lateinit var spinnerUnit: Spinner
    private lateinit var tvCalories: TextView
    private lateinit var tvProteins: TextView
    private lateinit var tvFats: TextView
    private lateinit var tvCarbs: TextView
    private lateinit var btnSaveMeal: Button
    private lateinit var tvSelectedTime: TextView
    private lateinit var btnPickTime: Button
    private lateinit var tvCalculatedInsulin: TextView
    private lateinit var tvICRatio: TextView



    private lateinit var foodItems: List<FoodItem>
    private var selectedFoodItem: FoodItem? = null

    private val calendar = Calendar.getInstance()
    private var isDateTimeSelected = false
    private var icValue: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_meal)

        // UI
        spinnerFoodItem = findViewById(R.id.spinnerFoodItem)
        etQuantity = findViewById(R.id.etQuantity)
        spinnerUnit = findViewById(R.id.spinnerUnit)
        tvCalories = findViewById(R.id.tvCalories)
        tvProteins = findViewById(R.id.tvProteins)
        tvFats = findViewById(R.id.tvFats)
        tvCarbs = findViewById(R.id.tvCarbs)
        btnSaveMeal = findViewById(R.id.btnSaveMeal)
        tvSelectedTime = findViewById(R.id.tvSelectedTime)
        btnPickTime = findViewById(R.id.btnPickMealTime)
        tvCalculatedInsulin = findViewById(R.id.tvCalculatedInsulin)
        tvICRatio = findViewById(R.id.tvICRatio)

        updateDateTimeDisplay()
        btnPickTime.setOnClickListener { showDateTimePicker() }
        btnSaveMeal.setOnClickListener { saveMealToDatabase() }

        loadFoodItems()
        loadUserProfileAndCalculateIC()

        etQuantity.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                updateNutritionDisplay()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun loadFoodItems() {
        val db = AppDatabase.getDatabase(this)

        lifecycleScope.launch {
            foodItems = db.foodDao().getAllFoodItems().first()

            runOnUiThread {
                if (foodItems.isEmpty()) {
                    Toast.makeText(this@AddMealActivity, "Нет доступных продуктов", Toast.LENGTH_LONG).show()
                    finish()
                    return@runOnUiThread
                }

                val foodNames = foodItems.map { it.name }
                val adapter = ArrayAdapter(this@AddMealActivity, android.R.layout.simple_spinner_item, foodNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerFoodItem.adapter = adapter

                spinnerFoodItem.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long
                    ) {
                        selectedFoodItem = foodItems[position]
                        updateUnitSpinner()
                        updateNutritionDisplay()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        selectedFoodItem = null
                    }
                }
            }
        }
    }

    private fun updateUnitSpinner() {
        val food = selectedFoodItem ?: return
        val allowedUnits = food.allowedUnits.split(",").map { it.trim() }

        val unitAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, allowedUnits)
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUnit.adapter = unitAdapter
    }

    private fun saveMealToDatabase() {
        val quantity = etQuantity.text.toString().toDoubleOrNull() ?: 0.0
        val food = selectedFoodItem
        val unit = spinnerUnit.selectedItem?.toString() ?: "г"

        if (quantity <= 0.0) {
            Toast.makeText(this, "Введите корректное количество", Toast.LENGTH_SHORT).show()
            return
        }

        if (food == null) {
            Toast.makeText(this, "Выберите продукт", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isDateTimeSelected) {
            Toast.makeText(this, "Выберите дату и время", Toast.LENGTH_SHORT).show()
            return
        }

        val entry = FoodEntry(
            foodItemId = food.id,
            quantity = quantity,
            unit = unit,
            timestamp = calendar.timeInMillis
        )

        val db = AppDatabase.getDatabase(this)
        lifecycleScope.launch {
            db.foodDao().insertFoodEntry(entry)
            runOnUiThread {
                Toast.makeText(this@AddMealActivity, "Запись сохранена", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun updateCalculatedInsulin() {
        val quantity = etQuantity.text.toString().toDoubleOrNull() ?: 0.0
        val food = selectedFoodItem ?: return
        val factor = quantity / 100.0
        val carbs = food.carbs * factor

        val ic = icValue
        if (ic != null && ic > 0) {
            val insulin = carbs / ic
            tvCalculatedInsulin.text = "Инсулин: %.1f ед.".format(insulin)
        } else {
            tvCalculatedInsulin.text = "Инсулин: недоступно"
        }
    }


    private fun updateNutritionDisplay() {
        val quantity = etQuantity.text.toString().toDoubleOrNull() ?: 0.0
        val food = selectedFoodItem ?: return

        val factor = quantity / 100.0

        tvCalories.text = "Калории: ${(food.calories * factor).toInt()}"
        tvProteins.text = "Белки: %.1f г".format(food.proteins * factor)
        tvFats.text = "Жиры: %.1f г".format(food.fats * factor)
        tvCarbs.text = "Углеводы: %.1f г".format(food.carbs * factor)

        updateCalculatedInsulin()

    }

    private fun loadUserProfileAndCalculateIC() {
        val db = AppDatabase.getDatabase(this)
        lifecycleScope.launch {
            val profile = db.userProfileDao().getUserProfile().first()
            if (profile != null) {
                val tdd = profile.basalDose + profile.bolusDose
                if (tdd > 0) {
                    val ic = 500 / tdd
                    icValue = ic
                    runOnUiThread {
                        tvICRatio.text = "Углеводный коэффициент: %.1f г/ед.".format(ic)
                        updateCalculatedInsulin()
                    }
                } else {
                    runOnUiThread {
                        tvICRatio.text = "Углеводный коэффициент: недостаточно данных"
                    }
                }
            } else {
                runOnUiThread {
                    tvICRatio.text = "Углеводный коэффициент: профиль не найден"
                }
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
}