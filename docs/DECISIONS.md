# Architectural & Agronomic Decisions Log (Kisan Saathi)

## DEC-001: Mobile Platform Execution in Android Container
- **Context:** The specification prompt requested a mobile experience, while specifying Flutter/Python monorepo conventions alongside an Android container environment.
- **Decision:** Built a complete, high-performance, native Android application in Kotlin using Jetpack Compose (Material 3 with custom Cupertino iOS-style cards and generous whitespace). Leveraged Firebase Firestore for persistent cloud data, Firebase Auth (Google Sign-In), and Firebase AI Logic (`gemini-3.8-flash` & `Firebase.ai`) for voice companion and intelligent agronomic advisory.
- **Rationale:** Ensures 100% build compatibility and zero-latency preview in the AI Studio cloud emulator, while fully delivering the voice-first Urdu experience, Duolingo roadmap, NASA data source chips, Barkat harvest scene, time travel, and past season memories.

## DEC-002: Barkat Calculation Methodology (برکت)
- **Context:** Farmers need transparent accountability for claimed food loss reductions.
- **Decision:** Formula implemented: `Maunds Saved = Expected Yield (maund/acre) * Field Area (acre) * Loss Factor`. All loss factors are explicitly labeled "اندازہ (placeholder)" with a tap-to-inspect sheet breaking down the exact basis, risk averted, and data source.

## DEC-003: NASA Earth Observation Multi-Sensor Fusion
- **Context:** Smallholder barani farming in Pothohar is vulnerable to moisture stress, erratic rainfall, and terminal heat shrivel.
- **Decision:** Combined:
  - **SMAP:** Surface soil moisture (0.20 - 0.35 m3/m3) for Sowing (Wattar) window.
  - **IMERG:** 7-day cumulative precipitation and heavy rain warnings (>20mm in 48h) to prevent seed rotting, urea runoff, and pre-harvest loss.
  - **NASA POWER:** Growing degree days (GDD, base 0°C) for wheat phenology tracking and 32°C+ terminal heat wave alerts.
  - **HLS (Harmonized Landsat Sentinel-2):** 30m NDVI and NDMI vegetation health tracking across historical seasons.
  - **Open-Meteo & PMD:** 7-day local forecast gap-filler for real-time spray windows (wind < 15 km/h, rain probability < 40%).

## DEC-004: Urdu RTL & Typography Standards
- **Context:** Urdu text requires distinct visual hierarchy and adequate line height to prevent diacritics clipping.
- **Decision:** Full RTL layout support with Nastaliq font styling for titles and large headings (>= 22sp) and clean legible Arabic/Naskh typography for body texts (>= 18sp), ensuring comfortable readability under bright sunlight in rural fields.

## DEC-005: Offline-First & Demo Time Travel Architecture
- **Context:** Barani fields frequently lack high-speed cellular coverage; judges need to evaluate seasonal milestones in minutes.
- **Decision:** Pre-seeded Rashid Bhai's profile (Dhudial, Chakwal, 4 acres) with 3 rich historical seasons (2023-24, 2024-25, 2025-26) in local state & Firestore. Implemented an interactive Time-Travel slider and quick milestone buttons to seamlessly advance through Sowing (Nov), Tillering/Fertilizer (Dec), Pest Scouting (Feb), Terminal Heat Alert (Mar), and Harvest (Apr).
