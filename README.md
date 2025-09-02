# 📚 Social Learning App with Quizzes, Tasks, Chat & Ads  

**"Learn, Connect, and Grow – All in one app!"**  

---

## 🚀 Overview  
The **Social Learning App** is an Android application designed to combine learning, productivity, and social interaction into one cohesive platform.  
It provides **quiz modules, task management, real-time chat, onboarding with ads, and user profiles** – all powered by **Firebase** and built with **Jetpack Compose + MVVM architecture**.  

---

## ✨ Features  
- 🔐 **Authentication & Onboarding**  
  - Firebase Authentication (Sign up / Login / Logout).  
  - Onboarding flow with Welcome, Features, and Privacy screens.  
  - AdMob Banner & Interstitial Ads during onboarding and login flow.  

- 📝 **Quizzes**  
  - Multiple-choice quizzes with a timer.  
  - Automatic question navigation.  
  - Real-time score tracking and history stored in Firebase.  

- ✅ **Task Management**  
  - Add, edit, delete personal tasks.  
  - Mark tasks as completed or pending.  
  - Priority and filter options.  
  - Firebase Realtime Database sync for persistent storage.  

- 💬 **Real-Time Chat**  
  - Group chat feature for all users.  
  - Real-time message updates using Firebase.  
  - Sender/receiver identification and timestamps.  

- 👤 **User Profile**  
  - Personalized profile section.  
  - Display name, email, and progress.  
  - Editable user details synced with Firebase.  

- 📢 **Ads Integration**  
  - Google AdMob Banner Ads.  
  - Google AdMob Interstitial Ads on onboarding/login.  

---

## 🛠️ Tech Stack  
- **Language:** Kotlin  
- **UI:** Jetpack Compose  
- **Architecture:** MVVM (Model–View–ViewModel)  
- **Database:** Firebase Realtime Database  
- **Authentication:** Firebase Authentication  
- **Messaging:** Firebase Cloud Messaging (Optional for notifications)  
- **Ads:** Google AdMob (Banner + Interstitial)  
- **IDE:** Android Studio  

---

## 📂 Project Structure  
app/
├── data/ # Firebase repositories & data models
├── ui/ # Jetpack Compose screens (Quiz, Tasks, Chat, Profile)
├── viewmodel/ # MVVM ViewModels for state management
├── navigation/ # Navigation graph for Compose
└── MainActivity.kt # App entry point

---
Made by Kotlin and compose magic by Laiba https://github.com/Laiba-Usman
