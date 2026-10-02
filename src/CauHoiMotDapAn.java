import java.util.List;

/**
 * Câu hỏi chỉ có 1 đáp án đúng (trắc nghiệm 1 lựa chọn).
 */
public class CauHoiMotDapAn extends CauHoi {

    public CauHoiMotDapAn(String noiDung, String maMonHoc, String doKho, List<DapAn> danhSachDapAn) {
        super(noiDung, maMonHoc, doKho, danhSachDapAn);
    }

    public CauHoiMotDapAn(String maCauHoi, String noiDung, String maMonHoc, String doKho, List<DapAn> danhSachDapAn) {
        super(maCauHoi, noiDung, maMonHoc, doKho, danhSachDapAn);
    }

    @Override
    public String loaiCauHoi() {
        return "Một đáp án";
    }

    // Đa hình: cách chấm điểm riêng cho câu hỏi 1 đáp án
    @Override
    public double chamDiem(List<Integer> luaChonCuaSV) {
        if (luaChonCuaSV == null || luaChonCuaSV.size() != 1) return 0.0;
        int idx = luaChonCuaSV.get(0);
        if (idx < 0 || idx >= danhSachDapAn.size()) return 0.0;
        return danhSachDapAn.get(idx).isDapAnDung() ? 1.0 : 0.0;
    }
}
