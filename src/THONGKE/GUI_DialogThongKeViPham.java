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

    public GUI_DialogThongKeViPham(Window parent) {
        super(parent, ModalityType.APPLICATION_MODAL);
        initUI();
        loadData();
    }

    private void initUI() {
        setTitle("THỐNG KÊ VI PHẠM");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // HEADER
        JLabel lblTitle = new JLabel("THỐNG KÊ VI PHẠM", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(220, 53, 69)); // Màu đỏ (Cảnh báo)
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setPreferredSize(new Dimension(0, 60));
        add(lblTitle, BorderLayout.NORTH);

        // CONTENT
        JPanel pnlContent = new JPanel(new BorderLayout(10, 10));
        pnlContent.setBorder(new EmptyBorder(10, 20, 10, 20));

        // TABLE
        String[] cols = {"Mã Phiếu", "Ngày Mượn", "Hẹn Trả", "Mã ĐG", "Lỗi Vi Phạm", "Tiền Phạt"};
        model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Kẻ lưới
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));

        // Header Table
        JTableHeader h = table.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 14));
        h.setBackground(new Color(245, 245, 245));
        
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i<cols.length; i++) table.getColumnModel().getColumn(i).setCellRenderer(center);
        
        // Renderer riêng cho cột Tiền Phạt (Màu đỏ đậm)
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setForeground(Color.RED);
                c.setFont(new Font("Segoe UI", Font.BOLD, 14));
                setHorizontalAlignment(CENTER);
                return c;
            }
        });

        // Footer (Tổng số)
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlFooter.setBackground(new Color(255, 250, 240)); 
        pnlFooter.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
        
        JLabel lblText = new JLabel("Tổng số trường hợp vi phạm: ");
        lblText.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        lblTongViPham = new JLabel("0");
        lblTongViPham.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTongViPham.setForeground(Color.RED);
        
        pnlFooter.add(lblText); 
        pnlFooter.add(lblTongViPham);

        // Gộp Content
        pnlContent.add(new JScrollPane(table), BorderLayout.CENTER);
        pnlContent.add(pnlFooter, BorderLayout.SOUTH);
        add(pnlContent, BorderLayout.CENTER);

        // BOTTOM BUTTON
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnXuat = new JButton("Xuất Excel"); // Đổi tên nút cho đúng chức năng
        btnXuat.setPreferredSize(new Dimension(150, 35));
        btnXuat.setBackground(new Color(40, 167, 69)); // Màu xanh Excel
        btnXuat.setForeground(Color.WHITE);
        btnXuat.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        pnlBot.add(btnXuat);
        add(pnlBot, BorderLayout.SOUTH);

        // SỰ KIỆN XUẤT FILE
        btnXuat.addActionListener(e -> xuLyXuatFile());

        // SỰ KIỆN CLICK ĐÚP CHUỘT (Giữ nguyên tính năng này)
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Nhấn đúp
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        String maPhieu = table.getValueAt(row, 0).toString();
                        DTO_PhieuMuon pmFull = dalPhieu.getPhieuByMa(maPhieu);
                        
                        if (pmFull != null) {
                            new GUI_DialogChiTietPhieuMuon(GUI_DialogThongKeViPham.this, pmFull).setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(GUI_DialogThongKeViPham.this, "Không tìm thấy dữ liệu!");
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
                pm.getTinhTrang(),
                phat
            });
        }
        lblTongViPham.setText(String.valueOf(list.size()) + " trường hợp");
    }

    // [CẬP NHẬT] Sử dụng ExcelExporter thay vì xuất file .txt
    private void xuLyXuatFile() {
        if (table.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Gọi class tiện ích ExcelExporter
        new ExcelExporter().exportTable(table);
    }
}