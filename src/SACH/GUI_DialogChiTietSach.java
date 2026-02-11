package SACH;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class GUI_DialogChiTietSach extends JDialog {

    private String maSach;
    private DAL_Sach dal = new DAL_Sach();
    private JLabel lblAnh;
    private JPanel pnlInfo;
    
    // Màu chủ đạo
    private Color mainColor = new Color(50, 115, 220); 

    public GUI_DialogChiTietSach(Window parent, String maSach) {
        super(parent, ModalityType.APPLICATION_MODAL);
        this.maSach = maSach;
        initUI();
        loadData();
    }

    private void initUI() {
        setTitle("THÔNG TIN CHI TIẾT SÁCH");
        setSize(950, 680); 
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);

        // --- 1. HEADER ---
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlHeader.setBackground(mainColor);
        pnlHeader.setBorder(new EmptyBorder(15, 0, 15, 0));
        
        JLabel lblHeader = new JLabel("CHI TIẾT CUỐN SÁCH");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(Color.WHITE);
        pnlHeader.add(lblHeader);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. CONTENT ---
        JPanel pnlContent = new JPanel(new BorderLayout(20, 20)); 
        pnlContent.setBorder(new EmptyBorder(20, 20, 20, 20));
        pnlContent.setBackground(new Color(245, 248, 253)); 

        // A. PANEL ẢNH (TRÁI)
        JPanel pnlLeft = new JPanel(new BorderLayout(0, 15));
        pnlLeft.setBackground(Color.WHITE);
        pnlLeft.putClientProperty(FlatClientProperties.STYLE, "arc: 20; border: 1,1,1,1, #E0E0E0");
        pnlLeft.setBorder(new EmptyBorder(15, 15, 15, 15));
        pnlLeft.setPreferredSize(new Dimension(320, 0)); 
        
        JLabel lblTitleAnh = new JLabel("Ảnh Bìa", SwingConstants.CENTER);
        lblTitleAnh.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitleAnh.setForeground(mainColor);
        
        lblAnh = new JLabel("Chưa có ảnh", SwingConstants.CENTER);
        lblAnh.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblAnh.setForeground(Color.GRAY);
        lblAnh.setBorder(BorderFactory.createDashedBorder(new Color(200, 200, 200), 2, 5, 2, true));
        lblAnh.setBackground(new Color(245, 248, 253));
        lblAnh.setOpaque(true);

        pnlLeft.add(lblTitleAnh, BorderLayout.NORTH);
        pnlLeft.add(lblAnh, BorderLayout.CENTER);

        // B. PANEL THÔNG TIN (PHẢI)
        pnlInfo = new JPanel(new GridBagLayout());
        pnlInfo.setBackground(Color.WHITE);
        pnlInfo.putClientProperty(FlatClientProperties.STYLE, "arc: 20; border: 1,1,1,1, #E0E0E0");
        
        // [FIX] TitledBorder được thiết kế lại để KHÔNG bị dính chữ
        TitledBorder tb = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE, 0), // Ẩn viền thừa
            "Thông Tin Chung", 
            TitledBorder.LEFT, 
            TitledBorder.TOP, 
            new Font("Segoe UI", Font.BOLD, 17), 
            mainColor
        );
        pnlInfo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5), 
            BorderFactory.createCompoundBorder(
                tb, 
                BorderFactory.createEmptyBorder(15, 10, 10, 10) // [QUAN TRỌNG] Đẩy nội dung xuống 15px cách xa Title
            )
        ));

        pnlContent.add(pnlLeft, BorderLayout.WEST);
        pnlContent.add(pnlInfo, BorderLayout.CENTER);
        add(pnlContent, BorderLayout.CENTER);

        // --- 3. FOOTER ---
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        pnlFooter.setBackground(new Color(245, 248, 253));
        pnlFooter.setBorder(new EmptyBorder(0, 20, 10, 20));
        
        JButton btnDong = new JButton("Đóng Lại");
        btnDong.setPreferredSize(new Dimension(140, 42));
        btnDong.setBackground(new Color(220, 53, 69));
        btnDong.setForeground(Color.WHITE);
        btnDong.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDong.setFocusPainted(false);
        btnDong.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDong.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0");
        btnDong.addActionListener(e -> dispose());
        
        pnlFooter.add(btnDong);
        add(pnlFooter, BorderLayout.SOUTH);
    }

    private void loadData() {
        pnlInfo.removeAll(); 
        
        DTO_Sach s = dal.getDetail(maSach);
        if(s == null) return;

        // Load ảnh
        if(s.getHinhAnh() != null && !s.getHinhAnh().isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(s.getHinhAnh());
                Image img = icon.getImage().getScaledInstance(280, 400, Image.SCALE_SMOOTH);
                lblAnh.setIcon(new ImageIcon(img));
                lblAnh.setText("");
                lblAnh.setBorder(BorderFactory.createEmptyBorder()); 
                lblAnh.setOpaque(false);
            } catch(Exception e) {
                lblAnh.setText("Lỗi hiển thị ảnh");
            }
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5); // Đệm dòng mượt hơn
        
        // Các dòng thông tin cơ bản
        addRow(0, "Mã Sách:", s.getMaCuonSach(), gbc);
        addRow(1, "Tên Sách:", s.getTenSach(), gbc);
        addRow(2, "Tác Giả:", s.getTacGia(), gbc);
        addRow(3, "Nhà Xuất Bản:", s.getNhaXuatBan(), gbc);
        addRow(4, "Năm Xuất Bản:", String.valueOf(s.getNamXuatBan()), gbc);
        addRow(5, "Thể Loại:", s.getTenTheLoai() != null ? s.getTenTheLoai() : s.getMaTheLoai(), gbc);
        addRow(6, "Giá Bìa:", String.format("%,.0f VNĐ", s.getGia()), gbc);
        addRow(7, "Số Lượng Tồn:", s.getSoLuong() + " cuốn", gbc);
        
        // --- PHẦN MÔ TẢ ---
        gbc.gridx = 0; gbc.gridy = 8; 
        gbc.weightx = 0; gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE; 
        
        JLabel lblMoTa = new JLabel("Mô Tả:");
        lblMoTa.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblMoTa.setForeground(new Color(100, 100, 100));
        lblMoTa.setPreferredSize(new Dimension(110, 30)); // [FIX] Cố định độ rộng 110px cho thẳng hàng
        pnlInfo.add(lblMoTa, gbc);
        
        gbc.gridx = 1; 
        gbc.weightx = 1.0; 
        gbc.weighty = 1.0; 
        gbc.fill = GridBagConstraints.BOTH; 
        
        String moTaText = (s.getMoTa() == null || s.getMoTa().equals("null") || s.getMoTa().trim().isEmpty()) 
                          ? "Không có mô tả chi tiết cho cuốn sách này." : s.getMoTa();
        
        JTextArea txtMoTa = new JTextArea(moTaText);
        txtMoTa.setWrapStyleWord(true);
        txtMoTa.setLineWrap(true);
        txtMoTa.setEditable(false);
        txtMoTa.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtMoTa.setForeground(new Color(50, 50, 50));
        txtMoTa.setBackground(new Color(245, 245, 245)); 
        txtMoTa.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12)); 
        
        JScrollPane sc = new JScrollPane(txtMoTa);
        sc.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0"); 
        sc.setPreferredSize(new Dimension(100, 120)); 
        
        pnlInfo.add(sc, gbc);
        
        pnlInfo.revalidate(); 
        pnlInfo.repaint();
    }

    // [FIX] Căn chỉnh hoàn hảo tất cả các cột
    private void addRow(int row, String labelText, String valueText, GridBagConstraints gbc) {
        gbc.gridx = 0; gbc.gridy = row; 
        gbc.weightx = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(100, 100, 100)); 
        lbl.setPreferredSize(new Dimension(110, 35)); // [QUAN TRỌNG] Ép cứng Label rộng đúng 110px
        pnlInfo.add(lbl, gbc);

        gbc.gridx = 1; 
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField txt = new JTextField(valueText);
        txt.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txt.setForeground(new Color(50, 50, 50));
        txt.setEditable(false);
        txt.setBackground(new Color(245, 245, 245)); 
        
        txt.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0");
        txt.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12)); 
        txt.setPreferredSize(new Dimension(0, 38)); // [QUAN TRỌNG] Ép cứng chiều cao 38px để không bị cắt chữ
        
        pnlInfo.add(txt, gbc);
    }
}