package MUONTRA;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import DOCGIA.DAL_DocGia;
import DOCGIA.DTO_DocGia;
import SACH.DAL_Sach;
import SACH.DTO_Sach;

public class GUI_DialogChiTietPhieuMuon extends JDialog {

    private DTO_PhieuMuon phieuXem; // Dữ liệu phiếu cần xem

    // Components
    private JTextField txtMaPhieu, txtMaDocGia, txtTenDocGia, txtMaThuThu;
    private JTextField txtNgayMuon, txtNgayHenTra, txtNgayTra; 
    private JTextField txtTinhTrang, txtTienPhat;
    
    private JTable tblSachMuon;
    private DefaultTableModel modelSachMuon;
    
    private DAL_DocGia dalDocGia = new DAL_DocGia();
    private DAL_Sach dalSach = new DAL_Sach();
    private DAL_PhieuMuon dalPhieuMuon = new DAL_PhieuMuon();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    
    // Màu chủ đạo
    private Color mainColor = new Color(50, 115, 220);

    public GUI_DialogChiTietPhieuMuon(Component parent, DTO_PhieuMuon pm) {
        this.phieuXem = pm;
        initUI();
        fillData();
    }

    private void initUI() {
        setTitle("CHI TIẾT PHIẾU MƯỢN");
        setSize(950, 650); 
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(new BorderLayout());

        // --- 1. HEADER ---
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlHeader.setBackground(mainColor);
        pnlHeader.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JLabel lblHeader = new JLabel("CHI TIẾT PHIẾU MƯỢN SÁCH");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(Color.WHITE);
        pnlHeader.add(lblHeader);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. CENTER CONTENT ---
        JPanel pnlContent = new JPanel(new BorderLayout(15, 15));
        pnlContent.setBorder(new EmptyBorder(15, 15, 15, 15));
        pnlContent.setBackground(Color.WHITE);

        // A. Panel Thông Tin Chung (Bên trái)
        JPanel pnlInfo = new JPanel(new GridBagLayout());
        pnlInfo.setBackground(Color.WHITE);
        pnlInfo.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), "Thông tin chung", 
            TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
            new Font("Segoe UI", Font.BOLD, 14), mainColor));
        pnlInfo.setPreferredSize(new Dimension(380, 0));

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
        txtTienPhat.setForeground(new Color(180, 0, 0)); 
        txtTienPhat.setFont(new Font("Segoe UI", Font.BOLD, 14));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addPair(pnlInfo, gbc, row++, "Mã Phiếu:", txtMaPhieu);
        addPair(pnlInfo, gbc, row++, "Mã Độc Giả:", txtMaDocGia);
        addPair(pnlInfo, gbc, row++, "Tên Độc Giả:", txtTenDocGia);
        addPair(pnlInfo, gbc, row++, "Thủ Thư Lập:", txtMaThuThu);
        addPair(pnlInfo, gbc, row++, "Ngày Mượn:", txtNgayMuon);
        addPair(pnlInfo, gbc, row++, "Ngày Hẹn Trả:", txtNgayHenTra);
        addPair(pnlInfo, gbc, row++, "Ngày Thực Trả:", txtNgayTra);
        addPair(pnlInfo, gbc, row++, "Tình Trạng:", txtTinhTrang);
        addPair(pnlInfo, gbc, row++, "Tiền Phạt:", txtTienPhat);
        
        // Đẩy các component lên trên
        gbc.gridy = row; gbc.weighty = 1.0;
        pnlInfo.add(new JLabel(), gbc);

        pnlContent.add(pnlInfo, BorderLayout.WEST);

        // B. Panel Danh Sách Sách (Bên phải)
        JPanel pnlTable = new JPanel(new BorderLayout());
        pnlTable.setBackground(Color.WHITE);
        pnlTable.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), "Danh sách sách đã mượn", 
            TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
            new Font("Segoe UI", Font.BOLD, 14), mainColor));

        modelSachMuon = new DefaultTableModel(new String[]{"Mã Sách", "Tên Sách", "Tình Trạng"}, 0);
        tblSachMuon = new JTable(modelSachMuon) {
            public boolean isCellEditable(int row, int col) { return false; } 
        };
        
        // Style Table
        tblSachMuon.setRowHeight(35);
        tblSachMuon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblSachMuon.setShowVerticalLines(false);
        tblSachMuon.setIntercellSpacing(new Dimension(0, 0));
        tblSachMuon.setSelectionBackground(new Color(232, 242, 252));
        tblSachMuon.setSelectionForeground(Color.BLACK);
        
        JTableHeader header = tblSachMuon.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(Color.WHITE);
        header.setForeground(mainColor);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, mainColor));
        header.setPreferredSize(new Dimension(0, 35));
        
        // Renderer
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tblSachMuon.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                setBorder(new EmptyBorder(0, 5, 0, 5));
                return c;
            }
        });
        
        tblSachMuon.getColumnModel().getColumn(1).setPreferredWidth(200);

        pnlTable.add(new JScrollPane(tblSachMuon), BorderLayout.CENTER);
        pnlContent.add(pnlTable, BorderLayout.CENTER);

        add(pnlContent, BorderLayout.CENTER);

        // --- 3. BUTTONS (SOUTH) ---
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        pnlBot.setBackground(new Color(245, 248, 253));
        
        JButton btnThoat = new JButton("Đóng");
        btnThoat.setPreferredSize(new Dimension(120, 40));
        btnThoat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThoat.setBackground(new Color(220, 53, 69)); // Đỏ
        btnThoat.setForeground(Color.WHITE);
        btnThoat.setFocusPainted(false);
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
        if(phieuXem.getTinhTrang().equals("Quá hạn")) txtTinhTrang.setForeground(new Color(220, 53, 69));
        else if(phieuXem.getTinhTrang().equals("Đã trả")) txtTinhTrang.setForeground(new Color(40, 167, 69));
        else txtTinhTrang.setForeground(new Color(255, 152, 0));

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
            modelSachMuon.addRow(new Object[]{ct.getMaCuonSach(), tenSach, ct.getTinhTrangSach()});
        }
    }

    // --- Helpers ---
    private void addPair(JPanel p, GridBagConstraints gbc, int row, String lblText, JTextField txt) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        JLabel l = new JLabel(lblText);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(new Color(80, 80, 80));
        p.add(l, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        p.add(txt, gbc);
    }

    private JTextField createReadOnlyField() {
        JTextField t = new JTextField();
        t.setEditable(false);
        t.setBackground(new Color(250, 252, 255)); 
        t.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Chữ đậm để nổi bật
        t.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)), 
            BorderFactory.createEmptyBorder(2, 0, 2, 0)));
        return t;
    }
}