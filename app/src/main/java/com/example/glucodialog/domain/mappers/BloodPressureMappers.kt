package com.example.glucodialog.domain.mappers

import com.example.glucodialog.data.local.BloodPressureEntry as LocalEntry
import com.example.glucodialog.domain.model.BloodPressureEntry as DomainEntry

fun DomainEntry.toLocal() = LocalEntry(
    id = id, systolic = systolic, diastolic = diastolic, pulse = pulse, timestamp = timestamp
)

fun LocalEntry.toDomain() = DomainEntry(
    id = id, systolic = systolic, diastolic = diastolic, pulse = pulse, timestamp = timestamp
)