package com.example.glucodialog

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.glucodialog.data.AppDatabase
import com.example.glucodialog.utils.WorkScheduler
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_dashboard)

        // Проверка наличия профиля
        checkUserProfile()

        // Обработка кнопок
        findViewById<Button>(R.id.btnAddMeal).setOnClickListener {
            startActivity(Intent(this, AddMealActivity::class.java))
        }

        findViewById<Button>(R.id.btnAddInsulin).setOnClickListener {
            startActivity(Intent(this, AddInsulinActivity::class.java))
        }

        findViewById<Button>(R.id.btnAddGlucose).setOnClickListener {
            startActivity(Intent(this, AddGlucoseActivity::class.java))
        }

        findViewById<Button>(R.id.btnAddMedication).setOnClickListener {
            startActivity(Intent(this, AddMedicationActivity::class.java))
        }

        findViewById<Button>(R.id.btnAddActivity).setOnClickListener {
            startActivity(Intent(this, AddPhysicalActivity::class.java))
        }

        findViewById<Button>(R.id.btnViewRecords).setOnClickListener {
            startActivity(Intent(this, ViewRecordsActivity::class.java))
        }

        findViewById<Button>(R.id.btnViewStatistics).setOnClickListener {
            startActivity(Intent(this, GlucoseStatsActivity::class.java))
        }

        findViewById<Button>(R.id.btnExportReport).setOnClickListener {
            startActivity(Intent(this, ExportReportActivity::class.java))
        }

        findViewById<Button>(R.id.btnImportFromExcel).setOnClickListener {
            startActivity(Intent(this, ImportActivity::class.java))
        }

        // Значок профиля
        findViewById<ImageButton>(R.id.btnProfileIcon).setOnClickListener {
            val intent = Intent(this, UserSetupActivity::class.java)
            intent.putExtra("mode", UserSetupActivity.MODE_EDIT)
            startActivity(intent)
        }

        WorkScheduler.scheduleDailyMedicationCheck(this)


    }

    private fun checkUserProfile() {
        val db = AppDatabase.getDatabase(this)
        scope.launch {
            val profile = db.userProfileDao().getUserProfile().first()
            if (profile == null) {
                startActivity(Intent(this@MainActivity, UserSetupActivity::class.java))
            }
        }
    }
}
