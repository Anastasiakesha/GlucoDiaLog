package com.example.glucodialog.domain.mappers

import com.example.glucodialog.data.relations.ActivityEntryWithType
import com.example.glucodialog.data.local.ActivityEntry as LocalEntry
import com.example.glucodialog.data.local.ActivityType as LocalType
import com.example.glucodialog.domain.model.ActivityEntry
import com.example.glucodialog.domain.model.ActivityEntryWithTypeDomain
import com.example.glucodialog.domain.model.ActivityType


fun ActivityEntryWithType.toDomain(): ActivityEntryWithTypeDomain {
    return ActivityEntryWithTypeDomain(
        entry = this.entry.toDomain(),
        type = this.type?.toDomain()
    )
}

//fun ActivityEntryWithType.toDomain(): ActivityEntryWithTypeDomain {
//    val safeType = type ?: ActivityType(
//        id = -1,
//        name = "Неизвестная активность",
//    )
//
//    return ActivityEntryWithTypeDomain(
//        entry = entry.toDomain(),
//        type = safeType as ActivityType
//    )
//}

fun ActivityEntry.toLocal(): LocalEntry {
    return LocalEntry(
        id = this.id,
        activityTypeId = this.activityTypeId,
        durationMinutes = this.durationMinutes,
        timestamp = this.timestamp
    )
}

fun LocalEntry.toDomain(): ActivityEntry {
    return ActivityEntry(
        id = this.id,
        activityTypeId = this.activityTypeId,
        durationMinutes = this.durationMinutes,
        timestamp = this.timestamp
    )
}

fun ActivityType.toLocal(): LocalType {
    return LocalType(
        id = this.id,
        name = this.name
    )
}

fun LocalType.toDomain(): ActivityType {
    return ActivityType(
        id = this.id,
        name = this.name
    )
}