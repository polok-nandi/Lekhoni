package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.model.ChartData
import com.example.model.ChartType
import com.example.model.ImageData
import com.example.model.ShapeData
import com.example.model.ShapeType
import com.example.model.TableData
import com.example.ui.theme.FormatColors
import com.example.ui.theme.NotoSansBengaliFamily

@Composable
fun InsertTableDialog(
    onDismiss: () -> Unit,
    onConfirm: (TableData) -> Unit
) {
    var rowCount by remember { mutableStateOf("3") }
    var colCount by remember { mutableStateOf("3") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ছক / টেবিল যুক্ত করুন (Insert Table)", fontFamily = NotoSansBengaliFamily, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = rowCount,
                    onValueChange = { rowCount = it.filter { ch -> ch.isDigit() } },
                    label = { Text("সারির সংখ্যা (Rows)", fontFamily = NotoSansBengaliFamily) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("table_rows_input")
                )
                OutlinedTextField(
                    value = colCount,
                    onValueChange = { colCount = it.filter { ch -> ch.isDigit() } },
                    label = { Text("কলামের সংখ্যা (Columns)", fontFamily = NotoSansBengaliFamily) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("table_cols_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val r = rowCount.toIntOrNull()?.coerceIn(1, 15) ?: 3
                    val c = colCount.toIntOrNull()?.coerceIn(1, 8) ?: 3
                    val rows = mutableListOf<List<String>>()
                    for (i in 0 until r) {
                        val row = mutableListOf<String>()
                        for (j in 0 until c) {
                            if (i == 0) row.add("কলাম ${j + 1}") else row.add("তথ্য ${i},${j + 1}")
                        }
                        rows.add(row)
                    }
                    onConfirm(TableData(rows = rows, colCount = c))
                },
                modifier = Modifier.testTag("confirm_insert_table_btn")
            ) {
                Text("যুক্ত করুন (Insert)", fontFamily = NotoSansBengaliFamily)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("বাতিল", fontFamily = NotoSansBengaliFamily) }
        }
    )
}

@Composable
fun EditTableDialog(
    tableData: TableData,
    onDismiss: () -> Unit,
    onSave: (TableData) -> Unit
) {
    var rows by remember { mutableStateOf(tableData.rows.map { it.toMutableList() }.toMutableList()) }
    var selectedRow by remember { mutableStateOf(0) }
    var selectedCol by remember { mutableStateOf(0) }
    var editingText by remember { mutableStateOf(rows.getOrNull(0)?.getOrNull(0) ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("টেবিল সম্পাদনা (Edit Table)", fontFamily = NotoSansBengaliFamily, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("ঘরের লেখা পরিবর্তন (Cell Content):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = editingText,
                    onValueChange = {
                        editingText = it
                        if (selectedRow in rows.indices && selectedCol in rows[selectedRow].indices) {
                            rows[selectedRow][selectedCol] = it
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("edit_cell_input")
                )

                Text("সেল নির্বাচন করুন (Tap to Select Cell):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    rows.forEachIndexed { rIdx, row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            row.forEachIndexed { cIdx, cell ->
                                val isSelected = (rIdx == selectedRow && cIdx == selectedCol)
                                Box(
                                    modifier = Modifier
                                        .size(width = 85.dp, height = 36.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                            else if (rIdx == 0) MaterialTheme.colorScheme.surfaceVariant
                                            else MaterialTheme.colorScheme.surface
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                                            RoundedCornerShape(4.dp)
                                        )
                                        .clickable {
                                            selectedRow = rIdx
                                            selectedCol = cIdx
                                            editingText = cell
                                        }
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cell.ifBlank { "..." },
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        fontFamily = NotoSansBengaliFamily
                                    )
                                }
                            }
                        }
                    }
                }

                // Table row/col actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            val newRow = MutableList(tableData.colCount) { "নতুন তথ্য" }
                            rows.add(newRow)
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.size(4.dp))
                        Text("সারি যোগ", fontSize = 12.sp, fontFamily = NotoSansBengaliFamily)
                    }
                    if (rows.size > 1) {
                        Button(
                            onClick = {
                                if (selectedRow in rows.indices) {
                                    rows.removeAt(selectedRow)
                                    selectedRow = (selectedRow - 1).coerceAtLeast(0)
                                    editingText = rows[selectedRow][selectedCol.coerceIn(0, rows[selectedRow].lastIndex)]
                                }
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.size(4.dp))
                            Text("সারি মুছুন", fontSize = 12.sp, fontFamily = NotoSansBengaliFamily)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(tableData.copy(rows = rows)) },
                modifier = Modifier.testTag("save_table_edit_btn")
            ) {
                Text("সংরক্ষণ", fontFamily = NotoSansBengaliFamily)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("বাতিল", fontFamily = NotoSansBengaliFamily) }
        }
    )
}

