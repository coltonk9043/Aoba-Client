package net.aoba.altmanager.login;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * A Token that represents an Auth Token for Minecraft.
 */
public class MCAuthToken {
	public final String accessToken;

	public MCAuthToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * Returns an instance of an AuthToken from JSON text.
	 * 
	 * @param json JSON text to parse.
	 * @return AuthToken object with properties filled from JSON.
	 */
	public static MCAuthToken fromJson(JsonObject json) {
		try {
			String accessToken = json.get("access_token").getAsString();
			return new MCAuthToken(accessToken);
		} catch (Throwable t) {
			throw new JsonParseException("Unable to parse Device Auth Code: " + json, t);
		}
	}
}
