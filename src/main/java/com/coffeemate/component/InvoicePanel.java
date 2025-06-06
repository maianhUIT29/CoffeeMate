package com.coffeemate.component;

import com.coffeemate.controller.DetailController;
import com.coffeemate.controller.EmployeeController;
import com.coffeemate.controller.InvoiceController;
import com.coffeemate.controller.MenuItemController;
import com.coffeemate.model.Detail;
import com.coffeemate.model.Employee;
import com.coffeemate.model.Invoice;
import com.coffeemate.model.MenuItem;
import com.formdev.flatlaf.FlatLightLaf;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * InvoicePanel: Quản lý hóa đơn cho CoffeeMate
 * - Đọc từ bảng Invoice + Employee + Detail + MenuItem
 * - Cho phép Sửa / Xóa / Xem chi tiết / Xuất Excel
 * - Phân trang (pagination) & tìm kiếm (search)
 */
public class InvoicePanel extends JPanel {
    private final InvoiceController invoiceController   = new InvoiceController();
    private final DetailController detailController     = new DetailController();
    private final EmployeeController employeeController = new EmployeeController();
    private final MenuItemController menuItemController = new MenuItemController();

    private final DefaultTableModel tableModel;
    private final JTable invoiceTable;
    private final JTextField searchField;
    private final JSpinner pageSpinner;
    private final JLabel totalPagesLabel;

    private String currentSearch = "";
    private int currentPage = 1;
    private int totalPages = 1;
    private final int pageSize = 20;

    // Màu sắc theo style Flat / Bootstrap-like
    private static final Color PRIMARY_COLOR    = new Color(0, 123, 255);
    private static final Color SECONDARY_COLOR  = new Color(108, 117, 125);
    private static final Color SUCCESS_COLOR    = new Color(40, 167, 69);
    private static final Color DANGER_COLOR     = new Color(220, 53, 69);
    private static final Color WARNING_COLOR    = new Color(255, 193, 7);
    private static final Color INFO_COLOR       = new Color(23, 162, 184);
    private static final Color LIGHT_COLOR      = new Color(248, 249, 250);
    private static final Color DARK_COLOR       = new Color(52, 58, 64);
    private static final Color WHITE_COLOR      = new Color(255, 255, 255);
    private static final Color BORDER_COLOR     = new Color(222, 226, 230);
    private static final Color CARD_COLOR       = new Color(255, 255, 255);
    private static final Color TEXT_COLOR       = new Color(33, 37, 41);
    private static final Color TEXT_SECONDARY   = new Color(108, 117, 125);

