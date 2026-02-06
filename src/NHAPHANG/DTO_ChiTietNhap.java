package NHAPHANG;

public class DTO_ChiTietNhap {
    private String maPhieu;
    private String maSach;
    private int soLuong;
    private double donGia;
    private double thanhTien;

    public DTO_ChiTietNhap(String maPhieu, String maSach, int soLuong, double donGia) {
        this.maPhieu = maPhieu;
        this.maSach = maSach;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = soLuong * donGia;
    }

    public String getMaPhieu() { return maPhieu; }
    public String getMaSach() { return maSach; }
    public int getSoLuong() { return soLuong; }
    public double getDonGia() { return donGia; }
    public double getThanhTien() { return thanhTien; }
}