# Cash Sense Project

Cash Sense is a native Android mobile application written in Kotlin. It's a mobile app designed to
help users manage their finances effectively. Users can create wallets in different currencies, set
up categories for transactions, or track subscription payment dates.

## Architecture

This project is a modern Android application that follows the official architecture guidance from
Google. It is a reactive, single-activity app that uses the following:

- **UI:** Built entirely with Jetpack Compose, including Material 3 components and adaptive layouts
  for different screen sizes.
- **State Management:** Unidirectional Data Flow (UDF) is implemented using Kotlin Coroutines and
  `Flow`s. `ViewModel`s act as state holders, exposing UI state as streams of data.
- **Dependency Injection:** Hilt is used for dependency injection throughout the app, simplifying
  the management of dependencies and improving testability.
- **Navigation:** Navigation is handled by Jetpack Navigation 3 for Compose, allowing for a
  declarative and type-safe way to navigate between screens.
- **Data:** The data layer is implemented using the repository pattern.
    - **Local Data:** Room and DataStore are used for local data persistence.
    - **Remote Data:** Ktor and OkHttp are used for fetching data from the network.
- **Background Processing:** WorkManager is used for deferrable background tasks.

## Modules

The main Android app lives in the `app/` folder. Feature modules live in `feature/` and core and
shared modules in `core/`.

## Localization

All `strings.xml` files with translations are located in the `core/locales/` folder.

## Version control and code location

- The project uses git and is hosted in https://github.com/nikbulavin/cashsense.
