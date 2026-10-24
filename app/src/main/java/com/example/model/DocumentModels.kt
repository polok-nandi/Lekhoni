package com.example.model

import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

enum class PaperSize(val displayName: String, val bengaliName: String, val widthPt: Int, val heightPt: Int) {
    A4("A4 (210 × 297 mm)", "এ৪ (২১০ × ২৯৭ মিমি)", 595, 842),
    LETTER("Letter (8.5 × 11 in)", "লেটার (৮.৫ × ১১ ইঞ্চি)", 612, 792),
    A5("A5 (148 × 210 mm)", "এ৫ (১৪৮ × ২১০ মিমি)", 420, 595),
    LEGAL("Legal (8.5 × 14 in)", "লিগ্যাল (৮.৫ × ১৪ ইঞ্চি)", 612, 1008),
    A3("A3 (297 × 420 mm)", "এ৩ (২৯৭ × ৪২০ মিমি)", 842, 1191)
}

enum class Orientation(val displayName: String, val bengaliName: String) {
    PORTRAIT("Portrait", "উলম্ব (পোর্ট্রেট)"),
    LANDSCAPE("Landscape", "অনুভূমিক (ল্যান্ডস্কেপ)")
}

enum class MarginOption(val displayName: String, val bengaliName: String, val topPt: Int, val bottomPt: Int, val leftPt: Int, val rightPt: Int) {
    NORMAL("Normal (1.0 in)", "স্বাভাবিক (১ ইঞ্চি)", 72, 72, 72, 72),
    NARROW("Narrow (0.5 in)", "সংকীর্ণ (০.৫ ইঞ্চি)", 36, 36, 36, 36),
    MODERATE("Moderate (0.75 in)", "পরিমিত (০.৭৫ ইঞ্চি)", 54, 54, 54, 54),
    WIDE("Wide (1.5 in)", "প্রশস্ত (১.৫ ইঞ্চি)", 72, 72, 108, 108)
}

enum class TextAlignOption {
    LEFT, CENTER, RIGHT, JUSTIFY
}

enum class BlockType(val displayName: String, val bengaliName: String) {
    PARAGRAPH("Paragraph", "অনুচ্ছেদ"),
    HEADING_1("Heading 1", "শিরোনাম ১"),
    HEADING_2("Heading 2", "শিরোনাম ২"),
    HEADING_3("Heading 3", "শিরোনাম ৩"),
    BULLET_LIST("Bullet List", "চিহ্নিত তালিকা"),
    NUMBERED_LIST("Numbered List", "সংখ্যাযুক্ত তালিকা"),
    BLOCKQUOTE("Quote", "উদ্ধৃতি"),
    TABLE("Table", "ছক / টেবিল"),
    IMAGE("Image", "ছবি"),
    SHAPE("Shape", "আকৃতি / শেপ"),
    CHART("Chart", "চিত্র / চার্ট"),
    DIVIDER("Divider", "বিভাজক রেখা"),
    PAGE_BREAK("Page Break", "পৃষ্ঠা বিরতি")
}

data class BlockFormatting(
    val bold: Boolean = false,
    val italic: Boolean = false,
    val underline: Boolean = false,
    val strikethrough: Boolean = false,
    val textColorHex: String = "#1B3B6F",
    val highlightColorHex: String = "#00000000",
    val alignment: TextAlignOption = TextAlignOption.LEFT,
    val fontSizeSp: Float = 16f,
    val fontFamily: String = "Noto Sans Bengali",
    val lineSpacingMultiplier: Float = 1.25f
)

data class TableData(
    val rows: List<List<String>> = listOf(
        listOf("বিবরণ (Item)", "পরিমাণ (Qty)", "মূল্য (Price)")
    ),
    val headers: List<String>? = listOf("বিবরণ (Item)", "পরিমাণ (Qty)", "মূল্য (Price)"),
    val colCount: Int = 3
)

data class ImageData(
    val uriOrUrl: String = "",
    val caption: String = "",
    val widthPercent: Float = 0.85f
)

