package DOCGIA;

import com.formdev.flatlaf.FlatClientProperties;
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
        setBorder(new EmptyBorder(20, 20, 20, 20)); // Căn lề form thoáng

        // --- 1. HEADER (Title + Search) ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(bgColor);
        pnlHeader.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel lblTitle = new JLabel("QUẢN LÝ ĐỘC GIẢ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(mainColor);

        // Panel chứa ô tìm kiếm (FlowLayout Right)
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlSearch.setBackground(bgColor);
        
        txtTimKiem = new JTextField();
        txtTimKiem.setPreferredSize(new Dimension(300, 42));
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        
        // [CẬP NHẬT] Thêm chữ mờ placeholder riêng cho ô tìm kiếm theo ý anh
        txtTimKiem.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập từ khóa tìm kiếm...");
        txtTimKiem.putClientProperty(FlatClientProperties.STYLE, 
            "arc: 999; borderWidth: 1; borderColor: #cccccc; focusedBorderColor: #1877F2; margin: 0, 15, 0, 15");
        
        JButton btnTim = createButton("Tìm kiếm", mainColor, Color.WHITE);
        btnTim.setPreferredSize(new Dimension(120, 42)); 
        
        JButton btnLamMoi = createButton("Làm mới", new Color(108, 117, 125), Color.WHITE);
        btnLamMoi.setPreferredSize(new Dimension(120, 42));

        pnlSearch.add(txtTimKiem);
        pnlSearch.add(btnTim);
        pnlSearch.add(btnLamMoi);

        pnlHeader.add(lblTitle, BorderLayout.WEST);
        pnlHeader.add(pnlSearch, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. CENTER (BẢNG DỮ LIỆU TRONG THẺ CARD) ---
        JPanel pnlTableCard = new JPanel(new BorderLayout());
        pnlTableCard.setBackground(Color.WHITE);
        pnlTableCard.putClientProperty(FlatClientProperties.STYLE, "arc: 20; border: 1,1,1,1, #E0E0E0");
        pnlTableCard.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] cols = {"Mã ĐG", "Tên Độc Giả", "Lớp", "Địa Chỉ", "Số Điện Thoại"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        
        // [STYLE PREMIUM]
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(232, 242, 252));
        table.setSelectionForeground(Color.BLACK);
        
        // Header Table
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setBackground(new Color(248, 249, 250));
        header.setForeground(new Color(50, 50, 50));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        header.setPreferredSize(new Dimension(0, 45));

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
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return c;
            }
        };
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);

        // Apply Renderers
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); 
        table.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);   
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); 
        table.getColumnModel().getColumn(3).setCellRenderer(leftRenderer);   
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); 

        table.getColumnModel().getColumn(1).setPreferredWidth(220);
        table.getColumnModel().getColumn(3).setPreferredWidth(280);

        JScrollPane sc = new JScrollPane(table);
        sc.getViewport().setBackground(Color.WHITE);
        sc.setBorder(BorderFactory.createEmptyBorder()); 
        
        pnlTableCard.add(sc, BorderLayout.CENTER);
        add(pnlTableCard, BorderLayout.CENTER);

        // --- 3. BOTTOM (BUTTONS) ---
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 20));
        pnlBot.setBackground(bgColor);

        JButton btnThem = createButton("Thêm Độc Giả", new Color(40, 167, 69), Color.WHITE);
        JButton btnSua = createButton("Sửa Độc Giả", new Color(255, 193, 7), Color.WHITE);
        JButton btnXoa = createButton("Xóa Độc Giả", new Color(220, 53, 69), Color.WHITE);

        pnlBot.add(btnThem);
        pnlBot.add(btnSua);
        pnlBot.add(btnXoa);
        add(pnlBot, BorderLayout.SOUTH);

        // --- EVENTS ---
        btnTim.addActionListener(e -> timKiem());
        txtTimKiem.addActionListener(e -> timKiem());
        
        btnLamMoi.addActionListener(e -> { 
            txtTimKiem.setText(""); 
            loadData(); 
        });

        btnThem.addActionListener(e -> new GUI_DialogThemDocGia(this).setVisible(true));
        
        btnSua.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row == -1) { 
                JOptionPane.showMessageDialog(this, "Vui lòng chọn độc giả cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE); 
                return; 
            }
            DTO_DocGia dg = getSelectedData(row);
            new GUI_DialogThemDocGia(this, dg).setVisible(true);
        });

        btnXoa.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row == -1) { 
                JOptionPane.showMessageDialog(this, "Vui lòng chọn độc giả cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE); 
                return; 
            }
            String ma = table.getValueAt(row, 0).toString();
            String ten = table.getValueAt(row, 1).toString();
            if(JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa độc giả:\n" + ten + " (" + ma + ")?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if(dal.delete(ma)) {
                    JOptionPane.showMessageDialog(this, "Đã xóa thành công!");
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại! (Độc giả này có thể đang mượn sách)", "Lỗi", JOptionPane.ERROR_MESSAGE);
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
            loadData();
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

    private JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setPreferredSize(new Dimension(160, 42));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0");
        return btn;
    }
}