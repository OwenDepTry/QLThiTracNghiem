import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Chương trình chính - Quản lý thi trắc nghiệm.
 * Menu console minh họa đầy đủ: xem / thêm / sửa / xóa / tìm kiếm
 * cho từng loại đối tượng, cùng chức năng làm bài thi & chấm điểm tự động.
 */
public class Main {
    static Scanner sc = new Scanner(System.in);
    static QuanLyHeThong qlht = new QuanLyHeThong();

    public static void main(String[] args) {
        System.out.println("======================================");
        System.out.println("   HỆ THỐNG QUẢN LÝ THI TRẮC NGHIỆM   ");
        System.out.println("======================================");

        qlht.docTatCa();
        if (qlht.getDsMonHoc().soLuong() == 0) {
            taoDuLieuMau();
            System.out.println(">> Chưa có dữ liệu trong 'data/' nên đã tạo dữ liệu mẫu để demo.");
        }

        boolean chay = true;
        while (chay) {
            inMenuChinh();
            int chon = inputInt("Chọn chức năng: ");
            switch (chon) {
                case 1: menuSinhVien(); break;
                case 2: menuGiaoVien(); break;
                case 3: menuMonHoc(); break;
                case 4: menuCauHoi(); break;
                case 5: menuDeThi(); break;
                case 6: lamBaiThi(); break;
                case 7: thongKe(); break;
                case 8: qlht.luuTatCa(); break;
                case 9: qlht.docTatCa(); break;
                case 0:
                    qlht.luuTatCa();
                    chay = false;
                    System.out.println("Đã lưu dữ liệu. Tạm biệt!");
                    break;
                default: System.out.println(">> Lựa chọn không hợp lệ!");
            }
        }
        sc.close();
    }

    static void inMenuChinh() {
        System.out.println("\n================ MENU CHÍNH ================");
        System.out.println("1. Quản lý Sinh viên");
        System.out.println("2. Quản lý Giáo viên");
        System.out.println("3. Quản lý Môn học");
        System.out.println("4. Quản lý Câu hỏi");
        System.out.println("5. Quản lý Đề thi");
        System.out.println("6. Làm bài thi & Chấm điểm tự động");
        System.out.println("7. Thống kê kết quả");
        System.out.println("8. Lưu dữ liệu ra file");
        System.out.println("9. Đọc dữ liệu từ file");
        System.out.println("0. Lưu & Thoát");
        System.out.printf("(Tổng số người dùng: %d | Môn học: %d | Câu hỏi: %d | Đề thi: %d)%n",
                NguoiDung.getSoLuongNguoiDung(), MonHoc.getSoLuongMonHoc(),
                CauHoi.getSoLuongCauHoi(), DeThi.getSoLuongDeThi());
    }

