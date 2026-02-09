package HETHONG;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import CHUNG.DBConnect;
import org.jfree.chart.*;
import org.jfree.data.general.DefaultPieDataset;

public class GUI_TrangChu extends JPanel {
    private DefaultTableModel model;
    private JPanel pnlCards, pnlChart, pnlTableArea;
    private JTable tblRecent;

    public GUI_TrangChu() {
        initComponents();
        // Nạp toàn bộ dữ liệu ngay khi khởi tạo
        refreshDashboard();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 248, 253));
        setBorder(new EmptyBorder(20, 25, 20, 25));

        // 1. Header Title
        JLabel lblTitle = new JLabel("Bảng Điều Khiển Tổng Quan");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        add(lblTitle, BorderLayout.NORTH);

        // 2. Container chính
        JPanel pnlCenter = new JPanel(new GridBagLayout());
        pnlCenter.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // --- KHU VỰC THẺ (KHÓA CHIỀU CAO ĐỂ KHÔNG BỊ MẤT) ---
        pnlCards = new JPanel(new GridLayout(1, 4, 20, 0));
        pnlCards.setOpaque(false);
        // Khóa chiều cao 140px - Đây là chỗ hay bị lỗi co lại nhất
        pnlCards.setPreferredSize(new Dimension(0, 140)); 
        pnlCards.setMinimumSize(new Dimension(0, 140));

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0; gbc.weighty = 0.0;
        gbc.insets = new Insets(0, 0, 25, 0);
        pnlCenter.add(pnlCards, gbc);

        // --- KHU VỰC DƯỚI (BIỂU ĐỒ & BẢNG) ---
        JPanel pnlBottom = new JPanel(new GridLayout(1, 2, 25, 0));
        pnlBottom.setOpaque(false);

        // Biểu đồ
        pnlChart = new JPanel(new BorderLayout());
        pnlChart.setBackground(Color.WHITE);
        pnlChart.setBorder(createTitleBorder("Thống kê Thể loại"));

        // Bảng mượn sách
        pnlTableArea = new JPanel(new BorderLayout());
        pnlTableArea.setBackground(Color.WHITE);
        pnlTableArea.setBorder(createTitleBorder("Hoạt động mượn sách gần đây"));

        model = new DefaultTableModel(new String[]{"Mã Phiếu", "Mã Độc Giả", "Ngày Mượn", "Trạng Thái"}, 0);
        tblRecent = new JTable(model);
        formatTable(tblRecent);
        pnlTableArea.add(new JScrollPane(tblRecent), BorderLayout.CENTER);

        pnlBottom.add(pnlChart);
        pnlBottom.add(pnlTableArea);

        gbc.gridy = 1; gbc.weighty = 1.0; // Cho phép khu vực này co giãn thoải mái
        gbc.insets = new Insets(0, 0, 0, 0);
        pnlCenter.add(pnlBottom, gbc);

        add(pnlCenter, BorderLayout.CENTER);
    }

    public void refreshDashboard() {
        // 1. Nạp Thẻ số liệu (Fix lỗi mất thẻ)
        pnlCards.removeAll();
        pnlCards.add(new MetricCard("TỔNG SÁCH", getCount("sach"), new Color(63, 81, 181), "📚"));
        pnlCards.add(new MetricCard("ĐỘC GIẢ", getCount("doc_gia"), new Color(0, 150, 136), "👥"));
        pnlCards.add(new MetricCard("ĐANG MƯỢN", getCount("phieu_muon WHERE TinhTrang = N'Đang mượn'"), new Color(255, 152, 0), "🔄"));
        pnlCards.add(new MetricCard("QUÁ HẠN", getCount("phieu_muon WHERE TinhTrang = N'Quá hạn'"), new Color(244, 67, 54), "⚠️"));

        // 2. Nạp Biểu đồ (Fix lỗi trắng biểu đồ)
        pnlChart.removeAll();
        pnlChart.add(new ChartPanel(createPieChart()), BorderLayout.CENTER);

        // 3. Nạp Bảng (Fix lỗi bảng trống)
        loadDataToTable();

        // Ép toàn bộ giao diện vẽ lại
        pnlCards.revalidate(); pnlCards.repaint();
        pnlChart.revalidate(); pnlChart.repaint();
        this.revalidate(); this.repaint();
    }

    private JFreeChart createPieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        String sql = "SELECT tl.TenTheLoai, COUNT(s.MaSach) FROM the_loai tl LEFT JOIN sach s ON tl.MaTheLoai = s.MaTheLoai GROUP BY tl.TenTheLoai";
        try (Connection conn = new DBConnect().getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                if (rs.getInt(2) > 0) dataset.setValue(rs.getString(1), rs.getInt(2));
            }
        } catch (Exception e) {}
        if (dataset.getItemCount() == 0) dataset.setValue("Chưa có sách", 1);
        return ChartFactory.createPieChart("", dataset, true, true, false);
    }

    private void loadDataToTable() {
        model.setRowCount(0);
        // Lưu ý: Đảm bảo tên bảng là 'phieu_muon' (viết thường) hoặc 'PHIEUMUON' theo đúng DB của bạn
        String sql = "SELECT TOP 10 MaPhieu, MaDG, NgayMuon, TinhTrang FROM phieu_muon ORDER BY NgayMuon DESC";
        try (Connection conn = new DBConnect().getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getDate(3), rs.getString(4)});
            }
        } catch (Exception e) { System.out.println("Lỗi nạp bảng: " + e.getMessage()); }
    }

    private int getCount(String target) {
        try (Connection conn = new DBConnect().getConnection(); Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + target)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {}
        return 0;
    }

    private void formatTable(JTable table) {
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
    }

    private Border createTitleBorder(String title) {
        return new TitledBorder(new LineBorder(new Color(230, 235, 240), 1), title, 0, 0, new Font("Segoe UI", Font.BOLD, 15));
    }

    // Class MetricCard (Giữ nguyên logic vẽ nhưng đảm bảo font hiển thị tốt)
    private class MetricCard extends JPanel {
        private String title, icon; private int value; private Color color;
        public MetricCard(String t, int v, Color c, String i) {
            this.title = t; this.value = v; this.color = c; this.icon = i;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
            g2.setColor(color);
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
            g2.drawString(icon, 25, 45);
            g2.setColor(Color.GRAY);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            g2.drawString(title, 25, 75);
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 32));
            g2.drawString(String.valueOf(value), 25, 115);
            g2.dispose();
        }
    }
}



