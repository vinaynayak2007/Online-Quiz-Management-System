/**
 * QuizPulse - Online Quiz Management System
 * Client Application Logic (Java 21 Spring Boot + Firebase)
 */

class QuizApp {
  constructor() {
    this.apiBase = '/api';
    this.quizzes = [];
    this.currentQuiz = null;
    this.currentQuestionIndex = 0;
    this.studentAnswers = {}; // { questionId: [selectedIndices] }
    this.flaggedQuestions = new Set();
    this.timerInterval = null;
    this.timeRemaining = 0; // seconds
    this.totalTimeSpent = 0;
    this.currentStudent = { name: 'Vinay Nayak', email: '' };
    this.lastResult = null;
    this.soundEnabled = true;
    this.audioCtx = null;

    // Admin state
    this.isAdminAuthenticated = false;
    this.editorQuestions = [];

    this.init();
  }

  async init() {
    this.initAudio();
    this.bindEvents();
    await this.checkFirebaseStatus();
    await this.loadQuizzes();
    await this.loadAnalytics();

    // Support URL routing via query parameters: ?tab=admin or ?take=quiz-id
    const params = new URLSearchParams(window.location.search);
    const tabParam = params.get('tab');
    const takeParam = params.get('take');

    if (takeParam) {
      this.pendingQuizId = takeParam;
      this.currentStudent = { name: "Vinay Nayak", email: "vinay@example.com" };
      await this.launchQuizPlayer();
      this.selectOption(1);
    } else if (tabParam) {
      this.showTab(tabParam);
    }
  }

  // --- AUDIO SYNTHESIS (Web Audio API) ---
  initAudio() {
    try {
      const AudioContext = window.AudioContext || window.webkitAudioContext;
      if (AudioContext) {
        this.audioCtx = new AudioContext();
      }
    } catch (e) {
      console.warn('Web Audio not supported');
    }
  }

  playSound(type) {
    if (!this.soundEnabled || !this.audioCtx) return;
    if (this.audioCtx.state === 'suspended') {
      this.audioCtx.resume();
    }

    const ctx = this.audioCtx;
    const osc = ctx.createOscillator();
    const gain = ctx.createGain();
    osc.connect(gain);
    gain.connect(ctx.destination);

    const now = ctx.currentTime;

    if (type === 'click') {
      osc.frequency.setValueAtTime(440, now);
      gain.gain.setValueAtTime(0.05, now);
      gain.gain.exponentialRampToValueAtTime(0.001, now + 0.05);
      osc.start(now);
      osc.stop(now + 0.05);
    } else if (type === 'success') {
      // Major chord arpeggio
      osc.type = 'triangle';
      osc.frequency.setValueAtTime(523.25, now); // C5
      osc.frequency.setValueAtTime(659.25, now + 0.1); // E5
      osc.frequency.setValueAtTime(783.99, now + 0.2); // G5
      gain.gain.setValueAtTime(0.1, now);
      gain.gain.exponentialRampToValueAtTime(0.001, now + 0.5);
      osc.start(now);
      osc.stop(now + 0.5);
    } else if (type === 'fail') {
      osc.type = 'sawtooth';
      osc.frequency.setValueAtTime(260, now);
      osc.frequency.linearRampToValueAtTime(160, now + 0.3);
      gain.gain.setValueAtTime(0.1, now);
      gain.gain.exponentialRampToValueAtTime(0.001, now + 0.35);
      osc.start(now);
      osc.stop(now + 0.35);
    } else if (type === 'warning') {
      osc.type = 'sine';
      osc.frequency.setValueAtTime(880, now);
      gain.gain.setValueAtTime(0.15, now);
      gain.gain.exponentialRampToValueAtTime(0.001, now + 0.1);
      osc.start(now);
      osc.stop(now + 0.1);
    }
  }

  toggleSound() {
    this.soundEnabled = !this.soundEnabled;
    const onIcon = document.getElementById('sound-icon-on');
    const offIcon = document.getElementById('sound-icon-off');
    if (this.soundEnabled) {
      onIcon.classList.remove('hidden');
      offIcon.classList.add('hidden');
      this.showToast('Sound effects enabled', '🔊');
    } else {
      onIcon.classList.add('hidden');
      offIcon.classList.remove('hidden');
      this.showToast('Sound muted', '🔇');
    }
  }

  // --- TAB ROUTING ---
  showTab(tabName) {
    const views = ['browse', 'runner', 'result', 'leaderboard', 'history', 'admin'];
    views.forEach(v => {
      const el = document.getElementById(`view-${v}`);
      if (el) el.classList.add('hidden');
      const navBtn = document.getElementById(`nav-${v}`);
      if (navBtn) navBtn.classList.remove('active');
    });

    const activeView = document.getElementById(`view-${tabName}`);
    if (activeView) activeView.classList.remove('hidden');

    const activeNav = document.getElementById(`nav-${tabName}`);
    if (activeNav) activeNav.classList.add('active');

    window.scrollTo({ top: 0, behavior: 'smooth' });

    if (tabName === 'browse') {
      this.loadQuizzes();
      this.loadAnalytics();
    } else if (tabName === 'leaderboard') {
      this.loadLeaderboard();
    } else if (tabName === 'history') {
      this.loadHistory();
    } else if (tabName === 'admin') {
      this.loadAdminQuizzes();
    }
  }

