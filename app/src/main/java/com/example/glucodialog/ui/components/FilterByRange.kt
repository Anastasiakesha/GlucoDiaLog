package com.example.glucodialog.ui.components

import com.example.glucodialog.domain.model.ChartPoint
import com.example.glucodialog.ui.constants.TimeRange

fun filterByRange(
    points: List<ChartPoint>,
    range: TimeRange
): List<ChartPoint> {

    val now = System.currentTimeMillis()

    val from = when (range) {
        TimeRange.Day -> now - 24 * 60 * 60 * 1000
        TimeRange.Week -> now - 7 * 24 * 60 * 60 * 1000
        TimeRange.Month -> now - 30L * 24 * 60 * 60 * 1000
    }

    return points.filter { it.timestamp >= from }
}