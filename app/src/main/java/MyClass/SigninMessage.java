package MyClass;

import android.support.annotation.Keep;

import java.io.Serializable;
@Keep
public class SigninMessage implements Serializable {
	private static final long serialVersionUID=1L;
	private String account;
	private String password;
	public SigninMessage(String a,String p) {
		account=a;
		password=p;
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
	

}
