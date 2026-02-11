package NHAPHANG;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import SACH.DAL_Sach; 
import SACH.DTO_Sach; 

public class GUI_TaoPhieuNhap extends JFrame {

    private JComboBox<DTO_NhaCungCap> cboNCC;
    private JComboBox<String> cboSach; 
    private JTextField txtSoLuong, txtDonGia;
    private JLabel lblTongTien, lblNguoiNhap, lblNgayNhap;
    private JTable tblChiTiet;
    private DefaultTableModel model;
    private JButton btnThem, btnXoa, btnLuu;

    private ArrayList<DTO_ChiTietNhap> listChiTiet = new ArrayList<>();
    private ArrayList<DTO_Sach> listSachGoc = new ArrayList<>(); 
    
    private DAL_NhaCungCap dalNCC = new DAL_NhaCungCap();
    private DAL_NhapHang dalNhap = new DAL_NhapHang();
    private DAL_Sach dalSach = new DAL_Sach(); 

    private String maNhanVienHienTai; 
    private Color mainColor = new Color(50, 115, 220);
    private Color bgColor = new Color(245, 248, 253);

    public GUI_TaoPhieuNhap(String maNhanVien, String tenNhanVien) {
        this.maNhanVienHienTai = maNhanVien;
        initUI(tenNhanVien);
        loadDataNCC();
        loadDataSach();
    }

