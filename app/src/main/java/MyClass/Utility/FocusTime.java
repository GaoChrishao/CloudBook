package MyClass.Utility;

import java.io.Serializable;
import java.sql.Date;

public class FocusTime implements Serializable {
    private static final long serialVersionUID=1L;
    private int hour;
    private int minute;
    private int second;
    public long start_time;
    public long end_time;
    public Date date;
    private String bookId;
    private String bookName;

    public FocusTime(int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }



    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }
}
