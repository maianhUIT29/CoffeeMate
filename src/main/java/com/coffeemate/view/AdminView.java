package com.coffeemate.view;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import com.coffeemate.component.AdDashBoard;
import com.coffeemate.component.InvoicePanel;  // ← import InvoicePanel


/**
 * AdminView là cửa sổ chính dành cho quản trị viên, sử dụng CardLayout để chuyển đổi giữa các panel.
 */
public class AdminView extends javax.swing.JFrame {
    private CardLayout cardLayout;

    public AdminView() {
        try {
            // Set system look and feel
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            initComponents();
            initCustomComponents();

            setMinimumSize(new Dimension(800, 600)); // Kích thước tối thiểu
            setLocationRelativeTo(null);             // Canh giữa màn hình
            setExtendedState(JFrame.MAXIMIZED_BOTH); // Mở rộng toàn màn hình
            setVisible(true);                        // Hiển thị

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khởi tạo giao diện: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initCustomComponents() {
        // Khởi tạo CardLayout cho CardPanel
        cardLayout = (CardLayout) CardPanel.getLayout();

        // Thêm các panel vào CardPanel với key tương ứng
        CardPanel.add(new com.coffeemate.component.MenuPanel(),     "MENU");
        CardPanel.add(new com.coffeemate.component.EmployeePanel(), "EMPLOYEE");
        CardPanel.add(new AdDashBoard(),                             "DASHBOARD");
        CardPanel.add(new InvoicePanel(),                            "INVOICE"); // ← Thêm InvoicePanel
       

        // Hiển thị panel mặc định
        cardLayout.show(CardPanel, "DASHBOARD");

        // Lắng nghe sự kiện chọn menu trên sidebar
        sidebar.setMenuSelectionListener(menuKey -> {
            // Giả sử sidebar trả về các key như "MENU", "EMPLOYEE", "DASHBOARD", "INVOICE"
            cardLayout.show(CardPanel, menuKey);
        });
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        cashierHeader = new com.coffeemate.component.CashierHeader();
        DisplayPanel   = new javax.swing.JPanel();
        sidebar        = new com.coffeemate.component.Sidebar();
        jspAdmin       = new javax.swing.JScrollPane();
        CardPanel      = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        cashierHeader.setMinimumSize(new java.awt.Dimension(170, 40));
        getContentPane().add(cashierHeader, java.awt.BorderLayout.PAGE_START);

        // CardPanel sẽ dùng CardLayout kiểu (0,0)
        CardPanel.setLayout(new java.awt.CardLayout());
        jspAdmin.setViewportView(CardPanel);

        javax.swing.GroupLayout DisplayPanelLayout = new javax.swing.GroupLayout(DisplayPanel);
        DisplayPanel.setLayout(DisplayPanelLayout);
        DisplayPanelLayout.setHorizontalGroup(
            DisplayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DisplayPanelLayout.createSequentialGroup()
                .addComponent(sidebar, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(jspAdmin, javax.swing.GroupLayout.DEFAULT_SIZE, 669, Short.MAX_VALUE))
        );
        DisplayPanelLayout.setVerticalGroup(
            DisplayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DisplayPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(DisplayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sidebar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jspAdmin, javax.swing.GroupLayout.DEFAULT_SIZE, 397, Short.MAX_VALUE)))
        );

        getContentPane().add(DisplayPanel, java.awt.BorderLayout.CENTER);

        pack();
    }

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        EventQueue.invokeLater(() -> {
            LoginForm view = new LoginForm();
            view.setVisible(true);
        });
    }

    // Variables declaration - do not modify
    private com.coffeemate.component.CashierHeader cashierHeader;
    private javax.swing.JPanel CardPanel;
    private javax.swing.JPanel DisplayPanel;
    private javax.swing.JScrollPane jspAdmin;
    private com.coffeemate.component.Sidebar sidebar;
    // End of variables declaration
}
