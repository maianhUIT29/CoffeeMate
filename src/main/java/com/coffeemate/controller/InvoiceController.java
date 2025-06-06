package com.coffeemate.controller;

import com.coffeemate.dao.DetailDAO;
import com.coffeemate.dao.InvoiceDAO;
import com.coffeemate.model.Invoice;
import com.coffeemate.model.ChartDataModel;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;

public class InvoiceController {
    private final InvoiceDAO invoiceDAO;
    private final DetailDAO detailDAO;

    public InvoiceController() {
        this.invoiceDAO = new InvoiceDAO();
        this.detailDAO  = new DetailDAO();
    }

    // Lấy danh sách hóa đơn
    public List<Invoice> getAllInvoices() {
        return invoiceDAO.getAllInvoices();
    }

    // Lấy hóa đơn theo ID
    public Invoice getInvoiceById(int id) {
        return invoiceDAO.getInvoiceById(id);
    }

    // Tạo hóa đơn mới
    /**
     * Tạo mới một Invoice (=Hóa đơn) và trả về ID do DB sinh.
     * @param employeeId       ID nhân viên / thu ngân (nếu chưa có, truyền 0)
     * @param totalAmount      tổng tiền (BigDecimal)
     * @param paymentStatus    trạng thái thanh toán (ví dụ "Paid" hoặc "Unpaid")
     * @param issueDate        ngày phát hành (java.util.Date)
     * @return invoiceId vừa sinh (nếu thành công), hoặc -1 nếu thất bại
     */
    public int createInvoice(int employeeId, BigDecimal totalAmount, String paymentStatus, Date issueDate) {
        // Khởi tạo đối tượng Invoice
        Invoice invoice = new Invoice(0, employeeId, totalAmount, paymentStatus, issueDate);
        // Gọi DAO để lưu vào DB và lấy về invoiceId
        return invoiceDAO.createInvoice(invoice);
    }
    // Cập nhật hóa đơn
    public boolean updateInvoice(Invoice invoice) {
        return invoiceDAO.updateInvoice(invoice);
    }

    // Xóa hóa đơn
    public boolean deleteInvoice(int id) {
        return invoiceDAO.deleteInvoice(id);
    }

    // Top N hóa đơn giá trị cao nhất
   public List<Map<String, Object>> getTopInvoices(int limit) {
        return invoiceDAO.getTopInvoices(limit);
    }

    // Tổng doanh thu đã thanh toán
    public BigDecimal getTotalRevenue() {
        return invoiceDAO.getTotalRevenue();
    }

    // Doanh thu theo từng tháng của năm
    public List<ChartDataModel> getMonthlyRevenue(int year) {
        return invoiceDAO.getMonthlyRevenue(year);
    }

    // Doanh thu theo năm
    public List<ChartDataModel> getYearlyRevenue() {
        return invoiceDAO.getYearlyRevenue();
    }

    // Tỷ lệ số lượng hóa đơn theo trạng thái thanh toán
    public List<ChartDataModel> getInvoiceStatusDistribution() {
        return invoiceDAO.getInvoiceStatusDistribution();
    }
    //trả về tổng số hóa đơn
     public int countInvoices() {
        return invoiceDAO.countInvoices();
    }
     //lấy doanh thu hôm nay
        public BigDecimal getTodayRevenue() {
        return invoiceDAO.getTodayRevenue();
    }
     // Doanh thu theo tuần trong năm
    public List<ChartDataModel> getWeeklyRevenueByYear(int year) {
        return invoiceDAO.getWeeklyRevenueByYear(year);
    }
    //lấy năm có hóa đơn
     public List<Integer> getAvailableYears() {
        return invoiceDAO.getAvailableYears();
    }
     //trả về doanh thu tháng hiện tại
     public BigDecimal getRevenueForCurrentMonth() {
        return invoiceDAO.getRevenueForCurrentMonth();
    }
     //lấy top nv theo doanh thu
       public List<Map<String, Object>> getTopEmployeesByRevenue(int topN) {
        return invoiceDAO.getTopEmployeesByRevenue(topN);
    }
 /**
     * Xóa tất cả Detail của hóa đơn, sau đó xóa hóa đơn.
     * @param invoiceId ID hóa đơn cần xóa
     * @return true nếu xóa thành công cả Detail và Invoice; false nếu có lỗi
     */
    public boolean deleteInvoiceById(int invoiceId) {
        // 1) Xóa toàn bộ Detail có cùng InvoiceID
        boolean detailsDeleted = detailDAO.deleteByInvoiceId(invoiceId);
        if (!detailsDeleted) {
            return false;  // Nếu không xóa được detail, dừng ngay
        }
        // 2) Xóa Invoice
        return invoiceDAO.deleteInvoice(invoiceId);
    }
    
     public int createEmptyInvoice(int employeeId, String paymentStatus, java.util.Date issueDate) {
        // Chuyển java.util.Date sang java.sql.Date để DAO chèn
        Date sqlDate = new Date(issueDate.getTime());
        return invoiceDAO.createEmptyInvoice(employeeId, paymentStatus, sqlDate);
    }

    /**
     * Cập nhật lại TotalAmount của hóa đơn đã tồn tại.
     */
    public boolean updateInvoiceTotal(int invoiceId, BigDecimal newTotal) {
        return invoiceDAO.updateInvoiceTotal(invoiceId, newTotal);
    }

    /**
     * Tạo hóa đơn đầy đủ (nếu cần), bao gồm cả TotalAmount ngay từ đầu.
     */
    public int createInvoice(Invoice invoice) {
        return invoiceDAO.createInvoice(invoice);
    }
   
 } 
