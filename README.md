# 📱 Online Quiz Management System - Native Android App (Java)

[![Android](https://img.shields.io/badge/Android-APK%20Release-3DDC84.svg?logo=android&logoColor=white)](OnlineQuizSystem.apk)
[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

An enterprise-grade, **100% Pure Native Android Application (`.apk`)** for mobile examinations and assessments, developed in Java with zero HTML web dependencies. Featuring a secure initial authentication gateway, fullscreen anti-cheat exam runner with countdown timers, student exam catalogs, instant scoring & detailed question reviews, an in-app administrator control studio with live user pass/fail records, and full implementation of **Course Outcomes CO-1 through CO-4**.

---

## 📲 Download the Native Android App (`.apk`)

| Resource | Link / Access | Description |
| :--- | :--- | :--- |
| 📱 **Direct APK Download** | [📥 **Download OnlineQuizSystem.apk**](OnlineQuizSystem.apk) | Standalone release-signed Android APK ready to install |
| 🏷️ **GitHub Latest Release** | [🔗 **GitHub Releases (v1.0.2)**](https://github.com/vinaynayak2007/Online-Quiz-Management-System/releases/tag/v1.0.2) | Official release page with release notes & binaries |
| 🌐 **GitHub Repository** | [🌐 **vinaynayak2007/Online-Quiz-Management-System**](https://github.com/vinaynayak2007/Online-Quiz-Management-System) | Full source tree and native Android client |
| 🎓 **Course Outcomes Report** | [📄 **COURSE_OUTCOMES_REPORT.md**](COURSE_OUTCOMES_REPORT.md) | Academic technical report detailing CO-1, CO-2, CO-3, and CO-4 |

---

## 🌟 Native Android Application Architecture (Zero HTML / Pure Android)

### 1. 🔒 Authentication Gateway (`LoginActivity`)
- **Initial View**: Clean, dark-themed login & registration screen. Neither the student exam panel nor the admin studio is visible prior to authentication.
- **Segmented Tabs**:
  - **🔑 Sign In Tab**: Input Phone Number (or Username) and Password.
  - **📝 Student Registration Tab**: Input **Full Name** + **Phone Number** (10 digits) + **Password**.
- **Quick-Fill Buttons**:
  - `🛡️ Quick-Fill Admin`: Pre-fills `Admin` & `Admin123`.
  - `👤 Quick-Fill Student`: Pre-fills demo candidate credentials (`9876543210` / `student123`).

### 2. 🎓 Student Examination Arena (`ExamListActivity`)
- Shows personalized welcome header: `"Welcome, Candidate (📞 Phone)"`.
- Displays available **Examination Tracks**:
  1. **Java 21 & Object-Oriented Programming** (10 Mins | 4 Questions | Passing Score: 70%)
  2. **Data Structures & Algorithms** (15 Mins | 4 Questions | Passing Score: 60%)
  3. **Google Firebase & Cloud Architecture** (12 Mins | 4 Questions | Passing Score: 75%)
  4. **Modern Full-Stack Web Development** (10 Mins | 4 Questions | Passing Score: 65%)
- Displays candidate's previous attempt scores and **PASSED ✅** or **FAILED ❌** badges directly on each card.
- Single-tap **"🚀 Start Exam (Full Screen Mode)"** button.

### 3. 🖥️ Strict Fullscreen Anti-Cheat Exam Runner (`ExamActivity`)
- **Immersive Fullscreen Kiosk Mode**: Activates `SYSTEM_UI_FLAG_IMMERSIVE_STICKY`, hiding notification bar and soft navigation keys.
- **Screen Awake Lock**: Keeps device screen awake throughout the exam (`FLAG_KEEP_SCREEN_ON`).
- **Anti-Cheat & Tab Switch Interceptor**:
  - `onPause()` detects when candidate leaves the exam to switch apps or pull down notification panels.
  - Triggers an urgent violation dialog: `"⚠️ EXAM INTEGRITY VIOLATION: Leaving the exam or switching apps is monitored! Violation Count: #X."`
- **Native Live Timer**: Real-time ticking `CountDownTimer` with color transition (turns urgent red during final 60 seconds) and auto-submission on timeout.
- **Back Button Protection**: Intercepts hardware back button with a confirmation dialog to prevent accidental session termination.

### 4. 📊 Instant Scorecard & Question Breakdown (`ResultActivity`)
- Displays animated icon, large **PASSED ✅** (emerald green) or **FAILED ❌** (red) badge.
- Summary metrics: Score earned, percentage, passing score threshold, total points.
- Question-by-question review showing candidate's choice, designated right answer, and detailed teacher explanations.
- "Back to Examination Catalog" button returns smoothly to the exam list.

### 5. 🛡️ In-App Admin Management Studio (`AdminActivity`)
- Accessible strictly via username **`Admin`** and password **`Admin123`**.
- **Executive Metric Cards ("show how many use pass")**:
  - 👥 **TOTAL USERS**: Count of registered candidates.
  - 📝 **TOTAL ATTEMPTS**: Total examination sittings.
  - 🏆 **USERS PASSED**: Big green counter showing count of passed attempts.
  - ⚠️ **USERS FAILED**: Big red counter showing count of failed attempts.
  - 📈 **PASS RATE %**: Dynamic overall pass percentage.
- **Exam Submissions & Pass Records Table**:
  - Candidate Name, Phone Number, Exam Title, Score %, and **PASSED ✅** / **FAILED ❌** badges with submission timestamps.
- **Registered Users List**:
  - Full Name, Phone, Registered Date, Exams Taken, Exams Passed, and Status.
- **Question & Right Answer Studio**:
  - View all questions by exam track.
  - **1-Click Right Answer Switcher**: Tap any option row on mobile to instantly designate it as the correct answer with immediate green highlight and database sync!

---

## 🎓 Course Outcomes (CO) Addressed in this Micro-Project

This project strictly adheres to academic syllabus objectives through dedicated Java implementations:

| Course Outcome | Description | Technical Implementation |
| :--- | :--- | :--- |
| **CO-1** | **Classes, Objects & Inheritance** | • `User` base class (`com.quizsystem.model.User`).<br>• Inheritance demonstrated via `Candidate extends User` (with exam metrics, pass percentage, student ID) and `AdminUser extends User` (with super-admin credentials and permission controls).<br>• Object models for `Question`, `Quiz`, `StudentAnswer`, and `QuizResult`. |
| **CO-2** | **Interfaces, Polymorphism & Package Organization** | • `QuizEvaluator` interface (`com.quizsystem.evaluator`) with runtime polymorphic strategies: `StandardQuizEvaluator` (standard grading) and `StrictPenaltyQuizEvaluator` (competitive penalty scoring).<br>• `ReportGenerator` interface (`com.quizsystem.report`) with polymorphic implementations: `PerformanceSummaryReportGenerator` (class statistics) and `CertificateReportGenerator` (accreditation rosters).<br>• Organized into specialized packages: `model`, `evaluator`, `report`, `exception`, `concurrency`, `service`, `controller`. |
| **CO-3** | **Exception Handling, Timer Thread & Synchronization** | • Custom exception hierarchy (`com.quizsystem.exception`): `InvalidInputException`, `DatabaseException`, `QuizExpiredException`, and `DuplicateSubmissionException`, handled centrally via `@RestControllerAdvice` (`GlobalExceptionHandler`).<br>• **Separate Timer Thread**: `QuizTimerManager` (`com.quizsystem.concurrency`) runs a background daemon thread ticking active sessions to automatically enforce quiz expiry.<br>• **Synchronization**: `SubmissionSyncManager` uses `synchronized` blocks to atomically block duplicate or concurrent submissions. |
| **CO-4** | **Collections (`ArrayList`) & Java Streams** | • `ArrayList<Question>`, `ArrayList<StudentAnswer>`, `ArrayList<Attempt>`, and `ArrayList<Candidate>` used throughout the business logic.<br>• Java Stream API operations (`.filter()`, `.mapToDouble()`, `.average()`, `.collect(groupingBy())`, `.collect(partitioningBy())`, `.sorted()`) compute class averages, top performers, and pass/fail summaries. |

📄 *For full code listings and mapping, see [`COURSE_OUTCOMES_REPORT.md`](COURSE_OUTCOMES_REPORT.md).*

---

## 📱 Quick Links & Downloads

| Resource | Link / Access | Description |
| :--- | :--- | :--- |
| **Android App (APK)** | [📥 **Download OnlineQuizSystem.apk**](OnlineQuizSystem.apk) | Ready-to-install Android package for student mobile exams |
| **GitHub APK Direct Link** | [🔗 **Raw GitHub APK Download**](https://github.com/vinaynayak2007/Online-Quiz-Management-System/raw/main/OnlineQuizSystem.apk) | Direct APK download link from the GitHub repository |
| **GitHub Repository** | [🌐 **vinaynayak2007/Online-Quiz-Management-System**](https://github.com/vinaynayak2007/Online-Quiz-Management-System) | Full source code, Android client, and backend |
| **Web Student Portal** | [🖥️ `http://localhost:8080/`](http://localhost:8080/) | Interactive exam runner, countdown timers, scorecards |
| **Question Studio (Admin)** | [⚙️ `http://localhost:8080/admin.html`](http://localhost:8080/admin.html) | Standalone visual question & right-answer control suite |

---

## 📲 Android Mobile Exam Application (`OnlineQuizSystem.apk`)

The included Android application (`OnlineQuizSystem.apk`, signed and release-ready) allows students to take exams directly on their Android devices with 100% offline capability or online syncing.

### 🌟 Android App Features
- **Standalone Offline Exam Engine**: Pre-bundled with comprehensive question banks across Java 21, Data Structures & Algorithms, Google Firebase Cloud Architecture, and Modern Web Development.
- **Real-Time Timed Exams**: Interactive ticking timer with automatic warning states and instant auto-submit on countdown expiry.
- **Question Flagging & Navigator**: Jump effortlessly between questions and review marked items before final submission.
- **Immediate Evaluation & Scorecard**: Instant grading with percentage, letter grade (A+, A, B, C, F), and question-by-question breakdown showing correct choices and detailed teacher explanations.
- **Native Android Hardware Acceleration**: Smooth, high-performance animations, fluid touch scrolling, and zero lag on mobile screens.

### 📥 How to Install the APK on Android
1. **Download the APK**:
   - Direct download via browser: [`OnlineQuizSystem.apk`](https://github.com/vinaynayak2007/Online-Quiz-Management-System/raw/main/OnlineQuizSystem.apk)
   - Or when running the server locally: `http://localhost:8080/OnlineQuizSystem.apk`
2. **Allow Installation from Unknown Sources**:
   - On Android 8.0+: When prompted after downloading, tap **Settings** and toggle **"Allow from this source"**.
   - On older Android versions: Go to **Settings > Security > Unknown Sources** and enable it.
3. **Install & Launch**:
   - Tap `OnlineQuizSystem.apk` in your phone's notification panel or Downloads folder.
   - Tap **Install** and then **Open**.
   - Start taking your quizzes immediately!

---

## 📸 Application Screenshot Previews

### 1. Student Quiz Runner & Dashboard (`index.html`)
![Online Quiz Management System Dashboard](quiz_dashboard_preview.png)

### 2. Dedicated Single-Page Question & Right Answer Studio (`admin.html`)
![Admin Panel Question & Right Answer Management](admin_panel_preview.png)

### 3. Interactive Question Editor & Real-Time Options Builder Modal
![Question & Right Answer Editor Modal](admin_edit_modal_preview.png)

### 4. Instant 1-Click Right Answer Toggle with Live Feedback
![Instant Right Answer Toggle](admin_updated_answer_preview.png)

---

## 🌟 Highlights & Key Features

### 🛠️ Dedicated Question & Right Answer Studio (`admin.html`)
- **Single-Page Administrative Suite**: Fully responsive control center accessible at `http://localhost:8080/admin.html` or `/admin`.
- **Live Visual Right Answer Highlighting**: Clear emerald green borders, radiant badges, and indicator pills distinguishing the designated correct answer from distractors.
- **Instant 1-Click Right Answer Switcher**: Admin can change the designated right answer directly on any question card with a single click, instantly syncing via `PATCH /api/quizzes/{quizId}/questions/{questionId}/right-answer`.
- **Comprehensive Question Modal**: Full-featured creator/editor supporting:
  - Dynamic option builders (add/remove options, rename choices).
  - Radio buttons for single-choice and checkboxes for multiple-choice questions.
  - Question statement, points weight, and teacher explanations.
  - Live side-by-side student preview box before committing changes.
- **Quiz Hierarchy Selector**: Interactive sidebar to toggle between different assessment tracks (Java, Algorithms, Firebase Cloud, Web Dev) or generate new quizzes.
- **Search & Filter Engine**: Instant search across question prompts, option strings, and explanations, plus filtering by single choice, multiple choice, and true/false.

### 🎓 Student Quiz Portal (`index.html`)
- **Interactive Quiz Player**: Clean, distraction-free interface with custom options selectors and question navigation.
- **Real-Time Countdown Timer**: Live ticking timer with visual and auditory warnings when under 60 seconds.
- **Question Navigator & Flagging**: Jump between questions, mark questions for review, and track answered vs unanswered status.
- **Instant Evaluation Engine**: Automatically grades single-choice, multiple-choice, and true/false questions upon submission.
- **Question-by-Question Review**: Displays candidate's choice vs correct answers with in-depth educational explanations.
- **Printable Certificate of Achievement**: Generates a high-resolution, printable completion certificate with verification ID.
- **Hall of Fame & Leaderboard**: Real-time ranking with gold, silver, and bronze badges based on score and completion time.
- **Sound Effects**: Native Web Audio API sound feedback for clicks, timer alerts, and passing/failing chimes.

### 🔥 Google Firebase Integration
- **Project Configuration**: Integrated with Google Firebase project `micro-project-96796` (`firebase-adminsdk-fbsvc@micro-project-96796.iam.gserviceaccount.com`).
- **Cloud Firestore Storage**: Connects directly to Google Cloud Firestore via the official Firebase Admin SDK (`com.google.firebase:firebase-admin:9.2.0`).
- **Hybrid Auto-Fallback Emulator**: If Firestore API is pending activation on GCP, the system runs seamlessly in local emulated mode (file-backed JSON in `data/`), ensuring zero-friction setup and zero data loss.
- **Runtime Firebase Configuration**: Switch to live Firebase at any time through the UI by pasting your service account private key JSON.
- **Android Client Module**: Includes a native Android Java client architecture in `android-client/` with Firestore integration helpers.

---

## 🏛️ Architecture & Tech Stack

```
Online-Quiz-Management-System/
├── src/main/java/com/quizsystem/
│   ├── QuizApplication.java             # Spring Boot Main Entrypoint
│   ├── config/
│   │   ├── FirebaseConfig.java          # Firebase Admin SDK & Credentials Initializer
│   │   └── WebConfig.java               # CORS & Static Resource Routing
│   ├── model/
│   │   ├── Quiz.java                    # Quiz Entity
│   │   ├── Question.java                # Question Model (MCQ, True/False, Multi-select)
│   │   ├── Attempt.java                 # Student Submission & Results Record
│   │   ├── QuizSubmission.java          # Request Payload
│   │   ├── QuizResult.java              # Evaluated Scorecard with Explanations
│   │   └── FirebaseStatus.java          # Connection State Model
│   ├── service/
│   │   ├── FirebaseService.java         # Firestore Cloud & Emulated Storage Service
│   │   ├── QuizService.java             # Quiz CRUD & Public Sanitization Logic
│   │   └── AttemptService.java          # Grading Engine & Leaderboard Computations
│   └── controller/
│       ├── QuizController.java          # /api/quizzes REST Endpoints
│       ├── AttemptController.java       # /api/attempts & submission evaluations
│       ├── LeaderboardController.java   # /api/leaderboard REST Endpoints
│       ├── AnalyticsController.java     # /api/analytics Admin Metrics
│       └── FirebaseController.java      # /api/firebase configuration & status
├── src/main/resources/
│   ├── application.properties           # Spring Boot & Port 8080 Configuration
│   ├── initial-quizzes.json             # Seed Quizzes & Question Banks
│   └── static/                          # Modern Single-Page Web Frontend
│       ├── index.html                   # Responsive Dashboard & Quiz Arena
│       ├── css/styles.css               # Modern Custom Theme & Print Certificate Styling
│       └── js/app.js                    # Web Audio, Timer Engine, State & API Client
├── android-client/                      # Android Java + Firebase Client Reference
│   ├── build.gradle
│   └── src/main/java/com/quizsystem/android/
└── pom.xml                              # Maven Configuration & Dependencies
```

---

## 🚀 Getting Started

### Prerequisites
- **Java 17 or Java 21** installed (`java -version`)
- **Maven 3.8+** (or use the included `./mvnw` wrapper)

### Running the Application

1. **Clone the repository:**
   ```bash
   git clone https://github.com/vinaynayak2007/Online-Quiz-Management-System.git
   cd Online-Quiz-Management-System
   ```

2. **Build and Run with Maven Wrapper:**
   ```bash
   ./mvnw spring-boot:run
   ```
   *On Windows:*
   ```cmd
   mvnw.cmd spring-boot:run
   ```

3. **Access the Application:**
   Open your browser and navigate to:
   ```
   http://localhost:8080
   ```

---

## 🔥 Connecting to Google Firebase

By default, the application runs in **Local Emulated Mode** with preloaded sample quizzes and persistent JSON storage. To connect to your live Firebase project:

1. Go to the [Firebase Console](https://console.firebase.google.com/).
2. Create or open your project, then navigate to **Project Settings** > **Service accounts**.
3. Click **Generate new private key** and download `serviceAccountKey.json`.
4. Place `serviceAccountKey.json` into the root directory of the project, or set the environment variable:
   ```bash
   export GOOGLE_APPLICATION_CREDENTIALS=/path/to/serviceAccountKey.json
   ```
5. *Alternatively*, open the running app at `http://localhost:8080`, click the **Firebase Ready** badge in the top navigation, and paste your JSON private key directly into the configuration modal!

---

## 📡 REST API Documentation

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/quizzes` | Get list of public active quizzes (answers sanitized for security) |
| `GET` | `/api/quizzes/{id}` | Get specific quiz questions for student taking mode |
| `POST` | `/api/quizzes/{id}/submit` | Submit answers, calculate score, and return detailed scorecard |
| `GET` | `/api/quizzes/{id}/admin` | Get complete quiz including correct answers and explanations |
| `POST` | `/api/quizzes` | Create a new quiz and questions bank (Admin) |
| `PUT` | `/api/quizzes/{id}` | Update existing quiz metadata (Admin) |
| `DELETE` | `/api/quizzes/{id}` | Delete quiz (Admin) |
| `GET` | `/api/quizzes/{id}/questions` | Get all questions with answers for a quiz (Admin Studio) |
| `GET` | `/api/quizzes/{id}/questions/{qId}` | Get single question details with options and answers |
| `POST` | `/api/quizzes/{id}/questions` | Add a new question to a quiz |
| `PUT` | `/api/quizzes/{id}/questions/{qId}` | Update question text, options, right answers, points, and explanation |
| `PATCH` | `/api/quizzes/{id}/questions/{qId}/right-answer` | Instant 1-click update of designated correct answer index |
| `DELETE` | `/api/quizzes/{id}/questions/{qId}` | Delete question from quiz |
| `GET` | `/api/leaderboard` | Get ranked leaderboard entries |
| `GET` | `/api/analytics` | Get aggregate system statistics |
| `GET` | `/api/firebase/status` | Check live Firebase Firestore connection status |
| `POST` | `/api/firebase/configure` | Dynamically apply Firebase service account credentials |

---

## 🧪 Running Automated Tests

Run the included JUnit 5 integration tests to verify context initialization, evaluation engines, and security sanitization:

```bash
./mvnw test
```

---

## 📱 Android Client Reference

A ready-to-import native Android Java client is included in the `android-client/` directory. It demonstrates:
- Connecting to Firebase Firestore directly from mobile
- Fetching quizzes and questions dynamically
- Managing countdown timers on Android
- Submitting quiz attempt results to Firestore

---

## 👤 Author & Maintainer

- **Developer**: Vinay N ([@vinaynayak2007](https://github.com/vinaynayak2007))
- **Project**: Online Quiz Management System
- **Repository**: [vinaynayak2007/Online-Quiz-Management-System](https://github.com/vinaynayak2007/Online-Quiz-Management-System)
- **GitHub Project Board**: [Online Quiz Management System Project #7](https://github.com/users/vinaynayak2007/projects/7)

---

## 📄 License
This project is licensed under the MIT License.
