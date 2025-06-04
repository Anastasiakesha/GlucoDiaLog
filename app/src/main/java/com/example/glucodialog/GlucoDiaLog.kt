package com.example.glucodialog

import android.app.Application
import com.example.glucodialog.data.AppDatabase

class GlucoDiaLog : Application() {
    override fun onCreate() {
        super.onCreate()
        AppDatabase.getDatabase(this) // инициализация и предзаполнение
    }
}