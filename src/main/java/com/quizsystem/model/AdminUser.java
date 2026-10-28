package com.quizsystem.model;

import java.util.ArrayList;
import java.util.List;

/**
 * CO-1: AdminUser class demonstrating Inheritance in Java (AdminUser extends User).
 * Represents an administrator who manages questions, right answers, and monitors pass statistics.
 */
public class AdminUser extends User {
    private static final long serialVersionUID = 1L;

    private String adminLevel; // "SUPER_ADMIN", "INSTRUCTOR"
    private List<String> managedDepartments;

    public AdminUser() {
        super();
        this.role = "ADMIN";
        this.adminLevel = "SUPER_ADMIN";
        this.managedDepartments = new ArrayList<>();
    }

    public AdminUser(String id, String name, String phone, String password, long registeredAt, String adminLevel) {
        super(id, name, phone, password, "ADMIN", registeredAt);
        this.adminLevel = adminLevel;
        this.managedDepartments = new ArrayList<>();
    }

    @Override
    public boolean isAdministrator() {
        return true;
    }

    public boolean canEditQuestions() {
        return true;
    }

    @Override
    public String getDisplayName() {
        return "[Admin: " + this.adminLevel + "] " + this.name;
    }

    public String getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(String adminLevel) {
        this.adminLevel = adminLevel;
    }

    public List<String> getManagedDepartments() {
        return managedDepartments;
    }

    public void setManagedDepartments(List<String> managedDepartments) {
        this.managedDepartments = managedDepartments;
    }
}
