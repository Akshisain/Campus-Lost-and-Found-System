package ui;

import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * UserDashboardFrame provides the student and faculty interface:
 * - Read-only browsing of campus lost & found keys and locks
 * - Multi-criteria search
 * - Detailed item view dialog
 * - Self-service password change
 * - Explicitly omits administrative CRUD controls
 */
public class UserDashboardFrame extends JFrame {

    private final User currentUser;
    private ManageItemsPanel directoryPanel;

    public UserDashboardFrame(User user) {
        super("Campus Lost Key & Lock Directory - Student & Staff Portal");
        this.currentUser = user;

        initComponents();

        setSize(1040, 700);
        setMinimumSize(new Dimension(880, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleLogout();
            }
        });
    }

    private void initComponents() {
        JPanel rootPanel = new JPanel(new BorderLayout(10, 10));
        rootPanel.setBackground(UIStyle.COLOR_BG_APP);
        rootPanel.setBorder(new EmptyBorder(12, 16, 12, 16));

        // Top Header
        JPanel headerCard = UIStyle.createCardPanel();
        headerCard.setLayout(new BorderLayout(15, 10));

        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 3, 3));
        leftPanel.setBackground(UIStyle.COLOR_CARD_BG);

        JLabel titleLbl = new JLabel("Campus Lost Key & Lock Directory");
        titleLbl.setFont(UIStyle.FONT_HEADER);
        titleLbl.setForeground(UIStyle.COLOR_PRIMARY_DARK);

        JLabel welcomeLbl = new JLabel("Welcome, " + currentUser.getFullName() + " | Poornima Institute of Engineering & Technology");
        welcomeLbl.setFont(UIStyle.FONT_REGULAR);
        welcomeLbl.setForeground(UIStyle.COLOR_TEXT_MUTED);

        leftPanel.add(titleLbl);
        leftPanel.add(welcomeLbl);

        // Right quick buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        rightPanel.setBackground(UIStyle.COLOR_CARD_BG);

        UIStyle.StyledButton changePwdBtn = UIStyle.createSecondaryButton("Change Password");
        changePwdBtn.addActionListener(e -> showChangePassword());

        UIStyle.StyledButton logoutBtn = UIStyle.createDangerButton("Logout");
        logoutBtn.addActionListener(e -> handleLogout());

        rightPanel.add(changePwdBtn);
        rightPanel.add(logoutBtn);

        headerCard.add(leftPanel, BorderLayout.WEST);
        headerCard.add(rightPanel, BorderLayout.EAST);

        // Instructions banner
        JLabel instructionsLabel = new JLabel("<html><body style='color:#475569; font-size:11px;'>"
                + "Search and check reported lost or found keys and locks below. If you recognize an item, "
                + "click <b>'View Record Details'</b> or double-click the record row to see contact and claiming information."
                + "</body></html>");
        instructionsLabel.setBorder(new EmptyBorder(6, 0, 0, 0));
        headerCard.add(instructionsLabel, BorderLayout.SOUTH);

        rootPanel.add(headerCard, BorderLayout.NORTH);

        // Center Content: Directory Panel (User Mode - Read Only)
        directoryPanel = new ManageItemsPanel(currentUser);
        rootPanel.add(directoryPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerBar = new JPanel(new BorderLayout());
        footerBar.setBackground(UIStyle.COLOR_BG_APP);
        footerBar.setBorder(new EmptyBorder(4, 4, 4, 4));

        JLabel studentLabel = new JLabel("PIET Minor Project NSP | Student:Achint Sharma   (PIET25CR002)");
        studentLabel.setFont(UIStyle.FONT_SMALL);
        studentLabel.setForeground(UIStyle.COLOR_TEXT_MUTED);

        JLabel userRoleLabel = new JLabel("Role: Campus User (Read-Only Directory Access)");
        userRoleLabel.setFont(UIStyle.FONT_SMALL);
        userRoleLabel.setForeground(UIStyle.COLOR_PRIMARY_DARK);

        footerBar.add(studentLabel, BorderLayout.WEST);
        footerBar.add(userRoleLabel, BorderLayout.EAST);
        rootPanel.add(footerBar, BorderLayout.SOUTH);

        setContentPane(rootPanel);
    }

    private void showChangePassword() {
        ChangePasswordDialog dialog = new ChangePasswordDialog(this, currentUser);
        dialog.setVisible(true);
    }

    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to log out of the system?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }
}
