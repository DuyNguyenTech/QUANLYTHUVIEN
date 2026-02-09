package MUONTRA;

import HETHONG.GUI_Main;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class GUI_QuanLyMuonTra extends JPanel {

    private Color mainColor = new Color(50, 115, 220);
    private Color bgColor = new Color(245, 248, 253);

    private JTextField txtTimKiem;
    private JTable table;
    private DefaultTableModel model;
    private DAL_PhieuMuon dal = new DAL_PhieuMuon();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public GUI_QuanLyMuonTra() {
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(bgColor);

        // --- 1. HEADER PANEL (Sử dụng GridBagLayout để chống đè chữ) ---
        JPanel pnlTop = new JPanel(new GridBagLayout());
        pnlTop.setBackground(Color.WHITE);
        pnlTop.setBorder(new EmptyBorder(15, 25, 15, 25));
        GridBagConstraints gbc = new GridBagConstraints();

        // Tiêu đề bên trái
        JLabel lblTitle = new JLabel("QUẢN LÝ PHIẾU MƯỢN TRẢ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(mainColor);
        
        gbc.gridx = 0; // Cột 0
        gbc.gridy = 0; // Hàng 0
        gbc.weightx = 1.0; // Chiếm toàn bộ không gian dư thừa để đẩy phần search sang phải
        gbc.anchor = GridBagConstraints.WEST; // Căn trái
        pnlTop.add(lblTitle, gbc);

        // Vùng tìm kiếm bên phải
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlSearch.setOpaque(false);
        
        JLabel lblSearchText = new JLabel("Tìm kiếm:");
        lblSearchText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        txtTimKiem = new JTextField(); 
        txtTimKiem.setPreferredSize(new Dimension(200, 35));
        
        JButton btnTim = createButton("Tìm kiếm", mainColor);
        btnTim.setPreferredSize(new Dimension(100, 35)); 
        
        JButton btnLamMoi = createButton("Làm mới", new Color(46, 125, 50));
        btnLamMoi.setPreferredSize(new Dimension(100, 35));
        
        pnlSearch.add(lblSearchText); 
        pnlSearch.add(txtTimKiem); 
        pnlSearch.add(btnTim); 
        pnlSearch.add(btnLamMoi);
        
        gbc.gridx = 1; // Cột 1
        gbc.weightx = 0; // Không chiếm thêm không gian
        gbc.anchor = GridBagConstraints.EAST; // Căn phải
        pnlTop.add(pnlSearch, gbc);

        add(pnlTop, BorderLayout.NORTH);

        // --- 2. CENTER PANEL (GIỮ NGUYÊN) ---
        JPanel pnlCenter = new JPanel(new BorderLayout());
        pnlCenter.setOpaque(false);
        pnlCenter.setBorder(new EmptyBorder(10, 20, 10, 20));

        String[] cols = {"Mã Phiếu", "Mã ĐG", "Ngày Mượn", "Hẹn Trả", "Ngày Trả", "Số Lượng", "Tình Trạng", "Phí Phạt", "Thủ Thư"};
        model = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int row, int col) { return false; } };
        table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(Color.WHITE); 
        header.setForeground(mainColor);
        header.setPreferredSize(new Dimension(0, 40));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i<cols.length; i++) table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);

        // Renderer màu sắc trạng thái
        table.getColumnModel().getColumn(6).setCellRenderer(new StatusRenderer());
        table.getColumnModel().getColumn(7).setCellRenderer(new StatusRenderer());

        JScrollPane sc = new JScrollPane(table);
        sc.getViewport().setBackground(Color.WHITE);
        pnlCenter.add(sc, BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        // --- 3. BOTTOM PANEL (GIỮ NGUYÊN) ---
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        pnlBot.setBackground(Color.WHITE);

        JButton btnThem = createButton("Tạo Phiếu Mượn", mainColor);
        JButton btnSua = createButton("Cập Nhật Phiếu", new Color(255, 152, 0)); 
        JButton btnXoa = createButton("Xóa Phiếu", new Color(220, 53, 69)); 
        JButton btnXem = createButton("Xem Chi Tiết", new Color(23, 162, 184)); 

        // Gán sự kiện cho các nút Tìm kiếm / Làm mới
        btnTim.addActionListener(e -> xuLyTimKiem());
        txtTimKiem.addActionListener(e -> xuLyTimKiem());
        btnLamMoi.addActionListener(e -> { txtTimKiem.setText(""); loadData(); });

        btnThem.addActionListener(e -> {
            Window parent = SwingUtilities.getWindowAncestor(this);
            String maThuThu = getMaThuThu(parent);
            new GUI_DialogPhieuMuon(parent, maThuThu).setVisible(true);
            loadData();
        });

        btnSua.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu!"); return; }
            DTO_PhieuMuon pm = parseDataFromRow(row);
            Window parent = SwingUtilities.getWindowAncestor(this);
            new GUI_DialogPhieuMuon(parent, pm, getMaThuThu(parent)).setVisible(true);
            loadData();
        });

        pnlBot.add(btnThem); pnlBot.add(btnSua); pnlBot.add(btnXoa); pnlBot.add(btnXem);
        add(pnlBot, BorderLayout.SOUTH);
    }

    // Tách riêng Renderer để code gọn hơn
    private class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String status = (String) table.getModel().getValueAt(row, 6); 
            if (!isSelected && status != null) {
                if (status.equals("Quá hạn")) c.setForeground(Color.RED);
                else if (status.equals("Đã trả")) c.setForeground(new Color(40, 167, 69));
                else c.setForeground(new Color(0, 123, 255));
            }
            setHorizontalAlignment(JLabel.CENTER);
            return c;
        }
    }

    private String getMaThuThu(Window parent) {
        if (parent instanceof GUI_Main) return ((GUI_Main) parent).getTaiKhoan().getUserName();
        return "admin";
    }

    private DTO_PhieuMuon parseDataFromRow(int row) {
        DTO_PhieuMuon pm = new DTO_PhieuMuon();
        pm.setMaPhieuMuon(table.getValueAt(row, 0).toString());
        pm.setMaDocGia(table.getValueAt(row, 1).toString());
        pm.setTinhTrang(table.getValueAt(row, 6).toString());
        if(table.getColumnCount() > 8) pm.setMaThuThu(table.getValueAt(row, 8).toString());
        try {
            if (table.getValueAt(row, 2) != null) pm.setNgayMuon(new java.sql.Date(sdf.parse(table.getValueAt(row, 2).toString()).getTime()));
            if (table.getValueAt(row, 3) != null) pm.setNgayHenTra(new java.sql.Date(sdf.parse(table.getValueAt(row, 3).toString()).getTime()));
        } catch(Exception ex) {}
        return pm;
    }

    public void loadData() {
        model.setRowCount(0);
        ArrayList<DTO_PhieuMuon> list = dal.getList();
        for(DTO_PhieuMuon pm : list) themDongVaoBang(pm);
    }
    
    private void xuLyTimKiem() {
        String key = txtTimKiem.getText().trim();
        if (key.isEmpty()) { loadData(); return; }
        model.setRowCount(0);
        ArrayList<DTO_PhieuMuon> list = dal.timKiem(key);
        for(DTO_PhieuMuon pm : list) themDongVaoBang(pm);
    }
    
    private void themDongVaoBang(DTO_PhieuMuon pm) {
        String ngayTraStr = (pm.getNgayTra() != null) ? sdf.format(pm.getNgayTra()) : "-";
        model.addRow(new Object[]{
            pm.getMaPhieuMuon(), pm.getMaDocGia(), sdf.format(pm.getNgayMuon()), sdf.format(pm.getNgayHenTra()),
            ngayTraStr, pm.getSoLuongSach(), pm.getTinhTrang(), String.format("%,.0f", pm.getTienPhat()), pm.getMaThuThu()
        });
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        return btn;
    }
}




