package NHAPHANG;

import CHUNG.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DAL_NhapHang {
    
    // 1. TẠO PHIẾU NHẬP (Transaction: Lưu Phiếu -> Lưu Chi Tiết -> Cộng Tồn Kho)
    public boolean taoPhieuNhap(DTO_PhieuNhap pn, ArrayList<DTO_ChiTietNhap> listCT) {
        Connection conn = new DBConnect().getConnection();
        if(conn == null) return false;
        
        try {
            // Tắt chế độ tự động lưu để quản lý Transaction thủ công
            conn.setAutoCommit(false); 

            // Bước 1: Insert PHIEU_NHAP
            String sql1 = "INSERT INTO phieu_nhap VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps1 = conn.prepareStatement(sql1);
            ps1.setString(1, pn.getMaPhieu());
            ps1.setString(2, pn.getMaNCC());
            ps1.setString(3, pn.getMaNhanVien());
            ps1.setDate(4, pn.getNgayNhap());
            ps1.setDouble(5, pn.getTongTien());
            ps1.executeUpdate();

            // Bước 2: Insert CHI_TIET_NHAP & Cập nhật kho sách
            String sql2 = "INSERT INTO chi_tiet_nhap VALUES (?, ?, ?, ?, ?)";
            // [LƯU Ý] Sửa MaSach thành MaCuonSach cho đúng với khóa chính bảng SACH của anh
            String sql3 = "UPDATE sach SET SoLuong = SoLuong + ? WHERE MaCuonSach = ?"; 
            
            PreparedStatement ps2 = conn.prepareStatement(sql2);
            PreparedStatement ps3 = conn.prepareStatement(sql3);

            for (DTO_ChiTietNhap ct : listCT) {
                // Thêm chi tiết nhập
                ps2.setString(1, ct.getMaPhieu());
                ps2.setString(2, ct.getMaSach()); // Đây chính là MaCuonSach
                ps2.setInt(3, ct.getSoLuong());
                ps2.setDouble(4, ct.getDonGia());
                ps2.setDouble(5, ct.getThanhTien());
                ps2.executeUpdate();

                // Cộng dồn số lượng vào kho
                ps3.setInt(1, ct.getSoLuong());
                ps3.setString(2, ct.getMaSach()); // Update theo MaCuonSach
                ps3.executeUpdate();
            }

            // Nếu chạy đến đây mà không lỗi thì CHỐT đơn (Commit)
            conn.commit(); 
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            try {
                conn.rollback(); // Gặp lỗi thì hoàn tác lại như chưa từng nhập
            } catch (SQLException ex) { ex.printStackTrace(); }
        } finally {
            try { conn.close(); } catch (Exception e) {}
        }
        return false;
    }

    // 2. LẤY DANH SÁCH PHIẾU NHẬP (Để hiển thị lịch sử)
    public ArrayList<DTO_PhieuNhap> getDanhSachPhieuNhap() {
        ArrayList<DTO_PhieuNhap> list = new ArrayList<>();
        Connection conn = new DBConnect().getConnection();
        try {
            // Join 3 bảng để lấy Tên NCC và Tên Thủ Thư thay vì hiển thị Mã
            String sql = "SELECT pn.MaPhieuNhap, ncc.TenNCC, tt.TenThuThu, pn.NgayNhap, pn.TongTien " +
                         "FROM phieu_nhap pn " +
                         "LEFT JOIN nha_cung_cap ncc ON pn.MaNCC = ncc.MaNCC " +
                         "LEFT JOIN thu_thu tt ON pn.MaNhanVien = tt.MaThuThu " +
                         "ORDER BY pn.NgayNhap DESC"; // Sắp xếp ngày mới nhất lên đầu
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                // Mẹo: Tận dụng constructor DTO_PhieuNhap để chứa Tên thay vì Mã (để hiển thị lên bảng cho đẹp)
                list.add(new DTO_PhieuNhap(
                    rs.getString("MaPhieuNhap"),
                    rs.getString("TenNCC"),     // Lưu Tên NCC vào biến MaNCC
                    rs.getString("TenThuThu"),  // Lưu Tên Thủ Thư vào biến MaNhanVien
                    rs.getDate("NgayNhap"),
                    rs.getDouble("TongTien")
                ));
            }
            conn.close();
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}