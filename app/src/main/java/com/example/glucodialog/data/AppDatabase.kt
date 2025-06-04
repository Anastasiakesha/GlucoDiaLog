package com.example.glucodialog.data

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.glucodialog.data.migrations.MIGRATION_1_2

@Database(
    entities = [
        ActivityEntry::class, ActivityType::class,
        FoodEntry::class, FoodItem::class,
        GlucoseEntry::class,
        InsulinEntry::class, InsulinType::class,
        MedicationEntry::class, MedicationType::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun activityDao(): ActivityDao
    abstract fun foodDao(): FoodDao
    abstract fun glucoseDao(): GlucoseDao
    abstract fun insulinDao(): InsulinDao
    abstract fun medicationDao(): MedicationDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gluco_database"
                )
//                  .fallbackToDestructiveMigration(true)
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(AppDatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Предзаполнение
            CoroutineScope(Dispatchers.IO).launch {
                INSTANCE?.let { database ->
                    preloadData(database)
                }
            }
        }

        private suspend fun preloadData(db: AppDatabase) {
            // Предзаполнение продуктов
            db.foodDao().insertAllFoodItems(
                listOf(
                    FoodItem(name = "Яблоко", calories = 52, proteins = 0.3, fats = 0.2, carbs = 14.0),
                    FoodItem(name = "Куриная грудка", calories = 165, proteins = 31.0, fats = 3.6, carbs = 0.0),
                    FoodItem(name = "Хлеб", calories = 250, proteins = 8.0, fats = 2.5, carbs = 48.0)
                )
            )

            // Предзаполнение инсулина
            db.insulinDao().insertAllInsulinTypes(
                listOf(
                    InsulinType(name = "Новорапид", type = "Быстродействующий", durationHours = 4),
                    InsulinType(name = "Лантус", type = "Длительного действия", durationHours = 24)
                )
            )

            // Предзаполнение лекарств
            db.medicationDao().insertAllMedicationTypes(
                listOf(
                    MedicationType(name = "Метформин"),
                    MedicationType(name = "Глюкофаж")
                )
            )

            // Предзаполнение активности
            db.activityDao().insertAllActivityTypes(
                listOf(
                    ActivityType(name = "Ходьба"),
                    ActivityType(name = "Бег"),
                    ActivityType(name = "Плавание")
                )
            )
        }
    }
}
