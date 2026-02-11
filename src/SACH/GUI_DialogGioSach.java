package SACH;

import MUONTRA.DAL_PhieuMuon;
import MUONTRA.DTO_PhieuMuon;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.ArrayList;

public class GUI_DialogGioSach extends JDialog {

    private JTable table;
    private DefaultTableModel model;
    private ArrayList<DTO_Sach> listGioSach;
    private GUI_TraCuuSach parentGUI; 
    private String maDG;
    private boolean daGuiYeuCau = false;
    private DAL_PhieuMuon dalPM = new DAL_PhieuMuon();
    
    // Màu sắc chủ đạo
    private Color mainColor = new Color(50, 115, 220);
    private Color redColor = new Color(220, 53, 69);

    public GUI_DialogGioSach(Window parentFrame, GUI_TraCuuSach parentGUI, ArrayList<DTO_Sach> list, String maDG) {
        super(parentFrame, "GIỎ SÁCH CỦA BẠN", ModalityType.APPLICATION_MODAL);
        this.parentGUI = parentGUI;
        this.listGioSach = list;
        this.maDG = maDG;
        initUI();
        loadData();
    }

    private void initUI() {
        setSize(850, 550);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // --- 1. HEADER ---
        JLabel lblHeader = new JLabel("DANH SÁCH SÁCH CHỌN MƯỢN", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(mainColor);
        lblHeader.setBorder(new EmptyBorder(20, 0, 10, 0));
        add(lblHeader, BorderLayout.NORTH);

        // --- 2. TABLE ---
        JPanel pnlTable = new JPanel(new BorderLayout());
        pnlTable.setBorder(new EmptyBorder(10, 20, 10, 20));
        pnlTable.setBackground(Color.WHITE);

        String[] cols = {"Mã Sách", "Tên Sách", "Tác Giả", "Thể Loại", "Năm XB"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        
        table = new JTable(model);
        table.setRowHeight(35); // Tăng chiều cao dòng
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowVerticalLines(false); // Bỏ đường kẻ dọc
        table.setSelectionBackground(new Color(232, 242, 252)); // Màu khi chọn dòng
        table.setSelectionForeground(Color.BLACK);
        
        // Header Table
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(248, 249, 250));
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        
        // Căn giữa cột Năm XB
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        JScrollPane sc = new JScrollPane(table);
        sc.getViewport().setBackground(Color.WHITE);
        sc.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        pnlTable.add(sc, BorderLayout.CENTER);
        add(pnlTable, BorderLayout.CENTER);

        // --- 3. FOOTER (BUTTONS) ---
        JPanel pnlBot = new JPanel(new BorderLayout());
        pnlBot.setBackground(Color.WHITE);
        pnlBot.setBorder(new EmptyBorder(10, 20, 20, 20));

        // Label Tổng số lượng
        JLabel lblTong = new JLabel("Tổng số lượng: " + listGioSach.size() + " cuốn");
        lblTong.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTong.setForeground(Color.DARK_GRAY);
        
        // Panel chứa nút
        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlBtn.setBackground(Color.WHITE);

        JButton btnXoa = new JButton("Xóa khỏi giỏ");
        styleButton(btnXoa, redColor); // Màu đỏ

        JButton btnGui = new JButton("Gửi Yêu Cầu Mượn");
        styleButton(btnGui, mainColor); // Màu xanh chủ đạo

        pnlBtn.add(btnXoa);
        pnlBtn.add(btnGui);

        pnlBot.add(lblTong, BorderLayout.WEST);
        pnlBot.add(pnlBtn, BorderLayout.EAST);
        add(pnlBot, BorderLayout.SOUTH);

        // --- EVENTS ---
        
        // Xử lý XÓA
        btnXoa.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sách cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if(JOptionPane.showConfirmDialog(this, "Bạn chắc chắn muốn xóa sách này khỏi giỏ?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                listGioSach.remove(row);
                loadData();
                lblTong.setText("Tổng số lượng: " + listGioSach.size() + " cuốn");
                
                // Cập nhật số lượng trên nút ở GUI cha
                if (parentGUI != null) {
                    parentGUI.capNhatSoLuongGio();
                }
            }
        });

        // Xử lý GỬI
        btnGui.addActionListener(e -> xuLyMuon());
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(180, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        // Bo góc theo style FlatLaf
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0");
    }

    private void loadData() {
        model.setRowCount(0);
        for (DTO_Sach s : listGioSach) {
            model.addRow(new Object[]{
                s.getMaCuonSach(), 
                s.getTenSach(), 
                s.getTacGia(), 
                s.getMaTheLoai(), 
                s.getNamXuatBan()
            });
        }
    }

    private void xuLyMuon() {
        if(listGioSach.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ sách đang trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE); 
            return;
        }
        
        // Xác nhận lần cuối
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Xác nhận mượn " + listGioSach.size() + " cuốn sách này?\nThời hạn mượn: 7 ngày.", 
            "Xác nhận mượn", JOptionPane.YES_NO_OPTION);
            
        if(confirm != JOptionPane.YES_OPTION) return;

        DTO_PhieuMuon pm = new DTO_PhieuMuon();
        // Tạo mã phiếu ngẫu nhiên theo thời gian
        pm.setMaPhieuMuon("PM" + System.currentTimeMillis()/1000);
        pm.setMaDocGia(maDG);
        pm.setMaThuThu("ONLINE"); 
        pm.setNgayMuon(new Date(System.currentTimeMillis()));
        // Hẹn trả sau 7 ngày
        pm.setNgayHenTra(new Date(System.currentTimeMillis() + (7L * 24 * 60 * 60 * 1000)));
        
        ArrayList<String> dsMa = new ArrayList<>();
        for(DTO_Sach s : listGioSach) dsMa.add(s.getMaCuonSach());
        
        if(dalPM.themPhieuMuon(pm, dsMa)) {
            JOptionPane.showMessageDialog(this, "Gửi yêu cầu thành công!\nVui lòng đến thư viện để nhận sách.");
            daGuiYeuCau = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi tạo phiếu mượn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isDaGuiYeuCau() {
        return daGuiYeuCau;
    }
}