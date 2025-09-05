package com.example.glucodialog

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.example.glucodialog.data.AppDatabase
import com.example.glucodialog.ui.MedicationEntryScreen
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class AddMedicationActivity : AppCompatActivity() {

        private val scope = MainScope()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getDatabase(this)
        scope.launch {
            val userProfile = db.userProfileDao().getUserProfile().firstOrNull()
            val medicationDao = db.medicationDao()

            runOnUiThread {
                setContent {
                    MedicationEntryScreen(
                        userProfile = userProfile,
                        medicationDao = medicationDao,
                        onBack = { finish() }
                    )
                }
            }
        }
    }
}