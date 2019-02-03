package MyClass;

import android.support.annotation.Keep;

import java.io.Serializable;
import java.sql.Date;
@Keep
public class BookHas extends Book implements Serializable{
    private int allPages;
    private int readPages;
    private Date lastRead;

    public BookHas(String id) {
        super(id);
    }

    public int getAllPages() {
        return allPages;
    }

    public void setAllPages(int allPages) {
        this.allPages = allPages;
    }

    public int getReadPages() {
        return readPages;
    }

    public void setReadPages(int readPages) {
        this.readPages = readPages;
    }

    public Date getLastRead() {
        return lastRead;
    }

    public void setLastRead(Date lastRead) {
        this.lastRead = lastRead;
    }
}
