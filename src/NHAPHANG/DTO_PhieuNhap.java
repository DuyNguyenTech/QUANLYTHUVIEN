package NHAPHANG;

import java.sql.Date;

public class DTO_PhieuNhap {
    private String maPhieu;
    private String maNCC;
    private String maNhanVien;
    private Date ngayNhap;
    private double tongTien;

    public DTO_PhieuNhap(String maPhieu, String maNCC, String maNhanVien, Date ngayNhap, double tongTien) {
        this.maPhieu = maPhieu;
        this.maNCC = maNCC;
        this.maNhanVien = maNhanVien;
        this.ngayNhap = ngayNhap;
        this.tongTien = tongTien;
    }
    
    // Getter & Setter
    public String getMaPhieu() { return maPhieu; }
    public String getMaNCC() { return maNCC; }
    public String getMaNhanVien() { return maNhanVien; }
    public Date getNgayNhap() { return ngayNhap; }
    public double getTongTien() { return tongTien; }
}