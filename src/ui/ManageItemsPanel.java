package ui;

import dao.LostItemDAO;
import model.LostItem;
import model.User;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * ManageItemsPanel provides the primary management UI:
 * - Interactive JTable with status badges and custom header renderer
 * - Multi-criteria search (keyword, item type, status)
 * - Complete CRUD form with validation (Add, Update, Delete)
 * - High-contrast StyledButton controls
 */
public class ManageItemsPanel extends JPanel {

    private final User currentUser;
    private final boolean isAdmin;
    private final LostItemDAO itemDAO;

    // Search bar components
    private JTextField searchField;
    private JComboBox<String> filterTypeCombo;
    private JComboBox<String> filterStatusCombo;
    private UIStyle.StyledButton searchButton;
    private UIStyle.StyledButton resetButton;

    // JTable components
    private JTable itemTable;
    private DefaultTableModel tableModel;
    private JLabel recordCountLabel;

    // Form components (for Admin)
    private JTextField idField;
    private JComboBox<String> itemTypeCombo;
    private JTextField descField;
    private JTextField locationField;
    private JTextField dateField;
    private JTextField reportedByField;
    private JTextField contactField;
    private JComboBox<String> statusCombo;

    // Action buttons
    private UIStyle.StyledButton addButton;
    private UIStyle.StyledButton updateButton;
    private UIStyle.StyledButton deleteButton;
    private UIStyle.StyledButton clearButton;
    private UIStyle.StyledButton markFoundButton;
    private UIStyle.StyledButton markReturnedButton;
    private UIStyle.StyledButton viewDetailsButton;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public ManageItemsPanel(User user) {
        this.currentUser = user;
        this.isAdmin = user != null && user.isAdmin();
        this.itemDAO = new LostItemDAO();

        DATE_FORMAT.setLenient(false);

        setLayout(new BorderLayout(10, 10));
        setBackground(UIStyle.COLOR_BG_APP);
        setBorder(new EmptyBorder(8, 8, 8, 8));

        initComponents();
        loadTableData();
    }

    private void initComponents() {
        // Top: Search and Filter Bar
        add(createSearchPanel(), BorderLayout.NORTH);

        // Center: JTable view
        add(createTablePanel(), BorderLayout.CENTER);

        // East: If Admin, display Form on East side
        if (isAdmin) {
            add(createFormPanel(), BorderLayout.EAST);
        } else {
            // For User, show bottom action bar with View Details
            JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
            bottomBar.setBackground(UIStyle.COLOR_BG_APP);
            viewDetailsButton = UIStyle.createPrimaryButton("View Record Details");
            viewDetailsButton.addActionListener(e -> showSelectedDetails());
            bottomBar.add(viewDetailsButton);
            add(bottomBar, BorderLayout.SOUTH);
        }
    }

