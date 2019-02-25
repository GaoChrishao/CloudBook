package MyClass;

import java.io.Serializable;
import java.sql.Date;

public class CommentMessage implements Serializable {
    private static final long serialVersionUID=1L;
    private String account;
    private String book_id;
    private String comment_id;
    private String comment;
    private String password;
    private Date date;
    public CommentMessage(String a,String c,String p,String bookid) {
        account=a;
        comment=c;
        password = p;
        book_id=bookid;
    }
    public CommentMessage(String a,String c,String p,String bookid,String commentId,Date date) {
        account=a;
        comment=c;
        password = p;
        book_id=bookid;
        comment_id=commentId;
        this.date= date;
    }
    public CommentMessage(String a,String c,String bookid,String commentId,Date date) {
        account=a;
        comment=c;
        book_id=bookid;
        comment_id=commentId;
        this.date= date;
    }
    public String getAccount() {
        return account;
    }
    public void setAccount(String account) {
        this.account = account;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
	public String getBook_id() {
		return book_id;
	}
	public void setBook_id(String book_id) {
		this.book_id = book_id;
	}
	public String getComment_id() {
		return comment_id;
	}
	public void setComment_id(String comment_id) {
		this.comment_id = comment_id;
	}
    
}
