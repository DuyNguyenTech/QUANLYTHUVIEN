-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1:3307
-- Thời gian đã tạo: Th2 06, 2026 lúc 05:18 PM
-- Phiên bản máy phục vụ: 10.4.32-MariaDB
-- Phiên bản PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `quanlythuvien`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `chi_tiet_nhap`
--

CREATE TABLE `chi_tiet_nhap` (
  `MaPhieuNhap` varchar(50) NOT NULL,
  `MaSach` varchar(50) NOT NULL,
  `SoLuong` int(11) DEFAULT 0,
  `DonGia` decimal(15,2) DEFAULT 0.00,
  `ThanhTien` decimal(15,2) DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `chi_tiet_nhap`
--

INSERT INTO `chi_tiet_nhap` (`MaPhieuNhap`, `MaSach`, `SoLuong`, `DonGia`, `ThanhTien`) VALUES
('PN2602062216', 'S012', 100, 20000.00, 2000000.00);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `chi_tiet_phieu_muon`
--

CREATE TABLE `chi_tiet_phieu_muon` (
  `MaPhieuMuon` varchar(50) NOT NULL,
  `MaCuonSach` varchar(50) NOT NULL,
  `TinhTrangSach` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `chi_tiet_phieu_muon`
--

INSERT INTO `chi_tiet_phieu_muon` (`MaPhieuMuon`, `MaCuonSach`, `TinhTrangSach`) VALUES
('PM01', 'S001', 'Đang mượn'),
('PM01', 'S002', 'Đang mượn'),
('PM02', 'S003', 'Đang mượn'),
('PM04', 'S006', 'Mới'),
('PM05', 'S007', 'Mới'),
('PM05', 'S008', 'Mới'),
('PM1770221007', 'S002', 'Đang mượn'),
('PM1770394336', 'S003', 'Đang mượn'),
('PM2602042158', 'S002', 'Đang mượn'),
('PM2602042159', 'S004', 'Đang mượn'),
('PM2602042220', 'S008', 'Đang mượn'),
('PM346388', 'S005', 'Đang mượn'),
('PM417193', 'S011', 'Đang mượn'),
('PM542067', 'S001', 'Đang mượn'),
('PM583006', 'S001', 'Đang mượn');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `doc_gia`
--

CREATE TABLE `doc_gia` (
  `MaDocGia` varchar(50) NOT NULL,
  `TenDocGia` varchar(100) NOT NULL,
  `Lop` varchar(50) DEFAULT NULL,
  `NgaySinh` date DEFAULT NULL,
  `GioiTinh` varchar(10) DEFAULT NULL,
  `DiaChi` varchar(255) DEFAULT NULL,
  `SoDienThoai` varchar(15) DEFAULT NULL,
  `NgayLapThe` date DEFAULT NULL,
  `NgayHetHan` date DEFAULT NULL,
  `HoatDong` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `doc_gia`
--

INSERT INTO `doc_gia` (`MaDocGia`, `TenDocGia`, `Lop`, `NgaySinh`, `GioiTinh`, `DiaChi`, `SoDienThoai`, `NgayLapThe`, `NgayHetHan`, `HoatDong`) VALUES
('DG001', 'Nguyễn Thị Ngọc Ánh', '10A1', NULL, NULL, 'Khu A - KTX', '0987654321', NULL, NULL, 1),
('DG002', 'Trần Văn Tâm', '11B2', NULL, NULL, 'Khu B - KTX', '0912345678', NULL, NULL, 1),
('DG003', 'Lê Thái Kiệt', '12C3', NULL, NULL, 'Quận 1, TP.HCM', '0909123456', NULL, NULL, 1),
('DG004', 'Phạm Hoàng Anh', '10A5', NULL, NULL, 'Thủ Đức, TP.HCM', '0385123456', NULL, NULL, 1),
('DG005', 'Ngô Thành Lợi', '11B1', NULL, NULL, 'Khu C - KTX', '0978123123', NULL, NULL, 1),
('DG006', 'Võ Thị Hồng Nhung', '12A4', NULL, NULL, 'Bình Thạnh, TP.HCM', '0369852147', NULL, NULL, 1),
('DG007', 'Nguyễn Lý Lý', '10C2', NULL, NULL, 'Quận 9, TP.HCM', '0933456789', NULL, NULL, 1),
('DG008', 'Trần Mỹ Lin', '11A8', NULL, NULL, 'Khu A - KTX', '0868123456', NULL, NULL, 1),
('DG010', 'Nguyễn Thị Ý Nhi', '10A3', NULL, NULL, 'Quận 3, TP.HCM', '0399887766', NULL, NULL, 1),
('dg099', 'du a', '12', NULL, NULL, 'h', '0123456789', NULL, NULL, 1),
('DG0999', 'khanh', '12A9', NULL, NULL, 'soc trang', '0910293939', NULL, NULL, 1),
('DG4918', 'an', 'dh22', '2000-02-02', 'Nữ', 'ct', '0123456789', '2026-01-23', '2026-07-23', 1),
('DG7540', 'duy', '12D4', '2026-01-21', 'Nam', 'hg', '000000000000000', '2026-01-21', '2026-07-21', 1);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `nha_cung_cap`
--

CREATE TABLE `nha_cung_cap` (
  `MaNCC` varchar(20) NOT NULL,
  `TenNCC` varchar(255) NOT NULL,
  `DiaChi` varchar(255) DEFAULT NULL,
  `SDT` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `nha_cung_cap`
--

INSERT INTO `nha_cung_cap` (`MaNCC`, `TenNCC`, `DiaChi`, `SDT`) VALUES
('NCC001', 'NXB Kim Đồng', '55 Quang Trung, Hà Nội', '02439434730'),
('NCC002', 'Nhà Sách Fahasa', '60-62 Lê Lợi, TP.HCM', '1900636467'),
('NCC003', 'NXB Trẻ', '161B Lý Chính Thắng, TP.HCM', '0283931628');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `phieu_muon`
--

CREATE TABLE `phieu_muon` (
  `MaPhieuMuon` varchar(50) NOT NULL,
  `MaDocGia` varchar(50) DEFAULT NULL,
  `MaThuThu` varchar(50) DEFAULT NULL,
  `NgayMuon` date DEFAULT NULL,
  `NgayHenTra` date DEFAULT NULL,
  `NgayTra` date DEFAULT NULL,
  `TinhTrang` varchar(50) DEFAULT NULL,
  `TienPhat` decimal(15,2) DEFAULT 0.00,
  `GhiChu` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `phieu_muon`
--

INSERT INTO `phieu_muon` (`MaPhieuMuon`, `MaDocGia`, `MaThuThu`, `NgayMuon`, `NgayHenTra`, `NgayTra`, `TinhTrang`, `TienPhat`, `GhiChu`) VALUES
('PM01', 'DG008', 'TT001', '2026-01-28', '2026-02-04', NULL, 'Đang mượn', 0.00, 'Mượn sách mới'),
('PM02', 'DG006', 'TT001', '2026-01-08', '2026-01-15', NULL, 'Quá hạn', 0.00, 'Liên hệ độc giả gấp'),
('PM04', 'DG004', 'TT001', '2023-09-01', '2023-09-08', '2023-09-10', 'Đã trả', 5000.00, 'Trễ 2 ngày'),
('PM05', 'DG005', 'TT002', '2026-01-27', '2026-02-03', NULL, 'Đang mượn', 0.00, ''),
('PM1770221007', 'DG001', 'admin', '2026-02-04', '2026-02-11', NULL, NULL, 0.00, NULL),
('PM1770394336', 'DG004', 'admin', '2026-02-06', '2026-02-13', NULL, NULL, 0.00, NULL),
('PM2602042158', 'DG4918', 'staff', '2026-02-04', '2026-02-11', NULL, NULL, 0.00, NULL),
('PM2602042159', 'DG4918', 'staff', '2026-02-04', '2026-02-11', NULL, NULL, 0.00, NULL),
('PM2602042220', 'DG4918', 'admin', '2026-02-04', '2026-02-11', NULL, NULL, 0.00, NULL),
('PM346388', 'DG006', 'TT001', '2026-01-27', '2026-01-28', '2026-01-29', 'Đã trả', 5000.00, ''),
('PM417193', 'DG003', 'TT001', '2026-01-28', '2026-02-04', NULL, 'Đang mượn', 0.00, ''),
('PM542067', 'DG001', 'TT001', '2026-01-28', '2026-02-04', NULL, 'Đang mượn', 0.00, ''),
('PM583006', 'DG4918', 'TT001', '2026-01-27', '2026-01-28', NULL, 'Đang mượn', 0.00, '');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `phieu_nhap`
--

CREATE TABLE `phieu_nhap` (
  `MaPhieuNhap` varchar(50) NOT NULL,
  `MaNCC` varchar(20) DEFAULT NULL,
  `MaNhanVien` varchar(50) DEFAULT NULL,
  `NgayNhap` date DEFAULT NULL,
  `TongTien` decimal(15,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `phieu_nhap`
--

INSERT INTO `phieu_nhap` (`MaPhieuNhap`, `MaNCC`, `MaNhanVien`, `NgayNhap`, `TongTien`) VALUES
('PN2602062216', 'NCC002', 'admin', '2026-02-06', 2000000.00);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `sach`
--

CREATE TABLE `sach` (
  `MaCuonSach` varchar(50) NOT NULL,
  `MaSach` varchar(50) DEFAULT NULL,
  `TenSach` varchar(255) NOT NULL,
  `TacGia` varchar(100) DEFAULT NULL,
  `NamXuatBan` int(11) DEFAULT NULL,
  `NhaXuatBan` varchar(100) DEFAULT NULL,
  `Gia` decimal(15,2) DEFAULT NULL,
  `MoTa` text DEFAULT NULL,
  `TinhTrang` varchar(50) DEFAULT NULL,
  `SoLuong` int(11) DEFAULT 0,
  `HinhAnh` varchar(255) DEFAULT NULL,
  `MaTheLoai` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `sach`
--

INSERT INTO `sach` (`MaCuonSach`, `MaSach`, `TenSach`, `TacGia`, `NamXuatBan`, `NhaXuatBan`, `Gia`, `MoTa`, `TinhTrang`, `SoLuong`, `HinhAnh`, `MaTheLoai`) VALUES
('cs099', '344', 'f', 'ưe', 34, 'egeg', 1233.00, '', 'Mới', 4, '', 'TL06'),
('s000999', NULL, 'an', 'a', 90, 'ee', 1233.00, '', NULL, 12, '', 'TL01'),
('S001', 'ISBN-001', 'Clean Code - Mã Sạch', 'Robert C. Martin', 2020, 'NXB Tôn Giáo', 185000.00, 'Cuốn sách gối đầu giường của mọi lập trình viên.', 'Mới', 8, 'clean_code.jpg', 'TL01'),
('S002', 'ISBN-002', 'Lập Trinh Java Cơ Bản', 'Phạm Văn Ất', 2022, 'NXB Thanh Niên', 120000.00, 'Giáo trình nhập môn Java.', 'Mới', 48, 'java_basic.jpg', 'TL01'),
('S003', 'ISBN-003', 'Head First Design Patterns', 'Eric Freeman', 2021, 'O Reilly', 350000.00, 'Học về mẫu thiết kế phần mềm.', 'Mới', 4, 'design_pattern.jpg', 'TL01'),
('S004', 'ISBN-004', 'Dế Mèn Phiêu Lưu Ký', 'Tô Hoài', 2019, 'NXB Kim Đồng', 45000.00, 'Tác phẩm văn học thiếu nhi kinh điển.', 'Cũ', 11, 'de_men.jpg', 'TL02'),
('S005', 'ISBN-005', 'Mắt Biếc', 'Nguyễn Nhật Ánh', 2023, 'NXB Trẻ', 110000.00, 'Câu chuyện tình yêu buồn.', 'Mới', 19, 'mat_biec.jpg', 'TL02'),
('S006', 'ISBN-006', 'Số Đỏ', 'Vũ Trọng Phụng', 2018, 'NXB Văn Học', 60000.00, 'Tiểu thuyết trào phúng.', 'Cũ', 3, 'so_do.jpg', 'TL02'),
('S007', 'ISBN-007', 'Harry Potter và Hòn Đá Phù Thủy', 'J.K. Rowling', 2020, 'NXB Trẻ', 150000.00, 'Tập 1 của series Harry Potter.', 'Mới', 15, 'harry_potter_1.jpg', 'TL03'),
('S008', 'ISBN-008', 'Nhà Giả Kim', 'Paulo Coelho', 2021, 'NXB Văn Học', 79000.00, 'Hành trình theo đuổi ước mơ.', 'Mới', 99, 'nha_gia_kim.jpg', 'TL03'),
('S009', 'ISBN-009', 'Cha Giàu Cha Nghèo', 'Robert Kiyosaki', 2022, 'NXB Trẻ', 95000.00, 'Tư duy tài chính cá nhân.', 'Mới', 31, 'cha_giau.jpg', 'TL04'),
('S010', 'ISBN-010', 'Marketing Giỏi Phải Kiếm Được Tiền', 'Sergio Zyman', 2019, 'NXB Lao Động', 125000.00, 'Chiến lược Marketing thực chiến.', 'Cũ', 2, 'marketing.jpg', 'TL04'),
('S011', 'ISBN-011', 'Đắc Nhân Tâm', 'Dale Carnegie', 2023, 'NXB Tổng Hợp', 86000.00, 'Nghệ thuật thu phục lòng người.', 'Mới', 39, 'dac_nhan_tam.jpg', 'TL05'),
('S0110', NULL, 'lap trinh', 'nhieu tac gia', 2023, 'giao duc', 345000.00, 'mo ta', NULL, 100, 'D:\\KNN_Java\\QUANLYTHUVIEN\\src\\com\\qlthuvien\\IMAGES\\book_images\\7.jpg', 'TL01'),
('S012', 'ISBN-012', 'Tuổi Trẻ Đáng Giá Bao Nhiêu', 'Rosie Nguyễn', 2020, 'NXB Hội Nhà Văn', 75000.00, 'Sách truyền cảm hứng cho giới trẻ.', 'Mới', 25, 'tuoi_tre.jpg', 'TL05'),
('S013', 'ISBN-013', 'Vũ Trụ Trong Vỏ Hạt Dẻ', 'Stephen Hawking', 2018, 'NXB Tri Thức', 115000.00, 'Khám phá vũ trụ huyền bí.', 'Cũ', 1, 'vu_tru.jpg', 'TL06'),
('S014', 'ISBN-014', 'Sapiens: Lược Sử Loài Người', 'Yuval Noah Harari', 2021, 'NXB Tri Thức', 210000.00, 'Lịch sử tiến hóa của nhân loại.', 'Mới', 8, 'sapiens.jpg', 'TL06'),
('S015', 'ISBN-015', 'Gen: Lịch Sử Và Tương Lai', 'Siddhartha Mukherjee', 2022, 'NXB Dân Trí', 190000.00, 'Kiến thức về di truyền học.', 'Mới', 4, 'gen.jpg', 'TL06'),
('s023', '23', 'h', 'f', 4444, 'ffs', 2.00, 'ddddddddddddddddddddddddddddd', 'Mới', 16, '5.jpg', 'TL03'),
('S067', NULL, 'code', 'duy', 2000, 'a', 123.00, 'code dễ lắm', NULL, 100, 'D:\\KNN_Java\\QUANLYTHUVIEN\\src\\com\\qlthuvien\\IMAGES\\book_images\\6.jpg', 'TL05');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `tai_khoan`
--

CREATE TABLE `tai_khoan` (
  `TenDangNhap` varchar(50) NOT NULL,
  `MatKhau` varchar(50) NOT NULL,
  `PhanQuyen` int(11) DEFAULT 1,
  `MaThuThu` varchar(50) DEFAULT NULL,
  `Email` varchar(100) DEFAULT NULL,
  `MaDocGia` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `tai_khoan`
--

INSERT INTO `tai_khoan` (`TenDangNhap`, `MatKhau`, `PhanQuyen`, `MaThuThu`, `Email`, `MaDocGia`) VALUES
('admin', '111', 1, 'TT001', NULL, NULL),
('an', '111', 3, NULL, 'nduy78999@gmail.com ', 'DG4918'),
('duynt', '111', 3, NULL, 'phuocnp25@gmail.com', 'DG7540'),
('NVDuy', '123', 2, 'TT5613', NULL, NULL),
('staff', '111', 2, 'TT002', NULL, NULL);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `tham_so`
--

CREATE TABLE `tham_so` (
  `TenThamSo` varchar(50) NOT NULL,
  `GiaTri` varchar(255) DEFAULT NULL,
  `MoTa` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `tham_so`
--

INSERT INTO `tham_so` (`TenThamSo`, `GiaTri`, `MoTa`) VALUES
('DuongDanBackup', 'D:/Backup_QLTV', 'Thư mục mặc định để lưu file backup'),
('SoNgayMuonToiDa', '14', 'Số ngày tối đa độc giả được mượn sách'),
('SoSachMuonToiDa', '5', 'Số lượng sách tối đa được mượn'),
('TienPhatMoiNgay', '4000', 'Số tiền phạt cho mỗi ngày quá hạn (VNĐ)');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `the_loai`
--

CREATE TABLE `the_loai` (
  `MaTheLoai` varchar(50) NOT NULL,
  `TenTheLoai` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `the_loai`
--

INSERT INTO `the_loai` (`MaTheLoai`, `TenTheLoai`) VALUES
('TL01', 'Công Nghệ Thông Tin'),
('TL02', 'Văn Học Việt Nam'),
('TL03', 'Tiểu Thuyết Nước Ngoài'),
('TL04', 'Kinh Tế & Quản Trị'),
('TL05', 'Kỹ Năng Sống'),
('TL06', 'Khoa Học - Đời Sống'),
('TL07', 'Sinh Tồn'),
('TL08', 'Kỹ năng'),
('TL09', 'Học Cách Làm Người');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `thu_thu`
--

CREATE TABLE `thu_thu` (
  `MaThuThu` varchar(50) NOT NULL,
  `TenThuThu` varchar(100) NOT NULL,
  `NgaySinh` date DEFAULT NULL,
  `GioiTinh` varchar(10) DEFAULT NULL,
  `DiaChi` varchar(255) DEFAULT NULL,
  `SoDienThoai` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `thu_thu`
--

INSERT INTO `thu_thu` (`MaThuThu`, `TenThuThu`, `NgaySinh`, `GioiTinh`, `DiaChi`, `SoDienThoai`) VALUES
('admin', 'Quản Trị Viên', NULL, NULL, NULL, NULL),
('ONLINE', 'Hệ Thống Tự Động', NULL, NULL, NULL, NULL),
('staff', 'Nhân Viên Staff', NULL, NULL, NULL, NULL),
('TT001', 'Nguyen Van Admin', '1990-01-01', 'Nam', 'Can Tho', '0909123456'),
('TT002', 'Tran Thi Nhan Vien', '1995-05-05', 'Nam', 'Vinh Long', '0909999888'),
('TT5613', 'Nguyen Thanh Duy', '2004-05-28', 'Nam', 'Hau Giang', '0337106030');

--
-- Chỉ mục cho các bảng đã đổ
--

--
-- Chỉ mục cho bảng `chi_tiet_nhap`
--
ALTER TABLE `chi_tiet_nhap`
  ADD PRIMARY KEY (`MaPhieuNhap`,`MaSach`),
  ADD KEY `MaSach` (`MaSach`);

--
-- Chỉ mục cho bảng `chi_tiet_phieu_muon`
--
ALTER TABLE `chi_tiet_phieu_muon`
  ADD PRIMARY KEY (`MaPhieuMuon`,`MaCuonSach`),
  ADD KEY `MaCuonSach` (`MaCuonSach`);

--
-- Chỉ mục cho bảng `doc_gia`
--
ALTER TABLE `doc_gia`
  ADD PRIMARY KEY (`MaDocGia`);

--
-- Chỉ mục cho bảng `nha_cung_cap`
--
ALTER TABLE `nha_cung_cap`
  ADD PRIMARY KEY (`MaNCC`);

--
-- Chỉ mục cho bảng `phieu_muon`
--
ALTER TABLE `phieu_muon`
  ADD PRIMARY KEY (`MaPhieuMuon`),
  ADD KEY `MaDocGia` (`MaDocGia`),
  ADD KEY `MaThuThu` (`MaThuThu`);

--
-- Chỉ mục cho bảng `phieu_nhap`
--
ALTER TABLE `phieu_nhap`
  ADD PRIMARY KEY (`MaPhieuNhap`),
  ADD KEY `MaNCC` (`MaNCC`),
  ADD KEY `MaNhanVien` (`MaNhanVien`);

--
-- Chỉ mục cho bảng `sach`
--
ALTER TABLE `sach`
  ADD PRIMARY KEY (`MaCuonSach`),
  ADD KEY `MaTheLoai` (`MaTheLoai`);

--
-- Chỉ mục cho bảng `tai_khoan`
--
ALTER TABLE `tai_khoan`
  ADD PRIMARY KEY (`TenDangNhap`),
  ADD KEY `MaThuThu` (`MaThuThu`),
  ADD KEY `FK_TK_DG` (`MaDocGia`);

--
-- Chỉ mục cho bảng `tham_so`
--
ALTER TABLE `tham_so`
  ADD PRIMARY KEY (`TenThamSo`);

--
-- Chỉ mục cho bảng `the_loai`
--
ALTER TABLE `the_loai`
  ADD PRIMARY KEY (`MaTheLoai`);

--
-- Chỉ mục cho bảng `thu_thu`
--
ALTER TABLE `thu_thu`
  ADD PRIMARY KEY (`MaThuThu`);

--
-- Các ràng buộc cho các bảng đã đổ
--

--
-- Các ràng buộc cho bảng `chi_tiet_nhap`
--
ALTER TABLE `chi_tiet_nhap`
  ADD CONSTRAINT `chi_tiet_nhap_ibfk_1` FOREIGN KEY (`MaPhieuNhap`) REFERENCES `phieu_nhap` (`MaPhieuNhap`) ON DELETE CASCADE,
  ADD CONSTRAINT `chi_tiet_nhap_ibfk_2` FOREIGN KEY (`MaSach`) REFERENCES `sach` (`MaCuonSach`) ON DELETE CASCADE;

--
-- Các ràng buộc cho bảng `chi_tiet_phieu_muon`
--
ALTER TABLE `chi_tiet_phieu_muon`
  ADD CONSTRAINT `chi_tiet_phieu_muon_ibfk_1` FOREIGN KEY (`MaPhieuMuon`) REFERENCES `phieu_muon` (`MaPhieuMuon`) ON DELETE CASCADE,
  ADD CONSTRAINT `chi_tiet_phieu_muon_ibfk_2` FOREIGN KEY (`MaCuonSach`) REFERENCES `sach` (`MaCuonSach`);

--
-- Các ràng buộc cho bảng `phieu_muon`
--
ALTER TABLE `phieu_muon`
  ADD CONSTRAINT `phieu_muon_ibfk_1` FOREIGN KEY (`MaDocGia`) REFERENCES `doc_gia` (`MaDocGia`),
  ADD CONSTRAINT `phieu_muon_ibfk_2` FOREIGN KEY (`MaThuThu`) REFERENCES `thu_thu` (`MaThuThu`);

--
-- Các ràng buộc cho bảng `phieu_nhap`
--
ALTER TABLE `phieu_nhap`
  ADD CONSTRAINT `phieu_nhap_ibfk_1` FOREIGN KEY (`MaNCC`) REFERENCES `nha_cung_cap` (`MaNCC`) ON DELETE SET NULL,
  ADD CONSTRAINT `phieu_nhap_ibfk_2` FOREIGN KEY (`MaNhanVien`) REFERENCES `thu_thu` (`MaThuThu`) ON DELETE SET NULL;

--
-- Các ràng buộc cho bảng `sach`
--
ALTER TABLE `sach`
  ADD CONSTRAINT `sach_ibfk_1` FOREIGN KEY (`MaTheLoai`) REFERENCES `the_loai` (`MaTheLoai`) ON DELETE SET NULL;

--
-- Các ràng buộc cho bảng `tai_khoan`
--
ALTER TABLE `tai_khoan`
  ADD CONSTRAINT `FK_TK_DG` FOREIGN KEY (`MaDocGia`) REFERENCES `doc_gia` (`MaDocGia`),
  ADD CONSTRAINT `tai_khoan_ibfk_1` FOREIGN KEY (`MaThuThu`) REFERENCES `thu_thu` (`MaThuThu`) ON DELETE SET NULL;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
