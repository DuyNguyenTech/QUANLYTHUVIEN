package THONGKE;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GUI_DialogThongKeSach extends JDialog {

    private DAL_ThongKe dal = new DAL_ThongKe();
    private JRadioButton rdoDangMuon, rdoTrongKho;
    private JTable table;
    private DefaultTableModel model;
    private JLabel lblTongSoLuong;

    public GUI_DialogThongKeSach(Component parent) {
        initUI();
        // Mặc định chọn "Đang mượn" khi mở lên
        rdoDangMuon.setSelected(true);
        loadData("MUON");
        loadTongTaiSan();
    }

    private void initUI() {
        setTitle("CHI TIẾT THỐNG KÊ TÀI LIỆU");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(new BorderLayout());

        // --- 1. HEADER ---
        JLabel lblTitle = new JLabel("THỐNG KÊ TÀI LIỆU", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(100, 149, 237)); // Màu xanh dịu
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setPreferredSize(new Dimension(0, 60));
        add(lblTitle, BorderLayout.NORTH);

        // --- 2. CONTENT ---
        JPanel pnlContent = new JPanel(new BorderLayout(10, 10));
        pnlContent.setBorder(new EmptyBorder(10, 20, 10, 20));

        // A. Filter Radio Buttons
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 10));
        rdoDangMuon = new JRadioButton("Sách đang cho mượn");
        rdoTrongKho = new JRadioButton("Sách còn trong kho");
        
        rdoDangMuon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rdoTrongKho.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        ButtonGroup group = new ButtonGroup();
        group.add(rdoDangMuon);
        group.add(rdoTrongKho);
        
        pnlFilter.add(rdoDangMuon);
        pnlFilter.add(rdoTrongKho);

        // B. Table
        String[] cols = {"Mã Cuốn Sách", "Tên Sách", "Tình Trạng"};
        model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JTableHeader h = table.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 14));
        h.setBackground(new Color(240, 240, 240));

        // Căn chỉnh cột
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(350);
        table.getColumnModel().getColumn(2).setPreferredWidth(250);

        // C. Panel Tổng Số Lượng
        JPanel pnlInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlInfo.setBackground(new Color(255, 250, 240)); 
        pnlInfo.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        JLabel lblText = new JLabel("Tổng số lượng sách: ");
        lblText.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        lblTongSoLuong = new JLabel("Đang tính toán...");
        lblTongSoLuong.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTongSoLuong.setForeground(Color.RED);
        
        pnlInfo.add(lblText);
        pnlInfo.add(lblTongSoLuong);

        // Lắp ghép phần trên
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.add(pnlFilter, BorderLayout.NORTH);
        
        JLabel lblListTitle = new JLabel(" Danh sách:");
        lblListTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblListTitle.setBorder(new EmptyBorder(5, 0, 5, 0));
        pnlTop.add(lblListTitle, BorderLayout.SOUTH);
        
        pnlContent.add(pnlTop, BorderLayout.NORTH);
        pnlContent.add(new JScrollPane(table), BorderLayout.CENTER);
        pnlContent.add(pnlInfo, BorderLayout.SOUTH);

        add(pnlContent, BorderLayout.CENTER);

        // --- 3. BOTTOM BUTTONS ---
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBot.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JButton btnLog = new JButton("Ghi log");
        btnLog.setPreferredSize(new Dimension(100, 35));
        btnLog.setBackground(Color.WHITE);
        btnLog.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        pnlBot.add(btnLog);
        add(pnlBot, BorderLayout.SOUTH);

        // --- EVENTS ---
        rdoDangMuon.addActionListener(e -> loadData("MUON"));
        rdoTrongKho.addActionListener(e -> loadData("KHO"));
        
        btnLog.addActionListener(e -> xuLyGhiLog());
    }

    private void loadData(String type) {
        model.setRowCount(0);
        ArrayList<DTO_ThongKeSach> list;
        
        if (type.equals("MUON")) {
            list = dal.getListSachDangMuon();
        } else {
            list = dal.getListSachTrongKho();
        }

        for (DTO_ThongKeSach s : list) {
            model.addRow(new Object[]{s.getMaSach(), s.getTenSach(), s.getTinhTrang()});
        }
    }

    private void loadTongTaiSan() {
        new Thread(() -> {
            int tong = dal.getTongTaiSanSach();
            SwingUtilities.invokeLater(() -> lblTongSoLuong.setText(tong + " cuốn"));
        }).start();
    }

    // --- CẬP NHẬT: LƯU FILE RA DESKTOP ---
    private void xuLyGhiLog() {
        try {
            // 1. Tạo tên file theo thời gian
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            
            // 2. Lấy đường dẫn Desktop của người dùng
            String userHome = System.getProperty("user.home");
            String desktopPath = userHome + File.separator + "Desktop" + File.separator;
            
            // 3. Đường dẫn file hoàn chỉnh
            String fileName = desktopPath + "Log_ThongKe_" + timeStamp + ".txt";

            PrintWriter pw = new PrintWriter(new FileWriter(fileName));
            
            pw.println("BÁO CÁO THỐNG KÊ TÀI LIỆU THƯ VIỆN");
            pw.println("Thời gian xuất: " + new Date());
            pw.println("Tổng số sách: " + lblTongSoLuong.getText());
            pw.println("--------------------------------------------------");
            pw.println("Loại danh sách: " + (rdoDangMuon.isSelected() ? "Sách Đang Cho Mượn" : "Sách Còn Trong Kho"));
            pw.println("Số lượng bản ghi: " + model.getRowCount());
            pw.println("--------------------------------------------------");
            pw.printf("%-15s %-40s %-30s\n", "MÃ SÁCH", "TÊN SÁCH", "TÌNH TRẠNG");
            pw.println("--------------------------------------------------");
            
            for(int i=0; i<model.getRowCount(); i++) {
                String ma = model.getValueAt(i, 0).toString();
                String ten = model.getValueAt(i, 1).toString();
                String tt = model.getValueAt(i, 2).toString();
                pw.printf("%-15s %-40s %-30s\n", ma, ten, tt);
            }
            pw.close();
            
            JOptionPane.showMessageDialog(this, "Đã ghi log thành công!" + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi ghi file!");
        }
    }
}