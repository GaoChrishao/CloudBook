package MyClass;

import java.io.Serializable;
import java.util.List;

public class CommentList implements Serializable {
	private static final long serialVersionUID = 1L;
	public int sum;
	public List<CommentMessage> commentsList;
	
	public CommentList(List<CommentMessage> list) {	
		commentsList=list;
		sum=commentsList.size();
	}

}
