package MyClass;

public class User {
    private String id;
    private String username;
    private String password;
    private int exp;
    private int readtime;
    private int achieve;
    private int comments_num;
    private int books;

    public User() {
        this("0001", "", "", 0, 0, 0, 0, 0);
    }

    public User(String id, String username, String password, int exp, int readtime, int achieve, int comments_num, int books) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.exp = exp;
        this.readtime = readtime;
        this.achieve = achieve;
        this.comments_num = comments_num;
        this.books = books;
    }

    public User(String id, String username, String password) {
        this(id, username, password, 0, 0, 0, 0, 0);
    }

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

    public void setComments_num(int comments_sum) {
        this.comments_num = comments_sum;
    }

    public int getBooks() {
        return books;
    }

    public void setBooks(int books) {
        this.books = books;
    }
}
