package com.example.glucodialog

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.glucodialog.data.AppDatabase
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import com.example.glucodialog.utils.PdfExporter

class ExportPDFActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            PdfExporter.exportAllData(
                this@ExportPDFActivity,
                AppDatabase.getDatabase(this@ExportPDFActivity)
            )

            Toast.makeText(this@ExportPDFActivity, "Экспорт в PDF завершён", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

}