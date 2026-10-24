package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.export.FileExporter
import com.example.model.LekhoniDocument
import com.example.model.StarterTemplates
import com.example.ui.theme.BengaliGreen
import com.example.ui.theme.LekhoniGoldAccent
import com.example.ui.theme.LekhoniInkPrimary
import com.example.ui.theme.NotoSansBengaliFamily
import com.example.viewmodel.AppLanguage
import com.example.viewmodel.LekhoniViewModel
import com.example.viewmodel.Screen
import com.example.viewmodel.SortOrder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: LekhoniViewModel,
    onOpenDocument: (String) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val context = LocalContext.current
    val language by viewModel.appLanguage.collectAsState()
    val isBn = (language == AppLanguage.BENGALI)

    val documents by viewModel.displayedDocuments.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filterStarred by viewModel.filterStarred.collectAsState()
    val isGridView by viewModel.isGridView.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    val storageBytes by viewModel.storageBytes.collectAsState()
    val docCount by viewModel.documentCount.collectAsState()

    var showRenameDialogForDoc by remember { mutableStateOf<LekhoniDocument?>(null) }
    var renameText by remember { mutableStateOf("") }

    var showDeleteConfirmForDoc by remember { mutableStateOf<LekhoniDocument?>(null) }
    var showImportDialog by remember { mutableStateOf(false) }
    var importRawText by remember { mutableStateOf("") }
    var importTitle by remember { mutableStateOf("আমদানিকৃত নথি") }

    var showSortMenu by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(LekhoniInkPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "ল",
                                color = LekhoniGoldAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                fontFamily = NotoSansBengaliFamily
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isBn) "লেখনী" else "Lekhoni",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                fontFamily = NotoSansBengaliFamily
                            )
                            Text(
                                text = if (isBn) "বাংলা অফিস স্যুট" else "Open Office Suite",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    // Import Document button
                    IconButton(
                        onClick = { showImportDialog = true },
                        modifier = Modifier.testTag("action_import_doc_btn")
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = "Import Document")
                    }

                    // Grid / List View Toggle
                    IconButton(
                        onClick = { viewModel.isGridView.value = !isGridView },
                        modifier = Modifier.testTag("view_toggle_btn")
                    ) {
                        Icon(
                            if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                            contentDescription = "Toggle Grid/List View"
                        )
                    }

                    // Sort menu
                    Box {
                        IconButton(
                            onClick = { showSortMenu = true },
                            modifier = Modifier.testTag("sort_menu_btn")
                        ) {
                            Icon(Icons.Default.Sort, contentDescription = "Sort")
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (isBn) "সর্বশেষ সম্পাদিত (নতুন প্রথমে)" else "Date Modified (Newest)", fontFamily = NotoSansBengaliFamily) },
                                onClick = {
                                    viewModel.sortOrder.value = SortOrder.DATE_MODIFIED_DESC
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(if (isBn) "পূর্বে সম্পাদিত (পুরাতন প্রথমে)" else "Date Modified (Oldest)", fontFamily = NotoSansBengaliFamily) },
                                onClick = {
                                    viewModel.sortOrder.value = SortOrder.DATE_MODIFIED_ASC
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(if (isBn) "শিরোনাম অনুযায়ী (ক-হ / A-Z)" else "Name (A-Z)", fontFamily = NotoSansBengaliFamily) },
                                onClick = {
                                    viewModel.sortOrder.value = SortOrder.TITLE_ASC
                                    showSortMenu = false
                                }
                            )
                        }
                    }

                    // Settings Button
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier.testTag("settings_btn")
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.createNewDocument("blank") },
                containerColor = LekhoniInkPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("fab_new_document")
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Document")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.searchQuery.value = it },
                        placeholder = {
                            Text(
                                text = if (isBn) "নথির শিরোনাম বা লেখা খুঁজুন..." else "Search documents or text...",
                                fontSize = 13.sp,
                                fontFamily = NotoSansBengaliFamily
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        singleLine = true,
                        colors = androidx.compose.material3.TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.weight(1f).testTag("search_doc_input")
                    )
                }
            }

            // Starter Templates Section
            Column(modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 8.dp)) {
                Text(
                    text = if (isBn) "রেডিমেড টেমপ্লেট (Starter Templates)" else "Templates Gallery",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = LekhoniInkPrimary,
                    fontFamily = NotoSansBengaliFamily,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StarterTemplates.templates.forEach { tmpl ->
                        Card(
                            onClick = { viewModel.createNewDocument(tmpl.id) },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(10.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier
                                .width(135.dp)
                                .height(105.dp)
                                .testTag("template_card_${tmpl.id}")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .background(LekhoniInkPrimary.copy(alpha = 0.1f), RoundedCornerShape(6.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Description,
                                        contentDescription = null,
                                        tint = LekhoniInkPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = if (isBn) tmpl.nameBn else tmpl.nameEn,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        fontFamily = NotoSansBengaliFamily
                                    )
                                    Text(
                                        text = if (isBn) tmpl.descriptionBn else tmpl.descriptionEn,
                                        fontSize = 10.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontFamily = NotoSansBengaliFamily
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Tabs: All Documents vs Starred
            TabRow(
                selectedTabIndex = if (filterStarred) 1 else 0,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = !filterStarred,
                    onClick = { viewModel.filterStarred.value = false },
                    text = {
                        Text(
                            text = if (isBn) "সকল নথি (${documents.size})" else "All Documents (${documents.size})",
                            fontFamily = NotoSansBengaliFamily
                        )
                    }
                )
                Tab(
                    selected = filterStarred,
                    onClick = { viewModel.filterStarred.value = true },
                    text = {
                        Text(
                            text = if (isBn) "তারকাচিহ্নিত (Starred)" else "Starred",
                            fontFamily = NotoSansBengaliFamily
                        )
                    }
                )
            }

            // Storage status strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBn) "সংরক্ষণাগার: ${storageBytes / 1024} KB ব্যবহৃত | মোট $docCount টি নথি"
                    else "Storage: ${storageBytes / 1024} KB used | $docCount total docs",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Documents List or Grid
            if (documents.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Description,
                            contentDescription = null,
                            tint = Color.Gray.copy(alpha = 0.5f),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = if (isBn) "কোন নথি পাওয়া যায়নি" else "No documents found",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            fontFamily = NotoSansBengaliFamily
                        )
                        Text(
                            text = if (isBn) "নতুন নথি তৈরি করতে নিচের + বোতাম চাপুন" else "Tap + to create a new document",
                            fontSize = 12.sp,
                            color = Color.Gray.copy(alpha = 0.8f),
                            fontFamily = NotoSansBengaliFamily
                        )
                    }
                }
            } else {
                if (isGridView) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(documents) { doc ->
                            DocumentGridCard(
                                doc = doc,
                                isBn = isBn,
                                dateFormat = dateFormat,
                                onOpen = { onOpenDocument(doc.id) },
                                onToggleStar = { viewModel.toggleStarred(doc.id, doc.isStarred) },
                                onDuplicate = { viewModel.duplicateDocument(doc) },
                                onRename = {
                                    showRenameDialogForDoc = doc
                                    renameText = doc.title
                                },
                                onDelete = { showDeleteConfirmForDoc = doc },
                                onExportPdf = {
                                    val file = viewModel.exportPdf(context)
                                    if (file != null) {
                                        FileExporter.shareFile(context, file, "application/pdf", doc.title)
                                    }
                                }
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(documents) { doc ->
                            DocumentListItem(
                                doc = doc,
                                isBn = isBn,
                                dateFormat = dateFormat,
                                onOpen = { onOpenDocument(doc.id) },
                                onToggleStar = { viewModel.toggleStarred(doc.id, doc.isStarred) },
                                onDuplicate = { viewModel.duplicateDocument(doc) },
                                onRename = {
                                    showRenameDialogForDoc = doc
                                    renameText = doc.title
                                },
                                onDelete = { showDeleteConfirmForDoc = doc },
                                onExportPdf = {
                                    val file = viewModel.exportPdf(context)
                                    if (file != null) {
                                        FileExporter.shareFile(context, file, "application/pdf", doc.title)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Rename Dialog
    if (showRenameDialogForDoc != null) {
        val doc = showRenameDialogForDoc!!
        AlertDialog(
            onDismissRequest = { showRenameDialogForDoc = null },
            title = { Text(if (isBn) "নথির নাম পরিবর্তন" else "Rename Document", fontFamily = NotoSansBengaliFamily) },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    label = { Text(if (isBn) "নথির শিরোনাম" else "Document Title", fontFamily = NotoSansBengaliFamily) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("rename_input_field")
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.renameDocument(doc.id, renameText)
                        showRenameDialogForDoc = null
                    },
                    modifier = Modifier.testTag("confirm_rename_btn")
                ) {
                    Text(if (isBn) "সংরক্ষণ" else "Save", fontFamily = NotoSansBengaliFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialogForDoc = null }) {
                    Text(if (isBn) "বাতিল" else "Cancel", fontFamily = NotoSansBengaliFamily)
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmForDoc != null) {
        val doc = showDeleteConfirmForDoc!!
        AlertDialog(
            onDismissRequest = { showDeleteConfirmForDoc = null },
            title = { Text(if (isBn) "নথি মুছে ফেলতে চান?" else "Delete Document?", fontFamily = NotoSansBengaliFamily) },
            text = {
                Text(
                    text = if (isBn) "'${doc.title}' নথিটি স্থায়ীভাবে মুছে যাবে।"
                    else "Are you sure you want to permanently delete '${doc.title}'?",
                    fontFamily = NotoSansBengaliFamily
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteDocument(doc.id)
                        showDeleteConfirmForDoc = null
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.testTag("confirm_delete_btn")
                ) {
                    Text(if (isBn) "মুছে ফেলুন (Delete)" else "Delete", color = Color.White, fontFamily = NotoSansBengaliFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmForDoc = null }) {
                    Text(if (isBn) "বাতিল" else "Cancel", fontFamily = NotoSansBengaliFamily)
                }
            }
        )
    }

    // Import Document Dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text(if (isBn) "নথি ইম্পোর্ট করুন (Import Document)" else "Import Document", fontFamily = NotoSansBengaliFamily) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        if (isBn) "TXT, Markdown বা Lekhoni JSON কোড এখানে পেস্ট করুন:"
                        else "Paste TXT, Markdown, or Lekhoni JSON content:",
                        fontSize = 12.sp,
                        fontFamily = NotoSansBengaliFamily
                    )
                    OutlinedTextField(
                        value = importTitle,
                        onValueChange = { importTitle = it },
                        label = { Text(if (isBn) "নথির নাম" else "Document Title", fontFamily = NotoSansBengaliFamily) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = importRawText,
                        onValueChange = { importRawText = it },
                        label = { Text(if (isBn) "নথির বিষয়বস্তু / লেখা" else "Content", fontFamily = NotoSansBengaliFamily) },
                        maxLines = 8,
                        modifier = Modifier.fillMaxWidth().height(160.dp).testTag("import_content_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (importRawText.isNotBlank()) {
                            viewModel.importDocumentFromText(importTitle, importRawText)
                            showImportDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_import_btn")
                ) {
                    Text(if (isBn) "আমদানি করুন (Import)" else "Import", fontFamily = NotoSansBengaliFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text(if (isBn) "বাতিল" else "Cancel", fontFamily = NotoSansBengaliFamily)
                }
            }
        )
    }
}

@Composable
fun DocumentListItem(
    doc: LekhoniDocument,
    isBn: Boolean,
    dateFormat: SimpleDateFormat,
    onOpen: () -> Unit,
    onToggleStar: () -> Unit,
    onDuplicate: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onExportPdf: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        onClick = onOpen,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth().testTag("doc_item_${doc.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(LekhoniInkPrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Description,
                    contentDescription = null,
                    tint = LekhoniInkPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = doc.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = NotoSansBengaliFamily
                )
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = dateFormat.format(Date(doc.updatedAt)),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(" • ", fontSize = 11.sp, color = Color.Gray)
                    Text(
                        text = "${doc.wordCount()} শব্দ | ${doc.pageSettings.paperSize.name}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = NotoSansBengaliFamily
                    )
                }
            }

            IconButton(onClick = onToggleStar, modifier = Modifier.size(36.dp)) {
                Icon(
                    if (doc.isStarred) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "Star",
                    tint = if (doc.isStarred) LekhoniGoldAccent else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }

            Box {
                IconButton(onClick = { showMenu = true }, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More", modifier = Modifier.size(20.dp))
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(if (isBn) "খুলুন" else "Open", fontFamily = NotoSansBengaliFamily) },
                        leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                        onClick = {
                            showMenu = false
                            onOpen()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(if (isBn) "পিডিএফ শেয়ার করুন" else "Share as PDF", fontFamily = NotoSansBengaliFamily) },
                        leadingIcon = { Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color.Red) },
                        onClick = {
                            showMenu = false
                            onExportPdf()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(if (isBn) "প্রতিলিপি তৈরি (Duplicate)" else "Duplicate", fontFamily = NotoSansBengaliFamily) },
                        leadingIcon = { Icon(Icons.Default.FileDownload, contentDescription = null) },
                        onClick = {
                            showMenu = false
                            onDuplicate()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(if (isBn) "পুনঃনামকরণ (Rename)" else "Rename", fontFamily = NotoSansBengaliFamily) },
                        leadingIcon = { Icon(Icons.Default.DriveFileRenameOutline, contentDescription = null) },
                        onClick = {
                            showMenu = false
                            onRename()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(if (isBn) "মুছে ফেলুন (Delete)" else "Delete", color = Color.Red, fontFamily = NotoSansBengaliFamily) },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) },
                        onClick = {
                            showMenu = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DocumentGridCard(
    doc: LekhoniDocument,
    isBn: Boolean,
    dateFormat: SimpleDateFormat,
    onOpen: () -> Unit,
    onToggleStar: () -> Unit,
    onDuplicate: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onExportPdf: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        onClick = onOpen,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .testTag("doc_grid_item_${doc.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(LekhoniInkPrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Description,
                        contentDescription = null,
                        tint = LekhoniInkPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Row {
                    IconButton(onClick = onToggleStar, modifier = Modifier.size(28.dp)) {
                        Icon(
                            if (doc.isStarred) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Star",
                            tint = if (doc.isStarred) LekhoniGoldAccent else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Box {
                        IconButton(onClick = { showMenu = true }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu", modifier = Modifier.size(18.dp))
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (isBn) "পিডিএফ শেয়ার" else "Share PDF", fontFamily = NotoSansBengaliFamily) },
                                onClick = {
                                    showMenu = false
                                    onExportPdf()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(if (isBn) "প্রতিলিপি" else "Duplicate", fontFamily = NotoSansBengaliFamily) },
                                onClick = {
                                    showMenu = false
                                    onDuplicate()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(if (isBn) "পুনঃনামকরণ" else "Rename", fontFamily = NotoSansBengaliFamily) },
                                onClick = {
                                    showMenu = false
                                    onRename()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(if (isBn) "মুছুন" else "Delete", color = Color.Red, fontFamily = NotoSansBengaliFamily) },
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                }
                            )
                        }
                    }
                }
            }

            Column {
                Text(
                    text = doc.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = NotoSansBengaliFamily
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = dateFormat.format(Date(doc.updatedAt)),
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${doc.wordCount()} শব্দ",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = NotoSansBengaliFamily
                )
            }
        }
    }
}
