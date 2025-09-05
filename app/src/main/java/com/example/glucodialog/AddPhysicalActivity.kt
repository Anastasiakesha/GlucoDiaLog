package com.example.glucodialog

import android.os.Bundle
import android.widget.*
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.example.glucodialog.data.AppDatabase
import com.example.glucodialog.ui.ActivityEntryScreen
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch


class AddPhysicalActivity : AppCompatActivity() {
    private val scope = MainScope()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDatabase.getDatabase(this)
        scope.launch {
            val userProfile = db.userProfileDao().getUserProfile().firstOrNull()
            val activityDao = db.activityDao()

            runOnUiThread {
                setContent {
                    ActivityEntryScreen(
                        userProfile = userProfile,
                        activityDao = activityDao,
                        onBack = { finish() }
                    )
                }
            }
        }
    }
}