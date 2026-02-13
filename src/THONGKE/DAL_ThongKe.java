package THONGKE;

import java.sql.*;
import java.util.*;
import CHUNG.DBConnect;
import MUONTRA.DTO_PhieuMuon;

public class DAL_ThongKe {

    // Helper: Hàm dùng nội bộ để đếm số lượng nhanh
    private int getCount(String sql) {
        try (Connection conn = new DBConnect().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public int getCountGlobal(String sql) {
        return getCount(sql);
    }

    // ==========================================================================
    // PHẦN 1: DASHBOARD & BIỂU ĐỒ
    // ==========================================================================

    public Map<String, Integer> getDataPieChart() {
        Map<String, Integer> map = new LinkedHashMap<>();
     // Sửa trong DAL_ThongKe.java
        String sql = "SELECT tl.TenTheLoai, COUNT(s.MaCuonSach) as SoLuong " +
                     "FROM the_loai tl " +
                     "LEFT JOIN sach s ON tl.MaTheLoai = s.MaTheLoai " +
                     "GROUP BY tl.TenTheLoai HAVING SoLuong > 0 " +
                     "ORDER BY SoLuong DESC"; // Đã bỏ LIMIT 5
//        String sql = "SELECT tl.TenTheLoai, COUNT(s.MaCuonSach) as SoLuong " +
//                     "FROM the_loai tl " +
//                     "LEFT JOIN sach s ON tl.MaTheLoai = s.MaTheLoai " +
//                     "GROUP BY tl.TenTheLoai HAVING SoLuong > 0 " +
//                     "ORDER BY SoLuong DESC LIMIT 5";
        try (Connection conn = new DBConnect().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getString("TenTheLoai"), rs.getInt("SoLuong"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return map;
    }

    public int getTongDauSach() { return getCount("SELECT SUM(SoLuong) FROM sach"); }
// // Sửa lại hàm này trong DAL_ThongKe.java
//    public int getTongPhieuMuon() { 
//        // Dùng SUM(SoLuong) để đếm tổng số cuốn sách, LIKE để nhận diện tiếng Việt chính xác
//        return getCount("SELECT SUM(SoLuong) FROM phieu_muon WHERE TinhTrang LIKE N'%Đang mượn%'"); 
//    }
    public int getTongPhieuMuon() { return getCount("SELECT COUNT(*) FROM phieu_muon WHERE TinhTrang = N'Đang mượn'"); }
    public int getTongDocGia() { return getCount("SELECT COUNT(*) FROM doc_gia"); }
 // Sửa lại hàm này trong DAL_ThongKe.java
    public int getTongViPham() { 
        // Đếm tất cả các dòng có tình trạng chứa chữ 'Quá hạn'
        return getCount("SELECT COUNT(*) FROM phieu_muon WHERE TinhTrang LIKE N'%Quá hạn%'"); 
    }
//    public int getTongViPham() { return getCount("SELECT COUNT(*) FROM phieu_muon WHERE TinhTrang = N'Quá hạn'"); }

    // ==========================================================================
    // PHẦN 2: LOGIC LẤY DANH SÁCH MƯỢN TRẢ & VI PHẠM
    // ==========================================================================

    public ArrayList<DTO_PhieuMuon> getListViPham() {
        return getListMuonTra("VI_PHAM_SQL_CUSTOM"); 
    }

    public ArrayList<DTO_PhieuMuon> getListMuonTra(String option) {
        ArrayList<DTO_PhieuMuon> list = new ArrayList<>();
        String sql = "SELECT * FROM phieu_muon WHERE 1=1";
        
        if (option.equals("DAY")) sql += " AND DATE(NgayMuon) = CURDATE()";
        else if (option.equals("WEEK")) sql += " AND YEARWEEK(NgayMuon) = YEARWEEK(CURDATE())";
        else if (option.equals("MONTH")) sql += " AND MONTH(NgayMuon) = MONTH(CURDATE())";
        else if (option.equals("CURRENT")) sql += " AND TinhTrang = N'Đang mượn'";
        else if (option.equals("VI_PHAM_SQL_CUSTOM")) sql += " AND TinhTrang = N'Quá hạn'"; 

        try (Connection conn = new DBConnect().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                DTO_PhieuMuon pm = new DTO_PhieuMuon();
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

    // ==========================================================================
    // PHẦN 3: QUẢN LÝ THÔNG BÁO
    // ==========================================================================

    public String getThongBaoMoiNhat() {
        StringBuilder sb = new StringBuilder();
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

    public ArrayList<Object[]> getLichSuThongBao() {
        ArrayList<Object[]> list = new ArrayList<>();
        String sql = "SELECT TieuDe, NoiDung, NgayDang, NguoiDang FROM thong_bao ORDER BY NgayDang DESC";
        try (Connection conn = new DBConnect().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Object[]{ rs.getString(1), rs.getString(2), rs.getTimestamp(3), rs.getString(4) });
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean insertThongBao(String tieuDe, String noiDung, String nguoiDang) {
        String sql = "INSERT INTO thong_bao (TieuDe, NoiDung, NgayDang, NguoiDang) VALUES (?, ?, CURRENT_TIMESTAMP, ?)";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tieuDe); ps.setString(2, noiDung); ps.setString(3, nguoiDang);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }

    public boolean deleteThongBao(String tieuDe, java.sql.Timestamp ngayDang) {
        String sql = "DELETE FROM thong_bao WHERE TieuDe = ? AND NgayDang = ?";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tieuDe); ps.setTimestamp(2, ngayDang);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }

    // ==========================================================================
    // PHẦN 4: CHI TIẾT SÁCH & GỢI Ý (FIX LỖI GẠCH ĐỎ TRANG CHỦ)
    // ==========================================================================

    // Hàm phục vụ bảng Hoạt động gần đây cho Thủ thư
    
 // Sửa trong hàm getMuonTraGanDay()
    public ArrayList<Object[]> getMuonTraGanDay() {
        ArrayList<Object[]> list = new ArrayList<>();
        String sql = "SELECT MaPhieuMuon, MaDocGia, NgayMuon, TinhTrang " +
                     "FROM phieu_muon " +
                     "ORDER BY NgayMuon DESC LIMIT 10"; 
        // Nếu muốn hiện tên thay vì mã DG, hãy chắc chắn bảng doc_gia có MaDocGia khớp 100%
        try (Connection conn = new DBConnect().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) { 
                list.add(new Object[]{ rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4) }); 
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    
//    public ArrayList<Object[]> getMuonTraGanDay() {
//        ArrayList<Object[]> list = new ArrayList<>();
//        String sql = "SELECT pm.MaPhieuMuon, dg.HoTen, pm.NgayMuon, pm.TinhTrang " +
//                     "FROM phieu_muon pm LEFT JOIN doc_gia dg ON pm.MaDocGia = dg.MaDocGia " +
//                     "ORDER BY pm.NgayMuon DESC LIMIT 10";
//        try (Connection conn = new DBConnect().getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//            while (rs.next()) { 
//                list.add(new Object[]{ rs.getString(1), rs.getString(2), rs.getDate(3), rs.getString(4) }); 
//            }
//        } catch (Exception e) { e.printStackTrace(); }
//        return list;
//    }

    // Hàm phục vụ gợi ý sách cho Độc giả
    public ArrayList<Object[]> getSachGoiY(String maDocGia) {
        ArrayList<Object[]> list = new ArrayList<>();
        // Lấy ngẫu nhiên 3 cuốn sách để gợi ý
        String sql = "SELECT TenSach, TacGia FROM sach ORDER BY RAND() LIMIT 3";
        try (Connection conn = new DBConnect().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) { 
                list.add(new Object[]{ rs.getString(1), rs.getString(2) }); 
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public ArrayList<DTO_ThongKeSach> getListSachDangMuon() {
        ArrayList<DTO_ThongKeSach> list = new ArrayList<>();
        String sql = "SELECT DISTINCT s.MaCuonSach, s.TenSach, pm.TinhTrang FROM sach s JOIN chi_tiet_phieu_muon ct ON s.MaCuonSach = ct.MaCuonSach JOIN phieu_muon pm ON ct.MaPhieuMuon = pm.MaPhieuMuon WHERE pm.TinhTrang = N'Đang mượn' OR pm.TinhTrang = N'Quá hạn'";
        try (Connection conn = new DBConnect().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) { list.add(new DTO_ThongKeSach(rs.getString(1), rs.getString(2), rs.getString(3))); }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public ArrayList<DTO_ThongKeSach> getListSachTrongKho() {
        ArrayList<DTO_ThongKeSach> list = new ArrayList<>();
        String sql = "SELECT MaCuonSach, TenSach FROM sach";
        try (Connection conn = new DBConnect().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) { list.add(new DTO_ThongKeSach(rs.getString(1), rs.getString(2), "Sẵn sàng")); }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}




//package THONGKE;
//
//import java.sql.*;
//import java.util.*;
//import CHUNG.DBConnect;
//
//public class DAL_ThongKe {
//
//    // ==========================================================================
//    // PHẦN 1: CÁC HÀM CHO DASHBOARD
//    // ==========================================================================
//
//    public Map<String, Integer> getDataPieChart() {
//        Map<String, Integer> map = new LinkedHashMap<>();
//        String sql = "SELECT tl.TenTheLoai, SUM(s.SoLuong) as TongSo " +
//                     "FROM the_loai tl " +
//                     "LEFT JOIN sach s ON tl.MaTheLoai = s.MaTheLoai " +
//                     "GROUP BY tl.TenTheLoai HAVING TongSo > 0 " +
//                     "ORDER BY TongSo DESC LIMIT 5";
//        try (Connection conn = new DBConnect().getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//            while (rs.next()) {
//                map.put(rs.getString("TenTheLoai"), rs.getInt("TongSo"));
//            }
//        } catch (Exception e) { e.printStackTrace(); }
//        return map;
//    }
//
//    // ==========================================================================
//    // PHẦN 2: CÁC HÀM PHỤC VỤ CHO GUI_DialogThongKeMuonTra (SỬA LỖI GẠCH ĐỎ)
//    // ==========================================================================
//
//    /**
//     * Hàm quan trọng nhất để fix lỗi gạch đỏ ở GUI
//     */
//    public ArrayList<MUONTRA.DTO_PhieuMuon> getListMuonTra(String option) {
//        ArrayList<MUONTRA.DTO_PhieuMuon> list = new ArrayList<>();
//        String sql = "SELECT * FROM phieu_muon WHERE 1=1 ";
//
//        // Lọc theo điều kiện thời gian từ GUI truyền vào
//        if (option.equals("DAY")) {
//            sql += " AND DATE(NgayMuon) = CURDATE()";
//        } else if (option.equals("WEEK")) {
//            sql += " AND YEARWEEK(NgayMuon) = YEARWEEK(CURDATE())";
//        } else if (option.equals("MONTH")) {
//            sql += " AND MONTH(NgayMuon) = MONTH(CURDATE()) AND YEAR(NgayMuon) = YEAR(CURDATE())";
//        } else if (option.equals("CURRENT")) {
//            sql += " AND NgayTra IS NULL";
//        }
//
//        try (Connection conn = new DBConnect().getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//            while (rs.next()) {
//                MUONTRA.DTO_PhieuMuon pm = new MUONTRA.DTO_PhieuMuon();
//                pm.setMaPhieuMuon(rs.getString("MaPhieuMuon"));
//                pm.setNgayMuon(rs.getDate("NgayMuon"));
//                pm.setNgayTra(rs.getDate("NgayTra"));
//                pm.setNgayHenTra(rs.getDate("NgayHenTra")); 
//                pm.setMaDocGia(rs.getString("MaDocGia"));
//                pm.setTienPhat(rs.getDouble("TienPhat"));
//                list.add(pm);
//            }
//        } catch (Exception e) { e.printStackTrace(); }
//        return list;
//    }
//
//    // ==========================================================================
//    // PHẦN 3: CÁC HÀM TIỆN ÍCH DASHBOARD
//    // ==========================================================================
//
//    public int getTongDauSach() { return getCount("SELECT SUM(SoLuong) FROM sach"); }
//    public int getTongPhieuMuon() { return getCount("SELECT COUNT(*) FROM phieu_muon WHERE NgayTra IS NULL AND MaThuThu <> 'ONLINE'"); }
//    public int getTongDocGia() { return getCount("SELECT COUNT(*) FROM doc_gia"); }
//    public int getTongViPham() { return getCount("SELECT COUNT(*) FROM phieu_muon WHERE NgayTra IS NULL AND NgayHenTra < CURDATE()"); }
//
//    private int getCount(String sql) {
//        try (Connection conn = new DBConnect().getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//            if (rs.next()) return rs.getInt(1);
//        } catch (Exception e) { }
//        return 0;
//    }
//
//    // ... Giữ lại các hàm getThongBaoMoiNhat, getSachGoiY, getListViPham của bạn bên dưới ...
//    public String getThongBaoMoiNhat() {
//        StringBuilder sb = new StringBuilder();
//        String sql = "SELECT TieuDe, NoiDung FROM thong_bao ORDER BY NgayDang DESC LIMIT 2";
//        try (Connection conn = new DBConnect().getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//            while (rs.next()) {
//                sb.append("• ").append(rs.getString("TieuDe")).append(": ")
//                  .append(rs.getString("NoiDung")).append("\n\n");
//            }
//        } catch (Exception e) { return "Chào mừng bạn đến với thư viện!"; }
//        return sb.length() > 0 ? sb.toString() : "Hiện chưa có thông báo mới.";
//    }
//    
// // Thêm vào DAL_ThongKe.java
//    public ArrayList<DTO_ThongKeSach> getListSachDangMuon() {
//        ArrayList<DTO_ThongKeSach> list = new ArrayList<>();
//        String sql = "SELECT s.MaCuonSach, s.TenSach FROM sach s " +
//                     "JOIN chi_tiet_phieu_muon ct ON s.MaCuonSach = ct.MaCuonSach " +
//                     "JOIN phieu_muon pm ON ct.MaPhieuMuon = pm.MaPhieuMuon " +
//                     "WHERE pm.NgayTra IS NULL";
//        try (Connection conn = new DBConnect().getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//            while (rs.next()) {
//                list.add(new DTO_ThongKeSach(rs.getString(1), rs.getString(2), "Đang mượn"));
//            }
//        } catch (Exception e) { e.printStackTrace(); }
//        return list;
//    }
//
//    public ArrayList<DTO_ThongKeSach> getListSachTrongKho() {
//        ArrayList<DTO_ThongKeSach> list = new ArrayList<>();
//        String sql = "SELECT MaCuonSach, TenSach FROM sach WHERE SoLuong > 0";
//        try (Connection conn = new DBConnect().getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//            while (rs.next()) {
//                list.add(new DTO_ThongKeSach(rs.getString(1), rs.getString(2), "Trong kho"));
//            }
//        } catch (Exception e) { e.printStackTrace(); }
//        return list;
//    }
//    
//    public ArrayList<MUONTRA.DTO_PhieuMuon> getListViPham() {
//        ArrayList<MUONTRA.DTO_PhieuMuon> list = new ArrayList<>();
//        // Lấy phiếu chưa trả và đã quá hạn so với ngày hiện tại
//        String sql = "SELECT * FROM phieu_muon WHERE NgayTra IS NULL AND NgayHenTra < CURDATE()";
//        try (Connection conn = new DBConnect().getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//            while (rs.next()) {
//                MUONTRA.DTO_PhieuMuon pm = new MUONTRA.DTO_PhieuMuon();
//                pm.setMaPhieuMuon(rs.getString("MaPhieuMuon"));
//                pm.setMaDocGia(rs.getString("MaDocGia"));
//                pm.setNgayMuon(rs.getDate("NgayMuon"));
//                pm.setNgayHenTra(rs.getDate("NgayHenTra"));
//                pm.setTinhTrang("Quá hạn"); 
//                pm.setTienPhat(rs.getDouble("TienPhat"));
//                list.add(pm);
//            }
//        } catch (Exception e) { e.printStackTrace(); }
//        return list;
//    }
//    
// // ==========================================================================
//    // PHẦN 4: CÁC HÀM QUẢN LÝ THÔNG BÁO (FIX LỖI GUI_QuanLyThongBao)
//    // ==========================================================================
//
//    public boolean insertThongBao(String tieuDe, String noiDung, String nguoiDang) {
//        String sql = "INSERT INTO thong_bao (TieuDe, NoiDung, NgayDang, NguoiDang) VALUES (?, ?, CURRENT_TIMESTAMP, ?)";
//        try (Connection conn = new DBConnect().getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setString(1, tieuDe);
//            ps.setString(2, noiDung);
//            ps.setString(3, nguoiDang);
//            return ps.executeUpdate() > 0;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    public boolean deleteThongBao(String tieuDe, java.sql.Timestamp ngayDang) {
//        String sql = "DELETE FROM thong_bao WHERE TieuDe = ? AND NgayDang = ?";
//        try (Connection conn = new DBConnect().getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setString(1, tieuDe);
//            ps.setTimestamp(2, ngayDang);
//            return ps.executeUpdate() > 0;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    public ArrayList<Object[]> getLichSuThongBao() {
//        ArrayList<Object[]> list = new ArrayList<>();
//        String sql = "SELECT TieuDe, NoiDung, NgayDang, NguoiDang FROM thong_bao ORDER BY NgayDang DESC";
//        try (Connection conn = new DBConnect().getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//            while (rs.next()) {
//                list.add(new Object[]{
//                    rs.getString("TieuDe"),
//                    rs.getString("NoiDung"),
//                    rs.getTimestamp("NgayDang"),
//                    rs.getString("NguoiDang")
//                });
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
//    
// // ==========================================================================
//    // PHẦN 5: BỔ SUNG CHO TRANG CHỦ (FIX LỖI GUI_TrangChu)
//    // ==========================================================================
//
//    // Lấy danh sách mượn trả gần đây cho Admin/Staff
//    public ArrayList<Object[]> getMuonTraGanDay() {
//        ArrayList<Object[]> list = new ArrayList<>();
//        String sql = "SELECT pm.MaPhieuMuon, dg.HoTen, pm.NgayMuon, " +
//                     "CASE WHEN pm.NgayTra IS NULL THEN N'Đang mượn' ELSE N'Đã trả' END as TrangThai " +
//                     "FROM phieu_muon pm " +
//                     "JOIN doc_gia dg ON pm.MaDocGia = dg.MaDocGia " +
//                     "ORDER BY pm.NgayMuon DESC LIMIT 10";
//        try (Connection conn = new DBConnect().getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//            while (rs.next()) {
//                list.add(new Object[]{
//                    rs.getString(1),
//                    rs.getString(2),
//                    rs.getDate(3),
//                    rs.getString(4)
//                });
//            }
//        } catch (Exception e) { e.printStackTrace(); }
//        return list;
//    }
//
//    // Hàm đếm dữ liệu tổng quát theo câu lệnh SQL truyền vào
//    public int getCountGlobal(String sql) {
//        try (Connection conn = new DBConnect().getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//            if (rs.next()) return rs.getInt(1);
//        } catch (Exception e) { }
//        return 0;
//    }
//
//    // Gợi ý sách cho Độc giả (Dựa trên thể loại họ hay mượn)
//    public ArrayList<Object[]> getSachGoiY(String maDocGia) {
//        ArrayList<Object[]> list = new ArrayList<>();
//        String sql = "SELECT TenSach, TacGia FROM sach " +
//                     "WHERE MaTheLoai IN (SELECT MaTheLoai FROM sach s JOIN chi_tiet_phieu_muon ct ON s.MaCuonSach = ct.MaCuonSach JOIN phieu_muon pm ON ct.MaPhieuMuon = pm.MaPhieuMuon WHERE pm.MaDocGia = ?) " +
//                     "ORDER BY RAND() LIMIT 3";
//        try (Connection conn = new DBConnect().getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setString(1, maDocGia);
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()) {
//                list.add(new Object[]{ rs.getString(1), rs.getString(2) });
//            }
//        } catch (Exception e) { }
//        // Nếu chưa mượn bao giờ, gợi ý sách mới nhất
//        if(list.isEmpty()){
//            try (Connection conn = new DBConnect().getConnection();
//                 Statement stmt = conn.createStatement();
//                 ResultSet rs = stmt.executeQuery("SELECT TenSach, TacGia FROM sach ORDER BY MaCuonSach DESC LIMIT 3")) {
//                while (rs.next()) list.add(new Object[]{ rs.getString(1), rs.getString(2) });
//            } catch (Exception e) {}
//        }
//        return list;
//    }
//    
//}



//package THONGKE;
//
//import java.sql.*;
//import java.util.*;
//import CHUNG.DBConnect;
//
//public class DAL_ThongKe {
//
//    // ==========================================================================
//    // PHẦN 1: CÁC HÀM CHO DASHBOARD (ADMIN & ĐỘC GIẢ)
//    // ==========================================================================
//
//    public Map<String, Integer> getDataPieChart() {
//        Map<String, Integer> map = new LinkedHashMap<>();
//        String sql = "SELECT tl.TenTheLoai, COUNT(s.MaCuonSach) as SoLuong " +
//                     "FROM sach s JOIN the_loai tl ON s.MaTheLoai = tl.MaTheLoai " +
//                     "GROUP BY tl.TenTheLoai ORDER BY SoLuong DESC LIMIT 5";
//        try (Connection conn = new DBConnect().getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//            while (rs.next()) {
//                map.put(rs.getString("TenTheLoai"), rs.getInt("SoLuong"));
//            }
//        } catch (Exception e) { e.printStackTrace(); }
//        return map;
//    }
//
//    public ArrayList<Object[]> getMuonTraGanDay() {
//        ArrayList<Object[]> list = new ArrayList<>();
//        String sql = "SELECT PM.MaPhieuMuon, DG.TenDocGia, PM.NgayMuon, PM.TinhTrang " +
//                     "FROM phieu_muon PM JOIN doc_gia DG ON PM.MaDocGia = DG.MaDocGia " +
//                     "ORDER BY PM.NgayMuon DESC LIMIT 10";
//        try (Connection conn = new DBConnect().getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//            while (rs.next()) {
//                list.add(new Object[]{
//                    rs.getString(1), rs.getString(2),
//                    rs.getDate(3) != null ? rs.getDate(3).toString() : "",
//                    rs.getString(4)
//                });
//            }
//        } catch (Exception e) { e.printStackTrace(); }
//        return list;
//    }
//
//    /**
//     * Lấy thông báo mới nhất từ bảng thong_bao để hiển thị lên Dashboard Độc giả
//     */
//    public String getThongBaoMoiNhat() {
//        StringBuilder sb = new StringBuilder();
//        // Truy vấn dựa trên bảng thong_bao trong file SQL của bạn
//        String sql = "SELECT TieuDe, NoiDung FROM thong_bao ORDER BY NgayDang DESC LIMIT 2";
//        try (Connection conn = new DBConnect().getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//            while (rs.next()) {
//                sb.append("• ").append(rs.getString("TieuDe")).append(": ")
//                  .append(rs.getString("NoiDung")).append("\n\n");
//            }
//        } catch (Exception e) { return "Chào mừng bạn đến với thư viện!"; }
//        return sb.length() > 0 ? sb.toString() : "Hiện chưa có thông báo mới.";
//    }
//
//    /**
//     * Thêm thông báo mới (Dành cho Admin và Staff truyền tin xuống Độc giả)
//     */
//    public boolean insertThongBao(String tieuDe, String noiDung, String nguoiDang) {
//        String sql = "INSERT INTO thong_bao (TieuDe, NoiDung, NguoiDang) VALUES (?, ?, ?)";
//        try (Connection conn = new DBConnect().getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setString(1, tieuDe);
//            ps.setString(2, noiDung);
//            ps.setString(3, nguoiDang);
//            return ps.executeUpdate() > 0;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    public ArrayList<Object[]> getSachGoiY(String maDG) {
//        ArrayList<Object[]> list = new ArrayList<>();
//        String sql = "SELECT TenSach, TacGia FROM sach WHERE MaTheLoai = (" +
//                     "SELECT s.MaTheLoai FROM phieu_muon pm " +
//                     "JOIN chi_tiet_phieu_muon ct ON pm.MaPhieuMuon = ct.MaPhieuMuon " +
//                     "JOIN sach s ON ct.MaCuonSach = s.MaCuonSach " +
//                     "WHERE pm.MaDocGia = ? GROUP BY s.MaTheLoai ORDER BY COUNT(*) DESC LIMIT 1) LIMIT 3";
//        try (Connection conn = new DBConnect().getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setString(1, maDG);
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()) {
//                list.add(new Object[]{ rs.getString(1), rs.getString(2) });
//            }
//        } catch (Exception e) { e.printStackTrace(); }
//        return list;
//    }
//
//    // ==========================================================================
//    // PHẦN 2: CÁC HÀM CHO 3 DIALOG THỐNG KÊ CHI TIẾT
//    // ==========================================================================
//
//    public ArrayList<DTO_ThongKeSach> getListSachDangMuon() {
//        ArrayList<DTO_ThongKeSach> list = new ArrayList<>();
//        String sql = "SELECT MaCuonSach, TenSach FROM sach WHERE MaCuonSach IN (SELECT MaCuonSach FROM chi_tiet_phieu_muon ct JOIN phieu_muon pm ON ct.MaPhieuMuon = pm.MaPhieuMuon WHERE pm.NgayTra IS NULL)";
//        try (Connection conn = new DBConnect().getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//            while (rs.next()) {
//                list.add(new DTO_ThongKeSach(rs.getString(1), rs.getString(2), "Đang mượn"));
//            }
//        } catch (Exception e) { e.printStackTrace(); }
//        return list;
//    }
//
//    public ArrayList<DTO_ThongKeSach> getListSachTrongKho() {
//        ArrayList<DTO_ThongKeSach> list = new ArrayList<>();
//        String sql = "SELECT MaCuonSach, TenSach FROM sach WHERE SoLuong > 0";
//        try (Connection conn = new DBConnect().getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//            while (rs.next()) {
//                list.add(new DTO_ThongKeSach(rs.getString(1), rs.getString(2), "Trong kho"));
//            }
//        } catch (Exception e) { e.printStackTrace(); }
//        return list;
//    }
//
//    public ArrayList<MUONTRA.DTO_PhieuMuon> getListMuonTra(String option) {
//        ArrayList<MUONTRA.DTO_PhieuMuon> list = new ArrayList<>();
//        String sql = "SELECT * FROM phieu_muon WHERE 1=1 ";
//        
//        if (option.equals("DAY")) {
//            sql += " AND DATE(NgayMuon) = CURDATE()";
//        } else if (option.equals("WEEK")) {
//            sql += " AND YEARWEEK(NgayMuon) = YEARWEEK(CURDATE())";
//        } else if (option.equals("MONTH")) {
//            sql += " AND MONTH(NgayMuon) = MONTH(CURDATE()) AND YEAR(NgayMuon) = YEAR(CURDATE())";
//        } else if (option.equals("CURRENT")) {
//            sql += " AND NgayTra IS NULL";
//        }
//
//        try (Connection conn = new DBConnect().getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//            while (rs.next()) {
//                MUONTRA.DTO_PhieuMuon pm = new MUONTRA.DTO_PhieuMuon();
//                pm.setMaPhieuMuon(rs.getString("MaPhieuMuon"));
//                pm.setNgayMuon(rs.getDate("NgayMuon"));
//                pm.setNgayTra(rs.getDate("NgayTra"));
//                pm.setNgayHenTra(rs.getDate("NgayHenTra")); 
//                pm.setMaDocGia(rs.getString("MaDocGia"));
//                pm.setTinhTrang(rs.getString("TinhTrang"));
//                pm.setTienPhat(rs.getDouble("TienPhat"));
//                list.add(pm);
//            }
//        } catch (Exception e) { e.printStackTrace(); }
//        return list;
//    }
//    
//    public ArrayList<MUONTRA.DTO_PhieuMuon> getListViPham() {
//        ArrayList<MUONTRA.DTO_PhieuMuon> list = new ArrayList<>();
//        String sql = "SELECT * FROM phieu_muon WHERE TinhTrang LIKE N'%Quá hạn%' OR TienPhat > 0";
//        try (Connection conn = new DBConnect().getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//            while (rs.next()) {
//                MUONTRA.DTO_PhieuMuon pm = new MUONTRA.DTO_PhieuMuon();
//                pm.setMaPhieuMuon(rs.getString("MaPhieuMuon"));
//                pm.setMaDocGia(rs.getString("MaDocGia"));
//                pm.setNgayMuon(rs.getDate("NgayMuon"));
//                pm.setNgayHenTra(rs.getDate("NgayHenTra"));
//                pm.setTinhTrang(rs.getString("TinhTrang"));
//                pm.setTienPhat(rs.getDouble("TienPhat"));
//                list.add(pm);
//            }
//        } catch (Exception e) { e.printStackTrace(); }
//        return list;
//    }
//
//    // ==========================================================================
//    // PHẦN 3: CÁC HÀM TIỆN ÍCH CHUNG
//    // ==========================================================================
//
//    public int getTongDauSach() { return getCount("SELECT COUNT(*) FROM sach"); }
//    public int getTongPhieuMuon() { return getCount("SELECT COUNT(*) FROM phieu_muon"); }
//    public int getTongDocGia() { return getCount("SELECT COUNT(*) FROM doc_gia"); }
//    public int getTongViPham() { return getCount("SELECT COUNT(*) FROM phieu_muon WHERE TinhTrang LIKE N'%Quá hạn%' OR TienPhat > 0"); }
//
//    public int getCountGlobal(String sql) { return getCount(sql); }
//
//    private int getCount(String sql) {
//        try (Connection conn = new DBConnect().getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//            if (rs.next()) return rs.getInt(1);
//        } catch (Exception e) { }
//        return 0;
//    }
//
//    // ==========================================================================
//    // PHẦN 4: CODE MỚI BỔ SUNG - QUẢN LÝ THÔNG BÁO
//    // ==========================================================================
//
//    public ArrayList<Object[]> getLichSuThongBao() {
//        ArrayList<Object[]> list = new ArrayList<>();
//        String sql = "SELECT TieuDe, NoiDung, NgayDang, NguoiDang FROM thong_bao ORDER BY NgayDang DESC";
//        try (Connection conn = new DBConnect().getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//            while (rs.next()) {
//                list.add(new Object[]{
//                    rs.getString("TieuDe"), 
//                    rs.getString("NoiDung"), 
//                    rs.getTimestamp("NgayDang"), 
//                    rs.getString("NguoiDang")
//                });
//            }
//        } catch (Exception e) { e.printStackTrace(); }
//        return list;
//    }
//
//    public boolean deleteThongBao(String tieuDe, Timestamp ngayDang) {
//        String sql = "DELETE FROM thong_bao WHERE TieuDe = ? AND NgayDang = ?";
//        try (Connection conn = new DBConnect().getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setString(1, tieuDe);
//            ps.setTimestamp(2, ngayDang);
//            return ps.executeUpdate() > 0;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//}