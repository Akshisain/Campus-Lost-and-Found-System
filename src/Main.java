import ui.LoginFrame;

import javax.swing.*;

/**
 * Main Application Launcher for the Campus Lost Key & Lock Management System.
 *
 * Student Name: AKSHI MANOJ SAIN
 * Student ID:   PIET25CS065
 * College:      Poornima Institute of Engineering & Technology (PIET)
 * Course:       B.Tech II Year – III Semester Minor Project (NSP)
 * Session:      2026–27
 */
public class Main {

    public static void main(String[] args) {
        // Set cross-platform System Look & Feel for native OS aesthetics
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }

        // Launch application on Swing Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
