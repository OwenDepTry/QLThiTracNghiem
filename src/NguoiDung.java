/**
 * Lớp trừu tượng NguoiDung - lớp cha của SinhVien, GiaoVien.
 * Thể hiện: abstract class, abstract method, thuộc tính static, kế thừa, đa hình.
 */
public abstract class NguoiDung implements IHienThi {

    // Thuộc tính static: đếm tổng số người dùng đã tạo trong hệ thống
    protected static int soLuongNguoiDung = 0;

    protected String maNguoiDung;
    protected String hoTen;
    protected String ngaySinh;
    protected String diaChi;
    protected String soDienThoai;

    // Hàm thiết lập (constructor)
    public NguoiDung(String maNguoiDung, String hoTen, String ngaySinh, String diaChi, String soDienThoai) {
        this.maNguoiDung = maNguoiDung;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.diaChi = diaChi;
        this.soDienThoai = soDienThoai;
        soLuongNguoiDung++;
    }

    // Phương thức trừu tượng: mỗi loại người dùng trả lời khác nhau -> đa hình
    public abstract String getVaiTro();

    // Phương thức static
    public static int getSoLuongNguoiDung() {
        return soLuongNguoiDung;
    }

    public String getMaNguoiDung() { return maNguoiDung; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(String ngaySinh) { this.ngaySinh = ngaySinh; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    // Cài đặt sẵn ở lớp cha, các lớp con có thể override thêm (đa hình)
    @Override
    public void hienThi() {
        System.out.printf("[%-9s] Mã: %-6s Họ tên: %-22s Ngày sinh: %-11s SĐT: %-11s Địa chỉ: %s%n",
                getVaiTro(), maNguoiDung, hoTen, ngaySinh, soDienThoai, diaChi);
    }
}
