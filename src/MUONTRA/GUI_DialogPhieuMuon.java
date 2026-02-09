package MUONTRA;

import DOCGIA.DAL_DocGia;
import DOCGIA.DTO_DocGia;
import SACH.DAL_Sach;
import SACH.DTO_Sach;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class GUI_DialogPhieuMuon extends JDialog {

    private JTextField txtMaPhieu, txtMaDG, txtTenDG, txtNgayMuon, txtHanTra, txtTienPhat, txtThuThu;
    private JComboBox<String> cboTinhTrang;
    private JTable tblDocGia, tblKhoSach, tblSachMuon;
    private DefaultTableModel modelDocGia, modelKho, modelMuon;
    
    private ArrayList<DTO_Sach> listKho = new ArrayList<>();
    private ArrayList<DTO_Sach> listMuon = new ArrayList<>();
    
    private DAL_Sach dalSach = new DAL_Sach();
    private DAL_DocGia dalDG = new DAL_DocGia();
    private DAL_PhieuMuon dalPM = new DAL_PhieuMuon();
    
    private boolean isUpdate = false;
    private String currentMaThuThu; 
    
    // Màu chủ đạo
    private Color mainColor = new Color(50, 115, 220); 

    // Constructor 1: TẠO MỚI
    public GUI_DialogPhieuMuon(Window parent, String maThuThuDangNhap) {
        super(parent, ModalityType.APPLICATION_MODAL);
        this.currentMaThuThu = maThuThuDangNhap;
        this.isUpdate = false;
        initUI();
        
        // Auto gen data
        txtMaPhieu.setText("PM" + System.currentTimeMillis() / 1000);
        txtNgayMuon.setText(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        long sevenDays = 7L * 24 * 60 * 60 * 1000;
        txtHanTra.setText(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(System.currentTimeMillis() + sevenDays)));
        
        txtThuThu.setText(currentMaThuThu);
    }

    // Constructor 2: CẬP NHẬT
    public GUI_DialogPhieuMuon(Window parent, DTO_PhieuMuon pm, String maThuThuDangNhap) {
        super(parent, ModalityType.APPLICATION_MODAL);
        this.currentMaThuThu = maThuThuDangNhap;
        this.isUpdate = true;
        initUI();
        
        // Fill data cũ
        txtMaPhieu.setText(pm.getMaPhieuMuon());
        txtMaDG.setText(pm.getMaDocGia());
        
        DTO_DocGia dg = dalDG.getChiTiet(pm.getMaDocGia());
        if(dg != null) txtTenDG.setText(dg.getTenDocGia());
        
        txtNgayMuon.setText(pm.getNgayMuon().toString());
        txtHanTra.setText(pm.getNgayHenTra().toString());
        cboTinhTrang.setSelectedItem(pm.getTinhTrang());
        txtTienPhat.setText(String.valueOf((int)pm.getTienPhat()));
        
        if("ONLINE".equalsIgnoreCase(pm.getMaThuThu()) || "Chờ duyệt".equals(pm.getTinhTrang())) {
             txtThuThu.setText(currentMaThuThu); 
        } else {
             txtThuThu.setText(pm.getMaThuThu());
        }
        
        txtMaPhieu.setEditable(false);
        txtMaDG.setEditable(false); 

        // Load sách của phiếu này
        listMuon = dalPM.getSachCuaPhieu(pm.getMaPhieuMuon());
        updateTableMuon();
    }

    private void initUI() {
        setTitle(isUpdate ? "CẬP NHẬT PHIẾU MƯỢN" : "TẠO PHIẾU MƯỢN MỚI");
        setSize(1250, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false);

        // --- 1. HEADER ---
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlHeader.setBackground(mainColor);
        pnlHeader.setBorder(new EmptyBorder(10, 0, 10, 0));
        JLabel lblHeader = new JLabel(isUpdate ? "CẬP NHẬT THÔNG TIN PHIẾU MƯỢN" : "LẬP PHIẾU MƯỢN SÁCH MỚI");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(Color.WHITE);
        pnlHeader.add(lblHeader);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. CENTER (INPUT + TABLES) ---
        JPanel pnlCenter = new JPanel(new BorderLayout(10, 10));
        pnlCenter.setBackground(new Color(245, 248, 253));
        pnlCenter.setBorder(new EmptyBorder(10, 10, 10, 10));

        // A. PANEL INPUT (Thông tin phiếu)
        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setBackground(Color.WHITE);
        pnlInput.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), "Thông tin phiếu mượn",
            TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
            new Font("Segoe UI", Font.BOLD, 14), mainColor));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Init Components
        txtMaPhieu = createTextField(); txtMaPhieu.setEditable(false); txtMaPhieu.setBackground(new Color(245, 245, 245));
        txtMaDG = createTextField();
        txtTenDG = createTextField(); txtTenDG.setEditable(false); txtTenDG.setBackground(new Color(245, 245, 245));
        txtThuThu = createTextField(); txtThuThu.setEditable(false); txtThuThu.setBackground(new Color(245, 245, 245));
        txtNgayMuon = createTextField();
        txtHanTra = createTextField();
        txtTienPhat = createTextField(); txtTienPhat.setText("0");
        cboTinhTrang = new JComboBox<>(new String[]{"Đang mượn", "Đã trả", "Quá hạn", "Chờ duyệt"});
        cboTinhTrang.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboTinhTrang.setPreferredSize(new Dimension(150, 30));

        // Row 1
        gbc.gridx=0; gbc.gridy=0; pnlInput.add(createLabel("Mã Phiếu:"), gbc);
        gbc.gridx=1; gbc.gridy=0; gbc.weightx=1.0; pnlInput.add(txtMaPhieu, gbc);
        
        gbc.gridx=2; gbc.gridy=0; gbc.weightx=0.0; pnlInput.add(createLabel("Thủ Thư:"), gbc);
        gbc.gridx=3; gbc.gridy=0; gbc.weightx=1.0; pnlInput.add(txtThuThu, gbc);

        // Row 2
        gbc.gridx=0; gbc.gridy=1; gbc.weightx=0.0; pnlInput.add(createLabel("Mã Độc Giả:"), gbc);
        gbc.gridx=1; gbc.gridy=1; gbc.weightx=1.0; pnlInput.add(txtMaDG, gbc);
        
        gbc.gridx=2; gbc.gridy=1; gbc.weightx=0.0; pnlInput.add(createLabel("Tên Độc Giả:"), gbc);
        gbc.gridx=3; gbc.gridy=1; gbc.weightx=1.0; pnlInput.add(txtTenDG, gbc);

        // Row 3
        gbc.gridx=0; gbc.gridy=2; gbc.weightx=0.0; pnlInput.add(createLabel("Ngày Mượn:"), gbc);
        gbc.gridx=1; gbc.gridy=2; gbc.weightx=1.0; pnlInput.add(txtNgayMuon, gbc);
        
        gbc.gridx=2; gbc.gridy=2; gbc.weightx=0.0; pnlInput.add(createLabel("Hạn Trả:"), gbc);
        gbc.gridx=3; gbc.gridy=2; gbc.weightx=1.0; pnlInput.add(txtHanTra, gbc);

        // Row 4
        gbc.gridx=0; gbc.gridy=3; gbc.weightx=0.0; pnlInput.add(createLabel("Tình Trạng:"), gbc);
        gbc.gridx=1; gbc.gridy=3; gbc.weightx=1.0; pnlInput.add(cboTinhTrang, gbc);
        
        gbc.gridx=2; gbc.gridy=3; gbc.weightx=0.0; pnlInput.add(createLabel("Tiền Phạt (VNĐ):"), gbc);
        gbc.gridx=3; gbc.gridy=3; gbc.weightx=1.0; pnlInput.add(txtTienPhat, gbc);

        pnlCenter.add(pnlInput, BorderLayout.NORTH);

        // B. PANEL TABLES (3 BẢNG)
        JPanel pnlTables = new JPanel(new GridLayout(1, 3, 15, 0));
        pnlTables.setOpaque(false);

        // 1. Bảng Độc Giả
        String[] colsDG = {"Mã ĐG", "Tên Độc Giả", "SĐT"};
        modelDocGia = new DefaultTableModel(colsDG, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        tblDocGia = createStyledTable(modelDocGia);
        pnlTables.add(createTablePanel("1. Chọn Độc Giả (Click)", tblDocGia, null));

        // 2. Bảng Kho Sách
        String[] colsKho = {"Mã Sách", "Tên Sách", "Tồn"};
        modelKho = new DefaultTableModel(colsKho, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        tblKhoSach = createStyledTable(modelKho);
        pnlTables.add(createTablePanel("2. Kho Sách (Double Click)", tblKhoSach, null));

        // 3. Bảng Sách Chọn
        String[] colsMuon = {"Mã Sách", "Tên Sách"};
        modelMuon = new DefaultTableModel(colsMuon, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        tblSachMuon = createStyledTable(modelMuon);
        
        JButton btnXoaSach = new JButton("Xóa khỏi danh sách");
        btnXoaSach.setBackground(new Color(220, 53, 69)); btnXoaSach.setForeground(Color.WHITE);
        btnXoaSach.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        pnlTables.add(createTablePanel("3. Sách Đã Chọn", tblSachMuon, btnXoaSach));

        pnlCenter.add(pnlTables, BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        // --- 3. BOTTOM BUTTONS ---
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        pnlBot.setBackground(new Color(245, 248, 253));
        pnlBot.setBorder(new EmptyBorder(5, 0, 10, 0));

        JButton btnSave = createButton(isUpdate ? "Cập Nhật Phiếu" : "Lưu Phiếu Mượn", new Color(40, 167, 69));
        JButton btnCancel = createButton("Thoát", new Color(220, 53, 69));
        
        pnlBot.add(btnSave);
        pnlBot.add(btnCancel);
        add(pnlBot, BorderLayout.SOUTH);

        // --- EVENTS ---
        loadDataDocGia();
        loadDataKhoSach();

        // Event Table 1
        tblDocGia.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tblDocGia.getSelectedRow();
                if(row >= 0) {
                    txtMaDG.setText(tblDocGia.getValueAt(row, 0).toString());
                    txtTenDG.setText(tblDocGia.getValueAt(row, 1).toString());
                }
            }
        });

        // Event Table 2
        tblKhoSach.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    int row = tblKhoSach.getSelectedRow();
                    if(row >= 0) {
                        DTO_Sach s = listKho.get(row);
                        if(s.getSoLuong() <= 0) { JOptionPane.showMessageDialog(null, "Sách này đã hết!"); return; }
                        for(DTO_Sach sm : listMuon) if(sm.getMaCuonSach().equals(s.getMaCuonSach())) return;
                        listMuon.add(s);
                        updateTableMuon();
                    }
                }
            }
        });

        // Event Delete Book
        btnXoaSach.addActionListener(e -> {
            int row = tblSachMuon.getSelectedRow();
            if(row >= 0) {
                listMuon.remove(row);
                updateTableMuon();
            }
        });

        // Event Save
        btnSave.addActionListener(e -> xuLyLuu());
        btnCancel.addActionListener(e -> dispose());
    }

    // --- HELPER UI METHODS ---
    private JPanel createTablePanel(String title, JTable table, JButton btn) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), title,
            TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
            new Font("Segoe UI", Font.BOLD, 13), mainColor));
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        if(btn != null) p.add(btn, BorderLayout.SOUTH);
        return p;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setRowHeight(30);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        t.getTableHeader().setBackground(Color.WHITE);
        t.getTableHeader().setForeground(mainColor);
        t.setSelectionBackground(new Color(232, 242, 252));
        
        // Striped Rows
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                setBorder(new EmptyBorder(0, 5, 0, 5));
                return c;
            }
        });
        return t;
    }

    private JLabel createLabel(String t) {
        JLabel l = new JLabel(t); l.setFont(new Font("Segoe UI", Font.BOLD, 13)); return l;
    }
    
    private JTextField createTextField() {
        JTextField t = new JTextField(); t.setPreferredSize(new Dimension(150, 30)); 
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200,200,200)), new EmptyBorder(2,5,2,5)));
        return t;
    }

    private JButton createButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setPreferredSize(new Dimension(160, 45));
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setFocusPainted(false); b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    // --- LOGIC GIỮ NGUYÊN ---
    private void loadDataDocGia() {
        modelDocGia.setRowCount(0);
        ArrayList<DTO_DocGia> list = dalDG.getList();
        for(DTO_DocGia d : list) modelDocGia.addRow(new Object[]{d.getMaDocGia(), d.getTenDocGia(), d.getSdt()});
    }

    private void loadDataKhoSach() {
        listKho = dalSach.getList();
        modelKho.setRowCount(0);
        for(DTO_Sach s : listKho) modelKho.addRow(new Object[]{s.getMaCuonSach(), s.getTenSach(), s.getSoLuong()});
    }

    private void updateTableMuon() {
        modelMuon.setRowCount(0);
        for(DTO_Sach s : listMuon) modelMuon.addRow(new Object[]{s.getMaCuonSach(), s.getTenSach()});
    }

    private void xuLyLuu() {
        if(txtMaDG.getText().isEmpty()) { JOptionPane.showMessageDialog(this, "Chưa chọn độc giả!"); return; }
        if(listMuon.isEmpty()) { JOptionPane.showMessageDialog(this, "Chưa chọn sách nào!"); return; }
        
        DTO_PhieuMuon pm = new DTO_PhieuMuon();
        pm.setMaPhieuMuon(txtMaPhieu.getText());
        pm.setMaDocGia(txtMaDG.getText());
        pm.setMaThuThu(txtThuThu.getText()); 
        pm.setNgayMuon(Date.valueOf(txtNgayMuon.getText()));
        pm.setNgayHenTra(Date.valueOf(txtHanTra.getText()));
        pm.setTinhTrang(cboTinhTrang.getSelectedItem().toString());
        try { pm.setTienPhat(Double.parseDouble(txtTienPhat.getText())); } catch(Exception e) { pm.setTienPhat(0); }

        ArrayList<String> dsMaSach = new ArrayList<>();
        for(DTO_Sach s : listMuon) dsMaSach.add(s.getMaCuonSach());

        boolean kq;
        if(isUpdate) kq = dalPM.suaPhieuMuon(pm); 
        else kq = dalPM.themPhieuMuon(pm, dsMaSach);

        if(kq) {
            JOptionPane.showMessageDialog(this, "Thành công!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Thất bại! Kiểm tra lại thông tin.");
        }
    }
}