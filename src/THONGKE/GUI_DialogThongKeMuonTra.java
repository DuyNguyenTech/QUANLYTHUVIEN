package THONGKE;

import CHUNG.ExcelExporter;
import MUONTRA.DAL_PhieuMuon;
import MUONTRA.DTO_PhieuMuon;
import MUONTRA.GUI_DialogChiTietPhieuMuon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
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
    private JLabel lblTongSo;
    private JRadioButton rdoNgay, rdoTuan, rdoThang, rdoDangMuon;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    
    // Màu chủ đạo
    private Color mainColor = new Color(50, 115, 220);

    public GUI_DialogThongKeMuonTra(Window parent) {
        super(parent, ModalityType.APPLICATION_MODAL);
        initUI();
        rdoThang.setSelected(true); // Mặc định chọn Tháng
        loadData("MONTH");
    }

    private void initUI() {
        setTitle("BÁO CÁO MƯỢN TRẢ");
        setSize(950, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- 1. HEADER ---
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlHeader.setBackground(mainColor);
        pnlHeader.setBorder(new EmptyBorder(15, 0, 15, 0));
        
        JLabel lblTitle = new JLabel("THỐNG KÊ HOẠT ĐỘNG MƯỢN TRẢ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        pnlHeader.add(lblTitle);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. CONTENT ---
        JPanel pnlContent = new JPanel(new BorderLayout(10, 10));
        pnlContent.setBorder(new EmptyBorder(10, 20, 10, 20));
        pnlContent.setBackground(Color.WHITE);

        // A. Filter (Radio Buttons)
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        pnlFilter.setBackground(Color.WHITE);
        pnlFilter.setBorder(BorderFactory.createTitledBorder("Chọn thời gian thống kê"));
        
        rdoNgay = new JRadioButton("Hôm nay");
        rdoTuan = new JRadioButton("Tuần này");
        rdoThang = new JRadioButton("Tháng này");
        rdoDangMuon = new JRadioButton("Đang mượn (Chưa trả)");
        
        Font fontFilter = new Font("Segoe UI", Font.PLAIN, 14);
        rdoNgay.setFont(fontFilter); rdoNgay.setBackground(Color.WHITE);
        rdoTuan.setFont(fontFilter); rdoTuan.setBackground(Color.WHITE);
        rdoThang.setFont(fontFilter); rdoThang.setBackground(Color.WHITE);
        rdoDangMuon.setFont(fontFilter); rdoDangMuon.setBackground(Color.WHITE);

        ButtonGroup bg = new ButtonGroup();
        bg.add(rdoNgay); bg.add(rdoTuan); bg.add(rdoThang); bg.add(rdoDangMuon);
        
        pnlFilter.add(rdoNgay); 
        pnlFilter.add(rdoTuan); 
        pnlFilter.add(rdoThang); 
        pnlFilter.add(rdoDangMuon);
        
        pnlContent.add(pnlFilter, BorderLayout.NORTH);

        // B. Table
        String[] cols = {"Mã Phiếu", "Ngày Mượn", "Ngày Trả", "Trạng Thái"};
        model = new DefaultTableModel(cols, 0) { 
            public boolean isCellEditable(int r, int c) { return false; } 
        };
        table = new JTable(model);
        
        // Style Table Premium
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(232, 242, 252));
        table.setSelectionForeground(Color.BLACK);
        
        JTableHeader h = table.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 14));
        h.setBackground(Color.WHITE);
        h.setForeground(mainColor);
        h.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, mainColor));
        h.setPreferredSize(new Dimension(0, 40));
        
        // Renderer Chung
        DefaultTableCellRenderer center = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if(!isSelected) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                return c;
            }
        };
        center.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i<cols.length; i++) table.getColumnModel().getColumn(i).setCellRenderer(center);

        // Renderer Trạng Thái (Tô màu)
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if(!isSelected) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                
                String status = (String) value;
                if ("Đang mượn".equals(status)) {
                    setForeground(new Color(255, 152, 0)); // Cam
                    setFont(new Font("Segoe UI", Font.BOLD, 13));
                } else {
                    setForeground(new Color(40, 167, 69)); // Xanh lá
                    setFont(new Font("Segoe UI", Font.BOLD, 13));
                }
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        });

        // C. Footer (Tổng số)
        JPanel pnlFooterInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlFooterInfo.setBackground(new Color(245, 248, 253));
        pnlFooterInfo.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        JLabel lblText = new JLabel("Tổng số phiếu tìm thấy: ");
        lblText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        lblTongSo = new JLabel("0");
        lblTongSo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTongSo.setForeground(new Color(220, 53, 69)); // Đỏ
        
        pnlFooterInfo.add(lblText); 
        pnlFooterInfo.add(lblTongSo);

        JPanel pnlCenterWrapper = new JPanel(new BorderLayout());
        pnlCenterWrapper.add(new JScrollPane(table), BorderLayout.CENTER);
        pnlCenterWrapper.add(pnlFooterInfo, BorderLayout.SOUTH);
        
        pnlContent.add(pnlCenterWrapper, BorderLayout.CENTER);
        add(pnlContent, BorderLayout.CENTER);

        // --- 3. BOTTOM BUTTONS ---
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBot.setBackground(new Color(245, 248, 253));
        pnlBot.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JButton btnXuat = new JButton("Xuất Excel");
        btnXuat.setPreferredSize(new Dimension(140, 40));
        btnXuat.setBackground(new Color(40, 167, 69)); // Xanh Excel
        btnXuat.setForeground(Color.WHITE);
        btnXuat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnXuat.setFocusPainted(false);
        btnXuat.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton btnDong = new JButton("Đóng");
        btnDong.setPreferredSize(new Dimension(100, 40));
        btnDong.setBackground(new Color(220, 53, 69));
        btnDong.setForeground(Color.WHITE);
        btnDong.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDong.setFocusPainted(false);
        
        pnlBot.add(btnXuat);
        pnlBot.add(btnDong);
        add(pnlBot, BorderLayout.SOUTH);

        // --- EVENTS ---
        rdoNgay.addActionListener(e -> loadData("DAY"));
        rdoTuan.addActionListener(e -> loadData("WEEK"));
        rdoThang.addActionListener(e -> loadData("MONTH"));
        rdoDangMuon.addActionListener(e -> loadData("CURRENT"));
        
        btnXuat.addActionListener(e -> xuLyXuatExcel());
        btnDong.addActionListener(e -> dispose());

        // Double click xem chi tiết
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        String maPhieu = table.getValueAt(row, 0).toString();
                        DTO_PhieuMuon pmFull = dalPhieu.getPhieuByMa(maPhieu);
                        
                        if (pmFull != null) {
                            new GUI_DialogChiTietPhieuMuon(GUI_DialogThongKeMuonTra.this, pmFull).setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(GUI_DialogThongKeMuonTra.this, "Không tìm thấy dữ liệu chi tiết!");
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
            // Logic hiển thị trạng thái đơn giản cho thống kê
            String trangThai = (pm.getNgayTra() == null) ? "Đang mượn" : "Đã trả";
            
            model.addRow(new Object[]{
                pm.getMaPhieuMuon(),
                (pm.getNgayMuon() != null ? sdf.format(pm.getNgayMuon()) : ""),
                ngayTra,
                trangThai
            });
        }
        lblTongSo.setText(String.valueOf(list.size()));
    }

    private void xuLyXuatExcel() {
        if (table.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        new ExcelExporter().exportTable(table);
    }
}