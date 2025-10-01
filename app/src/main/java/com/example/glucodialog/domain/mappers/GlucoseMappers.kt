package com.example.glucodialog.domain.mappers

import com.example.glucodialog.data.local.GlucoseEntry as LocalEntry
import com.example.glucodialog.domain.model.GlucoseEntry

fun GlucoseEntry.toLocal(): LocalEntry {
    return LocalEntry(
        id = this.id,
        glucoseLevel = this.glucoseLevel,
        unit = this.unit,
        timestamp = this.timestamp,
        note = this.note
    )
}

fun LocalEntry.toDomain(): GlucoseEntry {
    return GlucoseEntry(
        id = this.id,
        glucoseLevel = this.glucoseLevel,
        unit = this.unit,
        timestamp = this.timestamp,
        note = this.note
    )
}
