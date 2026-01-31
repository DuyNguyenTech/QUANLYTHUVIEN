package com.qlthuvien.GUI;

import com.qlthuvien.DAL.DAL_Signup;
import com.qlthuvien.DTO.DTO_DocGia; // Import đúng DTO
import com.qlthuvien.DTO.TaiKhoan;
// import com.qlthuvien.UTIL.EmailService; // Bỏ comment nếu bạn đã có class này

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class GUI_Signup extends JFrame {

    private JTextField txtUser, txtEmail, txtHoTen, txtSDT, txtDiaChi, txtNgaySinh;
    private JPasswordField txtPass, txtRePass;
    private JComboBox<String> cbxGioiTinh;
    private JButton btnSignup, btnBack;

    // Gọi trực tiếp DAL thay vì BLL để giảm bớt file
    private DAL_Signup dal = new DAL_Signup();

    public GUI_Signup() {
        initUI();
    }

    private void initUI() {
        setTitle("Đăng Ký Tài Khoản Độc Giả");
        setSize(500, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        // Header
        JLabel lblTitle = new JLabel("ĐĂNG KÝ THÀNH VIÊN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(25, 118, 210));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(lblTitle, BorderLayout.NORTH);

        // Form Panel
        JPanel panelForm = new JPanel(null);
        panelForm.setBackground(Color.WHITE);

        int y = 10;
        addLabel(panelForm, "Họ và tên:", 30, y);
        txtHoTen = addTextField(panelForm, 30, y + 25);
        
        y += 70;
        addLabel(panelForm, "Email (Nhận OTP):", 30, y);
        txtEmail = addTextField(panelForm, 30, y + 25);

        y += 70;
        addLabel(panelForm, "Tên đăng nhập:", 30, y);
        txtUser = addTextField(panelForm, 30, y + 25);

        y += 70;
        addLabel(panelForm, "Mật khẩu:", 30, y);
        txtPass = addPasswordField(panelForm, 30, y + 25);
        
        // --- CỘT BÊN PHẢI ---
        int xRight = 260;
        int yRight = 10;
        
        addLabel(panelForm, "Ngày sinh (dd/MM/yyyy):", xRight, yRight);
        txtNgaySinh = addTextField(panelForm, xRight, yRight + 25);
        txtNgaySinh.setToolTipText("Ví dụ: 20/11/2000");

        yRight += 70;
        addLabel(panelForm, "Giới tính:", xRight, yRight);
        String[] phai = {"Nam", "Nữ", "Khác"};
        cbxGioiTinh = new JComboBox<>(phai);
        cbxGioiTinh.setBounds(xRight, yRight + 25, 200, 35);
        cbxGioiTinh.setBackground(Color.WHITE);
        panelForm.add(cbxGioiTinh);

        yRight += 70;
        addLabel(panelForm, "Số điện thoại:", xRight, yRight);
        txtSDT = addTextField(panelForm, xRight, yRight + 25);

        yRight += 70;
        addLabel(panelForm, "Nhập lại mật khẩu:", xRight, yRight);
        txtRePass = addPasswordField(panelForm, xRight, yRight + 25);
        
        // Địa chỉ (Full width)
        y = 300;
        addLabel(panelForm, "Địa chỉ:", 30, y);
        txtDiaChi = addTextField(panelForm, 30, y + 25);
        txtDiaChi.setSize(430, 35);

        // Nút Đăng ký
        btnSignup = new JButton("XÁC THỰC & ĐĂNG KÝ");
        btnSignup.setBounds(30, 380, 430, 45);
        btnSignup.setBackground(new Color(25, 118, 210));
        btnSignup.setForeground(Color.WHITE);
        btnSignup.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSignup.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSignup.addActionListener(e -> xuLyDangKy());
        panelForm.add(btnSignup);
        
        // Nút Quay lại
        btnBack = new JButton("Quay lại Đăng nhập");
        btnBack.setBounds(30, 440, 430, 35);
        btnBack.setBackground(Color.WHITE);
        btnBack.setForeground(Color.GRAY);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> {
            this.dispose();
            new GUI_Login().setVisible(true);
        });
        panelForm.add(btnBack);

        add(panelForm, BorderLayout.CENTER);
    }

    private boolean validateForm() {
        if (txtUser.getText().isEmpty() || txtHoTen.getText().isEmpty() || 
            txtEmail.getText().isEmpty() || txtSDT.getText().isEmpty() || 
            txtNgaySinh.getText().isEmpty() || txtDiaChi.getText().isEmpty() ||
            txtPass.getPassword().length == 0 || txtRePass.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return false;
        }

        String p1 = new String(txtPass.getPassword());
        String p2 = new String(txtRePass.getPassword());
        if (!p1.equals(p2)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu nhập lại không khớp!");
            return false;
        }
        
        // Validate đơn giản ngày sinh
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            sdf.parse(txtNgaySinh.getText());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ngày sinh sai định dạng (dd/MM/yyyy)!");
            return false;
        }

        return true;
    }

    private void xuLyDangKy() {
        if (!validateForm()) return;
        
        // 1. Check trùng
        String check = dal.checkExist(txtUser.getText(), txtEmail.getText());
        if(!"OK".equals(check)) {
            JOptionPane.showMessageDialog(this, check, "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Gửi OTP (Giả lập để test nhanh, nếu có EmailService thì dùng code cũ)
        String otpServer = String.valueOf(new Random().nextInt(900000) + 100000); // Random 6 số
        String email = txtEmail.getText();
        
        // --- GIẢ LẬP GỬI MAIL (Hiện dialog mã luôn để test) ---
        // Nếu muốn gửi thật: String otpServer = EmailService.sendOTP(email);
        JOptionPane.showMessageDialog(this, "Mã OTP test của bạn là: " + otpServer); 
        
        String otpUser = JOptionPane.showInputDialog(this, 
                "Mã xác thực đã gửi đến " + email + "\nNhập mã OTP:", 
                "Xác thực", JOptionPane.QUESTION_MESSAGE);

        if (otpUser != null && otpUser.equals(otpServer)) {
            thucHienLuuDB();
        } else {
            JOptionPane.showMessageDialog(this, "Mã OTP không đúng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void thucHienLuuDB() {
        try {
            String maDG = "DG" + new Random().nextInt(10000);
            
            // Dùng DTO_DocGia chuẩn
            DTO_DocGia dg = new DTO_DocGia();
            dg.setMaDocGia(maDG);
            dg.setTenDocGia(txtHoTen.getText()); // Sửa setHoTen -> setTenDocGia
            dg.setGioiTinh(cbxGioiTinh.getSelectedItem().toString());
            dg.setDiaChi(txtDiaChi.getText());
            dg.setSoDienThoai(txtSDT.getText()); // Sửa setSdt -> setSoDienThoai
            
            SimpleDateFormat userFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date dateParsed = userFormat.parse(txtNgaySinh.getText());
            dg.setNgaySinh(new java.sql.Date(dateParsed.getTime()));
            dg.setLop("Mới"); // Mặc định lớp

            TaiKhoan tk = new TaiKhoan();
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

    // UI Helpers (Standard Swing)
    private void addLabel(JPanel p, String text, int x, int y) {
        JLabel l = new JLabel(text);
        l.setBounds(x, y, 200, 20);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(l);
    }
    private JTextField addTextField(JPanel p, int x, int y) {
        JTextField t = new JTextField();
        t.setBounds(x, y, 200, 35);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        p.add(t);
        return t;
    }
    private JPasswordField addPasswordField(JPanel p, int x, int y) {
        JPasswordField t = new JPasswordField();
        t.setBounds(x, y, 200, 35);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        p.add(t);
        return t;
    }
}