package MyClass;

import android.support.annotation.Keep;

import java.io.Serializable;
@Keep
public class ActivationMessage  implements Serializable {
	private static final long serialVersionUID = 1L;
	private String activationUserID;
	private String activationUserPassword;
	private String activationCode;
	private String activationBookID;
	private int activationCondition;

	public ActivationMessage(String activationUserID, String activationUserPassword, String activationCode) {
		this.setActivationUserID(activationUserID);
		this.setActivationUserPassword(activationUserPassword);
		this.setActivationCode(activationCode);
	}

	public String getActivationUserID() {
		return activationUserID;
	}

	public void setActivationUserID(String activationUserID) {
		this.activationUserID = activationUserID;
	}

	public String getActivationUserPassword() {
		return activationUserPassword;
	}

	public void setActivationUserPassword(String activationUserPassword) {
		this.activationUserPassword = activationUserPassword;
	}

	public String getActivationCode() {
		return activationCode;
	}

	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}

	public int getActivationCondition() {
		return activationCondition;
	}

	public void setActivationCondition(int activationCondition) {
		this.activationCondition = activationCondition;
	}

	public String getActivationBookID() {
		return activationBookID;
	}

	public void setActivationBookID(String activationBookID) {
		this.activationBookID = activationBookID;
	}

}
