/**
 * Lớp MonHoc.
 * Minh họa thuộc tính static (đếm số lượng, bộ đếm sinh mã) và phương thức static.
 */
public class MonHoc {
    private static int soLuongMonHoc = 0;
    private static int demTuTang = 0;

    private String maMon;
    private String tenMon;
    private int soTinChi;

    // Constructor tự sinh mã môn học
    public MonHoc(String tenMon, int soTinChi) {
        this.maMon = taoMaTuDong();
        this.tenMon = tenMon;
        this.soTinChi = soTinChi;
        soLuongMonHoc++;
    }

    // Constructor dùng khi đọc dữ liệu có sẵn mã từ file
    public MonHoc(String maMon, String tenMon, int soTinChi) {
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.soTinChi = soTinChi;
        soLuongMonHoc++;
        capNhatBoDem(maMon);
    }

    private static String taoMaTuDong() {
        demTuTang++;
        return String.format("MH%03d", demTuTang);
    }

    private static void capNhatBoDem(String maMon) {
        try {
            int n = Integer.parseInt(maMon.replaceAll("\\D", ""));
            if (n > demTuTang) demTuTang = n;
        } catch (Exception ignored) { }
    }

    public static int getSoLuongMonHoc() { return soLuongMonHoc; }

    public String getMaMon() { return maMon; }
    public String getTenMon() { return tenMon; }
    public void setTenMon(String tenMon) { this.tenMon = tenMon; }
    public int getSoTinChi() { return soTinChi; }
    public void setSoTinChi(int soTinChi) { this.soTinChi = soTinChi; }

    public String toFileLine() {
        return String.join("|", maMon, tenMon, String.valueOf(soTinChi));
    }

    public static MonHoc fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        return new MonHoc(p[0], p[1], Integer.parseInt(p[2]));
    }

    @Override
    public String toString() {
        return String.format("%-8s %-28s Số tín chỉ: %d", maMon, tenMon, soTinChi);
    }
}
