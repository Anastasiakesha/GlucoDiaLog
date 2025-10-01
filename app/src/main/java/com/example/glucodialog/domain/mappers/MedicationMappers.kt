package com.example.glucodialog.domain.mappers


import com.example.glucodialog.data.local.MedicationEntry as LocalEntry
import com.example.glucodialog.data.local.MedicationType as LocalType
import com.example.glucodialog.data.relations.MedicationEntryWithType as LocalEntryWithType
import com.example.glucodialog.domain.model.MedicationEntry
import com.example.glucodialog.domain.model.MedicationEntryWithTypeDomain
import com.example.glucodialog.domain.model.MedicationType

fun LocalType.toDomain() = MedicationType(id = id, name = name)
fun MedicationType.toLocal() = LocalType(id = id, name = name)

fun LocalEntry.toDomain() = MedicationEntry(
    id = id,
    medicationTypeId = medicationTypeId,
    dose = dose,
    unit = unit,
    timestamp = timestamp
)
fun MedicationEntry.toLocal() = LocalEntry(
    id = id,
    medicationTypeId = medicationTypeId,
    dose = dose,
    unit = unit,
    timestamp = timestamp
)

fun LocalEntryWithType.toDomain() = MedicationEntryWithTypeDomain(
    entry = entry.toDomain(),
    type = type?.toDomain()
)

//fun LocalEntryWithType.toDomain(): MedicationEntryWithTypeDomain {
//    val safeType = type ?: MedicationType(
//        id = -1,
//        name = "Неизвестный",
//    )
//
//    return MedicationEntryWithTypeDomain(
//        entry = entry.toDomain(),
//        type = safeType as MedicationType
//    )
//}