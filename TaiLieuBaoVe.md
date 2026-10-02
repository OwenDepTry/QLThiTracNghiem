# Tài liệu giải trình đồ án: Quản lý Thi Trắc Nghiệm

## 1. Sơ đồ kiến trúc (mô tả bằng chữ)

```
                    <<interface>>              <<interface>>
                     IHienThi                   ICoTheCham
                        ▲                            ▲
                        │ implements                 │ implements
                 NguoiDung (abstract)            CauHoi (abstract)
                 - soLuongNguoiDung: static       - soLuongCauHoi: static
                 + getVaiTro(): abstract          + loaiCauHoi(): abstract
                    ▲            ▲                     ▲              ▲
                    │            │                     │              │
               SinhVien      GiaoVien          CauHoiMotDapAn   CauHoiNhieuDapAn

   DeThi (1) o----- chứa nhiều -----o (n) CauHoi
   BaiLamThi (1) o----- chứa nhiều -----o (n) CauTraLoi
   QuanLyHeThong  ---- dùng ----> DanhSachDoiTuong<T>  (cho mọi loại: SinhVien, GiaoVien,
                                   MonHoc, CauHoi, DeThi, KetQuaThi)
   QuanLyHeThong  ---- dùng ----> FileManager (đọc/ghi text), ThongKe (thống kê)
   KetQuaThi ------ sinh ra bởi -- QuanLyHeThong.chamThiTuDong(BaiLamThi, DeThi)
```

## 2. Vì sao thiết kế như vậy?

- **`NguoiDung` là lớp trừu tượng** vì trong thực tế "người dùng chung chung" không
  tồn tại độc lập — chỉ có Sinh viên hoặc Giáo viên cụ thể. Đưa các thuộc tính chung
  (họ tên, ngày sinh, địa chỉ, SĐT) lên lớp cha giúp tránh lặp code, và phương thức
  `getVaiTro()` để trừu tượng buộc mỗi lớp con phải tự định nghĩa vai trò của mình.

- **`CauHoi` là lớp trừu tượng** tương tự: một "câu hỏi chung chung" không xác định
  được cách chấm điểm. Chỉ khi biết nó là *một đáp án* hay *nhiều đáp án* mới chấm
  được. Vì vậy `chamDiem()` (đến từ interface `ICoTheCham`) không được cài đặt ở
  `CauHoi` mà để hai lớp con `CauHoiMotDapAn` và `CauHoiNhieuDapAn` tự cài đặt
  theo cách riêng — đây chính là **đa hình**.

- **Hai interface `IHienThi` và `ICoTheCham`** tách riêng "khả năng hiển thị" và
  "khả năng chấm điểm" khỏi cây kế thừa, cho phép nhiều lớp không liên quan huyết
  thống vẫn có thể cùng cam kết thực hiện một hành vi chung (ví dụ sau này có thể
  thêm lớp khác cũng "có thể chấm điểm" mà không cần kế thừa `CauHoi`).

- **`DanhSachDoiTuong<T>`** là lớp generic dùng chung cho mọi loại danh sách
  (sinh viên, câu hỏi, đề thi...) thay vì viết 6 lớp danh sách riêng lẻ — vẫn thỏa
  yêu cầu "lớp mảng các đối tượng" nhưng tránh lặp code, thể hiện khả năng dùng
  Generic Programming.

- **`QuanLyHeThong`** đóng vai trò "nhạc trưởng": nắm giữ 6 `DanhSachDoiTuong`
  (một cho mỗi loại thực thể), gọi `FileManager` để lưu/đọc, gọi `ThongKe` để
  tổng hợp số liệu, và đặc biệt là hàm `chamThiTuDong()` — nơi minh họa đa hình rõ nhất.

## 3. Đa hình được thể hiện ở đâu? (câu hỏi hay gặp nhất)

Trong `QuanLyHeThong.chamThiTuDong()`:

```java
for (CauTraLoi tl : baiLam.getCauTraLoiList()) {
    CauHoi c = mapCauHoi.get(tl.getMaCauHoi());   // c khai báo kiểu CauHoi (lớp cha)
    tongDiemDat += c.chamDiem(tl.getLuaChon());   // gọi qua tham chiếu lớp cha
}
```

