package com.example.export

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.res.ResourcesCompat
import com.example.R
import com.example.model.BlockType
import com.example.model.ChartType
import com.example.model.LekhoniDocument
import com.example.model.Orientation
import com.example.model.ShapeType
import com.example.model.TextAlignOption
import java.io.File
import java.io.FileOutputStream

object PdfExporter {

    fun generatePdf(context: Context, document: LekhoniDocument): File {
        val pdfDocument = PdfDocument()

        val paperSize = document.pageSettings.paperSize
        val orientation = document.pageSettings.orientation
        val margin = document.pageSettings.marginOption

        val pageWidth = if (orientation == Orientation.PORTRAIT) paperSize.widthPt else paperSize.heightPt
        val pageHeight = if (orientation == Orientation.PORTRAIT) paperSize.heightPt else paperSize.widthPt

        val marginLeft = margin.leftPt.toFloat()
        val marginRight = margin.rightPt.toFloat()
        val marginTop = margin.topPt.toFloat()
        val marginBottom = margin.bottomPt.toFloat()

        val contentWidth = pageWidth - marginLeft - marginRight
        val usableHeight = pageHeight - marginTop - marginBottom

        // Fonts
        val sansTypeface = try {
            ResourcesCompat.getFont(context, R.font.noto_sans_bengali) ?: Typeface.SANS_SERIF
        } catch (e: Exception) {
            Typeface.SANS_SERIF
        }
        val serifTypeface = try {
            ResourcesCompat.getFont(context, R.font.noto_serif_bengali) ?: Typeface.SERIF
        } catch (e: Exception) {
            Typeface.SERIF
        }

        // Paints
        val textPaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(27, 59, 111)
            typeface = sansTypeface
            textSize = 12f
        }

        val headerFooterPaint = Paint().apply {
            isAntiAlias = true
            color = Color.DKGRAY
            typeface = sansTypeface
            textSize = 9f
        }

        val linePaint = Paint().apply {
            isAntiAlias = true
            color = Color.LTGRAY
            strokeWidth = 1f
        }

        val borderPaint = Paint().apply {
            isAntiAlias = true
            color = Color.GRAY
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
        }

        var currentPageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        fun drawHeaderAndFooter(c: Canvas, pageNum: Int) {
            // Watermark if present
            if (!document.pageSettings.watermark.isNullOrBlank()) {
                val wmPaint = Paint().apply {
                    isAntiAlias = true
                    color = Color.argb(30, 150, 150, 150)
                    textSize = 42f
                    typeface = sansTypeface
                    textAlign = Paint.Align.CENTER
                }
                c.save()
                c.rotate(-30f, pageWidth / 2f, pageHeight / 2f)
                c.drawText(document.pageSettings.watermark, pageWidth / 2f, pageHeight / 2f, wmPaint)
                c.restore()
            }

            // Page Border
            if (document.pageSettings.showBorder) {
                c.drawRect(marginLeft - 10f, marginTop - 15f, pageWidth - marginRight + 10f, pageHeight - marginBottom + 15f, borderPaint)
            }

            // Header
            if (document.header.isEnabled && document.header.text.isNotBlank()) {
                headerFooterPaint.textAlign = Paint.Align.LEFT
                c.drawText(document.header.text, marginLeft, marginTop - 20f, headerFooterPaint)
                c.drawLine(marginLeft, marginTop - 14f, pageWidth - marginRight, marginTop - 14f, linePaint)
            }

            // Footer
            if (document.footer.isEnabled) {
                val footerY = pageHeight - marginBottom + 25f
                c.drawLine(marginLeft, footerY - 12f, pageWidth - marginRight, footerY - 12f, linePaint)
                if (document.footer.text.isNotBlank()) {
                    headerFooterPaint.textAlign = Paint.Align.LEFT
                    c.drawText(document.footer.text, marginLeft, footerY, headerFooterPaint)
                }
                if (document.footer.showPageNumber) {
                    headerFooterPaint.textAlign = Paint.Align.RIGHT
                    c.drawText("পৃষ্ঠা $pageNum", pageWidth - marginRight, footerY, headerFooterPaint)
                }
            }
        }

