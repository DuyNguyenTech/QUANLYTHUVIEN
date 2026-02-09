package THUTHU;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.SimpleDateFormat;

public class GUI_DialogChiTietThuThu extends JDialog {

    private DTO_ThuThu nhanVien;
    private JTextField txtMa, txtTen, txtDiaChi, txtSDT, txtUser, txtPass, txtNgaySinh, txtGioiTinh, txtQuyen;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    
    // Màu chủ đạo
    private Color mainColor = new Color(50, 115, 220);

    public GUI_DialogChiTietThuThu(Component parent, DTO_ThuThu tt) {
        this.nhanVien = tt;
        initUI();
        fillData();
    }

    private void initUI() {
        setTitle("THÔNG TIN CHI TIẾT NHÂN VIÊN");
        setSize(850, 550);
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(new BorderLayout());
        setResizable(false);

        // --- 1. HEADER ---
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlHeader.setBackground(mainColor);
        pnlHeader.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JLabel lblTitle = new JLabel("HỒ SƠ NHÂN VIÊN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        pnlHeader.add(lblTitle);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. CONTENT ---
        JPanel pnlContent = new JPanel(new GridLayout(1, 2, 20, 0));
        pnlContent.setBackground(new Color(245, 248, 253));
        pnlContent.setBorder(new EmptyBorder(20, 20, 20, 20));

        // -- Cột Trái: Thông tin cá nhân --
        JPanel pnlLeft = new JPanel(new GridBagLayout());
        pnlLeft.setBackground(Color.WHITE);
        pnlLeft.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), "Thông tin cá nhân",
            TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
            new Font("Segoe UI", Font.BOLD, 14), mainColor));
        
        txtMa = createReadOnlyField();
        txtTen = createReadOnlyField();
        txtNgaySinh = createReadOnlyField();
        txtGioiTinh = createReadOnlyField();
        txtDiaChi = createReadOnlyField();
        txtSDT = createReadOnlyField();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addComp(pnlLeft, gbc, row++, "Mã NV:", txtMa);
        addComp(pnlLeft, gbc, row++, "Họ Tên:", txtTen);
        addComp(pnlLeft, gbc, row++, "Ngày Sinh:", txtNgaySinh);
        addComp(pnlLeft, gbc, row++, "Giới Tính:", txtGioiTinh);
        addComp(pnlLeft, gbc, row++, "Địa Chỉ:", txtDiaChi);
        addComp(pnlLeft, gbc, row++, "Số Điện Thoại:", txtSDT);
        
        // Đẩy lên trên
        gbc.gridy = row; gbc.weighty = 1.0;
        pnlLeft.add(new JLabel(), gbc);

        // -- Cột Phải: Tài khoản hệ thống --
        JPanel pnlRight = new JPanel(new GridBagLayout());
        pnlRight.setBackground(Color.WHITE);
        pnlRight.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), "Thông tin tài khoản",
            TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
            new Font("Segoe UI", Font.BOLD, 14), mainColor));
        
        txtUser = createReadOnlyField();
        txtPass = createReadOnlyField();
        txtQuyen = createReadOnlyField();
        txtQuyen.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Đậm để nhấn mạnh quyền
        
        row = 0;
        addComp(pnlRight, gbc, row++, "Username:", txtUser);
        addComp(pnlRight, gbc, row++, "Password:", txtPass);
        addComp(pnlRight, gbc, row++, "Quyền Hạn:", txtQuyen);
        
        // Đẩy lên trên
        gbc.gridy = row; gbc.weighty = 1.0;
        pnlRight.add(new JLabel(), gbc);

        pnlContent.add(pnlLeft);
        pnlContent.add(pnlRight);
        add(pnlContent, BorderLayout.CENTER);

        // --- 3. BOTTOM ---
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlBot.setBackground(new Color(245, 248, 253));
        pnlBot.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JButton btnThoat = new JButton("Đóng");
        btnThoat.setPreferredSize(new Dimension(150, 45));
        btnThoat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThoat.setBackground(new Color(108, 117, 125)); // Màu Xám
        btnThoat.setForeground(Color.WHITE);
        btnThoat.setFocusPainted(false);
        btnThoat.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
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
        
        if (nhanVien.getPhanQuyen() == 1) {
            txtQuyen.setText("Admin (Quản trị hệ thống)");
            txtQuyen.setForeground(new Color(220, 53, 69)); // Đỏ
        } else {
            txtQuyen.setText("Thủ thư (Nhân viên)");
            txtQuyen.setForeground(new Color(40, 167, 69)); // Xanh lá
        }
    }

    private JTextField createReadOnlyField() {
        JTextField t = new JTextField();
        t.setEditable(false);
        t.setBackground(new Color(250, 252, 255)); // Màu nền xám rất nhẹ
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        return t;
    }

    private void addComp(JPanel p, GridBagConstraints gbc, int row, String lblText, JComponent comp) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        JLabel lbl = new JLabel(lblText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(80, 80, 80));
        p.add(lbl, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        p.add(comp, gbc);
    }
}