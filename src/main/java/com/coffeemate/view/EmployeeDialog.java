package com.coffeemate.view;

import com.coffeemate.controller.EmployeeController;
import com.coffeemate.model.Employee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeeDialog {

    public static void showCreateDialog(Component parent, EmployeeController controller, Runnable onSuccess) {
        String fullName = JOptionPane.showInputDialog(parent, "Nhập họ tên nhân viên:");
        if (fullName == null || fullName.trim().isEmpty()) return;

        String role = JOptionPane.showInputDialog(parent, "Nhập vai trò:");
        if (role == null || role.trim().isEmpty()) return;

        String phone = JOptionPane.showInputDialog(parent, "Nhập số điện thoại:");
        if (phone == null || phone.trim().isEmpty()) return;

        String email = JOptionPane.showInputDialog(parent, "Nhập email:");
        if (email == null || email.trim().isEmpty()) return;

        Employee employee = new Employee();
        employee.setFullName(fullName);
        employee.setRole(role);
        employee.setPhone(phone);
        employee.setEmail(email);
        employee.setHireDate(new Date());

        if (controller.addEmployee(employee)) {
            JOptionPane.showMessageDialog(parent, "Thêm nhân viên thành công!");
            onSuccess.run();
        } else {
            JOptionPane.showMessageDialog(parent, "Thêm nhân viên thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
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
        if (row != -1) {
            int id = (int) table.getValueAt(row, 0);
            Employee employee = controller.getEmployeeById(id);
            if (employee != null) {
                String fullName = JOptionPane.showInputDialog(parent, "Nhập họ tên mới:", employee.getFullName());
                String role = JOptionPane.showInputDialog(parent, "Nhập vai trò mới:", employee.getRole());
                String phone = JOptionPane.showInputDialog(parent, "Nhập số điện thoại mới:", employee.getPhone());
                String email = JOptionPane.showInputDialog(parent, "Nhập email mới:", employee.getEmail());

                if (fullName != null && role != null && phone != null && email != null) {
                    employee.setFullName(fullName);
                    employee.setRole(role);
                    employee.setPhone(phone);
                    employee.setEmail(email);
                    if (controller.updateEmployee(employee)) {
                        JOptionPane.showMessageDialog(parent, "Cập nhật thành công!");
                        onSuccess.run();
                    } else {
                        JOptionPane.showMessageDialog(parent, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(parent, "Vui lòng chọn nhân viên để sửa!");
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
                    emp.getHireDate()
                });
            }
        }
    }
}