@Composable
fun InsertChartDialog(
    onDismiss: () -> Unit,
    onConfirm: (ChartData) -> Unit
) {
    var chartType by remember { mutableStateOf(ChartType.BAR) }
    var title by remember { mutableStateOf("মাসিক প্রবৃদ্ধি চার্ট") }
    var labelsStr by remember { mutableStateOf("জানু, ফেব্রু, মার্চ, এপ্রিল") }
    var valuesStr by remember { mutableStateOf("45, 60, 85, 95") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("গ্রাফ ও চার্ট যুক্ত করুন (Insert Chart)", fontFamily = NotoSansBengaliFamily, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("চার্টের ধরন (Chart Type):", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChartType.values().forEach { t ->
                        FilterChip(
                            selected = (chartType == t),
                            onClick = { chartType = t },
                            label = { Text(when(t) { ChartType.BAR -> "বার চার্ট"; ChartType.LINE -> "লাইন চার্ট"; ChartType.PIE -> "পাই চার্ট" }, fontFamily = NotoSansBengaliFamily) }
                        )
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("চার্ট শিরোনাম (Title)", fontFamily = NotoSansBengaliFamily) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = labelsStr,
                    onValueChange = { labelsStr = it },
                    label = { Text("লেবেলসমূহ (কমা দিয়ে পৃথক)", fontFamily = NotoSansBengaliFamily) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = valuesStr,
                    onValueChange = { valuesStr = it },
                    label = { Text("মানসমূহ (সংখ্যা, কমা দিয়ে পৃথক)", fontFamily = NotoSansBengaliFamily) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val labels = labelsStr.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    val values = valuesStr.split(",").mapNotNull { it.trim().toFloatOrNull() }
                    onConfirm(
                        ChartData(
                            chartType = chartType,
                            title = title,
                            labels = labels.ifEmpty { listOf("ক", "খ", "গ") },
                            values = values.ifEmpty { listOf(10f, 20f, 30f) }
                        )
                    )
                },
                modifier = Modifier.testTag("confirm_insert_chart_btn")
            ) {
                Text("যুক্ত করুন", fontFamily = NotoSansBengaliFamily)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("বাতিল", fontFamily = NotoSansBengaliFamily) }
        }
    )
}

@Composable
fun InsertShapeDialog(
    onDismiss: () -> Unit,
    onConfirm: (ShapeData) -> Unit
) {
    var shapeType by remember { mutableStateOf(ShapeType.ROUNDED_RECT) }
    var textInside by remember { mutableStateOf("লেখনী অফিস") }
    var selectedFillColor by remember { mutableStateOf(Color(0xFFE8ECEF)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("আকৃতি / শেপ যুক্ত করুন (Insert Shape)", fontFamily = NotoSansBengaliFamily, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("আকৃতির ধরন (Shape):", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ShapeType.values().forEach { st ->
                        FilterChip(
                            selected = (shapeType == st),
                            onClick = { shapeType = st },
                            label = { Text(when(st) {
                                ShapeType.RECTANGLE -> "আয়তক্ষেত্র"
                                ShapeType.ROUNDED_RECT -> "বৃত্তাকার কোণ"
                                ShapeType.CIRCLE -> "বৃত্ত"
                                ShapeType.ARROW -> "তীর চিহ্ন"
                                ShapeType.CALLOUT -> "বার্তা বক্স"
                                ShapeType.STAR -> "তারকা / ব্যাজ"
                            }, fontFamily = NotoSansBengaliFamily) }
                        )
                    }
                }

                OutlinedTextField(
                    value = textInside,
                    onValueChange = { textInside = it },
                    label = { Text("আকৃতির ভেতরের লেখা (Text Inside)", fontFamily = NotoSansBengaliFamily) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("ভরাট রং (Fill Color):", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        Color(0xFFE8ECEF),
                        Color(0xFFFFF176),
                        Color(0xFFA5D6A7),
                        Color(0xFF80DEEA),
                        Color(0xFFFFCC80),
                        Color(0xFFF3E5AB)
                    ).forEach { c ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(c)
                                .border(if (selectedFillColor == c) 2.dp else 1.dp, if (selectedFillColor == c) Color.Black else Color.Gray, CircleShape)
                                .clickable { selectedFillColor = c }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val hex = String.format("#%06X", (0xFFFFFF and selectedFillColor.toArgb()))
                    onConfirm(
                        ShapeData(
                            shapeType = shapeType,
                            text = textInside,
                            fillColorHex = hex,
                            strokeColorHex = "#1B3B6F"
                        )
                    )
                },
                modifier = Modifier.testTag("confirm_insert_shape_btn")
            ) {
                Text("যুক্ত করুন", fontFamily = NotoSansBengaliFamily)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("বাতিল", fontFamily = NotoSansBengaliFamily) }
        }
    )
}

@Composable
fun InsertImageDialog(
    onDismiss: () -> Unit,
    onConfirm: (ImageData) -> Unit
) {
    var caption by remember { mutableStateOf("নথি চিত্র ১") }
    var imageUrl by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ছবি ও চিত্র যুক্ত করুন (Insert Image)", fontFamily = NotoSansBengaliFamily, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = caption,
                    onValueChange = { caption = it },
                    label = { Text("ছবির শিরোনাম / ক্যাপশন (Caption)", fontFamily = NotoSansBengaliFamily) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("image_caption_input")
                )
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("ছবির লিংক বা ইউআরএল (ঐচ্ছিক)", fontFamily = NotoSansBengaliFamily) },
                    placeholder = { Text("https://example.com/photo.jpg") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        ImageData(
                            uriOrUrl = imageUrl,
                            caption = caption
                        )
                    )
                },
                modifier = Modifier.testTag("confirm_insert_image_btn")
            ) {
                Text("যুক্ত করুন", fontFamily = NotoSansBengaliFamily)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("বাতিল", fontFamily = NotoSansBengaliFamily) }
        }
    )
}

@Composable
fun FindReplaceDialog(
    onDismiss: () -> Unit,
    onReplace: (find: String, replace: String) -> Unit
) {
    var findText by remember { mutableStateOf("") }
    var replaceText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("খুঁজুন ও প্রতিস্থাপন করুন (Find & Replace)", fontFamily = NotoSansBengaliFamily, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = findText,
                    onValueChange = { findText = it },
                    label = { Text("যা খুঁজতে চান (Find)", fontFamily = NotoSansBengaliFamily) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("find_input")
                )
                OutlinedTextField(
                    value = replaceText,
                    onValueChange = { replaceText = it },
                    label = { Text("যা দিয়ে প্রতিস্থাপন করবেন (Replace With)", fontFamily = NotoSansBengaliFamily) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("replace_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (findText.isNotBlank()) {
                        onReplace(findText, replaceText)
                    }
                },
                modifier = Modifier.testTag("confirm_replace_btn")
            ) {
                Text("সব প্রতিস্থাপন করুন (Replace All)", fontFamily = NotoSansBengaliFamily)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("বন্ধ করুন", fontFamily = NotoSansBengaliFamily) }
        }
    )
}
