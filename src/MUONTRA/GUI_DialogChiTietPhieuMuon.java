package MUONTRA;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import DOCGIA.DAL_DocGia;
import DOCGIA.DTO_DocGia;
import SACH.DAL_Sach;
import SACH.DTO_Sach;

public class GUI_DialogChiTietPhieuMuon extends JDialog {

    private DTO_PhieuMuon phieuXem; // Dữ liệu phiếu cần xem

    // Components
    private JTextField txtMaPhieu, txtMaDocGia, txtTenDocGia, txtMaThuThu;
    private JTextField txtNgayMuon, txtNgayHenTra, txtNgayTra; // Dùng TextField thay vì JSpinner để hiển thị text
    private JTextField txtTinhTrang, txtTienPhat;
    
    private JTable tblSachMuon;
    private DefaultTableModel modelSachMuon;
    
    private DAL_DocGia dalDocGia = new DAL_DocGia();
    private DAL_Sach dalSach = new DAL_Sach();
    private DAL_PhieuMuon dalPhieuMuon = new DAL_PhieuMuon();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public GUI_DialogChiTietPhieuMuon(Component parent, DTO_PhieuMuon pm) {
        this.phieuXem = pm;
        initUI();
        fillData();
    }

    private void initUI() {
        setTitle("CHI TIẾT PHIẾU MƯỢN");
        setSize(900, 600); // Kích thước vừa phải
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(new BorderLayout());

        // --- 1. HEADER ---
        JLabel lblHeader = new JLabel("CHI TIẾT PHIẾU MƯỢN SÁCH", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblHeader.setOpaque(true);
        lblHeader.setBackground(new Color(23, 162, 184)); // Màu Cyan (Thông tin)
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setPreferredSize(new Dimension(0, 60));
        add(lblHeader, BorderLayout.NORTH);

        // --- 2. CENTER CONTENT ---
        JPanel pnlContent = new JPanel(new BorderLayout(15, 15));
        pnlContent.setBorder(new EmptyBorder(15, 15, 15, 15));

        // A. Panel Thông Tin Chung (Bên trái)
        JPanel pnlInfo = new JPanel(new GridLayout(9, 2, 10, 15)); // 9 dòng thông tin
        pnlInfo.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "Thông tin chung", 
            TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
            new Font("Segoe UI", Font.BOLD, 14), new Color(23, 162, 184)));
        pnlInfo.setPreferredSize(new Dimension(350, 0));

        // Khởi tạo các ô text (Read-only)
        txtMaPhieu = createReadOnlyField();
        txtMaDocGia = createReadOnlyField();
        txtTenDocGia = createReadOnlyField();
        txtMaThuThu = createReadOnlyField();
        txtNgayMuon = createReadOnlyField();
        txtNgayHenTra = createReadOnlyField();
        txtNgayTra = createReadOnlyField();
        txtTinhTrang = createReadOnlyField();
        txtTienPhat = createReadOnlyField();
        txtTienPhat.setForeground(Color.RED); 
        txtTienPhat.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Add vào panel
        addPair(pnlInfo, "Mã Phiếu:", txtMaPhieu);
        addPair(pnlInfo, "Mã Độc Giả:", txtMaDocGia);
        addPair(pnlInfo, "Tên Độc Giả:", txtTenDocGia);
        addPair(pnlInfo, "Thủ Thư Lập:", txtMaThuThu);
        addPair(pnlInfo, "Ngày Mượn:", txtNgayMuon);
        addPair(pnlInfo, "Ngày Hẹn Trả:", txtNgayHenTra);
        addPair(pnlInfo, "Ngày Thực Trả:", txtNgayTra);
        addPair(pnlInfo, "Tình Trạng:", txtTinhTrang);
        addPair(pnlInfo, "Tiền Phạt:", txtTienPhat);

        pnlContent.add(pnlInfo, BorderLayout.WEST);

