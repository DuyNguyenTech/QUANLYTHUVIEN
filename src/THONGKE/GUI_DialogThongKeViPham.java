package THONGKE;

import com.formdev.flatlaf.FlatClientProperties;
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
    
    private Color alertColor = new Color(220, 53, 69); 
    private Color bgColor = new Color(245, 248, 253);

    public GUI_DialogThongKeViPham(Window parent) {
        super(parent, ModalityType.APPLICATION_MODAL);
        initUI();
        loadData();
    }

    private void initUI() {
        setTitle("THỐNG KÊ VI PHẠM & QUÁ HẠN");
        setSize(1050, 720);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(bgColor);

        // --- 1. HEADER ---
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlHeader.setBackground(alertColor);
        pnlHeader.setBorder(new EmptyBorder(15, 0, 15, 0));
        
        JLabel lblTitle = new JLabel("DANH SÁCH VI PHẠM & QUÁ HẠN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);
        pnlHeader.add(lblTitle);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. CONTENT ---
        JPanel pnlContent = new JPanel(new BorderLayout(15, 15));
        pnlContent.setBorder(new EmptyBorder(25, 30, 10, 30));
        pnlContent.setBackground(bgColor);

        // A. Table Card Premium
        JPanel pnlTableCard = new JPanel(new BorderLayout());
        pnlTableCard.setBackground(Color.WHITE);
        // FIX: Truyền thuộc tính bo góc chuẩn FlatLaf 3.5.4
        pnlTableCard.putClientProperty("FlatLaf.style", "arc: 20; border: 1,1,1,1, #E0E0E0");
        pnlTableCard.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] cols = {"Mã Phiếu", "Ngày Mượn", "Hẹn Trả", "Mã Độc Giả", "Lỗi Vi Phạm", "Tiền Phạt"};
        model = new DefaultTableModel(cols, 0) { 
            @Override public boolean isCellEditable(int r, int c) { return false; } 
        };
        table = new JTable(model);
        
        // Style Table
        table.setRowHeight(42);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(232, 240, 250)); 
        table.setSelectionForeground(Color.BLACK);
        
        JTableHeader h = table.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 15));
        h.setBackground(new Color(248, 249, 250));
        h.setForeground(alertColor);
        h.setPreferredSize(new Dimension(0, 45));
        
        // Renderer chuyên nghiệp
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if(!isSelected) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(252, 252, 252));
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        };

        DefaultTableCellRenderer moneyRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if(!isSelected) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(252, 252, 252));
                setForeground(alertColor);
                setFont(new Font("Segoe UI", Font.BOLD, 14));
                setHorizontalAlignment(CENTER);
                return c;
            }
        };

        for(int i=0; i<5; i++) table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(moneyRenderer);

        JScrollPane sc = new JScrollPane(table);
        sc.setBorder(BorderFactory.createEmptyBorder());
        sc.getViewport().setBackground(Color.WHITE);
        pnlTableCard.add(sc, BorderLayout.CENTER);

        // B. Info Panel bên dưới bảng
        JPanel pnlFooterInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlFooterInfo.setOpaque(false);
        pnlFooterInfo.setBorder(new EmptyBorder(10, 5, 0, 5));
        
        JLabel lblText = new JLabel("Tổng cộng phát hiện: ");
        lblText.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblTongViPham = new JLabel("0 trường hợp");
        lblTongViPham.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTongViPham.setForeground(alertColor);
        
        pnlFooterInfo.add(lblText); 
        pnlFooterInfo.add(lblTongViPham);

        pnlContent.add(pnlTableCard, BorderLayout.CENTER);
        pnlContent.add(pnlFooterInfo, BorderLayout.SOUTH);
        add(pnlContent, BorderLayout.CENTER);

        // --- 3. BOTTOM BUTTONS ---
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        pnlBot.setOpaque(false);
        pnlBot.setBorder(new EmptyBorder(0, 0, 15, 25));
        
        JButton btnXuat = createButton("Xuất File Excel", new Color(40, 167, 69));
        JButton btnDong = createButton("Đóng", new Color(108, 117, 125));
        
        // Sắp xếp thứ tự theo ý anh
        pnlBot.add(btnXuat);
        pnlBot.add(btnDong);
        add(pnlBot, BorderLayout.SOUTH);

        // --- EVENTS ---
        btnXuat.addActionListener(e -> xuLyXuatFile());
        btnDong.addActionListener(e -> dispose());

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
                        }
                    }
                }
            }
        });
    }

    private void loadData() {
        model.setRowCount(0);
        java.util.ArrayList<MUONTRA.DTO_PhieuMuon> list = (java.util.ArrayList<MUONTRA.DTO_PhieuMuon>) dal.getListViPham();
        for (MUONTRA.DTO_PhieuMuon pm : list) {
            model.addRow(new Object[]{
                pm.getMaPhieuMuon(),
                (pm.getNgayMuon() != null ? sdf.format(pm.getNgayMuon()) : ""),
                (pm.getNgayHenTra() != null ? sdf.format(pm.getNgayHenTra()) : ""), 
                pm.getMaDocGia(),
                pm.getTinhTrang(), 
                df.format(pm.getTienPhat())
            });
        }
        lblTongViPham.setText(list.size() + " trường hợp");
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(160, 42));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // FIX: focusedBorderColor
        btn.putClientProperty("FlatLaf.style", "arc: 10; borderWidth: 0; focusedBorderColor: #FFFFFF");
        return btn;
    }

    private void xuLyXuatFile() {
        if (table.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        new ExcelExporter().exportTable(table);
    }
}