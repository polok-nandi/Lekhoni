package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.AiActionType
import com.example.ui.theme.BengaliGreen
import com.example.ui.theme.LekhoniGoldAccent
import com.example.ui.theme.LekhoniInkPrimary
import com.example.ui.theme.NotoSansBengaliFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantSheet(
    targetText: String,
    isGenerating: Boolean,
    generatedText: String?,
    errorMessage: String?,
    onExecuteAction: (AiActionType, customPrompt: String?) -> Unit,
    onApplyToCurrentBlock: () -> Unit,
    onAppendAsNewBlock: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedAction by remember { mutableStateOf(AiActionType.IMPROVE_BENGALI_GRAMMAR) }
    var customInstruction by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current
    var copiedNotification by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(LekhoniInkPrimary, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = LekhoniGoldAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            "লেখনী কৃত্রিম বুদ্ধিমত্তা (Gemini AI)",
                            fontFamily = NotoSansBengaliFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = LekhoniInkPrimary
                        )
                        Text(
                            "Powered by Google Gemini 3.5 Flash",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("বন্ধ করুন", fontFamily = NotoSansBengaliFamily)
                }
            }

            // Target Context Preview
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        "নির্বাচিত অংশ (Selected Context):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = targetText.ifBlank { "(সমগ্র নথি থেকে প্রাসঙ্গিক বিষয়বস্তু)" },
                        fontSize = 13.sp,
                        maxLines = 3,
                        fontFamily = NotoSansBengaliFamily,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Quick AI Actions list
            Text(
                "করণীয় নির্বাচন করুন (Choose AI Action):",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = NotoSansBengaliFamily
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AiActionType.values().forEach { action ->
                    FilterChip(
                        selected = (selectedAction == action),
                        onClick = { selectedAction = action },
                        label = {
                            Text(
                                text = action.displayNameBn,
                                fontSize = 12.sp,
                                fontFamily = NotoSansBengaliFamily
                            )
                        },
                        modifier = Modifier.testTag("ai_chip_${action.name}")
                    )
                }
            }

            // Custom Prompt Field
            OutlinedTextField(
                value = customInstruction,
                onValueChange = { customInstruction = it },
                label = { Text("কাস্টম নির্দেশনা বা প্রম্পট (ঐচ্ছিক)", fontFamily = NotoSansBengaliFamily) },
                placeholder = { Text("যেমন: আরও সহজ করে ৩টি বুলেট পয়েন্টে লিখুন...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("ai_custom_prompt_input")
            )

            // Generate Button
            Button(
                onClick = {
                    onExecuteAction(selectedAction, customInstruction.takeIf { it.isNotBlank() })
                },
                enabled = !isGenerating,
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("run_ai_btn"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LekhoniInkPrimary)
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.size(8.dp))
                    Text("AI রচনা করছে...", fontFamily = NotoSansBengaliFamily)
                } else {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.size(8.dp))
                    Text("উৎপাদন করুন (Generate with AI)", fontFamily = NotoSansBengaliFamily, fontWeight = FontWeight.Bold)
                }
            }

            // Error display
            if (errorMessage != null) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "ত্রুটি: $errorMessage",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 12.sp,
                        fontFamily = NotoSansBengaliFamily,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            // AI Generated Result Card
            if (generatedText != null) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(10.dp),
                    tonalElevation = 3.dp,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "AI ফলাফল (Generated Draft):",
                                fontWeight = FontWeight.Bold,
                                color = BengaliGreen,
                                fontSize = 13.sp,
                                fontFamily = NotoSansBengaliFamily
                            )
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(generatedText))
                                    copiedNotification = true
                                },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(18.dp))
                            }
                        }

                        Text(
                            text = generatedText,
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            fontFamily = NotoSansBengaliFamily
                        )

                        if (copiedNotification) {
                            Text("ক্লিপবোর্ডে কপি করা হয়েছে!", fontSize = 11.sp, color = BengaliGreen)
                        }

                        // Apply Options
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onApplyToCurrentBlock,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).testTag("ai_replace_block_btn")
                            ) {
                                Text("বর্তমান অংশে বসান (Replace)", fontSize = 12.sp, fontFamily = NotoSansBengaliFamily)
                            }
                            OutlinedButton(
                                onClick = onAppendAsNewBlock,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).testTag("ai_append_block_btn")
                            ) {
                                Text("নতুন অনুচ্ছেদ (Append)", fontSize = 12.sp, fontFamily = NotoSansBengaliFamily)
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}
