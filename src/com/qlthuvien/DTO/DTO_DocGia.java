package com.qlthuvien.DTO;

import java.sql.Date;

public class DTO_DocGia {
    private String maDocGia;
    private String tenDocGia;
    private Date ngaySinh;      // Dùng cho DAL cũ
    private String gioiTinh;    // Dùng cho DAL cũ
    private String lop;         // MỚI: Dùng cho GUI Thêm
    private String sdt;         
    private String diaChi;

    public DTO_DocGia() {}

    // Constructor 1: Dùng cho DAL (Lấy từ CSDL ra)
    public DTO_DocGia(String maDocGia, String tenDocGia, Date ngaySinh, String gioiTinh, String sdt, String diaChi) {
        this.maDocGia = maDocGia;
        this.tenDocGia = tenDocGia;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.sdt = sdt;
        this.diaChi = diaChi;
    }

    // Constructor 2: Dùng cho GUI (Lúc nhập liệu Thêm mới)
    public DTO_DocGia(String maDocGia, String tenDocGia, String lop, String diaChi, String sdt) {
        this.maDocGia = maDocGia;
        this.tenDocGia = tenDocGia;
        this.lop = lop;
        this.diaChi = diaChi;
        this.sdt = sdt;
    }

    // --- GETTER & SETTER ---
    public String getMaDocGia() { return maDocGia; }
    public void setMaDocGia(String maDocGia) { this.maDocGia = maDocGia; }

    public String getTenDocGia() { return tenDocGia; }
    public void setTenDocGia(String tenDocGia) { this.tenDocGia = tenDocGia; }

    public Date getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(Date ngaySinh) { this.ngaySinh = ngaySinh; }

    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }

    // --- BỔ SUNG GETTER/SETTER CHO LỚP ---
    public String getLop() { return lop; }
    public void setLop(String lop) { this.lop = lop; }

    // --- BỔ SUNG ALIAS CHO SỐ ĐIỆN THOẠI (Để khớp với GUI) ---
    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }
    
    // Hàm này giúp GUI gọi getSoDienThoai() không bị lỗi
    public String getSoDienThoai() { return sdt; } 
    public void setSoDienThoai(String sdt) { this.sdt = sdt; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
}