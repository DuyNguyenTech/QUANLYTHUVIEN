package NHAPHANG;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class GUI_QuanLyNhaCungCap extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JButton btnThem, btnSua, btnXoa;
    private DAL_NhaCungCap dal = new DAL_NhaCungCap();

    public GUI_QuanLyNhaCungCap() {
        setLayout(new BorderLayout());

        // 1. Header
        JPanel pnlHeader = new JPanel();
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        JLabel lblTitle = new JLabel("DANH SÁCH NHÀ CUNG CẤP");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(new Color(0, 102, 204));
        pnlHeader.add(lblTitle);
        add(pnlHeader, BorderLayout.NORTH);

        // 2. Bảng dữ liệu
        String[] cols = {"Mã NCC", "Tên Nhà Cung Cấp", "Địa Chỉ", "SĐT"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(230, 240, 255));
        
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 3. Nút bấm
        JPanel pnlBot = new JPanel();
        pnlBot.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        btnThem = createButton("Thêm Mới", new Color(0, 153, 76));
        btnSua = createButton("Sửa Thông Tin", new Color(255, 153, 51));
        btnXoa = createButton("Xóa NCC", new Color(204, 0, 0));

        pnlBot.add(btnThem);
        pnlBot.add(btnSua);
        pnlBot.add(btnXoa);
        add(pnlBot, BorderLayout.SOUTH);

        // Load dữ liệu ban đầu
        loadData();

        // 4. Sự kiện
        btnThem.addActionListener(e -> {
            new GUI_DialogNhaCungCap(null, null); // Mở form thêm
            loadData(); // Load lại bảng sau khi đóng form
        });

        btnSua.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn NCC cần sửa!");
                return;
            }
            // Lấy dữ liệu từ dòng đã chọn
            DTO_NhaCungCap ncc = new DTO_NhaCungCap(
                table.getValueAt(row, 0).toString(),
                table.getValueAt(row, 1).toString(),
                table.getValueAt(row, 2).toString(),
                table.getValueAt(row, 3).toString()
            );
            new GUI_DialogNhaCungCap(null, ncc); // Mở form sửa
            loadData();
        });

        btnXoa.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn NCC cần xóa!");
                return;
            }
            String maNCC = table.getValueAt(row, 0).toString();
            if (JOptionPane.showConfirmDialog(this, "Bạn chắc chắn muốn xóa NCC này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (dal.xoaNCC(maNCC)) {
                    JOptionPane.showMessageDialog(this, "Đã xóa thành công!");
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại! (Có thể do đã có dữ liệu liên quan)");
                }
            }
        });
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(140, 40));
        return btn;
    }

    private void loadData() {
        model.setRowCount(0);
        ArrayList<DTO_NhaCungCap> list = dal.getAllNCC();
        for (DTO_NhaCungCap ncc : list) {
            model.addRow(new Object[]{ncc.getMaNCC(), ncc.getTenNCC(), ncc.getDiaChi(), ncc.getSdt()});
        }
    }
    
}