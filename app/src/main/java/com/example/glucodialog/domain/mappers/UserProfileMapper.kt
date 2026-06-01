package com.example.glucodialog.domain.mappers


import com.example.glucodialog.data.local.UserProfile as LocalUserProfile
import com.example.glucodialog.domain.model.UserProfile as DomainUserProfile

fun LocalUserProfile.toDomain(): DomainUserProfile {
    return DomainUserProfile(
        id = id,
        email = email,
        name = name,
        gender = gender,
        weight = weight,
        height = height,
        diabetesType = diabetesType,
        targetGlucoseLow = targetGlucoseLow,
        targetGlucoseHigh = targetGlucoseHigh,
        glucoseUnit = glucoseUnit,
        pregnancyLmpTimestamp = pregnancyLmpTimestamp
    )
}

fun DomainUserProfile.toLocal(): LocalUserProfile {
    return LocalUserProfile(
        id = id,
        email = email,
        name = name,
        gender = gender,
        weight = weight,
        height = height,
        diabetesType = diabetesType,
        targetGlucoseLow = targetGlucoseLow,
        targetGlucoseHigh = targetGlucoseHigh,
        glucoseUnit = glucoseUnit,
        pregnancyLmpTimestamp = pregnancyLmpTimestamp
    )
}