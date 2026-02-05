package CHUNG;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {
    
    // 1. Cấu hình thông tin kết nối
    // URL: Địa chỉ MySQL. 3306 là cổng mặc định. QuanLyThuVien là tên Database của bạn.
    // useUnicode=true&characterEncoding=UTF-8: Bắt buộc có để không bị lỗi font tiếng Việt.
    private final String URL = "jdbc:mysql://localhost:3307/QuanLyThuVien?useUnicode=true&characterEncoding=UTF-8";
    
    // 2. Tài khoản đăng nhập MySQL (Bạn cài XAMPP hoặc MySQL Workbench thì thường là root/rỗng)
    private final String USER = "root"; 
    private final String PASS = ""; // <-- Điền mật khẩu MySQL của bạn vào đây (nếu có)

    // 3. Hàm tạo kết nối (Thay cho sqlConn.Open() bên C#)
    public Connection getConnection() {
        Connection conn = null;
        try {
            // Nạp Driver (Báo cho Java biết ta sắp dùng MySQL)
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Tạo đường dẫn kết nối
            conn = DriverManager.getConnection(URL, USER, PASS);
            // System.out.println("Kết nối CSDL thành công!"); // Bỏ comment dòng này để test
            
        } catch (ClassNotFoundException e) {
            System.err.println("Lỗi: Không tìm thấy thư viện MySQL Connector! Bạn đã Add External JAR chưa?");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Lỗi: Không thể kết nối đến Database! Kiểm tra lại tên DB, User, Pass.");
            e.printStackTrace();
        }
        return conn;
    }
    
    // Hàm main nhỏ để chạy thử xem kết nối được chưa
    public static void main(String[] args) {
        DBConnect db = new DBConnect();
        if(db.getConnection() != null) {
            System.out.println("KET NOI THANH CONG");
        } else {
            System.out.println("KET NOI THAT BAI");
        }
    }
}