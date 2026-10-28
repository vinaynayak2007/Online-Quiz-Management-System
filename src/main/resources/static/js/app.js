/**
 * QuizPulse - Unified Student Exam Portal & In-App Admin Management Studio
 * Java 21 + Spring Boot + Google Firebase + Standalone Offline Android Client
 */

// Embedded Default Quizzes for 100% Offline Android APK & Web Compatibility
const SEED_QUIZZES = [
  {
    "id": "quiz-java-core",
    "title": "Java 21 & Object-Oriented Programming",
    "description": "Comprehensive evaluation covering Java 21 features, Virtual Threads (Project Loom), Record Patterns, JVM memory model, and OOP principles.",
    "category": "Java",
    "durationMinutes": 10,
    "passingScorePercent": 70,
    "questions": [
      {
        "id": "q-j1",
        "text": "Which of the following is true regarding Java virtual threads introduced in Project Loom?",
        "type": "SINGLE_CHOICE",
        "options": [
          "They map 1:1 directly to operating system kernel threads",
          "They are lightweight threads managed by the JVM that drastically reduce memory overhead for concurrent tasks",
          "They completely replace asynchronous reactive programming and reactive streams",
          "They require manual operating system thread context switches"
        ],
        "correctAnswers": [1],
        "points": 10,
        "explanation": "Virtual threads are lightweight threads managed by the JVM rather than the underlying OS kernel, allowing high-throughput concurrent applications with minimal memory."
      },
      {
        "id": "q-j2",
        "text": "What does a sealed class or interface achieve in modern Java?",
        "type": "SINGLE_CHOICE",
        "options": [
          "It restricts which other classes or interfaces may extend or implement it via the 'permits' clause",
          "It prevents any class from inheriting from it, exactly like the final keyword",
          "It forces all sub-classes to be marked private",
          "It automatically encrypts the compiled bytecode class file"
        ],
        "correctAnswers": [0],
        "points": 10,
        "explanation": "Sealed classes and interfaces restrict which other classes or interfaces may extend or implement them using the permits clause."
      },
      {
        "id": "q-j3",
        "text": "Which JVM memory region is primarily responsible for storing object instances created with the 'new' keyword?",
        "type": "SINGLE_CHOICE",
        "options": [
          "Metaspace",
          "Java Heap Space",
          "JVM Call Stack",
          "Program Counter (PC) Register"
        ],
        "correctAnswers": [1],
        "points": 10,
        "explanation": "The Java Heap Space is the runtime data area from which memory for all class instances and arrays is allocated."
      },
      {
        "id": "q-j4",
        "text": "Which Java Stream terminal operation reduces elements into a single summary result such as a List, Set, or Map?",
        "type": "SINGLE_CHOICE",
        "options": [
          "flatMap()",
          "filter()",
          "collect()",
          "peek()"
        ],
        "correctAnswers": [2],
        "points": 10,
        "explanation": "Stream.collect() is a terminal operation that accumulates elements into collections like List, Set, or Map using Collectors."
      }
    ]
  },
  {
    "id": "quiz-dsa",
    "title": "Data Structures & Algorithms",
    "description": "Examine your proficiency in algorithmic time complexity (Big-O), balanced search trees, graph traversal, and dynamic programming.",
    "category": "Algorithms",
    "durationMinutes": 15,
    "passingScorePercent": 60,
    "questions": [
      {
        "id": "q-d1",
        "text": "What is the worst-case time complexity of searching for an element in a balanced Binary Search Tree (AVL or Red-Black Tree)?",
        "type": "SINGLE_CHOICE",
        "options": [
          "O(1)",
          "O(log N)",
          "O(N)",
          "O(N log N)"
        ],
        "correctAnswers": [1],
        "points": 10,
        "explanation": "In a balanced BST, tree height is strictly guaranteed to be O(log N), ensuring search, insert, and delete operations take O(log N) worst-case time."
      },
      {
        "id": "q-d2",
        "text": "Which data structure is fundamentally utilized by Breadth-First Search (BFS) graph traversal?",
        "type": "SINGLE_CHOICE",
        "options": [
          "Stack (LIFO)",
          "Queue (FIFO)",
          "Priority Heap",
          "Hash Set"
        ],
        "correctAnswers": [1],
        "points": 10,
        "explanation": "BFS uses a FIFO Queue to visit graph vertices level by level."
      },
      {
        "id": "q-d3",
        "text": "Which algorithm is standard for finding the shortest path in a weighted graph with non-negative edge weights?",
        "type": "SINGLE_CHOICE",
        "options": [
          "Dijkstra's Algorithm",
          "Kruskal's Algorithm",
          "Tarjan's SCC Algorithm",
          "Kosaraju's Algorithm"
        ],
        "correctAnswers": [0],
        "points": 10,
        "explanation": "Dijkstra's algorithm finds single-source shortest paths in weighted directed/undirected graphs with non-negative weights."
      },
      {
        "id": "q-d4",
        "text": "What is the primary characteristic that distinguishes Dynamic Programming from standard Divide and Conquer?",
        "type": "SINGLE_CHOICE",
        "options": [
          "Greedy choice property",
          "Overlapping subproblems and optimal substructure",
          "Randomized pivot selection",
          "Strict linear time execution"
        ],
        "correctAnswers": [1],
        "points": 10,
        "explanation": "Dynamic Programming applies when a problem has optimal substructure and overlapping subproblems that can be memoized."
      }
    ]
  },
  {
    "id": "quiz-firebase-cloud",
    "title": "Google Firebase & Cloud Architecture",
    "description": "Test understanding of Google Cloud Firestore, NoSQL document data modeling, Firebase Admin SDK security rules, and real-time syncing.",
    "category": "Cloud",
    "durationMinutes": 12,
    "passingScorePercent": 75,
    "questions": [
      {
        "id": "q-f1",
        "text": "How does Google Cloud Firestore primarily structure and store data?",
        "type": "SINGLE_CHOICE",
        "options": [
          "Relational tables with foreign key constraints",
          "Collections of documents containing key-value fields and subcollections",
          "A monolithic JSON tree",
          "Columnar wide-column families"
        ],
        "correctAnswers": [1],
        "points": 10,
        "explanation": "Firestore is a flexible, scalable NoSQL document database organized into collections containing documents with fields and subcollections."
      },
      {
        "id": "q-f2",
        "text": "What file format provides credentials to initialize the Firebase Admin SDK in Java applications?",
        "type": "SINGLE_CHOICE",
        "options": [
          "serviceAccountKey.json containing private key and client email",
          "firebase-config.xml",
          "id_rsa.pub SSH public key",
          "keystore.jks"
        ],
        "correctAnswers": [0],
        "points": 10,
        "explanation": "The Google Service Account private key JSON file (serviceAccountKey.json) is used with GoogleCredentials.fromStream() to authorize the Admin SDK."
      },
      {
        "id": "q-f3",
        "text": "What is the primary benefit of Firestore offline data persistence on mobile clients?",
        "type": "SINGLE_CHOICE",
        "options": [
          "It permanently disables server syncing",
          "It caches active data locally so users can read and write data seamlessly without network connectivity",
          "It eliminates all database query costs permanently",
          "It replaces SQLite database entirely"
        ],
        "correctAnswers": [1],
        "points": 10,
        "explanation": "Firestore offline persistence caches data locally so apps function smoothly offline and automatically reconcile changes upon reconnection."
      },
      {
        "id": "q-f4",
        "text": "In Cloud Firestore, what happens when you delete a document that has subcollections?",
        "type": "SINGLE_CHOICE",
        "options": [
          "All subcollections and their documents are automatically deleted recursively",
          "The operation throws a foreign key integrity violation error",
          "The document is deleted, but its subcollections remain orphaned unless explicitly deleted",
          "The database reverts to the previous snapshot"
        ],
        "correctAnswers": [2],
        "points": 10,
        "explanation": "Deleting a document in Firestore does not automatically delete its subcollections; subcollections remain accessible by direct path unless explicitly deleted."
      }
    ]
  },
  {
    "id": "quiz-web-dev",
    "title": "Modern Full-Stack Web Development",
    "description": "Assessment of modern HTML5 APIs, CSS Flexbox/Grid architectures, RESTful API design patterns, and Web Security (CORS/XSS).",
    "category": "Web Dev",
    "durationMinutes": 10,
    "passingScorePercent": 65,
    "questions": [
      {
        "id": "q-w1",
        "text": "Which HTTP method should be used to apply partial modifications to an existing resource in RESTful API design?",
        "type": "SINGLE_CHOICE",
        "options": [
          "GET",
          "POST",
          "PATCH",
          "PUT"
        ],
        "correctAnswers": [2],
        "points": 10,
        "explanation": "PATCH is defined by RFC 5789 for applying partial modifications to a resource, whereas PUT typically replaces the entire resource representation."
      },
      {
        "id": "q-w2",
        "text": "What browser mechanism restricts a web page from making requests to a different domain than the one that served it?",
        "type": "SINGLE_CHOICE",
        "options": [
          "Same-Origin Policy (SOP) and CORS headers",
          "Content Delivery Network (CDN)",
          "HTTP Strict Transport Security (HSTS)",
          "DNS Prefetching"
        ],
        "correctAnswers": [0],
        "points": 10,
        "explanation": "The Same-Origin Policy is a fundamental security model in web browsers; CORS allows servers to explicitly authorize cross-origin requests."
      },
      {
        "id": "q-w3",
        "text": "Which HTML5 API allows client-side web applications to programmatically request full-screen presentation?",
        "type": "SINGLE_CHOICE",
        "options": [
          "Fullscreen API (element.requestFullscreen())",
          "Screen Orientation API",
          "Web Notification API",
          "Service Worker API"
        ],
        "correctAnswers": [0],
        "points": 10,
        "explanation": "The Fullscreen API provides methods like Element.requestFullscreen() to present elements and entire web apps without browser UI chrome."
      },
      {
        "id": "q-w4",
        "text": "In CSS layout, what is the key difference between CSS Grid and Flexbox?",
        "type": "SINGLE_CHOICE",
        "options": [
          "Flexbox is two-dimensional while CSS Grid is strictly one-dimensional",
          "CSS Grid is two-dimensional (rows and columns) while Flexbox is primarily one-dimensional (row OR column)",
          "Flexbox only works with table elements",
          "CSS Grid does not support responsive breakpoints"
        ],
        "correctAnswers": [1],
        "points": 10,
        "explanation": "CSS Grid is inherently two-dimensional allowing simultaneous row and column placement, whereas Flexbox is designed for one-dimensional layouts."
      }
    ]
  }
];

