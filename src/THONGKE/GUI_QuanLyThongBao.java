package THONGKE;

import com.formdev.flatlaf.FlatClientProperties;
import HETHONG.DTO_TaiKhoan;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Timestamp;
import java.util.ArrayList;

public class GUI_QuanLyThongBao extends JPanel {

    private DTO_TaiKhoan tk;
    private DAL_ThongKe dal = new DAL_ThongKe();
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtTieuDe;
    private JTextArea txtNoiDung;

    private Color mainColor = new Color(50, 115, 220);
    private Color bgColor = new Color(245, 248, 253);

    public GUI_QuanLyThongBao(DTO_TaiKhoan tk) {
        this.tk = tk;
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 20));
        setBackground(bgColor);
        setBorder(new EmptyBorder(25, 30, 25, 30));

        // --- 1. PHẦN NHẬP LIỆU (BỐ CỤC TRÀN VIỀN - Ô TIÊU ĐỀ NGẮN GỌN) ---
        JPanel pnlInput = new JPanel(new BorderLayout(0, 15));
        pnlInput.setOpaque(false);

        JLabel lblHeader = new JLabel("TẠO THÔNG BÁO MỚI");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeader.setForeground(mainColor);
        pnlInput.add(lblHeader, BorderLayout.NORTH);

        // Sử dụng JPanel với GridBagLayout để kiểm soát chiều cao từng ô chính xác hơn
        JPanel pnlFields = new JPanel(new GridBagLayout());
        pnlFields.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        // Ô nhập Tiêu đề - Chiều cao rút ngắn xuống 35px để trông thanh mảnh
        txtTieuDe = new JTextField();
        txtTieuDe.setPreferredSize(new Dimension(0, 35)); 
        txtTieuDe.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTieuDe.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập tiêu đề thông báo");
        txtTieuDe.putClientProperty("FlatLaf.style", "arc: 15; borderWidth: 1; focusWidth: 2");
        
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        pnlFields.add(txtTieuDe, gbc);
        
        // Ô nhập Nội dung - Giữ nguyên độ rộng để viết được nhiều
        txtNoiDung = new JTextArea(5, 20);
        txtNoiDung.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNoiDung.setLineWrap(true);
        txtNoiDung.setWrapStyleWord(true);
        txtNoiDung.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập nội dung thông báo");
        
        JScrollPane scrollArea = new JScrollPane(txtNoiDung);
        scrollArea.putClientProperty("FlatLaf.style", "arc: 15; borderWidth: 1; focusWidth: 2");

        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        pnlFields.add(scrollArea, gbc);

        pnlInput.add(pnlFields, BorderLayout.CENTER);

        JButton btnDang = new JButton("Đăng Thông Báo");
        btnDang.setBackground(mainColor);
        btnDang.setForeground(Color.WHITE);
        btnDang.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnDang.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDang.setPreferredSize(new Dimension(0, 48));
        btnDang.putClientProperty("FlatLaf.style", "arc: 15"); 
        btnDang.addActionListener(e -> dangThongBao());
        pnlInput.add(btnDang, BorderLayout.SOUTH);

        add(pnlInput, BorderLayout.NORTH);

        // --- 2. BẢNG LỊCH SỬ (GIỮ NGUYÊN HIỂN THỊ DỄ NHÌN) ---
        JPanel pnlTableContainer = new JPanel(new BorderLayout(0, 10));
        pnlTableContainer.setOpaque(false);

        JLabel lblHistory = new JLabel("LỊCH SỬ THÔNG BÁO");
        lblHistory.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnlTableContainer.add(lblHistory, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"Tiêu Đề", "Nội Dung", "Ngày Đăng", "Người Đăng"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(40);
        
        table.setSelectionBackground(new Color(210, 230, 255)); 
        table.setSelectionForeground(mainColor); 
        table.setShowGrid(false);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scTable = new JScrollPane(table);
        scTable.putClientProperty("FlatLaf.style", "arc: 15; border: 1,1,1,1, #E8ECEF");
        pnlTableContainer.add(scTable, BorderLayout.CENTER);

        JButton btnXoa = new JButton("Xóa Thông Báo Được Chọn");
        btnXoa.setPreferredSize(new Dimension(0, 40));
        btnXoa.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnXoa.setBackground(new Color(220, 53, 69));
        btnXoa.setForeground(Color.WHITE);
        btnXoa.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnXoa.putClientProperty("FlatLaf.style", "arc: 10");
        btnXoa.addActionListener(e -> xoaThongBao());
        pnlTableContainer.add(btnXoa, BorderLayout.SOUTH);

        add(pnlTableContainer, BorderLayout.CENTER);
    }

    private void dangThongBao() {
        String tieuDe = txtTieuDe.getText().trim();
        String noiDung = txtNoiDung.getText().trim();
        if (tieuDe.isEmpty() || noiDung.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (dal.insertThongBao(tieuDe, noiDung, tk.getUserName())) {
            JOptionPane.showMessageDialog(this, "Thông báo đã được gửi đến toàn bộ độc giả!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            txtTieuDe.setText("");
            txtNoiDung.setText("");
            loadData();
        }
    }

    private void xoaThongBao() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một thông báo để xóa!");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Xác nhận xóa thông báo này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            String tieuDe = model.getValueAt(row, 0).toString();
            Timestamp ngayDang = (Timestamp) model.getValueAt(row, 2);
            if (dal.deleteThongBao(tieuDe, ngayDang)) {
                loadData();
            }
        }
    }

    private void loadData() {
        model.setRowCount(0);
        ArrayList<Object[]> list = dal.getLichSuThongBao();
        for (Object[] obj : list) {
            model.addRow(obj);
        }
    }
}