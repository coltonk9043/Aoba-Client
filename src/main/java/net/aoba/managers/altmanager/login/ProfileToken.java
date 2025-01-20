/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.managers.altmanager.login;

import com.google.gson.JsonObject;

/**
 * A class that represents a user and their respective name and id.
 */
public class ProfileToken {
	public final String id;
	public final String name;

	public ProfileToken(String id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * Returns an instance of an ProfileToken from JSON text.
	 * 
	 * @param root JSON text to parse.
	 * @return ProfileToken object with properties filled from JSON.
	 */
	public static ProfileToken fromJson(JsonObject root) {
		String id = root.get("id").getAsString();
		String name = root.get("name").getAsString();
		return new ProfileToken(id, name);
	}
}
