package com.qlthuvien.GUI;

import com.qlthuvien.DAL.DAL_Sach;
import com.qlthuvien.DAL.DAL_TheLoai; 
import com.qlthuvien.DTO.DTO_Sach;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.KeyAdapter; // Import thêm
import java.awt.event.KeyEvent;   // Import thêm
import java.io.File;
import java.util.ArrayList;

public class GUI_DialogThemSach extends JDialog {

    private GUI_QuanLySach parentGUI;
    private String maSachSua = null;
    private DAL_Sach dal = new DAL_Sach();
    private DAL_TheLoai dalTheLoai = new DAL_TheLoai(); 
    
    private JTextField txtMa, txtTen, txtTacGia, txtNXB, txtNamXB, txtGia, txtSoLuong;
    private JComboBox<String> cboTheLoai; 
    private JComboBox<String> cboTrangThai; 
    private JTextArea txtMoTa;
    private JLabel lblHinhAnh;
    private String duongDanAnh = "";

    public GUI_DialogThemSach(Window parent, GUI_QuanLySach parentGUI, String maSachSua) {
        super(parent, ModalityType.APPLICATION_MODAL);
        this.parentGUI = parentGUI;
        this.maSachSua = maSachSua;
        initUI();
        loadComboboxTheLoai(); 
        if(maSachSua != null) loadDataSua();
    }

