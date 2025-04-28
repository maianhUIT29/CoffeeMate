package gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import model.MenuItem;
import dao.MenuItemDAO;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;


public class MenuManagementUI extends JFrame {
    private JTable table;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh, btnSearch;
    private JTextField txtSearch;
    private MenuItemDAO menuItemDAO = MenuItemDAO.getInstance();

    public MenuManagementUI() {
        setTitle("Quản Lý Menu");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // Khối nút chức năng
        JPanel panelActions = new JPanel(new FlowLayout());
        btnAdd = new JButton("Thêm Món");
        btnEdit = new JButton("Sửa Món");
        btnDelete = new JButton("Xóa Món");
        btnRefresh = new JButton("Làm Mới");
        btnSearch = new JButton("Tìm Kiếm");
        txtSearch = new JTextField(20);

        panelActions.add(btnAdd);
        panelActions.add(btnEdit);
        panelActions.add(btnDelete);
        panelActions.add(btnRefresh);
        panelActions.add(txtSearch);
        panelActions.add(btnSearch);
        add(panelActions, BorderLayout.NORTH);

        // Bảng danh sách món
        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> openAddDialog());
        btnEdit.addActionListener(e -> openEditDialog());
        btnDelete.addActionListener(e -> deleteMenu());
        btnRefresh.addActionListener(e -> updateMenuTable());
        btnSearch.addActionListener(e -> searchMenu());

        updateMenuTable();
    }

    private void openAddDialog() {
        MenuItemDialog dialog = new MenuItemDialog(this, "Thêm Món", null);
        dialog.setVisible(true);
        updateMenuTable();
    }

    private void openEditDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn món cần sửa.");
            return;
        }
        int menuItemID = (int) table.getValueAt(selectedRow, 0);
        String itemName = (String) table.getValueAt(selectedRow, 1);
        float price = Float.parseFloat(table.getValueAt(selectedRow, 2).toString());
        String description = (String) table.getValueAt(selectedRow, 3);
        String status = (String) table.getValueAt(selectedRow, 4);

        MenuItem menuItem = new MenuItem(menuItemID, itemName, price, description, status);
        MenuItemDialog dialog = new MenuItemDialog(this, "Sửa Món", menuItem);
        dialog.setVisible(true);
        updateMenuTable();
    }

    private void deleteMenu() {
        int selectedRow = table.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn món cần xóa.");
        return;
    }
    int menuItemID = (int) table.getValueAt(selectedRow, 0);
    int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa/ngưng bán món này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
    if (confirm == JOptionPane.YES_OPTION) {
        if (menuItemDAO.deleteOrDisableMenuItem(menuItemID)) {
            JOptionPane.showMessageDialog(this, "Thao tác thành công!");
            updateMenuTable();
        } else {
            JOptionPane.showMessageDialog(this, "Thao tác thất bại. Hãy kiểm tra lại!");
        }
    }
    }

    private void searchMenu() {
        String keyword = txtSearch.getText().toLowerCase();
        ArrayList<MenuItem> menuItems = menuItemDAO.searchByName(keyword);
        fillTable(menuItems);
    }

    private void updateMenuTable() {
        ArrayList<MenuItem> menuItems = menuItemDAO.selectAll();
        fillTable(menuItems);
    }

    private void fillTable(ArrayList<MenuItem> menuItems) {
        String[] columnNames = {"ID", "Tên Món", "Giá", "Mô Tả", "Trạng Thái"};
        Object[][] data = new Object[menuItems.size()][5];
        for (int i = 0; i < menuItems.size(); i++) {
            data[i][0] = menuItems.get(i).getMenuItemID();
            data[i][1] = menuItems.get(i).getItemName();
            data[i][2] = menuItems.get(i).getPrice();
            data[i][3] = menuItems.get(i).getDescription();
            data[i][4] = menuItems.get(i).getStatus();
        }
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        table.setModel(model);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MenuManagementUI().setVisible(true);
        });
    }
}