package MUONTRA;

import com.formdev.flatlaf.FlatClientProperties;
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

    // Màu chủ đạo (Đồng bộ hệ thống)
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
        setBorder(new EmptyBorder(20, 20, 20, 20)); // Căn lề form thoáng

        // --- 1. HEADER (Title + Search) ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(bgColor);
        pnlHeader.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel lblTitle = new JLabel("QUẢN LÝ PHIẾU MƯỢN TRẢ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(mainColor);

        // Panel Tìm kiếm
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        pnlSearch.setBackground(bgColor);
        
        txtTimKiem = new JTextField(); 
        txtTimKiem.setPreferredSize(new Dimension(320, 42));
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        
        // [STYLE PREMIUM] Bo tròn viên thuốc, DUY NHẤT ô này có chữ mờ
        txtTimKiem.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập mã phiếu hoặc mã độc giả...");
        txtTimKiem.putClientProperty(FlatClientProperties.STYLE, 
            "arc: 999; borderWidth: 1; borderColor: #cccccc; focusedBorderColor: #1877F2; margin: 0, 15, 0, 15");
        
        JButton btnTim = createButton("Tìm kiếm", mainColor, Color.WHITE);
        btnTim.setPreferredSize(new Dimension(120, 42));
        
        JButton btnLamMoi = createButton("Làm mới", new Color(46, 125, 50), Color.WHITE);
        btnLamMoi.setPreferredSize(new Dimension(120, 42));
        
        pnlSearch.add(txtTimKiem); 
        pnlSearch.add(btnTim); 
        pnlSearch.add(btnLamMoi);
        
        pnlHeader.add(lblTitle, BorderLayout.WEST); 
        pnlHeader.add(pnlSearch, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. CENTER (BẢNG DỮ LIỆU TRONG THẺ CARD) ---
        JPanel pnlTableCard = new JPanel(new BorderLayout());
        pnlTableCard.setBackground(Color.WHITE);
        pnlTableCard.putClientProperty(FlatClientProperties.STYLE, "arc: 20; border: 1,1,1,1, #E0E0E0");
        pnlTableCard.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] cols = {"Mã Phiếu", "Mã ĐG", "Ngày Mượn", "Hẹn Trả", "Ngày Trả", "Số Lượng", "Tình Trạng", "Phí Phạt", "Thủ Thư"};
        model = new DefaultTableModel(cols, 0) { 
            @Override public boolean isCellEditable(int row, int col) { return false; } 
        };
        table = new JTable(model);
        
        // [STYLE PREMIUM]
        table.setRowHeight(42);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(232, 242, 252));
        table.setSelectionForeground(Color.BLACK);
        
        // Header Table
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setBackground(new Color(248, 249, 250));
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
        for(int i=0; i<cols.length; i++) table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);

        // Renderer cho cột Tình Trạng (Màu sắc trạng thái)
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (value != null) ? value.toString() : "";
                
                if (!isSelected) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                c.setFont(new Font("Segoe UI", Font.BOLD, 13));

                if (status.contains("Quá hạn") || status.contains("Vi phạm")) {
                    c.setForeground(new Color(220, 53, 69)); // Đỏ
                } else if (status.contains("Đã trả")) {
                    c.setForeground(new Color(40, 167, 69)); // Xanh lá
                } else if (status.contains("Đang mượn") || status.contains("Chờ duyệt")) {
                    c.setForeground(new Color(255, 152, 0)); // Cam
                } else {
                    c.setForeground(Color.BLACK);
                }
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        });

        // Renderer cho cột Phí Phạt (Màu đỏ nếu có tiền phạt)
        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                
                String val = (value != null) ? value.toString() : "0";
                if(!val.equals("0") && !val.equals("-")) {
                    c.setForeground(new Color(180, 0, 0));
                    c.setFont(new Font("Segoe UI", Font.BOLD, 14));
                } else {
                    c.setForeground(Color.GRAY);
                }
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        });

        JScrollPane sc = new JScrollPane(table);
        sc.getViewport().setBackground(Color.WHITE);
        sc.setBorder(BorderFactory.createEmptyBorder());
        pnlTableCard.add(sc, BorderLayout.CENTER);
        add(pnlTableCard, BorderLayout.CENTER);

        // --- 3. BOTTOM (BUTTONS) ---
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 20));
        pnlBot.setBackground(bgColor);

        JButton btnThem = createButton("Tạo Phiếu Mượn", mainColor, Color.WHITE);
        JButton btnSua = createButton("Cập Nhật / Trả", new Color(255, 152, 0), Color.WHITE); 
        JButton btnXem = createButton("Xem Chi Tiết", new Color(23, 162, 184), Color.WHITE); 
        JButton btnXoa = createButton("Xóa Phiếu", new Color(220, 53, 69), Color.WHITE); 

        // --- EVENTS ---
        btnTim.addActionListener(e -> xuLyTimKiem());
        txtTimKiem.addActionListener(e -> xuLyTimKiem()); 
        
        btnLamMoi.addActionListener(e -> {
            txtTimKiem.setText("");
            loadData();
        });

        btnThem.addActionListener(e -> {
            Window parent = SwingUtilities.getWindowAncestor(this);
            String maThuThu = "TT01";
            if (parent instanceof GUI_Main) {
                maThuThu = ((GUI_Main) parent).getTaiKhoan().getUserName(); 
            }
            new GUI_DialogPhieuMuon(parent, maThuThu).setVisible(true);
            loadData();
        });

        btnSua.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu cần xử lý!", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }
            DTO_PhieuMuon pm = parseDataFromRow(row);
            Window parent = SwingUtilities.getWindowAncestor(this);
            String maThuThu = "TT01";
            if (parent instanceof GUI_Main) maThuThu = ((GUI_Main) parent).getTaiKhoan().getUserName();
            new GUI_DialogPhieuMuon(parent, pm, maThuThu).setVisible(true);
            loadData();
        });
        
        btnXoa.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }
            String ma = table.getValueAt(row, 0).toString();
            if(JOptionPane.showConfirmDialog(this, "Xóa phiếu " + ma + " sẽ hoàn trả lại kho sách.\nBạn chắc chắn chứ?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if(dal.delete(ma)) {
                    JOptionPane.showMessageDialog(this, "Đã xóa thành công!");
                    loadData();
                } else JOptionPane.showMessageDialog(this, "Xóa thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnXem.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu cần xem!", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }
            DTO_PhieuMuon pm = parseDataFromRow(row);
            new GUI_DialogChiTietPhieuMuon(SwingUtilities.getWindowAncestor(this), pm).setVisible(true);
        });

        pnlBot.add(btnThem); 
        pnlBot.add(btnSua); 
        pnlBot.add(btnXem);
        pnlBot.add(btnXoa);

        add(pnlBot, BorderLayout.SOUTH);
    }

    private DTO_PhieuMuon parseDataFromRow(int row) {
        DTO_PhieuMuon pm = new DTO_PhieuMuon();
        pm.setMaPhieuMuon(table.getValueAt(row, 0).toString());
        pm.setMaDocGia(table.getValueAt(row, 1).toString());
        pm.setTinhTrang(table.getValueAt(row, 6).toString());
        if(table.getColumnCount() > 8) pm.setMaThuThu(table.getValueAt(row, 8).toString());
        String tiền = table.getValueAt(row, 7).toString().replace(",", "").replace(".", "");
        try { pm.setTienPhat(Double.parseDouble(tiền)); } catch(Exception ex) { pm.setTienPhat(0); }
        try {
            if (table.getValueAt(row, 2) != null) pm.setNgayMuon(new java.sql.Date(sdf.parse(table.getValueAt(row, 2).toString()).getTime()));
            if (table.getValueAt(row, 3) != null) pm.setNgayHenTra(new java.sql.Date(sdf.parse(table.getValueAt(row, 3).toString()).getTime()));
            String ngayTraStr = table.getValueAt(row, 4).toString();
            if(!ngayTraStr.equals("-")) pm.setNgayTra(new java.sql.Date(sdf.parse(ngayTraStr).getTime()));
        } catch(Exception ex) { }
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
        String phiPhatStr = String.format("%,.0f", pm.getTienPhat());
        model.addRow(new Object[]{
            pm.getMaPhieuMuon(), pm.getMaDocGia(), sdf.format(pm.getNgayMuon()), sdf.format(pm.getNgayHenTra()),
            ngayTraStr, pm.getSoLuongSach(), pm.getTinhTrang(), phiPhatStr, pm.getMaThuThu()
        });
    }

    private JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg); 
        btn.setForeground(fg);
        btn.setPreferredSize(new Dimension(160, 42));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0");
        return btn;
    }
}