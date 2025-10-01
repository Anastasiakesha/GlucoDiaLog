package com.example.glucodialog.domain.mappers

import com.example.glucodialog.data.local.InsulinEntry as LocalInsulinEntry
import com.example.glucodialog.data.local.InsulinType as LocalInsulinType
import com.example.glucodialog.data.relations.InsulinEntryWithType
import com.example.glucodialog.domain.model.InsulinEntry
import com.example.glucodialog.domain.model.InsulinType
import com.example.glucodialog.domain.model.InsulinEntryWithTypeDomain

fun LocalInsulinType.toDomain(): InsulinType =
    InsulinType(id = id, name = name, type = type, durationHours = durationHours)

fun InsulinType.toLocal(): LocalInsulinType =
    LocalInsulinType(id = id, name = name, type = type, durationHours = durationHours)

fun LocalInsulinEntry.toDomain(): InsulinEntry =
    InsulinEntry(id = id, insulinTypeId = insulinTypeId, doseUnits = doseUnits, unit = unit, timestamp = timestamp)

fun InsulinEntry.toLocal(): LocalInsulinEntry =
    LocalInsulinEntry(id = id, insulinTypeId = insulinTypeId, doseUnits = doseUnits, unit = unit, timestamp = timestamp)

fun InsulinEntryWithType.toDomain(): InsulinEntryWithTypeDomain =
    InsulinEntryWithTypeDomain(
        entry = entry.toDomain(),
        type = type?.toDomain()
    )
//fun InsulinEntryWithType.toDomain(): InsulinEntryWithTypeDomain {
//    val safeType = type ?: InsulinType(
//        id = -1,
//        name = "Неизвестный",
//        type = "",
//        durationHours = 0
//    )
//
//    return InsulinEntryWithTypeDomain(
//        entry = entry.toDomain(),
//        type = safeType as InsulinType
//    )
//}