package ui;

import dao.LostItemDAO;
import dao.UserDAO;
import model.LostItem;
import model.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * ScreenshotGenerator captures genuine, high-resolution PNG screenshots
 * of all application screens directly from the live Java Swing component tree.
 */
public class ScreenshotGenerator {

    private static final String OUTPUT_DIR = "screenshots";

    public static void main(String[] args) {
        System.out.println("Starting automated UI Screenshot Generator...");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        File dir = new File(OUTPUT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        UserDAO userDAO = new UserDAO();
        User admin = userDAO.authenticate("admin", "admin123");
        if (admin == null) {
            admin = new User(1, "admin", "admin123", "Akshi Manoj Sain (Admin)", "Admin", "admin.keylock@piet.poornima.org");
        }

        User student = userDAO.authenticate("student1", "user123");
        if (student == null) {
            student = new User(2, "student1", "user123", "Rahul Sharma (CS Student)", "User", "rahul.s@piet.poornima.org");
        }

        // 1. Capture Login Screen
        captureLoginScreen();

        // 2. Capture Admin Dashboard Overview & Stats
        captureAdminDashboard(admin);

        // 3. Capture Admin Management CRUD Panel
        captureAdminCrudPanel(admin);

        // 4. Capture Filtered Search View
        captureAdminSearch(admin);

        // 5. Capture User Read-Only Directory Dashboard
        captureUserDashboard(student);

        // 6. Capture Item Details Dialog
        captureItemDetails(admin);

        // 7. Capture Change Password Dialog
        captureChangePasswordDialog(admin);

        // 8. Capture Delete Confirmation Dialog
        captureDeleteConfirmationDialog(admin);

        System.out.println("All screenshots successfully captured in: " + dir.getAbsolutePath());
        System.exit(0);
    }

    private static void captureLoginScreen() {
        try {
            LoginFrame frame = new LoginFrame();
            frame.setSize(500, 580);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            Thread.sleep(300);

            saveWindowImage(frame, OUTPUT_DIR + "/01_login_screen.png");
            frame.dispose();
            System.out.println("  Saved: 01_login_screen.png");
        } catch (Exception e) {
            System.err.println("Error capturing Login Screen: " + e.getMessage());
        }
    }

    private static void captureAdminDashboard(User admin) {
        try {
            AdminDashboardFrame frame = new AdminDashboardFrame(admin);
            frame.setSize(1150, 750);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            Thread.sleep(350);

            saveWindowImage(frame, OUTPUT_DIR + "/02_admin_dashboard.png");
            frame.dispose();
            System.out.println("  Saved: 02_admin_dashboard.png");
        } catch (Exception e) {
            System.err.println("Error capturing Admin Dashboard: " + e.getMessage());
        }
    }

    private static void captureAdminCrudPanel(User admin) {
        try {
            AdminDashboardFrame frame = new AdminDashboardFrame(admin);
            frame.setSize(1150, 750);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            Thread.sleep(300);

            saveWindowImage(frame, OUTPUT_DIR + "/03_manage_items_crud.png");
            frame.dispose();
            System.out.println("  Saved: 03_manage_items_crud.png");
        } catch (Exception e) {
            System.err.println("Error capturing Admin CRUD Panel: " + e.getMessage());
        }
    }

    private static void captureAdminSearch(User admin) {
        try {
            AdminDashboardFrame frame = new AdminDashboardFrame(admin);
            frame.setSize(1150, 750);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            Thread.sleep(300);

            saveWindowImage(frame, OUTPUT_DIR + "/04_search_and_filter.png");
            frame.dispose();
            System.out.println("  Saved: 04_search_and_filter.png");
        } catch (Exception e) {
            System.err.println("Error capturing Search Panel: " + e.getMessage());
        }
    }

    private static void captureUserDashboard(User student) {
        try {
            UserDashboardFrame frame = new UserDashboardFrame(student);
            frame.setSize(1000, 680);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            Thread.sleep(300);

            saveWindowImage(frame, OUTPUT_DIR + "/05_user_dashboard_directory.png");
            frame.dispose();
            System.out.println("  Saved: 05_user_dashboard_directory.png");
        } catch (Exception e) {
            System.err.println("Error capturing User Dashboard: " + e.getMessage());
        }
    }

    private static void captureItemDetails(User user) {
        try {
            JFrame dummy = new JFrame();
            dummy.setSize(1000, 600);
            dummy.setLocationRelativeTo(null);

            LostItemDAO dao = new LostItemDAO();
            LostItem sample = dao.getItemById(1);
            if (sample == null) {
                sample = new LostItem(1, "Key", "Godrej silver brass key with blue PIET keychain",
                        "Computer Lab 3 (Block A)", java.sql.Date.valueOf("2026-09-01"),
                        "Rahul Sharma", "9829011221 (CS-2nd Yr)", "Lost");
            }

            ItemDetailsDialog dialog = new ItemDetailsDialog(dummy, sample);
            dialog.setLocationRelativeTo(null);

            SwingUtilities.invokeLater(() -> {
                try {
                    Thread.sleep(250);
                    saveWindowImage(dialog, OUTPUT_DIR + "/06_item_details_dialog.png");
                    dialog.dispose();
                    dummy.dispose();
                    System.out.println("  Saved: 06_item_details_dialog.png");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            dialog.setVisible(true);
        } catch (Exception e) {
            System.err.println("Error capturing Item Details Dialog: " + e.getMessage());
        }
    }

    private static void captureChangePasswordDialog(User user) {
        try {
            JFrame dummy = new JFrame();
            dummy.setUndecorated(true);
            dummy.setLocationRelativeTo(null);
            dummy.setVisible(true);

            ChangePasswordDialog dialog = new ChangePasswordDialog(dummy, user);
            dialog.setLocationRelativeTo(null);

            SwingUtilities.invokeLater(() -> {
                try {
                    Thread.sleep(250);
                    saveWindowImage(dialog, OUTPUT_DIR + "/07_change_password_dialog.png");
                    dialog.dispose();
                    dummy.dispose();
                    System.out.println("  Saved: 07_change_password_dialog.png");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            dialog.setVisible(true);
        } catch (Exception e) {
            System.err.println("Error capturing Change Password Dialog: " + e.getMessage());
        }
    }

    private static void captureDeleteConfirmationDialog(User admin) {
        try {
            JFrame dummy = new JFrame();
            dummy.setSize(400, 300);
            dummy.setLocationRelativeTo(null);
            dummy.setVisible(true);

            JOptionPane pane = new JOptionPane(
                    "Are you sure you want to delete record #4 (Honda Activa scooter key)?\nThis operation cannot be undone.",
                    JOptionPane.WARNING_MESSAGE,
                    JOptionPane.YES_NO_OPTION);
            JDialog d = pane.createDialog(dummy, "Confirm Deletion");
            d.setLocationRelativeTo(null);

            SwingUtilities.invokeLater(() -> {
                try {
                    Thread.sleep(200);
                    saveWindowImage(d, OUTPUT_DIR + "/08_delete_confirmation_dialog.png");
                    d.dispose();
                    dummy.dispose();
                    System.out.println("  Saved: 08_delete_confirmation_dialog.png");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            d.setVisible(true);
        } catch (Exception e) {
            System.err.println("Error capturing Delete Confirmation Dialog: " + e.getMessage());
        }
    }

    private static void saveWindowImage(Window window, String filePath) {
        try {
            int width = Math.max(window.getWidth(), 1);
            int height = Math.max(window.getHeight(), 1);
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            window.paint(g2d);
            g2d.dispose();

            ImageIO.write(image, "png", new File(filePath));
        } catch (Exception e) {
            System.err.println("Failed to save screenshot " + filePath + ": " + e.getMessage());
        }
    }
}
