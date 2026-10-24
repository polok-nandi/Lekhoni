package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.BlockType
import com.example.model.DocumentBlock
import com.example.model.LekhoniDocument
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Lekhoni", appName)
  }

  @Test
  fun `test document json roundtrip with real android json`() {
    val original = LekhoniDocument(
      title = "দাপ্তরিক নথি",
      blocks = listOf(
        DocumentBlock(type = BlockType.HEADING_1, text = "প্রথম অধ্যায়"),
        DocumentBlock(type = BlockType.PARAGRAPH, text = "লেখনী মুক্ত-উৎস বাংলা ডকুমেন্ট এডিটর।")
      )
    )

    val json = original.toJson()
    val restored = LekhoniDocument.fromJson(json)

    assertEquals(original.title, restored.title)
    assertEquals(2, restored.blocks.size)
    assertEquals("প্রথম অধ্যায়", restored.blocks[0].text)
    assertEquals("লেখনী মুক্ত-উৎস বাংলা ডকুমেন্ট এডিটর।", restored.blocks[1].text)
  }
}
