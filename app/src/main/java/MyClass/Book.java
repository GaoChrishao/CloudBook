package MyClass;

import android.support.annotation.Keep;

import java.io.Serializable;
@Keep
public class Book implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String writer;
    private int likes;
    private String name;
    private String content;
    private int comments;
    private String cover;
    private float price;
    public int buynumber;

    //全
    public Book(String id, String writer, int likes, String name, String content, int comments, String cover,float price) {
        this.id = id;
        this.writer = writer;
        this.likes = likes;
        this.name = name;
        this.content = content;
        this.comments = comments;
        this.cover = cover;
        this.price=price;
    }

    //无价格
    public Book(String id, String writer, int likes, String name, String content, int comments, String cover) {
        this.id = id;
        this.writer = writer;
        this.likes = likes;
        this.name = name;
        this.content = content;
        this.comments = comments;
        this.cover = cover;
    }


    public Book(String id) {
        this.id = id;
    }


    public String getId() {
        return id;
    }

    public String getWriter() {
        return writer;
    }

    public int getLikes() {
        return likes;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public int getComments() {
        return comments;
    }

    public String getCover() {
        return cover;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
