package com.example.glucodialog.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "insulin_therapy_plans",
    foreignKeys = [
        ForeignKey(entity = TherapyPlan::class, parentColumns = ["id"], childColumns = ["therapyPlanId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = InsulinType::class, parentColumns = ["id"], childColumns = ["insulinTypeId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("therapyPlanId"), Index("insulinTypeId")]
)
data class InsulinTherapyPlan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val therapyPlanId: Int,
    val insulinTypeId: Int,
    val dose: Double,
    val reminderTimeMinutes: Int? = null,
    val notes: String = ""
)