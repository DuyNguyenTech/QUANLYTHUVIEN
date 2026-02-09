package HETHONG;

import THONGKE.DAL_ThongKe;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class GUI_TrangChu extends JPanel {

    private DTO_TaiKhoan tk; 
    private DAL_ThongKe dal = new DAL_ThongKe(); // Gọi DAL xử lý dữ liệu

    // Class lưu trữ dữ liệu cột
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

        // 1. Load dữ liệu (Gọi qua DAL)
        loadData();

        // 2. Panel Biểu đồ
        JPanel pnlChart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                // Vẽ biểu đồ đè lên nền trong suốt
                drawChart((Graphics2D) g, getWidth(), getHeight());
            }
        };
        pnlChart.setOpaque(false); 
        add(pnlChart, BorderLayout.CENTER);

        // 3. Footer
        JLabel lblFooter = new JLabel("HỆ THỐNG QUẢN LÝ THƯ VIỆN - PHIÊN BẢN 2026", SwingConstants.CENTER);
        lblFooter.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFooter.setForeground(new Color(120, 130, 150));
        lblFooter.setBorder(new EmptyBorder(20, 0, 0, 0));
        add(lblFooter, BorderLayout.SOUTH);
    }

    // Vẽ nền Gradient sang trọng
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        int w = getWidth();
        int h = getHeight();
        Color color1 = new Color(255, 255, 255); 
        Color color2 = new Color(225, 240, 255); // Xanh rất nhạt
        GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);
    }

    private void loadData() {
        listData.clear();
        
        // --- LOGIC PHÂN QUYỀN ---
        // Giả sử quy ước: 1=Admin, 2=Thủ thư, 3=Độc giả
        if (tk.getPhanQuyen() == 3) { 
            // === VIEW CHO ĐỘC GIẢ ===
            String maDG = tk.getMaDocGia();
            
            // Gọi hàm từ DAL_ThongKe (đã thêm ở Bước 1)
            int tongLuot = dal.getCountAllPersonal(maDG);
            int dangMuon = dal.getCountPersonal(maDG, "Đang mượn");
            int daTra = dal.getCountPersonal(maDG, "Đã trả");
            int quaHan = dal.getCountPersonal(maDG, "Quá hạn");

            // Cột 1: Tổng lượt (Tím)
            listData.add(new DataColumn("TỔNG LỊCH SỬ", tongLuot, new Color(160, 100, 255), new Color(110, 50, 230)));
            // Cột 2: Đang mượn (Cam)
            listData.add(new DataColumn("ĐANG MƯỢN", dangMuon, new Color(255, 180, 60), new Color(255, 140, 0)));
            // Cột 3: Đã trả (Xanh lá)
            listData.add(new DataColumn("ĐÃ TRẢ", daTra, new Color(100, 220, 120), new Color(46, 180, 80)));
            // Cột 4: Quá hạn (Đỏ)
            listData.add(new DataColumn("QUÁ HẠN / PHẠT", quaHan, new Color(255, 94, 98), new Color(220, 40, 50)));

        } else {
            // === VIEW CHO ADMIN / THỦ THƯ ===
            // Gọi hàm sẵn có từ DAL_ThongKe
            int sach = dal.getTongDauSach();
            int docgia = dal.getTongDocGia();
            int dangmuon = dal.getTongPhieuMuon(); // Thực tế là tổng lượt mượn toàn hệ thống
            int vipham = dal.getTongViPham();
            
            listData.add(new DataColumn("KHO SÁCH", sach, new Color(41, 182, 246), new Color(2, 119, 189)));
            listData.add(new DataColumn("ĐỘC GIẢ", docgia, new Color(102, 187, 106), new Color(46, 125, 50)));
            listData.add(new DataColumn("HOẠT ĐỘNG", dangmuon, new Color(255, 167, 38), new Color(239, 108, 0)));
            listData.add(new DataColumn("CẢNH BÁO", vipham, new Color(239, 83, 80), new Color(198, 40, 40)));
        }

        // Tính Max Value để vẽ tỷ lệ cột
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
        String title = (tk.getPhanQuyen() == 3) ? "HỒ SƠ MƯỢN SÁCH CÁ NHÂN" : "TỔNG QUAN HỆ THỐNG THƯ VIỆN";
        
        g2.setFont(new Font("Segoe UI", Font.BOLD, 32));
        g2.setColor(new Color(60, 70, 90)); 
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, (w - fm.stringWidth(title)) / 2, 50);

        if (tk.getPhanQuyen() == 3) {
            g2.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            g2.setColor(new Color(100, 100, 100));
            String sub = "Xin chào " + tk.getUserName() + ", dưới đây là thống kê hoạt động của bạn.";
            g2.drawString(sub, (w - g2.getFontMetrics().stringWidth(sub)) / 2, 80);
        }

        // --- VẼ BIỂU ĐỒ CỘT ---
        int numCols = listData.size();
        int gap = 80; // Khoảng cách giữa các cột
        int maxBarHeight = h - 220; 
        int barWidth = 160; 
        
        int totalChartWidth = (numCols * barWidth) + ((numCols - 1) * gap);
        int startX = (w - totalChartWidth) / 2;
        int bottomY = h - 80; 

        for (int i = 0; i < numCols; i++) {
            DataColumn item = listData.get(i);
            
            double ratio = (double) item.value / maxValue;
            if (item.value > 0 && ratio < 0.05) ratio = 0.05; // Cột thấp nhất vẫn hiện 1 chút
            
            int barHeight = (int) (ratio * maxBarHeight);
            if (item.value == 0) barHeight = 4; // Nếu = 0 thì vẽ 1 vạch mỏng
            
            int x = startX + i * (barWidth + gap);
            int y = bottomY - barHeight;

            // 1. Vẽ bóng đổ (Shadow)
            g2.setColor(item.shadowColor);
            g2.fillRoundRect(x + 10, y + 10, barWidth - 10, barHeight - 5, 25, 25);

            // 2. Vẽ Cột (Gradient)
            GradientPaint gp = new GradientPaint(x, y, item.color1, x + barWidth, y + barHeight, item.color2);
            g2.setPaint(gp);
            g2.fillRoundRect(x, y, barWidth, barHeight, 20, 20);

            // 3. Vẽ Số liệu
            g2.setFont(new Font("Segoe UI", Font.BOLD, 42));
            String valStr = String.valueOf(item.value);
            fm = g2.getFontMetrics();
            
            // Logic: Nếu cột cao > 60px thì số nằm trong, thấp thì số nằm trên đầu
            int textY = y + 55;
            boolean isInside = true;
            if (barHeight < 60) { textY = y - 10; isInside = false; }

            if (isInside) {
                // Vẽ bóng chữ nhẹ nếu nằm trong cột
                g2.setColor(new Color(0,0,0,40));
                g2.drawString(valStr, x + (barWidth - fm.stringWidth(valStr)) / 2 + 2, textY + 2);
            }
            g2.setColor(isInside ? Color.WHITE : item.color2);
            g2.drawString(valStr, x + (barWidth - fm.stringWidth(valStr)) / 2, textY);

            // 4. Vẽ Tên Cột
            g2.setColor(new Color(80, 90, 100));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            fm = g2.getFontMetrics();
            g2.drawString(item.title, x + (barWidth - fm.stringWidth(item.title)) / 2, bottomY + 30);
        }
    }
}