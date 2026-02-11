package MUONTRA;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class GUI_LichSuMuon extends JPanel {

    // Màu chủ đạo
    private Color mainColor = new Color(50, 115, 220); 
    private Color bgColor = new Color(245, 248, 253);
    
    private JTable table;
    private DefaultTableModel model;
    private DAL_PhieuMuon dal = new DAL_PhieuMuon(); 
    private String currentMaDocGia; 
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    // Constructor nhận vào Mã Độc Giả
    public GUI_LichSuMuon(String maDocGia) {
        this.currentMaDocGia = maDocGia;
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(bgColor);
        setBorder(new EmptyBorder(20, 20, 20, 20)); // Căn lề form thoáng như Tra Cứu Sách

        // --- 1. HEADER ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(bgColor); // Đồng bộ màu nền
        pnlHeader.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel lblTitle = new JLabel("LỊCH SỬ MƯỢN SÁCH CỦA BẠN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26)); // Phóng to font cho sang
        lblTitle.setForeground(mainColor);
        pnlHeader.add(lblTitle, BorderLayout.WEST);
        
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. TABLE (Khung thẻ Card bo góc FlatLaf) ---
        JPanel pnlTableCard = new JPanel(new BorderLayout());
        pnlTableCard.setBackground(Color.WHITE);
        // Style bo góc 20px, có viền nhạt
        pnlTableCard.putClientProperty(FlatClientProperties.STYLE, "arc: 20; border: 1,1,1,1, #E0E0E0");
        pnlTableCard.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] cols = {"Mã Phiếu", "Tên Sách", "Ngày Mượn", "Hạn Trả", "Ngày Trả", "Trạng Thái", "Tiền Phạt"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        table = new JTable(model);
        
        // [STYLE PREMIUM]
        table.setRowHeight(40); // Dòng cao thoáng
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowVerticalLines(false); // Bỏ kẻ dọc
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(232, 242, 252)); // Màu chọn xanh nhạt
        table.setSelectionForeground(Color.BLACK);

        // Style Header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setBackground(new Color(248, 249, 250)); // Nền xám nhạt cho header
        header.setForeground(new Color(50, 50, 50));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        header.setPreferredSize(new Dimension(0, 45));

        // Renderer Chung (Căn giữa + Striped Rows)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                }
                setBorder(new EmptyBorder(0, 5, 0, 5));
                return c;
            }
        };
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        // Áp dụng renderer cho các cột (trừ cột Tên Sách và Trạng Thái)
        for(int i=0; i<cols.length; i++) {
            if(i != 1 && i != 5) { 
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
        
        // Renderer Cột Tên Sách (Căn trái)
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                setBorder(new EmptyBorder(0, 10, 0, 10)); // Padding
                return c;
            }
        };
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        table.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);
        table.getColumnModel().getColumn(1).setPreferredWidth(250); // Rộng hơn

        // Renderer Cột Trạng Thái (Tô màu chữ + Striped Rows)
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (value != null) ? value.toString() : "";
                
                // Giữ nền sọc
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                }
                
                c.setFont(new Font("Segoe UI", Font.BOLD, 14));
                
                // Tô màu chữ
                if (status.contains("Đang mượn")) {
                    c.setForeground(new Color(0, 123, 255)); // Xanh dương
                } else if (status.contains("Chờ duyệt")) {
                    c.setForeground(new Color(255, 152, 0)); // Cam (Quan trọng cho Online)
                } else if (status.contains("Quá hạn")) {
                    c.setForeground(new Color(220, 53, 69)); // Đỏ
                } else if (status.contains("Đã trả")) {
                    c.setForeground(new Color(40, 167, 69)); // Xanh lá
                } else {
                    c.setForeground(Color.BLACK);
                }
                
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        });

        JScrollPane sc = new JScrollPane(table);
        sc.getViewport().setBackground(Color.WHITE);
        sc.setBorder(BorderFactory.createEmptyBorder()); // Xóa viền đen mặc định của ScrollPane
        
        pnlTableCard.add(sc, BorderLayout.CENTER);
        add(pnlTableCard, BorderLayout.CENTER);
        
        // --- 3. FOOTER (BUTTON REFRESH) ---
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 15));
        pnlFooter.setBackground(bgColor);
        
        JButton btnRefresh = new JButton("Làm Mới Danh Sách");
        btnRefresh.setPreferredSize(new Dimension(180, 42));
        btnRefresh.setBackground(mainColor);
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // [QUAN TRỌNG] Bo góc nút bấm
        btnRefresh.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0");
        
        btnRefresh.addActionListener(e -> loadData());
        
        pnlFooter.add(btnRefresh);
        add(pnlFooter, BorderLayout.SOUTH);
    }

    // Load dữ liệu từ DAL lên bảng (Giữ nguyên logic của anh 100%)
    public void loadData() {
        model.setRowCount(0);
        if (currentMaDocGia == null || currentMaDocGia.isEmpty()) return;

        // Gọi hàm lấy lịch sử
        ArrayList<DTO_PhieuMuon> list = dal.getLichSuMuon(currentMaDocGia);
        
        for (DTO_PhieuMuon pm : list) {
            String ngayTra = (pm.getNgayTra() != null) ? sdf.format(pm.getNgayTra()) : "Chưa trả";
            String tienPhat = (pm.getTienPhat() > 0) ? String.format("%,.0f", pm.getTienPhat()) : "-";
            
            // Xử lý hiển thị trạng thái cho thân thiện
            String trangThaiHienThi = pm.getTinhTrang();
            if(pm.getMaThuThu() != null && pm.getMaThuThu().equalsIgnoreCase("ONLINE") && pm.getTinhTrang().equals("Đang mượn")) {
                 trangThaiHienThi = "Chờ duyệt"; // Nếu mượn online mà chưa được admin duyệt
            }

            model.addRow(new Object[]{
                pm.getMaPhieuMuon(),
                pm.getTenSach(), 
                sdf.format(pm.getNgayMuon()),
                sdf.format(pm.getNgayHenTra()),
                ngayTra,
                trangThaiHienThi, 
                tienPhat
            });
        }
    }
}