    private void initUI(String tenNhanVien) {
        setTitle("TẠO PHIẾU NHẬP HÀNG");
        setSize(1200, 750); 
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(bgColor);

        // --- 1. HEADER ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            new EmptyBorder(15, 25, 15, 25)
        ));

        JLabel lblTitle = new JLabel("LẬP PHIẾU NHẬP KHO");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(mainColor);
        
        JPanel pnlInfo = new JPanel(new GridLayout(2, 1, 0, 5));
        pnlInfo.setBackground(Color.WHITE);
        lblNguoiNhap = new JLabel("Nhân viên: " + tenNhanVien + " (" + maNhanVienHienTai + ")", SwingConstants.RIGHT);
        lblNguoiNhap.setFont(new Font("Segoe UI", Font.BOLD, 15));
        String today = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
        lblNgayNhap = new JLabel("Ngày lập: " + today, SwingConstants.RIGHT);
        lblNgayNhap.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblNgayNhap.setForeground(Color.GRAY);
        pnlInfo.add(lblNguoiNhap);
        pnlInfo.add(lblNgayNhap);
        
        pnlHeader.add(lblTitle, BorderLayout.WEST);
        pnlHeader.add(pnlInfo, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. CENTER (INPUT + TABLE + TỔNG TIỀN) ---
        JPanel pnlCenterOuter = new JPanel(new BorderLayout(20, 20));
        pnlCenterOuter.setBorder(new EmptyBorder(20, 25, 10, 25));
        pnlCenterOuter.setBackground(bgColor);

        // A. PANEL NHẬP LIỆU (TRÁI)
        JPanel pnlInputWrapper = new JPanel(new BorderLayout(0, 10));
        pnlInputWrapper.setBackground(Color.WHITE);
        pnlInputWrapper.putClientProperty(FlatClientProperties.STYLE, "arc: 20; border: 1,1,1,1, #E0E0E0");
        pnlInputWrapper.setBorder(new EmptyBorder(15, 15, 15, 15));
        pnlInputWrapper.setPreferredSize(new Dimension(420, 0));

        JLabel lblTitleInput = new JLabel("Thông Tin Nhập Sách", SwingConstants.LEFT);
        lblTitleInput.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitleInput.setForeground(mainColor);
        pnlInputWrapper.add(lblTitleInput, BorderLayout.NORTH);

        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5); gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;

        gbc.gridy = 0; pnlInput.add(createLabel("Nhà Cung Cấp:"), gbc);
        gbc.gridy = 1; cboNCC = new JComboBox<>(); cboNCC.setPreferredSize(new Dimension(300, 42));
        cboNCC.putClientProperty(FlatClientProperties.STYLE, "arc: 10"); pnlInput.add(cboNCC, gbc);

        gbc.gridy = 2; pnlInput.add(createLabel("Sách Cần Nhập:"), gbc);
        gbc.gridy = 3; cboSach = new JComboBox<>(); cboSach.setPreferredSize(new Dimension(300, 42));
        cboSach.putClientProperty(FlatClientProperties.STYLE, "arc: 10"); pnlInput.add(cboSach, gbc);

        gbc.gridy = 4; pnlInput.add(createLabel("Số Lượng:"), gbc);
        gbc.gridy = 5; txtSoLuong = createTextField(); pnlInput.add(txtSoLuong, gbc);

        gbc.gridy = 6; pnlInput.add(createLabel("Đơn Giá Nhập (VNĐ):"), gbc);
        gbc.gridy = 7; txtDonGia = createTextField(); pnlInput.add(txtDonGia, gbc);

        gbc.gridy = 8; gbc.insets = new Insets(25, 5, 10, 5);
        btnThem = createButton("+ Thêm Vào Phiếu Nhập", new Color(40, 167, 69));
        btnThem.setPreferredSize(new Dimension(0, 45)); pnlInput.add(btnThem, gbc);
        
        gbc.gridy = 9; gbc.weighty = 1.0; pnlInput.add(new JLabel(), gbc);
        pnlInputWrapper.add(pnlInput, BorderLayout.CENTER);
        pnlCenterOuter.add(pnlInputWrapper, BorderLayout.WEST);

        // B. PANEL BẢNG + TỔNG TIỀN (PHẢI)
        JPanel pnlTableAndTotal = new JPanel(new BorderLayout(0, 15));
        pnlTableAndTotal.setBackground(bgColor);

        // Bảng chi tiết bọc trong Card
        JPanel pnlTableWrapper = new JPanel(new BorderLayout(0, 10));
        pnlTableWrapper.setBackground(Color.WHITE);
        pnlTableWrapper.putClientProperty(FlatClientProperties.STYLE, "arc: 20; border: 1,1,1,1, #E0E0E0");
        pnlTableWrapper.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblTitleTable = new JLabel("Chi Tiết Phiếu Nhập", SwingConstants.LEFT);
        lblTitleTable.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitleTable.setForeground(mainColor);
        pnlTableWrapper.add(lblTitleTable, BorderLayout.NORTH);

        String[] cols = {"Mã Sách", "Tên Sách", "Số Lượng", "Đơn Giá", "Thành Tiền"};
        model = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tblChiTiet = new JTable(model);
        tblChiTiet.setRowHeight(40);
        tblChiTiet.setSelectionBackground(new Color(232, 242, 252));
        
        JTableHeader header = tblChiTiet.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(248, 249, 250));
        header.setPreferredSize(new Dimension(0, 45));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer(); center.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer money = new DefaultTableCellRenderer(); money.setHorizontalAlignment(JLabel.CENTER);
        money.setForeground(new Color(220, 53, 69)); money.setFont(new Font("Segoe UI", Font.BOLD, 14));

        for(int i=0; i<tblChiTiet.getColumnCount(); i++) {
            tblChiTiet.getColumnModel().getColumn(i).setCellRenderer(i >= 3 ? money : center);
        }
        tblChiTiet.getColumnModel().getColumn(1).setPreferredWidth(250);
        
        JScrollPane sc = new JScrollPane(tblChiTiet);
        sc.setBorder(BorderFactory.createEmptyBorder());
        sc.getViewport().setBackground(Color.WHITE);
        pnlTableWrapper.add(sc, BorderLayout.CENTER);
        
        // [QUAN TRỌNG] Đưa Tổng tiền vào đây để nó luôn bám sát bảng
        lblTongTien = new JLabel("Tổng tiền: 0 VNĐ", SwingConstants.RIGHT);
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTongTien.setForeground(new Color(220, 53, 69));
        lblTongTien.setBorder(new EmptyBorder(10, 0, 10, 10));
        
        pnlTableAndTotal.add(pnlTableWrapper, BorderLayout.CENTER);
        pnlTableAndTotal.add(lblTongTien, BorderLayout.SOUTH);

        pnlCenterOuter.add(pnlTableAndTotal, BorderLayout.CENTER);
        add(pnlCenterOuter, BorderLayout.CENTER);

        // --- 3. FOOTER (CÁC NÚT BẤM) ---
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 25, 25)); // Tăng Padding dưới
        pnlBottom.setBackground(Color.WHITE);
        pnlBottom.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));

        btnXoa = createButton("Xóa Dòng", new Color(255, 152, 0));
        btnXoa.setPreferredSize(new Dimension(150, 48));
        btnLuu = createButton("LƯU & NHẬP KHO", mainColor);
        btnLuu.setPreferredSize(new Dimension(220, 48));

        pnlBottom.add(btnXoa);
        pnlBottom.add(btnLuu);
        add(pnlBottom, BorderLayout.SOUTH);

        xuLySuKien();
    }
    
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(new Color(80, 80, 80));
        return lbl;
    }
    
    private JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setPreferredSize(new Dimension(300, 42));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tf.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderColor: #cccccc; focusedBorderColor: #1877F2; borderWidth: 1");
        return tf;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0");
        return btn;
    }

    private void xuLySuKien() {
        btnThem.addActionListener(e -> {
            try {
                if (cboSach.getSelectedIndex() < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn sách!"); return; }
                String slStr = txtSoLuong.getText().trim();
                String giaStr = txtDonGia.getText().trim();
                if(slStr.isEmpty() || giaStr.isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập số lượng và đơn giá!"); return; }
                int sl = Integer.parseInt(slStr);
                double gia = Double.parseDouble(giaStr);
                if (sl <= 0 || gia <= 0) { JOptionPane.showMessageDialog(this, "Số lượng và giá phải lớn hơn 0"); return; }
                int index = cboSach.getSelectedIndex();
                DTO_Sach sachChon = listSachGoc.get(index);
                String maSach = sachChon.getMaCuonSach(); 
                for (DTO_ChiTietNhap ct : listChiTiet) {
                    if (ct.getMaSach().equals(maSach)) { JOptionPane.showMessageDialog(this, "Sách này đã có trong phiếu nhập!"); return; }
                }
                DTO_ChiTietNhap ct = new DTO_ChiTietNhap(null, maSach, sl, gia);
                listChiTiet.add(ct);
                DecimalFormat df = new DecimalFormat("#,###");
                model.addRow(new Object[]{maSach, sachChon.getTenSach(), sl, df.format(gia), df.format(ct.getThanhTien())});
                tinhTongTien();
                txtSoLuong.setText(""); txtDonGia.setText(""); txtSoLuong.requestFocus();
            } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Lỗi nhập liệu!"); }
        });

        btnXoa.addActionListener(e -> {
            int row = tblChiTiet.getSelectedRow();
            if (row >= 0) { listChiTiet.remove(row); model.removeRow(row); tinhTongTien(); }
            else { JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng để xóa!"); }
        });

        btnLuu.addActionListener(e -> {
            if (listChiTiet.isEmpty()) { JOptionPane.showMessageDialog(this, "Phiếu nhập đang trống!"); return; }
            if (JOptionPane.showConfirmDialog(this, "Xác nhận nhập hàng?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                String maPhieu = "PN" + new SimpleDateFormat("yyMMddHHmm").format(new Date());
                DTO_NhaCungCap ncc = (DTO_NhaCungCap) cboNCC.getSelectedItem();
                DTO_PhieuNhap pn = new DTO_PhieuNhap(maPhieu, ncc.getMaNCC(), maNhanVienHienTai, new java.sql.Date(new Date().getTime()), layTongTienSo());
                ArrayList<DTO_ChiTietNhap> listSave = new ArrayList<>();
                for(DTO_ChiTietNhap ct : listChiTiet) { listSave.add(new DTO_ChiTietNhap(maPhieu, ct.getMaSach(), ct.getSoLuong(), ct.getDonGia())); }
                if (dalNhap.taoPhieuNhap(pn, listSave)) { JOptionPane.showMessageDialog(this, "NHẬP HÀNG THÀNH CÔNG!"); this.dispose(); }
                else { JOptionPane.showMessageDialog(this, "Lỗi khi lưu dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE); }
            }
        });
    }

    private void tinhTongTien() {
        DecimalFormat df = new DecimalFormat("#,### VNĐ");
        lblTongTien.setText("Tổng tiền: " + df.format(layTongTienSo()));
    }

    private double layTongTienSo() {
        double tong = 0;
        for (DTO_ChiTietNhap ct : listChiTiet) { tong += ct.getThanhTien(); }
        return tong;
    }

    private void loadDataNCC() {
        ArrayList<DTO_NhaCungCap> list = dalNCC.getAllNCC();
        for (DTO_NhaCungCap ncc : list) cboNCC.addItem(ncc);
    }

    private void loadDataSach() {
        listSachGoc = dalSach.getList(); 
        for (DTO_Sach s : listSachGoc) cboSach.addItem(s.getMaCuonSach() + " - " + s.getTenSach());
    }
}