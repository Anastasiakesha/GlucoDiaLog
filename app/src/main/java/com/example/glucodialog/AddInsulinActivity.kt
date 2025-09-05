package com.example.glucodialog

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.glucodialog.data.AppDatabase
import com.example.glucodialog.ui.GlucoseEntryScreen
import com.example.glucodialog.ui.InsulinEntryScreen
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class AddInsulinActivity : ComponentActivity() {
    private val scope = MainScope()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getDatabase(this)

        scope.launch {
            val userProfile = db.userProfileDao().getUserProfile().firstOrNull()
            val insulinDao = db.insulinDao()

            runOnUiThread {
                setContent {
                    InsulinEntryScreen(
                        userProfile = userProfile,
                        insulinDao = insulinDao,
                        onBack = { finish() }
                    )
                }
            }
        }
    }
}