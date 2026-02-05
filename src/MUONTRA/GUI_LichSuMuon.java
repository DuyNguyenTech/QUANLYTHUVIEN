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

    private Color mainColor = new Color(50, 115, 220); 
    private Color bgColor = new Color(245, 248, 253);
    
    private JTable table;
    private DefaultTableModel model;
    private DAL_PhieuMuon dal = new DAL_PhieuMuon(); // [SỬA]
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

        // --- HEADER ---
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitle = new JLabel("LỊCH SỬ MƯỢN SÁCH");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(mainColor);
        pnlHeader.add(lblTitle);
        
        add(pnlHeader, BorderLayout.NORTH);

        // --- TABLE ---
        JPanel pnlTable = new JPanel(new BorderLayout());
        pnlTable.setBorder(new EmptyBorder(20, 20, 20, 20));
        pnlTable.setBackground(bgColor);

        String[] cols = {"Mã Phiếu", "Tên Sách", "Ngày Mượn", "Hạn Trả", "Ngày Trả", "Trạng Thái", "Tiền Phạt"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));

        // Style Header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(240, 240, 240));
        header.setPreferredSize(new Dimension(0, 40));

        // Căn giữa nội dung bảng
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i<cols.length; i++) {
            if(i != 1) { // Trừ cột Tên Sách (để căn trái)
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
        
        // Cột tên sách rộng hơn
        table.getColumnModel().getColumn(1).setPreferredWidth(250);

        // Renderer tô màu Trạng Thái
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (value != null) ? value.toString() : "";
                c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                
                if (status.equals("Đang mượn")) {
                    c.setForeground(new Color(0, 123, 255)); // Xanh dương
                } else if (status.equals("Chờ duyệt")) {
                    c.setForeground(new Color(255, 152, 0)); // Cam (Màu cho trạng thái ONLINE)
                } else if (status.equals("Quá hạn")) {
                    c.setForeground(Color.RED);
                } else {
                    c.setForeground(new Color(40, 167, 69)); // Xanh lá (Đã trả)
                }
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        });

        pnlTable.add(new JScrollPane(table), BorderLayout.CENTER);
        add(pnlTable, BorderLayout.CENTER);
        
        // --- BUTTON LÀM MỚI (Góc dưới) ---
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlFooter.setBackground(bgColor);
        pnlFooter.setBorder(new EmptyBorder(0, 0, 10, 20));
        
        JButton btnRefresh = new JButton("Làm mới danh sách");
        btnRefresh.setPreferredSize(new Dimension(160, 40));
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

        // [SỬA LẠI] Gọi hàm getLichSuMuon từ DAL_PhieuMuon
        ArrayList<DTO_PhieuMuon> list = dal.getLichSuMuon(currentMaDocGia);
        
        for (DTO_PhieuMuon pm : list) {
            String ngayTra = (pm.getNgayTra() != null) ? sdf.format(pm.getNgayTra()) : "Chưa trả";
            String tienPhat = (pm.getTienPhat() > 0) ? String.format("%,.0f", pm.getTienPhat()) : "-";
            
            model.addRow(new Object[]{
                pm.getMaPhieuMuon(),
                pm.getTenSach(), // Đã có nhờ update DTO
                sdf.format(pm.getNgayMuon()),
                sdf.format(pm.getNgayHenTra()),
                ngayTra,
                pm.getTinhTrang(), // Sẽ hiện "Chờ duyệt" nếu MaThuThu là ONLINE
                tienPhat
            });
        }
    }
}