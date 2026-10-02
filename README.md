<<<<<<< HEAD
# 📚 Quản lý thi trắc nghiệm

> Đồ án môn **Lập trình hướng đối tượng – Java**

Một chương trình quản lý và thi trắc nghiệm chạy trên Console.
Đồ án được xây dựng để áp dụng các kiến thức OOP đã học vào một bài toán tương đối thực tế: quản lý người dùng, môn học, câu hỏi, đề thi và kết quả.

---

## 👥 Thành viên

|    MSSV    | Họ và tên            |   Vai trò   |
| :--------: | -------------------- | :---------: |
| 3124410066 | **Phan Tiến Đạt**    | Nhóm trưởng |
| 3124410067 | **Nguyễn Hải Đăng**  |  Thành viên |
| 3122410057 | **Nguyễn Khánh Duy** |  Thành viên |

---

## 🔎 Giới thiệu

Chương trình mô phỏng một hệ thống thi trắc nghiệm đơn giản, trong đó có hai nhóm người dùng chính là **sinh viên** và **giáo viên**.

Giáo viên có thể quản lý môn học, câu hỏi và đề thi. Sinh viên có thể chọn đề, làm bài và xem kết quả sau khi nộp bài.

Dữ liệu của chương trình được lưu bằng các file `.txt`, vì vậy có thể giữ lại dữ liệu sau khi thoát chương trình.

---

## ⚙️ Chức năng chính

### 👤 Quản lý người dùng

* Thêm, sửa, xóa sinh viên
* Thêm, sửa, xóa giáo viên
* Tìm kiếm và xem danh sách
* Quản lý thông tin lớp

### 📖 Quản lý môn học

* Thêm môn học
* Sửa thông tin môn học
* Xóa môn học
* Tìm kiếm môn học
* Xem danh sách môn học

### ❓ Quản lý câu hỏi

Chương trình hỗ trợ:

* Câu hỏi **một đáp án đúng**
* Câu hỏi **nhiều đáp án đúng**

Có thể thêm, sửa, xóa và tìm kiếm câu hỏi.

### 📝 Quản lý đề thi

* Tạo đề thi
* Thêm câu hỏi vào đề
* Sửa thông tin đề
* Xóa đề
* Xem danh sách đề thi

### 🧑‍💻 Làm bài thi

Sinh viên có thể chọn một đề thi và thực hiện bài làm trực tiếp trên Console.

Sau khi nộp bài, chương trình sẽ:

```text
→ Kiểm tra câu trả lời
→ Tính điểm
→ Lưu kết quả
→ Hiển thị kết quả
```

### 📊 Thống kê

Có một số chức năng thống kê kết quả thi như:

* Xem điểm
* Tính điểm trung bình
* Xem kết quả của sinh viên
* Thống kê thành tích

---

## 🧩 Một số kiến thức OOP được sử dụng

Đây là phần chính của đồ án nên nhóm có sử dụng khá nhiều kiến thức đã học trong môn OOP.

**Đóng gói (Encapsulation)**

Sử dụng `private`, getter và setter để quản lý thuộc tính của đối tượng.

**Kế thừa (Inheritance)**

Một số lớp có quan hệ kế thừa với nhau, ví dụ:

```text
NguoiDung
├── SinhVien
└── GiaoVien
```

và:

```text
CauHoi
├── CauHoiMotDapAn
└── CauHoiNhieuDapAn
```

**Đa hình (Polymorphism)**

Các đối tượng thuộc những lớp con khác nhau có thể được xử lý thông qua kiểu của lớp cha.

**Abstract Class**

Một số lớp được xây dựng dưới dạng lớp trừu tượng để làm cơ sở cho các lớp con.

**Interface**

Sử dụng interface để định nghĩa những chức năng chung mà các lớp cần thực hiện.

**Generic**

Sử dụng generic cho lớp quản lý danh sách đối tượng:

