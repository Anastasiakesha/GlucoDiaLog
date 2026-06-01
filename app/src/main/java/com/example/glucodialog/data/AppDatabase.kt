package com.example.glucodialog.data

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.glucodialog.data.local.ActivityEntry
import com.example.glucodialog.data.local.ActivityType
import com.example.glucodialog.data.local.FoodEntry
import com.example.glucodialog.data.local.FoodItem
import com.example.glucodialog.data.local.GlucoseEntry
import com.example.glucodialog.data.local.InsulinEntry
import com.example.glucodialog.data.local.InsulinType
import com.example.glucodialog.data.local.MedicationEntry
import com.example.glucodialog.data.local.MedicationType
import com.example.glucodialog.data.local.UserProfile
import com.example.glucodialog.data.local.BloodPressureEntry
import com.example.glucodialog.data.local.InsulinTherapyPlan
import com.example.glucodialog.data.local.MedicationTherapyPlan
import com.example.glucodialog.data.local.TherapyPlan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.glucodialog.data.migrations.MIGRATION_1_2
import com.example.glucodialog.data.migrations.MIGRATION_2_3
import com.example.glucodialog.data.migrations.MIGRATION_3_4
import com.example.glucodialog.data.migrations.MIGRATION_4_5
import com.example.glucodialog.data.migrations.MIGRATION_5_6
import com.example.glucodialog.data.migrations.MIGRATION_6_7
import com.example.glucodialog.data.migrations.MIGRATION_7_8
import com.example.glucodialog.data.migrations.MIGRATION_8_9
import com.example.glucodialog.data.migrations.MIGRATION_9_10
import com.example.glucodialog.data.migrations.MIGRATION_10_11
import com.example.glucodialog.data.migrations.MIGRATION_11_12
import com.example.glucodialog.data.migrations.MIGRATION_12_13
import com.example.glucodialog.data.repository.ActivityDao
import com.example.glucodialog.data.repository.BloodPressureDao
import com.example.glucodialog.data.repository.FoodDao
import com.example.glucodialog.data.repository.GlucoseDao
import com.example.glucodialog.data.repository.InsulinDao
import com.example.glucodialog.data.repository.MedicationDao
import com.example.glucodialog.data.repository.TherapyPlanDao
import com.example.glucodialog.data.repository.UserProfileDao
import kotlinx.coroutines.flow.first

@Database(
    entities = [
        ActivityEntry::class, ActivityType::class,
        FoodEntry::class, FoodItem::class,
        GlucoseEntry::class,
        InsulinEntry::class, InsulinType::class,
        MedicationEntry::class, MedicationType::class,
        UserProfile::class,
        BloodPressureEntry::class,
        TherapyPlan::class,
        InsulinTherapyPlan::class,
        MedicationTherapyPlan::class
    ],
    version = 13,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun activityDao(): ActivityDao
    abstract fun foodDao(): FoodDao
    abstract fun glucoseDao(): GlucoseDao
    abstract fun insulinDao(): InsulinDao
    abstract fun medicationDao(): MedicationDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun bloodPressureDao(): BloodPressureDao
    abstract fun therapyPlanDao(): TherapyPlanDao


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
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10,
                        MIGRATION_10_11, MIGRATION_11_12, MIGRATION_12_13
                    )
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
                    FoodItem(
                        name = "Яблоко",
                        calories = 52,
                        proteins = 0.3,
                        fats = 0.2,
                        carbs = 14.0,
                        allowedUnits = "г"
                    ),
                    FoodItem(
                        name = "Куриная грудка",
                        calories = 165,
                        proteins = 31.0,
                        fats = 3.6,
                        carbs = 0.0,
                        allowedUnits = "г"
                    ),
                    FoodItem(
                        name = "Хлеб",
                        calories = 250,
                        proteins = 8.0,
                        fats = 2.5,
                        carbs = 48.0,
                        allowedUnits = "г"
                    ),
                    FoodItem(
                        name = "Молоко",
                        calories = 60,
                        proteins = 3.2,
                        fats = 3.5,
                        carbs = 4.7,
                        allowedUnits = "г,мл"
                    )
                )
            )

            // Предзаполнение инсулина
            db.insulinDao().insertAllInsulinTypes(
                listOf(
                    InsulinType(name = "Новорапид", type = "Болюсный", durationHours = 4),
                    InsulinType(name = "Лантус", type = "Базальный", durationHours = 24)
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
