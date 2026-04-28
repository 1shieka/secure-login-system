package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection — creates and returns a MySQL database connection.
 * We keep this in one place so all classes share the same connection setup.
 */
public class DBConnection {

    
    private static final String URL      = "jdbc:mysql://localhost:3306/secure_login_db";
    private static final String USER     = "root";       // your MySQL username
    private static final String PASSWORD = "yourpassword";  // your MySQL password

    /**
     * Returns a live Connection object to the database.
     * Throws SQLException if connection fails (we handle it in callers).
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
