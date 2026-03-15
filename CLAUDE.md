# Unstamped Pages - Android App

A travel companion app for tracking countries visited, managing travel checklists, journaling trips, and collecting passport stamps.

## Quick Start

### Build Commands
```bash
# Build debug APK
./gradlew assembleDebug

# Run unit tests (JVM)
./gradlew test

# Run instrumented tests (requires emulator/device)
./gradlew connectedAndroidTest

# Clean build
./gradlew clean assembleDebug

# Check Java version (requires Java 17)
java -version
```

### Requirements
- **Java:** 17
- **Android SDK:** 34 (compile), 26 (min)
- **Gradle:** 8.5.0
- **Kotlin:** 1.9.24

## Project Structure

```
up-android/
├── app/
│   ├── build.gradle.kts          # App dependencies and config
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/unstampedpages/app/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── UnstampedPagesApp.kt
│   │   │   │   ├── analytics/    # Analytics tracking
│   │   │   │   ├── api/          # API service interfaces (stub)
│   │   │   │   ├── data/
│   │   │   │   │   ├── local/    # Room database, DAOs, entities
│   │   │   │   │   ├── model/    # Data models (Country, etc.)
│   │   │   │   │   └── repository/
│   │   │   │   ├── ui/
│   │   │   │   │   ├── navigation/
│   │   │   │   │   ├── screens/  # Feature screens
│   │   │   │   │   └── theme/    # Colors, typography
│   │   │   │   └── util/         # DateUtils, GeoJsonParser
│   │   │   └── res/
│   │   │       ├── raw/world_geo.json  # GeoJSON world map
│   │   │       └── xml/file_paths.xml  # FileProvider paths for camera
│   │   ├── test/                 # JVM unit tests
│   │   └── androidTest/          # Instrumented tests
├── build.gradle.kts              # Root build config
└── settings.gradle.kts
```

## Architecture

### Tech Stack
- **UI:** Jetpack Compose with Material 3
- **Navigation:** Compose Navigation with bottom nav bar
- **Database:** Room with KSP compiler
- **State Management:** ViewModel + StateFlow
- **Architecture:** MVVM with Repository pattern

### Database Schema

**AppDatabase** (`unstamped_pages.db`, version 2)

| Entity | Table | Primary Key | Description |
|--------|-------|-------------|-------------|
| `ChecklistItem` | `checklist_items` | `id` (auto) | Travel checklist items |
| `TripLogEntry` | `trip_log_entries` | `id` (auto) | Journal entries |
| `StampItem` | `stamp_items` | `countryCode` | Passport stamps |

### Navigation Routes

| Route | Screen | Icon |
|-------|--------|------|
| `home` | HomeScreen | House |
| `country_info` | CountryInfoScreen | Globe |
| `checklist` | ChecklistScreen | Checklist |
| `trip_log` | TripLogScreen | Journal |
| `my_stamps` | MyStampsScreen | PhotoLibrary |

## Key Features

### 1. Home Screen
- Welcome dashboard with feature cards
- Animated compass icon
- Navigation to all features

### 2. Country Info (World Map)
- Interactive canvas-based world map with GeoJSON data
- Pinch-to-zoom and pan gestures
- Country tap detection with ray-casting algorithm
- Bottom sheet with country details:
  - Population, currency, exchange rates
  - Safety level (color-coded)
  - Power outlet types

### 3. Travel Checklist
- Add/edit/delete items
- Toggle checked status
- Persisted to Room database

### 4. Trip Log (Journal)
- Create journal entries with title, content, location
- Date-based sorting (newest first)
- Full CRUD operations

### 5. My Stamps
- 195 countries list
- Add stamp images via:
  - **Camera capture** - Take photo directly in app (runtime permission required)
  - **Gallery upload** - Select existing image from device
- Images stored in `/files/upimages/`
- Camera temp files stored in `/cache/camera_temp/`
- Uses FileProvider for secure camera image URI sharing

## Data Sources

### Local Data
- **CountryList.kt:** 195 countries with ISO codes
- **CountryRepository:** 60+ countries with detailed info (population, currency, safety, outlets)
- **world_geo.json:** GeoJSON country boundaries for map rendering

### API (Stub Implementation)
```kotlin
// Future backend integration - currently returns NotImplementedError
interface ApiService {
    suspend fun registerUser(email: String, password: String): Result<UserResponse>
    suspend fun loginUser(email: String, password: String): Result<AuthResponse>
    suspend fun syncChecklistItems(items: List<ChecklistItem>): Result<List<ChecklistItem>>
    suspend fun syncTripLogEntries(entries: List<TripLogEntry>): Result<List<TripLogEntry>>
    suspend fun getCountryData(countryId: String): Result<CountryApiResponse>
    suspend fun getExchangeRates(): Result<ExchangeRatesResponse>
}
```

