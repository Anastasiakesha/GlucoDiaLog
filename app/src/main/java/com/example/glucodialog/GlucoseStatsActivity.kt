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
    private lateinit var tvMessage: TextView

    private val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        tvAverage = findViewById(R.id.tvAverage)
        tvDailyCount = findViewById(R.id.tvDailyCount)
        tvMinMax = findViewById(R.id.tvMinMax)
        chart = findViewById(R.id.glucoseChart)
        tvHbA1c = findViewById(R.id.tvHbA1c)
        tvMessage = findViewById(R.id.tvMessage)

        observeGlucoseData()
    }

    private fun observeGlucoseData() {
        val db = AppDatabase.getDatabase(this)

        scope.launch {
            db.glucoseDao().getAllGlucoseEntries().collect { entries ->
                runOnUiThread {
                    when {
                        entries.isEmpty() -> {
                            showMessage("Нет записей о глюкозе")
                        }

                        entries.size < 3 -> {
                            showMessage("Недостаточно данных для отображения статистики (минимум 3 записи)")
                        }

                        else -> {
                            // Достаточно данных
                            tvMessage.visibility = TextView.GONE
                            chart.visibility = LineChart.VISIBLE
                            tvAverage.visibility = TextView.VISIBLE
                            tvMinMax.visibility = TextView.VISIBLE
                            tvDailyCount.visibility = TextView.VISIBLE
                            tvHbA1c.visibility = TextView.VISIBLE

                            updateStats(entries)
                            updateChart(entries)
                        }
                    }
                }
            }
        }
    }

    private fun showMessage(message: String) {
        tvMessage.text = message
        tvMessage.visibility = TextView.VISIBLE

        chart.visibility = LineChart.GONE
        tvAverage.visibility = TextView.GONE
        tvMinMax.visibility = TextView.GONE
        tvDailyCount.visibility = TextView.GONE
        tvHbA1c.visibility = TextView.GONE
    }

    private fun normalize(entry: GlucoseEntry): Double {
        return if (entry.unit == "мг/дл") entry.glucoseLevel / 18.0 else entry.glucoseLevel
    }

    private fun updateStats(entries: List<GlucoseEntry>) {
        val mmolEntries = entries.map { normalize(it) }

        val avg = mmolEntries.average()
        val min = mmolEntries.minOrNull()
        val max = mmolEntries.maxOrNull()

        val measurementsPerDay = entries.groupBy {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it.timestamp
            "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.DAY_OF_YEAR)}"
        }.mapValues { it.value.size }

        val avgDaily = measurementsPerDay.values.average()
        val estimatedHbA1c = (avg + 2.59) / 1.59  // Формула в ммоль/л

        tvAverage.text = "Средняя: %.2f ммоль/л".format(avg)
        tvMinMax.text = "Мин/Макс: %.1f / %.1f".format(min ?: 0.0, max ?: 0.0)
        tvDailyCount.text = "Измерений в день: %.1f".format(avgDaily)
        tvHbA1c.text = "HbA1c: %.2f %%".format(estimatedHbA1c)
    }

    private fun updateChart(entries: List<GlucoseEntry>) {
        val sorted = entries.sortedBy { it.timestamp }

        val entriesLine = sorted.mapIndexed { index, entry ->
            val valueMmol = normalize(entry).toFloat()
            Entry(index.toFloat(), valueMmol)
        }

        val dataSet = LineDataSet(entriesLine, "Глюкоза (ммоль/л)").apply {
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
        chart.description.isEnabled = false

        chart.invalidate()
    }
}
