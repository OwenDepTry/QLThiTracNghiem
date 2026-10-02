import java.util.ArrayList;
import java.util.List;

/**
 * Lớp BaiLamThi - một lượt làm bài của sinh viên cho một đề thi.
 */
public class BaiLamThi {
    private String maBaiLam;
    private String mssv;
    private String maDeThi;
    private List<CauTraLoi> cauTraLoiList;

    public BaiLamThi(String maBaiLam, String mssv, String maDeThi) {
        this.maBaiLam = maBaiLam;
        this.mssv = mssv;
        this.maDeThi = maDeThi;
        this.cauTraLoiList = new ArrayList<>();
    }

    public void themCauTraLoi(CauTraLoi c) { cauTraLoiList.add(c); }

    public String getMaBaiLam() { return maBaiLam; }
    public String getMssv() { return mssv; }
    public String getMaDeThi() { return maDeThi; }
    public List<CauTraLoi> getCauTraLoiList() { return cauTraLoiList; }
}
