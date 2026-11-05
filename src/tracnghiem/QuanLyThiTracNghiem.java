package tracnghiem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Lớp điều khiển chương trình: menu chính và menu con cho từng thực thể.
 * Mỗi thực thể chính có đủ 5 chức năng: Xem, Thêm, Sửa, Xóa, Tìm kiếm.
 * Mọi thao tác thêm/sửa/xóa gọi qua lớp danh sách nên được ghi file ngay.
 */
public class QuanLyThiTracNghiem {

    private static final String GOI_Y_GIU_NGUYEN = "(Nhấn Enter để giữ nguyên giá trị trong [ ])";

    private DanhSachMonHoc dsMonHoc;
    private DanhSachNguoiDung dsNguoiDung;
    private DanhSachCauHoi dsCauHoi;
    private DanhSachDeThi dsDeThi;
    private DanhSachKetQua dsKetQua;
    private BuoiThi buoiThi;
    private ThongKe thongKe;
    /** Lỗi định dạng mã / trường bắt buộc / liên kết giữa các file, phát hiện sau khi đọc. */
    private final List<String> loiDuLieu = new ArrayList<>();

    public QuanLyThiTracNghiem() {
        this(DanhSach.THU_MUC_MAC_DINH);
    }

    public QuanLyThiTracNghiem(String thuMucDuLieu) {
        dsMonHoc = new DanhSachMonHoc(thuMucDuLieu + "/" + DanhSachMonHoc.TEN_FILE);
        dsNguoiDung = new DanhSachNguoiDung(thuMucDuLieu + "/" + DanhSachNguoiDung.TEN_FILE);
        dsCauHoi = new DanhSachCauHoi(thuMucDuLieu + "/" + DanhSachCauHoi.TEN_FILE);
        dsDeThi = new DanhSachDeThi(thuMucDuLieu + "/" + DanhSachDeThi.TEN_FILE);
        dsKetQua = new DanhSachKetQua(thuMucDuLieu + "/" + DanhSachKetQua.TEN_FILE);
        buoiThi = new BuoiThi(dsNguoiDung, dsDeThi, dsCauHoi, dsKetQua);
        thongKe = new ThongKe(dsMonHoc, dsNguoiDung, dsCauHoi, dsDeThi, dsKetQua);
        taiDuLieu(); // TỰ ĐỘNG ĐỌC FILE KHI KHỞI ĐỘNG
    }

    // ======================= ĐỌC / GHI TOÀN BỘ =======================

    public void taiDuLieu() {
        System.out.println("Đang đọc dữ liệu từ file...");
        int soMon = dsMonHoc.docLai();
        int soNguoi = dsNguoiDung.docLai();
        int soCau = dsCauHoi.docLai();
        int soDe = dsDeThi.docLai();
        int soKetQua = dsKetQua.docLai();
        // Không tự sinh lại mã còn được lưu trong lịch sử, kể cả dữ liệu cũ bị thiếu người/đề.
        for (KetQuaThi kq : dsKetQua.getTatCa()) {
            ThiSinh.capNhatBoDem(kq.getMaThiSinh());
            DeThi.capNhatBoDem(kq.getMaDeThi());
        }
        System.out.printf("Đã đọc: %d môn học, %d người dùng, %d câu hỏi, %d đề thi, %d kết quả.%n",
                soMon, soNguoi, soCau, soDe, soKetQua);
        kiemTraDuLieu();
        if (!loiDuLieu.isEmpty()) {
            System.out.println("CẢNH BÁO: dữ liệu có " + loiDuLieu.size()
                    + " lỗi, chương trình chỉ cho xem/tìm kiếm cho tới khi sửa xong:");
            inLoiDuLieu(15);
            System.out.println("Sửa các file trong data/ rồi chọn 8 -> 3 để đọc lại (8 -> 1 để xem lại danh sách lỗi).");
        }
    }

    // ======================= KIỂM TRA DỮ LIỆU SAU KHI ĐỌC =======================

    /**
     * Kiểm tra những gì từng file riêng lẻ không tự kiểm tra được:
     * định dạng mã, trường bắt buộc, và liên kết giữa các file
     * (câu hỏi -> môn, đề -> môn/câu hỏi, kết quả -> thí sinh/đề, điểm khớp số câu đúng).
     */
    private void kiemTraDuLieu() {
        loiDuLieu.clear();
        if (dsMonHoc.coLoiDoc() || dsNguoiDung.coLoiDoc() || dsCauHoi.coLoiDoc()
                || dsDeThi.coLoiDoc() || dsKetQua.coLoiDoc()) {
            return; // đã có file đọc lỗi nên dữ liệu chưa đủ để kiểm tra liên kết
        }
        for (MonHoc mh : dsMonHoc.getTatCa()) {
            kiemTraMa(DanhSachMonHoc.TEN_FILE, mh.getMa());
            kiemTraBatBuoc(DanhSachMonHoc.TEN_FILE, mh.getMa(), "tên môn", mh.getTenMon());
        }
        for (NguoiDung nd : dsNguoiDung.getTatCa()) {
            kiemTraMa(DanhSachNguoiDung.TEN_FILE, nd.getMa());
            kiemTraBatBuoc(DanhSachNguoiDung.TEN_FILE, nd.getMa(), "họ tên", nd.getHoTen());
            if (nd instanceof ThiSinh) {
                kiemTraBatBuoc(DanhSachNguoiDung.TEN_FILE, nd.getMa(), "lớp", ((ThiSinh) nd).getLop());
            } else if (nd instanceof GiaoVien) {
                kiemTraBatBuoc(DanhSachNguoiDung.TEN_FILE, nd.getMa(), "chuyên môn", ((GiaoVien) nd).getChuyenMon());
            }
        }
        for (CauHoi ch : dsCauHoi.getTatCa()) {
            kiemTraMa(DanhSachCauHoi.TEN_FILE, ch.getMa());
            kiemTraBatBuoc(DanhSachCauHoi.TEN_FILE, ch.getMa(), "nội dung", ch.getNoiDung());
            if (dsMonHoc.timTheoMa(ch.getMaMonHoc()) == null) {
                themLoi(DanhSachCauHoi.TEN_FILE, ch.getMa() + " thuộc môn " + ch.getMaMonHoc() + " không tồn tại");
            }
        }
        for (DeThi de : dsDeThi.getTatCa()) {
            kiemTraMa(DanhSachDeThi.TEN_FILE, de.getMa());
            kiemTraBatBuoc(DanhSachDeThi.TEN_FILE, de.getMa(), "tên đề", de.getTenDe());
            if (dsMonHoc.timTheoMa(de.getMaMonHoc()) == null) {
                themLoi(DanhSachDeThi.TEN_FILE, de.getMa() + " thuộc môn " + de.getMaMonHoc() + " không tồn tại");
            }
            if (de.getSoCauHoi() == 0) {
                themLoi(DanhSachDeThi.TEN_FILE, de.getMa() + " không có câu hỏi nào");
            }
            for (String maCH : de.getDanhSachMaCauHoi()) {
                CauHoi ch = dsCauHoi.timTheoMa(maCH);
                if (ch == null) {
                    themLoi(DanhSachDeThi.TEN_FILE, de.getMa() + " chứa câu " + maCH + " không tồn tại");
                } else if (!ch.getMaMonHoc().equalsIgnoreCase(de.getMaMonHoc())) {
                    themLoi(DanhSachDeThi.TEN_FILE, de.getMa() + " chứa câu " + maCH + " của môn khác ("
                            + ch.getMaMonHoc() + ")");
                }
            }
        }
        for (KetQuaThi kq : dsKetQua.getTatCa()) {
            kiemTraMa(DanhSachKetQua.TEN_FILE, kq.getMa());
            if (!(dsNguoiDung.timTheoMa(kq.getMaThiSinh()) instanceof ThiSinh)) {
                themLoi(DanhSachKetQua.TEN_FILE, kq.getMa() + " có thí sinh " + kq.getMaThiSinh() + " không tồn tại");
            }
            DeThi de = dsDeThi.timTheoMa(kq.getMaDeThi());
            if (de == null) {
                themLoi(DanhSachKetQua.TEN_FILE, kq.getMa() + " có đề " + kq.getMaDeThi() + " không tồn tại");
                continue;
            }
            if (kq.getTongSoCau() != de.getSoCauHoi()) {
                themLoi(DanhSachKetQua.TEN_FILE, kq.getMa() + " ghi " + kq.getTongSoCau() + " câu nhưng đề "
                        + de.getMa() + " có " + de.getSoCauHoi() + " câu");
                continue;
            }
            double[] khoang = khoangDiemHopLe(de, kq.getSoCauDung());
            if (kq.getDiem() < khoang[0] || kq.getDiem() > khoang[1]) {
                themLoi(DanhSachKetQua.TEN_FILE, kq.getMa() + " điểm " + kq.getDiem() + " không khớp "
                        + kq.getSoCauDung() + "/" + kq.getTongSoCau() + " câu đúng (hợp lệ: " + moTaKhoang(khoang) + ")");
            }
        }
    }

