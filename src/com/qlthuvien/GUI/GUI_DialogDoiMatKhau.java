package com.qlthuvien.GUI;

import com.qlthuvien.DAL.DAL_TaiKhoan;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GUI_DialogDoiMatKhau extends JDialog {

    private String username;
    private JPasswordField txtPassCu, txtPassMoi, txtXacNhan;
    private DAL_TaiKhoan dalTK = new DAL_TaiKhoan();

    public GUI_DialogDoiMatKhau(Component parent, String username) {
        super(SwingUtilities.getWindowAncestor(parent), ModalityType.APPLICATION_MODAL);
        this.username = username;
        initUI();
    }

    private void initUI() {
        setTitle("ĐỔI MẬT KHẨU");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel lblHeader = new JLabel("ĐỔI MẬT KHẨU", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeader.setForeground(new Color(50, 115, 220));
        lblHeader.setBorder(new EmptyBorder(15, 0, 15, 0));
        add(lblHeader, BorderLayout.NORTH);

        JPanel pnlCenter = new JPanel(new GridLayout(3, 1, 10, 10));
        pnlCenter.setBorder(new EmptyBorder(10, 30, 10, 30));
        pnlCenter.setBackground(Color.WHITE);

        txtPassCu = new JPasswordField();
        txtPassMoi = new JPasswordField();
        txtXacNhan = new JPasswordField();

        pnlCenter.add(createPanel("Mật khẩu cũ:", txtPassCu));
        pnlCenter.add(createPanel("Mật khẩu mới:", txtPassMoi));
        pnlCenter.add(createPanel("Xác nhận:", txtXacNhan));
        add(pnlCenter, BorderLayout.CENTER);

        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlBot.setBorder(new EmptyBorder(10, 0, 20, 0));

        JButton btnLuu = new JButton("Lưu Thay Đổi");
        btnLuu.setBackground(new Color(40, 167, 69));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.addActionListener(e -> xuLyDoiMatKhau());

        JButton btnHuy = new JButton("Hủy");
        btnHuy.setBackground(new Color(220, 53, 69));
        btnHuy.setForeground(Color.WHITE);
        btnHuy.addActionListener(e -> dispose());

        pnlBot.add(btnLuu);
        pnlBot.add(btnHuy);
        add(pnlBot, BorderLayout.SOUTH);
    }

    private void xuLyDoiMatKhau() {
        String oldPass = new String(txtPassCu.getPassword());
        String newPass = new String(txtPassMoi.getPassword());
        String confirm = new String(txtXacNhan.getPassword());

        if(oldPass.isEmpty() || newPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ thông tin!");
            return;
        }
        if(!newPass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!");
            return;
        }
        if(!dalTK.checkMatKhau(username, oldPass)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu cũ không đúng!");
            return;
        }
        if(dalTK.doiMatKhau(username, newPass)) {
            JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Có lỗi xảy ra!");
        }
    }

    private JPanel createPanel(String title, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBackground(Color.WHITE);
        p.add(new JLabel(title), BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }
}