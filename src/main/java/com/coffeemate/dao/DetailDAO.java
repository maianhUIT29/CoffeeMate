package com.coffeemate.dao;

import com.coffeemate.configs.DBConnection;
import com.coffeemate.model.Detail;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetailDAO {
    private Connection connection;

    // Constructor, kết nối với CSDL
    public DetailDAO() {
        connection = DBConnection.getConnection();
    }

    /**
     * 1. Xóa một chi tiết hóa đơn theo OrderDetailID.
     * @param detailId OrderDetailID cần xóa.
     * @return true nếu xóa thành công, false nếu thất bại.
     */
    public boolean deleteDetail(int detailId) {
        String sql = "DELETE FROM Detail WHERE OrderDetailID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, detailId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 1a. Xóa tất cả chi tiết hóa đơn của một Invoice (được gọi trước khi xóa Invoice để tránh ràng buộc FK).
     * @param invoiceId InvoiceID cần xóa chi tiết.
     * @return true nếu xóa ít nhất một bản ghi hoặc không có bản ghi nào, false nếu lỗi.
     */
    public boolean deleteByInvoiceId(int invoiceId) {
        String sql = "DELETE FROM Detail WHERE InvoiceID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, invoiceId);
            pstmt.executeUpdate();  // nếu không có bản ghi, executeUpdate trả về 0, nhưng vẫn coi là thành công
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 2. Cập nhật một chi tiết hóa đơn (Detail) đã tồn tại.
     *    Cập nhật các cột: InvoiceID, MenuItemID, Quantity, UnitPrice.
     * @param detail đối tượng Detail chứa thông tin mới (OrderDetailID đã có sẵn).
     * @return true nếu cập nhật thành công, false nếu thất bại.
     */
    public boolean updateDetail(Detail detail) {
        String sql =
            "UPDATE Detail " +
            "   SET InvoiceID   = ?, " +
            "       MenuItemID  = ?, " +
            "       Quantity    = ?, " +
            "       UnitPrice   = ? " +
            " WHERE OrderDetailID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, detail.getInvoiceId());
            pstmt.setInt(2, detail.getMenuItemId());
            pstmt.setInt(3, detail.getQuantity());
            pstmt.setBigDecimal(4, detail.getUnitPrice());
            pstmt.setInt(5, detail.getOrderDetailId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 3. Thêm một chi tiết mới (Detail). OrderDetailID sẽ được trigger tự động
     *    từ sequence DETAIL_SEQ khi insert. Sau đó trả về OrderDetailID vừa sinh.
     * @param detail đối tượng Detail (chưa có OrderDetailID), cần set InvoiceID, MenuItemID, Quantity, UnitPrice.
     * @return OrderDetailID vừa sinh hoặc -1 nếu có lỗi.
     */
    public int addDetail(Detail detail) {
        // Insert không đưa OrderDetailID vào, sequence + trigger trên DB sẽ tự sinh.
        String sqlInsert =
            "INSERT INTO Detail (InvoiceID, MenuItemID, Quantity, UnitPrice) " +
            "VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlInsert)) {
            pstmt.setInt(1, detail.getInvoiceId());
            pstmt.setInt(2, detail.getMenuItemId());
            pstmt.setInt(3, detail.getQuantity());
            pstmt.setBigDecimal(4, detail.getUnitPrice());

            int affected = pstmt.executeUpdate();
            if (affected == 0) {
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }

        // Lấy CURRVAL của DETAIL_SEQ
        String sqlSeq = "SELECT DETAIL_SEQ.CURRVAL FROM DUAL";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sqlSeq)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 4. Thêm phương thức để tạo (insert) một Detail mới
     */
    public boolean createDetail(int invoiceId, int menuItemId, int quantity, BigDecimal unitPrice) {
        String sql =
            "INSERT INTO Detail (InvoiceID, MenuItemID, Quantity, UnitPrice) " +
            "VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, invoiceId);
            pstmt.setInt(2, menuItemId);
            pstmt.setInt(3, quantity);
            pstmt.setBigDecimal(4, unitPrice);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 5. Lấy chi tiết hóa đơn dựa trên InvoiceID
     */
    public List<Detail> getDetailsByInvoiceId(int invoiceId) {
        List<Detail> details = new ArrayList<>();
        String query = "SELECT * FROM Detail WHERE InvoiceID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, invoiceId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Detail detail = new Detail(
                    rs.getInt("OrderDetailID"),
                    rs.getInt("InvoiceID"),
                    rs.getInt("MenuItemID"),
                    rs.getInt("Quantity"),
                    rs.getBigDecimal("UnitPrice")
                );
                details.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }

    /**
     * 6. Lấy tất cả chi tiết hóa đơn
     */
    public List<Detail> getAllDetails() {
        List<Detail> details = new ArrayList<>();
        String query = "SELECT * FROM Detail";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Detail detail = new Detail(
                    rs.getInt("OrderDetailID"),
                    rs.getInt("InvoiceID"),
                    rs.getInt("MenuItemID"),
                    rs.getInt("Quantity"),
                    rs.getBigDecimal("UnitPrice")
                );
                details.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }
}
