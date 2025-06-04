package com.example.glucodialog.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.glucodialog.data.InsulinEntry
import com.example.glucodialog.data.InsulinType

data class InsulinEntryWithType(
    @Embedded val entry: InsulinEntry,
    @Relation(
        parentColumn = "insulinTypeId",
        entityColumn = "id"
    )
    val type: InsulinType
)