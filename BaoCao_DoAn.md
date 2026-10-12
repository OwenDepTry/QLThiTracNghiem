# ĐỒ ÁN: ỨNG DỤNG QUẢN LÝ THI TRẮC NGHIỆM (Java 17, console)

> Đã biên dịch bằng `javac --release 17` và chạy thử toàn bộ kịch bản demo, kể cả các trường hợp nhập sai, file dữ liệu lỗi và thư mục `data/` chưa tồn tại.
> Tổng cộng **21 lớp** (không tính 3 interface và `Main`), khoảng 3.300 dòng mã.

## 1. Cây thư mục project

```
QuanLyThiTracNghiem/
├── src/
│   └── tracnghiem/
│       ├── IQuanLy.java              (interface)
│       ├── IFileIO.java              (interface)
│       ├── IChamDiem.java            (interface)
│       ├── NhapLieu.java             (1)
│       ├── NguoiDung.java            (2)  abstract
│       ├── ThiSinh.java              (3)
│       ├── GiaoVien.java             (4)
│       ├── MonHoc.java               (5)
│       ├── LuaChon.java              (6)
│       ├── CauHoi.java               (7)  abstract, implements IChamDiem
│       ├── CauHoiCoLuaChon.java      (8)  abstract
│       ├── CauHoiMotDapAn.java       (9)
│       ├── CauHoiNhieuDapAn.java     (10)
│       ├── CauHoiDungSai.java        (11)
│       ├── DeThi.java                (12)
│       ├── KetQuaThi.java            (13)
│       ├── DanhSach.java             (14) abstract, implements IQuanLy<T>, IFileIO
│       ├── DanhSachMonHoc.java       (15)
│       ├── DanhSachNguoiDung.java    (16)
│       ├── DanhSachCauHoi.java       (17)
│       ├── DanhSachDeThi.java        (18)
│       ├── DanhSachKetQua.java       (19)
│       ├── BuoiThi.java              (20)
│       ├── QuanLyThiTracNghiem.java  (21)
│       └── Main.java                 (không tính)
└── data/
    ├── monhoc.txt
    ├── nguoidung.txt
    ├── cauhoi.txt
    ├── dethi.txt
    └── ketqua.txt
```

## 2. Bảng đối chiếu yêu cầu

| # | Yêu cầu | Lớp / phương thức đáp ứng | Giải thích ngắn |
|---|---|---|---|
| 1 | ≥ 15 lớp | 21 lớp liệt kê ở mục 1 | Không tính `IQuanLy`, `IFileIO`, `IChamDiem`, `Main`. |
| 2 | Constructor không tham số + có tham số, lớp con gọi `super(...)` | Tất cả 21 lớp, ví dụ `ThiSinh()` → `super()`, `ThiSinh(ma, hoTen, ...)` → `super(ma, hoTen, ngaySinh, email)`; `DanhSachCauHoi()` → `super(THU_MUC_MAC_DINH + "/" + TEN_FILE)`; `NhapLieu()` → `this(sc)` | Constructor không tham số đều gán giá trị mặc định hợp lệ hoặc gọi `this(...)`. |
| 3 | ≥ 2 cây kế thừa có nghĩa nghiệp vụ | (a) `NguoiDung` → `ThiSinh`, `GiaoVien`; (b) `CauHoi` → `CauHoiCoLuaChon` → `CauHoiMotDapAn`, `CauHoiNhieuDapAn`; `CauHoi` → `CauHoiDungSai`; (c) `DanhSach<T>` → 5 lớp danh sách | 3 cây; cây câu hỏi có 3 tầng. |
| 4 | Đa hình | `ArrayList<CauHoi>` trong `BuoiThi.lamBai()` gọi `hienThiKhiThi()`, `laTraLoiHopLe()`, `kiemTraDapAn()`, `chamDiem()`; `ArrayList<NguoiDung>` trong `DanhSachNguoiDung` gọi `hienThi()`, `toFileString()`, `getVaiTro()` | Một danh sách kiểu lớp cha chứa nhiều lớp con; Java chọn phương thức theo đối tượng thực tế lúc chạy. |
| 5 | Lớp mảng đối tượng dùng `ArrayList` | `DanhSach<T>.danhSach` (`ArrayList<T>`) và 5 lớp con `DanhSachMonHoc`, `DanhSachNguoiDung`, `DanhSachCauHoi`, `DanhSachDeThi`, `DanhSachKetQua` | Mỗi thực thể chính có một lớp danh sách. |
| 6 | Đọc/ghi file UTF-8 cho tất cả danh sách, tự đọc khi khởi động, tự ghi sau thêm/sửa/xóa | `DanhSach.docFile()`, `DanhSach.ghiFile()`; `them()`, `sua()`, `xoa()` đều gọi `luu()`; `QuanLyThiTracNghiem` constructor gọi `taiDuLieu()` | File chưa có thì tự tạo; dòng lỗi bị bỏ qua và báo số dòng. |
| 7 | Thuộc tính static + phương thức static | `boDem` (static) và `sinhMa()`, `capNhatBoDem()` trong `ThiSinh`, `GiaoVien`, `MonHoc`, `CauHoi`, `DeThi`, `KetQuaThi`; `Scanner sc` static và toàn bộ `NhapLieu.nhapXxx()`, `kiemTraEmail()`, `kiemTraMa()` | Bộ đếm dùng chung cho mọi đối tượng cùng lớp để sinh mã tự động. |
| 8 | Lớp trừu tượng + phương thức trừu tượng | `NguoiDung` (`getVaiTro`, `hienThi`, `toFileString`), `CauHoi` (`kiemTraDapAn`, `hienThi`, `toFileString`, ...), `CauHoiCoLuaChon`, `DanhSach<T>` (`layMa`, `phanTichDong`, `chuyenThanhDong`, `hienThiPhanTu`, `inTieuDe`) | 4 lớp abstract. |
| 9 | ≥ 2 interface được implement thật | `IQuanLy<T>` và `IFileIO` do `DanhSach<T>` cài đặt; `IChamDiem` do `CauHoi` cài đặt và `CauHoiNhieuDapAn` override | `BuoiThi` dùng biến `IChamDiem boChamDiem = ch;` để chấm điểm. |
| 10 | Đủ 5 chức năng, tìm ≥ 2 tiêu chí | Menu con trong `QuanLyThiTracNghiem`: Môn học (mã, tên); Người dùng (mã, tên, vai trò, email); Câu hỏi (mã, từ khóa, môn, loại, độ khó); Đề thi (mã, tên, môn); Kết quả (mã, thí sinh, đề, khoảng điểm) | Tìm theo tên không phân biệt dấu (`NhapLieu.boDau`). |

**Ràng buộc thêm:** không cho trùng mã (`nhapMaMoi`, `DanhSach.them`); xóa luôn hỏi y/n (`NhapLieu.xacNhan`); xóa môn học hoặc câu hỏi đang được đề thi dùng thì in **CẢNH BÁO** (`xoaMonHoc`, `xoaCauHoi`); không cho nhập ký tự `|` vào dữ liệu.

## 3. Sơ đồ kế thừa

```
«interface» IQuanLy<T>     «interface» IFileIO        «interface» IChamDiem
          \                     /                              |
           \                   /                               |
     «abstract» DanhSach<T> ---                         «abstract» CauHoi
        ├── DanhSachMonHoc        (T = MonHoc)             ├── «abstract» CauHoiCoLuaChon   (chứa ArrayList<LuaChon>)
        ├── DanhSachNguoiDung     (T = NguoiDung)          │      ├── CauHoiMotDapAn
        ├── DanhSachCauHoi        (T = CauHoi)             │      └── CauHoiNhieuDapAn     (override chamDiem: điểm từng phần)
        ├── DanhSachDeThi         (T = DeThi)              └── CauHoiDungSai
        └── DanhSachKetQua        (T = KetQuaThi)

«abstract» NguoiDung
        ├── ThiSinh
        └── GiaoVien

Lớp độc lập: MonHoc, LuaChon, DeThi, KetQuaThi, NhapLieu, BuoiThi, QuanLyThiTracNghiem, Main
Quan hệ chứa: QuanLyThiTracNghiem có 5 DanhSach + 1 BuoiThi; BuoiThi dùng chung 4 DanhSach;
              DeThi chứa danh sách mã CauHoi; KetQuaThi tham chiếu mã ThiSinh và mã DeThi.
```

## 4. Mã nguồn đầy đủ

### 4.1. `src/tracnghiem/IQuanLy.java`

```java
package tracnghiem;

/**
 * INTERFACE: hợp đồng quản lý chung (thêm, sửa, xóa, tìm, hiển thị).
 * Được implement bởi lớp trừu tượng DanhSach<T>, nên mọi lớp danh sách đều có đủ 5 thao tác này.
 *
 * @param <T> kiểu đối tượng được quản lý
 */
public interface IQuanLy<T> {

    /** Thêm một đối tượng; trả về false nếu trùng mã. */
    boolean them(T doiTuong);

    /** Thay đối tượng có mã {@code ma} bằng đối tượng mới; trả về false nếu không tìm thấy. */
    boolean sua(String ma, T doiTuongMoi);

    /** Xóa đối tượng theo mã; trả về false nếu không tìm thấy. */
    boolean xoa(String ma);

    /** Tìm đối tượng theo mã (không phân biệt hoa thường); không có thì trả về null. */
    T timTheoMa(String ma);

    /** In toàn bộ danh sách ra màn hình. */
    void hienThiTatCa();
}
```

### 4.2. `src/tracnghiem/IFileIO.java`

```java
package tracnghiem;

/**
 * INTERFACE: hợp đồng đọc/ghi file text.
 * Được implement bởi lớp trừu tượng DanhSach<T>.
 */
public interface IFileIO {

    /**
     * Đọc dữ liệu từ file text UTF-8 vào danh sách (xóa dữ liệu cũ trong bộ nhớ).
     * Nếu file chưa tồn tại thì tự tạo file rỗng.
     *
     * @return số đối tượng đọc được
     */
    int docFile(String duongDan);

    /**
     * Ghi toàn bộ danh sách ra file text UTF-8, mỗi dòng một đối tượng.
     *
     * @return true nếu ghi thành công
     */
    boolean ghiFile(String duongDan);
}
```

### 4.3. `src/tracnghiem/IChamDiem.java`

```java
package tracnghiem;

/**
 * INTERFACE: hợp đồng chấm điểm một câu trả lời.
 * Được implement bởi lớp trừu tượng CauHoi; lớp CauHoiNhieuDapAn override lại để chấm điểm từng phần.
 */
public interface IChamDiem {

    /**
     * Chấm điểm câu trả lời.
     *
     * @param traLoi     câu trả lời thí sinh nhập
     * @param diemToiDa  số điểm tối đa của câu
     * @return số điểm đạt được (từ 0 đến diemToiDa)
     */
    double chamDiem(String traLoi, double diemToiDa);
}
```

### 4.4. `src/tracnghiem/NhapLieu.java`

