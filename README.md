# Kisan Saathi (کسان ساتھی)

**A voice-first farming companion for rainfed smallholder farmers in Pothohar, Pakistan.**

Kisan Saathi helps farmers make timely, practical decisions throughout the wheat-growing season. The project brings together NASA Earth-observation datasets, regional agronomy guidance, Urdu-first assistance, and simple tools for tracking field health and food-loss prevention.

## Project highlights

- **Seasonal crop roadmap** — guided stages for sowing, tillering, pest scouting, grain filling, and harvest, with clear progress states and actionable advisories.
- **Earth-observation context** — uses datasets and products including NASA SMAP soil moisture, GPM IMERG precipitation, Harmonized Landsat–Sentinel (HLS), NASA POWER, ECOSTRESS, and NASADEM. Advisory source labels help explain where information comes from.
- **Barkat (برکت) tracker** — illustrates estimated food loss prevented and explains the assumptions behind the estimate.
- **Season history** — connects current conditions with previous-season notes and outcomes.
- **Summer crop planning** — compares summer fallow, mungbean, and sorghum fodder against priorities such as water, income, soil health, and cattle feed.
- **Field health** — visualizes vegetation-health trends, including NDVI comparisons.
- **Ask Kisan Saathi** — an Urdu/English AI assistance experience for farmer questions.
- **Demo timeline** — lets users move through the crop season and see how roadmap guidance changes.

The primary Android application is built with Kotlin and Jetpack Compose. The repository also contains a Flutter client under `kisan_saathi_flutter/`; its current configuration uses mock repository and voice-service implementations by default.

## Repository layout

```text
.
├── app/                         # Native Android application (Kotlin + Compose)
├── config/                      # Crop, threshold, regional, and estimation configuration
├── docs/                        # Product decisions, data notes, and supporting documentation
├── gradle/                      # Gradle wrapper and version catalog
├── kisan_saathi_flutter/         # Flutter client
├── firestore.rules              # Firestore security rules
├── firestore.test.js            # Firestore rules tests
├── firebase.json                # Firebase emulator/project configuration
├── build.gradle.kts             # Root Android build configuration
└── settings.gradle.kts
```

## Tech stack

| Area | Technology |
|---|---|
| Android app | Kotlin, Jetpack Compose, Material 3 |
| Build | Gradle Kotlin DSL, Gradle version catalog |
| Backend services | Firebase Authentication, Cloud Firestore, Firebase AI Logic |
| AI assistance | Gemini through Firebase AI Logic |
| Flutter client | Flutter, Dart, Material |
| Localization | English and Urdu; bundled Nastaliq and Arabic-compatible fonts |
| Earth observation | NASA SMAP, GPM IMERG, HLS, NASA POWER, ECOSTRESS, NASADEM |
| Configuration and tests | YAML configuration, Firestore security rules, Gradle and Node.js tests |

## Getting started

### Requirements

For the native Android app:

- Android Studio (or the Android command-line tools)
- JDK 21
- Android SDK configured for the project (compile SDK 36)
- Firebase project configuration if you want to use Firebase-backed features

For the Flutter client:

- Flutter SDK compatible with Dart 3.2 or later
- Android tooling if you want to run it on an Android device or emulator

### Native Android app

1. Clone the repository:

   ```bash
   git clone https://github.com/starbucksat4thave/Kisan-Saathi.git
   cd Kisan-Saathi
   ```

2. Add your Firebase Android configuration at `app/google-services.json` and configure any required local secrets in `.env`. Do not commit private keys, service-account files, signing keystores, or local secret files.

3. Build the debug APK:

   ```bash
   ./gradlew :app:assembleDebug
   ```

4. Run unit tests:

   ```bash
   ./gradlew :app:testDebugUnitTest
   ```

On Windows, use `gradlew.bat` instead of `./gradlew`.

### Flutter client

The Flutter client is in its own directory:

```bash
cd kisan_saathi_flutter
flutter pub get
```

If the Android platform scaffold has not been generated in your checkout, create it once and then run the app:

```bash
flutter create . --platforms=android
flutter run
```

The current Flutter API configuration selects a mock backend by default. See [BACKEND_INTEGRATION_GUIDE.md](kisan_saathi_flutter/BACKEND_INTEGRATION_GUIDE.md) before switching it to a live API.

## Firestore rules and demo tests

The repository includes Firestore security rules and a Node.js rules test suite. The emulator is configured for port `8085`.

To run the rules tests, install the Firebase rules testing dependencies in your local Node.js environment, start the Firestore emulator, and then run:

```bash
make demo
```

The Android test task can be run with:

```bash
make test
```

See [docs/README.md](docs/README.md), [docs/DATA.md](docs/DATA.md), [docs/DECISIONS.md](docs/DECISIONS.md), and [docs/RECORDINGS.md](docs/RECORDINGS.md) for additional project context.

## Configuration and security notes

- Keep `.env`, API tokens, Firebase service-account credentials, signing keys, and keystores out of version control.
- Provide your own Firebase project configuration for local development.
- Review Firebase App Check and Firestore rules configuration before deploying to production.
- Earth-observation products have different spatial resolutions and update latencies. Check the source and timestamp shown with an advisory before using it for a field decision.
- The app provides decision support, not a substitute for local agronomic advice or on-farm assessment.

## Contributing

1. Create a feature branch.
2. Keep changes focused and add or update tests where appropriate.
3. Run the relevant Android, Flutter, or Firestore checks.
4. Open a pull request describing the change and any setup required.

## License

No license file is currently included. Unless a license is added, the project remains subject to the default copyright terms.
