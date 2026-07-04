# Summery App 
a modern, performance oriented Android application build with **Jetpack Compose** that follows 
the MVVM architecture. 
Summery strives to deliver a smooth and aesthetically pleasing Juice browsing, caching and secure authentication.

--- 

## Key Features 
* **Type-safe navigation:** taking advantage of Compose's Navigation type safe architecture for screen routing.
* **Smart UI Architecture:** re-usable custom UI elements such as a pagination indicator, Material 3 bottom sheets, custom text input fields and more.
* **Secure token caching:** Custom client-side encrypted session management using an ```EncryptedTokenManager```
* **Optimized Network Layer:** caching mechanisms with Retrofit to minimize network bandwidth usage

--- 

## Tech Stack & Architecture

The project is built using the **MVVM (Model-View-ViewModel)** architecture pattern keeping the codebase modular and more maintainable.

| Component | Technology used | Brief description | 
| :--- | :--- | :--- |
| **UI Layer** | Jetpack Compose | a declarative framework with custom and re-usable elements.
| **Networking** | Retrofit & OkHttp | REST API client implementation with interceptor based token injection. |
| **Security** | AndroidX Security | Jetpack Security EncryptedSharedPreferences for safe token storage. |

---

## Get started 

follow these steps to get the app running on your device : 
### Prerequisites 
* Android Studio Meerkat / 2024.3.2 version or newer 
* Android SDK 34+
* JDK 17 or higher 

### Installation 
1. Clone the repository:
```git clone [https://github.com/oxygenekiller82-max/Summery-kotlin.git] (https://github.com/oxygenekiller82-max/Summery-kotlin.git)```
2. Open the project in Android Studio 
3. Let Gradle sync 
4. Run the app (Emulator or connect a physical device via wifi or using a cable)