Biến `c` có **kiểu khai báo là `CauHoi`** (lớp cha, trừu tượng), nhưng **đối tượng
thực sự** được gán vào có thể là `CauHoiMotDapAn` hoặc `CauHoiNhieuDapAn`. Khi gọi
`c.chamDiem(...)`, JVM sẽ tra bảng phương thức ảo (virtual method table) tại thời
điểm chạy (runtime) để quyết định gọi đúng phiên bản `chamDiem()` của lớp con nào
— đây là **đa hình động (runtime polymorphism)**, không phải đa hình biên dịch
(overloading).

Tương tự với `hienThi()` (NguoiDung/SinhVien/GiaoVien) và `loaiCauHoi()`
(CauHoi/CauHoiMotDapAn/CauHoiNhieuDapAn) khi duyệt `List<NguoiDung>` hoặc
`List<CauHoi>` trong menu "Xem tất cả".

## 4. Cách chấm điểm chi tiết

- **Câu hỏi 1 đáp án** (`CauHoiMotDapAn`): sinh viên phải chọn đúng **duy nhất 1**
  chỉ số; nếu chọn đúng chỉ số của đáp án đánh dấu `dapAnDung = true` → 1.0 điểm,
  ngược lại 0.0 điểm.
- **Câu hỏi nhiều đáp án** (`CauHoiNhieuDapAn`): nếu tập lựa chọn của sinh viên
  trùng khớp **chính xác** tập đáp án đúng → 1.0 điểm (điểm tối đa). Nếu không khớp
  hoàn toàn, tính điểm từng phần theo công thức:
  `điểm = max(0, (số lựa chọn đúng - số lựa chọn sai) / tổng số đáp án đúng)`
  → khuyến khích sinh viên chọn đúng, đồng thời phạt nhẹ khi chọn sai/thừa.
- Điểm từng câu (0–1) được cộng dồn, quy đổi về thang 10:
  `điểm bài thi = (tổng điểm đạt / tổng số câu) × 10`, làm tròn 2 chữ số thập phân.
- `KetQuaThi.xepLoaiTheoDiem()` (static) áp mốc: ≥8.5 Giỏi, ≥7.0 Khá, ≥5.0
  Trung bình, còn lại Yếu.

## 5. Cấu trúc file dữ liệu (text, UTF-8, phân tách bằng `|`)

| File | Định dạng mỗi dòng |
|---|---|
| `data/sinhvien.txt` | `maND\|hoTen\|ngaySinh\|diaChi\|sdt\|mssv\|maLop\|email` |
| `data/giaovien.txt` | `maND\|hoTen\|ngaySinh\|diaChi\|sdt\|chuyenMon\|hocVi` |
| `data/monhoc.txt` | `maMon\|tenMon\|soTinChi` |
| `data/cauhoi.txt` | `maCauHoi\|loai\|maMon\|doKho\|noiDung\|noiDung1~0/1;noiDung2~0/1;...` |
| `data/dethi.txt` | `maDeThi\|tenDeThi\|maMon\|thoiGian\|maCH1,maCH2,...` |
| `data/ketqua.txt` | `maBaiLam\|mssv\|maDeThi\|diemSo\|xepLoai` |

`CauHoi.fromFileLine()` đọc trường `loai` để quyết định khởi tạo lại đúng lớp con
(`CauHoiMotDapAn` hay `CauHoiNhieuDapAn`) — đây cũng là một dạng đa hình khi tái
tạo đối tượng.

## 6. Bộ câu hỏi – trả lời chuẩn bị bảo vệ

**Q: Tại sao dùng `abstract class` cho `NguoiDung` mà không dùng `interface`?**
A: Vì `SinhVien` và `GiaoVien` chia sẻ *cả* thuộc tính (dữ liệu) *lẫn* hành vi
mặc định (`hienThi()` cơ bản) — interface (trước Java 8/9) không lưu được thuộc
tính và không có trạng thái. Abstract class phù hợp hơn khi các lớp con có quan
hệ "is-a" chặt và cần chia sẻ code triển khai sẵn.

**Q: Tại sao lại dùng thêm `interface` (`IHienThi`, `ICoTheCham`) trong khi đã có
abstract class?**
A: Interface dùng để định nghĩa **hợp đồng hành vi** độc lập với cây kế thừa. Ví
dụ `ICoTheCham` có thể được cài đặt bởi bất kỳ lớp nào cần "chấm điểm", không nhất
thiết phải là con của `CauHoi`. Nó cũng cho phép một lớp trong tương lai implement
nhiều interface cùng lúc (Java không hỗ trợ đa kế thừa lớp nhưng hỗ trợ đa kế thừa
interface).