```java
DanhSachDoiTuong<T>
```

**File I/O**

Dữ liệu được đọc và ghi xuống các file `.txt` thông qua lớp quản lý file.

---

## 📂 Cấu trúc project

```text
QLThiTracNghiem/
│
├── src/
│   ├── BaiLamThi.java
│   ├── CauHoi.java
│   ├── CauHoiMotDapAn.java
│   ├── CauHoiNhieuDapAn.java
│   ├── CauTraLoi.java
│   ├── DanhSachDoiTuong.java
│   ├── DapAn.java
│   ├── DeThi.java
│   ├── FileManager.java
│   ├── GiaoVien.java
│   ├── ICoTheCham.java
│   ├── IHienThi.java
│   ├── KetQuaThi.java
│   ├── Lop.java
│   ├── Main.java
│   ├── MonHoc.java
│   ├── NguoiDung.java
│   ├── QuanLyHeThong.java
│   ├── SinhVien.java
│   └── ThongKe.java
│
├── data/
│   └── *.txt
│
├── README.md
└── TaiLieuBaoVe.md
```

---

## 🛠️ Công nghệ sử dụng

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge\&logo=openjdk\&logoColor=white)

* **Java**
* **JDK**
* Console
* File `.txt`

---

## ▶️ Cách chạy

### 1. Clone repository

```bash
git clone <repository-url>
```

### 2. Mở project

Có thể mở project bằng các IDE như:

* IntelliJ IDEA
* Eclipse
* Visual Studio Code
* NetBeans

### 3. Chạy chương trình

Mở file:

```text
src/Main.java
```

và chạy hàm `main()`.

Nếu chạy bằng terminal:

```bash
javac -encoding UTF-8 -d bin src/*.java
java -cp bin Main
```

---

## 🖥️ Một chút về giao diện

Chương trình hiện tại sử dụng giao diện **Console**, các chức năng được lựa chọn thông qua menu.

Ví dụ:

```text
========================================
       QUAN LY THI TRAC NGHIEM
========================================

1. Quan ly sinh vien
2. Quan ly giao vien
3. Quan ly mon hoc
4. Quan ly cau hoi
5. Quan ly de thi
6. Lam bai thi
7. Thong ke
8. Luu du lieu
0. Thoat

Nhap lua chon:
```

---

## 💾 Dữ liệu

Các dữ liệu được lưu trong thư mục `data/`.

Một số dữ liệu có thể được lưu lại gồm:

```text
SinhVien
GiaoVien
MonHoc
CauHoi
DeThi
KetQuaThi
```

Việc tách dữ liệu ra khỏi source code giúp chương trình có thể đọc lại dữ liệu ở những lần chạy sau.

---

## 📌 Một số điểm chính của đồ án

* Có sử dụng **kế thừa và đa hình**
* Có **abstract class và interface**
* Có sử dụng **Generic**
* Có xử lý **đọc/ghi file**
* Có phân chia các lớp theo từng chức năng
* Có CRUD cho nhiều loại đối tượng
* Có phần làm bài và chấm điểm
* Có lưu kết quả thi

---

## 📄 Tài liệu

Trong repository có thể tham khảo thêm:

* `src/` – Source code của chương trình
* `data/` – Dữ liệu
* `TaiLieuBaoVe.md` – Tài liệu phục vụ bảo vệ đồ án
* `README.md` – Giới thiệu project

---

## 🎓 Thông tin đồ án

**Môn:** Lập trình hướng đối tượng
**Ngôn ngữ:** Java
**Năm:** 2026

---

<p align="center">
  <b>Đồ án được thực hiện bởi nhóm 3 sinh viên</b>
  <br>
  Phan Tiến Đạt · Nguyễn Hải Đăng · Nguyễn Khánh Duy
</p>
=======
# QLThiTracNghiem
>>>>>>> d8bd99a47d1bef95ecefc73a2a6c6d3286047c73