    private void kiemTraMa(String tenFile, String ma) {
        if (!NhapLieu.kiemTraMa(ma)) {
            themLoi(tenFile, "mã \"" + ma + "\" sai định dạng (2-15 ký tự chữ in hoa, số hoặc '_')");
        }
    }

    private void kiemTraBatBuoc(String tenFile, String ma, String tenTruong, String giaTri) {
        if (giaTri == null || giaTri.isBlank()) {
            themLoi(tenFile, ma + " thiếu " + tenTruong);
        }
    }

    private void themLoi(String tenFile, String noiDung) {
        loiDuLieu.add(tenFile + ": " + noiDung);
    }

    private void inLoiDuLieu(int gioiHan) {
        for (int i = 0; i < Math.min(gioiHan, loiDuLieu.size()); i++) {
            System.out.println("  - " + loiDuLieu.get(i));
        }
        if (loiDuLieu.size() > gioiHan) {
            System.out.println("  ... và " + (loiDuLieu.size() - gioiHan) + " lỗi khác.");
        }
    }

    // ======================= KHOẢNG ĐIỂM HỢP LỆ CỦA MỘT KẾT QUẢ =======================

    /**
     * Khoảng điểm [thấp nhất, cao nhất] có thể đạt khi làm đúng hoàn toàn soCauDung câu của đề,
     * theo đúng quy tắc chấm của BuoiThi: mỗi câu 10/n điểm, câu sai được 0 điểm,
     * riêng câu nhiều đáp án chọn đúng nhưng thiếu được điểm từng phần (tối đa (m-1)/m số điểm của câu).
     * - Thấp nhất: soCauDung câu đúng, các câu còn lại 0 điểm.
     * - Cao nhất: các câu sai rơi vào những câu nhiều đáp án có điểm từng phần lớn nhất.
     * Hai đầu được làm tròn ra ngoài 0,01 vì điểm lưu trong file đã làm tròn 2 chữ số.
     */
    private double[] khoangDiemHopLe(DeThi de, int soCauDung) {
        int tongSoCau = de.getSoCauHoi();
        if (tongSoCau == 0) {
            return new double[] {0, 0};
        }
        double diemMoiCau = 10.0 / tongSoCau;
        List<Double> diemTungPhan = new ArrayList<>();
        for (String maCH : de.getDanhSachMaCauHoi()) {
            CauHoi ch = dsCauHoi.timTheoMa(maCH);
            if (ch instanceof CauHoiNhieuDapAn) { // ĐA HÌNH: chỉ loại này có điểm từng phần
                int soDapAn = ((CauHoiNhieuDapAn) ch).getDapAnDung().length();
                diemTungPhan.add(diemMoiCau * (soDapAn - 1) / soDapAn);
            }
        }
        diemTungPhan.sort(Comparator.reverseOrder());
        double thapNhat = soCauDung * diemMoiCau;
        double caoNhat = thapNhat;
        for (int i = 0; i < Math.min(tongSoCau - soCauDung, diemTungPhan.size()); i++) {
            caoNhat += diemTungPhan.get(i);
        }
        caoNhat = Math.min(10.0, caoNhat);
        return new double[] {Math.floor(thapNhat * 100 + 1e-9) / 100, Math.ceil(caoNhat * 100 - 1e-9) / 100};
    }

    private static String moTaKhoang(double[] khoang) {
        return khoang[1] - khoang[0] < 0.015
                ? String.format("%.2f", khoang[0])
                : String.format("%.2f - %.2f", khoang[0], khoang[1]);
    }

