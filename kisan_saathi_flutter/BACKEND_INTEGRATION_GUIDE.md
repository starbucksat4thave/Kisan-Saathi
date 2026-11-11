# Kisan Saathi — Flutter Frontend & Backend Integration Guide

## 1. Directory Structure (`c:\Kisan Saathi\kisan_saathi_flutter\`)

```text
c:\Kisan Saathi\kisan_saathi_flutter\
├── pubspec.yaml
├── analysis_options.yaml
├── assets/
│   └── fonts/
│       ├── noto_nastaliq_urdu.ttf
│       └── noto_sans_arabic.ttf
├── lib/
│   ├── main.dart
│   ├── core/
│   │   ├── config/
│   │   │   └── api_config.dart          # Toggle Mock vs Live Backend + Base URL
│   │   ├── network/
│   │   │   └── kisan_api_client.dart    # REST JSON HTTP Client + Auth Token
│   │   ├── theme/
│   │   │   ├── design_tokens.dart       # Colors, 8dp Grid, Radii, Dual Fonts
│   │   │   └── app_theme.dart           # Material 3 Light & Dark ColorSchemes
│   │   └── widgets/
│   │       └── kisan_components.dart    # KisanCard, KisanButton, KisanChip, KisanStatusBadge
│   ├── data/
│   │   ├── models/
│   │   │   └── kisan_models.dart        # Domain models with fromJson / toJson
│   │   ├── repositories/
│   │   │   └── kisan_repository.dart    # MockKisanRepository + RemoteKisanRepository
│   │   └── services/
│   │       └── kisan_voice_service.dart # MockKisanVoiceService + RemoteKisanVoiceService
│   └── features/
│       ├── shell/
│       │   └── kisan_app_shell.dart     # Top Bar + 3-Tab Bottom Navigation
│       ├── home/
│       │   └── home_screen.dart         # Seasonal Roadmap + Advisory Cards + Quick Stats
│       ├── ask/
│       │   └── ask_screen.dart          # Voice AI Screen + Canvas Waveform Ripple
│       └── field_health/
│           └── field_health_screen.dart # Custom NDVI Line Chart + SMAP Moisture Gauge
└── test/
    └── widget_test.dart
```

---

## 2. How to Run Without Backend (Right Now)

Right now, `ApiConfig.useMockBackend = true` is set in `lib/core/config/api_config.dart`. The app runs 100% standalone using `MockKisanRepository` and `MockKisanVoiceService`.

```powershell
cd "c:\Kisan Saathi\kisan_saathi_flutter"
flutter create . --platforms=android
flutter pub get
flutter run
```

---

## 3. How to Connect Your Backend Later (2 Steps)

When your backend (Node.js / FastAPI / Django / Laravel) is ready:

### Step 1: Update `lib/core/config/api_config.dart`
Open `lib/core/config/api_config.dart` and change:
```dart
static const bool useMockBackend = false;
static const String baseUrl = 'http://10.0.2.2:8000/api/v1'; // or your live domain
```
*(Note: `10.0.2.2` connects the Android Emulator to `localhost` on your PC. For a real Android phone on the same Wi-Fi, use your PC's LAN IP like `http://192.168.1.10:8000/api/v1`.)*

### Step 2: Implement These JSON Endpoints on Your Backend

1. **`GET /api/v1/farmer/profile`**
   ```json
   {
     "id": "farmer_chakwal_01",
     "name_en": "Rashid Minhas",
     "name_ur": "راشد بھائی",
     "village_en": "Dhudial",
     "village_ur": "ڈھڈیال",
     "tehsil_en": "Chakwal",
     "plot_name_en": "Bara Khet",
     "plot_name_ur": "بڑا کھیت",
     "area_acres": 4.0,
     "irrigation_type": "Rainfed (Barani)",
     "barkat_saved_maunds": 6.0
   }
   ```

2. **`GET /api/v1/fields/telemetry?season=rabi`**
   ```json
   {
     "temperature_c": 21,
     "condition_label": "Clear · Mild",
     "soil_moisture_pct": 24,
     "soil_moisture_volumetric": 0.24,
     "wattar_status": "Optimal Wattar",
     "rain_forecast_mm": 12,
     "rain_window_label": "Thu · 48h",
     "cumulative_gdd": 145
   }
   ```

3. **`GET /api/v1/roadmap/stages?season=rabi`**
   ```json
   {
     "stages": [
       {
         "id": "sowing",
         "order": 2,
         "title_en": "Sowing · Optimal Wattar Window",
         "title_ur": "بوائی (وتر کا سنہری وقت)",
         "date_window": "Nov 01 – Nov 15",
         "gdd_target": "0–150 GDD",
         "status": "current",
         "summary_en": "Drill Akbar-19 at 50 kg/acre while SMAP moisture is 24%."
       }
     ]
   }
   ```

4. **`GET /api/v1/roadmap/advisories?season=rabi`** & **`POST /api/v1/roadmap/advisories/complete`**
   - `severity` accepts `"info"`, `"act"`, or `"alert"`.

5. **`POST /api/v1/voice/ask`**
   - Request body: `{"query": "...", "village": "Dhudial, Chakwal", "crop_stage": "...", "language": "ur"}`
   - Response body:
     ```json
     {
       "id": "ai_01",
       "is_farmer": false,
       "text_ur": "راشد بھائی، ناسا SMAP سیٹلائٹ کے مطابق...",
       "summary_en": "SMAP moisture is 24% (optimal)...",
       "timestamp_label": "Grounded · SMAP + IMERG",
       "sources": ["SMAP 0.24 m³/m³", "IMERG 48h"]
     }
     ```

6. **`GET /api/v1/earth-observation/ndvi?field_id=bara_khet`** & **`GET /api/v1/earth-observation/smap?field_id=bara_khet`**
   - Maps directly to `NdviObservationPoint.fromJson` and `SmapFieldHealthMetrics.fromJson` in `lib/data/models/kisan_models.dart`.