class AppManager {
  constructor() {
    this.currentUser = null; // { role: 'STUDENT'|'ADMIN', name: '', phone: '' }
    this.quizzes = [];
    this.users = [];
    this.attempts = [];

    // Active Exam State
    this.activeQuiz = null;
    this.currentQIndex = 0;
    this.studentAnswers = {}; // { qId: selectedIndex }
    this.flaggedQuestions = new Set();
    this.timerInterval = null;
    this.secondsRemaining = 0;
    this.cheatViolations = 0;

    // Admin Editing State
    this.editingQuestionQuizId = null;
    this.editingQuestionId = null;

    this.init();
  }

  async init() {
    this.loadLocalData();
    this.setupAntiCheatListeners();
    this.checkSession();
  }

  // --- STORAGE & INITIALIZATION ---
  loadLocalData() {
    // 1. Quizzes
    const savedQuizzes = localStorage.getItem('quiz_quizzes');
    if (savedQuizzes) {
      try {
        this.quizzes = JSON.parse(savedQuizzes);
      } catch (e) {
        this.quizzes = JSON.parse(JSON.stringify(SEED_QUIZZES));
      }
    } else {
      this.quizzes = JSON.parse(JSON.stringify(SEED_QUIZZES));
      this.saveQuizzesLocal();
    }

    // 2. Registered Users
    const savedUsers = localStorage.getItem('quiz_registered_users');
    if (savedUsers) {
      try {
        this.users = JSON.parse(savedUsers);
      } catch (e) {
        this.users = [];
      }
    } else {
      // Default initial sample user: Vinay Nayak
      this.users = [
        {
          id: 'usr-1',
          name: 'Vinay Nayak',
          phone: '9876543210',
          password: 'student123',
          role: 'STUDENT',
          registeredAt: Date.now() - 86400000,
          examsTaken: 1,
          examsPassed: 1
        }
      ];
      this.saveUsersLocal();
    }

    // 3. Exam Attempts
    const savedAttempts = localStorage.getItem('quiz_user_attempts');
    if (savedAttempts) {
      try {
        this.attempts = JSON.parse(savedAttempts);
      } catch (e) {
        this.attempts = [];
      }
    } else {
      // Default initial attempt
      this.attempts = [
        {
          id: 'att-sample-1',
          quizId: 'quiz-java-core',
          quizTitle: 'Java 21 & Object-Oriented Programming',
          studentName: 'Vinay Nayak',
          studentPhone: '9876543210',
          score: 30,
          totalPoints: 40,
          percentage: 75.0,
          passed: true,
          passingScorePercent: 70,
          timestamp: Date.now() - 3600000
        }
      ];
      this.saveAttemptsLocal();
    }
  }

