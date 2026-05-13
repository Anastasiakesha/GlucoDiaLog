package com.example.glucodialog.ui.screen

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.glucodialog.domain.model.ChartData
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedChartDialog(
    data: ChartData,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                CenterAlignedTopAppBar(
                    title = { Text("Подробный график") },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Закрыть")
                        }
                    }
                )

                if (data.lines.isEmpty() || data.lines.all { it.points.isEmpty() }) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Нет данных для отображения")
                    }
                    return@Surface
                }

                val allPoints = data.lines.flatMap { it.points }
                val maxValue = (allPoints.maxOfOrNull { it.value } ?: 1f).coerceAtLeast(1f)
                val minValue = 0f
                val valueRange = (maxValue - minValue).takeIf { it != 0f } ?: 1f

                val minTime = allPoints.minOfOrNull { it.timestamp } ?: 0L
                val maxTime = allPoints.maxOfOrNull { it.timestamp } ?: 1L
                val timeRange = (maxTime - minTime).toFloat().takeIf { it != 0f } ?: 1f

                val textPaint = Paint().apply {
                    color = android.graphics.Color.GRAY
                    textSize = 30f
                    textAlign = Paint.Align.RIGHT
                }

                Row(modifier = Modifier.fillMaxWidth().height(450.dp).padding(vertical = 16.dp)) {

                    Canvas(modifier = Modifier.width(60.dp).fillMaxHeight()) {
                        val topPadding = 40f
                        val bottomPadding = 80f
                        val usableHeight = size.height - topPadding - bottomPadding

                        drawContext.canvas.nativeCanvas.drawText(String.format("%.1f", maxValue), size.width - 10f, topPadding + 10f, textPaint)
                        drawContext.canvas.nativeCanvas.drawText(String.format("%.1f", maxValue / 2), size.width - 10f, topPadding + usableHeight / 2 + 10f, textPaint)
                        drawContext.canvas.nativeCanvas.drawText("0.0", size.width - 10f, size.height - bottomPadding + 10f, textPaint)
                    }

                    val hoursInRange = timeRange / (1000 * 60 * 60)
                    val calculatedWidth = maxOf(500.dp, (hoursInRange * 100).dp)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .horizontalScroll(rememberScrollState())
                    ) {
                        Canvas(modifier = Modifier.width(calculatedWidth).fillMaxHeight()) {
                            val topPadding = 40f
                            val bottomPadding = 80f
                            val horizontalPadding = 40f

                            val usableHeight = size.height - topPadding - bottomPadding
                            val usableWidth = size.width - (horizontalPadding * 2)

                            drawLine(Color.LightGray.copy(0.5f), Offset(0f, topPadding), Offset(size.width, topPadding))
                            drawLine(Color.LightGray.copy(0.5f), Offset(0f, topPadding + usableHeight / 2), Offset(size.width, topPadding + usableHeight / 2))
                            drawLine(Color.Gray.copy(0.8f), Offset(0f, size.height - bottomPadding), Offset(size.width, size.height - bottomPadding))

                            val timeSpan = maxTime - minTime
                            val interval = when {
                                timeSpan <= 24 * 3600000 -> 3 * 3600000L
                                timeSpan <= 7 * 24 * 3600000 -> 24 * 3600000L
                                else -> 48 * 3600000L
                            }

                            val labelFormatter = SimpleDateFormat(if (timeSpan <= 24 * 3600000) "HH:mm" else "dd.MM", Locale.getDefault())

                            var currentTick = (minTime / interval) * interval
                            while (currentTick <= maxTime + interval) {
                                if (currentTick >= minTime) {
                                    val x = horizontalPadding + ((currentTick - minTime) / timeRange) * usableWidth

                                    drawLine(Color.LightGray.copy(0.3f), Offset(x, topPadding), Offset(x, size.height - bottomPadding))

                                    drawContext.canvas.nativeCanvas.drawText(
                                        labelFormatter.format(Date(currentTick)),
                                        x, size.height - 30f,
                                        android.graphics.Paint().apply {
                                            color = android.graphics.Color.GRAY
                                            textSize = 28f
                                            textAlign = android.graphics.Paint.Align.CENTER
                                        }
                                    )
                                }
                                currentTick += interval
                            }

                            data.lines.forEach { line ->
                                val sortedPoints = line.points.sortedBy { it.timestamp }
                                if (sortedPoints.isEmpty()) return@forEach

                                val path = android.graphics.Path()
                                sortedPoints.forEachIndexed { index, point ->
                                    val x = horizontalPadding + ((point.timestamp - minTime) / timeRange) * usableWidth
                                    val y = topPadding + usableHeight - ((point.value - minValue) / valueRange) * usableHeight

                                    if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)

                                    drawCircle(line.color, radius = 6f, center = Offset(x, y))
                                }

                                drawPath(
                                    path = path.asComposePath(),
                                    color = line.color,
                                    style = Stroke(width = 5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    data.lines.forEach { line ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp)) {
                            Box(modifier = Modifier.size(12.dp).background(line.color, CircleShape))
                            Spacer(Modifier.width(6.dp))
                            Text(line.label, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}