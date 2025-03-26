/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.settings.friends;

import java.util.UUID;

public class Friend {
	private String username;
	private final UUID uuid;
	
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
	public boolean equals(Object other) {
		if(other instanceof Friend)
			return ((Friend)(other)).uuid.equals(uuid);
		return false;
	}
}
