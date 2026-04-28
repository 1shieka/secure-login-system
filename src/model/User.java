package model;

import java.sql.Timestamp;

/**
 * Abstract base class for all users in the system.
 * Uses OOP concept: abstraction — we define common behavior here,
 * and let subclasses (Admin, Student) fill in the specifics.
 */
public abstract class User {

    // Common fields every user has
    private int id;
    private String username;
    private String role;
    private int failedAttempts;
    private Timestamp lastLogin; // Added field

    // Updated constructor to initialize common fields including lastLogin
    public User(int id, String username, String role, int failedAttempts) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.failedAttempts = failedAttempts;
    }

    // Abstract method — each subclass must implement this
    // This is polymorphism: same method name, different behavior per role
    public abstract boolean canAccessFeature(String feature);

    // --- Getters & Setters ---

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public int getFailedAttempts() { return failedAttempts; }
    public Timestamp getLastLogin() { 
        return lastLogin; 
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }


}