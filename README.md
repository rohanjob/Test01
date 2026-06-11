# Varaha Nest

Varaha Nest is a comprehensive real estate application built for Android. It allows users to browse, buy, rent, and lease residential and commercial properties. The application features a modern UI built with Jetpack Compose, robust local caching with Room, and a scalable backend powered by Supabase.

## 🚀 Features

- **Property Listings**: Browse residential (apartments, houses), commercial (offices, shops), and PG/rental properties.
- **Detailed Property Views**: View high-quality images, amenities, pricing, and precise locations on Google Maps.
- **User Authentication**: Secure sign-up and login utilizing Supabase Auth.
- **Favorites & Leads**: Save favorite properties and directly contact owners/agents.
- **Real-time Notifications**: Stay updated with Firebase Cloud Messaging (FCM).
- **Support Hub**: Dedicated support center for issue resolution.

## 🛠 Tech Stack & Architecture

This project is built using modern Android development practices and libraries:

### Frontend
- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Toolkit**: [Jetpack Compose](https://developer.android.com/jetpack/compose) - Declarative UI framework
- **Architecture**: Clean Architecture with MVVM presentation patterns
- **Dependency Injection**: [Dagger Hilt](https://dagger.dev/hilt/)
- **Navigation**: [Jetpack Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- **Maps**: [Google Maps for Compose](https://github.com/googlemaps/android-maps-compose)
- **Concurrency**: [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & Flows
- **Network Client**: [Ktor](https://ktor.io/) - Asynchronous HTTP client

### Backend
- **Platform**: [Supabase](https://supabase.com/) (Auth, Storage, Realtime)
- **Services**: [Firebase](https://firebase.google.com/) (Cloud Messaging, Analytics, Crashlytics)

### Database
- **Remote Database**: PostgreSQL (via Supabase PostgREST)
- **Local Database**: [Room](https://developer.android.com/training/data-storage/room) - SQLite object mapping library for offline caching

## 📁 Project Structure

The codebase is organized into layers following Clean Architecture principles:

- `data`: Contains local database definitions (Room), remote API services (Supabase), and repository implementations.
- `domain`: Houses the core business logic, domain models, and Use Cases.
- `presentation`: Contains the UI components (Jetpack Compose screens) and ViewModels.
- `di`: Dependency injection modules utilizing Dagger Hilt.
- `theme`: Compose theming (Colors, Typography, Shapes).

## 🚀 Getting Started

### Prerequisites
- [Android Studio](https://developer.android.com/studio) (Latest version recommended)
- JDK 17 or higher

### Setup Instructions
1. Clone the repository:
   ```bash
   git clone https://github.com/rohanjob/Test01.git
   ```
2. Open the project in Android Studio.
3. Sync the project with Gradle files.
4. **Configuration**: 
   - Ensure you have the `google-services.json` file in your `app/` directory for Firebase services to function correctly.
   - Configure your Supabase URL and Anon Key in your environment or build configuration.
5. Build and run the application on an emulator or physical device.

## 📝 Database Schema
The SQL schema for the Supabase backend is documented in `supabase_schema.sql` located at the root of the project.