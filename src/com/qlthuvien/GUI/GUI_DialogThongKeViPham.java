package com.qlthuvien.GUI;

import com.qlthuvien.DAL.DAL_PhieuMuon; // [MỚI] Import DAL Phiếu
import com.qlthuvien.DAL.DAL_ThongKe;
import com.qlthuvien.DTO.DTO_PhieuMuon;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter; // [MỚI]
import java.awt.event.MouseEvent;   // [MỚI]
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat; // Dùng format tiền đẹp hơn
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GUI_DialogThongKeViPham extends JDialog {

    private DAL_ThongKe dal = new DAL_ThongKe();
    private DAL_PhieuMuon dalPhieu = new DAL_PhieuMuon(); // [MỚI] Khai báo để lấy chi tiết
    
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

        // Footer (Tổng số - Trang trí đồng bộ)
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlFooter.setBackground(new Color(255, 250, 240)); // Màu kem
        pnlFooter.setBorder(BorderFactory.createLineBorder(Color.ORANGE)); // Viền cam
        
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
        JButton btnXuat = new JButton("Xuất File");
        btnXuat.setPreferredSize(new Dimension(150, 35));
        btnXuat.setBackground(Color.WHITE);
        btnXuat.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        pnlBot.add(btnXuat);
        add(pnlBot, BorderLayout.SOUTH);

        btnXuat.addActionListener(e -> xuLyXuatFile());

        // [QUAN TRỌNG] THÊM SỰ KIỆN CLICK ĐÚP CHUỘT
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Nhấn đúp
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        // 1. Lấy Mã Phiếu
                        String maPhieu = table.getValueAt(row, 0).toString();
                        
                        // 2. Gọi DAL lấy thông tin chi tiết (bao gồm cả MaDocGia)
                        DTO_PhieuMuon pmFull = dalPhieu.getPhieuByMa(maPhieu);
                        
                        if (pmFull != null) {
                            // 3. Mở form chi tiết (Form này sẽ hiện Tên Độc Giả, Sách...)
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
            // Format tiền đẹp hơn
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

    private void xuLyXuatFile() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String path = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Log_ViPham_" + timeStamp + ".txt";
            PrintWriter pw = new PrintWriter(new FileWriter(path));
            pw.println("THỐNG KÊ VI PHẠM - " + new Date());
            pw.println("Tổng số vi phạm: " + lblTongViPham.getText());
            pw.println("--------------------------------------------------------------------------------");
            pw.printf("%-10s %-15s %-15s %-10s %-25s %-15s\n", "MÃ PM", "MƯỢN", "HẸN TRẢ", "MÃ ĐG", "LỖI", "PHẠT");
            pw.println("--------------------------------------------------------------------------------");
            
            for(int i=0; i<model.getRowCount(); i++) {
                pw.printf("%-10s %-15s %-15s %-10s %-25s %-15s\n", 
                    model.getValueAt(i, 0), model.getValueAt(i, 1), model.getValueAt(i, 2), 
                    model.getValueAt(i, 3), model.getValueAt(i, 4), model.getValueAt(i, 5));
            }
            pw.close();
            JOptionPane.showMessageDialog(this, "Đã lưu file tại: " + path);
        } catch (Exception e) { e.printStackTrace(); }
    }
}