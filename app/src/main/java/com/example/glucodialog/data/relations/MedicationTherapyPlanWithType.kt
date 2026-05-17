package com.example.glucodialog.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.glucodialog.data.local.MedicationTherapyPlan
import com.example.glucodialog.data.local.MedicationType

data class MedicationTherapyPlanWithType(
    @Embedded val plan: MedicationTherapyPlan,
    @Relation(
        parentColumn = "medicationTypeId",
        entityColumn = "id"
    )
    val type: MedicationType?
)