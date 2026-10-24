package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiActionType
import com.example.ai.GeminiService
import com.example.data.AppDatabase
import com.example.data.DocumentRepository
import com.example.export.FileExporter
import com.example.export.PdfExporter
import com.example.model.BlockFormatting
import com.example.model.BlockType
import com.example.model.ChartData
import com.example.model.DocumentBlock
import com.example.model.DocumentComment
import com.example.model.HeaderFooterConfig
import com.example.model.ImageData
import com.example.model.LekhoniDocument
import com.example.model.MarginOption
import com.example.model.Orientation
import com.example.model.PageSettings
import com.example.model.PaperSize
import com.example.model.ShapeData
import com.example.model.StarterTemplates
import com.example.model.TableData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

enum class AppLanguage {
    BENGALI, ENGLISH
}

enum class SortOrder {
    DATE_MODIFIED_DESC,
    DATE_MODIFIED_ASC,
    TITLE_ASC,
    TITLE_DESC
}

enum class EditorViewMode {
    MOBILE, DESKTOP, PRINT_LAYOUT
}

sealed class Screen {
    object Dashboard : Screen()
    data class Editor(val documentId: String) : Screen()
    object Settings : Screen()
}

class LekhoniViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DocumentRepository
    private val prefs = application.getSharedPreferences("lekhoni_prefs", Context.MODE_PRIVATE)

    // Current Navigation Screen
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Dashboard)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // App Preferences
    private val _appLanguage = MutableStateFlow(
        if (prefs.getString("app_language", "BN") == "EN") AppLanguage.ENGLISH else AppLanguage.BENGALI
    )
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    private val _useBengaliNumerals = MutableStateFlow(
        prefs.getBoolean("bengali_numerals", true)
    )
    val useBengaliNumerals: StateFlow<Boolean> = _useBengaliNumerals.asStateFlow()

    private val _editorViewMode = MutableStateFlow(EditorViewMode.MOBILE)
    val editorViewMode: StateFlow<EditorViewMode> = _editorViewMode.asStateFlow()

    private val _geminiApiKey = MutableStateFlow(
        prefs.getString("gemini_api_key", "") ?: ""
    )
    val geminiApiKey: StateFlow<String> = _geminiApiKey.asStateFlow()

    // Dashboard State
    val searchQuery = MutableStateFlow("")
    val filterStarred = MutableStateFlow(false)
    val isGridView = MutableStateFlow(false)
    val sortOrder = MutableStateFlow(SortOrder.DATE_MODIFIED_DESC)

    // Storage info
    val storageBytes = MutableStateFlow(0L)
    val documentCount = MutableStateFlow(0)

    // Current Document in Editor
    private val _activeDocument = MutableStateFlow<LekhoniDocument?>(null)
    val activeDocument: StateFlow<LekhoniDocument?> = _activeDocument.asStateFlow()

    val selectedBlockIndex = MutableStateFlow(0)

    // Undo / Redo Stacks
    private val undoStack = mutableListOf<LekhoniDocument>()
    private val redoStack = mutableListOf<LekhoniDocument>()

    // Auto-save debouncer
    private var autoSaveJob: Job? = null

    // AI Assistant State
    val isAiGenerating = MutableStateFlow(false)
    val aiResultText = MutableStateFlow<String?>(null)
    val aiErrorMessage = MutableStateFlow<String?>(null)

    // Toast / Feedback message
    val feedbackMessage = MutableStateFlow<String?>(null)

    init {
        val db = AppDatabase.getDatabase(application)
        repository = DocumentRepository(db.documentDao())

        viewModelScope.launch {
            repository.seedInitialDataIfNeeded()
            refreshStorageStats()
        }
    }

    val displayedDocuments: StateFlow<List<LekhoniDocument>> = combine(
        repository.allDocuments,
        searchQuery,
        filterStarred,
        sortOrder
    ) { docs, query, starredOnly, sort ->
        var list = docs
        if (starredOnly) {
            list = list.filter { it.isStarred }
        }
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.title.lowercase().contains(q) || it.toPlainText().lowercase().contains(q)
            }
        }
        when (sort) {
            SortOrder.DATE_MODIFIED_DESC -> list.sortedByDescending { it.updatedAt }
            SortOrder.DATE_MODIFIED_ASC -> list.sortedBy { it.updatedAt }
            SortOrder.TITLE_ASC -> list.sortedBy { it.title.lowercase() }
            SortOrder.TITLE_DESC -> list.sortedByDescending { it.title.lowercase() }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun setAppLanguage(language: AppLanguage) {
        _appLanguage.value = language
        prefs.edit().putString("app_language", if (language == AppLanguage.ENGLISH) "EN" else "BN").apply()
    }

    fun setUseBengaliNumerals(enabled: Boolean) {
        _useBengaliNumerals.value = enabled
        prefs.edit().putBoolean("bengali_numerals", enabled).apply()
    }

    fun setEditorViewMode(mode: EditorViewMode) {
        _editorViewMode.value = mode
    }

    fun setGeminiApiKey(key: String) {
        _geminiApiKey.value = key
        prefs.edit().putString("gemini_api_key", key).apply()
    }

    private suspend fun refreshStorageStats() {
        storageBytes.value = repository.getTotalStorageBytes()
        documentCount.value = repository.getDocumentCount()
    }

    // Document Management
    fun createNewDocument(templateId: String = "blank") {
        val template = StarterTemplates.templates.find { it.id == templateId }
        val newDoc = template?.documentFactory?.invoke() ?: LekhoniDocument()
        viewModelScope.launch {
            repository.saveDocument(newDoc)
            refreshStorageStats()
            openDocument(newDoc.id)
        }
    }

    fun openDocument(documentId: String) {
        viewModelScope.launch {
            val doc = repository.getDocumentById(documentId)
            if (doc != null) {
                undoStack.clear()
                redoStack.clear()
                _activeDocument.value = doc
                selectedBlockIndex.value = 0
                _currentScreen.value = Screen.Editor(documentId)
            }
        }
    }

    fun duplicateDocument(document: LekhoniDocument) {
        viewModelScope.launch {
            val dup = repository.duplicateDocument(document)
            refreshStorageStats()
            feedbackMessage.value = if (_appLanguage.value == AppLanguage.BENGALI) {
                "নথি প্রতিলিপি তৈরি করা হয়েছে"
            } else {
                "Document duplicated"
            }
        }
    }

    fun deleteDocument(id: String) {
        viewModelScope.launch {
            repository.deleteDocument(id)
            refreshStorageStats()
            if (_activeDocument.value?.id == id) {
                _activeDocument.value = null
                _currentScreen.value = Screen.Dashboard
            }
            feedbackMessage.value = if (_appLanguage.value == AppLanguage.BENGALI) {
                "নথি মুছে ফেলা হয়েছে"
            } else {
                "Document deleted"
            }
        }
    }

    fun toggleStarred(id: String, currentStarred: Boolean) {
        viewModelScope.launch {
            repository.toggleStarred(id, !currentStarred)
            if (_activeDocument.value?.id == id) {
                _activeDocument.value = _activeDocument.value?.copy(isStarred = !currentStarred)
            }
        }
    }

    fun renameDocument(id: String, newTitle: String) {
        if (newTitle.isBlank()) return
        viewModelScope.launch {
            repository.renameDocument(id, newTitle)
            if (_activeDocument.value?.id == id) {
                _activeDocument.value = _activeDocument.value?.copy(title = newTitle)
            }
        }
    }

    // Document Editing
    private fun recordUndoState() {
        _activeDocument.value?.let { current ->
            undoStack.add(current)
            if (undoStack.size > 25) undoStack.removeAt(0)
            redoStack.clear()
        }
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val previous = undoStack.removeAt(undoStack.lastIndex)
            _activeDocument.value?.let { current -> redoStack.add(current) }
            _activeDocument.value = previous
            scheduleAutoSave()
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val next = redoStack.removeAt(redoStack.lastIndex)
            _activeDocument.value?.let { current -> undoStack.add(current) }
            _activeDocument.value = next
            scheduleAutoSave()
        }
    }

    val canUndo: Boolean get() = undoStack.isNotEmpty()
    val canRedo: Boolean get() = redoStack.isNotEmpty()

    private fun scheduleAutoSave() {
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            delay(800)
            _activeDocument.value?.let { doc ->
                repository.saveDocument(doc)
                refreshStorageStats()
            }
        }
    }

    fun updateDocumentTitle(title: String) {
        recordUndoState()
        _activeDocument.value = _activeDocument.value?.copy(title = title)
        scheduleAutoSave()
    }

    fun updateBlockText(index: Int, text: String) {
        val doc = _activeDocument.value ?: return
        if (index !in doc.blocks.indices) return
        val updatedBlocks = doc.blocks.toMutableList()
        val currentBlock = updatedBlocks[index]
        updatedBlocks[index] = currentBlock.copy(text = text)
        _activeDocument.value = doc.copy(blocks = updatedBlocks)
        scheduleAutoSave()
    }

    fun updateBlockFormatting(index: Int, newFormatting: BlockFormatting) {
        recordUndoState()
        val doc = _activeDocument.value ?: return
        if (index !in doc.blocks.indices) return
        val updatedBlocks = doc.blocks.toMutableList()
        val currentBlock = updatedBlocks[index]
        updatedBlocks[index] = currentBlock.copy(formatting = newFormatting)
        _activeDocument.value = doc.copy(blocks = updatedBlocks)
        scheduleAutoSave()
    }

    fun changeBlockType(index: Int, newType: BlockType) {
        recordUndoState()
        val doc = _activeDocument.value ?: return
        if (index !in doc.blocks.indices) return
        val updatedBlocks = doc.blocks.toMutableList()
        val currentBlock = updatedBlocks[index]
        updatedBlocks[index] = currentBlock.copy(type = newType)
        _activeDocument.value = doc.copy(blocks = updatedBlocks)
        scheduleAutoSave()
    }

    fun addBlockAfter(index: Int, type: BlockType = BlockType.PARAGRAPH) {
        recordUndoState()
        val doc = _activeDocument.value ?: return
        val updatedBlocks = doc.blocks.toMutableList()
        val insertPos = (index + 1).coerceIn(0, updatedBlocks.size)
        val newBlock = when (type) {
            BlockType.TABLE -> DocumentBlock(type = BlockType.TABLE, tableData = TableData())
            BlockType.CHART -> DocumentBlock(type = BlockType.CHART, chartData = ChartData())
            BlockType.SHAPE -> DocumentBlock(type = BlockType.SHAPE, shapeData = ShapeData())
            BlockType.IMAGE -> DocumentBlock(type = BlockType.IMAGE, imageData = ImageData(caption = "নথি সংযুক্তি"))
            else -> DocumentBlock(type = type, text = "")
        }
        updatedBlocks.add(insertPos, newBlock)
        _activeDocument.value = doc.copy(blocks = updatedBlocks)
        selectedBlockIndex.value = insertPos
        scheduleAutoSave()
    }

    fun removeBlock(index: Int) {
        val doc = _activeDocument.value ?: return
        if (doc.blocks.size <= 1) return // Keep at least one block
        recordUndoState()
        val updatedBlocks = doc.blocks.toMutableList()
        updatedBlocks.removeAt(index)
        _activeDocument.value = doc.copy(blocks = updatedBlocks)
        selectedBlockIndex.value = (index - 1).coerceAtLeast(0)
        scheduleAutoSave()
    }

    fun moveBlock(index: Int, up: Boolean) {
        val doc = _activeDocument.value ?: return
        val target = if (up) index - 1 else index + 1
        if (target !in doc.blocks.indices) return
        recordUndoState()
        val updatedBlocks = doc.blocks.toMutableList()
        val item = updatedBlocks.removeAt(index)
        updatedBlocks.add(target, item)
        _activeDocument.value = doc.copy(blocks = updatedBlocks)
        selectedBlockIndex.value = target
        scheduleAutoSave()
    }

    fun updateTableData(index: Int, tableData: TableData) {
        recordUndoState()
        val doc = _activeDocument.value ?: return
        if (index !in doc.blocks.indices) return
        val updatedBlocks = doc.blocks.toMutableList()
        updatedBlocks[index] = updatedBlocks[index].copy(tableData = tableData)
        _activeDocument.value = doc.copy(blocks = updatedBlocks)
        scheduleAutoSave()
    }

    fun updateChartData(index: Int, chartData: ChartData) {
        recordUndoState()
        val doc = _activeDocument.value ?: return
        if (index !in doc.blocks.indices) return
        val updatedBlocks = doc.blocks.toMutableList()
        updatedBlocks[index] = updatedBlocks[index].copy(chartData = chartData)
        _activeDocument.value = doc.copy(blocks = updatedBlocks)
        scheduleAutoSave()
    }

    fun updateShapeData(index: Int, shapeData: ShapeData) {
        recordUndoState()
        val doc = _activeDocument.value ?: return
        if (index !in doc.blocks.indices) return
        val updatedBlocks = doc.blocks.toMutableList()
        updatedBlocks[index] = updatedBlocks[index].copy(shapeData = shapeData)
        _activeDocument.value = doc.copy(blocks = updatedBlocks)
        scheduleAutoSave()
    }

    fun updatePageSettings(
        paperSize: PaperSize,
        orientation: Orientation,
        marginOption: MarginOption,
        columns: Int,
        showBorder: Boolean,
        watermark: String?
    ) {
        recordUndoState()
        val doc = _activeDocument.value ?: return
        val updatedSettings = doc.pageSettings.copy(
            paperSize = paperSize,
            orientation = orientation,
            marginOption = marginOption,
            columns = columns,
            showBorder = showBorder,
            watermark = watermark
        )
        _activeDocument.value = doc.copy(pageSettings = updatedSettings)
        scheduleAutoSave()
    }

    fun updateHeaderFooter(
        headerText: String,
        footerText: String,
        showPageNumber: Boolean
    ) {
        recordUndoState()
        val doc = _activeDocument.value ?: return
        val updatedHeader = doc.header.copy(text = headerText)
        val updatedFooter = doc.footer.copy(text = footerText, showPageNumber = showPageNumber)
        _activeDocument.value = doc.copy(header = updatedHeader, footer = updatedFooter)
        scheduleAutoSave()
    }

    fun addComment(blockId: String, text: String, author: String = "Polok Nandi") {
        val doc = _activeDocument.value ?: return
        val newComment = DocumentComment(
            blockId = blockId,
            author = author,
            text = text
        )
        val updatedComments = doc.comments + newComment
        _activeDocument.value = doc.copy(comments = updatedComments)
        scheduleAutoSave()
    }

    fun resolveComment(commentId: String) {
        val doc = _activeDocument.value ?: return
        val updatedComments = doc.comments.map {
            if (it.id == commentId) it.copy(isResolved = !it.isResolved) else it
        }
        _activeDocument.value = doc.copy(comments = updatedComments)
        scheduleAutoSave()
    }

    fun deleteComment(commentId: String) {
        val doc = _activeDocument.value ?: return
        val updatedComments = doc.comments.filter { it.id != commentId }
        _activeDocument.value = doc.copy(comments = updatedComments)
        scheduleAutoSave()
    }

    fun findAndReplace(findText: String, replaceWith: String) {
        if (findText.isEmpty()) return
        val doc = _activeDocument.value ?: return
        recordUndoState()
        var replacementsCount = 0
        val updatedBlocks = doc.blocks.map { block ->
            if (block.text.contains(findText)) {
                replacementsCount++
                block.copy(text = block.text.replace(findText, replaceWith))
            } else {
                block
            }
        }
        _activeDocument.value = doc.copy(blocks = updatedBlocks)
        scheduleAutoSave()
        feedbackMessage.value = if (_appLanguage.value == AppLanguage.BENGALI) {
            "$replacementsCount টি শব্দ প্রতিস্থাপিত হয়েছে"
        } else {
            "Replaced $replacementsCount occurrences"
        }
    }

    // AI Execution
    fun runAiAction(action: AiActionType, targetText: String, customPrompt: String? = null) {
        isAiGenerating.value = true
        aiErrorMessage.value = null
        aiResultText.value = null

        viewModelScope.launch {
            val key = _geminiApiKey.value.takeIf { it.isNotBlank() }
            val result = GeminiService.executeAiTask(
                action = action,
                inputText = targetText,
                customPrompt = customPrompt,
                apiKeyOverride = key
            )
            isAiGenerating.value = false
            result.onSuccess { generatedText ->
                aiResultText.value = generatedText
            }.onFailure { error ->
                aiErrorMessage.value = error.message ?: "AI Generation Failed"
            }
        }
    }

    fun applyAiResultToCurrentBlock() {
        val text = aiResultText.value ?: return
        val doc = _activeDocument.value ?: return
        val idx = selectedBlockIndex.value
        if (idx in doc.blocks.indices) {
            updateBlockText(idx, text)
            aiResultText.value = null
            feedbackMessage.value = if (_appLanguage.value == AppLanguage.BENGALI) {
                "AI টেক্সট ডকুমেন্টে প্রয়োগ করা হয়েছে"
            } else {
                "AI text inserted into document"
            }
        }
    }

    fun appendAiResultAsNewBlock() {
        val text = aiResultText.value ?: return
        val idx = selectedBlockIndex.value
        addBlockAfter(idx, BlockType.PARAGRAPH)
        val newIdx = idx + 1
        updateBlockText(newIdx, text)
        aiResultText.value = null
    }

    // Export Actions
    fun exportPdf(context: Context): File? {
        val doc = _activeDocument.value ?: return null
        return try {
            val pdfFile = PdfExporter.generatePdf(context, doc)
            feedbackMessage.value = if (_appLanguage.value == AppLanguage.BENGALI) {
                "পিডিএফ সফলভাবে তৈরি হয়েছে: ${pdfFile.name}"
            } else {
                "PDF generated: ${pdfFile.name}"
            }
            pdfFile
        } catch (e: Exception) {
            feedbackMessage.value = "PDF Export Error: ${e.message}"
            null
        }
    }

    fun exportHtml(context: Context): File? {
        val doc = _activeDocument.value ?: return null
        val html = FileExporter.exportToHtml(doc)
        val safeTitle = doc.title.replace("[^a-zA-Z0-9\\u0980-\\u09FF._-]".toRegex(), "_")
        return FileExporter.saveTextToFile(context, "$safeTitle.html", html)
    }

    fun exportMarkdown(context: Context): File? {
        val doc = _activeDocument.value ?: return null
        val md = FileExporter.exportToMarkdown(doc)
        val safeTitle = doc.title.replace("[^a-zA-Z0-9\\u0980-\\u09FF._-]".toRegex(), "_")
        return FileExporter.saveTextToFile(context, "$safeTitle.md", md)
    }

    fun exportPlainText(context: Context): File? {
        val doc = _activeDocument.value ?: return null
        val txt = doc.toPlainText()
        val safeTitle = doc.title.replace("[^a-zA-Z0-9\\u0980-\\u09FF._-]".toRegex(), "_")
        return FileExporter.saveTextToFile(context, "$safeTitle.txt", txt)
    }

    fun exportLekhoniJson(context: Context): File? {
        val doc = _activeDocument.value ?: return null
        val json = doc.toJson()
        val safeTitle = doc.title.replace("[^a-zA-Z0-9\\u0980-\\u09FF._-]".toRegex(), "_")
        return FileExporter.saveTextToFile(context, "$safeTitle.lekhoni", json)
    }

    fun importDocumentFromText(title: String, rawContent: String) {
        viewModelScope.launch {
            val imported = if (rawContent.trim().startsWith("{") && rawContent.contains("\"blocks\"")) {
                LekhoniDocument.fromJson(rawContent).copy(
                    id = UUID.randomUUID().toString(),
                    title = title.removeSuffix(".lekhoni"),
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
            } else {
                FileExporter.parseImportedText(title, rawContent)
            }
            repository.saveDocument(imported)
            refreshStorageStats()
            openDocument(imported.id)
            feedbackMessage.value = if (_appLanguage.value == AppLanguage.BENGALI) {
                "নথি সফলভাবে ইম্পোর্ট করা হয়েছে"
            } else {
                "Document imported successfully"
            }
        }
    }

    fun convertNumeralsToBengali(input: String): String {
        val bnDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
        val sb = java.lang.StringBuilder()
        for (ch in input) {
            if (ch in '0'..'9') {
                sb.append(bnDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }
}