  saveQuizzesLocal() {
    localStorage.setItem('quiz_quizzes', JSON.stringify(this.quizzes));
  }
  saveUsersLocal() {
    localStorage.setItem('quiz_registered_users', JSON.stringify(this.users));
  }
  saveAttemptsLocal() {
    localStorage.setItem('quiz_user_attempts', JSON.stringify(this.attempts));
  }

  // --- AUTHENTICATION FLOW ---
  checkSession() {
    const sessionUser = localStorage.getItem('quiz_session_user');
    if (sessionUser) {
      try {
        this.currentUser = JSON.parse(sessionUser);
        this.renderUserInterface();
        return;
      } catch (e) {}
    }
    // Show auth view by default
    this.showView('auth-view');
  }

  setAuthTab(tab) {
    const loginBtn = document.getElementById('tab-login-btn');
    const registerBtn = document.getElementById('tab-register-btn');
    const formLogin = document.getElementById('form-login');
    const formRegister = document.getElementById('form-register');

    if (tab === 'login') {
      loginBtn.classList.add('active');
      registerBtn.classList.remove('active');
      formLogin.style.display = 'block';
      formRegister.style.display = 'none';
    } else {
      registerBtn.classList.add('active');
      loginBtn.classList.remove('active');
      formRegister.style.display = 'block';
      formLogin.style.display = 'none';
    }
  }

  fillAdminCredentials() {
    document.getElementById('login-identifier').value = 'Admin';
    document.getElementById('login-password').value = 'Admin123';
  }

  fillDemoStudentCredentials() {
    document.getElementById('login-identifier').value = '9876543210';
    document.getElementById('login-password').value = 'student123';
  }

  handleLogin(e) {
    e.preventDefault();
    const idField = document.getElementById('login-identifier').value.trim();
    const passField = document.getElementById('login-password').value.trim();

    // 1. Check for Admin Login: Admin / Admin123
    if (idField.toLowerCase() === 'admin' && passField === 'Admin123') {
      this.currentUser = {
        role: 'ADMIN',
        name: 'Administrator',
        phone: 'Admin'
      };
      localStorage.setItem('quiz_session_user', JSON.stringify(this.currentUser));
      this.showToast('🛡️ Welcome, Administrator!');
      this.renderUserInterface();
      return;
    }

    // 2. Check for Registered Student Login
    const user = this.users.find(u =>
      (u.phone === idField || u.name.toLowerCase() === idField.toLowerCase()) &&
      u.password === passField
    );

    if (user) {
      this.currentUser = {
        role: 'STUDENT',
        name: user.name,
        phone: user.phone
      };
      localStorage.setItem('quiz_session_user', JSON.stringify(this.currentUser));
      this.showToast(`👋 Welcome back, ${user.name}!`);
      this.renderUserInterface();
      return;
    }

    alert('❌ Invalid Credentials!\n\n• For Admin: Username "Admin" & Password "Admin123"\n• For Student: Please enter your registered phone number & password, or switch to the Register tab.');
  }

  handleRegister(e) {
    e.preventDefault();
    const name = document.getElementById('reg-name').value.trim();
    const phone = document.getElementById('reg-phone').value.trim();
    const password = document.getElementById('reg-password').value.trim();

    if (!name || !phone || !password) {
      alert('Please fill out all registration fields.');
      return;
    }

    // Check if phone already registered
    if (this.users.some(u => u.phone === phone)) {
      alert('A user with this phone number already exists! Please sign in.');
      this.setAuthTab('login');
      document.getElementById('login-identifier').value = phone;
      return;
    }

    const newUser = {
      id: 'usr-' + Date.now(),
      name: name,
      phone: phone,
      password: password,
      role: 'STUDENT',
      registeredAt: Date.now(),
      examsTaken: 0,
      examsPassed: 0
    };

    this.users.unshift(newUser);
    this.saveUsersLocal();

    // Sync to backend if online
    fetch('/api/users/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name, phone, password })
    }).catch(() => {});

