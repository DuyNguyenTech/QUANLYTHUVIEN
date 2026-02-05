package DOCGIA;

public class DocGia {
    private String maDocGia;
    private String tenDocGia;
    private String lop;
    private String diaChi;
    private String soDienThoai;

    public DocGia() {}

    public DocGia(String maDocGia, String tenDocGia, String lop, String diaChi, String soDienThoai) {
        this.maDocGia = maDocGia;
        this.tenDocGia = tenDocGia;
        this.lop = lop;
        this.diaChi = diaChi;
        this.soDienThoai = soDienThoai;
    }

    public String getMaDocGia() { return maDocGia; }
    public void setMaDocGia(String maDocGia) { this.maDocGia = maDocGia; }

    public String getTenDocGia() { return tenDocGia; }
    public void setTenDocGia(String tenDocGia) { this.tenDocGia = tenDocGia; }

    public String getLop() { return lop; }
    public void setLop(String lop) { this.lop = lop; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    
    // Alias cho GUI cũ
    public String getSdt() { return soDienThoai; }
    public void setSdt(String sdt) { this.soDienThoai = sdt; }
}