package com.qlthuvien.GUI;

import com.qlthuvien.DAL.DAL_ThuThu;
import com.qlthuvien.DTO.DTO_ThuThu;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Date;
import java.awt.event.ActionEvent; // Added import
import java.awt.event.ActionListener; // Added import

public class GUI_DialogNhanVien extends JDialog {

    private GUI_QuanLyNhanVien parentGUI;
    private DTO_ThuThu nhanVienEdit = null;
    private DAL_ThuThu dal = new DAL_ThuThu();

    private JTextField txtMa, txtTen, txtDiaChi, txtSDT, txtUser, txtPass;
    private JSpinner spnNgaySinh;
    private JComboBox<String> cboGioiTinh, cboQuyen;

    public GUI_DialogNhanVien(GUI_QuanLyNhanVien parent, DTO_ThuThu tt) {
        this.parentGUI = parent;
        this.nhanVienEdit = tt;
        initUI();
        if(tt != null) fillData();
        else genMa();
    }

    private void initUI() {
        setTitle(nhanVienEdit == null ? "THÊM NHÂN VIÊN" : "CẬP NHẬT NHÂN VIÊN");
        setSize(800, 550);
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(new BorderLayout());

        // Header
        JLabel lblTitle = new JLabel(getTitle(), SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setOpaque(true);
        lblTitle.setBackground(nhanVienEdit == null ? new Color(50, 115, 220) : new Color(255, 152, 0));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setPreferredSize(new Dimension(0, 50));
        add(lblTitle, BorderLayout.NORTH);

        // Content
        JPanel pnlContent = new JPanel(new GridLayout(1, 2, 15, 0));
        pnlContent.setBorder(new EmptyBorder(15, 15, 15, 15));

        // -- Cột Trái: Thông tin cá nhân --
        JPanel pnlLeft = new JPanel(new GridLayout(6, 2, 10, 15));
        pnlLeft.setBorder(BorderFactory.createTitledBorder("Thông tin cá nhân"));
        
        txtMa = new JTextField(); txtMa.setEditable(false);
        txtTen = new JTextField();
        txtDiaChi = new JTextField();
        txtSDT = new JTextField();
        spnNgaySinh = new JSpinner(new SpinnerDateModel());
        spnNgaySinh.setEditor(new JSpinner.DateEditor(spnNgaySinh, "dd/MM/yyyy"));
        cboGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});

        addComp(pnlLeft, "Mã NV:", txtMa);
        addComp(pnlLeft, "Họ Tên:", txtTen);
        addComp(pnlLeft, "Ngày Sinh:", spnNgaySinh);
        addComp(pnlLeft, "Giới Tính:", cboGioiTinh);
        addComp(pnlLeft, "Địa Chỉ:", txtDiaChi);
        addComp(pnlLeft, "SĐT:", txtSDT);

        // -- Cột Phải: Tài khoản đăng nhập --
        JPanel pnlRight = new JPanel(new GridLayout(6, 2, 10, 15));
        pnlRight.setBorder(BorderFactory.createTitledBorder("Tài khoản hệ thống"));
        
        txtUser = new JTextField();
        txtPass = new JTextField();
        cboQuyen = new JComboBox<>(new String[]{"Thủ Thư (Nhân viên)", "Admin (Quản trị)"});
        
        addComp(pnlRight, "Username:", txtUser);
        addComp(pnlRight, "Password:", txtPass);
        addComp(pnlRight, "Quyền:", cboQuyen);
        
        // Disable username nếu đang sửa (Khóa chính)
        if(nhanVienEdit != null) txtUser.setEditable(false);

        pnlContent.add(pnlLeft);
        pnlContent.add(pnlRight);
        add(pnlContent, BorderLayout.CENTER);

        // Buttons
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        JButton btnLuu = new JButton("Lưu");
        btnLuu.setPreferredSize(new Dimension(120, 40));
        btnLuu.setBackground(nhanVienEdit == null ? new Color(50, 115, 220) : new Color(255, 152, 0));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JButton btnHuy = new JButton("Hủy");
        btnHuy.setPreferredSize(new Dimension(100, 40));

        btnLuu.addActionListener(e -> xuLyLuu());
        btnHuy.addActionListener(e -> dispose());

        pnlBot.add(btnLuu); pnlBot.add(btnHuy);
        add(pnlBot, BorderLayout.SOUTH);

        // --- [NEW] Setup Enter Navigation ---
        setupEnterNavigation();
    }

    // --- [NEW] Method to handle Enter key navigation ---
    private void setupEnterNavigation() {
        // Helper to add Enter listener
        ActionListener nextFocusAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((JComponent)e.getSource()).transferFocus();
            }
        };

        // Chain the fields: Ten -> DiaChi -> SDT -> User -> Pass -> Save
        txtTen.addActionListener(nextFocusAction);
        // Note: Spinners and ComboBoxes handle focus differently or naturally with Tab, 
        // strictly for TextFields ActionListener works best for Enter.
        // We can manually chain the TextFields that are likely to be typed in sequence.
        
        txtDiaChi.addActionListener(nextFocusAction);
        txtSDT.addActionListener(nextFocusAction);
        txtUser.addActionListener(nextFocusAction);
        
        // For the last field (Password), Enter can trigger the Save action
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
        tt.setPhanQuyen(cboQuyen.getSelectedIndex() == 1 ? 1 : 2); // 1:Admin, 2:NV

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
    private void addComp(JPanel p, String l, JComponent c) { p.add(new JLabel(l)); p.add(c); }
}