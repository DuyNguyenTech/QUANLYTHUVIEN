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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class GUI_DialogThongKeViPham extends JDialog {

    private DAL_ThongKe dal = new DAL_ThongKe();
    private DAL_PhieuMuon dalPhieu = new DAL_PhieuMuon();
    
    private JTable table;
    private DefaultTableModel model;
    private JLabel lblTongViPham;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");
    
    // Màu chủ đạo cho Form Vi Phạm (Màu Đỏ Cảnh Báo)
    private Color alertColor = new Color(220, 53, 69); 

    public GUI_DialogThongKeViPham(Window parent) {
        super(parent, ModalityType.APPLICATION_MODAL);
        initUI();
        loadData();
    }

    private void initUI() {
        setTitle("THỐNG KÊ VI PHẠM");
        setSize(950, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- 1. HEADER ---
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlHeader.setBackground(alertColor);
        pnlHeader.setBorder(new EmptyBorder(15, 0, 15, 0));
        
        JLabel lblTitle = new JLabel("DANH SÁCH VI PHẠM & QUÁ HẠN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        pnlHeader.add(lblTitle);
        
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. CONTENT ---
        JPanel pnlContent = new JPanel(new BorderLayout(10, 10));
        pnlContent.setBorder(new EmptyBorder(10, 20, 10, 20));
        pnlContent.setBackground(Color.WHITE);

        // A. Table
        String[] cols = {"Mã Phiếu", "Ngày Mượn", "Hẹn Trả", "Mã ĐG", "Lỗi Vi Phạm", "Tiền Phạt"};
        model = new DefaultTableModel(cols, 0) { 
            public boolean isCellEditable(int r, int c) { return false; } 
        };
        table = new JTable(model);
        
        // Style Table Premium
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(255, 235, 238)); // Màu chọn đỏ nhạt
        table.setSelectionForeground(Color.BLACK);
        
        JTableHeader h = table.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 14));
        h.setBackground(Color.WHITE);
        h.setForeground(alertColor); // Header chữ đỏ
        h.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, alertColor));
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
        
        // Renderer Cột Tiền Phạt (Đỏ Đậm)
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if(!isSelected) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                
                c.setForeground(new Color(180, 0, 0)); // Đỏ đậm
                c.setFont(new Font("Segoe UI", Font.BOLD, 14));
                setHorizontalAlignment(CENTER);
                return c;
            }
        });

        // B. Footer Info (Tổng số)
        JPanel pnlInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlInfo.setBackground(new Color(255, 245, 245)); // Nền đỏ rất nhạt
        pnlInfo.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));
        
        JLabel lblText = new JLabel("Tổng số trường hợp vi phạm: ");
        lblText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        lblTongViPham = new JLabel("Calculating...");
        lblTongViPham.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTongViPham.setForeground(alertColor);
        
        pnlInfo.add(lblText); 
        pnlInfo.add(lblTongViPham);

        // Gộp Table + Info
        JPanel pnlCenterWrapper = new JPanel(new BorderLayout());
        pnlCenterWrapper.add(new JScrollPane(table), BorderLayout.CENTER);
        pnlCenterWrapper.add(pnlInfo, BorderLayout.SOUTH);
        
        pnlContent.add(pnlCenterWrapper, BorderLayout.CENTER);
        add(pnlContent, BorderLayout.CENTER);

        // --- 3. BOTTOM BUTTONS ---
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBot.setBackground(new Color(245, 248, 253)); // Hoặc màu trắng tùy sở thích
        pnlBot.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JButton btnXuat = new JButton("Xuất Excel");
        btnXuat.setPreferredSize(new Dimension(140, 40));
        btnXuat.setBackground(new Color(40, 167, 69)); // Xanh lá Excel
        btnXuat.setForeground(Color.WHITE);
        btnXuat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnXuat.setFocusPainted(false);
        btnXuat.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton btnDong = new JButton("Đóng");
        btnDong.setPreferredSize(new Dimension(100, 40));
        btnDong.setBackground(alertColor); // Đóng màu đỏ cho hợp tone
        btnDong.setForeground(Color.WHITE);
        btnDong.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDong.setFocusPainted(false);
        
        pnlBot.add(btnXuat);
        pnlBot.add(btnDong);
        add(pnlBot, BorderLayout.SOUTH);

        // --- EVENTS ---
        btnXuat.addActionListener(e -> xuLyXuatFile());
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
                            new GUI_DialogChiTietPhieuMuon(GUI_DialogThongKeViPham.this, pmFull).setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(GUI_DialogThongKeViPham.this, "Không tìm thấy dữ liệu chi tiết!");
                        }
                    }
                }
            }
        });
    }

    private void loadData() {
        model.setRowCount(0);
        ArrayList<DTO_PhieuMuon> list = dal.getListViPham();
        
        for (DTO_PhieuMuon pm : list) {
            String henTra = (pm.getNgayHenTra() != null) ? sdf.format(pm.getNgayHenTra()) : "";
            String phat = df.format(pm.getTienPhat());
            
            model.addRow(new Object[]{
                pm.getMaPhieuMuon(),
                (pm.getNgayMuon() != null ? sdf.format(pm.getNgayMuon()) : ""),
                henTra, 
                pm.getMaDocGia(),
                pm.getTinhTrang(), // Lỗi vi phạm (Quá hạn/Mất sách...)
                phat
            });
        }
        lblTongViPham.setText(list.size() + " trường hợp");
    }

    private void xuLyXuatFile() {
        if (table.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        new ExcelExporter().exportTable(table);
    }
}