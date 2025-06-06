package com.coffeemate.component;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartTheme;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import com.coffeemate.controller.DetailController;
import com.coffeemate.controller.EmployeeController;
import com.coffeemate.controller.InvoiceController;
import com.coffeemate.controller.MenuItemController;
import com.coffeemate.model.ChartDataModel;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.PiePlot;

/**
 * Admin dashboard panel for CoffeeMate application.
 * Displays statistics and charts for Employees, Menu Items, Invoices, and Revenue.
 */
public class AdDashBoard extends javax.swing.JPanel {
    private InvoiceController invoiceController = new InvoiceController();
    private EmployeeController employeeController = new EmployeeController();
    private MenuItemController menuItemController = new MenuItemController();
    private DetailController detailController = new DetailController();

    // Containers
    private javax.swing.JScrollPane spAdmin;
    private javax.swing.JPanel panelAdmin;
    private javax.swing.JPanel panelStatistic;

    // Statistic panels
    private JPanel employeePanel;
    private JPanel menuItemPanel;
    private JPanel invoicePanel;
    private JPanel revenuePanel;
    
    // Chart panels
    private JPanel revenueChartPanel;
    private JPanel topMenuItemsPanel;
    private JPanel topEmployeesPanel;
    private JPanel employeeRolePanel;
    
    // Filter components
    private JComboBox<String> timeRangeCombo;
    private JComboBox<Integer> yearCombo;
    private JPanel filterPanel;
    
    // Color scheme
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private final Color ACCENT_COLOR = new Color(231, 76, 60);
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    private final Color CARD_COLOR = new Color(255, 255, 255);
    private final Color BORDER_COLOR = new Color(189, 195, 199);

    public AdDashBoard() {
        initComponents();
        setupDashboard();
    }

    private void initComponents() {
        // Instantiate top‐level containers
        spAdmin = new JScrollPane();
        panelAdmin = new JPanel();
        panelStatistic = new JPanel();

        // The AdDashBoard itself uses a BorderLayout to hold the scroll pane
        setLayout(new BorderLayout());
        
        // panelAdmin will hold panelStatistic vertically
        panelAdmin.setLayout(new BoxLayout(panelAdmin, BoxLayout.Y_AXIS));
        spAdmin.setViewportView(panelAdmin);

        // panelStatistic will be populated in setupDashboard()
        panelStatistic.setLayout(new BoxLayout(panelStatistic, BoxLayout.Y_AXIS));
        panelAdmin.add(panelStatistic);

        // Add scroll pane to this panel
        add(spAdmin, BorderLayout.CENTER);
    }

