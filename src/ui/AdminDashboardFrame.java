package ui;

import dao.LostItemDAO;
import model.User;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * AdminDashboardFrame provides the full administrative command center for the
 * Campus Lost Key & Lock Management System.
 */
public class AdminDashboardFrame extends JFrame {

    private final User adminUser;
    private final LostItemDAO itemDAO;

    private JLabel statTotalLabel;
    private JLabel statLostLabel;
    private JLabel statFoundLabel;
    private JLabel statReturnedLabel;

    private JTabbedPane tabbedPane;
    private ManageItemsPanel managementPanel;

    public AdminDashboardFrame(User adminUser) {
        super("Campus Lost Key & Lock Management System - Admin Portal");
        this.adminUser = adminUser;
        this.itemDAO = new LostItemDAO();

        initComponents();
        refreshStats();

        setSize(1180, 760);
        setMinimumSize(new Dimension(1000, 680));
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
        setJMenuBar(createMenuBar());

        JPanel rootPanel = new JPanel(new BorderLayout(10, 10));
        rootPanel.setBackground(UIStyle.COLOR_BG_APP);
        rootPanel.setBorder(new EmptyBorder(12, 16, 12, 16));

        // Top Header Banner
        rootPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Center Content Tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIStyle.FONT_BOLD);
        tabbedPane.setBackground(Color.WHITE);

        managementPanel = new ManageItemsPanel(adminUser);
        tabbedPane.addTab("  Lost Key & Lock Management (CRUD)  ", managementPanel);

        rootPanel.add(tabbedPane, BorderLayout.CENTER);

        // Footer info bar
        JPanel footerBar = new JPanel(new BorderLayout());
        footerBar.setBackground(UIStyle.COLOR_BG_APP);
        footerBar.setBorder(new EmptyBorder(4, 4, 4, 4));

        JLabel studentLabel = new JLabel("PIET Minor Project NSP 2026-27 | Student: ACHINT SHARMA (PIET25CR002) | 3CSD");
        studentLabel.setFont(UIStyle.FONT_SMALL);
        studentLabel.setForeground(UIStyle.COLOR_TEXT_MUTED);

        JLabel statusLabel = new JLabel("MySQL Database: campus_lost_key_db | Connected (Port 3306)");
        statusLabel.setFont(UIStyle.FONT_SMALL);
        statusLabel.setForeground(UIStyle.COLOR_SUCCESS);

        footerBar.add(studentLabel, BorderLayout.WEST);
        footerBar.add(statusLabel, BorderLayout.EAST);
        rootPanel.add(footerBar, BorderLayout.SOUTH);

        setContentPane(rootPanel);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // System Menu
        JMenu systemMenu = new JMenu("System");
        systemMenu.setFont(UIStyle.FONT_BOLD);

        JMenuItem refreshItem = new JMenuItem("Refresh Dashboard");
        refreshItem.addActionListener(e -> {
            managementPanel.loadTableData();
            refreshStats();
        });

