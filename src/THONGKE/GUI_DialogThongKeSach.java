package THONGKE;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;

import CHUNG.ExcelExporter; // Import tiện ích xuất Excel

public class GUI_DialogThongKeSach extends JDialog {

    private DAL_ThongKe dal = new DAL_ThongKe();
    private JRadioButton rdoDangMuon, rdoTrongKho;
    private JTable table;
    private DefaultTableModel model;
    private JLabel lblTongSoLuong;
    
    // Màu chủ đạo
    private Color mainColor = new Color(50, 115, 220);

    public GUI_DialogThongKeSach(Component parent) {
        // Nếu parent là JPanel hoặc Component khác, lấy Window chứa nó
        super(SwingUtilities.getWindowAncestor(parent), ModalityType.APPLICATION_MODAL);
        initUI();
        // Mặc định chọn "Đang mượn" khi mở lên
        rdoDangMuon.setSelected(true);
        loadData("MUON");
        loadTongTaiSan();
    }

    private void initUI() {
        setTitle("CHI TIẾT THỐNG KÊ TÀI LIỆU");
        setSize(950, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- 1. HEADER ---
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlHeader.setBackground(mainColor);
        pnlHeader.setBorder(new EmptyBorder(15, 0, 15, 0));
        
        JLabel lblTitle = new JLabel("BÁO CÁO TÌNH TRẠNG SÁCH");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        pnlHeader.add(lblTitle);
        
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. CONTENT ---
        JPanel pnlContent = new JPanel(new BorderLayout(10, 10));
        pnlContent.setBorder(new EmptyBorder(10, 20, 10, 20));
        pnlContent.setBackground(Color.WHITE);

        // A. Filter Radio Buttons
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        pnlFilter.setBackground(Color.WHITE);
        pnlFilter.setBorder(BorderFactory.createTitledBorder("Lọc dữ liệu"));
        
        rdoDangMuon = new JRadioButton("Sách đang cho mượn");
        rdoTrongKho = new JRadioButton("Sách còn trong kho");
        
        Font fontRadio = new Font("Segoe UI", Font.PLAIN, 14);
        rdoDangMuon.setFont(fontRadio); rdoDangMuon.setBackground(Color.WHITE);
        rdoTrongKho.setFont(fontRadio); rdoTrongKho.setBackground(Color.WHITE);
        
        ButtonGroup group = new ButtonGroup();
        group.add(rdoDangMuon);
        group.add(rdoTrongKho);
        
        pnlFilter.add(rdoDangMuon);
        pnlFilter.add(rdoTrongKho);
        
        pnlContent.add(pnlFilter, BorderLayout.NORTH);

        // B. Table
        String[] cols = {"Mã Sách", "Tên Sách", "Tình Trạng"};
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

        // Renderer
        DefaultTableCellRenderer center = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if(!isSelected) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                return c;
            }
        };
        center.setHorizontalAlignment(JLabel.CENTER);
        
        // Apply renderer
        for(int i=0; i<cols.length; i++) table.getColumnModel().getColumn(i).setCellRenderer(center);
        
        // Độ rộng cột
        table.getColumnModel().getColumn(0).setPreferredWidth(120);
        table.getColumnModel().getColumn(1).setPreferredWidth(400); // Tên sách rộng
        
        // C. Footer Info (Tổng số lượng)
        JPanel pnlInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlInfo.setBackground(new Color(245, 248, 253));
        pnlInfo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JLabel lblText = new JLabel("Tổng số lượng sách trong danh sách: ");
        lblText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        lblTongSoLuong = new JLabel("Calculating...");
        lblTongSoLuong.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTongSoLuong.setForeground(new Color(220, 53, 69)); // Đỏ
        
        pnlInfo.add(lblText);
        pnlInfo.add(lblTongSoLuong);

        // Ghép Table + Info vào Panel giữa
        JPanel pnlCenterWrapper = new JPanel(new BorderLayout());
        pnlCenterWrapper.add(new JScrollPane(table), BorderLayout.CENTER);
        pnlCenterWrapper.add(pnlInfo, BorderLayout.SOUTH);
        
        pnlContent.add(pnlCenterWrapper, BorderLayout.CENTER);
        add(pnlContent, BorderLayout.CENTER);

        // --- 3. BOTTOM BUTTONS ---
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBot.setBackground(new Color(245, 248, 253));
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
        btnDong.setBackground(new Color(220, 53, 69));
        btnDong.setForeground(Color.WHITE);
        btnDong.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDong.setFocusPainted(false);
        
        pnlBot.add(btnXuat);
        pnlBot.add(btnDong);
        add(pnlBot, BorderLayout.SOUTH);

        // --- EVENTS ---
        rdoDangMuon.addActionListener(e -> loadData("MUON"));
        rdoTrongKho.addActionListener(e -> loadData("KHO"));
        
        btnXuat.addActionListener(e -> xuLyXuatExcel());
        btnDong.addActionListener(e -> dispose());
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
            // Hiển thị trạng thái đẹp hơn
            String tt = s.getTinhTrang();
            if(type.equals("MUON")) tt = "Đang được mượn";
            else if(type.equals("KHO")) tt = "Sẵn sàng";
            
            model.addRow(new Object[]{s.getMaSach(), s.getTenSach(), tt});
        }
        lblTongSoLuong.setText(list.size() + " cuốn");
    }

    private void loadTongTaiSan() {
        new Thread(() -> {
            // Có thể dùng hàm này để hiện tổng toàn bộ kho (không phụ thuộc filter)
             int tong = dal.getTongTaiSanSach();
             SwingUtilities.invokeLater(() -> lblTongSoLuong.setText(tong + " cuốn"));
        }).start();
    }

    // Xử lý xuất Excel chuyên nghiệp
    private void xuLyXuatExcel() {
        if (table.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        new ExcelExporter().exportTable(table);
    }
}