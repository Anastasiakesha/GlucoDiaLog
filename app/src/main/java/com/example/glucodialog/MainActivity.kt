package com.example.glucodialog

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_dashboard)

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
    }
}

