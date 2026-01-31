package com.qlthuvien.GUI;

import com.qlthuvien.DTO.DTO_ThuThu;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;

public class GUI_DialogChiTietNhanVien extends JDialog {

    private DTO_ThuThu nhanVien;
    private JTextField txtMa, txtTen, txtDiaChi, txtSDT, txtUser, txtPass, txtNgaySinh, txtGioiTinh, txtQuyen;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public GUI_DialogChiTietNhanVien(Component parent, DTO_ThuThu tt) {
        this.nhanVien = tt;
        initUI();
        fillData();
    }

    private void initUI() {
        setTitle("THÔNG TIN CHI TIẾT NHÂN VIÊN");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(new BorderLayout());

        // HEADER
        JLabel lblTitle = new JLabel("HỒ SƠ NHÂN VIÊN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(23, 162, 184)); // Màu Cyan (Info)
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setPreferredSize(new Dimension(0, 60));
        add(lblTitle, BorderLayout.NORTH);

        // CONTENT
        JPanel pnlContent = new JPanel(new GridLayout(1, 2, 20, 0));
        pnlContent.setBorder(new EmptyBorder(20, 20, 20, 20));

        // -- Cột Trái: Thông tin cá nhân --
        JPanel pnlLeft = new JPanel(new GridLayout(6, 2, 10, 20));
        pnlLeft.setBorder(BorderFactory.createTitledBorder("Thông tin cá nhân"));
        
        txtMa = createReadOnlyField();
        txtTen = createReadOnlyField();
        txtNgaySinh = createReadOnlyField();
        txtGioiTinh = createReadOnlyField();
        txtDiaChi = createReadOnlyField();
        txtSDT = createReadOnlyField();

        addComp(pnlLeft, "Mã NV:", txtMa);
        addComp(pnlLeft, "Họ Tên:", txtTen);
        addComp(pnlLeft, "Ngày Sinh:", txtNgaySinh);
        addComp(pnlLeft, "Giới Tính:", txtGioiTinh);
        addComp(pnlLeft, "Địa Chỉ:", txtDiaChi);
        addComp(pnlLeft, "SĐT:", txtSDT);

        // -- Cột Phải: Tài khoản hệ thống --
        JPanel pnlRight = new JPanel(new GridLayout(6, 2, 10, 20));
        pnlRight.setBorder(BorderFactory.createTitledBorder("Thông tin tài khoản"));
        
        txtUser = createReadOnlyField();
        txtPass = createReadOnlyField();
        txtQuyen = createReadOnlyField();
        
        addComp(pnlRight, "Username:", txtUser);
        addComp(pnlRight, "Password:", txtPass);
        addComp(pnlRight, "Quyền:", txtQuyen);
        
        // Thêm khoảng trống cho đẹp
        pnlRight.add(new JLabel("")); pnlRight.add(new JLabel(""));
        pnlRight.add(new JLabel("")); pnlRight.add(new JLabel(""));
        pnlRight.add(new JLabel("")); pnlRight.add(new JLabel(""));

        pnlContent.add(pnlLeft);
        pnlContent.add(pnlRight);
        add(pnlContent, BorderLayout.CENTER);

        // BOTTOM
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnThoat = new JButton("Đóng");
        btnThoat.setPreferredSize(new Dimension(150, 45));
        btnThoat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThoat.setBackground(new Color(108, 117, 125)); // Màu Xám
        btnThoat.setForeground(Color.WHITE);
        btnThoat.addActionListener(e -> dispose());
        
        pnlBot.add(btnThoat);
        add(pnlBot, BorderLayout.SOUTH);
    }

    private void fillData() {
        if(nhanVien == null) return;
        txtMa.setText(nhanVien.getMaThuThu());
        txtTen.setText(nhanVien.getTenThuThu());
        txtNgaySinh.setText(nhanVien.getNgaySinh() != null ? sdf.format(nhanVien.getNgaySinh()) : "");
        txtGioiTinh.setText(nhanVien.getGioiTinh());
        txtDiaChi.setText(nhanVien.getDiaChi());
        txtSDT.setText(nhanVien.getSoDienThoai());
        
        txtUser.setText(nhanVien.getTenDangNhap());
        txtPass.setText(nhanVien.getMatKhau());
        txtQuyen.setText(nhanVien.getPhanQuyen() == 1 ? "Admin (Quản trị)" : "Thủ thư (Nhân viên)");
    }

    private JTextField createReadOnlyField() {
        JTextField t = new JTextField();
        t.setEditable(false);
        t.setBackground(new Color(245, 248, 250)); // Màu nền xám nhẹ
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return t;
    }

    private void addComp(JPanel p, String l, JComponent c) {
        JLabel lbl = new JLabel(l);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        p.add(lbl); p.add(c);
    }
}