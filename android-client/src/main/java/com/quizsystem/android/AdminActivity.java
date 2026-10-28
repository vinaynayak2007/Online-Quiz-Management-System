package com.quizsystem.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminActivity extends Activity {

    private QuizDataStore dataStore;
    private TextView tvStatUsers, tvStatPassed, tvStatFailed, tvStatPassRate;
    private LinearLayout containerAttempts, containerUsers, containerQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        dataStore = QuizDataStore.getInstance(this);

        tvStatUsers = findViewById(R.id.tv_admin_stat_users);
        tvStatPassed = findViewById(R.id.tv_admin_stat_passed);
        tvStatFailed = findViewById(R.id.tv_admin_stat_failed);
        tvStatPassRate = findViewById(R.id.tv_admin_stat_pass_rate);

        containerAttempts = findViewById(R.id.container_admin_attempts);
        containerUsers = findViewById(R.id.container_admin_users);
        containerQuestions = findViewById(R.id.container_admin_questions);

        Button btnLogout = findViewById(R.id.btn_admin_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataStore.clearCurrentSession();
                startActivity(new Intent(AdminActivity.this, LoginActivity.class));
                finish();
            }
        });

        loadAdminData();
    }

    private void loadAdminData() {
        JSONArray users = dataStore.getUsers();
        JSONArray attempts = dataStore.getAttempts();

        int totalUsers = users.length();
        int totalAttempts = attempts.length();
        int passedCount = 0;

        for (int i = 0; i < totalAttempts; i++) {
            JSONObject a = attempts.optJSONObject(i);
            if (a != null && a.optBoolean("passed")) {
                passedCount++;
            }
        }
        int failedCount = totalAttempts - passedCount;
        int passRate = totalAttempts > 0 ? Math.round(((float) passedCount / totalAttempts) * 100) : 0;

        tvStatUsers.setText(String.valueOf(totalUsers));
        tvStatPassed.setText(passedCount + " Passed");
        tvStatFailed.setText(failedCount + " Failed");
        tvStatPassRate.setText(passRate + "%");

        renderAttempts(attempts);
        renderUsers(users);
        renderQuestions();
    }

    private void renderAttempts(JSONArray attempts) {
        containerAttempts.removeAllViews();
        if (attempts.length() == 0) {
            TextView empty = new TextView(this);
            empty.setText("No exam attempts recorded yet.");
            empty.setTextColor(0xFF94A3B8);
            empty.setPadding(16, 16, 16, 16);
            containerAttempts.addView(empty);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

        for (int i = 0; i < attempts.length(); i++) {
            JSONObject att = attempts.optJSONObject(i);
            if (att == null) continue;

            boolean passed = att.optBoolean("passed");
            String studentName = att.optString("studentName");
            String studentPhone = att.optString("studentPhone");
            String examTitle = att.optString("quizTitle");
            double pct = att.optDouble("percentage");
            long time = att.optLong("timestamp");

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.VERTICAL);
            row.setBackgroundColor(0xFF131B2E);
            row.setPadding(24, 20, 24, 20);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, 16);
            row.setLayoutParams(lp);

            TextView tvHeader = new TextView(this);
            tvHeader.setText(studentName + " (📞 " + studentPhone + ")");
            tvHeader.setTextColor(0xFFFFFFFF);
            tvHeader.setTextSize(14);
            tvHeader.setTypeface(null, android.graphics.Typeface.BOLD);
            row.addView(tvHeader);

            TextView tvExam = new TextView(this);
            tvExam.setText(examTitle + " • Score: " + pct + "%");
            tvExam.setTextColor(0xFFCBD5E1);
            tvExam.setTextSize(13);
            tvExam.setPadding(0, 4, 0, 4);
            row.addView(tvExam);

            TextView tvBadge = new TextView(this);
            tvBadge.setText(passed ? "STATUS: PASSED ✅" : "STATUS: FAILED ❌");
            tvBadge.setTextColor(passed ? 0xFF34D399 : 0xFFF87171);
            tvBadge.setTextSize(13);
            tvBadge.setTypeface(null, android.graphics.Typeface.BOLD);
            row.addView(tvBadge);

            TextView tvDate = new TextView(this);
            tvDate.setText("Submitted: " + sdf.format(new Date(time)));
            tvDate.setTextColor(0xFF64748B);
            tvDate.setTextSize(11);
            tvDate.setPadding(0, 4, 0, 0);
            row.addView(tvDate);

            containerAttempts.addView(row);
        }
    }

    private void renderUsers(JSONArray users) {
        containerUsers.removeAllViews();
        for (int i = 0; i < users.length(); i++) {
            JSONObject u = users.optJSONObject(i);
            if (u == null) continue;

            String name = u.optString("name");
            String phone = u.optString("phone");
            int taken = u.optInt("examsTaken", 0);
            int passed = u.optInt("examsPassed", 0);

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.VERTICAL);
            row.setBackgroundColor(0xFF131B2E);
            row.setPadding(24, 20, 24, 20);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, 16);
            row.setLayoutParams(lp);

            TextView tvName = new TextView(this);
            tvName.setText("👤 " + name);
            tvName.setTextColor(0xFFFFFFFF);
            tvName.setTextSize(14);
            tvName.setTypeface(null, android.graphics.Typeface.BOLD);
            row.addView(tvName);

            TextView tvPhone = new TextView(this);
            tvPhone.setText("Phone: " + phone + " | Exams Taken: " + taken + " | Exams Passed: " + passed);
            tvPhone.setTextColor(0xFF94A3B8);
            tvPhone.setTextSize(12);
            tvPhone.setPadding(0, 4, 0, 0);
            row.addView(tvPhone);

            containerUsers.addView(row);
        }
    }

    private void renderQuestions() {
        containerQuestions.removeAllViews();
        JSONArray quizzes = dataStore.getQuizzes();

        for (int i = 0; i < quizzes.length(); i++) {
            final JSONObject quiz = quizzes.optJSONObject(i);
            if (quiz == null) continue;

            final String quizId = quiz.optString("id");
            String quizTitle = quiz.optString("title");

            TextView trackHeader = new TextView(this);
            trackHeader.setText("📚 " + quizTitle);
            trackHeader.setTextColor(0xFFA5B4FC);
            trackHeader.setTextSize(15);
            trackHeader.setTypeface(null, android.graphics.Typeface.BOLD);
            trackHeader.setPadding(0, 16, 0, 8);
            containerQuestions.addView(trackHeader);

            JSONArray qList = quiz.optJSONArray("questions");
            if (qList == null) continue;

            for (int j = 0; j < qList.length(); j++) {
                final JSONObject q = qList.optJSONObject(j);
                if (q == null) continue;

                final String qId = q.optString("id");
                String qText = q.optString("text");
                final int rightAnswerIdx = q.optInt("correctAnswerIndex", 0);
                final JSONArray opts = q.optJSONArray("options");

                LinearLayout qCard = new LinearLayout(this);
                qCard.setOrientation(LinearLayout.VERTICAL);
                qCard.setBackgroundColor(0xFF131B2E);
                qCard.setPadding(24, 20, 24, 20);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 0, 0, 16);
                qCard.setLayoutParams(lp);

                TextView tvQ = new TextView(this);
                tvQ.setText("Q" + (j + 1) + ": " + qText);
                tvQ.setTextColor(0xFFFFFFFF);
                tvQ.setTextSize(14);
                tvQ.setTypeface(null, android.graphics.Typeface.BOLD);
                qCard.addView(tvQ);

                if (opts != null) {
                    for (int k = 0; k < opts.length(); k++) {
                        final int optIndex = k;
                        final boolean isRight = (optIndex == rightAnswerIdx);

                        LinearLayout optRow = new LinearLayout(this);
                        optRow.setOrientation(LinearLayout.HORIZONTAL);
                        optRow.setBackgroundColor(isRight ? 0xFF162C26 : 0xFF0B0F19);
                        optRow.setPadding(20, 16, 20, 16);

                        LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        rowLp.setMargins(0, 8, 0, 0);
                        optRow.setLayoutParams(rowLp);

                        TextView optText = new TextView(this);
                        optText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                        optText.setText(String.format("%c)  %s", (char) ('A' + k), opts.optString(k)));
                        optText.setTextColor(isRight ? 0xFF34D399 : 0xFFCBD5E1);
                        optText.setTextSize(13);
                        if (isRight) optText.setTypeface(null, android.graphics.Typeface.BOLD);
                        optRow.addView(optText);

                        TextView tag = new TextView(this);
                        tag.setText(isRight ? "✓ RIGHT ANSWER" : "Set as Right");
                        tag.setTextColor(isRight ? 0xFF34D399 : 0xFF64748B);
                        tag.setTextSize(11);
                        optRow.addView(tag);

                        // 1-Click Right Answer Switcher!
                        optRow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dataStore.setRightAnswer(quizId, qId, optIndex);
                                Toast.makeText(AdminActivity.this, "✅ Right answer updated to Option " + (char) ('A' + optIndex), Toast.LENGTH_SHORT).show();
                                renderQuestions();
                            }
                        });

                        qCard.addView(optRow);
                    }
                }

                containerQuestions.addView(qCard);
            }
        }
    }
}
