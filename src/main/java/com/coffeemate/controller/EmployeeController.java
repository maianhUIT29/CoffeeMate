package com.coffeemate.controller;

import com.coffeemate.configs.MailSender;
import com.coffeemate.dao.EmployeeDAO;
import com.coffeemate.model.Employee;

import java.util.List;

public class EmployeeController {

    private EmployeeDAO employeeDAO;

    public EmployeeController() {
        employeeDAO = new EmployeeDAO();  // Khởi tạo DAO
    }

    // Lấy danh sách tất cả nhân viên
    public List<Employee> getAllEmployees() {
        return employeeDAO.getAllEmployees();  // Lấy danh sách nhân viên từ DAO
    }

    // Các phương thức thêm, sửa, xóa nhân viên
    public boolean addEmployee(Employee employee) {
        return employeeDAO.addEmployee(employee);
    }

    public boolean updateEmployee(Employee employee) {
        return employeeDAO.updateEmployee(employee);
    }

    public boolean deleteEmployee(int employeeId) {
        return employeeDAO.deleteEmployee(employeeId);
    }

    // Lấy thông tin nhân viên theo ID
    public Employee getEmployeeById(int employeeId) {
        return employeeDAO.getEmployeeById(employeeId);  // Giả sử bạn đã định nghĩa phương thức này trong DAO
    }
    
    public Employee login(String email, String password) {
        return employeeDAO.login(email, password);
    }

    public boolean resetPassword(String email) {
        // Generate a new password
        String newPassword = generateNewPassword();

        // Update the password in the database
        boolean isUpdated = employeeDAO.forgetPassword(email, newPassword);

        // Send an email with the new password if the update was successful
        if (isUpdated) {
            MailSender mailSender = new MailSender();
            String subject = "SalesMate - New Password";
            String content = "Your new password is: " + newPassword + "\n"
                    + "Please log in and change your password immediately.";
            mailSender.sendEmail(email, subject, content);
        }

        return isUpdated;
    
    }

    private String generateNewPassword() {
        String basePassword = "123456";
        int randomNumber = (int) (Math.random() * 900) + 100; // Tạo số ngẫu nhiên 3 chữ số
        return basePassword + randomNumber;
    }
}
