package ui;

import model.User;
import service.LoginService;
import service.LoginService.LoginResult;

import javax.swing.*;
import java.awt.*;

/**
 * LoginUI — the login window built with Java Swing.
 * Clean, minimal design. Calls LoginService to verify credentials.
 */
public class LoginUI extends JFrame {

    // UI components
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;
    private JButton loginButton;

    private final LoginService loginService = new LoginService();

    public LoginUI() {
        setupWindow();
        buildUI();
    }

    /** Configure the main JFrame settings */
    private void setupWindow() {
        setTitle("Secure Login System");
        setSize(420, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center on screen
        setResizable(false);
        getContentPane().setBackground(new Color(30, 30, 46));
    }

    /** Build and add all UI components */
    private void buildUI() {
        // Main panel with padding
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(30, 30, 46));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        // --- Title ---
        JLabel title = new JLabel("🔐 Secure Login", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(205, 214, 244));
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(title, gbc);

        // --- Username label + field ---
        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        panel.add(makeLabel("Username:"), gbc);

        usernameField = new JTextField(15);
        styleTextField(usernameField);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        // --- Password label + field ---
        gbc.gridy = 2; gbc.gridx = 0;
        panel.add(makeLabel("Password:"), gbc);

        passwordField = new JPasswordField(15);
        styleTextField(passwordField);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // --- Login button ---
        loginButton = new JButton("Login");
        loginButton.setBackground(new Color(137, 180, 250));
        loginButton.setForeground(new Color(30, 30, 46));
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> handleLogin());

        gbc.gridy = 3; gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(loginButton, gbc);

        // --- Status/Error label ---
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(243, 139, 168)); // red-ish for errors
        gbc.gridy = 4;
        panel.add(statusLabel, gbc);

        add(panel);
    }

    /** Called when the Login button is clicked */
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // Basic empty input check
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("⚠ Please enter username and password.");
            return;
        }

        // Disable button while processing
        loginButton.setEnabled(false);
        statusLabel.setForeground(new Color(166, 227, 161));
        statusLabel.setText("Checking...");

        // Call the login service
        LoginResult result = loginService.login(username, password);

        switch (result) {
                    case SUCCESS -> {
                        User user = loginService.getLoggedInUser();
                        statusLabel.setText("✅ Welcome, " + user.getUsername() + "!");
                        // Open dashboard after short delay
                        Timer timer = new Timer(600, e -> {
                            dispose(); // close login window
                            new DashboardUI(user).setVisible(true);
                        });
                        timer.setRepeats(false);
                        timer.start();
                    }
                    case INVALID_CREDENTIALS -> {
                        statusLabel.setForeground(new Color(243, 139, 168));
                        statusLabel.setText("❌ Invalid username or password.");
                        loginButton.setEnabled(true);
                    }
                    case ACCOUNT_BLOCKED -> {
                        statusLabel.setForeground(new Color(250, 179, 135));
                        statusLabel.setText("🚫 Account blocked. Try again in 2 minutes.");
                        loginButton.setEnabled(true);
                    }
                    case ERROR -> {
                        statusLabel.setForeground(new Color(243, 139, 168));
                        statusLabel.setText("⚠ System error. Check DB connection.");
                        loginButton.setEnabled(true);
                    }
                    default -> throw new IllegalArgumentException("Unexpected value: " + result);
        }
    }

    // --- Helper methods ---

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(new Color(186, 194, 222));
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return lbl;
    }

    private void styleTextField(JTextField field) {
        field.setBackground(new Color(49, 50, 68));
        field.setForeground(new Color(205, 214, 244));
        field.setCaretColor(new Color(205, 214, 244));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(88, 91, 112)),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
    }
}
