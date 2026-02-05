
# HỆ THỐNG QUẢN LÝ THƯ VIỆN

Dự án phần mềm quản lý thư viện được xây dựng bằng ngôn ngữ **Java (Swing)** và hệ quản trị cơ sở dữ liệu **MySQL**. Hệ thống được thiết kế theo mô hình 3 lớp (3-Tier Architecture) và tổ chức mã nguồn theo hướng Module hóa (Package by Feature) để dễ dàng quản lý và mở rộng.

##  GIỚI THIỆU HỆ THỐNG

Hệ thống giải quyết trọn vẹn quy trình vận hành của một thư viện trường học hoặc thư viện công cộng quy mô vừa và nhỏ.

### Các Module chính:
1.  **HETHONG (System):** Quản lý đăng nhập, phân quyền, thông tin cá nhân.
2.  **SACH (Books):** Quản lý kho sách, thể loại, tra cứu và giỏ sách (cho độc giả).
3.  **DOCGIA (Readers):** Quản lý hồ sơ độc giả, cấp thẻ.
4.  **MUONTRA (Loans):** Xử lý quy trình mượn trả, gia hạn, phạt quá hạn, lịch sử giao dịch.
5.  **THUTHU (Staff):** Quản lý nhân sự, phân quyền (chỉ Admin).
6.  **THONGKE (Statistics):** Báo cáo số liệu trực quan.
7.  **CHUNG (Common):** Các tiện ích dùng chung (Kết nối DB, Xuất Excel, Email).

##  HƯỚNG DẪN SỬ DỤNG

Hệ thống phân chia 3 quyền hạn chính tương ứng với 3 tài khoản mẫu bạn cung cấp.

### 1️ Vai trò: QUẢN TRỊ VIÊN (ADMIN)
* **Tài khoản:** `admin`
* **Mật khẩu:** `111`
* **Quyền hạn:** Toàn quyền hệ thống (Full Access).

**Các chức năng dành riêng cho Admin:**
* **Quản lý Thủ thư (Module THUTHU):**
    * Vào menu **"Quản Lý Thủ Thư"**.
    * Có thể Thêm nhân viên mới (Staff), Xóa nhân viên nghỉ việc hoặc Sửa thông tin.
    * Cấp quyền truy cập cho nhân viên.
* **Xem Thống kê (Module THONGKE):**
    * Vào menu **"Thống Kê"**.
    * Xem biểu đồ số lượng sách mượn theo tháng/năm.
    * Xem Top độc giả mượn nhiều nhất.
    * Báo cáo doanh thu tiền phạt.
* *Ngoài ra, Admin có thể thực hiện mọi chức năng của Staff.*

### 2️ Vai trò: THỦ THƯ / NHÂN VIÊN (STAFF)
* **Tài khoản:** `staff`
* **Mật khẩu:** `111`
* **Quyền hạn:** Quản lý nghiệp vụ hàng ngày (Sách, Độc giả, Mượn trả).

**Hướng dẫn nghiệp vụ:**
* **Quản lý Sách (Module SACH):**
    * Vào **"Quản Lý Sách"** để nhập sách mới, cập nhật số lượng tồn kho hoặc chỉnh sửa thông tin sách.
    * Quản lý danh mục tại **"Quản Lý Thể Loại"**.
* **Quản lý Độc giả (Module DOCGIA):**
    * Vào **"Quản Lý Độc Giả"** để tạo thẻ mới cho người đọc.
* **Duyệt Phiếu Mượn (Module MUONTRA):**
    * Khi độc giả gửi yêu cầu mượn Online, vào **"Quản Lý Mượn Trả"**.
    * Tìm phiếu có trạng thái *"Chờ duyệt"* -> Kiểm tra thông tin -> Bấm **Duyệt**.
    * Hệ thống sẽ tự động trừ kho sách và ghi nhận tên Nhân viên duyệt phiếu.
* **Trả Sách & Phạt:**
    * Khi độc giả trả sách, tìm phiếu mượn tương ứng -> Bấm **"Trả Sách"**.
    * Nếu quá hạn, hệ thống tự động tính tiền phạt. Nhân viên thu tiền và xác nhận hoàn tất.

### 3️ Vai trò: ĐỘC GIẢ (READER)
* **Tài khoản:** `duynt` (Ví dụ cho tài khoản độc giả Duy Nguyễn Tech)
* **Mật khẩu:** `111`
* **Quyền hạn:** Tra cứu, Đặt mượn online, Xem lịch sử.

**Hướng dẫn trải nghiệm:**
* **Tra cứu & Mượn sách (Module SACH):**
    * Đăng nhập -> Chọn **"Tra Cứu Sách"**.
    * Tìm kiếm sách theo Tên, Tác giả hoặc Thể loại.
    * Bấm **"Thêm vào giỏ"** các cuốn sách muốn mượn.
    * Bấm nút **"Giỏ sách"** -> Kiểm tra lại -> Bấm **"Gửi Yêu Cầu"**.
* **Xem lịch sử (Module MUONTRA):**
    * Vào menu **"Lịch Sử Mượn"**.
    * Theo dõi trạng thái phiếu mượn: *Chờ duyệt, Đang mượn, Đã trả* hoặc *Quá hạn*.
    * Xem chi tiết tên sách và ngày hẹn trả để tránh bị phạt.

##  HƯỚNG DẪN CÀI ĐẶT (INSTALLATION)

Để chạy được dự án trên máy cá nhân, vui lòng thực hiện đúng theo các bước sau:

### Bước 1: Clone dự án
Mở Terminal hoặc Git Bash và chạy lệnh:
```bash
git clone [https://github.com/DuyNguyenTech/QUANLYTHUVIEN.git](https://github.com/DuyNguyenTech/QUANLYTHUVIEN.git)

```

### Bước 2: Cài đặt Cơ sở dữ liệu (Database)

1. Mở phần mềm quản lý Database (phpMyAdmin, MySQL Workbench, HeidiSQL...).
2. Tạo một database mới tên là: **`quanlythuvien`**.
* *Lưu ý: Bắt buộc đặt đúng tên này hoặc phải sửa lại trong code.*


3. Import file **`quanlythuvien.sql`** (nằm ở thư mục gốc của dự án vừa clone) vào database vừa tạo.
* *File này đã chứa sẵn cấu trúc bảng và dữ liệu mẫu cho các tài khoản admin, staff, duynt.*



### Bước 3: Cấu hình kết nối Java

1. Mở dự án bằng IDE (Eclipse, IntelliJ IDEA, NetBeans).
2. Tìm đến file cấu hình: `src/CHUNG/DBConnect.java`.
3. Chỉnh sửa thông tin `username` và `password` cho khớp với MySQL trên máy bạn:
```java
private String url = "jdbc:mysql://localhost:3306/quanlythuvien";
private String user = "root"; // Tên đăng nhập MySQL của bạn
private String pass = "";     // Mật khẩu MySQL của bạn (nếu không có thì để trống)

```
### Bước 4: Chạy ứng dụng

1. Tìm đến file chạy chính: `src/HETHONG/GUI_Login.java`.
2. Chuột phải -> **Run As** -> **Java Application**.
3. Đăng nhập bằng các tài khoản mẫu đã cung cấp ở trên (ví dụ: `admin` / `111`) để bắt đầu sử dụng.

---

**© 2026 Developed by...