# 📚 Hệ Thống Quản Lý Thư Viện

Phần mềm quản lý thư viện được xây dựng bằng **Java Swing (GUI)** và **MySQL**, hỗ trợ quản lý toàn diện các nghiệp vụ của thư viện từ mượn trả, nhập hàng, quản lý độc giả đến thống kê báo cáo. Hệ thống được thiết kế với giao diện hiện đại, thân thiện và tối ưu trải nghiệm người dùng.

---

## 🚀 Tính Năng Nổi Bật

Hệ thống được chia thành các phân hệ chức năng rõ ràng:

### 1. 🔐 Hệ Thống & Bảo Mật (`HETHONG`)

* **Đăng nhập & Phân quyền:** Hỗ trợ 3 vai trò: Admin (Quản trị viên), Thủ thư và Độc giả.
* **Trang chủ (Dashboard):** Giao diện Dashboard hiện đại với biểu đồ cột (Bar Chart) vẽ bằng đồ họa 2D, hiển thị trực quan tổng số sách, độc giả, phiếu đang mượn và quá hạn.
* **Thông tin cá nhân:** Xem và cập nhật thông tin tài khoản, đổi mật khẩu.

### 2. 📖 Quản Lý Sách (`SACH`)

* **Quản lý kho sách:** Thêm, xóa, sửa thông tin sách (Tên, tác giả, NXB, giá, hình ảnh...).
* **Phân loại:** Quản lý thể loại sách.
* **Tra cứu:** Tìm kiếm sách nâng cao theo nhiều tiêu chí (Mã, tên, tác giả, thể loại).

### 3. 📦 Quản Lý Nhập Hàng (`NHAPHANG`) - *New*

* **Nhà cung cấp:** Quản lý thông tin đối tác cung cấp sách.
* **Nhập hàng:** Tạo phiếu nhập sách mới, tự động tính tổng tiền.
* **Transaction:** Cơ chế giao dịch đảm bảo khi nhập hàng thành công, số lượng sách trong kho tự động tăng lên.
* **Lịch sử nhập:** Xem lại danh sách các phiếu đã nhập.

### 4. 🔄 Quản Lý Mượn Trả (`MUONTRA`)

* **Mượn sách:** Tạo phiếu mượn, kiểm tra điều kiện mượn (số lượng, hạn thẻ).
* **Trả sách:** Xử lý trả sách, tự động tính tiền phạt nếu quá hạn hoặc mất sách.
* **Lịch sử:** Độc giả có thể xem lại lịch sử mượn trả của mình.

### 5. 👥 Quản Lý Con Người (`DOCGIA`, `THUTHU`)

* **Độc giả:** Quản lý hồ sơ, cấp thẻ, gia hạn thẻ, khóa thẻ vi phạm.
* **Thủ thư:** (Dành cho Admin) Quản lý nhân viên, cấp tài khoản đăng nhập cho nhân viên.

### 6. 📊 Thống Kê & Báo Cáo (`THONGKE`)

* Thống kê số lượng sách theo thể loại.
* Thống kê sách mượn nhiều nhất (Top trending).
* Thống kê độc giả mượn nhiều nhất.
* Thống kê doanh thu tiền phạt.
* **Xuất Excel:** Hỗ trợ xuất các báo cáo ra file Excel.

### 7. ⚙️ Cài Đặt & Tiện Ích (`CAIDAT`, `CHUNG`)

* **Cấu hình hệ thống:** Tùy chỉnh quy định thư viện (Số ngày được mượn, số sách tối đa, tiền phạt/ngày) ngay trên giao diện.
* **Sao lưu & Phục hồi:** Backup dữ liệu ra file `.sql` và Restore lại khi cần thiết (tích hợp lệnh `mysqldump`).
* **Gửi Email:** Tự động gửi email thông báo (ví dụ: OTP, thông báo quá hạn).

---

## 🛠️ Yêu Cầu Hệ Thống & Thư Viện

