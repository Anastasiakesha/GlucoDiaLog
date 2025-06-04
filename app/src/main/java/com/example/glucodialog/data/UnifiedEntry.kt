package com.example.glucodialog.data

sealed class UnifiedEntry {
    abstract fun getTimestamp(): Long

        data class Glucose(val entry: GlucoseEntry) : UnifiedEntry() {
            override fun getTimestamp() = entry.timestamp
        }

        data class Food(val entry: FoodEntry, val itemName: String) : UnifiedEntry() {
            override fun getTimestamp() = entry.timestamp
        }

        data class Insulin(val entry: InsulinEntry, val typeName: String) : UnifiedEntry() {
            override fun getTimestamp() = entry.timestamp
        }

        data class Activity(val entry: ActivityEntry, val activityName: String) : UnifiedEntry() {
            override fun getTimestamp() = entry.timestamp
        }

        data class Medication(val entry: MedicationEntry, val medicationName: String) :
            UnifiedEntry() {
            override fun getTimestamp() = entry.timestamp
        }
        data class DateHeader(val date: String) : UnifiedEntry() {
            override fun getTimestamp(): Long {
                return Long.MIN_VALUE
        }
    }
}