```java
package tracnghiem;

import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Lớp tiện ích nhập liệu và kiểm tra dữ liệu.
 * STATIC: mọi phương thức đều là static và dùng chung MỘT Scanner static,
 * tránh tạo nhiều Scanner trên System.in (gây mất dữ liệu đệm).
 * Mọi phương thức nhập đều bắt lỗi, báo lỗi tiếng Việt và cho nhập lại.
 */
public class NhapLieu {

    // THUỘC TÍNH STATIC: Scanner dùng chung cho toàn chương trình
    private static Scanner sc = new Scanner(System.in, StandardCharsets.UTF_8);

    // THUỘC TÍNH STATIC: định dạng ngày dd/MM/yyyy, chế độ STRICT để chặn ngày sai như 31/02
    public static final DateTimeFormatter DINH_DANG_NGAY =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private static final Pattern MAU_EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+$");
    private static final Pattern MAU_MA = Pattern.compile("^[A-Z0-9_]{2,15}$");

    /** Constructor không tham số: giữ nguyên Scanner dùng chung hiện tại. */
    public NhapLieu() {
        this(sc);
    }

    /** Constructor có tham số: cho phép thay nguồn nhập dùng chung (ví dụ khi kiểm thử). */
    public NhapLieu(Scanner nguonNhap) {
        if (nguonNhap != null) {
            sc = nguonNhap;
        }
    }

    // ================== PHƯƠNG THỨC STATIC ĐỌC DÒNG ==================

    /** Đọc một dòng; nếu hết dữ liệu nhập (Ctrl+D / Ctrl+Z) thì kết thúc chương trình êm đẹp. */
    private static String docDong() {
        if (!sc.hasNextLine()) {
            System.out.println();
            System.out.println("Không còn dữ liệu nhập. Chương trình kết thúc.");
            System.exit(0);
        }
        return sc.nextLine().trim();
    }

    // ================== PHƯƠNG THỨC STATIC KIỂM TRA DỮ LIỆU ==================

    /** Ký tự '|' dùng làm dấu ngăn trường trong file nên không được xuất hiện trong dữ liệu nhập. */
    public static boolean chuaKyTuCam(String s) {
        return s != null && s.contains("|");
    }

    public static boolean kiemTraEmail(String email) {
        return email != null && MAU_EMAIL.matcher(email).matches();
    }

    /** Mã hợp lệ: 2-15 ký tự gồm chữ in hoa, số, gạch dưới. */
    public static boolean kiemTraMa(String ma) {
        return ma != null && MAU_MA.matcher(ma).matches();
    }

    // ================== NHẬP CHUỖI ==================

    /** Nhập chuỗi bắt buộc, không rỗng, không chứa '|'. */
    public static String nhapChuoi(String nhanDe) {
        while (true) {
            System.out.print(nhanDe + ": ");
            String s = docDong();
            if (s.isEmpty()) {
                System.out.println("Lỗi: không được để trống, vui lòng nhập lại.");
            } else if (chuaKyTuCam(s)) {
                System.out.println("Lỗi: không được chứa ký tự '|', vui lòng nhập lại.");
            } else {
                return s;
            }
        }
    }

    /** Nhập chuỗi khi sửa: Enter để giữ giá trị cũ. */
    public static String nhapChuoiTuyChon(String nhanDe, String macDinh) {
        while (true) {
            System.out.print(nhanDe + " [" + macDinh + "]: ");
            String s = docDong();
            if (s.isEmpty()) {
                return macDinh;
            }
            if (chuaKyTuCam(s)) {
                System.out.println("Lỗi: không được chứa ký tự '|', vui lòng nhập lại.");
            } else {
                return s;
            }
        }
    }

    /** Nhập chuỗi được phép rỗng (dùng khi thí sinh bỏ qua câu hỏi). */
    public static String nhapChuoiCoTheRong(String nhanDe) {
        while (true) {
            System.out.print(nhanDe + ": ");
            String s = docDong();
            if (chuaKyTuCam(s)) {
                System.out.println("Lỗi: không được chứa ký tự '|', vui lòng nhập lại.");
            } else {
                return s;
            }
        }
    }

    // ================== NHẬP MÃ ==================

    /** Nhập mã bắt buộc, tự đổi sang chữ in hoa và kiểm tra định dạng. */
    public static String nhapMa(String nhanDe) {
        while (true) {
            String ma = nhapChuoi(nhanDe).toUpperCase();
            if (kiemTraMa(ma)) {
                return ma;
            }
            System.out.println("Lỗi: mã chỉ gồm 2-15 ký tự chữ, số hoặc '_' (ví dụ TS006).");
        }
    }

    /** Nhập mã, Enter để dùng mã mặc định (ví dụ mã tự sinh). */
    public static String nhapMaTuyChon(String nhanDe, String macDinh) {
        while (true) {
            String ma = nhapChuoiTuyChon(nhanDe, macDinh).toUpperCase();
            if (kiemTraMa(ma)) {
                return ma;
            }
            System.out.println("Lỗi: mã chỉ gồm 2-15 ký tự chữ, số hoặc '_' (ví dụ TS006).");
        }
    }

    // ================== NHẬP SỐ NGUYÊN ==================

    /** Chuyển chuỗi thành số nguyên trong [min, max]; lỗi thì in thông báo và trả về null. */
    private static Integer chuyenSoNguyen(String s, int min, int max) {
        try {
            int so = Integer.parseInt(s);
            if (so < min || so > max) {
                System.out.println("Lỗi: giá trị phải trong khoảng " + min + " đến " + max + ".");
                return null;
            }
            return so;
        } catch (NumberFormatException e) {
            System.out.println("Lỗi: \"" + s + "\" không phải số nguyên hợp lệ, vui lòng nhập lại.");
            return null;
        }
    }

    public static int nhapSoNguyen(String nhanDe, int min, int max) {
        while (true) {
            System.out.print(nhanDe + ": ");
            Integer so = chuyenSoNguyen(docDong(), min, max);
            if (so != null) {
                return so;
            }
        }
    }

    public static int nhapSoNguyenTuyChon(String nhanDe, int min, int max, int macDinh) {
        while (true) {
            System.out.print(nhanDe + " [" + macDinh + "]: ");
            String s = docDong();
            if (s.isEmpty()) {
                return macDinh;
            }
            Integer so = chuyenSoNguyen(s, min, max);
            if (so != null) {
                return so;
            }
        }
    }

    // ================== NHẬP SỐ THỰC ==================

    /** Chuyển chuỗi thành số thực trong [min, max], chấp nhận cả dấu phẩy thập phân. */
    private static Double chuyenSoThuc(String s, double min, double max) {
        try {
            double so = Double.parseDouble(s.replace(',', '.'));
            if (!Double.isFinite(so) || so < min || so > max) {
                System.out.println("Lỗi: giá trị phải trong khoảng " + min + " đến " + max + ".");
                return null;
            }
            return so;
        } catch (NumberFormatException e) {
            System.out.println("Lỗi: \"" + s + "\" không phải số hợp lệ, vui lòng nhập lại.");
            return null;
        }
    }

    public static double nhapSoThuc(String nhanDe, double min, double max) {
        while (true) {
            System.out.print(nhanDe + ": ");
            Double so = chuyenSoThuc(docDong(), min, max);
            if (so != null) {
                return so;
            }
        }
    }

    public static double nhapSoThucTuyChon(String nhanDe, double min, double max, double macDinh) {
        while (true) {
            System.out.print(nhanDe + " [" + macDinh + "]: ");
            String s = docDong();
            if (s.isEmpty()) {
                return macDinh;
            }
            Double so = chuyenSoThuc(s, min, max);
            if (so != null) {
                return so;
            }
        }
    }

    // ================== NHẬP NGÀY ==================

    /** Chuyển chuỗi dd/MM/yyyy thành ngày; ngày phải từ năm 1900 và không sau hôm nay. */
    private static LocalDate chuyenNgay(String s) {
        try {
            LocalDate ngay = LocalDate.parse(s, DINH_DANG_NGAY);
            if (ngay.isAfter(LocalDate.now())) {
                System.out.println("Lỗi: ngày không được sau ngày hôm nay.");
                return null;
            }
            if (ngay.getYear() < 1900) {
                System.out.println("Lỗi: năm phải từ 1900 trở đi.");
                return null;
            }
            return ngay;
        } catch (DateTimeParseException e) {
            System.out.println("Lỗi: ngày \"" + s + "\" không hợp lệ (định dạng dd/MM/yyyy, ví dụ 05/09/2004).");
            return null;
        }
    }

    public static LocalDate nhapNgay(String nhanDe) {
        while (true) {
            System.out.print(nhanDe + " (dd/MM/yyyy): ");
            LocalDate ngay = chuyenNgay(docDong());
            if (ngay != null) {
                return ngay;
            }
        }
    }

    public static LocalDate nhapNgayTuyChon(String nhanDe, LocalDate macDinh) {
        while (true) {
            System.out.print(nhanDe + " (dd/MM/yyyy) [" + dinhDangNgay(macDinh) + "]: ");
            String s = docDong();
            if (s.isEmpty()) {
                return macDinh;
            }
            LocalDate ngay = chuyenNgay(s);
            if (ngay != null) {
                return ngay;
            }
        }
    }

    /** Đọc ngày từ file (ném ngoại lệ nếu sai để lớp danh sách bỏ qua dòng lỗi). */
    public static LocalDate docNgay(String s) {
        return LocalDate.parse(s.trim(), DINH_DANG_NGAY);
    }

    public static String dinhDangNgay(LocalDate ngay) {
        return ngay == null ? "" : ngay.format(DINH_DANG_NGAY);
    }

    // ================== NHẬP EMAIL ==================

    public static String nhapEmail(String nhanDe) {
        while (true) {
            String email = nhapChuoi(nhanDe);
            if (kiemTraEmail(email)) {
                return email;
            }
            System.out.println("Lỗi: email không hợp lệ (ví dụ ten@truong.edu.vn).");
        }
    }

    public static String nhapEmailTuyChon(String nhanDe, String macDinh) {
        while (true) {
            String email = nhapChuoiTuyChon(nhanDe, macDinh);
            if (kiemTraEmail(email)) {
                return email;
            }
            System.out.println("Lỗi: email không hợp lệ (ví dụ ten@truong.edu.vn).");
        }
    }

    // ================== XÁC NHẬN ==================

    /** Hỏi xác nhận y/n, nhập sai thì hỏi lại. */
    public static boolean xacNhan(String cauHoi) {
        while (true) {
            System.out.print(cauHoi + " (y/n): ");
            String s = docDong().toLowerCase();
            if (s.equals("y")) {
                return true;
            }
            if (s.equals("n")) {
                return false;
            }
            System.out.println("Lỗi: chỉ nhập y (đồng ý) hoặc n (không).");
        }
    }

    public static void dungManHinh() {
        System.out.print("Nhấn Enter để tiếp tục...");
        docDong();
    }

    // ================== TIỆN ÍCH STATIC KHÁC ==================

    /** Bỏ dấu tiếng Việt và đổi sang chữ thường, để tìm "nguyen" ra "Nguyễn". */
    public static String boDau(String s) {
        if (s == null) {
            return "";
        }
        String khongDau = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return khongDau.replace('đ', 'd').replace('Đ', 'D').toLowerCase();
    }

    /** Tìm gần đúng: không phân biệt hoa thường và dấu. */
    public static boolean chuaTuKhoa(String nguon, String tuKhoa) {
        return boDau(nguon).contains(boDau(tuKhoa).trim());
    }

    /** STATIC hỗ trợ sinh mã: lấy phần số của mã, ví dụ ("TS012", "TS") -> 12; sai dạng -> -1. */
    public static int maThanhSo(String ma, String tienTo) {
        if (ma == null || !ma.toUpperCase().startsWith(tienTo)) {
            return -1;
        }
        String phanSo = ma.substring(tienTo.length());
        if (phanSo.isEmpty() || phanSo.length() > 9) {
            return -1;
        }
        for (char c : phanSo.toCharArray()) {
            if (c < '0' || c > '9') {
                return -1;
            }
        }
        return Integer.parseInt(phanSo);
    }

    /** STATIC sinh mã: ("TS", 6) -> "TS006". */
    public static String taoMa(String tienTo, int so) {
        return String.format("%s%03d", tienTo, so);
    }

    public static double lamTron2(double so) {
        return Math.round(so * 100.0) / 100.0;
    }

    /** Rút gọn chuỗi dài khi in bảng. */
    public static String rutGon(String s, int doDaiToiDa) {
        if (s == null || s.length() <= doDaiToiDa) {
            return s;
        }
        return s.substring(0, doDaiToiDa - 3) + "...";
    }
}
```

### 4.5. `src/tracnghiem/NguoiDung.java`

```java
package tracnghiem;

import java.time.LocalDate;

/**
 * LỚP TRỪU TƯỢNG (abstract): khái niệm chung "người dùng" của hệ thống thi.
 * Không tạo đối tượng NguoiDung trực tiếp, chỉ tạo qua lớp con.
 * KẾ THỪA: ThiSinh extends NguoiDung, GiaoVien extends NguoiDung.
 */
public abstract class NguoiDung {

    // Định dạng một dòng khi in bảng, dùng chung cho các lớp con
    protected static final String DINH_DANG_DONG = "%-7s %-10s %-24s %-11s %-28s %s%n";

    protected String ma;
    protected String hoTen;
    protected LocalDate ngaySinh;
    protected String email;

    public NguoiDung() {
        this("", "", LocalDate.of(2000, 1, 1), "");
    }

    public NguoiDung(String ma, String hoTen, LocalDate ngaySinh, String email) {
        this.ma = ma;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.email = email;
    }

    public String getMa() {
        return ma;
    }

    public void setMa(String ma) {
        this.ma = ma;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // ===== PHƯƠNG THỨC TRỪU TƯỢNG: lớp con BẮT BUỘC override =====

    /** Tên vai trò: "Thí sinh" hoặc "Giáo viên". */
    public abstract String getVaiTro();

    /** In một dòng thông tin ra bảng. */
    public abstract void hienThi();

    /** Chuỗi một dòng để ghi file, bắt đầu bằng ký hiệu loại (THISINH|, GIAOVIEN|). */
    public abstract String toFileString();

    /** Phần dữ liệu chung khi ghi file, lớp con nối thêm phần riêng. */
    protected String thongTinChungFile() {
        return ma + "|" + hoTen + "|" + NhapLieu.dinhDangNgay(ngaySinh) + "|" + email;
    }

    /** In một dòng bảng; getVaiTro() được gọi ĐA HÌNH theo đối tượng thực tế. */
    protected void inDong(String thongTinRieng) {
        System.out.printf(DINH_DANG_DONG, ma, getVaiTro(), hoTen,
                NhapLieu.dinhDangNgay(ngaySinh), email, thongTinRieng);
    }

    /** STATIC: in tiêu đề bảng người dùng. */
    public static void inTieuDe() {
        System.out.printf(DINH_DANG_DONG, "Mã", "Vai trò", "Họ tên", "Ngày sinh", "Email", "Thông tin riêng");
        System.out.println("-".repeat(110));
    }
}
```

### 4.6. `src/tracnghiem/ThiSinh.java`

```java
package tracnghiem;

import java.time.LocalDate;

/**
 * KẾ THỪA: ThiSinh là một NguoiDung, có thêm thuộc tính lớp.
 * Override các phương thức trừu tượng getVaiTro(), hienThi(), toFileString().
 */
public class ThiSinh extends NguoiDung {

    public static final String KY_HIEU_LOAI = "THISINH";
    public static final String VAI_TRO = "Thí sinh";
    public static final String TIEN_TO = "TS";

    // THUỘC TÍNH STATIC: bộ đếm dùng chung cho mọi thí sinh, phục vụ sinh mã tự động
    private static int boDem = 0;

    private String lop;

    public ThiSinh() {
        super();
        this.lop = "";
    }

    public ThiSinh(String ma, String hoTen, LocalDate ngaySinh, String email, String lop) {
        super(ma, hoTen, ngaySinh, email); // gọi constructor lớp cha
        this.lop = lop;
        capNhatBoDem(ma);
    }

    // PHƯƠNG THỨC STATIC: sinh mã kế tiếp, ví dụ TS006
    public static String sinhMa() {
        return NhapLieu.taoMa(TIEN_TO, boDem + 1);
    }

    // PHƯƠNG THỨC STATIC: cập nhật bộ đếm theo mã lớn nhất đã có (gọi khi đọc file / tạo mới)
    public static void capNhatBoDem(String ma) {
        int so = NhapLieu.maThanhSo(ma, TIEN_TO);
        if (so > boDem) {
            boDem = so;
        }
    }

    public String getLop() {
        return lop;
    }

    public void setLop(String lop) {
        this.lop = lop;
    }

    @Override // ĐA HÌNH: override phương thức trừu tượng
    public String getVaiTro() {
        return VAI_TRO;
    }

    @Override
    public void hienThi() {
        inDong("Lớp: " + lop);
    }

    @Override
    public String toFileString() {
        return KY_HIEU_LOAI + "|" + thongTinChungFile() + "|" + lop;
    }
}
```

### 4.7. `src/tracnghiem/GiaoVien.java`

```java
package tracnghiem;

import java.time.LocalDate;

/**
 * KẾ THỪA: GiaoVien là một NguoiDung, có thêm thuộc tính chuyên môn.
 * Override các phương thức trừu tượng getVaiTro(), hienThi(), toFileString().
 */
public class GiaoVien extends NguoiDung {

    public static final String KY_HIEU_LOAI = "GIAOVIEN";
    public static final String VAI_TRO = "Giáo viên";
    public static final String TIEN_TO = "GV";

    // THUỘC TÍNH STATIC: bộ đếm sinh mã giáo viên
    private static int boDem = 0;

    private String chuyenMon;

    public GiaoVien() {
        super();
        this.chuyenMon = "";
    }

    public GiaoVien(String ma, String hoTen, LocalDate ngaySinh, String email, String chuyenMon) {
        super(ma, hoTen, ngaySinh, email); // gọi constructor lớp cha
        this.chuyenMon = chuyenMon;
        capNhatBoDem(ma);
    }

    // PHƯƠNG THỨC STATIC: sinh mã kế tiếp, ví dụ GV003
    public static String sinhMa() {
        return NhapLieu.taoMa(TIEN_TO, boDem + 1);
    }

    public static void capNhatBoDem(String ma) {
        int so = NhapLieu.maThanhSo(ma, TIEN_TO);
        if (so > boDem) {
            boDem = so;
        }
    }

    public String getChuyenMon() {
        return chuyenMon;
    }

    public void setChuyenMon(String chuyenMon) {
        this.chuyenMon = chuyenMon;
    }

    @Override // ĐA HÌNH: override phương thức trừu tượng
    public String getVaiTro() {
        return VAI_TRO;
    }

    @Override
    public void hienThi() {
        inDong("Chuyên môn: " + chuyenMon);
    }

    @Override
    public String toFileString() {
        return KY_HIEU_LOAI + "|" + thongTinChungFile() + "|" + chuyenMon;
    }
}
```

