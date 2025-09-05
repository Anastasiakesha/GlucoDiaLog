package com.example.glucodialog

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.glucodialog.data.AppDatabase
import com.example.glucodialog.utils.ExcelExporter
import kotlinx.coroutines.launch

class ExportReportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        lifecycleScope.launch {
            ExcelExporter.exportAllData(
                this@ExportReportActivity,
                AppDatabase.getDatabase(this@ExportReportActivity)
            )

            Toast.makeText(this@ExportReportActivity, "Экспорт в xls завершён", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
