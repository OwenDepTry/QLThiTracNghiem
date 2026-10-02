import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Lớp mảng các đối tượng (danh sách) dùng chung (generic) cho mọi loại đối tượng:
 * SinhVien, GiaoVien, MonHoc, CauHoi, DeThi, KetQuaThi...
 * Cung cấp đầy đủ: thêm, xóa, sửa (thông qua đối tượng lấy ra), tìm kiếm, liệt kê.
 */
public class DanhSachDoiTuong<T> {
    private List<T> ds;

    public DanhSachDoiTuong() {
        this.ds = new ArrayList<>();
    }

    public void them(T obj) { ds.add(obj); }

    public boolean xoa(T obj) { return ds.remove(obj); }

    public boolean xoaTheoDieuKien(Predicate<T> dieuKien) { return ds.removeIf(dieuKien); }

    public T timTheoDieuKien(Predicate<T> dieuKien) {
        for (T t : ds) {
            if (dieuKien.test(t)) return t;
        }
        return null;
    }

    public List<T> timDanhSach(Predicate<T> dieuKien) {
        List<T> ketQua = new ArrayList<>();
        for (T t : ds) {
            if (dieuKien.test(t)) ketQua.add(t);
        }
        return ketQua;
    }

    public List<T> layTatCa() { return ds; }

    public int soLuong() { return ds.size(); }
}
