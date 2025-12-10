# Copilot Instructions for MVVM-app

## Repository Overview

**Project Name**: MVVM_app  
**Type**: Android mobile application  
**Architecture**: MVVM (Model-View-ViewModel) pattern starter project  
**Primary Language**: Kotlin  
**Package**: com.eyepax.mvvm_app  
**Repository Size**: Small (~30 files)

This is an Android application with a splash screen that transitions to a main activity after 4 seconds. The app serves as a foundation for MVVM architecture development.

## Technology Stack

- **Language**: Kotlin 2.0.21
- **Build System**: Gradle 8.13 with Kotlin DSL (.kts files)
- **Android Gradle Plugin (AGP)**: 8.13.1 (⚠️ See Critical Build Issue below)
- **Java Version**: JDK 17 (Temurin distribution)
- **Compile SDK**: 36 (⚠️ See Critical Build Issue below)
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36
- **Dependencies**: AndroidX Core KTX, AppCompat, Material Design, ConstraintLayout
- **Testing**: JUnit 4.13.2, AndroidX Test (JUnit 1.3.0, Espresso 3.7.0)

## Critical Build Issues & Required Fixes

⚠️ **IMPORTANT**: The repository has configuration errors that prevent builds from succeeding:

### Issue 1: Invalid AGP Version
- **Problem**: `gradle/libs.versions.toml` specifies AGP version `8.13.1` which does not exist
- **Fix**: Change to a valid version like `8.3.0` or `8.5.0`
- **File**: `gradle/libs.versions.toml`, line 2
- **Change**: `agp = "8.13.1"` → `agp = "8.3.0"`

### Issue 2: Invalid compileSdk Syntax  
- **Problem**: `app/build.gradle.kts` has incorrect syntax `compileSdk { version = release(36) }`
- **Fix**: Use simple assignment `compileSdk = 35`
- **File**: `app/build.gradle.kts`, lines 8-10
- **Change**: 
  ```kotlin
  // BEFORE (incorrect):
  compileSdk {
      version = release(36)
  }
  
  // AFTER (correct):
  compileSdk = 35
  ```

### Issue 3: SDK Version Mismatch
- **Problem**: SDK 36 may not be available; SDK 35 is more stable
- **Fix**: Update targetSdk to 35
- **File**: `app/build.gradle.kts`, line 15
- **Change**: `targetSdk = 36` → `targetSdk = 35`

**These fixes must be applied before any build commands will succeed.**

## Build & Development Commands

### Prerequisites
1. **ALWAYS** run `chmod +x gradlew` before first Gradle command if gradlew is not executable
2. Ensure Java 17 is installed and JAVA_HOME is set
3. Android SDK must be available (or build will fail in environments without it)

### Build Commands (in order)

**Clean the project** (removes build artifacts):
```bash
./gradlew clean
```
- **Duration**: ~10-15 seconds after fixes
- **When to use**: Before fresh builds, after configuration changes

**Build debug APK**:
```bash
./gradlew assembleDebug --no-daemon --stacktrace
```
- **Duration**: 60-120 seconds (first build can take longer)
- **Output**: `app/build/outputs/apk/debug/*.apk`
- **Note**: Use `--no-daemon` in CI environments to avoid daemon issues
- **Note**: Use `--stacktrace` for detailed error information

**Build release APK**:
```bash
./gradlew assembleRelease --no-daemon
```
- **Output**: `app/build/outputs/apk/release/*.apk`

**Run unit tests**:
```bash
./gradlew test --no-daemon
```
- **Duration**: 30-60 seconds
- **Test file**: `app/src/test/java/com/eyepax/mvvm_app/ExampleUnitTest.kt`
- **Current tests**: 1 unit test (addition_isCorrect)

**Run lint checks**:
```bash
./gradlew lint --no-daemon
```
- **Duration**: 45-90 seconds
- **Output**: HTML report in `app/build/reports/lint-results.html`
- **Note**: Lint may complete with warnings; this is acceptable

**List all available tasks**:
```bash
./gradlew tasks
```

### Important Build Notes

- **ALWAYS** apply the fixes above before attempting any build
- First build downloads dependencies and takes significantly longer (2-5 minutes)
- Subsequent builds are faster due to caching
- If build fails with "Plugin not found", check AGP version in `gradle/libs.versions.toml`
- If build fails with syntax errors, verify `compileSdk` syntax in `app/build.gradle.kts`

## Project Structure

