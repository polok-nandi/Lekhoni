package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FindReplace
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.export.FileExporter
import com.example.model.BlockFormatting
import com.example.model.BlockType
import com.example.model.ChartData
import com.example.model.ImageData
import com.example.model.ShapeData
import com.example.model.TableData
import com.example.ui.components.AiAssistantSheet
import com.example.ui.components.BengaliToolsBar
import com.example.ui.components.DocumentCanvas
import com.example.ui.components.DocumentOutlineDrawer
import com.example.ui.components.EditTableDialog
import com.example.ui.components.FindReplaceDialog
import com.example.ui.components.FormattingToolbar
import com.example.ui.components.InsertChartDialog
import com.example.ui.components.InsertImageDialog
import com.example.ui.components.InsertShapeDialog
import com.example.ui.components.InsertTableDialog
import com.example.ui.components.PageLayoutDialog
import com.example.ui.theme.BengaliGreen
import com.example.ui.theme.LekhoniGoldAccent
import com.example.ui.theme.LekhoniInkPrimary
import com.example.ui.theme.NotoSansBengaliFamily
import com.example.viewmodel.AppLanguage
import com.example.viewmodel.EditorViewMode
import com.example.viewmodel.LekhoniViewModel
import com.example.viewmodel.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    viewModel: LekhoniViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    BackHandler {
        onBack()
    }

    val document by viewModel.activeDocument.collectAsState()
    val selectedIndex by viewModel.selectedBlockIndex.collectAsState()
    val viewMode by viewModel.editorViewMode.collectAsState()
    val language by viewModel.appLanguage.collectAsState()
    val isBn = (language == AppLanguage.BENGALI)
    val useBnNumerals by viewModel.useBengaliNumerals.collectAsState()

    val isAiGenerating by viewModel.isAiGenerating.collectAsState()
    val aiResultText by viewModel.aiResultText.collectAsState()
    val aiError by viewModel.aiErrorMessage.collectAsState()

    // Dialog state
    var showPageSetupDialog by remember { mutableStateOf(false) }
    var showInsertMenu by remember { mutableStateOf(false) }
    var showViewModeMenu by remember { mutableStateOf(false) }
    var showExportMenu by remember { mutableStateOf(false) }

    var showTableInsertDialog by remember { mutableStateOf(false) }
    var showChartInsertDialog by remember { mutableStateOf(false) }
    var showShapeInsertDialog by remember { mutableStateOf(false) }
    var showImageInsertDialog by remember { mutableStateOf(false) }
    var showFindReplaceDialog by remember { mutableStateOf(false) }
    var showAiSheet by remember { mutableStateOf(false) }

    var editingTableForBlockIndex by remember { mutableStateOf<Pair<Int, TableData>?>(null) }

    if (document == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("নথি লোড হচ্ছে...", fontFamily = NotoSansBengaliFamily)
        }
        return
    }

    val currentDoc = document!!
    val currentBlock = currentDoc.blocks.getOrNull(selectedIndex) ?: currentDoc.blocks.first()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DocumentOutlineDrawer(
                document = currentDoc,
                onNavigateToBlock = { idx ->
                    viewModel.selectedBlockIndex.value = idx
                },
                onAddComment = { blockId, text ->
                    viewModel.addComment(blockId, text)
                },
                onResolveComment = { commentId ->
                    viewModel.resolveComment(commentId)
                },
                onDeleteComment = { commentId ->
                    viewModel.deleteComment(commentId)
                },
                onClose = {
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        navigationIcon = {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier.testTag("editor_back_btn")
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        },
                        title = {
                            Column {
                                BasicTextField(
                                    value = currentDoc.title,
                                    onValueChange = { viewModel.updateDocumentTitle(it) },
                                    textStyle = TextStyle(
                                        fontFamily = NotoSansBengaliFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    ),
                                    cursorBrush = SolidColor(LekhoniInkPrimary),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("editor_doc_title_input")
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.CloudDone,
                                        contentDescription = null,
                                        tint = BengaliGreen,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = if (isBn) "সংরক্ষিত (Saved)" else "Saved",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        actions = {
                            // View Mode Menu
                            Box {
                                IconButton(
                                    onClick = { showViewModeMenu = true },
                                    modifier = Modifier.testTag("editor_view_mode_btn")
                                ) {
                                    Icon(
                                        when (viewMode) {
                                            EditorViewMode.MOBILE -> Icons.Default.Description
                                            EditorViewMode.DESKTOP -> Icons.Default.ViewAgenda
                                            EditorViewMode.PRINT_LAYOUT -> Icons.Default.Print
                                        },
                                        contentDescription = "View Mode"
                                    )
                                }
                                DropdownMenu(
                                    expanded = showViewModeMenu,
                                    onDismissRequest = { showViewModeMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("মোবাইল ভিউ (Mobile View)", fontFamily = NotoSansBengaliFamily) },
                                        onClick = {
                                            viewModel.setEditorViewMode(EditorViewMode.MOBILE)
                                            showViewModeMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("ডেস্কটপ ভিউ (Desktop Ribbon)", fontFamily = NotoSansBengaliFamily) },
                                        onClick = {
                                            viewModel.setEditorViewMode(EditorViewMode.DESKTOP)
                                            showViewModeMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("প্রিন্ট লেআউট (Print Layout)", fontFamily = NotoSansBengaliFamily) },
                                        onClick = {
                                            viewModel.setEditorViewMode(EditorViewMode.PRINT_LAYOUT)
                                            showViewModeMenu = false
                                        }
                                    )
                                }
                            }

                            // AI Assistant Button
                            IconButton(
                                onClick = { showAiSheet = true },
                                modifier = Modifier.testTag("open_ai_assistant_btn")
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(LekhoniInkPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.AutoAwesome,
                                        contentDescription = "Gemini AI",
                                        tint = LekhoniGoldAccent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Insert Tools Menu
                            Box {
                                IconButton(
                                    onClick = { showInsertMenu = true },
                                    modifier = Modifier.testTag("insert_elements_menu_btn")
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Insert")
                                }
                                DropdownMenu(
                                    expanded = showInsertMenu,
                                    onDismissRequest = { showInsertMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("ছক / টেবিল (Table)", fontFamily = NotoSansBengaliFamily) },
                                        leadingIcon = { Icon(Icons.Default.TableChart, contentDescription = null) },
                                        onClick = {
                                            showInsertMenu = false
                                            showTableInsertDialog = true
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("গ্রাফ ও চার্ট (Chart)", fontFamily = NotoSansBengaliFamily) },
                                        leadingIcon = { Icon(Icons.Default.TableChart, contentDescription = null) },
                                        onClick = {
                                            showInsertMenu = false
                                            showChartInsertDialog = true
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("আকৃতি / শেপ (Shape)", fontFamily = NotoSansBengaliFamily) },
                                        leadingIcon = { Icon(Icons.Default.Layers, contentDescription = null) },
                                        onClick = {
                                            showInsertMenu = false
                                            showShapeInsertDialog = true
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("ছবি (Image)", fontFamily = NotoSansBengaliFamily) },
                                        leadingIcon = { Icon(Icons.Default.Image, contentDescription = null) },
                                        onClick = {
                                            showInsertMenu = false
                                            showImageInsertDialog = true
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("পৃষ্ঠা বিরতি (Page Break)", fontFamily = NotoSansBengaliFamily) },
                                        onClick = {
                                            showInsertMenu = false
                                            viewModel.addBlockAfter(selectedIndex, BlockType.PAGE_BREAK)
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("বিভাজক রেখা (Divider)", fontFamily = NotoSansBengaliFamily) },
                                        onClick = {
                                            showInsertMenu = false
                                            viewModel.addBlockAfter(selectedIndex, BlockType.DIVIDER)
                                        }
                                    )
                                }
                            }

                            // Review / Outline Drawer Toggle
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                    }
                                },
                                modifier = Modifier.testTag("open_outline_drawer_btn")
                            ) {
                                Icon(Icons.Default.FormatListBulleted, contentDescription = "Outline & Comments")
                            }

                            // Export and Document Options Menu
                            Box {
                                IconButton(
                                    onClick = { showExportMenu = true },
                                    modifier = Modifier.testTag("editor_more_menu_btn")
                                ) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "More")
                                }
                                DropdownMenu(
                                    expanded = showExportMenu,
                                    onDismissRequest = { showExportMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text(if (isBn) "পৃষ্ঠা সেটআপ (Page Setup)" else "Page Setup", fontFamily = NotoSansBengaliFamily) },
                                        onClick = {
                                            showExportMenu = false
                                            showPageSetupDialog = true
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(if (isBn) "খুঁজুন ও প্রতিস্থাপন (Find & Replace)" else "Find & Replace", fontFamily = NotoSansBengaliFamily) },
                                        leadingIcon = { Icon(Icons.Default.FindReplace, contentDescription = null) },
                                        onClick = {
                                            showExportMenu = false
                                            showFindReplaceDialog = true
                                        }
                                    )
                                    Divider()
                                    DropdownMenuItem(
                                        text = { Text("পিডিএফ হিসেবে এক্সপোর্ট (Export PDF)", fontFamily = NotoSansBengaliFamily) },
                                        leadingIcon = { Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color.Red) },
                                        onClick = {
                                            showExportMenu = false
                                            val file = viewModel.exportPdf(context)
                                            if (file != null) {
                                                FileExporter.shareFile(context, file, "application/pdf", currentDoc.title)
                                            }
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("এইচটিএমএল হিসেবে এক্সপোর্ট (HTML)", fontFamily = NotoSansBengaliFamily) },
                                        leadingIcon = { Icon(Icons.Default.Code, contentDescription = null) },
                                        onClick = {
                                            showExportMenu = false
                                            val file = viewModel.exportHtml(context)
                                            if (file != null) {
                                                FileExporter.shareFile(context, file, "text/html", currentDoc.title)
                                            }
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("মার্কডাউন হিসেবে এক্সপোর্ট (Markdown)", fontFamily = NotoSansBengaliFamily) },
                                        onClick = {
                                            showExportMenu = false
                                            val file = viewModel.exportMarkdown(context)
                                            if (file != null) {
                                                FileExporter.shareFile(context, file, "text/markdown", currentDoc.title)
                                            }
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("সাধারণ টেক্সট ফাইল (TXT)", fontFamily = NotoSansBengaliFamily) },
                                        onClick = {
                                            showExportMenu = false
                                            val file = viewModel.exportPlainText(context)
                                            if (file != null) {
                                                FileExporter.shareFile(context, file, "text/plain", currentDoc.title)
                                            }
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("লেখনী ফাইল হিসেবে সংরক্ষণ (.lekhoni)", fontFamily = NotoSansBengaliFamily) },
                                        onClick = {
                                            showExportMenu = false
                                            val file = viewModel.exportLekhoniJson(context)
                                            if (file != null) {
                                                FileExporter.shareFile(context, file, "application/json", currentDoc.title)
                                            }
                                        }
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    // Word-Processor Formatting Toolbar
                    FormattingToolbar(
                        currentBlockType = currentBlock.type,
                        currentFormatting = currentBlock.formatting,
                        onBlockTypeChange = { newType ->
                            viewModel.changeBlockType(selectedIndex, newType)
                        },
                        onFormattingChange = { newFormatting ->
                            viewModel.updateBlockFormatting(selectedIndex, newFormatting)
                        },
                        canUndo = viewModel.canUndo,
                        canRedo = viewModel.canRedo,
                        onUndo = { viewModel.undo() },
                        onRedo = { viewModel.redo() },
                        onAddBlock = { type ->
                            viewModel.addBlockAfter(selectedIndex, type)
                        },
                        onDeleteBlock = {
                            viewModel.removeBlock(selectedIndex)
                        },
                        onMoveBlock = { up ->
                            viewModel.moveBlock(selectedIndex, up)
                        }
                    )

                    // Bengali Tools & Yuktakshar Strip
                    BengaliToolsBar(
                        onInsertSymbol = { sym ->
                            val currentText = currentBlock.text
                            viewModel.updateBlockText(selectedIndex, currentText + sym)
                        },
                        onToggleNumerals = {
                            viewModel.setUseBengaliNumerals(!useBnNumerals)
                        },
                        isBengaliNumeralsActive = useBnNumerals
                    )
                }
            },
            bottomBar = {
                // Document Info Footer
                Surface(
                    modifier = Modifier.fillMaxWidth().height(30.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isBn) "${currentDoc.wordCount()} টি শব্দ | ${currentDoc.charCount()} টি অক্ষর | ${currentDoc.paragraphCount()} টি অনুচ্ছেদ"
                            else "${currentDoc.wordCount()} words | ${currentDoc.charCount()} chars | ${currentDoc.paragraphCount()} paras",
                            fontSize = 11.sp,
                            fontFamily = NotoSansBengaliFamily,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${currentDoc.pageSettings.paperSize.name} • ${currentDoc.pageSettings.orientation.name}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        ) { innerPadding ->
            DocumentCanvas(
                document = currentDoc,
                viewMode = viewMode,
                selectedBlockIndex = selectedIndex,
                onSelectBlock = { viewModel.selectedBlockIndex.value = it },
                onUpdateBlockText = { idx, text ->
                    val finalText = if (useBnNumerals) viewModel.convertNumeralsToBengali(text) else text
                    viewModel.updateBlockText(idx, finalText)
                },
                onEditTable = { idx, tableData ->
                    editingTableForBlockIndex = Pair(idx, tableData)
                },
                onDeleteBlock = { idx ->
                    viewModel.removeBlock(idx)
                },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }

    // Dialogs
    if (showPageSetupDialog) {
        PageLayoutDialog(
            initialSettings = currentDoc.pageSettings,
            initialHeader = currentDoc.header,
            initialFooter = currentDoc.footer,
            onDismiss = { showPageSetupDialog = false },
            onSave = { newSettings, headerText, footerText, showPageNumber ->
                viewModel.updatePageSettings(
                    paperSize = newSettings.paperSize,
                    orientation = newSettings.orientation,
                    marginOption = newSettings.marginOption,
                    columns = newSettings.columns,
                    showBorder = newSettings.showBorder,
                    watermark = newSettings.watermark
                )
                viewModel.updateHeaderFooter(headerText, footerText, showPageNumber)
                showPageSetupDialog = false
            }
        )
    }

    if (showTableInsertDialog) {
        InsertTableDialog(
            onDismiss = { showTableInsertDialog = false },
            onConfirm = { tableData ->
                viewModel.addBlockAfter(selectedIndex, BlockType.TABLE)
                viewModel.updateTableData(selectedIndex + 1, tableData)
                showTableInsertDialog = false
            }
        )
    }

    if (editingTableForBlockIndex != null) {
        val (idx, tableData) = editingTableForBlockIndex!!
        EditTableDialog(
            tableData = tableData,
            onDismiss = { editingTableForBlockIndex = null },
            onSave = { updatedTable ->
                viewModel.updateTableData(idx, updatedTable)
                editingTableForBlockIndex = null
            }
        )
    }

    if (showChartInsertDialog) {
        InsertChartDialog(
            onDismiss = { showChartInsertDialog = false },
            onConfirm = { chartData ->
                viewModel.addBlockAfter(selectedIndex, BlockType.CHART)
                viewModel.updateChartData(selectedIndex + 1, chartData)
                showChartInsertDialog = false
            }
        )
    }

    if (showShapeInsertDialog) {
        InsertShapeDialog(
            onDismiss = { showShapeInsertDialog = false },
            onConfirm = { shapeData ->
                viewModel.addBlockAfter(selectedIndex, BlockType.SHAPE)
                viewModel.updateShapeData(selectedIndex + 1, shapeData)
                showShapeInsertDialog = false
            }
        )
    }

    if (showImageInsertDialog) {
        InsertImageDialog(
            onDismiss = { showImageInsertDialog = false },
            onConfirm = { imgData ->
                viewModel.addBlockAfter(selectedIndex, BlockType.IMAGE)
                val doc = viewModel.activeDocument.value
                val newIdx = selectedIndex + 1
                if (doc != null && newIdx in doc.blocks.indices) {
                    val updatedBlocks = doc.blocks.toMutableList()
                    updatedBlocks[newIdx] = updatedBlocks[newIdx].copy(imageData = imgData)
                    viewModel.openDocument(doc.id)
                }
                showImageInsertDialog = false
            }
        )
    }

    if (showFindReplaceDialog) {
        FindReplaceDialog(
            onDismiss = { showFindReplaceDialog = false },
            onReplace = { find, replace ->
                viewModel.findAndReplace(find, replace)
                showFindReplaceDialog = false
            }
        )
    }

    if (showAiSheet) {
        AiAssistantSheet(
            targetText = currentBlock.text.ifBlank { currentDoc.toPlainText().take(300) },
            isGenerating = isAiGenerating,
            generatedText = aiResultText,
            errorMessage = aiError,
            onExecuteAction = { action, customPrompt ->
                val txt = currentBlock.text.ifBlank { currentDoc.toPlainText().take(600) }
                viewModel.runAiAction(action, txt, customPrompt)
            },
            onApplyToCurrentBlock = {
                viewModel.applyAiResultToCurrentBlock()
                showAiSheet = false
            },
            onAppendAsNewBlock = {
                viewModel.appendAiResultAsNewBlock()
                showAiSheet = false
            },
            onDismiss = {
                showAiSheet = false
            }
        )
    }
}
