# GlucoseHelper

[![Build and Release](https://github.com/kenanjasim/GlucoseHelper/actions/workflows/android.yml/badge.svg)](https://github.com/kenanjasim/GlucoseHelper/actions/workflows/android.yml)

Android app written for my A-level coursework. Used to store and monitor data from blood glucose readings.

> **Note:** This is a school project, not a medical device. Don't rely on its insulin calculations for real treatment decisions.

## Features

- **Home** – your latest reading, your average, and a pie chart of readings above, on, or below your target
- **Logbook** – record blood glucose, carbs, insulin and notes; tap an entry to edit or delete it
- **Foods** – keep a list of foods with their carb content and portion size
- **Calculator** – work out an insulin dose from carbs eaten or from a blood glucose reading
- **Settings** – set your carb ratio, insulin sensitivity, ideal blood glucose level and name

All data is stored on the phone. There's no account or login, and the app doesn't need an internet connection.

## Download

Get the latest APK from the [Releases page](https://github.com/kenanjasim/GlucoseHelper/releases). You'll need to allow installing apps from unknown sources on your phone.

## Building

Requirements: JDK 17 and the Android SDK (platform 35). The easiest way is to open the project in Android Studio and press Run.

From the command line:

```bash
./gradlew assembleDebug
```

The APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

## Releases

GitHub Actions builds the app and runs the unit tests on every push to `main` and on every pull request. To publish a release, push a version tag:

```bash
git tag v1.1
git push origin v1.1
```

This creates a GitHub Release with the APK attached.
