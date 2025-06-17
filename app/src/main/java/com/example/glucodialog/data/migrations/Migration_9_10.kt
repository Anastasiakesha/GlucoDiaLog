package com.example.glucodialog.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Добавляем данные в insulin_types
        database.execSQL("INSERT INTO InsulinType (name, type, durationHours) VALUES ('Новорапид', 'Болюсный', 4)")
        database.execSQL("INSERT INTO InsulinType (name, type, durationHours) VALUES ('Лантус', 'Базальный', 24)")

        // Добавляем данные в medication_types
        database.execSQL("INSERT INTO MedicationType (name) VALUES ('Метформин')")
        database.execSQL("INSERT INTO MedicationType (name) VALUES ('Глюкофаж')")

        // Добавляем данные в activity_types
        database.execSQL("INSERT INTO ActivityType (name) VALUES ('Ходьба')")
        database.execSQL("INSERT INTO ActivityType (name) VALUES ('Бег')")
        database.execSQL("INSERT INTO ActivityType (name) VALUES ('Плавание')")

        // Добавляем данные в food_items
        database.execSQL(
            """
            INSERT INTO FoodItem (name, calories, proteins, fats, carbs, allowedUnits) VALUES
            ('Яблоко', 52, 0.3, 0.2, 14.0, 'г'),
            ('Куриная грудка', 165, 31.0, 3.6, 0.0, 'г'),
            ('Хлеб', 250, 8.0, 2.5, 48.0, 'г'),
            ('Молоко', 60, 3.2, 3.5, 4.7, 'г,мл')
            """
        )
    }
}
