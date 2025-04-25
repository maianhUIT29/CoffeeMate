package com.coffeemate.controller;

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
}
