package DOCGIA;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GUI_DialogThemDocGia extends JDialog {

    private JTextField txtMa, txtTen, txtLop, txtDiaChi, txtSDT;
    private GUI_QuanLyDocGia parentGUI;
    private DTO_DocGia docGiaEdit = null;
    
    // Màu chủ đạo
    private Color mainColor = new Color(50, 115, 220);
    private Color bgColor = new Color(245, 248, 253);

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
        setSize(550, 580); // Nới rộng kích thước cho thoáng
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(new BorderLayout());
        setResizable(false);
        getContentPane().setBackground(bgColor);

        // 1. HEADER
        String titleText = docGiaEdit == null ? "THÊM ĐỘC GIẢ MỚI" : "CẬP NHẬT THÔNG TIN";
        Color headerColor = docGiaEdit == null ? mainColor : new Color(255, 152, 0); 

        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlHeader.setBackground(headerColor);
        pnlHeader.setBorder(new EmptyBorder(15, 0, 15, 0));
        
        JLabel lblHeader = new JLabel(titleText.toUpperCase());
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(Color.WHITE);
        pnlHeader.add(lblHeader);
        add(pnlHeader, BorderLayout.NORTH);

        // 2. CONTENT (Bọc trong Card trắng bo góc)
        JPanel pnlWrapper = new JPanel(new BorderLayout());
        pnlWrapper.setBackground(bgColor);
        pnlWrapper.setBorder(new EmptyBorder(25, 25, 15, 25));

        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(Color.WHITE);
        // Bo góc Card 20px
        pnlForm.putClientProperty(FlatClientProperties.STYLE, "arc: 20; border: 1,1,1,1, #E0E0E0");
        
        // Tiêu đề nội bộ
        JLabel lblFormTitle = new JLabel("Thông Tin Chi Tiết");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblFormTitle.setForeground(headerColor);
        lblFormTitle.setBorder(new EmptyBorder(20, 20, 5, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 20, 12, 20); // Dãn cách dòng rộng rãi
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Khởi tạo các Textfield (KHÔNG CÓ CHỮ MỜ)
        txtMa = createTextField();
        txtTen = createTextField();
        txtLop = createTextField();
        txtDiaChi = createTextField();
        txtSDT = createTextField();

        int row = 0;
        addInputRow(pnlForm, gbc, row++, "Mã độc giả:", txtMa);
        addInputRow(pnlForm, gbc, row++, "Tên độc giả:", txtTen);
        addInputRow(pnlForm, gbc, row++, "Lớp học:", txtLop);
        addInputRow(pnlForm, gbc, row++, "Địa chỉ:", txtDiaChi);
        addInputRow(pnlForm, gbc, row++, "Số điện thoại:", txtSDT);

        // Panel chứa tiêu đề + form
        JPanel pnlCardContainer = new JPanel(new BorderLayout());
        pnlCardContainer.setBackground(Color.WHITE);
        pnlCardContainer.putClientProperty(FlatClientProperties.STYLE, "arc: 20");
        pnlCardContainer.add(lblFormTitle, BorderLayout.NORTH);
        pnlCardContainer.add(pnlForm, BorderLayout.CENTER);

        pnlWrapper.add(pnlCardContainer, BorderLayout.CENTER);
        add(pnlWrapper, BorderLayout.CENTER);

        // 3. BOTTOM BUTTONS
        JPanel pnlButton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        pnlButton.setBackground(bgColor);
        pnlButton.setBorder(new EmptyBorder(0, 0, 10, 5));
        
        JButton btnLuu = createButton(docGiaEdit == null ? "Lưu Lại" : "Cập Nhật", headerColor);
        JButton btnHuy = createButton("Hủy Bỏ", new Color(220, 53, 69));

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

        setupEnterNavigation();
    }

    private void addInputRow(JPanel p, GridBagConstraints gbc, int row, String label, JTextField txt) {
        gbc.gridx = 0; gbc.gridy = row; 
        gbc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(new Color(80, 80, 80));
        lbl.setPreferredSize(new Dimension(110, 30)); // Cố định nhãn cho thẳng hàng
        p.add(lbl, gbc);

        gbc.gridx = 1; gbc.gridy = row; 
        gbc.weightx = 1.0;
        p.add(txt, gbc);
    }

    private JTextField createTextField() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txt.setPreferredSize(new Dimension(280, 42));
        // Bo góc & Viền chuẩn FlatLaf - KHÔNG CHỮ MỜ
        txt.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderColor: #cccccc; focusedBorderColor: #1877F2; borderWidth: 1");
        return txt;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(130, 45));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0");
        return btn;
    }

    private void setupEnterNavigation() {
        JTextField[] fields = {txtMa, txtTen, txtLop, txtDiaChi, txtSDT};
        for (JTextField tf : fields) {
            tf.addActionListener(e -> tf.transferFocus());
        }
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
        txtMa.setBackground(new Color(240, 240, 240));
        txtMa.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0");
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
        if (txtMa.getText().trim().isEmpty() || 
            txtTen.getText().trim().isEmpty() || 
            txtLop.getText().trim().isEmpty() || 
            txtDiaChi.getText().trim().isEmpty() || 
            txtSDT.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ tất cả thông tin!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sdt = txtSDT.getText().trim();
        if (!sdt.matches("\\d{10}")) { 
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ (Phải bao gồm đúng 10 chữ số)!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
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
            if (dal.add(dg)) {
                JOptionPane.showMessageDialog(this, "Thêm độc giả thành công!");
                parentGUI.loadData(); 
                clearForm(); 
            } else {
                JOptionPane.showMessageDialog(this, "Thêm thất bại (Mã độc giả có thể đã tồn tại)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            if (dal.update(dg)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công!");
                parentGUI.loadData();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}