        drawHeaderAndFooter(canvas, currentPageNumber)

        var currentY = marginTop + 15f

        fun startNewPage() {
            pdfDocument.finishPage(page)
            currentPageNumber++
            pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNumber).create()
            page = pdfDocument.startPage(pageInfo)
            canvas = page.canvas
            drawHeaderAndFooter(canvas, currentPageNumber)
            currentY = marginTop + 15f
        }

        // Draw Document Title at top of first page
        val titlePaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(27, 59, 111)
            typeface = serifTypeface
            textSize = 20f
            isFakeBoldText = true
        }
        canvas.drawText(document.title, marginLeft, currentY, titlePaint)
        currentY += 28f
        canvas.drawLine(marginLeft, currentY - 8f, pageWidth - marginRight, currentY - 8f, linePaint)
        currentY += 15f

        // Draw Blocks
        for (block in document.blocks) {
            when (block.type) {
                BlockType.PAGE_BREAK -> {
                    startNewPage()
                }
                BlockType.DIVIDER -> {
                    if (currentY + 20f > pageHeight - marginBottom) startNewPage()
                    canvas.drawLine(marginLeft, currentY + 5f, pageWidth - marginRight, currentY + 5f, linePaint)
                    currentY += 20f
                }
                BlockType.HEADING_1, BlockType.HEADING_2, BlockType.HEADING_3 -> {
                    val hSize = when (block.type) {
                        BlockType.HEADING_1 -> 18f
                        BlockType.HEADING_2 -> 15f
                        else -> 13f
                    }
                    val headingPaint = Paint().apply {
                        isAntiAlias = true
                        color = Color.rgb(27, 59, 111)
                        typeface = if (block.formatting.fontFamily.contains("Serif", true)) serifTypeface else sansTypeface
                        textSize = hSize
                        isFakeBoldText = true
                    }
                    if (currentY + hSize + 15f > pageHeight - marginBottom) startNewPage()
                    canvas.drawText(block.text, marginLeft, currentY + hSize, headingPaint)
                    currentY += hSize + 18f
                }
                BlockType.PARAGRAPH, BlockType.BULLET_LIST, BlockType.NUMBERED_LIST, BlockType.BLOCKQUOTE -> {
                    val pSize = block.formatting.fontSizeSp * 0.75f // Scale from sp to pt
                    val pPaint = Paint().apply {
                        isAntiAlias = true
                        color = try {
                            Color.parseColor(block.formatting.textColorHex)
                        } catch (e: Exception) {
                            Color.rgb(30, 30, 30)
                        }
                        typeface = if (block.formatting.fontFamily.contains("Serif", true)) serifTypeface else sansTypeface
                        textSize = pSize
                        isFakeBoldText = block.formatting.bold
                    }

                    val prefix = when (block.type) {
                        BlockType.BULLET_LIST -> "• "
                        BlockType.NUMBERED_LIST -> "1. "
                        BlockType.BLOCKQUOTE -> "❝ "
                        else -> ""
                    }
                    val fullText = prefix + block.text
                    val indent = if (block.type == BlockType.BLOCKQUOTE) 20f else 0f
                    val effectiveWidth = contentWidth - indent

                    // Word wrap lines
                    val words = fullText.split(" ")
                    var line = ""
                    val lines = mutableListOf<String>()

                    for (word in words) {
                        val testLine = if (line.isEmpty()) word else "$line $word"
                        val bounds = Rect()
                        pPaint.getTextBounds(testLine, 0, testLine.length, bounds)
                        if (bounds.width() > effectiveWidth && line.isNotEmpty()) {
                            lines.add(line)
                            line = word
                        } else {
                            line = testLine
                        }
                    }
                    if (line.isNotEmpty()) lines.add(line)

                    val lineHeight = pSize * block.formatting.lineSpacingMultiplier * 1.3f

                    if (block.type == BlockType.BLOCKQUOTE) {
                        // draw quote bar
                        val quoteHeight = lines.size * lineHeight
                        if (currentY + quoteHeight > pageHeight - marginBottom) startNewPage()
                        val barPaint = Paint().apply {
                            color = Color.rgb(27, 59, 111)
                            strokeWidth = 3f
                        }
                        canvas.drawLine(marginLeft + 4f, currentY, marginLeft + 4f, currentY + quoteHeight, barPaint)
                    }

                    for (l in lines) {
                        if (currentY + lineHeight > pageHeight - marginBottom) {
                            startNewPage()
                        }
                        val xPos = when (block.formatting.alignment) {
                            TextAlignOption.CENTER -> marginLeft + indent + (effectiveWidth - pPaint.measureText(l)) / 2f
                            TextAlignOption.RIGHT -> marginLeft + indent + (effectiveWidth - pPaint.measureText(l))
                            else -> marginLeft + indent
                        }
                        canvas.drawText(l, xPos, currentY + pSize, pPaint)
                        currentY += lineHeight
                    }
                    currentY += 6f
                }
                BlockType.TABLE -> {
                    val table = block.tableData
                    if (table != null && table.rows.isNotEmpty()) {
                        val colWidth = contentWidth / table.colCount
                        val rowHeight = 22f

                        val tPaint = Paint().apply {
                            isAntiAlias = true
                            color = Color.BLACK
                            typeface = sansTypeface
                            textSize = 10f
                        }
                        val tLinePaint = Paint().apply {
                            color = Color.GRAY
                            strokeWidth = 1f
                            style = Paint.Style.STROKE
                        }
                        val headerBgPaint = Paint().apply {
                            color = Color.rgb(235, 240, 248)
                            style = Paint.Style.FILL
                        }

                        for ((rIndex, row) in table.rows.withIndex()) {
                            if (currentY + rowHeight > pageHeight - marginBottom) {
                                startNewPage()
                            }
                            val isHeader = (rIndex == 0)
                            if (isHeader) {
                                canvas.drawRect(marginLeft, currentY, marginLeft + contentWidth, currentY + rowHeight, headerBgPaint)
                            }

                            for (cIndex in 0 until minOf(row.size, table.colCount)) {
                                val cellX = marginLeft + cIndex * colWidth
                                canvas.drawRect(cellX, currentY, cellX + colWidth, currentY + rowHeight, tLinePaint)

                                val cellText = row[cIndex]
                                val truncated = if (tPaint.measureText(cellText) > colWidth - 8f) {
                                    cellText.take(12) + "…"
                                } else {
                                    cellText
                                }
                                if (isHeader) tPaint.isFakeBoldText = true else tPaint.isFakeBoldText = false
                                canvas.drawText(truncated, cellX + 4f, currentY + 15f, tPaint)
                            }
                            currentY += rowHeight
                        }
                        currentY += 12f
                    }
                }
                BlockType.CHART -> {
                    val chart = block.chartData
                    if (chart != null) {
                        val chartHeight = 120f
                        if (currentY + chartHeight + 20f > pageHeight - marginBottom) {
                            startNewPage()
                        }
                        val chartBoxPaint = Paint().apply {
                            color = Color.rgb(248, 250, 252)
                            style = Paint.Style.FILL
                        }
                        canvas.drawRect(marginLeft, currentY, marginLeft + contentWidth, currentY + chartHeight, chartBoxPaint)

                        val titleP = Paint().apply {
                            isAntiAlias = true
                            color = Color.rgb(27, 59, 111)
                            typeface = sansTypeface
                            textSize = 11f
                            isFakeBoldText = true
                        }
                        canvas.drawText("${chart.title} [${chart.chartType.name}]", marginLeft + 10f, currentY + 18f, titleP)

                        val barBaseY = currentY + chartHeight - 25f
                        val maxVal = (chart.values.maxOrNull() ?: 100f).coerceAtLeast(1f)
                        val slotWidth = (contentWidth - 40f) / chart.labels.size.coerceAtLeast(1)

                        val barPaint = Paint().apply {
                            style = Paint.Style.FILL
                            color = Color.rgb(27, 85, 205)
                        }
                        val labelPaint = Paint().apply {
                            isAntiAlias = true
                            color = Color.DKGRAY
                            typeface = sansTypeface
                            textSize = 9f
                            textAlign = Paint.Align.CENTER
                        }

                        for (i in chart.labels.indices) {
                            val v = chart.values.getOrNull(i) ?: 0f
                            val h = (v / maxVal) * 60f
                            val bx = marginLeft + 20f + i * slotWidth + slotWidth * 0.15f
                            val bw = slotWidth * 0.7f
                            canvas.drawRect(bx, barBaseY - h, bx + bw, barBaseY, barPaint)
                            canvas.drawText(chart.labels[i], bx + bw / 2f, barBaseY + 14f, labelPaint)
                        }

                        currentY += chartHeight + 14f
                    }
                }
                BlockType.SHAPE -> {
                    val shape = block.shapeData
                    if (shape != null) {
                        val shapeH = 40f
                        if (currentY + shapeH + 10f > pageHeight - marginBottom) startNewPage()
                        val sFill = Paint().apply {
                            color = try { Color.parseColor(shape.fillColorHex) } catch (e: Exception) { Color.LTGRAY }
                            style = Paint.Style.FILL
                        }
                        val sStroke = Paint().apply {
                            color = try { Color.parseColor(shape.strokeColorHex) } catch (e: Exception) { Color.rgb(27, 59, 111) }
                            style = Paint.Style.STROKE
                            strokeWidth = 2f
                        }
                        val sText = Paint().apply {
                            isAntiAlias = true
                            color = Color.DKGRAY
                            typeface = sansTypeface
                            textSize = 12f
                            textAlign = Paint.Align.CENTER
                        }

                        val shapeW = 200f
                        val left = marginLeft + (contentWidth - shapeW) / 2f
                        canvas.drawRoundRect(left, currentY, left + shapeW, currentY + shapeH, 8f, 8f, sFill)
                        canvas.drawRoundRect(left, currentY, left + shapeW, currentY + shapeH, 8f, 8f, sStroke)
                        canvas.drawText(shape.text, left + shapeW / 2f, currentY + 25f, sText)

                        currentY += shapeH + 14f
                    }
                }
                BlockType.IMAGE -> {
                    val img = block.imageData
                    if (img != null) {
                        val imgH = 80f
                        if (currentY + imgH + 20f > pageHeight - marginBottom) startNewPage()
                        val iPaint = Paint().apply {
                            color = Color.rgb(238, 242, 246)
                            style = Paint.Style.FILL
                        }
                        canvas.drawRect(marginLeft, currentY, marginLeft + contentWidth, currentY + imgH, iPaint)
                        val captionP = Paint().apply {
                            isAntiAlias = true
                            color = Color.GRAY
                            typeface = sansTypeface
                            textSize = 10f
                            textAlign = Paint.Align.CENTER
                        }
                        canvas.drawText("[ছবি: ${img.caption.ifBlank { "সংযুক্তি" }}]", marginLeft + contentWidth / 2f, currentY + imgH / 2f + 4f, captionP)
                        currentY += imgH + 12f
                    }
                }
            }
        }

        pdfDocument.finishPage(page)

        // Save file
        val outputDir = context.getExternalFilesDir(null) ?: context.cacheDir
        val safeTitle = document.title.replace("[^a-zA-Z0-9\\u0980-\\u09FF._-]".toRegex(), "_")
        val outputFile = File(outputDir, "${safeTitle}.pdf")
        val outStream = FileOutputStream(outputFile)
        pdfDocument.writeTo(outStream)
        outStream.flush()
        outStream.close()
        pdfDocument.close()

        return outputFile
    }
}
