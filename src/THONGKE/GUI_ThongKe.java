package THONGKE;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GUI_ThongKe extends JPanel {

    private DAL_ThongKe dal = new DAL_ThongKe();
    private JLabel lblSoLuongSach, lblSoLuongMuon, lblSoLuongViPham;

    public GUI_ThongKe() {
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 248, 253));

        // --- HEADER ---
        JLabel lblHeader = new JLabel("TỔNG QUAN HỆ THỐNG", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblHeader.setForeground(new Color(50, 115, 220));
        lblHeader.setOpaque(true);
        lblHeader.setBackground(Color.WHITE); 
        lblHeader.setBorder(new EmptyBorder(20, 0, 20, 0));
        add(lblHeader, BorderLayout.NORTH);

        // --- DASHBOARD (3 Ô) ---
        JPanel pnlDashboard = new JPanel(new GridLayout(1, 3, 40, 0)); 
        pnlDashboard.setBackground(new Color(245, 248, 253));
        pnlDashboard.setBorder(new EmptyBorder(30, 30, 300, 30));

        // 1. CARD SÁCH
        lblSoLuongSach = new JLabel("0");
        JPanel pnlSach = createCard("THỐNG KÊ SÁCH", lblSoLuongSach, new Color(50, 115, 220));
        
        pnlSach.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pnlSach.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new GUI_DialogThongKeSach(SwingUtilities.getWindowAncestor(GUI_ThongKe.this)).setVisible(true);
            }
        });

        // 2. CARD MƯỢN TRẢ
        lblSoLuongMuon = new JLabel("0");
        JPanel pnlMuon = createCard("THỐNG KÊ MƯỢN TRẢ", lblSoLuongMuon, new Color(40, 167, 69));
        
        pnlMuon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pnlMuon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new GUI_DialogThongKeMuonTra(SwingUtilities.getWindowAncestor(GUI_ThongKe.this)).setVisible(true);
            }
        });

        // 3. CARD VI PHẠM
        lblSoLuongViPham = new JLabel("0");
        JPanel pnlViPham = createCard("THỐNG KÊ VI PHẠM", lblSoLuongViPham, new Color(220, 53, 69));
        
        pnlViPham.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pnlViPham.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new GUI_DialogThongKeViPham(SwingUtilities.getWindowAncestor(GUI_ThongKe.this)).setVisible(true);
            }
        });

        // Thêm vào Dashboard
        pnlDashboard.add(pnlSach);
        pnlDashboard.add(pnlMuon);
        pnlDashboard.add(pnlViPham);

        add(pnlDashboard, BorderLayout.CENTER);
    }

    // --- HÀM LOAD DỮ LIỆU ---
    public void loadData() {
        new Thread(() -> {
            int sach = dal.getTongDauSach();
            int muon = dal.getTongPhieuMuon();
            int vipham = dal.getTongViPham();
            
            SwingUtilities.invokeLater(() -> {
                lblSoLuongSach.setText(String.valueOf(sach));
                lblSoLuongMuon.setText(String.valueOf(muon));
                lblSoLuongViPham.setText(String.valueOf(vipham));
            });
        }).start();
    }

    // --- HÀM TẠO GIAO DIỆN CARD ---
    private JPanel createCard(String title, JLabel lblNumber, Color themeColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Phần Tiêu đề (Màu nền)
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setOpaque(true);
        lblTitle.setBackground(themeColor);
        lblTitle.setPreferredSize(new Dimension(0, 50));
        
        // Phần Số liệu (Ở giữa)
        JPanel pnlCenter = new JPanel(new GridBagLayout());
        pnlCenter.setBackground(Color.WHITE);
        
        lblNumber.setFont(new Font("Segoe UI", Font.BOLD, 80)); // Tăng size chữ số lên cho đẹp
        lblNumber.setForeground(themeColor);
        pnlCenter.add(lblNumber);

        // Phần Đáy (Đã xóa ký tự lỗi)
        JLabel lblFooter = new JLabel("Xem chi tiết", SwingConstants.CENTER);
        lblFooter.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblFooter.setForeground(Color.GRAY);
        lblFooter.setPreferredSize(new Dimension(0, 30));
        lblFooter.setBorder(new EmptyBorder(5, 0, 5, 0));

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(pnlCenter, BorderLayout.CENTER);
        panel.add(lblFooter, BorderLayout.SOUTH);

        // Viền bóng đổ nhẹ
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        return panel;
    }
}