        // B. Panel Danh Sách Sách (Bên phải)
        JPanel pnlTable = new JPanel(new BorderLayout());
        pnlTable.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "Danh sách sách đã mượn", 
            TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
            new Font("Segoe UI", Font.BOLD, 14), new Color(23, 162, 184)));

        modelSachMuon = new DefaultTableModel(new String[]{"Mã Sách", "Tên Sách", "Tình Trạng"}, 0);
        tblSachMuon = new JTable(modelSachMuon) {
            public boolean isCellEditable(int row, int col) { return false; } // Không cho sửa bảng
        };
        tblSachMuon.setRowHeight(30);
        tblSachMuon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblSachMuon.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        pnlTable.add(new JScrollPane(tblSachMuon), BorderLayout.CENTER);
        pnlContent.add(pnlTable, BorderLayout.CENTER);

        add(pnlContent, BorderLayout.CENTER);

        // --- 3. BUTTONS (SOUTH) ---
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        
        JButton btnThoat = new JButton("Đóng");
        btnThoat.setPreferredSize(new Dimension(120, 40));
        btnThoat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThoat.setBackground(new Color(108, 117, 125)); // Màu xám
        btnThoat.setForeground(Color.WHITE);
        btnThoat.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnThoat.addActionListener(e -> dispose());
        pnlBot.add(btnThoat);
        
        add(pnlBot, BorderLayout.SOUTH);
    }

    private void fillData() {
        if(phieuXem == null) return;

        // 1. Điền thông tin chung
        txtMaPhieu.setText(phieuXem.getMaPhieuMuon());
        txtMaDocGia.setText(phieuXem.getMaDocGia());
        txtMaThuThu.setText(phieuXem.getMaThuThu());
        
        // Tìm tên độc giả
        ArrayList<DTO_DocGia> listDG = dalDocGia.getList();
        for(DTO_DocGia dg : listDG) {
            if(dg.getMaDocGia().equals(phieuXem.getMaDocGia())) {
                txtTenDocGia.setText(dg.getTenDocGia());
                break;
            }
        }

        // Format ngày tháng
        txtNgayMuon.setText(sdf.format(phieuXem.getNgayMuon()));
        txtNgayHenTra.setText(sdf.format(phieuXem.getNgayHenTra()));
        txtNgayTra.setText(phieuXem.getNgayTra() != null ? sdf.format(phieuXem.getNgayTra()) : "Chưa trả");

        // Tình trạng & Phí phạt
        txtTinhTrang.setText(phieuXem.getTinhTrang());
        if(phieuXem.getTinhTrang().equals("Quá hạn")) txtTinhTrang.setForeground(Color.RED);
        else if(phieuXem.getTinhTrang().equals("Đã trả")) txtTinhTrang.setForeground(new Color(40, 167, 69));
        else txtTinhTrang.setForeground(new Color(0, 123, 255));

        txtTienPhat.setText(new DecimalFormat("#,###").format(phieuXem.getTienPhat()) + " VNĐ");

        // 2. Điền danh sách sách
        ArrayList<DTO_ChiTietPhieuMuon> listCT = dalPhieuMuon.getChiTietPhieu(phieuXem.getMaPhieuMuon());
        ArrayList<DTO_Sach> listAllSach = dalSach.getList();

        for(DTO_ChiTietPhieuMuon ct : listCT) {
            String tenSach = "Unknown";
            for(DTO_Sach s : listAllSach) {
                if(s.getMaCuonSach().equals(ct.getMaCuonSach())) {
                    tenSach = s.getTenSach();
                    break;
                }
            }
            // Tình trạng sách lúc mượn (thường là "Đang mượn" hoặc lấy từ DB nếu có cột riêng)
            modelSachMuon.addRow(new Object[]{ct.getMaCuonSach(), tenSach, ct.getTinhTrangSach()});
        }
    }

    // --- Helpers ---
    private void addPair(JPanel p, String lbl, JTextField txt) {
        JLabel l = new JLabel(lbl);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        p.add(l); p.add(txt);
    }

    private JTextField createReadOnlyField() {
        JTextField t = new JTextField();
        t.setEditable(false);
        t.setBackground(new Color(245, 248, 250)); // Màu nền hơi xám nhẹ
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), 
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return t;
    }
}