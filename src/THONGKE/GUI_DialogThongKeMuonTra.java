package THONGKE;

import MUONTRA.DAL_PhieuMuon;
import MUONTRA.DTO_PhieuMuon;
import MUONTRA.GUI_DialogChiTietPhieuMuon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import CHUNG.ExcelExporter;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class GUI_DialogThongKeMuonTra extends JDialog {

    private DAL_ThongKe dal = new DAL_ThongKe();
    private DAL_PhieuMuon dalPhieu = new DAL_PhieuMuon();
    
    private JTable table;
    private DefaultTableModel model;
    private JLabel lblTongSach;
    private JRadioButton rdoNgay, rdoTuan, rdoThang, rdoDangMuon;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public GUI_DialogThongKeMuonTra(Window parent) {
        super(parent, ModalityType.APPLICATION_MODAL);
        initUI();
        rdoThang.setSelected(true); // Mặc định chọn Tháng
        loadData("MONTH");
    }

    private void initUI() {
        setTitle("THỐNG KÊ MƯỢN TRẢ");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- HEADER ---
        JLabel lblTitle = new JLabel("THỐNG KÊ MƯỢN TRẢ", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(100, 149, 237)); // Xanh dương dịu
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setPreferredSize(new Dimension(0, 60));
        add(lblTitle, BorderLayout.NORTH);

        // --- CONTENT ---
        JPanel pnlContent = new JPanel(new BorderLayout(10, 10));
        pnlContent.setBorder(new EmptyBorder(10, 20, 10, 20));

        // 1. Filter
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        rdoNgay = new JRadioButton("Mượn trong ngày");
        rdoTuan = new JRadioButton("Mượn trong tuần");
        rdoThang = new JRadioButton("Mượn trong tháng");
        rdoDangMuon = new JRadioButton("Phiếu đang mượn");
        
        // Font chữ cho Radio Button
        Font fontFilter = new Font("Segoe UI", Font.PLAIN, 14);
        rdoNgay.setFont(fontFilter); rdoTuan.setFont(fontFilter);
        rdoThang.setFont(fontFilter); rdoDangMuon.setFont(fontFilter);

        ButtonGroup bg = new ButtonGroup();
        bg.add(rdoNgay); bg.add(rdoTuan); bg.add(rdoThang); bg.add(rdoDangMuon);
        pnlFilter.add(rdoNgay); pnlFilter.add(rdoTuan); pnlFilter.add(rdoThang); pnlFilter.add(rdoDangMuon);
        
        pnlContent.add(pnlFilter, BorderLayout.NORTH);

        // 2. Table (Trang trí đẹp hơn)
        String[] cols = {"Mã Phiếu Mượn", "Ngày Mượn", "Ngày Trả", "Trạng Thái"};
        model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Kẻ lưới rõ ràng
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230)); // Màu kẻ xám nhạt
        
        // Header Bảng
        JTableHeader h = table.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 14));
        h.setBackground(new Color(245, 245, 245));
        
        // Căn giữa nội dung
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i<cols.length; i++) table.getColumnModel().getColumn(i).setCellRenderer(center);

        // 3. Footer (Tổng số - Trang trí giống form Sách)
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlFooter.setBackground(new Color(255, 250, 240)); // Màu kem
        pnlFooter.setBorder(BorderFactory.createLineBorder(Color.ORANGE)); // Viền cam
        
        JLabel lblText = new JLabel("Tổng số lượng phiếu: ");
        lblText.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        lblTongSach = new JLabel("0");
        lblTongSach.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTongSach.setForeground(Color.RED);
        
        pnlFooter.add(lblText); 
        pnlFooter.add(lblTongSach);

        // Gộp vào Content
        pnlContent.add(new JScrollPane(table), BorderLayout.CENTER);
        pnlContent.add(pnlFooter, BorderLayout.SOUTH); // Đưa footer vào sát bảng
        
        add(pnlContent, BorderLayout.CENTER);

        // --- BOTTOM BUTTON ---
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnXuat = new JButton("Xuất Excel"); // [MỚI] Đổi tên nút
        btnXuat.setPreferredSize(new Dimension(150, 35));
        btnXuat.setBackground(new Color(40, 167, 69)); // Màu xanh Excel
        btnXuat.setForeground(Color.WHITE);
        btnXuat.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        pnlBot.add(btnXuat);
        add(pnlBot, BorderLayout.SOUTH);

        // --- EVENTS ---
        rdoNgay.addActionListener(e -> loadData("DAY"));
        rdoTuan.addActionListener(e -> loadData("WEEK"));
        rdoThang.addActionListener(e -> loadData("MONTH"));
        rdoDangMuon.addActionListener(e -> loadData("CURRENT"));
        
        // [MỚI] Sự kiện Xuất Excel
        btnXuat.addActionListener(e -> xuLyXuatExcel());

        // Sự kiện Double Click vào bảng để xem chi tiết
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Nhấn đúp chuột
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        // 1. Lấy mã phiếu từ cột đầu tiên
                        String maPhieu = table.getValueAt(row, 0).toString();
                        
                        // 2. Gọi DAL lấy thông tin chi tiết (bao gồm MaDocGia)
                        DTO_PhieuMuon pmFull = dalPhieu.getPhieuByMa(maPhieu);
                        
                        if (pmFull != null) {
                            // 3. Mở Dialog Chi Tiết
                            new GUI_DialogChiTietPhieuMuon(GUI_DialogThongKeMuonTra.this, pmFull).setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(GUI_DialogThongKeMuonTra.this, "Không tìm thấy dữ liệu chi tiết phiếu này!");
                        }
                    }
                }
            }
        });
    }

    private void loadData(String option) {
        model.setRowCount(0);
        ArrayList<DTO_PhieuMuon> list = dal.getListMuonTra(option);
        
        for (DTO_PhieuMuon pm : list) {
            String ngayTra = (pm.getNgayTra() != null) ? sdf.format(pm.getNgayTra()) : "";
            // Logic hiển thị trạng thái
            String trangThai = (pm.getNgayTra() == null) ? "Đang mượn" : "Đã trả";
            
            model.addRow(new Object[]{
                pm.getMaPhieuMuon(),
                (pm.getNgayMuon() != null ? sdf.format(pm.getNgayMuon()) : ""),
                ngayTra,
                trangThai
            });
        }
        lblTongSach.setText(String.valueOf(list.size()) + " phiếu");
    }

    // [MỚI] Hàm xử lý xuất Excel sử dụng class tiện ích ExcelExporter
    private void xuLyXuatExcel() {
        if (table.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Gọi class tiện ích ExcelExporter (sẽ hiện hộp thoại chọn nơi lưu)
        new ExcelExporter().exportTable(table);
    }
}