**Q: Đa hình đem lại lợi ích gì trong đồ án này?**
A: Cho phép `QuanLyHeThong` chấm điểm cho **bất kỳ loại câu hỏi nào** mà không cần
biết trước đó là loại gì (không cần `if (c instanceof CauHoiMotDapAn) ... else if
...`). Khi cần thêm loại câu hỏi mới (ví dụ câu hỏi điền khuyết), chỉ cần tạo thêm
một lớp con kế thừa `CauHoi` và cài đặt `chamDiem()` — không phải sửa code chấm
điểm hiện có (tuân thủ nguyên lý Open/Closed).

**Q: Thuộc tính static dùng để làm gì, có rủi ro gì không?**
A: Dùng để lưu dữ liệu **dùng chung cho cả lớp**, không thuộc riêng đối tượng nào
— ví dụ tổng số người dùng, bộ đếm sinh mã tự động. Rủi ro: nếu chạy đa luồng
(multi-thread) mà không đồng bộ hóa, việc tăng biến static có thể bị lỗi race
condition; đồ án hiện chạy đơn luồng console nên không gặp vấn đề này.

**Q: Vì sao chọn lưu dữ liệu dạng text phân tách bằng `|` thay vì CSV chuẩn hay
JSON/XML?**
A: Đơn giản, không cần thư viện ngoài, dễ đọc/ghi bằng `java.nio.file.Files`, và
đề bài yêu cầu cụ thể "đọc/ghi dữ liệu lên file (text)". Dùng `|` thay vì `,` để
tránh xung đột với dấu phẩy có thể xuất hiện trong nội dung câu hỏi tiếng Việt.

**Q: Điều gì xảy ra nếu đề thi bị xóa nhưng câu hỏi trong đề vẫn còn trong danh
sách câu hỏi chung?**
A: `DeThi` chỉ giữ **tham chiếu** (reference) tới đối tượng `CauHoi` đã có trong
`dsCauHoi`, không sao chép dữ liệu. Khi xóa đề thi, chỉ đối tượng `DeThi` bị xóa
khỏi `dsDeThi`; các `CauHoi` vẫn còn nguyên trong `dsCauHoi` để dùng cho đề thi
khác — tránh trùng lặp dữ liệu.

**Q: Vì sao `DanhSachDoiTuong` viết dạng generic `<T>` mà không viết riêng
`DanhSachSinhVien`, `DanhSachCauHoi`,...?**
A: Để tái sử dụng code — logic thêm/xóa/sửa/tìm kiếm hoàn toàn giống nhau giữa
các loại đối tượng, chỉ khác kiểu dữ liệu. Generic giúp viết một lần, dùng cho
mọi kiểu, đúng tinh thần lớp "mảng các đối tượng" mà đề bài yêu cầu, đồng thời
an toàn kiểu dữ liệu tại thời điểm biên dịch (compile-time type safety).

**Q: Nếu muốn mở rộng thêm chức năng đăng nhập/phân quyền thì làm thế nào?**
A: Có thể thêm thuộc tính `matKhau`, `vaiTro` (enum) vào `NguoiDung`, viết thêm
lớp `QuanLyDangNhap` kiểm tra thông tin trước khi vào menu tương ứng (sinh viên
chỉ thấy menu làm bài thi, giáo viên/quản trị thấy đủ menu quản lý) — không ảnh
hưởng tới các lớp hiện có nhờ kiến trúc đã tách lớp rõ ràng.

**Q: Hướng phát triển tiếp theo của đồ án?**
A: (1) Chuyển sang giao diện đồ họa (Java Swing/JavaFX) hoặc web; (2) Lưu dữ liệu
bằng cơ sở dữ liệu (JDBC + MySQL) thay vì file text để truy vấn nhanh và an toàn
hơn; (3) Thêm chức năng thi có giới hạn thời gian thực (đếm ngược); (4) Xuất kết
quả thi ra file Excel/PDF; (5) Thêm cơ chế đăng nhập, phân quyền Sinh viên/Giáo
viên/Quản trị viên.
