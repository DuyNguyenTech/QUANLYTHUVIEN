package THUTHU;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class GUI_QuanLyThuThu extends JPanel {

    // Màu chủ đạo
    private Color mainColor = new Color(50, 115, 220);
    private Color bgColor = new Color(245, 248, 253);

    private JTable table;
    private DefaultTableModel model;
    private DAL_ThuThu dal = new DAL_ThuThu();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    
    // Khai báo biến tìm kiếm
    private JTextField txtTimKiem;

    public GUI_QuanLyThuThu() {
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(bgColor);

        // --- 1. HEADER (Tiêu đề + Tìm kiếm) ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(new EmptyBorder(15, 20, 15, 20));
        // Kẻ đường line dưới
        pnlHeader.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            new EmptyBorder(15, 20, 15, 20)
        ));

        // Tiêu đề bên trái
        JLabel lblTitle = new JLabel("QUẢN LÝ NHÂN SỰ (THỦ THƯ)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(mainColor);
        
        // Panel tìm kiếm bên phải
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlSearch.setBackground(Color.WHITE);
        
        txtTimKiem = new JTextField();
        txtTimKiem.setPreferredSize(new Dimension(250, 40));
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // Bo viền ô tìm kiếm
        txtTimKiem.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(0, 10, 0, 10)
        ));
        
        JButton btnTim = createBtn("Tìm kiếm", mainColor);
        btnTim.setPreferredSize(new Dimension(120, 40));
        
        JButton btnLamMoi = createBtn("Làm mới", new Color(46, 125, 50)); // Màu xanh lá đậm
        btnLamMoi.setPreferredSize(new Dimension(120, 40));

        pnlSearch.add(txtTimKiem);
        pnlSearch.add(btnTim);
        pnlSearch.add(btnLamMoi);

        pnlHeader.add(lblTitle, BorderLayout.WEST);
        pnlHeader.add(pnlSearch, BorderLayout.EAST);
        
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. TABLE ---
        JPanel pnlTable = new JPanel(new BorderLayout());
        pnlTable.setBorder(new EmptyBorder(20, 20, 20, 20));
        pnlTable.setBackground(bgColor);

        String[] cols = {"Mã TT", "Họ Tên", "Ngày Sinh", "Giới Tính", "SĐT", "Username", "Password", "Quyền Hạn"};
        model = new DefaultTableModel(cols, 0) { 
            public boolean isCellEditable(int r, int c) { return false; } 
        };
        table = new JTable(model);
        
        // [STYLE PREMIUM]
        table.setRowHeight(40); // Dòng cao thoáng
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowVerticalLines(false); // Bỏ kẻ dọc
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(232, 242, 252)); // Màu chọn xanh nhạt
        table.setSelectionForeground(Color.BLACK);
        
        // Style Header
        JTableHeader h = table.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 14));
        h.setBackground(Color.WHITE); 
        h.setForeground(mainColor);
        h.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, mainColor));
        h.setPreferredSize(new Dimension(0, 40));

        // Renderer Chung (Căn giữa, Striped Rows, Ẩn Password)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Striped Rows
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                }
                
                // Ẩn Password (Cột 6)
                if (column == 6) setText("********");
                
                // Tô màu Quyền hạn (Cột 7)
                if (column == 7) {
                    String role = (String) value;
                    if ("Admin".equals(role)) {
                        setForeground(new Color(220, 53, 69)); // Đỏ
                        setFont(new Font("Segoe UI", Font.BOLD, 14));
                    } else {
                        setForeground(new Color(0, 123, 255)); // Xanh
                        setFont(new Font("Segoe UI", Font.BOLD, 14));
                    }
                } else {
                    setForeground(Color.BLACK);
                }
                
                setBorder(new EmptyBorder(0, 5, 0, 5));
                return c;
            }
        };
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        // Áp dụng renderer
        for(int i=0; i<cols.length; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Chỉnh độ rộng cột
        table.getColumnModel().getColumn(1).setPreferredWidth(200); // Họ tên rộng hơn

        JScrollPane sc = new JScrollPane(table);
        sc.getViewport().setBackground(Color.WHITE);
        sc.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        pnlTable.add(sc, BorderLayout.CENTER);
        add(pnlTable, BorderLayout.CENTER);

        // --- 3. BUTTONS BOTTOM ---
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        pnlBot.setBackground(bgColor);
        
        JButton btnThem = createBtn("Thêm Nhân Viên", new Color(40, 167, 69)); // Xanh lá
        JButton btnSua = createBtn("Sửa Nhân Viên", new Color(255, 152, 0)); // Cam
        JButton btnXoa = createBtn("Xóa Nhân Viên", new Color(220, 53, 69)); // Đỏ
        JButton btnXem = createBtn("Xem Chi Tiết", new Color(23, 162, 184)); // Cyan

        // --- EVENTS ---
        
        // Sự kiện Tìm kiếm
        btnTim.addActionListener(e -> xuLyTimKiem());
        txtTimKiem.addActionListener(e -> xuLyTimKiem()); // Enter cũng tìm

        // Sự kiện Làm mới (Reset bảng)
        btnLamMoi.addActionListener(e -> {
            txtTimKiem.setText("");
            loadData();
        });

        // 1. THÊM
        btnThem.addActionListener(e -> {
            new GUI_DialogThuThu(this, null).setVisible(true);
            loadData();
        });
        
        // 2. SỬA
        btnSua.addActionListener(e -> {
            int r = table.getSelectedRow();
            if(r == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần sửa!"); return; }
            String maNV = table.getValueAt(r, 0).toString();
            DTO_ThuThu tt = dal.getDetail(maNV); 
            new GUI_DialogThuThu(this, tt).setVisible(true);
            loadData();
        });

        // 3. XÓA
        btnXoa.addActionListener(e -> {
            int r = table.getSelectedRow();
            if(r == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xóa!"); return; }
            String ma = table.getValueAt(r, 0).toString();
            String ten = table.getValueAt(r, 1).toString();
            
            if(JOptionPane.showConfirmDialog(this, "Xóa nhân viên " + ten + " sẽ xóa luôn tài khoản đăng nhập.\nBạn có chắc chắn muốn tiếp tục?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if(dal.delete(ma)) {
                    JOptionPane.showMessageDialog(this, "Đã xóa thành công!");
                    loadData();
                } else JOptionPane.showMessageDialog(this, "Xóa thất bại! (Có thể do ràng buộc dữ liệu)");
            }
        });

        // 4. XEM CHI TIẾT
        btnXem.addActionListener(e -> {
            int r = table.getSelectedRow();
            if(r == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xem!"); return; }
            String maNV = table.getValueAt(r, 0).toString();
            DTO_ThuThu tt = dal.getDetail(maNV);
            new GUI_DialogChiTietThuThu(SwingUtilities.getWindowAncestor(this), tt).setVisible(true);
        });

        pnlBot.add(btnThem); 
        pnlBot.add(btnSua); 
        pnlBot.add(btnXoa);
        pnlBot.add(btnXem);
        
        add(pnlBot, BorderLayout.SOUTH);
    }

    public void loadData() {
        model.setRowCount(0);
        ArrayList<DTO_ThuThu> list = dal.getList();
        for(DTO_ThuThu tt : list) {
            addDataToTable(tt);
        }
    }

    private void xuLyTimKiem() {
        String keyword = txtTimKiem.getText().trim();
        if(keyword.isEmpty()) {
            loadData();
            return;
        }
        
        model.setRowCount(0);
        ArrayList<DTO_ThuThu> list = dal.timKiem(keyword);
        for(DTO_ThuThu tt : list) {
            addDataToTable(tt);
        }
    }

    private void addDataToTable(DTO_ThuThu tt) {
        String role = (tt.getPhanQuyen() == 1) ? "Admin" : "Thủ thư";
        String ns = (tt.getNgaySinh() != null) ? sdf.format(tt.getNgaySinh()) : "";
        model.addRow(new Object[]{
            tt.getMaThuThu(), tt.getTenThuThu(), ns, tt.getGioiTinh(), tt.getSoDienThoai(),
            tt.getTenDangNhap(), tt.getMatKhau(), role
        });
    }

    private JButton createBtn(String t, Color c) {
        JButton b = new JButton(t); 
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBackground(c); 
        b.setForeground(Color.WHITE); 
        b.setPreferredSize(new Dimension(160, 45)); // Kích thước đồng bộ 160x45
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}