package com.quizsystem.android;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class QuizDataStore {
    private static QuizDataStore instance;
    private final SharedPreferences prefs;

    private static final String PREF_NAME = "quiz_native_prefs";
    private static final String KEY_USERS = "saved_users";
    private static final String KEY_ATTEMPTS = "saved_attempts";
    private static final String KEY_QUIZZES = "saved_quizzes";
    private static final String KEY_CURRENT_USER_NAME = "current_user_name";
    private static final String KEY_CURRENT_USER_PHONE = "current_user_phone";
    private static final String KEY_CURRENT_USER_ROLE = "current_user_role";

    private QuizDataStore(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        seedInitialDataIfEmpty();
    }

    public static synchronized QuizDataStore getInstance(Context context) {
        if (instance == null) {
            instance = new QuizDataStore(context);
        }
        return instance;
    }

    private void seedInitialDataIfEmpty() {
        // Seed Quizzes if empty
        if (!prefs.contains(KEY_QUIZZES)) {
            try {
                JSONArray quizzes = new JSONArray();

                // Quiz 1: Java 21 & OOP
                JSONObject q1 = new JSONObject();
                q1.put("id", "quiz-java-core");
                q1.put("title", "Java 21 & Object-Oriented Programming");
                q1.put("category", "Java");
                q1.put("durationMinutes", 10);
                q1.put("passingScorePercent", 70);
                JSONArray q1Questions = new JSONArray();

                q1Questions.put(createQuestionJson("q-j1", "Which of the following is true regarding Java virtual threads in Project Loom?",
                        new String[]{"They map 1:1 directly to OS kernel threads", "They are lightweight threads managed by the JVM reducing memory overhead", "They replace reactive streams entirely", "They require manual OS context switches"}, 1, 10,
                        "Virtual threads are lightweight threads managed by the JVM rather than OS kernel threads."));

                q1Questions.put(createQuestionJson("q-j2", "What does a sealed class or interface achieve in modern Java?",
                        new String[]{"Restricts which classes may extend or implement it via 'permits'", "Prevents any inheritance like the final keyword", "Forces all subclasses to be private", "Encrypts class bytecode"}, 0, 10,
                        "Sealed classes restrict inheritance to an explicitly permitted set of sub-types."));

                q1Questions.put(createQuestionJson("q-j3", "Which JVM memory region is primarily responsible for storing object instances created with 'new'?",
                        new String[]{"Metaspace", "Java Heap Space", "JVM Call Stack", "Program Counter"}, 1, 10,
                        "The Java Heap is where all class instances and arrays are allocated."));

                q1Questions.put(createQuestionJson("q-j4", "Which Java Stream terminal operation accumulates elements into a List, Set, or Map?",
                        new String[]{"flatMap()", "filter()", "collect()", "peek()"}, 2, 10,
                        "Stream.collect() accumulates elements into summary collections using Collectors."));

                q1.put("questions", q1Questions);
                quizzes.put(q1);

                // Quiz 2: Data Structures & Algorithms
                JSONObject q2 = new JSONObject();
                q2.put("id", "quiz-dsa");
                q2.put("title", "Data Structures & Algorithms");
                q2.put("category", "Algorithms");
                q2.put("durationMinutes", 15);
                q2.put("passingScorePercent", 60);
                JSONArray q2Questions = new JSONArray();

                q2Questions.put(createQuestionJson("q-d1", "What is worst-case time complexity of searching in a balanced Binary Search Tree?",
                        new String[]{"O(1)", "O(log N)", "O(N)", "O(N log N)"}, 1, 10,
                        "In balanced BSTs (AVL / Red-Black), tree height is guaranteed O(log N)."));

                q2Questions.put(createQuestionJson("q-d2", "Which data structure is fundamentally used by Breadth-First Search (BFS)?",
                        new String[]{"Stack (LIFO)", "Queue (FIFO)", "Priority Heap", "Hash Set"}, 1, 10,
                        "BFS traverses level-by-level using a FIFO Queue."));

                q2Questions.put(createQuestionJson("q-d3", "Which algorithm is standard for single-source shortest path with non-negative weights?",
                        new String[]{"Dijkstra's Algorithm", "Kruskal's Algorithm", "Tarjan's Algorithm", "Kosaraju's Algorithm"}, 0, 10,
                        "Dijkstra's algorithm finds single-source shortest paths in non-negative weighted graphs."));

                q2Questions.put(createQuestionJson("q-d4", "What distinguishes Dynamic Programming from standard Divide and Conquer?",
                        new String[]{"Greedy choice property", "Overlapping subproblems & optimal substructure", "Randomized pivots", "Linear time execution"}, 1, 10,
                        "DP applies when subproblems overlap and can be memoized."));

                q2.put("questions", q2Questions);
                quizzes.put(q2);

                // Quiz 3: Firebase Cloud Architecture
                JSONObject q3 = new JSONObject();
                q3.put("id", "quiz-firebase-cloud");
                q3.put("title", "Google Firebase & Cloud Architecture");
                q3.put("category", "Cloud");
                q3.put("durationMinutes", 12);
                q3.put("passingScorePercent", 75);
                JSONArray q3Questions = new JSONArray();

                q3Questions.put(createQuestionJson("q-f1", "How does Google Cloud Firestore structure and store data?",
                        new String[]{"Relational tables with foreign keys", "Collections of documents with fields and subcollections", "Monolithic JSON tree", "Wide-column families"}, 1, 10,
                        "Firestore is a NoSQL document database organized into collections and documents."));

                q3Questions.put(createQuestionJson("q-f2", "What file provides credentials to initialize the Firebase Admin SDK in Java?",
                        new String[]{"serviceAccountKey.json with private key and client email", "firebase-config.xml", "id_rsa.pub", "keystore.jks"}, 0, 10,
                        "The Google Service Account private key JSON file is used to authorize the Admin SDK."));

                q3Questions.put(createQuestionJson("q-f3", "What is the primary benefit of Firestore offline data persistence on mobile?",
                        new String[]{"Disables server sync", "Caches active data locally for seamless offline reading and writing", "Eliminates all query costs", "Replaces SQLite"}, 1, 10,
                        "Firestore caches data locally so mobile clients continue working offline."));

                q3Questions.put(createQuestionJson("q-f4", "In Cloud Firestore, what happens when you delete a document having subcollections?",
                        new String[]{"All subcollections are deleted recursively", "Throws foreign key violation error", "Document is deleted but subcollections remain accessible by direct path", "Reverts database"}, 2, 10,
                        "Deleting a document does not delete its subcollections; subcollections remain accessible unless deleted explicitly."));

                q3.put("questions", q3Questions);
                quizzes.put(q3);

                // Quiz 4: Modern Web Dev
                JSONObject q4 = new JSONObject();
                q4.put("id", "quiz-web-dev");
                q4.put("title", "Modern Full-Stack Web Development");
                q4.put("category", "Web Dev");
                q4.put("durationMinutes", 10);
                q4.put("passingScorePercent", 65);
                JSONArray q4Questions = new JSONArray();

                q4Questions.put(createQuestionJson("q-w1", "Which HTTP method should be used to apply partial modifications to a resource?",
                        new String[]{"GET", "POST", "PATCH", "PUT"}, 2, 10,
                        "PATCH is defined by RFC 5789 for applying partial modifications."));

                q4Questions.put(createQuestionJson("q-w2", "What browser mechanism restricts cross-origin HTTP requests?",
                        new String[]{"Same-Origin Policy (SOP) & CORS headers", "Content Delivery Network", "HSTS", "DNS Prefetching"}, 0, 10,
                        "Same-Origin Policy prevents scripts from reading responses across domains without CORS approval."));

                q4Questions.put(createQuestionJson("q-w3", "Which HTML5 API allows programmatic presentation without browser UI chrome?",
                        new String[]{"Fullscreen API", "Screen Orientation API", "Notification API", "Service Worker API"}, 0, 10,
                        "Element.requestFullscreen() opens fullscreen presentation."));

                q4Questions.put(createQuestionJson("q-w4", "In CSS layout, what is the key difference between CSS Grid and Flexbox?",
                        new String[]{"Flexbox is 2D while Grid is 1D", "CSS Grid is 2D (rows and columns) while Flexbox is 1D (row or column)", "Flexbox only works on tables", "Grid does not support responsiveness"}, 1, 10,
                        "CSS Grid is inherently two-dimensional while Flexbox handles one-dimensional flows."));

                q4.put("questions", q4Questions);
                quizzes.put(q4);

                prefs.edit().putString(KEY_QUIZZES, quizzes.toString()).apply();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Seed initial student: Vinay Nayak
        if (!prefs.contains(KEY_USERS)) {
            try {
                JSONArray users = new JSONArray();
                JSONObject user1 = new JSONObject();
                user1.put("id", "usr-1");
                user1.put("name", "Vinay Nayak");
                user1.put("phone", "9876543210");
                user1.put("password", "student123");
                user1.put("role", "STUDENT");
                user1.put("registeredAt", System.currentTimeMillis() - 86400000L);
                user1.put("examsTaken", 1);
                user1.put("examsPassed", 1);
                users.put(user1);

                prefs.edit().putString(KEY_USERS, users.toString()).apply();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Seed initial attempt
        if (!prefs.contains(KEY_ATTEMPTS)) {
            try {
                JSONArray attempts = new JSONArray();
                JSONObject a1 = new JSONObject();
                a1.put("id", "att-sample-1");
                a1.put("quizId", "quiz-java-core");
                a1.put("quizTitle", "Java 21 & Object-Oriented Programming");
                a1.put("studentName", "Vinay Nayak");
                a1.put("studentPhone", "9876543210");
                a1.put("score", 30);
                a1.put("totalPoints", 40);
                a1.put("percentage", 75.0);
                a1.put("passed", true);
                a1.put("passingScorePercent", 70);
                a1.put("timestamp", System.currentTimeMillis() - 3600000L);
                attempts.put(a1);

                prefs.edit().putString(KEY_ATTEMPTS, attempts.toString()).apply();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private JSONObject createQuestionJson(String id, String text, String[] options, int rightAnswerIdx, int points, String explanation) throws Exception {
        JSONObject q = new JSONObject();
        q.put("id", id);
        q.put("text", text);
        q.put("points", points);
        q.put("correctAnswerIndex", rightAnswerIdx);
        q.put("explanation", explanation);
        JSONArray opts = new JSONArray();
        for (String opt : options) opts.put(opt);
        q.put("options", opts);
        return q;
    }

    // --- AUTHENTICATION ---
    public String authenticate(String identifier, String password) {
        if (identifier == null || password == null) return null;
        String idClean = identifier.trim();
        String passClean = password.trim();

        // Admin: Admin / Admin123
        if (idClean.equalsIgnoreCase("Admin") && passClean.equals("Admin123")) {
            setCurrentSession("Administrator", "Admin", "ADMIN");
            return "ADMIN";
        }

        // Student check
        try {
            JSONArray users = new JSONArray(prefs.getString(KEY_USERS, "[]"));
            for (int i = 0; i < users.length(); i++) {
                JSONObject u = users.getJSONObject(i);
                if ((u.optString("phone").equalsIgnoreCase(idClean) || u.optString("name").equalsIgnoreCase(idClean))
                        && u.optString("password").equals(passClean)) {
                    setCurrentSession(u.optString("name"), u.optString("phone"), "STUDENT");
                    return "STUDENT";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean registerStudent(String name, String phone, String password) {
        if (name == null || phone == null || password == null) return false;
        String pClean = phone.trim();
        try {
            JSONArray users = new JSONArray(prefs.getString(KEY_USERS, "[]"));
            // Check duplicate phone
            for (int i = 0; i < users.length(); i++) {
                if (users.getJSONObject(i).optString("phone").equals(pClean)) {
                    return false; // already registered
                }
            }

            JSONObject newUser = new JSONObject();
            newUser.put("id", "usr-" + System.currentTimeMillis());
            newUser.put("name", name.trim());
            newUser.put("phone", pClean);
            newUser.put("password", password.trim());
            newUser.put("role", "STUDENT");
            newUser.put("registeredAt", System.currentTimeMillis());
            newUser.put("examsTaken", 0);
            newUser.put("examsPassed", 0);

            users.put(newUser);
            prefs.edit().putString(KEY_USERS, users.toString()).apply();
            setCurrentSession(name.trim(), pClean, "STUDENT");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setCurrentSession(String name, String phone, String role) {
        prefs.edit()
                .putString(KEY_CURRENT_USER_NAME, name)
                .putString(KEY_CURRENT_USER_PHONE, phone)
                .putString(KEY_CURRENT_USER_ROLE, role)
                .apply();
    }

    public void clearCurrentSession() {
        prefs.edit()
                .remove(KEY_CURRENT_USER_NAME)
                .remove(KEY_CURRENT_USER_PHONE)
                .remove(KEY_CURRENT_USER_ROLE)
                .apply();
    }

    public String getCurrentUserName() {
        return prefs.getString(KEY_CURRENT_USER_NAME, "Candidate");
    }

    public String getCurrentUserPhone() {
        return prefs.getString(KEY_CURRENT_USER_PHONE, "");
    }

    public String getCurrentUserRole() {
        return prefs.getString(KEY_CURRENT_USER_ROLE, null);
    }

    // --- DATA ACCESS ---
    public JSONArray getQuizzes() {
        try {
            return new JSONArray(prefs.getString(KEY_QUIZZES, "[]"));
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    public JSONObject getQuizById(String quizId) {
        JSONArray quizzes = getQuizzes();
        for (int i = 0; i < quizzes.length(); i++) {
            JSONObject q = quizzes.optJSONObject(i);
            if (q != null && q.optString("id").equals(quizId)) {
                return q;
            }
        }
        return null;
    }

    public JSONArray getUsers() {
        try {
            return new JSONArray(prefs.getString(KEY_USERS, "[]"));
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    public JSONArray getAttempts() {
        try {
            return new JSONArray(prefs.getString(KEY_ATTEMPTS, "[]"));
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    public void saveAttempt(String quizId, String quizTitle, String candidateName, String candidatePhone,
                            int score, int totalPoints, double percentage, boolean passed, int passMark) {
        try {
            JSONArray attempts = getAttempts();
            JSONObject att = new JSONObject();
            att.put("id", "att-" + System.currentTimeMillis());
            att.put("quizId", quizId);
            att.put("quizTitle", quizTitle);
            att.put("studentName", candidateName);
            att.put("studentPhone", candidatePhone);
            att.put("score", score);
            att.put("totalPoints", totalPoints);
            att.put("percentage", percentage);
            att.put("passed", passed);
            att.put("passingScorePercent", passMark);
            att.put("timestamp", System.currentTimeMillis());

            // Prepend
            JSONArray newArr = new JSONArray();
            newArr.put(att);
            for (int i = 0; i < attempts.length(); i++) newArr.put(attempts.getJSONObject(i));
            prefs.edit().putString(KEY_ATTEMPTS, newArr.toString()).apply();

            // Update user stats
            JSONArray users = getUsers();
            for (int i = 0; i < users.length(); i++) {
                JSONObject u = users.getJSONObject(i);
                if (u.optString("phone").equals(candidatePhone)) {
                    u.put("examsTaken", u.optInt("examsTaken", 0) + 1);
                    if (passed) {
                        u.put("examsPassed", u.optInt("examsPassed", 0) + 1);
                    }
                    break;
                }
            }
            prefs.edit().putString(KEY_USERS, users.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setRightAnswer(String quizId, String questionId, int optionIndex) {
        try {
            JSONArray quizzes = getQuizzes();
            for (int i = 0; i < quizzes.length(); i++) {
                JSONObject qz = quizzes.getJSONObject(i);
                if (qz.optString("id").equals(quizId)) {
                    JSONArray qList = qz.optJSONArray("questions");
                    if (qList != null) {
                        for (int j = 0; j < qList.length(); j++) {
                            JSONObject q = qList.getJSONObject(j);
                            if (q.optString("id").equals(questionId)) {
                                q.put("correctAnswerIndex", optionIndex);
                                break;
                            }
                        }
                    }
                    break;
                }
            }
            prefs.edit().putString(KEY_QUIZZES, quizzes.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
