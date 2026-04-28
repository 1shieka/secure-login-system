import ui.LoginUI;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        // Launch Swing UI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            LoginUI loginUI = new LoginUI();
            loginUI.setVisible(true);
        });
    }
}
