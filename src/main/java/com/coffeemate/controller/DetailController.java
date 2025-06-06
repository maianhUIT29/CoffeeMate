/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.coffeemate.controller;



import com.coffeemate.dao.DetailDAO;
import com.coffeemate.dao.InvoiceDAO;
import com.coffeemate.model.Detail;
import java.math.BigDecimal;
import java.util.List;

public class DetailController {
    private final DetailDAO detailDAO;
private final InvoiceDAO invoiceDAO;

    public DetailController() {
        invoiceDAO = new InvoiceDAO();
        detailDAO  = new DetailDAO();
    }
    
    /**
     * Thêm một Detail từ đối tượng Detail (chưa có OrderDetailId).
     * Trả về OrderDetailID vừa sinh, hoặc -1 nếu lỗi.
     *
     * @param detail đối tượng Detail (orderDetailId chưa có giá trị).
     * @return OrderDetailID vừa sinh, hoặc -1 nếu lỗi.
     */
    public int addDetail(Detail detail) {
        return detailDAO.addDetail(detail);
    }

    /**
     * Cập nhật một Detail đã tồn tại.
     * @param detail Detail mới (có OrderDetailId).
     * @return true nếu cập nhật thành công, false nếu thất bại.
     */
    public boolean updateDetail(Detail detail) {
        return detailDAO.updateDetail(detail);
    }

    /**
     * Xóa một Detail theo OrderDetailID.
     * @param detailId ID của detail cần xóa.
     * @return true nếu xóa thành công, false nếu thất bại.
     */
    public boolean deleteDetail(int detailId) {
        return detailDAO.deleteDetail(detailId);
    }

/**
     * Tạo một Detail (một dòng chi tiết) cho hóa đơn đã có.
     * @param invoiceId   ID của hóa đơn (InvoiceID)
     * @param menuItemId  ID của món (MenuItemID)
     * @param quantity    số lượng
     * @param unitPrice   đơn giá (BigDecimal)
     * @return true nếu chèn thành công, false nếu thất bại
     */
    public boolean createDetail(int invoiceId, int menuItemId, int quantity, BigDecimal unitPrice) {
        return detailDAO.createDetail(invoiceId, menuItemId, quantity, unitPrice);
    }
    // Lấy chi tiết hóa đơn theo InvoiceID
    public List<Detail> getDetailsByInvoiceId(int invoiceId) {
        return detailDAO.getDetailsByInvoiceId(invoiceId);
    }

    // Lấy tất cả chi tiết hóa đơn
    public List<Detail> getAllDetails() {
        return detailDAO.getAllDetails();
    }
    
     
}

