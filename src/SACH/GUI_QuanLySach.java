package SACH;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class GUI_QuanLySach extends JPanel {

    // Màu chủ đạo
    private Color mainColor = new Color(50, 115, 220); 
    private Color bgColor = new Color(245, 248, 253);
    
    private JTable tableSach;
    private DefaultTableModel modelSach;
    private JTextField txtTimKiem;
    private JComboBox<String> cboLoc;
    private DAL_Sach dal = new DAL_Sach();

    public GUI_QuanLySach() {
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(bgColor);

        // --- 1. HEADER (TIÊU ĐỀ & TÌM KIẾM) ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(new EmptyBorder(15, 20, 15, 20));
        // Tạo đường kẻ dưới nhẹ cho header
        pnlHeader.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel lblTitle = new JLabel("QUẢN LÝ KHO SÁCH", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(mainColor);
        
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        pnlSearch.setBackground(Color.WHITE);
        
        cboLoc = new JComboBox<>(new String[]{"Tất cả", "Mã sách", "Tên sách", "Tác giả"});
        cboLoc.setPreferredSize(new Dimension(130, 40));
        cboLoc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboLoc.setBackground(Color.WHITE);
        
        txtTimKiem = new JTextField();
        txtTimKiem.setPreferredSize(new Dimension(350, 40));
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // Bo viền cho ô tìm kiếm
        txtTimKiem.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(0, 10, 0, 10)
        ));
        
        JButton btnTim = createButton("Tìm kiếm", mainColor, Color.WHITE);
        JButton btnLamMoi = createButton("Làm Mới", new Color(108, 117, 125), Color.WHITE);
        
        pnlSearch.add(cboLoc);
        pnlSearch.add(txtTimKiem);
        pnlSearch.add(btnTim);
        pnlSearch.add(btnLamMoi);

        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setBackground(Color.WHITE);
        pnlTop.add(lblTitle, BorderLayout.NORTH);
        pnlTop.add(pnlSearch, BorderLayout.SOUTH);
        
        add(pnlTop, BorderLayout.NORTH);

        // --- 2. TABLE (BẢNG DỮ LIỆU) ---
        JPanel pnlTable = new JPanel(new BorderLayout());
        pnlTable.setBorder(new EmptyBorder(20, 20, 20, 20));
        pnlTable.setBackground(bgColor);

        String[] cols = {"Mã Cuốn", "Mã Sách", "Tên Sách", "Thể Loại", "Tác Giả", "Năm XB", "Giá Tiền", "Tình Trạng"};
        modelSach = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableSach = new JTable(modelSach);
        
        // [STYLE PREMIUM]
        tableSach.setRowHeight(40); // Dòng cao thoáng
        tableSach.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableSach.setShowVerticalLines(false); // Bỏ kẻ dọc, chỉ giữ kẻ ngang
        tableSach.setIntercellSpacing(new Dimension(0, 0));
        tableSach.setSelectionBackground(new Color(232, 242, 252)); // Màu khi chọn dòng (Xanh nhạt)
        tableSach.setSelectionForeground(Color.BLACK);

        // Style Header
        JTableHeader header = tableSach.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(Color.WHITE);
        header.setForeground(mainColor);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, mainColor)); // Kẻ dưới đậm màu xanh
        header.setPreferredSize(new Dimension(0, 40));

        // Renderer chung: Căn giữa và tô màu nền so le (Striped Rows)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    // Dòng chẵn trắng, dòng lẻ xám nhạt
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                }
                setBorder(new EmptyBorder(0, 5, 0, 5));
                return c;
            }
        };
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        // Áp dụng renderer cho các cột
        for (int i = 0; i < tableSach.getColumnCount(); i++) {
            tableSach.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Renderer riêng cho cột Tình trạng (Cột cuối - index 7) để tô màu chữ
        tableSach.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                
                String status = (value != null) ? value.toString() : "";
                if (status.contains("Còn") || status.contains("sẵn")) {
                    c.setForeground(new Color(40, 167, 69)); // Xanh lá đậm
                    setFont(new Font("Segoe UI", Font.BOLD, 13));
                } else {
                    c.setForeground(new Color(220, 53, 69)); // Đỏ
                    setFont(new Font("Segoe UI", Font.BOLD, 13));
                }
                setHorizontalAlignment(CENTER);
                return c;
            }
        });

        // Chỉnh độ rộng cột
        tableSach.getColumnModel().getColumn(0).setPreferredWidth(80);
        tableSach.getColumnModel().getColumn(1).setPreferredWidth(80);
        tableSach.getColumnModel().getColumn(2).setPreferredWidth(250); // Tên sách rộng hơn
        
        JScrollPane sc = new JScrollPane(tableSach);
        sc.getViewport().setBackground(Color.WHITE);
        sc.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        pnlTable.add(sc, BorderLayout.CENTER);
        add(pnlTable, BorderLayout.CENTER);

        // --- 3. FOOTER (BUTTONS) ---
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        pnlFooter.setBackground(bgColor);

        JButton btnThem = createButton("Thêm Sách", new Color(40, 167, 69), Color.WHITE);
        JButton btnSua = createButton("Sửa Thông Tin", new Color(255, 193, 7), Color.WHITE);
        JButton btnChiTiet = createButton("Xem Chi Tiết", new Color(23, 162, 184), Color.WHITE);
        JButton btnXoa = createButton("Xóa Sách", new Color(220, 53, 69), Color.WHITE);
        
        pnlFooter.add(btnThem);
        pnlFooter.add(btnSua);
        pnlFooter.add(btnChiTiet);
        pnlFooter.add(btnXoa);

        add(pnlFooter, BorderLayout.SOUTH);

        // --- EVENTS ---
        
        txtTimKiem.addActionListener(e -> xuLyTimKiem());
        btnTim.addActionListener(e -> xuLyTimKiem());
        
        btnLamMoi.addActionListener(e -> { 
            txtTimKiem.setText(""); 
            cboLoc.setSelectedIndex(0); 
            loadData(); 
        });
        
        btnThem.addActionListener(e -> new GUI_DialogThemSach(SwingUtilities.getWindowAncestor(this), GUI_QuanLySach.this, null).setVisible(true));
        
        btnSua.addActionListener(e -> {
            int row = tableSach.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn sách cần sửa!"); return; }
            String maSach = tableSach.getValueAt(row, 0).toString(); // Lấy Mã Cuốn
            new GUI_DialogThemSach(SwingUtilities.getWindowAncestor(this), GUI_QuanLySach.this, maSach).setVisible(true);
        });

        btnChiTiet.addActionListener(e -> {
            int row = tableSach.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn sách để xem!"); return; }
            String maSach = tableSach.getValueAt(row, 0).toString();
            new GUI_DialogChiTietSach(SwingUtilities.getWindowAncestor(this), maSach).setVisible(true);
        });

        btnXoa.addActionListener(e -> xuLyXoa());
        
        tableSach.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) btnChiTiet.doClick();
            }
        });
    }

    public void loadData() {
        modelSach.setRowCount(0);
        ArrayList<DTO_Sach> list = dal.getList();
        for (DTO_Sach s : list) themDongVaoBang(s);
    }

    private void themDongVaoBang(DTO_Sach s) {
        String trangThai = (s.getSoLuong() > 0) ? "Còn sẵn (" + s.getSoLuong() + ")" : "Hết hàng";
        String tenTL = (s.getTenTheLoai() != null && !s.getTenTheLoai().isEmpty()) ? s.getTenTheLoai() : s.getMaTheLoai();
        
        modelSach.addRow(new Object[] {
            s.getMaCuonSach(), s.getMaSach(), s.getTenSach(), tenTL,
            s.getTacGia(), s.getNamXuatBan(), String.format("%,.0f", s.getGia()), trangThai
        });
    }

    private void xuLyTimKiem() {
        String key = txtTimKiem.getText().trim();
        String luaChon = cboLoc.getSelectedItem().toString(); 
        
        if (key.isEmpty()) { loadData(); return; }
        
        modelSach.setRowCount(0);
        ArrayList<DTO_Sach> list = dal.searchSach(key, luaChon);
        
        for (DTO_Sach s : list) themDongVaoBang(s);
    }

    private void xuLyXoa() {
        int row = tableSach.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn sách cần xóa!"); return; }
        String ma = tableSach.getValueAt(row, 0).toString();
        
        if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa cuốn sách này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (dal.deleteSach(ma)) {
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại! (Sách có thể đang được mượn)");
            }
        }
    }

    private JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 45)); // Nút to hơn chút
        return btn;
    }
}