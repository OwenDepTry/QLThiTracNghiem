/**
 * Lớp KetQuaThi - kết quả (điểm số, xếp loại) của một bài làm.
 */
public class KetQuaThi {
    private String maBaiLam;
    private String mssv;
    private String maDeThi;
    private double diemSo;
    private String xepLoai;

    public KetQuaThi(String maBaiLam, String mssv, String maDeThi, double diemSo) {
        this.maBaiLam = maBaiLam;
        this.mssv = mssv;
        this.maDeThi = maDeThi;
        this.diemSo = diemSo;
        this.xepLoai = xepLoaiTheoDiem(diemSo);
    }

    // Phương thức static: quy tắc xếp loại dùng chung, không phụ thuộc đối tượng cụ thể
    public static String xepLoaiTheoDiem(double diem) {
        if (diem >= 8.5) return "Giỏi";
        if (diem >= 7.0) return "Khá";
        if (diem >= 5.0) return "Trung bình";
        return "Yếu";
    }

    public String getMaBaiLam() { return maBaiLam; }
    public String getMssv() { return mssv; }
    public String getMaDeThi() { return maDeThi; }
    public double getDiemSo() { return diemSo; }
    public String getXepLoai() { return xepLoai; }

    public String toFileLine() {
        return String.join("|", maBaiLam, mssv, maDeThi, String.valueOf(diemSo), xepLoai);
    }

    public static KetQuaThi fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        return new KetQuaThi(p[0], p[1], p[2], Double.parseDouble(p[3]));
    }

    @Override
    public String toString() {
        return String.format("%-8s SV: %-8s Đề: %-8s Điểm: %5.2f  Xếp loại: %s",
                maBaiLam, mssv, maDeThi, diemSo, xepLoai);
    }
}