**API Config:**
- Base URL: `https://api.unstampedpages.com`
- Version: `v1`
- Timeouts: 30s (connect, read, write)

## Testing

### Unit Tests (JVM) - `src/test/`
Run with: `./gradlew test`

| Package | Test File | Coverage |
|---------|-----------|----------|
| `analytics` | AnalyticsTest | AnalyticsEvents constants |
| `api` | ApiServiceTest | ApiConfig, response data classes |
| `data` | CountryListTest | 195 countries validation |
| `data.model` | CountryTest, CountryGeometryTest | Country, LatLng, point-in-polygon |
| `data.local.entity` | ChecklistItemTest, TripLogEntryTest, StampItemTest | Entity data classes |
| `data.repository` | CountryRepositoryTest | Repository methods |
| `ui.navigation` | NavRoutesTest | Navigation routes |
| `ui.screens.countryinfo` | CountryInfoViewModelTest | ViewModel state |
| `util` | DateUtilsTest, GeoJsonParserTest | Utilities |

### Instrumented Tests (Android) - `src/androidTest/`
Run with: `./gradlew connectedAndroidTest`

| Package | Test File | Coverage |
|---------|-----------|----------|
| `analytics` | AnalyticsManagerTest | AnalyticsManager |
| `data.local` | AppDatabaseTest | Room database, all DAOs |
| `data.repository` | ChecklistRepositoryTest, TripLogRepositoryTest, StampRepositoryTest | Repository operations |
| `data.repository` | CountryGeometryDataTest | GeoJSON loading |
| `ui.screens.*` | ChecklistViewModelTest, TripLogViewModelTest, MyStampsViewModelTest | ViewModel state, camera functionality |

### Test Notes
- ViewModels with async operations (database) are tested via Repository tests
- ViewModel tests focus on synchronous state management
- `ApiServiceImpl` uses `android.util.Log` - tested in androidTest only

## UI Theme

**Indiana Jones Adventure Theme:**
- Primary: Rich leather brown (`#6B4423`)
- Secondary: Antique gold (`#D4A843`)
- Accent: Deep adventure red (`#8B2500`)
- Background: Parchment/sand (`#F4E4BC`)
- Dark mode: Full support

## Key Files Reference

| Purpose | File Path |
|---------|-----------|
| App entry | `MainActivity.kt`, `UnstampedPagesApp.kt` |
| Database | `data/local/AppDatabase.kt` |
| Entities | `data/local/entity/*.kt` |
| DAOs | `data/local/dao/*.kt` |
| Repositories | `data/repository/*.kt` |
| ViewModels | `ui/screens/*/ViewModel.kt` |
| Navigation | `ui/navigation/NavRoutes.kt`, `BottomNavBar.kt` |
| World Map | `ui/screens/countryinfo/WorldMapCanvas.kt` |
| GeoJSON Parser | `util/GeoJsonParser.kt` |
| Country Data | `data/CountryList.kt`, `data/repository/CountryRepository.kt` |
| FileProvider paths | `res/xml/file_paths.xml` |

## Common Tasks

### Add a new database entity
1. Create entity in `data/local/entity/`
2. Create DAO in `data/local/dao/`
3. Add DAO to `AppDatabase.kt`
4. Increment database version and add migration
5. Create repository in `data/repository/`

### Add a new screen
1. Create screen composable in `ui/screens/{feature}/`
2. Create ViewModel if needed
3. Add route to `NavRoute` sealed class
4. Add navigation in `UnstampedPagesApp.kt`
5. Add to `BottomNavBar` if top-level

### Add unit tests
- JVM tests: `src/test/java/com/unstampedpages/app/`
- Android tests: `src/androidTest/java/com/unstampedpages/app/`
- Use `@RunWith(AndroidJUnit4::class)` for instrumented tests
- Repository tests use in-memory Room database

## Dependencies

**Core:**
- androidx.core-ktx:1.12.0
- androidx.lifecycle-runtime-ktx:2.6.2

**Compose:**
- Compose BOM 2024.01.00
- material3, material-icons-extended
- navigation-compose:2.7.6

**Database:**
- room-runtime, room-ktx:2.6.1
- room-compiler (KSP)

**Testing:**
- junit:4.13.2
- espresso-core:3.5.1
- room-testing:2.6.1
- kotlinx-coroutines-test:1.7.3

## Permissions
- `INTERNET` - For future API communication
- `CAMERA` - For capturing stamp images (runtime permission requested)

## Hardware Features
- `android.hardware.camera` (optional) - Camera for stamp capture
