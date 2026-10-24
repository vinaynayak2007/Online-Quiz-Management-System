package com.quizsystem.android;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Firebase Firestore Helper for Android Java Client
 */
public class FirebaseHelper {

    private static FirebaseHelper instance;
    private final FirebaseFirestore db;

    private FirebaseHelper() {
        this.db = FirebaseFirestore.getInstance();
    }

    public static synchronized FirebaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseHelper();
        }
        return instance;
    }

    public interface DataCallback<T> {
        void onSuccess(T data);
        void onFailure(Exception e);
    }

    public void fetchQuizzes(DataCallback<List<Map<String, Object>>> callback) {
        db.collection("quizzes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Map<String, Object>> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Map<String, Object> data = doc.getData();
                        data.put("id", doc.getId());
                        list.add(data);
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void submitAttempt(Map<String, Object> attemptData, DataCallback<String> callback) {
        db.collection("attempts")
                .add(attemptData)
                .addOnSuccessListener(documentReference -> callback.onSuccess(documentReference.getId()))
                .addOnFailureListener(callback::onFailure);
    }
}
