package com.qlthuvien.GUI;

import com.qlthuvien.DAL.DAL_Sach;
import com.qlthuvien.DAL.DAL_DocGia;
import com.qlthuvien.DAL.DAL_PhieuMuon;
import com.qlthuvien.DTO.DTO_Sach;
import com.qlthuvien.DTO.DTO_DocGia;
import com.qlthuvien.DTO.DTO_PhieuMuon;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GUI_DialogPhieuMuon extends JDialog {

    private JTextField txtMaPhieu, txtMaDocGia, txtTenDocGia, txtThuThu, txtNgayMuon, txtHanTra, txtTienPhat;
    private JComboBox<String> cboTinhTrang;
    
    private JTable tblDocGia, tblKhoSach, tblSachMuon;
    private DefaultTableModel modelDocGia, modelKhoSach, modelSachMuon;
    
    private DAL_Sach dalSach = new DAL_Sach();
    private DAL_DocGia dalDocGia = new DAL_DocGia();
    private DAL_PhieuMuon dalPhieu = new DAL_PhieuMuon(); 
    
    private GUI_QuanLyMuonTra parentGUI; 
    private DTO_PhieuMuon phieuSua = null; 
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); // Định dạng ngày chuẩn Việt Nam

    // --- CONSTRUCTOR 1: DÙNG CHO THÊM MỚI ---
    public GUI_DialogPhieuMuon(JPanel parent) {
        super(SwingUtilities.getWindowAncestor(parent), ModalityType.APPLICATION_MODAL);
        this.parentGUI = (GUI_QuanLyMuonTra) parent;
        initUI();
        loadDataDocGia();
        loadDataKhoSach();
        autoGenMaPhieu();
        txtThuThu.setText("ADMIN"); // Có thể thay bằng mã thủ thư đang đăng nhập
    }

    // --- CONSTRUCTOR 2: DÙNG CHO CẬP NHẬT (SỬA) ---
    public GUI_DialogPhieuMuon(JPanel parent, DTO_PhieuMuon pm) {
        super(SwingUtilities.getWindowAncestor(parent), ModalityType.APPLICATION_MODAL);
        this.parentGUI = (GUI_QuanLyMuonTra) parent;
        this.phieuSua = pm;
        initUI();
        loadDataDocGia();
        loadDataKhoSach();
        
        // Fill dữ liệu cũ
        txtMaPhieu.setText(pm.getMaPhieuMuon());
        txtMaDocGia.setText(pm.getMaDocGia());
        txtThuThu.setText(pm.getMaThuThu());
        txtNgayMuon.setText(sdf.format(pm.getNgayMuon()));
        txtHanTra.setText(sdf.format(pm.getNgayHenTra()));
        txtTienPhat.setText(String.valueOf((long)pm.getTienPhat()));
        cboTinhTrang.setSelectedItem(pm.getTinhTrang());
        
        setTitle("CẬP NHẬT PHIẾU MƯỢN: " + pm.getMaPhieuMuon());
    }

    private void initUI() {
        setTitle("TẠO PHIẾU MƯỢN MỚI");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // HEADER
        JLabel lblTitle = new JLabel(phieuSua == null ? "TẠO PHIẾU MƯỢN MỚI" : "CẬP NHẬT PHIẾU MƯỢN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(50, 115, 220));
        lblTitle.setPreferredSize(new Dimension(0, 50));
        add(lblTitle, BorderLayout.NORTH);

        // INFO PANEL
        JPanel pnlInfo = new JPanel(new GridLayout(3, 6, 10, 10));
        pnlInfo.setBorder(BorderFactory.createTitledBorder("Thông tin phiếu"));
        pnlInfo.setPreferredSize(new Dimension(0, 150));

        txtMaPhieu = new JTextField(); txtMaPhieu.setEditable(false);
        txtMaDocGia = new JTextField(); txtMaDocGia.setEditable(false);
        txtTenDocGia = new JTextField(); txtTenDocGia.setEditable(false);
        txtThuThu = new JTextField(); txtThuThu.setEditable(false);
        
        // Ngày mượn là hôm nay, Hạn trả là +7 ngày
        Date now = new Date();
        txtNgayMuon = new JTextField(sdf.format(now));
        txtHanTra = new JTextField(sdf.format(new Date(now.getTime() + (7L * 24 * 60 * 60 * 1000)))); 
        
        txtTienPhat = new JTextField("0");
        cboTinhTrang = new JComboBox<>(new String[]{"Đang mượn", "Đã trả", "Quá hạn"});

        pnlInfo.add(new JLabel("Mã Phiếu:")); pnlInfo.add(txtMaPhieu);
        pnlInfo.add(new JLabel("Mã Độc Giả:")); pnlInfo.add(txtMaDocGia);
        pnlInfo.add(new JLabel("Thủ Thư:")); pnlInfo.add(txtThuThu);
        
        pnlInfo.add(new JLabel("Tên Độc Giả:")); pnlInfo.add(txtTenDocGia);
        pnlInfo.add(new JLabel("Ngày Mượn:")); pnlInfo.add(txtNgayMuon);
        pnlInfo.add(new JLabel("Hạn Trả:")); pnlInfo.add(txtHanTra);

        pnlInfo.add(new JLabel("Tình Trạng:")); pnlInfo.add(cboTinhTrang);
        pnlInfo.add(new JLabel("Tiền Phạt (VNĐ):")); pnlInfo.add(txtTienPhat);
        pnlInfo.add(new JLabel("")); pnlInfo.add(new JLabel(""));

        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.add(lblTitle, BorderLayout.NORTH);
        pnlTop.add(pnlInfo, BorderLayout.CENTER);
        add(pnlTop, BorderLayout.NORTH);

        // CENTER TABLES
        JPanel pnlCenter = new JPanel(new GridLayout(1, 3, 10, 10));
        pnlCenter.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 1. Chọn Độc Giả
        JPanel pnlDG = new JPanel(new BorderLayout());
        pnlDG.setBorder(BorderFactory.createTitledBorder("1. Chọn Độc Giả"));
        String[] colDG = {"Mã ĐG", "Tên Độc Giả", "SĐT"};
        modelDocGia = new DefaultTableModel(colDG, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        tblDocGia = new JTable(modelDocGia);
        pnlDG.add(new JScrollPane(tblDocGia), BorderLayout.CENTER);

        // 2. Kho Sách
        JPanel pnlSach = new JPanel(new BorderLayout());
        pnlSach.setBorder(BorderFactory.createTitledBorder("2. Kho Sách (Nhấn đúp để chọn)"));
        String[] colSach = {"Mã Sách", "Tên Sách", "Tồn"};
        modelKhoSach = new DefaultTableModel(colSach, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        tblKhoSach = new JTable(modelKhoSach);
        pnlSach.add(new JScrollPane(tblKhoSach), BorderLayout.CENTER);

        // 3. Sách Mượn
        JPanel pnlMuon = new JPanel(new BorderLayout());
        pnlMuon.setBorder(BorderFactory.createTitledBorder("3. Sách Mượn"));
        String[] colMuon = {"Mã Sách", "Tên Sách"};
        modelSachMuon = new DefaultTableModel(colMuon, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        tblSachMuon = new JTable(modelSachMuon);
        
        JButton btnXoaSach = new JButton("Xóa khỏi danh sách");
        btnXoaSach.setBackground(new Color(220, 53, 69));
        btnXoaSach.setForeground(Color.WHITE);
        pnlMuon.add(new JScrollPane(tblSachMuon), BorderLayout.CENTER);
        pnlMuon.add(btnXoaSach, BorderLayout.SOUTH);

        pnlCenter.add(pnlDG); pnlCenter.add(pnlSach); pnlCenter.add(pnlMuon);
        add(pnlCenter, BorderLayout.CENTER);

        // BOTTOM BUTTONS
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnLuu = new JButton(phieuSua == null ? "Lưu Mới" : "Cập Nhật");
        btnLuu.setPreferredSize(new Dimension(150, 40));
        btnLuu.setBackground(new Color(50, 115, 220));
        btnLuu.setForeground(Color.WHITE);
        
        JButton btnThoat = new JButton("Thoát");
        btnThoat.setPreferredSize(new Dimension(150, 40));
        btnThoat.setBackground(Color.WHITE);

        pnlBot.add(btnLuu); pnlBot.add(btnThoat);
        add(pnlBot, BorderLayout.SOUTH);

        // --- EVENTS ---
        
        // 1. Chọn Độc Giả
        tblDocGia.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tblDocGia.getSelectedRow();
                if(row >= 0) {
                    txtMaDocGia.setText(tblDocGia.getValueAt(row, 0).toString());
                    txtTenDocGia.setText(tblDocGia.getValueAt(row, 1).toString());
                }
            }
        });

        // 2. Chọn Sách (Double Click)
        tblKhoSach.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { 
                    int row = tblKhoSach.getSelectedRow();
                    if (row < 0) return;
                    String ma = tblKhoSach.getValueAt(row, 0).toString();
                    String ten = tblKhoSach.getValueAt(row, 1).toString();
                    int ton = Integer.parseInt(tblKhoSach.getValueAt(row, 2).toString());

                    if (ton <= 0) { JOptionPane.showMessageDialog(null, "Sách này đã hết hàng!"); return; }
                    
                    // Kiểm tra trùng
                    for (int i = 0; i < modelSachMuon.getRowCount(); i++) {
                        if (modelSachMuon.getValueAt(i, 0).toString().equals(ma)) {
                            JOptionPane.showMessageDialog(null, "Bạn đã chọn sách này rồi!"); return;
                        }
                    }
                    // Giới hạn 5 cuốn (hoặc tùy ý)
                    if(modelSachMuon.getRowCount() >= 5) {
                        JOptionPane.showMessageDialog(null, "Chỉ được mượn tối đa 5 cuốn!"); return;
                    }
                    modelSachMuon.addRow(new Object[]{ma, ten});
                }
            }
        });

        // 3. Xóa Sách Mượn
        btnXoaSach.addActionListener(e -> {
            int row = tblSachMuon.getSelectedRow();
            if (row >= 0) modelSachMuon.removeRow(row);
            else JOptionPane.showMessageDialog(this, "Vui lòng chọn sách để xóa!");
        });

        btnThoat.addActionListener(e -> dispose());
        
        // 4. LƯU PHIẾU (QUAN TRỌNG: GỌI DAL THỰC SỰ)
        btnLuu.addActionListener(e -> {
            // Validation
            if(txtMaDocGia.getText().isEmpty()) { JOptionPane.showMessageDialog(this, "Chưa chọn độc giả!"); return; }
            if(modelSachMuon.getRowCount() == 0) { JOptionPane.showMessageDialog(this, "Chưa chọn sách nào để mượn!"); return; }
            
            // Prepare DTO
            DTO_PhieuMuon pm = new DTO_PhieuMuon();
            pm.setMaPhieuMuon(txtMaPhieu.getText());
            pm.setMaDocGia(txtMaDocGia.getText());
            pm.setMaThuThu(txtThuThu.getText());
            
            try {
                java.util.Date dMuon = sdf.parse(txtNgayMuon.getText());
                java.util.Date dHan = sdf.parse(txtHanTra.getText());
                pm.setNgayMuon(new java.sql.Date(dMuon.getTime()));
                pm.setNgayHenTra(new java.sql.Date(dHan.getTime()));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ngày tháng không hợp lệ (dd/MM/yyyy)!"); return;
            }
            
            // Prepare List Sách
            ArrayList<String> listMaSach = new ArrayList<>();
            for(int i=0; i<modelSachMuon.getRowCount(); i++) {
                listMaSach.add(modelSachMuon.getValueAt(i, 0).toString());
            }

            // GỌI HÀM TRANSACTION CỦA DAL
            if(phieuSua == null) {
                // Thêm Mới
                if(dalPhieu.themPhieuMuon(pm, listMaSach)) {
                    JOptionPane.showMessageDialog(this, "Tạo phiếu mượn thành công!");
                    parentGUI.loadData();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Lưu thất bại! Có lỗi xảy ra.");
                }
            } else {
                // Cập Nhật (Logic này phức tạp hơn, tạm thời chỉ hỗ trợ sửa thông tin chung)
                // Hoặc bạn có thể implement hàm updatePhieuMuon trong DAL tương tự
                JOptionPane.showMessageDialog(this, "Chức năng cập nhật chi tiết đang phát triển!");
            }
        });
    }

    private void loadDataDocGia() {
        modelDocGia.setRowCount(0);
        try {
            ArrayList<DTO_DocGia> list = dalDocGia.getList(); 
            for(DTO_DocGia dg : list) modelDocGia.addRow(new Object[]{dg.getMaDocGia(), dg.getTenDocGia(), dg.getSdt()});
        } catch (Exception e) {}
    }

    private void loadDataKhoSach() {
        modelKhoSach.setRowCount(0);
        try {
            ArrayList<DTO_Sach> list = dalSach.getList();
            for(DTO_Sach s : list) modelKhoSach.addRow(new Object[]{s.getMaCuonSach(), s.getTenSach(), s.getSoLuong()});
        } catch(Exception e) {}
    }
    
    private void autoGenMaPhieu() {
        if(phieuSua == null) {
            String timeStamp = new SimpleDateFormat("yyMMddHHmm").format(new Date());
            txtMaPhieu.setText("PM" + timeStamp);
        }
    }
}