    /**
     * Nhập điểm nằm trong khoảng hợp lệ. Nếu khoảng chỉ có một giá trị (đề không có câu nhiều đáp án)
     * thì tự tính điểm, không cần nhập. macDinh != null: Enter để giữ điểm cũ nếu điểm cũ vẫn hợp lệ.
     */
    private double nhapDiemHopLe(DeThi de, int soCauDung, Double macDinh) {
        double[] khoang = khoangDiemHopLe(de, soCauDung);
        if (khoang[1] - khoang[0] < 0.015) {
            double diem = NhapLieu.lamTron2(soCauDung * 10.0 / de.getSoCauHoi());
            System.out.printf("Điểm tự tính theo số câu đúng: %.2f%n", diem);
            return diem;
        }
        System.out.println("(Câu nhiều đáp án được điểm từng phần nên điểm hợp lệ nằm trong khoảng "
                + moTaKhoang(khoang) + ")");
        String nhan = "Điểm (" + moTaKhoang(khoang) + ")";
        if (macDinh != null && macDinh >= khoang[0] && macDinh <= khoang[1]) {
            return NhapLieu.nhapSoThucTuyChon(nhan, khoang[0], khoang[1], macDinh);
        }
        if (macDinh != null) {
            System.out.println("Điểm cũ " + macDinh + " không còn khớp số câu đúng, hãy nhập lại.");
        }
        return NhapLieu.nhapSoThuc(nhan, khoang[0], khoang[1]);
    }

    public void luuTatCa() {
        boolean thanhCong = dsMonHoc.luu();
        thanhCong = dsNguoiDung.luu() && thanhCong;
        thanhCong = dsCauHoi.luu() && thanhCong;
        thanhCong = dsDeThi.luu() && thanhCong;
        thanhCong = dsKetQua.luu() && thanhCong;
        System.out.println(thanhCong ? "Đã ghi toàn bộ dữ liệu ra file." : "Có lỗi khi ghi một số file (xem thông báo ở trên).");
    }

    // ======================= MENU CHÍNH =======================

    public void chay() {
        while (true) {
            System.out.println();
            System.out.println("=========== QUẢN LÝ THI TRẮC NGHIỆM ===========");
            System.out.println("1. Quản lý môn học");
            System.out.println("2. Quản lý người dùng (thí sinh, giáo viên)");
            System.out.println("3. Quản lý câu hỏi");
            System.out.println("4. Quản lý đề thi");
            System.out.println("5. Quản lý kết quả thi");
            System.out.println("6. Làm bài thi");
            System.out.println("7. Thống kê - báo cáo");
            System.out.println("8. Đọc/ghi file dữ liệu");
            System.out.println("0. Thoát");
            int chon = NhapLieu.nhapSoNguyen("Chọn chức năng", 0, 8);
            if (chon == 0) {
                System.out.println("Tạm biệt!");
                return;
            }
            try {
                switch (chon) {
                    case 1 -> menuMonHoc();
                    case 2 -> menuNguoiDung();
                    case 3 -> menuCauHoi();
                    case 4 -> menuDeThi();
                    case 5 -> menuKetQua();
                    case 6 -> {
                        if (coFileLoi()) System.out.println("Hãy sửa file lỗi rồi chọn 8 -> 3 để đọc lại trước khi thi.");
                        else buoiThi.toChucThi();
                    }
                    case 7 -> thongKe.menu();
                    case 8 -> menuFile();
                    default -> System.out.println("Chức năng không hợp lệ.");
                }
            } catch (NoSuchElementException e) {
                throw e;
            } catch (RuntimeException e) {
                // Lưới an toàn cuối cùng: không để chương trình bị dừng đột ngột
                System.out.println("Đã xảy ra lỗi: " + e.getMessage() + ". Vui lòng thử lại.");
            }
        }
    }

    private int chonMenuCon(String tieuDe) {
        System.out.println();
        System.out.println("----- " + tieuDe + " -----");
        System.out.println("1. Xem danh sách");
        System.out.println("2. Thêm");
        System.out.println("3. Sửa");
        System.out.println("4. Xóa");
        System.out.println("5. Tìm kiếm");
        System.out.println("0. Quay lại");
        int chon = NhapLieu.nhapSoNguyen("Chọn", 0, 5);
        if (chon >= 2 && chon <= 4 && coFileLoi()) {
            System.out.println("Chỉ xem/tìm kiếm: có file lỗi. Sửa file gốc rồi chọn 8 -> 3 để đọc lại.");
            return 0;
        }
        return chon;
    }

    // ======================= TIỆN ÍCH CHUNG =======================

    /** Nhập mã mới không trùng; Enter để dùng mã tự sinh. */
    private String nhapMaMoi(String maGoiY, DanhSach<?> danhSach) {
        while (true) {
            String ma = NhapLieu.nhapMaTuyChon("Mã (Enter để dùng mã tự sinh)", maGoiY);
            boolean coLichSu = (danhSach == dsNguoiDung && !dsKetQua.timTheoThiSinh(ma).isEmpty())
                    || (danhSach == dsDeThi && !dsKetQua.timTheoDeThi(ma).isEmpty());
            if (danhSach.timTheoMa(ma) == null && !coLichSu) {
                return ma;
            }
            System.out.println("Lỗi: mã " + ma + " đã tồn tại hoặc có lịch sử, vui lòng nhập mã khác.");
        }
    }

    /** Hiển thị kết quả tìm theo mã dưới dạng bảng một dòng. */
    private <T> void hienThiTheoMa(DanhSach<T> danhSach, String ma) {
        T doiTuong = danhSach.timTheoMa(ma);
        danhSach.hienThiDanhSach(doiTuong == null ? List.of() : List.of(doiTuong));
    }

    private void thongBao(boolean thanhCong, String hanhDong, String ma) {
        if (thanhCong) {
            System.out.println("Đã " + hanhDong + " " + ma + " và ghi file thành công.");
        } else {
            System.out.println("Không thể " + hanhDong + " " + ma + ".");
        }
    }

    /** Có file lỗi thì chỉ xem, tránh thay đổi dữ liệu dựa trên danh sách thiếu. */
    private boolean coFileLoi() {
        return dsMonHoc.coLoiDoc() || dsNguoiDung.coLoiDoc() || dsCauHoi.coLoiDoc()
                || dsDeThi.coLoiDoc() || dsKetQua.coLoiDoc() || !loiDuLieu.isEmpty();
    }

    /** Chọn mã môn học đang tồn tại; macDinh != null thì Enter để giữ môn cũ. */
    private String chonMonHoc(String macDinh) {
        if (dsMonHoc.soLuong() == 0)
            throw new IllegalStateException("Chưa có môn học, hãy thêm môn trước");
        StringBuilder sb = new StringBuilder("Các môn hiện có: ");
        for (MonHoc mh : dsMonHoc.getTatCa()) {
            sb.append(mh.getMa()).append(" (").append(mh.getTenMon()).append(")  ");
        }
        System.out.println(sb);
        while (true) {
            String ma = macDinh == null
                    ? NhapLieu.nhapMa("Mã môn học")
                    : NhapLieu.nhapMaTuyChon("Mã môn học", macDinh);
            MonHoc mh = dsMonHoc.timTheoMa(ma);
            if (mh != null) {
                return mh.getMa();
            }
            System.out.println("Lỗi: không tồn tại môn học có mã " + ma + ".");
        }
    }

