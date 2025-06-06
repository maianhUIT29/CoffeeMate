package com.coffeemate.controller;

import com.coffeemate.configs.MailSender;
import com.coffeemate.dao.EmployeeDAO;
import com.coffeemate.model.ChartDataModel;
import com.coffeemate.model.Employee;

import java.util.List;

public class EmployeeController {

    private final EmployeeDAO employeeDAO;

    public EmployeeController() {
        employeeDAO = new EmployeeDAO();
    }

    // 1. Đếm tổng số nhân viên
    public int countEmployee() {
        return employeeDAO.countEmployee();
    }

    // 2. Lấy danh sách tất cả nhân viên
    public List<Employee> getAllEmployees() {
        return employeeDAO.getAllEmployees();
    }

    // 3. Thêm nhân viên
    public boolean addEmployee(Employee employee) {
        if (employee.getFullName() == null || employee.getFullName().trim().isEmpty()) {
            return false;
        }
        if (employee.getEmail() == null || employee.getEmail().trim().isEmpty()) {
            return false;
        }
        return employeeDAO.addEmployee(employee);
    }

    // 4. Cập nhật nhân viên
    public boolean updateEmployee(Employee employee) {
        if (employee.getEmployeeID() <= 0) return false;
        if (employee.getFullName() == null || employee.getFullName().trim().isEmpty()) {
            return false;
        }
        return employeeDAO.updateEmployee(employee);
    }

    // 5. Xóa nhân viên
    public boolean deleteEmployee(int employeeId) {
        if (employeeId <= 0) return false;
        return employeeDAO.deleteEmployee(employeeId);
    }

    // 6. Lấy nhân viên theo ID (int)
    public Employee getEmployeeById(int employeeId) {
        if (employeeId <= 0) return null;
        return employeeDAO.getEmployeeById(employeeId);
    }

    // 7. Lấy nhân viên theo ID (Long overload)
    public Employee getEmployeeById(Long employeeId) {
        if (employeeId == null || employeeId <= 0) {
            return null;
        }
        return employeeDAO.getEmployeeById(employeeId.intValue());
    }

    // 8. Lấy nhân viên theo tên (match chính xác)
    public Employee getEmployeeByName(String name) {
        if (name == null || name.trim().isEmpty()) return null;
        return employeeDAO.getEmployeeByName(name);
    }

    // 9. Đăng nhập: trả về Employee nếu đúng, ngược lại trả về null
    public Employee login(String email, String password) {
        if (email != null) email = email.trim();
        if (password != null) password = password.trim();
        if (email.isEmpty() || password.isEmpty()) {
            return null;
        }
        return employeeDAO.login(email, password);
    }

    // 10. Quên mật khẩu (reset và gửi mail)
    public boolean resetPassword(String email) {
    if (email == null || email.trim().isEmpty()) {
        return false; // Nếu không nhập gì, thoát luôn
    }
    email = email.trim();

    // 1. Sinh mật khẩu mới
    String newPassword = generateNewPassword();

    // 2. Cố gắng cập nhật vào DB (nếu email không tồn tại, dao.forgetPassword trả về false,
    //    nhưng chúng ta vẫn tiếp tục gửi mail bên dưới)
    try {
        employeeDAO.forgetPassword(email, newPassword);
    } catch (Exception ex) {
        // Nếu có lỗi khi cập nhật DB, ghi log nhưng không abort việc gửi mail
        ex.printStackTrace();
    }

    // 3. Gửi mail bất kể DB thế nào
    try {
        MailSender mailSender = new MailSender();
        String subject = "CoffeeMate - Mật khẩu mới";
        String content = "Mật khẩu tạm thời của bạn là: " + newPassword + "\n"
                       + "Vui lòng đăng nhập và thay đổi mật khẩu ngay.";
        mailSender.sendEmail(email, subject, content);
        return true; // Mail đã được gửi thành công
    } catch (Exception e) {
        // Nếu fail ở bước gửi mail (SMTP, cấu hình, network,...), trả về false
        e.printStackTrace();
        return false;
    }
}


    // 11. Cập nhật mật khẩu cũ → mới (cần truyền cả oldPassword để kiểm tra)
    public boolean updatePassword(String email, String oldPassword, String newPassword) {
        if (email == null || email.trim().isEmpty() ||
            oldPassword == null || newPassword == null ||
            oldPassword.trim().isEmpty() || newPassword.trim().isEmpty()) {
            return false;
        }
        return employeeDAO.updatePassword(email.trim(), oldPassword.trim(), newPassword.trim());
    }

    // 12. Lấy số lượng nhân viên theo vai trò (dùng ChartDataModel)
    public List<ChartDataModel> getEmployeeCountByRole() {
        return employeeDAO.getEmployeeCountByRole();
    }

    // 13. Hàm sinh mật khẩu ngẫu nhiên
    private String generateNewPassword() {
        String basePassword = "123456";
        int randomNumber = (int) (Math.random() * 900) + 100; // 3 chữ số ngẫu nhiên
        return basePassword + randomNumber;
    }
}
