package gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import model.MenuItem;
import dao.MenuItemDAO;

public class MenuItemDialog extends JDialog {
    private JTextField txtItemName, txtPrice, txtDescription, txtStatus;
    private JButton btnSave, btnCancel;
    private MenuItem menuItem;
    private MenuItemDAO menuItemDAO = MenuItemDAO.getInstance();

    public MenuItemDialog(Frame parent, String title, MenuItem menuItem) {
        super(parent, title, true);
        this.menuItem = menuItem;

        setLayout(new GridLayout(5, 2, 5, 5));
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
        txtStatus = new JTextField();
        add(txtStatus);

        btnSave = new JButton("Xác Nhận");
        btnCancel = new JButton("Hủy");
        add(btnSave);
        add(btnCancel);

        if (menuItem != null) {
            txtItemName.setText(menuItem.getItemName());
            txtPrice.setText(String.valueOf(menuItem.getPrice()));
            txtDescription.setText(menuItem.getDescription());
            txtStatus.setText(menuItem.getStatus());
        }

        btnSave.addActionListener(e -> saveMenuItem());
        btnCancel.addActionListener(e -> dispose());
    }

    private void saveMenuItem() {
        String itemName = txtItemName.getText();
        String priceStr = txtPrice.getText();
        String description = txtDescription.getText();
        String status = txtStatus.getText();

        if (itemName.isEmpty() || priceStr.isEmpty() || description.isEmpty() || status.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin.");
            return;
        }

        float price;
        try {
            price = Float.parseFloat(priceStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá không hợp lệ.");
            return;
        }

        if (menuItem == null) {
            // Thêm mới
            if (menuItemDAO.checkIfExists(itemName)) {
                JOptionPane.showMessageDialog(this, "Tên món đã tồn tại!");
                return;
            }
            MenuItem newItem = new MenuItem(itemName, price, description, status);
            menuItemDAO.insert(newItem);
            JOptionPane.showMessageDialog(this, "Thêm món thành công!");
        } else {
            // Sửa món
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