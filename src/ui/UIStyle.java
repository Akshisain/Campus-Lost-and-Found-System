package ui;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * UIStyle encapsulates colors, fonts, borders, and custom components
 * ensuring high-contrast, clean, and accessible UI across all Swing screens.
 *
 * Custom StyledButton completely bypasses Windows Look & Feel native button rendering
 * bugs so button text and backgrounds are always distinct, crisp, and high-contrast.
 */
public class UIStyle {

    // Palette Colors
    public static final Color COLOR_PRIMARY = new Color(37, 99, 235);       // #2563EB - Royal Blue
    public static final Color COLOR_PRIMARY_DARK = new Color(30, 58, 138);  // #1E3A8A - Deep Navy
    public static final Color COLOR_SUCCESS = new Color(22, 163, 74);       // #16A34A - Emerald Green
    public static final Color COLOR_DANGER = new Color(220, 38, 38);        // #DC2626 - Crimson Red
    public static final Color COLOR_WARNING = new Color(217, 119, 6);       // #D97706 - Amber/Orange
    public static final Color COLOR_SECONDARY = new Color(100, 116, 139);   // #64748B - Slate Gray

    public static final Color COLOR_BG_APP = new Color(241, 245, 249);      // #F1F5F9 - Soft Gray-Blue
    public static final Color COLOR_CARD_BG = Color.WHITE;
    public static final Color COLOR_BORDER = new Color(203, 213, 225);      // #CBD5E1 - Light Slate
    public static final Color COLOR_TEXT_MAIN = new Color(15, 23, 42);      // #0F172A - Very Dark Slate
    public static final Color COLOR_TEXT_MUTED = new Color(100, 116, 139);  // #64748B
    public static final Color COLOR_TABLE_HEADER = new Color(30, 41, 59);   // #1E293B

    // Fonts
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    /**
     * Custom high-contrast button that guarantees text visibility and background colors
     * regardless of underlying OS or Look & Feel quirks.
     */
    public static class StyledButton extends JButton {
        private final Color normalBg;
        private final Color hoverBg;
        private final Color pressedBg;
        private final Color borderColor;

        public StyledButton(String text, Color bg, Color fg, Color border) {
            super(text);
            this.normalBg = bg;
            this.borderColor = border;
            this.hoverBg = blend(bg, Color.WHITE, 0.15f);
            this.pressedBg = blend(bg, Color.BLACK, 0.15f);

            setForeground(fg);
            setFont(FONT_BOLD);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(getPreferredSize().width, 34));
        }

        private static Color blend(Color c1, Color c2, float ratio) {
            float r = (1 - ratio) * c1.getRed() + ratio * c2.getRed();
            float g = (1 - ratio) * c1.getGreen() + ratio * c2.getGreen();
            float b = (1 - ratio) * c1.getBlue() + ratio * c2.getBlue();
            return new Color(Math.round(r), Math.round(g), Math.round(b));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            ButtonModel model = getModel();
            Color currentBg = normalBg;
            if (model.isPressed()) {
                currentBg = pressedBg;
            } else if (model.isRollover()) {
                currentBg = hoverBg;
            }

            // Fill background with subtle rounded rectangle
            g2.setColor(currentBg);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);

            // Draw border
            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
            }

            // Draw text centered with explicit foreground color
            FontMetrics fm = g2.getFontMetrics(getFont());
            Rectangle stringBounds = fm.getStringBounds(getText(), g2).getBounds();
            int textX = (getWidth() - stringBounds.width) / 2;
            int textY = (getHeight() - stringBounds.height) / 2 + fm.getAscent() - 1;

            g2.setFont(getFont());
            g2.setColor(getForeground());
            g2.drawString(getText(), textX, textY);

            g2.dispose();
        }
    }

    public static StyledButton createPrimaryButton(String text) {
        return new StyledButton(text, COLOR_PRIMARY, Color.WHITE, COLOR_PRIMARY_DARK);
    }

    public static StyledButton createSuccessButton(String text) {
        return new StyledButton(text, COLOR_SUCCESS, Color.WHITE, new Color(21, 128, 61));
    }

    public static StyledButton createDangerButton(String text) {
        return new StyledButton(text, COLOR_DANGER, Color.WHITE, new Color(185, 28, 28));
    }

    public static StyledButton createSecondaryButton(String text) {
        return new StyledButton(text, new Color(241, 245, 249), COLOR_TEXT_MAIN, COLOR_BORDER);
    }

    public static StyledButton createAccentButton(String text) {
        return new StyledButton(text, new Color(14, 116, 144), Color.WHITE, new Color(21, 94, 117));
    }

    /**
     * Styles input text fields
     */
    public static void styleTextField(JTextField tf) {
        tf.setFont(FONT_REGULAR);
        tf.setForeground(COLOR_TEXT_MAIN);
        tf.setCaretColor(COLOR_TEXT_MAIN);
        tf.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1),
                new EmptyBorder(6, 8, 6, 8)
        ));
    }

    /**
     * Styles combo boxes
     */
    public static void styleComboBox(JComboBox<?> cb) {
        cb.setFont(FONT_REGULAR);
        cb.setBackground(Color.WHITE);
        cb.setForeground(COLOR_TEXT_MAIN);
    }

    /**
     * Creates a standardized card panel with white background and subtle border.
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(COLOR_CARD_BG);
        panel.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1),
                new EmptyBorder(12, 16, 12, 16)
        ));
        return panel;
    }

    /**
     * Standardizes a JTable with bold headers, alternating rows, and centered status badges.
     */
    public static void styleTable(JTable table) {
        table.setFont(FONT_REGULAR);
        table.setRowHeight(30);
        table.setGridColor(new Color(226, 232, 240));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(COLOR_TEXT_MAIN);
        table.setShowGrid(true);
        table.setShowVerticalLines(false);

        // Explicit Header Renderer to bypass Windows L&F header color overrides
        TableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFoc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, val, isSel, hasFoc, row, col);
                lbl.setBackground(COLOR_TABLE_HEADER);
                lbl.setForeground(Color.WHITE);
                lbl.setFont(FONT_BOLD);
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
                lbl.setBorder(new EmptyBorder(8, 8, 8, 8));
                lbl.setOpaque(true);
                return lbl;
            }
        };

        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        // Custom Cell Renderer for Status Column and Alternating Rows
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSelected,
                                                           boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, isSelected, hasFocus, row, col);
                setBorder(new EmptyBorder(0, 8, 0, 8));

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                }

                // Status Column badge coloring
                if (col == 6 && val != null) {
                    String status = val.toString();
                    if ("Lost".equalsIgnoreCase(status)) {
                        c.setForeground(COLOR_DANGER);
                        setFont(FONT_BOLD);
                    } else if ("Found".equalsIgnoreCase(status)) {
                        c.setForeground(COLOR_PRIMARY);
                        setFont(FONT_BOLD);
                    } else if ("Returned".equalsIgnoreCase(status)) {
                        c.setForeground(COLOR_SUCCESS);
                        setFont(FONT_BOLD);
                    } else {
                        c.setForeground(COLOR_TEXT_MAIN);
                    }
                } else {
                    c.setForeground(COLOR_TEXT_MAIN);
                }

                return c;
            }
        });
    }
}
