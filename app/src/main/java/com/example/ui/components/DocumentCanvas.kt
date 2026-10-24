package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BlockType
import com.example.model.DocumentBlock
import com.example.model.LekhoniDocument
import com.example.model.Orientation
import com.example.model.TableData
import com.example.model.TextAlignOption
import com.example.ui.theme.DarkPaperCanvas
import com.example.ui.theme.DarkPaperCard
import com.example.ui.theme.LekhoniGoldAccent
import com.example.ui.theme.LekhoniInkPrimary
import com.example.ui.theme.NotoSansBengaliFamily
import com.example.ui.theme.NotoSerifBengaliFamily
import com.example.ui.theme.PaperCanvasBackground
import com.example.ui.theme.PaperCardLight
import com.example.viewmodel.EditorViewMode

@Composable
fun DocumentCanvas(
    document: LekhoniDocument,
    viewMode: EditorViewMode,
    selectedBlockIndex: Int,
    onSelectBlock: (Int) -> Unit,
    onUpdateBlockText: (Int, String) -> Unit,
    onEditTable: (Int, TableData) -> Unit,
    onDeleteBlock: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Resolve font family helper
    fun resolveFontFamily(name: String): FontFamily {
        return when {
            name.contains("Serif", ignoreCase = true) -> NotoSerifBengaliFamily
            name.contains("Mono", ignoreCase = true) -> FontFamily.Monospace
            else -> NotoSansBengaliFamily
        }
    }

    // Ruler bar for Desktop View
    val showRuler = (viewMode == EditorViewMode.DESKTOP)
    val canvasBg = if (MaterialTheme.colorScheme.background == PaperCardLight) PaperCanvasBackground else DarkPaperCanvas
    val paperBg = if (MaterialTheme.colorScheme.background == PaperCardLight) PaperCardLight else DarkPaperCard

    val paperPaddingHorizontal = when (viewMode) {
        EditorViewMode.MOBILE -> 16.dp
        EditorViewMode.DESKTOP -> 36.dp
        EditorViewMode.PRINT_LAYOUT -> when (document.pageSettings.marginOption) {
            com.example.model.MarginOption.NARROW -> 20.dp
            com.example.model.MarginOption.WIDE -> 48.dp
            else -> 32.dp
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(canvasBg),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Desktop Ruler Bar
            if (showRuler) {
                Surface(
                    modifier = Modifier.fillMaxWidth().height(26.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (i in 0..16) {
                            Text(
                                text = if (i % 2 == 0) "$i" else "|",
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Paper Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxHeight()
                        .widthIn(max = if (viewMode == EditorViewMode.MOBILE) 700.dp else 850.dp)
                        .padding(
                            horizontal = if (viewMode == EditorViewMode.MOBILE) 0.dp else 16.dp,
                            vertical = if (viewMode == EditorViewMode.MOBILE) 0.dp else 12.dp
                        )
                        .shadow(
                            elevation = if (viewMode == EditorViewMode.MOBILE) 0.dp else 6.dp,
                            shape = if (viewMode == EditorViewMode.MOBILE) RoundedCornerShape(0.dp) else RoundedCornerShape(4.dp)
                        ),
                    color = paperBg,
                    border = if (document.pageSettings.showBorder) androidx.compose.foundation.BorderStroke(1.5.dp, Color.Gray) else null
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                start = paperPaddingHorizontal,
                                end = paperPaddingHorizontal,
                                top = if (viewMode == EditorViewMode.PRINT_LAYOUT) 32.dp else 16.dp,
                                bottom = 48.dp
                            ),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Header (Print Layout / Desktop View)
                        if (document.header.isEnabled && document.header.text.isNotBlank()) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = document.header.text,
                                        fontSize = 11.sp,
                                        color = Color.Gray,
                                        fontFamily = NotoSansBengaliFamily
                                    )
                                    Text(
                                        text = "লেখনী মুক্ত নথি",
                                        fontSize = 10.sp,
                                        color = Color.LightGray
                                    )
                                }
                                Divider(color = Color.LightGray.copy(alpha = 0.5f))
                                Spacer(Modifier.height(8.dp))
                            }
                        }

                        // Watermark indicator if present
                        if (!document.pageSettings.watermark.isNullOrBlank()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = document.pageSettings.watermark,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Gray.copy(alpha = 0.2f),
                                        fontFamily = NotoSansBengaliFamily
                                    )
                                }
                            }
                        }

                        // Blocks Editor
                        itemsIndexed(document.blocks) { index, block ->
                            val isSelected = (index == selectedBlockIndex)

                            BlockItemView(
                                index = index,
                                block = block,
                                isSelected = isSelected,
                                onSelect = { onSelectBlock(index) },
                                onTextChange = { onUpdateBlockText(index, it) },
                                onEditTable = { onEditTable(index, it) },
                                onDelete = { onDeleteBlock(index) },
                                resolveFont = { resolveFontFamily(it) }
                            )
                        }

                        // Footer (Print Layout / Desktop View)
                        if (document.footer.isEnabled) {
                            item {
                                Spacer(Modifier.height(24.dp))
                                Divider(color = Color.LightGray.copy(alpha = 0.5f))
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = document.footer.text,
                                        fontSize = 11.sp,
                                        color = Color.Gray,
                                        fontFamily = NotoSansBengaliFamily
                                    )
                                    if (document.footer.showPageNumber) {
                                        Text(
                                            text = "পৃষ্ঠা ১",
                                            fontSize = 11.sp,
                                            color = Color.Gray,
                                            fontFamily = NotoSansBengaliFamily
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BlockItemView(
    index: Int,
    block: DocumentBlock,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onTextChange: (String) -> Unit,
    onEditTable: (TableData) -> Unit,
    onDelete: () -> Unit,
    resolveFont: (String) -> FontFamily
) {
    val f = block.formatting
    val textColor = try { Color(android.graphics.Color.parseColor(f.textColorHex)) } catch (e: Exception) { MaterialTheme.colorScheme.onSurface }
    val highlightColor = try { Color(android.graphics.Color.parseColor(f.highlightColorHex)) } catch (e: Exception) { Color.Transparent }

    val textAlign = when (f.alignment) {
        TextAlignOption.CENTER -> TextAlign.Center
        TextAlignOption.RIGHT -> TextAlign.Right
        TextAlignOption.JUSTIFY -> TextAlign.Justify
        else -> TextAlign.Left
    }

    val textDecoration = when {
        f.underline && f.strikethrough -> TextDecoration.combine(listOf(TextDecoration.Underline, TextDecoration.LineThrough))
        f.underline -> TextDecoration.Underline
        f.strikethrough -> TextDecoration.LineThrough
        else -> TextDecoration.None
    }

    val fontSize = when (block.type) {
        BlockType.HEADING_1 -> 24.sp
        BlockType.HEADING_2 -> 20.sp
        BlockType.HEADING_3 -> 17.sp
        else -> f.fontSizeSp.sp
    }

    val fontWeight = when {
        block.type in listOf(BlockType.HEADING_1, BlockType.HEADING_2, BlockType.HEADING_3) -> FontWeight.Bold
        f.bold -> FontWeight.Bold
        else -> FontWeight.Normal
    }

    val fontStyle = if (f.italic || block.type == BlockType.BLOCKQUOTE) FontStyle.Italic else FontStyle.Normal

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clipRoundedBorder(isSelected)
            .clickable { onSelect() }
            .padding(vertical = 4.dp, horizontal = 6.dp)
            .testTag("block_item_$index")
    ) {
        when (block.type) {
            BlockType.PAGE_BREAK -> {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(modifier = Modifier.weight(1f), color = Color.Gray)
                    Text(
                        " [ পৃষ্ঠা বিরতি / Page Break ] ",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontFamily = NotoSansBengaliFamily
                    )
                    Divider(modifier = Modifier.weight(1f), color = Color.Gray)
                }
            }
            BlockType.DIVIDER -> {
                Divider(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    color = LekhoniInkPrimary.copy(alpha = 0.4f),
                    thickness = 1.5.dp
                )
            }
            BlockType.BLOCKQUOTE -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(modifier = Modifier.width(4.dp).height(32.dp).background(LekhoniInkPrimary, RoundedCornerShape(2.dp)))
                    Spacer(Modifier.width(8.dp))
                    BasicTextField(
                        value = block.text,
                        onValueChange = onTextChange,
                        textStyle = TextStyle(
                            fontFamily = resolveFont(f.fontFamily),
                            fontSize = fontSize,
                            fontWeight = fontWeight,
                            fontStyle = fontStyle,
                            color = textColor,
                            textAlign = textAlign,
                            background = highlightColor,
                            lineHeight = (fontSize.value * f.lineSpacingMultiplier * 1.3f).sp
                        ),
                        cursorBrush = SolidColor(LekhoniInkPrimary),
                        modifier = Modifier.fillMaxWidth().testTag("blockquote_input_$index")
                    )
                }
            }
            BlockType.BULLET_LIST -> {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    Text("• ", fontSize = fontSize, fontWeight = FontWeight.Bold, color = LekhoniInkPrimary)
                    BasicTextField(
                        value = block.text,
                        onValueChange = onTextChange,
                        textStyle = TextStyle(
                            fontFamily = resolveFont(f.fontFamily),
                            fontSize = fontSize,
                            fontWeight = fontWeight,
                            fontStyle = fontStyle,
                            color = textColor,
                            textAlign = textAlign,
                            background = highlightColor
                        ),
                        cursorBrush = SolidColor(LekhoniInkPrimary),
                        modifier = Modifier.fillMaxWidth().testTag("bullet_input_$index")
                    )
                }
            }
            BlockType.NUMBERED_LIST -> {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    Text("${index + 1}. ", fontSize = fontSize, fontWeight = FontWeight.Bold, color = LekhoniInkPrimary)
                    BasicTextField(
                        value = block.text,
                        onValueChange = onTextChange,
                        textStyle = TextStyle(
                            fontFamily = resolveFont(f.fontFamily),
                            fontSize = fontSize,
                            fontWeight = fontWeight,
                            fontStyle = fontStyle,
                            color = textColor,
                            textAlign = textAlign,
                            background = highlightColor
                        ),
                        cursorBrush = SolidColor(LekhoniInkPrimary),
                        modifier = Modifier.fillMaxWidth().testTag("numbered_input_$index")
                    )
                }
            }
            BlockType.TABLE -> {
                val table = block.tableData ?: TableData()
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "ছক / টেবিল (${table.rows.size} সারি, ${table.colCount} কলাম)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = LekhoniInkPrimary
                        )
                        IconButton(
                            onClick = { onEditTable(table) },
                            modifier = Modifier.size(28.dp).testTag("edit_table_btn_$index")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Table", modifier = Modifier.size(16.dp))
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFD0D7DE), RoundedCornerShape(4.dp))
                    ) {
                        table.rows.forEachIndexed { rIdx, row ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (rIdx == 0) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent)
                            ) {
                                row.forEachIndexed { cIdx, cell ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .border(0.5.dp, Color(0xFFD0D7DE))
                                            .padding(6.dp)
                                    ) {
                                        Text(
                                            text = cell.ifBlank { " " },
                                            fontSize = 12.sp,
                                            fontWeight = if (rIdx == 0) FontWeight.Bold else FontWeight.Normal,
                                            fontFamily = NotoSansBengaliFamily
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            BlockType.CHART -> {
                val chart = block.chartData
                if (chart != null) {
                    ChartCanvas(chartData = chart)
                }
            }
            BlockType.SHAPE -> {
                val shape = block.shapeData
                if (shape != null) {
                    ShapeCanvas(shapeData = shape)
                }
            }
            BlockType.IMAGE -> {
                val img = block.imageData
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(img?.widthPercent ?: 0.85f)
                            .height(140.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFFD0D7DE), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "[সংযুক্ত চিত্র: ${img?.caption ?: "ছবি"}]",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = NotoSansBengaliFamily
                        )
                    }
                    if (!img?.caption.isNullOrBlank()) {
                        Text(
                            text = img?.caption ?: "",
                            fontSize = 11.sp,
                            fontStyle = FontStyle.Italic,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp),
                            fontFamily = NotoSansBengaliFamily
                        )
                    }
                }
            }
            else -> {
                // PARAGRAPH, HEADING_1, HEADING_2, HEADING_3
                BasicTextField(
                    value = block.text,
                    onValueChange = onTextChange,
                    textStyle = TextStyle(
                        fontFamily = resolveFont(f.fontFamily),
                        fontSize = fontSize,
                        fontWeight = fontWeight,
                        fontStyle = fontStyle,
                        textDecoration = textDecoration,
                        color = textColor,
                        textAlign = textAlign,
                        background = highlightColor,
                        lineHeight = (fontSize.value * f.lineSpacingMultiplier * 1.35f).sp
                    ),
                    cursorBrush = SolidColor(LekhoniInkPrimary),
                    decorationBox = { innerTextField ->
                        if (block.text.isEmpty() && isSelected) {
                            Text(
                                text = "লিখতে শুরু করুন...",
                                fontSize = fontSize,
                                color = Color.Gray.copy(alpha = 0.6f),
                                fontFamily = resolveFont(f.fontFamily)
                            )
                        }
                        innerTextField()
                    },
                    modifier = Modifier.fillMaxWidth().testTag("block_text_input_$index")
                )
            }
        }
    }
}

private fun Modifier.clipRoundedBorder(isSelected: Boolean): Modifier =
    if (isSelected) {
        this.border(1.dp, LekhoniInkPrimary.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
    } else {
        this
    }
