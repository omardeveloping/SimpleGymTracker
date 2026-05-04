---
version: alpha
name: SimpleGymTracker
description: SimpleGymTracker is a friendly, simple, and cute gym workout tracker. The design inherits Notion's warm minimalism and soft surfaces, re-energized with an Electric Blue accent that signals action, progress, and vitality. The interface prioritizes clarity during workouts — large touch targets, high-contrast set rows, and calming pastels for rest states. Light mode is the default; a dark mode is available for low-light gym environments.

colors:
  # Primary — Electric Blue (Action, Progress, Vitality)
  primary: "#3B82F6"
  primary-pressed: "#2563EB"
  primary-deep: "#1D4ED8"
  on-primary: "#ffffff"

  # Surfaces
  canvas: "#ffffff"
  surface: "#f6f5f4"
  surface-soft: "#fafaf9"
  surface-dark: "#1a1a1a"
  surface-dark-soft: "#262626"

  # Borders
  hairline: "#e5e3df"
  hairline-soft: "#ede9e4"
  hairline-strong: "#c8c4be"
  hairline-dark: "#333333"

  # Text
  ink-deep: "#000000"
  ink: "#1a1a1a"
  charcoal: "#37352f"
  slate: "#5d5b54"
  steel: "#787671"
  stone: "#a4a097"
  muted: "#bbb8b1"
  on-dark: "#ffffff"
  on-dark-muted: "#a4a097"

  # Card Tints (Workout States)
  card-tint-mint: "#d9f3e1"
  card-tint-sky: "#dcecfa"
  card-tint-lavender: "#e6e0f5"
  card-tint-peach: "#ffe8d4"
  card-tint-rose: "#fde0ec"
  card-tint-yellow: "#fef7d6"
  card-tint-cream: "#f8f5e8"
  card-tint-gray: "#f0eeec"

  # Semantic
  semantic-success: "#1aae39"
  semantic-warning: "#dd5b00"
  semantic-error: "#e03131"

  # Gym-Specific
  rest-timer-bg: "#f0f9ff"
  rest-timer-accent: "#3B82F6"
  workout-active: "#3B82F6"
  set-complete: "#1aae39"
  set-pending: "#bbb8b1"

rounded:
  xs: 4px
  sm: 6px
  md: 8px
  lg: 12px
  xl: 16px
  xxl: 20px
  full: 9999px

spacing:
  xxs: 4px
  xs: 8px
  sm: 12px
  md: 16px
  lg: 20px
  xl: 24px
  xxl: 32px
  xxxl: 40px

components:
  button-primary:
    backgroundColor: "{colors.primary}"
    textColor: "{colors.on-primary}"
    rounded: "{rounded.md}"
    padding: "12px 20px"
    fontWeight: 500

  button-secondary:
    backgroundColor: "transparent"
    textColor: "{colors.charcoal}"
    rounded: "{rounded.md}"
    padding: "12px 20px"
    border: "1px solid {colors.hairline-strong}"
    fontWeight: 500

  button-ghost:
    backgroundColor: "transparent"
    textColor: "{colors.charcoal}"
    rounded: "{rounded.sm}"
    padding: "8px 12px"
    fontWeight: 500

  card-base:
    backgroundColor: "{colors.canvas}"
    rounded: "{rounded.lg}"
    padding: "{spacing.md}"
    border: "1px solid {colors.hairline}"

  card-feature:
    backgroundColor: "{colors.surface}"
    rounded: "{rounded.lg}"
    padding: "{spacing.lg}"

  card-exercise:
    backgroundColor: "{colors.canvas}"
    rounded: "{rounded.lg}"
    padding: "{spacing.md}"
    border: "1px solid {colors.hairline}"

  card-set-row:
    backgroundColor: "{colors.surface-soft}"
    rounded: "{rounded.md}"
    padding: "{spacing.sm} {spacing.md}"

  text-input:
    backgroundColor: "{colors.canvas}"
    textColor: "{colors.ink}"
    rounded: "{rounded.md}"
    padding: "{spacing.sm} {spacing.md}"
    border: "1px solid {colors.hairline-strong}"
    height: 48px

  text-input-focused:
    border: "2px solid {colors.primary}"

  pill-tab:
    backgroundColor: "transparent"
    textColor: "{colors.steel}"
    rounded: "{rounded.full}"
    padding: "{spacing.xs} {spacing.md}"
    border: "1px solid {colors.hairline}"

  pill-tab-active:
    backgroundColor: "{colors.ink-deep}"
    textColor: "{colors.on-dark}"

  badge-primary:
    backgroundColor: "{colors.primary}"
    textColor: "{colors.on-primary}"
    rounded: "{rounded.full}"
    padding: "4px 10px"

  badge-tag:
    backgroundColor: "{colors.card-tint-sky}"
    textColor: "{colors.primary-deep}"
    rounded: "{rounded.sm}"
    padding: "2px 8px"

  rest-timer:
    backgroundColor: "{colors.rest-timer-bg}"
    textColor: "{colors.rest-timer-accent}"
    rounded: "{rounded.xl}"
    padding: "{spacing.xl}"
    border: "2px solid {colors.rest-timer-accent}"

typography:
  display:
    fontSize: 40px
    fontWeight: 600
    lineHeight: 1.15
  heading-1:
    fontSize: 32px
    fontWeight: 600
    lineHeight: 1.20
  heading-2:
    fontSize: 24px
    fontWeight: 600
    lineHeight: 1.25
  heading-3:
    fontSize: 20px
    fontWeight: 600
    lineHeight: 1.30
  body:
    fontSize: 16px
    fontWeight: 400
    lineHeight: 1.55
  body-medium:
    fontSize: 16px
    fontWeight: 500
    lineHeight: 1.55
  body-small:
    fontSize: 14px
    fontWeight: 400
    lineHeight: 1.50
  caption:
    fontSize: 12px
    fontWeight: 500
    lineHeight: 1.40

principles:
  - Light mode default; dark mode supported for low-light gym environments
  - Large touch targets (48px minimum) for use during workouts with sweaty hands
  - High-contrast set rows with clear completion states
  - Electric Blue as the action color — start workout, add set, timer active
  - Pastel card tints for exercise categories and rest states
  - Mint green for success/completed states
  - Calm, warm surfaces to reduce cognitive load during workouts
  - No clutter — one primary action per screen

screens:
  - name: Home
    description: Dashboard with "Start Workout" CTA, recent sessions shortcut, and quick stats
    primaryAction: Start Workout

  - name: ExerciseSelector
    description: Searchable list of exercises. Predefined + user-created. Floating "Create Custom" button.
    primaryAction: Select Exercise

  - name: ActiveWorkout
    description: Live session. Exercise cards with expandable set rows. Weight/reps inputs. Timer trigger.
    primaryAction: Add Set

  - name: SessionHistory
    description: Chronological list of past sessions. Tap to view details. Swipe to copy.
    primaryAction: Copy Session

  - name: ProgressChart
    description: Vico scatter plot with daily aggregation. Date range selector. Exercise filter.
    primaryAction: Filter

  - name: TimerConfig
    description: Rest timer countdown. Configure rest times between sets and exercises.
    primaryAction: Start Timer