    private void initUI() {
        setTitle(maSachSua == null ? "THÊM SÁCH MỚI" : "CẬP NHẬT THÔNG TIN SÁCH");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- 1. PANEL INPUT (THÔNG TIN) ---
        JPanel pnlInfo = new JPanel(new GridBagLayout());
        pnlInfo.setBorder(BorderFactory.createTitledBorder(null, "Thông tin sách", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Segoe UI", Font.BOLD, 14), Color.BLUE));
        pnlInfo.setBackground(Color.WHITE);

        txtMa = new JTextField(15);
        if(maSachSua != null) txtMa.setEditable(false);
        
        txtTen = new JTextField(15);
        txtTacGia = new JTextField(15);
        txtNXB = new JTextField(15);
        txtNamXB = new JTextField(15);
        txtGia = new JTextField(15);
        txtSoLuong = new JTextField(15);
        
        // ComboBox Thể Loại
        cboTheLoai = new JComboBox<>();
        cboTheLoai.setBackground(Color.WHITE);

        // ComboBox Trạng Thái
        cboTrangThai = new JComboBox<>(new String[]{"Còn sẵn", "Đang mượn", "Thanh lý"});
        cboTrangThai.setBackground(Color.WHITE);

        // Layout GridBag cho đẹp và thẳng hàng
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Cột 1
        gbc.gridx=0; gbc.gridy=0; pnlInfo.add(new JLabel("Mã Sách:"), gbc);
        gbc.gridx=1; gbc.gridy=0; pnlInfo.add(txtMa, gbc);
        
        gbc.gridx=0; gbc.gridy=1; pnlInfo.add(new JLabel("Tên Sách:"), gbc);
        gbc.gridx=1; gbc.gridy=1; pnlInfo.add(txtTen, gbc);
        
        gbc.gridx=0; gbc.gridy=2; pnlInfo.add(new JLabel("Tác Giả:"), gbc);
        gbc.gridx=1; gbc.gridy=2; pnlInfo.add(txtTacGia, gbc);
        
        gbc.gridx=0; gbc.gridy=3; pnlInfo.add(new JLabel("Giá Sách:"), gbc);
        gbc.gridx=1; gbc.gridy=3; pnlInfo.add(txtGia, gbc);

        // Cột 2
        gbc.gridx=2; gbc.gridy=0; pnlInfo.add(new JLabel("Thể Loại:"), gbc);
        gbc.gridx=3; gbc.gridy=0; pnlInfo.add(cboTheLoai, gbc);
        
        gbc.gridx=2; gbc.gridy=1; pnlInfo.add(new JLabel("Năm XB:"), gbc);
        gbc.gridx=3; gbc.gridy=1; pnlInfo.add(txtNamXB, gbc);
        
        gbc.gridx=2; gbc.gridy=2; pnlInfo.add(new JLabel("Nhà XB:"), gbc);
        gbc.gridx=3; gbc.gridy=2; pnlInfo.add(txtNXB, gbc);
        
        gbc.gridx=2; gbc.gridy=3; pnlInfo.add(new JLabel("Số Lượng:"), gbc);
        gbc.gridx=3; gbc.gridy=3; pnlInfo.add(txtSoLuong, gbc);

        // --- 2. PANEL CENTER (ẢNH + MÔ TẢ) ---
        JPanel pnlCenter = new JPanel(new BorderLayout(10, 10));
        pnlCenter.setBorder(new EmptyBorder(10, 10, 10, 10));
        pnlCenter.setBackground(Color.WHITE);

        // Phần Ảnh (Bên Trái)
        JPanel pnlAnh = new JPanel(new BorderLayout(5, 5));
        pnlAnh.setPreferredSize(new Dimension(220, 0));
        pnlAnh.setBorder(BorderFactory.createTitledBorder("Ảnh bìa"));
        pnlAnh.setBackground(Color.WHITE);
        
        lblHinhAnh = new JLabel("Chưa có ảnh", SwingConstants.CENTER);
        lblHinhAnh.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        lblHinhAnh.setBackground(new Color(240, 240, 240));
        lblHinhAnh.setOpaque(true);
        
        JButton btnChonAnh = new JButton("Chọn Ảnh");
        btnChonAnh.setBackground(Color.ORANGE);
        
        pnlAnh.add(lblHinhAnh, BorderLayout.CENTER);
        pnlAnh.add(btnChonAnh, BorderLayout.SOUTH);

        // Phần Mô Tả (Bên Phải)
        JPanel pnlMoTa = new JPanel(new BorderLayout());
        pnlMoTa.setBorder(BorderFactory.createTitledBorder("Mô tả sách"));
        pnlMoTa.setBackground(Color.WHITE);
        
        txtMoTa = new JTextArea();
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        txtMoTa.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnlMoTa.add(new JScrollPane(txtMoTa), BorderLayout.CENTER);

        pnlCenter.add(pnlAnh, BorderLayout.WEST);
        pnlCenter.add(pnlMoTa, BorderLayout.CENTER);

        // Tổng hợp Center
        JPanel pnlMain = new JPanel(new BorderLayout());
        pnlMain.add(pnlInfo, BorderLayout.NORTH);
        pnlMain.add(pnlCenter, BorderLayout.CENTER);
        add(pnlMain, BorderLayout.CENTER);

        // --- 3. PANEL BUTTONS ---
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        pnlBot.setBackground(Color.WHITE);
        
        JButton btnLuu = new JButton(maSachSua == null ? "Thêm" : "Cập Nhật");
        JButton btnLamMoi = new JButton("Làm mới");
        JButton btnHuy = new JButton("Thoát");
        
        styleButton(btnLuu, new Color(50, 115, 220));
        styleButton(btnLamMoi, new Color(23, 162, 184));
        styleButton(btnHuy, new Color(220, 53, 69));
        
        pnlBot.add(btnLuu);
        pnlBot.add(btnLamMoi);
        pnlBot.add(btnHuy);
        add(pnlBot, BorderLayout.SOUTH);

        // EVENTS
        btnChonAnh.addActionListener(e -> chonAnh());
        btnLuu.addActionListener(e -> xuLyLuu());
        btnLamMoi.addActionListener(e -> xoaForm());
        btnHuy.addActionListener(e -> dispose());

        // --- [MỚI] KÍCH HOẠT PHÍM ENTER ---
        setupEnterNavigation();
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setPreferredSize(new Dimension(120, 40));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
    }

    // --- [MỚI] HÀM XỬ LÝ PHÍM ENTER ---
    private void setupEnterNavigation() {
        // Danh sách các Textfield theo thứ tự nhập liệu
        JTextField[] fields = {txtMa, txtTen, txtTacGia, txtGia, txtNamXB, txtNXB, txtSoLuong};
        
        for (JTextField tf : fields) {
            tf.addActionListener(e -> tf.transferFocus()); // Nhấn Enter sẽ chuyển focus sang ô kế tiếp
        }

        // Xử lý riêng cho ComboBox (vì nó dùng KeyListener)
        cboTheLoai.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    cboTheLoai.transferFocus();
                }
            }
        });
    }

    // Load danh sách thể loại từ DB lên ComboBox
    private void loadComboboxTheLoai() {
        cboTheLoai.removeAllItems();
        ArrayList<String> list = dalTheLoai.getListTenTheLoai();
        for(String item : list) {
            cboTheLoai.addItem(item);
        }
    }

    private void chonAnh() {
        JFileChooser chooser = new JFileChooser();
        if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            duongDanAnh = file.getAbsolutePath();
            try {
                ImageIcon icon = new ImageIcon(duongDanAnh);
                Image img = icon.getImage().getScaledInstance(200, 250, Image.SCALE_SMOOTH);
                lblHinhAnh.setIcon(new ImageIcon(img));
                lblHinhAnh.setText("");
            } catch (Exception e) {
                e.printStackTrace();
            }
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
                try {
                    ImageIcon icon = new ImageIcon(duongDanAnh);
                    Image img = icon.getImage().getScaledInstance(200, 250, Image.SCALE_SMOOTH);
                    lblHinhAnh.setIcon(new ImageIcon(img));
                    lblHinhAnh.setText("");
                } catch(Exception e) {
                    lblHinhAnh.setText("Lỗi ảnh");
                }
            }
        }
    }

    private void xuLyLuu() {
        if(txtMa.getText().isEmpty() || txtTen.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã và Tên sách!"); return;
        }

        DTO_Sach s = new DTO_Sach();
        s.setMaCuonSach(txtMa.getText());
        s.setTenSach(txtTen.getText());
        s.setTacGia(txtTacGia.getText());
        s.setNhaXuatBan(txtNXB.getText());
        s.setMoTa(txtMoTa.getText());
        s.setHinhAnh(duongDanAnh); 
        
        String selectedTL = (String) cboTheLoai.getSelectedItem();
        if(selectedTL != null) {
            String[] parts = selectedTL.split(" - ");
            s.setMaTheLoai(parts[0].trim()); 
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thể loại!");
            return;
        }

        try {
            s.setNamXuatBan(Integer.parseInt(txtNamXB.getText()));
            s.setGia(Double.parseDouble(txtGia.getText()));
            s.setSoLuong(Integer.parseInt(txtSoLuong.getText()));
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "Năm, Giá, Số lượng phải là số!"); return;
        }

        boolean kq;
        if(maSachSua == null) { 
            if(dal.hasMaSach(s.getMaCuonSach())) {
                JOptionPane.showMessageDialog(this, "Mã sách đã tồn tại!"); return;
            }
            kq = dal.addSach(s);
        } else { 
            kq = dal.updateSach(s);
        }

        if(kq) {
            String msg = (maSachSua == null) ? "Thêm sách thành công!" : "Cập nhật thành công!";
            JOptionPane.showMessageDialog(this, msg);
            parentGUI.loadData();
            dispose();
        } else {
            System.err.println("Thêm thất bại. Kiểm tra lại CSDL xem có đủ cột MoTa, HinhAnh chưa.");
            JOptionPane.showMessageDialog(this, "Thao tác thất bại! Kiểm tra lại thông tin.");
        }
    }
    
    private void xoaForm() {
        txtMa.setText(""); txtTen.setText(""); txtTacGia.setText(""); 
        txtNXB.setText(""); txtNamXB.setText(""); txtGia.setText(""); 
        txtSoLuong.setText(""); txtMoTa.setText("");
        lblHinhAnh.setIcon(null); lblHinhAnh.setText("Chưa có ảnh");
        duongDanAnh = "";
    }
}