package DOCGIA;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GUI_DialogThemDocGia extends JDialog {

    private JTextField txtMa, txtTen, txtLop, txtDiaChi, txtSDT;
    private GUI_QuanLyDocGia parentGUI;
    private DTO_DocGia docGiaEdit = null;
    
    // Màu chủ đạo
    private Color mainColor = new Color(50, 115, 220);

    public GUI_DialogThemDocGia(GUI_QuanLyDocGia parent) {
        this.parentGUI = parent;
        this.docGiaEdit = null;
        initUI();
    }

    public GUI_DialogThemDocGia(GUI_QuanLyDocGia parent, DTO_DocGia dg) {
        this.parentGUI = parent;
        this.docGiaEdit = dg;
        initUI();
        fillData();
    }

    private void initUI() {
        setTitle(docGiaEdit == null ? "THÊM ĐỘC GIẢ" : "CẬP NHẬT ĐỘC GIẢ");
        setSize(500, 520); // Tăng chiều cao một chút cho thoáng
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(new BorderLayout());
        setResizable(false);

        // 1. HEADER
        String title = docGiaEdit == null ? "THÊM ĐỘC GIẢ MỚI" : "CẬP NHẬT THÔNG TIN";
        Color headerColor = docGiaEdit == null ? mainColor : new Color(255, 152, 0); // Xanh khi thêm, Cam khi sửa

        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlHeader.setBackground(headerColor);
        pnlHeader.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JLabel lblHeader = new JLabel(title.toUpperCase());
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeader.setForeground(Color.WHITE);
        pnlHeader.add(lblHeader);
        add(pnlHeader, BorderLayout.NORTH);

        // 2. CONTENT (INPUT FORM)
        JPanel pnlContent = new JPanel(new GridBagLayout());
        pnlContent.setBackground(Color.WHITE);
        pnlContent.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(15, 15, 15, 15),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Thông tin chi tiết",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 14), headerColor
            )
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Khởi tạo các Textfield với style đẹp
        txtMa = createTextField();
        txtTen = createTextField();
        txtLop = createTextField();
        txtDiaChi = createTextField();
        txtSDT = createTextField();

        int row = 0;
        addInputRow(pnlContent, gbc, row++, "Mã độc giả:", txtMa);
        addInputRow(pnlContent, gbc, row++, "Tên độc giả:", txtTen);
        addInputRow(pnlContent, gbc, row++, "Lớp:", txtLop);
        addInputRow(pnlContent, gbc, row++, "Địa chỉ:", txtDiaChi);
        addInputRow(pnlContent, gbc, row++, "Số điện thoại:", txtSDT);

        add(pnlContent, BorderLayout.CENTER);

        // 3. BOTTOM BUTTONS
        JPanel pnlButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        pnlButton.setBackground(Color.WHITE);
        pnlButton.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JButton btnLuu = createButton(docGiaEdit == null ? "Lưu Lại" : "Cập Nhật", headerColor);
        JButton btnHuy = createButton("Đóng", new Color(220, 53, 69));

        if (docGiaEdit == null) {
            JButton btnLamMoi = createButton("Làm Mới", new Color(23, 162, 184));
            btnLamMoi.addActionListener(e -> clearForm());
            pnlButton.add(btnLamMoi);
        }

        btnLuu.addActionListener(e -> xuLyLuu());
        btnHuy.addActionListener(e -> dispose());

        pnlButton.add(btnLuu);
        pnlButton.add(btnHuy);
        add(pnlButton, BorderLayout.SOUTH);

        // --- Kích hoạt phím Enter để chuyển ô ---
        setupEnterNavigation();
    }

    private void addInputRow(JPanel p, GridBagConstraints gbc, int row, String label, JTextField txt) {
        gbc.gridx = 0; gbc.gridy = row; 
        gbc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13)); // Label đậm nhẹ
        lbl.setForeground(new Color(80, 80, 80));
        p.add(lbl, gbc);

        gbc.gridx = 1; gbc.gridy = row; 
        gbc.weightx = 1.0;
        p.add(txt, gbc);
    }

    // Hàm tạo TextField chuẩn style
    private JTextField createTextField() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setPreferredSize(new Dimension(220, 35));
        // Padding bên trong
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return txt;
    }

    // Hàm tạo Button chuẩn style
    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(120, 40));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Logic điều hướng Enter
    private void setupEnterNavigation() {
        JTextField[] fields = {txtMa, txtTen, txtLop, txtDiaChi, txtSDT};
        for (JTextField tf : fields) {
            tf.addActionListener(e -> tf.transferFocus()); // Tự động nhảy sang component kế tiếp
        }
        // Riêng ô cuối cùng (SĐT) nhấn Enter sẽ gọi hàm Lưu
        txtSDT.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) xuLyLuu();
            }
        });
    }

    private void fillData() {
        txtMa.setText(docGiaEdit.getMaDocGia());
        txtMa.setEditable(false);
        txtMa.setBackground(new Color(245, 245, 245)); // Xám nhẹ để biết là readonly
        txtTen.setText(docGiaEdit.getTenDocGia());
        txtLop.setText(docGiaEdit.getLop());
        txtDiaChi.setText(docGiaEdit.getDiaChi());
        txtSDT.setText(docGiaEdit.getSoDienThoai());
    }

    private void clearForm() {
        txtMa.setText("");
        txtTen.setText("");
        txtLop.setText("");
        txtDiaChi.setText("");
        txtSDT.setText("");
        txtMa.requestFocus(); 
    }

    private void xuLyLuu() {
        // 1. Validate Rỗng
        if (txtMa.getText().trim().isEmpty() || 
            txtTen.getText().trim().isEmpty() || 
            txtLop.getText().trim().isEmpty() || 
            txtDiaChi.getText().trim().isEmpty() || 
            txtSDT.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ tất cả thông tin!");
            return;
        }

        // 2. Validate Số điện thoại (10 chữ số)
        String sdt = txtSDT.getText().trim();
        if (!sdt.matches("\\d{10}")) { 
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ (Phải đúng 10 chữ số)!");
            txtSDT.requestFocus();
            return;
        }

        DTO_DocGia dg = new DTO_DocGia(
            txtMa.getText().trim(), 
            txtTen.getText().trim(), 
            txtLop.getText().trim(), 
            txtDiaChi.getText().trim(), 
            sdt
        );
        DAL_DocGia dal = new DAL_DocGia();

        if (docGiaEdit == null) {
            // --- TRƯỜNG HỢP THÊM MỚI ---
            if (dal.add(dg)) {
                JOptionPane.showMessageDialog(this, "Thêm độc giả thành công!");
                parentGUI.loadData(); 
                clearForm(); 
            } else {
                JOptionPane.showMessageDialog(this, "Thêm thất bại (Mã độc giả có thể đã tồn tại)!");
            }
        } else {
            // --- TRƯỜNG HỢP CẬP NHẬT ---
            if (dal.update(dg)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công!");
                parentGUI.loadData();
                dispose(); // Đóng form
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
            }
        }
    }
}