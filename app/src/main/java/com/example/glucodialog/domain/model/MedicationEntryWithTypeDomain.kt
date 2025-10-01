package com.example.glucodialog.domain.model

data class MedicationEntryWithTypeDomain(
    val entry: MedicationEntry,
    val type: MedicationType?
)