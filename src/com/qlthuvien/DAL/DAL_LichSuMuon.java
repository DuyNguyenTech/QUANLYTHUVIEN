package com.qlthuvien.DAL;

import com.qlthuvien.UTIL.DBConnect;
import java.sql.*;
import java.util.ArrayList;

public class DAL_LichSuMuon {

    // Hàm lấy lịch sử mượn theo Mã Độc Giả
    public ArrayList<Object[]> getLichSuMuon(String maDocGia) {
        ArrayList<Object[]> list = new ArrayList<>();
        
        // [SỬA LỖI QUAN TRỌNG]
        // NgayTra, TinhTrang, TienPhat lấy từ bảng pm (Phiếu Mượn) chứ không phải ctp (Chi Tiết)
        String sql = "SELECT pm.MaPhieuMuon, s.TenSach, pm.NgayMuon, pm.NgayHenTra, " + 
                     "pm.NgayTra, pm.TinhTrang, pm.TienPhat " + 
                     "FROM phieu_muon pm " +
                     "JOIN chi_tiet_phieu_muon ctp ON pm.MaPhieuMuon = ctp.MaPhieuMuon " +
                     "JOIN sach s ON ctp.MaCuonSach = s.MaCuonSach " + 
                     "WHERE pm.MaDocGia = ? " +
                     "ORDER BY pm.NgayMuon DESC";

        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maDocGia);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                // Xử lý ngày trả
                Date ngayTra = rs.getDate("NgayTra");
                String strNgayTra = (ngayTra != null) ? ngayTra.toString() : "Chưa trả";
                
                // Xử lý tiền phạt
                double tienPhat = rs.getDouble("TienPhat");
                String strTienPhat = (tienPhat > 0) ? String.format("%,.0f VNĐ", tienPhat) : "-";

                // Xử lý ngày hẹn trả
                Date ngayHenTra = rs.getDate("NgayHenTra");
                String strHenTra = (ngayHenTra != null) ? ngayHenTra.toString() : "";

                // Thêm dòng dữ liệu
                list.add(new Object[] {
                    rs.getString("MaPhieuMuon"),
                    rs.getString("TenSach"),
                    rs.getDate("NgayMuon"),
                    strHenTra,
                    strNgayTra,
                    rs.getString("TinhTrang"), 
                    strTienPhat
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}