Để chạy được dự án, bạn cần cài đặt và thêm các thư viện sau vào `Build Path` của dự án:

### 1. Phần mềm

* **Java Development Kit (JDK):** Phiên bản 8 trở lên (Khuyên dùng JDK 17 hoặc 21).
* **Database:** MySQL Server (hoặc XAMPP/WAMP).
* **IDE:** Eclipse, NetBeans hoặc IntelliJ IDEA.

### 2. Thư viện (File .JAR cần import)

Bạn cần tải các file `.jar` sau và thêm vào thư mục `lib` hoặc `Referenced Libraries`:

* **MySQL Connector J:** (`mysql-connector-j-8.x.x.jar`) - Để kết nối CSDL.
* **Apache POI:** (Bộ thư viện `poi-x.x.x.jar`, `poi-ooxml-x.x.x.jar`, `commons-io`, `xmlbeans`...) - Dùng cho chức năng xuất Excel.
* **JavaMail API:** (`javax.mail.jar`, `activation.jar`) - Dùng cho chức năng gửi Email.
* **JCalendar:** (`jcalendar-1.4.jar`) - Dùng cho các ô chọn ngày tháng (Date Chooser).

---

## 🚀 Hướng Dẫn Cài Đặt & Chạy (Installation)

### Bước 1: Clone dự án

```bash
git clone https://github.com/duynguyentech/quanlythuvien.git

```

### Bước 2: Thiết lập Cơ sở dữ liệu

1. Mở **XAMPP** (hoặc MySQL Workbench), khởi động Apache và MySQL.
2. Truy cập **phpMyAdmin** (http://localhost/phpmyadmin).
3. Tạo một database mới tên là: `quanlythuvien`.
4. Chọn database vừa tạo, vào mục **Import**, chọn file `quanlythuvien.sql` (nằm trong thư mục gốc của dự án) và bấm **Go**.

### Bước 3: Cấu hình kết nối Database

Mở file `src/CHUNG/DBConnect.java` trong IDE của bạn và kiểm tra thông tin kết nối:

```java
// Ví dụ cấu hình
private String url = "jdbc:mysql://localhost:3306/quanlythuvien"; // Kiểm tra Port (3306 hoặc 3307)
private String user = "root"; // Tên đăng nhập MySQL
private String password = ""; // Mật khẩu MySQL (thường là rỗng trên XAMPP)

```

*Lưu ý: Nếu XAMPP của bạn đổi cổng MySQL (ví dụ 3307), hãy sửa lại url thành `jdbc:mysql://localhost:3307/quanlythuvien`.*

### Bước 4: Import thư viện (Dependencies)

* Trong **Eclipse**: Chuột phải vào dự án -> `Build Path` -> `Configure Build Path` -> Tab `Libraries` -> `Add External JARs` -> Chọn các file `.jar` đã tải ở mục Yêu cầu.

### Bước 5: Chạy ứng dụng

* Tìm file **`src/HETHONG/GUI_Login.java`**.
* Chuột phải -> `Run As` -> `Java Application`.

---

## 🔑 Tài Khoản Đăng Nhập Mặc Định

Dữ liệu mẫu có sẵn trong file SQL:

| Vai Trò | Tên Đăng Nhập | Mật Khẩu |
| --- | --- | --- |
| **Admin** | `admin` | `111` |
| **Thủ Thư** | `staff` | `111` |
| **Độc Giả** | `an` | `111` |

---

## 📸 Hình Ảnh Demo
* *Màn hình Dashboard thống kê.*
* *Giao diện nhập hàng.*
* *Giao diện mượn trả.*

---

## 🤝 Đóng Góp

Mọi đóng góp, báo lỗi hoặc yêu cầu tính năng mới đều được hoan nghênh. Vui lòng tạo [Issue](https://www.google.com/search?q=https://github.com/duynguyentech/quanlythuvien/issues) hoặc gửi [Pull Request](https://www.google.com/search?q=https://github.com/duynguyentech/quanlythuvien/pulls).

---

**Developed by Hai Chúng Toi**