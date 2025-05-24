package com.coffeemate.view;

import com.coffeemate.dao.MenuItemDAO;
import com.coffeemate.model.MenuItem;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class MenuPanel extends JPanel {
    private JTable table;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh, btnSearch;
    private JTextField txtSearch;
    private final MenuItemDAO menuItemDAO = MenuItemDAO.getInstance();

    private JSpinner pageSpinner;
    private JLabel lblTotalPages;
    private int currentPage = 1;
    private int rowsPerPage = 10;
    private int totalPages = 1;
    private ArrayList<MenuItem> allItems;

    public MenuPanel() {
        FlatLightLaf.setup();
        UIManager.put("Button.arc", 999);
        UIManager.put("Component.arc", 8);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.showVerticalLines", false);

        setLayout(new BorderLayout());
        setFont(new Font("Segoe UI", Font.PLAIN, 14));

        initComponents();
        initActions();
        updateMenuTable();
    }

    private void initComponents() {
        JPanel panelActions = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        panelActions.setBackground(Color.WHITE);

        txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnAdd     = createButtonWithEmoji("➕", "Thêm Món",  new Color(0x4CAF50));
        btnEdit    = createButtonWithEmoji("✏️", "Sửa",       new Color(0xFF9800));
        btnDelete  = createButtonWithEmoji("🗑️", "Xóa",       new Color(0xF44336));
        btnRefresh = createButtonWithEmoji("🔄", "Làm Mới",   new Color(0x2196F3));
        btnSearch  = createButtonWithEmoji("🔍", "Tìm",       new Color(0x03A9F4));

        panelActions.add(btnAdd);
        panelActions.add(btnEdit);
        panelActions.add(btnDelete);
        panelActions.add(btnRefresh);
        panelActions.add(txtSearch);
        panelActions.add(btnSearch);

        add(panelActions, BorderLayout.NORTH);

        table = new JTable();
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.setFillsViewportHeight(true);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(new Color(220, 220, 220));
        table.setSelectionBackground(new Color(232, 234, 246));
        table.setSelectionForeground(Color.BLACK);
        table.setShowGrid(true);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 247, 250));
                }
                setHorizontalAlignment(column == 0 || column == 2 ? JLabel.CENTER : JLabel.LEFT);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(scrollPane, BorderLayout.CENTER);

        // Pagination panel
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        paginationPanel.setBackground(Color.WHITE);

        JButton btnPrev = new JButton("« Trước");
        JButton btnNext = new JButton("Sau »");
        lblTotalPages = new JLabel("/ 1");
        pageSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1, 1));

        paginationPanel.add(btnPrev);
        paginationPanel.add(new JLabel("Trang:"));
        paginationPanel.add(pageSpinner);
        paginationPanel.add(lblTotalPages);
        paginationPanel.add(btnNext);

        add(paginationPanel, BorderLayout.SOUTH);

        btnPrev.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                updateTableWithPaging();
            }
        });

        btnNext.addActionListener(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                updateTableWithPaging();
            }
        });

        pageSpinner.addChangeListener(e -> {
            int value = (int) pageSpinner.getValue();
            if (value >= 1 && value <= totalPages) {
                currentPage = value;
                updateTableWithPaging();
            }
        });
    }

    private void initActions() {
        btnAdd    .addActionListener(e -> openAddDialog());
        btnEdit   .addActionListener(e -> openEditDialog());
        btnDelete .addActionListener(e -> deleteMenu());
        btnRefresh.addActionListener(e -> updateMenuTable());
        btnSearch .addActionListener(e -> searchMenu());
    }

    private JButton createButtonWithEmoji(String emoji, String text, Color color) {
        JButton button = new JButton();
        button.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 5));
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);

        JLabel emojiLabel = new JLabel(emoji);
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        textLabel.setForeground(Color.WHITE);

        JPanel content = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        content.setOpaque(false);
        content.add(emojiLabel);
        content.add(textLabel);

        button.add(content);
        return button;
    }

    private void openAddDialog() {
        MenuItemDialog dialog = new MenuItemDialog(
            SwingUtilities.getWindowAncestor(this),
            "Thêm Món",
            null
        );
        dialog.setVisible(true);
        updateMenuTable();
    }

    private void openEditDialog() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn món cần sửa.");
            return;
        }
        int id = (int) table.getValueAt(row, 0);
        String name = (String) table.getValueAt(row, 1);
        float price = Float.parseFloat(table.getValueAt(row, 2).toString());
        String desc = (String) table.getValueAt(row, 3);
        String status = (String) table.getValueAt(row, 4);

        MenuItem item = new MenuItem(id, name, price, desc, status);
        MenuItemDialog dialog = new MenuItemDialog(
            SwingUtilities.getWindowAncestor(this),
            "Sửa Món",
            item
        );
        dialog.setVisible(true);
        updateMenuTable();
    }

    private void deleteMenu() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn món cần xóa.");
            return;
        }
        int id = (int) table.getValueAt(row, 0);
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn xóa/ngưng bán món này?",
            "Xác nhận",
            JOptionPane.YES_NO_OPTION
        );
        if (choice == JOptionPane.YES_OPTION) {
            if (menuItemDAO.deleteOrDisableMenuItem(id)) {
                JOptionPane.showMessageDialog(this, "Thao tác thành công!");
                updateMenuTable();
            } else {
                JOptionPane.showMessageDialog(this, "Thao tác thất bại. Hãy kiểm tra lại!");
            }
        }
    }

    private void searchMenu() {
        String kw = txtSearch.getText().trim().toLowerCase();
        allItems = menuItemDAO.searchByName(kw);
        currentPage = 1;
        totalPages = (int) Math.ceil((double) allItems.size() / rowsPerPage);
        if (totalPages < 1) totalPages = 1;
        pageSpinner.setModel(new SpinnerNumberModel(currentPage, 1, totalPages, 1));
        lblTotalPages.setText("/ " + totalPages);
        updateTableWithPaging();
    }

    private void updateMenuTable() {
        allItems = menuItemDAO.selectAll();
        allItems.sort(Comparator.comparingInt(MenuItem::getMenuItemID));
        totalPages = (int) Math.ceil((double) allItems.size() / rowsPerPage);
        if (totalPages < 1) totalPages = 1;
        pageSpinner.setModel(new SpinnerNumberModel(currentPage, 1, totalPages, 1));
        lblTotalPages.setText("/ " + totalPages);
        updateTableWithPaging();
    }

    private void updateTableWithPaging() {
        String[] cols = {"ID", "Tên Món", "Giá", "Mô Tả", "Trạng Thái"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        int start = (currentPage - 1) * rowsPerPage;
        int end = Math.min(start + rowsPerPage, allItems.size());
        for (int i = start; i < end; i++) {
            MenuItem mi = allItems.get(i);
            model.addRow(new Object[]{
                mi.getMenuItemID(),
                mi.getItemName(),
                mi.getPrice(),
                mi.getDescription(),
                mi.getStatus()
            });
        }
        table.setModel(model);
        pageSpinner.setValue(currentPage);
        lblTotalPages.setText("/ " + totalPages);
    }
}
