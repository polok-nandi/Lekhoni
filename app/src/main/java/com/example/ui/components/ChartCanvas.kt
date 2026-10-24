package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChartData
import com.example.model.ChartType
import com.example.ui.theme.LekhoniInkPrimary
import com.example.ui.theme.NotoSansBengaliFamily

@Composable
fun ChartCanvas(
    chartData: ChartData,
    modifier: Modifier = Modifier
) {
    val barColors = listOf(
        Color(0xFF1B3B6F),
        Color(0xFF28589B),
        Color(0xFF006A4E),
        Color(0xFFD4AF37),
        Color(0xFFC0392B),
        Color(0xFF8E44AD)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clipRoundedBorder()
            .background(MaterialTheme.colorScheme.surface)
            .padding(14.dp)
    ) {
        Text(
            text = chartData.title,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = LekhoniInkPrimary,
            fontFamily = NotoSansBengaliFamily
        )
        Text(
            text = "ধরন: ${when(chartData.chartType) { ChartType.BAR -> "বার চার্ট"; ChartType.LINE -> "লাইন চার্ট"; ChartType.PIE -> "পাই চার্ট" }}",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = NotoSansBengaliFamily
        )

        Spacer(Modifier.height(12.dp))

        when (chartData.chartType) {
            ChartType.BAR -> {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                ) {
                    val w = size.width
                    val h = size.height
                    val bottomY = h - 30f
                    val maxVal = (chartData.values.maxOrNull() ?: 100f).coerceAtLeast(1f)
                    val count = chartData.labels.size.coerceAtLeast(1)
                    val slotWidth = (w - 40f) / count

                    // Draw baseline
                    drawLine(
                        color = Color.LightGray,
                        start = Offset(20f, bottomY),
                        end = Offset(w - 20f, bottomY),
                        strokeWidth = 2f
                    )

                    for (i in chartData.labels.indices) {
                        val v = chartData.values.getOrNull(i) ?: 0f
                        val barH = (v / maxVal) * (bottomY - 20f)
                        val bx = 20f + i * slotWidth + slotWidth * 0.15f
                        val bw = slotWidth * 0.7f
                        val barColor = barColors[i % barColors.size]

                        drawRect(
                            color = barColor,
                            topLeft = Offset(bx, bottomY - barH),
                            size = Size(bw, barH)
                        )

                        // Draw value and label
                        val paint = android.graphics.Paint().apply {
                            isAntiAlias = true
                            textSize = 28f
                            textAlign = android.graphics.Paint.Align.CENTER
                            this.color = android.graphics.Color.DKGRAY
                        }
                        drawContext.canvas.nativeCanvas.drawText(
                            chartData.labels[i],
                            bx + bw / 2f,
                            h - 5f,
                            paint
                        )
                        drawContext.canvas.nativeCanvas.drawText(
                            v.toInt().toString(),
                            bx + bw / 2f,
                            bottomY - barH - 8f,
                            paint
                        )
                    }
                }
            }
            ChartType.LINE -> {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                ) {
                    val w = size.width
                    val h = size.height
                    val bottomY = h - 30f
                    val maxVal = (chartData.values.maxOrNull() ?: 100f).coerceAtLeast(1f)
                    val count = chartData.labels.size.coerceAtLeast(1)
                    val slotWidth = if (count > 1) (w - 60f) / (count - 1) else 0f

                    drawLine(
                        color = Color.LightGray,
                        start = Offset(30f, bottomY),
                        end = Offset(w - 30f, bottomY),
                        strokeWidth = 2f
                    )

                    val points = mutableListOf<Offset>()
                    for (i in chartData.labels.indices) {
                        val v = chartData.values.getOrNull(i) ?: 0f
                        val py = bottomY - (v / maxVal) * (bottomY - 30f)
                        val px = 30f + i * slotWidth
                        points.add(Offset(px, py))

                        val paint = android.graphics.Paint().apply {
                            isAntiAlias = true
                            textSize = 28f
                            textAlign = android.graphics.Paint.Align.CENTER
                            color = android.graphics.Color.DKGRAY
                        }
                        drawContext.canvas.nativeCanvas.drawText(
                            chartData.labels[i],
                            px,
                            h - 5f,
                            paint
                        )
                    }

                    for (i in 0 until points.size - 1) {
                        drawLine(
                            color = LekhoniInkPrimary,
                            start = points[i],
                            end = points[i + 1],
                            strokeWidth = 4f
                        )
                    }

                    points.forEach { pt ->
                        drawCircle(color = Color(0xFFD4AF37), radius = 6f, center = pt)
                        drawCircle(color = LekhoniInkPrimary, radius = 6f, center = pt, style = Stroke(2f))
                    }
                }
            }
            ChartType.PIE -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(150.dp)) {
                        val total = chartData.values.sum().coerceAtLeast(1f)
                        var currentAngle = -90f

                        for (i in chartData.values.indices) {
                            val sweep = (chartData.values[i] / total) * 360f
                            val color = barColors[i % barColors.size]
                            drawArc(
                                color = color,
                                startAngle = currentAngle,
                                sweepAngle = sweep,
                                useCenter = true
                            )
                            currentAngle += sweep
                        }
                    }
                }
            }
        }
    }
}

private fun Modifier.clipRoundedBorder(): Modifier = this
    .border(1.dp, Color(0xFFD0D7DE), RoundedCornerShape(8.dp))
