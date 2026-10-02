import java.util.List;

/**
 * Lớp CauTraLoi - lưu các lựa chọn (chỉ số đáp án) của sinh viên cho một câu hỏi.
 */
public class CauTraLoi {
    private String maCauHoi;
    private List<Integer> luaChon;

    public CauTraLoi(String maCauHoi, List<Integer> luaChon) {
        this.maCauHoi = maCauHoi;
        this.luaChon = luaChon;
    }

    public String getMaCauHoi() { return maCauHoi; }
    public List<Integer> getLuaChon() { return luaChon; }
}
