package net.aoba.altmanager.login;

import com.google.gson.JsonObject;

public class ProfileToken {
	public final String id;
	public final String name;
	
	public ProfileToken(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public static ProfileToken fromJson(JsonObject root) {
		String id = root.get("id").getAsString();
		String name = root.get("name").getAsString();
		return new ProfileToken(id, name);
	}
}
