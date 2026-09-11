# PradaCalendar

Screen recording: https://www.youtube.com/watch?v=pGtKCOC01aI

## Current status

The event database, event/weather models, and event-store layer have been converted to Kotlin. The conversion is available for review in [PR #1](https://github.com/bearprada/PradaCalendar/pull/1).

Verification completed:

- `./gradlew build --no-daemon --stacktrace`
- `./gradlew testDebugUnitTest --no-daemon`
- `git diff --check`

Instrumentation tests still require a connected Android device or emulator.

## Build

The minimum supported Android version is Android 6.0 (API 23). Set `ANDROID_HOME` to your SDK directory, or set `sdk.dir` in `local.properties`.

Use JDK 17 or 21 and Android SDK Platform 35 with Build Tools 35.0.0.

```sh
./gradlew build
```

The project uses Gradle 8.11.1 and Android Gradle Plugin 8.9.2. APKs are written to `app/build/outputs/apk/`.

The app retains target SDK 24 to preserve its existing runtime behavior. This produces an expired-target-SDK lint warning; publishing to Google Play requires a separate target SDK migration.

## Import test data

Use the import mock data button on the toolbar.

![Import mock data](images/import_mock_data.jpg)

## Instrumentation tests and coverage

Run instrumentation tests with a connected Android phone or emulator:

```sh
./gradlew connectedCheck
```

Reports are written to `app/build/reports/androidTests`. Generate the debug coverage report with:

```sh
./gradlew createDebugCoverageReport
```

Coverage reports are written to `app/build/reports/coverage/`.

![Code coverage report](images/code_coverage_report.png)
