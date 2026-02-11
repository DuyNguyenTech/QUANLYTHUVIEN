package SACH;

import HETHONG.GUI_Main;
import HETHONG.DTO_TaiKhoan;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class GUI_TraCuuSach extends JPanel {

    private Color mainColor = new Color(50, 115, 220);
    private Color bgColor = new Color(245, 248, 253);

    private DAL_Sach dal = new DAL_Sach();
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtTimKiem;
    
    private ArrayList<DTO_Sach> gioSach = new ArrayList<>();
    private JButton btnGioSach;

    public GUI_TraCuuSach() {
        try {
            initUI();
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải giao diện Tra cứu sách: " + e.getMessage());
        }
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(bgColor);
        setBorder(new EmptyBorder(20, 20, 20, 20)); // Căn lề cho form thoáng hơn

        // --- HEADER ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(bgColor);
        pnlHeader.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel lblTitle = new JLabel("TRA CỨU SÁCH");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(mainColor);

        // --- SEARCH BAR ---
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlSearch.setBackground(bgColor);

        txtTimKiem = new JTextField();
        txtTimKiem.setPreferredSize(new Dimension(300, 42));
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        // Style FlatLaf: Bo tròn như viên thuốc, có placeholder text
        txtTimKiem.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập tên sách, tác giả...");
        txtTimKiem.putClientProperty(FlatClientProperties.STYLE, "arc: 999; borderWidth: 1; borderColor: #cccccc; focusedBorderColor: #1877F2; margin: 0, 15, 0, 15");

        JButton btnTim = new JButton("Tìm Kiếm");
        setupButton(btnTim, mainColor);

        JButton btnLamMoi = new JButton("Làm Mới");
        setupButton(btnLamMoi, new Color(108, 117, 125)); // Xám ghi

        pnlSearch.add(txtTimKiem);
        pnlSearch.add(btnTim);
        pnlSearch.add(btnLamMoi);

        pnlHeader.add(lblTitle, BorderLayout.WEST);
        pnlHeader.add(pnlSearch, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        // --- TABLE (Khung thẻ Card bo góc) ---
        JPanel pnlTableCard = new JPanel(new BorderLayout());
        pnlTableCard.setBackground(Color.WHITE);
        pnlTableCard.putClientProperty(FlatClientProperties.STYLE, "arc: 20; border: 1,1,1,1, #E0E0E0");
        pnlTableCard.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] cols = {"Mã Sách", "Tên Sách", "Tác Giả", "Thể Loại", "Năm XB", "Tình Trạng", "Số lượng"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(232, 242, 252));
        table.setSelectionForeground(Color.BLACK);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setBackground(new Color(248, 249, 250));
        header.setForeground(new Color(50, 50, 50));
        header.setPreferredSize(new Dimension(0, 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer left = new DefaultTableCellRenderer();
        left.setHorizontalAlignment(JLabel.LEFT);
        left.setBorder(new EmptyBorder(0, 10, 0, 0));

        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(1).setCellRenderer(left);
        table.getColumnModel().getColumn(2).setCellRenderer(left);
        table.getColumnModel().getColumn(3).setCellRenderer(center);
        table.getColumnModel().getColumn(4).setCellRenderer(center);

        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = value.toString();
                if (status.equals("Còn sách")) {
                    c.setForeground(new Color(40, 167, 69)); 
                    c.setFont(new Font("Segoe UI", Font.BOLD, 14));
                } else {
                    c.setForeground(new Color(220, 53, 69)); 
                    c.setFont(new Font("Segoe UI", Font.BOLD, 14));
                }
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        });
        
        // Ẩn cột số lượng nhưng vẫn giữ dữ liệu để kiểm tra logic
        table.getColumnModel().getColumn(6).setMinWidth(0);
        table.getColumnModel().getColumn(6).setMaxWidth(0);
        table.getColumnModel().getColumn(6).setWidth(0);

        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        pnlTableCard.add(scroll, BorderLayout.CENTER);
        add(pnlTableCard, BorderLayout.CENTER);

        // --- FOOTER (GIỎ HÀNG) ---
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        pnlFooter.setBackground(bgColor);
        
        JButton btnThemGio = new JButton("Thêm Vào Giỏ");
        setupButton(btnThemGio, new Color(255, 193, 7)); // Màu vàng
        btnThemGio.setForeground(Color.BLACK); 
        
        btnGioSach = new JButton("Xem Giỏ Sách (0)");
        setupButton(btnGioSach, new Color(40, 167, 69)); // Màu xanh lá

        pnlFooter.add(btnThemGio);
        pnlFooter.add(btnGioSach);
        add(pnlFooter, BorderLayout.SOUTH);

        // --- EVENTS ---
        btnTim.addActionListener(e -> xuLyTimKiem());
        btnLamMoi.addActionListener(e -> { txtTimKiem.setText(""); loadData(); });
        
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) { if (e.getKeyCode() == KeyEvent.VK_ENTER) xuLyTimKiem(); }
        });
        
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if(row >= 0) {
                        String ma = table.getValueAt(row, 0).toString();
                        Window win = SwingUtilities.getWindowAncestor(GUI_TraCuuSach.this);
                        new GUI_DialogChiTietSach(win, ma).setVisible(true);
                    }
                }
            }
        });

        btnThemGio.addActionListener(e -> xuLyThemVaoGio());
        btnGioSach.addActionListener(e -> xuLyXemGio());
    }

    private void setupButton(JButton btn, Color bg) {
        btn.setPreferredSize(new Dimension(160, 42));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0");
    }

    private void loadData() {
        model.setRowCount(0);
        ArrayList<DTO_Sach> list = dal.getList();
        for (DTO_Sach s : list) addDataRow(s);
    }

    private void xuLyTimKiem() {
        String key = txtTimKiem.getText().trim();
        if (key.isEmpty()) { loadData(); return; }
        model.setRowCount(0);
        ArrayList<DTO_Sach> list = dal.searchSach(key, "Tất cả"); // Cần đảm bảo DAL có hàm này
        for (DTO_Sach s : list) addDataRow(s);
    }

    private void addDataRow(DTO_Sach s) {
        String tinhTrang = (s.getSoLuong() > 0) ? "Còn sách" : "Hết sách";
        String theLoai = (s.getTenTheLoai() != null) ? s.getTenTheLoai() : s.getMaTheLoai();

        model.addRow(new Object[]{
            s.getMaCuonSach(),
            s.getTenSach(),
            s.getTacGia(),
            theLoai,
            s.getNamXuatBan(),
            tinhTrang,
            s.getSoLuong()
        });
    }

    // --- LOGIC GIỎ SÁCH ---
    private void xuLyThemVaoGio() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một cuốn sách từ danh sách!");
            return;
        }

        int soLuongTon = Integer.parseInt(table.getValueAt(row, 6).toString());
        if (soLuongTon <= 0) {
            JOptionPane.showMessageDialog(this, "Sách này hiện đã hết trong kho, không thể mượn!");
            return;
        }

        String maSach = table.getValueAt(row, 0).toString();
        
        for (DTO_Sach s : gioSach) {
            if (s.getMaCuonSach().equals(maSach)) {
                JOptionPane.showMessageDialog(this, "Bạn đã thêm sách này vào giỏ trước đó rồi!");
                return;
            }
        }
        
        if (gioSach.size() >= 5) {
            JOptionPane.showMessageDialog(this, "Bạn chỉ được mượn tối đa 5 cuốn sách cùng lúc!");
            return;
        }

        DTO_Sach s = dal.getDetail(maSach);
        if (s != null) {
            gioSach.add(s);
            capNhatSoLuongGio(); 
            JOptionPane.showMessageDialog(this, "Đã thêm [" + s.getTenSach() + "] vào giỏ!");
        }
    }

    // Hàm gọi về để cập nhật số lượng nút
    public void capNhatSoLuongGio() {
        btnGioSach.setText("Xem Giỏ Sách (" + gioSach.size() + ")");
    }

    private void xuLyXemGio() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        String maDG = "";
        
        if (parent instanceof GUI_Main) {
             GUI_Main main = (GUI_Main) parent;
             DTO_TaiKhoan tk = main.getTaiKhoan(); 
             if (tk != null) maDG = tk.getMaDocGia();
        }

        if (maDG == null || maDG.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chức năng này chỉ dành cho tài khoản Độc giả!");
            return;
        }

        // Truyền 'this' vào để Form con gọi lại hàm capNhatSoLuongGio()
        GUI_DialogGioSach dialog = new GUI_DialogGioSach(parent, this, gioSach, maDG);
        dialog.setVisible(true);
        
        // Nếu người dùng đã bấm Gửi Yêu Cầu thành công thì xóa giỏ sách
        if(dialog.isDaGuiYeuCau()) {
            gioSach.clear();
            capNhatSoLuongGio(); 
            loadData(); // Tải lại bảng sách
        }
    }
}