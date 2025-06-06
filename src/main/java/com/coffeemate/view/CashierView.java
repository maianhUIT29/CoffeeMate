package com.coffeemate.view;

import com.coffeemate.component.CashierHeader;
import com.coffeemate.controller.DetailController;
import com.coffeemate.controller.InvoiceController;
import com.coffeemate.controller.MenuItemController;
import com.coffeemate.model.Employee;
import com.coffeemate.model.MenuItem;
import com.coffeemate.utils.SessionManager;
import com.formdev.flatlaf.FlatLightLaf;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * CashierView: giao diện POS với FlatLaf, header “COFFEEMATE”,
 * có nhãn "Tìm kiếm" bên cạnh thanh tìm, nút Xóa món và Sửa SL trên bảng,
 * chỉ có một nút “Thanh toán” vừa tạo hóa đơn rỗng, tạo detail, cập nhật total, và xuất Excel.
 */
public class CashierView extends JFrame {

    private final MenuItemController menuItemController;
    private final InvoiceController invoiceController;
    private final DetailController detailController;

    private JTable tblInvoice;
    private DefaultTableModel invoiceTableModel;
    private JLabel lblTotal;
    private JPanel pnlMenuCards;
    private JTextField txtSearch;
    // checkoutMap: key = menuItemID, value = InvoiceLine (chứa MenuItem và số lượng)
    private final Map<Integer, InvoiceLine> checkoutMap = new HashMap<>();
    // map từ menuItemID → filename hình ảnh
    private final Map<Integer, String> imageMap = new HashMap<>();
    private java.util.List<MenuItem> allMenuItems;

    public CashierView() {
        // Thiết lập FlatLaf và UI defaults
        FlatLightLaf.setup();
        UIManager.put("Button.arc", 999);
        UIManager.put("Component.arc", 8);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.showVerticalLines", false);

        menuItemController = new MenuItemController();
        invoiceController  = new InvoiceController();
        detailController   = new DetailController();

        setTitle("CoffeeMate - Cashier POS");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);

        // Ánh xạ menuItemID → file ảnh
        imageMap.put(1, "coffee_black.png");
        imageMap.put(2, "latte.png");
        imageMap.put(3, "cappuccino.png");
        imageMap.put(4, "coffee_black.png");
        imageMap.put(5, "latte.png");
        imageMap.put(6, "cappuccino.png");
        imageMap.put(7, "coffee_black.png");
        imageMap.put(8, "latte.png");
        imageMap.put(9, "cappuccino.png");
        imageMap.put(10, "coffee_black.png");
        imageMap.put(11, "latte.png");
        imageMap.put(12, "cappuccino.png");
        imageMap.put(13, "coffee_black.png");
        imageMap.put(14, "latte.png");
        imageMap.put(15, "cappuccino.png");
        imageMap.put(16, "cappuccino.png");
        imageMap.put(17, "cappuccino.png");
        imageMap.put(18, "cappuccino.png");
        imageMap.put(19, "cappuccino.png");
        imageMap.put(20, "cappuccino.png");
        // … nếu có thêm ID khác, thêm vào đây …
        // Nếu không có ánh xạ, sử dụng "no_image.png" mặc định trong MenuItemCard.