### Root Directory Files
```
/
├── .github/
│   └── workflows/
│       └── mobile-ci.yml          # GitHub Actions CI workflow
├── .idea/                          # Android Studio configuration
├── app/                            # Main application module
├── gradle/                         # Gradle wrapper files
│   ├── libs.versions.toml         # ⚠️ Version catalog (contains AGP version error)
│   └── wrapper/
│       └── gradle-wrapper.properties  # Gradle 8.13 distribution
├── build.gradle.kts               # Root build configuration
├── settings.gradle.kts            # Project settings
├── gradle.properties              # Gradle JVM settings (-Xmx2048m)
├── gradlew                        # Gradle wrapper script (Unix)
├── gradlew.bat                    # Gradle wrapper script (Windows)
├── Jenkinsfile                    # Jenkins CI pipeline configuration
└── .gitignore                     # Git ignore patterns
```

### App Module Structure  
```
app/
├── build.gradle.kts               # ⚠️ App-level build config (contains compileSdk error)
├── proguard-rules.pro            # ProGuard configuration (default/empty)
└── src/
    ├── main/
    │   ├── AndroidManifest.xml   # App manifest (launcher: Splash_Activity)
    │   ├── java/com/eyepax/mvvm_app/
    │   │   ├── MainActivity.kt   # Main activity with edge-to-edge display
    │   │   └── Splash_Activity.kt  # Splash screen (4-second delay)
    │   └── res/
    │       ├── layout/
    │       │   ├── activity_main.xml
    │       │   └── activity_splash.xml
    │       ├── values/
    │       │   ├── colors.xml
    │       │   ├── strings.xml
    │       │   └── themes.xml
    │       ├── drawable/         # App icons and images
    │       └── mipmap-*/         # Launcher icons (various densities)
    ├── test/
    │   └── java/com/eyepax/mvvm_app/
    │       └── ExampleUnitTest.kt
    └── androidTest/
        └── java/com/eyepax/mvvm_app/
            └── ExampleInstrumentedTest.kt
```

## Source Code Details

### Activities

**Splash_Activity.kt** (Main launcher):
- Entry point of the application (MAIN/LAUNCHER intent filter)
- Uses Handler with 4-second delay before launching MainActivity
- Implements edge-to-edge display
- Layout: `R.layout.activity_splash`

**MainActivity.kt**:
- Main application activity (not exported)
- Implements edge-to-edge display with system bar insets
- Layout: `R.layout.activity_main`

### Naming Convention
⚠️ **Note**: `Splash_Activity` uses underscore naming (non-standard). Typical Kotlin convention is `SplashActivity` (camelCase).

## Continuous Integration

### GitHub Actions (`.github/workflows/mobile-ci.yml`)

**Triggers**: Push/PR to `main` or `development` branches

**Steps**:
1. Checkout repository
2. Setup JDK 17 (Temurin distribution)
3. Make gradlew executable (`chmod +x gradlew`)
4. Build debug APK (`./gradlew assembleDebug --no-daemon --stacktrace`)
5. Upload APK as artifact

**⚠️ CI will fail** until build configuration issues are fixed.

### Jenkins Pipeline (`Jenkinsfile`)

**Environment**: 
- `ANDROID_HOME=/opt/android-sdk`
- Requires `local.properties` with SDK path