    // ================= HÀM HỖ TRỢ NHẬP =================
    static int inputInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            try { return Integer.parseInt(s); } catch (Exception e) { System.out.println("Vui lòng nhập số nguyên!"); }
        }
    }

    static String inputLine(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    // ================= 1. QUẢN LÝ SINH VIÊN =================
    static void menuSinhVien() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- QUẢN LÝ SINH VIÊN ---");
            System.out.println("1.Xem tất cả  2.Thêm  3.Sửa  4.Xóa  5.Tìm kiếm  0.Quay lại");
            switch (inputInt("Chọn: ")) {
                case 1:
                    if (qlht.getDsSinhVien().soLuong() == 0) System.out.println("(Chưa có sinh viên nào)");
                    for (SinhVien sv : qlht.getDsSinhVien().layTatCa()) sv.hienThi();
                    break;
                case 2: {
                    String ma = "ND" + String.format("%03d", NguoiDung.getSoLuongNguoiDung() + 1);
                    String hoTen = inputLine("Họ tên: ");
                    String ns = inputLine("Ngày sinh (dd/MM/yyyy): ");
                    String dc = inputLine("Địa chỉ: ");
                    String sdt = inputLine("Số điện thoại: ");
                    String mssv = inputLine("MSSV: ");
                    String lop = inputLine("Mã lớp: ");
                    String email = inputLine("Email: ");
                    qlht.getDsSinhVien().them(new SinhVien(ma, hoTen, ns, dc, sdt, mssv, lop, email));
                    System.out.println(">> Đã thêm sinh viên, mã người dùng: " + ma);
                    break;
                }
                case 3: {
                    String mssv = inputLine("Nhập MSSV cần sửa: ");
                    SinhVien sv = qlht.getDsSinhVien().timTheoDieuKien(s -> s.getMssv().equals(mssv));
                    if (sv == null) { System.out.println(">> Không tìm thấy!"); break; }
                    sv.hienThi();
                    sv.setHoTen(inputLine("Họ tên mới: "));
                    sv.setDiaChi(inputLine("Địa chỉ mới: "));
                    sv.setSoDienThoai(inputLine("SĐT mới: "));
                    sv.setEmail(inputLine("Email mới: "));
                    System.out.println(">> Cập nhật thành công!");
                    break;
                }
                case 4: {
                    String mssv = inputLine("Nhập MSSV cần xóa: ");
                    boolean ok = qlht.getDsSinhVien().xoaTheoDieuKien(s -> s.getMssv().equals(mssv));
                    System.out.println(ok ? ">> Đã xóa." : ">> Không tìm thấy!");
                    break;
                }
                case 5: {
                    String kw = inputLine("Nhập từ khóa (họ tên / MSSV): ").toLowerCase();
                    List<SinhVien> kq = qlht.getDsSinhVien().timDanhSach(s ->
                            s.getHoTen().toLowerCase().contains(kw) || s.getMssv().toLowerCase().contains(kw));
                    if (kq.isEmpty()) System.out.println(">> Không tìm thấy kết quả.");
                    for (SinhVien sv : kq) sv.hienThi();
                    break;
                }
                case 0: back = true; break;
                default: System.out.println(">> Không hợp lệ!");
            }
        }
    }

    // ================= 2. QUẢN LÝ GIÁO VIÊN =================
    static void menuGiaoVien() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- QUẢN LÝ GIÁO VIÊN ---");
            System.out.println("1.Xem tất cả  2.Thêm  3.Sửa  4.Xóa  5.Tìm kiếm  0.Quay lại");
            switch (inputInt("Chọn: ")) {
                case 1:
                    if (qlht.getDsGiaoVien().soLuong() == 0) System.out.println("(Chưa có giáo viên nào)");
                    for (GiaoVien gv : qlht.getDsGiaoVien().layTatCa()) gv.hienThi();
                    break;
                case 2: {
                    String ma = "ND" + String.format("%03d", NguoiDung.getSoLuongNguoiDung() + 1);
                    String hoTen = inputLine("Họ tên: ");
                    String ns = inputLine("Ngày sinh (dd/MM/yyyy): ");
                    String dc = inputLine("Địa chỉ: ");
                    String sdt = inputLine("Số điện thoại: ");
                    String cm = inputLine("Chuyên môn: ");
                    String hv = inputLine("Học vị: ");
                    qlht.getDsGiaoVien().them(new GiaoVien(ma, hoTen, ns, dc, sdt, cm, hv));
                    System.out.println(">> Đã thêm giáo viên, mã người dùng: " + ma);
                    break;
                }
                case 3: {
                    String ma = inputLine("Nhập mã người dùng cần sửa: ");
                    GiaoVien gv = qlht.getDsGiaoVien().timTheoDieuKien(g -> g.getMaNguoiDung().equals(ma));
                    if (gv == null) { System.out.println(">> Không tìm thấy!"); break; }
                    gv.hienThi();
                    gv.setHoTen(inputLine("Họ tên mới: "));
                    gv.setChuyenMon(inputLine("Chuyên môn mới: "));
                    gv.setHocVi(inputLine("Học vị mới: "));
                    System.out.println(">> Cập nhật thành công!");
                    break;
                }
                case 4: {
                    String ma = inputLine("Nhập mã người dùng cần xóa: ");
                    boolean ok = qlht.getDsGiaoVien().xoaTheoDieuKien(g -> g.getMaNguoiDung().equals(ma));
                    System.out.println(ok ? ">> Đã xóa." : ">> Không tìm thấy!");
                    break;
                }
                case 5: {
                    String kw = inputLine("Nhập từ khóa (họ tên / chuyên môn): ").toLowerCase();
                    List<GiaoVien> kq = qlht.getDsGiaoVien().timDanhSach(g ->
                            g.getHoTen().toLowerCase().contains(kw) || g.getChuyenMon().toLowerCase().contains(kw));
                    if (kq.isEmpty()) System.out.println(">> Không tìm thấy kết quả.");
                    for (GiaoVien gv : kq) gv.hienThi();
                    break;
                }
                case 0: back = true; break;
                default: System.out.println(">> Không hợp lệ!");
            }
        }
    }

    // ================= 3. QUẢN LÝ MÔN HỌC =================
    static void menuMonHoc() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- QUẢN LÝ MÔN HỌC ---");
            System.out.println("1.Xem tất cả  2.Thêm  3.Sửa  4.Xóa  5.Tìm kiếm  0.Quay lại");
            switch (inputInt("Chọn: ")) {
                case 1:
                    if (qlht.getDsMonHoc().soLuong() == 0) System.out.println("(Chưa có môn học nào)");
                    for (MonHoc mh : qlht.getDsMonHoc().layTatCa()) System.out.println(mh);
                    break;
                case 2: {
                    String ten = inputLine("Tên môn học: ");
                    int stc = inputInt("Số tín chỉ: ");
                    MonHoc mh = new MonHoc(ten, stc);
                    qlht.getDsMonHoc().them(mh);
                    System.out.println(">> Đã thêm môn học, mã: " + mh.getMaMon());
                    break;
                }
                case 3: {
                    String ma = inputLine("Nhập mã môn cần sửa: ");
                    MonHoc mh = qlht.getDsMonHoc().timTheoDieuKien(m -> m.getMaMon().equals(ma));
                    if (mh == null) { System.out.println(">> Không tìm thấy!"); break; }
                    mh.setTenMon(inputLine("Tên môn mới: "));
                    mh.setSoTinChi(inputInt("Số tín chỉ mới: "));
                    System.out.println(">> Cập nhật thành công!");
                    break;
                }
                case 4: {
                    String ma = inputLine("Nhập mã môn cần xóa: ");
                    boolean ok = qlht.getDsMonHoc().xoaTheoDieuKien(m -> m.getMaMon().equals(ma));
                    System.out.println(ok ? ">> Đã xóa." : ">> Không tìm thấy!");
                    break;
                }
                case 5: {
                    String kw = inputLine("Nhập từ khóa (tên môn / mã môn): ").toLowerCase();
                    List<MonHoc> kq = qlht.getDsMonHoc().timDanhSach(m ->
                            m.getTenMon().toLowerCase().contains(kw) || m.getMaMon().toLowerCase().contains(kw));
                    if (kq.isEmpty()) System.out.println(">> Không tìm thấy kết quả.");
                    for (MonHoc mh : kq) System.out.println(mh);
                    break;
                }
                case 0: back = true; break;
                default: System.out.println(">> Không hợp lệ!");
            }
        }
    }

    // ================= 4. QUẢN LÝ CÂU HỎI =================
    static void menuCauHoi() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- QUẢN LÝ CÂU HỎI ---");
            System.out.println("1.Xem tất cả  2.Thêm  3.Sửa  4.Xóa  5.Tìm kiếm  0.Quay lại");
            switch (inputInt("Chọn: ")) {
                case 1:
                    if (qlht.getDsCauHoi().soLuong() == 0) System.out.println("(Chưa có câu hỏi nào)");
                    // Duyệt qua List<CauHoi> nhưng mỗi phần tử tự hiển thị đúng loại của nó -> đa hình
                    for (CauHoi c : qlht.getDsCauHoi().layTatCa()) c.hienThiCauHoi();
                    break;
                case 2: themCauHoi(); break;
                case 3: {
                    String ma = inputLine("Nhập mã câu hỏi cần sửa: ");
                    CauHoi c = qlht.getDsCauHoi().timTheoDieuKien(x -> x.getMaCauHoi().equals(ma));
                    if (c == null) { System.out.println(">> Không tìm thấy!"); break; }
                    c.hienThiCauHoi();
                    c.setNoiDung(inputLine("Nội dung câu hỏi mới: "));
                    c.setDoKho(inputLine("Độ khó mới (Dễ/Trung bình/Khó): "));
                    if (inputLine("Sửa lại toàn bộ đáp án? (y/n): ").equalsIgnoreCase("y")) {
                        c.setDanhSachDapAn(nhapDanhSachDapAn(c.loaiCauHoi()));
                    }
                    System.out.println(">> Cập nhật thành công!");
                    break;
                }
                case 4: {
                    String ma = inputLine("Nhập mã câu hỏi cần xóa: ");
                    boolean ok = qlht.getDsCauHoi().xoaTheoDieuKien(x -> x.getMaCauHoi().equals(ma));
                    System.out.println(ok ? ">> Đã xóa." : ">> Không tìm thấy!");
                    break;
                }
                case 5: {
                    String kw = inputLine("Nhập từ khóa (nội dung / mã môn): ").toLowerCase();
                    List<CauHoi> kq = qlht.getDsCauHoi().timDanhSach(x ->
                            x.getNoiDung().toLowerCase().contains(kw) || x.getMaMonHoc().toLowerCase().contains(kw));
                    if (kq.isEmpty()) System.out.println(">> Không tìm thấy kết quả.");
                    for (CauHoi c : kq) c.hienThiCauHoi();
                    break;
                }
                case 0: back = true; break;
                default: System.out.println(">> Không hợp lệ!");
            }
        }
    }

    static void themCauHoi() {
        String loai = inputLine("Loại câu hỏi (1: Một đáp án, 2: Nhiều đáp án): ");
        String noiDung = inputLine("Nội dung câu hỏi: ");
        String mon = inputLine("Mã môn học: ");
        String doKho = inputLine("Độ khó (Dễ/Trung bình/Khó): ");
        List<DapAn> ds = nhapDanhSachDapAn(loai.equals("2") ? "Nhiều đáp án" : "Một đáp án");

        CauHoi c;
        if (loai.equals("2")) {
            c = new CauHoiNhieuDapAn(noiDung, mon, doKho, ds);
        } else {
            c = new CauHoiMotDapAn(noiDung, mon, doKho, ds);
        }
        qlht.getDsCauHoi().them(c);
        System.out.println(">> Đã thêm câu hỏi, mã: " + c.getMaCauHoi());
    }

    static List<DapAn> nhapDanhSachDapAn(String loaiCauHoi) {
        int soLuong = inputInt("Số lượng đáp án: ");
        List<DapAn> ds = new ArrayList<>();
        for (int i = 0; i < soLuong; i++) {
            String nd = inputLine("  Đáp án " + i + ": ");
            ds.add(new DapAn(nd, false));
        }
        if (loaiCauHoi.equals("Nhiều đáp án")) {
            String idxs = inputLine("Nhập CÁC chỉ số đáp án ĐÚNG (cách nhau dấu phẩy, VD 0,2): ");
            for (String s : idxs.split(",")) {
                try {
                    int idx = Integer.parseInt(s.trim());
                    if (idx >= 0 && idx < ds.size()) ds.get(idx).setDapAnDung(true);
                } catch (Exception ignored) { }
            }
        } else {
            int idx = inputInt("Nhập chỉ số đáp án ĐÚNG: ");
            if (idx >= 0 && idx < ds.size()) ds.get(idx).setDapAnDung(true);
        }
        return ds;
    }

    // ================= 5. QUẢN LÝ ĐỀ THI =================
    static void menuDeThi() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- QUẢN LÝ ĐỀ THI ---");
            System.out.println("1.Xem tất cả  2.Thêm  3.Sửa (thêm/xóa câu hỏi)  4.Xóa  5.Tìm kiếm  0.Quay lại");
            switch (inputInt("Chọn: ")) {
                case 1:
                    if (qlht.getDsDeThi().soLuong() == 0) System.out.println("(Chưa có đề thi nào)");
                    for (DeThi dt : qlht.getDsDeThi().layTatCa()) System.out.println(dt);
                    break;
                case 2: {
                    String ten = inputLine("Tên đề thi: ");
                    String mon = inputLine("Mã môn học: ");
                    int tg = inputInt("Thời gian làm bài (phút): ");
                    DeThi dt = new DeThi(ten, mon, tg);
                    qlht.getDsDeThi().them(dt);
                    System.out.println(">> Đã tạo đề thi, mã: " + dt.getMaDeThi());
                    themCauHoiVaoDe(dt);
                    break;
                }
                case 3: {
                    String ma = inputLine("Nhập mã đề thi cần sửa: ");
                    DeThi dt = qlht.getDsDeThi().timTheoDieuKien(d -> d.getMaDeThi().equals(ma));
                    if (dt == null) { System.out.println(">> Không tìm thấy!"); break; }
                    System.out.println(dt);
                    System.out.println("1.Đổi tên/thời gian  2.Thêm câu hỏi  3.Xóa câu hỏi khỏi đề  0.Xong");
                    boolean tiep = true;
                    while (tiep) {
                        switch (inputInt("Chọn: ")) {
                            case 1:
                                dt.setTenDeThi(inputLine("Tên đề mới: "));
                                dt.setThoiGianLamBai(inputInt("Thời gian mới (phút): "));
                                break;
                            case 2: themCauHoiVaoDe(dt); break;
                            case 3: {
                                String maCH = inputLine("Mã câu hỏi cần xóa khỏi đề: ");
                                System.out.println(dt.xoaCauHoi(maCH) ? ">> Đã xóa khỏi đề." : ">> Không tìm thấy trong đề.");
                                break;
                            }
                            case 0: tiep = false; break;
                            default: System.out.println(">> Không hợp lệ!");
                        }
                    }
                    System.out.println(">> Cập nhật thành công!");
                    break;
                }
                case 4: {
                    String ma = inputLine("Nhập mã đề thi cần xóa: ");
                    boolean ok = qlht.getDsDeThi().xoaTheoDieuKien(d -> d.getMaDeThi().equals(ma));
                    System.out.println(ok ? ">> Đã xóa." : ">> Không tìm thấy!");
                    break;
                }
                case 5: {
                    String kw = inputLine("Nhập từ khóa (tên đề / mã môn): ").toLowerCase();
                    List<DeThi> kq = qlht.getDsDeThi().timDanhSach(d ->
                            d.getTenDeThi().toLowerCase().contains(kw) || d.getMaMonHoc().toLowerCase().contains(kw));
                    if (kq.isEmpty()) System.out.println(">> Không tìm thấy kết quả.");
                    for (DeThi dt : kq) System.out.println(dt);
                    break;
                }
                case 0: back = true; break;
                default: System.out.println(">> Không hợp lệ!");
            }
        }
    }

    static void themCauHoiVaoDe(DeThi dt) {
        boolean tiep = true;
        while (tiep) {
            String maCH = inputLine("Nhập mã câu hỏi để thêm vào đề (để trống để dừng): ");
            if (maCH.isEmpty()) { tiep = false; break; }
            CauHoi c = qlht.getDsCauHoi().timTheoDieuKien(x -> x.getMaCauHoi().equals(maCH));
            if (c == null) { System.out.println(">> Không tìm thấy câu hỏi mã " + maCH); continue; }
            dt.themCauHoi(c);
            System.out.println(">> Đã thêm câu hỏi " + maCH + " vào đề " + dt.getMaDeThi());
        }
    }

    // ================= 6. LÀM BÀI THI & CHẤM ĐIỂM TỰ ĐỘNG =================
    static void lamBaiThi() {
        String mssv = inputLine("Nhập MSSV sinh viên dự thi: ");
        SinhVien sv = qlht.getDsSinhVien().timTheoDieuKien(s -> s.getMssv().equals(mssv));
        if (sv == null) { System.out.println(">> Không tìm thấy sinh viên!"); return; }

        String maDe = inputLine("Nhập mã đề thi: ");
        DeThi dt = qlht.getDsDeThi().timTheoDieuKien(d -> d.getMaDeThi().equals(maDe));
        if (dt == null || dt.getDanhSachCauHoi().isEmpty()) {
            System.out.println(">> Không tìm thấy đề thi hoặc đề thi chưa có câu hỏi!");
            return;
        }

        System.out.println(">> Bắt đầu làm đề: " + dt.getTenDeThi() + " (" + dt.getDanhSachCauHoi().size() + " câu, "
                + dt.getThoiGianLamBai() + " phút)");
        String maBaiLam = "BL" + String.format("%04d", qlht.getDsKetQua().soLuong() + 1);
        BaiLamThi baiLam = new BaiLamThi(maBaiLam, mssv, maDe);

        for (CauHoi c : dt.getDanhSachCauHoi()) {
            c.hienThiCauHoi();
            List<Integer> luaChon = new ArrayList<>();
            if (c.loaiCauHoi().equals("Nhiều đáp án")) {
                String s = inputLine("Chọn CÁC đáp án (cách nhau dấu phẩy): ");
                for (String x : s.split(",")) {
                    try { luaChon.add(Integer.parseInt(x.trim())); } catch (Exception ignored) { }
                }
            } else {
                String s = inputLine("Chọn 1 đáp án: ");
                try { luaChon.add(Integer.parseInt(s.trim())); } catch (Exception ignored) { }
            }
            baiLam.themCauTraLoi(new CauTraLoi(c.getMaCauHoi(), luaChon));
        }

        KetQuaThi kq = qlht.chamThiTuDong(baiLam, dt);
        System.out.println("\n>> KẾT QUẢ: " + kq);
    }

    // ================= 7. THỐNG KÊ =================
    static void thongKe() {
        List<KetQuaThi> ds = qlht.getDsKetQua().layTatCa();
        if (ds.isEmpty()) { System.out.println(">> Chưa có kết quả thi nào."); return; }
        System.out.println("\n--- THỐNG KÊ KẾT QUẢ THI ---");
        System.out.println("Số lượt thi         : " + ds.size());
        System.out.println("Điểm trung bình      : " + ThongKe.diemTrungBinh(ds));
        System.out.println("Số lượt đạt (>=5.0)  : " + ThongKe.demSoDat(ds, 5.0));
        KetQuaThi cao = ThongKe.diemCaoNhat(ds);
        System.out.println("Điểm cao nhất        : " + (cao != null ? cao.toString() : "-"));
        System.out.println("Phân loại:");
        for (Map.Entry<String, Long> e : ThongKe.thongKeXepLoai(ds).entrySet()) {
            System.out.printf("   %-12s: %d%n", e.getKey(), e.getValue());
        }
    }

    // ================= DỮ LIỆU MẪU (DEMO) =================
    static void taoDuLieuMau() {
        MonHoc mh1 = new MonHoc("Lập trình hướng đối tượng", 4);
        MonHoc mh2 = new MonHoc("Cơ sở dữ liệu", 3);
        qlht.getDsMonHoc().them(mh1);
        qlht.getDsMonHoc().them(mh2);

        qlht.getDsGiaoVien().them(new GiaoVien("ND001", "Nguyễn Văn A", "01/01/1980",
                "TP.HCM", "0901111111", "Công nghệ phần mềm", "Thạc sĩ"));

        SinhVien sv1 = new SinhVien("ND002", "Trần Thị B", "12/05/2004", "TP.HCM",
                "0902222222", "SV001", "DH22CNTT01", "b.tran@example.com");
        SinhVien sv2 = new SinhVien("ND003", "Lê Văn C", "20/09/2004", "Hà Nội",
                "0903333333", "SV002", "DH22CNTT01", "c.le@example.com");
        qlht.getDsSinhVien().them(sv1);
        qlht.getDsSinhVien().them(sv2);

        List<DapAn> da1 = new ArrayList<>();
        da1.add(new DapAn("Class", false));
        da1.add(new DapAn("Object", false));
        da1.add(new DapAn("Interface", false));
        da1.add(new DapAn("Kế thừa (Inheritance)", true));
        CauHoi ch1 = new CauHoiMotDapAn(
                "Cơ chế cho phép lớp con sử dụng lại thuộc tính/phương thức của lớp cha gọi là gì?",
                mh1.getMaMon(), "Dễ", da1);

        List<DapAn> da2 = new ArrayList<>();
        da2.add(new DapAn("Encapsulation (Đóng gói)", true));
        da2.add(new DapAn("Inheritance (Kế thừa)", true));
        da2.add(new DapAn("Polymorphism (Đa hình)", true));
        da2.add(new DapAn("Compilation (Biên dịch)", false));
        CauHoi ch2 = new CauHoiNhieuDapAn(
                "Những đặc trưng nào sau đây thuộc về lập trình hướng đối tượng?",
                mh1.getMaMon(), "Trung bình", da2);

        List<DapAn> da3 = new ArrayList<>();
        da3.add(new DapAn("SELECT", true));
        da3.add(new DapAn("PRINT", false));
        da3.add(new DapAn("SHOW", false));
        da3.add(new DapAn("GET", false));
        CauHoi ch3 = new CauHoiMotDapAn(
                "Câu lệnh nào dùng để truy vấn dữ liệu trong SQL?",
                mh2.getMaMon(), "Dễ", da3);

        qlht.getDsCauHoi().them(ch1);
        qlht.getDsCauHoi().them(ch2);
        qlht.getDsCauHoi().them(ch3);

        DeThi dt1 = new DeThi("Đề thi giữa kỳ OOP", mh1.getMaMon(), 30);
        dt1.themCauHoi(ch1);
        dt1.themCauHoi(ch2);
        qlht.getDsDeThi().them(dt1);

        DeThi dt2 = new DeThi("Đề thi giữa kỳ CSDL", mh2.getMaMon(), 20);
        dt2.themCauHoi(ch3);
        qlht.getDsDeThi().them(dt2);
    }
}
