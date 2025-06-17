package com.example.glucodialog.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE user_profile_new (
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
                medicationDose REAL NOT NULL,
                medicationUnit TEXT NOT NULL
            )
        """.trimIndent())

        database.execSQL("""
            INSERT INTO user_profile_new (
                email, name, gender, weight, height, diabetesType,
                targetGlucoseLow, targetGlucoseHigh, glucoseUnit,
                bolusInsulin, bolusDose, basalInsulin, basalDose,
                medication, medicationDose, medicationUnit
            )
            SELECT
                email, name, gender, weight, height, diabetesType,
                targetGlucoseLow, targetGlucoseHigh, glucoseUnit,
                bolusInsulin, bolusDose, basalInsulin, basalDose,
                medication,
                CAST(medicationDose AS REAL),
                medicationUnit
            FROM user_profile
        """.trimIndent())

        database.execSQL("DROP TABLE user_profile")
        database.execSQL("ALTER TABLE user_profile_new RENAME TO user_profile")
    }
}
