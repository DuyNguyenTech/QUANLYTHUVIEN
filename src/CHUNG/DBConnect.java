package CHUNG;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnect {
    
    // 1. Cấu hình thông tin kết nối
    // Lưu ý: Bạn đang dùng Port 3307 (thường là XAMPP đổi port), nếu lỗi hãy thử về 3306
    private final String URL = "jdbc:mysql://localhost:3306/QLTV?useUnicode=true&characterEncoding=UTF-8";
    private final String USER = "root"; 
    private final String PASS = ""; // Điền mật khẩu MySQL nếu có

    // 2. Hàm tạo kết nối
    public Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            System.err.println("Lỗi: Không tìm thấy Driver MySQL!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Lỗi: Không thể kết nối DB! Check Port (3306/3307), User, Pass.");
            e.printStackTrace();
        }
        return conn;
    }

    // ==========================================================
    // PHẦN 1: HỖ TRỢ CÁC DAL (DAL_ThamSo, DAL_Sach...) GỌI LỆNH
    // ==========================================================

    // Dùng cho SELECT (Lấy dữ liệu)
    public ResultSet executeQuery(String sql) {
        Connection conn = getConnection();
        try {
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Dùng cho INSERT, UPDATE, DELETE (Thay đổi dữ liệu)
    public int executeUpdate(String sql) {
        Connection conn = getConnection();
        try {
            Statement stmt = conn.createStatement();
            return stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // ==========================================================
    // PHẦN 2: HỖ TRỢ MODULE SAO LƯU & PHỤC HỒI (BACKUP/RESTORE)
    // ==========================================================
    
    public String getUser() { return USER; }
    public String getPass() { return PASS; }
    public String getUrl() { return URL; }
    
    // Hàm tách tên Database từ URL để dùng cho lệnh mysqldump
    public String getDbName() {
        try {
            // URL dạng: jdbc:mysql://localhost:3307/QuanLyThuVien?use...
            String[] parts = URL.split("/");
            // Lấy phần cuối cùng và cắt bỏ các tham số sau dấu ?
            return parts[parts.length - 1].split("\\?")[0]; 
        } catch (Exception e) {
            return "QuanLyThuVien";
        }
    }

    // Hàm main test kết nối
    public static void main(String[] args) {
        DBConnect db = new DBConnect();
        Connection conn = db.getConnection();
        if(conn != null) {
            System.out.println("KET NOI THANH CONG! DB: " + db.getDbName());
            try { conn.close(); } catch (SQLException e) {}
        } else {
            System.out.println("KET NOI THAT BAI!");
        }
    }
}