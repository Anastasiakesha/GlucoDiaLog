package com.example.glucodialog.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS user_profile (
                email TEXT NOT NULL PRIMARY KEY,
                name TEXT NOT NULL,
                gender TEXT NOT NULL,
                weight REAL NOT NULL,
                height REAL NOT NULL,
                diabetesType TEXT NOT NULL,
                targetGlucoseLow REAL NOT NULL,
                targetGlucoseHigh REAL NOT NULL,
                glucoseUnit TEXT NOT NULL,
                bolusInsulin TEXT NOT NULL,
                bolusDose REAL NOT NULL,
                basalInsulin TEXT NOT NULL,
                basalDose REAL NOT NULL,
                medication TEXT NOT NULL,
                medicationDose TEXT NOT NULL
            )
        """.trimIndent())
    }
}