  // --- FIREBASE STATUS ---
  async checkFirebaseStatus() {
    try {
      const res = await fetch(`${this.apiBase}/firebase/status`);
      if (res.ok) {
        const data = await res.json();
        const pillText = document.getElementById('firebase-pill-text');
        const pill = document.getElementById('firebase-status-pill');

        if (data.connected) {
          pillText.textContent = `Firebase: ${data.projectId}`;
          pill.className = 'flex items-center gap-2 px-3 py-1.5 rounded-full text-xs font-semibold bg-emerald-950/60 border border-emerald-500/30 text-emerald-300 hover:bg-emerald-900/40 transition';
        } else {
          pillText.textContent = 'Firebase: Local Emulated';
          pill.className = 'flex items-center gap-2 px-3 py-1.5 rounded-full text-xs font-semibold bg-indigo-950/60 border border-indigo-500/30 text-indigo-300 hover:bg-indigo-900/40 transition';
        }

        // Update modal info
        document.getElementById('fb-modal-mode').textContent = data.mode;
        document.getElementById('fb-modal-db').textContent = data.database;
        document.getElementById('fb-modal-project').textContent = data.projectId;
        document.getElementById('fb-modal-message').textContent = data.message;
      }
    } catch (e) {
      console.warn('Could not query Firebase status:', e);
    }
  }

  openFirebaseModal() {
    document.getElementById('modal-firebase').classList.remove('hidden');
  }

  closeFirebaseModal() {
    document.getElementById('modal-firebase').classList.add('hidden');
  }

