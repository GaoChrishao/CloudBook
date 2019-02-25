package MyClass;

import java.io.Serializable;
import java.util.List;

public class HopebookList implements Serializable {
	private static final long serialVersionUID = 1L;
	public int sumbook;
	public List<HopebookList> hopebooksList;

	public HopebookList(List<HopebookList> list) {
		hopebooksList = list;
		sumbook = hopebooksList.size();
	}

}
