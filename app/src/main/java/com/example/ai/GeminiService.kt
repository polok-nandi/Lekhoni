package com.example.ai

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

enum class AiActionType(val displayNameEn: String, val displayNameBn: String, val promptInstruction: String) {
    IMPROVE_BENGALI_GRAMMAR(
        "Improve Bengali Grammar & Spelling",
        "বাংলা ব্যাকরণ ও বানান সংশোধন",
        "Please review and fix any grammatical mistakes, spelling errors, or awkward phrasing in the following Bengali text. Preserve the original meaning and tone, and return the improved version directly:"
    ),
    IMPROVE_ENGLISH_GRAMMAR(
        "Improve English Grammar & Spelling",
        "ইংরেজি ব্যাকরণ ও বানান সংশোধন",
        "Please proofread and correct grammar, punctuation, and spelling in the following English text. Return only the polished text:"
    ),
    MAKE_FORMAL(
        "Make Writing More Formal",
        "আনুষ্ঠানিক ও মার্জিত রূপ দান",
        "Rewrite the following text in a polite, professional, and formal style suitable for official correspondence:"
    ),
    SIMPLIFY(
        "Simplify Writing",
        "সহজ ভাষায় রূপান্তর",
        "Simplify the following text so that it is crystal clear, easy to read, and accessible to a wide audience:"
    ),
    EXPAND(
        "Expand & Elaborate",
        "ভাব সম্প্রসারণ ও বিস্তার",
        "Expand on the following passage with thoughtful details, context, and clear explanation while maintaining the core idea:"
    ),
    SUMMARIZE(
        "Summarize",
        "সারসংক্ষেপ তৈরি",
        "Provide a concise, high-impact summary of the main points in the following text:"
    ),
    TRANSLATE_TO_ENGLISH(
        "Translate Bengali to English",
        "বাংলা থেকে ইংরেজি অনুবাদ",
        "Translate the following Bengali text into natural, idiomatic, and accurate English:"
    ),
    TRANSLATE_TO_BENGALI(
        "Translate English to Bengali",
        "ইংরেজি থেকে বাংলা অনুবাদ",
        "Translate the following English text into natural, elegant, and grammatically accurate Bengali (বাংলা):"
    ),
    DRAFT_OFFICIAL_LETTER(
        "Draft Official Letter on Topic",
        "দাপ্তরিক চিঠির খসড়া তৈরি",
        "Draft a formal Bengali official letter (আবেদনপত্র) based on the following topic or rough notes:"
    ),
    CONTINUE_WRITING(
        "Continue Writing",
        "পরবর্তী অংশ লিখুন",
        "Continue writing the next logical paragraphs for this document, adhering seamlessly to the established tone and subject:"
    )
}

object GeminiService {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun executeAiTask(
        action: AiActionType,
        inputText: String,
        customPrompt: String? = null,
        apiKeyOverride: String? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val key = if (!apiKeyOverride.isNullOrBlank()) {
                apiKeyOverride.trim()
            } else {
                BuildConfig.GEMINI_API_KEY
            }

            if (key.isBlank() || key == "MY_GEMINI_API_KEY") {
                return@withContext Result.failure(
                    IllegalStateException("Gemini API Key is not configured. Please add your key in Settings or AI Studio Secrets.")
                )
            }

            val instruction = customPrompt ?: action.promptInstruction
            val fullPrompt = "$instruction\n\n\"\"\"\n$inputText\n\"\"\""

            val jsonBody = JSONObject().apply {
                val contentsArr = JSONArray()
                val contentObj = JSONObject()
                val partsArr = JSONArray()
                val partObj = JSONObject()
                partObj.put("text", fullPrompt)
                partsArr.put(partObj)
                contentObj.put("parts", partsArr)
                contentsArr.put(contentObj)
                put("contents", contentsArr)

                val generationConfig = JSONObject()
                generationConfig.put("temperature", 0.7)
                put("generationConfig", generationConfig)

                val sysInstruction = JSONObject()
                val sysParts = JSONArray()
                val sysPart = JSONObject()
                sysPart.put("text", "You are Lekhoni AI, a polite, precise Bengali and English document writing assistant created for Lekhoni Office Suite. Output only the desired document content directly without introductory conversational filler.")
                sysParts.put(sysPart)
                sysInstruction.put("parts", sysParts)
                put("systemInstruction", sysInstruction)
            }

            val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL?key=$key")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                val errorMsg = try {
                    val errJson = JSONObject(responseBody).optJSONObject("error")
                    errJson?.optString("message") ?: "API Error HTTP ${response.code}"
                } catch (e: Exception) {
                    "API Error HTTP ${response.code}: $responseBody"
                }
                return@withContext Result.failure(Exception(errorMsg))
            }

            val rootJson = JSONObject(responseBody)
            val candidates = rootJson.optJSONArray("candidates")
            if (candidates == null || candidates.length() == 0) {
                return@withContext Result.failure(Exception("No response generated by model."))
            }

            val content = candidates.getJSONObject(0).optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text")

            if (text.isNullOrBlank()) {
                return@withContext Result.failure(Exception("Empty response received from AI model."))
            }

            Result.success(text.trim())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun testConnection(apiKey: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val key = apiKey.trim()
            if (key.isBlank()) return@withContext Result.failure(IllegalArgumentException("Key cannot be empty"))

            val jsonBody = JSONObject().apply {
                val contentsArr = JSONArray()
                val contentObj = JSONObject()
                val partsArr = JSONArray()
                val partObj = JSONObject()
                partObj.put("text", "Reply with 'OK'")
                partsArr.put(partObj)
                contentObj.put("parts", partsArr)
                contentsArr.put(contentObj)
                put("contents", contentsArr)
            }

            val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL?key=$key")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                Result.success("সফল সংযোগ! (Connection verified successfully)")
            } else {
                val body = response.body?.string() ?: ""
                Result.failure(Exception("API Error HTTP ${response.code}: $body"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
