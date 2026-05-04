# SKILLS.md — SimpleGymTracker

## Installed Agent Skills

This project uses [autoskills](https://www.autoskills.sh/) to install curated AI agent skill files. These skills provide project-specific guidance for AI coding assistants.

### Active Skills (10 installed)

| # | Skill | Source | Relevance |
|---|-------|--------|-----------|
| 1 | `android-kotlin-core` | krutikJain | Kotlin best practices for Android |
| 2 | `android-compose-foundations` | krutikJain | Jetpack Compose patterns, state management, theming |
| 3 | `android-architecture-clean` | krutikJain | MVVM, Repository pattern, clean architecture |
| 4 | `android-di-hilt` | krutikJain | Dependency injection patterns (reference for future Hilt migration) |
| 5 | `android-gradle-build-logic` | krutikJain | Gradle configuration, build optimization |
| 6 | `android-coroutines-flow` | krutikJain | Coroutines, Flow, StateFlow best practices |
| 7 | `android-networking-retrofit-okhttp` | krutikJain | Networking patterns (future feature reference) |
| 8 | `android-testing-unit` | krutikJain | Unit testing patterns for Android |
| 9 | `java-docs` | github | Java documentation standards |
| 10 | `java-coding-standards` | affaan-m | Java/Kotlin coding conventions |

### Skill Location
All skill files are located in `.agents/skills/` and should NOT be committed to the repository (see `.gitignore`).

## Design System Skill

### DESIGN.md
A custom `DESIGN.md` is maintained at the project root. It defines:
- **Visual Theme:** Notion-inspired warm minimalism + Electric Blue accent
- **Color Palette:** Semantic colors, gym-specific tints, dark mode variants
- **Typography:** Scale optimized for mobile readability during workouts
- **Component Library:** Buttons, cards, inputs, pills, badges, timer components
- **Spacing System:** 4px base unit, touch-target minimums

### How to Use
When building UI, reference `DESIGN.md` directly:
- Use `{colors.primary}` (#3B82F6) for primary actions
- Use `{rounded.md}` (8px) for buttons, `{rounded.lg}` (12px) for cards
- Maintain 48px minimum touch targets for workout usability
- Prefer light mode; dark mode is secondary

## Tech Stack Reference

### Compose
- Material3 as base design system
- Custom theme overriding colors and typography per DESIGN.md
- Navigation Compose for screen routing
- Vico for chart rendering

### State Management
- ViewModel + StateFlow pattern
- `SharingStarted.WhileSubscribed(5000)` for UI observation
- Coroutines (`viewModelScope`) for all async operations

### Database
- Room 2.8.4 with KSP
- Singleton `AppDatabase` pattern
- Repository layer abstracts DAO access

## Quick Reference for Agents

### When Adding a New Screen
1. Define route in navigation setup
2. Create Composable in `ui/screens/`
3. Accept ViewModel as parameter (or use factory)
4. Wrap in `Scaffold` with appropriate top bar
5. Add `@Preview` composable
6. Reference DESIGN.md for colors and spacing

### When Adding a New Component
1. Keep it stateless if possible
2. Accept `modifier: Modifier = Modifier` parameter
3. Use Material3 base components (Button, Card, TextField)
4. Apply theme colors from DESIGN.md
5. Document with preview

### When Modifying Database
1. Coordinate with Omar (data owner)
2. Update entity → DAO → Repository → ViewModel chain
3. Increment database version if schema changes
4. Provide migration or fallback (destructive allowed during development)

## Resources

- **Android Compose Docs:** https://developer.android.com/jetpack/compose
- **Room Docs:** https://developer.android.com/training/data-storage/room
- **Navigation Compose:** https://developer.android.com/jetpack/compose/navigation
- **Vico Charts:** https://patrykandpatrick.com/vico/wiki/
- **Project Board:** https://github.com/users/omardeveloping/projects/3
