package DOCGIA;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI_DialogThemDocGia extends JDialog {

    private JTextField txtMa, txtTen, txtLop, txtDiaChi, txtSDT;
    private GUI_QuanLyDocGia parentGUI;
    private DTO_DocGia docGiaEdit = null; 

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
        setSize(500, 480);
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(new BorderLayout());

        // 1. HEADER
        String title = docGiaEdit == null ? "THÊM ĐỘC GIẢ MỚI" : "SỬA THÔNG TIN";
        Color headerColor = docGiaEdit == null ? new Color(50, 115, 220) : new Color(255, 152, 0);

        JLabel lblHeader = new JLabel(title, SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22)); // Header vẫn giữ đậm cho nổi bật
        lblHeader.setOpaque(true);
        lblHeader.setBackground(headerColor);
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setPreferredSize(new Dimension(0, 60));
        add(lblHeader, BorderLayout.NORTH);

        // 2. CONTENT
        JPanel pnlContent = new JPanel(new GridBagLayout());
        pnlContent.setBorder(new EmptyBorder(20, 30, 20, 30));
        pnlContent.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        // Khởi tạo các Textfield
        txtMa = new JTextField();
        txtTen = new JTextField();
        txtLop = new JTextField();
        txtDiaChi = new JTextField();
        txtSDT = new JTextField();

        addInputRow(pnlContent, gbc, row++, "Mã độc giả:", txtMa);
        addInputRow(pnlContent, gbc, row++, "Tên độc giả:", txtTen);
        addInputRow(pnlContent, gbc, row++, "Lớp:", txtLop);
        addInputRow(pnlContent, gbc, row++, "Địa chỉ:", txtDiaChi);
        addInputRow(pnlContent, gbc, row++, "Số điện thoại:", txtSDT);

        add(pnlContent, BorderLayout.CENTER);

        // 3. BOTTOM BUTTONS
        JPanel pnlButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        pnlButton.setBackground(Color.WHITE);
        pnlButton.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JButton btnLuu = new JButton(docGiaEdit == null ? "Lưu" : "Cập Nhật");
        styleButton(btnLuu, headerColor);
        
        JButton btnHuy = new JButton("Thoát");
        styleButton(btnHuy, new Color(220, 53, 69));

        if (docGiaEdit == null) {
            JButton btnLamMoi = new JButton("Làm Mới");
            styleButton(btnLamMoi, new Color(40, 167, 69));
            btnLamMoi.addActionListener(e -> clearForm());
            pnlButton.add(btnLamMoi);
        }

        btnLuu.addActionListener(e -> xuLyLuu());
        btnHuy.addActionListener(e -> dispose());

        pnlButton.add(btnLuu);
        pnlButton.add(btnHuy);
        add(pnlButton, BorderLayout.SOUTH);

        // --- XỬ LÝ PHÍM ENTER THÔNG MINH ---
        setupEnterKey(txtMa, txtTen);
        setupEnterKey(txtTen, txtLop);
        setupEnterKey(txtLop, txtDiaChi);
        setupEnterKey(txtDiaChi, txtSDT);
        
        // Với ô cuối cùng (SĐT): Enter = Lưu
        txtSDT.addActionListener(e -> xuLyLuu());
    }

    // Hàm tiện ích: Bấm Enter ở txtCurrent sẽ nhảy sang txtNext
    private void setupEnterKey(JTextField txtCurrent, JTextField txtNext) {
        txtCurrent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtNext.requestFocus(); // Chuyển focus sang ô kế tiếp
            }
        });
    }

    private void addInputRow(JPanel p, GridBagConstraints gbc, int row, String label, JTextField txt) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel lbl = new JLabel(label);
        
        // --- [SỬA LẠI]: Font thường (PLAIN) thay vì BOLD ---
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14)); 
        
        p.add(lbl, gbc);

        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setPreferredSize(new Dimension(220, 35));
        
        // Thêm padding cho textfield nhìn đẹp hơn
        txt.setBorder(BorderFactory.createCompoundBorder(
            txt.getBorder(), 
            BorderFactory.createEmptyBorder(2, 5, 2, 5)));
            
        p.add(txt, gbc);
    }

    private void fillData() {
        txtMa.setText(docGiaEdit.getMaDocGia());
        txtMa.setEditable(false);
        txtMa.setBackground(new Color(245, 245, 245));
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
        // 1. Validate: Kiểm tra rỗng TẤT CẢ các trường
        if (txtMa.getText().trim().isEmpty() || 
            txtTen.getText().trim().isEmpty() || 
            txtLop.getText().trim().isEmpty() || 
            txtDiaChi.getText().trim().isEmpty() || 
            txtSDT.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ tất cả thông tin!");
            return;
        }

        // 2. Validate: Số điện thoại phải là 10 số
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
            // THÊM MỚI
            if (dal.add(dg)) {
                JOptionPane.showMessageDialog(this, "Thêm thành công!");
                parentGUI.loadData(); 
                clearForm(); // Xóa trắng để nhập tiếp
            } else {
                JOptionPane.showMessageDialog(this, "Thêm thất bại (Mã độc giả đã tồn tại)!");
            }
        } else {
            // CẬP NHẬT
            if (dal.update(dg)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                parentGUI.loadData();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
            }
        }
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(100, 40));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}