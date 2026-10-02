/**
 * Lớp SinhVien kế thừa NguoiDung.
 */
public class SinhVien extends NguoiDung {
    private String mssv;
    private String maLop;
    private String email;

    public SinhVien(String maNguoiDung, String hoTen, String ngaySinh, String diaChi,
                     String soDienThoai, String mssv, String maLop, String email) {
        super(maNguoiDung, hoTen, ngaySinh, diaChi, soDienThoai);
        this.mssv = mssv;
        this.maLop = maLop;
        this.email = email;
    }

    @Override
    public String getVaiTro() {
        return "Sinh viên";
    }

    public String getMssv() { return mssv; }
    public void setMssv(String mssv) { this.mssv = mssv; }
    public String getMaLop() { return maLop; }
    public void setMaLop(String maLop) { this.maLop = maLop; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // Ghi đè thêm để hiển thị chi tiết hơn -> minh họa đa hình
    @Override
    public void hienThi() {
        super.hienThi();
        System.out.printf("            MSSV: %-10s Lớp: %-10s Email: %s%n", mssv, maLop, email);
    }

    public String toFileLine() {
        return String.join("|", maNguoiDung, hoTen, ngaySinh, diaChi, soDienThoai, mssv, maLop, email);
    }

    public static SinhVien fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        return new SinhVien(p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7]);
    }
}
