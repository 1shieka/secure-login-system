package service;

import dao.UserDAO;
import model.User;

import java.sql.SQLException;

/**
 * LoginService — handles the login logic.
 * Separates business rules from UI.
 */
public class LoginService {

    private final UserDAO userDAO = new UserDAO();

    // Max failed attempts before blocking
    private static final int MAX_ATTEMPTS = 3;

    // Store logged-in user
    private User loggedInUser;

    /**
     * Possible login results returned to UI
     */
    public enum LoginResult {
        SUCCESS,
        INVALID_CREDENTIALS,
        ACCOUNT_BLOCKED,
        ERROR
    }

    /**
     * Main login method
     */
    public LoginResult login(String username, String password) {

        if (username == null || username.isBlank() ||
            password == null || password.isBlank()) {
            return LoginResult.ERROR;
        }
    
        try {
            // Step 1: get user first
            User existing = userDAO.findUserByUsername(username);

            if (existing == null) {

               if (username.equalsIgnoreCase("admin")) {
                    return LoginResult.INVALID_CREDENTIALS;
                }

                // Create ONLY student account
                userDAO.createStudent(username, password);
            
                User newUser = userDAO.findUserByUsername(username);
                this.loggedInUser = newUser;
            
                return LoginResult.SUCCESS;
            }
    
    
            // Step 2: temporary block logic 
             if (existing.getFailedAttempts() >= MAX_ATTEMPTS) {

             long now = System.currentTimeMillis();

              long lastAttemptTime = (existing.getLastLogin() != null)
              ? existing.getLastLogin().getTime()
              : 0;

              long diff = now - lastAttemptTime;

              // 2 minutes = 120000 ms
              if (diff < 120000) {
              return LoginResult.ACCOUNT_BLOCKED;
             }
            }
    
            // Step 3: check credentials
            User user = userDAO.findUserByCredentials(username, password);
    
            if (user == null) {
                // Step 1: increment
                userDAO.incrementFailedAttempts(username);
                
            
                // Step 2: FORCE fresh read from DB
                User updated = userDAO.findUserByUsername(username);
            
                System.out.println("Debug Failed Attempts: " + updated.getFailedAttempts()); 
            
                if (updated.getFailedAttempts() >= MAX_ATTEMPTS) {
                    return LoginResult.ACCOUNT_BLOCKED;
                }
            
                return LoginResult.INVALID_CREDENTIALS;
            }
    
            // correct password → reset + update login time

            userDAO.updateLastLogin(username);
    
            // fetch fresh user
            User freshUser = userDAO.findUserByUsername(username);

    
            this.loggedInUser = freshUser; // ALWAYS fetch latest
            return LoginResult.SUCCESS;
    
        } catch (SQLException e) {
            e.printStackTrace();
            return LoginResult.ERROR;
        }
    }
    /**
     * Get logged-in user (used by Dashboard)
     */
    public User getLoggedInUser() {
        return loggedInUser;
    }
}