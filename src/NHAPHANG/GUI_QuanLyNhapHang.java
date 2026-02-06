package NHAPHANG;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

// Import để dùng class User trong GUI_Main
import HETHONG.GUI_Main; 

public class GUI_QuanLyNhapHang extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JButton btnNhapHang, btnQLNCC;
    private DAL_NhapHang dal = new DAL_NhapHang();
    
    // Cần thông tin user đang đăng nhập để truyền vào phiếu nhập
    private String maNV; 
    private String tenNV;

    public GUI_QuanLyNhapHang(String maNV, String tenNV) {
        this.maNV = maNV;
        this.tenNV = tenNV;
        
        setLayout(new BorderLayout());

        // --- 1. HEADER & TOOLBAR ---
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setBackground(Color.WHITE);
        pnlTop.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblTitle = new JLabel("LỊCH SỬ NHẬP HÀNG");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(new Color(0, 102, 204));
        
        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBtn.setBackground(Color.WHITE);

        btnQLNCC = new JButton("Quản Lý Nhà Cung Cấp");
        btnQLNCC.setBackground(new Color(255, 153, 51));
        btnQLNCC.setForeground(Color.WHITE);
        
        btnNhapHang = new JButton("+ Tạo Phiếu Nhập Mới");
        btnNhapHang.setBackground(new Color(0, 153, 76));
        btnNhapHang.setForeground(Color.WHITE);
        btnNhapHang.setFont(new Font("Arial", Font.BOLD, 14));
        btnNhapHang.setPreferredSize(new Dimension(200, 40));

        pnlBtn.add(btnQLNCC);
        pnlBtn.add(btnNhapHang);
        
        pnlTop.add(lblTitle, BorderLayout.WEST);
        pnlTop.add(pnlBtn, BorderLayout.EAST);
        add(pnlTop, BorderLayout.NORTH);

        // --- 2. TABLE ---
        String[] cols = {"Mã Phiếu", "Nhà Cung Cấp", "Người Nhập", "Ngày Nhập", "Tổng Tiền"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } // Không cho sửa trực tiếp
        };
        table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(230, 240, 255));
        
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- 3. EVENTS ---
        loadData();

        // Bấm nút Nhập hàng -> Mở Form to đùng (GUI_TaoPhieuNhap)
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

        // Bấm nút QLNCC -> Mở Panel QLNCC trong 1 cửa sổ Dialog cho gọn
        btnQLNCC.addActionListener(e -> {
            JDialog dialog = new JDialog();
            dialog.setTitle("Quản Lý Nhà Cung Cấp");
            dialog.setSize(800, 500);
            dialog.setLocationRelativeTo(null);
            dialog.add(new GUI_QuanLyNhaCungCap()); // Tái sử dụng JPanel cũ
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
                pn.getMaNCC(),      // Lúc nãy ở DAL mình đã lưu Tên NCC vào biến này
                pn.getMaNhanVien(), // Lúc nãy ở DAL mình đã lưu Tên NV vào biến này
                sdf.format(pn.getNgayNhap()),
                df.format(pn.getTongTien())
            });
        }
    }
}