package model;

/**
 * Admin user — has access to all features including the admin panel.
 * Extends User (inheritance) and provides its own canAccessFeature logic.
 */
public class Admin extends User {

    public Admin(int id, String username, int failedAttempts) {
        super(id, username, "ADMIN", failedAttempts);
    }

    /**
     * Admin can access everything: dashboard, admin_panel, reports, etc.
     */
    @Override
    public boolean canAccessFeature(String feature) {
        // Admin has full access — no restrictions on feature name
        return true;
    }
}
