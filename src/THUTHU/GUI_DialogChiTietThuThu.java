package THUTHU;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;

public class GUI_DialogChiTietThuThu extends JDialog {

    private DTO_ThuThu nhanVien;
    private JTextField txtMa, txtTen, txtDiaChi, txtSDT, txtUser, txtPass, txtNgaySinh, txtGioiTinh, txtQuyen;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    
    // Màu chủ đạo đồng bộ
    private Color mainColor = new Color(50, 115, 220);
    private Color bgColor = new Color(245, 248, 253);

    public GUI_DialogChiTietThuThu(Window parent, DTO_ThuThu tt) {
        super(parent, ModalityType.APPLICATION_MODAL);
        this.nhanVien = tt;
        initUI();
        fillData();
    }

    private void initUI() {
        setTitle("HỒ SƠ CHI TIẾT THỦ THƯ");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(bgColor);
        setResizable(false);

        // --- 1. HEADER ---
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlHeader.setBackground(mainColor);
        pnlHeader.setBorder(new EmptyBorder(15, 0, 15, 0));
        
        JLabel lblTitle = new JLabel("HỒ SƠ THỦ THƯ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        pnlHeader.add(lblTitle);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. CONTENT ---
        JPanel pnlContent = new JPanel(new GridLayout(1, 2, 25, 0));
        pnlContent.setBackground(bgColor);
        pnlContent.setBorder(new EmptyBorder(25, 25, 25, 25));

        // -- Cột Trái: Thông tin cá nhân --
        JPanel pnlLeft = createInfoSection("Thông tin cá nhân");
        JPanel innerLeft = (JPanel) pnlLeft.getClientProperty("innerPanel");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMa = createReadOnlyField();
        txtTen = createReadOnlyField();
        txtNgaySinh = createReadOnlyField();
        txtGioiTinh = createReadOnlyField();
        txtDiaChi = createReadOnlyField();
        txtSDT = createReadOnlyField();

        int row = 0;
        addComp(innerLeft, gbc, row++, "Mã Thủ Thư", txtMa);
        addComp(innerLeft, gbc, row++, "Họ Và Tên", txtTen);
        addComp(innerLeft, gbc, row++, "Ngày Sinh", txtNgaySinh);
        addComp(innerLeft, gbc, row++, "Giới Tính", txtGioiTinh);
        addComp(innerLeft, gbc, row++, "Địa Chỉ", txtDiaChi);
        addComp(innerLeft, gbc, row++, "Số Điện Thoại", txtSDT);

        // -- Cột Phải: Tài khoản hệ thống --
        JPanel pnlRight = createInfoSection("Thông tin tài khoản");
        JPanel innerRight = (JPanel) pnlRight.getClientProperty("innerPanel");
        
        txtUser = createReadOnlyField();
        txtPass = createReadOnlyField();
        txtQuyen = createReadOnlyField();
        txtQuyen.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        row = 0;
        addComp(innerRight, gbc, row++, "Tên Đăng Nhập", txtUser);
        addComp(innerRight, gbc, row++, "Mật Khẩu", txtPass);
        addComp(innerRight, gbc, row++, "Quyền Hạn", txtQuyen);

        pnlContent.add(pnlLeft);
        pnlContent.add(pnlRight);
        add(pnlContent, BorderLayout.CENTER);

        // --- 3. BOTTOM ---
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        pnlBot.setBackground(bgColor);
        
        JButton btnThoat = new JButton("Đóng");
        btnThoat.setPreferredSize(new Dimension(150, 42));
        btnThoat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThoat.setBackground(new Color(108, 117, 125));
        btnThoat.setForeground(Color.WHITE);
        btnThoat.setFocusPainted(false);
        btnThoat.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnThoat.putClientProperty("FlatLaf.style", "arc: 10; borderWidth: 0");
        
        btnThoat.addActionListener(e -> dispose());
        
        pnlBot.add(btnThoat);
        add(pnlBot, BorderLayout.SOUTH);
    }

    private JPanel createInfoSection(String title) {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.putClientProperty("FlatLaf.style", "arc: 20; border: 1,1,1,1, #E0E0E0");

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(mainColor);
        lblTitle.setBorder(new EmptyBorder(15, 20, 10, 20));
        
        JPanel innerGrid = new JPanel(new GridBagLayout());
        innerGrid.setOpaque(false);
        innerGrid.setBorder(new EmptyBorder(0, 10, 15, 10));
        
        container.add(lblTitle, BorderLayout.NORTH);
        container.add(innerGrid, BorderLayout.CENTER);
        container.putClientProperty("innerPanel", innerGrid);
        
        return container;
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
        txtPass.setText("********"); // Luôn ẩn pass ở chế độ xem
        
        if (nhanVien.getPhanQuyen() == 1) {
            txtQuyen.setText("Quản trị viên");
            txtQuyen.setForeground(new Color(220, 53, 69)); 
        } else {
            txtQuyen.setText("Thủ thư");
            txtQuyen.setForeground(new Color(40, 167, 69)); 
        }
    }

    private JTextField createReadOnlyField() {
        JTextField t = new JTextField();
        t.setEditable(false);
        t.setBackground(new Color(250, 252, 255));
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setForeground(new Color(60, 60, 60));
        t.putClientProperty("FlatLaf.style", "arc: 8; borderWidth: 1; borderColor: #E0E0E0");
        t.setPreferredSize(new Dimension(200, 38));
        return t;
    }

    private void addComp(JPanel p, GridBagConstraints gbc, int row, String lblText, JComponent comp) {
        gbc.gridy = row;
        gbc.gridx = 0; gbc.weightx = 0.35;
        JLabel lbl = new JLabel(lblText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(100, 100, 100));
        p.add(lbl, gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.65;
        p.add(comp, gbc);
    }
}