    // ======================= 1. MÔN HỌC =======================

    private void menuMonHoc() {
        while (true) {
            int chon = chonMenuCon("QUẢN LÝ MÔN HỌC");
            switch (chon) {
                case 0 -> {
                    return;
                }
                case 1 -> dsMonHoc.hienThiTatCa();
                case 2 -> themMonHoc();
                case 3 -> suaMonHoc();
                case 4 -> xoaMonHoc();
                case 5 -> timMonHoc();
                default -> System.out.println("Chức năng không hợp lệ.");
            }
        }
    }

    private void themMonHoc() {
        String ma = nhapMaMoi(MonHoc.sinhMa(), dsMonHoc);
        String ten = NhapLieu.nhapChuoi("Tên môn học");
        int soTinChi = NhapLieu.nhapSoNguyen("Số tín chỉ (1-10)", 1, 10);
        thongBao(dsMonHoc.them(new MonHoc(ma, ten, soTinChi)), "thêm môn học", ma);
    }

    private void suaMonHoc() {
        MonHoc cu = dsMonHoc.timTheoMa(NhapLieu.nhapMa("Mã môn học cần sửa"));
        if (cu == null) {
            System.out.println("Không tìm thấy môn học.");
            return;
        }
        dsMonHoc.hienThiDanhSach(List.of(cu));
        System.out.println(GOI_Y_GIU_NGUYEN);
        String ten = NhapLieu.nhapChuoiTuyChon("Tên môn học", cu.getTenMon());
        int soTinChi = NhapLieu.nhapSoNguyenTuyChon("Số tín chỉ (1-10)", 1, 10, cu.getSoTinChi());
        thongBao(dsMonHoc.sua(cu.getMa(), new MonHoc(cu.getMa(), ten, soTinChi)), "sửa môn học", cu.getMa());
    }

    private void xoaMonHoc() {
        MonHoc mh = dsMonHoc.timTheoMa(NhapLieu.nhapMa("Mã môn học cần xóa"));
        if (mh == null) {
            System.out.println("Không tìm thấy môn học.");
            return;
        }
        dsMonHoc.hienThiDanhSach(List.of(mh));
        List<DeThi> deDangDung = dsDeThi.timTheoMonHoc(mh.getMa());
        List<CauHoi> cauDangDung = dsCauHoi.timTheoMonHoc(mh.getMa());
        if (!deDangDung.isEmpty() || !cauDangDung.isEmpty()) {
            System.out.println("Không thể xóa: môn đang có câu hỏi hoặc đề thi.");
            return;
        }
        if (NhapLieu.xacNhan("Bạn chắc chắn muốn xóa môn " + mh.getMa() + "?")) {
            thongBao(dsMonHoc.xoa(mh.getMa()), "xóa môn học", mh.getMa());
        } else {
            System.out.println("Đã hủy xóa.");
        }
    }

    private void timMonHoc() {
        System.out.println("Tiêu chí: 1. Theo mã   2. Theo tên (gần đúng, không cần dấu)");
        int tieuChi = NhapLieu.nhapSoNguyen("Chọn tiêu chí", 1, 2);
        if (tieuChi == 1) {
            hienThiTheoMa(dsMonHoc, NhapLieu.nhapMa("Mã môn học"));
        } else {
            dsMonHoc.hienThiDanhSach(dsMonHoc.timTheoTen(NhapLieu.nhapChuoi("Từ khóa tên môn")));
        }
    }

    // ======================= 2. NGƯỜI DÙNG =======================

    private void menuNguoiDung() {
        while (true) {
            int chon = chonMenuCon("QUẢN LÝ NGƯỜI DÙNG");
            switch (chon) {
                case 0 -> {
                    return;
                }
                case 1 -> dsNguoiDung.hienThiTatCa();
                case 2 -> themNguoiDung();
                case 3 -> suaNguoiDung();
                case 4 -> xoaNguoiDung();
                case 5 -> timNguoiDung();
                default -> System.out.println("Chức năng không hợp lệ.");
            }
        }
    }

    private void themNguoiDung() {
        System.out.println("Loại người dùng: 1. Thí sinh   2. Giáo viên");
        int loai = NhapLieu.nhapSoNguyen("Chọn loại", 1, 2);
        String ma = nhapMaMoi(loai == 1 ? ThiSinh.sinhMa() : GiaoVien.sinhMa(), dsNguoiDung);
        String hoTen = NhapLieu.nhapChuoi("Họ tên");
        LocalDate ngaySinh = NhapLieu.nhapNgay("Ngày sinh");
        String email = NhapLieu.nhapEmail("Email");
        NguoiDung moi; // ĐA HÌNH: biến kiểu lớp cha trỏ tới đối tượng lớp con
        if (loai == 1) {
            moi = new ThiSinh(ma, hoTen, ngaySinh, email, NhapLieu.nhapChuoi("Lớp"));
        } else {
            moi = new GiaoVien(ma, hoTen, ngaySinh, email, NhapLieu.nhapChuoi("Chuyên môn"));
        }
        thongBao(dsNguoiDung.them(moi), "thêm " + moi.getVaiTro().toLowerCase(), ma);
    }

    private void suaNguoiDung() {
        NguoiDung cu = dsNguoiDung.timTheoMa(NhapLieu.nhapMa("Mã người dùng cần sửa"));
        if (cu == null) {
            System.out.println("Không tìm thấy người dùng.");
            return;
        }
        dsNguoiDung.hienThiDanhSach(List.of(cu));
        System.out.println(GOI_Y_GIU_NGUYEN);
        String hoTen = NhapLieu.nhapChuoiTuyChon("Họ tên", cu.getHoTen());
        LocalDate ngaySinh = NhapLieu.nhapNgayTuyChon("Ngày sinh", cu.getNgaySinh());
        String email = NhapLieu.nhapEmailTuyChon("Email", cu.getEmail());
        NguoiDung moi;
        if (cu instanceof ThiSinh) {
            ThiSinh ts = (ThiSinh) cu;
            moi = new ThiSinh(cu.getMa(), hoTen, ngaySinh, email, NhapLieu.nhapChuoiTuyChon("Lớp", ts.getLop()));
        } else {
            GiaoVien gv = (GiaoVien) cu;
            moi = new GiaoVien(cu.getMa(), hoTen, ngaySinh, email,
                    NhapLieu.nhapChuoiTuyChon("Chuyên môn", gv.getChuyenMon()));
        }
        thongBao(dsNguoiDung.sua(cu.getMa(), moi), "sửa người dùng", cu.getMa());
    }

