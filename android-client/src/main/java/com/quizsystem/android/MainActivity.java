package com.quizsystem.android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fetch Quizzes from Firebase Firestore
        FirebaseHelper.getInstance().fetchQuizzes(new FirebaseHelper.DataCallback<List<Map<String, Object>>>() {
            @Override
            public void onSuccess(List<Map<String, Object>> quizzes) {
                Toast.makeText(MainActivity.this, "Loaded " + quizzes.size() + " quizzes from Firebase!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(MainActivity.this, "Firebase error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void startQuiz(String quizId) {
        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra("quizId", quizId);
        startActivity(intent);
    }
}
