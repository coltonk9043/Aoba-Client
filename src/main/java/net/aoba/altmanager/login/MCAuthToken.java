package net.aoba.altmanager.login;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class MCAuthToken {
	public final String accessToken;
	
	public MCAuthToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	public static MCAuthToken fromJson(JsonObject json) {
		try {
			String accessToken = json.get("access_token").getAsString();
            return new MCAuthToken(accessToken);
        } catch (Throwable t) {
            throw new JsonParseException("Unable to parse Device Auth Code: " + json, t);
        }
	}
}