  async submitFirebaseConfig() {
    const credInput = document.getElementById('firebase-credentials-input').value.trim();
    if (!credInput) {
      this.showToast('Please paste your Firebase service account JSON', '⚠️');
      return;
    }

    try {
      const res = await fetch(`${this.apiBase}/firebase/configure`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ credentials: credInput })
      });
      const data = await res.json();
      if (res.ok && data.success) {
        this.showToast('Firebase configured successfully!', '🔥');
        this.closeFirebaseModal();
        await this.checkFirebaseStatus();
        await this.loadQuizzes();
      } else {
        alert(data.error || 'Failed to initialize Firebase credentials');
      }
    } catch (e) {
      alert('Error sending credentials: ' + e.message);
    }
  }

  // --- DATA LOADING & CATALOG ---
  async loadQuizzes() {
    try {
      const res = await fetch(`${this.apiBase}/quizzes`);
      if (res.ok) {
        this.quizzes = await res.json();
        this.populateCategories();
        this.renderQuizzesGrid(this.quizzes);
      }
    } catch (e) {
      console.error('Failed to load quizzes:', e);
    }
  }

  async loadAnalytics() {
    try {
      const res = await fetch(`${this.apiBase}/analytics`);
      if (res.ok) {
        const data = await res.json();
        document.getElementById('stat-total-quizzes').textContent = data.totalQuizzes || 0;
        document.getElementById('stat-total-questions').textContent = data.totalQuestions || 0;
        document.getElementById('stat-total-attempts').textContent = data.totalAttempts || 0;
        document.getElementById('stat-avg-score').textContent = (data.averageScore || 0) + '%';

        // Admin stats
        document.getElementById('admin-stat-quizzes').textContent = data.totalQuizzes || 0;
        document.getElementById('admin-stat-questions').textContent = data.totalQuestions || 0;
        document.getElementById('admin-stat-attempts').textContent = data.totalAttempts || 0;
        document.getElementById('admin-stat-passrate').textContent = (data.passRate || 0) + '%';
      }
    } catch (e) {
      console.error('Failed to load analytics:', e);
    }
  }

  populateCategories() {
    const categories = new Set(this.quizzes.map(q => q.category).filter(Boolean));
    const catSelect = document.getElementById('quiz-category-filter');
    const currentVal = catSelect.value;
    catSelect.innerHTML = '<option value="all">All Categories</option>';
    categories.forEach(cat => {
      const opt = document.createElement('option');
      opt.value = cat;
      opt.textContent = cat;
      catSelect.appendChild(opt);
    });
    catSelect.value = currentVal;
  }

  filterQuizzes() {
    const query = (document.getElementById('quiz-search-input').value || '').toLowerCase();
    const category = document.getElementById('quiz-category-filter').value;
    const difficulty = document.getElementById('quiz-difficulty-filter').value;

    const filtered = this.quizzes.filter(q => {
      const matchesQuery = !query ||
        q.title.toLowerCase().includes(query) ||
        (q.description && q.description.toLowerCase().includes(query)) ||
        (q.category && q.category.toLowerCase().includes(query)) ||
        (q.tags && q.tags.some(t => t.toLowerCase().includes(query)));

      const matchesCat = category === 'all' || (q.category && q.category.toLowerCase() === category.toLowerCase());
      const matchesDiff = difficulty === 'all' || (q.difficulty && q.difficulty.toLowerCase() === difficulty.toLowerCase());

      return matchesQuery && matchesCat && matchesDiff;
    });

    this.renderQuizzesGrid(filtered);
  }

  renderQuizzesGrid(list) {
    const container = document.getElementById('quizzes-grid');
    const emptyNotice = document.getElementById('no-quizzes-found');

    if (!list || list.length === 0) {
      container.innerHTML = '';
      emptyNotice.classList.remove('hidden');
      return;
    }

    emptyNotice.classList.add('hidden');
    container.innerHTML = list.map(q => {
      const diffColor = q.difficulty === 'Beginner' ? 'text-emerald-400 bg-emerald-500/10 border-emerald-500/20' :
                        q.difficulty === 'Advanced' ? 'text-rose-400 bg-rose-500/10 border-rose-500/20' :
                        'text-amber-400 bg-amber-500/10 border-amber-500/20';

      const tagHtml = (q.tags || []).slice(0, 3).map(t =>
        `<span class="text-[11px] px-2 py-0.5 rounded-md bg-slate-800 text-slate-300 border border-slate-700">#${t}</span>`
      ).join('');

      return `
        <div class="group bg-slate-800/80 hover:bg-slate-800 rounded-3xl border border-slate-700/80 hover:border-indigo-500/50 p-6 flex flex-col justify-between transition-all duration-200 shadow-xl hover:shadow-indigo-500/10">
          <div class="space-y-4">
            <div class="flex items-center justify-between gap-2">
              <span class="text-xs font-semibold px-2.5 py-0.5 rounded-full bg-indigo-500/20 text-indigo-300 border border-indigo-500/30">
                ${q.category || 'General'}
              </span>
              <span class="text-xs font-semibold px-2.5 py-0.5 rounded-full border ${diffColor}">
                ${q.difficulty || 'Intermediate'}
              </span>
            </div>

            <h3 class="text-xl font-bold text-white group-hover:text-indigo-300 transition line-clamp-1">
              ${q.title}
            </h3>

            <p class="text-sm text-slate-400 line-clamp-2 leading-relaxed">
              ${q.description || 'Interactive test designed to assess fundamental and advanced core competencies.'}
            </p>

            <div class="flex flex-wrap gap-1.5 pt-1">
              ${tagHtml}
            </div>
          </div>

          <div class="pt-6 mt-6 border-t border-slate-700/60 flex items-center justify-between">
            <div class="flex items-center gap-4 text-xs text-slate-400">
              <span class="flex items-center gap-1.5 font-medium">
                <svg class="w-4 h-4 text-indigo-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8.228 9c.549-1.165 2.03-2 3.772-2 2.21 0 4 1.343 4 3 0 1.4-1.278 2.575-3.006 2.907-.542.104-.994.54-.994 1.093m0 3h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                ${q.totalQuestions || (q.questions ? q.questions.length : 0)} Questions
              </span>
              <span class="flex items-center gap-1.5 font-medium">
                <svg class="w-4 h-4 text-indigo-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                ${q.durationMinutes || 10} Mins
              </span>
            </div>

            <button onclick="app.promptStudentInfo('${q.id}')" class="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white rounded-xl text-xs font-bold shadow-md shadow-indigo-600/30 transition flex items-center gap-1.5">
              <span>Start</span>
              <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path></svg>
            </button>
          </div>
        </div>
      `;
    }).join('');
  }

  startRandomQuiz() {
    if (this.quizzes.length === 0) return;
    const randomIndex = Math.floor(Math.random() * this.quizzes.length);
    this.promptStudentInfo(this.quizzes[randomIndex].id);
  }

  // --- STUDENT PROMPT & QUIZ INITIALIZATION ---
  promptStudentInfo(quizId) {
    const quiz = this.quizzes.find(q => q.id === quizId);
    if (!quiz) return;

    this.pendingQuizId = quizId;
    document.getElementById('modal-student-info').classList.remove('hidden');
    document.getElementById('student-name-input').focus();
  }

  closeStudentModal() {
    document.getElementById('modal-student-info').classList.add('hidden');
    this.pendingQuizId = null;
  }

  async launchQuizPlayer() {
    const nameInput = document.getElementById('student-name-input').value.trim();
    const emailInput = document.getElementById('student-email-input').value.trim();

    if (!nameInput) {
      alert('Please enter your full name to proceed with the assessment.');
      return;
    }

    this.currentStudent = { name: nameInput, email: emailInput };
    this.closeStudentModal();

    try {
      const res = await fetch(`${this.apiBase}/quizzes/${this.pendingQuizId}`);
      if (!res.ok) throw new Error('Could not fetch quiz details');

      this.currentQuiz = await res.json();
      this.currentQuestionIndex = 0;
      this.studentAnswers = {};
      this.flaggedQuestions.clear();
      this.timeRemaining = (this.currentQuiz.durationMinutes || 10) * 60;
      this.totalTimeSpent = 0;

      this.showTab('runner');
      this.startTimer();
      this.renderCurrentQuestion();
      this.renderQuestionPalette();
      this.playSound('click');
    } catch (e) {
      alert('Error launching quiz: ' + e.message);
    }
  }

  // --- TIMER ENGINE ---
  startTimer() {
    if (this.timerInterval) clearInterval(this.timerInterval);

    this.updateTimerDisplay();
    this.timerInterval = setInterval(() => {
      this.timeRemaining--;
      this.totalTimeSpent++;
      this.updateTimerDisplay();

      if (this.timeRemaining === 60) {
        this.playSound('warning');
        this.showToast('1 minute remaining! Review your answers.', '⚠️');
      }

      if (this.timeRemaining <= 0) {
        clearInterval(this.timerInterval);
        this.showToast('Time has expired! Submitting assessment...', '⏰');
        this.submitQuiz();
      }
    }, 1000);
  }

  updateTimerDisplay() {
    const m = Math.floor(Math.max(0, this.timeRemaining) / 60);
    const s = Math.max(0, this.timeRemaining) % 60;
    const formatted = `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`;

    const textEl = document.getElementById('player-timer-text');
    const container = document.getElementById('player-timer-container');
    if (textEl) textEl.textContent = formatted;

    if (this.timeRemaining < 60) {
      container.className = 'flex items-center gap-2 bg-rose-950/80 border border-rose-500/60 px-4 py-2 rounded-xl text-rose-300 animate-pulse';
    } else {
      container.className = 'flex items-center gap-2 bg-slate-900 border border-slate-700 px-4 py-2 rounded-xl text-slate-100';
    }
  }

  // --- QUESTION RENDERING & SELECTION ---
  renderCurrentQuestion() {
    if (!this.currentQuiz || !this.currentQuiz.questions || this.currentQuiz.questions.length === 0) return;

    const questions = this.currentQuiz.questions;
    const q = questions[this.currentQuestionIndex];

    // Header info
    document.getElementById('player-quiz-title').textContent = this.currentQuiz.title;
    document.getElementById('player-category-badge').textContent = this.currentQuiz.category || 'General';
    document.getElementById('player-difficulty-badge').textContent = this.currentQuiz.difficulty || 'Intermediate';

    document.getElementById('player-question-counter').textContent = `Question ${this.currentQuestionIndex + 1} of ${questions.length}`;
    document.getElementById('player-question-points').textContent = `${q.points || 1} ${q.points === 1 ? 'pt' : 'pts'}`;
    document.getElementById('player-question-text').textContent = q.text;

    // Linear progress bar
    const progress = Math.round(((this.currentQuestionIndex + 1) / questions.length) * 100);
    document.getElementById('player-progress-bar').style.width = `${progress}%`;

    // Flag button state
    const flagBtn = document.getElementById('flag-btn');
    if (this.flaggedQuestions.has(q.id)) {
      flagBtn.className = 'p-2.5 rounded-xl bg-amber-500/20 border border-amber-500 text-amber-400 transition';
    } else {
      flagBtn.className = 'p-2.5 rounded-xl bg-slate-900 border border-slate-700 text-slate-400 hover:text-amber-400 hover:border-amber-500/40 transition';
    }

    // Render options
    const optionsContainer = document.getElementById('player-options-list');
    const selectedList = this.studentAnswers[q.id] || [];
    const isMulti = q.type === 'MULTIPLE_CHOICE';

    optionsContainer.innerHTML = (q.options || []).map((opt, idx) => {
      const isSelected = selectedList.includes(idx);
      const letter = String.fromCharCode(65 + idx);

      return `
        <div onclick="app.selectOption(${idx})" class="quiz-option-card flex items-center justify-between p-4 rounded-2xl ${isSelected ? 'selected' : ''}">
          <div class="flex items-center gap-3">
            <span class="w-8 h-8 rounded-xl flex items-center justify-center font-bold text-xs ${isSelected ? 'bg-indigo-600 text-white' : 'bg-slate-800 text-slate-400 border border-slate-700'}">
              ${letter}
            </span>
            <span class="text-sm font-medium ${isSelected ? 'text-white' : 'text-slate-200'}">${opt}</span>
          </div>
          <div class="w-5 h-5 rounded-${isMulti ? 'md' : 'full'} border flex items-center justify-center ${isSelected ? 'bg-indigo-600 border-indigo-500 text-white' : 'border-slate-600'}">
            ${isSelected ? (isMulti ? '✓' : '●') : ''}
          </div>
        </div>
      `;
    }).join('');

    // Previous / Next / Submit buttons
    const prevBtn = document.getElementById('player-prev-btn');
    const nextBtn = document.getElementById('player-next-btn');
    const submitBtn = document.getElementById('player-submit-btn');

    prevBtn.disabled = this.currentQuestionIndex === 0;

    if (this.currentQuestionIndex === questions.length - 1) {
      nextBtn.classList.add('hidden');
      submitBtn.classList.remove('hidden');
    } else {
      nextBtn.classList.remove('hidden');
      submitBtn.classList.add('hidden');
    }

    this.renderQuestionPalette();
  }

  selectOption(optionIndex) {
    const q = this.currentQuiz.questions[this.currentQuestionIndex];
    const isMulti = q.type === 'MULTIPLE_CHOICE';

    let selected = this.studentAnswers[q.id] ? [...this.studentAnswers[q.id]] : [];

    if (isMulti) {
      if (selected.includes(optionIndex)) {
        selected = selected.filter(i => i !== optionIndex);
      } else {
        selected.push(optionIndex);
      }
    } else {
      selected = [optionIndex];
    }

    this.studentAnswers[q.id] = selected;
    this.playSound('click');
    this.renderCurrentQuestion();
  }

  clearSelection() {
    const q = this.currentQuiz.questions[this.currentQuestionIndex];
    delete this.studentAnswers[q.id];
    this.renderCurrentQuestion();
  }

  toggleFlagCurrentQuestion() {
    const q = this.currentQuiz.questions[this.currentQuestionIndex];
    if (this.flaggedQuestions.has(q.id)) {
      this.flaggedQuestions.delete(q.id);
    } else {
      this.flaggedQuestions.add(q.id);
      this.playSound('warning');
    }
    this.renderCurrentQuestion();
  }

  nextQuestion() {
    if (this.currentQuestionIndex < this.currentQuiz.questions.length - 1) {
      this.currentQuestionIndex++;
      this.playSound('click');
      this.renderCurrentQuestion();
    }
  }

  prevQuestion() {
    if (this.currentQuestionIndex > 0) {
      this.currentQuestionIndex--;
      this.playSound('click');
      this.renderCurrentQuestion();
    }
  }

  jumpToQuestion(index) {
    if (index >= 0 && index < this.currentQuiz.questions.length) {
      this.currentQuestionIndex = index;
      this.playSound('click');
      this.renderCurrentQuestion();
    }
  }

  renderQuestionPalette() {
    const paletteGrid = document.getElementById('player-palette-grid');
    if (!paletteGrid || !this.currentQuiz || !this.currentQuiz.questions) return;

    paletteGrid.innerHTML = this.currentQuiz.questions.map((q, idx) => {
      const isAnswered = this.studentAnswers[q.id] && this.studentAnswers[q.id].length > 0;
      const isFlagged = this.flaggedQuestions.has(q.id);
      const isCurrent = idx === this.currentQuestionIndex;

      let cls = 'palette-btn ';
      if (isFlagged) {
        cls += 'flagged ';
      } else if (isAnswered) {
        cls += 'answered ';
      } else {
        cls += 'unanswered ';
      }

      if (isCurrent) {
        cls += 'current ';
      }

      return `<button onclick="app.jumpToQuestion(${idx})" class="${cls}">${idx + 1}</button>`;
    }).join('');
  }

  confirmSubmitQuiz() {
    const questions = this.currentQuiz.questions;
    const answeredCount = Object.keys(this.studentAnswers).filter(k => this.studentAnswers[k].length > 0).length;
    const unansweredCount = questions.length - answeredCount;

    if (unansweredCount > 0) {
      if (!confirm(`You have ${unansweredCount} unanswered question(s). Are you sure you want to finish and submit?`)) {
        return;
      }
    }
    this.submitQuiz();
  }

  async submitQuiz() {
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
      this.timerInterval = null;
    }

    const payload = {
      studentName: this.currentStudent.name,
      studentEmail: this.currentStudent.email,
      timeSpentSeconds: this.totalTimeSpent,
      answers: Object.keys(this.studentAnswers).map(qid => ({
        questionId: qid,
        selectedOptions: this.studentAnswers[qid]
      }))
    };

    try {
      const res = await fetch(`${this.apiBase}/quizzes/${this.currentQuiz.id}/submit`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      if (!res.ok) throw new Error('Failed to evaluate quiz');

      const result = await res.json();
      this.lastResult = result;
      this.renderResult(result);
      this.showTab('result');

      if (result.passed) {
        this.playSound('success');
      } else {
        this.playSound('fail');
      }

      this.saveLocalHistory(result);
    } catch (e) {
      alert('Error submitting quiz: ' + e.message);
    }
  }

  // --- RESULT VIEW & REVIEW ---
  renderResult(res) {
    const gradeCircle = document.getElementById('result-grade-circle');
    gradeCircle.textContent = res.grade;
    gradeCircle.className = `inline-flex items-center justify-center w-24 h-24 rounded-full border-4 text-4xl font-black mx-auto mb-2 ${res.passed ? 'grade-circle-pass' : 'grade-circle-fail'}`;

    const titleEl = document.getElementById('result-status-title');
    const subtitleEl = document.getElementById('result-status-subtitle');

    if (res.passed) {
      titleEl.textContent = 'Congratulations! You Passed!';
      subtitleEl.textContent = `Excellent performance! You met the passing threshold of ${res.passingScorePercent}%.`;
    } else {
      titleEl.textContent = 'Assessment Incomplete / Not Passed';
      subtitleEl.textContent = `You scored ${res.percentage}%. The passing threshold is ${res.passingScorePercent}%. Keep studying and try again!`;
    }

    document.getElementById('result-score-text').textContent = `${res.score} / ${res.totalPoints}`;
    document.getElementById('result-percent-text').textContent = `${res.percentage}%`;
    document.getElementById('result-time-text').textContent = `${Math.floor(res.timeSpentSeconds / 60)}m ${res.timeSpentSeconds % 60}s`;
    document.getElementById('result-accuracy-text').textContent = `${res.correctCount} / ${res.totalQuestions}`;

    // Render detailed review list
    const reviewList = document.getElementById('result-review-list');
    reviewList.innerHTML = (res.evaluations || []).map((ev, idx) => {
      const statusBadge = ev.skipped ?
        '<span class="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-amber-500/20 text-amber-300 border border-amber-500/30">Skipped</span>' :
        (ev.correct ?
          '<span class="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-500/20 text-emerald-300 border border-emerald-500/30">Correct (+ ' + ev.pointsEarned + ' pts)</span>' :
          '<span class="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-rose-500/20 text-rose-300 border border-rose-500/30">Incorrect (0 pts)</span>');

      const optionsHtml = (ev.options || []).map((opt, optIdx) => {
        const isSelected = (ev.selectedOptions || []).includes(optIdx);
        const isCorrectChoice = (ev.correctAnswers || []).includes(optIdx);

        let borderClass = 'border-slate-700 bg-slate-900/60 text-slate-300';
        let indicator = '';

        if (isCorrectChoice) {
          borderClass = 'border-emerald-500/80 bg-emerald-950/40 text-emerald-200';
          indicator = '<span class="text-xs font-bold text-emerald-400">✓ Correct Answer</span>';
        } else if (isSelected && !isCorrectChoice) {
          borderClass = 'border-rose-500/80 bg-rose-950/40 text-rose-200';
          indicator = '<span class="text-xs font-bold text-rose-400">✗ Your Choice</span>';
        }

        return `
          <div class="flex items-center justify-between p-3 rounded-xl border text-sm ${borderClass}">
            <span>${opt}</span>
            ${indicator}
          </div>
        `;
      }).join('');

      return `
        <div class="bg-slate-800/80 rounded-2xl border border-slate-700 p-6 space-y-4 shadow-lg">
          <div class="flex items-center justify-between border-b border-slate-700/60 pb-3">
            <span class="text-xs font-bold uppercase tracking-wider text-indigo-400">Question ${idx + 1}</span>
            ${statusBadge}
          </div>
          <h4 class="text-base font-bold text-white leading-relaxed">${ev.questionText}</h4>
          <div class="space-y-2 pt-1">${optionsHtml}</div>
          ${ev.explanation ? `
            <div class="p-3.5 rounded-xl bg-slate-900/90 border border-slate-700/80 text-xs text-slate-300 space-y-1">
              <span class="font-bold text-indigo-300 uppercase tracking-wider text-[10px] block">Educational Explanation</span>
              <p class="leading-relaxed">${ev.explanation}</p>
            </div>
          ` : ''}
        </div>
      `;
    }).join('');
  }

  retakeCurrentQuiz() {
    if (this.currentQuiz) {
      this.promptStudentInfo(this.currentQuiz.id);
    }
  }

  // --- CERTIFICATE GENERATOR ---
  openCertificateModal() {
    if (!this.lastResult) return;
    const res = this.lastResult;

    document.getElementById('cert-student-name').textContent = res.studentName || 'Student';
    document.getElementById('cert-quiz-title').textContent = res.quizTitle;
    document.getElementById('cert-score-percent').textContent = `${res.percentage}%`;
    document.getElementById('cert-date').textContent = new Date(res.completedAt).toLocaleDateString('en-US', {
      year: 'numeric', month: 'long', day: 'numeric'
    });
    document.getElementById('cert-hash').textContent = `QP-${(res.attemptId || 'CERT').toUpperCase()}`;

    document.getElementById('modal-certificate').classList.remove('hidden');
  }

  closeCertificateModal() {
    document.getElementById('modal-certificate').classList.add('hidden');
  }

  // --- LEADERBOARD ---
  async loadLeaderboard() {
    const filter = document.getElementById('leaderboard-quiz-filter').value;
    try {
      const res = await fetch(`${this.apiBase}/leaderboard?quizId=${filter}&limit=25`);
      if (res.ok) {
        const list = await res.json();
        const tbody = document.getElementById('leaderboard-table-body');
        const emptyNotice = document.getElementById('leaderboard-empty');

        if (!list || list.length === 0) {
          tbody.innerHTML = '';
          emptyNotice.classList.remove('hidden');
          return;
        }

        emptyNotice.classList.add('hidden');
        tbody.innerHTML = list.map((item, idx) => {
          let rankBadge = `<span class="font-mono font-bold text-slate-400">#${idx + 1}</span>`;
          if (idx === 0) rankBadge = `<span class="text-xl">🥇</span>`;
          if (idx === 1) rankBadge = `<span class="text-xl">🥈</span>`;
          if (idx === 2) rankBadge = `<span class="text-xl">🥉</span>`;

          const timeFormatted = `${Math.floor(item.timeSpentSeconds / 60)}m ${item.timeSpentSeconds % 60}s`;
          const dateFormatted = new Date(item.timestamp).toLocaleDateString();

          return `
            <tr class="hover:bg-slate-800/40 transition">
              <td class="px-6 py-4">${rankBadge}</td>
              <td class="px-6 py-4 font-bold text-white">${item.studentName}</td>
              <td class="px-6 py-4 text-slate-300">${item.quizTitle}</td>
              <td class="px-6 py-4 text-emerald-400 font-extrabold">${item.percentage}%</td>
              <td class="px-6 py-4"><span class="px-2 py-0.5 rounded text-xs font-bold bg-indigo-500/20 text-indigo-300">${item.grade}</span></td>
              <td class="px-6 py-4 text-slate-400 font-mono text-xs">${timeFormatted}</td>
              <td class="px-6 py-4 text-slate-400 text-xs">${dateFormatted}</td>
            </tr>
          `;
        }).join('');
      }
    } catch (e) {
      console.error('Failed to load leaderboard:', e);
    }
  }

  // --- HISTORY ---
  saveLocalHistory(result) {
    try {
      const stored = JSON.parse(localStorage.getItem('quiz_history') || '[]');
      stored.unshift({
        quizTitle: result.quizTitle,
        studentName: result.studentName,
        percentage: result.percentage,
        grade: result.grade,
        passed: result.passed,
        timestamp: Date.now()
      });
      localStorage.setItem('quiz_history', JSON.stringify(stored.slice(0, 50)));
    } catch (e) {}
  }

  loadHistory() {
    const tbody = document.getElementById('history-table-body');
    const emptyNotice = document.getElementById('history-empty');

    let history = [];
    try {
      history = JSON.parse(localStorage.getItem('quiz_history') || '[]');
    } catch (e) {}

    if (history.length === 0) {
      tbody.innerHTML = '';
      emptyNotice.classList.remove('hidden');
      return;
    }

    emptyNotice.classList.add('hidden');
    tbody.innerHTML = history.map(item => `
      <tr class="hover:bg-slate-800/40 transition">
        <td class="px-6 py-4 text-xs text-slate-400">${new Date(item.timestamp).toLocaleString()}</td>
        <td class="px-6 py-4 font-bold text-white">${item.quizTitle}</td>
        <td class="px-6 py-4 text-slate-300">${item.studentName}</td>
        <td class="px-6 py-4 font-bold text-indigo-400">${item.grade}</td>
        <td class="px-6 py-4 font-extrabold text-emerald-400">${item.percentage}%</td>
        <td class="px-6 py-4">
          <span class="px-2.5 py-0.5 rounded-full text-xs font-bold ${item.passed ? 'bg-emerald-500/20 text-emerald-300' : 'bg-rose-500/20 text-rose-300'}">
            ${item.passed ? 'Passed' : 'Failed'}
          </span>
        </td>
      </tr>
    `).join('');
  }

  // --- ADMIN PORTAL ---
  async loadAdminQuizzes() {
    try {
      const res = await fetch(`${this.apiBase}/quizzes/admin/all`);
      if (res.ok) {
        const list = await res.json();
        const tbody = document.getElementById('admin-quizzes-table-body');
        tbody.innerHTML = list.map(q => `
          <tr class="hover:bg-slate-800/40 transition">
            <td class="px-6 py-4 font-bold text-white">${q.title}</td>
            <td class="px-6 py-4 text-slate-300"><span class="px-2 py-0.5 rounded text-xs bg-slate-800 border border-slate-700">${q.category}</span></td>
            <td class="px-6 py-4 text-slate-300 text-xs">${q.difficulty}</td>
            <td class="px-6 py-4 font-mono text-xs">${q.totalQuestions || (q.questions ? q.questions.length : 0)}</td>
            <td class="px-6 py-4 text-xs">${q.durationMinutes} min</td>
            <td class="px-6 py-4 text-xs font-bold text-amber-400">${q.passingScorePercent}%</td>
            <td class="px-6 py-4 text-right space-x-2">
              <button onclick="app.editQuiz('${q.id}')" class="px-3 py-1 bg-indigo-600/30 hover:bg-indigo-600 text-indigo-300 hover:text-white rounded-lg text-xs font-semibold transition">Edit</button>
              <button onclick="app.deleteQuiz('${q.id}')" class="px-3 py-1 bg-rose-600/30 hover:bg-rose-600 text-rose-300 hover:text-white rounded-lg text-xs font-semibold transition">Delete</button>
            </td>
          </tr>
        `).join('');
      }
    } catch (e) {
      console.error('Failed to load admin quizzes:', e);
    }
  }

  openQuizEditorModal(quiz = null) {
    document.getElementById('editor-quiz-id').value = quiz ? quiz.id : '';
    document.getElementById('editor-title').value = quiz ? quiz.title : '';
    document.getElementById('editor-description').value = quiz ? quiz.description : '';
    document.getElementById('editor-category').value = quiz ? quiz.category : '';
    document.getElementById('editor-difficulty').value = quiz ? quiz.difficulty : 'Intermediate';
    document.getElementById('editor-duration').value = quiz ? quiz.durationMinutes : 10;
    document.getElementById('editor-passing').value = quiz ? quiz.passingScorePercent : 60;

    this.editorQuestions = quiz && quiz.questions ? JSON.parse(JSON.stringify(quiz.questions)) : [];
    if (this.editorQuestions.length === 0) {
      this.addEditorQuestion();
    } else {
      this.renderEditorQuestions();
    }

    document.getElementById('quiz-editor-modal-title').textContent = quiz ? 'Edit Assessment' : 'Create New Assessment';
    document.getElementById('modal-quiz-editor').classList.remove('hidden');
  }

  closeQuizEditorModal() {
    document.getElementById('modal-quiz-editor').classList.add('hidden');
  }

  async editQuiz(id) {
    try {
      const res = await fetch(`${this.apiBase}/quizzes/${id}/admin`);
      if (res.ok) {
        const quiz = await res.json();
        this.openQuizEditorModal(quiz);
      }
    } catch (e) {
      alert('Error fetching quiz: ' + e.message);
    }
  }

  async deleteQuiz(id) {
    if (!confirm('Are you sure you want to permanently delete this quiz?')) return;
    try {
      const res = await fetch(`${this.apiBase}/quizzes/${id}`, { method: 'DELETE' });
      if (res.ok) {
        this.showToast('Quiz deleted successfully', '🗑️');
        await this.loadAdminQuizzes();
        await this.loadQuizzes();
        await this.loadAnalytics();
      }
    } catch (e) {
      alert('Error deleting quiz: ' + e.message);
    }
  }

  addEditorQuestion() {
    this.editorQuestions.push({
      id: 'q-' + Math.random().toString(36).substr(2, 8),
      text: '',
      type: 'SINGLE_CHOICE',
      options: ['Option A', 'Option B', 'Option C', 'Option D'],
      correctAnswers: [0],
      explanation: '',
      points: 1
    });
    this.renderEditorQuestions();
  }

  removeEditorQuestion(index) {
    this.editorQuestions.splice(index, 1);
    this.renderEditorQuestions();
  }

  renderEditorQuestions() {
    const container = document.getElementById('editor-questions-container');
    document.getElementById('editor-question-count-badge').textContent = `${this.editorQuestions.length} questions`;

    container.innerHTML = this.editorQuestions.map((q, idx) => `
      <div class="bg-slate-900/90 rounded-2xl border border-slate-700/80 p-5 space-y-4">
        <div class="flex items-center justify-between border-b border-slate-800 pb-3">
          <span class="text-xs font-bold uppercase tracking-wider text-indigo-400">Question ${idx + 1}</span>
          <button type="button" onclick="app.removeEditorQuestion(${idx})" class="text-xs text-rose-400 hover:text-rose-300 font-semibold">✕ Remove</button>
        </div>

        <div>
          <label class="block text-xs text-slate-400 uppercase font-semibold mb-1">Question Prompt *</label>
          <input type="text" value="${q.text || ''}" onchange="app.editorQuestions[${idx}].text = this.value" placeholder="e.g. What is the output of..." class="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-slate-100">
        </div>

        <div class="grid grid-cols-2 gap-3">
          <div>
            <label class="block text-xs text-slate-400 uppercase font-semibold mb-1">Question Type</label>
            <select onchange="app.changeEditorQuestionType(${idx}, this.value)" class="w-full px-3 py-2 bg-slate-800 border border-slate-700 rounded-xl text-xs text-slate-200">
              <option value="SINGLE_CHOICE" ${q.type === 'SINGLE_CHOICE' ? 'selected' : ''}>Single Choice (Radio)</option>
              <option value="MULTIPLE_CHOICE" ${q.type === 'MULTIPLE_CHOICE' ? 'selected' : ''}>Multiple Choice (Checkboxes)</option>
              <option value="TRUE_FALSE" ${q.type === 'TRUE_FALSE' ? 'selected' : ''}>True / False</option>
            </select>
          </div>
          <div>
            <label class="block text-xs text-slate-400 uppercase font-semibold mb-1">Point Value</label>
            <input type="number" min="1" max="10" value="${q.points || 1}" onchange="app.editorQuestions[${idx}].points = parseInt(this.value) || 1" class="w-full px-3 py-2 bg-slate-800 border border-slate-700 rounded-xl text-xs text-slate-100">
          </div>
        </div>

        <!-- Options builder -->
        <div class="space-y-2 pt-2">
          <label class="block text-xs text-slate-400 uppercase font-semibold">Options & Mark Correct (Check to set as correct answer)</label>
          ${(q.options || []).map((opt, optIdx) => {
            const isCorrect = (q.correctAnswers || []).includes(optIdx);
            return `
              <div class="flex items-center gap-2">
                <input type="checkbox" ${isCorrect ? 'checked' : ''} onchange="app.toggleEditorCorrectAnswer(${idx}, ${optIdx})" class="w-4 h-4 rounded text-indigo-600">
                <input type="text" value="${opt}" onchange="app.editorQuestions[${idx}].options[${optIdx}] = this.value" class="flex-1 px-3 py-1.5 bg-slate-800 border border-slate-700 rounded-lg text-xs text-slate-200">
              </div>
            `;
          }).join('')}
        </div>

        <div>
          <label class="block text-xs text-slate-400 uppercase font-semibold mb-1">Explanation</label>
          <textarea rows="2" placeholder="Why this answer is correct..." onchange="app.editorQuestions[${idx}].explanation = this.value" class="w-full px-3 py-1.5 bg-slate-800 border border-slate-700 rounded-xl text-xs text-slate-200">${q.explanation || ''}</textarea>
        </div>
      </div>
    `).join('');
  }

  changeEditorQuestionType(idx, newType) {
    const q = this.editorQuestions[idx];
    q.type = newType;
    if (newType === 'TRUE_FALSE') {
      q.options = ['True', 'False'];
      q.correctAnswers = [0];
    } else if (q.options.length < 2) {
      q.options = ['Option A', 'Option B', 'Option C', 'Option D'];
    }
    this.renderEditorQuestions();
  }

  toggleEditorCorrectAnswer(qIdx, optIdx) {
    const q = this.editorQuestions[qIdx];
    if (q.type === 'SINGLE_CHOICE' || q.type === 'TRUE_FALSE') {
      q.correctAnswers = [optIdx];
    } else {
      if (q.correctAnswers.includes(optIdx)) {
        q.correctAnswers = q.correctAnswers.filter(i => i !== optIdx);
      } else {
        q.correctAnswers.push(optIdx);
      }
    }
    this.renderEditorQuestions();
  }

  async saveQuizFromEditor() {
    const title = document.getElementById('editor-title').value.trim();
    const category = document.getElementById('editor-category').value.trim();
    const description = document.getElementById('editor-description').value.trim();
    const difficulty = document.getElementById('editor-difficulty').value;
    const duration = parseInt(document.getElementById('editor-duration').value) || 10;
    const passing = parseInt(document.getElementById('editor-passing').value) || 60;
    const quizId = document.getElementById('editor-quiz-id').value;

    if (!title || !category) {
      alert('Quiz Title and Category are required!');
      return;
    }

    if (this.editorQuestions.length === 0) {
      alert('Please add at least one question to the quiz.');
      return;
    }

    const payload = {
      id: quizId || null,
      title,
      description,
      category,
      difficulty,
      durationMinutes: duration,
      passingScorePercent: passing,
      questions: this.editorQuestions,
      active: true
    };

    try {
      const url = quizId ? `${this.apiBase}/quizzes/${quizId}` : `${this.apiBase}/quizzes`;
      const method = quizId ? 'PUT' : 'POST';

      const res = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      if (!res.ok) throw new Error('Failed to save quiz');

      this.showToast('Quiz published successfully!', '🚀');
      this.closeQuizEditorModal();
      await this.loadAdminQuizzes();
      await this.loadQuizzes();
      await this.loadAnalytics();
    } catch (e) {
      alert('Error saving quiz: ' + e.message);
    }
  }

  async resetSeedDataPrompt() {
    if (!confirm('This will restore all default quizzes. Are you sure?')) return;
    try {
      const res = await fetch(`${this.apiBase}/quizzes/admin/reset`, { method: 'POST' });
      if (res.ok) {
        this.showToast('Default sample quizzes restored!', '↺');
        await this.loadAdminQuizzes();
        await this.loadQuizzes();
        await this.loadAnalytics();
      }
    } catch (e) {
      alert('Error resetting seed: ' + e.message);
    }
  }

  // --- TOAST SYSTEM ---
  showToast(msg, icon = 'ℹ️') {
    const toast = document.getElementById('toast');
    document.getElementById('toast-message').textContent = msg;
    document.getElementById('toast-icon').textContent = icon;

    toast.classList.remove('translate-y-20', 'opacity-0');
    setTimeout(() => {
      toast.classList.add('translate-y-20', 'opacity-0');
    }, 3500);
  }

  bindEvents() {
    // Keyboard navigation during active quiz
    window.addEventListener('keydown', (e) => {
      const runnerView = document.getElementById('view-runner');
      if (runnerView && !runnerView.classList.contains('hidden')) {
        if (e.key === 'ArrowRight') {
          this.nextQuestion();
        } else if (e.key === 'ArrowLeft') {
          this.prevQuestion();
        } else if (['1', '2', '3', '4', '5'].includes(e.key)) {
          const idx = parseInt(e.key) - 1;
          const q = this.currentQuiz.questions[this.currentQuestionIndex];
          if (q && q.options && idx < q.options.length) {
            this.selectOption(idx);
          }
        }
      }
    });
  }
}

// Initialize Application on Window Load
window.app = new QuizApp();
