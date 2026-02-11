package THONGKE;

import com.formdev.flatlaf.FlatClientProperties;
import CHUNG.ExcelExporter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;

public class GUI_DialogThongKeSach extends JDialog {

    private DAL_ThongKe dal = new DAL_ThongKe();
    private JRadioButton rdoDangMuon, rdoTrongKho;
    private JTable table;
    private DefaultTableModel model;
    private JLabel lblTongSoLuong;
    
    private Color mainColor = new Color(50, 115, 220);
    private Color bgColor = new Color(245, 248, 253);

    public GUI_DialogThongKeSach(Component parent) {
        super(SwingUtilities.getWindowAncestor(parent), ModalityType.APPLICATION_MODAL);
        initUI();
        rdoDangMuon.setSelected(true);
        loadData("MUON");
    }

    private void initUI() {
        setTitle("CHI TIẾT THỐNG KÊ TÀI LIỆU");
        setSize(950, 680);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(bgColor);

        // --- 1. HEADER (Bo góc dưới) ---
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlHeader.setBackground(mainColor);
        pnlHeader.setBorder(new EmptyBorder(15, 0, 15, 0));
        JLabel lblTitle = new JLabel("BÁO CÁO TÌNH TRẠNG SÁCH");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        pnlHeader.add(lblTitle);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. CONTENT ---
        JPanel pnlContent = new JPanel(new BorderLayout(10, 15));
        pnlContent.setBorder(new EmptyBorder(15, 25, 10, 25));
        pnlContent.setOpaque(false);

        // A. Filter Radio Buttons (Bo góc 20px)
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 12));
        pnlFilter.setBackground(Color.WHITE);
        pnlFilter.putClientProperty("FlatLaf.style", "arc: 20; border: 1,1,1,1, #E0E0E0");
        
        rdoDangMuon = new JRadioButton("Sách đang cho mượn");
        rdoTrongKho = new JRadioButton("Sách còn trong kho");
        Font fontRadio = new Font("Segoe UI Semibold", Font.PLAIN, 15);
        rdoDangMuon.setFont(fontRadio); rdoDangMuon.setOpaque(false);
        rdoTrongKho.setFont(fontRadio); rdoTrongKho.setOpaque(false);
        
        ButtonGroup group = new ButtonGroup();
        group.add(rdoDangMuon); group.add(rdoTrongKho);
        pnlFilter.add(rdoDangMuon); pnlFilter.add(rdoTrongKho);
        pnlContent.add(pnlFilter, BorderLayout.NORTH);

        // B. Table Section (Bo góc 25px - FIX LỖI MỜ CHỮ)
        JPanel pnlTableWrapper = new JPanel(new BorderLayout());
        pnlTableWrapper.setBackground(Color.WHITE);
        pnlTableWrapper.putClientProperty("FlatLaf.style", "arc: 25; border: 1,1,1,1, #E0E0E0");
        pnlTableWrapper.setBorder(new EmptyBorder(10, 10, 10, 10));

        model = new DefaultTableModel(new String[]{"Mã Sách", "Tên Sách", "Tình Trạng"}, 0) { 
            @Override public boolean isCellEditable(int r, int c) { return false; } 
        };
        table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowVerticalLines(false);
        
        // CHỈNH MÀU KHI CLICK (Selection Color)
        table.setSelectionBackground(new Color(210, 230, 255)); // Màu xanh sáng hơn chút
        table.setSelectionForeground(mainColor); // CHỮ KHI CHỌN SẼ CÓ MÀU XANH ĐẬM CHO RÕ
        
        JTableHeader h = table.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 15));
        h.setBackground(Color.WHITE);
        h.setForeground(mainColor);
        h.setPreferredSize(new Dimension(0, 45));

        // Renderer tùy chỉnh để đảm bảo độ nét
        DefaultTableCellRenderer customRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                if (isSelected) {
                    setFont(new Font("Segoe UI", Font.BOLD, 14)); // Click dô là in đậm luôn cho nét
                } else {
                    setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(252, 252, 252));
                }
                return c;
            }
        };
        for(int i=0; i<3; i++) table.getColumnModel().getColumn(i).setCellRenderer(customRenderer);

        JScrollPane sc = new JScrollPane(table);
        sc.setBorder(BorderFactory.createEmptyBorder());
        sc.getViewport().setBackground(Color.WHITE);
        pnlTableWrapper.add(sc, BorderLayout.CENTER);

        // C. Footer Info (Bo góc 15px)
        JPanel pnlInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlInfo.setBackground(new Color(238, 242, 248));
        pnlInfo.putClientProperty("FlatLaf.style", "arc: 15");
        pnlInfo.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JLabel lblText = new JLabel("Tổng số lượng sách hiển thị: ");
        lblText.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblTongSoLuong = new JLabel("0 cuốn");
        lblTongSoLuong.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTongSoLuong.setForeground(new Color(220, 53, 69));
        pnlInfo.add(lblText); pnlInfo.add(lblTongSoLuong);

        JPanel pnlCenterWrapper = new JPanel(new BorderLayout(0, 15));
        pnlCenterWrapper.setOpaque(false);
        pnlCenterWrapper.add(pnlTableWrapper, BorderLayout.CENTER);
        pnlCenterWrapper.add(pnlInfo, BorderLayout.SOUTH);
        pnlContent.add(pnlCenterWrapper, BorderLayout.CENTER);
        add(pnlContent, BorderLayout.CENTER);

        // --- 3. BOTTOM BUTTONS (Bo góc nút 12px) ---
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        pnlBot.setOpaque(false);
        JButton btnXuat = createButton("Xuất Excel", new Color(40, 167, 69));
        JButton btnDong = createButton("Đóng", new Color(220, 53, 69));
        pnlBot.add(btnXuat); pnlBot.add(btnDong);
        add(pnlBot, BorderLayout.SOUTH);

        rdoDangMuon.addActionListener(e -> loadData("MUON"));
        rdoTrongKho.addActionListener(e -> loadData("KHO"));
        btnXuat.addActionListener(e -> new ExcelExporter().exportTable(table));
        btnDong.addActionListener(e -> dispose());
    }

    private void loadData(String type) {
        model.setRowCount(0);
        ArrayList<DTO_ThongKeSach> list = type.equals("MUON") ? dal.getListSachDangMuon() : dal.getListSachTrongKho();
        for (DTO_ThongKeSach s : list) {
            String tt = type.equals("MUON") ? "Đang được mượn" : "Sẵn sàng";
            model.addRow(new Object[]{s.getMaCuonSach(), s.getTenSach(), tt});
        }
        lblTongSoLuong.setText(list.size() + " cuốn");
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(150, 42));
        btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("FlatLaf.style", "arc: 12; borderWidth: 0");
        return btn;
    }
}