package dao;

import model.Admin;
import model.Student;
import model.User;
import util.DBConnection;
import util.HashUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDAO (Data Access Object) — handles all database operations for users.
 * Keeps database logic separate from UI and business logic. Clean design!
 */
public class UserDAO {

    /**
     * Tries to find a user by username + password.
     * Returns the User object if found, null if not found.
     */
    public User findUserByCredentials(String username, String password) throws SQLException {
        String hashedPassword = HashUtil.sha256(password); // hash the input password

        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return buildUser(rs);
                }
            }
        }
        return null;
    }

    /**
     * Fetches a user by username only.
     */
    public User findUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return buildUser(rs);
                }
            }
        }
        return null;
    }

    /**
     * Updates the last login timestamp for a user.
     */
    public void updateLastLogin(String username) throws SQLException {
        String sql = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.executeUpdate();
        }
    }

    /**
     * Increments the failed_attempts counter for a user by 1.
     */
    public void incrementFailedAttempts(String username) throws SQLException {
        String sql = "UPDATE users SET failed_attempts = failed_attempts + 1 WHERE username = ?";
    
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, username);
            int rows = stmt.executeUpdate();
            System.out.println("DEBUG → Rows updated for [" + username + "]: " + rows);
        }
    }

    /**
     * Resets failed_attempts back to 0 for a user.
     */
    public void resetFailedAttempts(String username) throws SQLException {
        String sql = "UPDATE users SET failed_attempts = 0 WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.executeUpdate();
        }
    }

    /**
     * Resets failed_attempts for ALL users — used by background reset thread.
     */
    public void resetAllFailedAttempts() throws SQLException {
        String sql = "UPDATE users SET failed_attempts = 0";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    /**
     * Returns a list of all users — used by the Admin Panel.
     */
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(buildUser(rs));
            }
        }
        return users;
    }

    /**
     * Helper: converts a ResultSet row into the correct User subclass.
     */

    private User buildUser(ResultSet rs) throws SQLException {
        int id             = rs.getInt("id");
        String username    = rs.getString("username");
        String role        = rs.getString("role");
        int failedAttempts = rs.getInt("failed_attempts");
    
        User user;
    
        if ("ADMIN".equalsIgnoreCase(role)) {
            user = new Admin(id, username, failedAttempts);
        } else {
            user = new Student(id, username, failedAttempts);
        }
    
       user.setLastLogin(rs.getTimestamp("last_login"));
    
        return user;
    }
     
    public void createStudent(String username, String password) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, role, failed_attempts) VALUES (?, ?, 'STUDENT', 0)";
    
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, username);
            stmt.setString(2, HashUtil.sha256(password));
    
            stmt.executeUpdate();
        }
    }

    public void deleteUser(String username) throws SQLException {
        String sql = "DELETE FROM users WHERE username = ? AND role = 'STUDENT'";
    
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, username);
            stmt.executeUpdate();
        }
    }

    }
