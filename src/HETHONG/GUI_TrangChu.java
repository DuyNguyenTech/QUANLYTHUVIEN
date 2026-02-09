package THONGKE;

import MUONTRA.DTO_PhieuMuon;
import CHUNG.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class DAL_ThongKe {

    // ==========================================================================
    // PHẦN 1: CÁC HÀM CHO DASHBOARD (GUI_TrangChu & GUI_ThongKe)
    // ==========================================================================

    // 1. Tổng số đầu sách (Số lượng tựa sách)
    public int getTongDauSach() {
        return getCount("SELECT COUNT(*) FROM SACH");
    }

    // 2. Tổng số độc giả
    public int getTongDocGia() {
        return getCount("SELECT COUNT(*) FROM DOC_GIA");
    }

    // 3. Tổng số lượt mượn (Tổng số phiếu)
    public int getTongPhieuMuon() {
        return getCount("SELECT COUNT(*) FROM PHIEU_MUON");
    }

    // 4. Tổng số vi phạm (Quá hạn hoặc có tiền phạt)
    public int getTongViPham() {
        return getCount("SELECT COUNT(*) FROM PHIEU_MUON WHERE TinhTrang LIKE N'%Quá hạn%' OR TienPhat > 0");
    }

    // 5. [MỚI] Lấy dữ liệu cho biểu đồ tròn (Số lượng sách theo thể loại)
    public HashMap<String, Integer> getSachTheoTheLoai() {
        HashMap<String, Integer> map = new HashMap<>();
        try (Connection conn = new DBConnect().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT TheLoai, COUNT(*) FROM SACH GROUP BY TheLoai")) {
            while (rs.next()) {
                map.put(rs.getString(1), rs.getInt(2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    // 6. [MỚI] Lấy danh sách 10 hoạt động gần đây nhất
    public ArrayList<String[]> getHoatDongGanDay() {
        ArrayList<String[]> list = new ArrayList<>();
        String sql = "SELECT PM.MaPhieuMuon, DG.TenDocGia, PM.NgayMuon, PM.TinhTrang " +
                     "FROM PHIEU_MUON PM " +
                     "JOIN DOC_GIA DG ON PM.MaDocGia = DG.MaDocGia " +
                     "ORDER BY PM.NgayMuon DESC LIMIT 10";
        try (Connection conn = new DBConnect().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String mota = rs.getString("TenDocGia") + " mượn phiếu " + rs.getString("MaPhieuMuon");
                String thoigian = rs.getString("NgayMuon");
                String trangthai = rs.getString("TinhTrang");
                list.add(new String[]{mota, thoigian, trangthai});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ==========================================================================
    // PHẦN 2: CÁC HÀM CHO BÁO CÁO CHI TIẾT
    // ==========================================================================

    // 7. Lấy danh sách sách đang được mượn (Chưa trả)
    public ArrayList<DTO_ThongKeSach> getListSachDangMuon() {
        ArrayList<DTO_ThongKeSach> list = new ArrayList<>();
        String sql = "SELECT s.MaCuonSach, s.TenSach, pm.MaDocGia, pm.MaPhieuMuon " +
                     "FROM CHI_TIET_PHIEU_MUON ct " +
                     "JOIN PHIEU_MUON pm ON ct.MaPhieuMuon = pm.MaPhieuMuon " +
                     "JOIN SACH s ON ct.MaCuonSach = s.MaCuonSach " +
                     "WHERE pm.NgayTra IS NULL";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String tinhTrang = "Mượn bởi " + rs.getString("MaDocGia") + " (PM: " + rs.getString("MaPhieuMuon") + ")";
                list.add(new DTO_ThongKeSach(rs.getString("MaCuonSach"), rs.getString("TenSach"), tinhTrang));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 8. Lấy danh sách sách còn trong kho
    public ArrayList<DTO_ThongKeSach> getListSachTrongKho() {
        ArrayList<DTO_ThongKeSach> list = new ArrayList<>();
        String sql = "SELECT MaCuonSach, TenSach, SoLuong FROM SACH WHERE SoLuong > 0";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new DTO_ThongKeSach(rs.getString("MaCuonSach"), rs.getString("TenSach"), "Tồn kho: " + rs.getInt("SoLuong")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 9. Tính tổng toàn bộ tài sản sách (Sách trong kho + Sách đang mượn)
    public int getTongTaiSanSach() {
        int tong = 0;
        try (Connection conn = new DBConnect().getConnection()) {
            // Tổng số lượng tồn
            PreparedStatement ps1 = conn.prepareStatement("SELECT SUM(SoLuong) FROM SACH");
            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next()) tong += rs1.getInt(1);

            // Tổng số lượng đang mượn
            PreparedStatement ps2 = conn.prepareStatement("SELECT COUNT(*) FROM CHI_TIET_PHIEU_MUON ct JOIN PHIEU_MUON pm ON ct.MaPhieuMuon = pm.MaPhieuMuon WHERE pm.NgayTra IS NULL");
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) tong += rs2.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tong;
    }

    // 10. Lấy danh sách mượn trả theo thời gian (Ngày, Tuần, Tháng)
    public ArrayList<DTO_PhieuMuon> getListMuonTra(String option) {
        ArrayList<DTO_PhieuMuon> list = new ArrayList<>();
        String sql = "SELECT * FROM PHIEU_MUON";

        // Filter theo thời gian
        switch (option) {
            case "DAY":
                sql += " WHERE DATE(NgayMuon) = CURDATE()";
                break;
            case "WEEK":
                sql += " WHERE YEARWEEK(NgayMuon, 1) = YEARWEEK(CURDATE(), 1)";
                break;
            case "MONTH":
                sql += " WHERE MONTH(NgayMuon) = MONTH(CURDATE()) AND YEAR(NgayMuon) = YEAR(CURDATE())";
                break;
            case "CURRENT":
                sql += " WHERE NgayTra IS NULL";
                break;
        }

        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                DTO_PhieuMuon pm = new DTO_PhieuMuon();
                pm.setMaPhieuMuon(rs.getString("MaPhieuMuon"));
                pm.setNgayMuon(rs.getDate("NgayMuon"));
                pm.setNgayTra(rs.getDate("NgayTra"));
                pm.setMaDocGia(rs.getString("MaDocGia"));
                pm.setTinhTrang(rs.getString("TinhTrang"));
                pm.setTienPhat(rs.getDouble("TienPhat"));
                list.add(pm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 11. Lấy danh sách vi phạm
    public ArrayList<DTO_PhieuMuon> getListViPham() {
        ArrayList<DTO_PhieuMuon> list = new ArrayList<>();
        String sql = "SELECT * FROM PHIEU_MUON WHERE TinhTrang LIKE N'%Quá hạn%' OR TienPhat > 0";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                DTO_PhieuMuon pm = new DTO_PhieuMuon();
                pm.setMaPhieuMuon(rs.getString("MaPhieuMuon"));
                pm.setNgayMuon(rs.getDate("NgayMuon"));
                pm.setNgayHenTra(rs.getDate("NgayHenTra"));
                pm.setNgayTra(rs.getDate("NgayTra"));
                pm.setMaDocGia(rs.getString("MaDocGia"));
                pm.setTinhTrang(rs.getString("TinhTrang")); // Lỗi vi phạm
                pm.setTienPhat(rs.getDouble("TienPhat"));
                list.add(pm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 12. [MỚI] Đếm số lượng phiếu theo trạng thái của riêng 1 độc giả
    public int getCountPersonal(String maDocGia, String trangThaiKeyword) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM PHIEU_MUON WHERE MaDocGia = ? AND TinhTrang LIKE ?";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maDocGia);
            ps.setString(2, "%" + trangThaiKeyword + "%");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) count = rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return count;
    }

    // 13. [MỚI] Đếm tổng số lượt mượn (Lịch sử) của riêng 1 độc giả
    public int getCountAllPersonal(String maDocGia) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM PHIEU_MUON WHERE MaDocGia = ?";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maDocGia);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) count = rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return count;
    }

    // --- Helper: Hàm đếm chung ---
    private int getCount(String sql) {
        int count = 0;
        try (Connection conn = new DBConnect().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) count = rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }
}