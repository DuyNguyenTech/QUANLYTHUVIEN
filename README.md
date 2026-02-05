# PHẦN MỀM QUẢN LÝ THƯ VIỆN

Dự án môn học Java - Quản lý thư viện trường học/công cộng.

## 🚀 Chức năng chính
- **Quản lý Sách:** Thêm, xóa, sửa, tìm kiếm, nhập hàng.
- **Quản lý Độc giả:** Theo dõi thông tin người mượn.
- **Quản lý Mượn/Trả:**
  - Tạo phiếu mượn (check tồn kho).
  - Tự động tính tiền phạt khi quá hạn.
  - Xem lịch sử mượn trả.
- **Thống kê:** Báo cáo số lượng sách, độc giả vi phạm.
- **Phân quyền:** Admin (Thủ thư) và Độc giả.

## 🛠 Công nghệ sử dụng
- **Ngôn ngữ:** Java (JDK 1.8 trở lên).
- **Giao diện:** Java Swing (GUI).
- **Cơ sở dữ liệu:** MySQL.
- **Kiến trúc:** Mô hình 3 lớp (3-Tier) + Tổ chức theo Module (Package by Feature).

## ⚙️ Hướng dẫn cài đặt
1. Clone dự án về máy.
2. Mở file `quanlythuvien.sql` và Import vào MySQL.
3. Mở file `src/com/qlthuvien/chung/DBConnect.java` để sửa lại thông tin đăng nhập MySQL (user/password).
4. Chạy file `GUI_Login.java` (trong gói `hethong`) để bắt đầu.

## 👤 Tác giả