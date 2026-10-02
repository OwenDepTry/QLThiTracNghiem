/**
 * Lớp Lop - thông tin lớp học của sinh viên.
 */
public class Lop {
    private String maLop;
    private String tenLop;
    private String giaoVienChuNhiem;

    public Lop(String maLop, String tenLop, String giaoVienChuNhiem) {
        this.maLop = maLop;
        this.tenLop = tenLop;
        this.giaoVienChuNhiem = giaoVienChuNhiem;
    }

    public String getMaLop() { return maLop; }
    public String getTenLop() { return tenLop; }
    public void setTenLop(String tenLop) { this.tenLop = tenLop; }
    public String getGiaoVienChuNhiem() { return giaoVienChuNhiem; }
    public void setGiaoVienChuNhiem(String gv) { this.giaoVienChuNhiem = gv; }

    @Override
    public String toString() {
        return String.format("%-8s %-20s GVCN: %s", maLop, tenLop, giaoVienChuNhiem);
    }
}
