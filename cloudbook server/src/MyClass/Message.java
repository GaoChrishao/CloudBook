package MyClass;

import java.io.Serializable;

public class Message implements Serializable {   //LikeComment
    private static final long serialVersionUID=1L;
    private int id;  //userid
    private String sender;		//pwd
    private String content;		//comment_id  
    

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
