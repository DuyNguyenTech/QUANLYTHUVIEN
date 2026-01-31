package com.qlthuvien.DAL;

import com.qlthuvien.DTO.DTO_DocGia; // Import đúng DTO
import com.qlthuvien.DTO.TaiKhoan;
import com.qlthuvien.UTIL.DBConnect;
import java.sql.*;
import java.time.LocalDate;

public class DAL_Signup {

    // 1. Kiểm tra tồn tại (Username hoặc Email)
    public String checkExist(String username, String email) {
        String sql = "SELECT * FROM tai_khoan WHERE TenDangNhap = ? OR Email = ?";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if(rs.getString("TenDangNhap").equals(username)) return "Tên đăng nhập đã tồn tại!";
                if(rs.getString("Email").equals(email)) return "Email đã được sử dụng!";
            }
        } catch (Exception e) { e.printStackTrace(); }
        return "OK"; // Không trùng
    }

    // 2. Đăng ký mới (Transaction)
    public boolean signup(DTO_DocGia dg, TaiKhoan tk) {
        Connection conn = null;
        try {
            conn = new DBConnect().getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // --- A. INSERT BẢNG ĐỘC GIẢ ---
            String sqlDG = "INSERT INTO doc_gia(MaDocGia, TenDocGia, NgaySinh, GioiTinh, DiaChi, SoDienThoai, NgayLapThe, NgayHetHan, HoatDong) VALUES(?,?,?,?,?,?,?,?,?)";
            PreparedStatement psDG = conn.prepareStatement(sqlDG);
            
            // Ngày hiện tại & Hết hạn
            Date today = Date.valueOf(LocalDate.now());
            Date expireDay = Date.valueOf(LocalDate.now().plusMonths(6));

            psDG.setString(1, dg.getMaDocGia());
            psDG.setString(2, dg.getTenDocGia());   // Sửa getHoTen -> getTenDocGia
            psDG.setDate(3, dg.getNgaySinh());
            psDG.setString(4, dg.getGioiTinh());
            psDG.setString(5, dg.getDiaChi());
            psDG.setString(6, dg.getSoDienThoai()); // Sửa getSdt -> getSoDienThoai
            
            psDG.setDate(7, today);      // NgayLapThe
            psDG.setDate(8, expireDay);  // NgayHetHan
            psDG.setInt(9, 1);           // HoatDong
            
            psDG.executeUpdate();

            // --- B. INSERT BẢNG TÀI KHOẢN ---
            String sqlTK = "INSERT INTO tai_khoan(TenDangNhap, MatKhau, PhanQuyen, MaDocGia, Email) VALUES(?,?,?,?,?)";
            PreparedStatement psTK = conn.prepareStatement(sqlTK);
            psTK.setString(1, tk.getUserName());
            psTK.setString(2, tk.getPassword());
            psTK.setInt(3, 3); // 3 = Độc giả
            psTK.setString(4, dg.getMaDocGia());
            psTK.setString(5, tk.getEmail());
            psTK.executeUpdate();

            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException ex) {}
        }
        return false;
    }
}