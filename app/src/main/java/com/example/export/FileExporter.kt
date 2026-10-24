package com.example.export

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.model.BlockType
import com.example.model.DocumentBlock
import com.example.model.LekhoniDocument
import java.io.File
import java.io.FileOutputStream

object FileExporter {

    fun exportToHtml(document: LekhoniDocument): String {
        val sb = StringBuilder()
        sb.append("<!DOCTYPE html>\n<html lang=\"bn\">\n<head>\n")
        sb.append("<meta charset=\"utf-8\">\n")
        sb.append("<title>${document.title}</title>\n")
        sb.append("<style>\n")
        sb.append("body { font-family: 'Noto Sans Bengali', 'SolaimanLipi', sans-serif; max-width: 800px; margin: 40px auto; padding: 20px; line-height: 1.6; color: #1b3b6f; }\n")
        sb.append("h1, h2, h3 { color: #1b3b6f; }\n")
        sb.append("table { width: 100%; border-collapse: collapse; margin: 20px 0; }\n")
        sb.append("th, td { border: 1px solid #d0d7de; padding: 8px 12px; text-align: left; }\n")
        sb.append("th { background-color: #f6f8fa; }\n")
        sb.append("blockquote { border-left: 4px solid #1b3b6f; margin: 0; padding-left: 16px; color: #57606a; }\n")
        sb.append("</style>\n</head>\n<body>\n")
        sb.append("<h1>${document.title}</h1>\n")

        for (block in document.blocks) {
            when (block.type) {
                BlockType.HEADING_1 -> sb.append("<h1>${block.text}</h1>\n")
                BlockType.HEADING_2 -> sb.append("<h2>${block.text}</h2>\n")
                BlockType.HEADING_3 -> sb.append("<h3>${block.text}</h3>\n")
                BlockType.PARAGRAPH -> sb.append("<p>${block.text}</p>\n")
                BlockType.BULLET_LIST -> sb.append("<ul><li>${block.text}</li></ul>\n")
                BlockType.NUMBERED_LIST -> sb.append("<ol><li>${block.text}</li></ol>\n")
                BlockType.BLOCKQUOTE -> sb.append("<blockquote>${block.text}</blockquote>\n")
                BlockType.DIVIDER -> sb.append("<hr/>\n")
                BlockType.PAGE_BREAK -> sb.append("<div style=\"page-break-after: always;\"></div>\n")
                BlockType.TABLE -> {
                    val t = block.tableData
                    if (t != null && t.rows.isNotEmpty()) {
                        sb.append("<table>\n")
                        t.rows.forEachIndexed { idx, row ->
                            sb.append("<tr>\n")
                            row.forEach { cell ->
                                if (idx == 0) sb.append("<th>$cell</th>\n") else sb.append("<td>$cell</td>\n")
                            }
                            sb.append("</tr>\n")
                        }
                        sb.append("</table>\n")
                    }
                }
                BlockType.CHART -> {
                    sb.append("<div style=\"border:1px solid #ccc; padding:10px; margin:10px 0;\"><strong>চার্ট: ${block.chartData?.title}</strong></div>\n")
                }
                BlockType.SHAPE -> {
                    sb.append("<div style=\"border:2px solid ${block.shapeData?.strokeColorHex ?: "#1B3B6F"}; background:${block.shapeData?.fillColorHex ?: "#eee"}; padding:10px; text-align:center; border-radius:8px;\">${block.shapeData?.text}</div>\n")
                }
                BlockType.IMAGE -> {
                    sb.append("<p><em>[ছবি: ${block.imageData?.caption}]</em></p>\n")
                }
            }
        }

        sb.append("</body>\n</html>")
        return sb.toString()
    }

    fun exportToMarkdown(document: LekhoniDocument): String {
        val sb = StringBuilder()
        sb.append("# ${document.title}\n\n")

        for (block in document.blocks) {
            when (block.type) {
                BlockType.HEADING_1 -> sb.append("# ${block.text}\n\n")
                BlockType.HEADING_2 -> sb.append("## ${block.text}\n\n")
                BlockType.HEADING_3 -> sb.append("### ${block.text}\n\n")
                BlockType.PARAGRAPH -> sb.append("${block.text}\n\n")
                BlockType.BULLET_LIST -> sb.append("- ${block.text}\n")
                BlockType.NUMBERED_LIST -> sb.append("1. ${block.text}\n")
                BlockType.BLOCKQUOTE -> sb.append("> ${block.text}\n\n")
                BlockType.DIVIDER -> sb.append("---\n\n")
                BlockType.PAGE_BREAK -> sb.append("\n---\n*পৃষ্ঠা বিরতি*\n---\n\n")
                BlockType.TABLE -> {
                    val t = block.tableData
                    if (t != null && t.rows.isNotEmpty()) {
                        t.rows.forEachIndexed { index, row ->
                            sb.append("| ").append(row.joinToString(" | ")).append(" |\n")
                            if (index == 0) {
                                sb.append("| ").append(row.map { "---" }.joinToString(" | ")).append(" |\n")
                            }
                        }
                        sb.append("\n")
                    }
                }
                BlockType.CHART -> sb.append("**চার্ট: ${block.chartData?.title}**\n\n")
                BlockType.SHAPE -> sb.append(">[${block.shapeData?.text}]\n\n")
                BlockType.IMAGE -> sb.append("![${block.imageData?.caption}](image_placeholder)\n\n")
            }
        }
        return sb.toString()
    }

    fun saveTextToFile(context: Context, filename: String, content: String): File {
        val outputDir = context.getExternalFilesDir(null) ?: context.cacheDir
        val file = File(outputDir, filename)
        FileOutputStream(file).use {
            it.write(content.toByteArray(Charsets.UTF_8))
        }
        return file
    }

    fun shareFile(context: Context, file: File, mimeType: String, subject: String) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "লেখনী - শেয়ার করুন"))
    }

    fun parseImportedText(title: String, content: String): LekhoniDocument {
        // Automatically break into paragraphs or headings
        val lines = content.lines()
        val blocks = mutableListOf<DocumentBlock>()

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) continue

            when {
                trimmed.startsWith("### ") -> {
                    blocks.add(DocumentBlock(type = BlockType.HEADING_3, text = trimmed.removePrefix("### ")))
                }
                trimmed.startsWith("## ") -> {
                    blocks.add(DocumentBlock(type = BlockType.HEADING_2, text = trimmed.removePrefix("## ")))
                }
                trimmed.startsWith("# ") -> {
                    blocks.add(DocumentBlock(type = BlockType.HEADING_1, text = trimmed.removePrefix("# ")))
                }
                trimmed.startsWith("- ") || trimmed.startsWith("* ") -> {
                    blocks.add(DocumentBlock(type = BlockType.BULLET_LIST, text = trimmed.drop(2)))
                }
                trimmed.startsWith("> ") -> {
                    blocks.add(DocumentBlock(type = BlockType.BLOCKQUOTE, text = trimmed.removePrefix("> ")))
                }
                trimmed == "---" -> {
                    blocks.add(DocumentBlock(type = BlockType.DIVIDER))
                }
                else -> {
                    blocks.add(DocumentBlock(type = BlockType.PARAGRAPH, text = trimmed))
                }
            }
        }

        if (blocks.isEmpty()) {
            blocks.add(DocumentBlock(type = BlockType.PARAGRAPH, text = content))
        }

        return LekhoniDocument(
            title = title,
            blocks = blocks
        )
    }
}
