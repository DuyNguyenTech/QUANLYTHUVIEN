package THONGKE;

import java.util.Date;

public class DTO_ThongKeViPham {
    private String maDocGia;
    private String tenDocGia;
    private String tenSach;
    private Date ngayMuon;
    private Date ngayHenTra;
    private long soNgayQuaHan;
    private String email; // <-- Cái này quan trọng

    public DTO_ThongKeViPham(String maDocGia, String tenDocGia, String tenSach, Date ngayMuon, Date ngayHenTra, long soNgayQuaHan, String email) {
        this.maDocGia = maDocGia;
        this.tenDocGia = tenDocGia;
        this.tenSach = tenSach;
        this.ngayMuon = ngayMuon;
        this.ngayHenTra = ngayHenTra;
        this.soNgayQuaHan = soNgayQuaHan;
        this.email = email;
    }

    // Các hàm getter
    public String getMaDocGia() { return maDocGia; }
    public String getTenDocGia() { return tenDocGia; }
    public String getTenSach() { return tenSach; }
    public Date getNgayMuon() { return ngayMuon; }
    public Date getNgayHenTra() { return ngayHenTra; }
    public long getSoNgayQuaHan() { return soNgayQuaHan; }
    public String getEmail() { return email; }
}