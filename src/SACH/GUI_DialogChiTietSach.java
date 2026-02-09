package SACH;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class GUI_DialogChiTietSach extends JDialog {

    private String maSach;
    private DAL_Sach dal = new DAL_Sach();
    private JLabel lblAnh;
    private JPanel pnlInfo;
    
    private Color mainColor = new Color(50, 115, 220); 

    public GUI_DialogChiTietSach(Window parent, String maSach) {
        super(parent, ModalityType.APPLICATION_MODAL);
        this.maSach = maSach;
        initUI();
        loadData();
    }

    private void initUI() {
        setTitle("THÔNG TIN CHI TIẾT SÁCH");
        setSize(900, 650); // Tăng kích thước form lên xíu cho thoáng
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false);

        // --- 1. HEADER ---
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlHeader.setBackground(mainColor);
        pnlHeader.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JLabel lblHeader = new JLabel("CHI TIẾT SÁCH");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(Color.WHITE);
        pnlHeader.add(lblHeader);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. CONTENT ---
        JPanel pnlContent = new JPanel(new BorderLayout(15, 15));
        pnlContent.setBorder(new EmptyBorder(20, 20, 20, 20));
        pnlContent.setBackground(Color.WHITE);

        // A. PANEL ẢNH (TRÁI)
        JPanel pnlLeft = new JPanel(new BorderLayout());
        pnlLeft.setBackground(Color.WHITE);
        pnlLeft.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Ảnh bìa",
            TitledBorder.CENTER, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14), mainColor
        ));
        pnlLeft.setPreferredSize(new Dimension(300, 0)); // Rộng hơn xíu để chứa ảnh to
        
        lblAnh = new JLabel("Không có ảnh", SwingConstants.CENTER);
        lblAnh.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblAnh.setForeground(Color.GRAY);
        pnlLeft.add(lblAnh, BorderLayout.CENTER);

        // B. PANEL THÔNG TIN (PHẢI)
        pnlInfo = new JPanel(new GridBagLayout());
        pnlInfo.setBackground(Color.WHITE);
        pnlInfo.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Thông tin chung",
            TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14), mainColor
        ));

        pnlContent.add(pnlLeft, BorderLayout.WEST);
        pnlContent.add(pnlInfo, BorderLayout.CENTER);
        add(pnlContent, BorderLayout.CENTER);

        // --- 3. FOOTER ---
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlFooter.setBackground(new Color(245, 248, 253));
        pnlFooter.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JButton btnDong = new JButton("Đóng");
        btnDong.setPreferredSize(new Dimension(120, 40));
        btnDong.setBackground(new Color(220, 53, 69));
        btnDong.setForeground(Color.WHITE);
        btnDong.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDong.setFocusPainted(false);
        btnDong.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDong.addActionListener(e -> dispose());
        
        pnlFooter.add(btnDong);
        add(pnlFooter, BorderLayout.SOUTH);
    }

    private void loadData() {
        pnlInfo.removeAll(); // Xóa cũ để vẽ lại cho chắc
        
        DTO_Sach s = dal.getDetail(maSach);
        if(s == null) return;

        // Load ảnh
        if(s.getHinhAnh() != null && !s.getHinhAnh().isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(s.getHinhAnh());
                Image img = icon.getImage().getScaledInstance(260, 350, Image.SCALE_SMOOTH);
                lblAnh.setIcon(new ImageIcon(img));
                lblAnh.setText("");
            } catch(Exception e) {
                lblAnh.setText("Lỗi hiển thị ảnh");
            }
        }

        // Setup Layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);
        
        // Các dòng thông tin cơ bản
        addRow(0, "Mã Sách:", s.getMaCuonSach(), gbc);
        addRow(1, "Tên Sách:", s.getTenSach(), gbc);
        addRow(2, "Tác Giả:", s.getTacGia(), gbc);
        addRow(3, "Nhà Xuất Bản:", s.getNhaXuatBan(), gbc);
        addRow(4, "Năm Xuất Bản:", String.valueOf(s.getNamXuatBan()), gbc);
        addRow(5, "Thể Loại:", s.getTenTheLoai() != null ? s.getTenTheLoai() : s.getMaTheLoai(), gbc);
        addRow(6, "Giá Bìa:", String.format("%,.0f VNĐ", s.getGia()), gbc);
        addRow(7, "Số Lượng Tồn:", s.getSoLuong() + " cuốn", gbc);
        
        // --- PHẦN MÔ TẢ (Quan trọng) ---
        gbc.gridx = 0; gbc.gridy = 8; 
        gbc.weightx = 0; gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Reset fill
        
        JLabel lblMoTa = new JLabel("Mô Tả:");
        lblMoTa.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblMoTa.setForeground(new Color(100, 100, 100));
        pnlInfo.add(lblMoTa, gbc);
        
        gbc.gridx = 1; 
        gbc.weightx = 1.0; 
        gbc.weighty = 1.0; // Giãn chiều cao
        gbc.fill = GridBagConstraints.BOTH; // Lấp đầy cả 2 chiều
        
        // Xử lý null cho mô tả
        String moTaText = (s.getMoTa() == null || s.getMoTa().equals("null")) ? "Không có mô tả." : s.getMoTa();
        
        JTextArea txtMoTa = new JTextArea(moTaText);
        txtMoTa.setWrapStyleWord(true);
        txtMoTa.setLineWrap(true);
        txtMoTa.setEditable(false);
        txtMoTa.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtMoTa.setBackground(new Color(250, 250, 250));
        txtMoTa.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JScrollPane sc = new JScrollPane(txtMoTa);
        sc.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        sc.setPreferredSize(new Dimension(100, 100)); // [FIX] Đảm bảo luôn có chiều cao tối thiểu
        
        pnlInfo.add(sc, gbc);
        
        pnlInfo.revalidate(); // Vẽ lại giao diện
        pnlInfo.repaint();
    }

    private void addRow(int row, String labelText, String valueText, GridBagConstraints gbc) {
        gbc.gridx = 0; gbc.gridy = row; 
        gbc.weightx = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(100, 100, 100));
        pnlInfo.add(lbl, gbc);

        gbc.gridx = 1; 
        gbc.weightx = 1.0;
        
        JTextField txt = new JTextField(valueText);
        txt.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txt.setForeground(new Color(50, 50, 50));
        txt.setEditable(false);
        txt.setBackground(Color.WHITE);
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            new EmptyBorder(0, 0, 5, 0)
        ));
        
        pnlInfo.add(txt, gbc);
    }
}