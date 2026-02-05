package THONGKE;

public class DTO_ThongKeSach {
    private String maSach;
    private String tenSach;
    private String tinhTrang; 

    public DTO_ThongKeSach() { }

    public DTO_ThongKeSach(String maSach, String tenSach, String tinhTrang) {
        this.maSach = maSach;
        this.tenSach = tenSach;
        this.tinhTrang = tinhTrang;
    }

    public String getMaSach() { return maSach; }
    public void setMaSach(String maSach) { this.maSach = maSach; }

    public String getTenSach() { return tenSach; }
    public void setTenSach(String tenSach) { this.tenSach = tenSach; }

    public String getTinhTrang() { return tinhTrang; }
    public void setTinhTrang(String tinhTrang) { this.tinhTrang = tinhTrang; }
}