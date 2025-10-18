<div align="center">
<img src="app/src/main/1024.png" width="200" alt="Game of Thrones App Icon" style="border-radius: 20px;"/>

# Game of Thrones App

**Modern Android showcase: MVI architecture, multi-module design, offline-first with 100% Jetpack Compose**

[![API Level](https://img.shields.io/badge/API-30+-blue?style=for-the-badge&logo=android&labelColor=1a1a1a)](https://developer.android.com/) [![Kotlin](https://img.shields.io/badge/Kotlin-2.2.20-purple?style=for-the-badge&logo=kotlin&labelColor=1a1a1a)](https://kotlinlang.org/) [![Compose](https://img.shields.io/badge/Jetpack%20Compose-2025.09.01-green?style=for-the-badge&logo=jetpackcompose&labelColor=1a1a1a)](https://developer.android.com/jetpack/compose)

</div>

---

## 📖 About

An Android app showcasing enterprise-level architecture and 2025 best practices. Features Game of Thrones character data with offline-first browsing, filtering, character comparison, and favourites management. All built with Clean Architecture, MVI pattern, and extensive testing coverage.

**Demonstrates:** Multi-module architecture, Konsist architectural testing, Firebase integration, Material 3 adaptive layouts, and comprehensive test coverage

---

## ✨ Key Features

**User Experience**

- Offline-first character browsing with Room caching and network sync
- Debounced search (300ms) with persistent history
- Multi-criteria filtering (status, culture, seasons) and flexible sorting
- Character comparison with side-by-side attribute breakdown
- Favourites management
- Firebase Analytics tracking
- Material 3 dynamic theming and dark mode support

**Technical Highlights**

- MVI pattern with unidirectional data flow
- Multi-module architecture (5 feature, 8 core modules)
- Offline-first with Room + Retrofit repository pattern
- Konsist architectural rules enforced
- Firebase Performance, Analytics, and Crashlytics
- Baseline Profiles for optimised startup
- Code quality: Detekt, Spotless, Kover coverage tracking

---

## 📱 Screenshots

<div align="center"> <table> <tr> <td><img src="screenshots/phone_search.png" width="200"/><br/><strong>Search & Filter</strong></td> <td><img src="screenshots/phone_details.png" width="200"/><br/><strong>Character Details</strong></td> <td><img src="screenshots/phone_favorites.png" width="200"/><br/><strong>Favourites</strong></td> <td><img src="screenshots/phone_comparison.png" width="200"/><br/><strong>Comparison</strong></td> <td><img src="screenshots/phone_settings.png" width="200"/><br/><strong>Settings</strong></td> </tr> </table> </div>

### Tablet Layout

<div align="center"> <table> <tr> <td><img src="screenshots/tablet_search.png" width="400"/><br/><strong>Search & Filter</strong></td> <td><img src="screenshots/tablet_details.png" width="400"/><br/><strong>Character Details</strong></td> </tr> </table> </div>

---

## 🏗️ Architecture

**MVI + Clean Architecture** with strict layer separation:

```
UI (Composables) → Intents
  ↓
ViewModel (StateFlow)
  ↓
UseCases (Domain Logic)
  ↓
Repository (Cache-first)
  ↓
Room ⟷ Retrofit
```

**Tech Stack**

- **Kotlin 2.2.20** with Coroutines, Flow, sealed classes
- **Compose BOM 2025.09.01** - 100% declarative UI, zero XML
- **Room 2.8.1** - Offline-first reactive queries
- **Retrofit 3.0** + **OkHttp 5.1** - Type-safe API client
- **Hilt 2.57.1** - Compile-time dependency injection
- **Firebase BOM 34.3.0** - Analytics, Crashlytics, Performance
- **Coil 3.0.4** - Image loading with Compose integration

**Testing & Quality**

- **44 test files**: Unit, integration, UI, and architectural tests
- **Konsist 0.17.3** - 22 architectural compliance rules
- **Detekt 1.23.7** + Compose rules - Static analysis
- **Spotless 8.0.0** + ktlint - Consistent formatting
- **Kover 0.9.2** - Code coverage tracking
- **Mockk, Turbine, Truth** - Robust test utilities

---

## 📂 Module Structure

```
app/                    # Application entry, WorkManager, Konsist tests
feature/                # Feature modules (presentation layer)
  ├── characters/       # List, search, filter
  ├── character-detail/ # Individual details
  ├── favorites/        # Favourites management
  ├── comparison/       # Character comparison
  └── settings/         # App preferences
core/                   # Shared modules
  ├── model/            # Domain models
  ├── domain/           # UseCases, repository interfaces
  ├── data/             # Repository implementations
  ├── database/         # Room DAOs and entities
  ├── network/          # Retrofit API
  ├── ui/               # Shared composables, theme
  ├── common/           # Utilities
  └── analytics/        # Firebase wrappers
```

**Why multi-module?**

- Parallel builds and Gradle caching reduce build times
- Clear boundaries prevent circular dependencies
- Features testable in isolation
- Konsist tests enforce layer separation

### Module Graph

```mermaid
%%{
  init: {
    'theme': 'base',
    'themeVariables': {"primaryTextColor":"#fff","primaryColor":"#6D5E0F","primaryBorderColor":"#DBC66E","lineColor":"#F8E287","tertiaryColor":"#665E40","fontSize":"12px"}
  }
}%%

graph LR
  subgraph :core
    :core:domain["domain"]
    :core:common["common"]
    :core:model["model"]
    :core:analytics["analytics"]
    :core:ui["ui"]
    :core:data["data"]
    :core:network["network"]
    :core:database["database"]
  end
  subgraph :feature
    :feature:settings["settings"]
    :feature:comparison["comparison"]
    :feature:characters["characters"]
    :feature:character-detail["character-detail"]
    :feature:favorites["favorites"]
  end
  :core:domain --> :core:common
  :core:domain --> :core:model
  :feature:settings --> :core:model
  :feature:settings --> :core:common
  :feature:settings --> :core:analytics
  :feature:settings --> :core:domain
  :feature:settings --> :core:ui
  :feature:settings --> :core:data
  :core:data --> :core:model
  :core:data --> :core:common
  :core:data --> :core:network
  :core:data --> :core:database
  :core:data --> :core:domain
  :core:data --> :core:ui
  :core:ui --> :core:common
  :core:ui --> :core:domain
  :core:database --> :core:common
  :feature:comparison --> :core:model
  :feature:comparison --> :core:common
  :feature:comparison --> :core:analytics
  :feature:comparison --> :core:domain
  :feature:comparison --> :core:ui
  :app --> :feature:characters
  :app --> :feature:character-detail
  :app --> :feature:favorites
  :app --> :feature:comparison
  :app --> :feature:settings
  :app --> :core:model
  :app --> :core:data
  :app --> :core:domain
  :app --> :core:ui
  :core:network --> :core:common
  :feature:favorites --> :core:model
  :feature:favorites --> :core:common
  :feature:favorites --> :core:analytics
  :feature:favorites --> :core:domain
  :feature:favorites --> :core:ui
  :feature:characters --> :core:model
  :feature:characters --> :core:common
  :feature:characters --> :core:analytics
  :feature:characters --> :core:domain
  :feature:characters --> :core:ui
  :feature:characters --> :core:data
  :feature:characters --> :core:network
  :feature:character-detail --> :core:model
  :feature:character-detail --> :core:common
  :feature:character-detail --> :core:analytics
  :feature:character-detail --> :core:domain
  :feature:character-detail --> :core:ui

classDef android-library fill:#534600,stroke:#fff,stroke-width:2px,color:#fff;
classDef android-application fill:#43664E,stroke:#fff,stroke-width:2px,color:#fff;
class :core:domain android-library
class :core:common android-library
class :core:model android-library
class :feature:settings android-library
class :core:analytics android-library
class :core:ui android-library
class :core:data android-library
class :core:network android-library
class :core:database android-library
class :feature:comparison android-library
class :app android-application
class :feature:characters android-library
class :feature:character-detail android-library
class :feature:favorites android-library

```
## 🧪 Testing

```bash
./gradlew test                    # All unit tests
./gradlew connectedAndroidTest    # UI tests
./gradlew koverHtmlReport         # Coverage report
./gradlew detekt                  # Static analysis
./gradlew spotlessApply           # Format code
```

**Coverage:** Kover configured with exclusions for generated code (Hilt, Room)

**Konsist Rules Enforce:**

- Layer dependency constraints (no presentation in domain)
- Naming conventions (UseCase suffix in domain, ViewModel in features)
- MVI patterns (ViewModels expose StateFlow, not MutableStateFlow)
- UseCase single responsibility (operator invoke only)


---

## 👨‍💻 Developer

**Darach Ronayne** | Senior Android Developer @ LUSH

🔗 [LinkedIn](https://www.linkedin.com/in/darachronayne/) | 🐙 [GitHub](https://github.com/DRonayne) | 📧 [Email](mailto:darachronayne@gmail.com)

---