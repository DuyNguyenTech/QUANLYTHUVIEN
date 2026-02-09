package SACH;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

public class GUI_DialogThemSach extends JDialog {

    private GUI_QuanLySach parentGUI;
    private String maSachSua = null;
    private DAL_Sach dal = new DAL_Sach();
    private DAL_TheLoai dalTheLoai = new DAL_TheLoai(); 
    
    private JTextField txtMa, txtTen, txtTacGia, txtNXB, txtNamXB, txtGia, txtSoLuong;
    private JComboBox<String> cboTheLoai; 
    private JTextArea txtMoTa;
    private JLabel lblHinhAnh;
    private String duongDanAnh = "";
    
    // Màu chủ đạo
    private Color mainColor = new Color(50, 115, 220); 

    public GUI_DialogThemSach(Window parent, GUI_QuanLySach parentGUI, String maSachSua) {
        super(parent, ModalityType.APPLICATION_MODAL);
        this.parentGUI = parentGUI;
        this.maSachSua = maSachSua;
        initUI();
        loadComboboxTheLoai(); 
        if(maSachSua != null) loadDataSua();
    }

    private void initUI() {
        setTitle(maSachSua == null ? "THÊM SÁCH MỚI" : "CẬP NHẬT SÁCH");
        setSize(950, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false);

        // --- 1. HEADER TITLE ---
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlHeader.setBackground(mainColor);
        pnlHeader.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JLabel lblHeader = new JLabel(maSachSua == null ? "THÊM SÁCH MỚI VÀO KHO" : "SỬA THÔNG TIN SÁCH");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeader.setForeground(Color.WHITE);
        pnlHeader.add(lblHeader);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. CONTENT (CENTER) ---
        JPanel pnlContent = new JPanel(new BorderLayout(15, 15));
        pnlContent.setBorder(new EmptyBorder(20, 20, 20, 20));
        pnlContent.setBackground(Color.WHITE);

        // === PANEL TRÁI: FORM NHẬP LIỆU ===
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(Color.WHITE);
        pnlForm.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            "Thông tin chi tiết", 
            TitledBorder.DEFAULT_JUSTIFICATION, 
            TitledBorder.DEFAULT_POSITION, 
            new Font("Segoe UI", Font.BOLD, 14), 
            mainColor
        ));

        // Init Components
        txtMa = createTextField();
        if(maSachSua != null) {
            txtMa.setEditable(false);
            txtMa.setBackground(new Color(245, 245, 245));
        }
        
        txtTen = createTextField();
        txtTacGia = createTextField();
        txtNXB = createTextField();
        txtNamXB = createTextField();
        txtGia = createTextField();
        txtSoLuong = createTextField();
        
        cboTheLoai = new JComboBox<>();
        cboTheLoai.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboTheLoai.setBackground(Color.WHITE);
        cboTheLoai.setPreferredSize(new Dimension(200, 35));

        // Add to GridBag
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Cột 1
        gbc.gridx=0; gbc.gridy=0; pnlForm.add(createLabel("Mã Sách:"), gbc);
        gbc.gridx=1; gbc.gridy=0; gbc.weightx=1.0; pnlForm.add(txtMa, gbc);
        
        gbc.gridx=0; gbc.gridy=1; gbc.weightx=0.0; pnlForm.add(createLabel("Tên Sách:"), gbc);
        gbc.gridx=1; gbc.gridy=1; gbc.weightx=1.0; pnlForm.add(txtTen, gbc);
        
        gbc.gridx=0; gbc.gridy=2; gbc.weightx=0.0; pnlForm.add(createLabel("Tác Giả:"), gbc);
        gbc.gridx=1; gbc.gridy=2; gbc.weightx=1.0; pnlForm.add(txtTacGia, gbc);
        
        gbc.gridx=0; gbc.gridy=3; gbc.weightx=0.0; pnlForm.add(createLabel("Nhà Xuất Bản:"), gbc);
        gbc.gridx=1; gbc.gridy=3; gbc.weightx=1.0; pnlForm.add(txtNXB, gbc);

        // Cột 2
        gbc.gridx=2; gbc.gridy=0; gbc.weightx=0.0; pnlForm.add(createLabel("Thể Loại:"), gbc);
        gbc.gridx=3; gbc.gridy=0; gbc.weightx=1.0; pnlForm.add(cboTheLoai, gbc);
        
        gbc.gridx=2; gbc.gridy=1; gbc.weightx=0.0; pnlForm.add(createLabel("Năm Xuất Bản:"), gbc);
        gbc.gridx=3; gbc.gridy=1; gbc.weightx=1.0; pnlForm.add(txtNamXB, gbc);
        
        gbc.gridx=2; gbc.gridy=2; gbc.weightx=0.0; pnlForm.add(createLabel("Giá:"), gbc);
        gbc.gridx=3; gbc.gridy=2; gbc.weightx=1.0; pnlForm.add(txtGia, gbc);
        
        gbc.gridx=2; gbc.gridy=3; gbc.weightx=0.0; pnlForm.add(createLabel("Số Lượng Nhập:"), gbc);
        gbc.gridx=3; gbc.gridy=3; gbc.weightx=1.0; pnlForm.add(txtSoLuong, gbc);

        // Hàng Mô tả (Chiếm toàn bộ chiều ngang)
        gbc.gridx=0; gbc.gridy=4; 
        gbc.weightx=0.0; 
        gbc.weighty=0.0; // Reset weight
        gbc.anchor=GridBagConstraints.NORTHWEST; 
        pnlForm.add(createLabel("Mô Tả:"), gbc);
        
        // Tăng kích thước ô nhập liệu mô tả
        txtMoTa = new JTextArea(8, 20); // Tăng lên 8 dòng
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        txtMoTa.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtMoTa.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JScrollPane scMoTa = new JScrollPane(txtMoTa);
        scMoTa.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        gbc.gridx=1; gbc.gridy=4; 
        gbc.gridwidth=3; 
        gbc.weightx=1.0; 
        gbc.weighty=1.0; // Cho phép giãn chiều cao
        gbc.fill=GridBagConstraints.BOTH; 
        pnlForm.add(scMoTa, gbc);

        // === PANEL PHẢI: ẢNH BÌA ===
        JPanel pnlImage = new JPanel(new BorderLayout(10, 10));
        pnlImage.setBackground(Color.WHITE);
        pnlImage.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            "Ảnh bìa sách", 
            TitledBorder.DEFAULT_JUSTIFICATION, 
            TitledBorder.DEFAULT_POSITION, 
            new Font("Segoe UI", Font.BOLD, 14), 
            mainColor
        ));
        pnlImage.setPreferredSize(new Dimension(250, 0));

        lblHinhAnh = new JLabel("Chưa chọn ảnh", SwingConstants.CENTER);
        lblHinhAnh.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblHinhAnh.setForeground(Color.GRAY);
        lblHinhAnh.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        lblHinhAnh.setBackground(new Color(245, 248, 253));
        lblHinhAnh.setOpaque(true);

        JButton btnChonAnh = createButton("Chọn Ảnh", new Color(255, 193, 7), Color.WHITE);
        btnChonAnh.setPreferredSize(new Dimension(0, 40));

        pnlImage.add(lblHinhAnh, BorderLayout.CENTER);
        pnlImage.add(btnChonAnh, BorderLayout.SOUTH);

        pnlContent.add(pnlForm, BorderLayout.CENTER);
        pnlContent.add(pnlImage, BorderLayout.EAST);
        add(pnlContent, BorderLayout.CENTER);

        // --- 3. FOOTER BUTTONS ---
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        pnlBot.setBackground(new Color(245, 248, 253));
        pnlBot.setBorder(new EmptyBorder(0, 20, 10, 20));
        
        JButton btnLuu = createButton(maSachSua == null ? "Lưu Sách" : "Cập Nhật", mainColor, Color.WHITE);
        JButton btnLamMoi = createButton("Làm Mới", new Color(23, 162, 184), Color.WHITE);
        JButton btnHuy = createButton("Đóng", new Color(220, 53, 69), Color.WHITE);
        
        pnlBot.add(btnLuu);
        pnlBot.add(btnLamMoi);
        pnlBot.add(btnHuy);
        add(pnlBot, BorderLayout.SOUTH);

        // --- EVENTS ---
        btnChonAnh.addActionListener(e -> chonAnh());
        btnLuu.addActionListener(e -> xuLyLuu());
        btnLamMoi.addActionListener(e -> xoaForm());
        btnHuy.addActionListener(e -> dispose());

        setupEnterNavigation();
    }

    // --- HELPER UI METHODS ---
    private JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setPreferredSize(new Dimension(200, 35));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 8, 5, 8)
        ));
        return tf;
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(80, 80, 80));
        return lbl;
    }

    private JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(120, 40));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // --- LOGIC ---

    private void setupEnterNavigation() {
        JTextField[] fields = {txtMa, txtTen, txtTacGia, txtGia, txtNamXB, txtNXB, txtSoLuong};
        for (JTextField tf : fields) {
            tf.addActionListener(e -> tf.transferFocus());
        }
        cboTheLoai.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) cboTheLoai.transferFocus();
            }
        });
    }

    private void loadComboboxTheLoai() {
        cboTheLoai.removeAllItems();
        ArrayList<DTO_TheLoai> list = dalTheLoai.getList();
        for(DTO_TheLoai tl : list) {
            cboTheLoai.addItem(tl.getMaTheLoai() + " - " + tl.getTenTheLoai());
        }
    }

    private void chonAnh() {
        JFileChooser chooser = new JFileChooser();
        if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            duongDanAnh = file.getAbsolutePath();
            hienThiAnh(duongDanAnh);
        }
    }

    private void hienThiAnh(String path) {
        try {
            ImageIcon icon = new ImageIcon(path);
            Image img = icon.getImage().getScaledInstance(200, 250, Image.SCALE_SMOOTH);
            lblHinhAnh.setIcon(new ImageIcon(img));
            lblHinhAnh.setText("");
        } catch (Exception e) {
            lblHinhAnh.setIcon(null);
            lblHinhAnh.setText("Lỗi hiển thị ảnh");
        }
    }

    private void loadDataSua() {
        DTO_Sach s = dal.getDetail(maSachSua);
        if(s != null) {
            txtMa.setText(s.getMaCuonSach());
            txtTen.setText(s.getTenSach());
            txtTacGia.setText(s.getTacGia());
            txtNXB.setText(s.getNhaXuatBan());
            txtNamXB.setText(String.valueOf(s.getNamXuatBan()));
            txtGia.setText(String.valueOf((long)s.getGia()));
            txtSoLuong.setText(String.valueOf(s.getSoLuong()));
            txtMoTa.setText(s.getMoTa());
            
            for(int i=0; i<cboTheLoai.getItemCount(); i++) {
                String item = cboTheLoai.getItemAt(i);
                if(item.startsWith(s.getMaTheLoai())) {
                    cboTheLoai.setSelectedIndex(i);
                    break;
                }
            }

            duongDanAnh = s.getHinhAnh();
            if(duongDanAnh != null && !duongDanAnh.isEmpty()) {
                hienThiAnh(duongDanAnh);
            }
        }
    }

    // [CẬP NHẬT] Tách biệt thông báo cho Thêm và Sửa
    private void xuLyLuu() {
        if(txtMa.getText().trim().isEmpty() || txtTen.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã sách và Tên sách không được để trống!"); return;
        }

        DTO_Sach s = new DTO_Sach();
        s.setMaCuonSach(txtMa.getText().trim());
        s.setMaSach(txtMa.getText().trim());
        s.setTenSach(txtTen.getText().trim());
        s.setTacGia(txtTacGia.getText().trim());
        s.setNhaXuatBan(txtNXB.getText().trim());
        s.setMoTa(txtMoTa.getText().trim());
        s.setHinhAnh(duongDanAnh); 
        
        String selectedTL = (String) cboTheLoai.getSelectedItem();
        if(selectedTL != null) {
            s.setMaTheLoai(selectedTL.split(" - ")[0].trim()); 
        }

        try {
            s.setNamXuatBan(Integer.parseInt(txtNamXB.getText().trim()));
            s.setGia(Double.parseDouble(txtGia.getText().trim()));
            s.setSoLuong(Integer.parseInt(txtSoLuong.getText().trim()));
        } catch(NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Năm XB, Giá và Số lượng phải là số hợp lệ!"); return;
        }

        if(maSachSua == null) { // Trường hợp THÊM SÁCH
            if(dal.getDetail(s.getMaCuonSach()) != null) {
                JOptionPane.showMessageDialog(this, "Mã sách " + s.getMaCuonSach() + " đã tồn tại!"); 
                return;
            }
            if(dal.addSach(s)) {
                JOptionPane.showMessageDialog(this, "Thêm sách thành công!");
                parentGUI.loadData();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm sách thất bại! Vui lòng kiểm tra lại.");
            }
        } else { // Trường hợp SỬA SÁCH
            if(dal.updateSach(s)) {
                JOptionPane.showMessageDialog(this, "Sửa sách thành công!");
                parentGUI.loadData();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Sửa sách thất bại! Vui lòng kiểm tra lại.");
            }
        }
    }
    
    private void xoaForm() {
        if(maSachSua == null) txtMa.setText("");
        txtTen.setText(""); txtTacGia.setText(""); 
        txtNXB.setText(""); txtNamXB.setText(""); txtGia.setText(""); 
        txtSoLuong.setText(""); txtMoTa.setText("");
        lblHinhAnh.setIcon(null); lblHinhAnh.setText("Chưa chọn ảnh");
        duongDanAnh = "";
        txtMa.requestFocus();
    }
}