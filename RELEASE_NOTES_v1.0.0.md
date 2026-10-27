# 🚀 Online Quiz Management System - Release v1.0.0

Welcome to the official **v1.0.0** release of the **Online Quiz & Exam Management System**!

This release includes the complete full-stack enterprise web portal (Java 21 + Spring Boot 3 + Google Firebase Firestore), the standalone visual Question Studio (`admin.html`), and the official signed **Android Exam APK** (`OnlineQuizSystem.apk`) for mobile assessments.

---

## 📦 Release Assets & Downloads

| Artifact | File | Description |
| :--- | :--- | :--- |
| 📱 **Android App (APK)** | `OnlineQuizSystem.apk` | Signed, standalone Android mobile application for student quizzes & exams |
| ☕ **Spring Boot Executable JAR** | `online-quiz-management-system-1.0.0.jar` | Standalone executable Spring Boot 3 server running on port 8080 |
| 🛠️ **Visual Question Studio** | `admin.html` | 100% offline-ready single-page admin panel for question & right-answer editing |
| 💻 **Source Code** | `Source code (zip / tar.gz)` | Complete source tree including Android client & Maven backend |

---

## 🌟 Highlights & Features in v1.0.0

### 📱 Android Mobile Exam App (`OnlineQuizSystem.apk`)
- **Ready to Install**: Fully signed with Android `apksigner` using RSA 2048-bit keys (v1 + v2 APK signature scheme).
- **Offline Exam Engine**: Pre-loaded with complete question banks across Java 21, Data Structures & Algorithms, Google Firebase Cloud Architecture, and Web Development.
- **Timed Exam Runner**: Live ticking countdown timer with warning chimes and auto-submission upon timeout.
- **Interactive Question Navigator**: Flag questions for review, jump between questions, and track unanswered questions.
- **Instant Mobile Scorecards**: Instant percentage, letter grade (A+, A, B, C, F), and question-by-question review showing candidate answers vs correct answers with detailed explanations.

### ⚙️ Dedicated Single-Page Question Studio (`admin.html`)
- **Visual Right-Answer Management**: 1-click designated correct answer switcher with live green glow indicators.
- **Interactive Question Editor**: Modal for modifying questions, point values, explanations, and dynamic option builders.
- **100% Standalone & Offline**: Runs directly from disk (`file:///admin.html`) via `localStorage` with JSON export/import when the server is not running.
- **REST & Firebase Sync**: Automatically syncs with Spring Boot and Google Firebase Firestore when online.

### ☕ Enterprise Java 21 & Spring Boot 3 Backend
- **Firebase Firestore Integration**: Native connection to Google Firebase project `micro-project-96796` via Firebase Admin SDK.
- **Zero-Friction Emulated Fallback**: Seamless fallback to local JSON storage if Firestore permissions are pending on GCP.
- **Comprehensive REST APIs**: Endpoints for quizzes (`/api/quizzes`), question updates (`PATCH /right-answer`), student submissions (`/api/attempts`), and analytics (`/api/analytics`).
- **Student Dashboard (`index.html`)**: Interactive web arena, Web Audio sound effects, hall of fame leaderboards, and printable verified certificates of achievement.

---

## 📲 How to Install `OnlineQuizSystem.apk` on Android

1. Download `OnlineQuizSystem.apk` from the release assets below.
2. In Android Settings, ensure **"Install unknown apps"** is enabled for your browser or file manager.
3. Tap the downloaded APK to install and launch the exam app!
