package com.coffeemate.dao;

import com.coffeemate.configs.DBConnection;
import com.coffeemate.model.ChartDataModel;
import com.coffeemate.model.Employee;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    // 1. Lấy danh sách tất cả nhân viên
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT * FROM Employee";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Employee e = new Employee();
                e.setEmployeeID(rs.getInt("EmployeeID"));
                e.setFullName(rs.getString("FullName"));
                e.setRole(rs.getString("Role"));
                e.setPhone(rs.getString("Phone"));
                e.setEmail(rs.getString("Email"));
                e.setHireDate(rs.getDate("HireDate"));
                e.setPassword(rs.getString("Password"));
                e.setStatus(rs.getString("Status"));
                employees.add(e);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return employees;
    }

    // 2. Lấy nhân viên theo ID (trả về null nếu không tìm thấy)
    public Employee getEmployeeById(int employeeId) {
        String query = "SELECT * FROM Employee WHERE EmployeeID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, employeeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Employee e = new Employee();
                    e.setEmployeeID(rs.getInt("EmployeeID"));
                    e.setFullName(rs.getString("FullName"));
                    e.setRole(rs.getString("Role"));
                    e.setPhone(rs.getString("Phone"));
                    e.setEmail(rs.getString("Email"));
                    e.setHireDate(rs.getDate("HireDate"));
                    e.setPassword(rs.getString("Password"));
                    return e;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // 3. Lấy nhân viên theo tên (match chính xác)
    public Employee getEmployeeByName(String name) {
        String sql = "SELECT * FROM Employee WHERE FullName = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Employee e = new Employee();
                    e.setEmployeeID(rs.getInt("EmployeeID"));
                    e.setFullName(rs.getString("FullName"));
                    e.setRole(rs.getString("Role"));
                    e.setPhone(rs.getString("Phone"));
                    e.setEmail(rs.getString("Email"));
                    e.setHireDate(rs.getDate("HireDate"));
                    e.setPassword(rs.getString("Password"));
                    return e;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // 4. Thêm nhân viên (chưa bao gồm mật khẩu)
    public boolean addEmployee(Employee employee) {
        String query = "INSERT INTO Employee (FullName, Role, Phone, Email, HireDate) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, employee.getFullName());
            stmt.setString(2, employee.getRole());
            stmt.setString(3, employee.getPhone());
            stmt.setString(4, employee.getEmail());
            stmt.setDate(5, new java.sql.Date(employee.getHireDate().getTime()));
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    // 5. Cập nhật thông tin nhân viên (không cập nhật password ở đây)
    public boolean updateEmployee(Employee employee) {
        String query = "UPDATE Employee SET FullName = ?, Role = ?, Phone = ?, Email = ?, HireDate = ? WHERE EmployeeID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, employee.getFullName());
            stmt.setString(2, employee.getRole());
            stmt.setString(3, employee.getPhone());
            stmt.setString(4, employee.getEmail());
            stmt.setDate(5, new java.sql.Date(employee.getHireDate().getTime()));
            stmt.setInt(6, employee.getEmployeeID());
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    // 6. Xóa nhân viên
   // 6. Xóa nhân viên (thực hiện thay đổi trạng thái)
public boolean deleteEmployee(int employeeId) {
    String sql = "UPDATE Employee SET status = 'inactive' WHERE EmployeeID = ? AND status = 'active'";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        
        ps.setInt(1, employeeId);  // employeeId là ID của nhân viên bạn muốn thay đổi trạng thái
        int rowsUpdated = ps.executeUpdate();

        if (rowsUpdated > 0) {
            System.out.println("Trạng thái của nhân viên đã được thay đổi thành 'inactive'.");
            return true;  // Trả về true nếu trạng thái đã được thay đổi thành công
        } else {
            System.out.println("Không có nhân viên nào có trạng thái 'active' với ID này.");
            return false;  // Trả về false nếu không tìm thấy nhân viên có trạng thái 'active'
        }
    } catch (SQLException e) {
        e.printStackTrace();
        System.err.println("Lỗi khi cập nhật trạng thái nhân viên: " + e.getMessage());
        return false;  // Trả về false nếu có lỗi xảy ra trong quá trình cập nhật
    }
}

    

    // 7. Đăng nhập: trả về Employee nếu đúng, ngược lại trả về null
    public Employee login(String email, String password) {
        String query = "SELECT * FROM Employee WHERE Email = ? AND Password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Employee e = new Employee();
                    e.setEmployeeID(rs.getInt("EmployeeID"));
                    e.setFullName(rs.getString("FullName"));
                    e.setRole(rs.getString("Role"));
                    e.setPhone(rs.getString("Phone"));
                    e.setEmail(rs.getString("Email"));
                    e.setHireDate(rs.getDate("HireDate"));
                    e.setPassword(rs.getString("Password"));
                    return e;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // 8. Cập nhật mật khẩu (kiểm tra oldPassword và update newPassword bằng một câu SQL duy nhất)
    public boolean updatePassword(String email, String oldPassword, String newPassword) {
        String sql = "UPDATE Employee SET Password = ? WHERE Email = ? AND Password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setString(2, email);
            ps.setString(3, oldPassword);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    // 9. Quên mật khẩu (reset password) – trực tiếp update, không cần kiểm tra oldPassword
    public boolean forgetPassword(String email, String newPassword) {
        String sql = "UPDATE Employee SET Password = ? WHERE Email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    // 10. Đếm tổng số nhân viên
    public int countEmployee() {
        String sql = "SELECT COUNT(*) FROM Employee";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    // 11. Đếm số nhân viên theo vai trò, trả về ChartDataModel
    public List<ChartDataModel> getEmployeeCountByRole() {
        List<ChartDataModel> result = new ArrayList<>();
        String sql = "SELECT Role, COUNT(*) AS cnt FROM Employee GROUP BY Role";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String role = rs.getString("Role");
                BigDecimal count = BigDecimal.valueOf(rs.getLong("cnt"));
                result.add(new ChartDataModel(role, count));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return result;
    }
}
