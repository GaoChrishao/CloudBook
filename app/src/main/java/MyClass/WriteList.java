package MyClass;

import android.support.annotation.Keep;

import java.io.Serializable;
import java.util.List;
@Keep
public class WriteList implements Serializable{
	private static final long serialVersionUID = 1L;
	public List<Write>writeList;
	
	public WriteList(List<Write>writeList) {
		this.writeList=writeList;
	}

}
