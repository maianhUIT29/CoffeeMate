package com.coffeemate.view;

import com.coffeemate.model.MenuItem;
import com.coffeemate.dao.MenuItemDAO;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.Dialog.ModalityType;

public class MenuItemDialog extends JDialog {
    private JTextField txtItemName, txtPrice, txtDescription;
    private JButton btnSave, btnCancel;
    private JRadioButton rbAvailable, rbUnavailable;
    private ButtonGroup statusGroup;
    private MenuItem menuItem;
    private final MenuItemDAO menuItemDAO = MenuItemDAO.getInstance();

    public MenuItemDialog(Window parent, String title, MenuItem menuItem) {
        super(parent, title, ModalityType.APPLICATION_MODAL);
        this.menuItem = menuItem;
        initUI(parent);
    }

    private void initUI(Component parent) {
        setLayout(new GridLayout(6, 2, 5, 5));
        setSize(400, 250);
        setLocationRelativeTo(parent);

        add(new JLabel("Tên Món:"));
        txtItemName = new JTextField();
        add(txtItemName);

        add(new JLabel("Giá:"));
        txtPrice = new JTextField();
        add(txtPrice);

        add(new JLabel("Mô Tả:"));
        txtDescription = new JTextField();
        add(txtDescription);

        add(new JLabel("Trạng Thái:"));
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rbAvailable = new JRadioButton("Available");
        rbUnavailable = new JRadioButton("Unavailable");
        statusGroup = new ButtonGroup();
        statusGroup.add(rbAvailable);
        statusGroup.add(rbUnavailable);
        statusPanel.add(rbAvailable);
        statusPanel.add(rbUnavailable);
        add(statusPanel);

        btnSave = new JButton("Xác Nhận");
        btnCancel = new JButton("Hủy");
        add(btnSave);
        add(btnCancel);

        if (menuItem != null) {
            txtItemName.setText(menuItem.getItemName());
            txtPrice.setText(String.valueOf(menuItem.getPrice()));
            txtDescription.setText(menuItem.getDescription());
            if ("Available".equalsIgnoreCase(menuItem.getStatus())) {
                rbAvailable.setSelected(true);
            } else {
                rbUnavailable.setSelected(true);
            }
        }

        btnSave.addActionListener(e -> saveMenuItem());
        btnCancel.addActionListener(e -> dispose());
    }

    private void saveMenuItem() {
        String itemName = txtItemName.getText().trim();
        String priceStr = txtPrice.getText().trim();
        String description = txtDescription.getText().trim();
        String status = rbAvailable.isSelected() ? "Available" : "Unavailable";

        if (itemName.isEmpty() || priceStr.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin.");
            return;
        }

        float price;
        try {
            price = Float.parseFloat(priceStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá không hợp lệ.");
            return;
        }

        if (menuItem == null) {
            if (menuItemDAO.checkIfExists(itemName)) {
                JOptionPane.showMessageDialog(this, "Tên món đã tồn tại!");
                return;
            }
            MenuItem newItem = new MenuItem(itemName, price, description, status);
            menuItemDAO.insert(newItem);
            JOptionPane.showMessageDialog(this, "Thêm món thành công!");
        } else {
            menuItem.setItemName(itemName);
            menuItem.setPrice(price);
            menuItem.setDescription(description);
            menuItem.setStatus(status);
            menuItemDAO.update(menuItem);
            JOptionPane.showMessageDialog(this, "Sửa món thành công!");
        }

        dispose();
    }
}