### 4.8. `src/tracnghiem/MonHoc.java`

```java
package tracnghiem;

/**
 * Thực thể môn học. Câu hỏi và đề thi đều thuộc về một môn học (qua mã môn).
 */
public class MonHoc {

    public static final String KY_HIEU_LOAI = "MONHOC";
    public static final String TIEN_TO = "MH";
    private static final String DINH_DANG_DONG = "%-7s %-40s %s%n";

    // THUỘC TÍNH STATIC: bộ đếm sinh mã môn học
    private static int boDem = 0;

    private String ma;
    private String tenMon;
    private int soTinChi;

    public MonHoc() {
        this("", "", 1);
    }

    public MonHoc(String ma, String tenMon, int soTinChi) {
        this.ma = ma;
        this.tenMon = tenMon;
        this.soTinChi = soTinChi;
        capNhatBoDem(ma);
    }

    // PHƯƠNG THỨC STATIC: sinh mã kế tiếp, ví dụ MH004
    public static String sinhMa() {
        return NhapLieu.taoMa(TIEN_TO, boDem + 1);
    }

    public static void capNhatBoDem(String ma) {
        int so = NhapLieu.maThanhSo(ma, TIEN_TO);
        if (so > boDem) {
            boDem = so;
        }
    }

    public String getMa() {
        return ma;
    }

    public void setMa(String ma) {
        this.ma = ma;
    }

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    public int getSoTinChi() {
        return soTinChi;
    }

    public void setSoTinChi(int soTinChi) {
        this.soTinChi = soTinChi;
    }

    public void hienThi() {
        System.out.printf(DINH_DANG_DONG, ma, tenMon, soTinChi);
    }

    public String toFileString() {
        return KY_HIEU_LOAI + "|" + ma + "|" + tenMon + "|" + soTinChi;
    }

    public static void inTieuDe() {
        System.out.printf(DINH_DANG_DONG, "Mã", "Tên môn học", "Số tín chỉ");
        System.out.println("-".repeat(60));
    }
}
```

### 4.9. `src/tracnghiem/LuaChon.java`

```java
package tracnghiem;

import java.util.List;

/**
 * Một lựa chọn (phương án) của câu hỏi trắc nghiệm, ví dụ "B. extends".
 * Câu hỏi có lựa chọn CHỨA một danh sách LuaChon (quan hệ has-a / thành phần).
 */
public class LuaChon {

    private char kyHieu;
    private String noiDung;

    public LuaChon() {
        this('A', "");
    }

    public LuaChon(char kyHieu, String noiDung) {
        this.kyHieu = Character.toUpperCase(kyHieu);
        this.noiDung = noiDung;
    }

    public char getKyHieu() {
        return kyHieu;
    }

    public void setKyHieu(char kyHieu) {
        this.kyHieu = Character.toUpperCase(kyHieu);
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public void hienThi() {
        System.out.println("      " + kyHieu + ". " + noiDung);
    }

    /** STATIC: kiểm tra ký hiệu có nằm trong danh sách lựa chọn không. */
    public static boolean coKyHieu(List<LuaChon> danhSach, char kyHieu) {
        char k = Character.toUpperCase(kyHieu);
        for (LuaChon lc : danhSach) {
            if (lc.getKyHieu() == k) {
                return true;
            }
        }
        return false;
    }
}
```

### 4.10. `src/tracnghiem/CauHoi.java`

```java
package tracnghiem;

/**
 * LỚP TRỪU TƯỢNG (abstract) gốc của cây kế thừa câu hỏi:
 *   CauHoi -> CauHoiCoLuaChon -> CauHoiMotDapAn, CauHoiNhieuDapAn
 *   CauHoi -> CauHoiDungSai
 * INTERFACE: implements IChamDiem, cài đặt chamDiem() dựa trên phương thức trừu tượng kiemTraDapAn().
 * "Câu hỏi chung chung" không biết cách kiểm tra đáp án, nên CauHoi phải là abstract.
 */
public abstract class CauHoi implements IChamDiem {

    public static final String TIEN_TO = "CH";

    // THUỘC TÍNH STATIC: bộ đếm sinh mã dùng chung cho mọi loại câu hỏi
    private static int boDem = 0;

    protected String ma;
    protected String noiDung;
    protected String maMonHoc;
    protected int doKho; // 1 = Dễ, 2 = Trung bình, 3 = Khó

    public CauHoi() {
        this("", "", "", 1);
    }

    public CauHoi(String ma, String noiDung, String maMonHoc, int doKho) {
        this.ma = ma;
        this.noiDung = noiDung;
        this.maMonHoc = maMonHoc;
        this.doKho = doKho;
        capNhatBoDem(ma);
    }

    // PHƯƠNG THỨC STATIC: sinh mã kế tiếp, ví dụ CH016
    public static String sinhMa() {
        return NhapLieu.taoMa(TIEN_TO, boDem + 1);
    }

    public static void capNhatBoDem(String ma) {
        int so = NhapLieu.maThanhSo(ma, TIEN_TO);
        if (so > boDem) {
            boDem = so;
        }
    }

    // PHƯƠNG THỨC STATIC: đổi số độ khó sang chữ
    public static String tenDoKho(int doKho) {
        switch (doKho) {
            case 1:
                return "Dễ";
            case 2:
                return "Trung bình";
            case 3:
                return "Khó";
            default:
                return "Không rõ";
        }
    }

    public String getMa() {
        return ma;
    }

    public void setMa(String ma) {
        this.ma = ma;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public String getMaMonHoc() {
        return maMonHoc;
    }

    public void setMaMonHoc(String maMonHoc) {
        this.maMonHoc = maMonHoc;
    }

    public int getDoKho() {
        return doKho;
    }

    public void setDoKho(int doKho) {
        this.doKho = doKho;
    }

    // ===== PHƯƠNG THỨC TRỪU TƯỢNG: mỗi loại câu hỏi tự cài đặt (ĐA HÌNH) =====

    /** Tên loại để hiển thị, ví dụ "Một đáp án". */
    public abstract String getLoai();

    /** Ký hiệu loại ở đầu dòng file: MOT, NHIEU, DUNGSAI. */
    public abstract String getKyHieuLoai();

    /** Kiểm tra câu trả lời có hoàn toàn đúng không. */
    public abstract boolean kiemTraDapAn(String traLoi);

    /** Kiểm tra câu trả lời có đúng định dạng của loại câu hỏi không (để báo nhập lại). */
    public abstract boolean laTraLoiHopLe(String traLoi);

    /** Hướng dẫn cách nhập câu trả lời. */
    public abstract String getHuongDanTraLoi();

    /** Đáp án đúng dạng chuỗi để in khi xem kết quả. */
    public abstract String getDapAnDungHienThi();

    /** In câu hỏi cho thí sinh làm bài (KHÔNG hiện đáp án). */
    public abstract void hienThiKhiThi(int soThuTu);

    /** In đầy đủ câu hỏi kèm đáp án (dành cho quản lý). */
    public abstract void hienThi();

    /** Chuỗi một dòng để ghi file. */
    public abstract String toFileString();

    // ===== CÀI ĐẶT INTERFACE IChamDiem =====

    /** Mặc định: đúng hoàn toàn được trọn điểm, sai được 0 điểm. Gọi kiemTraDapAn() ĐA HÌNH. */
    @Override
    public double chamDiem(String traLoi, double diemToiDa) {
        return kiemTraDapAn(traLoi) ? diemToiDa : 0.0;
    }

    /** Phần chung khi ghi file: LOAI|ma|noiDung|maMonHoc|doKho. */
    protected String thongTinChungFile() {
        return getKyHieuLoai() + "|" + ma + "|" + noiDung + "|" + maMonHoc + "|" + doKho;
    }

    /** In phần đầu dùng chung khi xem câu hỏi. */
    protected void inPhanChung() {
        System.out.printf("[%s] %s | Môn: %s | Độ khó: %s%n", ma, getLoai(), maMonHoc, tenDoKho(doKho));
        System.out.println("    " + noiDung);
    }
}
```

### 4.11. `src/tracnghiem/CauHoiCoLuaChon.java`

```java
package tracnghiem;

import java.util.ArrayList;
import java.util.List;

/**
 * LỚP TRỪU TƯỢNG trung gian: câu hỏi có danh sách lựa chọn A, B, C...
 * KẾ THỪA: extends CauHoi; là lớp cha của CauHoiMotDapAn và CauHoiNhieuDapAn.
 * Gom phần dùng chung (danh sách lựa chọn, cách hiển thị) để hai lớp con không lặp code.
 */
public abstract class CauHoiCoLuaChon extends CauHoi {

    public static final int SO_LUA_CHON_TOI_THIEU = 2;
    public static final int SO_LUA_CHON_TOI_DA = 6;

    protected ArrayList<LuaChon> danhSachLuaChon;

    public CauHoiCoLuaChon() {
        super();
        this.danhSachLuaChon = new ArrayList<>();
    }

    public CauHoiCoLuaChon(String ma, String noiDung, String maMonHoc, int doKho, List<LuaChon> danhSachLuaChon) {
        super(ma, noiDung, maMonHoc, doKho); // gọi constructor lớp cha CauHoi
        this.danhSachLuaChon = new ArrayList<>(danhSachLuaChon);
    }

    public ArrayList<LuaChon> getDanhSachLuaChon() {
        return new ArrayList<>(danhSachLuaChon);
    }

    public void setDanhSachLuaChon(List<LuaChon> danhSachLuaChon) {
        this.danhSachLuaChon = new ArrayList<>(danhSachLuaChon);
    }

    public boolean coKyHieu(char kyHieu) {
        return LuaChon.coKyHieu(danhSachLuaChon, kyHieu);
    }

    protected void hienThiCacLuaChon() {
        for (LuaChon lc : danhSachLuaChon) {
            lc.hienThi();
        }
    }

    /** Nội dung các lựa chọn nối bằng '|' để ghi file. */
    protected String cacLuaChonFile() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < danhSachLuaChon.size(); i++) {
            if (i > 0) {
                sb.append("|");
            }
            sb.append(danhSachLuaChon.get(i).getNoiDung());
        }
        return sb.toString();
    }

    @Override // Override phương thức trừu tượng của CauHoi, dùng chung cho 2 lớp con
    public void hienThiKhiThi(int soThuTu) {
        System.out.println("Câu " + soThuTu + " (" + getLoai() + "): " + noiDung);
        hienThiCacLuaChon();
    }
}
```

### 4.12. `src/tracnghiem/CauHoiMotDapAn.java`

```java
package tracnghiem;

import java.util.List;

/**
 * KẾ THỪA: câu hỏi chọn đúng MỘT đáp án, extends CauHoiCoLuaChon.
 * ĐA HÌNH: override kiemTraDapAn() so sánh 1 ký tự.
 * Dòng file: MOT|ma|noiDung|maMonHoc|doKho|dapAnDung|luaChonA|luaChonB|...
 */
public class CauHoiMotDapAn extends CauHoiCoLuaChon {

    public static final String KY_HIEU_LOAI = "MOT";

    private char dapAnDung;

    public CauHoiMotDapAn() {
        super();
        this.dapAnDung = 'A';
    }

    public CauHoiMotDapAn(String ma, String noiDung, String maMonHoc, int doKho,
                          List<LuaChon> danhSachLuaChon, char dapAnDung) {
        super(ma, noiDung, maMonHoc, doKho, danhSachLuaChon); // gọi constructor lớp cha
        this.dapAnDung = Character.toUpperCase(dapAnDung);
    }

    public char getDapAnDung() {
        return dapAnDung;
    }

    public void setDapAnDung(char dapAnDung) {
        this.dapAnDung = Character.toUpperCase(dapAnDung);
    }

    @Override
    public String getLoai() {
        return "Một đáp án";
    }

    @Override
    public String getKyHieuLoai() {
        return KY_HIEU_LOAI;
    }

    @Override
    public boolean laTraLoiHopLe(String traLoi) {
        return traLoi != null && traLoi.trim().length() == 1 && coKyHieu(traLoi.trim().charAt(0));
    }

    @Override // ĐA HÌNH: cách kiểm tra riêng của câu một đáp án
    public boolean kiemTraDapAn(String traLoi) {
        return laTraLoiHopLe(traLoi) && Character.toUpperCase(traLoi.trim().charAt(0)) == dapAnDung;
    }

    @Override
    public String getHuongDanTraLoi() {
        return "Nhập MỘT ký hiệu lựa chọn, ví dụ: B";
    }

    @Override
    public String getDapAnDungHienThi() {
        return String.valueOf(dapAnDung);
    }

    @Override
    public void hienThi() {
        inPhanChung();
        hienThiCacLuaChon();
        System.out.println("    => Đáp án đúng: " + dapAnDung);
    }

    @Override
    public String toFileString() {
        return thongTinChungFile() + "|" + dapAnDung + "|" + cacLuaChonFile();
    }
}
```

### 4.13. `src/tracnghiem/CauHoiNhieuDapAn.java`