//package HETHONG;
//
//import javax.swing.*;
//import javax.swing.border.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.sql.*;
//import CHUNG.DBConnect;
//
//import org.jfree.chart.ChartFactory;
//import org.jfree.chart.ChartPanel;
//import org.jfree.chart.JFreeChart;
//import org.jfree.data.general.DefaultPieDataset;
//import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
//import org.jfree.chart.plot.PiePlot;
//
//public class GUI_TrangChu extends JPanel {
//    private DefaultTableModel model;
//    private JPanel pnlCards;
//    private JPanel pnlChartContainer;
//    private Timer refreshTimer;
//
//    public GUI_TrangChu() {
//        initComponents();
//        
//        // Fix lỗi trắng trang: Ép hệ thống nạp dữ liệu sau khi Frame đã hiển thị 1 giây
//        new Timer(1000, e -> {
//            refreshData();
//            startAutoRefresh();
//            ((Timer)e.getSource()).stop();
//        }).start();
//    }
//
//    private void initComponents() {
//        setLayout(new BorderLayout(0, 0));
//        setBackground(new Color(245, 248, 253));
//        setBorder(new EmptyBorder(20, 25, 20, 25));
//
//        // 1. HEADER
//        JPanel pnlHeader = new JPanel(new BorderLayout());
//        pnlHeader.setOpaque(false);
//        pnlHeader.setBorder(new EmptyBorder(0, 0, 20, 0));
//        JLabel lblTitle = new JLabel("Bảng Điều Khiển Tổng Quan");
//        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
//        lblTitle.setForeground(new Color(40, 50, 70));
//        pnlHeader.add(lblTitle, BorderLayout.WEST);
//        add(pnlHeader, BorderLayout.NORTH);
//
//        // 2. CENTER BODY
//        JPanel pnlCenter = new JPanel(new GridBagLayout());
//        pnlCenter.setOpaque(false);
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.fill = GridBagConstraints.BOTH;
//
//        // 2.1. Metrics Cards - Ép chiều cao tối thiểu 150px
//        pnlCards = new JPanel(new GridLayout(1, 4, 20, 0));
//        pnlCards.setOpaque(false);
//        pnlCards.setMinimumSize(new Dimension(0, 150));
//        pnlCards.setPreferredSize(new Dimension(0, 150));
//
//        gbc.gridx = 0; gbc.gridy = 0;
//        gbc.weightx = 1.0; gbc.weighty = 0.0;
//        gbc.insets = new Insets(0, 0, 25, 0);
//        pnlCenter.add(pnlCards, gbc);
//
//        // 2.2. Biểu đồ & Bảng
//        JPanel pnlBottom = new JPanel(new GridBagLayout());
//        pnlBottom.setOpaque(false);
//        GridBagConstraints gbcBot = new GridBagConstraints();
//        gbcBot.fill = GridBagConstraints.BOTH;
//        gbcBot.weighty = 1.0;
//
//        pnlChartContainer = new JPanel(new BorderLayout());
//        pnlChartContainer.setBackground(Color.WHITE);
//        pnlChartContainer.setBorder(createStyledPanelBorder("Thống kê Thể loại"));
//        gbcBot.gridx = 0; gbcBot.weightx = 0.4;
//        gbcBot.insets = new Insets(0, 0, 0, 20);
//        pnlBottom.add(pnlChartContainer, gbcBot);
//
//        JPanel pnlActivity = new JPanel(new BorderLayout());
//        pnlActivity.setBackground(Color.WHITE);
//        pnlActivity.setBorder(createStyledPanelBorder("Hoạt động mượn sách gần đây"));
//        model = new DefaultTableModel(new String[]{"Mã Phiếu", "Mã Độc Giả", "Ngày Mượn", "Trạng Thái"}, 0);
//        JTable tblRecent = new JTable(model);
//        customizeTable(tblRecent);
//        pnlActivity.add(new JScrollPane(tblRecent), BorderLayout.CENTER);
//
//        gbcBot.gridx = 1; gbcBot.weightx = 0.6; gbcBot.insets = new Insets(0, 0, 0, 0);
//        pnlBottom.add(pnlActivity, gbcBot);
//
//        gbc.gridy = 1; gbc.weighty = 1.0; gbc.insets = new Insets(0, 0, 0, 0);
//        pnlCenter.add(pnlBottom, gbc);
//
//        add(pnlCenter, BorderLayout.CENTER);
//    }
//
//    private void refreshData() {
//        updateCards();
//        updateChart();
//        loadRecentActivity();
//    }
//
//    private void updateCards() {
//        pnlCards.removeAll();
//        pnlCards.add(new MetricCard("TỔNG SÁCH", getCount("sach"), new Color(63, 81, 181), "📚"));
//        pnlCards.add(new MetricCard("ĐỘC GIẢ", getCount("doc_gia"), new Color(0, 150, 136), "👥"));
//        pnlCards.add(new MetricCard("ĐANG MƯỢN", getCount("phieu_muon WHERE TinhTrang = N'Đang mượn'"), new Color(255, 152, 0), "🔄"));
//        pnlCards.add(new MetricCard("QUÁ HẠN", getCount("phieu_muon WHERE TinhTrang = N'Quá hạn'"), new Color(244, 67, 54), "⚠️"));
//        pnlCards.revalidate();
//        pnlCards.repaint();
//    }
//
//    private void updateChart() {
//        pnlChartContainer.removeAll();
//        pnlChartContainer.add(createPieChartPanel(), BorderLayout.CENTER);
//        pnlChartContainer.revalidate();
//        pnlChartContainer.repaint();
//    }
//
//    private void startAutoRefresh() {
//        if (refreshTimer != null) refreshTimer.stop();
//        refreshTimer = new Timer(30000, e -> refreshData());
//        refreshTimer.start();
//    }
//
//    private class MetricCard extends JPanel {
//        private Color mainColor;
//        private String title;
//        private int value;
//        private String iconText;
//
//        public MetricCard(String title, int value, Color mainColor, String iconText) {
//            this.title = title; this.value = value;
//            this.mainColor = mainColor; this.iconText = iconText;
//            setOpaque(false);
//        }
//
//        @Override
//        protected void paintComponent(Graphics g) {
//            Graphics2D g2 = (Graphics2D) g.create();
//            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//            g2.setColor(Color.WHITE);
//            g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 20, 20);
//            
//            g2.setColor(mainColor);
//            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
//            g2.drawString(iconText, 25, 45);
//
//            g2.setColor(new Color(120, 134, 154));
//            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
//            g2.drawString(title, 25, 75);
//
//            g2.setColor(new Color(40, 50, 70));
//            g2.setFont(new Font("Segoe UI", Font.BOLD, 34));
//            g2.drawString(String.valueOf(value), 25, 120);
//            g2.dispose();
//        }
//    }
//
//    private ChartPanel createPieChartPanel() {
//        DefaultPieDataset dataset = new DefaultPieDataset();
//        // Cần kiểm tra kỹ tên bảng THE_LOAI và cột MaTL/TenTL của bạn
//        String sql = "SELECT tl.TenTheLoai, COUNT(s.MaSach) FROM the_loai tl LEFT JOIN sach s ON tl.MaTheLoai = s.MaTheLoai GROUP BY tl.TenTheLoai";
//        try (Connection conn = new DBConnect().getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
//            while (rs.next()) { 
//                if(rs.getInt(2) > 0) dataset.setValue(rs.getString(1), rs.getInt(2)); 
//            }
//        } catch (Exception e) { System.err.println("SQL Biểu đồ lỗi: " + e.getMessage()); }
//
//        if (dataset.getItemCount() == 0) dataset.setValue("Trống", 1);
//
//        JFreeChart chart = ChartFactory.createPieChart("", dataset, true, true, false);
//        chart.setBackgroundPaint(Color.WHITE);
//        PiePlot plot = (PiePlot) chart.getPlot();
//        plot.setBackgroundPaint(Color.WHITE);
//        plot.setOutlineVisible(false);
//        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1}"));
//        return new ChartPanel(chart);
//    }
//
//    private void loadRecentActivity() {
//        if(model == null) return;
//        model.setRowCount(0);
//        // Chỉnh SQL theo hình image_68a57f.png của bạn
//        String sql = "SELECT TOP 10 MaPhieu, MaDG, NgayMuon, TinhTrang FROM phieu_muon ORDER BY NgayMuon DESC";
//        try (Connection conn = new DBConnect().getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
//            while (rs.next()) { 
//                model.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getDate(3), rs.getString(4)}); 
//            }
//        } catch (Exception e) { System.err.println("SQL Bảng hoạt động lỗi: " + e.getMessage()); }
//    }
//
//    private int getCount(String tableCondition) {
//        try (Connection conn = new DBConnect().getConnection(); Statement stmt = conn.createStatement(); 
//             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableCondition)) {
//            if (rs.next()) return rs.getInt(1);
//        } catch (Exception e) { System.err.println("SQL Count lỗi (" + tableCondition + "): " + e.getMessage()); }
//        return 0;
//    }
//
//    private Border createStyledPanelBorder(String title) {
//        TitledBorder tb = new TitledBorder(new LineBorder(new Color(230, 235, 240), 1, true), title);
//        tb.setTitleFont(new Font("Segoe UI", Font.BOLD, 16));
//        return new CompoundBorder(tb, new EmptyBorder(10, 10, 10, 10));
//    }
//
//    private void customizeTable(JTable table) {
//        table.setRowHeight(35);
//        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
//        table.setShowVerticalLines(false);
//    }
//}




