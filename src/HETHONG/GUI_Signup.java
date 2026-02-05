package HETHONG;

import DOCGIA.DTO_DocGia;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

import CHUNG.EmailService;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class GUI_Signup extends JFrame {

    private JTextField txtUser, txtEmail, txtHoTen, txtSDT, txtDiaChi, txtNgaySinh;
    private JPasswordField txtPass, txtRePass;
    private JComboBox<String> cbxGioiTinh;
    private JButton btnSignup, btnBack;

    private DAL_Signup dal = new DAL_Signup();
    
    // --- PALETTE MÀU ĐỒNG BỘ ---
    private Color colorPrimary = new Color(25, 118, 210); // Xanh dương đậm (Màu chủ đạo)
    private Color colorSecondary = new Color(227, 242, 253); // Xanh nhạt (Dùng cho nền focus)
    private Color colorHover = new Color(21, 101, 192);   // Màu khi di chuột
    private Color colorText = new Color(30, 60, 90);      // Màu chữ label (Xanh đen sang trọng)
    private Color colorBorder = new Color(200, 200, 200); // Màu viền xám nhẹ

    public GUI_Signup() {
        initUI();
    }

    private void initUI() {
        setTitle("Đăng Ký Tài Khoản Độc Giả");
        setSize(540, 720); // Tăng nhẹ kích thước
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        // --- 1. HEADER (MÀU MÈ BẮT MẮT) ---
        JPanel pnlHeader = new JPanel();
        pnlHeader.setBackground(colorPrimary); // Nền xanh chủ đạo
        pnlHeader.setPreferredSize(new Dimension(0, 80));
        pnlHeader.setLayout(new GridBagLayout());
        
        JLabel lblTitle = new JLabel("ĐĂNG KÝ THÀNH VIÊN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE); // Chữ trắng nổi bật
        pnlHeader.add(lblTitle);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. FORM CONTENT ---
        JPanel panelForm = new JPanel(null);
        panelForm.setBackground(Color.WHITE);
        panelForm.setBorder(new EmptyBorder(20, 20, 20, 20));

        int xLeft = 40;
        int xRight = 280;
        int y = 20;
        int w = 220;
        int h = 40;
        int gap = 75;

        // --- CỘT TRÁI ---
        addLabel(panelForm, "Họ và tên:", xLeft, y);
        txtHoTen = addTextField(panelForm, xLeft, y + 25, w, h);
        
        y += gap;
        addLabel(panelForm, "Email (Nhận OTP):", xLeft, y);
        txtEmail = addTextField(panelForm, xLeft, y + 25, w, h);

        y += gap;
        addLabel(panelForm, "Tên đăng nhập:", xLeft, y);
        txtUser = addTextField(panelForm, xLeft, y + 25, w, h);

        y += gap;
        addLabel(panelForm, "Mật khẩu:", xLeft, y);
        txtPass = addPasswordField(panelForm, xLeft, y + 25, w, h);
        
        // --- CỘT PHẢI ---
        int yR = 20;
        
        addLabel(panelForm, "Ngày sinh (dd/MM/yyyy):", xRight, yR);
        txtNgaySinh = addTextField(panelForm, xRight, yR + 25, w, h);
        txtNgaySinh.setToolTipText("Ví dụ: 20/11/2000");

        yR += gap;
        addLabel(panelForm, "Giới tính:", xRight, yR);
        String[] phai = {"Nam", "Nữ", "Khác"};
        cbxGioiTinh = new JComboBox<>(phai);
        cbxGioiTinh.setBounds(xRight, yR + 25, w, h);
        cbxGioiTinh.setBackground(Color.WHITE);
        cbxGioiTinh.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // Style cho ComboBox
        cbxGioiTinh.setBorder(new LineBorder(colorBorder));
        ((JComponent) cbxGioiTinh.getRenderer()).setOpaque(true);
        
        cbxGioiTinh.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) cbxGioiTinh.transferFocus();
            }
        });
        panelForm.add(cbxGioiTinh);

        yR += gap;
        addLabel(panelForm, "Số điện thoại:", xRight, yR);
        txtSDT = addTextField(panelForm, xRight, yR + 25, w, h);

        yR += gap;
        addLabel(panelForm, "Nhập lại mật khẩu:", xRight, yR);
        txtRePass = addPasswordField(panelForm, xRight, yR + 25, w, h);
        
        // --- ĐỊA CHỈ (FULL WIDTH) ---
        int yFull = 320;
        addLabel(panelForm, "Địa chỉ liên hệ:", xLeft, yFull);
        txtDiaChi = addTextField(panelForm, xLeft, yFull + 25, 460, h);

        // --- BUTTONS ---
        int yBtn = 410;
        btnSignup = new JButton("XÁC THỰC & ĐĂNG KÝ");
        btnSignup.setBounds(xLeft, yBtn, 460, 45);
        styleButton(btnSignup, colorPrimary, colorHover);
        btnSignup.addActionListener(e -> xuLyDangKy());
        panelForm.add(btnSignup);
        
        btnBack = new JButton("Quay lại Đăng nhập");
        btnBack.setBounds(xLeft, yBtn + 55, 460, 40);
        // Nút Back màu trắng, viền xanh cho tinh tế
        btnBack.setBackground(Color.WHITE);
        btnBack.setForeground(colorPrimary);
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBack.setFocusPainted(false);
        btnBack.setBorder(new LineBorder(colorPrimary));
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnBack.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { 
                btnBack.setBackground(colorSecondary); 
            }
            public void mouseExited(MouseEvent e) { 
                btnBack.setBackground(Color.WHITE); 
            }
        });
        btnBack.addActionListener(e -> {
            this.dispose();
            new GUI_Login().setVisible(true);
        });
        panelForm.add(btnBack);

        add(panelForm, BorderLayout.CENTER);

        // --- CẤU HÌNH ENTER (Logic giữ nguyên) ---
        setupEnterKey(txtHoTen, false);
        setupEnterKey(txtEmail, false);
        setupEnterKey(txtUser, false);
        setupEnterKey(txtPass, false);
        setupEnterKey(txtNgaySinh, false);
        setupEnterKey(txtSDT, false);
        setupEnterKey(txtRePass, false);
        setupEnterKey(txtDiaChi, true); // Cuối cùng -> Submit
    }

    // --- LOGIC VALIDATE (Đã tối ưu ở bước trước) ---
    private boolean validateForm() {
        String hoTen = txtHoTen.getText().trim();
        String email = txtEmail.getText().trim();
        String sdt = txtSDT.getText().trim();
        String ngaySinhStr = txtNgaySinh.getText().trim();
        String diaChi = txtDiaChi.getText().trim();
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword());
        String rePass = new String(txtRePass.getPassword());

        if (hoTen.isEmpty() || email.isEmpty() || sdt.isEmpty() || user.isEmpty() || diaChi.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng không để trống bất kỳ thông tin nào!");
            return false;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, "Định dạng Email không hợp lệ!");
            return false;
        }
        if (!sdt.matches("^\\d{10,11}$")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải là chữ số và có độ dài từ 10-11 số!");
            return false;
        }
        if (!pass.equals(rePass)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!");
            return false;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            Date ngaySinh = sdf.parse(ngaySinhStr);
            Date ngayHienTai = new Date();
            if (ngaySinh.after(ngayHienTai)) {
                JOptionPane.showMessageDialog(this, "Ngày sinh không hợp lệ!");
                return false;
            }
            long diff = ngayHienTai.getTime() - ngaySinh.getTime();
            long years = diff / (1000L * 60 * 60 * 24 * 365);
            if (years < 5 || years > 100) {
                JOptionPane.showMessageDialog(this, "Ngày sinh không hợp lệ (Phải từ 5-100 tuổi)!");
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng ngày sinh (dd/MM/yyyy)!");
            return false;
        }
        return true;
    }

    // --- LOGIC XỬ LÝ (Giữ nguyên) ---
    private void xuLyDangKy() {
        if (!validateForm()) return;
        
        String check = dal.checkExist(txtUser.getText(), txtEmail.getText());
        if(!"OK".equals(check)) {
            JOptionPane.showMessageDialog(this, check, "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String emailNhan = txtEmail.getText().trim();
        
        btnSignup.setEnabled(false);
        btnSignup.setText("Đang gửi Email...");
        btnSignup.setBackground(Color.GRAY);

        new Thread(() -> {
            String otpServer = EmailService.sendOTP(emailNhan);

            SwingUtilities.invokeLater(() -> {
                btnSignup.setEnabled(true);
                btnSignup.setText("XÁC THỰC & ĐĂNG KÝ");
                btnSignup.setBackground(colorPrimary);

                if (otpServer != null) {
                    String otpUser = JOptionPane.showInputDialog(this, 
                        "Mã xác thực đã gửi đến: " + emailNhan + 
                        "\nVui lòng nhập mã OTP:", 
                        "Xác thực Email", JOptionPane.QUESTION_MESSAGE);

                    if (otpUser != null && otpUser.trim().equals(otpServer)) {
                        thucHienLuuDB();
                    } else {
                        JOptionPane.showMessageDialog(this, "Mã OTP không đúng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể gửi Email!\nKiểm tra mạng hoặc địa chỉ Email.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            });
        }).start();
    }

    private void thucHienLuuDB() {
        try {
            String maDG = "DG" + new Random().nextInt(10000);
            
            DTO_DocGia dg = new DTO_DocGia();
            dg.setMaDocGia(maDG);
            dg.setTenDocGia(txtHoTen.getText());
            dg.setGioiTinh(cbxGioiTinh.getSelectedItem().toString());
            dg.setDiaChi(txtDiaChi.getText());
            dg.setSoDienThoai(txtSDT.getText());
            
            SimpleDateFormat userFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date dateParsed = userFormat.parse(txtNgaySinh.getText());
            dg.setNgaySinh(new java.sql.Date(dateParsed.getTime()));
            dg.setLop("Mới"); 

            DTO_TaiKhoan tk = new DTO_TaiKhoan();
            tk.setUserName(txtUser.getText());
            tk.setPassword(new String(txtPass.getPassword()));
            tk.setEmail(txtEmail.getText());

            if (dal.signup(dg, tk)) {
                JOptionPane.showMessageDialog(this, "Đăng ký thành công! Hãy đăng nhập.");
                this.dispose();
                new GUI_Login().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu vào CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- HELPER METHODS (ĐÃ NÂNG CẤP VISUAL) ---

    private void addLabel(JPanel p, String text, int x, int y) {
        JLabel l = new JLabel(text);
        l.setBounds(x, y, 200, 20);
        // [MỚI] Font đậm hơn và màu xanh đen cho Label
        l.setFont(new Font("Segoe UI", Font.BOLD, 13)); 
        l.setForeground(colorText);
        p.add(l);
    }

    private JTextField addTextField(JPanel p, int x, int y, int w, int h) {
        JTextField t = new JTextField();
        t.setBounds(x, y, w, h);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setBorder(new CompoundBorder(new LineBorder(colorBorder), new EmptyBorder(5, 10, 5, 10)));
        addVisualEffects(t);
        p.add(t);
        return t;
    }

    private JPasswordField addPasswordField(JPanel p, int x, int y, int w, int h) {
        JPasswordField t = new JPasswordField();
        t.setBounds(x, y, w, h);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setBorder(new CompoundBorder(new LineBorder(colorBorder), new EmptyBorder(5, 10, 5, 10)));
        addVisualEffects(t);
        p.add(t);
        return t;
    }

    // [MỚI] Hiệu ứng Visual nâng cao (Đổi nền khi focus)
    private void addVisualEffects(JTextField field) {
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                // Khi click vào: Viền xanh đậm + Nền xanh nhạt
                field.setBorder(new CompoundBorder(new LineBorder(colorPrimary, 1), new EmptyBorder(5, 10, 5, 10)));
                field.setBackground(colorSecondary); 
            }
            @Override
            public void focusLost(FocusEvent e) {
                // Khi thoát ra: Viền xám + Nền trắng
                field.setBorder(new CompoundBorder(new LineBorder(colorBorder, 1), new EmptyBorder(5, 10, 5, 10)));
                field.setBackground(Color.WHITE);
            }
        });
    }

    private void setupEnterKey(JTextField field, boolean isLastField) {
        field.addActionListener(e -> {
            if (isLastField) xuLyDangKy();
            else field.transferFocus();
        });
    }

    private void styleButton(JButton btn, Color bg, Color hover) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
        });
    }
}