```java
package tracnghiem;

import java.util.List;
import java.util.TreeSet;

/**
 * KẾ THỪA: câu hỏi có NHIỀU đáp án đúng, extends CauHoiCoLuaChon.
 * ĐA HÌNH: override kiemTraDapAn() so sánh tập đáp án, và override chamDiem() để chấm điểm từng phần.
 * Dòng file: NHIEU|ma|noiDung|maMonHoc|doKho|AC|luaChonA|luaChonB|...
 */
public class CauHoiNhieuDapAn extends CauHoiCoLuaChon {

    public static final String KY_HIEU_LOAI = "NHIEU";
    public static final int SO_DAP_AN_TOI_THIEU = 2;

    private String dapAnDung; // đã chuẩn hóa, ví dụ "AC"

    public CauHoiNhieuDapAn() {
        super();
        this.dapAnDung = "AB";
    }

    public CauHoiNhieuDapAn(String ma, String noiDung, String maMonHoc, int doKho,
                            List<LuaChon> danhSachLuaChon, String dapAnDung) {
        super(ma, noiDung, maMonHoc, doKho, danhSachLuaChon); // gọi constructor lớp cha
        this.dapAnDung = chuanHoaDapAn(dapAnDung);
    }

    /** STATIC: chuẩn hóa "c, a" -> "AC" (in hoa, bỏ trùng, sắp xếp). */
    public static String chuanHoaDapAn(String s) {
        TreeSet<Character> tap = new TreeSet<>();
        for (char c : s.toUpperCase().toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                tap.add(c);
            }
        }
        StringBuilder sb = new StringBuilder();
        for (char c : tap) {
            sb.append(c);
        }
        return sb.toString();
    }

    /** STATIC: chuỗi chỉ gồm ký hiệu có trong danh sách lựa chọn, dấu phẩy và khoảng trắng. */
    public static boolean laChuoiDapAnHopLe(String s, List<LuaChon> danhSachLuaChon) {
        if (s == null || s.isBlank()) {
            return false;
        }
        for (char c : s.toUpperCase().toCharArray()) {
            if (c == ',' || c == ' ') {
                continue;
            }
            if (!LuaChon.coKyHieu(danhSachLuaChon, c)) {
                return false;
            }
        }
        return !chuanHoaDapAn(s).isEmpty();
    }

    public String getDapAnDung() {
        return dapAnDung;
    }

    public void setDapAnDung(String dapAnDung) {
        this.dapAnDung = chuanHoaDapAn(dapAnDung);
    }

    @Override
    public String getLoai() {
        return "Nhiều đáp án";
    }

    @Override
    public String getKyHieuLoai() {
        return KY_HIEU_LOAI;
    }

    @Override
    public boolean laTraLoiHopLe(String traLoi) {
        return laChuoiDapAnHopLe(traLoi, danhSachLuaChon);
    }

    @Override // ĐA HÌNH: phải chọn ĐÚNG và ĐỦ tập đáp án
    public boolean kiemTraDapAn(String traLoi) {
        return laTraLoiHopLe(traLoi) && chuanHoaDapAn(traLoi).equals(dapAnDung);
    }

    /**
     * OVERRIDE phương thức chamDiem() của lớp cha CauHoi (interface IChamDiem):
     * chọn sai bất kỳ phương án nào -> 0 điểm; chỉ chọn đúng nhưng chưa đủ -> điểm theo tỉ lệ.
     */
    @Override
    public double chamDiem(String traLoi, double diemToiDa) {
        if (!laTraLoiHopLe(traLoi)) {
            return 0.0;
        }
        String daChon = chuanHoaDapAn(traLoi);
        for (char c : daChon.toCharArray()) {
            if (dapAnDung.indexOf(c) < 0) {
                return 0.0;
            }
        }
        return diemToiDa * daChon.length() / dapAnDung.length();
    }

    @Override
    public String getHuongDanTraLoi() {
        return "Nhập các ký hiệu đúng, cách nhau dấu phẩy, ví dụ: A,C";
    }

    @Override
    public String getDapAnDungHienThi() {
        return String.join(",", dapAnDung.split(""));
    }

    @Override
    public void hienThi() {
        inPhanChung();
        hienThiCacLuaChon();
        System.out.println("    => Đáp án đúng: " + getDapAnDungHienThi());
    }

    @Override
    public String toFileString() {
        return thongTinChungFile() + "|" + dapAnDung + "|" + cacLuaChonFile();
    }
}
```

### 4.14. `src/tracnghiem/CauHoiDungSai.java`

```java
package tracnghiem;

/**
 * KẾ THỪA: câu hỏi Đúng/Sai, extends trực tiếp CauHoi (không có danh sách lựa chọn).
 * ĐA HÌNH: override kiemTraDapAn() so sánh giá trị boolean.
 * Dòng file: DUNGSAI|ma|noiDung|maMonHoc|doKho|D  (D = Đúng, S = Sai)
 */
public class CauHoiDungSai extends CauHoi {

    public static final String KY_HIEU_LOAI = "DUNGSAI";

    private boolean dapAnDung;

    public CauHoiDungSai() {
        super();
        this.dapAnDung = true;
    }

    public CauHoiDungSai(String ma, String noiDung, String maMonHoc, int doKho, boolean dapAnDung) {
        super(ma, noiDung, maMonHoc, doKho); // gọi constructor lớp cha
        this.dapAnDung = dapAnDung;
    }

    /** STATIC: đọc "D"/"Đúng" -> true, "S"/"Sai" -> false, còn lại -> null. */
    public static Boolean docTraLoi(String traLoi) {
        if (traLoi == null) {
            return null;
        }
        String s = NhapLieu.boDau(traLoi).trim().toUpperCase();
        if (s.equals("D") || s.equals("DUNG")) {
            return Boolean.TRUE;
        }
        if (s.equals("S") || s.equals("SAI")) {
            return Boolean.FALSE;
        }
        return null;
    }

    public boolean isDapAnDung() {
        return dapAnDung;
    }

    public void setDapAnDung(boolean dapAnDung) {
        this.dapAnDung = dapAnDung;
    }

    @Override
    public String getLoai() {
        return "Đúng/Sai";
    }

    @Override
    public String getKyHieuLoai() {
        return KY_HIEU_LOAI;
    }

    @Override
    public boolean laTraLoiHopLe(String traLoi) {
        return docTraLoi(traLoi) != null;
    }

    @Override // ĐA HÌNH: cách kiểm tra riêng của câu Đúng/Sai
    public boolean kiemTraDapAn(String traLoi) {
        Boolean giaTri = docTraLoi(traLoi);
        return giaTri != null && giaTri == dapAnDung;
    }

    @Override
    public String getHuongDanTraLoi() {
        return "Nhập D (Đúng) hoặc S (Sai)";
    }

    @Override
    public String getDapAnDungHienThi() {
        return dapAnDung ? "Đúng" : "Sai";
    }

    @Override
    public void hienThiKhiThi(int soThuTu) {
        System.out.println("Câu " + soThuTu + " (" + getLoai() + "): " + noiDung);
        System.out.println("      D. Đúng      S. Sai");
    }

    @Override
    public void hienThi() {
        inPhanChung();
        System.out.println("    => Đáp án đúng: " + getDapAnDungHienThi());
    }

    @Override
    public String toFileString() {
        return thongTinChungFile() + "|" + (dapAnDung ? "D" : "S");
    }
}
```

### 4.15. `src/tracnghiem/DeThi.java`

```java
package tracnghiem;

import java.util.ArrayList;
import java.util.List;

/**
 * Thực thể đề thi: thuộc một môn học, có thời gian làm bài và danh sách MÃ câu hỏi.
 * Dòng file: DETHI|ma|tenDe|maMonHoc|thoiGianLamBai|CH001,CH002,...
 */
public class DeThi {

    public static final String KY_HIEU_LOAI = "DETHI";
    public static final String TIEN_TO = "DT";
    private static final String DINH_DANG_DONG = "%-7s %-32s %-8s %-9s %-7s %s%n";

    // THUỘC TÍNH STATIC: bộ đếm sinh mã đề thi
    private static int boDem = 0;

    private String ma;
    private String tenDe;
    private String maMonHoc;
    private int thoiGianLamBai; // phút
    private ArrayList<String> danhSachMaCauHoi;

    public DeThi() {
        this("", "", "", 15, new ArrayList<>());
    }

    public DeThi(String ma, String tenDe, String maMonHoc, int thoiGianLamBai, List<String> danhSachMaCauHoi) {
        this.ma = ma;
        this.tenDe = tenDe;
        this.maMonHoc = maMonHoc;
        this.thoiGianLamBai = thoiGianLamBai;
        this.danhSachMaCauHoi = new ArrayList<>(danhSachMaCauHoi);
        capNhatBoDem(ma);
    }

    // PHƯƠNG THỨC STATIC: sinh mã kế tiếp, ví dụ DT003
    public static String sinhMa() {
        return NhapLieu.taoMa(TIEN_TO, boDem + 1);
    }

    public static void capNhatBoDem(String ma) {
        int so = NhapLieu.maThanhSo(ma, TIEN_TO);
        if (so > boDem) {
            boDem = so;
        }
    }

    public String getMa() {
        return ma;
    }

    public void setMa(String ma) {
        this.ma = ma;
    }

    public String getTenDe() {
        return tenDe;
    }

    public void setTenDe(String tenDe) {
        this.tenDe = tenDe;
    }

    public String getMaMonHoc() {
        return maMonHoc;
    }

    public void setMaMonHoc(String maMonHoc) {
        this.maMonHoc = maMonHoc;
    }

    public int getThoiGianLamBai() {
        return thoiGianLamBai;
    }

    public void setThoiGianLamBai(int thoiGianLamBai) {
        this.thoiGianLamBai = thoiGianLamBai;
    }

    public ArrayList<String> getDanhSachMaCauHoi() {
        return new ArrayList<>(danhSachMaCauHoi);
    }

    public void setDanhSachMaCauHoi(List<String> danhSachMaCauHoi) {
        this.danhSachMaCauHoi = new ArrayList<>(danhSachMaCauHoi);
    }

    public int getSoCauHoi() {
        return danhSachMaCauHoi.size();
    }

    public boolean coCauHoi(String maCauHoi) {
        for (String m : danhSachMaCauHoi) {
            if (m.equalsIgnoreCase(maCauHoi)) {
                return true;
            }
        }
        return false;
    }

    /** Gỡ một câu hỏi khỏi đề (dùng khi câu hỏi bị xóa). */
    public boolean xoaCauHoi(String maCauHoi) {
        return danhSachMaCauHoi.removeIf(m -> m.equalsIgnoreCase(maCauHoi));
    }

    public void hienThi() {
        System.out.printf(DINH_DANG_DONG, ma, tenDe, maMonHoc, thoiGianLamBai + " phút",
                getSoCauHoi() + " câu", String.join(",", danhSachMaCauHoi));
    }

    public String toFileString() {
        return KY_HIEU_LOAI + "|" + ma + "|" + tenDe + "|" + maMonHoc + "|" + thoiGianLamBai + "|"
                + String.join(",", danhSachMaCauHoi);
    }

    public static void inTieuDe() {
        System.out.printf(DINH_DANG_DONG, "Mã", "Tên đề", "Môn", "Thời gian", "Số câu", "Mã câu hỏi");
        System.out.println("-".repeat(110));
    }
}
```

### 4.16. `src/tracnghiem/KetQuaThi.java`

```java
package tracnghiem;

import java.time.LocalDate;

/**
 * Thực thể kết quả thi của một thí sinh với một đề thi.
 * Dòng file: KETQUA|ma|maThiSinh|maDeThi|diem|soCauDung|tongSoCau|ngayThi
 */
public class KetQuaThi {

    public static final String KY_HIEU_LOAI = "KETQUA";
    public static final String TIEN_TO = "KQ";
    private static final String DINH_DANG_DONG = "%-7s %-10s %-8s %6s %10s   %-11s %s%n";

    // THUỘC TÍNH STATIC: bộ đếm sinh mã kết quả
    private static int boDem = 0;

    private String ma;
    private String maThiSinh;
    private String maDeThi;
    private double diem;
    private int soCauDung;
    private int tongSoCau;
    private LocalDate ngayThi;

    public KetQuaThi() {
        this("", "", "", 0.0, 0, 0, LocalDate.now());
    }

    public KetQuaThi(String ma, String maThiSinh, String maDeThi, double diem,
                     int soCauDung, int tongSoCau, LocalDate ngayThi) {
        this.ma = ma;
        this.maThiSinh = maThiSinh;
        this.maDeThi = maDeThi;
        this.diem = diem;
        this.soCauDung = soCauDung;
        this.tongSoCau = tongSoCau;
        this.ngayThi = ngayThi;
        capNhatBoDem(ma);
    }

    // PHƯƠNG THỨC STATIC: sinh mã kế tiếp, ví dụ KQ004
    public static String sinhMa() {
        return NhapLieu.taoMa(TIEN_TO, boDem + 1);
    }

    public static void capNhatBoDem(String ma) {
        int so = NhapLieu.maThanhSo(ma, TIEN_TO);
        if (so > boDem) {
            boDem = so;
        }
    }

    // PHƯƠNG THỨC STATIC: xếp loại theo thang điểm 10
    public static String xepLoai(double diem) {
        if (diem >= 8.0) {
            return "Giỏi";
        }
        if (diem >= 6.5) {
            return "Khá";
        }
        if (diem >= 5.0) {
            return "Trung bình";
        }
        return "Yếu";
    }

    public String getMa() {
        return ma;
    }

    public void setMa(String ma) {
        this.ma = ma;
    }

    public String getMaThiSinh() {
        return maThiSinh;
    }

    public void setMaThiSinh(String maThiSinh) {
        this.maThiSinh = maThiSinh;
    }

    public String getMaDeThi() {
        return maDeThi;
    }

    public void setMaDeThi(String maDeThi) {
        this.maDeThi = maDeThi;
    }

    public double getDiem() {
        return diem;
    }

    public void setDiem(double diem) {
        this.diem = diem;
    }

    public int getSoCauDung() {
        return soCauDung;
    }

    public void setSoCauDung(int soCauDung) {
        this.soCauDung = soCauDung;
    }

    public int getTongSoCau() {
        return tongSoCau;
    }

    public void setTongSoCau(int tongSoCau) {
        this.tongSoCau = tongSoCau;
    }

    public LocalDate getNgayThi() {
        return ngayThi;
    }

    public void setNgayThi(LocalDate ngayThi) {
        this.ngayThi = ngayThi;
    }

    public void hienThi() {
        System.out.printf(DINH_DANG_DONG, ma, maThiSinh, maDeThi, String.format("%.2f", diem),
                soCauDung + "/" + tongSoCau, xepLoai(diem), NhapLieu.dinhDangNgay(ngayThi));
    }

    public String toFileString() {
        return KY_HIEU_LOAI + "|" + ma + "|" + maThiSinh + "|" + maDeThi + "|" + diem + "|"
                + soCauDung + "|" + tongSoCau + "|" + NhapLieu.dinhDangNgay(ngayThi);
    }

    public static void inTieuDe() {
        System.out.printf(DINH_DANG_DONG, "Mã", "Thí sinh", "Đề thi", "Điểm", "Đúng/Tổng", "Xếp loại", "Ngày thi");
        System.out.println("-".repeat(75));
    }
}
```

### 4.17. `src/tracnghiem/DanhSach.java`

