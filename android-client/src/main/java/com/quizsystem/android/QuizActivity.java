package com.quizsystem.android;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizActivity extends AppCompatActivity {

    private String quizId;
    private CountDownTimer timer;
    private int timeRemainingSeconds = 600; // 10 minutes default
    private int currentQuestionIndex = 0;
    private final Map<String, List<Integer>> selectedAnswers = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        quizId = getIntent().getStringExtra("quizId");

        // 10-minute countdown timer with 1-second ticks
        timer = new CountDownTimer(timeRemainingSeconds * 1000L, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemainingSeconds = (int) (millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                submitToFirebase();
            }
        }.start();
    }

    public void submitToFirebase() {
        if (timer != null) timer.cancel();

        Map<String, Object> submission = new HashMap<>();
        submission.put("quizId", quizId);
        submission.put("answers", selectedAnswers);
        submission.put("timestamp", System.currentTimeMillis());

        FirebaseHelper.getInstance().submitAttempt(submission, new FirebaseHelper.DataCallback<String>() {
            @Override
            public void onSuccess(String attemptId) {
                Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
                intent.putExtra("attemptId", attemptId);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                // handle failure
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }
}