enum class ShapeType {
    RECTANGLE, ROUNDED_RECT, CIRCLE, ARROW, CALLOUT, STAR
}

data class ShapeData(
    val shapeType: ShapeType = ShapeType.ROUNDED_RECT,
    val text: String = "লেখনী",
    val fillColorHex: String = "#E8ECEF",
    val strokeColorHex: String = "#1B3B6F"
)

enum class ChartType {
    BAR, LINE, PIE
}

data class ChartData(
    val chartType: ChartType = ChartType.BAR,
    val title: String = "মাসিক প্রতিবেদন",
    val labels: List<String> = listOf("জানু", "ফেব্রু", "মার্চ", "এপ্রিল"),
    val values: List<Float> = listOf(45f, 65f, 80f, 95f)
)

data class DocumentBlock(
    val id: String = UUID.randomUUID().toString(),
    val type: BlockType = BlockType.PARAGRAPH,
    val text: String = "",
    val formatting: BlockFormatting = BlockFormatting(),
    val tableData: TableData? = null,
    val imageData: ImageData? = null,
    val shapeData: ShapeData? = null,
    val chartData: ChartData? = null
)

data class HeaderFooterConfig(
    val isEnabled: Boolean = true,
    val text: String = "",
    val showPageNumber: Boolean = true
)

data class PageSettings(
    val paperSize: PaperSize = PaperSize.A4,
    val orientation: Orientation = Orientation.PORTRAIT,
    val marginOption: MarginOption = MarginOption.NORMAL,
    val columns: Int = 1,
    val showBorder: Boolean = false,
    val watermark: String? = null
)

data class DocumentComment(
    val id: String = UUID.randomUUID().toString(),
    val blockId: String,
    val author: String = "Polok Nandi",
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isResolved: Boolean = false
)

