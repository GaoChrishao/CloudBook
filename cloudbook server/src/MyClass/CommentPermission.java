package MyClass;

import java.io.Serializable;

public class CommentPermission implements Serializable {
    private static final long serialVersionUID=1L;
    public CommentPermission(int p) {
        permissionCode = p;
    }
    public int permissionCode;
    //public String contentId;
}
