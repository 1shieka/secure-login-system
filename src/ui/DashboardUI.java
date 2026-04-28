package ui;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * DashboardUI — shown after successful login.
 * Content changes based on the user's role (Admin vs Student).
 * This is role-based access control in action!
 */
public class DashboardUI extends JFrame {

    private final User user; // the logged-in user

    public DashboardUI(User user) {
        User freshUser;
        try {
            // fetch updated user from DB
            freshUser = new UserDAO().findUserByUsername(user.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
            freshUser = user; // fallback
        }
    
        this.user = freshUser;
    
        setupWindow();
        buildUI();
    }

    private void setupWindow() {
        setTitle("Dashboard — " + user.getUsername() + " (" + user.getRole() + ")");
        setSize(600, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(30, 30, 46));
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));

        // --- Top header panel ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(49, 50, 68));
        header.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel welcome = new JLabel("Welcome, " + user.getUsername() + "!");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 18));
        welcome.setForeground(new Color(205, 214, 244));

        JLabel roleLabel = new JLabel("Role: " + user.getRole());
        roleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        roleLabel.setForeground(new Color(166, 227, 161));

        header.add(welcome, BorderLayout.WEST);
        header.add(roleLabel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // --- Center: tabbed panels ---
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(new Color(30, 30, 46));
        tabs.setForeground(new Color(205, 214, 244));

        // Dashboard tab — everyone sees this
        tabs.addChangeListener(e -> {
            tabs.setComponentAt(0, buildDashboardTab());
        });
        tabs.addTab("📋 Dashboard", buildDashboardTab());

        // Admin panel — only if user can access it (polymorphism used here)
        if (user.canAccessFeature("admin_panel")) {
            tabs.addTab("🔧 Admin Panel", buildAdminTab());
        }

        add(tabs, BorderLayout.CENTER);

        // --- Logout button ---
        JButton logout = new JButton("Logout");
        logout.setBackground(new Color(243, 139, 168));
        logout.setForeground(new Color(30, 30, 46));
        logout.setFont(new Font("SansSerif", Font.BOLD, 13));
        logout.setFocusPainted(false);
        logout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logout.addActionListener(e -> {
            dispose();
            new LoginUI().setVisible(true); // back to login screen
        });

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(new Color(30, 30, 46));
        footer.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));
        footer.add(logout);
        add(footer, BorderLayout.SOUTH);
    }

    /** General dashboard — visible to all logged-in users */
    private JPanel buildDashboardTab() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(30, 30, 46));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        String[] infoLines = {
            " Student Username   : " + user.getUsername(),
            " Your Role       : " + user.getRole(),
            " Total Failed Attempts : " + user.getFailedAttempts(),
        
            "",
            "Your current account is active.",
        };

        for (String line : infoLines) {
            JLabel lbl = new JLabel(line);
            lbl.setFont(new Font("Monospaced", Font.PLAIN, 14));
            lbl.setForeground(new Color(205, 214, 244));
            lbl.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
            panel.add(lbl);
        }

        return panel;
    }

    /**
     * Admin Panel — shows all users and their failed attempt counts.
     * Only visible to Admin users (controlled by canAccessFeature()).
     */
    private JPanel buildAdminTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 30, 46));

        JLabel heading = new JLabel("  All Registered Users", SwingConstants.LEFT);
        heading.setFont(new Font("SansSerif", Font.BOLD, 14));
        heading.setForeground(new Color(137, 180, 250));
        heading.setBorder(BorderFactory.createEmptyBorder(10, 10, 6, 10));
        panel.add(heading, BorderLayout.NORTH);

        // Table columns
        String[] cols = {"ID", "Username", "Role", "Failed Attempts", "Last Login"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        // Load all users from DB
        try {
            UserDAO dao = new UserDAO();
            List<User> users = dao.getAllUsers();

            for (User u : users) {
                String lastLogin = (u.getLastLogin() != null)
                        ? u.getLastLogin().toString()
                        : "Never";
            
                model.addRow(new Object[]{
                    u.getId(),
                    u.getUsername(),
                    u.getRole(),
                    u.getFailedAttempts(),
                    lastLogin   
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Failed to load users: " + e.getMessage(),
                "DB Error", JOptionPane.ERROR_MESSAGE);
        }

        JTable table = new JTable(model);

        JButton deleteBtn = new JButton("Delete Selected User");
        deleteBtn.setBackground(new Color(243, 139, 168));
        deleteBtn.setForeground(new Color(30, 30, 46));
        deleteBtn.setFocusPainted(false);
        deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        deleteBtn.addActionListener(e -> {
          int row = table.getSelectedRow();

          if (row == -1) {
             JOptionPane.showMessageDialog(this, "Select a user first.");
             return;
           }

           String username = model.getValueAt(row, 1).toString();
           String role = model.getValueAt(row, 2).toString();

           if (role.equalsIgnoreCase("ADMIN")) {
             JOptionPane.showMessageDialog(this, "Cannot delete admin user.");
             return;
           }

    int confirm = JOptionPane.showConfirmDialog(
        this,
        "Delete user '" + username + "'?",
        "Confirm Delete",
        JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
        try {
            new UserDAO().deleteUser(username);
            model.removeRow(row); // update UI instantly
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting user.");
        }
    }
});
        table.setBackground(new Color(49, 50, 68));
        table.setForeground(new Color(205, 214, 244));
        table.setGridColor(new Color(88, 91, 112));
        table.getTableHeader().setBackground(new Color(88, 91, 112));
        table.getTableHeader().setForeground(new Color(205, 214, 244));
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setRowHeight(24);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(new Color(30, 30, 46));
        bottomPanel.add(deleteBtn);
        
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
}