package HETHONG;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;

public class GUI_Login extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnSignup;
    private JButton btnExit;
    
    private BLL_Login bllLogin = new BLL_Login();
    
    // Bảng màu
    private Color mainColor = new Color(25, 118, 210);      // Xanh dương
    private Color greenColor = new Color(46, 125, 50);      // Xanh lá

    public GUI_Login() {
        initUI();
    }

    private void initUI() {
        setTitle("Hệ Thống Quản Lý Thư Viện");
        setSize(900, 550); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);

        // --- PHẦN 1: PANEL BÊN TRÁI ---
        JPanel panelLeft = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    String imgPath = "/com/qlthuvien/images/login_bg.jpg";
                    URL imgUrl = getClass().getResource(imgPath);
                    
                    if (imgUrl != null) {
                        ImageIcon icon = new ImageIcon(imgUrl);
                        g.drawImage(icon.getImage(), 0, 0, this.getWidth(), this.getHeight(), this);
                    } else {
                        g.setColor(mainColor); 
                        g.fillRect(0, 0, getWidth(), getHeight());
                    }
                } catch (Exception e) { e.printStackTrace(); }
            }
        };
        panelLeft.setBounds(0, 0, 400, 550);
        panelLeft.setLayout(null); 
        add(panelLeft);

        // --- PHẦN 2: PANEL BÊN PHẢI ---
        JPanel panelRight = new JPanel();
        panelRight.setBounds(400, 0, 500, 550);
        panelRight.setBackground(Color.WHITE);
        panelRight.setLayout(null);

        JLabel lblHeader = new JLabel("ĐĂNG NHẬP");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblHeader.setForeground(mainColor);
        lblHeader.setBounds(150, 40, 200, 50);
        panelRight.add(lblHeader);

        // 1. Username
        JLabel lblUser = new JLabel("Tên đăng nhập");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUser.setBounds(80, 110, 150, 30);
        panelRight.add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setBounds(80, 145, 340, 40);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập tài khoản...");
        txtUsername.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderColor: #1976D2; focusWidth: 2");
        txtUsername.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, UIManager.getIcon("Tree.leafIcon")); 
        panelRight.add(txtUsername);

        // 2. Password
        JLabel lblPass = new JLabel("Mật khẩu");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPass.setBounds(80, 200, 150, 30);
        panelRight.add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(80, 235, 340, 40);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập mật khẩu...");
        txtPassword.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderColor: #1976D2; showRevealButton: true"); 
        txtPassword.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, UIManager.getIcon("FileView.hardDriveIcon"));
        panelRight.add(txtPassword);

        // --- CÁC NÚT BẤM ---
        
        // 3. Nút Đăng nhập
        btnLogin = new JButton("Đăng nhập");
        btnLogin.setBounds(80, 310, 340, 45);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLogin.setBackground(mainColor);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.putClientProperty(FlatClientProperties.STYLE, "arc: 999; borderWidth: 0; hoverBackground: #1565C0");
        btnLogin.addActionListener(this::xuLyDangNhap);
        panelRight.add(btnLogin);

        // 4. Nút Đăng ký
        btnSignup = new JButton("Đăng ký tài khoản mới");
        btnSignup.setBounds(80, 370, 340, 45);
        btnSignup.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnSignup.setBackground(greenColor);
        btnSignup.setForeground(Color.WHITE);
        btnSignup.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSignup.putClientProperty(FlatClientProperties.STYLE, "arc: 999; borderWidth: 0; hoverBackground: #1B5E20");
        btnSignup.addActionListener(e -> {
            this.dispose(); 
            new GUI_Signup().setVisible(true);
        });
        panelRight.add(btnSignup);

        // 5. Nút Thoát
        btnExit = new JButton("Thoát");
        btnExit.setBounds(80, 430, 340, 45);
        btnExit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnExit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExit.putClientProperty(FlatClientProperties.STYLE, "arc: 999; background: #FFFFFF; foreground: #555555; borderColor: #CCCCCC; borderWidth: 1; hoverBackground: #F5F5F5; focusWidth: 0");
        btnExit.addActionListener(e -> System.exit(0));
        panelRight.add(btnExit);

        add(panelRight);

        // --- XỬ LÝ SỰ KIỆN ENTER (CẬP NHẬT MỚI) ---
        
        // 1. Tại ô Username: Bấm Enter -> Nhảy xuống ô Password
        txtUsername.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtPassword.requestFocus(); // Chuyển tiêu điểm (focus)
                }
            }
        });

        // 2. Tại ô Password: Bấm Enter -> Thực hiện Đăng nhập
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnLogin.doClick(); // Kích hoạt nút đăng nhập
                }
            }
        });
    }

    private void xuLyDangNhap(ActionEvent e) {
        String user = txtUsername.getText();
        String pass = new String(txtPassword.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DTO_TaiKhoan tk = bllLogin.checkLogin(user, pass);
        
        if (tk != null) {
            new GUI_Main(tk).setVisible(true); 
            this.dispose(); 
        } else {
            Object[] options = {"Đăng ký ngay", "Thử lại"};
            int choice = JOptionPane.showOptionDialog(this,
                    "Tài khoản hoặc mật khẩu không đúng!\nBạn có muốn đăng ký tài khoản mới không?",
                    "Lỗi đăng nhập",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null,
                    options,
                    options[1]);

            if (choice == JOptionPane.YES_OPTION) {
                this.dispose();
                new GUI_Signup().setVisible(true);
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }
        SwingUtilities.invokeLater(() -> new GUI_Login().setVisible(true));
    }
}