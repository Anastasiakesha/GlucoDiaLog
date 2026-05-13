package com.example.glucodialog.domain.model

import androidx.compose.ui.graphics.Color

data class ChartLine(
    val points: List<ChartPoint>,
    val color: Color,
    val label: String

)
