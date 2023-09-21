import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GetTime {

    public String dateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
        String time = now.format(timeFormatter);
        System.out.println("當前爬蟲時間:"+ time);
        return time;
    }
}