        allMenuItems = menuItemController.getAllMenuItems();
        initComponents();
        loadMenuItemsAsCards(allMenuItems);
    }

    private void initComponents() {
        getContentPane().setBackground(new Color(245, 245, 245));
        setLayout(new BorderLayout());

        // === 1. Header phía trên: CashierHeader ===
        CashierHeader header = new CashierHeader();
        header.setPreferredSize(new Dimension(getWidth(), 60));
        add(header, BorderLayout.NORTH);

        // === 2. JSplitPane giữa hai vùng trái–phải ===
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerSize(3);
        // Đổi tỉ lệ: bên trái chiếm 60%, bên phải chiếm 40%
        splitPane.setResizeWeight(0.8);
        splitPane.setBorder(null);

        // ===== Phần BÊN TRÁI: Search + Panel chứa cards =====
        JPanel leftWrapper = new JPanel(new BorderLayout());
        leftWrapper.setBackground(new Color(245, 245, 245));

        // Search bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(new Color(245, 245, 245));
        searchPanel.setBorder(new EmptyBorder(10, 15, 0, 15));

        JLabel lblSearch = new JLabel("Tìm kiếm:");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSearch.setForeground(new Color(55, 71, 79));
        searchPanel.add(lblSearch);

        txtSearch = new JTextField();
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setPreferredSize(new Dimension(200, 28));
        txtSearch.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
        txtSearch.setToolTipText("Tìm món theo tên...");
        searchPanel.add(txtSearch);

        // Lọc cards khi gõ trong ô tìm kiếm
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filterCards(); }
            @Override public void removeUpdate(DocumentEvent e) { filterCards(); }
            @Override public void changedUpdate(DocumentEvent e) { filterCards(); }
            private void filterCards() {
                String text = txtSearch.getText().trim().toLowerCase();
                if (text.isEmpty()) {
                    loadMenuItemsAsCards(allMenuItems);
                } else {
                    java.util.List<MenuItem> filtered = new ArrayList<>();
                    for (MenuItem mi : allMenuItems) {
                        if ("Available".equalsIgnoreCase(mi.getStatus())
                            && mi.getItemName().toLowerCase().contains(text)) {
                            filtered.add(mi);
                        }
                    }
                    loadMenuItemsAsCards(filtered);
                }
            }
        });

        // Panel chứa các MenuItemCard (GridLayout 3 cột)
        pnlMenuCards = new JPanel();
        pnlMenuCards.setLayout(new GridLayout(0, 3, 15, 15));
        pnlMenuCards.setBorder(new EmptyBorder(15, 15, 15, 15));
        pnlMenuCards.setBackground(new Color(245, 245, 245));

        JScrollPane scrollLeft = new JScrollPane(pnlMenuCards);
        scrollLeft.setBorder(null);
        scrollLeft.getVerticalScrollBar().setUnitIncrement(16);

        leftWrapper.add(searchPanel, BorderLayout.NORTH);
        leftWrapper.add(scrollLeft, BorderLayout.CENTER);

        // ===== Phần BÊN PHẢI: Bảng hóa đơn + các nút =====
        // 1) Tạo TableModel và JTable cho hóa đơn
        invoiceTableModel = new DefaultTableModel(
            new Object[]{"Tên món", "Đơn giá", "SL", "Thành tiền"}, 0
        ) {
            @Override public boolean isCellEditable(int row, int column) {
                return column == 2; // Chỉ cột SL được chỉnh trực tiếp
            }
            @Override public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return String.class;
                    case 1:
                    case 3: return Float.class;
                    case 2: return Integer.class;
                }
                return Object.class;
            }
        };

        tblInvoice = new JTable(invoiceTableModel);
        tblInvoice.setRowHeight(28);
        tblInvoice.setShowGrid(false);
        tblInvoice.setIntercellSpacing(new Dimension(0, 0));
        tblInvoice.getTableHeader().setReorderingAllowed(false);
        tblInvoice.getTableHeader().setDefaultRenderer(new HeaderRenderer());
        tblInvoice.putClientProperty("terminateEditOnFocusLost", true);

        // Nghe thay đổi cột SL để cập nhật lại Thành tiền và tổng
        tblInvoice.getModel().addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE
                && e.getColumn() == 2) {
                int row = e.getFirstRow();
                Integer qty = (Integer) invoiceTableModel.getValueAt(row, 2);
                if (qty == null || qty <= 0) qty = 1;
                Float unitPrice = (Float) invoiceTableModel.getValueAt(row, 1);
                float lineTotal = unitPrice * qty;
                invoiceTableModel.setValueAt(lineTotal, row, 3);

                // Cập nhật checkoutMap tương ứng
                String itemName = (String) invoiceTableModel.getValueAt(row, 0);
                for (Map.Entry<Integer, InvoiceLine> entry : checkoutMap.entrySet()) {
                    if (entry.getValue().getMenuItem().getItemName().equals(itemName)) {
                        entry.getValue().setQuantity(qty);
                        break;
                    }
                }
                recalcTotal();
            }
        });

        JScrollPane scrollInvoice = new JScrollPane(tblInvoice);
        scrollInvoice.setBorder(null);

        // 2) Panel phía trên bảng: tiêu đề “Hóa đơn” và 2 nút “Xóa món”, “Sửa SL”
        JLabel rightTitle = new JLabel("Hóa đơn");
        rightTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        rightTitle.setBorder(new EmptyBorder(10, 15, 0, 0));

        JButton btnDelete = new JButton("Xóa món");
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDelete.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDelete.setBackground(new Color(229, 57, 53));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.setBorder(new EmptyBorder(6, 20, 6, 20));
        btnDelete.addMouseListener(new ButtonHoverEffect(btnDelete, new Color(211, 47, 47)));
        btnDelete.addActionListener(e -> {
            int selectedRow = tblInvoice.getSelectedRow();
            if (selectedRow >= 0) {
                String itemName = (String) invoiceTableModel.getValueAt(selectedRow, 0);
                Integer keyToRemove = null;
                for (Map.Entry<Integer, InvoiceLine> entry : checkoutMap.entrySet()) {
                    if (entry.getValue().getMenuItem().getItemName().equals(itemName)) {
                        keyToRemove = entry.getKey();
                        break;
                    }
                }
                if (keyToRemove != null) {
                    checkoutMap.remove(keyToRemove);
                    refreshInvoiceTable();
                    recalcTotal();
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một món trên hóa đơn để xóa.",
                    "Chưa chọn món",
                    JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton btnEditQty = new JButton("Sửa SL");
        btnEditQty.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEditQty.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEditQty.setBackground(new Color(255, 193, 7));
        btnEditQty.setForeground(Color.WHITE);
        btnEditQty.setFocusPainted(false);
        btnEditQty.setBorder(new EmptyBorder(6, 20, 6, 20));
        btnEditQty.addMouseListener(new ButtonHoverEffect(btnEditQty, new Color(255, 160, 0)));
        btnEditQty.addActionListener(e -> {
            int selectedRow = tblInvoice.getSelectedRow();
            if (selectedRow >= 0) {
                Integer currentQty = (Integer) invoiceTableModel.getValueAt(selectedRow, 2);
                String input = JOptionPane.showInputDialog(
                    this,
                    "Nhập số lượng mới (phải > 0):",
                    currentQty
                );
                if (input != null) {
                    try {
                        int newQty = Integer.parseInt(input.trim());
                        if (newQty <= 0) {
                            JOptionPane.showMessageDialog(this,
                                "Số lượng phải lớn hơn 0.",
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        Float unitPrice = (Float) invoiceTableModel.getValueAt(selectedRow, 1);
                        float newLineTotal = unitPrice * newQty;
                        invoiceTableModel.setValueAt(newQty, selectedRow, 2);
                        invoiceTableModel.setValueAt(newLineTotal, selectedRow, 3);

                        String itemName = (String) invoiceTableModel.getValueAt(selectedRow, 0);
                        for (Map.Entry<Integer, InvoiceLine> entry : checkoutMap.entrySet()) {
                            if (entry.getValue().getMenuItem().getItemName().equals(itemName)) {
                                entry.getValue().setQuantity(newQty);
                                break;
                            }
                        }
                        recalcTotal();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this,
                            "Vui lòng nhập một số nguyên hợp lệ.",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một món trên hóa đơn để sửa SL.",
                    "Chưa chọn món",
                    JOptionPane.WARNING_MESSAGE);
            }
        });

        JPanel northRightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        northRightPanel.setBackground(new Color(245, 245, 245));
        northRightPanel.add(rightTitle);
        northRightPanel.add(btnDelete);
        northRightPanel.add(btnEditQty);
        northRightPanel.setBorder(new EmptyBorder(5, 15, 5, 15));

        // 3) Panel chứa TỔNG TIỀN và NÚT “THANH TOÁN”
        lblTotal = new JLabel("Tổng: 0 VND");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JButton btnPay = new JButton("Thanh toán");
        btnPay.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnPay.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnPay.setBackground(new Color(0, 150, 136));
        btnPay.setForeground(Color.WHITE);
        btnPay.setFocusPainted(false);
        btnPay.setBorder(new EmptyBorder(6, 20, 6, 20));
        btnPay.addMouseListener(new ButtonHoverEffect(btnPay, new Color(0, 137, 123)));
        btnPay.addActionListener(e -> {
    if (checkoutMap.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "Hóa đơn đang trống, không thể thanh toán.",
            "Lỗi",
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    // 1. Lấy EmployeeID từ SessionManager
    Employee loggedIn = SessionManager.getInstance().getLoggedInUser();
    if (loggedIn == null) {
        JOptionPane.showMessageDialog(this,
            "Không xác định được thu ngân hiện tại!",
            "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }
    int employeeId = loggedIn.getEmployeeID();

    try {
        // 2. Tạo một hóa đơn rỗng (TotalAmount = 0, PaymentStatus = "Paid")
        int invoiceId = invoiceController.createEmptyInvoice(
            employeeId, 
            "Paid", 
            new java.util.Date()
        );
        if (invoiceId <= 0) {
            throw new Exception("Không tạo được invoice mới (ID trả về <= 0).");
        }

        // 3. Lưu từng detail
        for (InvoiceLine line : checkoutMap.values()) {
            int menuItemId = line.getMenuItem().getMenuItemID();
            int quantity   = line.getQuantity();
            BigDecimal unitPrice = BigDecimal.valueOf(line.getMenuItem().getPrice());

            boolean ok = detailController.createDetail(
                invoiceId, 
                menuItemId, 
                quantity, 
                unitPrice
            );
            if (!ok) {
                throw new Exception(
                    "Lỗi khi lưu detail cho MenuItemID=" + menuItemId
                );
            }
        }

        // 4. Tính tổng tiền thực
        BigDecimal totalValue = checkoutMap.values().stream()
            .map(l -> BigDecimal.valueOf(l.getMenuItem().getPrice())
                                .multiply(BigDecimal.valueOf(l.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 5. Cập nhật lại TotalAmount vào bảng Invoice
        boolean updated = invoiceController.updateInvoiceTotal(invoiceId, totalValue);
        if (!updated) {
            throw new Exception("Không cập nhật được TotalAmount cho InvoiceID=" + invoiceId);
        }

        // 6. Xuất Excel
        exportInvoiceToExcel();

        JOptionPane.showMessageDialog(this,
            "Lưu hóa đơn (ID=" + invoiceId + "), cập nhật tổng và xuất Excel thành công!",
            "Thành công",
            JOptionPane.INFORMATION_MESSAGE);
        clearInvoice();
    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this,
            "Lỗi khi xử lý thanh toán: " + ex.getMessage(),
            "Lỗi",
            JOptionPane.ERROR_MESSAGE);
    }
});


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.add(lblTotal);
        buttonPanel.add(btnPay);

        // 4) Wrapper bên phải: ghép tất cả
        JPanel rightWrapper = new JPanel(new BorderLayout());
        rightWrapper.setBackground(new Color(245, 245, 245));
        rightWrapper.add(northRightPanel, BorderLayout.NORTH);
        rightWrapper.add(scrollInvoice, BorderLayout.CENTER);
        rightWrapper.add(buttonPanel, BorderLayout.SOUTH);

        // 5) Đưa vào JSplitPane
        splitPane.setLeftComponent(leftWrapper);
        splitPane.setRightComponent(rightWrapper);
        add(splitPane, BorderLayout.CENTER);
    }

    /**
     * Tải danh sách MenuItem thành các cards bên trái.
     */
    private void loadMenuItemsAsCards(java.util.List<MenuItem> list) {
        pnlMenuCards.removeAll();
        for (MenuItem mi : list) {
            if (!"Available".equalsIgnoreCase(mi.getStatus())) continue;
            MenuItemCard card = new MenuItemCard(mi);
            pnlMenuCards.add(card);
        }
        pnlMenuCards.revalidate();
        pnlMenuCards.repaint();
    }

    /**
     * Tính lại tổng tiền và hiển thị lên lblTotal.
     */
    private void recalcTotal() {
        float total = 0f;
        for (InvoiceLine line : checkoutMap.values()) {
            total += line.getMenuItem().getPrice() * line.getQuantity();
        }
        lblTotal.setText(String.format("Tổng: %.0f VND", total));
    }

    /**
     * Xóa hết hóa đơn hiện tại (map + table + cập nhật lblTotal).
     */
    private void clearInvoice() {
        checkoutMap.clear();
        invoiceTableModel.setRowCount(0);
        recalcTotal();
    }

    /**
     * Xuất hóa đơn sang file Excel (.xlsx) bằng Apache POI.
     * Nếu checkoutMap trống, hiện cảnh báo và return.
     */
    private void exportInvoiceToExcel() {
        if (checkoutMap.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Hóa đơn trống, không thể xuất Excel.",
                "Lỗi",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Hóa đơn");

        // Tạo header cột
        Row headerRow = sheet.createRow(0);
        String[] columns = {"Tên món", "Đơn giá", "SL", "Thành tiền"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        // Điền dữ liệu hóa đơn
        int rowNum = 1;
        for (InvoiceLine line : checkoutMap.values()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(line.getMenuItem().getItemName());
            row.createCell(1).setCellValue(line.getMenuItem().getPrice());
            row.createCell(2).setCellValue(line.getQuantity());
            float lineTotal = line.getMenuItem().getPrice() * line.getQuantity();
            row.createCell(3).setCellValue(lineTotal);
        }

        // Dòng tổng
        Row totalRow = sheet.createRow(rowNum);
        totalRow.createCell(2).setCellValue("Tổng:");
        totalRow.createCell(3).setCellValue(
            checkoutMap.values().stream()
                .mapToDouble(l -> l.getMenuItem().getPrice() * l.getQuantity())
                .sum()
        );

        // Tự động điều chỉnh độ rộng cột
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        String fileName = "Invoice_" + System.currentTimeMillis() + ".xlsx";
        File outFile = new File(System.getProperty("user.dir"), fileName);
        try (FileOutputStream fos = new FileOutputStream(outFile)) {
            workbook.write(fos);
            workbook.close();
            JOptionPane.showMessageDialog(this,
                "Đã lưu Excel tại:\n" + outFile.getAbsolutePath(),
                "Xuất Excel thành công",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khi xuất Excel:\n" + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * InvoiceLine: lưu tạm mỗi dòng trong hóa đơn (MenuItem + số lượng).
     */
    private static class InvoiceLine {
        private final MenuItem menuItem;
        private int quantity;
        public InvoiceLine(MenuItem menuItem, int quantity) {
            this.menuItem = menuItem;
            this.quantity = quantity;
        }
        public MenuItem getMenuItem() { return menuItem; }
        public int getQuantity()    { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    /**
     * MenuItemCard: hiển thị từng món (ảnh + tên + mô tả + giá) theo card.
     * Khi click, thêm vào checkoutMap (nếu chưa có) hoặc +1 số lượng.
     */
    private class MenuItemCard extends JPanel {
        private final MenuItem menuItem;
        public MenuItemCard(MenuItem menuItem) {
            this.menuItem = menuItem;
            initCard();
        }
        private void initCard() {
            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(200, 260));
            setBackground(Color.WHITE);
            setBorder(new LineBorder(new Color(220, 220, 220), 1, true));

            // Ảnh
            JLabel lblImage = new JLabel();
            lblImage.setHorizontalAlignment(JLabel.CENTER);
            String fileName = imageMap.getOrDefault(menuItem.getMenuItemID(), "no_image.png");
            java.net.URL imgUrl = getClass().getResource("/resource/coffeemate/resources/" + fileName);
            if (imgUrl != null) {
                ImageIcon rawIcon = new ImageIcon(imgUrl);
                Image scaled = rawIcon.getImage().getScaledInstance(180, 120, Image.SCALE_SMOOTH);
                lblImage.setIcon(new ImageIcon(scaled));
            } else {
                lblImage.setPreferredSize(new Dimension(180, 120));
                lblImage.setOpaque(true);
                lblImage.setBackground(new Color(238, 238, 238));
                lblImage.setText("No Image");
                lblImage.setForeground(new Color(117, 117, 117));
                lblImage.setHorizontalTextPosition(JLabel.CENTER);
                lblImage.setVerticalTextPosition(JLabel.CENTER);
            }
            add(lblImage, BorderLayout.NORTH);

            // Thông tin: tên + mô tả + giá
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setOpaque(false);
            infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            JLabel lblName = new JLabel(menuItem.getItemName());
            lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblName.setForeground(new Color(33, 33, 33));

            JLabel lblDesc = new JLabel("<html><body style='width:160px'>" +
                    menuItem.getDescription() + "</body></html>");
            lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblDesc.setForeground(new Color(117, 117, 117));

            JLabel lblPrice = new JLabel(String.format("%.0f VND", menuItem.getPrice()));
            lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblPrice.setForeground(new Color(0, 150, 136));

            infoPanel.add(lblName);
            infoPanel.add(Box.createVerticalStrut(6));
            infoPanel.add(lblDesc);
            infoPanel.add(Box.createVerticalGlue());
            infoPanel.add(lblPrice);
            add(infoPanel, BorderLayout.CENTER);

            // Khi hover/click
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    int id = menuItem.getMenuItemID();
                    if (checkoutMap.containsKey(id)) {
                        InvoiceLine line = checkoutMap.get(id);
                        line.setQuantity(line.getQuantity() + 1);
                    } else {
                        checkoutMap.put(id, new InvoiceLine(menuItem, 1));
                    }
                    refreshInvoiceTable();
                    recalcTotal();
                }
                @Override public void mouseEntered(MouseEvent e) {
                    setBackground(new Color(250, 250, 250));
                }
                @Override public void mouseExited(MouseEvent e) {
                    setBackground(Color.WHITE);
                }
            });
        }
    }

    /**
     * Cập nhật lại bảng tblInvoice từ checkoutMap.
     */
    private void refreshInvoiceTable() {
        invoiceTableModel.setRowCount(0);
        for (InvoiceLine line : checkoutMap.values()) {
            MenuItem mi = line.getMenuItem();
            int qty = line.getQuantity();
            float unitPrice = mi.getPrice();
            float lineTotal = unitPrice * qty;
            invoiceTableModel.addRow(new Object[]{
                mi.getItemName(),
                unitPrice,
                qty,
                lineTotal
            });
        }
    }

    /**
     * Renderer cho header bảng (màu nền, font, viền)
     */
    private static class HeaderRenderer extends JLabel implements javax.swing.table.TableCellRenderer {
        public HeaderRenderer() {
            setOpaque(true);
            setBackground(new Color(224, 224, 224));
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setForeground(new Color(55, 71, 79));
            setBorder(new EmptyBorder(5, 5, 5, 5));
        }
        @Override public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value == null ? "" : value.toString());
            return this;
        }
    }

    /**
     * Hiệu ứng hover đổi màu background cho JButton
     */
    private static class ButtonHoverEffect extends MouseAdapter {
        private final JButton button;
        private final Color hoverColor;
        private final Color originalColor;
        public ButtonHoverEffect(JButton button, Color hoverColor) {
            this.button = button;
            this.hoverColor = hoverColor;
            this.originalColor = button.getBackground();
        }
        @Override public void mouseEntered(MouseEvent e) {
            button.setBackground(hoverColor);
        }
        @Override public void mouseExited(MouseEvent e) {
            button.setBackground(originalColor);
        }
    }

    public static void main(String[] args) {
        // Nếu chạy riêng, vẫn set FlatLaf ở đây
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }
        SwingUtilities.invokeLater(() -> {
            CashierView view = new CashierView();
            view.setVisible(true);
        });
    }
}
