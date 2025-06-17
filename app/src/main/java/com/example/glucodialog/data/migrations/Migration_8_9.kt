package com.example.glucodialog.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE UserProfile ADD COLUMN medicationTimeMinutesFromMidnight INTEGER")
    }
}