    private void xoaNguoiDung() {
        NguoiDung nd = dsNguoiDung.timTheoMa(NhapLieu.nhapMa("Mã người dùng cần xóa"));
        if (nd == null) {
            System.out.println("Không tìm thấy người dùng.");
            return;
        }
        dsNguoiDung.hienThiDanhSach(List.of(nd));
        int soKetQua = dsKetQua.timTheoThiSinh(nd.getMa()).size();
        if (soKetQua > 0) {
            System.out.println("Không thể xóa: thí sinh đã có kết quả thi.");
            return;
        }
        if (NhapLieu.xacNhan("Bạn chắc chắn muốn xóa " + nd.getMa() + "?")) {
            thongBao(dsNguoiDung.xoa(nd.getMa()), "xóa người dùng", nd.getMa());
        } else {
            System.out.println("Đã hủy xóa.");
        }
    }

    private void timNguoiDung() {
        System.out.println("Tiêu chí: 1. Theo mã   2. Theo họ tên   3. Theo vai trò   4. Theo email");
        int tieuChi = NhapLieu.nhapSoNguyen("Chọn tiêu chí", 1, 4);
        switch (tieuChi) {
            case 1 -> hienThiTheoMa(dsNguoiDung, NhapLieu.nhapMa("Mã người dùng"));
            case 2 -> dsNguoiDung.hienThiDanhSach(dsNguoiDung.timTheoTen(NhapLieu.nhapChuoi("Từ khóa họ tên")));
            case 3 -> {
                System.out.println("Vai trò: 1. Thí sinh   2. Giáo viên");
                int vaiTro = NhapLieu.nhapSoNguyen("Chọn vai trò", 1, 2);
                dsNguoiDung.hienThiDanhSach(dsNguoiDung.timTheoVaiTro(vaiTro == 1 ? ThiSinh.VAI_TRO : GiaoVien.VAI_TRO));
            }
            default -> dsNguoiDung.hienThiDanhSach(dsNguoiDung.timTheoEmail(NhapLieu.nhapChuoi("Từ khóa email")));
        }
    }

    // ======================= 3. CÂU HỎI =======================

    private void menuCauHoi() {
        while (true) {
            int chon = chonMenuCon("QUẢN LÝ CÂU HỎI");
            switch (chon) {
                case 0 -> {
                    return;
                }
                case 1 -> dsCauHoi.hienThiTatCa();
                case 2 -> themCauHoi();
                case 3 -> suaCauHoi();
                case 4 -> xoaCauHoi();
                case 5 -> timCauHoi();
                default -> System.out.println("Chức năng không hợp lệ.");
            }
        }
    }

    private void themCauHoi() {
        if (dsMonHoc.soLuong() == 0) {
            System.out.println("Chưa có môn học nào. Hãy thêm môn học trước.");
            return;
        }
        System.out.println("Loại câu hỏi: 1. Một đáp án   2. Nhiều đáp án   3. Đúng/Sai");
        int loai = NhapLieu.nhapSoNguyen("Chọn loại", 1, 3);
        String ma = nhapMaMoi(CauHoi.sinhMa(), dsCauHoi);
        String noiDung = NhapLieu.nhapChuoi("Nội dung câu hỏi");
        String maMon = chonMonHoc(null);
        int doKho = NhapLieu.nhapSoNguyen("Độ khó (1-Dễ, 2-Trung bình, 3-Khó)", 1, 3);
        CauHoi moi = taoCauHoiTheoLoai(loai, ma, noiDung, maMon, doKho); // ĐA HÌNH
        thongBao(dsCauHoi.them(moi), "thêm câu hỏi", ma);
    }

    /** Tạo đúng lớp con theo loại; kiểu trả về là lớp cha CauHoi. */
    private CauHoi taoCauHoiTheoLoai(int loai, String ma, String noiDung, String maMon, int doKho) {
        if (loai == 3) {
            return new CauHoiDungSai(ma, noiDung, maMon, doKho, nhapDapAnDungSai());
        }
        List<LuaChon> cacLuaChon = nhapCacLuaChon();
        if (loai == 1) {
            return new CauHoiMotDapAn(ma, noiDung, maMon, doKho, cacLuaChon, nhapMotDapAn(cacLuaChon));
        }
        return new CauHoiNhieuDapAn(ma, noiDung, maMon, doKho, cacLuaChon, nhapNhieuDapAn(cacLuaChon));
    }

    private List<LuaChon> nhapCacLuaChon() {
        int soLuong = NhapLieu.nhapSoNguyen("Số lựa chọn (2-6)",
                CauHoiCoLuaChon.SO_LUA_CHON_TOI_THIEU, CauHoiCoLuaChon.SO_LUA_CHON_TOI_DA);
        List<LuaChon> ds = new ArrayList<>();
        for (int i = 0; i < soLuong; i++) {
            char kyHieu = (char) ('A' + i);
            ds.add(new LuaChon(kyHieu, NhapLieu.nhapChuoi("  Lựa chọn " + kyHieu)));
        }
        return ds;
    }

    private char nhapMotDapAn(List<LuaChon> cacLuaChon) {
        while (true) {
            String s = NhapLieu.nhapChuoi("Đáp án đúng (một ký hiệu, ví dụ B)").toUpperCase();
            if (s.length() == 1 && LuaChon.coKyHieu(cacLuaChon, s.charAt(0))) {
                return s.charAt(0);
            }
            System.out.println("Lỗi: đáp án phải là MỘT ký hiệu trong các lựa chọn đã nhập.");
        }
    }

    private String nhapNhieuDapAn(List<LuaChon> cacLuaChon) {
        while (true) {
            String s = NhapLieu.nhapChuoi("Các đáp án đúng (ít nhất 2, ví dụ A,C)");
            if (CauHoiNhieuDapAn.laChuoiDapAnHopLe(s, cacLuaChon)
                    && CauHoiNhieuDapAn.chuanHoaDapAn(s).length() >= CauHoiNhieuDapAn.SO_DAP_AN_TOI_THIEU) {
                return CauHoiNhieuDapAn.chuanHoaDapAn(s);
            }
            System.out.println("Lỗi: cần ít nhất 2 ký hiệu nằm trong các lựa chọn, cách nhau dấu phẩy.");
        }
    }

    private boolean nhapDapAnDungSai() {
        while (true) {
            Boolean giaTri = CauHoiDungSai.docTraLoi(NhapLieu.nhapChuoi("Đáp án đúng (D = Đúng, S = Sai)"));
            if (giaTri != null) {
                return giaTri;
            }
            System.out.println("Lỗi: chỉ nhập D hoặc S.");
        }
    }

