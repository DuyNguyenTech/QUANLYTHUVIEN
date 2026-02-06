package CAIDAT;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import CHUNG.DBConnect;

// [QUAN TRỌNG] Đổi từ JFrame sang JPanel
public class GUI_CauHinh extends JPanel {

    private JTextField txtNgayMuon, txtSachMuon, txtTienPhat, txtBackup;
    private JButton btnLuu;

    public GUI_CauHinh() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 248, 253)); // Màu nền tệp với Main

        // --- 1. HEADER (TIÊU ĐỀ) ---
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JLabel lblTitle = new JLabel("CẤU HÌNH THAM SỐ HỆ THỐNG");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(new Color(0, 102, 204));
        
        pnlHeader.add(lblTitle);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. FORM NHẬP LIỆU (CANH GIỮA) ---
        JPanel pnlCenter = new JPanel(new GridBagLayout());
        pnlCenter.setBackground(new Color(245, 248, 253));
        
        // Tạo panel con chứa form để bo viền cho đẹp
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(Color.WHITE);
        pnlForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Dòng 1: Số ngày mượn
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lbl1 = new JLabel("Số ngày mượn tối đa:");
        lbl1.setFont(new Font("Arial", Font.BOLD, 14));
        pnlForm.add(lbl1, gbc);

        gbc.gridx = 1;
        txtNgayMuon = new JTextField(25);
        txtNgayMuon.setPreferredSize(new Dimension(300, 35));
        pnlForm.add(txtNgayMuon, gbc);

        // Dòng 2: Số sách mượn
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lbl2 = new JLabel("Số sách mượn tối đa:");
        lbl2.setFont(new Font("Arial", Font.BOLD, 14));
        pnlForm.add(lbl2, gbc);

        gbc.gridx = 1;
        txtSachMuon = new JTextField(25);
        txtSachMuon.setPreferredSize(new Dimension(300, 35));
        pnlForm.add(txtSachMuon, gbc);

        // Dòng 3: Tiền phạt
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lbl3 = new JLabel("Tiền phạt/ngày (VNĐ):");
        lbl3.setFont(new Font("Arial", Font.BOLD, 14));
        pnlForm.add(lbl3, gbc);

        gbc.gridx = 1;
        txtTienPhat = new JTextField(25);
        txtTienPhat.setPreferredSize(new Dimension(300, 35));
        pnlForm.add(txtTienPhat, gbc);

        // Dòng 4: Đường dẫn Backup
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lbl4 = new JLabel("Thư mục Backup mặc định:");
        lbl4.setFont(new Font("Arial", Font.BOLD, 14));
        pnlForm.add(lbl4, gbc);

        gbc.gridx = 1;
        txtBackup = new JTextField(25);
        txtBackup.setPreferredSize(new Dimension(300, 35));
        pnlForm.add(txtBackup, gbc);

        // Dòng 5: Nút Lưu
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2; // Gộp 2 cột
        gbc.fill = GridBagConstraints.CENTER;
        
        btnLuu = new JButton("LƯU CẤU HÌNH");
        btnLuu.setPreferredSize(new Dimension(200, 45));
        btnLuu.setBackground(new Color(0, 153, 76));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setFont(new Font("Arial", Font.BOLD, 14));
        btnLuu.setFocusPainted(false);
        btnLuu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Thêm khoảng cách phía trên nút
        gbc.insets = new Insets(30, 10, 10, 10);
        pnlForm.add(btnLuu, gbc);

        // Add Form vào Center
        pnlCenter.add(pnlForm);
        add(pnlCenter, BorderLayout.CENTER);

        // Load dữ liệu và xử lý sự kiện
        loadData();
        btnLuu.addActionListener(e -> saveConfig());
    }

    private void loadData() {
        try {
            Connection conn = new DBConnect().getConnection();
            // Load từng tham số
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
            
            JOptionPane.showMessageDialog(this, "✅ Đã lưu cấu hình thành công!");
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