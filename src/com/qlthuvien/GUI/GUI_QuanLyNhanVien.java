package com.qlthuvien.GUI;

import com.qlthuvien.DAL.DAL_ThuThu;
import com.qlthuvien.DTO.DTO_ThuThu;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class GUI_QuanLyNhanVien extends JPanel {

    private Color mainColor = new Color(50, 115, 220);
    private JTable table;
    private DefaultTableModel model;
    private DAL_ThuThu dal = new DAL_ThuThu();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    
    // Khai báo biến tìm kiếm
    private JTextField txtTimKiem;

    public GUI_QuanLyNhanVien() {
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 248, 253));

        // --- 1. HEADER (Tiêu đề + Tìm kiếm) ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Tiêu đề bên trái
        JLabel lblTitle = new JLabel("QUẢN LÝ THỦ THƯ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(mainColor);
        
        // Panel tìm kiếm bên phải
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlSearch.setBackground(Color.WHITE);
        
        txtTimKiem = new JTextField();
        txtTimKiem.setPreferredSize(new Dimension(250, 35));
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JButton btnTim = createBtn("Tìm", mainColor);
        btnTim.setPreferredSize(new Dimension(100, 35));
        
        JButton btnLamMoi = createBtn("Làm mới", new Color(46, 125, 50)); // Màu xanh lá
        btnLamMoi.setPreferredSize(new Dimension(100, 35));

        pnlSearch.add(new JLabel("Tìm kiếm: "));
        pnlSearch.add(txtTimKiem);
        pnlSearch.add(btnTim);
        pnlSearch.add(btnLamMoi);

        pnlHeader.add(lblTitle, BorderLayout.WEST);
        pnlHeader.add(pnlSearch, BorderLayout.EAST);
        
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. TABLE ---
        String[] cols = {"Mã TT", "Họ Tên", "Ngày Sinh", "Giới Tính", "SĐT", "Username", "Password", "Quyền"};
        model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JTableHeader h = table.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 14));
        h.setBackground(Color.WHITE); h.setForeground(mainColor);
        h.setPreferredSize(new Dimension(0, 40));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i<cols.length; i++) table.getColumnModel().getColumn(i).setCellRenderer(center);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- 3. BUTTONS BOTTOM ---
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        pnlBot.setBackground(Color.WHITE);
        
        JButton btnThem = createBtn("Thêm Nhân Viên", mainColor);
        JButton btnSua = createBtn("Sửa Nhân Viên", new Color(255, 152, 0)); // Cam
        JButton btnXoa = createBtn("Xóa Nhân Viên", new Color(220, 53, 69)); // Đỏ
        JButton btnXem = createBtn("Xem Chi Tiết", new Color(23, 162, 184)); // Cyan

        // --- EVENTS ---
        
        // Sự kiện Tìm kiếm
        btnTim.addActionListener(e -> xuLyTimKiem());
        txtTimKiem.addActionListener(e -> xuLyTimKiem()); // Enter cũng tìm

        // Sự kiện Làm mới (Reset bảng)
        btnLamMoi.addActionListener(e -> {
            txtTimKiem.setText("");
            loadData();
        });

        // 1. THÊM
        btnThem.addActionListener(e -> new GUI_DialogNhanVien(this, null).setVisible(true));
        
        // 2. SỬA
        btnSua.addActionListener(e -> {
            int r = table.getSelectedRow();
            if(r == -1) { JOptionPane.showMessageDialog(this, "Chọn nhân viên cần sửa!"); return; }
            String maNV = table.getValueAt(r, 0).toString();
            DTO_ThuThu tt = dal.getDetail(maNV); 
            new GUI_DialogNhanVien(this, tt).setVisible(true);
        });

        // 3. XÓA
        btnXoa.addActionListener(e -> {
            int r = table.getSelectedRow();
            if(r == -1) { JOptionPane.showMessageDialog(this, "Chọn nhân viên cần xóa!"); return; }
            String ma = table.getValueAt(r, 0).toString();
            String ten = table.getValueAt(r, 1).toString();
            
            if(JOptionPane.showConfirmDialog(this, "Xóa nhân viên " + ten + " sẽ xóa luôn tài khoản.\nTiếp tục?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if(dal.delete(ma)) {
                    JOptionPane.showMessageDialog(this, "Đã xóa!");
                    loadData();
                } else JOptionPane.showMessageDialog(this, "Xóa thất bại!");
            }
        });

        // 4. XEM CHI TIẾT
        btnXem.addActionListener(e -> {
            int r = table.getSelectedRow();
            if(r == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xem!"); return; }
            String maNV = table.getValueAt(r, 0).toString();
            DTO_ThuThu tt = dal.getDetail(maNV);
            new GUI_DialogChiTietNhanVien(SwingUtilities.getWindowAncestor(this), tt).setVisible(true);
        });

        pnlBot.add(btnThem); 
        pnlBot.add(btnSua); 
        pnlBot.add(btnXoa);
        pnlBot.add(btnXem);
        
        add(pnlBot, BorderLayout.SOUTH);
    }

    public void loadData() {
        model.setRowCount(0);
        ArrayList<DTO_ThuThu> list = dal.getList();
        for(DTO_ThuThu tt : list) {
            addDataToTable(tt);
        }
    }

    private void xuLyTimKiem() {
        String keyword = txtTimKiem.getText().trim();
        if(keyword.isEmpty()) {
            loadData();
            return;
        }
        
        model.setRowCount(0);
        ArrayList<DTO_ThuThu> list = dal.timKiem(keyword);
        for(DTO_ThuThu tt : list) {
            addDataToTable(tt);
        }
    }

    private void addDataToTable(DTO_ThuThu tt) {
        String role = (tt.getPhanQuyen() == 1) ? "Admin" : "Thủ thư";
        String ns = (tt.getNgaySinh() != null) ? sdf.format(tt.getNgaySinh()) : "";
        model.addRow(new Object[]{
            tt.getMaThuThu(), tt.getTenThuThu(), ns, tt.getGioiTinh(), tt.getSoDienThoai(),
            tt.getTenDangNhap(), tt.getMatKhau(), role
        });
    }

    private JButton createBtn(String t, Color c) {
        JButton b = new JButton(t); b.setFont(new Font("Segoe UI", 1, 14));
        b.setBackground(c); b.setForeground(Color.WHITE); b.setPreferredSize(new Dimension(160, 40));
        return b;
    }
}