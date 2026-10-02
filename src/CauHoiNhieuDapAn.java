import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Câu hỏi có nhiều đáp án đúng (trắc nghiệm nhiều lựa chọn).
 */
public class CauHoiNhieuDapAn extends CauHoi {

    public CauHoiNhieuDapAn(String noiDung, String maMonHoc, String doKho, List<DapAn> danhSachDapAn) {
        super(noiDung, maMonHoc, doKho, danhSachDapAn);
    }

    public CauHoiNhieuDapAn(String maCauHoi, String noiDung, String maMonHoc, String doKho, List<DapAn> danhSachDapAn) {
        super(maCauHoi, noiDung, maMonHoc, doKho, danhSachDapAn);
    }

    @Override
    public String loaiCauHoi() {
        return "Nhiều đáp án";
    }

    // Đa hình: cách chấm điểm riêng cho câu hỏi nhiều đáp án (có tính điểm từng phần)
    @Override
    public double chamDiem(List<Integer> luaChonCuaSV) {
        Set<Integer> dapAnDung = new HashSet<>();
        for (int i = 0; i < danhSachDapAn.size(); i++) {
            if (danhSachDapAn.get(i).isDapAnDung()) dapAnDung.add(i);
        }
        if (luaChonCuaSV == null || luaChonCuaSV.isEmpty() || dapAnDung.isEmpty()) return 0.0;

        Set<Integer> daChon = new HashSet<>(luaChonCuaSV);
        if (daChon.equals(dapAnDung)) return 1.0; // chọn đúng chính xác toàn bộ

        int soChonDung = 0, soChonSai = 0;
        for (int i : daChon) {
            if (dapAnDung.contains(i)) soChonDung++; else soChonSai++;
        }
        double diem = (double) (soChonDung - soChonSai) / dapAnDung.size();
        return Math.max(0.0, diem);
    }
}
