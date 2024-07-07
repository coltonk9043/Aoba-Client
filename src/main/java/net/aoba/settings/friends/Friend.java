package net.aoba.settings.friends;

import java.util.UUID;

public class Friend {
	private String username;
	private UUID uuid;
	
	public Friend(String username, UUID uuid) {
		this.username = username;
		this.uuid = uuid;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	@Override
    public int hashCode() {
		return uuid.hashCode();
	}
	
	@Override
	public boolean equals(final Object other) {
		if(other instanceof Friend)
			return ((Friend)(other)).uuid.equals(this.uuid);
		return false;
	}
}
