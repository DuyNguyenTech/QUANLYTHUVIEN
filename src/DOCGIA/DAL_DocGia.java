package DOCGIA;

import java.sql.*;
import java.util.ArrayList;

import CHUNG.DBConnect;

public class DAL_DocGia {

    // 1. LẤY DANH SÁCH (Full thông tin)
    public ArrayList<DTO_DocGia> getList() {
        ArrayList<DTO_DocGia> list = new ArrayList<>();
        String sql = "SELECT * FROM doc_gia";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToDTO(rs));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 2. THÊM ĐỘC GIẢ (Dành cho Admin/Thủ thư)
    public boolean add(DTO_DocGia dg) {
        String sql = "INSERT INTO doc_gia (MaDocGia, TenDocGia, Lop, DiaChi, SoDienThoai, GioiTinh, NgaySinh) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dg.getMaDocGia());
            ps.setString(2, dg.getTenDocGia());
            ps.setString(3, dg.getLop());
            ps.setString(4, dg.getDiaChi());
            ps.setString(5, dg.getSoDienThoai());
            ps.setString(6, dg.getGioiTinh()); 
            ps.setDate(7, dg.getNgaySinh());   
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // 3. CẬP NHẬT TOÀN BỘ (Dành cho Admin/Thủ thư)
    public boolean update(DTO_DocGia dg) {
        String sql = "UPDATE doc_gia SET TenDocGia=?, Lop=?, DiaChi=?, SoDienThoai=?, GioiTinh=?, NgaySinh=? WHERE MaDocGia=?";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dg.getTenDocGia());
            ps.setString(2, dg.getLop());
            ps.setString(3, dg.getDiaChi());
            ps.setString(4, dg.getSoDienThoai());
            ps.setString(5, dg.getGioiTinh());
            ps.setDate(6, dg.getNgaySinh());
            ps.setString(7, dg.getMaDocGia());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // 4. XÓA
    public boolean delete(String maDocGia) {
        String sql = "DELETE FROM doc_gia WHERE MaDocGia=?";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maDocGia);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // 5. TÌM KIẾM
    public ArrayList<DTO_DocGia> search(String keyword) {
        ArrayList<DTO_DocGia> list = new ArrayList<>();
        String sql = "SELECT * FROM doc_gia WHERE MaDocGia LIKE ? OR TenDocGia LIKE ? OR SoDienThoai LIKE ?";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String query = "%" + keyword + "%";
            ps.setString(1, query);
            ps.setString(2, query);
            ps.setString(3, query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToDTO(rs));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 6. LẤY CHI TIẾT
    public DTO_DocGia getChiTiet(String maDocGia) {
        DTO_DocGia dg = null;
        String sql = "SELECT * FROM doc_gia WHERE MaDocGia = ?";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maDocGia);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                dg = mapResultSetToDTO(rs);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return dg;
    }

    // ==========================================================
    // CÁC HÀM RIÊNG CHO GIAO DIỆN "THÔNG TIN CÁ NHÂN"
    // ==========================================================
    
    // Alias lấy chi tiết
    public DTO_DocGia getChiTietDocGia(String maDocGia) {
        return getChiTiet(maDocGia);
    }

    // [TỐI ƯU] Chỉ Update các trường Độc giả được phép sửa (An toàn dữ liệu)
    public boolean capNhatThongTinCaNhan(DTO_DocGia dg) {
        String sql = "UPDATE doc_gia SET Lop=?, DiaChi=?, SoDienThoai=? WHERE MaDocGia=?";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dg.getLop());
            ps.setString(2, dg.getDiaChi());
            ps.setString(3, dg.getSoDienThoai());
            ps.setString(4, dg.getMaDocGia());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // ==========================================================

    // Hàm phụ trợ map dữ liệu
    private DTO_DocGia mapResultSetToDTO(ResultSet rs) throws SQLException {
        DTO_DocGia dg = new DTO_DocGia();
        dg.setMaDocGia(rs.getString("MaDocGia"));
        dg.setTenDocGia(rs.getString("TenDocGia"));
        dg.setLop(rs.getString("Lop"));
        dg.setDiaChi(rs.getString("DiaChi"));
        dg.setGioiTinh(rs.getString("GioiTinh")); 
        dg.setNgaySinh(rs.getDate("NgaySinh"));   
        
        try {
            dg.setSoDienThoai(rs.getString("SoDienThoai"));
        } catch (SQLException e) {
            try { dg.setSoDienThoai(rs.getString("SDT")); } catch (Exception ex) {}
        }
        return dg;
    }
}