```java
package tracnghiem;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * LỚP TRỪU TƯỢNG (abstract) cho mọi "lớp mảng đối tượng", bên trong dùng ArrayList.
 * INTERFACE: implements IQuanLy<T> (CRUD) và IFileIO (đọc/ghi file).
 * Phần chung (thêm/sửa/xóa/tìm/đọc/ghi file) viết MỘT lần ở đây;
 * lớp con chỉ override các PHƯƠNG THỨC TRỪU TƯỢNG riêng: lấy mã, đọc một dòng, ghi một dòng, in một phần tử.
 * KẾ THỪA: DanhSachMonHoc, DanhSachNguoiDung, DanhSachCauHoi, DanhSachDeThi, DanhSachKetQua.
 *
 * @param <T> kiểu phần tử
 */
public abstract class DanhSach<T> implements IQuanLy<T>, IFileIO {

    // THUỘC TÍNH STATIC: thư mục dữ liệu mặc định
    public static final String THU_MUC_MAC_DINH = "data";

    protected ArrayList<T> danhSach;
    protected String duongDanFile;

    public DanhSach() {
        this("");
    }

    public DanhSach(String duongDanFile) {
        this.danhSach = new ArrayList<>();
        this.duongDanFile = duongDanFile;
    }

    // ===== PHƯƠNG THỨC TRỪU TƯỢNG: lớp con bắt buộc cài đặt =====

    /** Lấy mã của một phần tử. */
    protected abstract String layMa(T doiTuong);

    /** Chuyển một dòng file thành đối tượng; dòng sai thì ném ngoại lệ. */
    protected abstract T phanTichDong(String dong);

    /** Chuyển một đối tượng thành một dòng file. */
    protected abstract String chuyenThanhDong(T doiTuong);

    /** In một phần tử ra màn hình. */
    protected abstract void hienThiPhanTu(T doiTuong);

    /** In tiêu đề bảng. */
    protected abstract void inTieuDe();

    // ===== CÀI ĐẶT INTERFACE IQuanLy<T> =====

    private int viTri(String ma) {
        if (ma == null) {
            return -1;
        }
        for (int i = 0; i < danhSach.size(); i++) {
            if (layMa(danhSach.get(i)).equalsIgnoreCase(ma.trim())) {
                return i;
            }
        }
        return -1;
    }

    /** Thêm và ghi file ngay; trùng mã thì không thêm. */
    @Override
    public boolean them(T doiTuong) {
        if (doiTuong == null || viTri(layMa(doiTuong)) >= 0) {
            return false;
        }
        danhSach.add(doiTuong);
        luu();
        return true;
    }

    /** Sửa và ghi file ngay; mã của đối tượng mới phải trùng mã cũ. */
    @Override
    public boolean sua(String ma, T doiTuongMoi) {
        int i = viTri(ma);
        if (i < 0 || doiTuongMoi == null || !layMa(doiTuongMoi).equalsIgnoreCase(ma.trim())) {
            return false;
        }
        danhSach.set(i, doiTuongMoi);
        luu();
        return true;
    }

    /** Xóa và ghi file ngay. */
    @Override
    public boolean xoa(String ma) {
        int i = viTri(ma);
        if (i < 0) {
            return false;
        }
        danhSach.remove(i);
        luu();
        return true;
    }

    @Override
    public T timTheoMa(String ma) {
        int i = viTri(ma);
        return i < 0 ? null : danhSach.get(i);
    }

    @Override
    public void hienThiTatCa() {
        hienThiDanhSach(danhSach);
    }

    /** In một danh sách bất kỳ (dùng cho kết quả tìm kiếm). */
    public void hienThiDanhSach(List<T> ds) {
        if (ds.isEmpty()) {
            System.out.println("(Không có dữ liệu phù hợp)");
            return;
        }
        inTieuDe();
        for (T dt : ds) {
            hienThiPhanTu(dt);
        }
        System.out.println("Tổng cộng: " + ds.size());
    }

    public List<T> getTatCa() {
        return Collections.unmodifiableList(danhSach);
    }

    public int soLuong() {
        return danhSach.size();
    }

    public String getDuongDanFile() {
        return duongDanFile;
    }

    public void setDuongDanFile(String duongDanFile) {
        this.duongDanFile = duongDanFile;
    }

    /** Ghi danh sách ra file mặc định. */
    public boolean luu() {
        return ghiFile(duongDanFile);
    }

    /** Đọc lại danh sách từ file mặc định. */
    public int docLai() {
        return docFile(duongDanFile);
    }

    // ===== CÀI ĐẶT INTERFACE IFileIO =====

    @Override
    public int docFile(String duongDan) {
        if (duongDan == null || duongDan.isBlank()) {
            System.out.println("Lỗi: chưa cấu hình đường dẫn file.");
            return 0;
        }
        Path p = Paths.get(duongDan);
        try {
            taoFileNeuChuaCo(p);
            List<String> cacDong = Files.readAllLines(p, StandardCharsets.UTF_8);
            danhSach.clear();
            int soDong = 0;
            for (String dong : cacDong) {
                soDong++;
                String d = dong.replace("﻿", "").trim(); // bỏ BOM nếu file lưu bằng Notepad
                if (d.isEmpty() || d.startsWith("#")) {
                    continue;
                }
                try {
                    T dt = phanTichDong(d); // ĐA HÌNH: lớp con quyết định tạo đối tượng gì
                    String ma = layMa(dt);
                    if (ma == null || ma.isBlank()) {
                        throw new IllegalArgumentException("mã trống");
                    }
                    if (viTri(ma) >= 0) {
                        throw new IllegalArgumentException("trùng mã " + ma);
                    }
                    danhSach.add(dt);
                } catch (NumberFormatException e) {
                    baoDongLoi(duongDan, soDong, "số không hợp lệ");
                } catch (DateTimeParseException e) {
                    baoDongLoi(duongDan, soDong, "ngày không hợp lệ");
                } catch (RuntimeException e) {
                    baoDongLoi(duongDan, soDong, e.getMessage());
                }
            }
            return danhSach.size();
        } catch (IOException e) {
            System.out.println("Lỗi đọc file " + duongDan + " (kiểm tra file có lưu dạng UTF-8 không): " + e.getMessage());
            return 0;
        }
    }

    @Override
    public boolean ghiFile(String duongDan) {
        if (duongDan == null || duongDan.isBlank()) {
            System.out.println("Lỗi: chưa cấu hình đường dẫn file.");
            return false;
        }
        Path p = Paths.get(duongDan);
        try {
            if (p.getParent() != null) {
                Files.createDirectories(p.getParent());
            }
            List<String> cacDong = new ArrayList<>();
            for (T dt : danhSach) {
                cacDong.add(chuyenThanhDong(dt)); // ĐA HÌNH: mỗi đối tượng tự biết cách ghi
            }
            Files.write(p, cacDong, StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            System.out.println("Lỗi ghi file " + duongDan + ": " + e.getMessage());
            return false;
        }
    }

    // ===== HỖ TRỢ =====

    private static void taoFileNeuChuaCo(Path p) throws IOException {
        if (p.getParent() != null) {
            Files.createDirectories(p.getParent());
        }
        if (!Files.exists(p)) {
            Files.createFile(p);
        }
    }

    private static void baoDongLoi(String duongDan, int soDong, String lyDo) {
        System.out.println("Cảnh báo: bỏ qua dòng " + soDong + " trong " + duongDan + " (" + lyDo + ")");
    }

    /** STATIC: tách dòng theo '|' và kiểm tra đủ số trường. */
    protected static String[] tachTruong(String dong, int soTruongToiThieu) {
        String[] t = dong.split("\\|", -1);
        if (t.length < soTruongToiThieu) {
            throw new IllegalArgumentException("thiếu trường dữ liệu, cần ít nhất " + soTruongToiThieu);
        }
        for (int i = 0; i < t.length; i++) {
            t[i] = t[i].trim();
        }
        return t;
    }

    /** STATIC: kiểm tra ký hiệu loại ở đầu dòng. */
    protected static void kiemTraLoai(String thucTe, String mongDoi) {
        if (!thucTe.equalsIgnoreCase(mongDoi)) {
            throw new IllegalArgumentException("ký hiệu loại phải là " + mongDoi + " nhưng gặp " + thucTe);
        }
    }
}
```

### 4.18. `src/tracnghiem/DanhSachMonHoc.java`

```java
package tracnghiem;

import java.util.ArrayList;
import java.util.List;

/**
 * KẾ THỪA: lớp danh sách môn học, extends DanhSach<MonHoc>.
 * Tìm kiếm: theo mã (timTheoMa kế thừa) và theo tên (timTheoTen).
 */
public class DanhSachMonHoc extends DanhSach<MonHoc> {

    public static final String TEN_FILE = "monhoc.txt";

    public DanhSachMonHoc() {
        super(THU_MUC_MAC_DINH + "/" + TEN_FILE);
    }

    public DanhSachMonHoc(String duongDanFile) {
        super(duongDanFile);
    }

    @Override
    protected String layMa(MonHoc doiTuong) {
        return doiTuong.getMa();
    }

    @Override
    protected MonHoc phanTichDong(String dong) {
        String[] t = tachTruong(dong, 4);
        kiemTraLoai(t[0], MonHoc.KY_HIEU_LOAI);
        int soTinChi = Integer.parseInt(t[3]);
        if (soTinChi < 1 || soTinChi > 10) {
            throw new IllegalArgumentException("số tín chỉ phải từ 1 đến 10");
        }
        return new MonHoc(t[1].toUpperCase(), t[2], soTinChi);
    }

    @Override
    protected String chuyenThanhDong(MonHoc doiTuong) {
        return doiTuong.toFileString();
    }

    @Override
    protected void hienThiPhanTu(MonHoc doiTuong) {
        doiTuong.hienThi();
    }

    @Override
    protected void inTieuDe() {
        MonHoc.inTieuDe();
    }

    /** Tìm theo tên gần đúng, không phân biệt hoa thường và dấu. */
    public List<MonHoc> timTheoTen(String tuKhoa) {
        List<MonHoc> ketQua = new ArrayList<>();
        for (MonHoc mh : danhSach) {
            if (NhapLieu.chuaTuKhoa(mh.getTenMon(), tuKhoa)) {
                ketQua.add(mh);
            }
        }
        return ketQua;
    }
}
```

### 4.19. `src/tracnghiem/DanhSachNguoiDung.java`

```java
package tracnghiem;

import java.util.ArrayList;
import java.util.List;

/**
 * KẾ THỪA: lớp danh sách người dùng, extends DanhSach<NguoiDung>.
 * ĐA HÌNH: một ArrayList<NguoiDung> chứa lẫn ThiSinh và GiaoVien;
 * gọi nd.hienThi(), nd.toFileString(), nd.getVaiTro() sẽ chạy đúng phiên bản của lớp con.
 * Tìm kiếm: theo mã, theo tên, theo vai trò, theo email.
 */
public class DanhSachNguoiDung extends DanhSach<NguoiDung> {

    public static final String TEN_FILE = "nguoidung.txt";

    public DanhSachNguoiDung() {
        super(THU_MUC_MAC_DINH + "/" + TEN_FILE);
    }

    public DanhSachNguoiDung(String duongDanFile) {
        super(duongDanFile);
    }

    @Override
    protected String layMa(NguoiDung doiTuong) {
        return doiTuong.getMa();
    }

    /** Dựa vào ký hiệu loại đầu dòng để tạo đúng lớp con. */
    @Override
    protected NguoiDung phanTichDong(String dong) {
        String[] t = tachTruong(dong, 6);
        String loai = t[0].toUpperCase();
        String ma = t[1].toUpperCase();
        if (!NhapLieu.kiemTraEmail(t[4])) {
            throw new IllegalArgumentException("email không hợp lệ");
        }
        if (loai.equals(ThiSinh.KY_HIEU_LOAI)) {
            return new ThiSinh(ma, t[2], NhapLieu.docNgay(t[3]), t[4], t[5]);
        }
        if (loai.equals(GiaoVien.KY_HIEU_LOAI)) {
            return new GiaoVien(ma, t[2], NhapLieu.docNgay(t[3]), t[4], t[5]);
        }
        throw new IllegalArgumentException("loại người dùng không hợp lệ: " + t[0]);
    }

    @Override
    protected String chuyenThanhDong(NguoiDung doiTuong) {
        return doiTuong.toFileString(); // ĐA HÌNH
    }

    @Override
    protected void hienThiPhanTu(NguoiDung doiTuong) {
        doiTuong.hienThi(); // ĐA HÌNH
    }

    @Override
    protected void inTieuDe() {
        NguoiDung.inTieuDe();
    }

    public List<NguoiDung> timTheoTen(String tuKhoa) {
        List<NguoiDung> ketQua = new ArrayList<>();
        for (NguoiDung nd : danhSach) {
            if (NhapLieu.chuaTuKhoa(nd.getHoTen(), tuKhoa)) {
                ketQua.add(nd);
            }
        }
        return ketQua;
    }

    /** Tìm theo vai trò: gọi getVaiTro() ĐA HÌNH. */
    public List<NguoiDung> timTheoVaiTro(String vaiTro) {
        List<NguoiDung> ketQua = new ArrayList<>();
        for (NguoiDung nd : danhSach) {
            if (nd.getVaiTro().equalsIgnoreCase(vaiTro)) {
                ketQua.add(nd);
            }
        }
        return ketQua;
    }

    public List<NguoiDung> timTheoEmail(String tuKhoa) {
        List<NguoiDung> ketQua = new ArrayList<>();
        for (NguoiDung nd : danhSach) {
            if (nd.getEmail().toLowerCase().contains(tuKhoa.toLowerCase().trim())) {
                ketQua.add(nd);
            }
        }
        return ketQua;
    }

    /** Lấy riêng các thí sinh (dùng cho buổi thi). */
    public List<ThiSinh> layDanhSachThiSinh() {
        List<ThiSinh> ketQua = new ArrayList<>();
        for (NguoiDung nd : danhSach) {
            if (nd instanceof ThiSinh) {
                ketQua.add((ThiSinh) nd);
            }
        }
        return ketQua;
    }
}
```

### 4.20. `src/tracnghiem/DanhSachCauHoi.java`

