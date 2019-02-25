package MyClass;

import java.io.Serializable;
import java.sql.Date;

public class UserMessage  implements Serializable {
	 private static final long serialVersionUID = 1L;
    private String id;
    private String username;
    private String password;
    private int exp;
    private int readtime;
    private int achieve;
    private int comments_num;
    private int books;
    public int type;
    public String oldpassword;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getReadtime() {
        return readtime;
    }

    public void setReadtime(int readtime) {
        this.readtime = readtime;
    }

    public int getAchieve() {
        return achieve;
    }

    public void setAchieve(int achieve) {
        this.achieve = achieve;
    }

    public int getComments_num() {
        return comments_num;
    }

    public void setComments_num(int comments_num) {
        this.comments_num = comments_num;
    }

    public int getBooks() {
        return books;
    }

    public void setBooks(int books) {
        this.books = books;
    }

    public UserMessage(String id, String username, String password) {
        this(id, username, password, 0, 0, 0, 0, 0);
    }

    public UserMessage(String id, String username, String password, int exp, int readtime, int achieve, int comments_num, int books) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.exp = exp;
        this.readtime = readtime;
        this.achieve = achieve;
        this.comments_num = comments_num;
        this.books = books;
        type=2;
    }
    public UserMessage(String id) {
        this.id = id;
        type=2;
    }
}
