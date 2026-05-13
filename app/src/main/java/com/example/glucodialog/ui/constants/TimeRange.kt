package com.example.glucodialog.ui.constants

sealed class TimeRange {
    object Day : TimeRange()
    object Week : TimeRange()
    object Month : TimeRange()
}