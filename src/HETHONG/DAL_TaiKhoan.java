package HETHONG;

import java.sql.*;

import CHUNG.DBConnect;

public class DAL_TaiKhoan {

    // 1. LOGIN (Đăng nhập) - Trả về đối tượng tài khoản
    public DTO_TaiKhoan login(String tenDangNhap, String matKhau) {
        DTO_TaiKhoan tk = null;
        String sql = "SELECT * FROM tai_khoan WHERE TenDangNhap = ? AND MatKhau = ?";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, tenDangNhap);
            ps.setString(2, matKhau);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                tk = new DTO_TaiKhoan();
                // Map dữ liệu từ Database vào DTO
                tk.setUserName(rs.getString("TenDangNhap")); 
                tk.setPassword(rs.getString("MatKhau"));
                tk.setPhanQuyen(rs.getInt("PhanQuyen"));
                tk.setMaDocGia(rs.getString("MaDocGia"));
                tk.setMaThuThu(rs.getString("MaThuThu"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return tk;
    }

    // =======================================================
    // [ĐÃ THÊM MỚI] Dùng cho GUI_DialogDoiMatKhau
    // =======================================================
    public boolean checkLogin(String username, String password) {
        String sql = "SELECT * FROM tai_khoan WHERE TenDangNhap = ? AND MatKhau = ?";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setString(2, password);
            
            ResultSet rs = ps.executeQuery();
            return rs.next(); // Nếu rs.next() có dữ liệu => trả về TRUE (Đúng mật khẩu)
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // 2. CHECK PASS CŨ (Giữ lại để tương thích ngược với các file code cũ của anh)
    public boolean checkMatKhau(String username, String password) {
        // Tái sử dụng lại hàm checkLogin cho code ngắn gọn
        return checkLogin(username, password); 
    }

    // 3. ĐỔI PASS (QUAN TRỌNG: Đã có WHERE để không update nhầm người khác)
    public boolean doiMatKhau(String username, String newPassword) {
        String sql = "UPDATE tai_khoan SET MatKhau = ? WHERE TenDangNhap = ?";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, newPassword); // Tham số 1: Pass mới
            ps.setString(2, username);    // Tham số 2: User cần đổi (WHERE)
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}