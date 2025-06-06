/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.coffeemate.component;

import com.coffeemate.utils.SessionManager;
import com.coffeemate.view.LoginForm;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import javax.swing.SwingUtilities;

/**
 *
 * @author meiln
 */
public class CashierHeader extends javax.swing.JPanel {

    /**
     * Creates new form CashierHeader
     */
    public CashierHeader() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblHeader = new javax.swing.JLabel();
        jToggleButton1 = new javax.swing.JToggleButton();

        setForeground(new java.awt.Color(255, 255, 255));

        lblHeader.setBackground(new java.awt.Color(255, 255, 255));
        lblHeader.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        lblHeader.setText("COFFEEMATE");

        jToggleButton1.setText("ĐĂNG XUẤT");
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jToggleButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 376, Short.MAX_VALUE)
                .addComponent(lblHeader, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblHeader)
                .addContainerGap(7, Short.MAX_VALUE))
            .addComponent(jToggleButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
      // TODO add your handling code here:
   // 1) Xóa session
    SessionManager.getInstance().logout();
    // 2) Mở LoginForm
    new LoginForm().setVisible(true);
    // 3) Đóng AdminView
    Window window = SwingUtilities.getWindowAncestor(this);
    if (window != null) {
        window.dispose();
    }    }//GEN-LAST:event_jToggleButton1ActionPerformed

  @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Vẽ gradient background cho header
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(255, 179, 71), // Bắt đầu màu cam
                0, getHeight(), new Color(255, 221, 102) // Kết thúc màu vàng
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight()); // Vẽ gradient nền
    }

  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JLabel lblHeader;
    // End of variables declaration//GEN-END:variables
}
