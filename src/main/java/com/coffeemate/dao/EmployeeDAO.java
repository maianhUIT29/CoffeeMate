package com.coffeemate.dao;

import com.coffeemate.configs.DBConnection;
import com.coffeemate.model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    
    public Employee login(String email, String password) {
        Employee employee = new Employee();
        String query = "SELECT * FROM Employee WHERE Email = ? AND Password = ?";
        
        try (Connection connection = DBConnection.getConnection(); PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("Login success");
                employee.setEmployeeID(rs.getInt("EmployeeID"));
                employee.setFullName(rs.getString("Fullname"));
                employee.setRole(rs.getString("Role"));
                employee.setPhone(rs.getString("Phone"));
                employee.setEmail(rs.getString("Email"));
                employee.setHireDate(rs.getDate("HireDate"));
                employee.setPassword(rs.getString("Password"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return employee;
    } 
    
    public boolean updatePassword(String email, String oldPassword, String newPassword) {
        if (!isPasswordCorrect(email, oldPassword)) {
            return false; // Old password is incorrect
        }

        String query = "UPDATE Employee SET Password = ? WHERE Email = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, newPassword);
            stmt.setString(2, email);

            return stmt.executeUpdate() > 0; // Return true if the update was successful
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean forgetPassword(String email, String newPassword) {
        String query = "UPDATE Employee SET Password = ? WHERE Email = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, newPassword);
            stmt.setString(2, email);

            return stmt.executeUpdate() > 0; // Return true if the update was successful
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isPasswordCorrect(String email, String oldPassword) {
        String query = "SELECT 1 FROM Employee WHERE Email = ? AND Password = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, oldPassword);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Return true if a record is found
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get  by ID
    public Employee etById(int Id) {
        Employee employee = null;
        String query = "SELECT * FROM s WHERE s_id = ?";

        try (Connection connection = DBConnection.getConnection(); PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, Id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                employee = new Employee();
                employee.setEmployeeID(rs.getInt("Employee"));
                employee.setFullName(rs.getString("FullName"));
                employee.setRole(rs.getString("Role"));
                employee.setPhone(rs.getString("Phone"));
                employee.setEmail(rs.getString("Email"));
                employee.setHireDate(rs.getDate("HireDate"));
                employee.setPassword(rs.getString("Password")); // Ensure password is set
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employee;
    }
     // Đếm số lượng 
    public int count() {
        String sql = "SELECT COUNT(*) FROM s";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1); // Trả về số lượng 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Nếu có lỗi hoặc không tìm thấy dữ liệu, trả về 0
    }

}
