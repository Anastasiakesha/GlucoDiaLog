package com.example.glucodialog.ui.constants

sealed class ChartType {

    object Glucose : ChartType()

    object Insulin : ChartType()

    object Medication : ChartType()

    object Activity : ChartType()

    object Food : ChartType()

}