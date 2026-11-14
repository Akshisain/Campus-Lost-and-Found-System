package ui;

import model.LostItem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * ItemDetailsDialog displays a structured, readable view of a selected Key or Lock record.
 * Accessible to both standard campus Users and Admins.
 */
public class ItemDetailsDialog extends JDialog {

    public ItemDetailsDialog(Frame owner, LostItem item) {
        super(owner, "Record Details #" + item.getRecordId() + " - " + item.getItemType(), true);
        initComponents(item);
        pack();
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private void initComponents(LostItem item) {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(UIStyle.COLOR_BG_APP);
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout(5, 5));
        headerPanel.setBackground(UIStyle.COLOR_BG_APP);

        JLabel titleLabel = new JLabel("Lost Item Record #" + item.getRecordId());
        titleLabel.setFont(UIStyle.FONT_HEADER);
        titleLabel.setForeground(UIStyle.COLOR_PRIMARY_DARK);

        JLabel statusLabel = new JLabel("Status: " + item.getStatus().toUpperCase());
        statusLabel.setFont(UIStyle.FONT_SUBHEADER);
        if ("Lost".equalsIgnoreCase(item.getStatus())) {
            statusLabel.setForeground(UIStyle.COLOR_DANGER);
        } else if ("Found".equalsIgnoreCase(item.getStatus())) {
            statusLabel.setForeground(UIStyle.COLOR_PRIMARY);
        } else {
            statusLabel.setForeground(UIStyle.COLOR_SUCCESS);
        }

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(statusLabel, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Details Grid in a Card
        JPanel cardPanel = UIStyle.createCardPanel();
        cardPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        addDetailRow(cardPanel, gbc, 0, "Record ID:", String.valueOf(item.getRecordId()));
        addDetailRow(cardPanel, gbc, 1, "Item Type:", item.getItemType());
        addDetailRow(cardPanel, gbc, 2, "Description:", item.getDescription());
        addDetailRow(cardPanel, gbc, 3, "Location:", item.getLocation());
        addDetailRow(cardPanel, gbc, 4, "Date Reported:", item.getDateReported().toString());
        addDetailRow(cardPanel, gbc, 5, "Reported By:", item.getReportedBy());
        addDetailRow(cardPanel, gbc, 6, "Contact Info:", item.getContactInfo());
        addDetailRow(cardPanel, gbc, 7, "Current Status:", item.getStatus());

        // Instruction Box
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 10, 6, 10);
        JLabel noteLabel = new JLabel("<html><body style='width:360px; color:#475569; font-size:10px;'>"
                + "<b>Note:</b> To claim a found key/lock or verify ownership, please contact the Main Campus "
                + "Security Desk or the reporting person with appropriate identification.</body></html>");
        cardPanel.add(noteLabel, gbc);

        mainPanel.add(cardPanel, BorderLayout.CENTER);

        // Close Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(UIStyle.COLOR_BG_APP);
        UIStyle.StyledButton closeBtn = UIStyle.createSecondaryButton("Close");
        closeBtn.addActionListener(e -> dispose());
        buttonPanel.add(closeBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);
    }

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.35;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIStyle.FONT_BOLD);
        lbl.setForeground(UIStyle.COLOR_TEXT_MAIN);
        panel.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 0.65;
        JLabel val = new JLabel(value != null ? value : "N/A");
        val.setFont(UIStyle.FONT_REGULAR);
        val.setForeground(UIStyle.COLOR_TEXT_MAIN);
        panel.add(val, gbc);
    }
}
