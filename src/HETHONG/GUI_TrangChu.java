package HETHONG;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import CHUNG.DBConnect;

public class GUI_TrangChu extends JPanel {

    private DTO_TaiKhoan tk; // Lưu tài khoản đang đăng nhập

    // Class lưu trữ dữ liệu cho từng cột
    private class DataColumn {
        String title;
        int value;
        Color color1, color2, shadowColor;

        public DataColumn(String title, int value, Color c1, Color c2) {
            this.title = title;
            this.value = value;
            this.color1 = c1;
            this.color2 = c2;
            this.shadowColor = new Color(c2.getRed(), c2.getGreen(), c2.getBlue(), 80);
        }
    }

    private ArrayList<DataColumn> listData = new ArrayList<>();
    private int maxValue = 0; 

    public GUI_TrangChu(DTO_TaiKhoan tk) {
        this.tk = tk;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // 1. Load dữ liệu (Tự động phân biệt Admin/Độc giả bên trong hàm này)
        loadDataDB();

        // 2. Panel Biểu đồ
        JPanel pnlChart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                // Không gọi super để giữ nền trong suốt, hàm paintComponent của GUI_TrangChu sẽ lo vẽ nền
                drawChart((Graphics2D) g, getWidth(), getHeight());
            }
        };
        pnlChart.setOpaque(false); 
        add(pnlChart, BorderLayout.CENTER);

        // 3. Footer
        JLabel lblFooter = new JLabel("HỆ THỐNG QUẢN LÝ THƯ VIỆN 2026", SwingConstants.CENTER);
        lblFooter.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFooter.setForeground(new Color(120, 130, 150));
        lblFooter.setBorder(new EmptyBorder(20, 0, 0, 0));
        add(lblFooter, BorderLayout.SOUTH);
    }

    // Vẽ nền Gradient
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        int w = getWidth();
        int h = getHeight();
        Color color1 = new Color(255, 255, 255); 
        Color color2 = new Color(195, 225, 250); 
        GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);
    }

    private void loadDataDB() {
        listData.clear();
        
        if (tk.getPhanQuyen() == 3) { 
            // --- [ĐỘC GIẢ] LOAD THỐNG KÊ CÁ NHÂN ---
            String maDG = tk.getMaDocGia();
            
            // Lấy số liệu cá nhân
            int dangMuon = getCountPersonal(maDG, "Đang mượn");
            int daTra = getCountPersonal(maDG, "Đã trả");
            int quaHan = getCountPersonal(maDG, "Quá hạn");
            int tongLuot = getCountAllPersonal(maDG); // Tổng số phiếu từng mượn

            // 1. TỔNG LƯỢT (Màu Tím Royal - Thể hiện lịch sử dày dạn)
            listData.add(new DataColumn("TỔNG LƯỢT", tongLuot, 
                    new Color(160, 100, 255), new Color(110, 50, 230)));

            // 2. ĐANG MƯỢN (Màu Cam - Đang hoạt động)
            listData.add(new DataColumn("ĐANG MƯỢN", dangMuon, 
                    new Color(255, 180, 60), new Color(255, 140, 0)));

            // 3. ĐÃ TRẢ (Màu Xanh Lá - Uy tín)
            listData.add(new DataColumn("ĐÃ TRẢ", daTra, 
                    new Color(100, 220, 120), new Color(46, 180, 80)));

            // 4. QUÁ HẠN (Màu Đỏ - Cảnh báo)
            listData.add(new DataColumn("QUÁ HẠN", quaHan, 
                    new Color(255, 94, 98), new Color(220, 40, 50)));

        } else {
            // --- [ADMIN/THỦ THƯ] LOAD THỐNG KÊ TOÀN HỆ THỐNG ---
            int sach = getCountGlobal("sach");
            int docgia = getCountGlobal("doc_gia");
            int dangmuon = getCountGlobal("phieu_muon WHERE TinhTrang LIKE N'%Đang mượn%'"); 
            int quahan = getCountGlobal("phieu_muon WHERE TinhTrang LIKE N'%Quá hạn%'");    
            
            listData.add(new DataColumn("TỔNG SÁCH", sach, new Color(0, 198, 255), new Color(0, 114, 255)));
            listData.add(new DataColumn("ĐỘC GIẢ", docgia, new Color(29, 233, 182), new Color(13, 169, 154)));
            listData.add(new DataColumn("ĐANG MƯỢN", dangmuon, new Color(255, 154, 68), new Color(252, 96, 118)));
            listData.add(new DataColumn("QUÁ HẠN", quahan, new Color(255, 94, 98), new Color(193, 39, 45)));
        }

        // Tính Max Value để chia tỷ lệ cột
        maxValue = 0;
        for (DataColumn d : listData) {
            if (d.value > maxValue) maxValue = d.value;
        }
        if (maxValue == 0) maxValue = 1; 
    }

    private void drawChart(Graphics2D g2, int w, int h) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // --- TIÊU ĐỀ THAY ĐỔI THEO VAI TRÒ ---
        String title = (tk.getPhanQuyen() == 3) ? 
                       "THỐNG KÊ CÁ NHÂN CỦA BẠN" : "TỔNG QUAN HOẠT ĐỘNG HỆ THỐNG";
        
        g2.setFont(new Font("Segoe UI", Font.BOLD, 32));
        g2.setColor(new Color(55, 65, 80)); 
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, (w - fm.stringWidth(title)) / 2, 60);

        // Nếu là Độc giả, thêm dòng chào nhỏ bên dưới
        if (tk.getPhanQuyen() == 3) {
            g2.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            g2.setColor(new Color(100, 100, 100));
            String sub = "Xin chào " + tk.getUserName() + ", đây là hồ sơ mượn sách của bạn.";
            g2.drawString(sub, (w - g2.getFontMetrics().stringWidth(sub)) / 2, 90);
        }

        // Cấu hình vẽ cột (Giống code cũ)
        int numCols = listData.size();
        int gap = 70;
        int maxBarHeight = h - 240; // Giảm chiều cao cột chút để chừa chỗ cho sub-title
        int barWidth = 170; 
        
        int totalChartWidth = (numCols * barWidth) + ((numCols - 1) * gap);
        int startX = (w - totalChartWidth) / 2;
        int bottomY = h - 90; 

        for (int i = 0; i < numCols; i++) {
            DataColumn item = listData.get(i);
            
            double TyLe = (double) item.value / maxValue;
            if (item.value > 0 && TyLe < 0.08) TyLe = 0.08; 
            
            int barHeight = (int) (TyLe * maxBarHeight);
            if (item.value == 0) barHeight = 5; 
            
            int x = startX + i * (barWidth + gap);
            int y = bottomY - barHeight;

            // Bóng
            g2.setColor(item.shadowColor);
            g2.fillRoundRect(x + 8, y + 15, barWidth - 16, barHeight - 10, 35, 35);
            g2.setColor(new Color(item.shadowColor.getRed(), item.shadowColor.getGreen(), item.shadowColor.getBlue(), 40));
            g2.fillRoundRect(x + 4, y + 8, barWidth - 8, barHeight, 40, 40);

            // Cột
            GradientPaint gp = new GradientPaint(x, y, item.color1, x + barWidth, y + barHeight, item.color2);
            g2.setPaint(gp);
            g2.fillRoundRect(x, y, barWidth, barHeight, 25, 25);

            // Số liệu
            g2.setFont(new Font("Segoe UI", Font.BOLD, 42));
            String valStr = String.valueOf(item.value);
            fm = g2.getFontMetrics();
            int textY = y + 60;
            boolean isInside = true;
            if (barHeight < 70) { textY = y - 15; isInside = false; }

            if (isInside) {
                g2.setColor(new Color(0,0,0,50));
                g2.drawString(valStr, x + (barWidth - fm.stringWidth(valStr)) / 2 + 2, textY + 2);
            }
            g2.setColor(isInside ? Color.WHITE : item.color2);
            g2.drawString(valStr, x + (barWidth - fm.stringWidth(valStr)) / 2, textY);

            // Tên cột
            g2.setColor(new Color(90, 100, 120));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
            fm = g2.getFontMetrics();
            g2.drawString(item.title.toUpperCase(), x + (barWidth - fm.stringWidth(item.title.toUpperCase())) / 2, bottomY + 35);
        }
    }

    // --- CÁC HÀM TRUY VẤN DỮ LIỆU ---

    // 1. Lấy thống kê cho Admin (Toàn hệ thống)
    private int getCountGlobal(String tableName) {
        int count = 0;
        try {
            Connection conn = new DBConnect().getConnection();
            Statement stmt = conn.createStatement();
            String query = "SELECT COUNT(*) FROM " + tableName;
            if(tableName.toUpperCase().contains("SELECT")) query = tableName; 
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) count = rs.getInt(1);
            conn.close();
        } catch (Exception e) { e.printStackTrace(); }
        return count;
    }

    // 2. Lấy thống kê riêng cho Độc giả theo Tình Trạng
    private int getCountPersonal(String maDocGia, String tinhTrangKeyword) {
        int count = 0;
        try {
            Connection conn = new DBConnect().getConnection();
            // Tìm trong bảng PHIEU_MUON theo MaDocGia và TinhTrang
            String sql = "SELECT COUNT(*) FROM PHIEU_MUON WHERE MaDocGia = ? AND TinhTrang LIKE ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, maDocGia);
            ps.setString(2, "%" + tinhTrangKeyword + "%");
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) count = rs.getInt(1);
            conn.close();
        } catch (Exception e) { e.printStackTrace(); }
        return count;
    }

    // 3. Lấy tổng số phiếu mượn của Độc giả (Không quan tâm tình trạng)
    private int getCountAllPersonal(String maDocGia) {
        int count = 0;
        try {
            Connection conn = new DBConnect().getConnection();
            String sql = "SELECT COUNT(*) FROM PHIEU_MUON WHERE MaDocGia = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, maDocGia);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) count = rs.getInt(1);
            conn.close();
        } catch (Exception e) { e.printStackTrace(); }
        return count;
    }
}