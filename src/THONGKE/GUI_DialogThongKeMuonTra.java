package THONGKE;

import com.formdev.flatlaf.FlatClientProperties;
import CHUNG.ExcelExporter;
import MUONTRA.DAL_PhieuMuon;
import MUONTRA.DTO_PhieuMuon;
import MUONTRA.GUI_DialogChiTietPhieuMuon;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class GUI_DialogThongKeMuonTra extends JDialog {

    private DAL_ThongKe dal = new DAL_ThongKe();
    private DAL_PhieuMuon dalPhieu = new DAL_PhieuMuon();
    
    private JTable table;
    private DefaultTableModel model;
    private JLabel lblTongSo;
    private JRadioButton rdoNgay, rdoTuan, rdoThang, rdoDangMuon;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    
    private Color mainColor = new Color(50, 115, 220);
    private Color bgColor = new Color(245, 248, 253);

    public GUI_DialogThongKeMuonTra(Window parent) {
        super(parent, ModalityType.APPLICATION_MODAL);
        initUI();
        // Mặc định chọn "Tháng này" và tải dữ liệu khi mở Dialog
        rdoThang.setSelected(true);
        loadData("MONTH");
    }

    private void initUI() {
        setTitle("BÁO CÁO MƯỢN TRẢ");
        setSize(1000, 720);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(bgColor);

        // --- 1. HEADER ---
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlHeader.setBackground(mainColor);
        pnlHeader.setBorder(new EmptyBorder(15, 0, 15, 0));
        JLabel lblTitle = new JLabel("THỐNG KÊ HOẠT ĐỘNG MƯỢN TRẢ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        pnlHeader.add(lblTitle);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. CONTENT ---
        JPanel pnlContent = new JPanel(new BorderLayout(0, 15));
        pnlContent.setBorder(new EmptyBorder(20, 30, 10, 30));
        pnlContent.setOpaque(false);

        // A. Filter Panel (Lọc dữ liệu)
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.CENTER, 35, 12));
        pnlFilter.setBackground(Color.WHITE);
        pnlFilter.putClientProperty("FlatLaf.style", "arc: 20; border: 1,1,1,1, #E0E0E0");
        
        rdoNgay = new JRadioButton("Hôm nay");
        rdoTuan = new JRadioButton("Tuần này");
        rdoThang = new JRadioButton("Tháng này");
        rdoDangMuon = new JRadioButton("Đang mượn (Chưa trả)");
        
        Font fontF = new Font("Segoe UI Semibold", Font.PLAIN, 14);
        rdoNgay.setFont(fontF); rdoNgay.setOpaque(false);
        rdoTuan.setFont(fontF); rdoTuan.setOpaque(false);
        rdoThang.setFont(fontF); rdoThang.setOpaque(false);
        rdoDangMuon.setFont(fontF); rdoDangMuon.setOpaque(false);

        ButtonGroup bg = new ButtonGroup();
        bg.add(rdoNgay); bg.add(rdoTuan); bg.add(rdoThang); bg.add(rdoDangMuon);
        
        pnlFilter.add(rdoNgay); pnlFilter.add(rdoTuan); pnlFilter.add(rdoThang); pnlFilter.add(rdoDangMuon);
        pnlContent.add(pnlFilter, BorderLayout.NORTH);

        // B. Table Section
        JPanel pnlTableCard = new JPanel(new BorderLayout());
        pnlTableCard.setBackground(Color.WHITE);
        pnlTableCard.putClientProperty("FlatLaf.style", "arc: 25; border: 1,1,1,1, #E0E0E0");
        pnlTableCard.setBorder(new EmptyBorder(10, 10, 10, 10));

        model = new DefaultTableModel(new String[]{"Mã Phiếu", "Ngày Mượn", "Ngày Trả", "Trạng Thái"}, 0) { 
            @Override public boolean isCellEditable(int r, int c) { return false; } 
        };
        table = new JTable(model);
        table.setRowHeight(40);
        
        table.setSelectionBackground(new Color(210, 230, 255));
        table.setSelectionForeground(mainColor);
        
        JTableHeader h = table.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 15));
        h.setForeground(mainColor);
        h.setBackground(Color.WHITE);
        h.setPreferredSize(new Dimension(0, 45));
        
        DefaultTableCellRenderer customRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                if (isSelected) {
                    setFont(new Font("Segoe UI", Font.BOLD, 14));
                } else {
                    setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(252, 252, 252));
                    if (column == 3 && value != null) {
                        setForeground(value.toString().equals("Đang mượn") ? new Color(255, 152, 0) : new Color(40, 167, 69));
                    } else { setForeground(Color.BLACK); }
                }
                return c;
            }
        };
        for(int i=0; i<4; i++) table.getColumnModel().getColumn(i).setCellRenderer(customRenderer);

        JScrollPane sc = new JScrollPane(table);
        sc.setBorder(BorderFactory.createEmptyBorder());
        sc.getViewport().setBackground(Color.WHITE);
        pnlTableCard.add(sc, BorderLayout.CENTER);

        // C. Footer Info
        JPanel pnlFooterInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlFooterInfo.setBackground(new Color(238, 242, 248));
        pnlFooterInfo.putClientProperty("FlatLaf.style", "arc: 15");
        pnlFooterInfo.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JLabel lblText = new JLabel("Tổng số phiếu tìm thấy: ");
        lblText.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblTongSo = new JLabel("0 phiếu");
        lblTongSo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTongSo.setForeground(new Color(220, 53, 69));
        
        pnlFooterInfo.add(lblText); 
        pnlFooterInfo.add(lblTongSo);

        JPanel pnlCenterWrapper = new JPanel(new BorderLayout(0, 15));
        pnlCenterWrapper.setOpaque(false);
        pnlCenterWrapper.add(pnlTableCard, BorderLayout.CENTER);
        pnlCenterWrapper.add(pnlFooterInfo, BorderLayout.SOUTH);
        pnlContent.add(pnlCenterWrapper, BorderLayout.CENTER);
        add(pnlContent, BorderLayout.CENTER);

        // --- 3. BOTTOM BUTTONS ---
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        pnlBot.setOpaque(false);
        JButton btnXuat = createButton("Xuất Excel", new Color(40, 167, 69));
        JButton btnDong = createButton("Đóng", new Color(220, 53, 69));
        pnlBot.add(btnXuat); pnlBot.add(btnDong);
        add(pnlBot, BorderLayout.SOUTH);

        // --- GẮN SỰ KIỆN CHO 4 CHỨC NĂNG LỌC ---
        rdoNgay.addActionListener(e -> loadData("DAY"));      // Lọc hôm nay
        rdoTuan.addActionListener(e -> loadData("WEEK"));     // Lọc tuần này
        rdoThang.addActionListener(e -> loadData("MONTH"));   // Lọc tháng này
        rdoDangMuon.addActionListener(e -> loadData("CURRENT")); // Lọc đang mượn
        
        btnXuat.addActionListener(e -> new ExcelExporter().exportTable(table));
        btnDong.addActionListener(e -> dispose());
        
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        String maPhieu = table.getValueAt(row, 0).toString();
                        DTO_PhieuMuon pmFull = dalPhieu.getPhieuByMa(maPhieu);
                        if (pmFull != null) new GUI_DialogChiTietPhieuMuon(GUI_DialogThongKeMuonTra.this, pmFull).setVisible(true);
                    }
                }
            }
        });
    }

    /**
     * Tải dữ liệu từ DAL_ThongKe dựa trên tham số lọc
     */
    private void loadData(String option) {
        model.setRowCount(0); // Xóa dữ liệu cũ trên bảng trước khi nạp mới
        
     // Thêm tiền tố MUONTRA. vào trước tên lớp
        ArrayList<MUONTRA.DTO_PhieuMuon> list = dal.getListMuonTra(option); 

        if (list != null) {
            for (MUONTRA.DTO_PhieuMuon pm : list) { // Chỗ này cũng phải có MUONTRA.
                String ngayTra = (pm.getNgayTra() != null) ? sdf.format(pm.getNgayTra()) : "";
                String trangThai = (pm.getNgayTra() == null) ? "Đang mượn" : "Đã trả";
                
                model.addRow(new Object[]{
                    pm.getMaPhieuMuon(), 
                    (pm.getNgayMuon() != null ? sdf.format(pm.getNgayMuon()) : ""), 
                    ngayTra, 
                    trangThai
                });
            }
            lblTongSo.setText(list.size() + " phiếu");
        }
        
//        ArrayList<DTO_PhieuMuon> list = dal.getListMuonTra(option); // Gọi DAL để lấy danh sách đã lọc
//        
//        if (list != null) {
//            for (DTO_PhieuMuon pm : list) {
//                String ngayTra = (pm.getNgayTra() != null) ? sdf.format(pm.getNgayTra()) : "";
//                // Hiển thị trạng thái "Đang mượn" nếu chưa có ngày trả, ngược lại là "Đã trả"
//                String trangThai = (pm.getNgayTra() == null) ? "Đang mượn" : "Đã trả";
//                
//                model.addRow(new Object[]{
//                    pm.getMaPhieuMuon(), 
//                    (pm.getNgayMuon() != null ? sdf.format(pm.getNgayMuon()) : ""), 
//                    ngayTra, 
//                    trangThai
//                });
//            }
//            lblTongSo.setText(list.size() + " phiếu"); // Cập nhật tổng số phiếu tìm thấy
//        }
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(150, 42));
        btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("FlatLaf.style", "arc: 12; borderWidth: 0");
        return btn;
    }
}