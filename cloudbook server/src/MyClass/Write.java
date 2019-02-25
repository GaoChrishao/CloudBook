package MyClass;

import java.io.Serializable;
import java.sql.Date;

public class Write implements Serializable {
    private static final long serialVersionUID = 1L;
    public String id;
    public String user_id;
    public String book_id;
    public String book_name;
    public Date date;
    public String content;

    public String getId() {
        if(id!=null) return id;
        else return "0";
    }

    public String getUser_id() {
        if(user_id!=null) return user_id;
        else return "0";
    }

    public String getBook_id() {
        if(book_id!=null) return book_id;
        else return "0";
    }

    public String getBook_name() {
        return book_name;
    }

    public Date getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public Write(String book_id, String book_name, Date date, String content) {
        this.book_id = book_id;
        this.book_name = book_name;
        this.date = date;
        this.content = content;
    }


}
