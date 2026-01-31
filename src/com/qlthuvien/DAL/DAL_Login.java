package com.qlthuvien.DAL;

import com.qlthuvien.DTO.TaiKhoan;
import com.qlthuvien.UTIL.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DAL_Login {
    
    public TaiKhoan checkLogin(String username, String password) {
        TaiKhoan tk = null;
        String sql = "SELECT * FROM TAI_KHOAN WHERE TenDangNhap = ? AND MatKhau = ?";
        
        DBConnect db = new DBConnect();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setString(2, password);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                tk = new TaiKhoan();
                tk.setUserName(rs.getString("TenDangNhap"));
                tk.setPassword(rs.getString("MatKhau"));
                tk.setPhanQuyen(rs.getInt("PhanQuyen")); // Lưu ý: Trong DB mình để INT, trong C# bạn để String? 
                // Nếu DB là String thì sửa thành rs.getString
                tk.setMaDocGia(rs.getString("MaDocGia"));
                tk.setMaThuThu(rs.getString("MaThuThu"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tk;
    }
}