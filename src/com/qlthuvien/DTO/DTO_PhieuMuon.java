package com.qlthuvien.DTO;

import java.sql.Date;

public class DTO_PhieuMuon {
    private String maPhieuMuon; 
    private String maDocGia;
    private String maThuThu;    
    private Date ngayMuon;
    private Date ngayHenTra;    
    private Date ngayTra;       
    private String tinhTrang;
    private double tienPhat;    
    private String ghiChu;
    
    // [MỚI] Biến phụ hỗ trợ hiển thị
    private int soLuongSach; 
    private String tenSach; // Thêm biến này để hiện tên sách trong lịch sử

    public DTO_PhieuMuon() {}

    public DTO_PhieuMuon(String maPhieuMuon, String maDocGia, String maThuThu, Date ngayMuon, Date ngayHenTra, Date ngayTra, String tinhTrang, double tienPhat, String ghiChu) {
        this.maPhieuMuon = maPhieuMuon;
        this.maDocGia = maDocGia;
        this.maThuThu = maThuThu;
        this.ngayMuon = ngayMuon;
        this.ngayHenTra = ngayHenTra;
        this.ngayTra = ngayTra;
        this.tinhTrang = tinhTrang;
        this.tienPhat = tienPhat;
        this.ghiChu = ghiChu;
    }

    // Getters & Setters cũ
    public String getMaPhieuMuon() { return maPhieuMuon; }
    public void setMaPhieuMuon(String maPhieuMuon) { this.maPhieuMuon = maPhieuMuon; }

    public String getMaDocGia() { return maDocGia; }
    public void setMaDocGia(String maDocGia) { this.maDocGia = maDocGia; }

    public String getMaThuThu() { return maThuThu; }
    public void setMaThuThu(String maThuThu) { this.maThuThu = maThuThu; }

    public Date getNgayMuon() { return ngayMuon; }
    public void setNgayMuon(Date ngayMuon) { this.ngayMuon = ngayMuon; }

    public Date getNgayHenTra() { return ngayHenTra; }
    public void setNgayHenTra(Date ngayHenTra) { this.ngayHenTra = ngayHenTra; }

    public Date getNgayTra() { return ngayTra; }
    public void setNgayTra(Date ngayTra) { this.ngayTra = ngayTra; }

    public String getTinhTrang() { return tinhTrang; }
    public void setTinhTrang(String tinhTrang) { this.tinhTrang = tinhTrang; }

    public double getTienPhat() { return tienPhat; }
    public void setTienPhat(double tienPhat) { this.tienPhat = tienPhat; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public int getSoLuongSach() { return soLuongSach; }
    public void setSoLuongSach(int soLuongSach) { this.soLuongSach = soLuongSach; }

    // [MỚI] Getter/Setter cho TenSach
    public String getTenSach() { return tenSach; }
    public void setTenSach(String tenSach) { this.tenSach = tenSach; }
}