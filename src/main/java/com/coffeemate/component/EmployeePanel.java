package com.coffeemate.component;

import com.coffeemate.controller.EmployeeController;
import com.coffeemate.model.Employee;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EmployeePanel extends JPanel {
    private JTable table;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh, btnSearch;
    private JTextField txtSearch;
    private final EmployeeController employeeController = new EmployeeController();

    public EmployeePanel() {
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
        updateEmployeeTable();
    }

    private void initComponents() {
        JPanel panelActions = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        panelActions.setBackground(Color.WHITE);

        txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnAdd     = createButtonWithEmoji("➕", "Thêm", new Color(0x4CAF50));
        btnEdit    = createButtonWithEmoji("✏️", "Sửa", new Color(0xFF9800));
        btnDelete  = createButtonWithEmoji("🗑️", "Xóa", new Color(0xF44336));
        btnRefresh = createButtonWithEmoji("🔄", "Làm Mới", new Color(0x2196F3));
        btnSearch  = createButtonWithEmoji("🔍", "Tìm", new Color(0x03A9F4));

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

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void initActions() {
        btnAdd    .addActionListener(e -> EmployeeDialog.showCreateDialog(this, employeeController, this::updateEmployeeTable));
        btnEdit   .addActionListener(e -> EmployeeDialog.showUpdateDialog(this, table, employeeController, this::updateEmployeeTable));
        btnDelete .addActionListener(e -> EmployeeDialog.showDeleteDialog(this, table, employeeController, this::updateEmployeeTable));
        btnRefresh.addActionListener(e -> updateEmployeeTable());
        btnSearch .addActionListener(e -> EmployeeDialog.showSearchDialog(this, table, employeeController));
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

   private void updateEmployeeTable() {
    List<Employee> list = employeeController.getAllEmployees();
    // Thêm cột "Mật khẩu" vào đây
    String[] cols = {"ID", "Họ tên", "Vai trò", "SDT", "Email", "Ngày tham gia", "Mật khẩu", "Trạng thái"};
    DefaultTableModel model = new DefaultTableModel(cols, 0);

    for (Employee emp : list) {
        // Lấy thêm mật khẩu từ emp.getPassword()
        Object[] rowData = new Object[] {
            emp.getEmployeeID(),
            emp.getFullName(),
            emp.getRole(),
            emp.getPhone(),
            emp.getEmail(),
            emp.getHireDate(),
            emp.getPassword(), 
            emp.getStatus()  
        };
        model.addRow(rowData);
    }
    table.setModel(model);
}

}