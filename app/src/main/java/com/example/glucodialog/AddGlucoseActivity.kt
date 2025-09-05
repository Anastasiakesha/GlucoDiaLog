package com.example.glucodialog

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.example.glucodialog.data.AppDatabase
import com.example.glucodialog.data.GlucoseEntry
import com.example.glucodialog.data.UserProfile
import com.example.glucodialog.ui.GlucoseEntryScreen
import com.example.glucodialog.utils.DateTimeHelper
import com.example.glucodialog.utils.HealthAnalyzer
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.*
class AddGlucoseActivity : AppCompatActivity() {

    private val scope = MainScope()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getDatabase(this)

        scope.launch {
            val userProfile = db.userProfileDao().getUserProfile().firstOrNull()
            val glucoseDao = db.glucoseDao()

            runOnUiThread {
                setContent {
                    GlucoseEntryScreen(
                        userProfile = userProfile,
                        glucoseDao = glucoseDao,
                        onBack = { finish() }
                    )
                }
            }
        }
    }
}