package net.aoba.altmanager.login;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class AuthToken {

	public final String accessToken;
	public final String refreshToken;
	
	public AuthToken(String accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}
	
	public static AuthToken fromJson(JsonObject json) {
		try {
			String accessToken = json.get("access_token").getAsString();
            String refreshToken = json.get("refresh_token").getAsString();
            return new AuthToken(accessToken, refreshToken);
        } catch (Throwable t) {
            throw new JsonParseException("Unable to parse Device Auth Code: " + json, t);
        }
	}
}
