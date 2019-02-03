package MyClass;

import android.support.annotation.Keep;

import java.io.Serializable;
import java.util.List;
@Keep
public class HopebookList implements Serializable {
	private static final long serialVersionUID = 1L;
	public int sumbook;
	public List<HopebookList> hopebooksList;

	public HopebookList(List<HopebookList> list) {
		hopebooksList = list;
		sumbook = hopebooksList.size();
	}

}
