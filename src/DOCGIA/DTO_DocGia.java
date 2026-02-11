package DOCGIA;

import java.sql.Date;

public class DTO_DocGia {
    private String maDocGia;
    private String tenDocGia;
    private Date ngaySinh;      
    private String gioiTinh;    
    private String lop;         
    private String sdt;         
    private String diaChi;

    // 1. Constructor rỗng (Bắt buộc phải có để DAL tạo đối tượng)
    public DTO_DocGia() {}

    // 2. Constructor FULL tham số (MỚI - Dùng để bao quát mọi trường hợp)
    public DTO_DocGia(String maDocGia, String tenDocGia, Date ngaySinh, String gioiTinh, String lop, String sdt, String diaChi) {
        this.maDocGia = maDocGia;
        this.tenDocGia = tenDocGia;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.lop = lop;
        this.sdt = sdt;
        this.diaChi = diaChi;
    }

    // 3. Constructor cho DAL cũ (Giữ nguyên để không lỗi code cũ)
    public DTO_DocGia(String maDocGia, String tenDocGia, Date ngaySinh, String gioiTinh, String sdt, String diaChi) {
        this.maDocGia = maDocGia;
        this.tenDocGia = tenDocGia;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.sdt = sdt;
        this.diaChi = diaChi;
    }

    // 4. Constructor cho GUI Thêm cũ (Giữ nguyên để không lỗi form Thêm)
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

    public String getLop() { return lop; }
    public void setLop(String lop) { this.lop = lop; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    // --- XỬ LÝ ĐỒNG BỘ SĐT (Alias) ---
    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }
    
    // Alias giúp GUI và DAL gọi getSoDienThoai() không bị lỗi
    public String getSoDienThoai() { return sdt; } 
    public void setSoDienThoai(String sdt) { this.sdt = sdt; }
}