package com.example.glucodialog.domain.mappers


import com.example.glucodialog.data.local.UserProfile as LocalUserProfile
import com.example.glucodialog.domain.model.UserProfile as DomainUserProfile

fun LocalUserProfile.toDomain(): DomainUserProfile {
    return DomainUserProfile(
        email = email,
        name = name,
        gender = gender,
        weight = weight,
        height = height,
        diabetesType = diabetesType,
        targetGlucoseLow = targetGlucoseLow,
        targetGlucoseHigh = targetGlucoseHigh,
        glucoseUnit = glucoseUnit,
        bolusInsulin = bolusInsulin,
        bolusDose = bolusDose,
        basalInsulin = basalInsulin,
        basalDose = basalDose,
        medication = medication,
        medicationDose = medicationDose,
        medicationUnit = medicationUnit,
        medicationTimeMinutesFromMidnight = medicationTimeMinutesFromMidnight
    )
}

fun DomainUserProfile.toLocal(): LocalUserProfile {
    return LocalUserProfile(
        email = email,
        name = name,
        gender = gender,
        weight = weight,
        height = height,
        diabetesType = diabetesType,
        targetGlucoseLow = targetGlucoseLow,
        targetGlucoseHigh = targetGlucoseHigh,
        glucoseUnit = glucoseUnit,
        bolusInsulin = bolusInsulin,
        bolusDose = bolusDose,
        basalInsulin = basalInsulin,
        basalDose = basalDose,
        medication = medication,
        medicationDose = medicationDose,
        medicationUnit = medicationUnit,
        medicationTimeMinutesFromMidnight = medicationTimeMinutesFromMidnight
    )
}