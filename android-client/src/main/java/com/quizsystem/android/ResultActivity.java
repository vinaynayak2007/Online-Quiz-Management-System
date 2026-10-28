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

public class ResultActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        QuizDataStore dataStore = QuizDataStore.getInstance(this);

        String quizId = getIntent().getStringExtra("quizId");
        int score = getIntent().getIntExtra("score", 0);
        int totalPoints = getIntent().getIntExtra("totalPoints", 40);
        double percentage = getIntent().getDoubleExtra("percentage", 0.0);
        boolean passed = getIntent().getBooleanExtra("passed", false);
        int passMark = getIntent().getIntExtra("passMark", 70);
        int[] choices = getIntent().getIntArrayExtra("candidateChoices");

        JSONObject quiz = dataStore.getQuizById(quizId);
        String title = (quiz != null) ? quiz.optString("title") : "Examination";

        TextView tvTitle = findViewById(R.id.tv_result_title);
        TextView tvCandidate = findViewById(R.id.tv_result_candidate);
        TextView tvPassBadge = findViewById(R.id.tv_result_pass_badge);
        TextView tvIcon = findViewById(R.id.tv_result_icon);

        TextView tvPct = findViewById(R.id.tv_score_percentage);
        TextView tvPassReq = findViewById(R.id.tv_score_pass_mark);
        TextView tvPts = findViewById(R.id.tv_score_points);

        tvTitle.setText(title);
        tvCandidate.setText("Candidate: " + dataStore.getCurrentUserName() + " (📞 " + dataStore.getCurrentUserPhone() + ")");
        tvPct.setText(percentage + "%");
        tvPassReq.setText(passMark + "%");
        tvPts.setText(score + " / " + totalPoints);

        if (passed) {
            tvIcon.setText("🎉");
            tvPassBadge.setText("PASSED ✅");
            tvPassBadge.setTextColor(0xFF34D399);
            tvPassBadge.setBackgroundColor(0xFF162C26);
        } else {
            tvIcon.setText("⚠️");
            tvPassBadge.setText("FAILED ❌");
            tvPassBadge.setTextColor(0xFFF87171);
            tvPassBadge.setBackgroundColor(0xFF2C1616);
        }

        // Render question reviews
        LinearLayout container = findViewById(R.id.container_review_items);
        if (quiz != null) {
            JSONArray questions = quiz.optJSONArray("questions");
            if (questions != null) {
                for (int i = 0; i < questions.length(); i++) {
                    JSONObject q = questions.optJSONObject(i);
                    if (q == null) continue;

                    int rightIdx = q.optInt("correctAnswerIndex", 0);
                    int candidateChoice = (choices != null && i < choices.length) ? choices[i] : -1;
                    boolean isCorrect = (candidateChoice == rightIdx);

                    JSONArray opts = q.optJSONArray("options");

                    LinearLayout item = new LinearLayout(this);
                    item.setOrientation(LinearLayout.VERTICAL);
                    item.setBackgroundColor(0xFF0F172A);
                    item.setPadding(24, 24, 24, 24);

                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, 0, 0, 16);
                    item.setLayoutParams(lp);

                    TextView qHead = new TextView(this);
                    qHead.setText("Q" + (i + 1) + ": " + q.optString("text"));
                    qHead.setTextColor(0xFFFFFFFF);
                    qHead.setTextSize(14);
                    qHead.setTypeface(null, android.graphics.Typeface.BOLD);
                    item.addView(qHead);

                    String yourText = (candidateChoice >= 0 && opts != null)
                            ? ((char) ('A' + candidateChoice)) + ") " + opts.optString(candidateChoice)
                            : "Unanswered";

                    TextView ans = new TextView(this);
                    ans.setText("Your Answer: " + yourText + (isCorrect ? " [✓ Correct]" : " [✗ Incorrect]"));
                    ans.setTextColor(isCorrect ? 0xFF34D399 : 0xFFF87171);
                    ans.setTextSize(13);
                    ans.setPadding(0, 6, 0, 2);
                    item.addView(ans);

                    if (!isCorrect && opts != null) {
                        TextView corr = new TextView(this);
                        corr.setText("Designated Right Answer: " + ((char) ('A' + rightIdx)) + ") " + opts.optString(rightIdx));
                        corr.setTextColor(0xFF34D399);
                        corr.setTextSize(13);
                        item.addView(corr);
                    }

                    String explanation = q.optString("explanation");
                    if (!explanation.isEmpty()) {
                        TextView exp = new TextView(this);
                        exp.setText("💡 Explanation: " + explanation);
                        exp.setTextColor(0xFF94A3B8);
                        exp.setTextSize(12);
                        exp.setPadding(0, 6, 0, 0);
                        item.addView(exp);
                    }

                    container.addView(item);
                }
            }
        }

        Button btnHome = findViewById(R.id.btn_return_home);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, ExamListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
}
