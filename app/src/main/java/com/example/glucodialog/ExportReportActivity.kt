package com.example.glucodialog

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.glucodialog.data.AppDatabase
import com.example.glucodialog.utils.ExcelExporter
import kotlinx.coroutines.launch

class ExportReportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_export_report)

        val btnExport = findViewById<Button>(R.id.btnExportToExcel)
        btnExport.setOnClickListener {
            lifecycleScope.launch {
                ExcelExporter.exportAllData(this@ExportReportActivity, AppDatabase.getDatabase(this@ExportReportActivity))
            }
        }
    }
}