**Stages**:
1. **Checkout**: Clone repository
2. **Configure Android SDK**: Create `local.properties` with `sdk.dir`
3. **Build Debug APK**: `./gradlew clean assembleDebug --no-daemon --stacktrace`
4. **Run Unit Tests**: `./gradlew test --no-daemon` (failures logged but don't fail build)
5. **Lint Check**: `./gradlew lint --no-daemon` (warnings logged but don't fail build)
6. **Archive APK**: Save to Jenkins artifacts

**Note**: Tests and lint issues are tolerated (logged but don't fail pipeline).

## Dependencies

**Version Catalog** (`gradle/libs.versions.toml`):
- androidx.core:core-ktx:1.17.0
- androidx.appcompat:appcompat:1.7.1
- com.google.android.material:material:1.13.0
- androidx.activity:activity:1.11.0
- androidx.constraintlayout:constraintlayout:2.2.1
- junit:junit:4.13.2
- androidx.test.ext:junit:1.3.0
- androidx.test.espresso:espresso-core:3.7.0

**Repositories**:
- Google Maven Repository
- Maven Central
- Gradle Plugin Portal (for plugins)

## Configuration Files

### gradle.properties
- JVM args: `-Xmx2048m -Dfile.encoding=UTF-8`
- AndroidX enabled: `android.useAndroidX=true`
- Kotlin code style: `official`
- Non-transitive R class: `android.nonTransitiveRClass=true`

### .gitignore
Ignores:
- `*.iml`, `.gradle/`, `/build`, `/captures`
- `/local.properties` (Android SDK path)
- `.idea/` (workspace, caches, libraries, modules)
- `.DS_Store`, `.externalNativeBuild`, `.cxx`

### settings.gradle.kts
- Root project name: `MVVM_app`
- Includes: `:app` module
- Repository mode: `FAIL_ON_PROJECT_REPOS` (prevents project-level repository declarations)

## Development Workflow

### Making Code Changes

1. **Fix build issues first** (apply fixes documented above)
2. Make your code changes
3. **Build locally**: `./gradlew assembleDebug --no-daemon`
4. **Run tests**: `./gradlew test --no-daemon`
5. **Run lint**: `./gradlew lint --no-daemon`
6. Commit changes
7. Push to trigger CI

### Adding Dependencies

1. Edit `gradle/libs.versions.toml`:
   - Add version in `[versions]` section
   - Add library in `[libraries]` section
2. Reference in `app/build.gradle.kts`:
   ```kotlin
   implementation(libs.your.library)
   ```
3. Sync Gradle: `./gradlew --refresh-dependencies`

### Modifying Layouts

- Main layouts: `app/src/main/res/layout/`
- Strings: `app/src/main/res/values/strings.xml`
- Colors: `app/src/main/res/values/colors.xml`
- Themes: `app/src/main/res/values/themes.xml` (also night variant)

### Adding New Activities

1. Create Kotlin file in `app/src/main/java/com/eyepax/mvvm_app/`
2. Create layout XML in `app/src/main/res/layout/`
3. Register in `AndroidManifest.xml`:
   ```xml
   <activity
       android:name=".YourActivity"
       android:exported="false">
   </activity>
   ```

## Testing

### Unit Tests
- Location: `app/src/test/java/com/eyepax/mvvm_app/`
- Framework: JUnit 4
- Run: `./gradlew test`
- Current: 1 test (ExampleUnitTest.addition_isCorrect)

### Instrumented Tests
- Location: `app/src/androidTest/java/com/eyepax/mvvm_app/`
- Framework: AndroidX Test, Espresso
- Require: Android device/emulator
- Current: 1 test (ExampleInstrumentedTest.useAppContext)

## Troubleshooting

### "Plugin not found" Error
- **Cause**: Invalid AGP version in `gradle/libs.versions.toml`
- **Fix**: Change `agp = "8.13.1"` to `agp = "8.3.0"`

### "Could not find method compileSdk" Error  
- **Cause**: Invalid syntax in `app/build.gradle.kts`
- **Fix**: Change `compileSdk { version = release(36) }` to `compileSdk = 35`

### "Permission denied: ./gradlew" Error
- **Fix**: Run `chmod +x gradlew` before Gradle commands

### "SDK location not found" Error
- **Fix**: Create `local.properties` with `sdk.dir=/path/to/android/sdk`
- **Note**: This file is git-ignored and machine-specific

### Build Timeout
- **Cause**: First build downloads many dependencies
- **Fix**: Increase timeout to 5-10 minutes for first build
- **Fix**: Use `--no-daemon` in CI environments

## Best Practices

1. **ALWAYS apply configuration fixes before building**
2. **ALWAYS** run `chmod +x gradlew` if permission denied
3. **Use `--no-daemon`** in CI/CD to avoid daemon issues
4. **Use `--stacktrace`** when debugging build failures
5. **Run lint before committing**: `./gradlew lint`
6. **Test on multiple SDK versions** if changing minSdk/targetSdk
7. **Follow Kotlin coding conventions**: camelCase for class names
8. **Keep versions synchronized** between `targetSdk` and `compileSdk`
9. **Trust these instructions**: Only search/explore if information here is incomplete or incorrect

## Quick Reference

| Task | Command | Duration |
|------|---------|----------|
| Clean | `./gradlew clean` | ~10-15s |
| Build Debug | `./gradlew assembleDebug --no-daemon` | ~60-120s |
| Run Tests | `./gradlew test --no-daemon` | ~30-60s |
| Run Lint | `./gradlew lint --no-daemon` | ~45-90s |
| List Tasks | `./gradlew tasks` | ~5-10s |

**Remember**: Configuration fixes must be applied before any command will succeed!
