package com.qlthuvien.GUI;

import com.qlthuvien.DAL.DAL_Sach;
import com.qlthuvien.DTO.DTO_Sach;
import com.qlthuvien.DTO.TaiKhoan; 

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
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(bgColor);

        // --- HEADER ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel lblTitle = new JLabel("TRA CỨU SÁCH");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(mainColor);

        // --- SEARCH BAR ---
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlSearch.setBackground(Color.WHITE);

        JLabel lblSearch = new JLabel("Tìm kiếm:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSearch.setForeground(Color.GRAY);

        txtTimKiem = new JTextField();
        txtTimKiem.setPreferredSize(new Dimension(250, 40));
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTimKiem.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JButton btnTim = new JButton("Tìm");
        setupButton(btnTim, mainColor);

        JButton btnLamMoi = new JButton("Làm mới");
        setupButton(btnLamMoi, new Color(40, 167, 69));

        pnlSearch.add(lblSearch);
        pnlSearch.add(txtTimKiem);
        pnlSearch.add(btnTim);
        pnlSearch.add(btnLamMoi);

        pnlHeader.add(lblTitle, BorderLayout.WEST);
        pnlHeader.add(pnlSearch, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        // --- TABLE ---
        JPanel pnlTable = new JPanel(new BorderLayout());
        pnlTable.setBackground(bgColor);
        pnlTable.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] cols = {"Mã Sách", "Tên Sách", "Tác Giả", "Thể Loại", "Năm XB", "Tình Trạng", "Số lượng"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(245, 245, 245));
        header.setForeground(new Color(50, 50, 50));
        header.setPreferredSize(new Dimension(0, 40));

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
                    c.setForeground(new Color(30, 130, 30)); 
                    c.setFont(new Font("Segoe UI", Font.BOLD, 14));
                } else {
                    c.setForeground(Color.RED); 
                    c.setFont(new Font("Segoe UI", Font.BOLD, 14));
                }
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        });
        
        table.getColumnModel().getColumn(6).setMinWidth(0);
        table.getColumnModel().getColumn(6).setMaxWidth(0);
        table.getColumnModel().getColumn(6).setWidth(0);

        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        pnlTable.add(scroll, BorderLayout.CENTER);
        add(pnlTable, BorderLayout.CENTER);

        // --- FOOTER (GIỎ HÀNG) ---
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        pnlFooter.setBackground(bgColor);
        
        JButton btnThemGio = new JButton("Thêm vào giỏ");
        setupButton(btnThemGio, new Color(255, 193, 7)); 
        btnThemGio.setForeground(Color.BLACK); 
        
        btnGioSach = new JButton("Giỏ sách (0)");
        setupButton(btnGioSach, new Color(40, 167, 69)); 

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
                        new GUI_DialogChiTietSach(SwingUtilities.getWindowAncestor(GUI_TraCuuSach.this), ma).setVisible(true);
                    }
                }
            }
        });

        btnThemGio.addActionListener(e -> xuLyThemVaoGio());
        btnGioSach.addActionListener(e -> xuLyXemGio());
    }

    private void setupButton(JButton btn, Color bg) {
        btn.setPreferredSize(new Dimension(150, 40));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        ArrayList<DTO_Sach> list = dal.searchSach(key, "Tất cả");
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
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sách cần mượn!");
            return;
        }

        int soLuongTon = Integer.parseInt(table.getValueAt(row, 6).toString());
        if (soLuongTon <= 0) {
            JOptionPane.showMessageDialog(this, "Sách này đã hết, không thể mượn!");
            return;
        }

        String maSach = table.getValueAt(row, 0).toString();
        
        for (DTO_Sach s : gioSach) {
            if (s.getMaCuonSach().equals(maSach)) {
                JOptionPane.showMessageDialog(this, "Bạn đã thêm sách này vào giỏ rồi!");
                return;
            }
        }
        
        if (gioSach.size() >= 5) {
            JOptionPane.showMessageDialog(this, "Chỉ được mượn tối đa 5 cuốn!");
            return;
        }

        DTO_Sach s = dal.getDetail(maSach);
        if (s != null) {
            gioSach.add(s);
            capNhatSoLuongGio(); // Gọi hàm cập nhật
            JOptionPane.showMessageDialog(this, "Đã thêm vào giỏ!");
        }
    }

    // [QUAN TRỌNG] Hàm này để GUI_DialogGioSach gọi về khi xóa sách
    public void capNhatSoLuongGio() {
        btnGioSach.setText("Giỏ sách (" + gioSach.size() + ")");
    }

    private void xuLyXemGio() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        String maDG = "";
        
        if (parent instanceof GUI_Main) {
             GUI_Main main = (GUI_Main) parent;
             TaiKhoan tk = main.getTaiKhoan(); 
             if (tk != null) {
                 maDG = tk.getMaDocGia();
             }
        }

        if (maDG == null || maDG.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chức năng này dành cho Độc giả!");
            return;
        }

        // [SỬA LẠI]: Truyền 'this' (GUI_TraCuuSach) vào
        GUI_DialogGioSach dialog = new GUI_DialogGioSach(parent, this, gioSach, maDG);
        dialog.setVisible(true);
        
        if(dialog.isDaGuiYeuCau()) {
            gioSach.clear();
            capNhatSoLuongGio(); // Cập nhật lại số lượng về 0
            loadData();
        }
    }
}