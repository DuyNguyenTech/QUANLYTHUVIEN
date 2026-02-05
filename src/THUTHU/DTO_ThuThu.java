package THUTHU;

import java.sql.Date;

public class DTO_ThuThu {
    // Thông tin cá nhân (Bảng thu_thu)
    private String maThuThu;
    private String tenThuThu;
    private Date ngaySinh;
    private String gioiTinh;
    private String diaChi;
    private String soDienThoai;

    // Thông tin tài khoản (Bảng tai_khoan) - Dữ liệu kèm theo
    private String tenDangNhap;
    private String matKhau;
    private int phanQuyen; // 1: Admin, 2: Thủ thư/Nhân viên

    public DTO_ThuThu() {}

    public DTO_ThuThu(String maThuThu, String tenThuThu, Date ngaySinh, String gioiTinh, String diaChi, String soDienThoai, String tenDangNhap, String matKhau, int phanQuyen) {
        this.maThuThu = maThuThu;
        this.tenThuThu = tenThuThu;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.diaChi = diaChi;
        this.soDienThoai = soDienThoai;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.phanQuyen = phanQuyen;
    }

    // Getters & Setters
    public String getMaThuThu() { return maThuThu; }
    public void setMaThuThu(String maThuThu) { this.maThuThu = maThuThu; }
    public String getTenThuThu() { return tenThuThu; }
    public void setTenThuThu(String tenThuThu) { this.tenThuThu = tenThuThu; }
    public Date getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(Date ngaySinh) { this.ngaySinh = ngaySinh; }
    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    
    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }
    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }
    public int getPhanQuyen() { return phanQuyen; }
    public void setPhanQuyen(int phanQuyen) { this.phanQuyen = phanQuyen; }
}