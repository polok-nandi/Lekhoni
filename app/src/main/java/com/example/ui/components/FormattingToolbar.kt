package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.FormatAlignJustify
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.FormatAlignRight
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatClear
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material.icons.filled.FormatColorText
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.FormatStrikethrough
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BlockFormatting
import com.example.model.BlockType
import com.example.model.TextAlignOption
import com.example.ui.theme.FormatColors
import com.example.ui.theme.HighlightColors
import com.example.ui.theme.LekhoniInkPrimary
import com.example.ui.theme.NotoSansBengaliFamily

@Composable
fun FormattingToolbar(
    currentBlockType: BlockType,
    currentFormatting: BlockFormatting,
    onBlockTypeChange: (BlockType) -> Unit,
    onFormattingChange: (BlockFormatting) -> Unit,
    canUndo: Boolean,
    canRedo: Boolean,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onAddBlock: (BlockType) -> Unit,
    onDeleteBlock: () -> Unit,
    onMoveBlock: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var showBlockTypeMenu by remember { mutableStateOf(false) }
    var showFontMenu by remember { mutableStateOf(false) }
    var showTextColorMenu by remember { mutableStateOf(false) }
    var showHighlightMenu by remember { mutableStateOf(false) }
    var showLineSpacingMenu by remember { mutableStateOf(false) }

    val fontFamilies = listOf(
        "Noto Sans Bengali",
        "Noto Serif Bengali",
        "Kalpurush (কালপুরুষ)",
        "SolaimanLipi (সোলাইমান লিপি)",
        "Siyam Rupali (সিয়াম রূপালী)",
        "Sans-Serif",
        "Serif",
        "Monospace"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Undo / Redo
            IconButton(
                onClick = onUndo,
                enabled = canUndo,
                modifier = Modifier.size(36.dp).testTag("action_undo_btn")
            ) {
                Icon(Icons.Default.Undo, contentDescription = "Undo", modifier = Modifier.size(20.dp))
            }
            IconButton(
                onClick = onRedo,
                enabled = canRedo,
                modifier = Modifier.size(36.dp).testTag("action_redo_btn")
            ) {
                Icon(Icons.Default.Redo, contentDescription = "Redo", modifier = Modifier.size(20.dp))
            }

            Box(modifier = Modifier.height(24.dp).width(1.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)))

            // Block Style / Type Selector
            Box {
                Surface(
                    onClick = { showBlockTypeMenu = true },
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.height(36.dp).testTag("block_type_selector")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentBlockType.bengaliName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = NotoSansBengaliFamily
                        )
                    }
                }
                DropdownMenu(
                    expanded = showBlockTypeMenu,
                    onDismissRequest = { showBlockTypeMenu = false }
                ) {
                    BlockType.values().forEach { bType ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "${bType.bengaliName} (${bType.displayName})",
                                    fontFamily = NotoSansBengaliFamily
                                )
                            },
                            onClick = {
                                onBlockTypeChange(bType)
                                showBlockTypeMenu = false
                            }
                        )
                    }
                }
            }

            // Font Family Selector
            Box {
                Surface(
                    onClick = { showFontMenu = true },
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.height(36.dp).testTag("font_family_selector")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentFormatting.fontFamily.take(14),
                            fontSize = 12.sp,
                            fontFamily = NotoSansBengaliFamily
                        )
                    }
                }
                DropdownMenu(
                    expanded = showFontMenu,
                    onDismissRequest = { showFontMenu = false }
                ) {
                    fontFamilies.forEach { fontName ->
                        DropdownMenuItem(
                            text = { Text(fontName, fontFamily = NotoSansBengaliFamily) },
                            onClick = {
                                onFormattingChange(currentFormatting.copy(fontFamily = fontName))
                                showFontMenu = false
                            }
                        )
                    }
                }
            }

            // Font Size Decrement
            IconButton(
                onClick = {
                    val newSize = (currentFormatting.fontSizeSp - 2f).coerceAtLeast(10f)
                    onFormattingChange(currentFormatting.copy(fontSizeSp = newSize))
                },
                modifier = Modifier.size(36.dp).testTag("font_size_minus")
            ) {
                Text("A-", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            // Font Size Indicator
            Text(
                text = "${currentFormatting.fontSizeSp.toInt()} pt",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 2.dp)
            )

            // Font Size Increment
            IconButton(
                onClick = {
                    val newSize = (currentFormatting.fontSizeSp + 2f).coerceAtMost(36f)
                    onFormattingChange(currentFormatting.copy(fontSizeSp = newSize))
                },
                modifier = Modifier.size(36.dp).testTag("font_size_plus")
            ) {
                Text("A+", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Box(modifier = Modifier.height(24.dp).width(1.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)))

            // Bold
            IconButton(
                onClick = { onFormattingChange(currentFormatting.copy(bold = !currentFormatting.bold)) },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (currentFormatting.bold) LekhoniInkPrimary.copy(alpha = 0.2f) else Color.Transparent
                ),
                modifier = Modifier.size(36.dp).testTag("format_bold_btn")
            ) {
                Icon(Icons.Default.FormatBold, contentDescription = "Bold", modifier = Modifier.size(19.dp))
            }

            // Italic
            IconButton(
                onClick = { onFormattingChange(currentFormatting.copy(italic = !currentFormatting.italic)) },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (currentFormatting.italic) LekhoniInkPrimary.copy(alpha = 0.2f) else Color.Transparent
                ),
                modifier = Modifier.size(36.dp).testTag("format_italic_btn")
            ) {
                Icon(Icons.Default.FormatItalic, contentDescription = "Italic", modifier = Modifier.size(19.dp))
            }

            // Underline
            IconButton(
                onClick = { onFormattingChange(currentFormatting.copy(underline = !currentFormatting.underline)) },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (currentFormatting.underline) LekhoniInkPrimary.copy(alpha = 0.2f) else Color.Transparent
                ),
                modifier = Modifier.size(36.dp).testTag("format_underline_btn")
            ) {
                Icon(Icons.Default.FormatUnderlined, contentDescription = "Underline", modifier = Modifier.size(19.dp))
            }

            // Strikethrough
            IconButton(
                onClick = { onFormattingChange(currentFormatting.copy(strikethrough = !currentFormatting.strikethrough)) },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (currentFormatting.strikethrough) LekhoniInkPrimary.copy(alpha = 0.2f) else Color.Transparent
                ),
                modifier = Modifier.size(36.dp).testTag("format_strikethrough_btn")
            ) {
                Icon(Icons.Default.FormatStrikethrough, contentDescription = "Strikethrough", modifier = Modifier.size(19.dp))
            }

            Box(modifier = Modifier.height(24.dp).width(1.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)))

            // Alignment options
            IconButton(
                onClick = { onFormattingChange(currentFormatting.copy(alignment = TextAlignOption.LEFT)) },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (currentFormatting.alignment == TextAlignOption.LEFT) LekhoniInkPrimary.copy(alpha = 0.2f) else Color.Transparent
                ),
                modifier = Modifier.size(36.dp).testTag("align_left_btn")
            ) {
                Icon(Icons.Default.FormatAlignLeft, contentDescription = "Align Left", modifier = Modifier.size(19.dp))
            }
            IconButton(
                onClick = { onFormattingChange(currentFormatting.copy(alignment = TextAlignOption.CENTER)) },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (currentFormatting.alignment == TextAlignOption.CENTER) LekhoniInkPrimary.copy(alpha = 0.2f) else Color.Transparent
                ),
                modifier = Modifier.size(36.dp).testTag("align_center_btn")
            ) {
                Icon(Icons.Default.FormatAlignCenter, contentDescription = "Align Center", modifier = Modifier.size(19.dp))
            }
            IconButton(
                onClick = { onFormattingChange(currentFormatting.copy(alignment = TextAlignOption.RIGHT)) },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (currentFormatting.alignment == TextAlignOption.RIGHT) LekhoniInkPrimary.copy(alpha = 0.2f) else Color.Transparent
                ),
                modifier = Modifier.size(36.dp).testTag("align_right_btn")
            ) {
                Icon(Icons.Default.FormatAlignRight, contentDescription = "Align Right", modifier = Modifier.size(19.dp))
            }
            IconButton(
                onClick = { onFormattingChange(currentFormatting.copy(alignment = TextAlignOption.JUSTIFY)) },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (currentFormatting.alignment == TextAlignOption.JUSTIFY) LekhoniInkPrimary.copy(alpha = 0.2f) else Color.Transparent
                ),
                modifier = Modifier.size(36.dp).testTag("align_justify_btn")
            ) {
                Icon(Icons.Default.FormatAlignJustify, contentDescription = "Align Justify", modifier = Modifier.size(19.dp))
            }

            Box(modifier = Modifier.height(24.dp).width(1.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)))

            // Text Color Picker
            Box {
                IconButton(
                    onClick = { showTextColorMenu = true },
                    modifier = Modifier.size(36.dp).testTag("text_color_btn")
                ) {
                    Icon(
                        Icons.Default.FormatColorText,
                        contentDescription = "Text Color",
                        tint = try { Color(android.graphics.Color.parseColor(currentFormatting.textColorHex)) } catch (e: Exception) { LekhoniInkPrimary },
                        modifier = Modifier.size(20.dp)
                    )
                }
                DropdownMenu(
                    expanded = showTextColorMenu,
                    onDismissRequest = { showTextColorMenu = false }
                ) {
                    Text(
                        "রং নির্বাচন করুন (Text Color)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp),
                        fontFamily = NotoSansBengaliFamily
                    )
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FormatColors.forEach { c ->
                            val hex = String.format("#%06X", (0xFFFFFF and c.toArgb()))
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(c)
                                    .border(1.dp, Color.Gray, CircleShape)
                                    .clickable {
                                        onFormattingChange(currentFormatting.copy(textColorHex = hex))
                                        showTextColorMenu = false
                                    }
                            )
                        }
                    }
                }
            }

            // Highlight Color Picker
            Box {
                IconButton(
                    onClick = { showHighlightMenu = true },
                    modifier = Modifier.size(36.dp).testTag("highlight_color_btn")
                ) {
                    Icon(Icons.Default.FormatColorFill, contentDescription = "Highlight Color", modifier = Modifier.size(20.dp))
                }
                DropdownMenu(
                    expanded = showHighlightMenu,
                    onDismissRequest = { showHighlightMenu = false }
                ) {
                    Text(
                        "হাইলাইট (Highlight Color)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp),
                        fontFamily = NotoSansBengaliFamily
                    )
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        HighlightColors.forEach { c ->
                            val hex = if (c == Color.Transparent) "#00000000" else String.format("#%06X", (0xFFFFFF and c.toArgb()))
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(if (c == Color.Transparent) Color.White else c)
                                    .border(1.dp, Color.Gray, CircleShape)
                                    .clickable {
                                        onFormattingChange(currentFormatting.copy(highlightColorHex = hex))
                                        showHighlightMenu = false
                                    }
                            )
                        }
                    }
                }
            }

            // Line Spacing
            Box {
                Surface(
                    onClick = { showLineSpacingMenu = true },
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.height(36.dp).testTag("line_spacing_selector")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${currentFormatting.lineSpacingMultiplier}x", fontSize = 12.sp)
                    }
                }
                DropdownMenu(
                    expanded = showLineSpacingMenu,
                    onDismissRequest = { showLineSpacingMenu = false }
                ) {
                    listOf(1.0f, 1.15f, 1.25f, 1.5f, 2.0f).forEach { spacing ->
                        DropdownMenuItem(
                            text = { Text("${spacing}x লাইন ফাঁকা", fontFamily = NotoSansBengaliFamily) },
                            onClick = {
                                onFormattingChange(currentFormatting.copy(lineSpacingMultiplier = spacing))
                                showLineSpacingMenu = false
                            }
                        )
                    }
                }
            }

            // Clear Formatting
            IconButton(
                onClick = { onFormattingChange(BlockFormatting()) },
                modifier = Modifier.size(36.dp).testTag("clear_format_btn")
            ) {
                Icon(Icons.Default.FormatClear, contentDescription = "Clear Format", modifier = Modifier.size(19.dp))
            }

            Box(modifier = Modifier.height(24.dp).width(1.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)))

            // Add Block After
            IconButton(
                onClick = { onAddBlock(BlockType.PARAGRAPH) },
                modifier = Modifier.size(36.dp).testTag("add_block_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Paragraph", tint = LekhoniInkPrimary, modifier = Modifier.size(20.dp))
            }

            // Move Block Up
            IconButton(
                onClick = { onMoveBlock(true) },
                modifier = Modifier.size(36.dp).testTag("move_block_up_btn")
            ) {
                Icon(Icons.Default.ArrowUpward, contentDescription = "Move Up", modifier = Modifier.size(18.dp))
            }

            // Move Block Down
            IconButton(
                onClick = { onMoveBlock(false) },
                modifier = Modifier.size(36.dp).testTag("move_block_down_btn")
            ) {
                Icon(Icons.Default.ArrowDownward, contentDescription = "Move Down", modifier = Modifier.size(18.dp))
            }

            // Delete Block
            IconButton(
                onClick = onDeleteBlock,
                modifier = Modifier.size(36.dp).testTag("delete_block_btn")
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Block", tint = Color.Red, modifier = Modifier.size(18.dp))
            }
        }
    }
}
