# 🎓 Course Outcomes (CO) Technical Implementation Report
## Micro-Project: Online Quiz & Examination Management System

---

## 📌 Executive Overview of Course Outcomes

| Course Outcome | Academic Definition | Implementation Status | Primary Java Classes & Packages |
| :--- | :--- | :---: | :--- |
| **CO-1** | Use Java classes and objects to represent candidates, questions, quizzes, answers, and results. Demonstrate inheritance through user/account classes. | **100% IMPLEMENTED** | `com.quizsystem.model` (`User`, `Candidate`, `AdminUser`, `Question`, `Quiz`, `StudentAnswer`, `QuizResult`, `Attempt`) |
| **CO-2** | Use interfaces and polymorphism to define common operations for quiz submission, evaluation, or report generation. Organize classes into Java packages. | **100% IMPLEMENTED** | `com.quizsystem.evaluator` (`QuizEvaluator`, `StandardQuizEvaluator`, `StrictPenaltyQuizEvaluator`), `com.quizsystem.report` (`ReportGenerator`, `PerformanceSummaryReportGenerator`, `CertificateReportGenerator`) |
| **CO-3** | Use exception handling for invalid input and database errors; use a separate timer thread and synchronization to manage quiz expiry and prevent duplicate submissions. | **100% IMPLEMENTED** | `com.quizsystem.exception` (`GlobalExceptionHandler`, `InvalidInputException`, `DatabaseException`, `QuizExpiredException`, `DuplicateSubmissionException`), `com.quizsystem.concurrency` (`QuizTimerManager`, `SubmissionSyncManager`) |
| **CO-4** | Use collections such as `ArrayList` to store questions and candidate responses, and Java stream operations to process scores and generate performance summaries. | **100% IMPLEMENTED** | `com.quizsystem.service` (`AttemptService`, `UserService`), `com.quizsystem.report` (`PerformanceSummaryReportGenerator`) |

---

## 🔍 Detailed Technical Verification by Course Outcome

```
       ========================================================================
       MICRO-PROJECT ARCHITECTURE MAPPING TO COURSE OUTCOMES
       ========================================================================

       [CO-1: Inheritance Hierarchy]
                  ┌──────────────────────┐
                  │      class User      │ (Base Account Model)
                  └──────────┬───────────┘
                             │ extends
             ┌───────────────┴───────────────┐
             ▼                               ▼
    ┌─────────────────┐             ┌─────────────────┐
    │ class Candidate │             │ class AdminUser │
    │ (Student Exams) │             │ (Staff & Quizzes│
    └─────────────────┘             └─────────────────┘

       [CO-2: Interfaces & Polymorphism]
             «interface»                           «interface»
           QuizEvaluator                         ReportGenerator
                 ▲                                     ▲
        ┌────────┴────────┐                   ┌────────┴────────┐
        │                 │                   │                 │
┌──────────────┐   ┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│StandardQuiz  │   │StrictPenalty │   │Performance   │   │Certificate   │
│Evaluator     │   │Evaluator     │   │SummaryReport │   │Report        │
└──────────────┘   └──────────────┘   └──────────────┘   └──────────────┘

       [CO-3: Concurrency & Exception Handling]
    ┌───────────────────────────┐      ┌───────────────────────────┐
    │    QuizTimerManager       │      │   SubmissionSyncManager   │
    │ (Separate Daemon Thread)  │      │ (synchronized lock permit)│
    │  • Ticks active sessions  │      │  • Prevents duplicate     │
    │  • Enforces auto-expiry   │      │    candidate submissions  │
    └───────────────────────────┘      └───────────────────────────┘

       [CO-4: Collections & Java Stream API]
    • ArrayList<Question>, ArrayList<StudentAnswer>, ArrayList<Attempt>
    • .filter(Attempt::isPassed)
    • .mapToDouble(Attempt::getPercentage).average()
    • .collect(Collectors.groupingBy(...)) & partitioningBy(...)
       ========================================================================
```

---

### 1️⃣ CO-1: Java Classes, Objects & Inheritance

#### A. Base Class and Inheritance Hierarchy
- **Base Class**: `com.quizsystem.model.User`
  - Encapsulates shared attributes across all system users: `id`, `name`, `phone`, `password`, `role`, and `registeredAt`.
  - Defines polymorphic baseline behavior:
    ```java
    public boolean isAdministrator() { return "ADMIN".equalsIgnoreCase(this.role); }
    public String getDisplayName() { return this.name + " (" + this.phone + ")"; }
    ```
