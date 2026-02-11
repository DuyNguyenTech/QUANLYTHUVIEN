package NHAPHANG;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class GUI_QuanLyNhapHang extends JPanel {
    
    // Màu chủ đạo (Đồng bộ với hệ thống)
    private Color mainColor = new Color(50, 115, 220); 
    private Color bgColor = new Color(245, 248, 253); // Nền xám nhạt Premium

    private JTable table;
    private DefaultTableModel model;
    private JButton btnNhapHang, btnQLNCC;
    private DAL_NhapHang dal = new DAL_NhapHang();
    
    // Thông tin nhân viên đăng nhập
    private String maNV; 
    private String tenNV;

    public GUI_QuanLyNhapHang(String maNV, String tenNV) {
        this.maNV = maNV;
        this.tenNV = tenNV;
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(bgColor);
        setBorder(new EmptyBorder(20, 20, 20, 20)); // Căn lề form thoáng như các form trước

        // --- 1. HEADER (TIÊU ĐỀ & NÚT CHỨC NĂNG) ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(bgColor); // Đồng bộ màu nền
        pnlHeader.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel lblTitle = new JLabel("LỊCH SỬ NHẬP HÀNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26)); // Font to chuẩn Premium
        lblTitle.setForeground(mainColor);
        
        // Panel chứa nút bấm (FlowLayout Right)
        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlBtn.setBackground(bgColor);

        // Tạo nút với kích thước đồng bộ
        btnQLNCC = createButton("Quản Lý Nhà Cung Cấp", new Color(255, 193, 7), Color.BLACK); // Vàng + Chữ đen cho dễ đọc
        btnNhapHang = createButton("+ Tạo Phiếu Nhập Mới", new Color(40, 167, 69), Color.WHITE); // Xanh lá

        pnlBtn.add(btnQLNCC);
        pnlBtn.add(btnNhapHang);
        
        pnlHeader.add(lblTitle, BorderLayout.WEST);
        pnlHeader.add(pnlBtn, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. TABLE (BẢNG DỮ LIỆU TRONG THẺ CARD) ---
        JPanel pnlTableCard = new JPanel(new BorderLayout());
        pnlTableCard.setBackground(Color.WHITE);
        // Bo góc Card
        pnlTableCard.putClientProperty(FlatClientProperties.STYLE, "arc: 20; border: 1,1,1,1, #E0E0E0");
        pnlTableCard.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] cols = {"Mã Phiếu", "Nhà Cung Cấp", "Người Nhập", "Ngày Nhập", "Tổng Tiền"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } 
        };
        table = new JTable(model);
        
        // [STYLE PREMIUM]
        table.setRowHeight(40); // Dòng cao thoáng
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowVerticalLines(false); // Bỏ kẻ dọc
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(232, 242, 252)); // Xanh nhạt khi chọn
        table.setSelectionForeground(Color.BLACK);
        
        // Style Header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setBackground(new Color(248, 249, 250)); // Xám nhạt hiện đại
        header.setForeground(new Color(50, 50, 50));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        header.setPreferredSize(new Dimension(0, 45));

        // Renderer chung (Căn giữa, Striped Rows)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                }
                setBorder(new EmptyBorder(0, 5, 0, 5));
                return c;
            }
        };
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        // Renderer cho cột Tổng Tiền (In đậm, màu đỏ Premium)
        DefaultTableCellRenderer moneyRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                
                c.setForeground(new Color(220, 53, 69)); // Màu đỏ chuẩn Flat
                c.setFont(new Font("Segoe UI", Font.BOLD, 15)); // Chữ to rõ
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        };

        // Apply Renderer
        for (int i = 0; i < table.getColumnCount(); i++) {
            if(i == 4) table.getColumnModel().getColumn(i).setCellRenderer(moneyRenderer); // Cột tiền
            else table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Chỉnh độ rộng cột
        table.getColumnModel().getColumn(1).setPreferredWidth(280); // Tên NCC rộng hơn

        JScrollPane sc = new JScrollPane(table);
        sc.getViewport().setBackground(Color.WHITE);
        sc.setBorder(BorderFactory.createEmptyBorder()); // Xóa viền đen mặc định của ScrollPane
        
        pnlTableCard.add(sc, BorderLayout.CENTER);
        add(pnlTableCard, BorderLayout.CENTER);

        // --- 3. EVENTS ---
        
        // Bấm nút Nhập hàng -> Mở Form tạo phiếu
        btnNhapHang.addActionListener(e -> {
            GUI_TaoPhieuNhap gui = new GUI_TaoPhieuNhap(maNV, tenNV);
            gui.setVisible(true);
            // Khi đóng form nhập hàng thì load lại bảng lịch sử
            gui.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    loadData();
                }
            });
        });

        // Bấm nút QLNCC -> Mở Dialog Quản Lý NCC
        btnQLNCC.addActionListener(e -> {
            JDialog dialog = new JDialog();
            dialog.setTitle("QUẢN LÝ NHÀ CUNG CẤP");
            dialog.setSize(950, 650); // Tăng kích thước dialog cho thoáng
            dialog.setLocationRelativeTo(null);
            dialog.add(new GUI_QuanLyNhaCungCap()); 
            dialog.setModal(true); // Chặn thao tác cửa sổ dưới
            dialog.setVisible(true);
        });
    }

    private void loadData() {
        model.setRowCount(0);
        ArrayList<DTO_PhieuNhap> list = dal.getDanhSachPhieuNhap();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        DecimalFormat df = new DecimalFormat("#,### VNĐ");
        
        for (DTO_PhieuNhap pn : list) {
            model.addRow(new Object[]{
                pn.getMaPhieu(),
                pn.getMaNCC(),      // Dữ liệu đã join tên NCC
                pn.getMaNhanVien(), // Dữ liệu đã join tên NV
                sdf.format(pn.getNgayNhap()),
                df.format(pn.getTongTien())
            });
        }
    }

    // Hàm tạo nút bấm chuẩn style Premium
    private JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // [QUAN TRỌNG] Set kích thước cố định và bo góc
        btn.setPreferredSize(new Dimension(210, 42)); 
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0");
        return btn;
    }
}