package com.qlthuvien.GUI;

import com.qlthuvien.DAL.DAL_TheLoai;
import com.qlthuvien.DTO.DTO_TheLoai;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class GUI_QuanLyTheLoai extends JPanel {

    // Màu sắc chủ đạo (đồng bộ với module Sách)
    private Color mainColor = new Color(50, 115, 220);
    private Color bgColor = new Color(245, 248, 253);

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtMa, txtTen;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi;
    
    private DAL_TheLoai dal = new DAL_TheLoai();

    public GUI_QuanLyTheLoai() {
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(bgColor);

        // --- 1. HEADER (Tiêu đề) ---
        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTop.setBackground(Color.WHITE);
        pnlTop.setBorder(new EmptyBorder(10, 20, 10, 20));
        JLabel lblTitle = new JLabel("QUẢN LÝ THỂ LOẠI SÁCH");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(mainColor);
        pnlTop.add(lblTitle);
        add(pnlTop, BorderLayout.NORTH);

        // --- 2. CENTER (Bảng dữ liệu) ---
        JPanel pnlCenter = new JPanel(new BorderLayout());
        pnlCenter.setBackground(bgColor);
        pnlCenter.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Setup Bảng
        String[] cols = {"Mã Thể Loại", "Tên Thể Loại"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(new Color(230, 230, 230));

        // Header Bảng
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(Color.WHITE);
        header.setForeground(mainColor);
        header.setPreferredSize(new Dimension(0, 40));

        // Căn giữa cột Mã
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        pnlCenter.add(scrollPane, BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        // --- 3. BOTTOM (Form nhập liệu + Nút) ---
        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setBackground(Color.WHITE);
        pnlBottom.setBorder(new EmptyBorder(20, 20, 20, 20));
        pnlBottom.setPreferredSize(new Dimension(0, 180));

        // 3a. Khu vực nhập liệu (TitledBorder đẹp)
        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setBackground(Color.WHITE);
        pnlInput.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), 
                "Thông tin chi tiết", 
                TitledBorder.DEFAULT_JUSTIFICATION, 
                TitledBorder.DEFAULT_POSITION, 
                new Font("Segoe UI", Font.BOLD, 14), 
                mainColor
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Mã thể loại
        gbc.gridx = 0; gbc.gridy = 0;
        pnlInput.add(new JLabel("Mã thể loại:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.3;
        txtMa = new JTextField();
        txtMa.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtMa.setPreferredSize(new Dimension(200, 30));
        pnlInput.add(txtMa, gbc);

        // Tên thể loại
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0;
        pnlInput.add(new JLabel("Tên thể loại:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0.7;
        txtTen = new JTextField();
        txtTen.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTen.setPreferredSize(new Dimension(200, 30));
        pnlInput.add(txtTen, gbc);

        pnlBottom.add(pnlInput, BorderLayout.CENTER);

        // 3b. Khu vực nút bấm (Dưới cùng)
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        pnlButtons.setBackground(Color.WHITE);

        btnThem = createButton("Thêm Mới", mainColor);
        btnSua = createButton("Cập Nhật", new Color(255, 152, 0)); // Cam
        btnXoa = createButton("Xóa Bỏ", new Color(220, 53, 69));   // Đỏ
        btnLamMoi = createButton("Làm Mới", new Color(40, 167, 69)); // Xanh lá

        pnlButtons.add(btnThem);
        pnlButtons.add(btnSua);
        pnlButtons.add(btnXoa);
        pnlButtons.add(btnLamMoi);

        pnlBottom.add(pnlButtons, BorderLayout.SOUTH);
        add(pnlBottom, BorderLayout.SOUTH);

        // --- EVENTS ---
        
        // Click bảng -> Đổ dữ liệu lên textfield
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtMa.setText(table.getValueAt(row, 0).toString());
                    txtTen.setText(table.getValueAt(row, 1).toString());
                    txtMa.setEditable(false); // Khóa mã không cho sửa
                    txtMa.setBackground(new Color(245, 245, 245));
                }
            }
        });

        // Nút Thêm
        btnThem.addActionListener(e -> {
            if (validateInput()) {
                if (dal.checkExist(txtMa.getText())) {
                    JOptionPane.showMessageDialog(this, "Mã thể loại đã tồn tại!");
                    return;
                }
                DTO_TheLoai tl = new DTO_TheLoai(txtMa.getText(), txtTen.getText());
                if (dal.add(tl)) {
                    JOptionPane.showMessageDialog(this, "Thêm thành công!");
                    loadData();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm thất bại!");
                }
            }
        });

        // Nút Sửa
        btnSua.addActionListener(e -> {
            if (table.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn thể loại cần sửa!");
                return;
            }
            if (validateInput()) {
                DTO_TheLoai tl = new DTO_TheLoai(txtMa.getText(), txtTen.getText());
                if (dal.update(tl)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                    loadData();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
                }
            }
        });

        // Nút Xóa
        btnXoa.addActionListener(e -> {
            if (table.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn thể loại cần xóa!");
                return;
            }
            String ma = txtMa.getText();
            if (JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn xóa thể loại: " + ma + "?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (dal.delete(ma)) {
                    JOptionPane.showMessageDialog(this, "Đã xóa!");
                    loadData();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại! (Có thể sách đang dùng thể loại này)");
                }
            }
        });

        // Nút Làm mới
        btnLamMoi.addActionListener(e -> clearForm());
    }

    private void loadData() {
        model.setRowCount(0);
        ArrayList<DTO_TheLoai> list = dal.getList();
        for (DTO_TheLoai tl : list) {
            model.addRow(new Object[]{tl.getMaTheLoai(), tl.getTenTheLoai()});
        }
    }

    private void clearForm() {
        txtMa.setText("");
        txtTen.setText("");
        txtMa.setEditable(true);
        txtMa.setBackground(Color.WHITE);
        table.clearSelection();
        txtMa.requestFocus();
    }

    private boolean validateInput() {
        if (txtMa.getText().trim().isEmpty() || txtTen.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Mã và Tên thể loại!");
            return false;
        }
        return true;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(120, 35));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}