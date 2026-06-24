# Unstamped Pages — Android

A travel companion app for Android that helps adventurers track countries visited, manage packing checklists, journal their trips, and collect digital passport stamps. Built with Jetpack Compose and an Indiana Jones–inspired adventure theme.

[Watch the demo](https://www.youtube.com/watch?v=j8xZrijS6s0) · [SonarCloud](https://sonarcloud.io/project/overview?id=jpdust_up-android)

---

## Table of Contents

- [Features](#features)
- [Screenshots](#screenshots)
- [Getting Started](#getting-started)
- [Architecture](#architecture)
- [Feature Deep Dives](#feature-deep-dives)
- [Data Layer](#data-layer)
- [UI & Theme](#ui--theme)
- [Observability](#observability)
- [Testing](#testing)
- [CI/CD](#cicd)
- [Configuration & Secrets](#configuration--secrets)
- [Future Work](#future-work)

---

## Features

| Feature | Description |
|---------|-------------|
| **World Map** | Interactive, pannable, zoomable world map with country tap-to-detail |
| **Country Info** | Detailed country cards — safety levels, visa requirements, currency converter, power outlets, health risks |
| **Travel Checklist** | Category-based packing list with templates, quantity tracking, multi-select, and pin support |
| **Trip Log** | Personal travel journal with title, content, location, and date fields |
| **My Stamps** | Digital passport stamp collection — photo per country via camera or gallery |
| **Travel Advisories** | Direct links to US, UK, Australian, and Canadian government travel advisories |

---

## Screenshots

> _Screenshots coming soon._

---

## Getting Started

### Requirements

| Tool | Version |
|------|---------|
| Java | 21 |
| Android SDK (compile) | 37 |
| Android SDK (target) | 36 |
| Android SDK (minimum) | 26 (Android 8.0 Oreo) |
| Gradle | 9.6.0 |
| AGP | 9.1.1 |
| Kotlin | 2.4.0 |

### Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build signed release AAB (requires keystore env vars — see Configuration)
./gradlew bundleRelease

# Run JVM unit tests
./gradlew test

# Run instrumented tests (requires running emulator or connected device)
./gradlew connectedAndroidTest

# Generate JaCoCo coverage report
./gradlew jacocoDebugCoverageReport

# Run SonarCloud analysis
./gradlew jacocoDebugCoverageReport sonar

# Clean + full build
./gradlew clean assembleDebug

# Check Java version
java -version
```

### Local Setup

1. Clone the repository.
2. Open in Android Studio (Hedgehog or later recommended).
3. Ensure `local.properties` contains your SDK path and New Relic token:
   ```properties
   sdk.dir=/path/to/your/android/sdk
   newrelic.token=YOUR_NEWRELIC_TOKEN
   ```
   > `local.properties` is gitignored. The New Relic token is only required if you want local monitoring; the app starts without it (the token will be an empty string and New Relic will not report events).
4. Sync Gradle and build.

---

## Architecture

### Pattern

**MVVM with Repository** — each feature screen has a dedicated `ViewModel` that exposes `StateFlow`s consumed by Compose UI. ViewModels never reference Android framework objects directly; all I/O goes through Repository interfaces.

```
UI (Composables)
    └── ViewModel (StateFlow, business logic)
            └── Repository (data access abstraction)
                    ├── Room DAOs (local persistence)
                    └── CountryRepository (in-memory country data)
```

### Tech Stack

| Layer | Technology |
|-------|-----------|
| UI | Jetpack Compose, Material 3 |
| Navigation | Compose Navigation, bottom nav bar |
| State management | `ViewModel` + `StateFlow` |
| Local database | Room 2.8.4 with KSP compiler |
| Dependency injection | Manual (constructor injection via `ViewModelProvider.Factory`) |
| Image handling | `ActivityResultContracts` (camera + gallery), `FileProvider` |
| JSON parsing | Gson (GeoJSON world map) |
| Build tooling | Gradle 9.6.0 (Kotlin DSL), KSP |
| Monitoring | New Relic Mobile SDK 7.7.6 |
| Code quality | SonarCloud, Android Lint, JaCoCo |

### Database Schema

**Database:** `unstamped_pages.db` — Room, version 3

| Entity | Table | Primary Key | Notable Columns |
|--------|-------|-------------|-----------------|
| `ChecklistItem` | `checklist_items` | `id` (auto) | `name`, `category`, `quantity`, `isChecked`, `isPinned`, `sortOrder` |
| `TripLogEntry` | `trip_log_entries` | `id` (auto) | `title`, `content`, `location`, `date` |
| `StampItem` | `stamp_items` | `countryCode` | `imagePath` |

### Navigation Routes

| Route | Screen | Bottom Nav Icon |
|-------|--------|----------------|
| `home` | `HomeScreen` | House |
| `country_info` | `CountryInfoScreen` | Explore (globe) |
| `checklist` | `ChecklistScreen` | Checklist |
| `trip_log` | `TripLogScreen` | Menu Book |
| `my_stamps` | `MyStampsScreen` | Photo Library |

Screen transitions use a 300 ms fade. The app header animates in/out based on the current route — hidden on `home`, visible elsewhere.

---


## Feature Deep Dives

### Home Screen

The home screen is the app's landing page and serves as the primary navigation hub.

- **Background**: Full-bleed image with a dark scrim overlay for text legibility.
- **Animated compass**: Custom-drawn `Canvas` composable with a radial gradient and cardinal direction labels. Rotates on first composition.
- **Feature cards**: Scrollable row of tap targets for each main section — each card uses a custom icon drawn in Canvas (compass, checklist, journal, stamp).
- **Auto-sizing title**: A custom `AutoSizeTitle` composable shrinks the font size dynamically to fit the available width without truncation.
- **Inspirational quote**: Static travel quote rendered at the bottom of the scroll area.

---

### Country Info (World Map)

The most technically complex screen in the app.

#### Map Rendering

- **GeoJSON data**: `world_geo.json` contains Natural Earth 10 m resolution country polygon boundaries. Parsed at startup by `GeoJsonParser` into `CountryGeometry` objects (lists of `LatLng` polygons).
- **Async loading**: `CountryGeometryData.initializeAsync()` is called from `MainActivity.onCreate()` to begin parsing on a background thread before the user navigates to the map, minimising perceived load time.
- **Binary cache**: `GeometryBinaryCache` serialises the parsed geometry to a binary format on first load and reads from cache on subsequent launches, avoiding re-parsing the full GeoJSON.
- **Canvas drawing**: `WorldMapCanvas` projects lat/long coordinates onto the Compose `Canvas` using an equirectangular projection scaled to the current zoom and pan offset.
- **Gestures**: `detectTransformGestures` handles simultaneous pinch-to-zoom and pan. Zoom is clamped to a sensible range; pan is bounded to prevent the map from being dragged entirely off screen.
- **Hit testing**: Country tap detection uses a ray-casting point-in-polygon algorithm against the projected polygon coordinates.

#### Color Modes

The map can be recoloured by theme:

| Mode | Colours countries by |
|------|----------------------|
| `DEFAULT` | Single neutral fill |
| `SECURITY_RISK` | Safety level (green → red) |
| `VISA_REQUIREMENTS` | Visa-free, visa-on-arrival, required |
| `PASSPORT_VALIDITY` | Required validity period |
| `YELLOW_FEVER` | Risk / vaccination required |
| `MALARIA` | Risk present |

#### Country Detail Sheet

Tapping a country (or selecting from the search bar) slides up a `ModalBottomSheet` containing:

- Flag emoji, country name, continent
- **Safety level** — colour-coded badge (green / yellow / orange / red)
- **Travel advisory links** — tabbed browser opening US State Dept., UK FCDO, Australian DFAT, and Canadian Global Affairs URLs in a Chrome Custom Tab
- **Visa requirement** — enum-driven label (Not Required, On Arrival, Required, eVisa, etc.)
- **Passport validity** — minimum validity requirement (6 months, 3 months, planned stay, etc.)
- **Currency converter** — bidirectional USD ↔ local currency calculator with live exchange rate data
- **Electrical outlets** — outlet type(s) used in the country (Type A, B, C, …)

#### Search

A `TextField` at the top of the screen filters the 195-country list. Results are shown in a dropdown; selecting one centres the map on the country and opens its detail sheet. `TerritoryAliases` handles common name variations (e.g. "Taiwan" → Chinese Taipei).

---

### Travel Checklist

A fully-featured packing list manager.

- **Categories**: Items are grouped into `ChecklistCategory` enums (Clothing, Documents, Electronics, Toiletries, etc.) each with a sort order.
- **Templates**: Pre-built packing list templates can be loaded via the `TemplateSelector` composable, replacing or appending to the current list.
- **Quantity tracking**: Each item can have a numeric quantity; a `QuantityPicker` inline component handles increment/decrement.
- **Pin / prioritise**: Items can be pinned to appear at the top of their category.
- **Swipe to delete**: `SwipeableChecklistItem` wraps each row in a swipe gesture that reveals a delete action.
- **Multi-select**: Long-pressing enters multi-select mode; `MultiSelectActionBar` appears at the bottom with bulk-delete.
- **Progress header**: `ProgressHeader` displays a percentage bar and X/Y count of checked items.
- **Persistence**: All state is written to Room via `ChecklistRepository` and survives process death.

---

### Trip Log (Journal)

A lightweight travel diary.

- **Entry list**: `LazyColumn` of cards sorted newest-first. Date badges show "Today" or "Yesterday" for recent entries via `DateUtils`.
- **Entry editor**: `JournalEntryEditor` composable with fields for title, body, location, and date. Appears inline with a fade transition — no separate screen navigation.
- **Full CRUD**: Create, read, update, and delete via `TripLogRepository` backed by Room.
- **Empty state**: Illustrated empty state with a prompt to create the first entry.

---

### My Stamps

A visual passport stamp collection.

- **Country list**: All 195 countries are displayed in a scrollable table. Countries without a stamp show an empty placeholder.
- **Add stamp**: Tapping a row opens a dialog offering:
  - **Camera** — launches the device camera via `ActivityResultContracts.TakePicture`. A temp file is created in the app's cache under `camera_temp/` and shared via `FileProvider`. On capture, the image is copied to permanent storage under `files/upimages/`.
  - **Gallery** — `ActivityResultContracts.GetContent` for picking an existing image. The selected URI is copied to `files/upimages/`.
- **Runtime permissions**: Camera permission is requested at stamp-add time if not already granted, with a rationale dialog.
- **Thumbnails**: Stamp images are loaded as `Bitmap` and displayed in 60 dp rounded cards.
- **Remove**: A dedicated button removes the stamp and deletes the stored image file.

---

## Data Layer

### Country Data

`CountryRepository` contains hand-curated data for 60+ countries including:

- ISO country code and localised name
- Continent
- Safety level (`SAFE`, `EXERCISE_CAUTION`, `RECONSIDER_TRAVEL`, `DO_NOT_TRAVEL`)
- Visa requirement enum
- Passport validity requirement
- Currency code, name, and exchange rate vs. USD
- Power outlet type(s)
- Yellow fever and malaria risk flags
- Travel advisory URL slugs for US, UK, AU, CA government sites

`CountryList.kt` provides all 195 UN-recognised countries as a flat list of `Country(code, name)` used for the stamps screen and search.

### API (Stub)

A future backend integration layer is scaffolded but not yet implemented:

```kotlin
interface ApiService {
    suspend fun registerUser(email: String, password: String): Result<UserResponse>
    suspend fun loginUser(email: String, password: String): Result<AuthResponse>
    suspend fun syncChecklistItems(items: List<ChecklistItem>): Result<List<ChecklistItem>>
    suspend fun syncTripLogEntries(entries: List<TripLogEntry>): Result<List<TripLogEntry>>
    suspend fun getCountryData(countryId: String): Result<CountryApiResponse>
    suspend fun getExchangeRates(): Result<ExchangeRatesResponse>
}
```

All methods currently throw `NotImplementedError`. The base URL is configured as `https://api.unstampedpages.com/v1` with 30 s timeouts.

---

## UI & Theme

The app uses an **Indiana Jones Adventure** theme throughout.

| Token | Value | Usage |
|-------|-------|-------|
| Primary | `#6B4423` — rich leather brown | Buttons, active nav, headers |
| Secondary | `#D4A843` — antique gold | Accents, selection indicators |
| Accent | `#8B2500` — deep adventure red | Destructive actions, highlights |
| Background | `#F4E4BC` — parchment/sand | Screen backgrounds |
| Surface | Warm off-white | Cards, sheets |

Full dark mode support is implemented — dark surfaces use warm tones rather than neutral greys to maintain the adventure aesthetic.

Map-specific colours (`MapLand`, `MapOcean`, `MapBorder`, `MapHighlight`) are defined separately in `Color.kt` and are not part of the Material colour scheme.

Typography uses Material 3's `Typography` with display and body type scales.

---

## Localisation

The app ships with translations for five languages:

| Language | Code | Region |
|----------|------|--------|
| English | `en` | Default |
| Spanish | `es` | — |
| French | `fr` | — |
| Arabic | `ar` | — |
| Chinese (Simplified) | `zh-CN` | Mainland China |

### Changing the language

**Android 13 and above (per-app language):**
1. Open the device **Settings** app
2. Navigate to **Apps → Unstamped Pages → Language**
3. Select your preferred language

The app advertises its supported locales via `res/xml/locale_config.xml`, so only the five languages above appear in the per-app picker.

**Android 12 and below:**
The app follows the system language. To change it, go to **Settings → General Management → Language** (path varies by manufacturer) and set your preferred language system-wide.

---

## Observability

The app integrates **New Relic Mobile** for production monitoring.

| Component | Version |
|-----------|---------|
| Gradle plugin (`com.newrelic.agent.android:agent-gradle-plugin`) | `7.7.6` |
| Android agent SDK (`com.newrelic.agent.android:android-agent`) | `7.7.6` |

### Initialisation

New Relic is started in `MainActivity.onCreate()` before `super.onCreate()` to capture the full activity lifecycle:

```kotlin
NewRelic.withApplicationToken(BuildConfig.NEW_RELIC_TOKEN)
    .withLogLevel(AgentLog.DEBUG)
    .start(this.applicationContext)
```

The token is injected at build time via `BuildConfig.NEW_RELIC_TOKEN`, sourced from:
- **CI**: `NEW_RELIC_TOKEN` GitHub Actions secret
- **Local dev**: `newrelic.token` in `local.properties` (gitignored)

If the token is absent or empty, New Relic starts but does not report events — the app runs normally.

The Gradle plugin (`id("newrelic")` in `app/build.gradle.kts`) performs bytecode instrumentation of HTTP calls and interaction traces at build time.

### Custom Event Schema

All product-level analytics flow through two custom event tables defined in `AppAnalytics.kt`:

#### `UserAction` — deliberate user interactions

Every tap, selection, or form input across all screens. Filter by `screen` and `action`.

| Attribute | Type | Description |
|-----------|------|-------------|
| `screen` | String | Which tab the event originated from (see screen values below) |
| `action` | String | What the user did (see action values below) |
| `countryId` | String | ISO country code — present on country-scoped events |
| `countryName` | String | Human-readable country name — present on country-scoped events |
| `source` | String | `"map"` or `"search"` — present on `countrySelected` events |
| `currencyCode` | String | ISO currency code — present on `usdChanged` / `foreignChanged` events |

**Screen values:**

| `screen` | Tab |
|----------|-----|
| `countries` | Country Info (world map) |
| `checklist` | Travel Checklist |
| `tripLog` | Trip Log |
| `stamps` | My Stamps |
| `home` | Home |

**Action values — Countries tab (`screen = 'countries'`):**

| `action` | Fired when |
|----------|-----------|
| `searchFocused` | User taps the country search bar |
| `countrySelected` | User selects a country (map tap or search result) |
| `legendOpened` | User taps the compass icon to open the map legend |
| `legendClosed` | User taps X to close the map legend |
| `filterDefault` | User selects the Default map view |
| `filterSecurityRisk` | User selects the Security Risk map view |
| `filterVisaRequirements` | User selects the Visa Requirements map view |
| `filterPassportValidity` | User selects the Passport Validity map view |
| `filterYellowFever` | User selects the Yellow Fever map view |
| `filterMalaria` | User selects the Malaria map view |
| `advisoryUsOpened` | User taps the US State Dept. travel advisory chip |
| `advisoryUkOpened` | User taps the UK FCDO travel advisory chip |
| `advisoryCaOpened` | User taps the Canadian Global Affairs advisory chip |
| `advisoryAuOpened` | User taps the Australian DFAT advisory chip |
| `usdChanged` | User focuses the USD input in the currency converter |
| `foreignChanged` | User focuses the foreign currency input in the currency converter |
| `countryInfoDismissed` | User dismisses the country detail bottom sheet |

#### `MapGesture` — high-frequency map gestures

Pinch-zoom and pan gestures are stored separately from `UserAction` to avoid skewing interaction counts and to allow independent retention/sampling.

| Attribute | Type | Description |
|-----------|------|-------------|
| `action` | String | `"zoomed"` or `"panned"` |
| `zoomedIn` | Boolean | `true` if final scale > starting scale — present on `zoomed` events |
| `zoomLevel` | Double | Scale factor at gesture end (1.0 = default zoom) — present on `zoomed` events |
| `direction` | String | Dominant pan direction — present on `panned` events |

**Pan direction values:** `"left"`, `"right"`, `"up"`, `"down"`

#### `TabNavigation` — bottom nav bar

Fired directly in `BottomNavBar.kt` (outside `AppAnalytics`) when the user taps the Countries tab:

| Attribute | Value |
|-----------|-------|
| `tab` | `"Countries"` |

### Example NRQL Queries

```sql
-- All Countries tab interactions by action
SELECT count(*) FROM UserAction
WHERE screen = 'countries'
FACET action SINCE 7 days ago

-- Most selected countries and how users found them
SELECT count(*) FROM UserAction
WHERE action = 'countrySelected'
FACET countryName, source SINCE 30 days ago LIMIT 20

-- Search-to-selection conversion funnel
SELECT funnel(sessionId,
  WHERE action = 'searchFocused',
  WHERE action = 'countrySelected' AND source = 'search'
) FROM UserAction WHERE screen = 'countries' SINCE 7 days ago

-- Map filter usage breakdown
SELECT count(*) FROM UserAction
WHERE action IN ('filterDefault','filterSecurityRisk','filterVisaRequirements',
                 'filterPassportValidity','filterYellowFever','filterMalaria')
FACET action SINCE 7 days ago

-- Travel advisory engagement by country and provider
SELECT count(*) FROM UserAction
WHERE action IN ('advisoryUsOpened','advisoryUkOpened','advisoryCaOpened','advisoryAuOpened')
FACET countryName, action SINCE 30 days ago LIMIT 20

-- Currency converter engagement
SELECT count(*) FROM UserAction
WHERE action IN ('usdChanged','foreignChanged')
FACET countryName, currencyCode SINCE 30 days ago

-- Zoom depth distribution
SELECT histogram(zoomLevel, 10, 20) FROM MapGesture
WHERE action = 'zoomed' SINCE 7 days ago

-- Pan direction breakdown
SELECT count(*) FROM MapGesture
WHERE action = 'panned'
FACET direction SINCE 7 days ago

-- Full user journey funnel on the Countries tab
SELECT funnel(sessionId,
  WHERE screen = 'countries',
  WHERE action = 'countrySelected',
  WHERE action IN ('advisoryUsOpened','advisoryUkOpened','advisoryCaOpened','advisoryAuOpened'),
  WHERE action IN ('usdChanged','foreignChanged'),
  WHERE action = 'countryInfoDismissed'
) FROM UserAction SINCE 30 days ago
```

---

## Testing

### Unit Tests (JVM) — `src/test/`

Run with `./gradlew test`

| Package | Test File | What's covered |
|---------|-----------|----------------|
| `data` | `CountryListTest` | 195-country list completeness and uniqueness |
| `data.local.entity` | `ChecklistItemTest`, `TripLogEntryTest`, `StampItemTest` | Entity equality, copy, defaults |
| `data.model` | `CountryTest`, `CountryGeometryTest` | Country model, `LatLng`, point-in-polygon algorithm |
| `data.model` | `ChecklistCategoryTest`, `ChecklistProgressTest`, `ChecklistTemplateTest` | Checklist model logic |
| `data.repository` | `CountryRepositoryTest`, `GeometryBinaryCacheTest` | Repository queries, cache serialisation |
| `ui.navigation` | `NavRoutesTest` | Route strings, `items` list ordering |
| `ui.screens.checklist` | `CategorySectionTest`, `SwipeableChecklistItemTest` | Component state |
| `ui.screens.countryinfo` | `CountryInfoViewModelTest`, `WorldMapTapTest`, `WorldMapCanvasTest`, `CountryBoundsTest`, `CurrencyInputHelperTest`, `MapColorModeTest`, `TerritoryAliasesTest` | ViewModel state, tap detection, map math |
| `ui.screens.mystamps` | `MyStampsUiStateTest` | UI state data class |
| `ui.theme` | `ColorTest`, `ContrastValidationTest`, `ThemeColorSchemeTest` | Colour token values, WCAG contrast |
| `util` | `DateUtilsTest`, `GeoJsonParserTest` | Date formatting, GeoJSON parsing |

### Instrumented Tests (Android) — `src/androidTest/`

Run with `./gradlew connectedAndroidTest`

| Package | Test File | What's covered |
|---------|-----------|----------------|
| `data.local` | `AppDatabaseTest` | Room database creation, all DAOs |
| `data.repository` | `ChecklistRepositoryTest`, `TripLogRepositoryTest`, `StampRepositoryTest` | Full CRUD via in-memory Room database |
| `data.repository` | `CountryGeometryDataTest` | GeoJSON loading from raw resources |
| `ui.screens.*` | `ChecklistViewModelTest`, `TripLogViewModelTest`, `MyStampsViewModelTest` | ViewModel state, coroutine flows, camera state |
| `ui.navigation` | `BottomNavBarTest` | Navigation bar rendering and item selection |

### Coverage

JaCoCo coverage is collected for both unit and instrumented tests and merged into a single XML report per build type at:

```
app/build/reports/jacoco/{debug|release}/jacoco.xml
```

Coverage is enforced via SonarCloud quality gates on every PR.

---

## CI/CD

The GitHub Actions workflow (`.github/workflows/up-android.yml`) runs on every push and pull request to `master`.

### Job 1 — Test & Analyze

Runs on all pushes and PRs:

1. Checkout (full history for SonarCloud blame)
2. Set up JDK 21 (Temurin)
3. Set up Gradle (with action caching)
4. Write `local.properties` with `$ANDROID_HOME`
5. **Run unit tests** — `testDebugUnitTest`
6. **Create AVD snapshot** (cached by API level + OS)
7. **Run instrumented tests** — `connectedDebugAndroidTest` on API 34 AOSP ATD emulator
8. **Generate JaCoCo coverage report**
9. **Run Android Lint**
10. **SonarCloud analysis** — blocks on quality gate result (`sonar.qualitygate.wait=true`)

### Job 2 — Sign & Deploy

Runs only on pushes to `master` (after Job 1 passes):

1. **Compute version code** — `YYYYMMDDn` scheme (UTC date + daily counter suffix)
2. **Decode release keystore** from Base64 secret
3. **Build signed release AAB** — `bundleRelease`
4. **Upload to Google Play** internal testing track
5. **Commit version code bump** back to `master` with `[skip ci]`

### Required GitHub Secrets

| Secret | Used for |
|--------|---------|
| `SONAR_TOKEN` | SonarCloud authentication |
| `KEYSTORE_BASE64` | Base64-encoded release keystore |
| `KEYSTORE_PASSWORD` | Keystore password |
| `KEY_ALIAS` | Signing key alias |
| `KEY_PASSWORD` | Signing key password |
| `PLAY_SERVICE_ACCOUNT_JSON` | Google Play upload service account |
| `NEW_RELIC_TOKEN` | New Relic mobile app token |

---

## Configuration & Secrets

### Dependency Verification

The project uses Gradle's dependency verification (`gradle/verification-metadata.xml`) with both SHA-256 checksums and PGP signature checks. Artifacts whose keys cannot be resolved from key servers are listed in `<ignored-keys>` and fall back to checksum-only verification.

When adding new dependencies, regenerate the verification metadata:

```bash
./gradlew --write-verification-metadata sha256,pgp help
```

### Dependency Locking

`app/build.gradle.kts` activates dependency locking on all runtime, compile, and annotation processor configurations. After adding or upgrading a dependency, update the lock file:

```bash
./gradlew :app:dependencies --write-locks
```

### Commons IO Pin

The root `build.gradle.kts` explicitly pins `commons-io` to `2.20.0` in both the buildscript classpath and all project configurations. This ensures the SonarQube plugin always gets a version with the `builder()` API (added in 2.7), regardless of what version Android tools request transitively.

### Build Memory

`gradle.properties` sets the Gradle daemon heap to 4 GiB with a 512 MiB metaspace cap. This is required because the New Relic Gradle plugin performs bytecode instrumentation via R8 at build time, which is memory-intensive.

---

## Future Work

- [ ] **User accounts & cloud sync** — implement the stubbed `ApiService` backend for checklist and trip log synchronisation across devices
- [ ] **Live exchange rates** — replace static exchange rate data in `CountryRepository` with a real-time currency API
- [ ] **Offline maps** — bundle vector tiles for offline map rendering without a data connection
- [ ] **Trip planning mode** — pre-trip checklist and itinerary builder separate from the active trip log
- [ ] **Push notifications** — travel advisory alerts when the safety level of a saved country changes
- [ ] **Stamp sharing** — share passport stamp collections as a shareable image or PDF
- [ ] **Additional New Relic events** — extend `AppAnalytics` to cover checklist interactions, journal entry creation, and stamp additions across the remaining tabs
- [ ] **Accessibility audit** — full TalkBack support and WCAG 2.1 AA compliance pass
- [ ] **Tablet / foldable layout** — adaptive two-pane layout for larger screens
- [ ] **Widgets** — home screen widget for quick checklist access
- [ ] **Automated screenshot tests** — Paparazzi or Shot integration for visual regression testing
