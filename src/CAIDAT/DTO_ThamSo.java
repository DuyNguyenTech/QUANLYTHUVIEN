package CAIDAT;

public class DTO_ThamSo {
    private String tenThamSo;
    private String giaTri;
    private String moTa;

    public DTO_ThamSo(String tenThamSo, String giaTri, String moTa) {
        this.tenThamSo = tenThamSo;
        this.giaTri = giaTri;
        this.moTa = moTa;
    }

    public DTO_ThamSo() {}

    public String getTenThamSo() { return tenThamSo; }
    public void setTenThamSo(String tenThamSo) { this.tenThamSo = tenThamSo; }
    public String getGiaTri() { return giaTri; }
    public void setGiaTri(String giaTri) { this.giaTri = giaTri; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
}