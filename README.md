# Contacts

A small Android contacts app: add a person (name, phone, date of birth), see them in the list, tap one for details, or delete them. Entries are saved on the device, so they survive rotation and app restarts.

This started as a Full Sail AD2 assignment ("LyonsJustin4.2"), a Java app that showed local HTML pages in WebViews and saved one serialized file per person. Version 2.0.0 is a ground-up native rewrite of that app, keeping its behavior and fixing its bugs — see [CHANGELOG.md](CHANGELOG.md) for the full list.

## What it does

- Three screens: list, add-person form, and person details, in one activity with Navigation Compose.
- The list shows every person with name, phone, and date of birth, sorted by name. The original list showed names only, and only after tapping a TEST button.
- Adding a person updates the list immediately. Deleting asks first, removes exactly that person by their database id, and returns to the list with the rest of the people intact. The original details screen never received the selected person, showed hardcoded placeholder text, and its Delete button did nothing.
- Blank or whitespace-only fields are rejected with inline errors, input is trimmed, phone numbers must contain 7 to 15 digits, and the date of birth must be a real date in YYYY-MM-DD format and not in the future. The original saved whatever the form sent, under a filename taken from the name, so a second person with the same name overwrote the first.
- Details loads the person by id from the database instead of relying on intent extras that never arrived.
- There is still no edit screen: people are added, viewed, and deleted, matching the original's scope. The date of birth is typed as YYYY-MM-DD, the format the original HTML date input produced; there is no date-picker dialog.
- Works in portrait and landscape.

No network, no permissions, no accounts, no tracking. Everything lives in a local Room database.

## Tech stack

- Kotlin 2.2.0, Jetpack Compose Material 3, Navigation Compose
- ViewModel + StateFlow, Room for persistence
- AGP 8.13.0, Gradle 8.14.3, Java 17
- Compile/target SDK 36, min SDK 24
- Kotlin DSL with a version catalog, R8 for release builds
- GitHub Actions CI: JVM unit tests + debug build on every push to `main`

## Project structure

```text
app/src/main/java/com/enonymous1/androidcontacts/
├── MainActivity.kt            # Builds the Room database and ViewModel, sets the Compose content
├── PeopleViewModel.kt         # People state, add/delete, validation hand-off
├── model/Person.kt            # Person model, pure validation
├── data/PeopleDatabase.kt     # Room entity, DAO, database, repository
└── ui/
    ├── ContactsApp.kt         # Navigation host: list / form / details
    ├── ListScreen.kt          # Person list with empty state and add button
    ├── FormScreen.kt          # Add-person form with validation errors
    └── DetailsScreen.kt       # Person details with confirmed delete
```

## Build and test

Open the project in Android Studio and run the `app` configuration, or from a terminal:

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug
```

The instrumented tests (including the Compose UI tests) need a device or emulator:

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

## License

MIT — see [LICENSE](LICENSE).
