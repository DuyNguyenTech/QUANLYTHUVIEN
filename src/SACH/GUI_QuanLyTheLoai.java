package SACH;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class GUI_QuanLyTheLoai extends JPanel {

    // Màu sắc chủ đạo (Đồng bộ toàn hệ thống)
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
        setBorder(new EmptyBorder(20, 20, 20, 20)); // Căn lề form thoáng

        // --- 1. HEADER ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(bgColor);
        pnlHeader.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel lblTitle = new JLabel("QUẢN LÝ THỂ LOẠI SÁCH");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(mainColor);
        pnlHeader.add(lblTitle, BorderLayout.WEST);
        
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. CENTER (BẢNG DỮ LIỆU TRONG THẺ CARD) ---
        JPanel pnlTableCard = new JPanel(new BorderLayout());
        pnlTableCard.setBackground(Color.WHITE);
        pnlTableCard.putClientProperty(FlatClientProperties.STYLE, "arc: 20; border: 1,1,1,1, #E0E0E0");
        pnlTableCard.setBorder(new EmptyBorder(10, 10, 10, 10));

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
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setBackground(new Color(248, 249, 250));
        header.setForeground(new Color(50, 50, 50));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        header.setPreferredSize(new Dimension(0, 45));

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
        
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        pnlTableCard.add(scrollPane, BorderLayout.CENTER);
        add(pnlTableCard, BorderLayout.CENTER);

        // --- 3. BOTTOM (INPUT + BUTTONS) ---
        JPanel pnlBottom = new JPanel(new BorderLayout(0, 15));
        pnlBottom.setBackground(bgColor);
        pnlBottom.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        // 3a. Panel Input (Nền trắng, bo góc 20px)
        JPanel pnlInput = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
        pnlInput.setBackground(Color.WHITE);
        pnlInput.putClientProperty(FlatClientProperties.STYLE, "arc: 20; border: 1,1,1,1, #E0E0E0");

        // Mã thể loại
        pnlInput.add(createLabel("Mã thể loại:"));
        txtMa = createTextField(); // Đã bỏ chữ mờ hoàn toàn
        pnlInput.add(txtMa);

        // Tên thể loại
        pnlInput.add(createLabel("Tên thể loại:"));
        txtTen = createTextField(); // Đã bỏ chữ mờ hoàn toàn
        pnlInput.add(txtTen);

        pnlBottom.add(pnlInput, BorderLayout.NORTH);

     // 3b. Panel Buttons - [ĐÃ SỬA: Thay FlowLayout.RIGHT bằng FlowLayout.CENTER để căn giữa]
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        pnlButtons.setBackground(bgColor);

        btnThem = createButton("Thêm Mới", new Color(40, 167, 69), Color.WHITE);
        btnSua = createButton("Cập Nhật", new Color(255, 193, 7), Color.WHITE); 
        btnXoa = createButton("Xóa Bỏ", new Color(220, 53, 69), Color.WHITE);
        btnLamMoi = createButton("Làm Mới", new Color(23, 162, 184), Color.WHITE);

        pnlButtons.add(btnThem);
        pnlButtons.add(btnSua);
        pnlButtons.add(btnXoa);
        pnlButtons.add(btnLamMoi);

        pnlBottom.add(pnlButtons, BorderLayout.SOUTH);
        add(pnlBottom, BorderLayout.SOUTH);

        // --- EVENTS ---
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtMa.setText(table.getValueAt(row, 0).toString());
                    txtTen.setText(table.getValueAt(row, 1).toString());
                    txtMa.setEditable(false); 
                    txtMa.setBackground(new Color(240, 240, 240));
                    txtMa.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0");
                }
            }
        });

        btnThem.addActionListener(e -> {
            if (validateInput()) {
                if (dal.checkExist(txtMa.getText().trim())) {
                    JOptionPane.showMessageDialog(this, "Mã thể loại này đã tồn tại!");
                    return;
                }
                DTO_TheLoai tl = new DTO_TheLoai(txtMa.getText().trim(), txtTen.getText().trim());
                if (dal.add(tl)) {
                    JOptionPane.showMessageDialog(this, "Thêm thể loại thành công!");
                    loadData();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi khi thêm thể loại!");
                }
            }
        });

        btnSua.addActionListener(e -> {
            if (table.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn thể loại trong bảng!");
                return;
            }
            if (validateInput()) {
                DTO_TheLoai tl = new DTO_TheLoai(txtMa.getText().trim(), txtTen.getText().trim());
                if (dal.update(tl)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật dữ liệu thành công!");
                    loadData();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
                }
            }
        });

        btnXoa.addActionListener(e -> {
            if (table.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần xóa!");
                return;
            }
            String ma = txtMa.getText();
            if (JOptionPane.showConfirmDialog(this, "Bạn chắc chắn muốn xóa thể loại: " + ma + "?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (dal.delete(ma)) {
                    JOptionPane.showMessageDialog(this, "Đã xóa thành công!");
                    loadData();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại! Thể loại này có thể đang chứa sách.");
                }
            }
        });

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
        txtMa.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderColor: #cccccc; focusedBorderColor: #1877F2; borderWidth: 1");
        table.clearSelection();
        txtMa.requestFocus();
    }

    private boolean validateInput() {
        if (txtMa.getText().trim().isEmpty() || txtTen.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng không để trống Mã hoặc Tên!");
            return false;
        }
        return true;
    }

    // --- HELPER UI ---
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(new Color(70, 70, 70));
        return lbl;
    }

    private JTextField createTextField() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txt.setPreferredSize(new Dimension(250, 40));
        // Đã bỏ FlatClientProperties.PLACEHOLDER_TEXT
        txt.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderColor: #cccccc; focusedBorderColor: #1877F2; borderWidth: 1");
        return txt;
    }

    private JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(150, 42)); 
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0");
        return btn;
    }
}