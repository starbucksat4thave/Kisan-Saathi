# 🌍 WanderPulse — AI Travel & Exploration Studio

WanderPulse is a next-generation Android application designed for world explorers, cultural travelers, and photographers. Built with modern Android architecture using **Kotlin** and **Jetpack Compose (Material Design 3)**, it unites real-time exploration maps, multimodal generative AI, live voice interactions, and trip journaling.

---

## 🌟 Key Features

### 1. 🗺️ Interactive Maps & Place Discovery
- **Topographic Interactive Map Canvas**: Vector-rendered map with real-time panning, zooming, custom thematic location pins, and user GPS pulsing beacon.
- **Search & Maps Grounding**:
  - **Google Maps Grounding**: Verifies live operating hours, exact coordinates, ratings, and foot-traffic wait times.
  - **Google Search Grounding**: Delivers seasonal travel advisories, local festival schedules, and regional cultural etiquette.
- **Category Filtering**: Instant sorting by Landmarks, Nature Wonders, Culinary Spots, Cultural Temples, and Transit Hubs.
- **Place Details & Directions**: Deep dives with address, coordinates, opening status, and direct "+ Itinerary" scheduling.

### 2. 🎙️ Live Voice Companion (`gemini-3.8-live`)
- **Live Conversational Mode**: Continuous audio turn-taking with Gemini 3.8 Live API.
- **Dynamic Waveform Visualizer**: Animated canvas audio visualizer responding to mic amplitude and speech output.
- **Context-Aware Prompts**: Sunset recommendations, monument history, foreign phrase translations, and leisure walking guides.

### 3. 📝 Instant Audio Transcriber (`gemini-3.5-transcribe`)
- **Field Audio Memos**: High-accuracy speech-to-text recording with confidence scoring and language detection.
- **Quick Actions**: One-tap copy to clipboard and direct saving to your WanderPulse Travel Journal.

### 4. 🎨 Multimodal Vision & Postcard Studio
- **Landmark Analyzer (`gemini-4-argon`)**: Multimodal vision analysis identifying architectural periods, historical construction methods, framing advice for photography, and secret spots within 5 minutes.
- **UHD Postcard Generator (`gemini-3-pro-image-preview`)**: Generates custom artwork with explicit resolution affordances: **1K**, **2K**, and **4K**.
- **Prompt-Based Image Editing (`gemini-nano-banana-2.1`)**: Text-guided image transformations (e.g., adding neon constellations, changing to golden hour sunset, atmospheric mist).

### 5. 📔 Expedition Journal & Itinerary Planner
- **Day-by-Day Scheduling**: Organize discovered stops, morning/afternoon time slots, and personalized travel tips.
- **Markdown Trip Export**: Export complete itinerary summaries and voice memos with one click.
- **GitHub Deployment Assistant**: Built-in instructions and Git CLI reference to push your code directly to GitHub.

---

## 🚀 How to Deploy to GitHub

### Option A: From AI Studio Web UI (Recommended)
1. In Google AI Studio, click the **Settings / Export** menu in the top-right corner.
2. Select **"Push to GitHub"**.
3. Authenticate with your GitHub account and specify your repository name.
4. Click **"Push"** — AI Studio will commit the full project with all assets!

### Option B: Using Git in Terminal
```bash
git init
git add .
git commit -m "Initial release of WanderPulse AI Exploration Studio"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/wanderpulse.git
git push -u origin main
```

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin 2.2+
- **UI Toolkit**: Jetpack Compose with Material Design 3 (Dynamic Color)
- **Image Loading**: Coil Compose
- **Architecture**: MVVM with unidirectional data flow (UDF) & Kotlin Coroutines StateFlow
- **Graphics**: Hardware-accelerated Jetpack Compose Canvas (custom map engine & audio waveform)
- **Build System**: Gradle Kotlin DSL (`build.gradle.kts`) with Version Catalog (`libs.versions.toml`)
- **API & Grounding Models**:
  - `gemini-3.8-flash`: General intelligence & Search/Maps Grounding
  - `gemini-3.8-live`: Real-time voice conversations
  - `gemini-3.5-transcribe`: High-fidelity audio transcription
  - `gemini-3-pro-image-preview`: 1K, 2K, and 4K image generation
  - `gemini-nano-banana-2.1`: Text-driven image editing
  - `gemini-4-argon`: Deep multimodal architectural vision & reasoning
  - `gemini-3.1-flash-lite`: Fast-turn factual assistance
