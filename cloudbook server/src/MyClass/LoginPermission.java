package MyClass;

import java.io.Serializable;

public class LoginPermission implements Serializable{
	private static final long serialVersionUID=1L;
	public UserMessage userInfor;
	
	/**
	 * 1 = can login
	 * -1 = wrong account or pwd
	 * 0 = no account exist
	 */
	public LoginPermission(int p,String n) {
		permissionCode = p;
		name=n;
	}
	public int permissionCode;
	public String name;
}
