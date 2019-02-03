package MyClass;

import android.support.annotation.Keep;

import java.io.Serializable;
@Keep
public class CommentPermission implements Serializable {
    private static final long serialVersionUID=1L;
    public CommentPermission(int p) {
        permissionCode = p;
    }
    public int permissionCode;
}
