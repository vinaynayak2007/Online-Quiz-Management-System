package com.quizsystem.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;

public class ExamListActivity extends Activity {

    private QuizDataStore dataStore;
    private LinearLayout containerExamCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_list);

        dataStore = QuizDataStore.getInstance(this);

        TextView tvWelcome = findViewById(R.id.tv_welcome_student);
        tvWelcome.setText("Welcome, " + dataStore.getCurrentUserName() + " (📞 " + dataStore.getCurrentUserPhone() + ")");

        Button btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataStore.clearCurrentSession();
                startActivity(new Intent(ExamListActivity.this, LoginActivity.class));
                finish();
            }
        });

        containerExamCards = findViewById(R.id.container_exam_cards);
        renderExamCards();
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderExamCards();
    }

    private void renderExamCards() {
        containerExamCards.removeAllViews();
        JSONArray quizzes = dataStore.getQuizzes();
        JSONArray attempts = dataStore.getAttempts();
        String myPhone = dataStore.getCurrentUserPhone();

        for (int i = 0; i < quizzes.length(); i++) {
            final JSONObject quiz = quizzes.optJSONObject(i);
            if (quiz == null) continue;

            final String quizId = quiz.optString("id");
            String title = quiz.optString("title");
            String category = quiz.optString("category");
            int duration = quiz.optInt("durationMinutes", 10);
            int passMark = quiz.optInt("passingScorePercent", 70);
            JSONArray questions = quiz.optJSONArray("questions");
            int qCount = (questions != null) ? questions.length() : 0;

            // Check if student already passed/failed this exam
            String resultBadge = "";
            for (int j = 0; j < attempts.length(); j++) {
                JSONObject att = attempts.optJSONObject(j);
                if (att != null && att.optString("quizId").equals(quizId) && att.optString("studentPhone").equals(myPhone)) {
                    boolean passed = att.optBoolean("passed");
                    double pct = att.optDouble("percentage");
                    resultBadge = passed ? " [PASSED: " + pct + "% ✅]" : " [FAILED: " + pct + "% ❌]";
                    break;
                }
            }

            // Create Native Card View
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setBackgroundColor(getResources().getColor(R.color.card_dark));
            card.setPadding(32, 32, 32, 32);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, 32);
            card.setLayoutParams(lp);

            TextView tvCategory = new TextView(this);
            tvCategory.setText(category.toUpperCase() + resultBadge);
            tvCategory.setTextColor(getResources().getColor(R.color.primary));
            tvCategory.setTextSize(12);
            tvCategory.setTypeface(null, android.graphics.Typeface.BOLD);
            card.addView(tvCategory);

            TextView tvTitle = new TextView(this);
            tvTitle.setText(title);
            tvTitle.setTextColor(0xFFFFFFFF);
            tvTitle.setTextSize(17);
            tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
            tvTitle.setPadding(0, 8, 0, 8);
            card.addView(tvTitle);

            TextView tvMeta = new TextView(this);
            tvMeta.setText("⏱️ " + duration + " Mins  |  📝 " + qCount + " Questions  |  🎯 Pass: " + passMark + "%");
            tvMeta.setTextColor(getResources().getColor(R.color.text_muted));
            tvMeta.setTextSize(13);
            tvMeta.setPadding(0, 0, 0, 20);
            card.addView(tvMeta);

            Button btnStart = new Button(this);
            btnStart.setText("🚀 Start Exam (Full Screen Mode)");
            btnStart.setTextColor(0xFFFFFFFF);
            btnStart.setBackgroundColor(getResources().getColor(R.color.primary));
            btnStart.setTextSize(13);
            btnStart.setTypeface(null, android.graphics.Typeface.BOLD);
            btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ExamListActivity.this, ExamActivity.class);
                    intent.putExtra("quizId", quizId);
                    startActivity(intent);
                }
            });
            card.addView(btnStart);

            containerExamCards.addView(card);
        }
    }
}
