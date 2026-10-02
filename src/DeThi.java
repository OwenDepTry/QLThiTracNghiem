import java.util.ArrayList;
import java.util.List;

/**
 * Lớp DeThi - một đề thi gồm nhiều CauHoi (có thể là câu hỏi khác loại nhau).
 */
public class DeThi {
    private static int soLuongDeThi = 0;
    private static int demTuTang = 0;

    private String maDeThi;
    private String tenDeThi;
    private String maMonHoc;
    private int thoiGianLamBai; // phút
    private List<CauHoi> danhSachCauHoi;

    public DeThi(String tenDeThi, String maMonHoc, int thoiGianLamBai) {
        this.maDeThi = taoMaTuDong();
        this.tenDeThi = tenDeThi;
        this.maMonHoc = maMonHoc;
        this.thoiGianLamBai = thoiGianLamBai;
        this.danhSachCauHoi = new ArrayList<>();
        soLuongDeThi++;
    }

    public DeThi(String maDeThi, String tenDeThi, String maMonHoc, int thoiGianLamBai) {
        this.maDeThi = maDeThi;
        this.tenDeThi = tenDeThi;
        this.maMonHoc = maMonHoc;
        this.thoiGianLamBai = thoiGianLamBai;
        this.danhSachCauHoi = new ArrayList<>();
        soLuongDeThi++;
        try {
            int n = Integer.parseInt(maDeThi.replaceAll("\\D", ""));
            if (n > demTuTang) demTuTang = n;
        } catch (Exception ignored) { }
    }

    private static String taoMaTuDong() {
        demTuTang++;
        return String.format("DT%03d", demTuTang);
    }

    public static int getSoLuongDeThi() { return soLuongDeThi; }

    public void themCauHoi(CauHoi c) { danhSachCauHoi.add(c); }
    public boolean xoaCauHoi(String maCauHoi) {
        return danhSachCauHoi.removeIf(c -> c.getMaCauHoi().equals(maCauHoi));
    }

    public double tinhTongDiemToiDa() { return danhSachCauHoi.size(); }

    public String getMaDeThi() { return maDeThi; }
    public String getTenDeThi() { return tenDeThi; }
    public void setTenDeThi(String tenDeThi) { this.tenDeThi = tenDeThi; }
    public String getMaMonHoc() { return maMonHoc; }
    public void setMaMonHoc(String maMonHoc) { this.maMonHoc = maMonHoc; }
    public int getThoiGianLamBai() { return thoiGianLamBai; }
    public void setThoiGianLamBai(int t) { this.thoiGianLamBai = t; }
    public List<CauHoi> getDanhSachCauHoi() { return danhSachCauHoi; }

    public String toFileLine() {
        List<String> ids = new ArrayList<>();
        for (CauHoi c : danhSachCauHoi) ids.add(c.getMaCauHoi());
        return String.join("|", maDeThi, tenDeThi, maMonHoc, String.valueOf(thoiGianLamBai), String.join(",", ids));
    }

    @Override
    public String toString() {
        return String.format("%-8s %-25s Môn: %-8s Thời gian: %3d phút  Số câu: %d",
                maDeThi, tenDeThi, maMonHoc, thoiGianLamBai, danhSachCauHoi.size());
    }
}
