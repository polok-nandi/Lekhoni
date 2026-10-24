package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ShapeData
import com.example.model.ShapeType
import com.example.ui.theme.LekhoniInkPrimary
import com.example.ui.theme.NotoSansBengaliFamily

@Composable
fun ShapeCanvas(
    shapeData: ShapeData,
    modifier: Modifier = Modifier
) {
    val fillColor = try { Color(android.graphics.Color.parseColor(shapeData.fillColorHex)) } catch (e: Exception) { Color(0xFFE8ECEF) }
    val strokeColor = try { Color(android.graphics.Color.parseColor(shapeData.strokeColorHex)) } catch (e: Exception) { LekhoniInkPrimary }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height

            when (shapeData.shapeType) {
                ShapeType.RECTANGLE -> {
                    drawRect(color = fillColor, size = Size(w, h))
                    drawRect(color = strokeColor, size = Size(w, h), style = Stroke(width = 3f))
                }
                ShapeType.ROUNDED_RECT -> {
                    drawRoundRect(
                        color = fillColor,
                        size = Size(w, h),
                        cornerRadius = CornerRadius(24f, 24f)
                    )
                    drawRoundRect(
                        color = strokeColor,
                        size = Size(w, h),
                        cornerRadius = CornerRadius(24f, 24f),
                        style = Stroke(width = 3f)
                    )
                }
                ShapeType.CIRCLE -> {
                    drawOval(color = fillColor, size = Size(w, h))
                    drawOval(color = strokeColor, size = Size(w, h), style = Stroke(width = 3f))
                }
                ShapeType.ARROW -> {
                    val path = Path().apply {
                        moveTo(10f, h * 0.3f)
                        lineTo(w - 60f, h * 0.3f)
                        lineTo(w - 60f, 10f)
                        lineTo(w - 10f, h * 0.5f)
                        lineTo(w - 60f, h - 10f)
                        lineTo(w - 60f, h * 0.7f)
                        lineTo(10f, h * 0.7f)
                        close()
                    }
                    drawPath(path, color = fillColor, style = Fill)
                    drawPath(path, color = strokeColor, style = Stroke(width = 3f))
                }
                ShapeType.CALLOUT -> {
                    val path = Path().apply {
                        moveTo(10f, 10f)
                        lineTo(w - 10f, 10f)
                        lineTo(w - 10f, h - 30f)
                        lineTo(60f, h - 30f)
                        lineTo(30f, h - 5f)
                        lineTo(40f, h - 30f)
                        lineTo(10f, h - 30f)
                        close()
                    }
                    drawPath(path, color = fillColor, style = Fill)
                    drawPath(path, color = strokeColor, style = Stroke(width = 3f))
                }
                ShapeType.STAR -> {
                    val cx = w / 2f
                    val cy = h / 2f
                    val outerR = minOf(w, h) / 2f - 10f
                    val innerR = outerR * 0.5f
                    val path = Path()

                    for (i in 0 until 10) {
                        val angle = (i * 36 - 90) * (Math.PI / 180.0)
                        val r = if (i % 2 == 0) outerR else innerR
                        val x = cx + (r * Math.cos(angle)).toFloat()
                        val y = cy + (r * Math.sin(angle)).toFloat()
                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }
                    path.close()
                    drawPath(path, color = fillColor, style = Fill)
                    drawPath(path, color = strokeColor, style = Stroke(width = 3f))
                }
            }
        }

        Text(
            text = shapeData.text,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = LekhoniInkPrimary,
            fontFamily = NotoSansBengaliFamily
        )
    }
}
