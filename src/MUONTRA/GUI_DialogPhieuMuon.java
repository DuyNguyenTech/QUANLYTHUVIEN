package MUONTRA;

import DOCGIA.DAL_DocGia;
import DOCGIA.DTO_DocGia;
import SACH.DAL_Sach;
import SACH.DTO_Sach;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class GUI_DialogPhieuMuon extends JDialog {

    private JTextField txtMaPhieu, txtMaDG, txtTenDG, txtNgayMuon, txtHanTra, txtTienPhat, txtThuThu;
    private JComboBox<String> cboTinhTrang;
    private JTable tblDocGia, tblKhoSach, tblSachMuon; // 3 Bảng như cũ
    private DefaultTableModel modelDocGia, modelKho, modelMuon;
    
    private ArrayList<DTO_Sach> listKho = new ArrayList<>();
    private ArrayList<DTO_Sach> listMuon = new ArrayList<>();
    
    private DAL_Sach dalSach = new DAL_Sach();
    private DAL_DocGia dalDG = new DAL_DocGia();
    private DAL_PhieuMuon dalPM = new DAL_PhieuMuon();
    
    private boolean isUpdate = false;
    private String currentMaThuThu; 

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
        
        // Logic chọn người duyệt: Nếu là ONLINE hoặc Chờ duyệt -> Gán người đang đăng nhập
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
        setSize(1200, 700); // Tăng kích thước để chứa đủ 3 bảng
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- PANEL INPUT (THÔNG TIN) ---
        JPanel pnlInput = new JPanel(new GridLayout(4, 4, 10, 10));
        pnlInput.setBorder(new TitledBorder("Thông tin phiếu"));
        pnlInput.setPreferredSize(new Dimension(0, 150));

        txtMaPhieu = new JTextField(); txtMaPhieu.setEditable(false);
        txtMaDG = new JTextField(); 
        txtTenDG = new JTextField(); txtTenDG.setEditable(false);
        txtThuThu = new JTextField(); txtThuThu.setEditable(false);
        txtNgayMuon = new JTextField();
        txtHanTra = new JTextField();
        txtTienPhat = new JTextField("0");
        
        String[] stt = {"Đang mượn", "Đã trả", "Quá hạn", "Chờ duyệt"};
        cboTinhTrang = new JComboBox<>(stt);

        pnlInput.add(new JLabel("Mã Phiếu:")); pnlInput.add(txtMaPhieu);
        pnlInput.add(new JLabel("Mã Độc Giả:")); pnlInput.add(txtMaDG);
        pnlInput.add(new JLabel("Thủ Thư:")); pnlInput.add(txtThuThu);
        
        pnlInput.add(new JLabel("Tên Độc Giả:")); pnlInput.add(txtTenDG);
        pnlInput.add(new JLabel("Ngày Mượn:")); pnlInput.add(txtNgayMuon);
        pnlInput.add(new JLabel("Hạn Trả:")); pnlInput.add(txtHanTra);
        
        pnlInput.add(new JLabel("Tình Trạng:")); pnlInput.add(cboTinhTrang);
        pnlInput.add(new JLabel("Tiền Phạt (VNĐ):")); pnlInput.add(txtTienPhat);

        add(pnlInput, BorderLayout.NORTH);

        // --- PANEL CENTER (CHỨA 3 BẢNG) ---
        JPanel pnlCenter = new JPanel(new GridLayout(1, 3, 10, 10)); // Chia làm 3 cột đều nhau
        pnlCenter.setBorder(new EmptyBorder(5, 5, 5, 5));

        // 1. BẢNG ĐỘC GIẢ
        JPanel pnlDG = new JPanel(new BorderLayout());
        pnlDG.setBorder(new TitledBorder("1. Chọn Độc Giả"));
        String[] colsDG = {"Mã ĐG", "Tên Độc Giả", "SĐT"};
        modelDocGia = new DefaultTableModel(colsDG, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        tblDocGia = new JTable(modelDocGia);
        pnlDG.add(new JScrollPane(tblDocGia), BorderLayout.CENTER);

        // 2. BẢNG KHO SÁCH
        JPanel pnlKho = new JPanel(new BorderLayout());
        pnlKho.setBorder(new TitledBorder("2. Kho Sách (Nhấn đúp để chọn)"));
        String[] colsKho = {"Mã Sách", "Tên Sách", "Tồn"};
        modelKho = new DefaultTableModel(colsKho, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        tblKhoSach = new JTable(modelKho);
        pnlKho.add(new JScrollPane(tblKhoSach), BorderLayout.CENTER);

        // 3. BẢNG SÁCH MƯỢN
        JPanel pnlMuon = new JPanel(new BorderLayout());
        pnlMuon.setBorder(new TitledBorder("3. Sách Đang Chọn"));
        String[] colsMuon = {"Mã Sách", "Tên Sách"};
        modelMuon = new DefaultTableModel(colsMuon, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        tblSachMuon = new JTable(modelMuon);
        JButton btnXoaSach = new JButton("Xóa khỏi danh sách");
        btnXoaSach.setBackground(new Color(220, 53, 69));
        btnXoaSach.setForeground(Color.WHITE);
        pnlMuon.add(new JScrollPane(tblSachMuon), BorderLayout.CENTER);
        pnlMuon.add(btnXoaSach, BorderLayout.SOUTH);

        pnlCenter.add(pnlDG);
        pnlCenter.add(pnlKho);
        pnlCenter.add(pnlMuon);
        add(pnlCenter, BorderLayout.CENTER);

        // --- PANEL BOTTOM (BUTTONS) ---
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnSave = new JButton(isUpdate ? "Cập Nhật" : "Tạo Mới");
        JButton btnCancel = new JButton("Thoát");
        
        btnSave.setPreferredSize(new Dimension(120, 40));
        btnSave.setBackground(new Color(40, 167, 69)); // Xanh lá
        btnSave.setForeground(Color.WHITE);

        pnlBot.add(btnSave);
        pnlBot.add(btnCancel);
        add(pnlBot, BorderLayout.SOUTH);

        // --- LOAD DỮ LIỆU & SỰ KIỆN ---
        loadDataDocGia();
        loadDataKhoSach();

        // Sự kiện: Chọn Độc giả từ bảng 1 -> Điền lên TextField
        tblDocGia.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tblDocGia.getSelectedRow();
                if(row >= 0) {
                    txtMaDG.setText(tblDocGia.getValueAt(row, 0).toString());
                    txtTenDG.setText(tblDocGia.getValueAt(row, 1).toString());
                }
            }
        });

        // Sự kiện: Chọn Sách từ kho (Double click) -> Sang bảng 3
        tblKhoSach.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    int row = tblKhoSach.getSelectedRow();
                    if(row >= 0) {
                        DTO_Sach s = listKho.get(row);
                        if(s.getSoLuong() <= 0) {
                            JOptionPane.showMessageDialog(null, "Sách này đã hết!");
                            return;
                        }
                        for(DTO_Sach sm : listMuon) {
                            if(sm.getMaCuonSach().equals(s.getMaCuonSach())) return;
                        }
                        listMuon.add(s);
                        updateTableMuon();
                    }
                }
            }
        });

        // Sự kiện: Xóa sách khỏi bảng 3
        btnXoaSach.addActionListener(e -> {
            int row = tblSachMuon.getSelectedRow();
            if(row >= 0) {
                listMuon.remove(row);
                updateTableMuon();
            }
        });

        // Sự kiện: LƯU
        btnSave.addActionListener(e -> xuLyLuu());
        btnCancel.addActionListener(e -> dispose());
    }

    private void loadDataDocGia() {
        modelDocGia.setRowCount(0);
        ArrayList<DTO_DocGia> list = dalDG.getList();
        for(DTO_DocGia d : list) {
            modelDocGia.addRow(new Object[]{d.getMaDocGia(), d.getTenDocGia(), d.getSdt()});
        }
    }

    private void loadDataKhoSach() {
        listKho = dalSach.getList();
        modelKho.setRowCount(0);
        for(DTO_Sach s : listKho) {
            modelKho.addRow(new Object[]{s.getMaCuonSach(), s.getTenSach(), s.getSoLuong()});
        }
    }

    private void updateTableMuon() {
        modelMuon.setRowCount(0);
        for(DTO_Sach s : listMuon) {
            modelMuon.addRow(new Object[]{s.getMaCuonSach(), s.getTenSach()});
        }
    }

    private void xuLyLuu() {
        // Kiểm tra dữ liệu đầu vào
        if(txtMaDG.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa chọn độc giả!");
            return;
        }
        if(listMuon.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa chọn sách nào!");
            return;
        }
        
        DTO_PhieuMuon pm = new DTO_PhieuMuon();
        pm.setMaPhieuMuon(txtMaPhieu.getText());
        pm.setMaDocGia(txtMaDG.getText());
        pm.setMaThuThu(txtThuThu.getText()); 
        pm.setNgayMuon(Date.valueOf(txtNgayMuon.getText()));
        pm.setNgayHenTra(Date.valueOf(txtHanTra.getText()));
        pm.setTinhTrang(cboTinhTrang.getSelectedItem().toString());
        try {
            pm.setTienPhat(Double.parseDouble(txtTienPhat.getText()));
        } catch(Exception e) { pm.setTienPhat(0); }

        ArrayList<String> dsMaSach = new ArrayList<>();
        for(DTO_Sach s : listMuon) dsMaSach.add(s.getMaCuonSach());

        boolean kq;
        if(isUpdate) {
            // Gọi hàm Cập nhật
            kq = dalPM.suaPhieuMuon(pm); 
        } else {
            // Gọi hàm Thêm mới
            kq = dalPM.themPhieuMuon(pm, dsMaSach);
        }

        if(kq) {
            JOptionPane.showMessageDialog(this, "Thành công!");
            dispose();
        } else {
            // Nếu vẫn lỗi, khả năng cao do chưa chạy lệnh SQL ở Phần 1
            JOptionPane.showMessageDialog(this, "Thất bại! Kiểm tra lại mã ĐG hoặc xem lại User đăng nhập đã có trong bảng ThuThu chưa.");
        }
    }
}