    private JPanel createSearchPanel() {
        JPanel panel = UIStyle.createCardPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 6));

        JLabel searchLbl = new JLabel("Search:");
        searchLbl.setFont(UIStyle.FONT_BOLD);
        panel.add(searchLbl);

        searchField = new JTextField(14);
        UIStyle.styleTextField(searchField);
        searchField.setToolTipText("Search by description, location, reporter, or ID");
        searchField.addActionListener(e -> performSearch());
        panel.add(searchField);

        JLabel typeLbl = new JLabel("Type:");
        typeLbl.setFont(UIStyle.FONT_BOLD);
        panel.add(typeLbl);

        filterTypeCombo = new JComboBox<>(new String[]{"All", "Key", "Lock", "Key Ring/Set"});
        UIStyle.styleComboBox(filterTypeCombo);
        filterTypeCombo.addActionListener(e -> performSearch());
        panel.add(filterTypeCombo);

        JLabel statusLbl = new JLabel("Status:");
        statusLbl.setFont(UIStyle.FONT_BOLD);
        panel.add(statusLbl);

        filterStatusCombo = new JComboBox<>(new String[]{"All", "Lost", "Found", "Returned"});
        UIStyle.styleComboBox(filterStatusCombo);
        filterStatusCombo.addActionListener(e -> performSearch());
        panel.add(filterStatusCombo);

        searchButton = UIStyle.createPrimaryButton("Search");
        searchButton.addActionListener(e -> performSearch());
        panel.add(searchButton);

        resetButton = UIStyle.createSecondaryButton("Reset / Show All");
        resetButton.addActionListener(e -> resetSearch());
        panel.add(resetButton);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(UIStyle.COLOR_BG_APP);

        String[] columns = {"ID", "Type", "Description", "Location", "Date Reported", "Reported By", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only cells
            }
        };

        itemTable = new JTable(tableModel);
        UIStyle.styleTable(itemTable);

        // Column widths
        itemTable.getColumnModel().getColumn(0).setPreferredWidth(45);  // ID
        itemTable.getColumnModel().getColumn(1).setPreferredWidth(85);  // Type
        itemTable.getColumnModel().getColumn(2).setPreferredWidth(210); // Description
        itemTable.getColumnModel().getColumn(3).setPreferredWidth(140); // Location
        itemTable.getColumnModel().getColumn(4).setPreferredWidth(95);  // Date
        itemTable.getColumnModel().getColumn(5).setPreferredWidth(125); // Reporter
        itemTable.getColumnModel().getColumn(6).setPreferredWidth(85);  // Status

        // Selection listener
        itemTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showSelectedDetails();
                } else if (isAdmin) {
                    populateFormFromSelection();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(itemTable);
        scrollPane.setBorder(new LineBorder(UIStyle.COLOR_BORDER, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Table status bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(UIStyle.COLOR_BG_APP);
        recordCountLabel = new JLabel("Total Records: 0");
        recordCountLabel.setFont(UIStyle.FONT_SMALL);
        recordCountLabel.setForeground(UIStyle.COLOR_TEXT_MUTED);
        statusBar.add(recordCountLabel, BorderLayout.WEST);

        JLabel tipLabel = new JLabel("Double-click any row to view complete details");
        tipLabel.setFont(UIStyle.FONT_SMALL);
        tipLabel.setForeground(UIStyle.COLOR_TEXT_MUTED);
        statusBar.add(tipLabel, BorderLayout.EAST);

        panel.add(statusBar, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createFormPanel() {
        JPanel formCard = UIStyle.createCardPanel();
        formCard.setLayout(new GridBagLayout());
        formCard.setPreferredSize(new Dimension(390, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form Title
        JLabel formTitle = new JLabel("Record Details & Actions");
        formTitle.setFont(UIStyle.FONT_SUBHEADER);
        formTitle.setForeground(UIStyle.COLOR_PRIMARY_DARK);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(2, 5, 8, 5);
        formCard.add(formTitle, gbc);

        // Reset insets
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.gridwidth = 1;

        idField = new JTextField();
        idField.setEditable(false);
        idField.setBackground(new Color(241, 245, 249));
        UIStyle.styleTextField(idField);

        itemTypeCombo = new JComboBox<>(new String[]{"Key", "Lock", "Key Ring/Set"});
        UIStyle.styleComboBox(itemTypeCombo);

        descField = new JTextField(16);
        UIStyle.styleTextField(descField);

        locationField = new JTextField(16);
        UIStyle.styleTextField(locationField);

        dateField = new JTextField(10);
        UIStyle.styleTextField(dateField);
        dateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));

        reportedByField = new JTextField(16);
        UIStyle.styleTextField(reportedByField);

        contactField = new JTextField(16);
        UIStyle.styleTextField(contactField);

        statusCombo = new JComboBox<>(new String[]{"Lost", "Found", "Returned"});
        UIStyle.styleComboBox(statusCombo);

        // Add fields to form
        int row = 1;
        addFormField(formCard, gbc, row++, "Record ID:", idField);
        addFormField(formCard, gbc, row++, "Item Type *:", itemTypeCombo);
        addFormField(formCard, gbc, row++, "Description *:", descField);
        addFormField(formCard, gbc, row++, "Location *:", locationField);
        addFormField(formCard, gbc, row++, "Date Reported *:", dateField);
        addFormField(formCard, gbc, row++, "Reported By *:", reportedByField);
        addFormField(formCard, gbc, row++, "Contact Info *:", contactField);
        addFormField(formCard, gbc, row++, "Status *:", statusCombo);

        // Primary Action Buttons Grid
        JPanel buttonsGrid = new JPanel(new GridLayout(2, 2, 6, 6));
        buttonsGrid.setBackground(UIStyle.COLOR_CARD_BG);

        addButton = UIStyle.createPrimaryButton("Add Record");
        addButton.addActionListener(e -> handleAddRecord());

        updateButton = UIStyle.createAccentButton("Update Record");
        updateButton.addActionListener(e -> handleUpdateRecord());

        deleteButton = UIStyle.createDangerButton("Delete Record");
        deleteButton.addActionListener(e -> handleDeleteRecord());

        clearButton = UIStyle.createSecondaryButton("Clear Form");
        clearButton.addActionListener(e -> clearForm());

        buttonsGrid.add(addButton);
        buttonsGrid.add(updateButton);
        buttonsGrid.add(deleteButton);
        buttonsGrid.add(clearButton);

        gbc.gridx = 0; gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(8, 5, 4, 5);
        formCard.add(buttonsGrid, gbc);

        // Quick Status Transitions
        JPanel quickStatusPanel = new JPanel(new GridLayout(1, 2, 6, 6));
        quickStatusPanel.setBackground(UIStyle.COLOR_CARD_BG);

        markFoundButton = new UIStyle.StyledButton("Mark Found", new Color(2, 132, 199), Color.WHITE, new Color(3, 105, 161));
        markFoundButton.addActionListener(e -> handleQuickStatusChange("Found"));

        markReturnedButton = UIStyle.createSuccessButton("Mark Returned");
        markReturnedButton.addActionListener(e -> handleQuickStatusChange("Returned"));

        quickStatusPanel.add(markFoundButton);
        quickStatusPanel.add(markReturnedButton);

        gbc.gridx = 0; gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(4, 5, 4, 5);
        formCard.add(quickStatusPanel, gbc);

        // View Details button
        viewDetailsButton = UIStyle.createSecondaryButton("View Full Record Details");
        viewDetailsButton.addActionListener(e -> showSelectedDetails());

        gbc.gridx = 0; gbc.gridy = row++;
        gbc.gridwidth = 2;
        formCard.add(viewDetailsButton, gbc);

        return formCard;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent comp) {
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIStyle.FONT_BOLD);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        panel.add(comp, gbc);
    }

    public void loadTableData() {
        List<LostItem> items = itemDAO.getAllItems();
        populateTable(items);
    }

    public void filterProgrammatically(String keyword, String itemType, String status) {
        if (keyword != null) searchField.setText(keyword);
        if (itemType != null) filterTypeCombo.setSelectedItem(itemType);
        if (status != null) filterStatusCombo.setSelectedItem(status);
        performSearch();
    }

    private void populateTable(List<LostItem> items) {
        tableModel.setRowCount(0);
        for (LostItem it : items) {
            tableModel.addRow(new Object[]{
                    it.getRecordId(),
                    it.getItemType(),
                    it.getDescription(),
                    it.getLocation(),
                    it.getDateReported(),
                    it.getReportedBy(),
                    it.getStatus()
            });
        }
        recordCountLabel.setText("Total Records: " + items.size());
    }

    private void performSearch() {
        String keyword = searchField.getText().trim();
        String itemType = (String) filterTypeCombo.getSelectedItem();
        String status = (String) filterStatusCombo.getSelectedItem();

        List<LostItem> results = itemDAO.searchItems(keyword, itemType, status);
        populateTable(results);
    }

    private void resetSearch() {
        searchField.setText("");
        filterTypeCombo.setSelectedIndex(0);
        filterStatusCombo.setSelectedIndex(0);
        loadTableData();
    }

    private void populateFormFromSelection() {
        int selectedRow = itemTable.getSelectedRow();
        if (selectedRow >= 0) {
            int recordId = (int) tableModel.getValueAt(selectedRow, 0);
            LostItem item = itemDAO.getItemById(recordId);
            if (item != null) {
                idField.setText(String.valueOf(item.getRecordId()));
                itemTypeCombo.setSelectedItem(item.getItemType());
                descField.setText(item.getDescription());
                locationField.setText(item.getLocation());
                dateField.setText(item.getDateReported().toString());
                reportedByField.setText(item.getReportedBy());
                contactField.setText(item.getContactInfo());
                statusCombo.setSelectedItem(item.getStatus());
            }
        }
    }

    public void selectFirstRow() {
        if (itemTable.getRowCount() > 0) {
            itemTable.setRowSelectionInterval(0, 0);
            populateFormFromSelection();
        }
    }

    private void showSelectedDetails() {
        int selectedRow = itemTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a record from the table to view details.",
                    "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int recordId = (int) tableModel.getValueAt(selectedRow, 0);
        LostItem item = itemDAO.getItemById(recordId);
        if (item != null) {
            Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            ItemDetailsDialog dialog = new ItemDetailsDialog(topFrame, item);
            dialog.setVisible(true);
        }
    }

    private void handleAddRecord() {
        if (!validateForm()) return;

        try {
            String type = (String) itemTypeCombo.getSelectedItem();
            String desc = descField.getText().trim();
            String loc = locationField.getText().trim();
            Date date = new Date(DATE_FORMAT.parse(dateField.getText().trim()).getTime());
            String reportedBy = reportedByField.getText().trim();
            String contact = contactField.getText().trim();
            String status = (String) statusCombo.getSelectedItem();

            LostItem newItem = new LostItem(type, desc, loc, date, reportedBy, contact, status);
            int generatedId = itemDAO.addItem(newItem);

            if (generatedId > 0) {
                JOptionPane.showMessageDialog(this,
                        "Record added successfully! Assigned ID: #" + generatedId,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadTableData();

                // Refresh Admin dashboard metrics if inside Admin frame
                Window w = SwingUtilities.getWindowAncestor(this);
                if (w instanceof AdminDashboardFrame) {
                    ((AdminDashboardFrame) w).refreshStats();
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to add record to the database.",
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid date format. Please use YYYY-MM-DD.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleUpdateRecord() {
        String idStr = idField.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select a record from the table to update.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validateForm()) return;

        try {
            int id = Integer.parseInt(idStr);
            String type = (String) itemTypeCombo.getSelectedItem();
            String desc = descField.getText().trim();
            String loc = locationField.getText().trim();
            Date date = new Date(DATE_FORMAT.parse(dateField.getText().trim()).getTime());
            String reportedBy = reportedByField.getText().trim();
            String contact = contactField.getText().trim();
            String status = (String) statusCombo.getSelectedItem();

            LostItem updatedItem = new LostItem(id, type, desc, loc, date, reportedBy, contact, status);
            boolean ok = itemDAO.updateItem(updatedItem);

            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "Record #" + id + " updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTableData();

                Window w = SwingUtilities.getWindowAncestor(this);
                if (w instanceof AdminDashboardFrame) {
                    ((AdminDashboardFrame) w).refreshStats();
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to update record in MySQL database.",
                        "Update Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid date format. Please use YYYY-MM-DD.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid Record ID.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteRecord() {
        String idStr = idField.getText().trim();
        if (idStr.isEmpty()) {
            int selectedRow = itemTable.getSelectedRow();
            if (selectedRow >= 0) {
                idStr = String.valueOf(tableModel.getValueAt(selectedRow, 0));
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please select a record from the table to delete.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        int id = Integer.parseInt(idStr);
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete record #" + id + "?\nThis operation cannot be undone.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            boolean deleted = itemDAO.deleteItem(id);
            if (deleted) {
                JOptionPane.showMessageDialog(this,
                        "Record #" + id + " has been deleted.",
                        "Deleted", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadTableData();

                Window w = SwingUtilities.getWindowAncestor(this);
                if (w instanceof AdminDashboardFrame) {
                    ((AdminDashboardFrame) w).refreshStats();
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Could not delete record from MySQL database.",
                        "Delete Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleQuickStatusChange(String targetStatus) {
        String idStr = idField.getText().trim();
        if (idStr.isEmpty()) {
            int selectedRow = itemTable.getSelectedRow();
            if (selectedRow >= 0) {
                idStr = String.valueOf(tableModel.getValueAt(selectedRow, 0));
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please select a record first.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        int id = Integer.parseInt(idStr);
        boolean ok = itemDAO.updateStatus(id, targetStatus);
        if (ok) {
            statusCombo.setSelectedItem(targetStatus);
            JOptionPane.showMessageDialog(this,
                    "Status for record #" + id + " updated to '" + targetStatus + "'!",
                    "Status Updated", JOptionPane.INFORMATION_MESSAGE);
            loadTableData();

            Window w = SwingUtilities.getWindowAncestor(this);
            if (w instanceof AdminDashboardFrame) {
                ((AdminDashboardFrame) w).refreshStats();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to update status in database.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateForm() {
        if (descField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Description cannot be empty.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            descField.requestFocus();
            return false;
        }

        if (locationField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Campus location cannot be empty.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            locationField.requestFocus();
            return false;
        }

        String dateStr = dateField.getText().trim();
        if (dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Date reported cannot be empty.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            dateField.requestFocus();
            return false;
        }

        try {
            DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid date format! Please enter date as YYYY-MM-DD (e.g. 2026-09-09).",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            dateField.requestFocus();
            return false;
        }

        if (reportedByField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Reported By cannot be empty.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            reportedByField.requestFocus();
            return false;
        }

        if (contactField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Contact info cannot be empty.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            contactField.requestFocus();
            return false;
        }

        return true;
    }

    public void clearForm() {
        idField.setText("");
        itemTypeCombo.setSelectedIndex(0);
        descField.setText("");
        locationField.setText("");
        dateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        reportedByField.setText("");
        contactField.setText("");
        statusCombo.setSelectedIndex(0);
        itemTable.clearSelection();
    }
}
