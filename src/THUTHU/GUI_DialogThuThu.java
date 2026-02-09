package THUTHU;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;

public class GUI_DialogThuThu extends JDialog {

    private GUI_QuanLyThuThu parentGUI;
    private DTO_ThuThu nhanVienEdit = null;
    private DAL_ThuThu dal = new DAL_ThuThu();

    private JTextField txtMa, txtTen, txtDiaChi, txtSDT, txtUser, txtPass;
    private JSpinner spnNgaySinh;
    private JComboBox<String> cboGioiTinh, cboQuyen;
    
    // Màu chủ đạo
    private Color mainColor = new Color(50, 115, 220);

    public GUI_DialogThuThu(GUI_QuanLyThuThu parent, DTO_ThuThu tt) {
        this.parentGUI = parent;
        this.nhanVienEdit = tt;
        initUI();
        if(tt != null) fillData();
        else genMa();
    }

    private void initUI() {
        setTitle(nhanVienEdit == null ? "THÊM NHÂN VIÊN MỚI" : "CẬP NHẬT THÔNG TIN");
        setSize(850, 580);
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(new BorderLayout());
        setResizable(false);

        // --- 1. HEADER ---
        Color headerColor = nhanVienEdit == null ? mainColor : new Color(255, 152, 0);
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlHeader.setBackground(headerColor);
        pnlHeader.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JLabel lblTitle = new JLabel(getTitle());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        pnlHeader.add(lblTitle);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. CONTENT ---
        JPanel pnlContent = new JPanel(new GridLayout(1, 2, 20, 0)); // Chia 2 cột lớn
        pnlContent.setBorder(new EmptyBorder(20, 20, 20, 20));
        pnlContent.setBackground(Color.WHITE);

        // -- Cột Trái: Thông tin cá nhân --
        JPanel pnlLeft = new JPanel(new GridBagLayout());
        pnlLeft.setBackground(Color.WHITE);
        pnlLeft.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), "Thông tin cá nhân",
            TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
            new Font("Segoe UI", Font.BOLD, 14), headerColor));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMa = createTextField(); txtMa.setEditable(false); txtMa.setBackground(new Color(245, 245, 245));
        txtTen = createTextField();
        txtDiaChi = createTextField();
        txtSDT = createTextField();
        spnNgaySinh = new JSpinner(new SpinnerDateModel());
        spnNgaySinh.setEditor(new JSpinner.DateEditor(spnNgaySinh, "dd/MM/yyyy"));
        spnNgaySinh.setPreferredSize(new Dimension(200, 35));
        spnNgaySinh.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        cboGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
        cboGioiTinh.setPreferredSize(new Dimension(200, 35));
        cboGioiTinh.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        int row = 0;
        addComp(pnlLeft, gbc, row++, "Mã NV:", txtMa);
        addComp(pnlLeft, gbc, row++, "Họ Tên:", txtTen);
        addComp(pnlLeft, gbc, row++, "Ngày Sinh:", spnNgaySinh);
        addComp(pnlLeft, gbc, row++, "Giới Tính:", cboGioiTinh);
        addComp(pnlLeft, gbc, row++, "Địa Chỉ:", txtDiaChi);
        addComp(pnlLeft, gbc, row++, "SĐT:", txtSDT);
        
        // Đẩy lên trên
        gbc.gridy = row; gbc.weighty = 1.0;
        pnlLeft.add(new JLabel(), gbc);

        // -- Cột Phải: Tài khoản hệ thống --
        JPanel pnlRight = new JPanel(new GridBagLayout());
        pnlRight.setBackground(Color.WHITE);
        pnlRight.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), "Tài khoản hệ thống",
            TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
            new Font("Segoe UI", Font.BOLD, 14), headerColor));
        
        txtUser = createTextField();
        txtPass = createTextField();
        cboQuyen = new JComboBox<>(new String[]{"Thủ Thư (Nhân viên)", "Admin (Quản trị)"});
        cboQuyen.setPreferredSize(new Dimension(200, 35));
        cboQuyen.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        if(nhanVienEdit != null) {
            txtUser.setEditable(false);
            txtUser.setBackground(new Color(245, 245, 245));
        }

        row = 0;
        gbc.weighty = 0.0; // Reset weight
        addComp(pnlRight, gbc, row++, "Username:", txtUser);
        addComp(pnlRight, gbc, row++, "Password:", txtPass);
        addComp(pnlRight, gbc, row++, "Quyền Hạn:", cboQuyen);
        
        // Đẩy lên trên
        gbc.gridy = row; gbc.weighty = 1.0;
        pnlRight.add(new JLabel(), gbc);

        pnlContent.add(pnlLeft);
        pnlContent.add(pnlRight);
        add(pnlContent, BorderLayout.CENTER);

        // --- 3. BUTTONS ---
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        pnlBot.setBackground(new Color(245, 248, 253));
        
        JButton btnLuu = createButton("Lưu Lại", headerColor);
        JButton btnHuy = createButton("Hủy Bỏ", new Color(220, 53, 69));

        btnLuu.addActionListener(e -> xuLyLuu());
        btnHuy.addActionListener(e -> dispose());

        pnlBot.add(btnLuu); pnlBot.add(btnHuy);
        add(pnlBot, BorderLayout.SOUTH);

        // Setup Enter
        setupEnterNavigation();
    }

    private void setupEnterNavigation() {
        ActionListener nextFocusAction = e -> ((JComponent)e.getSource()).transferFocus();
        txtTen.addActionListener(nextFocusAction);
        txtDiaChi.addActionListener(nextFocusAction);
        txtSDT.addActionListener(nextFocusAction);
        txtUser.addActionListener(nextFocusAction);
        txtPass.addActionListener(e -> xuLyLuu());
    }

    private void xuLyLuu() {
        if(txtTen.getText().isEmpty() || txtUser.getText().isEmpty() || txtPass.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Tên, Username và Password!"); return;
        }

        DTO_ThuThu tt = new DTO_ThuThu();
        tt.setMaThuThu(txtMa.getText());
        tt.setTenThuThu(txtTen.getText());
        tt.setNgaySinh(new Date(((java.util.Date)spnNgaySinh.getValue()).getTime()));
        tt.setGioiTinh(cboGioiTinh.getSelectedItem().toString());
        tt.setDiaChi(txtDiaChi.getText());
        tt.setSoDienThoai(txtSDT.getText());
        
        tt.setTenDangNhap(txtUser.getText());
        tt.setMatKhau(txtPass.getText());
        tt.setPhanQuyen(cboQuyen.getSelectedIndex() == 1 ? 1 : 2); 

        boolean kq;
        if(nhanVienEdit == null) kq = dal.add(tt);
        else kq = dal.update(tt);

        if(kq) {
            JOptionPane.showMessageDialog(this, "Thành công!");
            parentGUI.loadData();
            dispose();
        } else JOptionPane.showMessageDialog(this, "Thất bại (Có thể trùng Username)!");
    }

    private void fillData() {
        txtMa.setText(nhanVienEdit.getMaThuThu());
        txtTen.setText(nhanVienEdit.getTenThuThu());
        if(nhanVienEdit.getNgaySinh() != null) spnNgaySinh.setValue(nhanVienEdit.getNgaySinh());
        cboGioiTinh.setSelectedItem(nhanVienEdit.getGioiTinh());
        txtDiaChi.setText(nhanVienEdit.getDiaChi());
        txtSDT.setText(nhanVienEdit.getSoDienThoai());
        
        txtUser.setText(nhanVienEdit.getTenDangNhap());
        txtPass.setText(nhanVienEdit.getMatKhau());
        cboQuyen.setSelectedIndex(nhanVienEdit.getPhanQuyen() == 1 ? 1 : 0);
    }

    private void genMa() { txtMa.setText("TT" + System.currentTimeMillis()%10000); }
    
    // --- Helpers ---
    private void addComp(JPanel p, GridBagConstraints gbc, int row, String lbl, JComponent comp) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        JLabel l = new JLabel(lbl);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        p.add(l, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        p.add(comp, gbc);
    }
    
    private JTextField createTextField() {
        JTextField t = new JTextField();
        t.setPreferredSize(new Dimension(200, 35));
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200,200,200)),
            BorderFactory.createEmptyBorder(5,5,5,5)
        ));
        return t;
    }
    
    private JButton createButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setPreferredSize(new Dimension(120, 40));
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setFocusPainted(false); b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}