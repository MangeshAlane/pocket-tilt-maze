# Pocket Tilt Maze

A tilt-controlled maze game for Android with daily challenges and quick play modes.

## Features

- **Daily Maze**: A new procedurally generated maze every day with best time tracking
- **Quick Maze**: Generate random mazes on demand with configurable difficulty
- **Tilt Controls**: Use your device's accelerometer to navigate the ball through the maze
- **Customizable Settings**: Adjust sensitivity, maze size, and vibration feedback
- **Stats Tracking**: View your completion history and best times
- **Ad-Supported**: Free to play with banner and interstitial ads

## Technical Details

- **Platform**: Android (Kotlin, Native)
- **Min SDK**: 23 (Android 6.0)
- **Architecture**: MVVM with Navigation Component
- **Key Libraries**:
  - Google Mobile Ads SDK for monetization
  - AndroidX Navigation for fragment management
  - Material Design Components for UI

## Building the Project

1. Open the project in Android Studio
2. Sync Gradle files
3. Run on an emulator or physical device with accelerometer support

```bash
./gradlew assembleDebug
```

## Running Tests

```bash
./gradlew test
```

## Configuration

### AdMob Setup

The project uses test ad unit IDs by default. To use real ads:

1. Create an AdMob account
2. Register your app and get ad unit IDs
3. Replace the test IDs in `app/src/main/res/values/strings.xml`:
   - `admob_app_id`
   - `admob_banner_ad_unit_id`
   - `admob_interstitial_ad_unit_id`
4. Update the App ID in `AndroidManifest.xml`

## Game Mechanics

- **Maze Generation**: Uses recursive backtracking algorithm for perfect mazes
- **Daily Maze**: Deterministic generation based on date (same maze for all players on the same day)
- **Physics**: Simple velocity-based ball movement with friction and collision detection
- **Win Condition**: Ball reaches the goal area (bottom-right corner)

## License

This project is provided as-is for educational and commercial use.
