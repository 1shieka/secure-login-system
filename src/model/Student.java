package model;

/**
 * Student user — limited access. Cannot access admin-only features.
 * Demonstrates polymorphism: same method, different behavior than Admin.
 */
public class Student extends User {

    public Student(int id, String username, int failedAttempts) {
        super(id, username, "STUDENT", failedAttempts);
    }

    /**
     * Student can only access "dashboard" and "profile".
     * Blocked from "admin_panel" and "reports".
     */
    @Override
    public boolean canAccessFeature(String feature) {
        // Only allow these specific features for students
        return feature.equals("dashboard") || feature.equals("profile");
    }
}
