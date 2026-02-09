package CAIDAT;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import CHUNG.DBConnect;

public class GUI_CauHinh extends JPanel {

    private JTextField txtNgayMuon, txtSachMuon, txtTienPhat, txtBackup;
    private JButton btnLuu;
    
    // Màu chủ đạo
    private Color mainColor = new Color(50, 115, 220); 
    private Color bgColor = new Color(245, 248, 253);

    public GUI_CauHinh() {
        setLayout(new BorderLayout());
        setBackground(bgColor);

        // --- 1. HEADER ---
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            new EmptyBorder(10, 0, 10, 0)
        ));
        
        JLabel lblTitle = new JLabel("THIẾT LẬP THAM SỐ HỆ THỐNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(mainColor);
        
        pnlHeader.add(lblTitle);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. FORM CENTER ---
        JPanel pnlCenter = new JPanel(new GridBagLayout());
        pnlCenter.setBackground(bgColor);
        
        // Panel chứa form chính (Nền trắng, viền nhẹ)
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(Color.WHITE);
        pnlForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(40, 50, 40, 50)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10); // Tăng khoảng cách các dòng
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Init Components
        txtNgayMuon = createTextField();
        txtSachMuon = createTextField();
        txtTienPhat = createTextField();
        txtBackup = createTextField();

        // Row 1: Ngày mượn
        gbc.gridx = 0; gbc.gridy = 0;
        pnlForm.add(createLabel("Số ngày mượn tối đa:"), gbc);
        gbc.gridx = 1;
        pnlForm.add(txtNgayMuon, gbc);

        // Row 2: Số sách
        gbc.gridx = 0; gbc.gridy = 1;
        pnlForm.add(createLabel("Số lượng sách mượn tối đa:"), gbc);
        gbc.gridx = 1;
        pnlForm.add(txtSachMuon, gbc);

        // Row 3: Tiền phạt
        gbc.gridx = 0; gbc.gridy = 2;
        pnlForm.add(createLabel("Tiền phạt quá hạn (VNĐ/ngày):"), gbc);
        gbc.gridx = 1;
        pnlForm.add(txtTienPhat, gbc);

        // Row 4: Backup
        gbc.gridx = 0; gbc.gridy = 3;
        pnlForm.add(createLabel("Thư mục sao lưu dữ liệu (Backup):"), gbc);
        gbc.gridx = 1;
        pnlForm.add(txtBackup, gbc);

        // Row 5: Button Save
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2; // Gộp cột
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(40, 10, 10, 10); // Cách xa bên trên
        
        btnLuu = new JButton("LƯU CẤU HÌNH");
        btnLuu.setPreferredSize(new Dimension(220, 50));
        btnLuu.setBackground(new Color(40, 167, 69)); // Xanh lá
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLuu.setFocusPainted(false);
        btnLuu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        pnlForm.add(btnLuu, gbc);

        pnlCenter.add(pnlForm);
        add(pnlCenter, BorderLayout.CENTER);

        // Load data & Events
        loadData();
        btnLuu.addActionListener(e -> saveConfig());
    }

    // --- HELPER UI ---
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(new Color(80, 80, 80));
        return lbl;
    }

    private JTextField createTextField() {
        JTextField txt = new JTextField(25);
        txt.setPreferredSize(new Dimension(350, 40));
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return txt;
    }

    // --- LOGIC ---
    private void loadData() {
        try {
            Connection conn = new DBConnect().getConnection();
            txtNgayMuon.setText(getThamSo(conn, "SoNgayMuonToiDa"));
            txtSachMuon.setText(getThamSo(conn, "SoSachMuonToiDa"));
            txtTienPhat.setText(getThamSo(conn, "TienPhatMoiNgay"));
            txtBackup.setText(getThamSo(conn, "DuongDanBackup"));
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getThamSo(Connection conn, String tenThamSo) {
        String val = "";
        try {
            String sql = "SELECT GiaTri FROM THAM_SO WHERE TenThamSo = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, tenThamSo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) val = rs.getString("GiaTri");
        } catch (Exception e) {}
        return val;
    }

    private void saveConfig() {
        try {
            Connection conn = new DBConnect().getConnection();
            updateThamSo(conn, "SoNgayMuonToiDa", txtNgayMuon.getText());
            updateThamSo(conn, "SoSachMuonToiDa", txtSachMuon.getText());
            updateThamSo(conn, "TienPhatMoiNgay", txtTienPhat.getText());
            updateThamSo(conn, "DuongDanBackup", txtBackup.getText());
            
            JOptionPane.showMessageDialog(this, "✅ Cập nhật cấu hình thành công!");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu: " + e.getMessage());
        }
    }

    private void updateThamSo(Connection conn, String ten, String giaTri) throws Exception {
        String sql = "UPDATE THAM_SO SET GiaTri = ? WHERE TenThamSo = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, giaTri);
        ps.setString(2, ten);
        ps.executeUpdate();
    }
}