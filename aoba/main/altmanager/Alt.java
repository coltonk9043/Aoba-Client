package aoba.main.altmanager;

public class Alt {
	private String username;
	private String password;
	private boolean isCracked = false;
	
	public Alt(String username, String password) {
		this.username = username;
		this.password = password;
		if(this.password.isEmpty()) {
			this.isCracked = true;
		}
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}
	
	public boolean isCracked() {
		return this.isCracked;
	}
}
