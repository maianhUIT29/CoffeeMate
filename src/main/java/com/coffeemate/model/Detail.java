
package com.coffeemate.model;


import java.math.BigDecimal;

public class Detail {
    private int orderDetailId; // Khóa chính
    private int invoiceId;     // Khóa ngoại liên kết với Invoice
    private int menuItemId;    // Khóa ngoại liên kết với MenuItem
    private int quantity;      // Số lượng món ăn
    private BigDecimal unitPrice; // Giá của mỗi món ăn

    // Constructor
    public Detail(int orderDetailId, int invoiceId, int menuItemId, int quantity, BigDecimal unitPrice) {
        this.orderDetailId = orderDetailId;
        this.invoiceId = invoiceId;
        this.menuItemId = menuItemId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    // Getters and Setters
    public int getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(int orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(int menuItemId) {
        this.menuItemId = menuItemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}

