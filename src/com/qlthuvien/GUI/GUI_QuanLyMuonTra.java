package com.qlthuvien.GUI;

import com.qlthuvien.DAL.DAL_PhieuMuon;
import com.qlthuvien.DTO.DTO_PhieuMuon;
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

        // --- TOP PANEL ---
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setBackground(Color.WHITE);
        pnlTop.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel lblTitle = new JLabel("QUẢN LÝ PHIẾU MƯỢN TRẢ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(mainColor);

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlSearch.setBackground(Color.WHITE);
        txtTimKiem = new JTextField(); txtTimKiem.setPreferredSize(new Dimension(250, 35));
        JButton btnTim = createButton("Tìm kiếm", mainColor);
        
        JButton btnLamMoi = createButton("Làm mới", new Color(46, 125, 50));
        btnLamMoi.addActionListener(e -> loadData());
        
        pnlSearch.add(new JLabel("Tìm kiếm: ")); pnlSearch.add(txtTimKiem); pnlSearch.add(btnTim); pnlSearch.add(btnLamMoi);
        pnlTop.add(lblTitle, BorderLayout.WEST); pnlTop.add(pnlSearch, BorderLayout.EAST);
        add(pnlTop, BorderLayout.NORTH);

        // --- CENTER PANEL (TABLE) ---
        JPanel pnlCenter = new JPanel(new BorderLayout());
        pnlCenter.setBackground(bgColor);
        pnlCenter.setBorder(new EmptyBorder(10, 20, 10, 20));

        String[] cols = {"Mã Phiếu", "Mã ĐG", "Ngày Mượn", "Hẹn Trả", "Ngày Trả", "Số Lượng", "Tình Trạng", "Phí Phạt", "Thủ Thư"};
        model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int row, int col) { return false; } };
        table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(Color.WHITE); header.setForeground(mainColor);
        header.setPreferredSize(new Dimension(0, 40));

        // Renderer chung căn giữa
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i<cols.length; i++) table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);

        // Renderer màu sắc cho Tình Trạng và Phí Phạt
        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (String) table.getModel().getValueAt(row, 6); 
                
                if (!isSelected) {
                    if (status != null) {
                        if (status.equals("Quá hạn")) {
                            c.setForeground(Color.RED); 
                            c.setFont(new Font("Segoe UI", Font.BOLD, 14));
                        }
                        else if (status.equals("Đã trả")) c.setForeground(new Color(40, 167, 69)); // Xanh lá
                        else c.setForeground(new Color(0, 123, 255)); // Xanh dương
                    }
                } else {
                    c.setForeground(Color.WHITE);
                }
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        };
        table.getColumnModel().getColumn(6).setCellRenderer(statusRenderer);
        table.getColumnModel().getColumn(7).setCellRenderer(statusRenderer);

        JScrollPane sc = new JScrollPane(table);
        sc.getViewport().setBackground(Color.WHITE);
        pnlCenter.add(sc, BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        // --- BOTTOM PANEL (BUTTONS) ---
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        pnlBot.setBackground(Color.WHITE);

        // 1. Khởi tạo các nút
        JButton btnThem = createButton("Tạo Phiếu Mượn", mainColor);
        JButton btnSua = createButton("Cập Nhật Phiếu Mượn", new Color(255, 152, 0)); // Màu Cam
        JButton btnXoa = createButton("Xóa Phiếu", new Color(220, 53, 69)); // Màu Đỏ
        JButton btnXem = createButton("Xem Chi Tiết", new Color(23, 162, 184)); // Màu Cyan

        // 2. Gán sự kiện cho các nút
        
        // --- Nút THÊM ---
        btnThem.addActionListener(e -> new GUI_DialogPhieuMuon(this).setVisible(true));

        // --- Nút CẬP NHẬT / TRẢ SÁCH ---
        btnSua.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu cần xử lý!"); return; }

            DTO_PhieuMuon pm = parseDataFromRow(row);
            new GUI_DialogPhieuMuon(this, pm).setVisible(true);
        });
        
        // --- Nút XÓA ---
        btnXoa.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu cần xóa!"); return; }
            String ma = table.getValueAt(row, 0).toString();
            if(JOptionPane.showConfirmDialog(this, "Xóa phiếu " + ma + " sẽ hoàn trả lại kho sách.\nBạn chắc chắn chứ?") == JOptionPane.YES_OPTION) {
                if(dal.delete(ma)) {
                    JOptionPane.showMessageDialog(this, "Đã xóa!");
                    loadData();
                } else JOptionPane.showMessageDialog(this, "Xóa thất bại!");
            }
        });

        // --- Nút XEM CHI TIẾT ---
        btnXem.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu cần xem!"); return; }

            DTO_PhieuMuon pm = parseDataFromRow(row);
            new GUI_DialogChiTietPhieuMuon(SwingUtilities.getWindowAncestor(this), pm).setVisible(true);
        });

        // 3. Thêm nút vào Panel theo thứ tự yêu cầu: THÊM -> CẬP NHẬT -> XÓA -> XEM
        pnlBot.add(btnThem); 
        pnlBot.add(btnSua); 
        pnlBot.add(btnXoa);
        pnlBot.add(btnXem);

        add(pnlBot, BorderLayout.SOUTH);
    }

    // Hàm tiện ích để lấy dữ liệu từ dòng chọn -> DTO
    private DTO_PhieuMuon parseDataFromRow(int row) {
        DTO_PhieuMuon pm = new DTO_PhieuMuon();
        pm.setMaPhieuMuon(table.getValueAt(row, 0).toString());
        pm.setMaDocGia(table.getValueAt(row, 1).toString());
        pm.setTinhTrang(table.getValueAt(row, 6).toString());

        String tienPhatStr = table.getValueAt(row, 7).toString().replace(",", "").replace(".", "");
        try { pm.setTienPhat(Double.parseDouble(tienPhatStr)); } catch(Exception ex) { pm.setTienPhat(0); }

        try {
            if (table.getValueAt(row, 2) != null) pm.setNgayMuon(new java.sql.Date(sdf.parse(table.getValueAt(row, 2).toString()).getTime()));
            if (table.getValueAt(row, 3) != null) pm.setNgayHenTra(new java.sql.Date(sdf.parse(table.getValueAt(row, 3).toString()).getTime()));
            
            String ngayTraStr = table.getValueAt(row, 4).toString();
            if(!ngayTraStr.equals("-")) pm.setNgayTra(new java.sql.Date(sdf.parse(ngayTraStr).getTime()));
        } catch(Exception ex) { ex.printStackTrace(); }
        return pm;
    }

    public void loadData() {
        model.setRowCount(0);
        ArrayList<DTO_PhieuMuon> list = dal.getList();
        for(DTO_PhieuMuon pm : list) {
            String ngayTraStr = (pm.getNgayTra() != null) ? sdf.format(pm.getNgayTra()) : "-";
            String phiPhatStr = String.format("%,.0f", pm.getTienPhat());
            
            model.addRow(new Object[]{
                pm.getMaPhieuMuon(), pm.getMaDocGia(), sdf.format(pm.getNgayMuon()), sdf.format(pm.getNgayHenTra()),
                ngayTraStr, pm.getSoLuongSach(), pm.getTinhTrang(), phiPhatStr, pm.getMaThuThu()
            });
        }
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(180, 40));
        return btn;
    }
}