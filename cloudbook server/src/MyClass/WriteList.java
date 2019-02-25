package MyClass;

import java.io.Serializable;
import java.util.List;

public class WriteList implements Serializable{
	private static final long serialVersionUID = 1L;
	public List<Write>writeList;
	public WriteList(List<Write>writeList) {
		this.writeList=writeList;
	}

}
