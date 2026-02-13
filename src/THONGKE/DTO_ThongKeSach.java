package THONGKE;

public class DTO_ThongKeSach {
    private String maCuonSach;
    private String tenSach;
    private String tinhTrang; // Đổi từ trangThai thành tinhTrang cho khớp với GUI

    public DTO_ThongKeSach() {}

    public DTO_ThongKeSach(String maCuonSach, String tenSach, String tinhTrang) {
        this.maCuonSach = maCuonSach;
        this.tenSach = tenSach;
        this.tinhTrang = tinhTrang;
    }

    public String getMaCuonSach() { return maCuonSach; }
    public void setMaCuonSach(String maCuonSach) { this.maCuonSach = maCuonSach; }

    public String getTenSach() { return tenSach; }
    public void setTenSach(String tenSach) { this.tenSach = tenSach; }

    public String getTinhTrang() { return tinhTrang; } // Hàm này sẽ giúp GUI hết báo lỗi
    public void setTinhTrang(String tinhTrang) { this.tinhTrang = tinhTrang; }
}



//package THONGKE;
//
//public class DTO_ThongKeSach {
//    private String maCuonSach;
//    private String tenSach;
//    private String trangThai;
//
//    public DTO_ThongKeSach() {}
//
//    public DTO_ThongKeSach(String maCuonSach, String tenSach, String trangThai) {
//        this.maCuonSach = maCuonSach;
//        this.tenSach = tenSach;
//        this.trangThai = trangThai;
//    }
//
//    public String getMaCuonSach() { return maCuonSach; }
//    public void setMaCuonSach(String maCuonSach) { this.maCuonSach = maCuonSach; }
//
//    public String getTenSach() { return tenSach; }
//    public void setTenSach(String tenSach) { this.tenSach = tenSach; }
//
//    public String getTrangThai() { return trangThai; }
//    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
//}