- **Derived Class 1 (Inheritance)**: `com.quizsystem.model.Candidate extends User`
  - Encapsulates student-specific exam metrics:
    - Fields: `studentId`, `examsTaken`, `examsPassed`, `targetCategory`.
    - Methods: `recordExamResult(boolean passed)`, `getPassPercentage()`, `getPerformanceStatus()`.
    - Overrides `getDisplayName()`:
      ```java
      @Override
      public String getDisplayName() {
          return "[Student] " + this.name + " (ID: " + this.studentId + ", Phone: " + this.phone + ")";
      }
      ```
- **Derived Class 2 (Inheritance)**: `com.quizsystem.model.AdminUser extends User`
  - Encapsulates administrative privileges:
    - Fields: `adminLevel` (e.g. `SUPER_ADMIN`), `managedDepartments`.
    - Overrides `isAdministrator()` to return `true` and `canEditQuestions()` to return `true`.

#### B. Domain Representation Classes & Objects
- `Question`: Represents single-choice, multiple-choice, or true/false test questions with point values and educational explanations.
- `Quiz`: Represents an assessment track with metadata, duration, category, and an `ArrayList<Question>`.
- `StudentAnswer`: Stores candidate answer selections for a specific question ID.
- `QuizSubmission`: Data Transfer Object aggregating candidate ID, elapsed time, and list of answers.
- `QuizResult`: Evaluated scorecard recording points earned, percentage, grade, pass status, and question evaluations.
- `Attempt`: Historical record of an exam sitting persisted to cloud database and local JSON.

---

### 2️⃣ CO-2: Interfaces, Polymorphism & Package Organization

#### A. Java Package Structure
All source code is cleanly segregated into specialized Java packages:
- `com.quizsystem` — Spring Boot entry point (`QuizApplication.java`).
- `com.quizsystem.model` — Domain entities (`User`, `Candidate`, `AdminUser`, `Quiz`, `Question`, `Attempt`).
- `com.quizsystem.evaluator` — Interfaces and polymorphic evaluation strategies.
- `com.quizsystem.report` — Interfaces and polymorphic reporting generators.
- `com.quizsystem.exception` — Custom exception hierarchy and centralized REST advice.
- `com.quizsystem.concurrency` — Background timer thread and synchronization managers.
- `com.quizsystem.service` — Business logic (`AttemptService`, `QuizService`, `UserService`, `FirebaseService`).
- `com.quizsystem.controller` — REST controllers (`AttemptController`, `QuizController`, `UserController`, `AnalyticsController`).
- `com.quizsystem.config` — Firebase Admin SDK and CORS web configurations.

#### B. Interfaces & Polymorphic Evaluation (`QuizEvaluator`)
```java
package com.quizsystem.evaluator;

public interface QuizEvaluator {
    QuizResult evaluate(Quiz quiz, QuizSubmission submission);
    String getStrategyName();
}
```
- **Implementation 1**: `StandardQuizEvaluator implements QuizEvaluator`
  - Linear evaluation with proportional point accumulation and standard grade scale (`A+`, `A`, `B`, `C`, `D`, `F`).
- **Implementation 2**: `StrictPenaltyQuizEvaluator implements QuizEvaluator`
  - Competitive exam evaluation with negative marking penalties (-2 points per incorrect answer).

#### C. Interfaces & Polymorphic Reporting (`ReportGenerator`)
```java
package com.quizsystem.report;

public interface ReportGenerator {
    String generateReport(List<Attempt> attempts);
    String getReportFormat();
}
```
- **Implementation 1**: `PerformanceSummaryReportGenerator implements ReportGenerator`
  - Computes class-wide performance metrics, pass/fail ratios, and track popularity using Java Streams.
- **Implementation 2**: `CertificateReportGenerator implements ReportGenerator`
  - Formats verification rosters and accreditation certificates for qualifying candidates.

---

### 3️⃣ CO-3: Exception Handling, Timer Thread & Synchronization

#### A. Exception Handling Hierarchy
```
java.lang.Throwable
 └── java.lang.Exception
      └── java.lang.RuntimeException
           └── QuizSystemException (Base)
                ├── InvalidInputException (HTTP 400 Bad Request)
                ├── DatabaseException (HTTP 500 Internal Server Error)
                ├── QuizExpiredException (HTTP 410 Gone)
                └── DuplicateSubmissionException (HTTP 409 Conflict)
```
- **`GlobalExceptionHandler` (`@RestControllerAdvice`)**:
  Interprets domain exceptions and issues structured JSON error payloads containing timestamps, HTTP status codes, error classifications, and descriptive messages.

#### B. Separate Timer Thread (`QuizTimerManager`)
- Implements multi-threading with a dedicated background daemon thread:
  ```java
  timerThreadPool = Executors.newSingleThreadScheduledExecutor(r -> {
      Thread t = new Thread(r, "Quiz-Timer-Thread");
      t.setDaemon(true);
      return t;
  });
  timerThreadPool.scheduleAtFixedRate(this::checkSessionExpirations, 1, 1, TimeUnit.SECONDS);
  ```
