package MyClass;

import android.support.annotation.Keep;

import java.io.Serializable;
import java.sql.Date;
@Keep
public class Focus implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String userId;
    private String bookId;
    private String bookName;
    private Date date;
    private Date start_time;
    private Date end_time;

    public Focus(String userId, Date date, Date start_time, Date end_time) {
        this.userId = userId;
        this.date = date;
        this.start_time = start_time;
        this.end_time = end_time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getStart_time() {
        return start_time;
    }

    public void setStart_time(Date start_time) {
        this.start_time = start_time;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
    }
}
