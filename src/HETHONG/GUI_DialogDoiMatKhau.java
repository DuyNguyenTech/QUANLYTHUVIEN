package HETHONG;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class GUI_DialogDoiMatKhau extends JDialog {

    private DTO_TaiKhoan taiKhoan; 
    private JPasswordField txtPassCu, txtPassMoi, txtXacNhan;
    private JButton btnLuu, btnHuy;
    private DAL_TaiKhoan dalTK = new DAL_TaiKhoan(); 

    // Constructor nhận DTO_TaiKhoan để khớp với GUI_Main và GUI_ThongTinCaNhan
    public GUI_DialogDoiMatKhau(Window parent, DTO_TaiKhoan tk) {
        super(parent, "Đổi Mật Khẩu", ModalityType.APPLICATION_MODAL); // Chặn tương tác với cửa sổ cha
        this.taiKhoan = tk;
        initUI();
    }

    private void initUI() {
        setSize(420, 390); // Tăng height xíu cho thoáng
        setLocationRelativeTo(getParent());
        setLayout(null);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);

        // --- 1. HEADER ---
        JLabel lblTitle = new JLabel("ĐỔI MẬT KHẨU", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(50, 115, 220)); // Màu xanh thương hiệu
        lblTitle.setBounds(0, 20, 420, 30);
        add(lblTitle);

        int x = 40, y = 70, w = 320, h = 40;
        int gap = 70;

        // --- 2. INPUTS ---
        
        // Mật khẩu cũ
        addLabel("Mật khẩu hiện tại:", x, y);
        txtPassCu = createPasswordField("Nhập mật khẩu hiện tại...");
        txtPassCu.setBounds(x, y + 25, w, h);
        add(txtPassCu);

        y += gap;
        // Mật khẩu mới
        addLabel("Mật khẩu mới:", x, y);
        txtPassMoi = createPasswordField("Nhập mật khẩu mới...");
        txtPassMoi.setBounds(x, y + 25, w, h);
        add(txtPassMoi);

        y += gap;
        // Xác nhận mật khẩu
        addLabel("Xác nhận mật khẩu mới:", x, y);
        txtXacNhan = createPasswordField("Nhập lại mật khẩu mới...");
        txtXacNhan.setBounds(x, y + 25, w, h);
        add(txtXacNhan);

        // --- 3. BUTTONS ---
        int yBtn = 295;
        
        btnLuu = new JButton("Lưu Thay Đổi");
        btnLuu.setBounds(x, yBtn, 150, 42);
        btnLuu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLuu.setBackground(new Color(40, 167, 69)); // Xanh lá
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLuu.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0");
        btnLuu.addActionListener(this::xuLyDoiMatKhau);
        add(btnLuu);

        btnHuy = new JButton("Hủy Bỏ");
        btnHuy.setBounds(x + 170, yBtn, 150, 42);
        btnHuy.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnHuy.setBackground(new Color(220, 53, 69)); // Đỏ
        btnHuy.setForeground(Color.WHITE);
        btnHuy.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHuy.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0");
        btnHuy.addActionListener(e -> dispose());
        add(btnHuy);
    }

    private void addLabel(String text, int x, int y) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(new Color(80, 80, 80));
        l.setBounds(x, y, 200, 20);
        add(l);
    }

    private JPasswordField createPasswordField(String placeholder) {
        JPasswordField t = new JPasswordField();
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // Style FlatLaf: Bo góc + Nút hiện mật khẩu (Eye button) + Placeholder text
        t.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        t.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderColor: #cccccc; focusedBorderColor: #1877F2; borderWidth: 1; showRevealButton: true");
        return t;
    }

    private void xuLyDoiMatKhau(ActionEvent e) {
        String oldPass = new String(txtPassCu.getPassword());
        String newPass = new String(txtPassMoi.getPassword());
        String confirm = new String(txtXacNhan.getPassword());

        if (oldPass.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!newPass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (newPass.length() < 3) {
            JOptionPane.showMessageDialog(this, "Mật khẩu mới phải có ít nhất 3 ký tự!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- GỌI DAL ĐỂ XỬ LÝ ---
        // Giả sử DAL_TaiKhoan của anh đã có hàm checkLogin(user, pass) để kiểm tra mật khẩu cũ
        // Nếu anh có hàm checkMatKhau thì dùng checkMatKhau nhé. Ở đây em dùng thử checkLogin cho phổ biến.
        boolean dungPassCu = dalTK.checkLogin(taiKhoan.getUserName(), oldPass); 
        
        if (!dungPassCu) {
            JOptionPane.showMessageDialog(this, "Mật khẩu hiện tại không đúng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Gọi hàm đổi mật khẩu
        boolean thanhCong = dalTK.doiMatKhau(taiKhoan.getUserName(), newPass);
        
        if (thanhCong) {
            JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!\nVui lòng đăng nhập lại để tiếp tục sử dụng.");
            this.dispose(); // Đóng form đổi pass
            
            // Đóng tất cả các form hiện tại và mở form Login (Bắt buộc đăng nhập lại)
            for (Window window : Window.getWindows()) {
                window.dispose(); 
            }
            new GUI_Login().setVisible(true);
            
        } else {
            JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi cập nhật mật khẩu!", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
        }
    }
}