    /** Xác định số loại (1, 2, 3) của câu hỏi hiện có để nhập lại đáp án cùng loại. */
    private int layLoaiCauHoi(CauHoi ch) {
        if (ch instanceof CauHoiDungSai) {
            return 3;
        }
        if (ch instanceof CauHoiNhieuDapAn) {
            return 2;
        }
        return 1;
    }

    private void suaCauHoi() {
        CauHoi cu = dsCauHoi.timTheoMa(NhapLieu.nhapMa("Mã câu hỏi cần sửa"));
        if (cu == null) {
            System.out.println("Không tìm thấy câu hỏi.");
            return;
        }
        for (DeThi de : dsDeThi.timDeChuaCauHoi(cu.getMa())) {
            if (!dsKetQua.timTheoDeThi(de.getMa()).isEmpty()) {
                System.out.println("Không sửa câu thuộc đề đã có kết quả. Hãy tạo câu mới.");
                return;
            }
        }
        cu.hienThi(); // ĐA HÌNH
        System.out.println(GOI_Y_GIU_NGUYEN);
        String noiDung = NhapLieu.nhapChuoiTuyChon("Nội dung", cu.getNoiDung());
        String maMon = chonMonHoc(cu.getMaMonHoc());
        if (!maMon.equalsIgnoreCase(cu.getMaMonHoc())
                && !dsDeThi.timDeChuaCauHoi(cu.getMa()).isEmpty()) {
            System.out.println("Không đổi môn: câu đang nằm trong đề thi.");
            return;
        }
        int doKho = NhapLieu.nhapSoNguyenTuyChon("Độ khó (1-3)", 1, 3, cu.getDoKho());
        CauHoi moi;
        if (NhapLieu.xacNhan("Có nhập lại lựa chọn/đáp án không?")) {
            moi = taoCauHoiTheoLoai(layLoaiCauHoi(cu), cu.getMa(), noiDung, maMon, doKho);
        } else {
            // Tạo bản mới để khi lưu thất bại, đối tượng cũ vẫn nguyên vẹn.
            if (cu instanceof CauHoiMotDapAn) {
                CauHoiMotDapAn ch = (CauHoiMotDapAn) cu;
                moi = new CauHoiMotDapAn(cu.getMa(), noiDung, maMon, doKho,
                        ch.getDanhSachLuaChon(), ch.getDapAnDung());
            } else if (cu instanceof CauHoiNhieuDapAn) {
                CauHoiNhieuDapAn ch = (CauHoiNhieuDapAn) cu;
                moi = new CauHoiNhieuDapAn(cu.getMa(), noiDung, maMon, doKho,
                        ch.getDanhSachLuaChon(), ch.getDapAnDung());
            } else {
                moi = new CauHoiDungSai(cu.getMa(), noiDung, maMon, doKho,
                        ((CauHoiDungSai) cu).isDapAnDung());
            }
        }
        thongBao(dsCauHoi.sua(cu.getMa(), moi), "sửa câu hỏi", cu.getMa());
    }

    private void xoaCauHoi() {
        CauHoi ch = dsCauHoi.timTheoMa(NhapLieu.nhapMa("Mã câu hỏi cần xóa"));
        if (ch == null) {
            System.out.println("Không tìm thấy câu hỏi.");
            return;
        }
        if (!dsDeThi.timDeChuaCauHoi(ch.getMa()).isEmpty()) {
            System.out.println("Không thể xóa: câu hỏi đang nằm trong đề thi.");
            return;
        }
        ch.hienThi();
        if (NhapLieu.xacNhan("Bạn chắc chắn muốn xóa câu hỏi " + ch.getMa() + "?"))
            thongBao(dsCauHoi.xoa(ch.getMa()), "xóa câu hỏi", ch.getMa());
        else System.out.println("Đã hủy xóa.");
    }

    private void timCauHoi() {
        System.out.println("Tiêu chí: 1. Theo mã   2. Theo từ khóa nội dung   3. Theo môn học"
                + "   4. Theo loại   5. Theo độ khó");
        int tieuChi = NhapLieu.nhapSoNguyen("Chọn tiêu chí", 1, 5);
        switch (tieuChi) {
            case 1 -> hienThiTheoMa(dsCauHoi, NhapLieu.nhapMa("Mã câu hỏi"));
            case 2 -> dsCauHoi.hienThiDanhSach(dsCauHoi.timTheoTuKhoa(NhapLieu.nhapChuoi("Từ khóa")));
            case 3 -> dsCauHoi.hienThiDanhSach(dsCauHoi.timTheoMonHoc(NhapLieu.nhapMa("Mã môn học")));
            case 4 -> {
                System.out.println("Loại: 1. Một đáp án   2. Nhiều đáp án   3. Đúng/Sai");
                int loai = NhapLieu.nhapSoNguyen("Chọn loại", 1, 3);
                String kyHieu = loai == 1 ? CauHoiMotDapAn.KY_HIEU_LOAI
                        : loai == 2 ? CauHoiNhieuDapAn.KY_HIEU_LOAI : CauHoiDungSai.KY_HIEU_LOAI;
                dsCauHoi.hienThiDanhSach(dsCauHoi.timTheoLoai(kyHieu));
            }
            default -> dsCauHoi.hienThiDanhSach(dsCauHoi.timTheoDoKho(
                    NhapLieu.nhapSoNguyen("Độ khó (1-Dễ, 2-Trung bình, 3-Khó)", 1, 3)));
        }
    }

    // ======================= 4. ĐỀ THI =======================

    private void menuDeThi() {
        while (true) {
            int chon = chonMenuCon("QUẢN LÝ ĐỀ THI");
            switch (chon) {
                case 0 -> {
                    return;
                }
                case 1 -> dsDeThi.hienThiTatCa();
                case 2 -> themDeThi();
                case 3 -> suaDeThi();
                case 4 -> xoaDeThi();
                case 5 -> timDeThi();
                default -> System.out.println("Chức năng không hợp lệ.");
            }
        }
    }

