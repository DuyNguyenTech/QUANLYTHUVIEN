package CAIDAT;

import CHUNG.DBConnect;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DAL_ThamSo {
    DBConnect db = new DBConnect();

    // Lấy tất cả tham số về dạng Map cho dễ dùng (Ví dụ: map.get("SoNgayMuonToiDa"))
    public Map<String, String> getAllThamSo() {
        Map<String, String> map = new HashMap<>();
        try {
            String sql = "SELECT * FROM THAM_SO";
            ResultSet rs = db.executeQuery(sql);
            while (rs.next()) {
                map.put(rs.getString("TenThamSo"), rs.getString("GiaTri"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    // Cập nhật giá trị
    public boolean updateThamSo(String tenThamSo, String giaTriMoi) {
        try {
            String sql = "UPDATE THAM_SO SET GiaTri = '" + giaTriMoi + "' WHERE TenThamSo = '" + tenThamSo + "'";
            return db.executeUpdate(sql) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}