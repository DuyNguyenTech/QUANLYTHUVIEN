package THONGKE;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GUI_ThongKe extends JPanel {

    private DAL_ThongKe dal = new DAL_ThongKe();
    private JLabel lblSoLuongSach, lblSoLuongMuon, lblSoLuongViPham;

    // Bảng màu hiện đại đồng bộ với hệ thống
    private Color mainColor = new Color(50, 115, 220);
    private Color successColor = new Color(40, 167, 69);
    private Color dangerColor = new Color(220, 53, 69);
    private Color bgColor = new Color(245, 248, 253);

    public GUI_ThongKe() {
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(bgColor);

        // --- 1. HEADER: Thiết kế phẳng, tinh tế ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            new EmptyBorder(25, 40, 25, 40)
        ));

        JLabel lblTitle = new JLabel("THỐNG KÊ CHI TIẾT");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(new Color(45, 52, 54));
        pnlHeader.add(lblTitle, BorderLayout.WEST);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. DASHBOARD: Bố cục Grid rộng rãi ---
        JPanel pnlDashboard = new JPanel(new GridLayout(1, 3, 35, 0)); 
        pnlDashboard.setBackground(bgColor);
        pnlDashboard.setBorder(new EmptyBorder(50, 50, 50, 50)); 

        lblSoLuongSach = new JLabel("0");
        JPanel pnlSach = createModernStatCard("KHO SÁCH", "📚", lblSoLuongSach, mainColor, 1);

        lblSoLuongMuon = new JLabel("0");
        JPanel pnlMuon = createModernStatCard("MƯỢN TRẢ", "🔄", lblSoLuongMuon, successColor, 2);

        lblSoLuongViPham = new JLabel("0");
        JPanel pnlViPham = createModernStatCard("VI PHẠM", "⚠️", lblSoLuongViPham, dangerColor, 3);

        pnlDashboard.add(pnlSach);
        pnlDashboard.add(pnlMuon);
        pnlDashboard.add(pnlViPham);

        add(pnlDashboard, BorderLayout.CENTER);
    }

    /**
     * Hàm tạo Card thống kê phong cách hiện đại (Modern Stat Card)
     */
    private JPanel createModernStatCard(String title, String icon, JLabel lblNumber, Color themeColor, int type) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Bo góc 30px và viền mờ cao cấp
        card.putClientProperty("FlatLaf.style", "arc: 30; border: 1,1,1,1, #E8ECEF"); 

        // Thanh trạng thái phía dưới tạo điểm nhấn
        JPanel pnlBottomStatus = new JPanel();
        pnlBottomStatus.setBackground(themeColor);
        pnlBottomStatus.setPreferredSize(new Dimension(0, 5));
        pnlBottomStatus.putClientProperty("FlatLaf.arc", 30);

        JPanel pnlContent = new JPanel(new GridBagLayout());
        pnlContent.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        
        // 1. Icon minh họa lớn
        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 50));
        lblIcon.setForeground(themeColor);
        gbc.insets = new Insets(10, 0, 10, 0);
        pnlContent.add(lblIcon, gbc);

        // 2. Con số thống kê nổi bật
        lblNumber.setFont(new Font("Segoe UI", Font.BOLD, 60)); 
        lblNumber.setForeground(new Color(45, 52, 54));
        gbc.insets = new Insets(0, 0, 5, 0);
        pnlContent.add(lblNumber, gbc);

        // 3. Tiêu đề danh mục
        JLabel lblT = new JLabel(title);
        lblT.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 16));
        lblT.setForeground(new Color(108, 117, 125));
        gbc.insets = new Insets(0, 0, 20, 0);
        pnlContent.add(lblT, gbc);

        card.add(pnlContent, BorderLayout.CENTER);
        card.add(pnlBottomStatus, BorderLayout.SOUTH);

        // Hiệu ứng Hover nâng cao
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.putClientProperty("FlatLaf.style", "arc: 30; border: 1,1,1,1, " + toHexString(themeColor));
                card.setBackground(new Color(250, 252, 255));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                card.putClientProperty("FlatLaf.style", "arc: 30; border: 1,1,1,1, #E8ECEF");
                card.setBackground(Color.WHITE);
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                Window parent = SwingUtilities.getWindowAncestor(GUI_ThongKe.this);
                if (type == 1) new GUI_DialogThongKeSach(parent).setVisible(true);
                else if (type == 2) new GUI_DialogThongKeMuonTra(parent).setVisible(true);
                else new GUI_DialogThongKeViPham(parent).setVisible(true);
            }
        });

        return card;
    }

    public void loadData() {
        new Thread(() -> {
            try {
                // Lấy dữ liệu từ DAL
                int sach = dal.getTongDauSach();
                int muon = dal.getTongPhieuMuon();
                int vipham = dal.getTongViPham();
                
                SwingUtilities.invokeLater(() -> {
                    lblSoLuongSach.setText(String.format("%,d", sach));
                    lblSoLuongMuon.setText(String.format("%,d", muon));
                    lblSoLuongViPham.setText(String.format("%,d", vipham));
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private String toHexString(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
}