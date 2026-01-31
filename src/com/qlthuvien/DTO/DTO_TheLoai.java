package com.qlthuvien.DTO;

public class DTO_TheLoai {
    private String maTheLoai;
    private String tenTheLoai;

    public DTO_TheLoai() {
    }

    public DTO_TheLoai(String maTheLoai, String tenTheLoai) {
        this.maTheLoai = maTheLoai;
        this.tenTheLoai = tenTheLoai;
    }

    public String getMaTheLoai() { return maTheLoai; }
    public void setMaTheLoai(String maTheLoai) { this.maTheLoai = maTheLoai; }

    public String getTenTheLoai() { return tenTheLoai; }
    public void setTenTheLoai(String tenTheLoai) { this.tenTheLoai = tenTheLoai; }
    
    // Dùng để hiển thị trong ComboBox (nếu cần)
    @Override
    public String toString() {
        return tenTheLoai;
    }
}