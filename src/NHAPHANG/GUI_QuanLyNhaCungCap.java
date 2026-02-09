package NHAPHANG;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;

public class GUI_QuanLyNhaCungCap extends JPanel {
    
    // Màu chủ đạo
    private Color mainColor = new Color(50, 115, 220); 
    private Color bgColor = new Color(245, 248, 253);

    private JTable table;
    private DefaultTableModel model;
    private JButton btnThem, btnSua, btnXoa;
    private DAL_NhaCungCap dal = new DAL_NhaCungCap();

    public GUI_QuanLyNhaCungCap() {
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(bgColor);

        // --- 1. HEADER ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(new EmptyBorder(15, 20, 15, 20));
        // Kẻ đường line dưới header
        pnlHeader.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel lblTitle = new JLabel("DANH SÁCH NHÀ CUNG CẤP");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(mainColor);
        
        pnlHeader.add(lblTitle, BorderLayout.WEST);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. TABLE ---
        JPanel pnlTable = new JPanel(new BorderLayout());
        pnlTable.setBorder(new EmptyBorder(20, 20, 20, 20));
        pnlTable.setBackground(bgColor);

        String[] cols = {"Mã Nhà Cung Cấp", "Tên Nhà Cung Cấp", "Địa Chỉ", "Số Điện Thoại"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        
        // [STYLE PREMIUM]
        table.setRowHeight(40); // Dòng cao thoáng
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(232, 242, 252));
        table.setSelectionForeground(Color.BLACK);
        
        // Style Header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(Color.WHITE);
        header.setForeground(mainColor);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, mainColor));
        header.setPreferredSize(new Dimension(0, 40));

        // Renderer chung (Căn giữa, Striped Rows)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                }
                setBorder(new EmptyBorder(0, 5, 0, 5));
                return c;
            }
        };
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        // Áp dụng renderer
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Chỉnh độ rộng cột
        table.getColumnModel().getColumn(1).setPreferredWidth(250); // Tên NCC rộng hơn
        table.getColumnModel().getColumn(2).setPreferredWidth(300); // Địa chỉ rộng hơn

        JScrollPane sc = new JScrollPane(table);
        sc.getViewport().setBackground(Color.WHITE);
        sc.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        pnlTable.add(sc, BorderLayout.CENTER);
        add(pnlTable, BorderLayout.CENTER);

        // --- 3. BUTTONS ---
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        pnlBot.setBackground(bgColor);
        
        btnThem = createButton("Thêm Mới", new Color(40, 167, 69));
        btnSua = createButton("Sửa Thông Tin", new Color(255, 193, 7));
        btnXoa = createButton("Xóa Nhà Cung Cấp", new Color(220, 53, 69));

        pnlBot.add(btnThem);
        pnlBot.add(btnSua);
        pnlBot.add(btnXoa);
        add(pnlBot, BorderLayout.SOUTH);

        // --- 4. EVENTS ---
        
        btnThem.addActionListener(e -> {
            new GUI_DialogNhaCungCap(null, null); // Mở form thêm
            loadData(); // Load lại bảng sau khi đóng form
        });

        btnSua.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn Nhà Cung Cấp cần sửa!");
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
                JOptionPane.showMessageDialog(this, "Vui lòng chọn Nhà Cung Cấp cần xóa!");
                return;
            }
            String maNCC = table.getValueAt(row, 0).toString();
            String tenNCC = table.getValueAt(row, 1).toString();
            
            if (JOptionPane.showConfirmDialog(this, "Bạn chắc chắn muốn xóa nhà cung cấp: " + tenNCC + "?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (dal.xoaNCC(maNCC)) {
                    JOptionPane.showMessageDialog(this, "Đã xóa thành công!");
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại! (Có thể do đã có dữ liệu phiếu nhập liên quan)");
                }
            }
        });
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(160, 45)); // Kích thước đồng bộ
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
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