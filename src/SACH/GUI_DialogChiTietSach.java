package SACH;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GUI_DialogChiTietSach extends JDialog {

    private String maSach;
    private DAL_Sach dal = new DAL_Sach();
    private JLabel lblAnh;
    private JPanel pnlInfo;

    public GUI_DialogChiTietSach(Window parent, String maSach) {
        super(parent, ModalityType.APPLICATION_MODAL);
        this.maSach = maSach;
        initUI();
        loadData();
    }

    private void initUI() {
        setTitle("CHI TIẾT SÁCH");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- BÊN TRÁI: ẢNH ---
        JPanel pnlLeft = new JPanel(new BorderLayout());
        pnlLeft.setBackground(Color.WHITE);
        pnlLeft.setPreferredSize(new Dimension(300, 0));
        pnlLeft.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        lblAnh = new JLabel("Không có ảnh", SwingConstants.CENTER);
        lblAnh.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        pnlLeft.add(lblAnh, BorderLayout.CENTER);

        // --- BÊN PHẢI: THÔNG TIN (NỀN XANH) ---
        pnlInfo = new JPanel(new GridBagLayout());
        pnlInfo.setBackground(new Color(100, 149, 237)); // Màu xanh dịu (Cornflower Blue)
        pnlInfo.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Container chính
        add(pnlLeft, BorderLayout.WEST);
        add(pnlInfo, BorderLayout.CENTER);
    }

    private void loadData() {
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

        // Load thông tin
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        addRow(0, "Mã sách:", s.getMaCuonSach(), gbc);
        addRow(1, "Tên sách:", s.getTenSach(), gbc);
        addRow(2, "Tác giả:", s.getTacGia(), gbc);
        addRow(3, "Giá:", String.format("%,.0f VNĐ", s.getGia()), gbc);
        addRow(4, "Thể loại:", s.getTenTheLoai() != null ? s.getTenTheLoai() : s.getMaTheLoai(), gbc);
        addRow(5, "Năm XB:", String.valueOf(s.getNamXuatBan()), gbc);
        addRow(6, "Nhà XB:", s.getNhaXuatBan(), gbc);
        addRow(7, "Số lượng:", s.getSoLuong() + "", gbc);
        addRow(8, "Trạng thái:", s.getTinhTrang(), gbc);
        
        // Mô tả (TextArea)
        gbc.gridx = 0; gbc.gridy = 9;
        JLabel lblMoTa = new JLabel("Mô tả:");
        lblMoTa.setForeground(Color.WHITE);
        lblMoTa.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Tiêu đề vẫn in đậm
        pnlInfo.add(lblMoTa, gbc);
        
        gbc.gridx = 1; gbc.gridy = 9; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        JTextArea txtMoTa = new JTextArea(s.getMoTa());
        txtMoTa.setWrapStyleWord(true);
        txtMoTa.setLineWrap(true);
        txtMoTa.setEditable(false);
        txtMoTa.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Nội dung mô tả font thường
        pnlInfo.add(new JScrollPane(txtMoTa), gbc);
    }

    private void addRow(int row, String label, String value, GridBagConstraints gbc) {
        // Cột 1: Label (Tiêu đề) - Giữ in đậm
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.weighty = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14)); 
        lbl.setForeground(Color.WHITE);
        pnlInfo.add(lbl, gbc);

        // Cột 2: Value (Nội dung) - CHUYỂN VỀ FONT THƯỜNG
        gbc.gridx = 1; gbc.weightx = 1.0;
        JTextField txt = new JTextField(value);
        
        // --- [SỬA LẠI]: Dùng Font.PLAIN thay vì Font.BOLD ---
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14)); 
        
        txt.setEditable(false);
        txt.setBackground(Color.WHITE);
        // Thêm padding cho Textfield nhìn thoáng hơn
        txt.setBorder(BorderFactory.createCompoundBorder(
            txt.getBorder(), 
            BorderFactory.createEmptyBorder(2, 5, 2, 5)));
            
        pnlInfo.add(txt, gbc);
    }
}