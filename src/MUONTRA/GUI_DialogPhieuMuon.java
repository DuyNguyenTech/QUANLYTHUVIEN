package MUONTRA;

import com.formdev.flatlaf.FlatClientProperties;
import DOCGIA.DAL_DocGia;
import DOCGIA.DTO_DocGia;
import SACH.DAL_Sach;
import SACH.DTO_Sach;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
    
    private Color mainColor = new Color(50, 115, 220); 
    private Color bgColor = new Color(245, 248, 253);

    public GUI_DialogPhieuMuon(Window parent, String maThuThuDangNhap) {
        super(parent, ModalityType.APPLICATION_MODAL);
        this.currentMaThuThu = maThuThuDangNhap;
        this.isUpdate = false;
        initUI();
        
        txtMaPhieu.setText("PM" + System.currentTimeMillis() / 1000);
        txtNgayMuon.setText(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        long sevenDays = 7L * 24 * 60 * 60 * 1000;
        txtHanTra.setText(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(System.currentTimeMillis() + sevenDays)));
        txtThuThu.setText(currentMaThuThu);
    }

    public GUI_DialogPhieuMuon(Window parent, DTO_PhieuMuon pm, String maThuThuDangNhap) {
        super(parent, ModalityType.APPLICATION_MODAL);
        this.currentMaThuThu = maThuThuDangNhap;
        this.isUpdate = true;
        initUI();
        
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
        listMuon = dalPM.getSachCuaPhieu(pm.getMaPhieuMuon());
        updateTableMuon();
    }

    private void initUI() {
        setTitle(isUpdate ? "CẬP NHẬT PHIẾU MƯỢN" : "TẠO PHIẾU MƯỢN MỚI");
        // [ĐÃ SỬA] Thu gọn chiều cao form xuống 650px để không bị mất nút tác vụ
        setSize(1280, 650); 
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false);
        getContentPane().setBackground(bgColor);

        // --- 1. HEADER ---
        Color headerColor = isUpdate ? new Color(255, 152, 0) : mainColor;
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlHeader.setBackground(headerColor);
        pnlHeader.setBorder(new EmptyBorder(10, 0, 10, 0));
        JLabel lblHeader = new JLabel(isUpdate ? "CẬP NHẬT THÔNG TIN PHIẾU MƯỢN" : "LẬP PHIẾU MƯỢN SÁCH MỚI");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeader.setForeground(Color.WHITE);
        pnlHeader.add(lblHeader);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. CENTER ---
        JPanel pnlCenter = new JPanel(new BorderLayout(10, 10));
        pnlCenter.setBackground(bgColor);
        pnlCenter.setBorder(new EmptyBorder(10, 15, 5, 15));

        // A. PANEL INPUT (Thông tin phiếu - Thu nhỏ insets)
        JPanel pnlInputCard = new JPanel(new BorderLayout(0, 5));
        pnlInputCard.setBackground(Color.WHITE);
        pnlInputCard.putClientProperty(FlatClientProperties.STYLE, "arc: 20; border: 1,1,1,1, #E0E0E0");
        pnlInputCard.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel lblTitleInput = new JLabel("Thông Tin Phiếu Mượn", SwingConstants.LEFT);
        lblTitleInput.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitleInput.setForeground(headerColor);
        pnlInputCard.add(lblTitleInput, BorderLayout.NORTH);

        JPanel pnlInputContent = new JPanel(new GridBagLayout());
        pnlInputContent.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8); // Thu nhỏ khoảng cách dòng
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMaPhieu = createTextField(); txtMaPhieu.setEditable(false); txtMaPhieu.setBackground(new Color(245, 245, 245));
        txtMaDG = createTextField();
        txtTenDG = createTextField(); txtTenDG.setEditable(false); txtTenDG.setBackground(new Color(245, 245, 245));
        txtThuThu = createTextField(); txtThuThu.setEditable(false); txtThuThu.setBackground(new Color(245, 245, 245));
        txtNgayMuon = createTextField();
        txtHanTra = createTextField();
        txtTienPhat = createTextField(); txtTienPhat.setText("0");
        cboTinhTrang = new JComboBox<>(new String[]{"Đang mượn", "Đã trả", "Quá hạn", "Chờ duyệt"});
        cboTinhTrang.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboTinhTrang.putClientProperty(FlatClientProperties.STYLE, "arc: 10");

        gbc.gridy=0; gbc.gridx=0; pnlInputContent.add(createLabel("Mã Phiếu:"), gbc);
        gbc.gridx=1; gbc.weightx=1.0; pnlInputContent.add(txtMaPhieu, gbc);
        gbc.gridx=2; gbc.weightx=0.0; pnlInputContent.add(createLabel("Thủ Thư:"), gbc);
        gbc.gridx=3; gbc.weightx=1.0; pnlInputContent.add(txtThuThu, gbc);

        gbc.gridy=1; gbc.gridx=0; gbc.weightx=0.0; pnlInputContent.add(createLabel("Mã Độc Giả:"), gbc);
        gbc.gridx=1; gbc.weightx=1.0; pnlInputContent.add(txtMaDG, gbc);
        gbc.gridx=2; gbc.weightx=0.0; pnlInputContent.add(createLabel("Tên Độc Giả:"), gbc);
        gbc.gridx=3; gbc.weightx=1.0; pnlInputContent.add(txtTenDG, gbc);

        gbc.gridy=2; gbc.gridx=0; gbc.weightx=0.0; pnlInputContent.add(createLabel("Ngày Mượn:"), gbc);
        gbc.gridx=1; gbc.weightx=1.0; pnlInputContent.add(txtNgayMuon, gbc);
        gbc.gridx=2; gbc.weightx=0.0; pnlInputContent.add(createLabel("Hạn Trả:"), gbc);
        gbc.gridx=3; gbc.weightx=1.0; pnlInputContent.add(txtHanTra, gbc);

        gbc.gridy=3; gbc.gridx=0; gbc.weightx=0.0; pnlInputContent.add(createLabel("Tình Trạng:"), gbc);
        gbc.gridx=1; gbc.weightx=1.0; pnlInputContent.add(cboTinhTrang, gbc);
        gbc.gridx=2; gbc.weightx=0.0; pnlInputContent.add(createLabel("Phí Phạt:"), gbc);
        gbc.gridx=3; gbc.weightx=1.0; pnlInputContent.add(txtTienPhat, gbc);

        pnlInputCard.add(pnlInputContent, BorderLayout.CENTER);
        pnlCenter.add(pnlInputCard, BorderLayout.NORTH);

        // B. PANEL 3 BẢNG (Thu nhỏ khoảng cách và bảng)
        JPanel pnlTables = new JPanel(new GridLayout(1, 3, 12, 0));
        pnlTables.setOpaque(false);

        modelDocGia = new DefaultTableModel(new String[]{"Mã ĐG", "Tên Độc Giả", "SĐT"}, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        tblDocGia = createStyledTable(modelDocGia);
        pnlTables.add(createTableCard("1. Chọn Độc Giả", tblDocGia, null, headerColor));

        modelKho = new DefaultTableModel(new String[]{"Mã Sách", "Tên Sách", "Tồn"}, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        tblKhoSach = createStyledTable(modelKho);
        pnlTables.add(createTableCard("2. Kho Sách", tblKhoSach, null, headerColor));

        modelMuon = new DefaultTableModel(new String[]{"Mã Sách", "Tên Sách"}, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        tblSachMuon = createStyledTable(modelMuon);
        JButton btnXoaSach = new JButton("Xóa Khỏi Danh Sách");
        btnXoaSach.setBackground(new Color(220, 53, 69)); btnXoaSach.setForeground(Color.WHITE);
        btnXoaSach.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnXoaSach.putClientProperty(FlatClientProperties.STYLE, "arc: 8; borderWidth: 0");
        pnlTables.add(createTableCard("3. Sách Đã Chọn", tblSachMuon, btnXoaSach, headerColor));

        pnlCenter.add(pnlTables, BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        // --- 3. BOTTOM (NÚT TÁC VỤ) ---
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        pnlBot.setBackground(bgColor);
        pnlBot.setBorder(new EmptyBorder(0, 0, 10, 15));
        
        JButton btnSave = createButton(isUpdate ? "Cập Nhật Phiếu" : "Lưu Phiếu Mượn", new Color(40, 167, 69));
        JButton btnCancel = createButton("Đóng Lại", new Color(108, 117, 125));
        
        pnlBot.add(btnSave); pnlBot.add(btnCancel);
        add(pnlBot, BorderLayout.SOUTH);

        // --- EVENTS ---
        loadDataDocGia();
        loadDataKhoSach();
        tblDocGia.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tblDocGia.getSelectedRow();
                if(row >= 0) {
                    txtMaDG.setText(tblDocGia.getValueAt(row, 0).toString());
                    txtTenDG.setText(tblDocGia.getValueAt(row, 1).toString());
                }
            }
        });
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
        btnXoaSach.addActionListener(e -> {
            int row = tblSachMuon.getSelectedRow();
            if(row >= 0) { listMuon.remove(row); updateTableMuon(); }
        });
        btnSave.addActionListener(e -> xuLyLuu());
        btnCancel.addActionListener(e -> dispose());
    }

    private JPanel createTableCard(String title, JTable table, JButton btn, Color titleColor) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setBackground(Color.WHITE);
        p.putClientProperty(FlatClientProperties.STYLE, "arc: 15; border: 1,1,1,1, #E0E0E0");
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(titleColor);
        p.add(lbl, BorderLayout.NORTH);
        
        JScrollPane sc = new JScrollPane(table);
        sc.setBorder(BorderFactory.createEmptyBorder());
        sc.getViewport().setBackground(Color.WHITE);
        p.add(sc, BorderLayout.CENTER);
        
        if(btn != null) {
            btn.setPreferredSize(new Dimension(0, 32));
            p.add(btn, BorderLayout.SOUTH);
        }
        return p;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        // [ĐÃ SỬA] Thu nhỏ chiều cao dòng xuống 30px để tiết kiệm diện tích
        t.setRowHeight(30); 
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t.setShowVerticalLines(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setSelectionBackground(new Color(232, 242, 252));
        
        JTableHeader h = t.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 12));
        h.setBackground(new Color(248, 249, 250));
        h.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                setBorder(new EmptyBorder(0, 5, 0, 5));
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        });
        return t;
    }

    private JLabel createLabel(String t) {
        JLabel l = new JLabel(t); 
        l.setFont(new Font("Segoe UI", Font.BOLD, 12)); 
        l.setForeground(new Color(80, 80, 80));
        return l;
    }
    
    private JTextField createTextField() {
        JTextField t = new JTextField(); 
        t.setPreferredSize(new Dimension(140, 32)); // Thu nhỏ ô nhập liệu
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.putClientProperty(FlatClientProperties.STYLE, "arc: 8; borderColor: #cccccc; focusedBorderColor: #1877F2; borderWidth: 1");
        return t;
    }

    private JButton createButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setPreferredSize(new Dimension(170, 42)); // Thu gọn nút bấm
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false); b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0");
        return b;
    }

    private void loadDataDocGia() {
        modelDocGia.setRowCount(0);
        ArrayList<DTO_DocGia> list = dalDG.getList();
        for(DTO_DocGia d : list) modelDocGia.addRow(new Object[]{d.getMaDocGia(), d.getTenDocGia(), d.getSoDienThoai()});
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
        if(txtMaDG.getText().isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng chọn độc giả từ bảng!"); return; }
        if(listMuon.isEmpty()) { JOptionPane.showMessageDialog(this, "Danh sách sách mượn đang trống!"); return; }
        
        DTO_PhieuMuon pm = new DTO_PhieuMuon();
        pm.setMaPhieuMuon(txtMaPhieu.getText());
        pm.setMaDocGia(txtMaDG.getText());
        pm.setMaThuThu(txtThuThu.getText()); 
        try {
            pm.setNgayMuon(Date.valueOf(txtNgayMuon.getText()));
            pm.setNgayHenTra(Date.valueOf(txtHanTra.getText()));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Định dạng ngày phải là YYYY-MM-DD!"); return;
        }
        pm.setTinhTrang(cboTinhTrang.getSelectedItem().toString());
        try { pm.setTienPhat(Double.parseDouble(txtTienPhat.getText())); } catch(Exception e) { pm.setTienPhat(0); }

        ArrayList<String> dsMaSach = new ArrayList<>();
        for(DTO_Sach s : listMuon) dsMaSach.add(s.getMaCuonSach());

        boolean kq = isUpdate ? dalPM.suaPhieuMuon(pm) : dalPM.themPhieuMuon(pm, dsMaSach);

        if(kq) {
            JOptionPane.showMessageDialog(this, "Đã lưu phiếu mượn thành công!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu phiếu! Vui lòng kiểm tra lại dữ liệu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}