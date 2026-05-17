package com.example.glucodialog.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.glucodialog.data.local.InsulinTherapyPlan
import com.example.glucodialog.data.local.MedicationTherapyPlan
import com.example.glucodialog.data.local.TherapyPlan

data class TherapyPlanWithDetails(
    @Embedded val plan: TherapyPlan,

    @Relation(
        entity = InsulinTherapyPlan::class,
        parentColumn = "id",
        entityColumn = "therapyPlanId"
    )
    val insulinPlans: List<InsulinTherapyPlanWithType>,

    @Relation(
        entity = MedicationTherapyPlan::class,
        parentColumn = "id",
        entityColumn = "therapyPlanId"
    )
    val medicationPlans: List<MedicationTherapyPlanWithType>
)