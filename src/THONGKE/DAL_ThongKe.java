package THONGKE;

import java.sql.*;
import java.util.*;
import CHUNG.DBConnect;

public class DAL_ThongKe {

    // ==========================================================================
    // PHẦN 1: CÁC HÀM CHO DASHBOARD (ADMIN & ĐỘC GIẢ)
    // ==========================================================================

    public Map<String, Integer> getDataPieChart() {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT tl.TenTheLoai, COUNT(s.MaCuonSach) as SoLuong " +
                     "FROM sach s JOIN the_loai tl ON s.MaTheLoai = tl.MaTheLoai " +
                     "GROUP BY tl.TenTheLoai ORDER BY SoLuong DESC LIMIT 5";
        try (Connection conn = new DBConnect().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getString("TenTheLoai"), rs.getInt("SoLuong"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return map;
    }

    public ArrayList<Object[]> getMuonTraGanDay() {
        ArrayList<Object[]> list = new ArrayList<>();
        String sql = "SELECT PM.MaPhieuMuon, DG.TenDocGia, PM.NgayMuon, PM.TinhTrang " +
                     "FROM phieu_muon PM JOIN doc_gia DG ON PM.MaDocGia = DG.MaDocGia " +
                     "ORDER BY PM.NgayMuon DESC LIMIT 10";
        try (Connection conn = new DBConnect().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString(1), rs.getString(2),
                    rs.getDate(3) != null ? rs.getDate(3).toString() : "",
                    rs.getString(4)
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /**
     * Lấy thông báo mới nhất từ bảng thong_bao để hiển thị lên Dashboard Độc giả
     */
    public String getThongBaoMoiNhat() {
        StringBuilder sb = new StringBuilder();
        // Truy vấn dựa trên bảng thong_bao trong file SQL của bạn
        String sql = "SELECT TieuDe, NoiDung FROM thong_bao ORDER BY NgayDang DESC LIMIT 2";
        try (Connection conn = new DBConnect().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                sb.append("• ").append(rs.getString("TieuDe")).append(": ")
                  .append(rs.getString("NoiDung")).append("\n\n");
            }
        } catch (Exception e) { return "Chào mừng bạn đến với thư viện!"; }
        return sb.length() > 0 ? sb.toString() : "Hiện chưa có thông báo mới.";
    }

    /**
     * Thêm thông báo mới (Dành cho Admin và Staff truyền tin xuống Độc giả)
     */
    public boolean insertThongBao(String tieuDe, String noiDung, String nguoiDang) {
        String sql = "INSERT INTO thong_bao (TieuDe, NoiDung, NguoiDang) VALUES (?, ?, ?)";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tieuDe);
            ps.setString(2, noiDung);
            ps.setString(3, nguoiDang);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Object[]> getSachGoiY(String maDG) {
        ArrayList<Object[]> list = new ArrayList<>();
        String sql = "SELECT TenSach, TacGia FROM sach WHERE MaTheLoai = (" +
                     "SELECT s.MaTheLoai FROM phieu_muon pm " +
                     "JOIN chi_tiet_phieu_muon ct ON pm.MaPhieuMuon = ct.MaPhieuMuon " +
                     "JOIN sach s ON ct.MaCuonSach = s.MaCuonSach " +
                     "WHERE pm.MaDocGia = ? GROUP BY s.MaTheLoai ORDER BY COUNT(*) DESC LIMIT 1) LIMIT 3";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maDG);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{ rs.getString(1), rs.getString(2) });
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // ==========================================================================
    // PHẦN 2: CÁC HÀM CHO 3 DIALOG THỐNG KÊ CHI TIẾT
    // ==========================================================================

    public ArrayList<DTO_ThongKeSach> getListSachDangMuon() {
        ArrayList<DTO_ThongKeSach> list = new ArrayList<>();
        String sql = "SELECT MaCuonSach, TenSach FROM sach WHERE MaCuonSach IN (SELECT MaCuonSach FROM chi_tiet_phieu_muon ct JOIN phieu_muon pm ON ct.MaPhieuMuon = pm.MaPhieuMuon WHERE pm.NgayTra IS NULL)";
        try (Connection conn = new DBConnect().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new DTO_ThongKeSach(rs.getString(1), rs.getString(2), "Đang mượn"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public ArrayList<DTO_ThongKeSach> getListSachTrongKho() {
        ArrayList<DTO_ThongKeSach> list = new ArrayList<>();
        String sql = "SELECT MaCuonSach, TenSach FROM sach WHERE SoLuong > 0";
        try (Connection conn = new DBConnect().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new DTO_ThongKeSach(rs.getString(1), rs.getString(2), "Trong kho"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public ArrayList<MUONTRA.DTO_PhieuMuon> getListMuonTra(String option) {
        ArrayList<MUONTRA.DTO_PhieuMuon> list = new ArrayList<>();
        String sql = "SELECT * FROM phieu_muon WHERE 1=1 ";
        
        if (option.equals("DAY")) {
            sql += " AND DATE(NgayMuon) = CURDATE()";
        } else if (option.equals("WEEK")) {
            sql += " AND YEARWEEK(NgayMuon) = YEARWEEK(CURDATE())";
        } else if (option.equals("MONTH")) {
            sql += " AND MONTH(NgayMuon) = MONTH(CURDATE()) AND YEAR(NgayMuon) = YEAR(CURDATE())";
        } else if (option.equals("CURRENT")) {
            sql += " AND NgayTra IS NULL";
        }

        try (Connection conn = new DBConnect().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                MUONTRA.DTO_PhieuMuon pm = new MUONTRA.DTO_PhieuMuon();
                pm.setMaPhieuMuon(rs.getString("MaPhieuMuon"));
                pm.setNgayMuon(rs.getDate("NgayMuon"));
                pm.setNgayTra(rs.getDate("NgayTra"));
                pm.setNgayHenTra(rs.getDate("NgayHenTra")); 
                pm.setMaDocGia(rs.getString("MaDocGia"));
                pm.setTinhTrang(rs.getString("TinhTrang"));
                pm.setTienPhat(rs.getDouble("TienPhat"));
                list.add(pm);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    
    public ArrayList<MUONTRA.DTO_PhieuMuon> getListViPham() {
        ArrayList<MUONTRA.DTO_PhieuMuon> list = new ArrayList<>();
        String sql = "SELECT * FROM phieu_muon WHERE TinhTrang LIKE N'%Quá hạn%' OR TienPhat > 0";
        try (Connection conn = new DBConnect().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                MUONTRA.DTO_PhieuMuon pm = new MUONTRA.DTO_PhieuMuon();
                pm.setMaPhieuMuon(rs.getString("MaPhieuMuon"));
                pm.setMaDocGia(rs.getString("MaDocGia"));
                pm.setNgayMuon(rs.getDate("NgayMuon"));
                pm.setNgayHenTra(rs.getDate("NgayHenTra"));
                pm.setTinhTrang(rs.getString("TinhTrang"));
                pm.setTienPhat(rs.getDouble("TienPhat"));
                list.add(pm);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // ==========================================================================
    // PHẦN 3: CÁC HÀM TIỆN ÍCH CHUNG
    // ==========================================================================

    public int getTongDauSach() { return getCount("SELECT COUNT(*) FROM sach"); }
    public int getTongPhieuMuon() { return getCount("SELECT COUNT(*) FROM phieu_muon"); }
    public int getTongDocGia() { return getCount("SELECT COUNT(*) FROM doc_gia"); }
    public int getTongViPham() { return getCount("SELECT COUNT(*) FROM phieu_muon WHERE TinhTrang LIKE N'%Quá hạn%' OR TienPhat > 0"); }

    public int getCountGlobal(String sql) { return getCount(sql); }

    private int getCount(String sql) {
        try (Connection conn = new DBConnect().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { }
        return 0;
    }

    // ==========================================================================
    // PHẦN 4: CODE MỚI BỔ SUNG - QUẢN LÝ THÔNG BÁO
    // ==========================================================================

    public ArrayList<Object[]> getLichSuThongBao() {
        ArrayList<Object[]> list = new ArrayList<>();
        String sql = "SELECT TieuDe, NoiDung, NgayDang, NguoiDang FROM thong_bao ORDER BY NgayDang DESC";
        try (Connection conn = new DBConnect().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("TieuDe"), 
                    rs.getString("NoiDung"), 
                    rs.getTimestamp("NgayDang"), 
                    rs.getString("NguoiDang")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean deleteThongBao(String tieuDe, Timestamp ngayDang) {
        String sql = "DELETE FROM thong_bao WHERE TieuDe = ? AND NgayDang = ?";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tieuDe);
            ps.setTimestamp(2, ngayDang);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}