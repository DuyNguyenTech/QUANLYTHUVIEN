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
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblHeader.setForeground(new Color(50, 115, 220));
        lblHeader.setOpaque(true);
        lblHeader.setBackground(Color.WHITE); 
        lblHeader.setBorder(new EmptyBorder(25, 0, 25, 0));
        
        // Thêm đường kẻ dưới header
        JPanel pnlHeaderWrapper = new JPanel(new BorderLayout());
        pnlHeaderWrapper.add(lblHeader, BorderLayout.CENTER);
        pnlHeaderWrapper.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
        
        add(pnlHeaderWrapper, BorderLayout.NORTH);

        // --- DASHBOARD (3 Ô) ---
        JPanel pnlDashboard = new JPanel(new GridLayout(1, 3, 40, 0)); 
        pnlDashboard.setBackground(new Color(245, 248, 253));
        // Padding xung quanh để các card không dính sát lề
        pnlDashboard.setBorder(new EmptyBorder(40, 40, 300, 40)); 

        // 1. CARD SÁCH (Màu Xanh Dương)
        lblSoLuongSach = new JLabel("0");
        JPanel pnlSach = createCard("KHO SÁCH", "Tổng đầu sách hiện có", lblSoLuongSach, new Color(50, 115, 220));
        
        pnlSach.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new GUI_DialogThongKeSach(SwingUtilities.getWindowAncestor(GUI_ThongKe.this)).setVisible(true);
            }
        });

        // 2. CARD MƯỢN TRẢ (Màu Xanh Lá)
        lblSoLuongMuon = new JLabel("0");
        JPanel pnlMuon = createCard("HOẠT ĐỘNG", "Lượt mượn sách tháng này", lblSoLuongMuon, new Color(40, 167, 69));
        
        pnlMuon.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new GUI_DialogThongKeMuonTra(SwingUtilities.getWindowAncestor(GUI_ThongKe.this)).setVisible(true);
            }
        });

        // 3. CARD VI PHẠM (Màu Đỏ)
        lblSoLuongViPham = new JLabel("0");
        JPanel pnlViPham = createCard("CẢNH BÁO", "Số phiếu quá hạn / Vi phạm", lblSoLuongViPham, new Color(220, 53, 69));
        
        pnlViPham.addMouseListener(new MouseAdapter() {
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
        // Chạy thread riêng để không bị đơ giao diện khi query lâu
        new Thread(() -> {
            int sach = dal.getTongDauSach();
            int muon = dal.getTongPhieuMuon();
            int vipham = dal.getTongViPham();
            
            SwingUtilities.invokeLater(() -> {
                lblSoLuongSach.setText(String.format("%,d", sach));
                lblSoLuongMuon.setText(String.format("%,d", muon));
                lblSoLuongViPham.setText(String.format("%,d", vipham));
            });
        }).start();
    }

    // --- HÀM TẠO GIAO DIỆN CARD ---
    private JPanel createCard(String title, String subtitle, JLabel lblNumber, Color themeColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Header của Card (Màu nền)
        JPanel pnlTitle = new JPanel(new BorderLayout());
        pnlTitle.setBackground(themeColor);
        pnlTitle.setBorder(new EmptyBorder(15, 0, 15, 0));
        
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        pnlTitle.add(lblTitle, BorderLayout.CENTER);
        
        // Phần Nội dung (Số liệu + Subtitle)
        JPanel pnlContent = new JPanel(new GridBagLayout());
        pnlContent.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        
        // Số liệu to
        lblNumber.setFont(new Font("Segoe UI", Font.BOLD, 60)); // Số to hơn nữa
        lblNumber.setForeground(themeColor);
        pnlContent.add(lblNumber, gbc);
        
        // Subtitle nhỏ
        JLabel lblSubtitle = new JLabel(subtitle);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(Color.GRAY);
        pnlContent.add(lblSubtitle, gbc);

        // Footer "Xem chi tiết" - [ĐÃ XÓA KÝ TỰ MŨI TÊN LỖI]
        JLabel lblFooter = new JLabel("Xem chi tiết", SwingConstants.CENTER);
        lblFooter.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFooter.setForeground(themeColor);
        lblFooter.setBorder(new EmptyBorder(10, 0, 15, 0));
        lblFooter.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panel.add(pnlTitle, BorderLayout.NORTH);
        panel.add(pnlContent, BorderLayout.CENTER);
        panel.add(lblFooter, BorderLayout.SOUTH);

        // Viền và Hiệu ứng Hover đơn giản
        panel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBorder(BorderFactory.createLineBorder(themeColor, 2)); // Đổi màu viền khi hover
            }
            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
            }
        });

        return panel;
    }
}