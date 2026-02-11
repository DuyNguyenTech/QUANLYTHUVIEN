package MUONTRA;

import com.formdev.flatlaf.FlatClientProperties;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import DOCGIA.DAL_DocGia;
import DOCGIA.DTO_DocGia;
import SACH.DAL_Sach;
import SACH.DTO_Sach;

public class GUI_DialogChiTietPhieuMuon extends JDialog {

    private DTO_PhieuMuon phieuXem; 

    private JTextField txtMaPhieu, txtMaDocGia, txtTenDocGia, txtMaThuThu;
    private JTextField txtNgayMuon, txtNgayHenTra, txtNgayTra; 
    private JTextField txtTinhTrang, txtTienPhat;
    
    private JTable tblSachMuon;
    private DefaultTableModel modelSachMuon;
    
    private DAL_DocGia dalDocGia = new DAL_DocGia();
    private DAL_Sach dalSach = new DAL_Sach();
    private DAL_PhieuMuon dalPhieuMuon = new DAL_PhieuMuon();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    
    private Color mainColor = new Color(50, 115, 220);
    private Color bgColor = new Color(245, 248, 253);

    public GUI_DialogChiTietPhieuMuon(Component parent, DTO_PhieuMuon pm) {
        this.phieuXem = pm;
        initUI();
        fillData();
    }

    private void initUI() {
        setTitle("CHI TIẾT PHIẾU MƯỢN");
        setSize(1100, 700); // Tăng kích thước tổng thể để các ô không bị chèn ép
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(new BorderLayout());
        getContentPane().setBackground(bgColor);

        // --- 1. HEADER ---
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlHeader.setBackground(mainColor);
        pnlHeader.setBorder(new EmptyBorder(15, 0, 15, 0));
        
        JLabel lblHeader = new JLabel("THÔNG TIN CHI TIẾT PHIẾU MƯỢN");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblHeader.setForeground(Color.WHITE);
        pnlHeader.add(lblHeader);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. CENTER CONTENT ---
        JPanel pnlContent = new JPanel(new BorderLayout(25, 25));
        pnlContent.setBorder(new EmptyBorder(20, 25, 20, 25));
        pnlContent.setBackground(bgColor);

        // A. Panel Thông Tin Chung (Bên trái) - [CỐ ĐỊNH KÍCH THƯỚC Ô HIỂN THỊ]
        JPanel pnlInfoWrapper = new JPanel(new BorderLayout(0, 15));
        pnlInfoWrapper.setBackground(Color.WHITE);
        pnlInfoWrapper.putClientProperty(FlatClientProperties.STYLE, "arc: 20; border: 1,1,1,1, #E0E0E0");
        pnlInfoWrapper.setBorder(new EmptyBorder(20, 20, 20, 20));
        pnlInfoWrapper.setPreferredSize(new Dimension(450, 0)); // Rộng hơn một chút

        JLabel lblTitleInfo = new JLabel("Thông Tin Phiếu", SwingConstants.LEFT);
        lblTitleInfo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitleInfo.setForeground(mainColor);
        pnlInfoWrapper.add(lblTitleInfo, BorderLayout.NORTH);

        JPanel pnlInfo = new JPanel(new GridBagLayout());
        pnlInfo.setBackground(Color.WHITE);

        txtMaPhieu = createReadOnlyField();
        txtMaDocGia = createReadOnlyField();
        txtTenDocGia = createReadOnlyField();
        txtMaThuThu = createReadOnlyField();
        txtNgayMuon = createReadOnlyField();
        txtNgayHenTra = createReadOnlyField();
        txtNgayTra = createReadOnlyField();
        txtTinhTrang = createReadOnlyField();
        txtTienPhat = createReadOnlyField();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10); // Tăng dãn cách dòng
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
        
        gbc.gridy = row; gbc.weighty = 1.0;
        pnlInfo.add(new JLabel(), gbc);
        pnlInfoWrapper.add(pnlInfo, BorderLayout.CENTER);

        pnlContent.add(pnlInfoWrapper, BorderLayout.WEST);

        // B. Panel Danh Sách Sách (Bên phải)
        JPanel pnlTableWrapper = new JPanel(new BorderLayout(0, 15));
        pnlTableWrapper.setBackground(Color.WHITE);
        pnlTableWrapper.putClientProperty(FlatClientProperties.STYLE, "arc: 20; border: 1,1,1,1, #E0E0E0");
        pnlTableWrapper.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitleTable = new JLabel("Danh Sách Sách Đã Mượn", SwingConstants.LEFT);
        lblTitleTable.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitleTable.setForeground(mainColor);
        pnlTableWrapper.add(lblTitleTable, BorderLayout.NORTH);

