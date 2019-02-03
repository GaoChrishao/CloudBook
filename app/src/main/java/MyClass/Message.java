package MyClass;

import android.support.annotation.Keep;

import java.io.Serializable;
@Keep
public class Message implements Serializable {
    private static final long serialVersionUID=1L;
    private int id;
    private String sender;
    private String content;

    public Message(int id, String sender, String content) {
        this.id = id;
        this.sender = sender;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
