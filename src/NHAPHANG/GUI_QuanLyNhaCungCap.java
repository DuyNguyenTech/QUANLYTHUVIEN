package NHAPHANG;

import com.formdev.flatlaf.FlatClientProperties;

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
        setBorder(new EmptyBorder(20, 20, 20, 20)); // Căn lề form thoáng như hệ thống

        // --- 1. HEADER ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(bgColor); // Đồng bộ nền
        pnlHeader.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel lblTitle = new JLabel("DANH SÁCH NHÀ CUNG CẤP");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26)); // Font to chuẩn Premium
        lblTitle.setForeground(mainColor);
        
        pnlHeader.add(lblTitle, BorderLayout.WEST);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. TABLE (TRONG THẺ CARD BO GÓC) ---
        JPanel pnlTableCard = new JPanel(new BorderLayout());
        pnlTableCard.setBackground(Color.WHITE);
        pnlTableCard.putClientProperty(FlatClientProperties.STYLE, "arc: 20; border: 1,1,1,1, #E0E0E0");
        pnlTableCard.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] cols = {"Mã NCC", "Tên Nhà Cung Cấp", "Địa Chỉ", "Số Điện Thoại"};
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
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setBackground(new Color(248, 249, 250)); // Nền xám nhạt
        header.setForeground(new Color(50, 50, 50));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        header.setPreferredSize(new Dimension(0, 45));

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

        // Renderer Căn trái cho Tên NCC và Địa chỉ (dễ đọc chữ dài)
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                setBorder(new EmptyBorder(0, 10, 0, 0)); // Padding trái
                return c;
            }
        };
        
        // Áp dụng renderer
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Mã
        table.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);   // Tên
        table.getColumnModel().getColumn(2).setCellRenderer(leftRenderer);   // Địa chỉ
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // SĐT
        
        // Chỉnh độ rộng cột
        table.getColumnModel().getColumn(0).setPreferredWidth(100); 
        table.getColumnModel().getColumn(1).setPreferredWidth(250); 
        table.getColumnModel().getColumn(2).setPreferredWidth(350); // Địa chỉ cho rộng nhất
        table.getColumnModel().getColumn(3).setPreferredWidth(150);

        JScrollPane sc = new JScrollPane(table);
        sc.getViewport().setBackground(Color.WHITE);
        sc.setBorder(BorderFactory.createEmptyBorder()); // Xóa viền đen của cuộn
        
        pnlTableCard.add(sc, BorderLayout.CENTER);
        add(pnlTableCard, BorderLayout.CENTER);

        // --- 3. BUTTONS (FOOTER) ---
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15)); // Canh phải cho hiện đại
        pnlBot.setBackground(bgColor);
        pnlBot.setBorder(new EmptyBorder(0, 0, 0, 0));
        
        btnThem = createButton("Thêm Mới", new Color(40, 167, 69), Color.WHITE);
        btnSua = createButton("Sửa Thông Tin", new Color(255, 193, 7), Color.BLACK); // Đổi chữ đen
        btnXoa = createButton("Xóa NCC", new Color(220, 53, 69), Color.WHITE);

        pnlBot.add(btnThem);
        pnlBot.add(btnSua);
        pnlBot.add(btnXoa);
        add(pnlBot, BorderLayout.SOUTH);

        // --- 4. EVENTS ---
        btnThem.addActionListener(e -> {
            new GUI_DialogNhaCungCap(null, null); 
            loadData(); 
        });

        btnSua.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn NCC cần sửa!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
                return;
            }
            DTO_NhaCungCap ncc = new DTO_NhaCungCap(
                table.getValueAt(row, 0).toString(),
                table.getValueAt(row, 1).toString(),
                table.getValueAt(row, 2).toString(),
                table.getValueAt(row, 3).toString()
            );
            new GUI_DialogNhaCungCap(null, ncc); 
            loadData();
        });

        btnXoa.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn NCC cần xóa!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String maNCC = table.getValueAt(row, 0).toString();
            String tenNCC = table.getValueAt(row, 1).toString();
            
            if (JOptionPane.showConfirmDialog(this, "Bạn chắc chắn muốn xóa nhà cung cấp:\n" + tenNCC + "?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (dal.xoaNCC(maNCC)) {
                    JOptionPane.showMessageDialog(this, "Đã xóa thành công!");
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại! (Do đã có dữ liệu phiếu nhập liên quan)", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // Hàm tạo nút bấm chuẩn style Premium
    private JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setPreferredSize(new Dimension(150, 42)); // Kích thước gọn gàng
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Bo góc nút
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0");
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