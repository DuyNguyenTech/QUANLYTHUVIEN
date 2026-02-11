package NHAPHANG;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GUI_DialogNhaCungCap extends JDialog {
    
    private JTextField txtMa, txtTen, txtDiaChi, txtSDT;
    private JButton btnLuu, btnHuy;
    private boolean isUpdate = false;
    private DAL_NhaCungCap dal = new DAL_NhaCungCap();

    // Màu chủ đạo Premium
    private Color mainColor = new Color(50, 115, 220); 
    private Color bgColor = new Color(245, 248, 253);

    public GUI_DialogNhaCungCap(Window parent, DTO_NhaCungCap ncc) {
        super(parent, "Thông Tin Nhà Cung Cấp", ModalityType.APPLICATION_MODAL);
        
        if (ncc != null) isUpdate = true;
        initUI();
        
        if (isUpdate) { 
            txtMa.setText(ncc.getMaNCC());
            txtMa.setEditable(false); // Mã không được sửa
            // Đổi màu nền xám mờ báo hiệu không cho sửa
            txtMa.setBackground(new Color(235, 235, 235));
            txtMa.setForeground(Color.GRAY);
            txtMa.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0");
            
            txtTen.setText(ncc.getTenNCC());
            txtDiaChi.setText(ncc.getDiaChi());
            txtSDT.setText(ncc.getSdt());
        }

        setVisible(true); // Hiển thị Form sau khi vẽ xong
    }

    private void initUI() {
        setSize(500, 480); // Nới rộng kích thước cho thoáng
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        setResizable(false);
        getContentPane().setBackground(bgColor);

        // --- 1. HEADER TITLE ---
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlHeader.setBackground(mainColor);
        pnlHeader.setBorder(new EmptyBorder(15, 0, 15, 0));
        
        JLabel lblHeader = new JLabel(isUpdate ? "CẬP NHẬT NHÀ CUNG CẤP" : "THÊM NHÀ CUNG CẤP MỚI");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(Color.WHITE);
        pnlHeader.add(lblHeader);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. CONTENT (FORM NHẬP LIỆU) ---
        JPanel pnlContent = new JPanel(new BorderLayout());
        pnlContent.setBackground(bgColor);
        pnlContent.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Bọc Form trong thẻ Card trắng bo góc
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(Color.WHITE);
        pnlForm.putClientProperty(FlatClientProperties.STYLE, "arc: 20; border: 1,1,1,1, #E0E0E0");
        pnlForm.setBorder(new EmptyBorder(20, 15, 20, 15));

        // [ĐÃ SỬA] Xóa sạch tham số placeholder chữ mờ
        txtMa = createTextField();
        txtTen = createTextField();
        txtDiaChi = createTextField();
        txtSDT = createTextField();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 10, 12, 10); // Khoảng cách các dòng
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Dòng 1: Mã NCC
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        pnlForm.add(createLabel("Mã NCC:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        pnlForm.add(txtMa, gbc);

        // Dòng 2: Tên NCC
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        pnlForm.add(createLabel("Tên NCC:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        pnlForm.add(txtTen, gbc);

        // Dòng 3: Địa Chỉ
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        pnlForm.add(createLabel("Địa Chỉ:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        pnlForm.add(txtDiaChi, gbc);

        // Dòng 4: SĐT
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        pnlForm.add(createLabel("Số ĐT:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1.0;
        pnlForm.add(txtSDT, gbc);

        pnlContent.add(pnlForm, BorderLayout.CENTER);
        add(pnlContent, BorderLayout.CENTER);

        // --- 3. FOOTER (BUTTONS) ---
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        pnlFooter.setBackground(bgColor);
        pnlFooter.setBorder(new EmptyBorder(0, 20, 10, 20));

        btnLuu = createButton("Lưu Thông Tin", new Color(40, 167, 69)); // Xanh lá
        btnHuy = createButton("Hủy Bỏ", new Color(220, 53, 69)); // Đỏ

        pnlFooter.add(btnLuu);
        pnlFooter.add(btnHuy);
        add(pnlFooter, BorderLayout.SOUTH);

        // --- 4. EVENTS ---
        btnLuu.addActionListener(e -> xuLyLuu());
        btnHuy.addActionListener(e -> dispose());
    }

    // --- HELPER UI METHODS ---
    // [ĐÃ SỬA] Hàm tạo TextField không còn thuộc tính PLACEHOLDER_TEXT
    private JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setPreferredSize(new Dimension(250, 42)); // Ô nhập liệu cao thoáng
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tf.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderColor: #cccccc; focusedBorderColor: #1877F2; borderWidth: 1");
        return tf;
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(new Color(80, 80, 80)); 
        return lbl;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(140, 42)); 
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0"); // Bo góc nút
        return btn;
    }

    // --- LOGIC ---
    private void xuLyLuu() {
        // 1. Kiểm tra rỗng
        if(txtMa.getText().trim().isEmpty() || txtTen.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã và Tên Nhà Cung Cấp!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Kiểm tra Số điện thoại phải đúng 10 chữ số
        String sdt = txtSDT.getText().trim();
        if (!sdt.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ!\nPhải bao gồm đúng 10 chữ số (Ví dụ: 0912345678).", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            return; 
        }
        
        // 3. Gom dữ liệu
        DTO_NhaCungCap ncc = new DTO_NhaCungCap(txtMa.getText().trim(), txtTen.getText().trim(), txtDiaChi.getText().trim(), sdt);
        
        // 4. Gọi DAL xử lý
        boolean kq;
        if (isUpdate) kq = dal.suaNCC(ncc);
        else kq = dal.themNCC(ncc);

        if (kq) {
            JOptionPane.showMessageDialog(this, "Lưu thông tin thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Đóng cửa sổ
        } else {
            JOptionPane.showMessageDialog(this, "Lưu thất bại! Có thể Mã Nhà Cung Cấp đã tồn tại.", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
        }
    }
}