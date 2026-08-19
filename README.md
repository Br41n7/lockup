# LockUp
## _An Android-based Cellebrite UFED self-defense application_

LockUp is an Android application that will monitor the device for signs for attempts to image it using known forensic tools like the Cellebrite UFED. Here is a [blog](https://blog.korelogic.com/blog/2020/06/29/cellebrite_good_times_come_on) I wrote.

- Proof-of-Concept. Not meant as an in-depth defense
- Android API 28, Does not require root
- Relies on RECEIVE_BOOT_COMPLETED to start a Service and AccessibilityService
- Monitors USB events through ACTION_USB_DEVICE, package installations, and known exploit staging locations on the filesystem
- Detects Logical Extractions, File System Extractions, and Physical Extractions leveraging ADB
- Will automatically respond with a factory reset with DeviceAdminReceiver
- Beginning steps to researching more robust anti-forensic techniques

## Signature Detection

- Exploit staging directories and known filenames
- Known file hashes
- Application names and certificate metadata

## TODO Signatures

- Binary-level identifiers
- Hardcoded RSA keys used for ADB authentication (requires root)

## Installation

I avoided including everything needed to build LockUp, making this application so accessible that it may be easily used to avoid criminal prosecution was not my goal. Instead, my goal was to help support my research into forensic tools in showing how they aren't immune to software issues. 

## Author

Matt Bergin, [KoreLogic](https://www.korelogic.com/)

## History

Most recently I [presented](https://www.blackhat.com/asia-21/briefings/schedule/index.html#anti-forensics-reverse-engineering-a-leading-phone-forensic-tool-21789) my research at Blackhat Asia 2021.

I've released security advisories for the Cellebrite UFED which you may also be interested in:

- [KL-001-2020-003: Cellebrite EPR Decryption Relies on Hardcoded AES Key Material](https://korelogic.com/Resources/Advisories/KL-001-2020-003.txt)
- [KL-001-2020-002: Cellebrite Restricted Desktop Escape and Escalation of User Privilege](https://korelogic.com/Resources/Advisories/KL-001-2020-002.txt)
- [KL-001-2020-001: Cellebrite Hardcoded ADB Authentication Keys](https://korelogic.com/Resources/Advisories/KL-001-2020-001.txt)

## Audit & Bug Fix Summary

The codebase was audited, refactored, and updated with several key fixes and enhancements:

### 1. Duress / Lockup Password Matching (`LockUpPlausibleService.java` & `preferences.xml`)
- **Bug Fixed**: Previously, `LockUpPlausibleService` used a cumulative character-by-character counter (`pointsHave`) that persisted across events and keystrokes without resetting. This caused false positives (accidental wipes) when typing normal text on the lock screen.
- **Fix Applied**: Replaced the accumulator loop with a clean, stateless string parser. The entered text is sanitized (trimming whitespace and removing list string wrappers `[text]`) and directly compared against the configured duress password (`deniabilityPw`) and its prefix.
- **Password Setup**: Enabled `deniabilityPw` (`EditTextPreference`) in `preferences.xml` and `SettingsActivity.java` so users can easily set up their custom lockup password in Settings.

### 2. Device Wipe Switch Fallthrough (`Defense.java`)
- **Bug Fixed**: In `Defense.java`, the `switch (response)` block lacked `break` statements. Choosing "Lock" fell through directly into `wipeData(0)`, causing any defensive action to perform a full factory reset.
- **Fix Applied**: Added `break;` statements to each case to ensure "Lock" and "Factory Reset" operate independently.
- **Refactoring**: Removed `extends AppCompatActivity` from `Defense.java`, converting it into a clean helper class that uses `Context`.

### 3. Restricted USB Monitoring & Launch-Time Wipe Prevention (`LockUpService.java`)
- **USB Monitoring**: Updated `mUsbAttachReceiver` so forensic staging directory monitoring (`monitor_staging_dir()`) ONLY starts when an external USB device or accessory is connected (`ACTION_USB_DEVICE_ATTACHED` or `ACTION_USB_ACCESSORY_ATTACHED`). Added proper `break` statements to prevent false triggers on `DETACHED` or `default` cases.
- **First-Launch Wipes**: Updated `mAppInstallReceiver` to inspect only newly installed packages (`ACTION_PACKAGE_ADDED`), preventing the app from scanning all pre-existing installed applications and wiping the phone on first launch.

### 4. Settings Preferences UI Fixes (`preferences.xml` & `SettingsActivity.java`)
- **Missing Preferences**: Added missing UI elements (`desiredResponse` ListPreference and `notifyUser` SwitchPreference) to `preferences.xml`.
- **Initialization**: Fixed `SettingsActivity.java` to set `"initialized"` to `true` after initial setup.

### 5. Build Environment & Modern Android Compatibility
- **Gradle Configuration**: Added `settings.gradle`, `build.gradle`, `gradle.properties`, and `app/build.gradle` targeting Android SDK 34 with Java 8 compatibility (supporting Android Studio and AIDE).
- **AndroidX Migration**: Migrated all deprecated Support Library v7 imports and layout elements (e.g. `ConstraintLayout`) to `androidx`.
- **Android 12+ Compliance**: Added explicit `android:exported` attributes to components with intent filters in `AndroidManifest.xml`.

### 6. Automated Testing
- Added `PlausibleServiceTest.java` JVM unit test suite to verify text sanitization, password matching, and USB attachment filter actions.

## License

[Creative Commons Zero 1.0](https://github.com/mbkore/lockup/blob/main/LICENSE)


