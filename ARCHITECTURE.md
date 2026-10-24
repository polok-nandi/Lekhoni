# Lekhoni Software Architecture

## High-Level Modules

```
lekhoni/
├── com.example.model/         # Structured document model, page settings, blocks, templates
│   ├── DocumentModels.kt      # LekhoniDocument, DocumentBlock, BlockType, PageSettings
│   └── StarterTemplates.kt    # 10 bilingual starter templates
├── com.example.data/          # Local persistence layer (Room SQLite)
│   ├── DocumentEntity.kt      # Room database entity
│   ├── DocumentDao.kt         # Reactive Flow queries & CRUD
│   ├── AppDatabase.kt         # Database instance & migration config
│   └── DocumentRepository.kt  # Clean architecture data abstraction
├── com.example.export/        # Document conversion & publishing
│   ├── PdfExporter.kt         # Android PdfDocument high-fidelity printer
│   └── FileExporter.kt        # HTML, Markdown, Plain Text, and Lekhoni JSON
├── com.example.ai/            # Optional Google Gemini 3.5 Flash integration
│   └── GeminiService.kt       # Grammar, translation, and rewrite pipeline
├── com.example.ui.theme/      # Theme, M3 colors & typography
│   ├── Color.kt               # Lekhoni Ink & Bengali paper palette
│   ├── Type.kt                # Noto Sans & Serif Bengali typography scale
│   └── Theme.kt               # Centralized M3 light and dark theme
├── com.example.ui.components/ # Modular, accessible UI components
│   ├── FormattingToolbar.kt   # Word-processor formatting ribbon
│   ├── BengaliToolsBar.kt     # Yuktakshar & punctuation helper
│   ├── DocumentCanvas.kt      # Multi-view document paper renderer
│   ├── ChartCanvas.kt         # Canvas-drawn Bar, Line, Pie charts
│   ├── ShapeCanvas.kt         # Canvas-drawn shapes with text
│   ├── PageLayoutDialog.kt    # Paper size, margins & orientation setup
│   ├── InsertElementsDialogs.kt # Tables, charts, shapes, images dialogs
│   ├── AiAssistantSheet.kt    # Interactive Gemini AI bottom sheet
│   └── DocumentOutlineDrawer.kt # Outline navigation & comments
└── com.example.ui.screens/    # Primary top-level screens
    ├── DashboardScreen.kt     # Document management dashboard
    ├── EditorScreen.kt        # Word-processor editing experience
    └── SettingsScreen.kt      # Preferences, AI setup, typography & credits
```

## Data Persistence & Autosave Cycle

1. Any text or formatting change dispatches `scheduleAutoSave()` in `LekhoniViewModel`.
2. Debounced after 800ms to avoid unnecessary disk I/O while typing fast.
3. Serialized via structured JSON schema into Room SQLite (`documents` table).
4. Emits reactive updates across dashboard and editor.
