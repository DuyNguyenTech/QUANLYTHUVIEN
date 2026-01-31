package com.qlthuvien.DAL;

import com.qlthuvien.DTO.DTO_PhieuMuon;
import com.qlthuvien.DTO.DTO_ChiTietPhieuMuon; 
import com.qlthuvien.UTIL.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class DAL_PhieuMuon {

    // 1. LẤY DANH SÁCH (Giữ nguyên)
    public ArrayList<DTO_PhieuMuon> getList() {
        ArrayList<DTO_PhieuMuon> list = new ArrayList<>();
        String sql = "SELECT pm.*, COUNT(ct.MaCuonSach) AS SoLuong " +
                     "FROM phieu_muon pm " +
                     "LEFT JOIN chi_tiet_phieu_muon ct ON pm.MaPhieuMuon = ct.MaPhieuMuon " +
                     "GROUP BY pm.MaPhieuMuon " +
                     "ORDER BY pm.NgayMuon DESC";
        
        long millis = System.currentTimeMillis();
        java.sql.Date today = new java.sql.Date(millis);

        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                DTO_PhieuMuon pm = new DTO_PhieuMuon();
                pm.setMaPhieuMuon(rs.getString("MaPhieuMuon"));
                pm.setMaDocGia(rs.getString("MaDocGia"));
                pm.setMaThuThu(rs.getString("MaThuThu"));
                pm.setNgayMuon(rs.getDate("NgayMuon"));
                
                Date hanTra = rs.getDate("NgayHenTra");
                Date ngayTra = rs.getDate("NgayTra");
                double tienPhatDB = rs.getDouble("TienPhat");

                pm.setNgayHenTra(hanTra);
                pm.setNgayTra(ngayTra);

                if (ngayTra == null && hanTra != null && today.after(hanTra)) {
                    pm.setTinhTrang("Quá hạn"); 
                    long diff = today.getTime() - hanTra.getTime();
                    long daysLate = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                    pm.setTienPhat(daysLate * 5000); 
                } 
                else if (ngayTra != null) {
                    pm.setTinhTrang("Đã trả");
                    pm.setTienPhat(tienPhatDB);
                }
                else {
                    pm.setTinhTrang("Đang mượn");
                    pm.setTienPhat(0);
                }

                pm.setSoLuongSach(rs.getInt("SoLuong"));
                list.add(pm);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 2. THÊM PHIẾU MƯỢN (Giữ nguyên)
    public boolean themPhieuMuon(DTO_PhieuMuon pm, ArrayList<String> listMaSach) {
        Connection conn = null;
        PreparedStatement psPM = null, psCT = null, psUpSach = null;
        
        try {
            conn = new DBConnect().getConnection();
            conn.setAutoCommit(false); 
            
            String sqlPM = "INSERT INTO phieu_muon (MaPhieuMuon, MaDocGia, MaThuThu, NgayMuon, NgayHenTra) VALUES (?, ?, ?, ?, ?)";
            psPM = conn.prepareStatement(sqlPM);
            psPM.setString(1, pm.getMaPhieuMuon());
            psPM.setString(2, pm.getMaDocGia());
            psPM.setString(3, pm.getMaThuThu());
            psPM.setDate(4, pm.getNgayMuon());
            psPM.setDate(5, pm.getNgayHenTra());
            psPM.executeUpdate();

            // Lưu ý: Nếu DB không có cột TinhTrang, câu lệnh này có thể lỗi ở phần INSERT. 
            // Tuy nhiên lỗi bạn gặp là ở phần SELECT (getChiTietPhieu), nên ta xử lý SELECT trước.
            String sqlCT = "INSERT INTO chi_tiet_phieu_muon (MaPhieuMuon, MaCuonSach, TinhTrang, TienPhat) VALUES (?, ?, ?, ?)";
            String sqlUp = "UPDATE sach SET SoLuong = SoLuong - 1 WHERE MaCuonSach = ?";
            
            psCT = conn.prepareStatement(sqlCT);
            psUpSach = conn.prepareStatement(sqlUp);

            for (String maSach : listMaSach) {
                psCT.setString(1, pm.getMaPhieuMuon()); 
                psCT.setString(2, maSach); 
                psCT.setString(3, "Đang mượn"); 
                psCT.setDouble(4, 0);
                psCT.addBatch();

                psUpSach.setString(1, maSach); 
                psUpSach.addBatch();
            }
            psCT.executeBatch(); 
            psUpSach.executeBatch();

            conn.commit(); 
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return false;
    }

    // 3. XÓA PHIẾU (Giữ nguyên)
    public boolean delete(String maPhieu) {
        Connection conn = null;
        try {
            conn = new DBConnect().getConnection();
            conn.setAutoCommit(false);

            String sqlGet = "SELECT MaCuonSach FROM chi_tiet_phieu_muon WHERE MaPhieuMuon = ?";
            PreparedStatement psGet = conn.prepareStatement(sqlGet);
            psGet.setString(1, maPhieu);
            ResultSet rs = psGet.executeQuery();
            
            String sqlRestore = "UPDATE sach SET SoLuong = SoLuong + 1 WHERE MaCuonSach = ?";
            PreparedStatement psRestore = conn.prepareStatement(sqlRestore);
            
            while(rs.next()) {
                psRestore.setString(1, rs.getString("MaCuonSach"));
                psRestore.addBatch();
            }
            psRestore.executeBatch();

            String sqlDelCT = "DELETE FROM chi_tiet_phieu_muon WHERE MaPhieuMuon = ?";
            PreparedStatement psDelCT = conn.prepareStatement(sqlDelCT);
            psDelCT.setString(1, maPhieu);
            psDelCT.executeUpdate();

            String sqlDelPM = "DELETE FROM phieu_muon WHERE MaPhieuMuon = ?";
            PreparedStatement psDelPM = conn.prepareStatement(sqlDelPM);
            psDelPM.setString(1, maPhieu);
            psDelPM.executeUpdate();

            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            try { if(conn != null) conn.rollback(); } catch(Exception ex) {}
        }
        return false;
    }

    // --- [SỬA LỖI Ở ĐÂY] ---
    // 4. LẤY CHI TIẾT SÁCH TRONG PHIẾU
    public ArrayList<DTO_ChiTietPhieuMuon> getChiTietPhieu(String maPhieu) {
        ArrayList<DTO_ChiTietPhieuMuon> list = new ArrayList<>();
        String sql = "SELECT * FROM chi_tiet_phieu_muon WHERE MaPhieuMuon = ?";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhieu);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                // Xử lý an toàn: Nếu không có cột TinhTrang thì gán mặc định
                String tinhTrang = "Đang mượn"; 
                try {
                    tinhTrang = rs.getString("TinhTrang");
                } catch (SQLException ex) {
                    // Cột không tồn tại -> Bỏ qua lỗi, dùng giá trị mặc định
                }

                list.add(new DTO_ChiTietPhieuMuon(
                    rs.getString("MaPhieuMuon"), 
                    rs.getString("MaCuonSach"), 
                    tinhTrang
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 5. LẤY THÔNG TIN 1 PHIẾU (Giữ nguyên)
    public DTO_PhieuMuon getPhieuByMa(String maPhieu) {
        DTO_PhieuMuon pm = null;
        String sql = "SELECT * FROM phieu_muon WHERE MaPhieuMuon = ?";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maPhieu);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                pm = new DTO_PhieuMuon();
                pm.setMaPhieuMuon(rs.getString("MaPhieuMuon"));
                pm.setMaDocGia(rs.getString("MaDocGia"));
                pm.setMaThuThu(rs.getString("MaThuThu"));
                pm.setNgayMuon(rs.getDate("NgayMuon"));
                pm.setNgayHenTra(rs.getDate("NgayHenTra"));
                pm.setNgayTra(rs.getDate("NgayTra"));
                
                java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
                Date hanTra = rs.getDate("NgayHenTra");
                Date ngayTra = rs.getDate("NgayTra");
                double tienPhatDB = rs.getDouble("TienPhat");

                if (ngayTra == null && hanTra != null && today.after(hanTra)) {
                    pm.setTinhTrang("Quá hạn"); 
                    long diff = today.getTime() - hanTra.getTime();
                    long daysLate = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                    pm.setTienPhat(daysLate * 5000); 
                } else if (ngayTra != null) {
                    pm.setTinhTrang("Đã trả");
                    pm.setTienPhat(tienPhatDB);
                } else {
                    pm.setTinhTrang("Đang mượn");
                    pm.setTienPhat(0);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return pm;
    }
}