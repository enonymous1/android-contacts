# Changelog

> Public export note: this repository is a squashed single-commit export of the 2.0.0 modernization below. The original private history is not included.

## 2.0.0 — 2026-10-28

Native rewrite of the original LyonsJustin4.2 assignment, on the user's rewrite pick.

- Rewritten in Kotlin with Jetpack Compose Material 3 and Navigation Compose; the WebViews, local HTML pages, JavaScript bridge, XML layouts, fragments, and three activities are gone. One activity hosts list, form, and details destinations.
- Data is persisted with Room (`contacts.db`). The original wrote one Java-serialized `Person` file per name into the app files directory, so a second person with the same name overwrote the first, and reading the list deserialized every file in the directory, crashing on any non-person file.
- Delete is by database id, behind a confirmation dialog, and returns to the list. The original details screen never received the selected name (it was passed as activity options instead of intent extras), its `getPerson()` returned null, it showed hardcoded EXAMPLE / 555-555-5555 / 00/00/0000 text, and its Delete button had no handler.
- The list loads automatically and is sorted by name. The original only populated when a TEST button was tapped, and appended a new list on every tap, duplicating entries. The unused stylesheet was never linked by any page.
- Validation is real: blank and whitespace-only fields are rejected with inline errors, input is trimmed, phone numbers must contain 7 to 15 digits, and the date of birth must be a real date in YYYY-MM-DD format and not in the future. The original saved unvalidated form data under an unsanitized filename and finished the hosting activity after every save.
- Form drafts survive rotation. The unused storage permissions from the manifest are gone; the app needs no permissions and makes no network calls.
- No edit screen was added; the scope matches the original (add, view, delete). The date of birth is typed as YYYY-MM-DD, the format the original HTML date input produced; no date-picker dialog was added.
- Toolchain: AGP 8.13.0, Gradle 8.14.3, Kotlin 2.2.0, Java 17, compile/target SDK 36, min SDK 24, Kotlin DSL with a version catalog, R8 for release builds.
- Package and application ID changed from `com.example.lyonsjustin42` to `com.enonymous1.androidcontacts`; the app is now called Contacts. The project was renamed from `contacts` to `android-contacts` before publication (project name and package only; the app display name and the `contacts.db` database file are unchanged).
- Added GitHub Actions CI (unit tests + debug build), an MIT LICENSE, and this changelog.
- Removed from tracking: the committed 4.6 MB release APK and the IDE configuration files. They were build output and editor state, not source. The stock launcher icon from the original is kept.
- Tests: 13 validation unit tests, 4 ViewModel tests with a fake repository (including deleting one of two same-named people), and 7 instrumented tests (package name plus 6 Compose UI tests covering the empty state, add flow, validation errors, invalid phone/date, details, and delete — the UI tests need a device or emulator and are not run in CI).

## 1.0.0

Original LyonsJustin4.2 assignment: Java, AppCompat, XML layouts and fragments, local HTML pages in WebViews with a JavaScript bridge, one serialized file per person. The details screen was unfinished, as described above.
