package com.coffeemate.component;

import com.coffeemate.controller.EmployeeController;
import com.coffeemate.model.Employee;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.table.DefaultTableModel;

public class EmployeeDialog {

    // Nếu bạn muốn cũng hiển thị mật khẩu khi tạo mới, có thể tương tự sửa ở đây.
    public static void showCreateDialog(Component parent, EmployeeController controller, Runnable onSuccess) {
        // Tạo panel chứa các trường nhập liệu
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        panel.add(new JLabel("Họ tên:"), gbc);
        gbc.gridx = 1;
        JTextField txtFullName = new JTextField(20);
        panel.add(txtFullName, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Vai trò:"), gbc);
        gbc.gridx = 1;
        JTextField txtRole = new JTextField(20);
        panel.add(txtRole, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("SĐT:"), gbc);
        gbc.gridx = 1;
        JTextField txtPhone = new JTextField(20);
        panel.add(txtPhone, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        JTextField txtEmail = new JTextField(20);
        panel.add(txtEmail, gbc);

        // Thêm trường mật khẩu nếu cần
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridx = 1;
        JPasswordField txtPassword = new JPasswordField(20);
        panel.add(txtPassword, gbc);

        int result = JOptionPane.showConfirmDialog(
                parent,
                panel,
                "Thêm nhân viên mới",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String fullName = txtFullName.getText().trim();
            String role = txtRole.getText().trim();
            String phone = txtPhone.getText().trim();
            String email = txtEmail.getText().trim();
            String password = new String(txtPassword.getPassword());

            // Kiểm tra ràng buộc cơ bản
            if (fullName.isEmpty() || role.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(parent, "Vui lòng điền đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Employee employee = new Employee();
            employee.setFullName(fullName);
            employee.setRole(role);
            employee.setPhone(phone);
            employee.setEmail(email);
            employee.setHireDate(new Date());
            employee.setPassword(password); // hoặc employee.setPasswordHash(...) nếu bạn hash trước

            if (controller.addEmployee(employee)) {
                JOptionPane.showMessageDialog(parent, "Thêm nhân viên thành công!");
                onSuccess.run();
            } else {
                JOptionPane.showMessageDialog(parent, "Thêm nhân viên thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void showDeleteDialog(Component parent, JTable table, EmployeeController controller, Runnable onSuccess) {
        int row = table.getSelectedRow();
        if (row != -1) {
            int id = (int) table.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(parent, "Bạn có chắc chắn muốn xóa nhân viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION && controller.deleteEmployee(id)) {
                JOptionPane.showMessageDialog(parent, "Xóa nhân viên thành công!");
                onSuccess.run();
            } else {
                JOptionPane.showMessageDialog(parent, "Xóa nhân viên thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(parent, "Vui lòng chọn nhân viên để xóa!");
        }
    }

    public static void showUpdateDialog(Component parent, JTable table, EmployeeController controller, Runnable onSuccess) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(parent, "Vui lòng chọn nhân viên để sửa!");
            return;
        }

        int id = (int) table.getValueAt(row, 0);
        Employee employee = controller.getEmployeeById(id);
        if (employee == null) {
            JOptionPane.showMessageDialog(parent, "Không tìm thấy thông tin nhân viên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Tạo panel chứa tất cả trường thông tin hiện tại, cho phép sửa
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        panel.add(new JLabel("Họ tên:"), gbc);
        gbc.gridx = 1;
        JTextField txtFullName = new JTextField(employee.getFullName(), 20);
        panel.add(txtFullName, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Vai trò:"), gbc);
        gbc.gridx = 1;
        JTextField txtRole = new JTextField(employee.getRole(), 20);
        panel.add(txtRole, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("SĐT:"), gbc);
        gbc.gridx = 1;
        JTextField txtPhone = new JTextField(employee.getPhone(), 20);
        panel.add(txtPhone, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        JTextField txtEmail = new JTextField(employee.getEmail(), 20);
        panel.add(txtEmail, gbc);

        // Thêm trường mật khẩu để sửa (hiện dưới dạng rỗng, nếu muốn đổi mật khẩu)
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Mật khẩu mới:"), gbc);
        gbc.gridx = 1;
        JPasswordField txtPassword = new JPasswordField(20);
        panel.add(txtPassword, gbc);

        int result = JOptionPane.showConfirmDialog(
                parent,
                panel,
                "Cập nhật thông tin nhân viên - ID: " + id,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String fullName = txtFullName.getText().trim();
            String role = txtRole.getText().trim();
            String phone = txtPhone.getText().trim();
            String email = txtEmail.getText().trim();
            String newPassword = new String(txtPassword.getPassword()).trim();

            // Nếu bạn muốn bắt buộc nhập, kiểm tra rỗng, hoặc có thể để trống nếu không đổi mật khẩu
            if (fullName.isEmpty() || role.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(parent, "Họ tên, vai trò, SĐT và email không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            employee.setFullName(fullName);
            employee.setRole(role);
            employee.setPhone(phone);
            employee.setEmail(email);
            // Chỉ cập nhật password khi người dùng có nhập mới
            if (!newPassword.isEmpty()) {
                employee.setPassword(newPassword); // hoặc employee.setPasswordHash(...) nếu bạn hash trước
            }

            if (controller.updateEmployee(employee)) {
                JOptionPane.showMessageDialog(parent, "Cập nhật thông tin thành công!");
                onSuccess.run();
            } else {
                JOptionPane.showMessageDialog(parent, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void showSearchDialog(Component parent, JTable table, EmployeeController controller) {
        String keyword = JOptionPane.showInputDialog(parent, "Nhập tên nhân viên cần tìm kiếm:");
        if (keyword != null && !keyword.trim().isEmpty()) {
            String lowerKeyword = keyword.trim().toLowerCase();
            List<Employee> employees = controller.getAllEmployees().stream()
                    .filter(e -> e.getFullName() != null &&
                            java.util.Arrays.stream(e.getFullName().toLowerCase().split("\\s+"))
                                    .anyMatch(part -> part.equals(lowerKeyword)))
                    .collect(Collectors.toList());

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);
            for (Employee emp : employees) {
                model.addRow(new Object[]{
                    emp.getEmployeeID(),
                    emp.getFullName(),
                    emp.getRole(),
                    emp.getPhone(),
                    emp.getEmail(),
                    emp.getHireDate(),
                    // Nếu đã thêm cột mật khẩu ở EmployeePanel, có thể hiển thị tại đây
                    emp.getPassword()
                });
            }
        }
    }
}
