package NHAPHANG;

import CHUNG.DBConnect;
import java.sql.ResultSet;
import java.util.ArrayList;

public class DAL_NhaCungCap {
    DBConnect db = new DBConnect();

    public ArrayList<DTO_NhaCungCap> getAllNCC() {
        ArrayList<DTO_NhaCungCap> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM NHA_CUNG_CAP";
            ResultSet rs = db.executeQuery(sql);
            while (rs.next()) {
                list.add(new DTO_NhaCungCap(
                    rs.getString("MaNCC"),
                    rs.getString("TenNCC"),
                    rs.getString("DiaChi"),
                    rs.getString("SDT")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean themNCC(DTO_NhaCungCap ncc) {
        String sql = "INSERT INTO NHA_CUNG_CAP VALUES ('" + ncc.getMaNCC() + "', N'" + ncc.getTenNCC() + "', N'" + ncc.getDiaChi() + "', '" + ncc.getSdt() + "')";
        return db.executeUpdate(sql) > 0;
    }

    public boolean suaNCC(DTO_NhaCungCap ncc) {
        String sql = "UPDATE NHA_CUNG_CAP SET TenNCC = N'" + ncc.getTenNCC() + "', DiaChi = N'" + ncc.getDiaChi() + "', SDT = '" + ncc.getSdt() + "' WHERE MaNCC = '" + ncc.getMaNCC() + "'";
        return db.executeUpdate(sql) > 0;
    }

    public boolean xoaNCC(String maNCC) {
        String sql = "DELETE FROM NHA_CUNG_CAP WHERE MaNCC = '" + maNCC + "'";
        return db.executeUpdate(sql) > 0;
    }
}