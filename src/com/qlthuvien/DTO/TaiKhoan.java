package com.qlthuvien.DTO;

public class TaiKhoan {
    private String userName;
    private String password;
    private int phanQuyen;
    private String maThuThu;
    private String maDocGia;
    private String email;

    public TaiKhoan() {}

    public TaiKhoan(String userName, String password, int phanQuyen, String maThuThu, String maDocGia, String email) {
        this.userName = userName;
        this.password = password;
        this.phanQuyen = phanQuyen;
        this.maThuThu = maThuThu;
        this.maDocGia = maDocGia;
        this.email = email;
    }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getPhanQuyen() { return phanQuyen; }
    public void setPhanQuyen(int phanQuyen) { this.phanQuyen = phanQuyen; }

    public String getMaThuThu() { return maThuThu; }
    public void setMaThuThu(String maThuThu) { this.maThuThu = maThuThu; }

    public String getMaDocGia() { return maDocGia; }
    public void setMaDocGia(String maDocGia) { this.maDocGia = maDocGia; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}