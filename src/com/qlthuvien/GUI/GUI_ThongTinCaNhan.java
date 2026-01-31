package com.qlthuvien.GUI;

import com.qlthuvien.DAL.DAL_DocGia;
import com.qlthuvien.DTO.DTO_DocGia;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GUI_ThongTinCaNhan extends JPanel {

    private String maDocGia;
    private String currentUserName; // Biến lưu Tên Đăng Nhập để đổi pass
    private JTextField txtMa, txtTen, txtLop, txtDiaChi, txtSDT;
    private DAL_DocGia dal = new DAL_DocGia();

    // [QUAN TRỌNG] Constructor nhận thêm userName
    public GUI_ThongTinCaNhan(String maDocGia, String userName) {
        this.maDocGia = maDocGia;
        this.currentUserName = userName; // Lưu lại để dùng khi bấm nút đổi pass
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 248, 253));

        // 1. HEADER
        JLabel lblHeader = new JLabel("THÔNG TIN CÁ NHÂN", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblHeader.setForeground(new Color(50, 115, 220));
        lblHeader.setBorder(new EmptyBorder(20, 0, 20, 0));
        add(lblHeader, BorderLayout.NORTH);

        // 2. FORM
        JPanel pnlCenter = new JPanel(new GridBagLayout());
        pnlCenter.setBackground(Color.WHITE);
        pnlCenter.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(10, 50, 50, 50), 
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMa = createField(false);      // Không cho sửa Mã
        txtTen = createField(false);     // Không cho sửa Tên
        txtLop = createField(true);      // Cho sửa Lớp
        txtDiaChi = createField(true);   // Cho sửa Địa chỉ
        txtSDT = createField(true);      // Cho sửa SĐT

        int row = 0;
        addFormRow(pnlCenter, gbc, row++, "Mã Độc Giả:", txtMa);
        addFormRow(pnlCenter, gbc, row++, "Họ và Tên:", txtTen);
        addFormRow(pnlCenter, gbc, row++, "Lớp:", txtLop);
        addFormRow(pnlCenter, gbc, row++, "Địa Chỉ:", txtDiaChi);
        addFormRow(pnlCenter, gbc, row++, "Số Điện Thoại:", txtSDT);

        add(pnlCenter, BorderLayout.CENTER);

        // 3. BUTTONS
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        pnlBot.setBackground(new Color(245, 248, 253));
        pnlBot.setBorder(new EmptyBorder(20, 0, 20, 0));

        // Nút Cập Nhật
        JButton btnSave = new JButton("Cập Nhật Thông Tin");
        btnSave.setPreferredSize(new Dimension(200, 45));
        btnSave.setBackground(new Color(40, 167, 69));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(e -> xuLyCapNhat());
        
        // Nút Đổi Mật Khẩu
        JButton btnChangePass = new JButton("Đổi Mật Khẩu");
        btnChangePass.setPreferredSize(new Dimension(180, 45));
        btnChangePass.setBackground(new Color(255, 193, 7)); 
        btnChangePass.setForeground(Color.BLACK);
        btnChangePass.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnChangePass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // [SỬA LỖI] Truyền currentUserName vào Dialog thay vì maDocGia
        btnChangePass.addActionListener(e -> {
            new GUI_DialogDoiMatKhau(this, currentUserName).setVisible(true);
        });

        pnlBot.add(btnSave);
        pnlBot.add(btnChangePass);

        add(pnlBot, BorderLayout.SOUTH);
    }

    private void loadData() {
        DTO_DocGia dg = dal.getChiTiet(maDocGia);
        if (dg != null) {
            txtMa.setText(dg.getMaDocGia());
            txtTen.setText(dg.getTenDocGia());
            txtLop.setText(dg.getLop());
            txtDiaChi.setText(dg.getDiaChi());
            txtSDT.setText(dg.getSoDienThoai());
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin độc giả!");
        }
    }

    private void xuLyCapNhat() {
        if (txtLop.getText().isEmpty() || txtDiaChi.getText().isEmpty() || txtSDT.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ thông tin!");
            return;
        }

        DTO_DocGia dgMoi = new DTO_DocGia(
            txtMa.getText(),
            txtTen.getText(),
            txtLop.getText(),
            txtDiaChi.getText(),
            txtSDT.getText()
        );

        if (dal.update(dgMoi)) {
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
        }
    }

    private JTextField createField(boolean editable) {
        JTextField t = new JTextField(25);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        t.setEditable(editable);
        if(!editable) t.setBackground(new Color(240, 240, 240));
        return t;
    }

    private void addFormRow(JPanel p, GridBagConstraints gbc, int row, String label, JTextField txt) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        p.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1;
        p.add(txt, gbc);
    }
}