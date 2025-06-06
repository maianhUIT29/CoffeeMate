package com.coffeemate.dao;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.coffeemate.configs.DBConnection;
import com.coffeemate.model.ChartDataModel;
import com.coffeemate.model.Invoice;

public class InvoiceDAO {
    private final Connection connection;

    public InvoiceDAO() {
        // Lấy kết nối đến Oracle DB
        connection = DBConnection.getConnection();
    }

    /**
     * Tạo một hóa đơn rỗng: EmployeeID, TotalAmount=0, PaymentStatus, IssueDate.
     * Trả về invoiceId do DB tự sinh hoặc -1 nếu lỗi.
     */
   // InvoiceDAO.java

public int createEmptyInvoice(int employeeId, String paymentStatus, java.sql.Date issueDate) {
    int generatedId = -1;

    String insertSql =
        "INSERT INTO Invoice (EmployeeID, TotalAmount, PaymentStatus, IssueDate) " +
        "VALUES (?, ?, ?, ?)";

    try (PreparedStatement pstmt = connection.prepareStatement(insertSql)) {
        pstmt.setInt(1, employeeId);
        pstmt.setBigDecimal(2, BigDecimal.ZERO);
        pstmt.setString(3, paymentStatus);
        pstmt.setDate(4, issueDate);

        int affected = pstmt.executeUpdate();
        if (affected == 0) {
            return -1;
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return -1;
    }

    // Lấy CURRVAL của sequence SEQ_INVOICE  <-- Tên này sai, cần đổi thành INVOICE_SEQ
    String seqValSql = "SELECT INVOICE_SEQ.CURRVAL FROM DUAL";

    try (Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(seqValSql)) {
        if (rs.next()) {
            generatedId = rs.getInt(1);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return -1;
    }

    return generatedId;
}

    /**
     * Cập nhật lại TotalAmount của hóa đơn đã tồn tại.
     */
    public boolean updateInvoiceTotal(int invoiceId, BigDecimal newTotal) {
        String sql =
            "UPDATE Invoice SET TotalAmount = ? WHERE InvoiceID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, newTotal);
            pstmt.setInt(2, invoiceId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Phương thức cũ: Tạo Invoice đầy đủ (EmployeeID, TotalAmount, PaymentStatus, IssueDate).
     */
public int createInvoice(Invoice invoice) {
    int generatedId = -1;

    String insertSql =
        "INSERT INTO Invoice (EmployeeID, TotalAmount, PaymentStatus, IssueDate) " +
        "VALUES (?, ?, ?, ?)";

    try (PreparedStatement pstmt = connection.prepareStatement(insertSql)) {
        pstmt.setInt(1, invoice.getEmployeeId());
        pstmt.setBigDecimal(2, invoice.getTotalAmount());
        pstmt.setString(3, invoice.getPaymentStatus());
        pstmt.setDate(4, new java.sql.Date(invoice.getIssueDate().getTime()));

        int affected = pstmt.executeUpdate();
        if (affected == 0) {
            return -1;
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return -1;
    }

    // Đổi tên sequence ở đây cho đúng: INVOICE_SEQ
    String seqValSql = "SELECT INVOICE_SEQ.CURRVAL FROM DUAL";

    try (Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(seqValSql)) {
        if (rs.next()) {
            generatedId = rs.getInt(1);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return -1;
    }

    return generatedId;
}

    
    /**
     * 1. Đếm tổng số hóa đơn trong bảng Invoice
     */
    public int countInvoices() {
        String sql = "SELECT COUNT(*) FROM Invoice";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 2. Lấy tổng doanh thu của tất cả hóa đơn đã thanh toán (PaymentStatus = 'Paid')
     */
    public BigDecimal getTotalRevenue() {
        String sql = 
            "SELECT NVL(SUM(TotalAmount), 0) " +
            "  FROM Invoice " +
            " WHERE PaymentStatus = 'Paid'";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    /**
     * 3. Lấy doanh thu của ngày hiện tại (chỉ tính invoice đã Paid)
     */
    public BigDecimal getTodayRevenue() {
        String sql = 
            "SELECT NVL(SUM(TotalAmount), 0) " +
            "  FROM Invoice " +
            " WHERE PaymentStatus = 'Paid' " +
            "   AND TRUNC(IssueDate) = TRUNC(SYSDATE)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    /**
     * 4. Lấy doanh thu của tháng hiện tại (chỉ tính invoice đã Paid)
     */
    public BigDecimal getRevenueForCurrentMonth() {
        String sql = 
            "SELECT NVL(SUM(TotalAmount), 0) AS total_revenue " +
            "  FROM Invoice " +
            " WHERE PaymentStatus = 'Paid' " +
            "   AND IssueDate >= TRUNC(SYSDATE, 'MM') " +
            "   AND IssueDate <  ADD_MONTHS(TRUNC(SYSDATE, 'MM'), 1)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal("total_revenue");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    /**
     * 5. Lấy danh sách các năm (distinct) mà có hóa đơn trong bảng
     */
    public List<Integer> getAvailableYears() {
        List<Integer> years = new ArrayList<>();
        String sql = 
            "SELECT DISTINCT EXTRACT(YEAR FROM IssueDate) AS year " +
            "  FROM Invoice " +
            " ORDER BY year DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                years.add(rs.getInt("year"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return years;
    }

    /**
     * 6. Doanh thu theo từng tháng của một năm nhất định (Oracle)
     */
    public List<ChartDataModel> getMonthlyRevenue(int year) {
        List<ChartDataModel> result = new ArrayList<>();
        String sql = 
            "SELECT TO_CHAR(IssueDate, 'MM') AS month, SUM(TotalAmount) AS revenue " +
            "  FROM Invoice " +
            " WHERE PaymentStatus = 'Paid' " +
            "   AND EXTRACT(YEAR FROM IssueDate) = ? " +
            " GROUP BY TO_CHAR(IssueDate, 'MM') " +
            " ORDER BY month";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, year);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String month = rs.getString("month");
                    BigDecimal revenue = rs.getBigDecimal("revenue");
                    result.add(new ChartDataModel("Tháng " + month, revenue));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 7. Doanh thu theo từng năm (Oracle)
     */
    public List<ChartDataModel> getYearlyRevenue() {
        List<ChartDataModel> result = new ArrayList<>();
        String sql = 
            "SELECT EXTRACT(YEAR FROM IssueDate) AS year, SUM(TotalAmount) AS revenue " +
            "  FROM Invoice " +
            " WHERE PaymentStatus = 'Paid' " +
            " GROUP BY EXTRACT(YEAR FROM IssueDate) " +
            " ORDER BY year";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int yr = rs.getInt("year");
                BigDecimal revenue = rs.getBigDecimal("revenue");
                result.add(new ChartDataModel(String.valueOf(yr), revenue));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 8. Doanh thu theo tuần trong năm (Oracle)
     */
    public List<ChartDataModel> getWeeklyRevenueByYear(int year) {
        List<ChartDataModel> result = new ArrayList<>();
        String sql = 
            "SELECT TO_CHAR(IssueDate, 'IW') AS week, SUM(TotalAmount) AS revenue " +
            "  FROM Invoice " +
            " WHERE PaymentStatus = 'Paid' " +
            "   AND EXTRACT(YEAR FROM IssueDate) = ? " +
            " GROUP BY TO_CHAR(IssueDate, 'IW') " +
            " ORDER BY week";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, year);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String week = rs.getString("week");
                    BigDecimal revenue = rs.getBigDecimal("revenue");
                    result.add(new ChartDataModel("Tuần " + week, revenue));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 9. Top N nhân viên có doanh thu cao nhất (JOIN với bảng Employee)
     *    Trả về List<Map> để dễ hiển thị: mỗi map chứa EmployeeID, employee_name, total_revenue
     */
    public List<Map<String, Object>> getTopEmployeesByRevenue(int topN) {
        List<Map<String, Object>> result = new ArrayList<>();   
        String sql =
            "SELECT e.EmployeeID, e.FullName AS employee_name, SUM(i.TotalAmount) AS total_revenue\n" +
            "  FROM Invoice i\n" +
            "  JOIN Employee e ON i.EmployeeID = e.EmployeeID\n" +
            " WHERE i.PaymentStatus = 'Paid'\n" +
            " GROUP BY e.EmployeeID, e.FullName\n" +
            " ORDER BY total_revenue DESC\n" +
            " FETCH FIRST ? ROWS ONLY";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, topN);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    // Lấy đúng tên cột (trùng với ALIAS trong SELECT)
                    int         empId   = rs.getInt("EmployeeID");
                    String      name    = rs.getString("employee_name");
                    BigDecimal  revenue = rs.getBigDecimal("total_revenue");

                    row.put("EmployeeID",    empId);
                    row.put("employee_name", name);
                    row.put("total_revenue", revenue);

                    result.add(row);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return result;
    }


    /**
     * 10. Top N hóa đơn có tổng tiền lớn nhất (Trả về List<Map> để dễ hiển thị)
     */
    public List<Map<String, Object>> getTopInvoices(int limit) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = 
            "SELECT i.InvoiceID, e.FullName AS employee_name, i.TotalAmount, i.IssueDate " +
            "  FROM Invoice i " +
            "  JOIN Employee e ON i.EmployeeID = e.EmployeeID " +
            " WHERE i.PaymentStatus = 'Paid' " +
            " ORDER BY i.TotalAmount DESC " +
            " FETCH FIRST ? ROWS ONLY";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("InvoiceID",      rs.getInt("InvoiceID"));
                    row.put("employee_name",  rs.getString("employee_name"));
                    row.put("TotalAmount",    rs.getBigDecimal("TotalAmount"));
                    row.put("IssueDate",      rs.getDate("IssueDate"));
                    result.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 11. Trả về một hóa đơn theo InvoiceID (sử dụng mapResultSetToInvoice để trả về đối tượng Invoice)
     */
    public Invoice getInvoiceById(int invoiceId) {
        String sql = "SELECT * FROM Invoice WHERE InvoiceID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, invoiceId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToInvoice(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 12. Trả về toàn bộ danh sách hóa đơn (trả về List<Invoice>)
     */
   public List<Invoice> getAllInvoices() {
    List<Invoice> invoices = new ArrayList<>();
    String sql = "SELECT * FROM Invoice";
    try (Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        while (rs.next()) {
            invoices.add(mapResultSetToInvoice(rs));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return invoices;
}


    

    /**
     * 14. Cập nhật hóa đơn (UPDATE)
     */
    public boolean updateInvoice(Invoice invoice) {
        String sql = 
            "UPDATE Invoice " +
            "   SET EmployeeID = ?, TotalAmount = ?, PaymentStatus = ?, IssueDate = ? " +
            " WHERE InvoiceID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, invoice.getEmployeeId());
            pstmt.setBigDecimal(2, invoice.getTotalAmount());
            pstmt.setString(3, invoice.getPaymentStatus());
            pstmt.setDate(4, new java.sql.Date(invoice.getIssueDate().getTime()));
            pstmt.setInt(5, invoice.getInvoiceId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 15. Xóa hóa đơn theo ID (DELETE)
     */
    public boolean deleteInvoice(int invoiceId) {
        String sql = "DELETE FROM Invoice WHERE InvoiceID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, invoiceId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 16. Trả về một hàng ResultSet thành đối tượng Invoice
     */
    private Invoice mapResultSetToInvoice(ResultSet rs) throws SQLException {
        int invoiceId    = rs.getInt("InvoiceID");
        int employeeId   = rs.getInt("EmployeeID");
        BigDecimal total = rs.getBigDecimal("TotalAmount");
        String status    = rs.getString("PaymentStatus");
        Date issueDate   = rs.getDate("IssueDate");
        return new Invoice(invoiceId, employeeId, total, status, issueDate);
    }

    /**
     * 17. Tỷ lệ số lượng hóa đơn theo trạng thái thanh toán (Paid/Unpaid)
     */
    public List<ChartDataModel> getInvoiceStatusDistribution() {
        List<ChartDataModel> result = new ArrayList<>();
        String sql = 
            "SELECT PaymentStatus AS status, COUNT(*) AS cnt " +
            "  FROM Invoice " +
            " GROUP BY PaymentStatus";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String status = rs.getString("status");
                int count     = rs.getInt("cnt");
                result.add(new ChartDataModel(status, BigDecimal.valueOf(count)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    
}
