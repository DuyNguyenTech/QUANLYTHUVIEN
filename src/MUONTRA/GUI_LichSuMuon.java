package MUONTRA;

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

        // --- 1. HEADER ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(new EmptyBorder(15, 20, 15, 20));
        // Kẻ đường line dưới
        pnlHeader.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            new EmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel lblTitle = new JLabel("LỊCH SỬ MƯỢN SÁCH CỦA BẠN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(mainColor);
        pnlHeader.add(lblTitle, BorderLayout.WEST);
        
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. TABLE ---
        JPanel pnlTable = new JPanel(new BorderLayout());
        pnlTable.setBorder(new EmptyBorder(20, 20, 20, 20));
        pnlTable.setBackground(bgColor);

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
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(Color.WHITE);
        header.setForeground(mainColor);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, mainColor));
        header.setPreferredSize(new Dimension(0, 40));

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
                
                c.setFont(new Font("Segoe UI", Font.BOLD, 13));
                
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
        sc.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        pnlTable.add(sc, BorderLayout.CENTER);
        add(pnlTable, BorderLayout.CENTER);
        
        // --- 3. FOOTER (BUTTON REFRESH) ---
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlFooter.setBackground(bgColor);
        pnlFooter.setBorder(new EmptyBorder(0, 0, 20, 20));
        
        JButton btnRefresh = new JButton("Làm mới danh sách");
        btnRefresh.setPreferredSize(new Dimension(180, 45));
        btnRefresh.setBackground(mainColor);
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnRefresh.addActionListener(e -> loadData());
        
        pnlFooter.add(btnRefresh);
        add(pnlFooter, BorderLayout.SOUTH);
    }

    // Load dữ liệu từ DAL lên bảng
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