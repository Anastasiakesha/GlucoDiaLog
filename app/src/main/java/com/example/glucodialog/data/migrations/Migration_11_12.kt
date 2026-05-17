package com.example.glucodialog.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_11_12 = object : Migration(11, 12) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE user_profile_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, email TEXT NOT NULL, name TEXT NOT NULL, gender TEXT NOT NULL, weight REAL NOT NULL, height REAL NOT NULL, diabetesType TEXT NOT NULL, targetGlucoseLow REAL NOT NULL, targetGlucoseHigh REAL NOT NULL, glucoseUnit TEXT NOT NULL
            )
        """.trimIndent())

        database.execSQL("""
            INSERT INTO user_profile_new (email, name, gender, weight, height, diabetesType, targetGlucoseLow, targetGlucoseHigh, glucoseUnit) 
            SELECT email, name, gender, weight, height, diabetesType, targetGlucoseLow, targetGlucoseHigh, glucoseUnit FROM user_profile
        """.trimIndent())

        database.execSQL("DROP TABLE user_profile")
        database.execSQL("ALTER TABLE user_profile_new RENAME TO user_profile")

        database.execSQL("CREATE TABLE IF NOT EXISTS `therapy_plans` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER NOT NULL, `startDate` INTEGER NOT NULL, `endDate` INTEGER, `notes` TEXT NOT NULL, FOREIGN KEY(`userId`) REFERENCES `user_profile`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_therapy_plans_userId` ON `therapy_plans` (`userId`)")

        database.execSQL("CREATE TABLE IF NOT EXISTS `insulin_therapy_plans` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `therapyPlanId` INTEGER NOT NULL, `insulinTypeId` INTEGER NOT NULL, `dose` REAL NOT NULL, `reminderTimeMinutes` INTEGER, `notes` TEXT NOT NULL, FOREIGN KEY(`therapyPlanId`) REFERENCES `therapy_plans`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`insulinTypeId`) REFERENCES `insulin_types`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_insulin_therapy_plans_therapyPlanId` ON `insulin_therapy_plans` (`therapyPlanId`)")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_insulin_therapy_plans_insulinTypeId` ON `insulin_therapy_plans` (`insulinTypeId`)")

        database.execSQL("CREATE TABLE IF NOT EXISTS `medication_therapy_plans` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `therapyPlanId` INTEGER NOT NULL, `medicationTypeId` INTEGER NOT NULL, `dose` TEXT NOT NULL, `reminderTimeMinutes` INTEGER, `notes` TEXT NOT NULL, FOREIGN KEY(`therapyPlanId`) REFERENCES `therapy_plans`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`medicationTypeId`) REFERENCES `medication_types`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_medication_therapy_plans_therapyPlanId` ON `medication_therapy_plans` (`therapyPlanId`)")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_medication_therapy_plans_medicationTypeId` ON `medication_therapy_plans` (`medicationTypeId`)")
    }
}