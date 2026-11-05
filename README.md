# ĐỒ ÁN: ỨNG DỤNG QUẢN LÝ THI TRẮC NGHIỆM (Java 17, console)

> Đã biên dịch bằng `javac --release 17` và chạy thử toàn bộ kịch bản demo, kể cả các trường hợp nhập sai, file dữ liệu lỗi và thư mục `data/` chưa tồn tại.
> Tổng cộng **23 lớp** (không tính 3 interface và `Main`), khoảng 3.983 dòng mã.
>
> **Bổ sung:** chức năng **Thống kê - báo cáo** (menu 7, lớp `ThongKe` + `ThongKeDiem`) và bộ dữ liệu mẫu **≥ 50 bản ghi cho mỗi file** (50 môn học, 60 người dùng = 50 thí sinh + 10 giáo viên, 80 câu hỏi, 50 đề thi, 150 kết quả thi).

## 1. Hướng dẫn chạy chương trình

### 1.1. Chuẩn bị

- Cài **JDK 17 trở lên** (ví dụ Eclipse Temurin 17 hoặc Oracle JDK 17/21).
- Kiểm tra đã cài đúng chưa: mở PowerShell gõ hai lệnh sau, cả hai đều phải ra số phiên bản (17 trở lên):

```powershell
java -version
javac -version
```

> **Quan trọng:** luôn chạy chương trình khi đang đứng ở **thư mục gốc của đồ án** (thư mục có chứa `src/`, `data/`, `README.md`). Chương trình đọc dữ liệu theo đường dẫn `data/...`, nếu đứng sai thư mục sẽ thấy báo "Đã đọc: 0 môn học, 0 người dùng..." và dữ liệu trống.

### 1.2. Chạy bằng PowerShell trên Windows (khuyên dùng)

**Bước 1 – Mở PowerShell tại thư mục đồ án.** Cách nhanh: mở thư mục đồ án trong File Explorer, bấm vào thanh địa chỉ, gõ `powershell` rồi Enter. Hoặc dùng lệnh `cd` (đường dẫn có dấu cách thì phải để trong ngoặc kép):

```powershell
cd "C:\Users\Admin\OneDrive - Saigon University\Documents\QuanLyThiTracNghiem"
```

**Bước 2 – Bật tiếng Việt (UTF-8) cho cửa sổ PowerShell.** Mỗi lần mở cửa sổ mới đều phải chạy lại:

```powershell
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
[Console]::InputEncoding  = [System.Text.Encoding]::UTF8
```

**Bước 3 – Biên dịch** (chỉ cần làm lại khi có sửa code). Các file `.class` được tạo trong thư mục `out/`:

```powershell
javac -encoding UTF-8 -d out src\tracnghiem\*.java
```

Biên dịch thành công thì không in ra gì (có thể có 1 dòng `warning`, không sao).

**Bước 4 – Chạy chương trình:**

```powershell
java "-Dfile.encoding=UTF-8" -cp out tracnghiem.Main
```

Màn hình hiện dòng `Đã đọc: 50 môn học, 60 người dùng, 80 câu hỏi, 50 đề thi, 150 kết quả.` và menu chính là chạy đúng. Gõ số tương ứng rồi Enter để chọn chức năng, gõ `0` để thoát.

### 1.3. Chạy bằng Command Prompt (cmd)

```bat
cd /d "C:\Users\Admin\OneDrive - Saigon University\Documents\QuanLyThiTracNghiem"
chcp 65001
javac -encoding UTF-8 -d out src\tracnghiem\*.java
java -Dfile.encoding=UTF-8 -cp out tracnghiem.Main
```

`chcp 65001` chuyển cmd sang UTF-8 để hiện tiếng Việt.

### 1.4. Chạy bằng VS Code hoặc IntelliJ IDEA

- **VS Code:** cài extension *Extension Pack for Java* → `File > Open Folder...` chọn **thư mục gốc của đồ án** → mở `src/tracnghiem/Main.java` → bấm **Run** phía trên hàm `main`. Nếu gõ tiếng Việt có dấu bị lỗi, mở Terminal của VS Code và chạy theo mục 1.2.
- **IntelliJ IDEA:** `File > Open...` chọn thư mục đồ án → chuột phải thư mục `src` → *Mark Directory as > Sources Root* → mở `Main.java` bấm nút ▶. Nếu báo đọc được 0 bản ghi: vào *Run > Edit Configurations...* và đặt **Working directory** là thư mục gốc của đồ án.

### 1.5. macOS / Linux

```bash
cd đường/dẫn/tới/QuanLyThiTracNghiem
javac -encoding UTF-8 -d out src/tracnghiem/*.java
java -cp out tracnghiem.Main
```

### 1.6. Lỗi thường gặp

| Hiện tượng | Nguyên nhân | Cách sửa |
|---|---|---|
| `'javac' is not recognized...` / `javac : The term 'javac' is not recognized` | Chưa cài JDK hoặc chưa thêm vào PATH | Cài JDK 17+, khi cài chọn *Add to PATH* (hoặc thêm `...\jdk-17\bin` vào biến môi trường Path), rồi mở lại PowerShell |
| Chữ tiếng Việt hiện thành `?` hoặc ký tự lạ | Console chưa ở chế độ UTF-8 | Chạy lại Bước 2 (PowerShell) hoặc `chcp 65001` (cmd) trước khi chạy |
| `Đã đọc: 0 môn học, 0 người dùng, ...` | Đang đứng sai thư mục | `cd` về thư mục gốc đồ án (có `data/`) rồi chạy lại |
| `Error: Could not find or load main class tracnghiem.Main` | Chưa biên dịch hoặc sai `-cp` | Chạy lại Bước 3, kiểm tra có thư mục `out\tracnghiem\` |
| `UnsupportedClassVersionError` | Lệnh `java` cũ hơn `javac` (máy có nhiều bản Java) | Kiểm tra `java -version` và `javac -version` phải cùng bản 17+, hoặc biên dịch lại |
| `Cảnh báo: bỏ qua dòng N trong data/...` | Dòng trong file dữ liệu bị sai định dạng | Sửa lại dòng đó (lưu file dạng UTF-8), các dòng khác vẫn đọc bình thường |

### 1.7. Khôi phục dữ liệu mẫu

Mọi thao tác thêm/sửa/xóa/làm bài thi đều **ghi thẳng vào file** trong `data/`. Nên **sao lưu thư mục `data/` trước khi demo**; muốn quay về dữ liệu ban đầu thì chép đè lại thư mục `data/` từ bản sao lưu (hoặc `git checkout -- data` nếu dùng git). Nếu xóa hẳn thư mục `data/`, chương trình tự tạo 5 file rỗng, không báo lỗi.

## 2. Cây thư mục project

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
│       ├── ThongKeDiem.java          (21) bộ cộng dồn số liệu điểm
│       ├── ThongKe.java              (22) thống kê - báo cáo
│       ├── QuanLyThiTracNghiem.java  (23)
│       └── Main.java                 (không tính)
└── data/
    ├── monhoc.txt
    ├── nguoidung.txt
    ├── cauhoi.txt
    ├── dethi.txt
    ├── ketqua.txt
    └── baocao_thongke.txt        (tạo ra khi chọn 7 → 10 Xuất báo cáo)
```
