package com.qlthuvien.GUI;

import com.qlthuvien.DAL.DAL_PhieuMuon;
import com.qlthuvien.DTO.DTO_PhieuMuon;
import com.qlthuvien.DTO.DTO_Sach;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.ArrayList;

public class GUI_DialogGioSach extends JDialog {

    private JTable table;
    private DefaultTableModel model;
    private ArrayList<DTO_Sach> listGioSach;
    private GUI_TraCuuSach parentGUI; // Biến lưu cha
    private String maDG;
    private boolean daGuiYeuCau = false;
    private DAL_PhieuMuon dalPM = new DAL_PhieuMuon();

    // Constructor nhận thêm GUI_TraCuuSach
    public GUI_DialogGioSach(Window parentFrame, GUI_TraCuuSach parentGUI, ArrayList<DTO_Sach> list, String maDG) {
        super(parentFrame, "GIỎ SÁCH CỦA BẠN", ModalityType.APPLICATION_MODAL);
        this.parentGUI = parentGUI; // Lưu lại
        this.listGioSach = list;
        this.maDG = maDG;
        initUI();
        loadData();
    }

    private void initUI() {
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Table ---
        String[] cols = {"Mã Sách", "Tên Sách", "Tác Giả", "Thể Loại", "Năm XB"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(30);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- Panel Bot ---
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBot.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblTong = new JLabel("Tổng: " + listGioSach.size() + " cuốn   ");
        
        JButton btnXoa = new JButton("Xóa khỏi giỏ");
        JButton btnGui = new JButton("Gửi Yêu Cầu");
        btnGui.setBackground(new Color(50, 115, 220));
        btnGui.setForeground(Color.WHITE);

        pnlBot.add(lblTong);
        pnlBot.add(btnXoa);
        pnlBot.add(btnGui);
        add(pnlBot, BorderLayout.SOUTH);

        // --- Sự kiện XÓA ---
        btnXoa.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sách để xóa!");
                return;
            }
            
            // Xóa khỏi list
            listGioSach.remove(row);
            
            // Cập nhật giao diện Dialog
            loadData();
            lblTong.setText("Tổng: " + listGioSach.size() + " cuốn   ");
            
            // [QUAN TRỌNG] Gọi cha cập nhật nút ngay lập tức
            if (parentGUI != null) {
                parentGUI.capNhatSoLuongGio();
            }
            
            JOptionPane.showMessageDialog(this, "Đã xóa khỏi giỏ!");
        });

        // --- Sự kiện GỬI ---
        btnGui.addActionListener(e -> xuLyMuon());
    }

    private void loadData() {
        model.setRowCount(0);
        for (DTO_Sach s : listGioSach) {
            model.addRow(new Object[]{s.getMaCuonSach(), s.getTenSach(), s.getTacGia(), s.getMaTheLoai(), s.getNamXuatBan()});
        }
    }

    private void xuLyMuon() {
        if(listGioSach.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ sách trống!"); return;
        }
        
        DTO_PhieuMuon pm = new DTO_PhieuMuon();
        pm.setMaPhieuMuon("PM" + System.currentTimeMillis()/1000);
        pm.setMaDocGia(maDG);
        pm.setMaThuThu("ONLINE"); 
        pm.setNgayMuon(new Date(System.currentTimeMillis()));
        pm.setNgayHenTra(new Date(System.currentTimeMillis() + (7L * 24 * 60 * 60 * 1000)));
        
        ArrayList<String> dsMa = new ArrayList<>();
        for(DTO_Sach s : listGioSach) dsMa.add(s.getMaCuonSach());
        
        if(dalPM.themPhieuMuon(pm, dsMa)) {
            JOptionPane.showMessageDialog(this, "Gửi yêu cầu thành công! Vui lòng chờ duyệt.");
            daGuiYeuCau = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Thất bại! Có lỗi xảy ra.");
        }
    }
    
    public boolean isDaGuiYeuCau() {
        return daGuiYeuCau;
    }
}