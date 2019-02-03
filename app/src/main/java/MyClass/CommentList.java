package MyClass;

import android.support.annotation.Keep;

import java.io.Serializable;
import java.util.List;
@Keep
public class CommentList implements Serializable {
	private static final long serialVersionUID = 1L;
	public int sum;
	public List<CommentMessage> commentsList;
	
	public CommentList(List<CommentMessage> list) {	
		commentsList=list;
		sum=commentsList.size();
	}

}
