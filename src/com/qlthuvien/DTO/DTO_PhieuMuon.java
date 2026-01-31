package com.qlthuvien.DTO;

import java.sql.Date;

public class DTO_PhieuMuon {
    private String maPhieuMuon; // SQL: MaPhieuMuon
    private String maDocGia;
    private String maThuThu;    // SQL: MaThuThu
    private Date ngayMuon;
    private Date ngayHenTra;    // SQL: NgayHenTra
    private Date ngayTra;       // Mới thêm
    private String tinhTrang;
    private double tienPhat;    // Mới thêm
    private String ghiChu;
    
    // Biến phụ hỗ trợ hiển thị (không có trong bảng)
    private int soLuongSach; 

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

    // Getters & Setters
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
}