    /** Nhập danh sách mã câu hỏi thuộc môn; trả về null nếu môn chưa có câu hỏi. */
    private List<String> nhapDanhSachMaCauHoi(String maMon) {
        List<CauHoi> cauCuaMon = dsCauHoi.timTheoMonHoc(maMon);
        if (cauCuaMon.isEmpty()) {
            System.out.println("Môn " + maMon + " chưa có câu hỏi nào. Hãy thêm câu hỏi trước.");
            return null;
        }
        System.out.println("Câu hỏi của môn " + maMon + ":");
        for (CauHoi ch : cauCuaMon) {
            System.out.printf("  %-7s %-14s %s%n", ch.getMa(), ch.getLoai(), NhapLieu.rutGon(ch.getNoiDung(), 60));
        }
        while (true) {
            String s = NhapLieu.nhapChuoi("Nhập các mã câu hỏi, cách nhau dấu phẩy (hoặc TATCA)");
            List<String> ketQua = new ArrayList<>();
            if (s.equalsIgnoreCase("TATCA")) {
                for (CauHoi ch : cauCuaMon) {
                    ketQua.add(ch.getMa());
                }
                return ketQua;
            }
            LinkedHashSet<String> cacMa = new LinkedHashSet<>(); // giữ thứ tự, bỏ trùng
            boolean hopLe = true;
            for (String phan : s.split(",")) {
                String maCH = phan.trim().toUpperCase();
                if (maCH.isEmpty()) {
                    continue;
                }
                CauHoi ch = dsCauHoi.timTheoMa(maCH);
                if (ch == null) {
                    System.out.println("Lỗi: không có câu hỏi mã " + maCH + ".");
                    hopLe = false;
                    break;
                }
                if (!ch.getMaMonHoc().equalsIgnoreCase(maMon)) {
                    System.out.println("Lỗi: câu hỏi " + maCH + " không thuộc môn " + maMon + ".");
                    hopLe = false;
                    break;
                }
                cacMa.add(ch.getMa());
            }
            if (hopLe && cacMa.isEmpty()) {
                System.out.println("Lỗi: phải chọn ít nhất 1 câu hỏi.");
                hopLe = false;
            }
            if (hopLe) {
                ketQua.addAll(cacMa);
                return ketQua;
            }
        }
    }

    private void themDeThi() {
        if (dsMonHoc.soLuong() == 0) {
            System.out.println("Chưa có môn học nào. Hãy thêm môn học trước.");
            return;
        }
        String ma = nhapMaMoi(DeThi.sinhMa(), dsDeThi);
        String ten = NhapLieu.nhapChuoi("Tên đề thi");
        String maMon = chonMonHoc(null);
        int thoiGian = NhapLieu.nhapSoNguyen("Thời gian làm bài (phút, 1-180)", 1, 180);
        List<String> cacMa = nhapDanhSachMaCauHoi(maMon);
        if (cacMa == null) {
            return;
        }
        thongBao(dsDeThi.them(new DeThi(ma, ten, maMon, thoiGian, cacMa)), "thêm đề thi", ma);
    }

    private void suaDeThi() {
        DeThi cu = dsDeThi.timTheoMa(NhapLieu.nhapMa("Mã đề thi cần sửa"));
        if (cu == null) {
            System.out.println("Không tìm thấy đề thi.");
            return;
        }
        if (!dsKetQua.timTheoDeThi(cu.getMa()).isEmpty()) {
            System.out.println("Không sửa đề đã có kết quả. Hãy tạo đề mới để giữ lịch sử.");
            return;
        }
        dsDeThi.hienThiDanhSach(List.of(cu));
        System.out.println(GOI_Y_GIU_NGUYEN);
        String ten = NhapLieu.nhapChuoiTuyChon("Tên đề thi", cu.getTenDe());
        String maMon = chonMonHoc(cu.getMaMonHoc());
        int thoiGian = NhapLieu.nhapSoNguyenTuyChon("Thời gian làm bài (phút, 1-180)", 1, 180, cu.getThoiGianLamBai());
        List<String> cacMa = cu.getDanhSachMaCauHoi();
        if (!maMon.equalsIgnoreCase(cu.getMaMonHoc())) {
            System.out.println("Đã đổi môn học nên phải chọn lại câu hỏi.");
            cacMa = nhapDanhSachMaCauHoi(maMon);
            if (cacMa == null) {
                System.out.println("Đã hủy sửa đề thi.");
                return;
            }
        } else if (NhapLieu.xacNhan("Có chọn lại danh sách câu hỏi không?")) {
            List<String> cacMaMoi = nhapDanhSachMaCauHoi(maMon);
            if (cacMaMoi != null) {
                cacMa = cacMaMoi;
            }
        }
        thongBao(dsDeThi.sua(cu.getMa(), new DeThi(cu.getMa(), ten, maMon, thoiGian, cacMa)),
                "sửa đề thi", cu.getMa());
    }

    private void xoaDeThi() {
        DeThi de = dsDeThi.timTheoMa(NhapLieu.nhapMa("Mã đề thi cần xóa"));
        if (de == null) {
            System.out.println("Không tìm thấy đề thi.");
            return;
        }
        dsDeThi.hienThiDanhSach(List.of(de));
        int soKetQua = dsKetQua.timTheoDeThi(de.getMa()).size();
        if (soKetQua > 0) {
            System.out.println("Không thể xóa: đề đã có kết quả thi.");
            return;
        }
        if (NhapLieu.xacNhan("Bạn chắc chắn muốn xóa đề " + de.getMa() + "?")) {
            thongBao(dsDeThi.xoa(de.getMa()), "xóa đề thi", de.getMa());
        } else {
            System.out.println("Đã hủy xóa.");
        }
    }

    private void timDeThi() {
        System.out.println("Tiêu chí: 1. Theo mã   2. Theo tên đề   3. Theo môn học");
        int tieuChi = NhapLieu.nhapSoNguyen("Chọn tiêu chí", 1, 3);
        switch (tieuChi) {
            case 1 -> hienThiTheoMa(dsDeThi, NhapLieu.nhapMa("Mã đề thi"));
            case 2 -> dsDeThi.hienThiDanhSach(dsDeThi.timTheoTen(NhapLieu.nhapChuoi("Từ khóa tên đề")));
            default -> dsDeThi.hienThiDanhSach(dsDeThi.timTheoMonHoc(NhapLieu.nhapMa("Mã môn học")));
        }
    }

    // ======================= 5. KẾT QUẢ THI =======================

    private void menuKetQua() {
        while (true) {
            int chon = chonMenuCon("QUẢN LÝ KẾT QUẢ THI");
            switch (chon) {
                case 0 -> {
                    return;
                }
                case 1 -> dsKetQua.hienThiTatCa();
                case 2 -> themKetQua();
                case 3 -> suaKetQua();
                case 4 -> xoaKetQua();
                case 5 -> timKetQua();
                default -> System.out.println("Chức năng không hợp lệ.");
            }
        }
    }

    private ThiSinh chonThiSinh() {
        while (true) {
            NguoiDung nd = dsNguoiDung.timTheoMa(NhapLieu.nhapMa("Mã thí sinh"));
            if (nd instanceof ThiSinh) {
                return (ThiSinh) nd;
            }
            System.out.println(nd == null ? "Lỗi: không tìm thấy mã này." : "Lỗi: mã này không phải thí sinh.");
        }
    }

