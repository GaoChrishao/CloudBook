package MyClass;

import java.io.Serializable;

public class SignUpPermission implements Serializable {
	private static final long serialVersionUID = 1L;

	// -1 Existing account
	// 0 Account or password cannot be empty
	// 1 can sign up

	public SignUpPermission(String n, int p) {
		permissionCode = p;
		name = n;
	}

	public int permissionCode;
	public String name;
}
