package app;

public class Utilisateur {
	private String username;
	private String password;
	
	public Utilisateur(String username, String password) {
		this.setUsername(username);
		this.setPassword(password);
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true; // Same object reference
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false; // Not the same type or null
        }
        Utilisateur autre = (Utilisateur) obj;
        return this.getUsername().equals(autre.getUsername()) && this.getPassword().equals(autre.getPassword());
    }
}
