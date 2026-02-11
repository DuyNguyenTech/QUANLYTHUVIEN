package THUTHU;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.Date;

public class GUI_DialogThuThu extends JDialog {

    private GUI_QuanLyThuThu parentGUI;
    private DTO_ThuThu nhanVienEdit = null;
    private DAL_ThuThu dal = new DAL_ThuThu();

    private JTextField txtMa, txtTen, txtDiaChi, txtSDT, txtUser, txtPass;
    private JSpinner spnNgaySinh;
    private JComboBox<String> cboGioiTinh, cboQuyen;
    
    private Color mainColor = new Color(50, 115, 220);
    private Color bgColor = new Color(245, 248, 253);

    public GUI_DialogThuThu(GUI_QuanLyThuThu parent, DTO_ThuThu tt) {
        super(SwingUtilities.getWindowAncestor(parent), ModalityType.APPLICATION_MODAL);
        this.parentGUI = parent;
        this.nhanVienEdit = tt;
        initUI();
        if(tt != null) fillData();
        else genMa();
    }

    private void initUI() {
        setTitle(nhanVienEdit == null ? "THÊM THỦ THƯ MỚI" : "CẬP NHẬT THÔNG TIN");
        setSize(900, 620);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(bgColor);

        // --- 1. HEADER ---
        Color headerColor = nhanVienEdit == null ? mainColor : new Color(255, 152, 0);
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlHeader.setBackground(headerColor);
        pnlHeader.setBorder(new EmptyBorder(15, 0, 15, 0));
        
        JLabel lblTitle = new JLabel(getTitle());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        pnlHeader.add(lblTitle);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. CONTENT ---
        JPanel pnlContent = new JPanel(new GridLayout(1, 2, 25, 0)); 
        pnlContent.setBorder(new EmptyBorder(25, 25, 25, 25));
        pnlContent.setBackground(bgColor);

        // -- Cột Trái --
        JPanel pnlLeft = createSectionPanel("Thông tin cá nhân", headerColor);
        JPanel innerLeft = (JPanel) pnlLeft.getClientProperty("innerPanel");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMa = createStyledTextField(); txtMa.setEditable(false); 
        txtTen = createStyledTextField();
        txtDiaChi = createStyledTextField();
        txtSDT = createStyledTextField();
        
        spnNgaySinh = new JSpinner(new SpinnerDateModel());
        spnNgaySinh.setEditor(new JSpinner.DateEditor(spnNgaySinh, "dd/MM/yyyy"));
        spnNgaySinh.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        cboGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
        cboGioiTinh.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        int row = 0;
        addComp(innerLeft, gbc, row++, "Mã Thủ Thư", txtMa);
        addComp(innerLeft, gbc, row++, "Họ Và Tên", txtTen);
        addComp(innerLeft, gbc, row++, "Ngày Sinh", spnNgaySinh);
        addComp(innerLeft, gbc, row++, "Giới Tính", cboGioiTinh);
        addComp(innerLeft, gbc, row++, "Địa Chỉ", txtDiaChi);
        addComp(innerLeft, gbc, row++, "Số Điện Thoại", txtSDT);

        // -- Cột Phải --
        JPanel pnlRight = createSectionPanel("Tài khoản hệ thống", headerColor);
        JPanel innerRight = (JPanel) pnlRight.getClientProperty("innerPanel");
        
        txtUser = createStyledTextField();
        txtPass = createStyledTextField();
        cboQuyen = new JComboBox<>(new String[]{"Thủ Thư", "Quản trị viên"});
        cboQuyen.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        if(nhanVienEdit != null) {
            txtUser.setEditable(false);
            txtUser.putClientProperty(FlatClientProperties.STYLE, "background: #F0F0F0");
        }

        row = 0;
        addComp(innerRight, gbc, row++, "Tên Đăng Nhập", txtUser);
        addComp(innerRight, gbc, row++, "Mật Khẩu", txtPass);
        addComp(innerRight, gbc, row++, "Quyền Hạn", cboQuyen);

        pnlContent.add(pnlLeft);
        pnlContent.add(pnlRight);
        add(pnlContent, BorderLayout.CENTER);

        // --- 3. BUTTONS (LƯU TRƯỚC HỦY SAU) ---
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        pnlBot.setBackground(bgColor);
        
        JButton btnLuu = createStyledButton("Lưu Dữ Liệu", headerColor);
        JButton btnHuy = createStyledButton("Hủy Bỏ", new Color(108, 117, 125));

        btnLuu.addActionListener(e -> xuLyLuu());
        btnHuy.addActionListener(e -> dispose());

        pnlBot.add(btnLuu); // Lưu nằm trước
        pnlBot.add(btnHuy); // Hủy nằm sau
        add(pnlBot, BorderLayout.SOUTH);

        setupEnterNavigation();
    }

    private JPanel createSectionPanel(String title, Color themeColor) {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.putClientProperty("FlatLaf.style", "arc: 20; border: 1,1,1,1, #E0E0E0");

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(themeColor);
        lblTitle.setBorder(new EmptyBorder(15, 20, 10, 20));
        
        JPanel innerGridPanel = new JPanel(new GridBagLayout());
        innerGridPanel.setOpaque(false);
        innerGridPanel.setBorder(new EmptyBorder(0, 10, 15, 10));
        
        container.add(lblTitle, BorderLayout.NORTH);
        container.add(innerGridPanel, BorderLayout.CENTER);
        container.putClientProperty("innerPanel", innerGridPanel);
        
        return container;
    }

    private void addComp(JPanel p, GridBagConstraints gbc, int row, String lbl, JComponent comp) {
        gbc.gridy = row;
        gbc.gridx = 0; gbc.weightx = 0.3;
        JLabel l = new JLabel(lbl);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        p.add(l, gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.7;
        p.add(comp, gbc);
    }

    private void xuLyLuu() {
        if(txtTen.getText().isEmpty() || txtUser.getText().isEmpty() || txtPass.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Tên, Username và Password!"); 
            return;
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

        boolean kq = (nhanVienEdit == null) ? dal.add(tt) : dal.update(tt);

        if(kq) {
            JOptionPane.showMessageDialog(this, "Thành công!");
            parentGUI.loadData();
            dispose();
        } else JOptionPane.showMessageDialog(this, "Thất bại!");
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

    private void genMa() { txtMa.setText("TT" + (System.currentTimeMillis()%100000)); }

    private JTextField createStyledTextField() {
        JTextField t = new JTextField();
        t.setPreferredSize(new Dimension(200, 38));
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // FIX: focusedBorderColor
        t.putClientProperty("FlatLaf.style", "arc: 8; borderWidth: 1; focusedBorderColor: #3273DC");
        return t;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setPreferredSize(new Dimension(140, 42));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.putClientProperty("FlatLaf.style", "arc: 10; borderWidth: 0");
        return b;
    }

    private void setupEnterNavigation() {
        ActionListener nextFocus = e -> ((JComponent)e.getSource()).transferFocus();
        txtTen.addActionListener(nextFocus);
        txtDiaChi.addActionListener(nextFocus);
        txtSDT.addActionListener(nextFocus);
        txtUser.addActionListener(nextFocus);
        txtPass.addActionListener(e -> xuLyLuu());
    }
}