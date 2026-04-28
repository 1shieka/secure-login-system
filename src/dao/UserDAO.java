package dao;

import model.Admin;
import model.Student;
import model.User;
import util.DBConnection;
import util.HashUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User findUserByCredentials(String username, String password) throws SQLException {
        String hashedPassword = HashUtil.sha256(password);

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

    public void updateLastLogin(String username) throws SQLException {
        String sql = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.executeUpdate();
        }
    }

    public void incrementFailedAttempts(String username) throws SQLException {
    String sql = "UPDATE users SET failed_attempts = failed_attempts + 1 WHERE username = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        conn.setAutoCommit(true); // 🔥 ensure commit

        stmt.setString(1, username);
        int rows = stmt.executeUpdate();

        System.out.println("DEBUG increment → " + username + " rows: " + rows);
    }
}


    // ✅ FIXED: only reset affected users
    public void resetAllFailedAttempts() throws SQLException {
        String sql = "UPDATE users SET failed_attempts = 0 WHERE failed_attempts > 0";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            int rows = stmt.executeUpdate(sql);
            System.out.println("[SecurityAnalyzer] Reset users: " + rows);
        }
    }

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

    private User buildUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String role = rs.getString("role");
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