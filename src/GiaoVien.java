/**
 * Lớp GiaoVien kế thừa NguoiDung.
 */
public class GiaoVien extends NguoiDung {
    private String chuyenMon;
    private String hocVi;

    public GiaoVien(String maNguoiDung, String hoTen, String ngaySinh, String diaChi,
                     String soDienThoai, String chuyenMon, String hocVi) {
        super(maNguoiDung, hoTen, ngaySinh, diaChi, soDienThoai);
        this.chuyenMon = chuyenMon;
        this.hocVi = hocVi;
    }

    @Override
    public String getVaiTro() {
        return "Giáo viên";
    }

    public String getChuyenMon() { return chuyenMon; }
    public void setChuyenMon(String chuyenMon) { this.chuyenMon = chuyenMon; }
    public String getHocVi() { return hocVi; }
    public void setHocVi(String hocVi) { this.hocVi = hocVi; }

    @Override
    public void hienThi() {
        super.hienThi();
        System.out.printf("            Học vị: %-10s Chuyên môn: %s%n", hocVi, chuyenMon);
    }

    public String toFileLine() {
        return String.join("|", maNguoiDung, hoTen, ngaySinh, diaChi, soDienThoai, chuyenMon, hocVi);
    }

    public static GiaoVien fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        return new GiaoVien(p[0], p[1], p[2], p[3], p[4], p[5], p[6]);
    }
}
