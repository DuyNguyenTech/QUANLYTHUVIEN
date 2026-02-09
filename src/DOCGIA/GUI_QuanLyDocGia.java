package DOCGIA;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;

public class GUI_QuanLyDocGia extends JPanel {

    // Màu chủ đạo (Đồng bộ với các module khác)
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

        // --- 1. HEADER (Title + Search) ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(new EmptyBorder(15, 20, 15, 20));
        // Kẻ đường line dưới header tạo điểm nhấn
        pnlHeader.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel lblTitle = new JLabel("QUẢN LÝ ĐỘC GIẢ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(mainColor);

        // Panel chứa ô tìm kiếm
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlSearch.setBackground(Color.WHITE);
        
        txtTimKiem = new JTextField();
        txtTimKiem.setPreferredSize(new Dimension(300, 40));
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // Bo viền ô tìm kiếm
        txtTimKiem.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(0, 10, 0, 10)
        ));
        
        JButton btnTim = createButton("Tìm kiếm", mainColor);
        btnTim.setPreferredSize(new Dimension(120, 40)); // Nút tìm nhỏ hơn chút cho cân đối header
        
        JButton btnLamMoi = createButton("Làm mới", new Color(108, 117, 125)); // Màu xám
        btnLamMoi.setPreferredSize(new Dimension(120, 40));

        pnlSearch.add(txtTimKiem);
        pnlSearch.add(btnTim);
        pnlSearch.add(btnLamMoi);

        pnlHeader.add(lblTitle, BorderLayout.WEST);
        pnlHeader.add(pnlSearch, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. CENTER (TABLE) ---
        JPanel pnlCenter = new JPanel(new BorderLayout());
        pnlCenter.setBackground(bgColor);
        pnlCenter.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] cols = {"Mã ĐG", "Tên Độc Giả", "Lớp", "Địa Chỉ", "Số Điện Thoại"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        
        // [STYLE PREMIUM]
        table.setRowHeight(40); // Dòng cao thoáng
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowVerticalLines(false); // Bỏ kẻ dọc
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(232, 242, 252)); // Màu chọn xanh nhạt
        table.setSelectionForeground(Color.BLACK);
        
        // Header Table
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(Color.WHITE);
        header.setForeground(mainColor);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, mainColor)); // Kẻ dưới đậm
        header.setPreferredSize(new Dimension(0, 40));

        // Renderer: Căn giữa và Striped Rows
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
        
        // Renderer: Căn trái cho Tên & Địa chỉ
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                }
                setBorder(new EmptyBorder(0, 10, 0, 10)); // Padding trái nhiều hơn
                return c;
            }
        };
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);

        // Apply Renderers
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Mã
        table.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);   // Tên
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Lớp
        table.getColumnModel().getColumn(3).setCellRenderer(leftRenderer);   // Địa chỉ
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // SĐT

        // Chỉnh độ rộng cột
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(250);

        JScrollPane sc = new JScrollPane(table);
        sc.getViewport().setBackground(Color.WHITE);
        sc.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        pnlCenter.add(sc, BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        // --- 3. BOTTOM (BUTTONS) ---
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        pnlBot.setBackground(bgColor);

        JButton btnThem = createButton("Thêm Độc Giả", new Color(40, 167, 69)); // Xanh lá
        JButton btnSua = createButton("Sửa Độc Giả", new Color(255, 193, 7));   // Vàng cam
        JButton btnXoa = createButton("Xóa Độc Giả", new Color(220, 53, 69));   // Đỏ

        pnlBot.add(btnThem);
        pnlBot.add(btnSua);
        pnlBot.add(btnXoa);
        add(pnlBot, BorderLayout.SOUTH);

        // --- EVENTS ---
        
        // Sự kiện tìm kiếm
        btnTim.addActionListener(e -> timKiem());
        txtTimKiem.addActionListener(e -> timKiem()); // Enter cũng tìm
        
        // Sự kiện làm mới
        btnLamMoi.addActionListener(e -> { 
            txtTimKiem.setText(""); 
            loadData(); 
        });

        // Sự kiện Thêm
        btnThem.addActionListener(e -> new GUI_DialogThemDocGia(this).setVisible(true));
        
        // Sự kiện Sửa
        btnSua.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row == -1) { 
                JOptionPane.showMessageDialog(this, "Vui lòng chọn độc giả cần sửa!"); 
                return; 
            }
            DTO_DocGia dg = getSelectedData(row);
            new GUI_DialogThemDocGia(this, dg).setVisible(true);
        });

        // Sự kiện Xóa
        btnXoa.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row == -1) { 
                JOptionPane.showMessageDialog(this, "Vui lòng chọn độc giả cần xóa!"); 
                return; 
            }
            String ma = table.getValueAt(row, 0).toString();
            String ten = table.getValueAt(row, 1).toString();
            if(JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa độc giả: " + ten + " (" + ma + ")?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if(dal.delete(ma)) {
                    JOptionPane.showMessageDialog(this, "Đã xóa thành công!");
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại! (Có thể độc giả đang mượn sách)");
                }
            }
        });
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
        if(key.isEmpty()) { 
            loadData(); // Nếu rỗng thì load lại tất cả
            return; 
        }
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

    // Hàm tạo nút bấm chuẩn style đồng bộ
    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(160, 45)); // Kích thước chuẩn
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Thêm hiệu ứng hover đơn giản nếu muốn (Optional)
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg);
            }
        });
        
        return btn;
    }
}