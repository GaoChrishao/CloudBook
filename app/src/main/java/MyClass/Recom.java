package MyClass;

import android.support.annotation.Keep;

import java.io.Serializable;
import java.sql.Date;
@Keep
public class Recom implements Serializable {
    private static final long serialVersionUID = 1L;
    public String id;
    public String book_id;
    public String book_name;
    public String content;

    public String getId() {
        if(id!=null) return id;
        else return "0";
    }


    public Recom(String id, String book_id, String book_name, String content) {
        this.id = id;
        this.book_id = book_id;
        this.book_name = book_name;
        this.content = content;
    }
}