- **Automatic Expiry Enforcement**:
  Every second, the background timer thread inspects active sessions. If the candidate's allowed duration has elapsed, the session is atomically marked expired, and any subsequent submission attempt triggers `QuizExpiredException`.

#### C. Synchronization & Duplicate Submission Prevention (`SubmissionSyncManager`)
- Employs **`synchronized` locking** to protect the submission pipeline:
  ```java
  public void acquireSubmissionPermit(String submissionKey) {
      synchronized (syncLock) {
          if (processedSubmissions.contains(submissionKey)) {
              throw new DuplicateSubmissionException("Duplicate submission detected! This exam has already been processed.");
          }
          if (inFlightSubmissions.contains(submissionKey)) {
              throw new DuplicateSubmissionException("An identical submission is currently being evaluated.");
          }
          inFlightSubmissions.add(submissionKey);
      }
  }
  ```
- Guaranteed thread safety against network retries or rapid double-clicking of the "Submit" button.

---

### 4️⃣ CO-4: Collections (`ArrayList`) & Java Stream Operations

#### A. Collection Usage (`ArrayList`)
- Questions in a Quiz: `List<Question> questions = new ArrayList<>();`
- Candidate Answer Selections: `List<StudentAnswer> answers = new ArrayList<>();`
- Evaluation Results: `List<QuestionEvaluation> evaluations = new ArrayList<>();`
- Candidate Rosters: `List<Candidate> candidates = new ArrayList<>();`

#### B. Java Stream Operations in `AttemptService` and `PerformanceSummaryReportGenerator`
1. **Filtering & Counting (`filter`)**:
   ```java
   long passedCount = attempts.stream()
       .filter(Attempt::isPassed)
       .count();
   ```
2. **Numerical Mapping & Aggregation (`mapToDouble`, `average`)**:
   ```java
   double averageScore = attempts.stream()
       .mapToDouble(Attempt::getPercentage)
       .average()
       .orElse(0.0);
   ```
3. **Partitioning By Pass / Fail (`partitioningBy`)**:
   ```java
   Map<Boolean, List<Attempt>> passFailPartition = attempts.stream()
       .collect(Collectors.partitioningBy(Attempt::isPassed));
   ```
4. **Grouping By Category / Track (`groupingBy`)**:
   ```java
   Map<String, Long> attemptsPerTrack = attempts.stream()
       .collect(Collectors.groupingBy(Attempt::getQuizTitle, Collectors.counting()));
   ```
5. **Leaderboard Sorting & Limiting (`sorted`, `limit`)**:
   ```java
   List<Attempt> topScores = attempts.stream()
       .sorted(Comparator.comparingDouble(Attempt::getPercentage).reversed())
       .limit(10)
       .collect(Collectors.toCollection(ArrayList::new));
   ```

---

## 🧪 Live Verification Demonstrations

### 1. Verification of CO-1 (Polymorphic Candidate Display)
```bash
curl -s http://localhost:8080/api/users/admin-summary | python3 -m json.tool
```
*Response Output snippet*:
```json
{
    "displayName": "[Student] Vinay Nayak (ID: STU-9876543210, Phone: 9876543210)",
    "examsTaken": 2,
    "examsPassed": 1,
    "passPercentage": 50.0,
    "performanceStatus": "SATISFACTORY",
    "administrator": false
}
```

### 2. Verification of CO-2 & CO-4 (Stream Performance Report)
```bash
curl -s http://localhost:8080/api/analytics/report
```
*Output*:
```
=====================================================
    ACADEMIC EXAMINATION PERFORMANCE SUMMARY REPORT   
=====================================================
Total Examinations Submitted : 2
Candidates Passed            : 1
Candidates Failed            : 1
Overall Pass Rate            : 50.0%
Overall Class Average Score  : 50.0%
-----------------------------------------------------
Submissions by Examination Track:
 • Java 21 & Object-Oriented Programming: 2 attempts
=====================================================
```

### 3. Verification of CO-3 (Duplicate Submission Blocking)
```bash
curl -s -X POST http://localhost:8080/api/quizzes/quiz-java-core/submit \
  -H "Content-Type: application/json" \
  -d '{"studentName":"Vinay Nayak","studentEmail":"9876543210","answers":[{"questionId":"q-j1","selectedOptions":[1]}]}'
```
*Output on second attempt*:
```json
{
  "status": 409,
  "error": "DUPLICATE_SUBMISSION",
  "message": "Duplicate submission detected! This exam has already been submitted and processed."
}
```

---
*Generated for Vinay Nayak's Micro Project: Online Quiz Management System*
EOF