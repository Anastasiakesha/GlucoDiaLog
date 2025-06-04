package com.example.glucodialog.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.glucodialog.data.MedicationEntry
import com.example.glucodialog.data.MedicationType

data class MedicationEntryWithType(
    @Embedded val entry: MedicationEntry,
    @Relation(
        parentColumn = "medicationTypeId",
        entityColumn = "id"
    )
    val type: MedicationType
)