package com.qlthuvien.DAL;

import com.qlthuvien.DTO.DTO_ThuThu;
import com.qlthuvien.UTIL.DBConnect;
import java.sql.*;
import java.util.ArrayList;

public class DAL_ThuThu {

    // 1. LẤY DANH SÁCH
    public ArrayList<DTO_ThuThu> getList() {
        ArrayList<DTO_ThuThu> list = new ArrayList<>();
        String sql = "SELECT t.*, k.TenDangNhap, k.MatKhau, k.PhanQuyen " +
                     "FROM thu_thu t " +
                     "LEFT JOIN tai_khoan k ON t.MaThuThu = k.MaThuThu";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                DTO_ThuThu tt = new DTO_ThuThu();
                tt.setMaThuThu(rs.getString("MaThuThu"));
                tt.setTenThuThu(rs.getString("TenThuThu"));
                tt.setNgaySinh(rs.getDate("NgaySinh"));
                tt.setGioiTinh(rs.getString("GioiTinh"));
                tt.setDiaChi(rs.getString("DiaChi"));
                tt.setSoDienThoai(rs.getString("SoDienThoai"));
                tt.setTenDangNhap(rs.getString("TenDangNhap"));
                tt.setMatKhau(rs.getString("MatKhau"));
                tt.setPhanQuyen(rs.getInt("PhanQuyen"));
                list.add(tt);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 2. LẤY CHI TIẾT THEO MÃ (MỚI THÊM)
    public DTO_ThuThu getDetail(String maThuThu) {
        DTO_ThuThu tt = null;
        String sql = "SELECT t.*, k.TenDangNhap, k.MatKhau, k.PhanQuyen " +
                     "FROM thu_thu t " +
                     "LEFT JOIN tai_khoan k ON t.MaThuThu = k.MaThuThu " +
                     "WHERE t.MaThuThu = ?";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maThuThu);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                tt = new DTO_ThuThu();
                tt.setMaThuThu(rs.getString("MaThuThu"));
                tt.setTenThuThu(rs.getString("TenThuThu"));
                tt.setNgaySinh(rs.getDate("NgaySinh"));
                tt.setGioiTinh(rs.getString("GioiTinh"));
                tt.setDiaChi(rs.getString("DiaChi")); // Quan trọng: Lấy địa chỉ
                tt.setSoDienThoai(rs.getString("SoDienThoai"));
                tt.setTenDangNhap(rs.getString("TenDangNhap"));
                tt.setMatKhau(rs.getString("MatKhau"));
                tt.setPhanQuyen(rs.getInt("PhanQuyen"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return tt;
    }

    // 3. THÊM
    public boolean add(DTO_ThuThu tt) {
        Connection conn = new DBConnect().getConnection();
        try {
            conn.setAutoCommit(false);
            String sqlTT = "INSERT INTO thu_thu (MaThuThu, TenThuThu, NgaySinh, GioiTinh, DiaChi, SoDienThoai) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement psTT = conn.prepareStatement(sqlTT);
            psTT.setString(1, tt.getMaThuThu()); psTT.setString(2, tt.getTenThuThu());
            psTT.setDate(3, tt.getNgaySinh()); psTT.setString(4, tt.getGioiTinh());
            psTT.setString(5, tt.getDiaChi()); psTT.setString(6, tt.getSoDienThoai());
            psTT.executeUpdate();

            if(tt.getTenDangNhap() != null && !tt.getTenDangNhap().isEmpty()) {
                String sqlTK = "INSERT INTO tai_khoan (TenDangNhap, MatKhau, PhanQuyen, MaThuThu) VALUES (?, ?, ?, ?)";
                PreparedStatement psTK = conn.prepareStatement(sqlTK);
                psTK.setString(1, tt.getTenDangNhap()); psTK.setString(2, tt.getMatKhau());
                psTK.setInt(3, tt.getPhanQuyen()); psTK.setString(4, tt.getMaThuThu());
                psTK.executeUpdate();
            }
            conn.commit(); return true;
        } catch (Exception e) {
            e.printStackTrace(); try { if(conn!=null) conn.rollback(); } catch(SQLException ex){}
        }
        return false;
    }

    // 4. SỬA
    public boolean update(DTO_ThuThu tt) {
        Connection conn = new DBConnect().getConnection();
        try {
            conn.setAutoCommit(false);
            String sqlTT = "UPDATE thu_thu SET TenThuThu=?, NgaySinh=?, GioiTinh=?, DiaChi=?, SoDienThoai=? WHERE MaThuThu=?";
            PreparedStatement psTT = conn.prepareStatement(sqlTT);
            psTT.setString(1, tt.getTenThuThu()); psTT.setDate(2, tt.getNgaySinh());
            psTT.setString(3, tt.getGioiTinh()); psTT.setString(4, tt.getDiaChi());
            psTT.setString(5, tt.getSoDienThoai()); psTT.setString(6, tt.getMaThuThu());
            psTT.executeUpdate();

            if(tt.getTenDangNhap() != null) {
                String sqlTK = "UPDATE tai_khoan SET MatKhau=?, PhanQuyen=? WHERE TenDangNhap=?";
                PreparedStatement psTK = conn.prepareStatement(sqlTK);
                psTK.setString(1, tt.getMatKhau()); psTK.setInt(2, tt.getPhanQuyen());
                psTK.setString(3, tt.getTenDangNhap());
                psTK.executeUpdate();
            }
            conn.commit(); return true;
        } catch (Exception e) {
            e.printStackTrace(); try { if(conn!=null) conn.rollback(); } catch(SQLException ex){}
        }
        return false;
    }

    // 5. XÓA
    public boolean delete(String maThuThu) {
        Connection conn = new DBConnect().getConnection();
        try {
            conn.setAutoCommit(false);
            PreparedStatement psTK = conn.prepareStatement("DELETE FROM tai_khoan WHERE MaThuThu=?");
            psTK.setString(1, maThuThu); psTK.executeUpdate();

            PreparedStatement psTT = conn.prepareStatement("DELETE FROM thu_thu WHERE MaThuThu=?");
            psTT.setString(1, maThuThu); psTT.executeUpdate();
            
            conn.commit(); return true;
        } catch (Exception e) {
            e.printStackTrace(); try { if(conn!=null) conn.rollback(); } catch(SQLException ex){}
        }
        return false;
    }
 // ... (Các hàm cũ getList, add, update, delete giữ nguyên) ...

    // 6. TÌM KIẾM (MỚI THÊM)
    public ArrayList<DTO_ThuThu> timKiem(String keyword) {
        ArrayList<DTO_ThuThu> list = new ArrayList<>();
        // Tìm theo Mã, Tên, hoặc SĐT
        String sql = "SELECT t.*, k.TenDangNhap, k.MatKhau, k.PhanQuyen " +
                     "FROM thu_thu t " +
                     "LEFT JOIN tai_khoan k ON t.MaThuThu = k.MaThuThu " +
                     "WHERE t.MaThuThu LIKE ? OR t.TenThuThu LIKE ? OR t.SoDienThoai LIKE ?";
        
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String query = "%" + keyword + "%";
            ps.setString(1, query);
            ps.setString(2, query);
            ps.setString(3, query);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DTO_ThuThu tt = new DTO_ThuThu();
                tt.setMaThuThu(rs.getString("MaThuThu"));
                tt.setTenThuThu(rs.getString("TenThuThu"));
                tt.setNgaySinh(rs.getDate("NgaySinh"));
                tt.setGioiTinh(rs.getString("GioiTinh"));
                tt.setDiaChi(rs.getString("DiaChi"));
                tt.setSoDienThoai(rs.getString("SoDienThoai"));
                tt.setTenDangNhap(rs.getString("TenDangNhap"));
                tt.setMatKhau(rs.getString("MatKhau"));
                tt.setPhanQuyen(rs.getInt("PhanQuyen"));
                list.add(tt);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}