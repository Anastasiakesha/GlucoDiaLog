package com.example.glucodialog.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.glucodialog.data.ActivityEntry
import com.example.glucodialog.data.ActivityType

data class ActivityEntryWithType(
    @Embedded val entry: ActivityEntry,
    @Relation(
        parentColumn = "activityTypeId",
        entityColumn = "id"
    )
    val type: ActivityType
)