    // Auto login
    this.currentUser = {
      role: 'STUDENT',
      name: newUser.name,
      phone: newUser.phone
    };
    localStorage.setItem('quiz_session_user', JSON.stringify(this.currentUser));
    this.showToast('🎉 Registration Successful! Welcome to QuizPulse.');
    this.renderUserInterface();
  }

  logout() {
    this.currentUser = null;
    localStorage.removeItem('quiz_session_user');
    document.getElementById('logged-in-controls').style.display = 'none';
    document.getElementById('user-greeting-sub').textContent = 'Secure Exam Assessment System';
    this.showView('auth-view');
    this.showToast('Logged out successfully.');
  }

  // --- VIEW ROUTING ---
  showView(viewId) {
    document.querySelectorAll('.view-section').forEach(sec => sec.classList.remove('active'));
    const target = document.getElementById(viewId);
    if (target) {
      target.classList.add('active');
    }
  }

  renderUserInterface() {
    if (!this.currentUser) {
      this.showView('auth-view');
      return;
    }

    const ctrl = document.getElementById('logged-in-controls');
    const pill = document.getElementById('user-display-pill');
    const sub = document.getElementById('user-greeting-sub');

    ctrl.style.display = 'flex';
    pill.textContent = `${this.currentUser.role === 'ADMIN' ? '🛡️ Admin' : '👤'} ${this.currentUser.name}`;

    if (this.currentUser.role === 'ADMIN') {
      sub.textContent = 'Admin Control Studio';
      this.renderAdminView();
      this.showView('admin-view');
    } else {
      sub.textContent = `Student: ${this.currentUser.name} (${this.currentUser.phone})`;
      this.renderStudentView();
      this.showView('student-view');
    }
  }

  navigateHome() {
    if (!this.currentUser) {
      this.showView('auth-view');
    } else if (this.currentUser.role === 'ADMIN') {
      this.showView('admin-view');
    } else {
      this.showView('student-view');
    }
  }

  // --- STUDENT EXAM VIEW ("after login exam name show") ---
  renderStudentView() {
    const container = document.getElementById('exam-catalog-grid');
    container.innerHTML = '';
    document.getElementById('student-exam-count').textContent = this.quizzes.length;

    this.quizzes.forEach(quiz => {
      // Find candidate's last attempt on this quiz
      const userAttempt = this.attempts.find(a =>
        a.quizId === quiz.id && (a.studentPhone === this.currentUser.phone || a.studentName === this.currentUser.name)
      );

      const card = document.createElement('div');
      card.className = 'exam-card';

      let statusBadge = '';
      if (userAttempt) {
        if (userAttempt.passed) {
          statusBadge = `<span class="badge badge-passed" style="margin-left:auto;">PASSED (${userAttempt.percentage}%)</span>`;
        } else {
          statusBadge = `<span class="badge badge-failed" style="margin-left:auto;">FAILED (${userAttempt.percentage}%)</span>`;
        }
      }

      card.innerHTML = `
        <div>
          <div style="display:flex; align-items:center;">
            <span class="exam-badge">${quiz.category || 'EXAM'}</span>
            ${statusBadge}
          </div>
          <h3 class="exam-title">${quiz.title}</h3>
          <p class="exam-desc">${quiz.description || 'Test your proficiency and earn verified certification.'}</p>
        </div>

        <div>
          <div class="exam-meta">
            <div class="exam-meta-item">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
              <span>${quiz.durationMinutes || 10} Mins</span>
            </div>
            <div class="exam-meta-item">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
              <span>${quiz.questions.length} Questions</span>
            </div>
            <div class="exam-meta-item">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="20 6 9 17 4 12"/></svg>
              <span>Pass: ${quiz.passingScorePercent || 70}%</span>
            </div>
          </div>

          <button onclick="app.startExam('${quiz.id}')" class="btn btn-primary" style="width:100%; padding:10px;">
            🚀 Start Exam (Full Screen Mode)
          </button>
        </div>
      `;
      container.appendChild(card);
    });

    this.renderStudentHistory();
  }

  renderStudentHistory() {
    const tbody = document.getElementById('student-history-tbody');
    tbody.innerHTML = '';

    const myAttempts = this.attempts.filter(a =>
      a.studentPhone === this.currentUser.phone || a.studentName === this.currentUser.name
    );

    if (myAttempts.length === 0) {
      tbody.innerHTML = `<tr><td colspan="5" style="text-align:center; padding:24px; color:#94a3b8;">No exams attempted yet. Choose an examination above to begin!</td></tr>`;
      return;
    }

    myAttempts.forEach(att => {
      const tr = document.createElement('tr');
      const passBadge = att.passed
        ? `<span class="badge badge-passed">PASSED ✅</span>`
        : `<span class="badge badge-failed">FAILED ❌</span>`;

      tr.innerHTML = `
        <td style="font-weight:600; color:white;">${att.quizTitle}</td>
        <td style="font-weight:700;">${att.percentage}% (${att.score}/${att.totalPoints} pts)</td>
        <td>${att.passingScorePercent || 70}%</td>
        <td>${passBadge}</td>
        <td style="color:#94a3b8;">${new Date(att.timestamp).toLocaleString()}</td>
      `;
      tbody.appendChild(tr);
    });
  }

  // --- FULL SCREEN STRICT EXAM RUNNER ("full screen make no tab change nothing") ---
  startExam(quizId) {
    const quiz = this.quizzes.find(q => q.id === quizId);
    if (!quiz || !quiz.questions || quiz.questions.length === 0) {
      alert('Selected exam does not contain any questions.');
      return;
    }

    this.activeQuiz = quiz;
    this.currentQIndex = 0;
    this.studentAnswers = {};
    this.flaggedQuestions.clear();
    this.cheatViolations = 0;
    this.secondsRemaining = (quiz.durationMinutes || 10) * 60;

    // Trigger Fullscreen
    this.enterStrictFullscreen();

    // Notify Android App via Javascript bridge
    if (window.AndroidApp && typeof window.AndroidApp.setExamActive === 'function') {
      window.AndroidApp.setExamActive(true);
    }

    // Show Exam Runner View
    document.getElementById('runner-exam-title').textContent = quiz.title;
    document.getElementById('runner-exam-category').textContent = quiz.category || 'EXAM';
    document.getElementById('exam-runner-view').classList.add('active');

    this.renderQuestionGrid();
    this.renderActiveQuestion();
    this.startTimer();
  }

  enterStrictFullscreen() {
    try {
      const docEl = document.documentElement;
      if (docEl.requestFullscreen) {
        docEl.requestFullscreen().catch(() => {});
      } else if (docEl.webkitRequestFullscreen) {
        docEl.webkitRequestFullscreen();
      } else if (docEl.msRequestFullscreen) {
        docEl.msRequestFullscreen();
      }
    } catch (e) {}

    if (window.AndroidApp && typeof window.AndroidApp.enterFullscreen === 'function') {
      window.AndroidApp.enterFullscreen();
    }
  }

  setupAntiCheatListeners() {
    // 1. Visibility change listener (tab change / minimize)
    document.addEventListener('visibilitychange', () => {
      if (document.hidden && this.activeQuiz) {
        this.triggerCheatAlert();
      }
    });

    // 2. Window blur listener (switching away from window)
    window.addEventListener('blur', () => {
      if (this.activeQuiz) {
        this.triggerCheatAlert();
      }
    });
  }

  triggerCheatAlert() {
    if (!this.activeQuiz) return;
    this.cheatViolations++;
    document.getElementById('cheat-violation-count').textContent = this.cheatViolations;
    document.getElementById('anti-cheat-overlay').classList.add('active');

    // Beep sound
    try {
      const audioCtx = new (window.AudioContext || window.webkitAudioContext)();
      const osc = audioCtx.createOscillator();
      osc.type = 'sawtooth';
      osc.frequency.setValueAtTime(440, audioCtx.currentTime);
      osc.connect(audioCtx.destination);
      osc.start();
      osc.stop(audioCtx.currentTime + 0.3);
    } catch (e) {}
  }

  dismissCheatWarning() {
    document.getElementById('anti-cheat-overlay').classList.remove('active');
    this.enterStrictFullscreen();
  }

  startTimer() {
    if (this.timerInterval) clearInterval(this.timerInterval);
    this.updateTimerDisplay();

    this.timerInterval = setInterval(() => {
      this.secondsRemaining--;
      this.updateTimerDisplay();

      if (this.secondsRemaining <= 0) {
        clearInterval(this.timerInterval);
        alert('⏰ Time Expired! Your exam will be submitted automatically.');
        this.submitExam();
      }
    }, 1000);
  }

  updateTimerDisplay() {
    const mins = Math.floor(this.secondsRemaining / 60);
    const secs = this.secondsRemaining % 60;
    const str = `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;
    const box = document.getElementById('runner-timer-box');
    const text = document.getElementById('runner-timer-text');
    text.textContent = str;

    if (this.secondsRemaining <= 60) {
      box.classList.add('urgent');
    } else {
      box.classList.remove('urgent');
    }
  }

  renderQuestionGrid() {
    const grid = document.getElementById('runner-question-grid');
    grid.innerHTML = '';

    this.activeQuiz.questions.forEach((q, idx) => {
      const btn = document.createElement('button');
      btn.className = 'grid-btn';
      if (idx === this.currentQIndex) btn.classList.add('current');
      if (this.studentAnswers[q.id] !== undefined) btn.classList.add('answered');
      if (this.flaggedQuestions.has(q.id)) btn.classList.add('flagged');

      btn.textContent = idx + 1;
      btn.onclick = () => this.jumpToQuestion(idx);
      grid.appendChild(btn);
    });
  }

  renderActiveQuestion() {
    const q = this.activeQuiz.questions[this.currentQIndex];
    document.getElementById('runner-q-index').textContent = `Question ${this.currentQIndex + 1} of ${this.activeQuiz.questions.length}`;
    document.getElementById('runner-q-points').textContent = `${q.points || 10} Points`;
    document.getElementById('runner-q-statement').textContent = q.text;

    // Flag button text
    const isFlagged = this.flaggedQuestions.has(q.id);
    document.getElementById('flag-btn-text').textContent = isFlagged ? 'Flagged for Review' : 'Flag for Review';
    document.getElementById('runner-flag-btn').style.borderColor = isFlagged ? 'var(--warning)' : '#334155';

    // Prev / Next button state
    document.getElementById('runner-prev-btn').disabled = (this.currentQIndex === 0);
    document.getElementById('runner-next-btn').textContent = (this.currentQIndex === this.activeQuiz.questions.length - 1) ? 'Review / Finish' : 'Next Question';

    // Options list
    const optionsContainer = document.getElementById('runner-options-list');
    optionsContainer.innerHTML = '';

    const selectedOption = this.studentAnswers[q.id];

    q.options.forEach((optText, optIdx) => {
      const optDiv = document.createElement('div');
      optDiv.className = 'option-item';
      if (selectedOption === optIdx) {
        optDiv.classList.add('selected');
      }

      optDiv.onclick = () => this.selectOption(optIdx);

      const marker = String.fromCharCode(65 + optIdx); // A, B, C, D
      optDiv.innerHTML = `
        <div class="option-marker">${marker}</div>
        <div style="font-size:15px; color:#e2e8f0; line-height:1.4;">${optText}</div>
      `;
      optionsContainer.appendChild(optDiv);
    });

    this.renderQuestionGrid();
  }

  selectOption(optIdx) {
    const q = this.activeQuiz.questions[this.currentQIndex];
    this.studentAnswers[q.id] = optIdx;
    this.renderActiveQuestion();
  }

  toggleFlagQuestion() {
    const q = this.activeQuiz.questions[this.currentQIndex];
    if (this.flaggedQuestions.has(q.id)) {
      this.flaggedQuestions.delete(q.id);
    } else {
      this.flaggedQuestions.add(q.id);
    }
    this.renderActiveQuestion();
  }

  nextQuestion() {
    if (this.currentQIndex < this.activeQuiz.questions.length - 1) {
      this.currentQIndex++;
      this.renderActiveQuestion();
    } else {
      this.confirmSubmitExam();
    }
  }

  prevQuestion() {
    if (this.currentQIndex > 0) {
      this.currentQIndex--;
      this.renderActiveQuestion();
    }
  }

  jumpToQuestion(idx) {
    this.currentQIndex = idx;
    this.renderActiveQuestion();
  }

  confirmSubmitExam() {
    const answeredCount = Object.keys(this.studentAnswers).length;
    const totalCount = this.activeQuiz.questions.length;
    const unanswered = totalCount - answeredCount;

    let msg = `Are you sure you want to submit your exam?\n\n• Answered: ${answeredCount}/${totalCount}\n`;
    if (unanswered > 0) {
      msg += `⚠️ You still have ${unanswered} UNANSWERED questions!\n`;
    }

    if (confirm(msg)) {
      this.submitExam();
    }
  }

  submitExam() {
    if (this.timerInterval) clearInterval(this.timerInterval);

    // Calculate score
    let score = 0;
    let totalPoints = 0;
    let correctCount = 0;
    const evaluations = [];

    this.activeQuiz.questions.forEach(q => {
      const qPoints = q.points || 10;
      totalPoints += qPoints;

      const userChoice = this.studentAnswers[q.id];
      const correctChoice = (q.correctAnswers && q.correctAnswers.length > 0) ? q.correctAnswers[0] : 0;
      const isCorrect = (userChoice !== undefined && userChoice === correctChoice);

      if (isCorrect) {
        score += qPoints;
        correctCount++;
      }

      evaluations.push({
        qText: q.text,
        userChoice: userChoice,
        correctChoice: correctChoice,
        options: q.options,
        isCorrect: isCorrect,
        explanation: q.explanation || ''
      });
    });

    const percentage = Math.round((score / totalPoints) * 100);
    const passThreshold = this.activeQuiz.passingScorePercent || 70;
    const passed = (percentage >= passThreshold);

    // Record Attempt
    const attempt = {
      id: 'att-' + Date.now(),
      quizId: this.activeQuiz.id,
      quizTitle: this.activeQuiz.title,
      studentName: this.currentUser.name,
      studentPhone: this.currentUser.phone,
      score: score,
      totalPoints: totalPoints,
      percentage: percentage,
      passed: passed,
      passingScorePercent: passThreshold,
      timestamp: Date.now()
    };

    this.attempts.unshift(attempt);
    this.saveAttemptsLocal();

    // Update User Stats
    const user = this.users.find(u => u.phone === this.currentUser.phone);
    if (user) {
      user.examsTaken = (user.examsTaken || 0) + 1;
      if (passed) user.examsPassed = (user.examsPassed || 0) + 1;
      this.saveUsersLocal();
    }

    // Sync Attempt to Backend if available
    fetch(`/api/quizzes/${this.activeQuiz.id}/submit`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        quizId: this.activeQuiz.id,
        studentName: this.currentUser.name,
        studentEmail: this.currentUser.phone,
        timeSpentSeconds: ((this.activeQuiz.durationMinutes || 10) * 60) - this.secondsRemaining,
        answers: Object.entries(this.studentAnswers).map(([qId, optIdx]) => ({
          questionId: qId,
          selectedOptions: [optIdx]
        }))
      })
    }).catch(() => {});

    // Notify Android App
    if (window.AndroidApp && typeof window.AndroidApp.setExamActive === 'function') {
      window.AndroidApp.setExamActive(false);
    }

    // Close Exam Runner View
    document.getElementById('exam-runner-view').classList.remove('active');
    this.activeQuiz = null;

    // Show Scorecard Modal
    this.showScorecard(attempt, evaluations);
  }

  showScorecard(attempt, evaluations) {
    const modal = document.getElementById('scorecard-modal');
    modal.classList.add('active');

    document.getElementById('scorecard-exam-title').textContent = attempt.quizTitle;
    document.getElementById('scorecard-candidate-name').textContent = `Candidate: ${attempt.studentName} (📞 ${attempt.studentPhone})`;
    document.getElementById('scorecard-percentage').textContent = `${attempt.percentage}%`;
    document.getElementById('scorecard-pass-mark').textContent = `${attempt.passingScorePercent}%`;
    document.getElementById('scorecard-points').textContent = `${attempt.score} / ${attempt.totalPoints}`;

    const passBadge = document.getElementById('scorecard-pass-badge');
    const icon = document.getElementById('scorecard-icon');

    if (attempt.passed) {
      passBadge.className = 'badge badge-passed';
      passBadge.textContent = 'EXAM PASSED ✅';
      icon.textContent = '🎉';
    } else {
      passBadge.className = 'badge badge-failed';
      passBadge.textContent = 'DID NOT PASS ❌';
      icon.textContent = '⚠️';
    }

    const evalContainer = document.getElementById('scorecard-evaluations');
    evalContainer.innerHTML = '';

    evaluations.forEach((item, idx) => {
      const card = document.createElement('div');
      card.style.background = '#0b0f19';
      card.style.border = `1px solid ${item.isCorrect ? 'var(--success)' : 'var(--danger)'}`;
      card.style.borderRadius = '8px';
      card.style.padding = '12px';

      const yourOpt = item.userChoice !== undefined ? `${String.fromCharCode(65 + item.userChoice)}: ${item.options[item.userChoice]}` : 'Unanswered';
      const corrOpt = `${String.fromCharCode(65 + item.correctChoice)}: ${item.options[item.correctChoice]}`;

      card.innerHTML = `
        <div style="font-weight:700; font-size:13px; color:white; margin-bottom:4px;">Q${idx + 1}: ${item.qText}</div>
        <div style="font-size:12px; margin-bottom:2px; color:${item.isCorrect ? '#34d399' : '#f87171'};">
          Your Answer: <strong>${yourOpt}</strong> ${item.isCorrect ? '✓ Correct' : '✗ Incorrect'}
        </div>
        ${!item.isCorrect ? `<div style="font-size:12px; color:#34d399; margin-bottom:2px;">Designated Correct Answer: <strong>${corrOpt}</strong></div>` : ''}
        ${item.explanation ? `<div style="font-size:11px; color:#94a3b8; margin-top:4px; font-style:italic;">💡 Explanation: ${item.explanation}</div>` : ''}
      `;
      evalContainer.appendChild(card);
    });
  }

  closeScorecard() {
    document.getElementById('scorecard-modal').classList.remove('active');
    this.renderStudentView();
    try {
      if (document.exitFullscreen) document.exitFullscreen().catch(() => {});
    } catch (e) {}
  }

  exitExam() {
    if (this.timerInterval) clearInterval(this.timerInterval);
    document.getElementById('exam-runner-view').classList.remove('active');
    this.activeQuiz = null;
    this.renderStudentView();
    try {
      if (document.exitFullscreen) document.exitFullscreen().catch(() => {});
    } catch (e) {}
  }

  // --- ADMIN CONTROL CENTER ("Admin and Admin123 use login admin panel show user and exam show how many use pass") ---
  renderAdminView() {
    // 1. Calculate Pass/Fail Stats
    const totalUsers = this.users.length;
    const totalAttempts = this.attempts.length;
    const passedAttempts = this.attempts.filter(a => a.passed).length;
    const failedAttempts = totalAttempts - passedAttempts;
    const passRate = totalAttempts > 0 ? Math.round((passedAttempts / totalAttempts) * 100) : 0;

    document.getElementById('stat-total-users').textContent = totalUsers;
    document.getElementById('stat-total-attempts').textContent = totalAttempts;
    document.getElementById('stat-passed-users').textContent = `${passedAttempts} Passed`;
    document.getElementById('stat-failed-users').textContent = `${failedAttempts} Failed`;
    document.getElementById('stat-pass-rate').textContent = `${passRate}%`;

    // 2. Render Attempts Table ("show how many use pass")
    const attemptsTbody = document.getElementById('admin-attempts-tbody');
    attemptsTbody.innerHTML = '';

    if (this.attempts.length === 0) {
      attemptsTbody.innerHTML = `<tr><td colspan="7" style="text-align:center; padding:24px; color:#94a3b8;">No exam submissions recorded yet.</td></tr>`;
    } else {
      this.attempts.forEach(att => {
        const tr = document.createElement('tr');
        const badge = att.passed
          ? `<span class="badge badge-passed">PASSED (${att.percentage}%) ✅</span>`
          : `<span class="badge badge-failed">FAILED (${att.percentage}%) ❌</span>`;

        tr.innerHTML = `
          <td style="font-weight:700; color:white;">${att.studentName}</td>
          <td style="color:#a5b4fc; font-family:monospace;">${att.studentPhone || 'N/A'}</td>
          <td style="font-weight:600;">${att.quizTitle}</td>
          <td style="font-weight:700;">${att.score} / ${att.totalPoints} pts</td>
          <td>${att.passingScorePercent || 70}%</td>
          <td>${badge}</td>
          <td style="color:#94a3b8;">${new Date(att.timestamp).toLocaleString()}</td>
        `;
        attemptsTbody.appendChild(tr);
      });
    }

    // 3. Render Registered Users Table ("show user")
    const usersTbody = document.getElementById('admin-users-tbody');
    usersTbody.innerHTML = '';

    this.users.forEach(u => {
      const tr = document.createElement('tr');
      const passRatio = u.examsTaken > 0 ? `${u.examsPassed || 0} / ${u.examsTaken}` : '0';
      const statusBadge = (u.examsPassed > 0)
        ? `<span class="badge badge-passed">Passed Exam(s) ✅</span>`
        : (u.examsTaken > 0 ? `<span class="badge badge-failed">Needs Retest ❌</span>` : `<span class="badge" style="background:#334155; color:#cbd5e1;">New Student</span>`);

      tr.innerHTML = `
        <td style="font-weight:700; color:white;">${u.name}</td>
        <td style="color:#a5b4fc; font-family:monospace;">${u.phone}</td>
        <td style="color:#94a3b8;">${new Date(u.registeredAt).toLocaleDateString()}</td>
        <td style="font-weight:600;">${u.examsTaken || 0}</td>
        <td style="font-weight:600; color:#34d399;">${u.examsPassed || 0}</td>
        <td>${statusBadge}</td>
      `;
      usersTbody.appendChild(tr);
    });

    // 4. Populate Quiz Selector for Questions Studio
    const quizSel = document.getElementById('admin-quiz-selector');
    quizSel.innerHTML = '';
    this.quizzes.forEach(q => {
      const opt = document.createElement('option');
      opt.value = q.id;
      opt.textContent = `${q.title} (${q.questions.length} Questions)`;
      quizSel.appendChild(opt);
    });

    this.renderAdminQuestions();
  }

  setAdminTab(tab) {
    const usersBtn = document.getElementById('admin-tab-users-btn');
    const questionsBtn = document.getElementById('admin-tab-questions-btn');
    const usersContent = document.getElementById('admin-tab-users-content');
    const questionsContent = document.getElementById('admin-tab-questions-content');

    if (tab === 'users') {
      usersBtn.classList.add('active');
      questionsBtn.classList.remove('active');
      usersContent.style.display = 'block';
      questionsContent.style.display = 'none';
    } else {
      questionsBtn.classList.add('active');
      usersBtn.classList.remove('active');
      questionsContent.style.display = 'block';
      usersContent.style.display = 'none';
      this.renderAdminQuestions();
    }
  }

  renderAdminQuestions() {
    const quizId = document.getElementById('admin-quiz-selector').value;
    const quiz = this.quizzes.find(q => q.id === quizId);
    const container = document.getElementById('admin-questions-list');
    container.innerHTML = '';

    if (!quiz || !quiz.questions || quiz.questions.length === 0) {
      container.innerHTML = `<div style="text-align:center; padding:30px; color:#94a3b8; background:var(--card-dark); border-radius:12px;">No questions in this exam yet. Click "Add New Question" to create one!</div>`;
      return;
    }

    quiz.questions.forEach((q, qIdx) => {
      const card = document.createElement('div');
      card.className = 'question-card';
      card.style.marginBottom = '16px';
      card.style.padding = '20px';

      const correctIndex = (q.correctAnswers && q.correctAnswers.length > 0) ? q.correctAnswers[0] : 0;

      let optionsHtml = '';
      q.options.forEach((optText, optIdx) => {
        const isRight = (optIdx === correctIndex);
        optionsHtml += `
          <div onclick="app.setRightAnswer('${quiz.id}', '${q.id}', ${optIdx})"
               style="display:flex; align-items:center; justify-content:space-between; padding:12px 16px; margin-bottom:8px; border-radius:8px; cursor:pointer; transition:all 0.2s;
                      background: ${isRight ? 'rgba(16, 185, 129, 0.15)' : '#0b0f19'};
                      border: 1.5px solid ${isRight ? 'var(--success)' : '#1e293b'};
                      box-shadow: ${isRight ? '0 0 12px rgba(16, 185, 129, 0.2)' : 'none'};">
            <div style="display:flex; align-items:center; gap:12px;">
              <span style="font-weight:700; width:22px; height:22px; border-radius:50%; display:inline-flex; align-items:center; justify-content:center;
                           background:${isRight ? 'var(--success)' : '#1e293b'}; color:${isRight ? 'white' : '#94a3b8'}; font-size:11px;">
                ${String.fromCharCode(65 + optIdx)}
              </span>
              <span style="font-size:14px; color:${isRight ? '#34d399' : '#cbd5e1'}; font-weight:${isRight ? '700' : '400'};">
                ${optText}
              </span>
            </div>
            <div>
              ${isRight
                ? `<span class="badge badge-passed">✓ RIGHT ANSWER</span>`
                : `<span style="font-size:11px; color:#64748b;">Click to set as right</span>`}
            </div>
          </div>
        `;
      });

      card.innerHTML = `
        <div style="display:flex; justify-content:space-between; align-items:flex-start; margin-bottom:12px;">
          <div>
            <span style="font-size:12px; font-weight:700; color:var(--primary);">Question ${qIdx + 1} (${q.points || 10} Points)</span>
            <h4 style="font-size:16px; font-weight:700; color:white; margin-top:4px;">${q.text}</h4>
          </div>
          <div style="display:flex; gap:8px;">
            <button onclick="app.editQuestion('${quiz.id}', '${q.id}')" class="btn btn-outline btn-sm">Edit</button>
            <button onclick="app.deleteQuestion('${quiz.id}', '${q.id}')" class="btn btn-danger btn-sm">Delete</button>
          </div>
        </div>

        <div style="margin-top:12px;">
          ${optionsHtml}
        </div>

        ${q.explanation ? `<div style="font-size:12px; color:#94a3b8; margin-top:8px; font-style:italic;">💡 <strong>Teacher Explanation:</strong> ${q.explanation}</div>` : ''}
      `;
      container.appendChild(card);
    });
  }

  // --- 1-CLICK RIGHT ANSWER TOGGLE ---
  setRightAnswer(quizId, questionId, optionIndex) {
    const quiz = this.quizzes.find(q => q.id === quizId);
    if (!quiz) return;
    const question = quiz.questions.find(q => q.id === questionId);
    if (!question) return;

    question.correctAnswers = [optionIndex];
    this.saveQuizzesLocal();

    // Sync to backend if online
    fetch(`/api/quizzes/${quizId}/questions/${questionId}/right-answer`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ rightAnswerIndex: optionIndex })
    }).catch(() => {});

    this.showToast(`✅ Right answer updated to Option ${String.fromCharCode(65 + optionIndex)}!`);
    this.renderAdminQuestions();
  }

  // --- ADD / EDIT QUESTION MODAL ---
  showAddQuestionModal() {
    this.editingQuestionQuizId = null;
    this.editingQuestionId = null;
    document.getElementById('modal-q-heading').textContent = 'Add New Question';
    document.getElementById('q-edit-text').value = '';
    document.getElementById('q-edit-points').value = 10;
    document.getElementById('q-edit-explanation').value = '';

    // Populate target quiz dropdown
    const sel = document.getElementById('q-edit-target-quiz');
    sel.innerHTML = '';
    this.quizzes.forEach(q => {
      const opt = document.createElement('option');
      opt.value = q.id;
      opt.textContent = q.title;
      sel.appendChild(opt);
    });

    // Populate 4 blank option rows with first selected as right answer
    const container = document.getElementById('modal-options-builder');
    container.innerHTML = '';
    for (let i = 0; i < 4; i++) {
      this.createOptionRow(container, i, '', i === 0);
    }

    document.getElementById('question-modal').classList.add('active');
  }

  editQuestion(quizId, questionId) {
    const quiz = this.quizzes.find(q => q.id === quizId);
    if (!quiz) return;
    const q = quiz.questions.find(item => item.id === questionId);
    if (!q) return;

    this.editingQuestionQuizId = quizId;
    this.editingQuestionId = questionId;

    document.getElementById('modal-q-heading').textContent = 'Edit Question';
    document.getElementById('q-edit-text').value = q.text;
    document.getElementById('q-edit-points').value = q.points || 10;
    document.getElementById('q-edit-explanation').value = q.explanation || '';

    const sel = document.getElementById('q-edit-target-quiz');
    sel.innerHTML = '';
    const opt = document.createElement('option');
    opt.value = quiz.id;
    opt.textContent = quiz.title;
    sel.appendChild(opt);

    const container = document.getElementById('modal-options-builder');
    container.innerHTML = '';
    const correctIdx = (q.correctAnswers && q.correctAnswers.length > 0) ? q.correctAnswers[0] : 0;

    q.options.forEach((optText, idx) => {
      this.createOptionRow(container, idx, optText, idx === correctIdx);
    });

    document.getElementById('question-modal').classList.add('active');
  }

  createOptionRow(container, idx, value, isChecked) {
    const row = document.createElement('div');
    row.className = 'modal-option-row';
    row.style.display = 'flex';
    row.style.alignItems = 'center';
    row.style.gap = '10px';

    row.innerHTML = `
      <input type="radio" name="modal-right-opt" value="${idx}" ${isChecked ? 'checked' : ''} title="Mark as Right Answer" style="width:18px; height:18px; accent-color:var(--success);">
      <input type="text" class="form-control modal-opt-input" placeholder="Option ${String.fromCharCode(65 + idx)} text" value="${value.replace(/"/g, '&quot;')}" required>
      <button type="button" onclick="this.parentElement.remove()" class="btn btn-danger btn-sm" style="padding:6px 10px;" title="Remove option">✕</button>
    `;
    container.appendChild(row);
  }

  addOptionRow() {
    const container = document.getElementById('modal-options-builder');
    const nextIdx = container.children.length;
    this.createOptionRow(container, nextIdx, '', false);
  }

  closeQuestionModal() {
    document.getElementById('question-modal').classList.remove('active');
  }

  saveQuestion(e) {
    e.preventDefault();
    const text = document.getElementById('q-edit-text').value.trim();
    const points = parseInt(document.getElementById('q-edit-points').value, 10) || 10;
    const explanation = document.getElementById('q-edit-explanation').value.trim();
    const targetQuizId = document.getElementById('q-edit-target-quiz').value;

    const optInputs = document.querySelectorAll('.modal-opt-input');
    const options = Array.from(optInputs).map(inp => inp.value.trim()).filter(v => v.length > 0);

    if (options.length < 2) {
      alert('Please provide at least 2 options.');
      return;
    }

    const checkedRadio = document.querySelector('input[name="modal-right-opt"]:checked');
    const correctIndex = checkedRadio ? parseInt(checkedRadio.value, 10) : 0;

    const quiz = this.quizzes.find(q => q.id === targetQuizId);
    if (!quiz) return;

    if (this.editingQuestionId) {
      // Update existing
      const q = quiz.questions.find(item => item.id === this.editingQuestionId);
      if (q) {
        q.text = text;
        q.points = points;
        q.options = options;
        q.correctAnswers = [correctIndex < options.length ? correctIndex : 0];
        q.explanation = explanation;
      }
      this.showToast('Question updated successfully!');
    } else {
      // Add new
      const newQ = {
        id: 'q-' + Date.now(),
        text: text,
        type: 'SINGLE_CHOICE',
        points: points,
        options: options,
        correctAnswers: [correctIndex < options.length ? correctIndex : 0],
        explanation: explanation
      };
      quiz.questions.push(newQ);
      this.showToast('New question created!');
    }

    this.saveQuizzesLocal();
    this.closeQuestionModal();
    this.renderAdminQuestions();
  }

  deleteQuestion(quizId, questionId) {
    if (!confirm('Are you sure you want to delete this question?')) return;
    const quiz = this.quizzes.find(q => q.id === quizId);
    if (!quiz) return;
    quiz.questions = quiz.questions.filter(q => q.id !== questionId);
    this.saveQuizzesLocal();
    this.showToast('Question removed.');
    this.renderAdminQuestions();
  }

  previewStudentView() {
    this.showView('student-view');
    this.renderStudentView();
    this.showToast('Viewing Student Portal');
  }

  // --- UI TOAST HELPER ---
  showToast(msg) {
    if (window.AndroidApp && typeof window.AndroidApp.showToast === 'function') {
      window.AndroidApp.showToast(msg);
    }
    const toast = document.createElement('div');
    toast.style.position = 'fixed';
    toast.style.bottom = '24px';
    toast.style.right = '24px';
    toast.style.background = '#1e293b';
    toast.style.color = '#f8fafc';
    toast.style.border = '1px solid var(--primary)';
    toast.style.padding = '12px 20px';
    toast.style.borderRadius = '10px';
    toast.style.fontSize = '14px';
    toast.style.fontWeight = '600';
    toast.style.boxShadow = '0 10px 25px rgba(0,0,0,0.5)';
    toast.style.zIndex = '99999';
    toast.style.transition = 'all 0.3s ease';
    toast.textContent = msg;

    document.body.appendChild(toast);
    setTimeout(() => {
      toast.style.opacity = '0';
      toast.style.transform = 'translateY(10px)';
      setTimeout(() => toast.remove(), 300);
    }, 2800);
  }
}

// Global App Instance
const app = new AppManager();
