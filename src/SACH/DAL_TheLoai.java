package SACH;

import java.sql.*;
import java.util.ArrayList;

import CHUNG.DBConnect;

public class DAL_TheLoai {

    // 1. Lấy toàn bộ danh sách (Trả về đối tượng DTO - Dùng cho bảng QL Thể Loại)
    public ArrayList<DTO_TheLoai> getList() {
        ArrayList<DTO_TheLoai> list = new ArrayList<>();
        String sql = "SELECT * FROM the_loai";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new DTO_TheLoai(rs.getString("MaTheLoai"), rs.getString("TenTheLoai")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 2. [MỚI THÊM] Lấy danh sách tên định dạng String (Dùng cho ComboBox bên Thêm Sách)
    public ArrayList<String> getListTenTheLoai() {
        ArrayList<String> list = new ArrayList<>();
        String sql = "SELECT MaTheLoai, TenTheLoai FROM the_loai";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                // Kết quả sẽ là: "TL01 - Khoa Học", "TL02 - Văn Học"...
                list.add(rs.getString("MaTheLoai") + " - " + rs.getString("TenTheLoai"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 3. Thêm thể loại
    public boolean add(DTO_TheLoai tl) {
        String sql = "INSERT INTO the_loai (MaTheLoai, TenTheLoai) VALUES (?, ?)";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tl.getMaTheLoai());
            ps.setString(2, tl.getTenTheLoai());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // 4. Sửa thể loại
    public boolean update(DTO_TheLoai tl) {
        String sql = "UPDATE the_loai SET TenTheLoai=? WHERE MaTheLoai=?";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tl.getTenTheLoai());
            ps.setString(2, tl.getMaTheLoai());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // 5. Xóa thể loại
    public boolean delete(String maTL) {
        String sql = "DELETE FROM the_loai WHERE MaTheLoai=?";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maTL);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
    
    // 6. Kiểm tra trùng mã
    public boolean checkExist(String maTL) {
        boolean exist = false;
        String sql = "SELECT * FROM the_loai WHERE MaTheLoai = ?";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maTL);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) exist = true;
        } catch (Exception e) { e.printStackTrace(); }
        return exist;
    }
}