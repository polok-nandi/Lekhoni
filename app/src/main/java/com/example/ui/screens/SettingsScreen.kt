package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.GeminiService
import com.example.export.FileExporter
import com.example.ui.theme.BengaliGreen
import com.example.ui.theme.LekhoniGoldAccent
import com.example.ui.theme.LekhoniInkPrimary
import com.example.ui.theme.NotoSansBengaliFamily
import com.example.ui.theme.NotoSerifBengaliFamily
import com.example.viewmodel.AppLanguage
import com.example.viewmodel.EditorViewMode
import com.example.viewmodel.LekhoniViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: LekhoniViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    BackHandler { onBack() }

    val language by viewModel.appLanguage.collectAsState()
    val isBn = (language == AppLanguage.BENGALI)
    val useBnNumerals by viewModel.useBengaliNumerals.collectAsState()
    val currentApiKey by viewModel.geminiApiKey.collectAsState()
    val defaultViewMode by viewModel.editorViewMode.collectAsState()
    val storageBytes by viewModel.storageBytes.collectAsState()
    val docCount by viewModel.documentCount.collectAsState()

    var apiKeyInput by remember { mutableStateOf(currentApiKey) }
    var isTestingConnection by remember { mutableStateOf(false) }
    var connectionTestResult by remember { mutableStateOf<String?>(null) }
    var fontPreviewText by remember { mutableStateOf("আমার সোনার বাংলা, আমি তোমায় ভালোবাসি। যুক্তাক্ষর: ক্ষ, জ্ঞ, ষ্ণ, ঙ্গ, ত্র") }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("settings_back_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    Text(
                        text = if (isBn) "সেটিংস ও তথ্য (Settings)" else "Settings & About",
                        fontWeight = FontWeight.Bold,
                        fontFamily = NotoSansBengaliFamily
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Language & Numerals Section
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = LekhoniInkPrimary)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (isBn) "ভাষা ও সংখ্যা রূপান্তর (Language & Numerals)" else "Language & Numerals",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            fontFamily = NotoSansBengaliFamily
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = isBn,
                            onClick = { viewModel.setAppLanguage(AppLanguage.BENGALI) },
                            label = { Text("বাংলা (Bengali)", fontFamily = NotoSansBengaliFamily) },
                            modifier = Modifier.testTag("lang_bn_chip")
                        )
                        FilterChip(
                            selected = !isBn,
                            onClick = { viewModel.setAppLanguage(AppLanguage.ENGLISH) },
                            label = { Text("English", fontFamily = NotoSansBengaliFamily) },
                            modifier = Modifier.testTag("lang_en_chip")
                        )
                    }

                    Divider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isBn) "বাংলা সংখ্যা স্বয়ংক্রিয় রূপান্তর (১২৩৪৫৬৭৮৯০)" else "Convert to Bengali Numerals",
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp,
                                fontFamily = NotoSansBengaliFamily
                            )
                            Text(
                                text = if (isBn) "টাইপ করার সময় ইংরেজি সংখ্যাগুলোকে সরাসরি বাংলা সংখ্যায় রূপান্তর করে।"
                                else "Automatically transforms English digits into Bengali numerals during typing.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontFamily = NotoSansBengaliFamily
                            )
                        }
                        Switch(
                            checked = useBnNumerals,
                            onCheckedChange = { viewModel.setUseBengaliNumerals(it) },
                            modifier = Modifier.testTag("bengali_numerals_switch")
                        )
                    }
                }
            }

            // 2. Default View Mode
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isBn) "ডিফল্ট ভিউ মোড (Default View Mode)" else "Default View Mode",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        fontFamily = NotoSansBengaliFamily
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        EditorViewMode.values().forEach { mode ->
                            FilterChip(
                                selected = (defaultViewMode == mode),
                                onClick = { viewModel.setEditorViewMode(mode) },
                                label = {
                                    Text(
                                        when (mode) {
                                            EditorViewMode.MOBILE -> if (isBn) "মোবাইল" else "Mobile"
                                            EditorViewMode.DESKTOP -> if (isBn) "ডেস্কটপ" else "Desktop"
                                            EditorViewMode.PRINT_LAYOUT -> if (isBn) "প্রিন্ট লেআউট" else "Print Layout"
                                        },
                                        fontFamily = NotoSansBengaliFamily
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // 3. Gemini AI Configuration
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = LekhoniGoldAccent)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (isBn) "কৃত্রিম বুদ্ধিমত্তা কনফিগারেশন (Gemini AI)" else "Gemini AI Settings",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            fontFamily = NotoSansBengaliFamily
                        )
                    }

                    Text(
                        text = if (isBn) "Google Gemini 3.5 Flash মডেল ব্যবহার করে বাংলা বানান, ব্যাকরণ সংশোধন ও চিঠি তৈরি করা হয়। আপনি নিজস্ব API Key ব্যবহার করতে পারেন।"
                        else "Gemini 3.5 Flash powers Bengali grammar review, rewriting, and translation. You can provide your own API key.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = NotoSansBengaliFamily
                    )

                    OutlinedTextField(
                        value = apiKeyInput,
                        onValueChange = { apiKeyInput = it },
                        label = { Text("Gemini API Key", fontFamily = NotoSansBengaliFamily) },
                        placeholder = { Text("AIzaSy...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("gemini_api_key_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.setGeminiApiKey(apiKeyInput)
                                Toast.makeText(context, if (isBn) "API Key সংরক্ষিত হয়েছে" else "API Key saved", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f).testTag("save_api_key_btn")
                        ) {
                            Text(if (isBn) "সংরক্ষণ করুন" else "Save Key", fontFamily = NotoSansBengaliFamily)
                        }

                        OutlinedButton(
                            onClick = {
                                isTestingConnection = true
                                connectionTestResult = null
                                scope.launch {
                                    val res = GeminiService.testConnection(apiKeyInput)
                                    isTestingConnection = false
                                    connectionTestResult = if (res.isSuccess) {
                                        res.getOrNull()
                                    } else {
                                        "ত্রুটি: ${res.exceptionOrNull()?.message}"
                                    }
                                }
                            },
                            enabled = !isTestingConnection && apiKeyInput.isNotBlank(),
                            modifier = Modifier.weight(1f).testTag("test_api_key_btn")
                        ) {
                            if (isTestingConnection) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            } else {
                                Text(if (isBn) "সংযোগ পরীক্ষা" else "Test Connection", fontFamily = NotoSansBengaliFamily)
                            }
                        }
                    }

                    if (connectionTestResult != null) {
                        Surface(
                            color = if (connectionTestResult!!.startsWith("ত্রুটি") || connectionTestResult!!.startsWith("Error"))
                                MaterialTheme.colorScheme.errorContainer else BengaliGreen.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = connectionTestResult!!,
                                fontSize = 12.sp,
                                fontFamily = NotoSansBengaliFamily,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }

            // 4. Bengali Typography & Font Manager
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FontDownload, contentDescription = null, tint = LekhoniInkPrimary)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (isBn) "বাংলা টাইপোগ্রাফি ও ফন্ট ম্যানেজার (Typography)" else "Bengali Typography & Fonts",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            fontFamily = NotoSansBengaliFamily
                        )
                    }

                    Text(
                        text = if (isBn) "লেখনীতে গুগল ফন্টস Noto Sans Bengali ও Noto Serif Bengali সরাসরি সংযুক্ত রয়েছে। কালপুরুষ, সোলাইমানলিপি ও সিয়াম রূপালীর জন্য ফলব্যাক সমর্থিত।"
                        else "Lekhoni bundles Noto Sans & Serif Bengali locally. Fallbacks for Kalpurush, SolaimanLipi, and Siyam Rupali are supported.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = NotoSansBengaliFamily
                    )

                    OutlinedTextField(
                        value = fontPreviewText,
                        onValueChange = { fontPreviewText = it },
                        label = { Text(if (isBn) "ফন্ট প্রিভিউ লিখুন" else "Font Preview Text", fontFamily = NotoSansBengaliFamily) },
                        modifier = Modifier.fillMaxWidth().testTag("font_preview_input")
                    )

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Noto Sans Bengali:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(fontPreviewText, fontFamily = NotoSansBengaliFamily, fontSize = 15.sp)
                            Divider()
                            Text("Noto Serif Bengali:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(fontPreviewText, fontFamily = NotoSerifBengaliFamily, fontSize = 15.sp)
                        }
                    }

                    Text(
                        text = "ফন্ট লাইসেন্স: SIL Open Font License (OFL 1.1)",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }

            // 5. Storage & Backups
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Storage, contentDescription = null, tint = LekhoniInkPrimary)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (isBn) "স্থানীয় ডেটা ও ব্যাকআপ (Storage)" else "Local Storage & Data",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            fontFamily = NotoSansBengaliFamily
                        )
                    }

                    Text(
                        text = if (isBn) "মোট সংরক্ষিত নথি: $docCount টি | ব্যবহৃত জায়গা: ${storageBytes / 1024} KB"
                        else "Total saved documents: $docCount | Disk footprint: ${storageBytes / 1024} KB",
                        fontSize = 13.sp,
                        fontFamily = NotoSansBengaliFamily
                    )

                    Button(
                        onClick = {
                            val activeDoc = viewModel.activeDocument.value
                            if (activeDoc != null) {
                                val file = viewModel.exportLekhoniJson(context)
                                if (file != null) {
                                    FileExporter.shareFile(context, file, "application/json", activeDoc.title)
                                }
                            } else {
                                Toast.makeText(context, if (isBn) "কোন সক্রিয় নথি নেই" else "No active document", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("export_backup_btn")
                    ) {
                        Text(if (isBn) "বর্তমান নথির ব্যাকআপ ডাউনলোড (.lekhoni)" else "Export Document Backup (.lekhoni)", fontFamily = NotoSansBengaliFamily)
                    }
                }
            }

            // 6. About Lekhoni & Creator Credit
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(LekhoniInkPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "ল",
                                color = LekhoniGoldAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                fontFamily = NotoSansBengaliFamily
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "লেখনী (Lekhoni)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                fontFamily = NotoSansBengaliFamily,
                                color = LekhoniInkPrimary
                            )
                            Text(
                                text = "A Bengali-friendly, open-source document editor.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Divider()

                    Text(
                        text = "উদ্যোক্তা ও মূল নির্মাতা: পলক নন্দী (Polok Nandi)",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        fontFamily = NotoSansBengaliFamily
                    )
                    Text(
                        text = "যোগাযোগ: nandipolok@gmail.com",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "সংস্করণ (Version): 1.0.0 (মুক্ত-উৎস সংস্করণ)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "লাইসেন্স: MIT License (উন্মুক্ত ও পরিবর্তনযোগ্য)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "লেখনী একটি ওপেন-সোর্স বাংলা ডকুমেন্ট এডিটর ও অফিস স্যুট। এটি সম্পূর্ণ অফলাইনে কাজ করে এবং ব্যবহারকারীর ব্যক্তিগত নথিপত্র সম্পূর্ণ নিরাপদ রাখে।",
                            fontSize = 11.sp,
                            fontFamily = NotoSansBengaliFamily,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}
