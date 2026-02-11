package HETHONG;

import DOCGIA.DAL_DocGia;
import DOCGIA.DTO_DocGia;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;

public class GUI_ThongTinCaNhan extends JPanel {

    private String maDocGia;
    private String currentUserName; 
    private JTextField txtMa, txtTen, txtLop, txtDiaChi, txtSDT, txtNgaySinh, txtGioiTinh;
    private DAL_DocGia dal = new DAL_DocGia();

    // Constructor nhận thông tin từ Main
    public GUI_ThongTinCaNhan(String maDocGia, String userName) {
        this.maDocGia = maDocGia;
        this.currentUserName = userName;
        
        System.out.println("=> Đang tải giao diện cho Độc giả: " + maDocGia); // Log debug
        
        try {
            initUI();
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi vẽ giao diện: " + e.getMessage());
        }
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 248, 253)); 
        setBorder(new EmptyBorder(20, 20, 20, 20)); 

        // --- 1. CARD CONTENT (Khung trắng bo tròn) ---
        JPanel pnlCard = new JPanel(null); // Layout null để setBounds tự do
        pnlCard.setBackground(Color.WHITE);
        pnlCard.putClientProperty(FlatClientProperties.STYLE, "arc: 20; border: 1,1,1,1, #E0E0E0");
        
        // [CỰC QUAN TRỌNG] Ép kích thước để Swing không tự thu nhỏ Form
        Dimension cardSize = new Dimension(850, 600);
        pnlCard.setPreferredSize(cardSize);
        pnlCard.setMinimumSize(cardSize);
        pnlCard.setMaximumSize(cardSize);
        
        // --- 2. HEADER ---
        JLabel lblHeader = new JLabel("HỒ SƠ CÁ NHÂN", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblHeader.setForeground(new Color(50, 115, 220)); 
        lblHeader.setBounds(0, 30, 850, 40);
        pnlCard.add(lblHeader);

        // --- 3. FORM INPUT ---
        int x = 180, y = 100, wLabel = 150, wText = 400, h = 40, gap = 55;

        // Khởi tạo các ô nhập liệu
        txtMa = createField(false);
        txtTen = createField(false);
        txtNgaySinh = createField(false);
        txtGioiTinh = createField(false);
        txtLop = createField(true);
        txtSDT = createField(true);
        txtDiaChi = createField(true);

        // Gắn vào Card
        addFormRow(pnlCard, "Mã Độc Giả:", x, y, wLabel, wText, h, txtMa);
        y += gap;
        addFormRow(pnlCard, "Họ và Tên:", x, y, wLabel, wText, h, txtTen);
        y += gap;
        addFormRow(pnlCard, "Ngày Sinh:", x, y, wLabel, wText, h, txtNgaySinh);
        y += gap;
        addFormRow(pnlCard, "Giới Tính:", x, y, wLabel, wText, h, txtGioiTinh);
        y += gap;
        addFormRow(pnlCard, "Lớp/Đơn vị:", x, y, wLabel, wText, h, txtLop); 
        y += gap;
        addFormRow(pnlCard, "Số Điện Thoại:", x, y, wLabel, wText, h, txtSDT); 
        y += gap;
        addFormRow(pnlCard, "Địa Chỉ:", x, y, wLabel, wText, h, txtDiaChi); 

        // --- 4. BUTTONS ---
        int yBtn = y + 70;
        
        JButton btnSave = new JButton("Lưu Thay Đổi");
        setupButton(btnSave, new Color(40, 167, 69)); 
        btnSave.setBounds(x + 20, yBtn, 170, 45);
        btnSave.addActionListener(e -> xuLyCapNhat());
        pnlCard.add(btnSave);

        JButton btnChangePass = new JButton("Đổi Mật Khẩu");
        setupButton(btnChangePass, new Color(255, 193, 7)); 
        btnChangePass.setForeground(Color.BLACK);
        btnChangePass.setBounds(x + 210, yBtn, 170, 45);
        btnChangePass.addActionListener(e -> {
            DTO_TaiKhoan tkTam = new DTO_TaiKhoan();
            tkTam.setUserName(currentUserName);
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            new GUI_DialogDoiMatKhau(parentFrame, tkTam).setVisible(true);
        });
        pnlCard.add(btnChangePass);

        // --- WRAPPER (Cách an toàn nhất để canh giữa) ---
        JPanel pnlWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 30));
        pnlWrapper.setBackground(new Color(245, 248, 253));
        pnlWrapper.add(pnlCard);
        
        add(pnlWrapper, BorderLayout.CENTER);
    }

    private JTextField createField(boolean editable) {
        JTextField t = new JTextField();
        t.setFont(new Font("Segoe UI", Font.BOLD, 15));
        t.setEditable(editable);
        if(editable) {
            t.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderColor: #cccccc; focusedBorderColor: #1877F2; borderWidth: 1");
            t.setBackground(Color.WHITE);
        } else {
            t.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0"); 
            t.setBackground(new Color(242, 242, 242)); 
            t.setForeground(Color.GRAY);
        }
        return t;
    }

    private void addFormRow(JPanel p, String title, int x, int y, int wLbl, int wTxt, int h, JTextField txt) {
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(new Color(80, 80, 80));
        lbl.setBounds(x, y, wLbl, h);
        p.add(lbl);

        txt.setBounds(x + wLbl, y, wTxt, h);
        p.add(txt);
    }

    private void setupButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0");
    }

    private void loadData() {
        if (maDocGia == null || maDocGia.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lỗi: Không tìm thấy Mã Độc Giả!");
            return;
        }

        DTO_DocGia dg = dal.getChiTiet(maDocGia); 
        
        if (dg != null) {
            txtMa.setText(dg.getMaDocGia());
            txtTen.setText(dg.getTenDocGia());
            txtLop.setText(dg.getLop());
            txtDiaChi.setText(dg.getDiaChi());
            txtSDT.setText(dg.getSoDienThoai());
            
            if(dg.getNgaySinh() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                txtNgaySinh.setText(sdf.format(dg.getNgaySinh()));
            } else {
                txtNgaySinh.setText("Chưa cập nhật");
            }

            if(dg.getGioiTinh() != null && !dg.getGioiTinh().trim().isEmpty()) {
                txtGioiTinh.setText(dg.getGioiTinh());
            } else {
                txtGioiTinh.setText("Chưa cập nhật");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hồ sơ trên CSDL!");
        }
    }

    private void xuLyCapNhat() {
        if (txtLop.getText().isEmpty() || txtDiaChi.getText().isEmpty() || txtSDT.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin (Lớp, Địa chỉ, SĐT)!");
            return;
        }

        DTO_DocGia dgMoi = new DTO_DocGia();
        dgMoi.setMaDocGia(txtMa.getText());
        dgMoi.setLop(txtLop.getText());
        dgMoi.setDiaChi(txtDiaChi.getText());
        dgMoi.setSoDienThoai(txtSDT.getText());

        if (dal.capNhatThongTinCaNhan(dgMoi)) { 
            JOptionPane.showMessageDialog(this, "Cập nhật hồ sơ thành công!");
            loadData(); 
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại! Vui lòng kiểm tra lại kết nối Database.");
        }
    }
}