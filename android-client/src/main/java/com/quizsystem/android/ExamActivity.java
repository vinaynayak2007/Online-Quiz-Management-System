package com.quizsystem.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ExamActivity extends Activity {

    private QuizDataStore dataStore;
    private JSONObject currentQuiz;
    private JSONArray questions;
    private int currentQuestionIndex = 0;
    private int totalQuestions = 0;
    private int durationMinutes = 10;
    private int passingScorePercent = 70;

    private TextView tvActiveTitle, tvTimer, tvProgress, tvPoints, tvStatement;
    private RadioGroup rgOptions;
    private Button btnPrev, btnNext, btnFlag, btnSubmit;

    private CountDownTimer timer;
    private int timeRemainingSeconds = 600;
    private final Map<Integer, Integer> candidateAnswers = new HashMap<>(); // qIndex -> optionIndex
    private final Map<Integer, Boolean> flaggedMap = new HashMap<>();

    private boolean isSubmitted = false;
    private int cheatViolationCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableStrictFullscreen();
        setContentView(R.layout.activity_exam);

        dataStore = QuizDataStore.getInstance(this);
        String quizId = getIntent().getStringExtra("quizId");
        currentQuiz = dataStore.getQuizById(quizId);

        if (currentQuiz == null) {
            Toast.makeText(this, "Quiz not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        questions = currentQuiz.optJSONArray("questions");
        totalQuestions = (questions != null) ? questions.length() : 0;
        durationMinutes = currentQuiz.optInt("durationMinutes", 10);
        passingScorePercent = currentQuiz.optInt("passingScorePercent", 70);
        timeRemainingSeconds = durationMinutes * 60;

        initViews();
        renderQuestion(currentQuestionIndex);
        startTimer();
    }

    private void enableStrictFullscreen() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            enableStrictFullscreen();
        }
    }

    // --- ANTI-CHEAT & TAB SWITCH INTERCEPTION ---
    @Override
    protected void onPause() {
        super.onPause();
        if (!isSubmitted && !isFinishing()) {
            cheatViolationCount++;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableStrictFullscreen();
        if (cheatViolationCount > 0 && !isSubmitted) {
            showCheatAlert();
        }
    }

    private void showCheatAlert() {
        new AlertDialog.Builder(this)
            .setTitle("⚠️ EXAM INTEGRITY VIOLATION")
            .setMessage("Leaving the examination application or switching apps is strictly prohibited!\n\nViolation Count: " + cheatViolationCount)
            .setPositiveButton("I Understand - Return to Exam", null)
            .setCancelable(false)
            .show();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
            .setTitle("⚠️ Active Exam in Progress")
            .setMessage("Leaving now will void your exam session. Are you sure you want to exit?")
            .setPositiveButton("Yes, Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (timer != null) timer.cancel();
                    finish();
                }
            })
            .setNegativeButton("Stay in Exam", null)
            .show();
    }

    private void initViews() {
        tvActiveTitle = findViewById(R.id.tv_exam_active_title);
        tvTimer = findViewById(R.id.tv_countdown_timer);
        tvProgress = findViewById(R.id.tv_q_progress);
        tvPoints = findViewById(R.id.tv_q_points);
        tvStatement = findViewById(R.id.tv_q_statement);
        rgOptions = findViewById(R.id.rg_options);

        btnPrev = findViewById(R.id.btn_prev_question);
        btnNext = findViewById(R.id.btn_next_question);
        btnFlag = findViewById(R.id.btn_flag_review);
        btnSubmit = findViewById(R.id.btn_finish_submit);

        tvActiveTitle.setText(currentQuiz.optString("title"));

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentQuestionIndex > 0) {
                    saveCurrentSelection();
                    currentQuestionIndex--;
                    renderQuestion(currentQuestionIndex);
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCurrentSelection();
                if (currentQuestionIndex < totalQuestions - 1) {
                    currentQuestionIndex++;
                    renderQuestion(currentQuestionIndex);
                } else {
                    confirmSubmit();
                }
            }
        });

        btnFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isFlagged = flaggedMap.getOrDefault(currentQuestionIndex, false);
                flaggedMap.put(currentQuestionIndex, !isFlagged);
                btnFlag.setText(!isFlagged ? "🚩 Flagged" : "🚩 Flag");
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmSubmit();
            }
        });
    }

    private void startTimer() {
        timer = new CountDownTimer(timeRemainingSeconds * 1000L, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemainingSeconds = (int) (millisUntilFinished / 1000L);
                int mins = timeRemainingSeconds / 60;
                int secs = timeRemainingSeconds % 60;
                tvTimer.setText(String.format("%02d:%02d", mins, secs));

                if (timeRemainingSeconds <= 60) {
                    tvTimer.setTextColor(0xFFF87171); // urgent red
                }
            }

            @Override
            public void onFinish() {
                Toast.makeText(ExamActivity.this, "⏰ Time Expired! Submitting exam automatically.", Toast.LENGTH_LONG).show();
                performSubmit();
            }
        }.start();
    }

    private void renderQuestion(int index) {
        if (questions == null || index < 0 || index >= totalQuestions) return;
        JSONObject q = questions.optJSONObject(index);
        if (q == null) return;

        tvProgress.setText("Question " + (index + 1) + " of " + totalQuestions);
        tvPoints.setText(q.optInt("points", 10) + " Points");
        tvStatement.setText(q.optString("text"));

        boolean isFlagged = flaggedMap.getOrDefault(index, false);
        btnFlag.setText(isFlagged ? "🚩 Flagged" : "🚩 Flag");

        btnPrev.setEnabled(index > 0);
        btnNext.setText(index == totalQuestions - 1 ? "Review / Submit" : "Next");

        rgOptions.removeAllViews();
        JSONArray opts = q.optJSONArray("options");
        Integer savedChoice = candidateAnswers.get(index);

        if (opts != null) {
            for (int i = 0; i < opts.length(); i++) {
                RadioButton rb = new RadioButton(this);
                rb.setId(1000 + i);
                rb.setText(String.format("%c)  %s", (char) ('A' + i), opts.optString(i)));
                rb.setTextColor(0xFFCBD5E1);
                rb.setTextSize(15);
                rb.setPadding(20, 24, 20, 24);

                if (savedChoice != null && savedChoice == i) {
                    rb.setChecked(true);
                }
                rgOptions.addView(rb);
            }
        }
    }

    private void saveCurrentSelection() {
        int checkedId = rgOptions.getCheckedRadioButtonId();
        if (checkedId >= 1000) {
            candidateAnswers.put(currentQuestionIndex, checkedId - 1000);
        }
    }

    private void confirmSubmit() {
        saveCurrentSelection();
        int answered = candidateAnswers.size();
        int unanswered = totalQuestions - answered;

        String msg = "Are you ready to submit your exam?\n\n• Answered: " + answered + "/" + totalQuestions;
        if (unanswered > 0) {
            msg += "\n⚠️ Warning: You still have " + unanswered + " UNANSWERED questions!";
        }

        new AlertDialog.Builder(this)
            .setTitle("Confirm Exam Submission")
            .setMessage(msg)
            .setPositiveButton("Submit Exam", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    performSubmit();
                }
            })
            .setNegativeButton("Review Answers", null)
            .show();
    }

    private void performSubmit() {
        saveCurrentSelection();
        isSubmitted = true;
        if (timer != null) timer.cancel();

        // Calculate score
        int score = 0;
        int totalPoints = 0;

        for (int i = 0; i < totalQuestions; i++) {
            JSONObject q = questions.optJSONObject(i);
            int qPts = q.optInt("points", 10);
            totalPoints += qPts;

            int rightIdx = q.optInt("correctAnswerIndex", 0);
            Integer candidateChoice = candidateAnswers.get(i);

            if (candidateChoice != null && candidateChoice == rightIdx) {
                score += qPts;
            }
        }

        double percentage = totalPoints > 0 ? ((double) score / totalPoints) * 100.0 : 0.0;
        percentage = Math.round(percentage * 10.0) / 10.0;
        boolean passed = percentage >= passingScorePercent;

        // Save Attempt
        dataStore.saveAttempt(
                currentQuiz.optString("id"),
                currentQuiz.optString("title"),
                dataStore.getCurrentUserName(),
                dataStore.getCurrentUserPhone(),
                score,
                totalPoints,
                percentage,
                passed,
                passingScorePercent
        );

        // Open Result Activity
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("quizId", currentQuiz.optString("id"));
        intent.putExtra("score", score);
        intent.putExtra("totalPoints", totalPoints);
        intent.putExtra("percentage", percentage);
        intent.putExtra("passed", passed);
        intent.putExtra("passMark", passingScorePercent);

        // Pass candidate selections
        int[] choices = new int[totalQuestions];
        for (int i = 0; i < totalQuestions; i++) {
            choices[i] = candidateAnswers.getOrDefault(i, -1);
        }
        intent.putExtra("candidateChoices", choices);

        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }
}
