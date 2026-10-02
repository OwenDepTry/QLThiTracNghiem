import java.util.ArrayList;
import java.util.List;

/**
 * Lớp trừu tượng CauHoi - lớp cha của CauHoiMotDapAn, CauHoiNhieuDapAn.
 * Cài đặt interface ICoTheCham nhưng để phương thức chamDiem() cho lớp con
 * tự hiện thực theo cách riêng (đa hình).
 */
public abstract class CauHoi implements ICoTheCham {

    protected static int soLuongCauHoi = 0;
    private static int demTuTang = 0;

    protected String maCauHoi;
    protected String noiDung;
    protected String maMonHoc;
    protected String doKho;      // "Dễ", "Trung bình", "Khó"
    protected List<DapAn> danhSachDapAn;

    public CauHoi(String noiDung, String maMonHoc, String doKho, List<DapAn> danhSachDapAn) {
        this.maCauHoi = taoMaTuDong();
        this.noiDung = noiDung;
        this.maMonHoc = maMonHoc;
        this.doKho = doKho;
        this.danhSachDapAn = danhSachDapAn != null ? danhSachDapAn : new ArrayList<>();
        soLuongCauHoi++;
    }

    public CauHoi(String maCauHoi, String noiDung, String maMonHoc, String doKho, List<DapAn> danhSachDapAn) {
        this.maCauHoi = maCauHoi;
        this.noiDung = noiDung;
        this.maMonHoc = maMonHoc;
        this.doKho = doKho;
        this.danhSachDapAn = danhSachDapAn != null ? danhSachDapAn : new ArrayList<>();
        soLuongCauHoi++;
        try {
            int n = Integer.parseInt(maCauHoi.replaceAll("\\D", ""));
            if (n > demTuTang) demTuTang = n;
        } catch (Exception ignored) { }
    }

    private static String taoMaTuDong() {
        demTuTang++;
        return String.format("CH%04d", demTuTang);
    }

    public static int getSoLuongCauHoi() { return soLuongCauHoi; }

    /** Phương thức trừu tượng: mỗi loại câu hỏi có tên loại khác nhau -> đa hình */
    public abstract String loaiCauHoi();

    public String getMaCauHoi() { return maCauHoi; }
    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }
    public String getMaMonHoc() { return maMonHoc; }
    public void setMaMonHoc(String maMonHoc) { this.maMonHoc = maMonHoc; }
    public String getDoKho() { return doKho; }
    public void setDoKho(String doKho) { this.doKho = doKho; }
    public List<DapAn> getDanhSachDapAn() { return danhSachDapAn; }
    public void setDanhSachDapAn(List<DapAn> danhSachDapAn) { this.danhSachDapAn = danhSachDapAn; }

    public void hienThiCauHoi() {
        System.out.printf("[%s] (%s - %s) Môn: %s%n    %s%n",
                maCauHoi, loaiCauHoi(), doKho, maMonHoc, noiDung);
        int i = 0;
        for (DapAn d : danhSachDapAn) {
            System.out.printf("      %d. %s%n", i++, d);
        }
    }

    public String toFileLine() {
        StringBuilder sb = new StringBuilder();
        sb.append(maCauHoi).append("|").append(loaiCauHoi()).append("|").append(maMonHoc)
          .append("|").append(doKho).append("|").append(noiDung).append("|");
        List<String> parts = new ArrayList<>();
        for (DapAn d : danhSachDapAn) {
            parts.add(d.getNoiDung().replace("~", " ").replace(";", ",") + "~" + (d.isDapAnDung() ? "1" : "0"));
        }
        sb.append(String.join(";", parts));
        return sb.toString();
    }

    public static CauHoi fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        String ma = p[0], loai = p[1], mon = p[2], doKhoStr = p[3], noiDung = p[4];
        List<DapAn> ds = new ArrayList<>();
        if (p.length > 5 && !p[5].isEmpty()) {
            for (String part : p[5].split(";")) {
                String[] dp = part.split("~");
                ds.add(new DapAn(dp[0], dp.length > 1 && dp[1].equals("1")));
            }
        }
        if (loai.equals("Nhiều đáp án")) {
            return new CauHoiNhieuDapAn(ma, noiDung, mon, doKhoStr, ds);
        } else {
            return new CauHoiMotDapAn(ma, noiDung, mon, doKhoStr, ds);
        }
    }
}
