package com.example.glucodialog.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.glucodialog.domain.model.ChartData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AnimatedLineChart(
    data: ChartData,
    modifier: Modifier = Modifier
) {
    if (data.lines.isEmpty() || data.lines.all { it.points.isEmpty() }) {
        Box(modifier = modifier.height(220.dp), contentAlignment = Alignment.Center) {
            Text("Нет данных за выбранный период", color = Color.Gray)
        }
        return
    }

    val animationProgress = remember { Animatable(0f) }

    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val dateFormatter = remember { SimpleDateFormat("dd MMM", Locale.getDefault()) }

    val textMeasurer = rememberTextMeasurer()

    LaunchedEffect(data) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1000,
                easing = LinearOutSlowInEasing
            )
        )
    }

    Column(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxWidth().height(200.dp)) {
            val allPoints = data.lines.flatMap { it.points }
            val maxValue = allPoints.maxOfOrNull { it.value } ?: 1f
            val minValue = 0f
            val valueRange = (maxValue - minValue).takeIf { it != 0f } ?: 1f

            val minTime = allPoints.minOfOrNull { it.timestamp } ?: 0L
            val maxTime = allPoints.maxOfOrNull { it.timestamp } ?: 1L
            val timeRange = (maxTime - minTime).toFloat().takeIf { it != 0f } ?: 1f

            drawLine(
                color = Color.Gray.copy(alpha = 0.3f),
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = 2f
            )

            data.lines.forEach { line ->
                val sortedPoints = line.points.sortedBy { it.timestamp }

                if (sortedPoints.size < 2) {
                    sortedPoints.firstOrNull()?.let { point ->
                        val x = if (timeRange == 1f) size.width / 2 else ((point.timestamp - minTime) / timeRange) * size.width
                        val y = size.height - ((point.value - minValue) / valueRange) * size.height

                        drawCircle(line.color, radius = 6f, center = Offset(x, y), alpha = animationProgress.value)

                        val text = String.format(Locale.getDefault(), "%.1f", point.value)
                        val textLayoutResult = textMeasurer.measure(
                            text = text,
                            style = TextStyle(color = Color.DarkGray, fontSize = 10.sp)
                        )
                        drawText(
                            textLayoutResult = textLayoutResult,
                            topLeft = Offset(x - textLayoutResult.size.width / 2, y - textLayoutResult.size.height - 10f),
                            alpha = animationProgress.value
                        )
                    }
                    return@forEach
                }

                val path = Path()
                sortedPoints.forEachIndexed { index, point ->
                    val x = ((point.timestamp - minTime) / timeRange) * size.width
                    val y = size.height - ((point.value - minValue) / valueRange) * size.height
                    if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }

                val pathMeasure = android.graphics.PathMeasure(path.asAndroidPath(), false)
                val androidPath = android.graphics.Path()
                pathMeasure.getSegment(0f, pathMeasure.length * animationProgress.value, androidPath, true)

                drawPath(
                    path = androidPath.asComposePath(),
                    color = line.color,
                    style = Stroke(width = 5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                sortedPoints.forEach { point ->
                    val x = ((point.timestamp - minTime) / timeRange) * size.width
                    val y = size.height - ((point.value - minValue) / valueRange) * size.height

                    drawCircle(color = line.color, radius = 6f, center = Offset(x, y), alpha = animationProgress.value)

                    val text = String.format(Locale.getDefault(), "%.1f", point.value)
                    val textLayoutResult = textMeasurer.measure(
                        text = text,
                        style = TextStyle(color = Color.DarkGray, fontSize = 10.sp)
                    )

                    drawText(
                        textLayoutResult = textLayoutResult,
                        topLeft = Offset(x - textLayoutResult.size.width / 2, y - textLayoutResult.size.height - 10f),
                        alpha = animationProgress.value
                    )
                }
            }
        }

        if (data.lines.isNotEmpty()) {
            val allPoints = data.lines.flatMap { it.points }
            val minTime = allPoints.minOfOrNull { it.timestamp } ?: 0L
            val maxTime = allPoints.maxOfOrNull { it.timestamp } ?: 0L

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = timeFormatter.format(Date(minTime)),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )

                if (maxTime - minTime > 24 * 60 * 60 * 1000) {
                    Text(
                        text = dateFormatter.format(Date((minTime + maxTime) / 2)),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }

                Text(
                    text = timeFormatter.format(Date(maxTime)),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            data.lines.forEach { line ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp)) {
                    Box(modifier = Modifier.size(10.dp).background(line.color, CircleShape))
                    Spacer(Modifier.width(4.dp))
                    Text(line.label, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}