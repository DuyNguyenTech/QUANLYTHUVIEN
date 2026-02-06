package HETHONG;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import CHUNG.DBConnect;

public class GUI_TrangChu extends JPanel {

    // Class lưu trữ dữ liệu cho từng cột
    private class DataColumn {
        String title;
        int value;
        Color color1; // Màu trên (nhạt)
        Color color2; // Màu dưới (đậm)

        public DataColumn(String title, int value, Color c1, Color c2) {
            this.title = title;
            this.value = value;
            this.color1 = c1;
            this.color2 = c2;
        }
    }

    private ArrayList<DataColumn> listData = new ArrayList<>();
    private int maxValue = 0; // Để tính tỷ lệ chiều cao

    public GUI_TrangChu() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 248, 253));
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // 1. Load dữ liệu từ CSDL
        loadDataDB();

        // 2. Panel Biểu đồ (Chiếm toàn bộ không gian)
        JPanel pnlChart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawChart((Graphics2D) g, getWidth(), getHeight());
            }
        };
        pnlChart.setOpaque(false); // Để trong suốt thấy nền
        add(pnlChart, BorderLayout.CENTER);

        // 3. Footer (Chữ chào mừng nhỏ bên dưới)
        JLabel lblFooter = new JLabel("HỆ THỐNG QUẢN LÝ THƯ VIỆN 2026", SwingConstants.CENTER);
        lblFooter.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFooter.setForeground(new Color(150, 150, 150));
        lblFooter.setBorder(new EmptyBorder(20, 0, 0, 0));
        add(lblFooter, BorderLayout.SOUTH);
    }

    private void loadDataDB() {
        listData.clear();
        // Lấy số liệu thực tế
        int sach = getCount("sach");
        int docgia = getCount("doc_gia");
        int dangmuon = getCount("phieu_muon WHERE TinhTrang = 'Đang mượn'"); // Hoặc like N'Đang mượn'
        int quahan = getCount("phieu_muon WHERE TinhTrang = 'Quá hạn'");    // Hoặc like N'Quá hạn'
        
        // [QUAN TRỌNG] Nếu dữ liệu = 0 thì gán = 1 để cột vẫn hiện lên 1 chút cho đẹp, không bị mất hút
        // Anh có thể bỏ logic này nếu muốn đúng tuyệt đối 100%
        
        // Thêm vào list (Title, Value, Color Start, Color End)
        listData.add(new DataColumn("TỔNG SÁCH", sach, new Color(100, 181, 246), new Color(25, 118, 210))); // Xanh dương
        listData.add(new DataColumn("ĐỘC GIẢ", docgia, new Color(129, 199, 132), new Color(56, 142, 60)));    // Xanh lá
        listData.add(new DataColumn("ĐANG MƯỢN", dangmuon, new Color(255, 183, 77), new Color(245, 124, 0))); // Cam
        listData.add(new DataColumn("QUÁ HẠN", quahan, new Color(229, 115, 115), new Color(211, 47, 47)));   // Đỏ

        // Tìm giá trị lớn nhất để chia tỷ lệ chiều cao
        maxValue = 0;
        for (DataColumn d : listData) {
            if (d.value > maxValue) maxValue = d.value;
        }
        if (maxValue == 0) maxValue = 1; // Tránh chia cho 0
    }

    private void drawChart(Graphics2D g2, int w, int h) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Vẽ tiêu đề lớn
        g2.setFont(new Font("Arial", Font.BOLD, 28));
        g2.setColor(new Color(60, 60, 60));
        String title = "THỐNG KÊ TỔNG QUAN";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, (w - fm.stringWidth(title)) / 2, 50);

        // Cấu hình vẽ cột
        int numCols = listData.size();
        int gap = 60; // Khoảng cách giữa các cột
        int maxBarHeight = h - 200; // Chiều cao tối đa của cột (chừa lề trên dưới)
        int barWidth = 160; // Độ rộng mỗi cột (Cố định cho đẹp)
        
        // Tính vị trí bắt đầu X để căn giữa toàn bộ biểu đồ
        int totalChartWidth = (numCols * barWidth) + ((numCols - 1) * gap);
        int startX = (w - totalChartWidth) / 2;
        int bottomY = h - 80; // Đường chân của biểu đồ

        // Vòng lặp vẽ từng cột
        for (int i = 0; i < numCols; i++) {
            DataColumn item = listData.get(i);
            
            // Tính chiều cao cột dựa trên tỷ lệ giá trị (Value / MaxValue)
            // Math.max(item.value, maxValue/10) -> Mẹo nhỏ: Nếu giá trị nhỏ quá thì vẫn vẽ cao tầm 10% để hiển thị được số
            double TyLe = (double) item.value / maxValue;
            if (TyLe < 0.05) TyLe = 0.05; // Cột thấp nhất cũng phải cao 5% để đẹp
            
            int barHeight = (int) (TyLe * maxBarHeight);
            
            int x = startX + i * (barWidth + gap);
            int y = bottomY - barHeight;

            // 1. Vẽ bóng đổ (Shadow)
            g2.setColor(new Color(0, 0, 0, 20));
            g2.fillRoundRect(x + 10, y + 10, barWidth, barHeight, 20, 20);

            // 2. Vẽ Cột (Gradient)
            GradientPaint gp = new GradientPaint(x, y, item.color1, x, y + barHeight, item.color2);
            g2.setPaint(gp);
            g2.fillRoundRect(x, y, barWidth, barHeight, 20, 20);

            // 3. Vẽ Số liệu (Nằm trong cột hoặc trên đỉnh cột)
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 36));
            String valStr = String.valueOf(item.value);
            fm = g2.getFontMetrics();
            
            // Nếu cột cao thì số nằm trong, thấp quá thì số nằm trên đầu
            int textY = y + 50; 
            if (barHeight < 60) { 
                textY = y - 10; 
                g2.setColor(item.color2); // Đổi màu chữ nếu nằm ngoài
            }
            g2.drawString(valStr, x + (barWidth - fm.stringWidth(valStr)) / 2, textY);

            // 4. Vẽ Tên Cột (Dưới chân)
            g2.setColor(new Color(80, 80, 80));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            fm = g2.getFontMetrics();
            g2.drawString(item.title, x + (barWidth - fm.stringWidth(item.title)) / 2, bottomY + 30);
        }
    }

    private int getCount(String tableName) {
        int count = 0;
        try {
            Connection conn = new DBConnect().getConnection();
            Statement stmt = conn.createStatement();
            String query = "SELECT COUNT(*) FROM " + tableName;
            // Nếu có chữ WHERE thì không cần nối chuỗi
            if(tableName.toUpperCase().contains("SELECT")) query = tableName; 
            
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                count = rs.getInt(1);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }
}