package com.example.glucodialog.ui.components

import android.text.TextPaint
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedLineChart(values: List<Float>, modifier: Modifier = Modifier) {
    if (values.isEmpty()) return

    var animationProgress by remember { mutableStateOf(0f) }
    LaunchedEffect(values) {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing)
        ) { value, _ ->
            animationProgress = value
        }
    }

    Canvas(modifier = modifier.height(200.dp)) {
        val maxValue = values.maxOrNull() ?: 1f
        val minValue = values.minOrNull() ?: 0f
        val range = (maxValue - minValue).takeIf { it != 0f } ?: 1f
        val stepX = if (values.size > 1) size.width / (values.size - 1) else size.width / 2

        val path = Path()
        values.forEachIndexed { index, value ->
            val x = index * stepX
            val y = size.height - ((value - minValue) / range) * size.height
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }


        val pathMeasure = PathMeasure()
        pathMeasure.setPath(path, false)
        val animatedPath = Path()
        pathMeasure.getSegment(0f, pathMeasure.length * animationProgress, animatedPath, true)

        drawPath(
            path = animatedPath,
            brush = Brush.horizontalGradient(
                colors = listOf(Color.Blue, Color.Green, Color(0xFFFFA500), Color.Red)
            ),
            style = Stroke(width = 4f, cap = StrokeCap.Round)
        )


        values.forEachIndexed { index, value ->
            val x = index * stepX
            val y = size.height - ((value - minValue) / range) * size.height
            if (index.toFloat() / (values.size - 1) <= animationProgress) {
                val color = when {
                    value < 4f -> Color.Blue
                    value <= 7f -> Color.Green
                    value <= 10f -> Color(0xFFFFA500)
                    else -> Color.Red
                }
                drawCircle(color = color, radius = 6f, center = Offset(x, y))


                drawIntoCanvas { canvas ->
                    val paint = TextPaint().apply {
                        this.color = android.graphics.Color.BLACK
                        textSize = 30f
                        isAntiAlias = true
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                    canvas.nativeCanvas.drawText("%.1f".format(value), x, y - 16f, paint)
                }
            }
        }
    }
}