package ui;

import dao.UserDAO;
import db.DBConnection;
import model.User;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * LoginFrame provides the initial authentication gateway.
 * Authenticates against MySQL and routes users to either the Admin Dashboard or User Dashboard.
 */
public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private UIStyle.StyledButton loginButton;
    private UIStyle.StyledButton clearButton;
    private final UserDAO userDAO;

    public LoginFrame() {
        super("Campus Lost Key & Lock Management System - Login");
        this.userDAO = new UserDAO();

        initComponents();

        setSize(520, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(UIStyle.COLOR_BG_APP);
        mainPanel.setBorder(new EmptyBorder(25, 35, 25, 35));

        // Header Panel with College branding
        JPanel headerPanel = new JPanel(new GridLayout(3, 1, 4, 4));
        headerPanel.setBackground(UIStyle.COLOR_BG_APP);

        JLabel collegeLabel = new JLabel("Poornima Institute of Engineering & Technology", SwingConstants.CENTER);
        collegeLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        collegeLabel.setForeground(UIStyle.COLOR_SECONDARY);

        JLabel titleLabel = new JLabel("Lost Key & Lock Management", SwingConstants.CENTER);
        titleLabel.setFont(UIStyle.FONT_HEADER);
        titleLabel.setForeground(UIStyle.COLOR_PRIMARY_DARK);

        JLabel subLabel = new JLabel("B.Tech Minor Project NSP | Secure Authentication", SwingConstants.CENTER);
        subLabel.setFont(UIStyle.FONT_SMALL);
        subLabel.setForeground(UIStyle.COLOR_TEXT_MUTED);

        headerPanel.add(collegeLabel);
        headerPanel.add(titleLabel);
        headerPanel.add(subLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center: Login Card
        JPanel cardPanel = UIStyle.createCardPanel();
        cardPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel loginHeader = new JLabel("Sign In to Your Account", SwingConstants.CENTER);
        loginHeader.setFont(UIStyle.FONT_SUBHEADER);
        loginHeader.setForeground(UIStyle.COLOR_TEXT_MAIN);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 8, 18, 8);
        cardPanel.add(loginHeader, gbc);

        // Reset insets
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridwidth = 1;

        // Username
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel userLbl = new JLabel("Username:");
        userLbl.setFont(UIStyle.FONT_BOLD);
        cardPanel.add(userLbl, gbc);

        usernameField = new JTextField(18);
        UIStyle.styleTextField(usernameField);
        gbc.gridx = 1; gbc.weightx = 0.7;
        cardPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        JLabel passLbl = new JLabel("Password:");
        passLbl.setFont(UIStyle.FONT_BOLD);
        cardPanel.add(passLbl, gbc);

        passwordField = new JPasswordField(18);
        UIStyle.styleTextField(passwordField);
        gbc.gridx = 1; gbc.weightx = 0.7;
        cardPanel.add(passwordField, gbc);

        // Enter key listener on password field
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        });

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        buttonPanel.setBackground(UIStyle.COLOR_CARD_BG);

        loginButton = UIStyle.createPrimaryButton("Login");
        loginButton.addActionListener(e -> handleLogin());

        clearButton = UIStyle.createSecondaryButton("Clear");
        clearButton.addActionListener(e -> clearInputs());

        buttonPanel.add(loginButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(18, 8, 8, 8);
        cardPanel.add(buttonPanel, gbc);

        // Helper demo accounts banner
        JPanel demoBox = new JPanel(new GridLayout(3, 1, 3, 3));
        demoBox.setBackground(new Color(248, 250, 252));
        demoBox.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(8, 12, 8, 12)
        ));

        JLabel demoTitle = new JLabel("Evaluation Demo Credentials:");
        demoTitle.setFont(UIStyle.FONT_BOLD);
        demoTitle.setForeground(UIStyle.COLOR_PRIMARY_DARK);

        JLabel adminDemo = new JLabel("• Admin:  admin  /  admin123");
        adminDemo.setFont(UIStyle.FONT_SMALL);
        adminDemo.setForeground(UIStyle.COLOR_TEXT_MAIN);

        JLabel userDemo = new JLabel("• User:   student1  /  user123");
        userDemo.setFont(UIStyle.FONT_SMALL);
        userDemo.setForeground(UIStyle.COLOR_TEXT_MAIN);

        demoBox.add(demoTitle);
        demoBox.add(adminDemo);
        demoBox.add(userDemo);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(14, 8, 8, 8);
        cardPanel.add(demoBox, gbc);

        mainPanel.add(cardPanel, BorderLayout.CENTER);

        // Footer
        JLabel footerLabel = new JLabel("ACHINT SHARMA (PIET25CS002) | Session 2026–27", SwingConstants.CENTER);
        footerLabel.setFont(UIStyle.FONT_SMALL);
        footerLabel.setForeground(UIStyle.COLOR_TEXT_MUTED);
        mainPanel.add(footerLabel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // Validation
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter your username.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            usernameField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter your password.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            passwordField.requestFocus();
            return;
        }

        // Test DB connection first
        if (!DBConnection.testConnection()) {
            JOptionPane.showMessageDialog(this,
                    "Database connection failure! Please ensure MySQL Server is running on port 3306.",
                    "Connection Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Authenticate
        User user = userDAO.authenticate(username, password);

        if (user == null) {
            JOptionPane.showMessageDialog(this,
                    "Invalid username or password. Please check your credentials.",
                    "Authentication Failed", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            passwordField.requestFocus();
            return;
        }

        // Login success - Route by role
        dispose();
        if (user.isAdmin()) {
            SwingUtilities.invokeLater(() -> new AdminDashboardFrame(user).setVisible(true));
        } else {
            SwingUtilities.invokeLater(() -> new UserDashboardFrame(user).setVisible(true));
        }
    }

    private void clearInputs() {
        usernameField.setText("");
        passwordField.setText("");
        usernameField.requestFocus();
    }
}
