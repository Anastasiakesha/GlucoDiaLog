package com.example.glucodialog

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.glucodialog.data.AppDatabase
import com.example.glucodialog.utils.ExcelImporter
import kotlinx.coroutines.launch

class ImportActivity : AppCompatActivity() {

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri == null) {
                Toast.makeText(this, "Файл не выбран", Toast.LENGTH_SHORT).show()
                finish()
                return@registerForActivityResult
            }

            lifecycleScope.launch {
                val db = AppDatabase.getDatabase(this@ImportActivity)
                ExcelImporter.importFromExcel(this@ImportActivity, uri, db)
                Toast.makeText(this@ImportActivity, "Импорт завершён", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        filePickerLauncher.launch("application/vnd.ms-excel")
    }
}
