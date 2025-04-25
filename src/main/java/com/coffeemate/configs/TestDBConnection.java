/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.coffeemate.configs;
import com.coffeemate.configs.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 *
 * @author meiln
 */
public class TestDBConnection {
   
    public static void main(String[] args) {
        // Kết nối đến cơ sở dữ liệu và hiển thị số lượng nhân viên
        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("Kết nối cơ sở dữ liệu thành công!");

            // Truy vấn SQL để đếm số lượng nhân viên
            String query = "SELECT COUNT(*) FROM Employee";
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                 
                if (rs.next()) {
                    int employeeCount = rs.getInt(1);  // Lấy số lượng nhân viên từ cột đầu tiên
                    System.out.println("Số lượng nhân viên trong cơ sở dữ liệu: " + employeeCount);
                }

            } catch (SQLException e) {
                System.out.println("Lỗi khi truy vấn cơ sở dữ liệu: " + e.getMessage());
            }

        } catch (SQLException e) {
            System.out.println("Không thể kết nối đến cơ sở dữ liệu: " + e.getMessage());
        }
    }
}


