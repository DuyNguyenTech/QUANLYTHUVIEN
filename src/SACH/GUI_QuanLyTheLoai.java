package SACH;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class GUI_QuanLyTheLoai extends JPanel {

    // Màu sắc chủ đạo (Đồng bộ với GUI_QuanLySach)
    private Color mainColor = new Color(50, 115, 220);
    private Color bgColor = new Color(245, 248, 253);

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtMa, txtTen;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi;
    
    private DAL_TheLoai dal = new DAL_TheLoai();

    public GUI_QuanLyTheLoai() {
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

        JLabel lblTitle = new JLabel("QUẢN LÝ THỂ LOẠI SÁCH");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(mainColor);
        pnlHeader.add(lblTitle, BorderLayout.WEST);
        
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. CENTER (BẢNG DỮ LIỆU) ---
        JPanel pnlCenter = new JPanel(new BorderLayout());
        pnlCenter.setBackground(bgColor);
        pnlCenter.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Setup Bảng
        String[] cols = {"Mã Thể Loại", "Tên Thể Loại"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        
        // [STYLE PREMIUM]
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(232, 242, 252));
        table.setSelectionForeground(Color.BLACK);

        // Header Bảng
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(Color.WHITE);
        header.setForeground(mainColor);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, mainColor));
        header.setPreferredSize(new Dimension(0, 40));

        // Renderer chung (Căn giữa & Striped Rows)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
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
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        // Áp dụng renderer
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        pnlCenter.add(scrollPane, BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        // --- 3. BOTTOM (INPUT + BUTTONS) ---
        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setBackground(bgColor);
        pnlBottom.setBorder(new EmptyBorder(0, 20, 20, 20));
        
        // 3a. Panel Input (Nền trắng, bo góc)
        JPanel pnlInput = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 15));
        pnlInput.setBackground(Color.WHITE);
        pnlInput.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(10, 10, 10, 10)
        ));

        // Mã thể loại
        pnlInput.add(createLabel("Mã thể loại:"));
        txtMa = createTextField();
        pnlInput.add(txtMa);

        // Tên thể loại
        pnlInput.add(createLabel("Tên thể loại:"));
        txtTen = createTextField();
        pnlInput.add(txtTen);

        pnlBottom.add(pnlInput, BorderLayout.CENTER);

        // 3b. Panel Buttons (Dưới cùng)
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        pnlButtons.setBackground(bgColor);

        // [MÀU SẮC ĐỒNG BỘ VỚI CÁC MODULE KHÁC]
        btnThem = createButton("Thêm Thể Loại", new Color(40, 167, 69));   // Xanh lá
        btnSua = createButton("Sửa Thể Loại", new Color(255, 193, 7));    // Vàng cam
        btnXoa = createButton("Xóa Bỏ", new Color(220, 53, 69));      // Đỏ
        btnLamMoi = createButton("Làm Mới", new Color(23, 162, 184)); // Xanh Cyan

        pnlButtons.add(btnThem);
        pnlButtons.add(btnSua);
        pnlButtons.add(btnXoa);
        pnlButtons.add(btnLamMoi);

        pnlBottom.add(pnlButtons, BorderLayout.SOUTH);
        add(pnlBottom, BorderLayout.SOUTH);

        // --- EVENTS ---
        
        // Click bảng -> Đổ dữ liệu lên textfield
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtMa.setText(table.getValueAt(row, 0).toString());
                    txtTen.setText(table.getValueAt(row, 1).toString());
                    txtMa.setEditable(false); // Khóa mã không cho sửa
                    txtMa.setBackground(new Color(245, 245, 245));
                }
            }
        });

        // Nút Thêm
        btnThem.addActionListener(e -> {
            if (validateInput()) {
                if (dal.checkExist(txtMa.getText().trim())) {
                    JOptionPane.showMessageDialog(this, "Mã thể loại đã tồn tại!");
                    return;
                }
                DTO_TheLoai tl = new DTO_TheLoai(txtMa.getText().trim(), txtTen.getText().trim());
                if (dal.add(tl)) {
                    JOptionPane.showMessageDialog(this, "Thêm thành công!");
                    loadData();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm thất bại!");
                }
            }
        });

        // Nút Sửa
        btnSua.addActionListener(e -> {
            if (table.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn thể loại cần sửa!");
                return;
            }
            if (validateInput()) {
                DTO_TheLoai tl = new DTO_TheLoai(txtMa.getText().trim(), txtTen.getText().trim());
                if (dal.update(tl)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                    loadData();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
                }
            }
        });

        // Nút Xóa
        btnXoa.addActionListener(e -> {
            if (table.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn thể loại cần xóa!");
                return;
            }
            String ma = txtMa.getText();
            if (JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn xóa thể loại: " + ma + "?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (dal.delete(ma)) {
                    JOptionPane.showMessageDialog(this, "Đã xóa!");
                    loadData();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại! (Có thể sách đang dùng thể loại này)");
                }
            }
        });

        // Nút Làm mới
        btnLamMoi.addActionListener(e -> clearForm());
    }

    private void loadData() {
        model.setRowCount(0);
        ArrayList<DTO_TheLoai> list = dal.getList();
        for (DTO_TheLoai tl : list) {
            model.addRow(new Object[]{tl.getMaTheLoai(), tl.getTenTheLoai()});
        }
    }

    private void clearForm() {
        txtMa.setText("");
        txtTen.setText("");
        txtMa.setEditable(true);
        txtMa.setBackground(Color.WHITE);
        table.clearSelection();
        txtMa.requestFocus();
    }

    private boolean validateInput() {
        if (txtMa.getText().trim().isEmpty() || txtTen.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Mã và Tên thể loại!");
            return false;
        }
        return true;
    }

    // --- HELPER UI ---
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(new Color(50, 50, 50));
        return lbl;
    }

    private JTextField createTextField() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setPreferredSize(new Dimension(200, 35));
        // Bo viền và padding
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 8, 5, 8)
        ));
        return txt;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(150, 45)); // Kích thước cố định giống các module khác
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}