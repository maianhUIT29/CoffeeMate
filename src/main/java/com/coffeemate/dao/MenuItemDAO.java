package com.coffeemate.dao;

import java.sql.Connection;
import com.coffeemate.configs.DBConnection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import com.coffeemate.model.MenuItem;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

public class MenuItemDAO {

    public static MenuItemDAO getInstance() {
        return new MenuItemDAO();
    }

    public int insert(MenuItem t) {
        int ketQua = 0;
        try {
            // B1: Tạo kết nối đến CSDL
            Connection con = DBConnection.getConnection();

            // B2: Tạo câu lệnh SQL với PreparedStatement
            String sql = "INSERT INTO MenuItem (MenuItemID, ItemName, Price, Description, Status) " + 
                         "VALUES (MenuItem_seq.NEXTVAL, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);

            // B3: Gán các giá trị vào PreparedStatement
            ps.setString(1, t.getItemName());
            ps.setFloat(2, t.getPrice());
            ps.setString(3, t.getDescription());
            ps.setString(4, t.getStatus());

            // B4: Thực thi câu lệnh
            ketQua = ps.executeUpdate();

            // B5: In ra kết quả
            System.out.println("Ban da thuc thi: " + sql);
            System.out.println("Co " + ketQua + " dong bi thay doi!");

            // B6: Ngắt kết nối
            DBConnection.closeConnection(con);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ketQua;
    }

    public int update(MenuItem t) {
        int ketQua = 0;
        try {
            // B1: Tạo kết nối đến CSDL
            Connection con = DBConnection.getConnection();

            // B2: Tạo câu lệnh SQL với PreparedStatement
            String sql = "UPDATE MenuItem SET ItemName = ?, Price = ?, Description = ?, Status = ? WHERE MenuItemID = ?";
            PreparedStatement ps = con.prepareStatement(sql);

            // B3: Gán các giá trị vào PreparedStatement
            ps.setString(1, t.getItemName());
            ps.setFloat(2, t.getPrice());
            ps.setString(3, t.getDescription());
            ps.setString(4, t.getStatus());
            ps.setInt(5, t.getMenuItemID());

            // B4: Thực thi câu lệnh
            ketQua = ps.executeUpdate();

            // B5: In ra kết quả
            System.out.println("Ban da thuc thi: " + sql);
            System.out.println("Co " + ketQua + " dong bi thay doi!");

            // B6: Ngắt kết nối
            DBConnection.closeConnection(con);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ketQua;
    }

    public int delete(MenuItem t) {
        int ketQua = 0;
        try {
            // B1: Tạo kết nối đến CSDL
            Connection con = DBConnection.getConnection();

            // B2: Tạo câu lệnh SQL với PreparedStatement
            String sql = "DELETE FROM MenuItem WHERE MenuItemID = ?";
            PreparedStatement ps = con.prepareStatement(sql);

            // B3: Gán giá trị vào PreparedStatement
            ps.setInt(1, t.getMenuItemID());

            // B4: Thực thi câu lệnh
            ketQua = ps.executeUpdate();

            // B5: In ra kết quả
            System.out.println("Ban da thuc thi: " + sql);
            System.out.println("Co " + ketQua + " dong bi xoa!");

            // B6: Ngắt kết nối
            DBConnection.closeConnection(con);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ketQua;
    }

    public ArrayList<MenuItem> selectAll() {
        ArrayList<MenuItem> ketQua = new ArrayList<MenuItem>();
        try {
            // B1: Tạo kết nối đến CSDL
            Connection con = DBConnection.getConnection();

            // B2: Tạo câu lệnh SQL với PreparedStatement
            String sql = "SELECT * FROM MenuItem";
            PreparedStatement ps = con.prepareStatement(sql);

            // B3: Thực thi câu lệnh
            ResultSet rs = ps.executeQuery();

            // B4: Xử lý kết quả
            while (rs.next()) {
                int MenuItemID = rs.getInt("MenuItemID");
                String ItemName = rs.getString("ItemName");
                float Price = rs.getFloat("Price");
                String Description = rs.getString("Description");
                String Status = rs.getString("Status");

                MenuItem menuItem = new MenuItem(MenuItemID, ItemName, Price, Description, Status);
                ketQua.add(menuItem);
            }

            // B5: In ra kết quả
            System.out.println("Ban da thuc thi: " + sql);

            // B6: Ngắt kết nối
            DBConnection.closeConnection(con);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ketQua;
    }

    public MenuItem selectById(MenuItem t) {
        MenuItem ketQua = null;
        try {
            // B1: Tạo kết nối đến CSDL
            Connection con = DBConnection.getConnection();

            // B2: Tạo câu lệnh SQL với PreparedStatement
            String sql = "SELECT * FROM MenuItem WHERE MenuItemID = ?";
            PreparedStatement ps = con.prepareStatement(sql);

            // B3: Gán giá trị vào PreparedStatement
            ps.setInt(1, t.getMenuItemID());

            // B4: Thực thi câu lệnh
            ResultSet rs = ps.executeQuery();

            // B5: Xử lý kết quả
            if (rs.next()) {
                int MenuItemID = rs.getInt("MenuItemID");
                String ItemName = rs.getString("ItemName");
                float Price = rs.getFloat("Price");
                String Description = rs.getString("Description");
                String Status = rs.getString("Status");

                ketQua = new MenuItem(MenuItemID, ItemName, Price, Description, Status);
            }

            // B6: In ra kết quả
            System.out.println("Ban da thuc thi: " + sql);

            // B7: Ngắt kết nối
            DBConnection.closeConnection(con);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ketQua;
    }

    public ArrayList<MenuItem> selectByCondition(String condition) {
        ArrayList<MenuItem> ketQua = new ArrayList<MenuItem>();
        try {
            // B1: Tạo kết nối đến CSDL
            Connection con = DBConnection.getConnection();

            // B2: Tạo câu lệnh SQL với PreparedStatement
            String sql = "SELECT * FROM MenuItem WHERE " + condition;
            PreparedStatement ps = con.prepareStatement(sql);

            // B3: Thực thi câu lệnh
            ResultSet rs = ps.executeQuery();

            // B4: Xử lý kết quả
            while (rs.next()) {
                int MenuItemID = rs.getInt("MenuItemID");
                String ItemName = rs.getString("ItemName");
                float Price = rs.getFloat("Price");
                String Description = rs.getString("Description");
                String Status = rs.getString("Status");

                MenuItem menuItem = new MenuItem(MenuItemID, ItemName, Price, Description, Status);
                ketQua.add(menuItem);
            }

            // B5: In ra kết quả
            System.out.println("Ban da thuc thi: " + sql);

            // B6: Ngắt kết nối
            DBConnection.closeConnection(con);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ketQua; 
    }

    public boolean checkIfExists(String ItemName) {
        String sql = "SELECT COUNT(*) FROM MenuItem WHERE ItemName = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ItemName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Nếu có ít nhất 1 món trùng tên, trả về true
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // Không có món trùng tên
    }

    public ArrayList<MenuItem> searchByName(String ItemName){
        ArrayList<MenuItem> menuItems = new ArrayList<>();
        String sql = "SELECT * FROM MenuItem WHERE LOWER(ItemName) LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + ItemName + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                MenuItem menuItem = new MenuItem(
                    rs.getInt("MenuItemID"),
                    rs.getString("ItemName"),
                    rs.getFloat("Price"),
                    rs.getString("Description"),
                    rs.getString("Status")
                );
                menuItems.add(menuItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return menuItems;
    }

    public int deleteByID(int MenuItemID) {
        int result = 0;
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "DELETE FROM MenuItem WHERE MenuItemID = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, MenuItemID);
            result = pst.executeUpdate();
            DBConnection.closeConnection(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean deleteOrDisableMenuItem(int menuItemId) {
        Connection conn = null;
        PreparedStatement pstmtCheck = null;
        PreparedStatement pstmtUpdateStatus = null;
        PreparedStatement pstmtDelete = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection(); // Kết nối database

            // Kiểm tra món có đang được bán không
            String sqlCheck = "SELECT COUNT(*) FROM Detail WHERE MenuItemID = ?";
            pstmtCheck = conn.prepareStatement(sqlCheck);
            pstmtCheck.setInt(1, menuItemId);
            rs = pstmtCheck.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);

                if (count > 0) {
                    // Nếu đã bán: Chỉ cập nhật trạng thái Unavailable
                    String sqlUpdate = "UPDATE MenuItem SET Status = 'Unavailable' WHERE MenuItemID = ?";
                    pstmtUpdateStatus = conn.prepareStatement(sqlUpdate);
                    pstmtUpdateStatus.setInt(1, menuItemId);
                    int rowsAffected = pstmtUpdateStatus.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "Món đã từng bán, đã đổi sang 'Ngưng bán'.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        return true;
                    } else {
                        JOptionPane.showMessageDialog(null, "Cập nhật trạng thái thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }

                } else {
                    // Nếu chưa bán: Xóa bình thường
                    String sqlDelete = "DELETE FROM MenuItem WHERE MenuItemID = ?";
                    pstmtDelete = conn.prepareStatement(sqlDelete);
                    pstmtDelete.setInt(1, menuItemId);
                    int rowsDeleted = pstmtDelete.executeUpdate();

                    if (rowsDeleted > 0) {
                        JOptionPane.showMessageDialog(null, "Xóa món thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        return true;
                    } else {
                        JOptionPane.showMessageDialog(null, "Không tìm thấy món để xóa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi hệ thống: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pstmtCheck != null) pstmtCheck.close(); } catch (Exception e) {}
            try { if (pstmtUpdateStatus != null) pstmtUpdateStatus.close(); } catch (Exception e) {}
            try { if (pstmtDelete != null) pstmtDelete.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
        return false;
    }

    public int countMenuItem() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM MenuItem";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    //lay mon ban chay tu detail
    public List<Map<String, Object>> getTopSellingMenuItems(int topN) {
        List<Map<String, Object>> result = new ArrayList<>();

        // Giả sử: 
        //   - Bảng Detail có cột MenuItemID và Quantity (số lượng món trong từng dòng chi tiết).
        //   - Bảng MenuItem có cột MenuItemID (PK) và ItemName (tên món).
        String sql =
            "SELECT di.MenuItemID, mi.ItemName AS menu_name, SUM(di.Quantity) AS quantity " +
            "FROM Detail di " +
            "JOIN MenuItem mi ON di.MenuItemID = mi.MenuItemID " +
            "GROUP BY di.MenuItemID, mi.ItemName " +
            "ORDER BY quantity DESC " +
            "FETCH FIRST ? ROWS ONLY";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, topN);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("menu_item_id", rs.getInt("MenuItemID"));
                    row.put("menu_name", rs.getString("menu_name"));
                    row.put("quantity", rs.getInt("quantity"));
                    result.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }
}