    private DeThi chonDeThi() {
        while (true) {
            DeThi de = dsDeThi.timTheoMa(NhapLieu.nhapMa("Mã đề thi"));
            if (de != null) {
                return de;
            }
            System.out.println("Lỗi: không tìm thấy đề thi.");
        }
    }

    /** Thêm thủ công (ví dụ nhập điểm bài thi trên giấy). Thi trực tiếp dùng menu 6. */
    private void themKetQua() {
        if (dsNguoiDung.layDanhSachThiSinh().isEmpty() || dsDeThi.soLuong() == 0) {
            System.out.println("Cần có ít nhất một thí sinh và một đề thi trước khi nhập kết quả.");
            return;
        }
        System.out.println("(Nhập kết quả thủ công. Để thi trực tiếp, dùng menu 6 - Làm bài thi.)");
        String ma = nhapMaMoi(KetQuaThi.sinhMa(), dsKetQua);
        ThiSinh ts = chonThiSinh();
        DeThi de = chonDeThi();
        int tongSoCau = de.getSoCauHoi();
        int soCauDung = NhapLieu.nhapSoNguyen("Số câu đúng (0-" + tongSoCau + ")", 0, tongSoCau);
        double diem = nhapDiemHopLe(de, soCauDung, null); // điểm phải khớp số câu đúng
        LocalDate ngayThi = NhapLieu.nhapNgay("Ngày thi");
        thongBao(dsKetQua.them(new KetQuaThi(ma, ts.getMa(), de.getMa(), NhapLieu.lamTron2(diem),
                soCauDung, tongSoCau, ngayThi)), "thêm kết quả", ma);
    }

    private void suaKetQua() {
        KetQuaThi cu = dsKetQua.timTheoMa(NhapLieu.nhapMa("Mã kết quả cần sửa"));
        if (cu == null) {
            System.out.println("Không tìm thấy kết quả.");
            return;
        }
        DeThi de = dsDeThi.timTheoMa(cu.getMaDeThi());
        if (de == null || de.getSoCauHoi() != cu.getTongSoCau()) {
            System.out.println("Không sửa được: đề " + cu.getMaDeThi() + " không còn khớp với kết quả này.");
            return;
        }
        dsKetQua.hienThiDanhSach(List.of(cu));
        System.out.println(GOI_Y_GIU_NGUYEN);
        // Nhập số câu đúng TRƯỚC, vì khoảng điểm hợp lệ phụ thuộc vào số câu đúng
        int soCauDung = NhapLieu.nhapSoNguyenTuyChon("Số câu đúng (0-" + cu.getTongSoCau() + ")",
                0, cu.getTongSoCau(), cu.getSoCauDung());
        double diem = nhapDiemHopLe(de, soCauDung, cu.getDiem());
        LocalDate ngayThi = NhapLieu.nhapNgayTuyChon("Ngày thi", cu.getNgayThi());
        KetQuaThi moi = new KetQuaThi(cu.getMa(), cu.getMaThiSinh(), cu.getMaDeThi(),
                NhapLieu.lamTron2(diem), soCauDung, cu.getTongSoCau(), ngayThi);
        thongBao(dsKetQua.sua(cu.getMa(), moi), "sửa kết quả", cu.getMa());
    }

    private void xoaKetQua() {
        KetQuaThi kq = dsKetQua.timTheoMa(NhapLieu.nhapMa("Mã kết quả cần xóa"));
        if (kq == null) {
            System.out.println("Không tìm thấy kết quả.");
            return;
        }
        dsKetQua.hienThiDanhSach(List.of(kq));
        if (NhapLieu.xacNhan("Bạn chắc chắn muốn xóa kết quả " + kq.getMa() + "?")) {
            thongBao(dsKetQua.xoa(kq.getMa()), "xóa kết quả", kq.getMa());
        } else {
            System.out.println("Đã hủy xóa.");
        }
    }

    private void timKetQua() {
        System.out.println("Tiêu chí: 1. Theo mã kết quả   2. Theo mã thí sinh   3. Theo mã đề thi   4. Theo khoảng điểm");
        int tieuChi = NhapLieu.nhapSoNguyen("Chọn tiêu chí", 1, 4);
        switch (tieuChi) {
            case 1 -> hienThiTheoMa(dsKetQua, NhapLieu.nhapMa("Mã kết quả"));
            case 2 -> dsKetQua.hienThiDanhSach(dsKetQua.timTheoThiSinh(NhapLieu.nhapMa("Mã thí sinh")));
            case 3 -> dsKetQua.hienThiDanhSach(dsKetQua.timTheoDeThi(NhapLieu.nhapMa("Mã đề thi")));
            default -> {
                double tu = NhapLieu.nhapSoThuc("Điểm từ (0-10)", 0, 10);
                double den = NhapLieu.nhapSoThuc("Điểm đến (" + tu + "-10)", tu, 10);
                dsKetQua.hienThiDanhSach(dsKetQua.timTheoKhoangDiem(tu, den));
            }
        }
    }

    // ======================= 8. ĐỌC / GHI FILE =======================

    private void menuFile() {
        while (true) {
            System.out.println();
            System.out.println("----- ĐỌC/GHI FILE DỮ LIỆU -----");
            System.out.println("1. Xem thông tin các file dữ liệu");
            System.out.println("2. Ghi toàn bộ dữ liệu ra file");
            System.out.println("3. Đọc lại toàn bộ dữ liệu từ file");
            System.out.println("0. Quay lại");
            int chon = NhapLieu.nhapSoNguyen("Chọn", 0, 3);
            switch (chon) {
                case 0 -> {
                    return;
                }
                case 1 -> {
                    inThongTinFile(dsMonHoc, "Môn học");
                    inThongTinFile(dsNguoiDung, "Người dùng");
                    inThongTinFile(dsCauHoi, "Câu hỏi");
                    inThongTinFile(dsDeThi, "Đề thi");
                    inThongTinFile(dsKetQua, "Kết quả thi");
                    if (loiDuLieu.isEmpty()) {
                        System.out.println("  Kiểm tra liên kết dữ liệu: không có lỗi.");
                    } else {
                        System.out.println("  Dữ liệu có " + loiDuLieu.size() + " lỗi:");
                        inLoiDuLieu(Integer.MAX_VALUE);
                    }
                }
                case 2 -> luuTatCa();
                case 3 -> taiDuLieu();
                default -> System.out.println("Chức năng không hợp lệ.");
            }
        }
    }

    private void inThongTinFile(DanhSach<?> danhSach, String ten) {
        System.out.printf("  %-12s %-24s %d bản ghi%n", ten, danhSach.getDuongDanFile(), danhSach.soLuong());
    }
}
