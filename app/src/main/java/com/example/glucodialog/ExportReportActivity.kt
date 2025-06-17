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
    private lateinit var progressBar: ProgressBar
    private lateinit var tvExporting: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_export_report)

        progressBar = findViewById(R.id.progressBar)
        tvExporting = findViewById(R.id.tvExporting)


        lifecycleScope.launch {
            ExcelExporter.exportAllData(
                this@ExportReportActivity,
                AppDatabase.getDatabase(this@ExportReportActivity)
            )

            Toast.makeText(this@ExportReportActivity, "Экспорт завершён", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