        modelSachMuon = new DefaultTableModel(new String[]{"Mã Sách", "Tên Sách", "Tình Trạng"}, 0);
        tblSachMuon = new JTable(modelSachMuon) {
            public boolean isCellEditable(int row, int col) { return false; } 
        };
        
        tblSachMuon.setRowHeight(40);
        tblSachMuon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblSachMuon.setShowVerticalLines(false);
        tblSachMuon.setSelectionBackground(new Color(232, 242, 252));
        
        JTableHeader header = tblSachMuon.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(248, 249, 250));
        header.setPreferredSize(new Dimension(0, 45));
        
        tblSachMuon.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                setBorder(new EmptyBorder(0, 5, 0, 5));
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        });
        
        tblSachMuon.getColumnModel().getColumn(1).setPreferredWidth(250);

        JScrollPane sc = new JScrollPane(tblSachMuon);
        sc.setBorder(BorderFactory.createEmptyBorder());
        sc.getViewport().setBackground(Color.WHITE);
        pnlTableWrapper.add(sc, BorderLayout.CENTER);
        
        pnlContent.add(pnlTableWrapper, BorderLayout.CENTER);
        add(pnlContent, BorderLayout.CENTER);

        // --- 3. BOTTOM ---
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        pnlBot.setBackground(bgColor);
        
        JButton btnThoat = new JButton("Đóng");
        btnThoat.setPreferredSize(new Dimension(150, 45));
        btnThoat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThoat.setBackground(new Color(220, 53, 69)); 
        btnThoat.setForeground(Color.WHITE);
        btnThoat.setFocusPainted(false);
        btnThoat.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnThoat.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0");

        btnThoat.addActionListener(e -> dispose());
        pnlBot.add(btnThoat);
        add(pnlBot, BorderLayout.SOUTH);
    }

    private void fillData() {
        if(phieuXem == null) return;

        txtMaPhieu.setText(phieuXem.getMaPhieuMuon());
        txtMaDocGia.setText(phieuXem.getMaDocGia());
        txtMaThuThu.setText(phieuXem.getMaThuThu());
        
        ArrayList<DTO_DocGia> listDG = dalDocGia.getList();
        for(DTO_DocGia dg : listDG) {
            if(dg.getMaDocGia().equals(phieuXem.getMaDocGia())) {
                txtTenDocGia.setText(dg.getTenDocGia());
                break;
            }
        }

        txtNgayMuon.setText(sdf.format(phieuXem.getNgayMuon()));
        txtNgayHenTra.setText(sdf.format(phieuXem.getNgayHenTra()));
        txtNgayTra.setText(phieuXem.getNgayTra() != null ? sdf.format(phieuXem.getNgayTra()) : "Chưa trả");

        txtTinhTrang.setText(phieuXem.getTinhTrang());
        if(phieuXem.getTinhTrang().contains("Quá hạn")) txtTinhTrang.setForeground(new Color(220, 53, 69));
        else if(phieuXem.getTinhTrang().contains("Đã trả")) txtTinhTrang.setForeground(new Color(40, 167, 69));
        else txtTinhTrang.setForeground(new Color(255, 152, 0));

        txtTienPhat.setText(new DecimalFormat("#,###").format(phieuXem.getTienPhat()) + " VNĐ");
        if(phieuXem.getTienPhat() > 0) txtTienPhat.setForeground(new Color(180, 0, 0));

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

    private void addPair(JPanel p, GridBagConstraints gbc, int row, String lblText, JTextField txt) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        JLabel l = new JLabel(lblText);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(new Color(80, 80, 80));
        l.setPreferredSize(new Dimension(120, 42)); // Tăng nhãn để khớp chiều cao ô dữ liệu
        p.add(l, gbc);
        
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        p.add(txt, gbc);
    }

    private JTextField createReadOnlyField() {
        JTextField t = new JTextField();
        t.setEditable(false);
        t.setBackground(new Color(245, 245, 245)); 
        t.setFont(new Font("Segoe UI", Font.BOLD, 15)); // Chữ to và rõ hơn
        t.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0");
        t.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15)); // Padding trái phải
        t.setPreferredSize(new Dimension(0, 42)); // [FIX] Ép cứng chiều cao 42px để hiển thị đầy đủ
        return t;
    }
}