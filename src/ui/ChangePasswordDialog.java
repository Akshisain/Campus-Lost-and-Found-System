package ui;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * ChangePasswordDialog allows logged-in Admin or User to securely update their password.
 */
public class ChangePasswordDialog extends JDialog {

    private final User currentUser;
    private final UserDAO userDAO;

    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private UIStyle.StyledButton updateButton;
    private UIStyle.StyledButton cancelButton;

    public ChangePasswordDialog(Frame owner, User user) {
        super(owner, "Change Password - " + user.getUsername(), true);
        this.currentUser = user;
        this.userDAO = new UserDAO();

        initComponents();
        pack();
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 15));
        mainPanel.setBackground(UIStyle.COLOR_BG_APP);
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Header
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 3, 3));
        headerPanel.setBackground(UIStyle.COLOR_BG_APP);
        JLabel titleLabel = new JLabel("Update Account Password");
        titleLabel.setFont(UIStyle.FONT_HEADER);
        titleLabel.setForeground(UIStyle.COLOR_TEXT_MAIN);

        JLabel userLabel = new JLabel("Logged in as: " + currentUser.getFullName() + " (" + currentUser.getRole() + ")");
        userLabel.setFont(UIStyle.FONT_REGULAR);
        userLabel.setForeground(UIStyle.COLOR_TEXT_MUTED);

        headerPanel.add(titleLabel);
        headerPanel.add(userLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form fields in card
        JPanel cardPanel = UIStyle.createCardPanel();
        cardPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        currentPasswordField = new JPasswordField(18);
        newPasswordField = new JPasswordField(18);
        confirmPasswordField = new JPasswordField(18);

        UIStyle.styleTextField(currentPasswordField);
        UIStyle.styleTextField(newPasswordField);
        UIStyle.styleTextField(confirmPasswordField);

        // Current Password
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel lblCur = new JLabel("Current Password:");
        lblCur.setFont(UIStyle.FONT_BOLD);
        cardPanel.add(lblCur, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        cardPanel.add(currentPasswordField, gbc);

        // New Password
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel lblNew = new JLabel("New Password:");
        lblNew.setFont(UIStyle.FONT_BOLD);
        cardPanel.add(lblNew, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        cardPanel.add(newPasswordField, gbc);

        // Confirm New Password
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        JLabel lblConf = new JLabel("Confirm Password:");
        lblConf.setFont(UIStyle.FONT_BOLD);
        cardPanel.add(lblConf, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        cardPanel.add(confirmPasswordField, gbc);

        mainPanel.add(cardPanel, BorderLayout.CENTER);

        // Action Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(UIStyle.COLOR_BG_APP);

        cancelButton = UIStyle.createSecondaryButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        updateButton = UIStyle.createPrimaryButton("Update Password");
        updateButton.addActionListener(e -> handlePasswordUpdate());

        buttonPanel.add(cancelButton);
        buttonPanel.add(updateButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);
    }

    private void handlePasswordUpdate() {
        String currentPwd = new String(currentPasswordField.getPassword()).trim();
        String newPwd = new String(newPasswordField.getPassword()).trim();
        String confPwd = new String(confirmPasswordField.getPassword()).trim();

        if (currentPwd.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter your current password.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            currentPasswordField.requestFocus();
            return;
        }

        if (newPwd.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a new password.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            newPasswordField.requestFocus();
            return;
        }

        if (newPwd.length() < 4) {
            JOptionPane.showMessageDialog(this,
                    "New password must be at least 4 characters long.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            newPasswordField.requestFocus();
            return;
        }

        if (!newPwd.equals(confPwd)) {
            JOptionPane.showMessageDialog(this,
                    "New password and Confirmation password do not match.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            confirmPasswordField.requestFocus();
            return;
        }

        boolean success = userDAO.changePassword(currentUser.getUserId(), currentPwd, newPwd);
        if (success) {
            currentUser.setPassword(newPwd);
            JOptionPane.showMessageDialog(this,
                    "Password successfully updated!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Current password incorrect. Please try again.",
                    "Update Failed", JOptionPane.ERROR_MESSAGE);
            currentPasswordField.selectAll();
            currentPasswordField.requestFocus();
        }
    }
}
