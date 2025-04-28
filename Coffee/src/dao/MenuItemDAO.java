
package dao;

import java.sql.Connection;
import database.DBConnection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import model.MenuItem;
import java.sql.ResultSet;
import javax.swing.JOptionPane;

public class MenuItemDAO implements DAOInterface<MenuItem>{

    public static MenuItemDAO getInstance() {
        return new MenuItemDAO();
    }
    
    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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
    
    @Override
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
    
    @Override
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
    
    @Override
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
    
    @Override
    public boolean deleteOrDisableMenuItem(int menuItemId) {
        Connection conn = null;
        PreparedStatement pstmtCheck = null;
        PreparedStatement pstmtUpdateStatus = null;
        PreparedStatement pstmtDelete = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection(); // Kết nối database

            // Kiểm tra món có đang được bán không
            String sqlCheck = "SELECT COUNT(*) FROM OrderDetail WHERE MenuItemID = ?";
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
}
