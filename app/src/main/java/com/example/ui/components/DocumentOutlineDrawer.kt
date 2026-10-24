package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BlockType
import com.example.model.DocumentComment
import com.example.model.LekhoniDocument
import com.example.ui.theme.BengaliGreen
import com.example.ui.theme.LekhoniInkPrimary
import com.example.ui.theme.NotoSansBengaliFamily

@Composable
fun DocumentOutlineDrawer(
    document: LekhoniDocument,
    onNavigateToBlock: (Int) -> Unit,
    onAddComment: (blockId: String, text: String) -> Unit,
    onResolveComment: (String) -> Unit,
    onDeleteComment: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    var newCommentText by remember { mutableStateOf("") }

    val headingsWithIndex = document.blocks.mapIndexedNotNull { index, block ->
        if (block.type in listOf(BlockType.HEADING_1, BlockType.HEADING_2, BlockType.HEADING_3)) {
            Triple(index, block.type, block.text)
        } else null
    }

    Surface(
        modifier = modifier
            .fillMaxHeight()
            .width(320.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "নথি পর্যালোচনা (Review Pane)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    fontFamily = NotoSansBengaliFamily,
                    color = LekhoniInkPrimary
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("সূচিপত্র (Outline)", fontFamily = NotoSansBengaliFamily) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("মন্তব্য (${document.comments.size})", fontFamily = NotoSansBengaliFamily) }
                )
            }

            Spacer(Modifier.height(12.dp))

            if (selectedTab == 0) {
                // Outline Headings
                if (headingsWithIndex.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        Text(
                            text = "কোন শিরোনাম নেই\n(Heading 1-3 যুক্ত করুন)",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp,
                            fontFamily = NotoSansBengaliFamily
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(headingsWithIndex) { (index, type, text) ->
                            val indent = when (type) {
                                BlockType.HEADING_1 -> 0.dp
                                BlockType.HEADING_2 -> 16.dp
                                else -> 28.dp
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = indent)
                                    .clipRoundedCorner(6.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .clickable {
                                        onNavigateToBlock(index)
                                        onClose()
                                    }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.FormatListBulleted,
                                    contentDescription = null,
                                    tint = LekhoniInkPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = text.ifBlank { "(শিরোনামহীন)" },
                                    fontSize = 13.sp,
                                    fontWeight = if (type == BlockType.HEADING_1) FontWeight.Bold else FontWeight.Medium,
                                    fontFamily = NotoSansBengaliFamily,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            } else {
                // Comments Tab
                Column(modifier = Modifier.weight(1f)) {
                    if (document.comments.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                            Text(
                                text = "কোন মন্তব্য নেই",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp,
                                fontFamily = NotoSansBengaliFamily
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(document.comments) { c ->
                                Surface(
                                    color = if (c.isResolved) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(8.dp),
                                    tonalElevation = 2.dp,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = c.author,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = LekhoniInkPrimary
                                            )
                                            Row {
                                                IconButton(
                                                    onClick = { onResolveComment(c.id) },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Default.Check,
                                                        contentDescription = "Resolve",
                                                        tint = if (c.isResolved) BengaliGreen else Color.Gray,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                                IconButton(
                                                    onClick = { onDeleteComment(c.id) },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Default.Delete,
                                                        contentDescription = "Delete",
                                                        tint = Color.Red,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = c.text,
                                            fontSize = 13.sp,
                                            fontFamily = NotoSansBengaliFamily,
                                            color = if (c.isResolved) Color.Gray else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Add new comment input
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newCommentText,
                        onValueChange = { newCommentText = it },
                        placeholder = { Text("নতুন মন্তব্য লিখুন...", fontFamily = NotoSansBengaliFamily, fontSize = 12.sp) },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    if (newCommentText.isNotBlank()) {
                                        val firstBlockId = document.blocks.firstOrNull()?.id ?: ""
                                        onAddComment(firstBlockId, newCommentText)
                                        newCommentText = ""
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Comment, contentDescription = "Add")
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

private fun Modifier.clipRoundedCorner(radius: androidx.compose.ui.unit.Dp): Modifier =
    this.background(Color.Transparent, RoundedCornerShape(radius))
