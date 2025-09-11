# Android Movie App

A complete Android Movie App built with modern Android development practices featuring MVVM architecture, Hilt dependency injection, Retrofit networking, and Navigation Component.

## Features

### Core Functionality
- **Browse Movies**: Popular, Top Rated, Now Playing, and Upcoming movies
- **Search Movies**: Search across all movie categories
- **Movie Details**: Detailed information with ratings, release dates, and descriptions
- **Modern UI**: Material Design with dark theme
- **Offline-Ready**: Proper error handling and loading states

### Technical Features
- **MVVM Architecture**: Clean separation of concerns
- **Hilt Dependency Injection**: Modern DI with KSP (Kotlin Symbol Processing)
- **Retrofit + OkHttp**: Complete networking layer with logging
- **Navigation Component**: Type-safe navigation with Safe Args
- **StateFlow + Coroutines**: Reactive programming with modern concurrency
- **Repository Pattern**: Clean data layer abstraction
- **Image Loading**: Glide with caching and error handling
- **Resource Wrapper**: Comprehensive state management (Loading, Success, Error)

## Architecture

```
app/
├── data/
│   ├── api/           # Retrofit API interfaces
│   ├── model/         # Data models (Movie, Genre, etc.)
│   └── repository/    # Repository implementations
├── di/                # Hilt dependency injection modules
├── ui/
│   ├── fragments/     # UI fragments (MovieList, MovieDetail)
│   ├── adapters/      # RecyclerView adapters
│   └── viewmodels/    # ViewModels for UI state management
└── utils/             # Utility classes (Resource wrapper)
```

## Setup Instructions

### 1. API Key Configuration
This app uses The Movie Database (TMDB) API. You need to:

1. Get a free API key from [TMDB](https://www.themoviedb.org/settings/api)
2. Open `MovieRepository.kt`
3. Replace `YOUR_TMDB_API_KEY` with your actual API key:

```kotlin
companion object {
    private const val API_KEY = "your_actual_api_key_here"
}
```

### 2. Build Configuration
The project uses modern Android development tools:

- **Android Gradle Plugin**: 7.4.2
- **Kotlin**: 1.8.10
- **KSP**: 1.8.10-1.0.9 (replaces kapt for better performance)
- **Hilt**: 2.44
- **Retrofit**: 2.9.0
- **Navigation**: 2.5.3

### 3. Key Dependencies
```gradle
// Hilt - Using KSP instead of kapt
implementation "com.google.dagger:hilt-android:2.44"
ksp "com.google.dagger:hilt-android-compiler:2.44"

// Network
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'

// Navigation
implementation 'androidx.navigation:navigation-fragment-ktx:2.5.3'
implementation 'androidx.navigation:navigation-ui-ktx:2.5.3'

// Image Loading - Using KSP instead of kapt
implementation 'com.github.bumptech.glide:glide:4.14.2'
ksp 'com.github.bumptech.glide:compiler:4.14.2'
```

## Migration from kapt to KSP

This project demonstrates the migration from `kapt` to `KSP` (Kotlin Symbol Processing) for better build performance:

### Before (kapt):
```gradle
plugins {
    id 'kotlin-kapt'
}

dependencies {
    kapt "com.google.dagger:hilt-android-compiler:2.44"
    kapt 'com.github.bumptech.glide:compiler:4.14.2'
}
```

### After (KSP):
```gradle
plugins {
    id 'com.google.devtools.ksp'
}

dependencies {
    ksp "com.google.dagger:hilt-android-compiler:2.44"
    ksp 'com.github.bumptech.glide:compiler:4.14.2'
}
```

### Benefits of KSP:
- **Faster Build Times**: Up to 2x faster than kapt
- **Better Memory Usage**: More efficient processing
- **Future-Proof**: Google's recommended solution for annotation processing

## App Structure

### Movie List Screen
- Tab-based navigation (Popular, Top Rated, Now Playing, Upcoming)
- Search functionality across all categories
- Grid layout with movie posters
- Loading states and error handling

### Movie Detail Screen
- Backdrop and poster images
- Movie title, release date, and rating
- Full movie description
- Proper error handling and loading states

### Navigation Flow
```
MovieListFragment → (tap movie) → MovieDetailFragment
```

## Error Handling

The app uses a `Resource` wrapper class for comprehensive state management:

```kotlin
sealed class Resource<T> {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T> : Resource<T>()
    class Error<T>(message: String) : Resource<T>(message = message)
}
```

## Building the Project

1. Clone the repository
2. Add your TMDB API key (see Setup Instructions)
3. Open in Android Studio
4. Build and run

### Build Commands
```bash
./gradlew build          # Build the project
./gradlew assembleDebug  # Build debug APK
./gradlew test          # Run unit tests
```

## Production Ready Features

- **Proper Error Handling**: Network errors, empty states, loading indicators
- **Modern Architecture**: MVVM with Repository pattern
- **Performance Optimized**: KSP instead of kapt, DiffUtil for RecyclerView
- **Type Safety**: Navigation Safe Args, sealed classes for states
- **Memory Efficient**: Proper lifecycle management, Flow for reactive streams
- **Testable**: Dependency injection makes testing easier

This app serves as a comprehensive example of modern Android development practices and can be used as a template for production applications.