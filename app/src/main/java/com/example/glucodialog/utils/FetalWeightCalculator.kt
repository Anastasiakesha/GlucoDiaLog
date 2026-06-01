package com.example.glucodialog.utils

import kotlin.math.pow

object FetalWeightCalculator {
    fun calculateEFW(bpd: Double, hc: Double, ac: Double, fl: Double): Double {
        val b = bpd / 10.0
        val h = hc / 10.0
        val a = ac / 10.0
        val f = fl / 10.0

        val log10Weight = 1.3596 +
                (0.0064 * h) +
                (0.0424 * a) +
                (0.1746 * f) +
                (0.00061 * b * a) -
                (0.00386 * a * f)

        return 10.0.pow(log10Weight)
    }
}