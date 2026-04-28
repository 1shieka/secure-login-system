package service;

import dao.UserDAO;

import java.sql.SQLException;

/**
 * SecurityAnalyzer — runs a background thread that automatically resets
 * failed login attempts every 2 minutes. This simulates a "timeout unlock".
 *
 * Demonstrates basic multithreading with a daemon thread.
 */
public class SecurityAnalyzer implements Runnable {

    private final UserDAO userDAO = new UserDAO();

    // How long to wait before resetting (2 minutes = 120,000 ms)
    private static final long RESET_INTERVAL_MS = 2 * 60 * 1000;

    private volatile boolean running = true; // volatile = safely shared across threads

    /**
     * This method runs in a separate background thread.
     * Every 2 minutes, it resets all failed login attempt counters.
     */
    @Override
    public void run() {
        System.out.println("[SecurityAnalyzer] Background reset thread started.");

        while (running) {
            try {
                // Wait for the reset interval
                Thread.sleep(RESET_INTERVAL_MS);

                // Reset all failed attempts in the database
                userDAO.resetAllFailedAttempts();
                System.out.println("[SecurityAnalyzer] Failed attempts reset for all users.");

            } catch (InterruptedException e) {
                // Thread was interrupted — stop gracefully
                System.out.println("[SecurityAnalyzer] Thread interrupted, stopping.");
                Thread.currentThread().interrupt();
                break;

            } catch (SQLException e) {
                System.err.println("[SecurityAnalyzer] DB error during reset: " + e.getMessage());
            }
        }
    }

    /**
     * Starts the analyzer as a daemon thread.
     * Daemon threads automatically stop when the main app closes.
     */
    public void startInBackground() {
        Thread thread = new Thread(this);
        thread.setDaemon(true); // stops automatically when app exits
        thread.setName("SecurityAnalyzer-Thread");
        thread.start();
    }

    /**
     * Stops the background thread gracefully.
     */
    public void stop() {
        running = false;
    }
}