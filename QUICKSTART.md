# Pocket Tilt Maze - Quick Start Guide

## What's Been Built

A complete Android tilt-controlled maze game with:
- ✅ Daily Maze mode (new maze every day)
- ✅ Quick Maze mode (random mazes on demand)
- ✅ Accelerometer tilt controls
- ✅ Settings (vibration, maze size, sensitivity)
- ✅ Stats tracking (best times, completion history)
- ✅ AdMob integration (banner + interstitial ads)

## How to Build & Run

### Option 1: Android Studio (Recommended)
1. Open Android Studio
2. File → Open → Select `c:\Users\Mahavir\Downloads\pocket tilt`
3. Wait for Gradle sync to complete
4. Click Run ▶️ button
5. Select a device/emulator with accelerometer support

### Option 2: Command Line
```bash
cd "c:\Users\Mahavir\Downloads\pocket tilt"
gradlew.bat assembleDebug
```
APK will be in: `app\build\outputs\apk\debug\app-debug.apk`

## Testing the Game

1. **Home Screen**: Tap "Play Daily Maze" or "Play Quick Maze"
2. **Tilt Controls**: Tilt your device to move the ball
3. **Goal**: Navigate to the green goal area (bottom-right)
4. **Settings**: Adjust sensitivity if ball moves too fast/slow
5. **Stats**: View your completion history

## Important Notes

⚠️ **Accelerometer Required**: Must test on a physical device or emulator with virtual sensors enabled

⚠️ **Test Ads**: Currently using AdMob test IDs. Replace with real IDs before publishing:
- Edit: `app\src\main\res\values\strings.xml`
- Update: `admob_app_id`, `admob_banner_ad_unit_id`, `admob_interstitial_ad_unit_id`

## Project Files

- **10 Kotlin files**: Core game logic, UI, data management
- **12 XML layouts**: All screens and navigation
- **Unit tests**: Maze generation verification
- **Complete Gradle setup**: All dependencies configured

## Next Steps

1. ✅ Build and test on device
2. 📱 Create app icon
3. 🎯 Replace AdMob test IDs with real ones
4. 🔐 Set up signing for release
5. 🚀 Publish to Google Play Store

See [walkthrough.md](file:///C:/Users/Mahavir/.gemini/antigravity/brain/0a27f043-6aa6-4e65-b57f-ed4cd71582e7/walkthrough.md) for complete implementation details.
