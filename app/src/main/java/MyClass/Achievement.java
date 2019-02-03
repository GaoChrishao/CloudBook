package MyClass;

import android.support.annotation.Keep;

import java.io.Serializable;
@Keep
public class Achievement implements Serializable {
    private static final long serialVersionUID = 1L;
    private String content;
    private String pic_path;

    public Achievement(String content, String pic_path) {
        this.content = content;
        this.pic_path = pic_path;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPic_path() {
        return pic_path;
    }

    public void setPic_path(String pic_path) {
        this.pic_path = pic_path;
    }
}
