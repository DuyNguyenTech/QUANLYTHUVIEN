package NHAPHANG;

import javax.swing.*;
import java.awt.*;

public class GUI_DialogNhaCungCap extends JDialog {
    private JTextField txtMa, txtTen, txtDiaChi, txtSDT;
    private JButton btnLuu;
    private boolean isUpdate = false;
    private DAL_NhaCungCap dal = new DAL_NhaCungCap();

    public GUI_DialogNhaCungCap(JFrame parent, DTO_NhaCungCap ncc) {
        super(parent, "Thông Tin Nhà Cung Cấp", true);
        setSize(400, 320); // Tăng chiều cao xíu cho thoáng
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(5, 2, 10, 10));

        add(new JLabel("  Mã NCC:"));
        txtMa = new JTextField();
        add(txtMa);

        add(new JLabel("  Tên NCC:"));
        txtTen = new JTextField();
        add(txtTen);

        add(new JLabel("  Địa Chỉ:"));
        txtDiaChi = new JTextField();
        add(txtDiaChi);

        add(new JLabel("  Số ĐT:"));
        txtSDT = new JTextField();
        add(txtSDT);

        btnLuu = new JButton("Lưu Thông Tin");
        btnLuu.setBackground(new Color(0, 153, 76));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setFont(new Font("Arial", Font.BOLD, 14));
        
        add(new JLabel("")); // Ô trống giữ chỗ
        add(btnLuu);

        if (ncc != null) { // Chế độ sửa
            isUpdate = true;
            txtMa.setText(ncc.getMaNCC());
            txtMa.setEditable(false); // Mã không được sửa
            txtTen.setText(ncc.getTenNCC());
            txtDiaChi.setText(ncc.getDiaChi());
            txtSDT.setText(ncc.getSdt());
        }

        btnLuu.addActionListener(e -> xuLyLuu());
        setVisible(true);
    }

    private void xuLyLuu() {
        // 1. Kiểm tra rỗng
        if(txtMa.getText().trim().isEmpty() || txtTen.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã và Tên NCC!");
            return;
        }

        // 2. [MỚI] Kiểm tra Số điện thoại phải đúng 10 chữ số
        String sdt = txtSDT.getText().trim();
        // Regex: \\d{10} nghĩa là phải là ký tự số (0-9) và đúng 10 ký tự
        if (!sdt.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ!\nPhải bao gồm đúng 10 chữ số (Ví dụ: 0912345678).");
            return; 
        }
        
        // 3. Gom dữ liệu
        DTO_NhaCungCap ncc = new DTO_NhaCungCap(txtMa.getText().trim(), txtTen.getText().trim(), txtDiaChi.getText().trim(), sdt);
        
        // 4. Gọi DAL xử lý
        boolean kq;
        if (isUpdate) kq = dal.suaNCC(ncc);
        else kq = dal.themNCC(ncc);

        if (kq) {
            JOptionPane.showMessageDialog(this, "Lưu thành công!");
            dispose(); // Đóng cửa sổ
        } else {
            JOptionPane.showMessageDialog(this, "Lưu thất bại! Có thể Mã NCC đã tồn tại.");
        }
    }
}