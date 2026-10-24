# Lekhoni (লেখনী) 🖋️
> **A Bengali-friendly, open-source document editor and office suite.**  
> *একটি বাংলা-বান্ধব মুক্ত-উৎসের ডকুমেন্ট এডিটর ও অফিস সফটওয়্যার।*

Created and authored by **Polok Nandi** (পলক নন্দী).

---

## 🌟 Overview (সংক্ষিপ্ত পরিচিতি)

**Lekhoni** is an open-source, modern productivity document editor engineered specifically for seamless Bengali and English document creation, formatting, local persistence, and high-fidelity PDF publishing. Inspired by Microsoft Word, Google Docs, and LibreOffice Writer, Lekhoni delivers a desktop-grade word processing experience on Android devices, foldables, tablets, and desktops.

### Key Capabilities
- **Bengali Typography First**: Native handling of Unicode Bengali, complex ligatures (যুক্তাক্ষর: ক্ষ, জ্ঞ, ষ্ণ, ঙ্গ, ত্র), vowel signs (কার ও ফলা), ref, and punctuation (। ৳ — ‘ ’ “ ”).
- **Dual Language UI**: Instant 1-tap switching between Bengali (বাংলা) and English UI.
- **Auto Bengali Numerals**: Automatic conversion of numbers to Bengali digits (`১২৩৪৫৬৭৮৯০`).
- **Structured Block Editor**: Rich headings (H1-H3), paragraphs, blockquotes, bulleted and numbered lists, dividers, and page breaks.
- **Embedded Insert Tools**:
  - **Tables**: Customizable rows and columns with inline cell editing and header styling.
  - **Charts**: Custom Canvas-rendered Bar, Line, and Pie charts with editable labels and values.
  - **Shapes**: Rectangles, rounded boxes, circles, arrows, callouts, and star badges with text inside.
  - **Images**: Custom captions, alignment, and preview framing.
- **Page Layout & Print Engine**: Real paper proportions (A4, Letter, A5, Legal, A3), orientation (Portrait/Landscape), margins (Normal, Narrow, Moderate, Wide), multi-column flow, page borders, watermarks, headers, and footers with auto-page numbering.
- **Multiple Viewing Modes**:
  1. *Mobile View*: Optimized for narrow mobile screens with touch-friendly ribbon panels.
  2. *Desktop View*: Desktop ribbon bar, centered paper workspace with ruler indicators.
  3. *Print Layout View*: Exact paper margins, boundaries, and headers/footers.
- **PDF & Multi-format Export**:
  - Real Android `PdfDocument` generation respecting paper sizes, typography, and pagination.
  - HTML, Markdown (.md), Plain Text (.txt), and native Lekhoni JSON (.lekhoni).
- **Gemini AI Writing Assistant (gemini-3.5-flash)**:
  - Bengali and English grammar correction.
  - Formal rewriting, simplification, elaboration, and summarization.
  - Bidirectional translation (Bengali ↔ English).
  - Draft generator for official letters, job applications, and resumes.
- **10 Built-in Starter Templates**:
  1. Blank Document (খালি নথি)
  2. Bengali Official Letter (দাপ্তরিক আবেদনপত্র)
  3. Job Application (চাকরির আবেদন)
  4. CV or Résumé (জীবনবৃত্তান্ত)
  5. Academic Assignment (একাডেমিক অ্যাসাইনমেন্ট)
  6. Research Paper (গবেষণা পত্র)
  7. Business Report (ব্যবসায়িক প্রতিবেদন)
  8. Meeting Minutes (সভার কার্যবিবরণী)
  9. Invoice / Bill (চালানপত্র)
  10. Table-based Document (ছকবদ্ধ নথি)

---

## 🏛️ Architecture & Tech Stack

- **Runtime**: Android SDK (minSdk 24, targetSdk 36)
- **Language**: Kotlin 2.2.10
- **UI Toolkit**: Jetpack Compose & Material Design 3 (M3)
- **Local Persistence**: Room SQLite Database with Repository Pattern
- **Typography Engine**: Google Fonts local offline bundling (`Noto Sans Bengali`, `Noto Serif Bengali`)
- **PDF Generation**: Native Android `android.graphics.pdf.PdfDocument`
- **AI Integration**: Google Gemini REST API (`gemini-3.5-flash`) via OkHttp/Retrofit

---

## 🚀 Building & Development

### Prerequisites
- Android Studio Ladybug / Meerkat or newer
- JDK 17 or JDK 21
- Android SDK 36

### Local Compilation
```bash
# Clone the repository
git clone https://github.com/poloknandi/lekhoni.git
cd lekhoni

# Run Robolectric unit tests
gradle :app:testDebugUnitTest

# Assemble Debug APK
gradle :app:assembleDebug
```

---

## 📜 License

Licensed under the [MIT License](LICENSE).  
Copyright (c) 2026 Polok Nandi.
