package com.example.glucodialog.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "medication_therapy_plans",
    foreignKeys = [
        ForeignKey(entity = TherapyPlan::class, parentColumns = ["id"], childColumns = ["therapyPlanId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = MedicationType::class, parentColumns = ["id"], childColumns = ["medicationTypeId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("therapyPlanId"), Index("medicationTypeId")]
)
data class MedicationTherapyPlan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val therapyPlanId: Int,
    val medicationTypeId: Int,
    val dose: String,
    val reminderTimeMinutes: Int? = null,
    val notes: String = ""
)