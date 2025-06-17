package com.example.glucodialog.utils


import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.glucodialog.data.AppDatabase
import com.example.glucodialog.utils.HealthAnalyzer

class MedicationCheckWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val db = AppDatabase.getDatabase(context)
        HealthAnalyzer.checkMedicationCompliance(context, db)
        return Result.success()
    }
}
