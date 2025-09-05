package com.example.glucodialog.ui.constants


object Labels {

    val DIABETES_TYPE_LABELS = mapOf(
        "1type/gestational" to "СД 1",
        "2 type" to "СД 2",
        "gestational" to "ГСД"
    )

    val MEDICATION_UNITS = listOf("мг", "г", "мл", "таб", "капли", "ед")
        .associateWith { it }

    val GLUCOSE_UNITS_PROFILE = mapOf(
        "mmol" to "ммоль/л",
        "mgdl" to "мг/дл"
    )

    val GLUCOSE_UNITS = listOf("ммоль/л", "мг/дл")

    val DURATION_OPTIONS = listOf("Короткий", "Средний", "Длинный")
}
