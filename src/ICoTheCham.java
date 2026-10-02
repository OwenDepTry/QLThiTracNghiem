import java.util.List;

/**
 * Interface ICoTheCham
 * Các loại câu hỏi khác nhau sẽ có cách chấm điểm khác nhau (đa hình).
 */
public interface ICoTheCham {
    /**
     * Chấm điểm dựa trên lựa chọn (chỉ số đáp án) của sinh viên.
     * @param luaChonCuaSV danh sách chỉ số đáp án sinh viên đã chọn
     * @return điểm số trong khoảng [0.0 , 1.0] cho câu hỏi này
     */
    double chamDiem(List<Integer> luaChonCuaSV);
}
