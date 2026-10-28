package com.quizsystem.model;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String name;
    private String phone;
    private String password;
    private String role; // "STUDENT" or "ADMIN"
    private long registeredAt;
    private int examsTaken;
    private int examsPassed;

    public User() {
    }

    public User(String id, String name, String phone, String password, String role, long registeredAt) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.registeredAt = registeredAt;
        this.examsTaken = 0;
        this.examsPassed = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public long getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(long registeredAt) {
        this.registeredAt = registeredAt;
    }

    public int getExamsTaken() {
        return examsTaken;
    }

    public void setExamsTaken(int examsTaken) {
        this.examsTaken = examsTaken;
    }

    public int getExamsPassed() {
        return examsPassed;
    }

    public void setExamsPassed(int examsPassed) {
        this.examsPassed = examsPassed;
    }
}
