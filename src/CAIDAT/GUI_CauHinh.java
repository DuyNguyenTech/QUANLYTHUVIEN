package CAIDAT;

import com.formdev.flatlaf.FlatClientProperties;
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
    
    // Màu chủ đạo đồng bộ hệ thống
    private Color mainColor = new Color(50, 115, 220); 
    private Color bgColor = new Color(245, 248, 253);
    private Color successColor = new Color(40, 167, 69);

    public GUI_CauHinh() {
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(bgColor);

        // --- 1. HEADER ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            new EmptyBorder(25, 30, 25, 30)
        ));
        
        JLabel lblTitle = new JLabel("THIẾT LẬP THAM SỐ HỆ THỐNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(mainColor);
        pnlHeader.add(lblTitle, BorderLayout.WEST);
        
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. FORM CENTER ---
        JPanel pnlCenter = new JPanel(new GridBagLayout());
        pnlCenter.setBackground(bgColor);
        
        // Panel chứa form chính (Card Premium)
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(Color.WHITE);
        
        // Thiết lập bo góc và viền cho Card (FlatLaf 3.5.4)
        pnlForm.putClientProperty("FlatLaf.style", "arc: 25; border: 1,1,1,1, #E0E0E0");
        pnlForm.setBorder(new EmptyBorder(50, 60, 50, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10); 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1: Ngày mượn
        gbc.gridx = 0; gbc.gridy = 0;
        pnlForm.add(createLabel("Số ngày mượn tối đa:"), gbc);
        txtNgayMuon = createStyledTextField();
        gbc.gridx = 1;
        pnlForm.add(txtNgayMuon, gbc);

        // Row 2: Số sách
        gbc.gridx = 0; gbc.gridy = 1;
        pnlForm.add(createLabel("Số lượng sách mượn tối đa:"), gbc);
        txtSachMuon = createStyledTextField();
        gbc.gridx = 1;
        pnlForm.add(txtSachMuon, gbc);

        // Row 3: Tiền phạt
        gbc.gridx = 0; gbc.gridy = 2;
        pnlForm.add(createLabel("Tiền phạt quá hạn (VNĐ/ngày):"), gbc);
        txtTienPhat = createStyledTextField();
        gbc.gridx = 1;
        pnlForm.add(txtTienPhat, gbc);

        // Row 4: Backup
        gbc.gridx = 0; gbc.gridy = 3;
        pnlForm.add(createLabel("Đường dẫn sao lưu (Backup):"), gbc);
        txtBackup = createStyledTextField();
        gbc.gridx = 1;
        pnlForm.add(txtBackup, gbc);

        // Row 5: Button Save
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(45, 10, 10, 10);
        
        btnLuu = new JButton("LƯU THAY ĐỔI");
        btnLuu.setPreferredSize(new Dimension(250, 50));
        btnLuu.setBackground(successColor);
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLuu.setFocusPainted(false);
        btnLuu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLuu.putClientProperty("FlatLaf.style", "arc: 12; borderWidth: 0");
        
        btnLuu.addActionListener(e -> saveConfig());
        pnlForm.add(btnLuu, gbc);

        pnlCenter.add(pnlForm);
        add(pnlCenter, BorderLayout.CENTER);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(new Color(100, 100, 100));
        return lbl;
    }

    private JTextField createStyledTextField() {
        JTextField txt = new JTextField(25);
        txt.setPreferredSize(new Dimension(380, 42));
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        // ĐÃ FIX: focusedBorderColor (có chữ d)
        txt.putClientProperty("FlatLaf.style", 
            "arc: 8; " +
            "borderWidth: 1; " +
            "focusedBorderColor: #3273DC; " +
            "margin: 0, 10, 0, 10");
        return txt;
    }

    private void loadData() {
        new Thread(() -> {
            try (Connection conn = new DBConnect().getConnection()) {
                String d1 = getThamSo(conn, "SoNgayMuonToiDa");
                String d2 = getThamSo(conn, "SoSachMuonToiDa");
                String d3 = getThamSo(conn, "TienPhatMoiNgay");
                String d4 = getThamSo(conn, "DuongDanBackup");
                
                SwingUtilities.invokeLater(() -> {
                    txtNgayMuon.setText(d1);
                    txtSachMuon.setText(d2);
                    txtTienPhat.setText(d3);
                    txtBackup.setText(d4);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private String getThamSo(Connection conn, String tenThamSo) {
        String val = "";
        String sql = "SELECT GiaTri FROM THAM_SO WHERE TenThamSo = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenThamSo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) val = rs.getString("GiaTri");
            }
        } catch (Exception e) {}
        return val;
    }

    private void saveConfig() {
        try (Connection conn = new DBConnect().getConnection()) {
            updateThamSo(conn, "SoNgayMuonToiDa", txtNgayMuon.getText());
            updateThamSo(conn, "SoSachMuonToiDa", txtSachMuon.getText());
            updateThamSo(conn, "TienPhatMoiNgay", txtTienPhat.getText());
            updateThamSo(conn, "DuongDanBackup", txtBackup.getText());
            
            JOptionPane.showMessageDialog(this, "✅ Đã lưu cấu hình hệ thống thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Lỗi khi lưu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateThamSo(Connection conn, String ten, String giaTri) throws Exception {
        String sql = "UPDATE THAM_SO SET GiaTri = ? WHERE TenThamSo = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, giaTri);
            ps.setString(2, ten);
            ps.executeUpdate();
        }
    }
}