```java
package tracnghiem;

import java.util.ArrayList;
import java.util.List;

/**
 * KẾ THỪA: lớp danh sách câu hỏi, extends DanhSach<CauHoi>.
 * ĐA HÌNH: một ArrayList<CauHoi> chứa lẫn CauHoiMotDapAn, CauHoiNhieuDapAn, CauHoiDungSai.
 * Tìm kiếm: theo mã, theo từ khóa nội dung, theo môn học, theo loại, theo độ khó.
 */
public class DanhSachCauHoi extends DanhSach<CauHoi> {

    public static final String TEN_FILE = "cauhoi.txt";

    public DanhSachCauHoi() {
        super(THU_MUC_MAC_DINH + "/" + TEN_FILE);
    }

    public DanhSachCauHoi(String duongDanFile) {
        super(duongDanFile);
    }

    @Override
    protected String layMa(CauHoi doiTuong) {
        return doiTuong.getMa();
    }

    /**
     * Ký hiệu đầu dòng quyết định tạo lớp con nào:
     * MOT|...  -> CauHoiMotDapAn, NHIEU|... -> CauHoiNhieuDapAn, DUNGSAI|... -> CauHoiDungSai.
     */
    @Override
    protected CauHoi phanTichDong(String dong) {
        String[] t = tachTruong(dong, 6);
        String loai = t[0].toUpperCase();
        String ma = t[1].toUpperCase();
        String noiDung = t[2];
        String maMon = t[3].toUpperCase();
        int doKho = Integer.parseInt(t[4]);
        if (doKho < 1 || doKho > 3) {
            throw new IllegalArgumentException("độ khó phải từ 1 đến 3");
        }
        String dapAn = t[5].toUpperCase();

        if (loai.equals(CauHoiMotDapAn.KY_HIEU_LOAI)) {
            List<LuaChon> ds = docCacLuaChon(t, 6);
            if (dapAn.length() != 1 || !LuaChon.coKyHieu(ds, dapAn.charAt(0))) {
                throw new IllegalArgumentException("đáp án đúng không nằm trong các lựa chọn");
            }
            return new CauHoiMotDapAn(ma, noiDung, maMon, doKho, ds, dapAn.charAt(0));
        }
        if (loai.equals(CauHoiNhieuDapAn.KY_HIEU_LOAI)) {
            List<LuaChon> ds = docCacLuaChon(t, 6);
            if (!CauHoiNhieuDapAn.laChuoiDapAnHopLe(dapAn, ds)
                    || CauHoiNhieuDapAn.chuanHoaDapAn(dapAn).length() < CauHoiNhieuDapAn.SO_DAP_AN_TOI_THIEU) {
                throw new IllegalArgumentException("câu nhiều đáp án cần ít nhất 2 đáp án đúng nằm trong các lựa chọn");
            }
            return new CauHoiNhieuDapAn(ma, noiDung, maMon, doKho, ds, dapAn);
        }
        if (loai.equals(CauHoiDungSai.KY_HIEU_LOAI)) {
            Boolean giaTri = CauHoiDungSai.docTraLoi(dapAn);
            if (giaTri == null) {
                throw new IllegalArgumentException("đáp án Đúng/Sai phải là D hoặc S");
            }
            return new CauHoiDungSai(ma, noiDung, maMon, doKho, giaTri);
        }
        throw new IllegalArgumentException("loại câu hỏi không hợp lệ: " + t[0]);
    }

    /** STATIC: đọc các lựa chọn từ vị trí batDau, gán ký hiệu A, B, C... */
    private static List<LuaChon> docCacLuaChon(String[] t, int batDau) {
        int soLuong = t.length - batDau;
        if (soLuong < CauHoiCoLuaChon.SO_LUA_CHON_TOI_THIEU || soLuong > CauHoiCoLuaChon.SO_LUA_CHON_TOI_DA) {
            throw new IllegalArgumentException("số lựa chọn phải từ 2 đến 6");
        }
        List<LuaChon> ds = new ArrayList<>();
        for (int i = 0; i < soLuong; i++) {
            if (t[batDau + i].isEmpty()) {
                throw new IllegalArgumentException("lựa chọn " + (char) ('A' + i) + " bị trống");
            }
            ds.add(new LuaChon((char) ('A' + i), t[batDau + i]));
        }
        return ds;
    }

    @Override
    protected String chuyenThanhDong(CauHoi doiTuong) {
        return doiTuong.toFileString(); // ĐA HÌNH
    }

    @Override
    protected void hienThiPhanTu(CauHoi doiTuong) {
        doiTuong.hienThi(); // ĐA HÌNH: mỗi loại câu hỏi tự in theo cách riêng
        System.out.println("-".repeat(80));
    }

    @Override
    protected void inTieuDe() {
        System.out.println("=".repeat(80));
    }

    public List<CauHoi> timTheoTuKhoa(String tuKhoa) {
        List<CauHoi> ketQua = new ArrayList<>();
        for (CauHoi ch : danhSach) {
            if (NhapLieu.chuaTuKhoa(ch.getNoiDung(), tuKhoa)) {
                ketQua.add(ch);
            }
        }
        return ketQua;
    }

    public List<CauHoi> timTheoMonHoc(String maMonHoc) {
        List<CauHoi> ketQua = new ArrayList<>();
        for (CauHoi ch : danhSach) {
            if (ch.getMaMonHoc().equalsIgnoreCase(maMonHoc.trim())) {
                ketQua.add(ch);
            }
        }
        return ketQua;
    }

    /** Tìm theo loại: gọi getKyHieuLoai() ĐA HÌNH. */
    public List<CauHoi> timTheoLoai(String kyHieuLoai) {
        List<CauHoi> ketQua = new ArrayList<>();
        for (CauHoi ch : danhSach) {
            if (ch.getKyHieuLoai().equalsIgnoreCase(kyHieuLoai)) {
                ketQua.add(ch);
            }
        }
        return ketQua;
    }

    public List<CauHoi> timTheoDoKho(int doKho) {
        List<CauHoi> ketQua = new ArrayList<>();
        for (CauHoi ch : danhSach) {
            if (ch.getDoKho() == doKho) {
                ketQua.add(ch);
            }
        }
        return ketQua;
    }
}
```

### 4.21. `src/tracnghiem/DanhSachDeThi.java`

```java
package tracnghiem;

import java.util.ArrayList;
import java.util.List;

/**
 * KẾ THỪA: lớp danh sách đề thi, extends DanhSach<DeThi>.
 * Tìm kiếm: theo mã, theo tên, theo môn học. Có thêm hàm kiểm tra câu hỏi đang được đề nào dùng.
 */
public class DanhSachDeThi extends DanhSach<DeThi> {

    public static final String TEN_FILE = "dethi.txt";

    public DanhSachDeThi() {
        super(THU_MUC_MAC_DINH + "/" + TEN_FILE);
    }

    public DanhSachDeThi(String duongDanFile) {
        super(duongDanFile);
    }

    @Override
    protected String layMa(DeThi doiTuong) {
        return doiTuong.getMa();
    }

    @Override
    protected DeThi phanTichDong(String dong) {
        String[] t = tachTruong(dong, 6);
        kiemTraLoai(t[0], DeThi.KY_HIEU_LOAI);
        int thoiGian = Integer.parseInt(t[4]);
        if (thoiGian < 1 || thoiGian > 180) {
            throw new IllegalArgumentException("thời gian làm bài phải từ 1 đến 180 phút");
        }
        List<String> cacMa = new ArrayList<>();
        for (String m : t[5].split(",")) {
            String maCH = m.trim().toUpperCase();
            if (!maCH.isEmpty() && !cacMa.contains(maCH)) {
                cacMa.add(maCH);
            }
        }
        return new DeThi(t[1].toUpperCase(), t[2], t[3].toUpperCase(), thoiGian, cacMa);
    }

    @Override
    protected String chuyenThanhDong(DeThi doiTuong) {
        return doiTuong.toFileString();
    }

    @Override
    protected void hienThiPhanTu(DeThi doiTuong) {
        doiTuong.hienThi();
    }

    @Override
    protected void inTieuDe() {
        DeThi.inTieuDe();
    }

    public List<DeThi> timTheoTen(String tuKhoa) {
        List<DeThi> ketQua = new ArrayList<>();
        for (DeThi de : danhSach) {
            if (NhapLieu.chuaTuKhoa(de.getTenDe(), tuKhoa)) {
                ketQua.add(de);
            }
        }
        return ketQua;
    }

    public List<DeThi> timTheoMonHoc(String maMonHoc) {
        List<DeThi> ketQua = new ArrayList<>();
        for (DeThi de : danhSach) {
            if (de.getMaMonHoc().equalsIgnoreCase(maMonHoc.trim())) {
                ketQua.add(de);
            }
        }
        return ketQua;
    }

    /** Các đề đang chứa câu hỏi (để cảnh báo khi xóa câu hỏi). */
    public List<DeThi> timDeChuaCauHoi(String maCauHoi) {
        List<DeThi> ketQua = new ArrayList<>();
        for (DeThi de : danhSach) {
            if (de.coCauHoi(maCauHoi)) {
                ketQua.add(de);
            }
        }
        return ketQua;
    }

    /** Gỡ câu hỏi khỏi mọi đề rồi ghi file; trả về số đề bị ảnh hưởng. */
    public int goCauHoiKhoiCacDe(String maCauHoi) {
        int dem = 0;
        for (DeThi de : danhSach) {
            if (de.xoaCauHoi(maCauHoi)) {
                dem++;
            }
        }
        if (dem > 0) {
            luu();
        }
        return dem;
    }
}
```

### 4.22. `src/tracnghiem/DanhSachKetQua.java`

```java
package tracnghiem;

import java.util.ArrayList;
import java.util.List;

/**
 * KẾ THỪA: lớp danh sách kết quả thi, extends DanhSach<KetQuaThi>.
 * Tìm kiếm: theo mã, theo thí sinh, theo đề thi, theo khoảng điểm.
 */
public class DanhSachKetQua extends DanhSach<KetQuaThi> {

    public static final String TEN_FILE = "ketqua.txt";

    public DanhSachKetQua() {
        super(THU_MUC_MAC_DINH + "/" + TEN_FILE);
    }

    public DanhSachKetQua(String duongDanFile) {
        super(duongDanFile);
    }

    @Override
    protected String layMa(KetQuaThi doiTuong) {
        return doiTuong.getMa();
    }

    @Override
    protected KetQuaThi phanTichDong(String dong) {
        String[] t = tachTruong(dong, 8);
        kiemTraLoai(t[0], KetQuaThi.KY_HIEU_LOAI);
        double diem = Double.parseDouble(t[4]);
        int soCauDung = Integer.parseInt(t[5]);
        int tongSoCau = Integer.parseInt(t[6]);
        if (diem < 0 || diem > 10) {
            throw new IllegalArgumentException("điểm phải từ 0 đến 10");
        }
        if (soCauDung < 0 || tongSoCau < 0 || soCauDung > tongSoCau) {
            throw new IllegalArgumentException("số câu đúng/tổng số câu không hợp lệ");
        }
        return new KetQuaThi(t[1].toUpperCase(), t[2].toUpperCase(), t[3].toUpperCase(),
                diem, soCauDung, tongSoCau, NhapLieu.docNgay(t[7]));
    }

    @Override
    protected String chuyenThanhDong(KetQuaThi doiTuong) {
        return doiTuong.toFileString();
    }

    @Override
    protected void hienThiPhanTu(KetQuaThi doiTuong) {
        doiTuong.hienThi();
    }

    @Override
    protected void inTieuDe() {
        KetQuaThi.inTieuDe();
    }

    public List<KetQuaThi> timTheoThiSinh(String maThiSinh) {
        List<KetQuaThi> ketQua = new ArrayList<>();
        for (KetQuaThi kq : danhSach) {
            if (kq.getMaThiSinh().equalsIgnoreCase(maThiSinh.trim())) {
                ketQua.add(kq);
            }
        }
        return ketQua;
    }

    public List<KetQuaThi> timTheoDeThi(String maDeThi) {
        List<KetQuaThi> ketQua = new ArrayList<>();
        for (KetQuaThi kq : danhSach) {
            if (kq.getMaDeThi().equalsIgnoreCase(maDeThi.trim())) {
                ketQua.add(kq);
            }
        }
        return ketQua;
    }

    public List<KetQuaThi> timTheoKhoangDiem(double tu, double den) {
        List<KetQuaThi> ketQua = new ArrayList<>();
        for (KetQuaThi kq : danhSach) {
            if (kq.getDiem() >= tu && kq.getDiem() <= den) {
                ketQua.add(kq);
            }
        }
        return ketQua;
    }
}
```

### 4.23. `src/tracnghiem/BuoiThi.java`