        JMenuItem changePwdItem = new JMenuItem("Change Password");
        changePwdItem.addActionListener(e -> showChangePassword());

        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> handleLogout());

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));

        systemMenu.add(refreshItem);
        systemMenu.addSeparator();
        systemMenu.add(changePwdItem);
        systemMenu.add(logoutItem);
        systemMenu.addSeparator();
        systemMenu.add(exitItem);

        // Records Menu
        JMenu recordsMenu = new JMenu("Records");
        recordsMenu.setFont(UIStyle.FONT_BOLD);

        JMenuItem clearFormItem = new JMenuItem("Clear Entry Form");
        clearFormItem.addActionListener(e -> managementPanel.clearForm());

        recordsMenu.add(clearFormItem);

        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setFont(UIStyle.FONT_BOLD);

        JMenuItem aboutItem = new JMenuItem("About Project");
        aboutItem.addActionListener(e -> showAboutDialog());

        helpMenu.add(aboutItem);

        menuBar.add(systemMenu);
        menuBar.add(recordsMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    private JPanel createHeaderPanel() {
        JPanel headerCard = UIStyle.createCardPanel();
        headerCard.setLayout(new BorderLayout(15, 12));

        // Left title & user welcome
        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 3, 3));
        leftPanel.setBackground(UIStyle.COLOR_CARD_BG);

        JLabel titleLbl = new JLabel("Campus Lost Key & Lock Management System");
        titleLbl.setFont(UIStyle.FONT_HEADER);
        titleLbl.setForeground(UIStyle.COLOR_PRIMARY_DARK);

        JLabel welcomeLbl = new JLabel("Administrator: " + adminUser.getFullName() + " | Poornima Institute of Engineering & Technology");
        welcomeLbl.setFont(UIStyle.FONT_REGULAR);
        welcomeLbl.setForeground(UIStyle.COLOR_TEXT_MUTED);

        leftPanel.add(titleLbl);
        leftPanel.add(welcomeLbl);

        // Right quick buttons with high-contrast StyledButton
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

        // Statistics Metric Cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 12, 0));
        statsPanel.setBackground(UIStyle.COLOR_CARD_BG);
        statsPanel.setBorder(new EmptyBorder(8, 0, 0, 0));

        statTotalLabel = new JLabel("0");
        statLostLabel = new JLabel("0");
        statFoundLabel = new JLabel("0");
        statReturnedLabel = new JLabel("0");

        statsPanel.add(createStatCard("Total Records", statTotalLabel, UIStyle.COLOR_PRIMARY_DARK));
        statsPanel.add(createStatCard("Active Lost Keys/Locks", statLostLabel, UIStyle.COLOR_DANGER));
        statsPanel.add(createStatCard("Found / Recovered", statFoundLabel, UIStyle.COLOR_PRIMARY));
        statsPanel.add(createStatCard("Returned to Owner", statReturnedLabel, UIStyle.COLOR_SUCCESS));

        headerCard.add(statsPanel, BorderLayout.SOUTH);
        return headerCard;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(new Color(248, 250, 252));
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(8, 12, 8, 12)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UIStyle.FONT_SMALL);
        titleLbl.setForeground(UIStyle.COLOR_TEXT_MUTED);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(accentColor);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    public void refreshStats() {
        int total = itemDAO.getTotalCount();
        int lost = itemDAO.getCountByStatus("Lost");
        int found = itemDAO.getCountByStatus("Found");
        int returned = itemDAO.getCountByStatus("Returned");

        statTotalLabel.setText(String.valueOf(total));
        statLostLabel.setText(String.valueOf(lost));
        statFoundLabel.setText(String.valueOf(found));
        statReturnedLabel.setText(String.valueOf(returned));
    }

    private void showChangePassword() {
        ChangePasswordDialog dialog = new ChangePasswordDialog(this, adminUser);
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

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "<html><body style='width:340px; font-family: Segoe UI;'>"
                + "<h2 style='color:#1E3A8A; margin-bottom:2px;'>Campus Lost Key & Lock Management System</h2>"
                + "<p><b>Minor Project (NSP) - B.Tech II Year / III Sem</b></p>"
                + "<hr/>"
                + "<p><b>Student:</b> AKSHI MANOJ SAIN<br/>"
                + "<b>Student ID:</b> PIET25CS065<br/>"
                + "<b>Section:</b> 3CSD<br/>"
                + "<b>College:</b> Poornima Institute of Engineering & Technology<br/>"
                + "<b>Session:</b> 2026–27</p>"
                + "<hr/>"
                + "<p style='color:#475569;'>Built strictly with Java Swing, JDBC, and MySQL.<br/>"
                + "Simple, functional, polished, and PIET compliant.</p>"
                + "</body></html>",
                "About Project", JOptionPane.INFORMATION_MESSAGE);
    }
}
