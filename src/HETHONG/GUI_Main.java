package HETHONG;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

// Import module Cài đặt & Sao lưu
import CAIDAT.GUI_CauHinh;
import CAIDAT.GUI_SaoLuuPhucHoi;
import CHUNG.DBConnect;
import DOCGIA.GUI_QuanLyDocGia;
import MUONTRA.GUI_LichSuMuon;
import MUONTRA.GUI_QuanLyMuonTra;
import SACH.GUI_QuanLySach;
import SACH.GUI_QuanLyTheLoai;
import SACH.GUI_TraCuuSach;
import THONGKE.GUI_ThongKe;
import THUTHU.GUI_QuanLyThuThu;
// Import module Nhập Hàng
import NHAPHANG.GUI_QuanLyNhapHang;

public class GUI_Main extends JFrame {

    private DTO_TaiKhoan dTO_TaiKhoan;
    private JPanel panelContent;
    private JPanel panelHeader;
    private JLabel lblClock;
    private JButton btnActive;

    // --- BẢNG MÀU CHUẨN ---
    private Color mainColor = new Color(50, 115, 220);      
    private Color activeColor = new Color(255, 255, 255);   
    private Color activeText = new Color(50, 115, 220);     
    private Color normalText = new Color(240, 245, 255);    
    private Color hoverColor = new Color(90, 145, 235);     
    private Color bgColor = new Color(245, 248, 253);       

    public GUI_Main(DTO_TaiKhoan tk) {
        this.dTO_TaiKhoan = tk;
        initUI();
        startClock(); 
    }

