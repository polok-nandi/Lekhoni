package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HeaderFooterConfig
import com.example.model.MarginOption
import com.example.model.Orientation
import com.example.model.PageSettings
import com.example.model.PaperSize
import com.example.ui.theme.NotoSansBengaliFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageLayoutDialog(
    initialSettings: PageSettings,
    initialHeader: HeaderFooterConfig,
    initialFooter: HeaderFooterConfig,
    onDismiss: () -> Unit,
    onSave: (PageSettings, String, String, Boolean) -> Unit
) {
    var selectedPaperSize by remember { mutableStateOf(initialSettings.paperSize) }
    var selectedOrientation by remember { mutableStateOf(initialSettings.orientation) }
    var selectedMargin by remember { mutableStateOf(initialSettings.marginOption) }
    var columns by remember { mutableStateOf(initialSettings.columns) }
    var showBorder by remember { mutableStateOf(initialSettings.showBorder) }
    var watermarkText by remember { mutableStateOf(initialSettings.watermark ?: "") }

    var headerText by remember { mutableStateOf(initialHeader.text) }
    var footerText by remember { mutableStateOf(initialFooter.text) }
    var showPageNumber by remember { mutableStateOf(initialFooter.showPageNumber) }

    var paperExpanded by remember { mutableStateOf(false) }
    var marginExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "পৃষ্ঠা বিন্যাস ও লেআউট (Page Setup)",
                fontFamily = NotoSansBengaliFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Paper Size
                Text("কাগজের আকার (Paper Size):", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                ExposedDropdownMenuBox(
                    expanded = paperExpanded,
                    onExpandedChange = { paperExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedPaperSize.bengaliName,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = paperExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor().testTag("paper_size_dropdown")
                    )
                    ExposedDropdownMenu(
                        expanded = paperExpanded,
                        onDismissRequest = { paperExpanded = false }
                    ) {
                        PaperSize.values().forEach { size ->
                            DropdownMenuItem(
                                text = { Text("${size.bengaliName} (${size.displayName})", fontFamily = NotoSansBengaliFamily) },
                                onClick = {
                                    selectedPaperSize = size
                                    paperExpanded = false
                                }
                            )
                        }
                    }
                }

                // Orientation
                Text("পৃষ্ঠার দিক (Orientation):", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Orientation.values().forEach { orient ->
                        FilterChip(
                            selected = (selectedOrientation == orient),
                            onClick = { selectedOrientation = orient },
                            label = { Text(orient.bengaliName, fontFamily = NotoSansBengaliFamily) },
                            modifier = Modifier.testTag("orient_${orient.name}")
                        )
                    }
                }

                // Margins
                Text("মার্জিন (Margins):", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                ExposedDropdownMenuBox(
                    expanded = marginExpanded,
                    onExpandedChange = { marginExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedMargin.bengaliName,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = marginExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor().testTag("margin_dropdown")
                    )
                    ExposedDropdownMenu(
                        expanded = marginExpanded,
                        onDismissRequest = { marginExpanded = false }
                    ) {
                        MarginOption.values().forEach { m ->
                            DropdownMenuItem(
                                text = { Text("${m.bengaliName} (${m.displayName})", fontFamily = NotoSansBengaliFamily) },
                                onClick = {
                                    selectedMargin = m
                                    marginExpanded = false
                                }
                            )
                        }
                    }
                }

                // Columns
                Text("কলাম সংখ্যা (Columns):", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(1, 2, 3).forEach { colNum ->
                        FilterChip(
                            selected = (columns == colNum),
                            onClick = { columns = colNum },
                            label = { Text("$colNum কলাম", fontFamily = NotoSansBengaliFamily) },
                            modifier = Modifier.testTag("col_$colNum")
                        )
                    }
                }

                // Page Border
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("পৃষ্ঠার সীমানা রেখা (Page Border):", fontSize = 13.sp, fontFamily = NotoSansBengaliFamily)
                    Switch(
                        checked = showBorder,
                        onCheckedChange = { showBorder = it },
                        modifier = Modifier.testTag("show_border_switch")
                    )
                }

                // Watermark
                OutlinedTextField(
                    value = watermarkText,
                    onValueChange = { watermarkText = it },
                    label = { Text("জলছাপ / ওয়াটারমার্ক (Watermark)", fontFamily = NotoSansBengaliFamily) },
                    placeholder = { Text("যেমন: গোপনীয় / খসড়া") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("watermark_input")
                )

                // Header
                OutlinedTextField(
                    value = headerText,
                    onValueChange = { headerText = it },
                    label = { Text("হেডার লেখা (Header Text)", fontFamily = NotoSansBengaliFamily) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("header_text_input")
                )

                // Footer
                OutlinedTextField(
                    value = footerText,
                    onValueChange = { footerText = it },
                    label = { Text("ফুটার লেখা (Footer Text)", fontFamily = NotoSansBengaliFamily) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("footer_text_input")
                )

                // Show Page Numbers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = showPageNumber,
                        onCheckedChange = { showPageNumber = it },
                        modifier = Modifier.testTag("page_num_checkbox")
                    )
                    Text("ফুটারে পৃষ্ঠা নম্বর দেখান (Page Numbers)", fontSize = 13.sp, fontFamily = NotoSansBengaliFamily)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updatedSettings = initialSettings.copy(
                        paperSize = selectedPaperSize,
                        orientation = selectedOrientation,
                        marginOption = selectedMargin,
                        columns = columns,
                        showBorder = showBorder,
                        watermark = watermarkText.takeIf { it.isNotBlank() }
                    )
                    onSave(updatedSettings, headerText, footerText, showPageNumber)
                },
                modifier = Modifier.testTag("save_page_setup_btn")
            ) {
                Text("সংরক্ষণ করুন (Apply)", fontFamily = NotoSansBengaliFamily)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল (Cancel)", fontFamily = NotoSansBengaliFamily)
            }
        }
    )
}
