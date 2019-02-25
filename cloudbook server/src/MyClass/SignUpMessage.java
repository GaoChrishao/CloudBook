package MyClass;

import java.io.Serializable;

public class SignUpMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	private String account;
	private String password;
	private String repassword;// �ٴ����������

	public SignUpMessage(String a, String p, String r) {
		account = a;
		password = p;
		repassword = r;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRepassword() {
		return repassword;
	}

	public void setRepassword(String repassword) {
		this.repassword = repassword;
	}

}
