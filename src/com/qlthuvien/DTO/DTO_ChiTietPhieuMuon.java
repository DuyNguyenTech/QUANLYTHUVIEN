package com.qlthuvien.DTO;

public class DTO_ChiTietPhieuMuon {
    private String maPhieuMuon;
    private String maCuonSach;
    private String tinhTrangSach;

    public DTO_ChiTietPhieuMuon() {}

    public DTO_ChiTietPhieuMuon(String maPhieuMuon, String maCuonSach, String tinhTrangSach) {
        this.maPhieuMuon = maPhieuMuon;
        this.maCuonSach = maCuonSach;
        this.tinhTrangSach = tinhTrangSach;
    }

    public String getMaPhieuMuon() { return maPhieuMuon; }
    public void setMaPhieuMuon(String maPhieuMuon) { this.maPhieuMuon = maPhieuMuon; }

    public String getMaCuonSach() { return maCuonSach; }
    public void setMaCuonSach(String maCuonSach) { this.maCuonSach = maCuonSach; }

    public String getTinhTrangSach() { return tinhTrangSach; }
    public void setTinhTrangSach(String tinhTrangSach) { this.tinhTrangSach = tinhTrangSach; }
}