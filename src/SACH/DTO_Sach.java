package SACH;

public class DTO_Sach {
    // Các trường dữ liệu (Bao gồm cả cũ và mới)
    private String maCuonSach;
    private String tenSach;
    private String maTheLoai;
    private String tenTheLoai; // Dùng cho hiển thị tên
    private String tacGia;
    private int namXuatBan;
    private String nhaXuatBan; 
    private double gia;       
    private int soLuong;
    private String tinhTrang;
    private String moTa;       // Dùng cho form Thêm/Chi tiết
    private String hinhAnh;    // Dùng cho form Thêm/Chi tiết

    public DTO_Sach() { }

    public DTO_Sach(String maCuonSach, String tenSach, String maTheLoai, String tacGia, 
                    int namXuatBan, String nhaXuatBan, double gia, int soLuong, String moTa, String hinhAnh) {
        this.maCuonSach = maCuonSach;
        this.tenSach = tenSach;
        this.maTheLoai = maTheLoai;
        this.tacGia = tacGia;
        this.namXuatBan = namXuatBan;
        this.nhaXuatBan = nhaXuatBan;
        this.gia = gia;
        this.soLuong = soLuong;
        this.moTa = moTa;
        this.hinhAnh = hinhAnh;
        this.tinhTrang = (soLuong > 0) ? "Còn sách" : "Hết sách";
    }

    // --- CÁC GETTER/SETTER CHUẨN ---
    public String getMaCuonSach() { return maCuonSach; }
    public void setMaCuonSach(String maCuonSach) { this.maCuonSach = maCuonSach; }

    // [QUAN TRỌNG] Hàm giả lập để KHÔNG LÀM LỖI code cũ
    public String getMaSach() { return maCuonSach; } 
    public void setMaSach(String maSach) { this.maCuonSach = maSach; }

    public String getTenSach() { return tenSach; }
    public void setTenSach(String tenSach) { this.tenSach = tenSach; }

    public String getMaTheLoai() { return maTheLoai; }
    public void setMaTheLoai(String maTheLoai) { this.maTheLoai = maTheLoai; }

    public String getTenTheLoai() { return tenTheLoai; }
    public void setTenTheLoai(String tenTheLoai) { this.tenTheLoai = tenTheLoai; }

    public String getTacGia() { return tacGia; }
    public void setTacGia(String tacGia) { this.tacGia = tacGia; }

    public int getNamXuatBan() { return namXuatBan; }
    public void setNamXuatBan(int namXuatBan) { this.namXuatBan = namXuatBan; }

    public String getNhaXuatBan() { return nhaXuatBan; }
    public void setNhaXuatBan(String nhaXuatBan) { this.nhaXuatBan = nhaXuatBan; }

    public double getGia() { return gia; }
    public void setGia(double gia) { this.gia = gia; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { 
        this.soLuong = soLuong;
        this.tinhTrang = (soLuong > 0) ? "Còn sách" : "Hết sách";
    }

    public String getTinhTrang() { return tinhTrang; }
    public void setTinhTrang(String tinhTrang) { this.tinhTrang = tinhTrang; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }
}