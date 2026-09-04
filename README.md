# New Frost Keyboard

Android IME keyboard project with a new package:

`com.newfrost.keyboard`

## Build on GitHub

1. Create a new GitHub repository.
2. Upload this project.
3. Open it in Android Studio or use GitHub Actions.
4. Build:
   `./gradlew assembleDebug`
5. APK:
   `app/build/outputs/apk/debug/app-debug.apk`

## Install

Install the APK, then enable **New Frost Keyboard** from Android Settings > Keyboard / On-screen keyboard.

## Current foundation

- Independent Android package and application.
- Vietnamese QWERTY IME foundation.
- Frost/glass-inspired translucent rounded UI.
- Rounded keyboard container and keys.
- Settings activity.
- Designed so the visual/theme and Vietnamese input engine can be expanded without depending on Gboard code.

## Important

This project is an independently implemented keyboard. It does not bundle or redistribute Gboard's proprietary code/assets.
