package THONGKE;

import MUONTRA.DTO_PhieuMuon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import CHUNG.DBConnect;

public class DAL_ThongKe {

    // --- 1. CÁC HÀM ĐẾM TỔNG QUAN (DASHBOARD) ---
    public int getTongDauSach() {
        int count = 0;
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM sach");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) count = rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return count;
    }

    public int getTongPhieuMuon() {
        int count = 0;
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM phieu_muon");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) count = rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return count;
    }

    public int getTongViPham() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM phieu_muon WHERE TinhTrang LIKE N'%Quá hạn%' OR TienPhat > 0";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) count = rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return count;
    }

    // --- 2. CÁC HÀM CHO THỐNG KÊ SÁCH ---
    public ArrayList<DTO_ThongKeSach> getListSachDangMuon() {
        ArrayList<DTO_ThongKeSach> list = new ArrayList<>();
        String sql = "SELECT s.MaCuonSach, s.TenSach, pm.MaDocGia, pm.MaPhieuMuon FROM chi_tiet_phieu_muon ct JOIN phieu_muon pm ON ct.MaPhieuMuon = pm.MaPhieuMuon JOIN sach s ON ct.MaCuonSach = s.MaCuonSach WHERE pm.NgayTra IS NULL";
        try (Connection conn = new DBConnect().getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(new DTO_ThongKeSach(rs.getString("MaCuonSach"), rs.getString("TenSach"), "Mượn bởi " + rs.getString("MaDocGia") + " (PM: " + rs.getString("MaPhieuMuon") + ")"));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public ArrayList<DTO_ThongKeSach> getListSachTrongKho() {
        ArrayList<DTO_ThongKeSach> list = new ArrayList<>();
        String sql = "SELECT MaCuonSach, TenSach, SoLuong FROM sach WHERE SoLuong > 0";
        try (Connection conn = new DBConnect().getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(new DTO_ThongKeSach(rs.getString("MaCuonSach"), rs.getString("TenSach"), "Tồn kho: " + rs.getInt("SoLuong")));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public int getTongTaiSanSach() {
        int tong = 0;
        try (Connection conn = new DBConnect().getConnection()) {
            PreparedStatement ps1 = conn.prepareStatement("SELECT SUM(SoLuong) FROM sach");
            ResultSet rs1 = ps1.executeQuery();
            if(rs1.next()) tong += rs1.getInt(1);
            PreparedStatement ps2 = conn.prepareStatement("SELECT COUNT(*) FROM chi_tiet_phieu_muon ct JOIN phieu_muon pm ON ct.MaPhieuMuon = pm.MaPhieuMuon WHERE pm.NgayTra IS NULL");
            ResultSet rs2 = ps2.executeQuery();
            if(rs2.next()) tong += rs2.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return tong;
    }

    // --- 3. THỐNG KÊ MƯỢN TRẢ ---
    public ArrayList<DTO_PhieuMuon> getListMuonTra(String option) {
        ArrayList<DTO_PhieuMuon> list = new ArrayList<>();
        String sql = "";
        switch (option) {
            case "DAY": sql = "SELECT * FROM phieu_muon WHERE DATE(NgayMuon) = CURDATE()"; break;
            case "WEEK": sql = "SELECT * FROM phieu_muon WHERE YEARWEEK(NgayMuon, 1) = YEARWEEK(CURDATE(), 1)"; break;
            case "MONTH": sql = "SELECT * FROM phieu_muon WHERE MONTH(NgayMuon) = MONTH(CURDATE()) AND YEAR(NgayMuon) = YEAR(CURDATE())"; break;
            case "CURRENT": sql = "SELECT * FROM phieu_muon WHERE NgayTra IS NULL"; break;
            default: sql = "SELECT * FROM phieu_muon"; break;
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
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // --- 4. THỐNG KÊ VI PHẠM ---
    public ArrayList<DTO_PhieuMuon> getListViPham() {
        ArrayList<DTO_PhieuMuon> list = new ArrayList<>();
        String sql = "SELECT * FROM phieu_muon WHERE TinhTrang LIKE N'%Quá hạn%' OR TienPhat > 0";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                DTO_PhieuMuon pm = new DTO_PhieuMuon();
                pm.setMaPhieuMuon(rs.getString("MaPhieuMuon"));
                pm.setNgayMuon(rs.getDate("NgayMuon"));
                pm.setNgayHenTra(rs.getDate("NgayHenTra")); // Lấy thêm ngày hẹn trả
                pm.setNgayTra(rs.getDate("NgayTra"));
                pm.setMaDocGia(rs.getString("MaDocGia"));
                pm.setTinhTrang(rs.getString("TinhTrang"));
                pm.setTienPhat(rs.getDouble("TienPhat"));
                list.add(pm);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}