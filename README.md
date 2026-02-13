# Triple Power Skip (Android)

Android app that listens for a **triple press of the power button in quick succession** (best effort via an Accessibility Service) and then sends a **Next** action to active media sessions, prioritizing:

- `com.google.android.apps.youtube.music` (YouTube Music)
- `com.google.android.youtube` (YouTube)

## Important platform note

Android does not officially expose all power-key interactions to third-party apps. On many devices/OS builds, power-button events may be partially restricted. This app uses the only viable user-space route (`AccessibilityService` key filtering) and provides best-effort behavior.

## Build

1. Open the project in Android Studio (latest stable).
2. Let Gradle sync.
3. Build and install on your device.

## Enable

1. Open app.
2. Tap button to open Accessibility Settings.
3. Enable **Triple Power Skip Service**.
4. Start media in YouTube or YouTube Music.
5. Triple-press power quickly.

## Xiaomi / HyperOS note

On Xiaomi devices, disable battery optimizations for the app and allow accessibility/background operation to improve reliability.
