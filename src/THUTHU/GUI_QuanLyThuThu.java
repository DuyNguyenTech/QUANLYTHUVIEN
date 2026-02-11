package THUTHU;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class GUI_QuanLyThuThu extends JPanel {

    // Màu chủ đạo đồng bộ hệ thống
    private Color mainColor = new Color(50, 115, 220);
    private Color bgColor = new Color(245, 248, 253);

    private JTable table;
    private DefaultTableModel model;
    private DAL_ThuThu dal = new DAL_ThuThu();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private JTextField txtTimKiem;

    public GUI_QuanLyThuThu() {
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(bgColor);
        setBorder(new EmptyBorder(20, 20, 20, 20)); // Căn lề form thoáng

        // --- 1. HEADER (Tiêu đề + Tìm kiếm) ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(bgColor);
        pnlHeader.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel lblTitle = new JLabel("QUẢN LÝ THỦ THƯ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(mainColor);
        
        // Panel tìm kiếm bên phải
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        pnlSearch.setBackground(bgColor);
        
        txtTimKiem = new JTextField();
        txtTimKiem.setPreferredSize(new Dimension(280, 42));
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        
        // [PREMIUM STYLE] Bo tròn viên thuốc và thêm chữ mờ theo ý anh
        txtTimKiem.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập tên hoặc mã thủ thư...");
        txtTimKiem.putClientProperty(FlatClientProperties.STYLE, 
            "arc: 999; borderWidth: 1; borderColor: #cccccc; focusedBorderColor: #1877F2; margin: 0, 15, 0, 15");
        
        JButton btnTim = createBtn("Tìm kiếm", mainColor, Color.WHITE);
        btnTim.setPreferredSize(new Dimension(120, 42));
        
        JButton btnLamMoi = createBtn("Làm mới", new Color(108, 117, 125), Color.WHITE);
        btnLamMoi.setPreferredSize(new Dimension(120, 42));

        pnlSearch.add(txtTimKiem);
        pnlSearch.add(btnTim);
        pnlSearch.add(btnLamMoi);

        pnlHeader.add(lblTitle, BorderLayout.WEST);
        pnlHeader.add(pnlSearch, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. TABLE (TRONG THẺ CARD TRẮNG) ---
        JPanel pnlTableCard = new JPanel(new BorderLayout());
        pnlTableCard.setBackground(Color.WHITE);
        pnlTableCard.putClientProperty(FlatClientProperties.STYLE, "arc: 20; border: 1,1,1,1, #E0E0E0");
        pnlTableCard.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] cols = {"Mã TT", "Họ Tên", "Ngày Sinh", "Giới Tính", "SĐT", "Username", "Password", "Quyền Hạn"};
        model = new DefaultTableModel(cols, 0) { 
            @Override public boolean isCellEditable(int r, int c) { return false; } 
        };
        table = new JTable(model);
        
        // [STYLE PREMIUM]
        table.setRowHeight(42);
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

        // Renderer Chung (Striped Rows, Ẩn Password, Tô màu Role)
        DefaultTableCellRenderer customRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                }
                
                // Ẩn Password (Cột 6)
                if (column == 6) setText("********");
                
                // Tô màu Quyền hạn (Cột 7)
                if (column == 7) {
                    String role = (String) value;
                    if ("Admin".equals(role)) {
                        setForeground(new Color(220, 53, 69)); // Đỏ mạnh mẽ
                        setFont(new Font("Segoe UI", Font.BOLD, 14));
                    } else {
                        setForeground(new Color(0, 123, 255)); // Xanh chuyên nghiệp
                        setFont(new Font("Segoe UI", Font.BOLD, 14));
                    }
                } else {
                    if (!isSelected) setForeground(new Color(50, 50, 50));
                }
                
                setBorder(new EmptyBorder(0, 5, 0, 5));
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        };
        
        for(int i=0; i<cols.length; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(customRenderer);
        }
        
        table.getColumnModel().getColumn(1).setPreferredWidth(200); 

        JScrollPane sc = new JScrollPane(table);
        sc.getViewport().setBackground(Color.WHITE);
        sc.setBorder(BorderFactory.createEmptyBorder());
        
        pnlTableCard.add(sc, BorderLayout.CENTER);
        add(pnlTableCard, BorderLayout.CENTER);

        // --- 3. BUTTONS BOTTOM ---
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 20));
        pnlBot.setBackground(bgColor);
        
        JButton btnThem = createBtn("Thêm Thủ Thư", new Color(40, 167, 69), Color.WHITE); 
        JButton btnSua = createBtn("Sửa Thủ Thư", new Color(255, 193, 7), Color.WHITE); 
        JButton btnXoa = createBtn("Xóa Thủ Thư", new Color(220, 53, 69), Color.WHITE); 
        JButton btnXem = createBtn("Xem Chi Tiết", new Color(23, 162, 184), Color.WHITE); 

        pnlBot.add(btnThem); 
        pnlBot.add(btnSua); 
        pnlBot.add(btnXoa);
        pnlBot.add(btnXem);
        
        add(pnlBot, BorderLayout.SOUTH);

        // --- EVENTS ---
        btnTim.addActionListener(e -> xuLyTimKiem());
        txtTimKiem.addActionListener(e -> xuLyTimKiem()); 

        btnLamMoi.addActionListener(e -> {
            txtTimKiem.setText("");
            loadData();
        });

        btnThem.addActionListener(e -> {
            new GUI_DialogThuThu(this, null).setVisible(true);
            loadData();
        });
        
        btnSua.addActionListener(e -> {
            int r = table.getSelectedRow();
            if(r == -1) { 
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE); 
                return; 
            }
            String maNV = table.getValueAt(r, 0).toString();
            DTO_ThuThu tt = dal.getDetail(maNV); 
            new GUI_DialogThuThu(this, tt).setVisible(true);
            loadData();
        });

        btnXoa.addActionListener(e -> {
            int r = table.getSelectedRow();
            if(r == -1) { 
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE); 
                return; 
            }
            String ma = table.getValueAt(r, 0).toString();
            String ten = table.getValueAt(r, 1).toString();
            
            if(JOptionPane.showConfirmDialog(this, "Xóa nhân viên " + ten + " sẽ xóa luôn tài khoản đăng nhập.\nBạn chắc chắn muốn tiếp tục?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if(dal.delete(ma)) {
                    JOptionPane.showMessageDialog(this, "Đã xóa thành công!");
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại! Nhân viên này có thể đang có dữ liệu liên quan.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnXem.addActionListener(e -> {
            int r = table.getSelectedRow();
            if(r == -1) { 
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xem!", "Thông báo", JOptionPane.WARNING_MESSAGE); 
                return; 
            }
            String maNV = table.getValueAt(r, 0).toString();
            DTO_ThuThu tt = dal.getDetail(maNV);
            new GUI_DialogChiTietThuThu(SwingUtilities.getWindowAncestor(this), tt).setVisible(true);
        });
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

    private JButton createBtn(String t, Color bg, Color fg) {
        JButton b = new JButton(t); 
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBackground(bg); 
        b.setForeground(fg); 
        b.setPreferredSize(new Dimension(160, 42)); 
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0");
        return b;
    }
}