```java
package tracnghiem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Tổ chức một buổi thi: chọn thí sinh, chọn đề, làm bài có tính giờ, chấm điểm, lưu KetQuaThi.
 * ĐA HÌNH: danh sách ArrayList<CauHoi> chứa đủ 3 loại câu hỏi; khi gọi
 * hienThiKhiThi(), laTraLoiHopLe(), kiemTraDapAn(), chamDiem() thì Java tự chọn
 * phiên bản của lớp con thực tế (dynamic binding).
 */
public class BuoiThi {

    private DanhSachNguoiDung dsNguoiDung;
    private DanhSachDeThi dsDeThi;
    private DanhSachCauHoi dsCauHoi;
    private DanhSachKetQua dsKetQua;

    /** Constructor không tham số: tự tạo các danh sách và đọc dữ liệu từ thư mục data/. */
    public BuoiThi() {
        this(new DanhSachNguoiDung(), new DanhSachDeThi(), new DanhSachCauHoi(), new DanhSachKetQua());
        dsNguoiDung.docLai();
        dsDeThi.docLai();
        dsCauHoi.docLai();
        dsKetQua.docLai();
    }

    /** Constructor có tham số: dùng chung các danh sách với chương trình quản lý. */
    public BuoiThi(DanhSachNguoiDung dsNguoiDung, DanhSachDeThi dsDeThi,
                   DanhSachCauHoi dsCauHoi, DanhSachKetQua dsKetQua) {
        this.dsNguoiDung = dsNguoiDung;
        this.dsDeThi = dsDeThi;
        this.dsCauHoi = dsCauHoi;
        this.dsKetQua = dsKetQua;
    }

    /** Luồng chính của buổi thi. */
    public void toChucThi() {
        System.out.println();
        System.out.println("----- LÀM BÀI THI -----");
        List<ThiSinh> cacThiSinh = dsNguoiDung.layDanhSachThiSinh();
        if (cacThiSinh.isEmpty()) {
            System.out.println("Chưa có thí sinh nào. Hãy thêm thí sinh trước.");
            return;
        }
        if (dsDeThi.soLuong() == 0) {
            System.out.println("Chưa có đề thi nào. Hãy tạo đề thi trước.");
            return;
        }
        ThiSinh thiSinh = chonThiSinh(cacThiSinh);
        if (thiSinh == null) {
            System.out.println("Đã hủy buổi thi.");
            return;
        }
        DeThi deThi = chonDeThi();
        if (deThi == null) {
            System.out.println("Đã hủy buổi thi.");
            return;
        }
        if (!NhapLieu.xacNhan("Thí sinh " + thiSinh.getHoTen() + " bắt đầu làm đề " + deThi.getMa() + "?")) {
            System.out.println("Đã hủy buổi thi.");
            return;
        }
        KetQuaThi ketQua = lamBai(thiSinh, deThi);
        if (ketQua != null) {
            if (dsKetQua.them(ketQua)) {
                System.out.println("Đã lưu kết quả " + ketQua.getMa() + " vào file " + dsKetQua.getDuongDanFile() + ".");
            } else {
                System.out.println("Lỗi: không lưu được kết quả (trùng mã " + ketQua.getMa() + ").");
            }
        }
    }

    private ThiSinh chonThiSinh(List<ThiSinh> cacThiSinh) {
        System.out.println("Danh sách thí sinh:");
        for (ThiSinh ts : cacThiSinh) {
            System.out.printf("  %-7s %-24s lớp %s%n", ts.getMa(), ts.getHoTen(), ts.getLop());
        }
        while (true) {
            String ma = NhapLieu.nhapChuoi("Nhập mã thí sinh (0 để hủy)").toUpperCase();
            if (ma.equals("0")) {
                return null;
            }
            NguoiDung nd = dsNguoiDung.timTheoMa(ma);
            if (nd instanceof ThiSinh) {
                return (ThiSinh) nd;
            }
            if (nd == null) {
                System.out.println("Lỗi: không tìm thấy người dùng có mã " + ma + ".");
            } else {
                System.out.println("Lỗi: " + ma + " là " + nd.getVaiTro().toLowerCase() + ", không phải thí sinh.");
            }
        }
    }

    private DeThi chonDeThi() {
        System.out.println("Danh sách đề thi:");
        for (DeThi de : dsDeThi.getTatCa()) {
            System.out.printf("  %-7s %-32s môn %-7s %3d phút, %d câu%n", de.getMa(), de.getTenDe(),
                    de.getMaMonHoc(), de.getThoiGianLamBai(), de.getSoCauHoi());
        }
        while (true) {
            String ma = NhapLieu.nhapChuoi("Nhập mã đề thi (0 để hủy)").toUpperCase();
            if (ma.equals("0")) {
                return null;
            }
            DeThi de = dsDeThi.timTheoMa(ma);
            if (de != null) {
                return de;
            }
            System.out.println("Lỗi: không tìm thấy đề thi có mã " + ma + ".");
        }
    }

    /** Làm bài và chấm điểm; trả về KetQuaThi hoặc null nếu đề không có câu hỏi hợp lệ. */
    private KetQuaThi lamBai(ThiSinh thiSinh, DeThi deThi) {
        // ĐA HÌNH: biến kiểu lớp cha CauHoi chứa đối tượng của các lớp con
        ArrayList<CauHoi> cacCauHoi = new ArrayList<>();
        for (String maCH : deThi.getDanhSachMaCauHoi()) {
            CauHoi ch = dsCauHoi.timTheoMa(maCH);
            if (ch == null) {
                System.out.println("Cảnh báo: câu hỏi " + maCH + " không còn tồn tại, bỏ qua.");
            } else {
                cacCauHoi.add(ch);
            }
        }
        if (cacCauHoi.isEmpty()) {
            System.out.println("Đề thi không có câu hỏi hợp lệ, không thể thi.");
            return null;
        }

        int soCau = cacCauHoi.size();
        double diemMoiCau = 10.0 / soCau;
        String[] cacTraLoi = new String[soCau];
        long hetGioLuc = System.currentTimeMillis() + deThi.getThoiGianLamBai() * 60_000L;

        System.out.println();
        System.out.println("Đề: " + deThi.getTenDe() + " | " + soCau + " câu | " + deThi.getThoiGianLamBai() + " phút");
        System.out.println("Nhấn Enter để bỏ qua câu. Hết giờ thì các câu chưa làm được 0 điểm.");

        for (int i = 0; i < soCau; i++) {
            CauHoi ch = cacCauHoi.get(i);
            long conLai = (hetGioLuc - System.currentTimeMillis()) / 1000;
            if (conLai <= 0) {
                System.out.println("HẾT GIỜ! Các câu còn lại không được tính.");
                break;
            }
            System.out.println();
            System.out.printf("(Còn %d phút %02d giây)%n", conLai / 60, conLai % 60);
            ch.hienThiKhiThi(i + 1); // ĐA HÌNH
            System.out.println("  Hướng dẫn: " + ch.getHuongDanTraLoi()); // ĐA HÌNH
            String traLoi;
            while (true) {
                traLoi = NhapLieu.nhapChuoiCoTheRong("  Trả lời");
                if (traLoi.isEmpty() || ch.laTraLoiHopLe(traLoi)) { // ĐA HÌNH
                    break;
                }
                System.out.println("  Lỗi: câu trả lời không hợp lệ. " + ch.getHuongDanTraLoi());
            }
            if (System.currentTimeMillis() > hetGioLuc) {
                System.out.println("HẾT GIỜ! Câu trả lời vừa nhập không được tính.");
                break;
            }
            cacTraLoi[i] = traLoi;
        }

        // ===== CHẤM ĐIỂM ĐA HÌNH =====
        double tongDiem = 0;
        int soCauDung = 0;
        System.out.println();
        System.out.println("===== KẾT QUẢ BÀI THI =====");
        System.out.printf("%-4s %-7s %-14s %-9s %-9s %-6s %s%n", "Câu", "Mã", "Loại", "Trả lời", "Đáp án", "Đúng?", "Điểm");
        for (int i = 0; i < soCau; i++) {
            CauHoi ch = cacCauHoi.get(i);
            String traLoi = cacTraLoi[i] == null ? "" : cacTraLoi[i];
            boolean dung = !traLoi.isEmpty() && ch.kiemTraDapAn(traLoi); // ĐA HÌNH
            IChamDiem boChamDiem = ch; // INTERFACE: biến kiểu interface trỏ tới đối tượng câu hỏi
            double diem = traLoi.isEmpty() ? 0.0 : boChamDiem.chamDiem(traLoi, diemMoiCau);
            if (dung) {
                soCauDung++;
            }
            tongDiem += diem;
            System.out.printf("%-4d %-7s %-14s %-9s %-9s %-6s %.2f%n", i + 1, ch.getMa(), ch.getLoai(),
                    traLoi.isEmpty() ? "(bỏ)" : traLoi.toUpperCase(), ch.getDapAnDungHienThi(),
                    dung ? "Đúng" : "Sai", diem);
        }
        tongDiem = Math.min(10.0, NhapLieu.lamTron2(tongDiem));
        System.out.println("---------------------------------------------");
        System.out.printf("Thí sinh: %s - %s%n", thiSinh.getMa(), thiSinh.getHoTen());
        System.out.printf("Số câu đúng: %d/%d | Điểm: %.2f | Xếp loại: %s%n",
                soCauDung, soCau, tongDiem, KetQuaThi.xepLoai(tongDiem));

        return new KetQuaThi(KetQuaThi.sinhMa(), thiSinh.getMa(), deThi.getMa(),
                tongDiem, soCauDung, soCau, LocalDate.now());
    }
}
```

### 4.24. `src/tracnghiem/QuanLyThiTracNghiem.java`

```java
package tracnghiem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

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
        System.out.printf("Đã đọc: %d môn học, %d người dùng, %d câu hỏi, %d đề thi, %d kết quả.%n",
                soMon, soNguoi, soCau, soDe, soKetQua);
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
            System.out.println("7. Đọc/ghi file dữ liệu");
            System.out.println("0. Thoát");
            int chon = NhapLieu.nhapSoNguyen("Chọn chức năng", 0, 7);
            if (chon == 0) {
                System.out.println("Dữ liệu đã được lưu sau mỗi thao tác. Tạm biệt!");
                return;
            }
            try {
                switch (chon) {
                    case 1 -> menuMonHoc();
                    case 2 -> menuNguoiDung();
                    case 3 -> menuCauHoi();
                    case 4 -> menuDeThi();
                    case 5 -> menuKetQua();
                    case 6 -> buoiThi.toChucThi();
                    case 7 -> menuFile();
                    default -> System.out.println("Chức năng không hợp lệ.");
                }
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
        return NhapLieu.nhapSoNguyen("Chọn", 0, 5);
    }

    // ======================= TIỆN ÍCH CHUNG =======================

    /** Nhập mã mới không trùng; Enter để dùng mã tự sinh. */
    private String nhapMaMoi(String maGoiY, DanhSach<?> danhSach) {
        while (true) {
            String ma = NhapLieu.nhapMaTuyChon("Mã (Enter để dùng mã tự sinh)", maGoiY);
            if (danhSach.timTheoMa(ma) == null) {
                return ma;
            }
            System.out.println("Lỗi: mã " + ma + " đã tồn tại, vui lòng nhập mã khác.");
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

    /** Chọn mã môn học đang tồn tại; macDinh != null thì Enter để giữ môn cũ. */
    private String chonMonHoc(String macDinh) {
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
            System.out.println("CẢNH BÁO: môn học đang được dùng bởi " + deDangDung.size()
                    + " đề thi và " + cauDangDung.size() + " câu hỏi.");
            for (DeThi de : deDangDung) {
                System.out.println("   - Đề " + de.getMa() + ": " + de.getTenDe());
            }
            System.out.println("Nếu xóa, các đề thi/câu hỏi này sẽ trỏ tới môn học không còn tồn tại.");
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
            System.out.println("Lưu ý: người dùng này có " + soKetQua + " kết quả thi, kết quả vẫn được giữ lại.");
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
        cu.hienThi(); // ĐA HÌNH
        System.out.println(GOI_Y_GIU_NGUYEN);
        String noiDung = NhapLieu.nhapChuoiTuyChon("Nội dung", cu.getNoiDung());
        String maMon = chonMonHoc(cu.getMaMonHoc());
        int doKho = NhapLieu.nhapSoNguyenTuyChon("Độ khó (1-3)", 1, 3, cu.getDoKho());
        CauHoi moi;
        if (NhapLieu.xacNhan("Có nhập lại lựa chọn/đáp án không?")) {
            moi = taoCauHoiTheoLoai(layLoaiCauHoi(cu), cu.getMa(), noiDung, maMon, doKho);
        } else {
            cu.setNoiDung(noiDung);
            cu.setMaMonHoc(maMon);
            cu.setDoKho(doKho);
            moi = cu;
        }
        thongBao(dsCauHoi.sua(cu.getMa(), moi), "sửa câu hỏi", cu.getMa());
    }

    private void xoaCauHoi() {
        CauHoi ch = dsCauHoi.timTheoMa(NhapLieu.nhapMa("Mã câu hỏi cần xóa"));
        if (ch == null) {
            System.out.println("Không tìm thấy câu hỏi.");
            return;
        }
        ch.hienThi();
        List<DeThi> deDangDung = dsDeThi.timDeChuaCauHoi(ch.getMa());
        if (!deDangDung.isEmpty()) {
            System.out.println("CẢNH BÁO: câu hỏi đang được dùng trong " + deDangDung.size() + " đề thi:");
            for (DeThi de : deDangDung) {
                System.out.println("   - Đề " + de.getMa() + ": " + de.getTenDe());
            }
            System.out.println("Nếu xóa, câu hỏi sẽ được gỡ khỏi các đề thi trên.");
        }
        if (NhapLieu.xacNhan("Bạn chắc chắn muốn xóa câu hỏi " + ch.getMa() + "?")) {
            boolean daXoa = dsCauHoi.xoa(ch.getMa());
            thongBao(daXoa, "xóa câu hỏi", ch.getMa());
            if (daXoa && !deDangDung.isEmpty()) {
                int soDe = dsDeThi.goCauHoiKhoiCacDe(ch.getMa());
                System.out.println("Đã gỡ câu hỏi khỏi " + soDe + " đề thi và ghi file đề thi.");
            }
        } else {
            System.out.println("Đã hủy xóa.");
        }
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
            System.out.println("Lưu ý: đề này có " + soKetQua + " kết quả thi, kết quả vẫn được giữ lại.");
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
        double diem = NhapLieu.nhapSoThuc("Điểm (0-10)", 0, 10);
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
        dsKetQua.hienThiDanhSach(List.of(cu));
        System.out.println(GOI_Y_GIU_NGUYEN);
        double diem = NhapLieu.nhapSoThucTuyChon("Điểm (0-10)", 0, 10, cu.getDiem());
        int soCauDung = NhapLieu.nhapSoNguyenTuyChon("Số câu đúng (0-" + cu.getTongSoCau() + ")",
                0, cu.getTongSoCau(), cu.getSoCauDung());
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

    // ======================= 7. ĐỌC / GHI FILE =======================

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
```

### 4.25. `src/tracnghiem/Main.java`

```java
package tracnghiem;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Điểm khởi động chương trình.
 */
public class Main {

    public static void main(String[] args) {
        // In tiếng Việt UTF-8 ra console (Windows cần chạy "chcp 65001" trước)
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
        QuanLyThiTracNghiem chuongTrinh = new QuanLyThiTracNghiem();
        chuongTrinh.chay();
    }
}
```

## 5. Dữ liệu mẫu trong `data/`

3 môn học, 5 thí sinh + 2 giáo viên, 15 câu hỏi (7 MOT, 4 NHIEU, 4 DUNGSAI), 2 đề thi, 3 kết quả thi. Lưu các file ở dạng **UTF-8**.

### `data/monhoc.txt`

```text
MONHOC|MH001|Lập trình hướng đối tượng|3
MONHOC|MH002|Cơ sở dữ liệu|3
MONHOC|MH003|Mạng máy tính|2
```

### `data/nguoidung.txt`

```text
THISINH|TS001|Nguyễn Văn An|12/03/2005|an.nguyen@sv.edu.vn|CNTT01
THISINH|TS002|Trần Thị Bình|25/07/2005|binh.tran@sv.edu.vn|CNTT01
THISINH|TS003|Lê Hoàng Cường|08/11/2004|cuong.le@sv.edu.vn|CNTT02
THISINH|TS004|Phạm Thu Dung|19/01/2005|dung.pham@sv.edu.vn|CNTT02
THISINH|TS005|Võ Minh Em|30/05/2004|em.vo@sv.edu.vn|KTPM01
GIAOVIEN|GV001|Đặng Quốc Huy|02/09/1985|huy.dang@gv.edu.vn|Lập trình Java
GIAOVIEN|GV002|Bùi Thị Lan|14/04/1988|lan.bui@gv.edu.vn|Cơ sở dữ liệu
```

### `data/cauhoi.txt`

