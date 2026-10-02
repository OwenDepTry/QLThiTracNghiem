import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lớp ThongKe - chỉ chứa các phương thức static, không cần khởi tạo đối tượng.
 */
public class ThongKe {

    public static double diemTrungBinh(List<KetQuaThi> ds) {
        if (ds.isEmpty()) return 0;
        double tong = 0;
        for (KetQuaThi kq : ds) tong += kq.getDiemSo();
        return Math.round((tong / ds.size()) * 100.0) / 100.0;
    }

    public static long demSoDat(List<KetQuaThi> ds, double diemChuan) {
        long dem = 0;
        for (KetQuaThi kq : ds) if (kq.getDiemSo() >= diemChuan) dem++;
        return dem;
    }

    public static KetQuaThi diemCaoNhat(List<KetQuaThi> ds) {
        KetQuaThi max = null;
        for (KetQuaThi kq : ds) if (max == null || kq.getDiemSo() > max.getDiemSo()) max = kq;
        return max;
    }

    public static Map<String, Long> thongKeXepLoai(List<KetQuaThi> ds) {
        Map<String, Long> map = new LinkedHashMap<>();
        for (String loai : new String[]{"Giỏi", "Khá", "Trung bình", "Yếu"}) map.put(loai, 0L);
        for (KetQuaThi kq : ds) map.merge(kq.getXepLoai(), 1L, Long::sum);
        return map;
    }
}
