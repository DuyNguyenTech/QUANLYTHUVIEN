package com.qlthuvien.GUI;

import com.qlthuvien.DAL.DAL_DocGia;
import com.qlthuvien.DTO.DTO_DocGia;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;

public class GUI_QuanLyDocGia extends JPanel {

    private Color mainColor = new Color(50, 115, 220);
    private Color bgColor = new Color(245, 248, 253);

    private JTextField txtTimKiem;
    private JTable table;
    private DefaultTableModel model;
    private DAL_DocGia dal = new DAL_DocGia();

    public GUI_QuanLyDocGia() {
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(bgColor);

        // --- TOP (Title + Search) ---
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setBackground(Color.WHITE);
        pnlTop.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel lblTitle = new JLabel("QUẢN LÝ ĐỘC GIẢ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(mainColor);

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlSearch.setBackground(Color.WHITE);
        
        txtTimKiem = new JTextField();
        txtTimKiem.setPreferredSize(new Dimension(250, 35));
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JButton btnTim = createButton("Tìm kiếm", mainColor);
        btnTim.addActionListener(e -> timKiem());
        
        JButton btnLamMoi = createButton("Làm mới", new Color(46, 125, 50));
        btnLamMoi.addActionListener(e -> { txtTimKiem.setText(""); loadData(); });

        pnlSearch.add(new JLabel("Tìm kiếm: "));
        pnlSearch.add(txtTimKiem);
        pnlSearch.add(btnTim);
        pnlSearch.add(btnLamMoi);

        pnlTop.add(lblTitle, BorderLayout.WEST);
        pnlTop.add(pnlSearch, BorderLayout.EAST);
        add(pnlTop, BorderLayout.NORTH);

        // --- CENTER (Table) ---
        JPanel pnlCenter = new JPanel(new BorderLayout());
        pnlCenter.setBackground(bgColor);
        pnlCenter.setBorder(new EmptyBorder(10, 20, 10, 20));

        String[] cols = {"Mã ĐG", "Tên Độc Giả", "Lớp", "Địa Chỉ", "Số Điện Thoại"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(new Color(230, 230, 230));
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(Color.WHITE);
        header.setForeground(mainColor);
        header.setPreferredSize(new Dimension(0, 40));

        // Căn giữa cột Mã, Lớp, SĐT
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        JScrollPane sc = new JScrollPane(table);
        sc.getViewport().setBackground(Color.WHITE);
        sc.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        pnlCenter.add(sc, BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        // --- BOTTOM (Buttons) ---
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        pnlBot.setBackground(Color.WHITE);
        pnlBot.setBorder(new EmptyBorder(5, 20, 5, 20));

        JButton btnThem = createButton("Thêm Độc Giả", mainColor);
        JButton btnSua = createButton("Sửa Độc Giả", new Color(255, 152, 0));
        JButton btnXoa = createButton("Xóa Độc Giả", new Color(220, 53, 69));

        btnThem.addActionListener(e -> new GUI_DialogThemDocGia(this).setVisible(true));
        
        btnSua.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn độc giả cần sửa!"); return; }
            DTO_DocGia dg = getSelectedData(row);
            new GUI_DialogThemDocGia(this, dg).setVisible(true);
        });

        btnXoa.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn độc giả cần xóa!"); return; }
            String ma = table.getValueAt(row, 0).toString();
            String ten = table.getValueAt(row, 1).toString();
            if(JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa độc giả: " + ten + " (" + ma + ")?") == JOptionPane.YES_OPTION) {
                if(dal.delete(ma)) {
                    JOptionPane.showMessageDialog(this, "Đã xóa thành công!");
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại! (Có thể độc giả đang mượn sách)");
                }
            }
        });

        pnlBot.add(btnThem);
        pnlBot.add(btnSua);
        pnlBot.add(btnXoa);
        add(pnlBot, BorderLayout.SOUTH);
    }

    public void loadData() {
        model.setRowCount(0);
        ArrayList<DTO_DocGia> list = dal.getList();
        for(DTO_DocGia dg : list) {
            model.addRow(new Object[]{dg.getMaDocGia(), dg.getTenDocGia(), dg.getLop(), dg.getDiaChi(), dg.getSoDienThoai()});
        }
    }

    private void timKiem() {
        String key = txtTimKiem.getText().trim();
        if(key.isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa!"); return; }
        model.setRowCount(0);
        ArrayList<DTO_DocGia> list = dal.search(key);
        for(DTO_DocGia dg : list) {
            model.addRow(new Object[]{dg.getMaDocGia(), dg.getTenDocGia(), dg.getLop(), dg.getDiaChi(), dg.getSoDienThoai()});
        }
    }

    private DTO_DocGia getSelectedData(int row) {
        return new DTO_DocGia(
            table.getValueAt(row, 0).toString(),
            table.getValueAt(row, 1).toString(),
            table.getValueAt(row, 2).toString(),
            table.getValueAt(row, 3).toString(),
            table.getValueAt(row, 4).toString()
        );
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(150, 40));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 20, 8, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(bg.darker()); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(bg); }
        });
        return btn;
    }
}