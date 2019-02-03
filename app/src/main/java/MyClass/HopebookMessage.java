package MyClass;

import android.support.annotation.Keep;

import java.io.Serializable;
@Keep
public class HopebookMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	private String ID;
	private String userID;
	private String bookID;
	private int choose;

	public HopebookMessage(String userID, String bookID) {
		this.userID = userID;
		this.setBookID(bookID);
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getBookID() {
		return bookID;
	}

	public void setBookID(String bookID) {
		this.bookID = bookID;
	}

	public int getChoose() {
		return choose;
	}

	public void setChoose(int choose) {
		this.choose = choose;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

}
