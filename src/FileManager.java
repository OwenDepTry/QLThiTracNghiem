import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp FileManager - chỉ chứa các phương thức static để đọc/ghi dữ liệu dạng text (UTF-8).
 */
public class FileManager {

    public static void ghiFile(String duongDan, List<String> dongDuLieu) {
        try {
            Path p = Paths.get(duongDan);
            if (p.getParent() != null) Files.createDirectories(p.getParent());
            Files.write(p, dongDuLieu, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Lỗi ghi file " + duongDan + ": " + e.getMessage());
        }
    }

    public static List<String> docFile(String duongDan) {
        List<String> ketQua = new ArrayList<>();
        Path p = Paths.get(duongDan);
        if (!Files.exists(p)) return ketQua;
        try {
            ketQua = Files.readAllLines(p, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Lỗi đọc file " + duongDan + ": " + e.getMessage());
        }
        return ketQua;
    }
}
