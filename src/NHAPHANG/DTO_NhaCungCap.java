package NHAPHANG;

public class DTO_NhaCungCap {
    private String maNCC;
    private String tenNCC;
    private String diaChi;
    private String sdt;

    public DTO_NhaCungCap() {}

    public DTO_NhaCungCap(String maNCC, String tenNCC, String diaChi, String sdt) {
        this.maNCC = maNCC;
        this.tenNCC = tenNCC;
        this.diaChi = diaChi;
        this.sdt = sdt;
    }

    public String getMaNCC() { return maNCC; }
    public void setMaNCC(String maNCC) { this.maNCC = maNCC; }
    public String getTenNCC() { return tenNCC; }
    public void setTenNCC(String tenNCC) { this.tenNCC = tenNCC; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }
    
    @Override
    public String toString() {
        return tenNCC; // Để hiển thị tên trong các danh sách chọn sau này
    }
}