//package HETHONG;
//
//import javax.swing.*;
//import javax.swing.border.EmptyBorder;
//import java.awt.*;
//import java.awt.geom.RoundRectangle2D;
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.Statement;
//import java.text.DecimalFormat;
//import java.util.ArrayList;
//import java.util.Collections;
//import CHUNG.DBConnect;
//
//public class GUI_TrangChu extends JPanel {
//
//    // Class lưu trữ dữ liệu cho từng cột
//    private class DataColumn {
//        String title;
//        int value;
//        Color color1; // Màu trên (nhạt)
//        Color color2; // Màu dưới (đậm)
//
//        public DataColumn(String title, int value, Color c1, Color c2) {
//            this.title = title;
//            this.value = value;
//            this.color1 = c1;
//            this.color2 = c2;
//        }
//    }
//
//    private ArrayList<DataColumn> listData = new ArrayList<>();
//    private int maxValue = 0; // Để tính tỷ lệ chiều cao
//
//    public GUI_TrangChu() {
//        setLayout(new BorderLayout());
//        setBackground(new Color(245, 248, 253));
//        setBorder(new EmptyBorder(30, 30, 30, 30));
//
//        // 1. Load dữ liệu từ CSDL
//        loadDataDB();
//
//        // 2. Panel Biểu đồ (Chiếm toàn bộ không gian)
//        JPanel pnlChart = new JPanel() {
//            @Override
//            protected void paintComponent(Graphics g) {
//                super.paintComponent(g);
//                drawChart((Graphics2D) g, getWidth(), getHeight());
//            }
//        };
//        pnlChart.setOpaque(false); // Để trong suốt thấy nền
//        add(pnlChart, BorderLayout.CENTER);
//
//        // 3. Footer (Chữ chào mừng nhỏ bên dưới)
//        JLabel lblFooter = new JLabel("HỆ THỐNG QUẢN LÝ THƯ VIỆN 2026", SwingConstants.CENTER);
//        lblFooter.setFont(new Font("Segoe UI", Font.BOLD, 14));
//        lblFooter.setForeground(new Color(150, 150, 150));
//        lblFooter.setBorder(new EmptyBorder(20, 0, 0, 0));
//        add(lblFooter, BorderLayout.SOUTH);
//    }
//
//    private void loadDataDB() {
//        listData.clear();
//        // Lấy số liệu thực tế
//        int sach = getCount("sach");
//        int docgia = getCount("doc_gia");
//        int dangmuon = getCount("phieu_muon WHERE TinhTrang = 'Đang mượn'"); // Hoặc like N'Đang mượn'
//        int quahan = getCount("phieu_muon WHERE TinhTrang = 'Quá hạn'");    // Hoặc like N'Quá hạn'
//        
//        // [QUAN TRỌNG] Nếu dữ liệu = 0 thì gán = 1 để cột vẫn hiện lên 1 chút cho đẹp, không bị mất hút
//        // Anh có thể bỏ logic này nếu muốn đúng tuyệt đối 100%
//        
//        // Thêm vào list (Title, Value, Color Start, Color End)
//        listData.add(new DataColumn("TỔNG SÁCH", sach, new Color(100, 181, 246), new Color(25, 118, 210))); // Xanh dương
//        listData.add(new DataColumn("ĐỘC GIẢ", docgia, new Color(129, 199, 132), new Color(56, 142, 60)));    // Xanh lá
//        listData.add(new DataColumn("ĐANG MƯỢN", dangmuon, new Color(255, 183, 77), new Color(245, 124, 0))); // Cam
//        listData.add(new DataColumn("QUÁ HẠN", quahan, new Color(229, 115, 115), new Color(211, 47, 47)));   // Đỏ
//
//        // Tìm giá trị lớn nhất để chia tỷ lệ chiều cao
//        maxValue = 0;
//        for (DataColumn d : listData) {
//            if (d.value > maxValue) maxValue = d.value;
//        }
//        if (maxValue == 0) maxValue = 1; // Tránh chia cho 0
//    }
//
//    private void drawChart(Graphics2D g2, int w, int h) {
//        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//
//        // Vẽ tiêu đề lớn
//        g2.setFont(new Font("Arial", Font.BOLD, 28));
//        g2.setColor(new Color(60, 60, 60));
//        String title = "THỐNG KÊ TỔNG QUAN";
//        FontMetrics fm = g2.getFontMetrics();
//        g2.drawString(title, (w - fm.stringWidth(title)) / 2, 50);
//
//        // Cấu hình vẽ cột
//        int numCols = listData.size();
//        int gap = 60; // Khoảng cách giữa các cột
//        int maxBarHeight = h - 200; // Chiều cao tối đa của cột (chừa lề trên dưới)
//        int barWidth = 160; // Độ rộng mỗi cột (Cố định cho đẹp)
//        
//        // Tính vị trí bắt đầu X để căn giữa toàn bộ biểu đồ
//        int totalChartWidth = (numCols * barWidth) + ((numCols - 1) * gap);
//        int startX = (w - totalChartWidth) / 2;
//        int bottomY = h - 80; // Đường chân của biểu đồ
//
//        // Vòng lặp vẽ từng cột
//        for (int i = 0; i < numCols; i++) {
//            DataColumn item = listData.get(i);
//            
//            // Tính chiều cao cột dựa trên tỷ lệ giá trị (Value / MaxValue)
//            // Math.max(item.value, maxValue/10) -> Mẹo nhỏ: Nếu giá trị nhỏ quá thì vẫn vẽ cao tầm 10% để hiển thị được số
//            double TyLe = (double) item.value / maxValue;
//            if (TyLe < 0.05) TyLe = 0.05; // Cột thấp nhất cũng phải cao 5% để đẹp
//            
//            int barHeight = (int) (TyLe * maxBarHeight);
//            
//            int x = startX + i * (barWidth + gap);
//            int y = bottomY - barHeight;
//
//            // 1. Vẽ bóng đổ (Shadow)
//            g2.setColor(new Color(0, 0, 0, 20));
//            g2.fillRoundRect(x + 10, y + 10, barWidth, barHeight, 20, 20);
//
//            // 2. Vẽ Cột (Gradient)
//            GradientPaint gp = new GradientPaint(x, y, item.color1, x, y + barHeight, item.color2);
//            g2.setPaint(gp);
//            g2.fillRoundRect(x, y, barWidth, barHeight, 20, 20);
//
//            // 3. Vẽ Số liệu (Nằm trong cột hoặc trên đỉnh cột)
//            g2.setColor(Color.WHITE);
//            g2.setFont(new Font("Arial", Font.BOLD, 36));
//            String valStr = String.valueOf(item.value);
//            fm = g2.getFontMetrics();
//            
//            // Nếu cột cao thì số nằm trong, thấp quá thì số nằm trên đầu
//            int textY = y + 50; 
//            if (barHeight < 60) { 
//                textY = y - 10; 
//                g2.setColor(item.color2); // Đổi màu chữ nếu nằm ngoài
//            }
//            g2.drawString(valStr, x + (barWidth - fm.stringWidth(valStr)) / 2, textY);
//
//            // 4. Vẽ Tên Cột (Dưới chân)
//            g2.setColor(new Color(80, 80, 80));
//            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
//            fm = g2.getFontMetrics();
//            g2.drawString(item.title, x + (barWidth - fm.stringWidth(item.title)) / 2, bottomY + 30);
//        }
//    }
//
//    private int getCount(String tableName) {
//        int count = 0;
//        try {
//            Connection conn = new DBConnect().getConnection();
//            Statement stmt = conn.createStatement();
//            String query = "SELECT COUNT(*) FROM " + tableName;
//            // Nếu có chữ WHERE thì không cần nối chuỗi
//            if(tableName.toUpperCase().contains("SELECT")) query = tableName; 
//            
//            ResultSet rs = stmt.executeQuery(query);
//            if (rs.next()) {
//                count = rs.getInt(1);
//            }
//            conn.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return count;
//    }
//}