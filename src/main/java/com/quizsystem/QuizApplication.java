package com.quizsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QuizApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuizApplication.class, args);
        System.out.println("==================================================================");
        System.out.println("🚀 Online Quiz Management System (Java + Firebase) is RUNNING!");
        System.out.println("🌐 Web Dashboard & Quiz Player: http://localhost:8080");
        System.out.println("📡 REST APIs: http://localhost:8080/api/quizzes");
        System.out.println("🔥 Firebase Status API: http://localhost:8080/api/firebase/status");
        System.out.println("==================================================================");
    }
}