//package MUONTRA;
//
//import HETHONG.GUI_Main;
//
//import javax.swing.*;
//import javax.swing.border.EmptyBorder;
//import javax.swing.table.DefaultTableCellRenderer;
//import javax.swing.table.DefaultTableModel;
//import javax.swing.table.JTableHeader;
//import java.awt.*;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//
//public class GUI_QuanLyMuonTra extends JPanel {
//
//    private Color mainColor = new Color(50, 115, 220);
//    private Color bgColor = new Color(245, 248, 253);
//
//    private JTextField txtTimKiem;
//    private JTable table;
//    private DefaultTableModel model;
//    private DAL_PhieuMuon dal = new DAL_PhieuMuon();
//    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//
//    public GUI_QuanLyMuonTra() {
//        initUI();
//        loadData();
//    }
//
//    private void initUI() {
//        setLayout(new BorderLayout());
//        setBackground(bgColor);
//
//        // --- TOP PANEL ---
//        JPanel pnlTop = new JPanel(new BorderLayout());
//        pnlTop.setBackground(Color.WHITE);
//        pnlTop.setBorder(new EmptyBorder(10, 20, 10, 20));
//
//        JLabel lblTitle = new JLabel("QUẢN LÝ PHIẾU MƯỢN TRẢ");
//        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
//        lblTitle.setForeground(mainColor);
//
//        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//        pnlSearch.setBackground(Color.WHITE);
//        
//        txtTimKiem = new JTextField(); 
//        txtTimKiem.setPreferredSize(new Dimension(250, 35));
//        txtTimKiem.setToolTipText("Nhập Mã phiếu hoặc Mã độc giả...");
//        
//        JButton btnTim = createButton("Tìm kiếm", mainColor);
//        btnTim.setPreferredSize(new Dimension(120, 35)); 
//        
//        JButton btnLamMoi = createButton("Làm mới", new Color(46, 125, 50));
//        btnLamMoi.setPreferredSize(new Dimension(120, 35));
//        
//        // Sự kiện tìm kiếm
//        btnTim.addActionListener(e -> xuLyTimKiem());
//        txtTimKiem.addActionListener(e -> xuLyTimKiem()); 
//        
//        btnLamMoi.addActionListener(e -> {
//            txtTimKiem.setText("");
//            loadData();
//        });
//        
//        pnlSearch.add(new JLabel("Tìm kiếm: ")); 
//        pnlSearch.add(txtTimKiem); 
//        pnlSearch.add(btnTim); 
//        pnlSearch.add(btnLamMoi);
//        
//        pnlTop.add(lblTitle, BorderLayout.WEST); 
//        pnlTop.add(pnlSearch, BorderLayout.EAST);
//        add(pnlTop, BorderLayout.NORTH);
//
//        // --- CENTER PANEL (TABLE) ---
//        JPanel pnlCenter = new JPanel(new BorderLayout());
//        pnlCenter.setBackground(bgColor);
//        pnlCenter.setBorder(new EmptyBorder(10, 20, 10, 20));
//
//        String[] cols = {"Mã Phiếu", "Mã ĐG", "Ngày Mượn", "Hẹn Trả", "Ngày Trả", "Số Lượng", "Tình Trạng", "Phí Phạt", "Thủ Thư"};
//        model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int row, int col) { return false; } };
//        table = new JTable(model);
//        table.setRowHeight(35);
//        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//        
//        JTableHeader header = table.getTableHeader();
//        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
//        header.setBackground(Color.WHITE); header.setForeground(mainColor);
//        header.setPreferredSize(new Dimension(0, 40));
//
//        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
//        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
//        for(int i=0; i<cols.length; i++) table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
//
//        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
//            @Override
//            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//                String status = (String) table.getModel().getValueAt(row, 6); 
//                
//                if (!isSelected) {
//                    if (status != null) {
//                        if (status.equals("Quá hạn")) {
//                            c.setForeground(Color.RED); 
//                            c.setFont(new Font("Segoe UI", Font.BOLD, 14));
//                        }
//                        else if (status.equals("Đã trả")) c.setForeground(new Color(40, 167, 69)); 
//                        else if (status.equals("Chờ duyệt")) c.setForeground(new Color(255, 152, 0)); 
//                        else c.setForeground(new Color(0, 123, 255)); 
//                    }
//                } else {
//                    c.setForeground(Color.WHITE);
//                }
//                setHorizontalAlignment(JLabel.CENTER);
//                return c;
//            }
//        };
//        table.getColumnModel().getColumn(6).setCellRenderer(statusRenderer);
//        table.getColumnModel().getColumn(7).setCellRenderer(statusRenderer);
//
//        JScrollPane sc = new JScrollPane(table);
//        sc.getViewport().setBackground(Color.WHITE);
//        pnlCenter.add(sc, BorderLayout.CENTER);
//        add(pnlCenter, BorderLayout.CENTER);
//
//        // --- BOTTOM PANEL (BUTTONS) ---
//        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
//        pnlBot.setBackground(Color.WHITE);
//
//        JButton btnThem = createButton("Tạo Phiếu Mượn", mainColor);
//        JButton btnSua = createButton("Cập Nhật Phiếu", new Color(255, 152, 0)); 
//        JButton btnXoa = createButton("Xóa Phiếu", new Color(220, 53, 69)); 
//        JButton btnXem = createButton("Xem Chi Tiết", new Color(23, 162, 184)); 
//
//        // --- [ĐÃ SỬA] Nút THÊM: Lấy mã thủ thư ---
//        btnThem.addActionListener(e -> {
//            Window parent = SwingUtilities.getWindowAncestor(this);
//            String maThuThu = "TT01"; // Mặc định nếu lỗi
//            if (parent instanceof GUI_Main) {
//                // Sửa getMaNhanVien() -> getUserName() hoặc getMaThuThu()
//                // Ở đây mình dùng getUserName() vì nó thường có sẵn trong TaiKhoan
//                maThuThu = ((GUI_Main) parent).getTaiKhoan().getUserName(); 
//            }
//            new GUI_DialogPhieuMuon(parent, maThuThu).setVisible(true);
//            loadData();
//        });
//
//        // --- [ĐÃ SỬA] Nút CẬP NHẬT ---
//        btnSua.addActionListener(e -> {
//            int row = table.getSelectedRow();
//            if(row == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu cần xử lý!"); return; }
//
//            DTO_PhieuMuon pm = parseDataFromRow(row);
//            
//            Window parent = SwingUtilities.getWindowAncestor(this);
//            String maThuThu = "TT01";
//            if (parent instanceof GUI_Main) {
//                // Sửa getMaNhanVien() -> getUserName()
//                maThuThu = ((GUI_Main) parent).getTaiKhoan().getUserName();
//            }
//            
//            new GUI_DialogPhieuMuon(parent, pm, maThuThu).setVisible(true);
//            loadData();
//        });
//        
//        // --- Nút XÓA ---
//        btnXoa.addActionListener(e -> {
//            int row = table.getSelectedRow();
//            if(row == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu cần xóa!"); return; }
//            String ma = table.getValueAt(row, 0).toString();
//            if(JOptionPane.showConfirmDialog(this, "Xóa phiếu " + ma + " sẽ hoàn trả lại kho sách.\nBạn chắc chắn chứ?") == JOptionPane.YES_OPTION) {
//                if(dal.delete(ma)) {
//                    JOptionPane.showMessageDialog(this, "Đã xóa!");
//                    loadData();
//                } else JOptionPane.showMessageDialog(this, "Xóa thất bại!");
//            }
//        });
//
//        // --- Nút XEM CHI TIẾT ---
//        btnXem.addActionListener(e -> {
//            int row = table.getSelectedRow();
//            if(row == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu cần xem!"); return; }
//
//            DTO_PhieuMuon pm = parseDataFromRow(row);
//            new GUI_DialogChiTietPhieuMuon(SwingUtilities.getWindowAncestor(this), pm).setVisible(true);
//        });
//
//        pnlBot.add(btnThem); 
//        pnlBot.add(btnSua); 
//        pnlBot.add(btnXoa);
//        pnlBot.add(btnXem);
//
//        add(pnlBot, BorderLayout.SOUTH);
//    }
//
//    private DTO_PhieuMuon parseDataFromRow(int row) {
//        DTO_PhieuMuon pm = new DTO_PhieuMuon();
//        pm.setMaPhieuMuon(table.getValueAt(row, 0).toString());
//        pm.setMaDocGia(table.getValueAt(row, 1).toString());
//        pm.setTinhTrang(table.getValueAt(row, 6).toString());
//        
//        if(table.getColumnCount() > 8) {
//             pm.setMaThuThu(table.getValueAt(row, 8).toString());
//        }
//
//        String tienPhatStr = table.getValueAt(row, 7).toString().replace(",", "").replace(".", "");
//        try { pm.setTienPhat(Double.parseDouble(tienPhatStr)); } catch(Exception ex) { pm.setTienPhat(0); }
//
//        try {
//            if (table.getValueAt(row, 2) != null) pm.setNgayMuon(new java.sql.Date(sdf.parse(table.getValueAt(row, 2).toString()).getTime()));
//            if (table.getValueAt(row, 3) != null) pm.setNgayHenTra(new java.sql.Date(sdf.parse(table.getValueAt(row, 3).toString()).getTime()));
//            
//            String ngayTraStr = table.getValueAt(row, 4).toString();
//            if(!ngayTraStr.equals("-")) pm.setNgayTra(new java.sql.Date(sdf.parse(ngayTraStr).getTime()));
//        } catch(Exception ex) { ex.printStackTrace(); }
//        return pm;
//    }
//
//    public void loadData() {
//        model.setRowCount(0);
//        ArrayList<DTO_PhieuMuon> list = dal.getList();
//        for(DTO_PhieuMuon pm : list) {
//            themDongVaoBang(pm);
//        }
//    }
//    
//    private void xuLyTimKiem() {
//        String key = txtTimKiem.getText().trim();
//        if (key.isEmpty()) {
//            loadData();
//            return;
//        }
//        model.setRowCount(0);
//        ArrayList<DTO_PhieuMuon> list = dal.timKiem(key);
//        for(DTO_PhieuMuon pm : list) {
//            themDongVaoBang(pm);
//        }
//    }
//    
//    private void themDongVaoBang(DTO_PhieuMuon pm) {
//        String ngayTraStr = (pm.getNgayTra() != null) ? sdf.format(pm.getNgayTra()) : "-";
//        String phiPhatStr = String.format("%,.0f", pm.getTienPhat());
//        
//        model.addRow(new Object[]{
//            pm.getMaPhieuMuon(), pm.getMaDocGia(), sdf.format(pm.getNgayMuon()), sdf.format(pm.getNgayHenTra()),
//            ngayTraStr, pm.getSoLuongSach(), pm.getTinhTrang(), phiPhatStr, pm.getMaThuThu()
//        });
//    }
//
//    private JButton createButton(String text, Color bg) {
//        JButton btn = new JButton(text);
//        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
//        btn.setBackground(bg); btn.setForeground(Color.WHITE);
//        btn.setPreferredSize(new Dimension(180, 40));
//        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        return btn;
//    }
//}