    private void initUI() {
        // Setup Title
        String roleTitle = "ĐỘC GIẢ";
        if (dTO_TaiKhoan.getPhanQuyen() == 1) roleTitle = "QUẢN TRỊ VIÊN";
        else if (dTO_TaiKhoan.getPhanQuyen() == 2) roleTitle = "THỦ THƯ";

        setTitle("HỆ THỐNG QUẢN LÝ THƯ VIỆN - " + roleTitle);
        
        setSize(1200, 750); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
        
        setLayout(new BorderLayout());

        // =================================================================
        // --- A. SIDEBAR (MENU TRÁI) ---
        // =================================================================
        JPanel panelMenu = new JPanel(new BorderLayout());
        panelMenu.setPreferredSize(new Dimension(280, 0));
        panelMenu.setBackground(mainColor); 

        // --- 1. HEADER SIDEBAR (AVATAR) ---
        JPanel panelUser = new JPanel();
        panelUser.setLayout(new BoxLayout(panelUser, BoxLayout.Y_AXIS));
        panelUser.setBackground(mainColor);
        panelUser.setBorder(new EmptyBorder(15, 0, 10, 0));

        // Logic Avatar
        String avatarName = "reader.png"; 
        if (dTO_TaiKhoan.getPhanQuyen() == 1) avatarName = "admin.png";
        else if (dTO_TaiKhoan.getPhanQuyen() == 2) avatarName = "staff.png";
        else {
            String gioitinh = getGioiTinhDocGia(dTO_TaiKhoan.getMaDocGia());
            if (gioitinh != null && gioitinh.equalsIgnoreCase("Nam")) avatarName = "user_male.png";
            else avatarName = "user_female.png";
        }
        final String finalAvatarPath = "/com/qlthuvien/images/" + avatarName;

        // Vẽ Avatar
        JPanel pnlAvatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                
                int size = 80; 
                int x = (getWidth() - size) / 2;
                int y = 0;
                try {
                    URL imgUrl = getClass().getResource(finalAvatarPath);
                    if (imgUrl != null) {
                        ImageIcon icon = new ImageIcon(imgUrl);
                        Ellipse2D.Double circle = new Ellipse2D.Double(x, y, size, size);
                        g2.setClip(circle);
                        g2.drawImage(icon.getImage(), x, y, size, size, this);
                        g2.setClip(null);
                        g2.setColor(Color.WHITE);
                        g2.setStroke(new BasicStroke(3f));
                        g2.draw(circle);
                    }
                } catch (Exception e) { e.printStackTrace(); }
            }
        };
        pnlAvatar.setPreferredSize(new Dimension(280, 90));
        pnlAvatar.setBackground(mainColor);
        panelUser.add(pnlAvatar);

        // Tên hiển thị
        String displayText = dTO_TaiKhoan.getUserName().toUpperCase();
        if (dTO_TaiKhoan.getPhanQuyen() == 1) displayText = "QUẢN TRỊ VIÊN";
        else if (dTO_TaiKhoan.getPhanQuyen() == 2) displayText = "THỦ THƯ";

        JLabel lblInfo = new JLabel(displayText);
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblInfo.setForeground(Color.WHITE);
        lblInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblInfo.setBorder(new EmptyBorder(10, 0, 10, 0));
        panelUser.add(lblInfo);
        
        panelMenu.add(panelUser, BorderLayout.NORTH);

        // --- 2. MENU LIST ---
        JPanel panelList = new JPanel();
        panelList.setLayout(new BoxLayout(panelList, BoxLayout.Y_AXIS)); 
        panelList.setBackground(mainColor);

        // Thêm nút Trang Chủ (Chung cho tất cả)
        panelList.add(createMenuButton("Trang Chủ"));

        if (dTO_TaiKhoan.getPhanQuyen() == 3) { // --- ĐỘC GIẢ ---
            panelList.add(createMenuButton("Tra Cứu Sách"));
            panelList.add(createMenuButton("Lịch Sử Mượn"));
            panelList.add(createMenuButton("Thông Tin Cá Nhân"));
        } else { // --- ADMIN (1) & THỦ THƯ (2) ---
            panelList.add(createMenuButton("Quản Lý Sách"));
            panelList.add(createMenuButton("Quản Lý Nhập Hàng")); 
            
            panelList.add(createMenuButton("Quản Lý Thể Loại")); 
            panelList.add(createMenuButton("Quản Lý Độc Giả"));
            panelList.add(createMenuButton("Quản Lý Mượn Trả"));
            
            if (dTO_TaiKhoan.getPhanQuyen() == 1) {
                panelList.add(createMenuButton("Quản Lý Thủ Thư")); 
            }

            panelList.add(createMenuButton("Thống Kê"));
            
            // Module Cài đặt & Sao lưu
            if (dTO_TaiKhoan.getPhanQuyen() == 1) {
                panelList.add(createMenuButton("Cài Đặt Hệ Thống"));
                panelList.add(createMenuButton("Sao Lưu Dữ Liệu"));
            }
        }

        JScrollPane scrollPane = new JScrollPane(panelList);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); 
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        
        panelMenu.add(scrollPane, BorderLayout.CENTER);

        // --- 3. LOGOUT ---
        JButton btnLogout = new JButton("Đăng Xuất");
        btnLogout.setPreferredSize(new Dimension(240, 45));
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setBackground(new Color(220, 53, 69)); 
        btnLogout.setBorder(BorderFactory.createEmptyBorder());
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> {
            this.dispose();
            new GUI_Login().setVisible(true);
        });

        JPanel pnlBottom = new JPanel();
        pnlBottom.setBackground(mainColor);
        pnlBottom.setBorder(new EmptyBorder(10, 0, 20, 0));
        pnlBottom.add(btnLogout);
        
        panelMenu.add(pnlBottom, BorderLayout.SOUTH);

        add(panelMenu, BorderLayout.WEST);

        // =================================================================
        // --- B. MAIN CONTENT ---
        // =================================================================
        JPanel pnlRight = new JPanel(new BorderLayout());
        
        // Header
        panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(Color.WHITE);
        panelHeader.setPreferredSize(new Dimension(0, 60));
        panelHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 220, 245)));
        
        lblClock = new JLabel("00:00:00 | 01/01/2026");
        lblClock.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblClock.setForeground(mainColor); 
        lblClock.setBorder(new EmptyBorder(0, 0, 0, 30));
        panelHeader.add(lblClock, BorderLayout.EAST);
        
        pnlRight.add(panelHeader, BorderLayout.NORTH);

        // Body
        panelContent = new JPanel(new BorderLayout());
        panelContent.setBackground(bgColor);
        
        // [CẬP NHẬT] Truyền thông tin tài khoản vào để phân quyền hiển thị Dashboard
        panelContent.add(new GUI_TrangChu(dTO_TaiKhoan)); 

        pnlRight.add(panelContent, BorderLayout.CENTER);
        add(pnlRight, BorderLayout.CENTER);
    }

    // --- LOGIC HỖ TRỢ ---

    private void startClock() {
        Timer timer = new Timer(1000, e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss  |  dd/MM/yyyy");
            lblClock.setText(sdf.format(new Date()));
        });
        timer.start();
    }

    private String getGioiTinhDocGia(String maDG) {
        String gioitinh = "Nam"; 
        if(maDG == null) return gioitinh;
        try {
            Connection conn = new DBConnect().getConnection();
            String sql = "SELECT GioiTinh FROM DOC_GIA WHERE MaDocGia = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, maDG);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) gioitinh = rs.getString("GioiTinh");
            conn.close();
        } catch (Exception e) { e.printStackTrace(); }
        return gioitinh;
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(280, 40)); 
        btn.setPreferredSize(new Dimension(250, 40));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(normalText);
        btn.setBackground(mainColor); 
        btn.setBorder(new EmptyBorder(0, 30, 0, 0)); 
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn != btnActive) btn.setBackground(hoverColor);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (btn != btnActive) btn.setBackground(mainColor);
            }
        });

        btn.addActionListener(e -> {
            if (btnActive != null) {
                btnActive.setBackground(mainColor);
                btnActive.setForeground(normalText);
            }
            btnActive = btn;
            btnActive.setBackground(activeColor);
            btnActive.setForeground(activeText);
            
            changeContent(text);
        });

        if(btnActive == null && text.equals("Trang Chủ")) {
            btnActive = btn;
            btn.setBackground(activeColor);
            btn.setForeground(activeText);
        }
        return btn;
    }

    // --- HÀM CHUYỂN ĐỔI GIAO DIỆN CHÍNH ---
    private void changeContent(String menuName) {
        // Với các chức năng thường thì xóa content cũ để load cái mới
        panelContent.removeAll();

        switch (menuName) {
            case "Trang Chủ":
                // [CẬP NHẬT] Truyền thông tin tài khoản vào để phân quyền hiển thị Dashboard
                panelContent.add(new GUI_TrangChu(dTO_TaiKhoan));
                break;
            case "Quản Lý Sách":
                panelContent.add(new GUI_QuanLySach());                
                break;
            case "Quản Lý Nhập Hàng":
                String maNV = dTO_TaiKhoan.getMaThuThu();
                if (maNV == null) maNV = dTO_TaiKhoan.getUserName(); 
                panelContent.add(new GUI_QuanLyNhapHang(maNV, dTO_TaiKhoan.getUserName()));
                break;
            case "Quản Lý Thể Loại":
                panelContent.add(new GUI_QuanLyTheLoai());                
                break;
            case "Quản Lý Độc Giả":
                panelContent.add(new GUI_QuanLyDocGia());                   
                break;
            case "Quản Lý Mượn Trả":
                panelContent.add(new GUI_QuanLyMuonTra());
                break;
            case "Quản Lý Thủ Thư": 
                panelContent.add(new GUI_QuanLyThuThu());
                break;
            case "Thống Kê": 
                panelContent.add(new GUI_ThongKe());
                break;
            case "Cài Đặt Hệ Thống":
                panelContent.add(new GUI_CauHinh());
                break;
            case "Sao Lưu Dữ Liệu":
                panelContent.add(new GUI_SaoLuuPhucHoi());
                break;
            case "Tra Cứu Sách":
                panelContent.add(new GUI_TraCuuSach());
                break;
            case "Lịch Sử Mượn":
                String maDocGia = dTO_TaiKhoan.getMaDocGia(); 
                panelContent.add(new GUI_LichSuMuon(maDocGia)); 
                break;
            case "Thông Tin Cá Nhân":
                panelContent.add(new GUI_ThongTinCaNhan(dTO_TaiKhoan.getMaDocGia(), dTO_TaiKhoan.getUserName()));                
                break;
            default:
                panelContent.add(createPlaceholder("Chức năng: " + menuName));
                break;
                
        }
        panelContent.revalidate();
        panelContent.repaint();
    }

    private JPanel createPlaceholder(String title) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(bgColor);
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lbl.setForeground(new Color(200, 200, 200));
        p.add(lbl);
        return p;
    }

    public DTO_TaiKhoan getTaiKhoan() {
        return this.dTO_TaiKhoan;
    }
}