    public InvoicePanel() {
    setLayout(new BorderLayout());
    setBackground(new Color(245, 245, 245));

    // === HEADER CHÍNH (Gradient) ===
    JLabel titleLabel = new JLabel("Quản lý hóa đơn");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
    titleLabel.setForeground(WHITE_COLOR);

    JPanel headerPanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(
                    0, 0, PRIMARY_COLOR,
                    getWidth(), 0, PRIMARY_COLOR.darker()
            );
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.dispose();
        }
    };
    headerPanel.setLayout(new BorderLayout());
    headerPanel.setPreferredSize(new Dimension(0, 70));
    headerPanel.setBorder(new EmptyBorder(5, 20, 5, 20));
    headerPanel.add(titleLabel, BorderLayout.WEST);

    // Thêm headerPanel vào NORTH
    add(headerPanel, BorderLayout.NORTH);

    // === TOOLBAR (Sửa / Xóa / Xuất Excel / Làm mới) + Search ===
    JPanel toolbarWrap = new JPanel(new BorderLayout());
    toolbarWrap.setBackground(CARD_COLOR);
    toolbarWrap.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR),
            new EmptyBorder(10, 10, 10, 10)
    ));

    // --- Nút chức năng ---
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    buttonPanel.setBackground(CARD_COLOR);

    JButton editBtn    = createIconButton("Sửa HĐ", INFO_COLOR,    "/icons/edit.png");
    JButton deleteBtn  = createIconButton("Xóa HĐ", DANGER_COLOR,  "/icons/delete.png");
    JButton exportBtn  = createIconButton("Xuất Excel", PRIMARY_COLOR, "/icons/export.png");
    JButton refreshBtn = createIconButton("Làm mới", SECONDARY_COLOR, "/icons/refresh.png");

    buttonPanel.add(editBtn);
    buttonPanel.add(deleteBtn);
    buttonPanel.add(exportBtn);
    buttonPanel.add(refreshBtn);

    // --- Search bar ---
    JPanel searchWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    searchWrap.setBackground(CARD_COLOR);
    JLabel searchLabel = new JLabel("Tìm kiếm:");
    searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    searchLabel.setForeground(TEXT_SECONDARY);

    searchField = new JTextField(20);
    searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    searchField.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(4, 8, 4, 8)
    ));
    searchField.setToolTipText("Tìm theo Mã HĐ hoặc Trạng thái");
    searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
        public void insertUpdate(javax.swing.event.DocumentEvent e) { filterAndLoad(); }
        public void removeUpdate(javax.swing.event.DocumentEvent e) { filterAndLoad(); }
        public void changedUpdate(javax.swing.event.DocumentEvent e) { filterAndLoad(); }
    });

    searchWrap.add(searchLabel);
    searchWrap.add(searchField);

    toolbarWrap.add(buttonPanel, BorderLayout.WEST);
    toolbarWrap.add(searchWrap, BorderLayout.EAST);

    // Thêm toolbarWrap ngay dưới headerPanel, vào vị trí NORTH
    add(toolbarWrap, BorderLayout.NORTH);

    // === BẢNG HIỂN THỊ HÓA ĐƠN ===
    String[] columns = {"Mã HĐ", "Nhân viên", "Ngày lập", "Tổng tiền", "Trạng thái", "Chi tiết"};
    tableModel = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int col) {
            return col == 5; // Chỉ cột "Chi tiết" cho phép tương tác như nút
        }
    };

    invoiceTable = new JTable(tableModel) {
        @Override
        public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
            Component comp = super.prepareRenderer(renderer, row, col);
            if (col == 4) { // Cột trạng thái
                String status = (String) getValueAt(row, col);
                if ("Paid".equalsIgnoreCase(status)) {
                    comp.setForeground(SUCCESS_COLOR);
                } else {
                    comp.setForeground(WARNING_COLOR);
                }
            } else {
                comp.setForeground(TEXT_COLOR);
            }
            return comp;
        }
    };
    invoiceTable.setRowHeight(42);
    invoiceTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    invoiceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    invoiceTable.setShowGrid(false);
    invoiceTable.setIntercellSpacing(new Dimension(0, 0));
    invoiceTable.setAutoCreateRowSorter(true);

    styleModernTable(invoiceTable);

    // Cột "Chi tiết" hiển thị nút
    invoiceTable.getColumnModel().getColumn(5).setCellRenderer(new DetailButtonRenderer());
    invoiceTable.getColumnModel().getColumn(5).setCellEditor(new DetailButtonEditor(new JCheckBox()));

    JScrollPane scrollPane = new JScrollPane(invoiceTable);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    JPanel tablePanelArea = new JPanel(new BorderLayout());
    tablePanelArea.setBackground(CARD_COLOR);
    tablePanelArea.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(5, 5, 5, 5)
    ));
    tablePanelArea.add(scrollPane, BorderLayout.CENTER);

    // Thêm tablePanelArea vào vùng CENTER để nó hiển thị chính giữa
    add(tablePanelArea, BorderLayout.CENTER);

    // === PHẦN ĐIỀU KHIỂN PHÂN TRANG ===
    JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
    paginationPanel.setBackground(CARD_COLOR);
    paginationPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

    JButton prevBtn = createGradientButton("« Trước", INFO_COLOR);
    JButton nextBtn = createGradientButton("Sau »", INFO_COLOR);

    pageSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1, 1));
    JComponent editor = pageSpinner.getEditor();
    JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor) editor;
    spinnerEditor.getTextField().setHorizontalAlignment(JTextField.CENTER);
    spinnerEditor.getTextField().setFont(new Font("Segoe UI", Font.PLAIN, 14));
    pageSpinner.setPreferredSize(new Dimension(60, 30));

    totalPagesLabel = new JLabel("/ 1");
    totalPagesLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    totalPagesLabel.setForeground(TEXT_COLOR);

    prevBtn.addActionListener(e -> {
        if (currentPage > 1) {
            currentPage--;
            pageSpinner.setValue(currentPage);
            loadInvoices();
        }
    });
    nextBtn.addActionListener(e -> {
        if (currentPage < totalPages) {
            currentPage++;
            pageSpinner.setValue(currentPage);
            loadInvoices();
        }
    });
    pageSpinner.addChangeListener(e -> {
        currentPage = (int) pageSpinner.getValue();
        loadInvoices();
    });

    paginationPanel.add(prevBtn);
    paginationPanel.add(new JLabel("Trang:"));
    paginationPanel.add(pageSpinner);
    paginationPanel.add(totalPagesLabel);
    paginationPanel.add(nextBtn);

    // Đặt paginationPanel vào SOUTH
    add(paginationPanel, BorderLayout.SOUTH);

    // === ĐĂNG KÝ SỰ KIỆN CHO CÁC NÚT ===
        // === ĐĂNG KÝ SỰ KIỆN CHO CÁC NÚT ===
    // --- Nút "Sửa HĐ" ---
    editBtn.addActionListener(e -> {
        int sel = invoiceTable.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một hóa đơn để sửa.", "Chưa chọn HĐ",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = invoiceTable.convertRowIndexToModel(sel);
        int invoiceId = (int) tableModel.getValueAt(modelRow, 0);

        // Lấy thông tin hiện tại của Invoice
        Invoice inv = invoiceController.getInvoiceById(invoiceId);
        if (inv == null) {
            JOptionPane.showMessageDialog(this,
                    "Không tìm thấy hóa đơn #" + invoiceId, "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Lấy tên nhân viên hiện tại
        Employee oldEmp = employeeController.getEmployeeById(inv.getEmployeeId());
        String oldEmpName = (oldEmp != null) ? oldEmp.getFullName() : "";

        // Dialog sửa: Nhập Tên NV và Trạng thái
        JTextField txtEmpName = new JTextField(oldEmpName);
        String[] statuses = {"Paid", "Unpaid"};
        JComboBox<String> cmbStatus = new JComboBox<>(statuses);
        cmbStatus.setSelectedItem(inv.getPaymentStatus());

        JPanel editPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        editPanel.add(new JLabel("Tên nhân viên lập (FullName):"));
        editPanel.add(txtEmpName);
        editPanel.add(new JLabel("Trạng thái (PaymentStatus):"));
        editPanel.add(cmbStatus);

        int result = JOptionPane.showConfirmDialog(
                this, editPanel,
                "Sửa Hóa đơn #" + invoiceId,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
        if (result == JOptionPane.OK_OPTION) {
            String newEmpName = txtEmpName.getText().trim();
            String newStatus  = (String) cmbStatus.getSelectedItem();

            // Tìm Employee theo tên mới
            Employee newEmp = employeeController.getEmployeeByName(newEmpName);
            if (newEmp == null) {
                JOptionPane.showMessageDialog(this,
                        "Không tìm thấy nhân viên với tên: " + newEmpName,
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Cập nhật vào object Invoice và gọi controller
            inv.setEmployeeId(newEmp.getEmployeeID());
            inv.setPaymentStatus(newStatus);

            boolean ok = invoiceController.updateInvoice(inv);
            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "Cập nhật hóa đơn thành công.", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                loadInvoices();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Cập nhật thất bại. Vui lòng thử lại.", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    });

    // --- Nút "Xóa HĐ" ---
deleteBtn.addActionListener(e -> {
    int sel = invoiceTable.getSelectedRow();
    if (sel < 0) {
        JOptionPane.showMessageDialog(this,
                "Vui lòng chọn một hóa đơn để xóa.", "Chưa chọn HĐ",
                JOptionPane.WARNING_MESSAGE);
        return;
    }
    int modelRow = invoiceTable.convertRowIndexToModel(sel);
    int invoiceId = (int) tableModel.getValueAt(modelRow, 0);

    int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn xóa hóa đơn #" + invoiceId + "?\n" +
            "Tất cả chi tiết cũng sẽ bị xóa.",
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
    if (confirm == JOptionPane.YES_OPTION) {
        // 1) Xóa detail và hoá đơn cùng lúc thông qua InvoiceController.deleteInvoiceById
        boolean ok = invoiceController.deleteInvoiceById(invoiceId);
        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "Đã xóa hóa đơn #" + invoiceId + ".", "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
            loadInvoices();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Không thể xóa hóa đơn. Vui lòng thử lại.", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
});


    refreshBtn.addActionListener(e -> loadInvoices());
    exportBtn.addActionListener(e -> exportToExcel());

    // Cuối cùng, đổ dữ liệu lần đầu
    loadInvoices();
}


    /**
     * Đổ dữ liệu Invoice lên bảng, có hỗ trợ tìm kiếm (search) & phân trang (pagination).
     */
    private void loadInvoices() {
        tableModel.setRowCount(0);

        List<Invoice> all = invoiceController.getAllInvoices();
        // Lọc theo searchField (InvoiceID hoặc PaymentStatus)
        String text = currentSearch.toLowerCase().trim();
        List<Invoice> filtered = all.stream().filter(inv -> {
            boolean m1 = String.valueOf(inv.getInvoiceId()).contains(text)
                    || inv.getPaymentStatus().toLowerCase().contains(text);
            return m1;
        }).collect(Collectors.toList());

        int total = filtered.size();
        totalPages = Math.max((int) Math.ceil((double) total / pageSize), 1);
        currentPage = Math.min(Math.max(currentPage, 1), totalPages);
        pageSpinner.setModel(new SpinnerNumberModel(currentPage, 1, totalPages, 1));
        totalPagesLabel.setText("/ " + totalPages);

        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (Invoice inv : filtered.subList(start, end)) {
            Employee emp = employeeController.getEmployeeById(inv.getEmployeeId());
            String empName = (emp != null) ? emp.getFullName() : "—";

            tableModel.addRow(new Object[]{
                    inv.getInvoiceId(),
                    empName,
                    sdf.format(inv.getIssueDate()),
                    inv.getTotalAmount().setScale(0, BigDecimal.ROUND_HALF_UP).toString() + " VND",
                    inv.getPaymentStatus(),
                    "Chi tiết"
            });
        }

        // Sắp xếp lại theo InvoiceID tăng dần
        ((TableRowSorter<?>) invoiceTable.getRowSorter())
                .setSortKeys(java.util.List.of(new RowSorter.SortKey(0, SortOrder.ASCENDING)));
    }

    /**
     * Khi người dùng gõ vào ô tìm kiếm, gọi loadInvoices() lại.
     */
    private void filterAndLoad() {
        currentSearch = searchField.getText();
        currentPage = 1;
        loadInvoices();
    }

    /**
     * Hiển thị dialog chi tiết / chỉnh sửa của một hóa đơn (InvoiceID).
     * Cho phép sửa SL, xóa, thêm mới, và cập nhật tổng.
     */
    /**
 * Hiển thị dialog chi tiết của một hóa đơn (chỉ hiện sản phẩm, số lượng, đơn giá, thành tiền).
 */
private void showDetailDialog(int invoiceId) {
    Invoice inv = invoiceController.getInvoiceById(invoiceId);
    if (inv == null) {
        JOptionPane.showMessageDialog(this,
                "Không tìm thấy hóa đơn #" + invoiceId, "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Lấy tất cả Detail của invoice
    List<Detail> details = detailController.getDetailsByInvoiceId(invoiceId);

    // Tạo dialog modal
    Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
    JDialog dialog = new JDialog(owner, "Chi tiết HĐ #" + invoiceId, true);
    dialog.setLayout(new BorderLayout());
    dialog.getContentPane().setBackground(LIGHT_COLOR);

    // Header gradient đơn giản
    JPanel hdr = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(
                    0, 0, PRIMARY_COLOR,
                    getWidth(), 0, PRIMARY_COLOR.darker()
            );
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.dispose();
        }
    };
    hdr.setLayout(new BorderLayout());
    hdr.setPreferredSize(new Dimension(0, 50));
    hdr.setBorder(new EmptyBorder(5, 20, 5, 20));
    JLabel lblTitle = new JLabel("Chi tiết Hóa đơn #" + invoiceId);
    lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
    lblTitle.setForeground(WHITE_COLOR);
    hdr.add(lblTitle, BorderLayout.WEST);
    dialog.add(hdr, BorderLayout.NORTH);

    // CHỈ HIỂN THỊ BẢNG SẢN PHẨM:
    String[] cols = {"Tên món", "Số lượng", "Đơn giá (VND)", "Thành tiền (VND)"};
    DefaultTableModel detailModel = new DefaultTableModel(cols, 0) {
        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };
    JTable detailTbl = new JTable(detailModel);
    detailTbl.setRowHeight(30);
    detailTbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    styleModernTable(detailTbl);

    // Đổ dữ liệu vào table: lấy tên món từ MenuItemController
    for (Detail d : details) {
        MenuItem mi = menuItemController.getMenuItemById(d.getMenuItemId());
        String itemName = (mi != null) ? mi.getItemName() : "Không tìm thấy món";
        int qty = d.getQuantity();
        // Đơn giá lưu trong Detail là BigDecimal (hoặc float), hiển thị bỏ phần thập phân
        BigDecimal unitPrice = d.getUnitPrice().setScale(0, BigDecimal.ROUND_HALF_UP);
        BigDecimal lineTotal = d.getUnitPrice()
                               .multiply(new BigDecimal(qty))
                               .setScale(0, BigDecimal.ROUND_HALF_UP);

        detailModel.addRow(new Object[]{
                itemName,
                qty,
                unitPrice.toString(),
                lineTotal.toString()
        });
    }

    JScrollPane scrollPane = new JScrollPane(detailTbl);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    JPanel centerPanel = new JPanel(new BorderLayout());
    centerPanel.setBackground(CARD_COLOR);
    centerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
    centerPanel.add(scrollPane, BorderLayout.CENTER);

    dialog.add(centerPanel, BorderLayout.CENTER);

    // Ở dưới chỉ có nút Đóng
    JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.CENTER));
    btnWrap.setBackground(LIGHT_COLOR);
    btnWrap.setBorder(new EmptyBorder(10, 10, 15, 10));
    JButton btnClose = createGradientButton("Đóng", SECONDARY_COLOR);
    btnClose.setPreferredSize(new Dimension(100, 30));
    btnClose.addActionListener(e -> dialog.dispose());
    btnWrap.add(btnClose);

    dialog.add(btnWrap, BorderLayout.SOUTH);

    dialog.setSize(600, 400);
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
}
 
    private void exportToExcel() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Không có dữ liệu để xuất.", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file Excel");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel (*.xlsx)", "xlsx"));
        int userChoice = fileChooser.showSaveDialog(this);
        if (userChoice != JFileChooser.APPROVE_OPTION) return;

        File file = fileChooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".xlsx")) {
            file = new File(file.getAbsolutePath() + ".xlsx");
        }

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Danh sách Hóa đơn");

            // Tạo header
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < tableModel.getColumnCount(); col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(tableModel.getColumnName(col));
            }

            // Điền dữ liệu (bao gồm cột "Chi tiết")
            for (int r = 0; r < tableModel.getRowCount(); r++) {
                Row row = sheet.createRow(r + 1);
                for (int c = 0; c < tableModel.getColumnCount(); c++) {
                    Object val = tableModel.getValueAt(r, c);
                    if (val != null) row.createCell(c).setCellValue(val.toString());
                }
            }

            // Auto-size cột
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }

            JOptionPane.showMessageDialog(this,
                    "Đã lưu Excel tại:\n" + file.getAbsolutePath(),
                    "Xuất Excel thành công",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi xuất Excel:\n" + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Style lại JTable theo phong cách hiện đại (Flat / Bootstrap-like).
     */
    private void styleModernTable(JTable table) {
        // Header
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        tbl, value, isSelected, hasFocus, row, column);
                lbl.setBackground(PRIMARY_COLOR);
                lbl.setForeground(WHITE_COLOR);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
                lbl.setHorizontalAlignment(JLabel.CENTER);
                lbl.setBorder(new CompoundBorder(
                        new LineBorder(PRIMARY_COLOR.darker(), 2),
                        new EmptyBorder(0, 5, 0, 5)
                ));
                return lbl;
            }
        });

        // Rows
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    lbl.setBackground(new Color(232, 240, 254));
                    lbl.setForeground(PRIMARY_COLOR);
                } else {
                    lbl.setBackground((row % 2 == 0) ? CARD_COLOR : LIGHT_COLOR);
                    lbl.setForeground(TEXT_COLOR);
                }
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                lbl.setBorder(new EmptyBorder(5, 10, 5, 10));
                if (column == 0 || column == 2) {
                    lbl.setHorizontalAlignment(JLabel.CENTER);
                } else if (column == 3) {
                    lbl.setHorizontalAlignment(JLabel.RIGHT);
                } else {
                    lbl.setHorizontalAlignment(JLabel.LEFT);
                }
                return lbl;
            }
        });
    }

    /**
     * Tạo một JButton gradient với nền baseColor, text trắng, hover effect.
     */
    private JButton createGradientButton(String text, Color baseColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, baseColor,
                        0, getHeight(), baseColor.darker()
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(WHITE_COLOR);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
        btn.setBorder(new EmptyBorder(6, 12, 6, 12));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(baseColor.darker());
                btn.setBorder(new EmptyBorder(4, 10, 4, 10));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(baseColor);
                btn.setBorder(new EmptyBorder(6, 12, 6, 12));
            }
        });
        return btn;
    }

    /**
     * Tạo một JButton có icon (nếu tồn tại), text trắng, background gradient baseColor.
     */
    private JButton createIconButton(String text, Color baseColor, String iconPath) {
        JButton btn = createGradientButton(text, baseColor);
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                btn.setIcon(icon);
                btn.setIconTextGap(6);
            }
        } catch (Exception ignored) { }
        return btn;
    }

    /**
     * Renderer cho cột "Chi tiết" (hiển thị như một nút “Xem”).
     */
    private class DetailButtonRenderer extends JButton implements TableCellRenderer {
        public DetailButtonRenderer() {
            setOpaque(true);
            setText("Xem");
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(WHITE_COLOR);
            setBackground(SECONDARY_COLOR);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object val,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            if (isSelected) {
                setBackground(SECONDARY_COLOR.darker());
            } else {
                setBackground(SECONDARY_COLOR);
            }
            return this;
        }
    }

    /**
     * Editor cho cột "Chi tiết": khi người dùng nhấn nút, sẽ hiển thị dialog chi tiết cho HĐ đó.
     */
    private class DetailButtonEditor extends DefaultCellEditor {
        private final JButton btn;
        private int currentRow;

        public DetailButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            btn = new JButton("Xem");
            btn.setOpaque(true);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btn.setForeground(WHITE_COLOR);
            btn.setBackground(SECONDARY_COLOR);
            btn.setBorderPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btn.addActionListener(e -> fireEditingStopped());
            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    btn.setBackground(SECONDARY_COLOR.darker());
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    btn.setBackground(SECONDARY_COLOR);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            currentRow = row;
            return btn;
        }

        @Override
        public Object getCellEditorValue() {
            int modelRow = invoiceTable.convertRowIndexToModel(currentRow);
            int invoiceId = (int) tableModel.getValueAt(modelRow, 0);
            SwingUtilities.invokeLater(() -> showDetailDialog(invoiceId));
            return "Xem";
        }
    }

    /**
     * Renderer cho cột "Xóa" trong detail table.
     */
    private class DeleteButtonRenderer extends JButton implements TableCellRenderer {
        public DeleteButtonRenderer() {
            setOpaque(true);
            setText("Xóa");
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(WHITE_COLOR);
            setBackground(DANGER_COLOR);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object val,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            if (isSelected) {
                setBackground(DANGER_COLOR.darker());
            } else {
                setBackground(DANGER_COLOR);
            }
            return this;
        }
    }

    /**
     * Editor cho cột "Xóa" trong detail table.
     */
    private class DeleteButtonEditor extends DefaultCellEditor {
        private final JButton btn;
        private final JTable detailTblRef;
        private final DefaultTableModel detailModelRef;
        private final List<Integer> toDeleteRef;
        private int currentRow;

        public DeleteButtonEditor(JCheckBox checkBox, JTable tbl, DefaultTableModel mdl, List<Integer> toDel) {
            super(checkBox);
            detailTblRef     = tbl;
            detailModelRef   = mdl;
            toDeleteRef      = toDel;

            btn = new JButton("Xóa");
            btn.setOpaque(true);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btn.setForeground(WHITE_COLOR);
            btn.setBackground(DANGER_COLOR);
            btn.setBorderPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btn.addActionListener(e -> fireEditingStopped());
            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    btn.setBackground(DANGER_COLOR.darker());
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    btn.setBackground(DANGER_COLOR);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            currentRow = row;
            return btn;
        }

        @Override
        public Object getCellEditorValue() {
            int modelRow = detailTblRef.convertRowIndexToModel(currentRow);
            int detailId = (int) detailModelRef.getValueAt(modelRow, 0);

            if (detailId > 0) {
                toDeleteRef.add(detailId);
            }
            detailModelRef.removeRow(modelRow);
            return "Xóa";
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("CoffeeMate - Quản lý hóa đơn");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 600);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new InvoicePanel());
            frame.setVisible(true);
        });
    }
}
