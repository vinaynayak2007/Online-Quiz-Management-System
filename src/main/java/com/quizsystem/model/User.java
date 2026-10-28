package com.quizsystem.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;

/**
 * CO-1: Base class representing an authenticated user/account in the system.
 * Demonstrates Object-Oriented Inheritance with specialized Candidate and Admin subclasses.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
    defaultImpl = User.class
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Candidate.class, name = "CANDIDATE"),
    @JsonSubTypes.Type(value = AdminUser.class, name = "ADMIN")
})
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String id;
    protected String name;
    protected String phone;
    protected String password;
    protected String role; // "STUDENT" or "ADMIN"
    protected long registeredAt;

    public User() {
    }

    public User(String id, String name, String phone, String password, String role, long registeredAt) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.registeredAt = registeredAt;
    }

    // Common polymorphic methods
    public boolean isAdministrator() {
        return "ADMIN".equalsIgnoreCase(this.role);
    }

    public String getDisplayName() {
        return this.name + " (" + this.phone + ")";
    }

    // Getters and Setters
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

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", role='" + role + '\'' +
                ", registeredAt=" + registeredAt +
                '}';
    }
}
