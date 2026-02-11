package HETHONG;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import THONGKE.DAL_ThongKe;

public class GUI_TrangChu extends JPanel {

    private DTO_TaiKhoan tk;
    private DAL_ThongKe dalThongKe = new DAL_ThongKe();
    private Color bgColor = new Color(245, 248, 253);

    private JLabel lblTotalSach, lblTotalDocGia, lblTotalMuon, lblTotalQuaHan;
    private JTable tableMuonGanDay;
    private DefaultTableModel modelMuon;
    private Map<String, Integer> pieData;
    private JPanel pnlDrawingChart;
    
    // Biến giao diện cho Độc giả
    private JTextArea txtThongBao, txtSuggest, txtRules;

    public GUI_TrangChu(DTO_TaiKhoan tk) {
        this.tk = tk;
        initUI();
        loadDataDB();
    }

    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setBackground(bgColor);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // 1. HEADER - Cập nhật cho cả Admin và Staff
        String headerText = (tk.getPhanQuyen() == 1 || tk.getPhanQuyen() == 2) ? "TỔNG QUAN HỆ THỐNG" : "TỔNG QUAN TÀI KHOẢN";
        JLabel lblHeader = new JLabel(headerText);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblHeader.setForeground(new Color(45, 52, 54));
        add(lblHeader, BorderLayout.NORTH);

        // 2. CENTER CONTAINER
        JPanel pnlCenter = new JPanel(new BorderLayout(0, 25));
        pnlCenter.setOpaque(false);

        // --- A. THẺ THỐNG KÊ ---
        JPanel pnlStats = new JPanel(new GridLayout(1, 4, 20, 0));
        pnlStats.setOpaque(false);

        lblTotalSach = new JLabel("0");
        lblTotalDocGia = new JLabel("0");
        lblTotalMuon = new JLabel("0");
        lblTotalQuaHan = new JLabel("0");

        // Cập nhật: Quyền 1 (Admin) và 2 (Staff) dùng chung thẻ lớn
        if (tk.getPhanQuyen() == 1 || tk.getPhanQuyen() == 2) {
            pnlStats.add(createStatCard("TỔNG SỐ SÁCH", lblTotalSach, "📚", new Color(52, 152, 219)));
            pnlStats.add(createStatCard("ĐỘC GIẢ", lblTotalDocGia, "👥", new Color(46, 204, 113)));
            pnlStats.add(createStatCard("ĐANG MƯỢN", lblTotalMuon, "🔄", new Color(241, 196, 15)));
            pnlStats.add(createStatCard("QUÁ HẠN", lblTotalQuaHan, "⚠️", new Color(231, 76, 60)));
        } else {
            pnlStats.add(createMiniStatCard("SÁCH HIỆN CÓ", lblTotalSach, "📖", new Color(52, 152, 219)));
            pnlStats.add(createMiniStatCard("SÁCH ĐANG MƯỢN", lblTotalMuon, "📄", new Color(46, 204, 113)));
            pnlStats.add(createMiniStatCard("SÁCH QUÁ HẠN", lblTotalQuaHan, "⏰", new Color(231, 76, 60)));
            JPanel pnlEmpty = new JPanel(); pnlEmpty.setOpaque(false); pnlStats.add(pnlEmpty);
        }
        pnlCenter.add(pnlStats, BorderLayout.NORTH);

        // --- B. VÙNG NỘI DUNG CHÍNH ---
        JPanel pnlVisuals = new JPanel(new GridLayout(1, 2, 25, 0));
        pnlVisuals.setOpaque(false);

        // 1. CỘT PHẢI: BIỂU ĐỒ (Chung)
        JPanel pnlChartSection = new JPanel(new BorderLayout());
        pnlChartSection.setBackground(Color.WHITE);
        pnlChartSection.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblChartTitle = new JLabel("THỐNG KÊ THỂ LOẠI");
        lblChartTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnlChartSection.add(lblChartTitle, BorderLayout.NORTH);
        
