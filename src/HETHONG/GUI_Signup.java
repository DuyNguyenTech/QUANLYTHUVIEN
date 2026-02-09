package HETHONG;

import DOCGIA.DTO_DocGia;
import CHUNG.EmailService;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
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
    
    // Màu sắc chủ đạo (Giống GUI_Login)
    private Color brandColor = new Color(24, 119, 242); 
    private Color greenColor = new Color(40, 167, 69); 

    public GUI_Signup() {
        initUI();
    }

    private void initUI() {
        setTitle("Đăng Ký Thành Viên");
        setSize(600, 750); // Form to hơn chút cho thoáng
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        // --- 1. HEADER ---
        JPanel pnlHeader = new JPanel();
        pnlHeader.setBounds(0, 0, 600, 80);
        pnlHeader.setBackground(brandColor);
        pnlHeader.setLayout(null);
        
        JLabel lblTitle = new JLabel("ĐĂNG KÝ THÀNH VIÊN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(0, 20, 600, 40);
        pnlHeader.add(lblTitle);
        add(pnlHeader);

        // --- 2. FORM CONTENT ---
        int xL = 40, xR = 310; // Tọa độ X cột trái và phải
        int y = 110;
        int w = 250, h = 65; // Height bao gồm cả label + textfield
        int gap = 10; // Khoảng cách giữa các dòng

        // --- Hàng 1: Họ tên & Ngày sinh ---
        addLabel("Họ và tên:", xL, y);
        txtHoTen = createTextField("Nhập họ tên đầy đủ...");
        txtHoTen.setBounds(xL, y + 25, w, 40);
        add(txtHoTen);

        addLabel("Ngày sinh (dd/MM/yyyy):", xR, y);
        txtNgaySinh = createTextField("Ví dụ: 20/11/2000");
        txtNgaySinh.setBounds(xR, y + 25, w, 40);
        add(txtNgaySinh);

        y += h + gap;

        // --- Hàng 2: Email & Giới tính ---
        addLabel("Email (Nhận mã OTP):", xL, y);
        txtEmail = createTextField("example@gmail.com");
        txtEmail.setBounds(xL, y + 25, w, 40);
        add(txtEmail);

        addLabel("Giới tính:", xR, y);
        String[] phai = {"Nam", "Nữ"}; // [ĐÚNG YÊU CẦU] Chỉ Nam/Nữ
        cbxGioiTinh = new JComboBox<>(phai);
        cbxGioiTinh.setBounds(xR, y + 25, w, 40);
        cbxGioiTinh.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbxGioiTinh.setBackground(Color.WHITE);
        add(cbxGioiTinh);

        y += h + gap;

        // --- Hàng 3: Tên đăng nhập & SĐT ---
        addLabel("Tên đăng nhập:", xL, y);
        txtUser = createTextField("Username viết liền...");
        txtUser.setBounds(xL, y + 25, w, 40);
        add(txtUser);

        addLabel("Số điện thoại:", xR, y);
        txtSDT = createTextField("Số điện thoại...");
        txtSDT.setBounds(xR, y + 25, w, 40);
        add(txtSDT);

        y += h + gap;

        // --- Hàng 4: Mật khẩu & Nhập lại ---
        addLabel("Mật khẩu:", xL, y);
        txtPass = createPasswordField("Mật khẩu...");
        txtPass.setBounds(xL, y + 25, w, 40);
        add(txtPass);

        addLabel("Nhập lại mật khẩu:", xR, y);
        txtRePass = createPasswordField("Xác nhận mật khẩu...");
        txtRePass.setBounds(xR, y + 25, w, 40);
        add(txtRePass);

        y += h + gap;

        // --- Hàng 5: Địa chỉ (Full width) ---
        addLabel("Địa chỉ liên hệ:", xL, y);
        txtDiaChi = createTextField("Số nhà, đường, phường/xã...");
        txtDiaChi.setBounds(xL, y + 25, 520, 40);
        add(txtDiaChi);

        // --- 3. BUTTONS ---
        int yBtn = 530;
        
        btnSignup = new JButton("XÁC THỰC & ĐĂNG KÝ");
        btnSignup.setBounds(xL, yBtn, 520, 50);
        btnSignup.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnSignup.setBackground(brandColor);
        btnSignup.setForeground(Color.WHITE);
        btnSignup.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Bo tròn nút
        btnSignup.putClientProperty(FlatClientProperties.STYLE, "arc: 999; borderWidth: 0; hoverBackground: #1565C0");
        btnSignup.addActionListener(e -> xuLyDangKy());
        add(btnSignup);

        btnBack = new JButton("Quay lại Đăng nhập");
        btnBack.setBounds(xL, yBtn + 65, 520, 45);
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBack.setBackground(Color.WHITE);
        btnBack.setForeground(brandColor);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Bo tròn nút viền
        btnBack.putClientProperty(FlatClientProperties.STYLE, "arc: 999; borderWidth: 1; borderColor: #1877F2; hoverBackground: #F0F8FF; focusWidth: 0");
        btnBack.addActionListener(e -> {
            this.dispose();
            new GUI_Login().setVisible(true);
        });
        add(btnBack);

        // --- ENTER KEY NAVIGATION ---
        setupEnterKey();
    }

    // --- HELPER UI ---
    private void addLabel(String text, int x, int y) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(new Color(80, 80, 80));
        l.setBounds(x, y, 200, 25);
        add(l);
    }

    private JTextField createTextField(String placeHolder) {
        JTextField t = new JTextField();
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // FlatLaf style - Đã sửa lỗi chính tả 'focusedBorderColor'
        t.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeHolder);
        t.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderColor: #cccccc; focusedBorderColor: #1877F2; borderWidth: 1");
        return t;
    }

    private JPasswordField createPasswordField(String placeHolder) {
        JPasswordField t = new JPasswordField();
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // FlatLaf style - Đã sửa lỗi chính tả 'focusedBorderColor'
        t.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeHolder);
        t.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderColor: #cccccc; focusedBorderColor: #1877F2; borderWidth: 1; showRevealButton: true");
        return t;
    }

    private void setupEnterKey() {
        // Tự động nhảy sang ô tiếp theo khi nhấn Enter
        Component[] order = {txtHoTen, txtNgaySinh, txtEmail, cbxGioiTinh, txtUser, txtSDT, txtPass, txtRePass, txtDiaChi};
        for (int i = 0; i < order.length - 1; i++) {
            Component c = order[i];
            Component next = order[i + 1];
            if (c instanceof JTextField) {
                ((JTextField) c).addActionListener(e -> next.requestFocus());
            } else if (c instanceof JComboBox) {
                // Với ComboBox, Enter không tự kích hoạt ActionListener như TextField
                ((JComboBox) c).addKeyListener(new KeyAdapter() {
                    public void keyPressed(KeyEvent e) { if(e.getKeyCode() == KeyEvent.VK_ENTER) next.requestFocus(); }
                });
            }
        }
        // Ô cuối cùng Enter -> Submit
        txtDiaChi.addActionListener(e -> xuLyDangKy());
    }

    // --- LOGIC XỬ LÝ (GIỮ NGUYÊN) ---
    
    private boolean validateForm() {
        if (txtHoTen.getText().trim().isEmpty() || txtEmail.getText().trim().isEmpty() || 
            txtSDT.getText().trim().isEmpty() || txtUser.getText().trim().isEmpty() || 
            txtDiaChi.getText().trim().isEmpty() || new String(txtPass.getPassword()).isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!txtEmail.getText().trim().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, "Email không hợp lệ!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!new String(txtPass.getPassword()).equals(new String(txtRePass.getPassword()))) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void xuLyDangKy() {
        if (!validateForm()) return;
        
        String check = dal.checkExist(txtUser.getText(), txtEmail.getText());
        if(!"OK".equals(check)) {
            JOptionPane.showMessageDialog(this, check, "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String emailNhan = txtEmail.getText().trim();
        
        btnSignup.setEnabled(false);
        btnSignup.setText("Đang gửi OTP...");
        btnSignup.setBackground(Color.GRAY);

        new Thread(() -> {
            String otpServer = EmailService.sendOTP(emailNhan);

            SwingUtilities.invokeLater(() -> {
                btnSignup.setEnabled(true);
                btnSignup.setText("XÁC THỰC & ĐĂNG KÝ");
                btnSignup.setBackground(brandColor);

                if (otpServer != null) {
                    String otpUser = JOptionPane.showInputDialog(this, 
                        "Mã xác thực đã gửi đến: " + emailNhan + "\nNhập mã OTP:", 
                        "Xác thực Email", JOptionPane.QUESTION_MESSAGE);

                    if (otpUser != null && otpUser.trim().equals(otpServer)) {
                        thucHienLuuDB();
                    } else {
                        JOptionPane.showMessageDialog(this, "Mã OTP sai!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Gửi Email thất bại! Kiểm tra kết nối mạng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
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

            // Sử dụng DTO_TaiKhoan đúng package (thường là HETHONG hoặc DTO chung)
            // Nếu DTO_TaiKhoan nằm trong package HETHONG thì dùng trực tiếp
            DTO_TaiKhoan tk = new DTO_TaiKhoan(); 
            tk.setUserName(txtUser.getText());
            tk.setPassword(new String(txtPass.getPassword()));
            tk.setEmail(txtEmail.getText());

            if (dal.signup(dg, tk)) {
                JOptionPane.showMessageDialog(this, "Đăng ký thành công! Vui lòng đăng nhập.");
                this.dispose();
                new GUI_Login().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi lưu dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi định dạng ngày sinh hoặc dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) { }
        SwingUtilities.invokeLater(() -> new GUI_Signup().setVisible(true));
    }
}