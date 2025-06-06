package com.coffeemate.model;

import java.math.BigDecimal;
import java.util.Date;

public class Invoice {
    private int invoiceId;          // Khóa chính
    private int employeeId;         // Khóa ngoại liên kết với Employee
    private BigDecimal totalAmount; // Tổng số tiền của hóa đơn
    private String paymentStatus;   // Trạng thái thanh toán của hóa đơn
    private Date issueDate;         // Ngày xuất hóa đơn

    // Constructor
    public Invoice(int invoiceId, int employeeId, BigDecimal totalAmount, String paymentStatus, Date issueDate) {
        this.invoiceId = invoiceId;
        this.employeeId = employeeId;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
        this.issueDate = issueDate;
    }

    // Getters and Setters
    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }
}