    private void setupDashboard() {
        // Set background colors
        setBackground(BACKGROUND_COLOR);
        panelAdmin.setBackground(BACKGROUND_COLOR);
        panelStatistic.setBackground(BACKGROUND_COLOR);
        
        // Apply modern chart theme
        ChartTheme theme = StandardChartTheme.createJFreeTheme();
        ChartFactory.setChartTheme(theme);
        
        // Build sections
        createStatisticsPanels();
        createFilterPanel();
        createRevenueWidgets();
        createCharts();
        
        // Layout: panelStatistic already has BoxLayout (Y_AXIS)
        panelStatistic.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 1. Title
        JLabel dashboardTitle = new JLabel("Bảng điều khiển");
        dashboardTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        dashboardTitle.setForeground(PRIMARY_COLOR);
        dashboardTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        dashboardTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panelStatistic.add(dashboardTitle);
        
        // 2. Statistic panels row
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 15, 0));
        statsRow.setBackground(BACKGROUND_COLOR);
        statsRow.add(employeePanel);
        statsRow.add(menuItemPanel);
        statsRow.add(invoicePanel);
        statsRow.add(revenuePanel);
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        panelStatistic.add(statsRow);
        panelStatistic.add(Box.createVerticalStrut(20));
        
        // 3. Revenue widgets row
        JPanel revenueWidgetsRow = new JPanel(new GridLayout(1, 2, 15, 0));
        revenueWidgetsRow.setBackground(BACKGROUND_COLOR);
        revenueWidgetsRow.add(createDailyRevenueWidget());
        revenueWidgetsRow.add(createMonthlyRevenueWidget());
        revenueWidgetsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        revenueWidgetsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        panelStatistic.add(revenueWidgetsRow);
        panelStatistic.add(Box.createVerticalStrut(20));
        
        // 4. Charts title
        JLabel chartsTitle = new JLabel("Phân tích & Thống kê");
        chartsTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        chartsTitle.setForeground(PRIMARY_COLOR);
        chartsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        chartsTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panelStatistic.add(chartsTitle);
        
        // 5. Revenue chart with filter
        JPanel revenueChartContainer = new JPanel(new BorderLayout(0, 10));
        revenueChartContainer.setBackground(BACKGROUND_COLOR);
        revenueChartContainer.add(filterPanel, BorderLayout.NORTH);
        revenueChartContainer.add(createPanelWithBorder(revenueChartPanel, "Biểu đồ doanh thu"), BorderLayout.CENTER);
        revenueChartContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelStatistic.add(revenueChartContainer);
        panelStatistic.add(Box.createVerticalStrut(15));
        
        // 6. Top Menu Items chart
        JPanel topMenuItemsContainer = new JPanel(new BorderLayout());
        topMenuItemsContainer.setBackground(BACKGROUND_COLOR);
        topMenuItemsContainer.add(createPanelWithBorder(topMenuItemsPanel, "Món bán chạy"), BorderLayout.CENTER);
        topMenuItemsContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelStatistic.add(topMenuItemsContainer);
        panelStatistic.add(Box.createVerticalStrut(15));
        
        // 7. Top Employees chart
        JPanel topEmployeesContainer = new JPanel(new BorderLayout());
        topEmployeesContainer.setBackground(BACKGROUND_COLOR);
        topEmployeesContainer.add(createPanelWithBorder(topEmployeesPanel, "Top nhân viên"), BorderLayout.CENTER);
        topEmployeesContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelStatistic.add(topEmployeesContainer);
        panelStatistic.add(Box.createVerticalStrut(15));
        
        // 8. Employee Role chart
        JPanel roleContainer = new JPanel(new BorderLayout());
        roleContainer.setBackground(BACKGROUND_COLOR);
        roleContainer.add(createPanelWithBorder(employeeRolePanel, "Vai trò nhân viên"), BorderLayout.CENTER);
        roleContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelStatistic.add(roleContainer);
    }

    private JPanel createPanelWithBorder(JPanel contentPanel, String title) {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(CARD_COLOR);
        container.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        container.add(titleLabel, BorderLayout.NORTH);
        container.add(contentPanel, BorderLayout.CENTER);
        return container;
    }

    private void createStatisticsPanels() {
        employeePanel = createStatPanel(
            "Nhân viên",
            String.valueOf(employeeController.countEmployee()),
            "Tổng số nhân viên",
            PRIMARY_COLOR
        );

        menuItemPanel = createStatPanel(
            "Món",
            String.valueOf(menuItemController.countMenuItem()),
            "Tổng số món",
            SECONDARY_COLOR
        );

        invoicePanel = createStatPanel(
            "Hóa đơn",
            String.valueOf(invoiceController.countInvoices()),
            "Tổng số hóa đơn",
            ACCENT_COLOR
        );

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String totalRevenue = formatter.format(invoiceController.getTotalRevenue());
        revenuePanel = createStatPanel(
            "Doanh thu",
            totalRevenue,
            "Tổng doanh thu",
            new Color(46, 204, 113)
        );
    }

    private JPanel createStatPanel(String title, String value, String description, Color accentColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(15, 15, 15, 15)
        ));
        // Color strip on left
        JPanel colorStrip = new JPanel();
        colorStrip.setBackground(accentColor);
        colorStrip.setPreferredSize(new Dimension(5, 0));
        panel.add(colorStrip, BorderLayout.WEST);

        // Content (title, value, description)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CARD_COLOR);
        contentPanel.setBorder(new EmptyBorder(0, 10, 0, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(accentColor);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(TEXT_COLOR);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(TEXT_COLOR);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(valueLabel);
        contentPanel.add(Box.createVerticalStrut(3));
        contentPanel.add(descLabel);

        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    private void createFilterPanel() {
        filterPanel = new JPanel();
        filterPanel.setBackground(CARD_COLOR);
        filterPanel.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 0));

        String[] timeRanges = {"Theo tháng", "Theo năm", "Theo tuần"};
        timeRangeCombo = new JComboBox<>(timeRanges);
        timeRangeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        timeRangeCombo.setPreferredSize(new Dimension(120, 30));
        timeRangeCombo.setBackground(Color.WHITE);
        timeRangeCombo.setForeground(TEXT_COLOR);
        timeRangeCombo.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(PRIMARY_COLOR);
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(TEXT_COLOR);
                }
                return c;
            }
        });
        timeRangeCombo.addActionListener(e -> updateCharts());

        yearCombo = new JComboBox<>();
        yearCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        yearCombo.setPreferredSize(new Dimension(100, 30));
        yearCombo.setBackground(Color.WHITE);
        yearCombo.setForeground(TEXT_COLOR);
        yearCombo.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(PRIMARY_COLOR);
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(TEXT_COLOR);
                }
                return c;
            }
        });
        updateYearCombo();
        yearCombo.addActionListener(e -> updateCharts());

        JLabel filterTitle = new JLabel("Lọc dữ liệu:");
        filterTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        filterTitle.setForeground(PRIMARY_COLOR);

        JLabel timeLabel = new JLabel("Xem theo:");
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel yearLabel = new JLabel("Năm:");
        yearLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        filterPanel.add(filterTitle);
        filterPanel.add(timeLabel);
        filterPanel.add(timeRangeCombo);
        filterPanel.add(yearLabel);
        filterPanel.add(yearCombo);

        JButton applyButton = createStyledButton("Áp dụng", PRIMARY_COLOR, Color.WHITE);
        applyButton.addActionListener(e -> updateCharts());
        filterPanel.add(applyButton);
    }

    private void updateYearCombo() {
        yearCombo.removeAllItems();
        List<Integer> years = invoiceController.getAvailableYears();
        for (Integer year : years) {
            yearCombo.addItem(year);
        }
    }

    private void createRevenueWidgets() {
        // No‐op here; widgets are created on demand in the layout
    }

    private JPanel createDailyRevenueWidget() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(15, 15, 15, 15)
        ));
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String dailyRevenue = formatter.format(invoiceController.getTodayRevenue());
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CARD_COLOR);
        JPanel colorStrip = new JPanel();
        colorStrip.setBackground(SECONDARY_COLOR);
        colorStrip.setPreferredSize(new Dimension(5, 0));
        panel.add(colorStrip, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("Doanh thu hôm nay");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(SECONDARY_COLOR);
        JLabel valueLabel = new JLabel(dailyRevenue);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(TEXT_COLOR);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(valueLabel);
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMonthlyRevenueWidget() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(15, 15, 15, 15)
        ));
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String monthlyRevenue = formatter.format(invoiceController.getRevenueForCurrentMonth());
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CARD_COLOR);
        JPanel colorStrip = new JPanel();
        colorStrip.setBackground(PRIMARY_COLOR);
        colorStrip.setPreferredSize(new Dimension(5, 0));
        panel.add(colorStrip, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("Doanh thu tháng này");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(PRIMARY_COLOR);
        JLabel valueLabel = new JLabel(monthlyRevenue);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(TEXT_COLOR);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(valueLabel);
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    private void updateCharts() {
        String selectedRange = (String) timeRangeCombo.getSelectedItem();
        Integer selectedYear = (Integer) yearCombo.getSelectedItem();
        if (selectedYear == null) return;

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String title = "";

        switch (selectedRange) {
            case "Theo năm":
                List<ChartDataModel> yearlyData = invoiceController.getYearlyRevenue();
                for (ChartDataModel data : yearlyData) {
                    dataset.addValue(data.getValue().doubleValue(), "Doanh thu", data.getLabel());
                }
                title = "Doanh thu theo năm";
                break;
            case "Theo tháng":
                List<ChartDataModel> monthlyData = invoiceController.getMonthlyRevenue(selectedYear);
                for (ChartDataModel data : monthlyData) {
                    dataset.addValue(data.getValue().doubleValue(), "Doanh thu", data.getLabel());
                }
                title = "Doanh thu theo tháng - " + selectedYear;
                break;
            case "Theo tuần":
                List<ChartDataModel> weeklyData = invoiceController.getWeeklyRevenueByYear(selectedYear);
                for (ChartDataModel data : weeklyData) {
                    dataset.addValue(data.getValue().doubleValue(), "Doanh thu", data.getLabel());
                }
                title = "Doanh thu theo tuần - " + selectedYear;
                break;
        }

        JFreeChart chart = ChartFactory.createLineChart(
            title,
            "Thời gian",
            "Doanh thu (Triệu VNĐ)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        chart.setBackgroundPaint(Color.WHITE);
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 14));
        chart.getTitle().setPaint(PRIMARY_COLOR);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(new Color(220, 220, 220));
        plot.setRangeGridlinePaint(new Color(220, 220, 220));

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setNumberFormatOverride(new NumberFormat() {
            @Override
            public StringBuffer format(double number, StringBuffer toAppendTo, java.text.FieldPosition pos) {
                return new StringBuffer(Math.round(number / 1000000) + "");
            }
            @Override
            public StringBuffer format(long number, StringBuffer toAppendTo, java.text.FieldPosition pos) {
                return format((double) number, toAppendTo, pos);
            }
            @Override
            public Number parse(String source, java.text.ParsePosition parsePosition) {
                return null;
            }
        });

        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesPaint(0, PRIMARY_COLOR);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShapesFilled(0, true);
        plot.setRenderer(renderer);

        revenueChartPanel.removeAll();
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 300));
        revenueChartPanel.add(chartPanel);
        revenueChartPanel.revalidate();
        revenueChartPanel.repaint();
    }

    private void createCharts() {
        // Revenue chart
        revenueChartPanel = new JPanel(new BorderLayout());
        revenueChartPanel.setBackground(Color.WHITE);

        DefaultCategoryDataset revenueDataset = new DefaultCategoryDataset();
        List<ChartDataModel> yearlyData = invoiceController.getYearlyRevenue();
        for (ChartDataModel data : yearlyData) {
            revenueDataset.addValue(data.getValue().doubleValue(), "Doanh thu", data.getLabel());
        }

        JFreeChart revenueChart = ChartFactory.createLineChart(
            "Doanh thu theo năm", "Năm", "Doanh thu (Triệu VNĐ)", revenueDataset,
            PlotOrientation.VERTICAL, true, true, false
        );
        revenueChart.setBackgroundPaint(Color.WHITE);
        revenueChart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 14));
        revenueChart.getTitle().setPaint(PRIMARY_COLOR);

        CategoryPlot revenuePlot = revenueChart.getCategoryPlot();
        revenuePlot.setBackgroundPaint(Color.WHITE);
        revenuePlot.setDomainGridlinePaint(new Color(220, 220, 220));
        revenuePlot.setRangeGridlinePaint(new Color(220, 220, 220));

        NumberAxis rangeAxis = (NumberAxis) revenuePlot.getRangeAxis();
        rangeAxis.setNumberFormatOverride(new NumberFormat() {
            @Override
            public StringBuffer format(double number, StringBuffer toAppendTo, java.text.FieldPosition pos) {
                return new StringBuffer(Math.round(number / 1000000) + "");
            }
            @Override
            public StringBuffer format(long number, StringBuffer toAppendTo, java.text.FieldPosition pos) {
                return format((double) number, toAppendTo, pos);
            }
            @Override
            public Number parse(String source, java.text.ParsePosition parsePosition) { return null; }
        });

        LineAndShapeRenderer revenueRenderer = new LineAndShapeRenderer();
        revenueRenderer.setSeriesPaint(0, PRIMARY_COLOR);
        revenueRenderer.setSeriesStroke(0, new BasicStroke(2.0f));
        revenueRenderer.setSeriesShapesVisible(0, true);
        revenueRenderer.setSeriesShapesFilled(0, true);
        revenuePlot.setRenderer(revenueRenderer);

        ChartPanel revenueCP = new ChartPanel(revenueChart);
        revenueCP.setPreferredSize(new Dimension(400, 300));
        revenueChartPanel.add(revenueCP);

        // Top MenuItems chart
        topMenuItemsPanel = new JPanel(new BorderLayout());
        topMenuItemsPanel.setBackground(Color.WHITE);

        DefaultCategoryDataset menuItemsDataset = new DefaultCategoryDataset();
        List<Map<String, Object>> topMenuItems = menuItemController.getTopSellingMenuItems(5);
        for (Map<String, Object> item : topMenuItems) {
            menuItemsDataset.addValue((Integer) item.get("quantity"), "Số lượng", item.get("menu_name").toString());
        }

        JFreeChart menuItemsChart = ChartFactory.createBarChart(
            "Món bán chạy", "Món", "Số lượng", menuItemsDataset,
            PlotOrientation.VERTICAL, false, true, false
        );
        menuItemsChart.setBackgroundPaint(Color.WHITE);
        menuItemsChart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 14));
        menuItemsChart.getTitle().setPaint(PRIMARY_COLOR);

        CategoryPlot menuItemsPlot = menuItemsChart.getCategoryPlot();
        menuItemsPlot.setBackgroundPaint(Color.WHITE);
        menuItemsPlot.setDomainGridlinePaint(new Color(220, 220, 220));
        menuItemsPlot.setRangeGridlinePaint(new Color(220, 220, 220));
        BarRenderer menuRenderer = (BarRenderer) menuItemsPlot.getRenderer();
        menuRenderer.setSeriesPaint(0, SECONDARY_COLOR);
        menuRenderer.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter());
        menuRenderer.setShadowVisible(false);

        ChartPanel menuCP = new ChartPanel(menuItemsChart);
        menuCP.setPreferredSize(new Dimension(400, 300));
        topMenuItemsPanel.add(menuCP);

        // Top Employees chart
       topEmployeesPanel = new JPanel(new BorderLayout());
        topEmployeesPanel.setBackground(Color.WHITE);

        DefaultCategoryDataset employeesDataset = new DefaultCategoryDataset();
        // Lấy List<Map<String,Object>> từ DAO qua Controller
        List<Map<String, Object>> topEmployees = invoiceController.getTopEmployeesByRevenue(5);
        for (Map<String, Object> emp : topEmployees) {
            // Trong DAO chúng ta đã map keys: "EmployeeID", "employee_name", "total_revenue"
            int employeeId           = (Integer)    emp.get("EmployeeID");
            String fullName          = (String)     emp.get("employee_name");
            BigDecimal totalRevenueBD= (BigDecimal) emp.get("total_revenue");
            double revenueValue      = totalRevenueBD.doubleValue();

            String label = fullName + " (" + employeeId + ")";
            employeesDataset.addValue(revenueValue, "Doanh thu", label);
        }

        JFreeChart employeesChart = ChartFactory.createBarChart(
            "Top nhân viên theo doanh thu",
            "Nhân viên",
            "Doanh thu (Triệu VNĐ)",
            employeesDataset,
            PlotOrientation.VERTICAL,
            false,
            true,
            false
        );
        employeesChart.setBackgroundPaint(Color.WHITE);
        employeesChart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 14));
        employeesChart.getTitle().setPaint(PRIMARY_COLOR);

        CategoryPlot employeesPlot = employeesChart.getCategoryPlot();
        employeesPlot.setBackgroundPaint(Color.WHITE);
        employeesPlot.setDomainGridlinePaint(new Color(220, 220, 220));
        employeesPlot.setRangeGridlinePaint(new Color(220, 220, 220));

        NumberAxis empRangeAxis = (NumberAxis) employeesPlot.getRangeAxis();
        empRangeAxis.setNumberFormatOverride(new java.text.NumberFormat() {
            @Override
            public StringBuffer format(double number, StringBuffer toAppendTo, java.text.FieldPosition pos) {
                return new StringBuffer(Math.round(number / 1_000_000) + "");
            }
            @Override
            public StringBuffer format(long number, StringBuffer toAppendTo, java.text.FieldPosition pos) {
                return format((double) number, toAppendTo, pos);
            }
            @Override
            public Number parse(String source, java.text.ParsePosition parsePosition) {
                return null;
            }
        });

        BarRenderer empRenderer = (BarRenderer) employeesPlot.getRenderer();
        empRenderer.setSeriesPaint(0, new Color(41, 128, 185));
        empRenderer.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter());
        empRenderer.setShadowVisible(false);
        empRenderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        empRenderer.setItemLabelsVisible(true);
        empRenderer.setItemLabelFont(new Font("Segoe UI", Font.BOLD, 12));
        empRenderer.setItemLabelPaint(Color.WHITE);
        empRenderer.setPositiveItemLabelPosition(new ItemLabelPosition(
            ItemLabelAnchor.CENTER, org.jfree.ui.TextAnchor.CENTER
        ));

        ChartPanel employeesCP = new ChartPanel(employeesChart);
        employeesCP.setPreferredSize(new Dimension(400, 300));
        topEmployeesPanel.add(employeesCP);

        // Employee Role distribution
        employeeRolePanel = new JPanel(new BorderLayout());
        employeeRolePanel.setBackground(Color.WHITE);

        org.jfree.data.general.DefaultPieDataset roleDataset = new org.jfree.data.general.DefaultPieDataset();
        List<ChartDataModel> roleData = employeeController.getEmployeeCountByRole();
        for (ChartDataModel data : roleData) {
            roleDataset.setValue(data.getLabel(), data.getValue());
        }

        JFreeChart roleChart = ChartFactory.createPieChart(
            "Vai trò nhân viên", roleDataset, true, true, false
        );
        roleChart.setBackgroundPaint(Color.WHITE);
        roleChart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 14));
        roleChart.getTitle().setPaint(new Color(46, 204, 113));

        PiePlot rolePlot = (PiePlot) roleChart.getPlot();
        rolePlot.setBackgroundPaint(Color.WHITE);
        rolePlot.setOutlineVisible(false);
        rolePlot.setLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        rolePlot.setLabelBackgroundPaint(Color.WHITE);

        ChartPanel roleCP = new ChartPanel(roleChart);
        roleCP.setPreferredSize(new Dimension(400, 300));
        employeeRolePanel.add(roleCP);
    }

    private void styleTable(JTable table) {
        JTableHeader header = table.getTableHeader();
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR));
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                label.setBackground(PRIMARY_COLOR);
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 1, BORDER_COLOR),
                    BorderFactory.createEmptyBorder(0, 5, 0, 5)
                ));
                label.setHorizontalAlignment(JLabel.CENTER);
                return label;
            }
        });

        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setGridColor(new Color(220, 220, 220));
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(5, 0));
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(new Color(232, 234, 246));
        table.setSelectionForeground(TEXT_COLOR);
        table.setFocusable(false);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, false, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 247, 250));
                }
                int horizontalAlignment = SwingConstants.LEFT;
                if (column == 0 || column == 2 || column == 3) {
                    horizontalAlignment = SwingConstants.CENTER;
                }
                ((DefaultTableCellRenderer) c).setHorizontalAlignment(horizontalAlignment);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return c;
            }
        });
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                if (getModel().isPressed()) {
                    g.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g.setColor(bgColor.brighter());
                } else {
                    g.setColor(bgColor);
                }
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        button.setForeground(fgColor);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        return button;
    }
}
