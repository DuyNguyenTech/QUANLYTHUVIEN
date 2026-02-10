package NHAPHANG;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

// Import các module khác
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
    
    // Màu chủ đạo
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
        setSize(1150, 700); 
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setBackground(bgColor);

        // --- 1. HEADER ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(new EmptyBorder(15, 20, 15, 20));
        pnlHeader.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel lblTitle = new JLabel("LẬP PHIẾU NHẬP KHO");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(mainColor);
        
        JPanel pnlInfo = new JPanel(new GridLayout(2, 1));
        pnlInfo.setBackground(Color.WHITE);
        
        lblNguoiNhap = new JLabel("Nhân viên: " + tenNhanVien + " (" + maNhanVienHienTai + ")");
        lblNguoiNhap.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNguoiNhap.setForeground(Color.DARK_GRAY);
        
        String today = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
        lblNgayNhap = new JLabel("Ngày lập: " + today);
        lblNgayNhap.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblNgayNhap.setForeground(Color.GRAY);

        pnlInfo.add(lblNguoiNhap);
        pnlInfo.add(lblNgayNhap);
        
        pnlHeader.add(lblTitle, BorderLayout.WEST);
        pnlHeader.add(pnlInfo, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. CENTER (INPUT + TABLE) ---
        JPanel pnlCenter = new JPanel(new BorderLayout(15, 15));
        pnlCenter.setBorder(new EmptyBorder(15, 15, 15, 15));
        pnlCenter.setBackground(bgColor);

        // A. PANEL NHẬP LIỆU (TRÁI)
        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setBackground(Color.WHITE);
        pnlInput.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            "Thông tin nhập", 
            TitledBorder.DEFAULT_JUSTIFICATION, 
            TitledBorder.DEFAULT_POSITION, 
            new Font("Segoe UI", Font.BOLD, 14), 
            mainColor
        ));
        pnlInput.setPreferredSize(new Dimension(380, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10); 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Các component nhập liệu
        gbc.gridx = 0; gbc.gridy = 0;
        pnlInput.add(createLabel("Nhà Cung Cấp:"), gbc);
        
        gbc.gridy = 1;
        cboNCC = new JComboBox<>();
        cboNCC.setPreferredSize(new Dimension(300, 35));
        cboNCC.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboNCC.setBackground(Color.WHITE);
        pnlInput.add(cboNCC, gbc);

        gbc.gridy = 2;
        pnlInput.add(createLabel("Sách Cần Nhập:"), gbc);
        
        gbc.gridy = 3;
        cboSach = new JComboBox<>();
        cboSach.setPreferredSize(new Dimension(300, 35));
        cboSach.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboSach.setBackground(Color.WHITE);
        pnlInput.add(cboSach, gbc);

        gbc.gridy = 4;
        pnlInput.add(createLabel("Số Lượng:"), gbc);
        
        gbc.gridy = 5;
        txtSoLuong = createTextField();
        pnlInput.add(txtSoLuong, gbc);

        gbc.gridy = 6;
        pnlInput.add(createLabel("Đơn Giá Nhập (VNĐ):"), gbc);
        
        gbc.gridy = 7;
        txtDonGia = createTextField();
        pnlInput.add(txtDonGia, gbc);

        gbc.gridy = 8;
        gbc.insets = new Insets(20, 10, 10, 10); // Cách xa nút bấm chút
        btnThem = createButton("+ Thêm Vào Giỏ", new Color(40, 167, 69));
        btnThem.setPreferredSize(new Dimension(0, 45));
        pnlInput.add(btnThem, gbc);
        
        // Đẩy các thành phần lên trên cùng
        gbc.gridy = 9; gbc.weighty = 1.0;
        pnlInput.add(new JLabel(), gbc);

        pnlCenter.add(pnlInput, BorderLayout.WEST);

        // B. BẢNG CHI TIẾT (PHẢI)
        JPanel pnlTable = new JPanel(new BorderLayout());
        pnlTable.setBackground(Color.WHITE);
        pnlTable.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            "Chi tiết phiếu nhập", 
            TitledBorder.DEFAULT_JUSTIFICATION, 
            TitledBorder.DEFAULT_POSITION, 
            new Font("Segoe UI", Font.BOLD, 14), 
            mainColor
        ));

        String[] cols = {"Mã Sách", "Tên Sách", "Số Lượng", "Đơn Giá", "Thành Tiền"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblChiTiet = new JTable(model);
        
        // Style Table
        tblChiTiet.setRowHeight(35);
        tblChiTiet.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblChiTiet.setShowVerticalLines(false);
        tblChiTiet.setIntercellSpacing(new Dimension(0, 0));
        tblChiTiet.setSelectionBackground(new Color(232, 242, 252));
        tblChiTiet.setSelectionForeground(Color.BLACK);
        
        JTableHeader header = tblChiTiet.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(Color.WHITE);
        header.setForeground(mainColor);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, mainColor));
        header.setPreferredSize(new Dimension(0, 40));

        // Renderer
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        DefaultTableCellRenderer moneyRenderer = new DefaultTableCellRenderer();
        moneyRenderer.setHorizontalAlignment(JLabel.CENTER);
        moneyRenderer.setForeground(new Color(180, 0, 0)); // Màu đỏ tiền
        moneyRenderer.setFont(new Font("Segoe UI", Font.BOLD, 14));

        for(int i=0; i<tblChiTiet.getColumnCount(); i++) {
            if(i >= 3) tblChiTiet.getColumnModel().getColumn(i).setCellRenderer(moneyRenderer);
            else tblChiTiet.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        tblChiTiet.getColumnModel().getColumn(1).setPreferredWidth(200);
        pnlTable.add(new JScrollPane(tblChiTiet), BorderLayout.CENTER);
        pnlCenter.add(pnlTable, BorderLayout.CENTER);

        add(pnlCenter, BorderLayout.CENTER);

        // --- 3. BOTTOM (TỔNG TIỀN & LƯU) ---
        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));
        pnlBottom.setPreferredSize(new Dimension(0, 80));
        pnlBottom.setBackground(Color.WHITE);

        JPanel pnlTong = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        pnlTong.setBackground(Color.WHITE);
        
        lblTongTien = new JLabel("Tổng tiền: 0 VNĐ");
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTongTien.setForeground(new Color(220, 53, 69)); // Đỏ

        btnXoa = createButton("Xóa Dòng", new Color(255, 152, 0));
        btnXoa.setPreferredSize(new Dimension(140, 45));

        btnLuu = createButton("LƯU & NHẬP KHO", mainColor);
        btnLuu.setPreferredSize(new Dimension(200, 45));

        pnlTong.add(btnXoa);
        pnlTong.add(Box.createHorizontalStrut(20)); // Khoảng cách
        pnlTong.add(lblTongTien);
        pnlTong.add(Box.createHorizontalStrut(20));
        pnlTong.add(btnLuu);
        
        pnlBottom.add(pnlTong, BorderLayout.CENTER);
        add(pnlBottom, BorderLayout.SOUTH);

        // --- SỰ KIỆN ---
        xuLySuKien();
    }
    
    // --- HELPER UI ---
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(80, 80, 80));
        return lbl;
    }
    
    private JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setPreferredSize(new Dimension(300, 35));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 8, 5, 8)
        ));
        return tf;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // --- LOGIC ---

    private void xuLySuKien() {
        // 1. Thêm sách
        btnThem.addActionListener(e -> {
            try {
                if (cboSach.getSelectedIndex() < 0) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn sách!");
                    return;
                }
                
                String slStr = txtSoLuong.getText().trim();
                String giaStr = txtDonGia.getText().trim();

                if(slStr.isEmpty() || giaStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập số lượng và đơn giá!");
                    return;
                }

                int sl = Integer.parseInt(slStr);
                double gia = Double.parseDouble(giaStr);

                if (sl <= 0 || gia <= 0) {
                    JOptionPane.showMessageDialog(this, "Số lượng và giá phải lớn hơn 0");
                    return;
                }

                int index = cboSach.getSelectedIndex();
                DTO_Sach sachChon = listSachGoc.get(index);
                
                String maSach = sachChon.getMaCuonSach(); 
                String tenSach = sachChon.getTenSach();

                for (DTO_ChiTietNhap ct : listChiTiet) {
                    if (ct.getMaSach().equals(maSach)) {
                        JOptionPane.showMessageDialog(this, "Sách này đã có trong giỏ hàng!");
                        return;
                    }
                }

                DTO_ChiTietNhap ct = new DTO_ChiTietNhap(null, maSach, sl, gia);
                listChiTiet.add(ct);

                DecimalFormat df = new DecimalFormat("#,###");
                model.addRow(new Object[]{maSach, tenSach, sl, df.format(gia), df.format(ct.getThanhTien())});

                tinhTongTien();
                
                txtSoLuong.setText("");
                txtDonGia.setText("");
                txtSoLuong.requestFocus();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi nhập liệu: Số lượng và Giá phải là số!");
            }
        });

        // 2. Xóa dòng
        btnXoa.addActionListener(e -> {
            int row = tblChiTiet.getSelectedRow();
            if (row >= 0) {
                listChiTiet.remove(row);
                model.removeRow(row);
                tinhTongTien();
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng để xóa!");
            }
        });

        // 3. Lưu phiếu
        btnLuu.addActionListener(e -> {
            if (listChiTiet.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Giỏ hàng đang trống!");
                return;
            }

            if (JOptionPane.showConfirmDialog(this, 
                "Xác nhận nhập hàng?\nKho sách sẽ được cộng thêm số lượng.", 
                "Xác nhận nhập kho", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                
                String maPhieu = "PN" + new SimpleDateFormat("yyMMddHHmm").format(new Date());
                
                DTO_NhaCungCap ncc = (DTO_NhaCungCap) cboNCC.getSelectedItem();
                double tongTien = layTongTienSo();

                DTO_PhieuNhap pn = new DTO_PhieuNhap(
                    maPhieu, 
                    ncc.getMaNCC(), 
                    maNhanVienHienTai, 
                    new java.sql.Date(new Date().getTime()), 
                    tongTien
                );

                ArrayList<DTO_ChiTietNhap> listSave = new ArrayList<>();
                for(DTO_ChiTietNhap ct : listChiTiet) {
                    listSave.add(new DTO_ChiTietNhap(maPhieu, ct.getMaSach(), ct.getSoLuong(), ct.getDonGia()));
                }

                boolean kq = dalNhap.taoPhieuNhap(pn, listSave);
                
                if (kq) {
                    JOptionPane.showMessageDialog(this, "NHẬP HÀNG THÀNH CÔNG!\nĐã cập nhật số lượng sách vào kho.");
                    this.dispose(); 
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi khi lưu vào CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void tinhTongTien() {
        double tong = layTongTienSo();
        DecimalFormat df = new DecimalFormat("#,### VNĐ");
        lblTongTien.setText("Tổng tiền: " + df.format(tong));
    }

    private double layTongTienSo() {
        double tong = 0;
        for (DTO_ChiTietNhap ct : listChiTiet) {
            tong += ct.getThanhTien();
        }
        return tong;
    }

    private void loadDataNCC() {
        ArrayList<DTO_NhaCungCap> list = dalNCC.getAllNCC();
        for (DTO_NhaCungCap ncc : list) {
            cboNCC.addItem(ncc);
        }
    }

    private void loadDataSach() {
        try {
            listSachGoc = dalSach.getList(); 
            for (DTO_Sach s : listSachGoc) {
                cboSach.addItem(s.getMaCuonSach() + " - " + s.getTenSach());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}