package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BengaliGreen
import com.example.ui.theme.NotoSansBengaliFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BengaliToolsBar(
    onInsertSymbol: (String) -> Unit,
    onToggleNumerals: () -> Unit,
    isBengaliNumeralsActive: Boolean,
    modifier: Modifier = Modifier
) {
    val quickSymbols = listOf("।", "৳", "—", "‘", "’", "“", "”", "…", "্", "ং", "ঃ", "ঁ", "ৎ")
    val commonYuktakshars = listOf("ক্ষ", "জ্ঞ", "ঞ্চ", "ঞ্জ", "ষ্ণ", "ঙ্গ", "ঙ্ক", "ত্র", "শ্ন", "ণ্ড", "দ্ধ", "দ্ব", "স্ম", "স্থ")

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Numerals toggle
            OutlinedButton(
                onClick = onToggleNumerals,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isBengaliNumeralsActive) BengaliGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                ),
                contentPadding = ButtonDefaults.ContentPadding,
                modifier = Modifier.testTag("toggle_bengali_numerals_btn")
            ) {
                Text(
                    text = if (isBengaliNumeralsActive) "সংখ্যা: ১২৩" else "Nums: 123",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = NotoSansBengaliFamily
                )
            }

            // Quick Bengali Punctuation symbols
            quickSymbols.forEach { sym ->
                ElevatedAssistChip(
                    onClick = { onInsertSymbol(sym) },
                    label = {
                        Text(
                            text = sym,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = NotoSansBengaliFamily
                        )
                    },
                    modifier = Modifier.testTag("sym_$sym")
                )
            }

            // Yuktakshar quick chips
            commonYuktakshars.forEach { y ->
                ElevatedAssistChip(
                    onClick = { onInsertSymbol(y) },
                    label = {
                        Text(
                            text = y,
                            fontSize = 13.sp,
                            fontFamily = NotoSansBengaliFamily
                        )
                    },
                    modifier = Modifier.testTag("yukta_$y")
                )
            }
        }
    }
}
