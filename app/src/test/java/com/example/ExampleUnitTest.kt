package com.example

import com.example.export.FileExporter
import com.example.model.BlockType
import com.example.model.DocumentBlock
import com.example.model.LekhoniDocument
import com.example.model.StarterTemplates
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testStarterTemplates() {
    assertEquals(10, StarterTemplates.templates.size)
    val official = StarterTemplates.templates.find { it.id == "official_letter" }
    assertNotNull(official)
    val doc = official!!.documentFactory()
    assertTrue(doc.blocks.isNotEmpty())
  }

  @Test
  fun testMarkdownAndHtmlExport() {
    val doc = LekhoniDocument(
      title = "পরীক্ষা",
      blocks = listOf(
        DocumentBlock(type = BlockType.HEADING_1, text = "শিরোনাম"),
        DocumentBlock(type = BlockType.PARAGRAPH, text = "সাধারণ অনুচ্ছেদ")
      )
    )

    val html = FileExporter.exportToHtml(doc)
    assertTrue(html.contains("<!DOCTYPE html>"))
    assertTrue(html.contains("<h1>শিরোনাম</h1>"))
    assertTrue(html.contains("<p>সাধারণ অনুচ্ছেদ</p>"))

    val md = FileExporter.exportToMarkdown(doc)
    assertTrue(md.contains("# পরীক্ষা"))
    assertTrue(md.contains("সাধারণ অনুচ্ছেদ"))
  }

  @Test
  fun testWordCountWithBengali() {
    val doc = LekhoniDocument(
      title = "নথি",
      blocks = listOf(
        DocumentBlock(type = BlockType.PARAGRAPH, text = "আমার সোনার বাংলা আমি তোমায় ভালোবাসি")
      )
    )
    assertEquals(7, doc.wordCount())
  }
}
