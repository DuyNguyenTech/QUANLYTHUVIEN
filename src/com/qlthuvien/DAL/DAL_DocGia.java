package com.qlthuvien.DAL;

import com.qlthuvien.DTO.DTO_DocGia;
import com.qlthuvien.UTIL.DBConnect;
import java.sql.*;
import java.util.ArrayList;

public class DAL_DocGia {

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

    public boolean add(DTO_DocGia dg) {
        String sql = "INSERT INTO doc_gia (MaDocGia, TenDocGia, Lop, DiaChi, SoDienThoai) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dg.getMaDocGia());
            ps.setString(2, dg.getTenDocGia());
            ps.setString(3, dg.getLop());
            ps.setString(4, dg.getDiaChi());
            ps.setString(5, dg.getSoDienThoai());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean update(DTO_DocGia dg) {
        String sql = "UPDATE doc_gia SET TenDocGia=?, Lop=?, DiaChi=?, SoDienThoai=? WHERE MaDocGia=?";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dg.getTenDocGia());
            ps.setString(2, dg.getLop());
            ps.setString(3, dg.getDiaChi());
            ps.setString(4, dg.getSoDienThoai());
            ps.setString(5, dg.getMaDocGia());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean delete(String maDocGia) {
        String sql = "DELETE FROM doc_gia WHERE MaDocGia=?";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maDocGia);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

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

    // Hàm phụ trợ để map dữ liệu tránh lặp code
    private DTO_DocGia mapResultSetToDTO(ResultSet rs) throws SQLException {
        DTO_DocGia dg = new DTO_DocGia();
        dg.setMaDocGia(rs.getString("MaDocGia"));
        dg.setTenDocGia(rs.getString("TenDocGia"));
        dg.setLop(rs.getString("Lop"));
        dg.setDiaChi(rs.getString("DiaChi"));
        try {
            dg.setSoDienThoai(rs.getString("SoDienThoai"));
        } catch (SQLException e) {
            dg.setSoDienThoai(rs.getString("SDT"));
        }
        return dg;
    }
}