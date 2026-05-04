# AGENTS.md — SimpleGymTracker

## Project Overview

**SimpleGymTracker** is an Android application for tracking gym workouts. It is designed to be simple, beautiful, and easy to use — a deliberate contrast to the cluttered, unintuitive fitness apps on the Play Store.

- **Repository:** https://github.com/omardeveloping/SimpleGymTracker
- **Team:** Omar (Data/Backend), Bryan (UI/Design)
- **License:** Private / TBD

## Architecture

### Tech Stack
| Technology | Purpose |
|------------|---------|
| Android Studio | Development environment |
| Kotlin | Main programming language |
| Jetpack Compose | UI framework |
| Room | Local database ORM |
| SQLite | Underlying database |
| Navigation Compose | Screen navigation |
| Vico | Charting library for progress graphs |
| ViewModel | UI state management |
| Coroutines + Flow | Reactive data streams |

### Architecture Pattern
**MVVM + Repository Pattern**
- **UI Layer:** Compose screens + ViewModels (StateFlow)
- **Domain Layer:** Repository classes abstract data operations
- **Data Layer:** Room entities, DAOs, and AppDatabase

### Package Structure
```
com.example.simplegymtracker
├── data
│   ├── AppDatabase.kt          # Room database singleton
│   ├── dao/                    # Data Access Objects
│   ├── entity/                 # Room entities
│   └── repository/             # Repository classes
├── ui
│   ├── theme/                  # Colors, Typography, Theme
│   ├── viewmodel/              # ViewModels
│   └── screens/                # Composable screens (NEW)
└── MainActivity.kt             # NavHost entry point
```

## Build Instructions

### Prerequisites
- Android Studio (latest stable)
- JDK 21
- Android SDK 36

### Build
```bash
./gradlew assembleDebug
```

### Run Tests
```bash
./gradlew test
```

## Coding Conventions

### Kotlin
- Follow Kotlin Coding Conventions (official)
- Use `val` by default; `var` only when necessary
- Prefer immutable data structures
- Use coroutines (`viewModelScope`) for async operations
- Flows exposed as `StateFlow` with `WhileSubscribed(5000)`

### Compose
- Stateless composables where possible; hoist state to ViewModel
- Use `Modifier` parameter pattern for all composables
- Material3 components as base; customize via theme
- Preview every screen composable

### Navigation
- Routes defined as sealed class or constants
- Deep linking ready (future)
- Back handling explicit

## Key Decisions

1. **No Hilt/DI (for now):** Manual ViewModelProvider.Factory pattern. Can migrate to Hilt later.
2. **Room for TimerConfig:** The spec mentions DataStore for settings, but Omar implemented TimerConfig in Room. We keep it to respect boundaries.
3. **Single Database:** `AppDatabase` singleton pattern with standard Room builder.
4. **Light Mode Default:** Dark mode supported but not default — gyms vary in lighting.

## Design System

Refer to `DESIGN.md` for:
- Color palette (Electric Blue primary)
- Typography scale
- Component styles (buttons, cards, inputs)
- Spacing system
- Screen-specific layouts

## Team Responsibilities

| Member | Role | Responsibilities |
|--------|------|------------------|
| Omar | Data Engineer | Database schema, Room setup, data logic, chart data prep, session copy logic |
| Bryan | UI/Frontend | All screens, navigation, theme, animations, UX polish |

## Communication

- **Git:** Feature branches → PR → main
- **Project Board:** https://github.com/users/omardeveloping/projects/3
- **Mockup:** https://www.figma.com/file/VWO9d50v7v2bwC3sKwB7Vw/Simple-Gym-Tracker

## Future Considerations

- Dependency Injection (Hilt)
- DataStore migration for simple settings
- Export/share workout data
- Wear OS companion app
- Cloud sync