        pnlDrawingChart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawSmoothPieChart((Graphics2D) g, getWidth(), getHeight());
            }
        };
        pnlDrawingChart.setOpaque(false);
        pnlChartSection.add(pnlDrawingChart, BorderLayout.CENTER);

        // 2. CỘT TRÁI: HIỂN THỊ THEO QUYỀN
        // Cập nhật: Quyền 1 và 2 hiện bảng Hoạt động gần đây
        if (tk.getPhanQuyen() == 1 || tk.getPhanQuyen() == 2) {
            JPanel pnlRecent = new JPanel(new BorderLayout());
            pnlRecent.setBackground(Color.WHITE);
            pnlRecent.setBorder(new EmptyBorder(20, 20, 20, 20));
            JLabel lblRecentTitle = new JLabel("HOẠT ĐỘNG GẦN ĐÂY");
            lblRecentTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
            pnlRecent.add(lblRecentTitle, BorderLayout.NORTH);

            modelMuon = new DefaultTableModel(new String[]{"Mã Phiếu", "Độc Giả", "Ngày Mượn", "Trạng Thái"}, 0);
            tableMuonGanDay = new JTable(modelMuon);
            styleTable(tableMuonGanDay);
            JScrollPane scrollTable = new JScrollPane(tableMuonGanDay);
            pnlRecent.add(scrollTable, BorderLayout.CENTER);
            pnlVisuals.add(pnlRecent);
        } else {
            JPanel pnlRightSide = new JPanel(new GridLayout(3, 1, 0, 15));
            pnlRightSide.setOpaque(false);
            
            txtThongBao = createContentArea();
            txtSuggest = createContentArea();
            txtRules = createContentArea();
            txtRules.setText("• Mượn tối đa 5 cuốn/14 ngày.\n• Tiền phạt quá hạn: 4.000đ/ngày.");

            pnlRightSide.add(createSectionPanel("THÔNG BÁO MỚI", txtThongBao, new Color(52, 152, 219)));
            pnlRightSide.add(createSectionPanel("GỢI Ý CHO BẠN", txtSuggest, new Color(230, 126, 34)));
            pnlRightSide.add(createSectionPanel("QUY ĐỊNH MƯỢN TRẢ", txtRules, new Color(45, 52, 54)));
            
            pnlVisuals.add(pnlRightSide);
        }

        pnlVisuals.add(pnlChartSection);
        pnlCenter.add(pnlVisuals, BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);
    }

    private JPanel createMiniStatCard(String title, JLabel lblValue, String icon, Color color) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        lblIcon.setForeground(color);
        JPanel pnlText = new JPanel(new GridLayout(2, 1, 0, 0));
        pnlText.setOpaque(false);
        JLabel lblT = new JLabel(title);
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblT.setForeground(Color.GRAY);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 28));
        pnlText.add(lblT); pnlText.add(lblValue);
        card.add(lblIcon, BorderLayout.WEST);
        card.add(pnlText, BorderLayout.CENTER);
        return card;
    }

    private JPanel createStatCard(String title, JLabel lblValue, String icon, Color themeColor) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(20, 15, 20, 15));
        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        lblIcon.setForeground(themeColor);
        card.add(lblIcon, BorderLayout.WEST);
        JPanel pnlText = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlText.setOpaque(false);
        JLabel lblT = new JLabel(title);
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 32));
        pnlText.add(lblT); pnlText.add(lblValue);
        card.add(pnlText, BorderLayout.CENTER);
        return card;
    }

    private JTextArea createContentArea() {
        JTextArea txt = new JTextArea("Đang tải...");
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setEditable(false);
        txt.setOpaque(false);
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        return txt;
    }

    private JPanel createSectionPanel(String title, JTextArea content, Color accentColor) {
        JPanel pnl = new JPanel(new BorderLayout(0, 8));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new EmptyBorder(15, 15, 15, 15));
        JLabel lblT = new JLabel(title);
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblT.setForeground(accentColor);
        pnl.add(lblT, BorderLayout.NORTH);
        pnl.add(content, BorderLayout.CENTER);
        return pnl;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i<table.getColumnCount(); i++) table.getColumnModel().getColumn(i).setCellRenderer(center);
    }

    private void drawSmoothPieChart(Graphics2D g2d, int w, int h) {
        if (pieData == null || pieData.isEmpty()) return;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int size = Math.min(w, h) - 120;
        int x = 40; int y = (h - size) / 2;
        Color[] colors = { new Color(52, 152, 219), new Color(46, 204, 113), new Color(155, 89, 182), new Color(241, 196, 15), new Color(230, 126, 34) };
        double total = 0;
        for (int val : pieData.values()) total += val;
        double curAngle = 90; int colorIdx = 0;
        int legendX = x + size + 30; int legendY = y + 20;
        for (Map.Entry<String, Integer> entry : pieData.entrySet()) {
            double angle = (entry.getValue() / total) * 360;
            g2d.setColor(colors[colorIdx % colors.length]);
            g2d.fillArc(x, y, size, size, (int) curAngle, (int) Math.ceil(angle));
            g2d.fillRect(legendX, legendY, 12, 12);
            g2d.setColor(new Color(60, 60, 60));
            g2d.drawString(entry.getKey() + " (" + entry.getValue() + ")", legendX + 20, legendY + 11);
            legendY += 30; curAngle += angle; colorIdx++;
        }
        g2d.setColor(Color.WHITE); g2d.fillOval(x + size/4, y + size/4, size/2, size/2);
    }

    private void loadDataDB() {
        new Thread(() -> {
            try {
                int sach = dalThongKe.getTongDauSach();
                int docgia = 0, dangMuon = 0, quaHan = 0;
                ArrayList<Object[]> listFinal = new ArrayList<>();
                String notice = ""; ArrayList<Object[]> suggest = new ArrayList<>();

                // Cập nhật: Quyền 1 và 2 tải dữ liệu quản lý tổng hợp
                if (tk.getPhanQuyen() == 1 || tk.getPhanQuyen() == 2) {
                    docgia = dalThongKe.getTongDocGia();
                    dangMuon = dalThongKe.getTongPhieuMuon();
                    quaHan = dalThongKe.getTongViPham();
                    listFinal = dalThongKe.getMuonTraGanDay();
                } else {
                    String maDG = tk.getMaDocGia();
                    dangMuon = dalThongKe.getCountGlobal("SELECT COUNT(*) FROM phieu_muon WHERE MaDocGia = '" + maDG + "' AND TinhTrang LIKE N'%Đang mượn%'");
                    quaHan = dalThongKe.getCountGlobal("SELECT COUNT(*) FROM phieu_muon WHERE MaDocGia = '" + maDG + "' AND TinhTrang LIKE N'%Quá hạn%'");
                    notice = dalThongKe.getThongBaoMoiNhat();
                    suggest = dalThongKe.getSachGoiY(maDG);
                }

                pieData = dalThongKe.getDataPieChart();
                final int s = sach, d = docgia, m = dangMuon, q = quaHan;
                final ArrayList<Object[]> activities = listFinal;
                final String nt = notice; final ArrayList<Object[]> sg = suggest;

                SwingUtilities.invokeLater(() -> {
                    lblTotalSach.setText(String.valueOf(s));
                    // Cập nhật nhãn hiển thị số lượng độc giả cho Admin/Staff
                    if (tk.getPhanQuyen() == 1 || tk.getPhanQuyen() == 2) lblTotalDocGia.setText(String.valueOf(d));
                    else lblTotalDocGia.setText("Cá nhân");
                    
                    lblTotalMuon.setText(String.valueOf(m));
                    lblTotalQuaHan.setText(String.valueOf(q));

                    if (tk.getPhanQuyen() == 1 || tk.getPhanQuyen() == 2) {
                        modelMuon.setRowCount(0);
                        for (Object[] row : activities) modelMuon.addRow(row);
                    } else {
                        txtThongBao.setText(nt);
                        StringBuilder sb = new StringBuilder();
                        for (Object[] r : sg) sb.append("• ").append(r[0]).append(" (").append(r[1]).append(")\n");
                        txtSuggest.setText(sb.length() > 0 ? sb.toString() : "Hãy mượn thêm sách để nhận gợi ý!");
                    }
                    if (pnlDrawingChart != null) pnlDrawingChart.repaint();
                });
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }
}