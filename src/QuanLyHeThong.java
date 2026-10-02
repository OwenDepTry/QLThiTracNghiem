import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lớp QuanLyHeThong - trung tâm nghiệp vụ, quản lý toàn bộ danh sách đối tượng
 * và thao tác đọc/ghi file, chấm điểm tự động (minh họa đa hình).
 */
public class QuanLyHeThong {
    private DanhSachDoiTuong<SinhVien> dsSinhVien = new DanhSachDoiTuong<>();
    private DanhSachDoiTuong<GiaoVien> dsGiaoVien = new DanhSachDoiTuong<>();
    private DanhSachDoiTuong<MonHoc> dsMonHoc = new DanhSachDoiTuong<>();
    private DanhSachDoiTuong<CauHoi> dsCauHoi = new DanhSachDoiTuong<>();
    private DanhSachDoiTuong<DeThi> dsDeThi = new DanhSachDoiTuong<>();
    private DanhSachDoiTuong<KetQuaThi> dsKetQua = new DanhSachDoiTuong<>();

    private static final String THU_MUC = "data";

    public DanhSachDoiTuong<SinhVien> getDsSinhVien() { return dsSinhVien; }
    public DanhSachDoiTuong<GiaoVien> getDsGiaoVien() { return dsGiaoVien; }
    public DanhSachDoiTuong<MonHoc> getDsMonHoc() { return dsMonHoc; }
    public DanhSachDoiTuong<CauHoi> getDsCauHoi() { return dsCauHoi; }
    public DanhSachDoiTuong<DeThi> getDsDeThi() { return dsDeThi; }
    public DanhSachDoiTuong<KetQuaThi> getDsKetQua() { return dsKetQua; }

    /**
     * Chấm điểm tự động cho một bài làm.
     * Với mỗi câu hỏi, gọi cauHoi.chamDiem(...) thông qua tham chiếu kiểu CauHoi (lớp cha),
     * nhưng thực thi sẽ chạy đúng theo lớp con thực sự (CauHoiMotDapAn / CauHoiNhieuDapAn)
     * -> đây chính là minh họa TÍNH ĐA HÌNH (runtime polymorphism).
     */
    public KetQuaThi chamThiTuDong(BaiLamThi baiLam, DeThi deThi) {
        double tongDiemDat = 0;
        Map<String, CauHoi> mapCauHoi = new HashMap<>();
        for (CauHoi c : deThi.getDanhSachCauHoi()) mapCauHoi.put(c.getMaCauHoi(), c);

        for (CauTraLoi tl : baiLam.getCauTraLoiList()) {
            CauHoi c = mapCauHoi.get(tl.getMaCauHoi());
            if (c != null) {
                tongDiemDat += c.chamDiem(tl.getLuaChon()); // <-- gọi đa hình
            }
        }
        double tongCauHoi = deThi.getDanhSachCauHoi().size();
        double diem10 = tongCauHoi == 0 ? 0 : (tongDiemDat / tongCauHoi) * 10;
        diem10 = Math.round(diem10 * 100.0) / 100.0;

        KetQuaThi kq = new KetQuaThi(baiLam.getMaBaiLam(), baiLam.getMssv(), baiLam.getMaDeThi(), diem10);
        dsKetQua.them(kq);
        return kq;
    }

    public void luuTatCa() {
        List<String> l1 = new ArrayList<>();
        for (SinhVien sv : dsSinhVien.layTatCa()) l1.add(sv.toFileLine());
        FileManager.ghiFile(THU_MUC + "/sinhvien.txt", l1);

        List<String> l2 = new ArrayList<>();
        for (GiaoVien gv : dsGiaoVien.layTatCa()) l2.add(gv.toFileLine());
        FileManager.ghiFile(THU_MUC + "/giaovien.txt", l2);

        List<String> l3 = new ArrayList<>();
        for (MonHoc mh : dsMonHoc.layTatCa()) l3.add(mh.toFileLine());
        FileManager.ghiFile(THU_MUC + "/monhoc.txt", l3);

        List<String> l4 = new ArrayList<>();
        for (CauHoi ch : dsCauHoi.layTatCa()) l4.add(ch.toFileLine());
        FileManager.ghiFile(THU_MUC + "/cauhoi.txt", l4);

        List<String> l5 = new ArrayList<>();
        for (DeThi dt : dsDeThi.layTatCa()) l5.add(dt.toFileLine());
        FileManager.ghiFile(THU_MUC + "/dethi.txt", l5);

        List<String> l6 = new ArrayList<>();
        for (KetQuaThi kq : dsKetQua.layTatCa()) l6.add(kq.toFileLine());
        FileManager.ghiFile(THU_MUC + "/ketqua.txt", l6);

        System.out.println("Đã lưu toàn bộ dữ liệu vào thư mục '" + THU_MUC + "'.");
    }

    public void docTatCa() {
        for (String line : FileManager.docFile(THU_MUC + "/sinhvien.txt"))
            if (!line.isBlank()) dsSinhVien.them(SinhVien.fromFileLine(line));

        for (String line : FileManager.docFile(THU_MUC + "/giaovien.txt"))
            if (!line.isBlank()) dsGiaoVien.them(GiaoVien.fromFileLine(line));

        for (String line : FileManager.docFile(THU_MUC + "/monhoc.txt"))
            if (!line.isBlank()) dsMonHoc.them(MonHoc.fromFileLine(line));

        for (String line : FileManager.docFile(THU_MUC + "/cauhoi.txt"))
            if (!line.isBlank()) dsCauHoi.them(CauHoi.fromFileLine(line));

        Map<String, CauHoi> mapCauHoi = new HashMap<>();
        for (CauHoi c : dsCauHoi.layTatCa()) mapCauHoi.put(c.getMaCauHoi(), c);

        for (String line : FileManager.docFile(THU_MUC + "/dethi.txt")) {
            if (line.isBlank()) continue;
            String[] p = line.split("\\|", -1);
            DeThi dt = new DeThi(p[0], p[1], p[2], Integer.parseInt(p[3]));
            if (p.length > 4 && !p[4].isEmpty()) {
                for (String maCH : p[4].split(",")) {
                    CauHoi c = mapCauHoi.get(maCH);
                    if (c != null) dt.themCauHoi(c);
                }
            }
            dsDeThi.them(dt);
        }

        for (String line : FileManager.docFile(THU_MUC + "/ketqua.txt"))
            if (!line.isBlank()) dsKetQua.them(KetQuaThi.fromFileLine(line));

        System.out.println("Đã đọc dữ liệu từ thư mục '" + THU_MUC + "'.");
    }
}
