package com.example.glucodialog

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.glucodialog.data.AppDatabase
import com.example.glucodialog.data.GlucoseEntry
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class GlucoseStatsActivity : AppCompatActivity() {

    private lateinit var tvAverage: TextView
    private lateinit var tvDailyCount: TextView
    private lateinit var tvMinMax: TextView
    private lateinit var chart: LineChart
    private lateinit var tvHbA1c: TextView


    private val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        tvAverage = findViewById(R.id.tvAverage)
        tvDailyCount = findViewById(R.id.tvDailyCount)
        tvMinMax = findViewById(R.id.tvMinMax)
        chart = findViewById(R.id.glucoseChart)
        tvHbA1c = findViewById(R.id.tvHbA1c)

        observeGlucoseData()
    }

    private fun observeGlucoseData() {
        val db = AppDatabase.getDatabase(this)

        scope.launch {
            db.glucoseDao().getAllGlucoseEntries().collect { entries ->
                if (entries.isNotEmpty()) {
                    updateStats(entries)
                    updateChart(entries)
                }
            }
        }
    }

    private fun updateStats(entries: List<GlucoseEntry>) {
        val avg = entries.map { it.glucoseLevel }.average()
        val min = entries.minByOrNull { it.glucoseLevel }?.glucoseLevel
        val max = entries.maxByOrNull { it.glucoseLevel }?.glucoseLevel

        val measurementsPerDay = entries.groupBy {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it.timestamp
            "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.DAY_OF_YEAR)}"
        }.mapValues { it.value.size }

        val avgDaily = measurementsPerDay.values.average()

        val estimatedHbA1c = (avg + 2.52) / 1.59

        tvAverage.text = "Среднее: %.2f ммоль/л".format(avg)
        tvMinMax.text = "Мин/Макс: %.1f / %.1f".format(min ?: 0.0, max ?: 0.0)
        tvDailyCount.text = "Измерений в день: %.1f".format(avgDaily)
        tvHbA1c.text = "HbA1c: %.2f %%".format(estimatedHbA1c)
    }


    private fun updateChart(entries: List<GlucoseEntry>) {
        val sorted = entries.sortedBy { it.timestamp }

        val entriesLine = sorted.mapIndexed { index, entry ->
            Entry(index.toFloat(), entry.glucoseLevel.toFloat())
        }

        val dataSet = LineDataSet(entriesLine, "Глюкоза").apply {
            color = getColor(com.google.android.material.R.color.material_dynamic_tertiary60)
            setDrawCircles(true)
            setDrawValues(false)
            lineWidth = 2f
        }

        val lineData = LineData(dataSet)
        chart.data = lineData

        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.roundToInt()
                return if (index in sorted.indices) {
                    val date = Date(sorted[index].timestamp)
                    SimpleDateFormat("dd.MM", Locale.getDefault()).format(date)
                } else ""
            }
        }

        chart.invalidate()
    }
}
