package NHAPHANG;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

// Import các module khác
import CHUNG.DBConnect;
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

    public GUI_TaoPhieuNhap(String maNhanVien, String tenNhanVien) {
        this.maNhanVienHienTai = maNhanVien;
        setTitle("Tạo Phiếu Nhập Hàng Mới");
        setSize(1100, 680); 
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- PHẦN 1: HEADER ---
        JPanel pnlHeader = new JPanel(new GridLayout(2, 1));
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        pnlHeader.setBackground(new Color(230, 240, 255));

        JLabel lblTitle = new JLabel("PHIẾU NHẬP KHO", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(new Color(0, 51, 153));
        
        JPanel pnlInfo = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 5));
        pnlInfo.setOpaque(false);
        
        lblNguoiNhap = new JLabel("Người nhập: " + tenNhanVien + " (" + maNhanVien + ")");
        lblNguoiNhap.setFont(new Font("Arial", Font.BOLD, 14));
        
        String today = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        lblNgayNhap = new JLabel("Ngày lập: " + today);
        lblNgayNhap.setFont(new Font("Arial", Font.ITALIC, 14));

        pnlInfo.add(lblNguoiNhap);
        pnlInfo.add(lblNgayNhap);
        
        pnlHeader.add(lblTitle);
        pnlHeader.add(pnlInfo);
        add(pnlHeader, BorderLayout.NORTH);

        // --- PHẦN 2: CENTER ---
        JPanel pnlCenter = new JPanel(new BorderLayout(10, 10));
        pnlCenter.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setBorder(BorderFactory.createTitledBorder("Thông tin nhập"));
        pnlInput.setPreferredSize(new Dimension(380, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 10, 12, 10); 
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        pnlInput.add(new JLabel("Chọn Nhà Cung Cấp:"), gbc);
        
        gbc.gridy = 1;
        cboNCC = new JComboBox<>();
        cboNCC.setPreferredSize(new Dimension(300, 30));
        loadDataNCC(); 
        pnlInput.add(cboNCC, gbc);

        gbc.gridy = 2;
        pnlInput.add(new JLabel("Chọn Sách cần nhập:"), gbc);
        
        gbc.gridy = 3;
        cboSach = new JComboBox<>();
        cboSach.setPreferredSize(new Dimension(300, 30));
        loadDataSach(); 
        pnlInput.add(cboSach, gbc);

        gbc.gridy = 4;
        pnlInput.add(new JLabel("Số lượng nhập:"), gbc);
        
        gbc.gridy = 5;
        txtSoLuong = new JTextField();
        txtSoLuong.setPreferredSize(new Dimension(300, 30));
        pnlInput.add(txtSoLuong, gbc);

        gbc.gridy = 6;
        pnlInput.add(new JLabel("Giá nhập 1 cuốn (VNĐ):"), gbc);
        
        gbc.gridy = 7;
        txtDonGia = new JTextField();
        txtDonGia.setPreferredSize(new Dimension(300, 30));
        pnlInput.add(txtDonGia, gbc);

        gbc.gridy = 8;
        btnThem = new JButton("Thêm vào giỏ hàng");
        btnThem.setBackground(new Color(0, 153, 76));
        btnThem.setForeground(Color.WHITE);
        btnThem.setFont(new Font("Arial", Font.BOLD, 13));
        btnThem.setPreferredSize(new Dimension(0, 45));
        pnlInput.add(btnThem, gbc);

        pnlCenter.add(pnlInput, BorderLayout.WEST);

        String[] cols = {"Mã Sách", "Tên Sách", "Số Lượng", "Đơn Giá", "Thành Tiền"};
        model = new DefaultTableModel(cols, 0);
        tblChiTiet = new JTable(model);
        tblChiTiet.setRowHeight(28);
        tblChiTiet.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblChiTiet.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblChiTiet.getTableHeader().setBackground(new Color(240, 240, 240));
        pnlCenter.add(new JScrollPane(tblChiTiet), BorderLayout.CENTER);

        add(pnlCenter, BorderLayout.CENTER);

        // --- PHẦN 3: BOTTOM ---
        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        pnlBottom.setPreferredSize(new Dimension(0, 80));
        pnlBottom.setBackground(Color.WHITE);

        JPanel pnlTong = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        pnlTong.setBackground(Color.WHITE);
        
        lblTongTien = new JLabel("Tổng tiền: 0 VNĐ");
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 22));
        lblTongTien.setForeground(new Color(204, 0, 0));

        btnXoa = new JButton("Xóa dòng chọn");
        btnXoa.setBackground(new Color(255, 102, 102));
        btnXoa.setForeground(Color.WHITE);

        btnLuu = new JButton("LƯU & NHẬP KHO");
        btnLuu.setFont(new Font("Arial", Font.BOLD, 16));
        btnLuu.setBackground(new Color(0, 102, 204));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setPreferredSize(new Dimension(220, 45));

        pnlTong.add(btnXoa);
        pnlTong.add(lblTongTien);
        pnlTong.add(btnLuu);
        
        pnlBottom.add(pnlTong, BorderLayout.CENTER);
        add(pnlBottom, BorderLayout.SOUTH);

        // --- SỰ KIỆN ---
        xuLySuKien();
    }

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

                for (int i = 0; i < listChiTiet.size(); i++) {
                    if (listChiTiet.get(i).getMaSach().equals(maSach)) {
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
                JOptionPane.showMessageDialog(this, "Chọn dòng để xóa!");
            }
        });

        // 3. Lưu phiếu
        btnLuu.addActionListener(e -> {
            if (listChiTiet.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Giỏ hàng đang trống!");
                return;
            }

            if (JOptionPane.showConfirmDialog(this, "Xác nhận nhập hàng?\nKho sách sẽ được cộng thêm số lượng.", "Chốt đơn", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                
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
                    JOptionPane.showMessageDialog(this, "✅ NHẬP HÀNG THÀNH CÔNG!\nĐã cập nhật số lượng sách vào kho.");
                    this.dispose(); 
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Lỗi khi lưu vào CSDL!");
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