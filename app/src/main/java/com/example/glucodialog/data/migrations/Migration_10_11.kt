package com.example.glucodialog.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_10_11 = object : Migration(10, 11) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `blood_pressure_entries` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                `systolic` INTEGER NOT NULL, 
                `diastolic` INTEGER NOT NULL, 
                `pulse` INTEGER NOT NULL, 
                `timestamp` INTEGER NOT NULL
            )
        """.trimIndent())
    }
}