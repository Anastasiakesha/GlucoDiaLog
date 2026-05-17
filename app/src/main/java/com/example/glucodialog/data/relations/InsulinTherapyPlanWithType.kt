package com.example.glucodialog.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.glucodialog.data.local.InsulinTherapyPlan
import com.example.glucodialog.data.local.InsulinType

data class InsulinTherapyPlanWithType(
    @Embedded val plan: InsulinTherapyPlan,
    @Relation(
        parentColumn = "insulinTypeId",
        entityColumn = "id"
    )
    val type: InsulinType?
)