data class LekhoniDocument(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "নতুন নথি (Untitled)",
    val pageSettings: PageSettings = PageSettings(),
    val header: HeaderFooterConfig = HeaderFooterConfig(isEnabled = true, text = "লেখনী - মুক্ত নথি"),
    val footer: HeaderFooterConfig = HeaderFooterConfig(isEnabled = true, text = "Lekhoni Open Office", showPageNumber = true),
    val blocks: List<DocumentBlock> = listOf(DocumentBlock(type = BlockType.PARAGRAPH, text = "")),
    val comments: List<DocumentComment> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isStarred: Boolean = false
) {
    fun toPlainText(): String {
        val sb = StringBuilder()
        sb.append(title).append("\n\n")
        for (block in blocks) {
            when (block.type) {
                BlockType.PARAGRAPH, BlockType.HEADING_1, BlockType.HEADING_2, BlockType.HEADING_3,
                BlockType.BULLET_LIST, BlockType.NUMBERED_LIST, BlockType.BLOCKQUOTE -> {
                    sb.append(block.text).append("\n\n")
                }
                BlockType.TABLE -> {
                    block.tableData?.rows?.forEach { row ->
                        sb.append(row.joinToString(" | ")).append("\n")
                    }
                    sb.append("\n")
                }
                BlockType.CHART -> {
                    sb.append("[চার্ট: ${block.chartData?.title}]\n\n")
                }
                BlockType.SHAPE -> {
                    sb.append("[শেপ: ${block.shapeData?.text}]\n\n")
                }
                BlockType.IMAGE -> {
                    sb.append("[ছবি: ${block.imageData?.caption}]\n\n")
                }
                BlockType.DIVIDER -> sb.append("---\n\n")
                BlockType.PAGE_BREAK -> sb.append("\n[--- পৃষ্ঠা বিরতি ---]\n\n")
            }
        }
        return sb.toString()
    }

    fun wordCount(): Int {
        val text = toPlainText()
        if (text.isBlank()) return 0
        return text.trim().split("\\s+".toRegex()).size
    }

    fun charCount(): Int {
        return toPlainText().length
    }

    fun paragraphCount(): Int {
        return blocks.count { it.type in listOf(BlockType.PARAGRAPH, BlockType.BLOCKQUOTE, BlockType.BULLET_LIST, BlockType.NUMBERED_LIST) }
    }

    fun toJson(): String {
        val root = JSONObject()
        root.put("id", id)
        root.put("title", title)
        root.put("createdAt", createdAt)
        root.put("updatedAt", updatedAt)
        root.put("isStarred", isStarred)

        // Page settings
        val pageObj = JSONObject()
        pageObj.put("paperSize", pageSettings.paperSize.name)
        pageObj.put("orientation", pageSettings.orientation.name)
        pageObj.put("marginOption", pageSettings.marginOption.name)
        pageObj.put("columns", pageSettings.columns)
        pageObj.put("showBorder", pageSettings.showBorder)
        pageObj.put("watermark", pageSettings.watermark ?: "")
        root.put("pageSettings", pageObj)

        // Header / Footer
        val headerObj = JSONObject()
        headerObj.put("isEnabled", header.isEnabled)
        headerObj.put("text", header.text)
        headerObj.put("showPageNumber", header.showPageNumber)
        root.put("header", headerObj)

        val footerObj = JSONObject()
        footerObj.put("isEnabled", footer.isEnabled)
        footerObj.put("text", footer.text)
        footerObj.put("showPageNumber", footer.showPageNumber)
        root.put("footer", footerObj)

        // Blocks
        val blocksArr = JSONArray()
        for (b in blocks) {
            val bObj = JSONObject()
            bObj.put("id", b.id)
            bObj.put("type", b.type.name)
            bObj.put("text", b.text)

            val fObj = JSONObject()
            fObj.put("bold", b.formatting.bold)
            fObj.put("italic", b.formatting.italic)
            fObj.put("underline", b.formatting.underline)
            fObj.put("strikethrough", b.formatting.strikethrough)
            fObj.put("textColorHex", b.formatting.textColorHex)
            fObj.put("highlightColorHex", b.formatting.highlightColorHex)
            fObj.put("alignment", b.formatting.alignment.name)
            fObj.put("fontSizeSp", b.formatting.fontSizeSp.toDouble())
            fObj.put("fontFamily", b.formatting.fontFamily)
            fObj.put("lineSpacingMultiplier", b.formatting.lineSpacingMultiplier.toDouble())
            bObj.put("formatting", fObj)

            if (b.tableData != null) {
                val tObj = JSONObject()
                tObj.put("colCount", b.tableData.colCount)
                val rowsArr = JSONArray()
                for (row in b.tableData.rows) {
                    val rowArr = JSONArray()
                    row.forEach { rowArr.put(it) }
                    rowsArr.put(rowArr)
                }
                tObj.put("rows", rowsArr)
                bObj.put("tableData", tObj)
            }

            if (b.imageData != null) {
                val iObj = JSONObject()
                iObj.put("uriOrUrl", b.imageData.uriOrUrl)
                iObj.put("caption", b.imageData.caption)
                iObj.put("widthPercent", b.imageData.widthPercent.toDouble())
                bObj.put("imageData", iObj)
            }

            if (b.shapeData != null) {
                val sObj = JSONObject()
                sObj.put("shapeType", b.shapeData.shapeType.name)
                sObj.put("text", b.shapeData.text)
                sObj.put("fillColorHex", b.shapeData.fillColorHex)
                sObj.put("strokeColorHex", b.shapeData.strokeColorHex)
                bObj.put("shapeData", sObj)
            }

            if (b.chartData != null) {
                val cObj = JSONObject()
                cObj.put("chartType", b.chartData.chartType.name)
                cObj.put("title", b.chartData.title)
                val lArr = JSONArray()
                b.chartData.labels.forEach { lArr.put(it) }
                cObj.put("labels", lArr)
                val vArr = JSONArray()
                b.chartData.values.forEach { vArr.put(it.toDouble()) }
                cObj.put("values", vArr)
                bObj.put("chartData", cObj)
            }

            blocksArr.put(bObj)
        }
        root.put("blocks", blocksArr)

        // Comments
        val comArr = JSONArray()
        for (c in comments) {
            val cObj = JSONObject()
            cObj.put("id", c.id)
            cObj.put("blockId", c.blockId)
            cObj.put("author", c.author)
            cObj.put("text", c.text)
            cObj.put("timestamp", c.timestamp)
            cObj.put("isResolved", c.isResolved)
            comArr.put(cObj)
        }
        root.put("comments", comArr)

        return root.toString()
    }

    companion object {
        fun fromJson(jsonStr: String): LekhoniDocument {
            try {
                val root = JSONObject(jsonStr)
                val id = root.optString("id", UUID.randomUUID().toString())
                val title = root.optString("title", "Untitled Document")
                val createdAt = root.optLong("createdAt", System.currentTimeMillis())
                val updatedAt = root.optLong("updatedAt", System.currentTimeMillis())
                val isStarred = root.optBoolean("isStarred", false)

                var pageSettings = PageSettings()
                if (root.has("pageSettings")) {
                    val pObj = root.getJSONObject("pageSettings")
                    pageSettings = PageSettings(
                        paperSize = runCatching { PaperSize.valueOf(pObj.getString("paperSize")) }.getOrDefault(PaperSize.A4),
                        orientation = runCatching { Orientation.valueOf(pObj.getString("orientation")) }.getOrDefault(Orientation.PORTRAIT),
                        marginOption = runCatching { MarginOption.valueOf(pObj.getString("marginOption")) }.getOrDefault(MarginOption.NORMAL),
                        columns = pObj.optInt("columns", 1),
                        showBorder = pObj.optBoolean("showBorder", false),
                        watermark = pObj.optString("watermark").takeIf { !it.isNullOrBlank() }
                    )
                }

                var header = HeaderFooterConfig(isEnabled = true, text = "লেখনী")
                if (root.has("header")) {
                    val hObj = root.getJSONObject("header")
                    header = HeaderFooterConfig(
                        isEnabled = hObj.optBoolean("isEnabled", true),
                        text = hObj.optString("text", ""),
                        showPageNumber = hObj.optBoolean("showPageNumber", true)
                    )
                }

                var footer = HeaderFooterConfig(isEnabled = true, text = "")
                if (root.has("footer")) {
                    val fObj = root.getJSONObject("footer")
                    footer = HeaderFooterConfig(
                        isEnabled = fObj.optBoolean("isEnabled", true),
                        text = fObj.optString("text", ""),
                        showPageNumber = fObj.optBoolean("showPageNumber", true)
                    )
                }

                val blocks = mutableListOf<DocumentBlock>()
                if (root.has("blocks")) {
                    val bArr = root.getJSONArray("blocks")
                    for (i in 0 until bArr.length()) {
                        val bObj = bArr.getJSONObject(i)
                        val bId = bObj.optString("id", UUID.randomUUID().toString())
                        val bType = runCatching { BlockType.valueOf(bObj.getString("type")) }.getOrDefault(BlockType.PARAGRAPH)
                        val bText = bObj.optString("text", "")

                        var formatting = BlockFormatting()
                        if (bObj.has("formatting")) {
                            val fObj = bObj.getJSONObject("formatting")
                            formatting = BlockFormatting(
                                bold = fObj.optBoolean("bold", false),
                                italic = fObj.optBoolean("italic", false),
                                underline = fObj.optBoolean("underline", false),
                                strikethrough = fObj.optBoolean("strikethrough", false),
                                textColorHex = fObj.optString("textColorHex", "#1B3B6F"),
                                highlightColorHex = fObj.optString("highlightColorHex", "#00000000"),
                                alignment = runCatching { TextAlignOption.valueOf(fObj.getString("alignment")) }.getOrDefault(TextAlignOption.LEFT),
                                fontSizeSp = fObj.optDouble("fontSizeSp", 16.0).toFloat(),
                                fontFamily = fObj.optString("fontFamily", "Noto Sans Bengali"),
                                lineSpacingMultiplier = fObj.optDouble("lineSpacingMultiplier", 1.25).toFloat()
                            )
                        }

                        var tableData: TableData? = null
                        if (bObj.has("tableData")) {
                            val tObj = bObj.getJSONObject("tableData")
                            val rArr = tObj.getJSONArray("rows")
                            val rows = mutableListOf<List<String>>()
                            for (r in 0 until rArr.length()) {
                                val row = mutableListOf<String>()
                                val cellArr = rArr.getJSONArray(r)
                                for (c in 0 until cellArr.length()) {
                                    row.add(cellArr.getString(c))
                                }
                                rows.add(row)
                            }
                            tableData = TableData(rows = rows, colCount = tObj.optInt("colCount", 3))
                        }

                        var imageData: ImageData? = null
                        if (bObj.has("imageData")) {
                            val iObj = bObj.getJSONObject("imageData")
                            imageData = ImageData(
                                uriOrUrl = iObj.optString("uriOrUrl", ""),
                                caption = iObj.optString("caption", ""),
                                widthPercent = iObj.optDouble("widthPercent", 0.85).toFloat()
                            )
                        }

                        var shapeData: ShapeData? = null
                        if (bObj.has("shapeData")) {
                            val sObj = bObj.getJSONObject("shapeData")
                            shapeData = ShapeData(
                                shapeType = runCatching { ShapeType.valueOf(sObj.getString("shapeType")) }.getOrDefault(ShapeType.ROUNDED_RECT),
                                text = sObj.optString("text", ""),
                                fillColorHex = sObj.optString("fillColorHex", "#E8ECEF"),
                                strokeColorHex = sObj.optString("strokeColorHex", "#1B3B6F")
                            )
                        }

                        var chartData: ChartData? = null
                        if (bObj.has("chartData")) {
                            val cObj = bObj.getJSONObject("chartData")
                            val lArr = cObj.getJSONArray("labels")
                            val labels = mutableListOf<String>()
                            for (l in 0 until lArr.length()) labels.add(lArr.getString(l))

                            val vArr = cObj.getJSONArray("values")
                            val values = mutableListOf<Float>()
                            for (v in 0 until vArr.length()) values.add(vArr.getDouble(v).toFloat())

                            chartData = ChartData(
                                chartType = runCatching { ChartType.valueOf(cObj.getString("chartType")) }.getOrDefault(ChartType.BAR),
                                title = cObj.optString("title", "Chart"),
                                labels = labels,
                                values = values
                            )
                        }

                        blocks.add(
                            DocumentBlock(
                                id = bId,
                                type = bType,
                                text = bText,
                                formatting = formatting,
                                tableData = tableData,
                                imageData = imageData,
                                shapeData = shapeData,
                                chartData = chartData
                            )
                        )
                    }
                }

                if (blocks.isEmpty()) {
                    blocks.add(DocumentBlock(type = BlockType.PARAGRAPH, text = ""))
                }

                val comments = mutableListOf<DocumentComment>()
                if (root.has("comments")) {
                    val comArr = root.getJSONArray("comments")
                    for (i in 0 until comArr.length()) {
                        val cObj = comArr.getJSONObject(i)
                        comments.add(
                            DocumentComment(
                                id = cObj.optString("id", UUID.randomUUID().toString()),
                                blockId = cObj.optString("blockId", ""),
                                author = cObj.optString("author", "Polok Nandi"),
                                text = cObj.optString("text", ""),
                                timestamp = cObj.optLong("timestamp", System.currentTimeMillis()),
                                isResolved = cObj.optBoolean("isResolved", false)
                            )
                        )
                    }
                }

                return LekhoniDocument(
                    id = id,
                    title = title,
                    pageSettings = pageSettings,
                    header = header,
                    footer = footer,
                    blocks = blocks,
                    comments = comments,
                    createdAt = createdAt,
                    updatedAt = updatedAt,
                    isStarred = isStarred
                )
            } catch (e: Exception) {
                return LekhoniDocument(title = "পুনরুদ্ধারকৃত নথি (Recovered)")
            }
        }
    }
}