```text
MOT|CH001|Từ khóa nào dùng để kế thừa một lớp trong Java?|MH001|1|B|implements|extends|inherits|super
MOT|CH002|Phạm vi truy cập nào chỉ cho phép truy cập bên trong chính lớp đó?|MH001|1|C|public|protected|private|default
MOT|CH003|Lớp nào là lớp cha của mọi lớp trong Java?|MH001|2|A|Object|Class|System|Main
NHIEU|CH004|Những phát biểu nào đúng về lớp trừu tượng (abstract class)?|MH001|2|AC|Có thể chứa phương thức trừu tượng|Có thể tạo đối tượng trực tiếp bằng new|Có thể có constructor|Phải khai báo bằng từ khóa interface
NHIEU|CH005|Những kỹ thuật nào thể hiện tính đa hình trong Java?|MH001|3|AB|Ghi đè phương thức (override)|Nạp chồng phương thức (overload)|Khai báo biến static|Khai báo hằng final
DUNGSAI|CH006|Một lớp trong Java có thể implement nhiều interface cùng lúc.|MH001|1|D
DUNGSAI|CH007|Phương thức static có thể truy cập trực tiếp thuộc tính không static của lớp.|MH001|2|S
MOT|CH008|Câu lệnh SQL nào dùng để truy vấn dữ liệu?|MH002|1|C|INSERT|UPDATE|SELECT|DELETE
MOT|CH009|Khóa chính (primary key) có đặc điểm nào?|MH002|2|B|Cho phép giá trị trùng lặp|Duy nhất và không được rỗng|Luôn là kiểu số|Chỉ xuất hiện trong bảng con
NHIEU|CH010|Những lệnh nào thuộc nhóm thao tác dữ liệu (DML)?|MH002|2|AC|INSERT|CREATE|UPDATE|DROP
DUNGSAI|CH011|Một bảng có thể có nhiều khóa ngoại.|MH002|1|D
MOT|CH012|Dạng chuẩn 1 (1NF) yêu cầu điều gì?|MH002|3|A|Mọi thuộc tính đều là nguyên tố|Không có phụ thuộc bắc cầu|Không có phụ thuộc bộ phận|Cơ sở dữ liệu có ít nhất hai bảng
MOT|CH013|Giao thức HTTP mặc định dùng cổng nào?|MH003|1|C|21|25|80|443
NHIEU|CH014|Những giao thức nào hoạt động ở tầng giao vận (Transport)?|MH003|2|AC|TCP|IP|UDP|HTTP
DUNGSAI|CH015|Địa chỉ IPv4 có độ dài 32 bit.|MH003|1|D
```

### `data/dethi.txt`

```text
DETHI|DT001|Kiểm tra giữa kỳ Lập trình OOP|MH001|15|CH001,CH002,CH003,CH004,CH005,CH006,CH007
DETHI|DT002|Kiểm tra nhanh Cơ sở dữ liệu|MH002|10|CH008,CH009,CH010,CH011,CH012
```

### `data/ketqua.txt`

```text
KETQUA|KQ001|TS001|DT001|8.57|6|7|20/09/2026
KETQUA|KQ002|TS002|DT001|5.71|4|7|20/09/2026
KETQUA|KQ003|TS003|DT002|10.0|5|5|25/09/2026
```

## 6. Lệnh biên dịch và chạy

Đứng ở thư mục gốc `QuanLyThiTracNghiem/` (thư mục chứa `src/` và `data/`), vì chương trình đọc file theo đường dẫn tương đối `data/...`.

**Windows (cmd / PowerShell):**
```bat
chcp 65001
javac -encoding UTF-8 -d out src\tracnghiem\*.java
java -Dfile.encoding=UTF-8 -cp out tracnghiem.Main
```

**macOS / Linux:**
```bash
javac -encoding UTF-8 -d out src/tracnghiem/*.java
java -cp out tracnghiem.Main
```

Ghi chú:
- `chcp 65001` chuyển console Windows sang UTF-8 để hiện tiếng Việt. Nếu console cũ vẫn lỗi khi gõ có dấu, nên chạy trong terminal của IntelliJ / VS Code.
- Muốn khôi phục dữ liệu mẫu: chép lại 5 file ở mục 5 vào `data/`.
- Xóa thư mục `data/` rồi chạy lại: chương trình tự tạo 5 file rỗng, không báo lỗi.

## 7. Kịch bản demo

Mỗi bước ghi theo dạng *menu chính → menu con → dữ liệu nhập*. Dòng chữ nghiêng là điều cần chỉ cho giảng viên thấy.

**Bước 0 – Khởi động.** Chạy chương trình.
*Dòng đầu tiên "Đã đọc: 3 môn học, 7 người dùng, 15 câu hỏi, 2 đề thi, 3 kết quả." chứng minh tự đọc file khi khởi động.*

**Bước 1 – Xem**
1. `1` (Môn học) → `1` (Xem) → bảng 3 môn → `0`.
2. `2` (Người dùng) → `1` → bảng 7 người, cột Vai trò hiện "Thí sinh"/"Giáo viên".
   *Một danh sách `ArrayList<NguoiDung>` in ra hai kiểu khác nhau nhờ đa hình `hienThi()`.* → `0`.
3. `3` (Câu hỏi) → `1` → 15 câu, mỗi loại in đáp án theo cách riêng → `0`.

**Bước 2 – Thêm (kèm nhập sai)**
1. `2` → `2` → `1` (Thí sinh) → Enter (nhận mã tự sinh **TS006**) → `Hồ Văn Mới` → nhập sai `31/02/2005` (báo lỗi) → `15/08/2005` → nhập sai `abc@` (báo lỗi) → `moi.ho@sv.edu.vn` → `CNTT03`.
   *Mã TS006 do phương thức static `ThiSinh.sinhMa()` sinh ra.*
2. Thử thêm lần nữa và gõ mã `TS001` → báo "mã đã tồn tại" → chứng minh không trùng mã. → `0`.
3. `3` → `2` → `1` (Một đáp án) → Enter (**CH016**) → `Từ khóa nào dùng để khai báo hằng số trong Java?` → `MH001` → `1` → `4` → `static`, `final`, `const`, `var` → `B` → `0`.

**Bước 3 – Sửa**
`2` → `3` → `TS006` → Enter, Enter, Enter (giữ nguyên họ tên, ngày sinh, email) → `KTPM02` → "Đã sửa ... và ghi file". → `0`.

**Bước 4 – Xóa (kèm cảnh báo)**
1. `1` → `4` → `MH001` → hiện **CẢNH BÁO** môn đang dùng bởi 1 đề thi và 8 câu hỏi → `n` → "Đã hủy xóa". → `0`.
2. `3` → `4` → `CH002` → **CẢNH BÁO** câu hỏi nằm trong đề DT001 → `y` → câu hỏi bị xóa và tự gỡ khỏi DT001. → `0`.

**Bước 5 – Tìm kiếm**
1. `2` → `5` → `2` (theo họ tên) → gõ `nguyen` (không dấu) → ra "Nguyễn Văn An". → `0`.
2. `3` → `5` → `4` (theo loại) → `3` (Đúng/Sai) → ra 4 câu Đúng/Sai. → `0`.
3. (Tùy chọn) `5` → `5` → `4` (khoảng điểm) → `8` → `10`.

**Bước 6 – Làm bài thi**
`6` → gõ `GV001` (báo "là giáo viên, không phải thí sinh") → `TS006` → `DT002` → `y` → lần lượt trả lời `C`, `B`, `A,C`, `D`, `A`.
Có thể thử nhập sai (ví dụ `X` ở câu Đúng/Sai) để thấy báo lỗi, hoặc chỉ chọn `A` ở câu nhiều đáp án để thấy **điểm từng phần**.
*Bảng kết quả in Loại / Trả lời / Đáp án / Điểm từng câu; điểm 10.00 và "Đã lưu kết quả KQ004".*

**Bước 7 – Đọc/ghi file**
1. `5` → `1` → thấy dòng **KQ004** vừa thi. → `0`.
2. Mở `data/ketqua.txt` bằng Notepad → thấy dòng `KETQUA|KQ004|TS006|DT002|10.0|5|5|...` (đã ghi ngay sau khi thi).
3. Sửa tay một điểm trong file, lưu lại (UTF-8). Về chương trình: `7` → `3` (Đọc lại toàn bộ) → `5` → `1` → thấy điểm đã đổi.
4. Thêm một dòng rác (ví dụ `ABC`) vào file rồi đọc lại → chương trình in "Cảnh báo: bỏ qua dòng ..." và vẫn chạy bình thường.
5. `7` → `1` xem đường dẫn và số bản ghi của 5 file; `0` → `0` thoát.

## 8. 15 câu hỏi vấn đáp thường gặp

**1. Đa hình nằm ở đâu trong đồ án?**
Rõ nhất ở `BuoiThi.lamBai()`: `ArrayList<CauHoi> cacCauHoi` chứa lẫn `CauHoiMotDapAn`, `CauHoiNhieuDapAn`, `CauHoiDungSai`. Vòng lặp gọi `ch.hienThiKhiThi()`, `ch.laTraLoiHopLe()`, `ch.kiemTraDapAn()`, `ch.chamDiem()`; Java chọn phiên bản theo đối tượng thực tế lúc chạy (dynamic binding). Tương tự `DanhSachNguoiDung` gọi `nd.hienThi()` cho cả `ThiSinh` lẫn `GiaoVien`.

**2. Vì sao `CauHoi` là abstract?**
Một "câu hỏi chung chung" không biết đáp án dạng gì nên không thể cài đặt `kiemTraDapAn()`. Đánh dấu abstract để cấm `new CauHoi()` và buộc mọi lớp con phải tự cài đặt các phương thức trừu tượng.

**3. Tại sao có thêm lớp `CauHoiCoLuaChon`?**
Câu một đáp án và nhiều đáp án đều có danh sách `LuaChon` và cách in giống nhau. Gom vào lớp cha trung gian để không lặp code; `CauHoiDungSai` không có lựa chọn nên kế thừa thẳng `CauHoi`.

**4. Abstract class khác interface thế nào? Đồ án dùng mỗi loại ở đâu?**
Abstract class có thuộc tính, constructor và code dùng chung, lớp con chỉ được `extends` một lớp. Interface là hợp đồng "biết làm gì", một lớp `implements` được nhiều interface. Ví dụ: `DanhSach<T>` là abstract class (có `ArrayList`, đường dẫn file, code CRUD chung) và cùng lúc `implements IQuanLy<T>, IFileIO`.

**5. Interface `IChamDiem` được dùng thật ở đâu?**
`CauHoi implements IChamDiem` cài đặt `chamDiem()` mặc định (đúng được trọn điểm). `CauHoiNhieuDapAn` override lại để cho điểm từng phần. Trong `BuoiThi.lamBai()` có dòng `IChamDiem boChamDiem = ch; boChamDiem.chamDiem(traLoi, diemMoiCau);`.

**6. Thuộc tính static dùng để làm gì?**
`boDem` trong `ThiSinh`, `CauHoi`, `DeThi`... là bộ đếm dùng chung cho mọi đối tượng cùng lớp, để `sinhMa()` trả về mã kế tiếp (TS006, CH016). `NhapLieu.sc` là Scanner static dùng chung cho cả chương trình.

**7. Vì sao phương thức trong `NhapLieu` đều là static?**
Chúng không phụ thuộc trạng thái của một đối tượng cụ thể nên gọi thẳng `NhapLieu.nhapSoNguyen(...)` mà không cần tạo đối tượng. Dùng chung một Scanner tránh lỗi mất dữ liệu đệm khi tạo nhiều Scanner trên `System.in`.

**8. Sinh mã tự động có bị trùng sau khi tắt mở lại chương trình không?**
Không. Constructor có tham số của mỗi thực thể gọi `capNhatBoDem(ma)`; khi `docFile()` tạo lại đối tượng từ file, bộ đếm được cập nhật bằng số lớn nhất đã có. Ngoài ra `DanhSach.them()` vẫn kiểm tra trùng mã lần cuối.

**9. Làm sao biết một dòng trong file là lớp con nào?**
Trường đầu dòng là ký hiệu loại. `DanhSachCauHoi.phanTichDong()` đọc `MOT` / `NHIEU` / `DUNGSAI` để `new` đúng lớp con; `DanhSachNguoiDung.phanTichDong()` đọc `THISINH` / `GIAOVIEN`. Khi ghi, mỗi đối tượng tự trả về dòng của mình qua `toFileString()` (đa hình).

**10. File dữ liệu bị sai hoặc chưa tồn tại thì sao?**
`DanhSach.docFile()` tự tạo thư mục và file rỗng nếu chưa có. Mỗi dòng được bọc `try/catch`; dòng sai (thiếu trường, sai số, sai ngày, trùng mã) bị bỏ qua kèm "Cảnh báo: bỏ qua dòng N", các dòng còn lại vẫn được đọc. Lỗi `IOException` được bắt và báo bằng tiếng Việt.

**11. Khi nào dữ liệu được ghi xuống file?**
Ngay sau mỗi thao tác thêm/sửa/xóa: `DanhSach.them()`, `sua()`, `xoa()` đều gọi `luu()` → `ghiFile()`. Kết quả thi được ghi khi `BuoiThi` gọi `dsKetQua.them(ketQua)`.

**12. Chương trình chống nhập sai thế nào?**
Mọi lần nhập đi qua `NhapLieu`: bắt `NumberFormatException`, `DateTimeParseException`, kiểm tra khoảng giá trị, email bằng regex, ngày dùng `ResolverStyle.STRICT` (chặn 31/02), cấm ký tự `|`. Sai thì báo lỗi tiếng Việt và cho nhập lại. Menu chính còn có `try/catch` cuối cùng để không bao giờ dừng đột ngột.

**13. Tại sao `DanhSach` dùng generic `<T>`?**
Để viết code thêm/sửa/xóa/tìm/đọc/ghi một lần cho mọi loại danh sách. Phần khác nhau giữa các loại được tách thành phương thức trừu tượng (`layMa`, `phanTichDong`, `chuyenThanhDong`, `hienThiPhanTu`), lớp con chỉ cần cài đặt các phần đó.

**14. Override và overload khác nhau ra sao? Chỉ ví dụ.**
Override: lớp con viết lại phương thức cùng chữ ký của lớp cha, ví dụ `CauHoiNhieuDapAn.chamDiem()` ghi đè `CauHoi.chamDiem()`; được quyết định lúc chạy. Overload: cùng tên khác tham số trong một lớp, ví dụ hai constructor `ThiSinh()` và `ThiSinh(ma, hoTen, ngaySinh, email, lop)`; được quyết định lúc biên dịch.

**15. Tính đóng gói thể hiện ở đâu? Vì sao getter danh sách trả về bản sao?**
Thuộc tính khai báo `private`/`protected`, truy cập qua getter/setter. `DeThi.getDanhSachMaCauHoi()` trả về bản sao và `DanhSach.getTatCa()` trả về danh sách chỉ đọc, để bên ngoài không sửa trực tiếp làm lệch dữ liệu với file; mọi thay đổi phải qua `them/sua/xoa` (có ghi file).
