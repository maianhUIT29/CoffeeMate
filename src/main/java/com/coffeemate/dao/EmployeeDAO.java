package com.coffeemate.dao;

import com.coffeemate.configs.DBConnection;
import com.coffeemate.model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    private Connection connection;

    // Khởi tạo kết nối cơ sở dữ liệu
    public EmployeeDAO() {
        this.connection = DBConnection.getConnection();  
    }

    // Lấy danh sách tất cả nhân viên
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT * FROM Employee";  

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Employee employee = new Employee();
                employee.setEmployeeID(rs.getInt("EmployeeID"));
                employee.setFullName(rs.getString("FullName"));
                employee.setRole(rs.getString("Role"));
                employee.setPhone(rs.getString("Phone"));
                employee.setEmail(rs.getString("Email"));
                employee.setHireDate(rs.getDate("HireDate"));
                employees.add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employees;
    }

    // Lấy thông tin nhân viên theo ID
    public Employee getEmployeeById(int employeeId) {
        String query = "SELECT * FROM Employee WHERE EmployeeID = ?";
        Employee employee = null;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, employeeId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    employee = new Employee();
                    employee.setEmployeeID(rs.getInt("EmployeeID"));
                    employee.setFullName(rs.getString("FullName"));
                    employee.setRole(rs.getString("Role"));
                    employee.setPhone(rs.getString("Phone"));
                    employee.setEmail(rs.getString("Email"));
                    employee.setHireDate(rs.getDate("HireDate"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employee;
    }

    // Các phương thức thêm, sửa, xóa nhân viên
    public boolean addEmployee(Employee employee) {
        String query = "INSERT INTO Employee (FullName, Role, Phone, Email, HireDate) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, employee.getFullName());
            stmt.setString(2, employee.getRole());
            stmt.setString(3, employee.getPhone());
            stmt.setString(4, employee.getEmail());
            stmt.setDate(5, new java.sql.Date(employee.getHireDate().getTime()));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateEmployee(Employee employee) {
        String query = "UPDATE Employee SET FullName = ?, Role = ?, Phone = ?, Email = ?, HireDate = ? WHERE EmployeeID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, employee.getFullName());
            stmt.setString(2, employee.getRole());
            stmt.setString(3, employee.getPhone());
            stmt.setString(4, employee.getEmail());
            stmt.setDate(5, new java.sql.Date(employee.getHireDate().getTime()));
            stmt.setInt(6, employee.getEmployeeID());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteEmployee(int employeeId) {
        String query = "DELETE FROM Employee WHERE EmployeeID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, employeeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
