/**
 * Lớp DapAn - một phương án trả lời của câu hỏi trắc nghiệm.
 */
public class DapAn {
    private String noiDung;
    private boolean dapAnDung;

    public DapAn(String noiDung, boolean dapAnDung) {
        this.noiDung = noiDung;
        this.dapAnDung = dapAnDung;
    }

    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }
    public boolean isDapAnDung() { return dapAnDung; }
    public void setDapAnDung(boolean dapAnDung) { this.dapAnDung = dapAnDung; }

    @Override
    public String toString() {
        return noiDung + (dapAnDung ? "  [Đáp án đúng]" : "");
    }
}
