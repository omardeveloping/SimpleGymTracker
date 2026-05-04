# Simple Gym Tracker

> Simple and Cute Gym Tracker

A beautiful, intuitive Android application for tracking gym workouts. Built with Kotlin, Jetpack Compose, and Room.

## Overview

Available fitness apps on the Play Store tend to be cluttered with too many features or unintuitive interfaces. **Simple Gym Tracker** focuses on what matters: logging your exercises, tracking your sets, and visualizing your progress — all wrapped in a clean, friendly design.

## Features

- [x] **Daily Exercise Log** — Log exercises performed during the day
- [x] **Exercise Selection** — Choose from predefined exercises or create custom ones
- [x] **Sets Log** — Record weight and repetitions for each set
- [x] **Rest Timer** — Built-in timer for rest between sets
- [x] **Session History** — View and search previously recorded sessions
- [x] **Progress Chart** — Scatter plot with daily aggregation and configurable range
- [x] **Copy Previous Sessions** — Reuse past workouts as templates
- [x] **Timer Configuration** — Customize rest times between sets and exercises

## Tech Stack

| Technology | Purpose |
|------------|---------|
| Android Studio | Development environment |
| Kotlin | Main programming language |
| Jetpack Compose | UI framework and screen creation |
| Room | Local database management |
| SQLite | Phone-local database |
| Navigation Compose | Navigation between screens |
| Vico | Progress chart rendering |
| ViewModel | Connecting UI with logic and data |
| Coroutines + Flow | Reactive data streams |

## Architecture

**MVVM + Repository Pattern**

```
UI Layer (Compose + ViewModels)
    ↓
Repository Layer (Data abstraction)
    ↓
Data Layer (Room DAOs + Entities)
```

## Team

| Member | Role | Responsibilities |
|--------|------|------------------|
| **Omar** | Data Engineer | Database schema, Room setup, data logic, chart data preparation, session copy logic, settings persistence |
| **Bryan** | UI/Frontend | All application screens, visual interface, navigation, forms, session history view, progress chart, timer UI, UX polish |

## Design

The app follows a custom design system based on Notion's warm minimalism, energized with an **Electric Blue** accent for action and progress.

- **Primary Color:** `#3B82F6` (Electric Blue)
- **Default Theme:** Light mode (Dark mode supported)
- **Design System:** See `DESIGN.md`
- **Figma Mockup:** [Simple Gym Tracker](https://www.figma.com/file/VWO9d50v7v2bwC3sKwB7Vw/Simple-Gym-Tracker)

## Getting Started

### Prerequisites
- Android Studio (latest stable)
- JDK 21
- Android SDK 36

### Build
```bash
./gradlew assembleDebug
```

### Run
```bash
./gradlew installDebug
```

## Project Links

- **Repository:** https://github.com/omardeveloping/SimpleGymTracker
- **Project Board:** https://github.com/users/omardeveloping/projects/3

## Monetization

- Ads in non-invasive areas
- Future: Lifetime PRO plan with advanced capabilities

## License

Private project — all rights reserved.
