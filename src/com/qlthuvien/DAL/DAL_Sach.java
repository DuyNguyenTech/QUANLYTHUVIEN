package com.qlthuvien.DAL;

import com.qlthuvien.DTO.DTO_Sach;
import com.qlthuvien.UTIL.DBConnect;
import java.sql.*;
import java.util.ArrayList;

public class DAL_Sach {

    // 1. LẤY DANH SÁCH
    public ArrayList<DTO_Sach> getList() {
        ArrayList<DTO_Sach> list = new ArrayList<>();
        String sql = "SELECT s.*, tl.TenTheLoai FROM sach s LEFT JOIN the_loai tl ON s.MaTheLoai = tl.MaTheLoai";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(getFromResultSet(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 2. SEARCH (NÂNG CẤP: Hỗ trợ lọc theo tiêu chí)
    public ArrayList<DTO_Sach> searchSach(String keyword, String luaChon) {
        ArrayList<DTO_Sach> list = new ArrayList<>();
        String sql = "SELECT s.*, tl.TenTheLoai FROM sach s " +
                     "LEFT JOIN the_loai tl ON s.MaTheLoai = tl.MaTheLoai WHERE ";

        // Xử lý câu SQL dựa trên lựa chọn từ ComboBox
        if (luaChon.equals("Mã sách")) {
            sql += "s.MaCuonSach LIKE ?";
        } else if (luaChon.equals("Tên sách")) {
            sql += "s.TenSach LIKE ?";
        } else if (luaChon.equals("Tác giả")) {
            sql += "s.TacGia LIKE ?";
        } else {
            // Mặc định: Tất cả (Tìm trên cả 3 trường)
            sql += "(s.MaCuonSach LIKE ? OR s.TenSach LIKE ? OR s.TacGia LIKE ?)";
        }

        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String q = "%" + keyword + "%";
            
            if (luaChon.equals("Tất cả")) {
                // Nếu tìm tất cả thì set 3 tham số
                ps.setString(1, q);
                ps.setString(2, q);
                ps.setString(3, q);
            } else {
                // Nếu tìm cụ thể thì chỉ set 1 tham số
                ps.setString(1, q);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(getFromResultSet(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    
    // Giữ lại hàm cũ (Overload) để tránh lỗi ở các module khác (như Độc giả)
    public ArrayList<DTO_Sach> searchSach(String keyword) {
        return searchSach(keyword, "Tất cả");
    }
    
    public ArrayList<DTO_Sach> timKiem(String k) { return searchSach(k, "Tất cả"); }

    // 3. GET DETAIL
    public DTO_Sach getDetail(String maSach) {
        DTO_Sach s = null;
        String sql = "SELECT s.*, tl.TenTheLoai FROM sach s LEFT JOIN the_loai tl ON s.MaTheLoai = tl.MaTheLoai WHERE s.MaCuonSach = ?";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSach);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) s = getFromResultSet(rs);
        } catch (Exception e) { e.printStackTrace(); }
        return s;
    }

    // 4. THÊM SÁCH
    public boolean addSach(DTO_Sach s) {
        String sql = "INSERT INTO sach (MaCuonSach, TenSach, MaTheLoai, TacGia, NamXuatBan, NhaXuatBan, Gia, SoLuong, MoTa, HinhAnh) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = new DBConnect().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getMaCuonSach()); ps.setString(2, s.getTenSach()); ps.setString(3, s.getMaTheLoai());
            ps.setString(4, s.getTacGia()); ps.setInt(5, s.getNamXuatBan()); ps.setString(6, s.getNhaXuatBan());
            ps.setDouble(7, s.getGia()); ps.setInt(8, s.getSoLuong()); ps.setString(9, s.getMoTa()); ps.setString(10, s.getHinhAnh());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // 5. SỬA SÁCH
    public boolean updateSach(DTO_Sach s) {
        String sql = "UPDATE sach SET TenSach=?, MaTheLoai=?, TacGia=?, NamXuatBan=?, NhaXuatBan=?, Gia=?, SoLuong=?, MoTa=?, HinhAnh=? WHERE MaCuonSach=?";
        try (Connection conn = new DBConnect().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getTenSach()); ps.setString(2, s.getMaTheLoai()); ps.setString(3, s.getTacGia());
            ps.setInt(4, s.getNamXuatBan()); ps.setString(5, s.getNhaXuatBan()); ps.setDouble(6, s.getGia());
            ps.setInt(7, s.getSoLuong()); ps.setString(8, s.getMoTa()); ps.setString(9, s.getHinhAnh()); ps.setString(10, s.getMaCuonSach());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // 6. XÓA SÁCH
    public boolean deleteSach(String ma) {
        String sql = "DELETE FROM sach WHERE MaCuonSach=?";
        try (Connection conn = new DBConnect().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ma); return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // 7. CHECK TRÙNG
    public boolean hasMaSach(String ma) {
        String sql = "SELECT COUNT(*) FROM sach WHERE MaCuonSach=?";
        try (Connection conn = new DBConnect().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ma); ResultSet rs = ps.executeQuery();
            if(rs.next()) return rs.getInt(1) > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // HÀM MAP DỮ LIỆU
    private DTO_Sach getFromResultSet(ResultSet rs) throws Exception {
        DTO_Sach s = new DTO_Sach();
        s.setMaCuonSach(rs.getString("MaCuonSach"));
        s.setTenSach(rs.getString("TenSach"));
        s.setMaTheLoai(rs.getString("MaTheLoai"));
        s.setTacGia(rs.getString("TacGia"));
        s.setNamXuatBan(rs.getInt("NamXuatBan"));
        s.setSoLuong(rs.getInt("SoLuong"));
        
        try { s.setNhaXuatBan(rs.getString("NhaXuatBan")); } catch (SQLException e) { s.setNhaXuatBan(""); }
        try { s.setGia(rs.getDouble("Gia")); } catch (SQLException e) { s.setGia(0); }
        try { s.setMoTa(rs.getString("MoTa")); } catch (SQLException e) { s.setMoTa(""); }
        try { s.setHinhAnh(rs.getString("HinhAnh")); } catch (SQLException e) { s.setHinhAnh(""); }
        try { s.setTenTheLoai(rs.getString("TenTheLoai")); } catch (SQLException e) { s.setTenTheLoai(s.getMaTheLoai()); }

        s.setTinhTrang((s.getSoLuong() > 0) ? "Còn sách" : "Hết sách");
        return s;
    }
}