package net.aoba.altmanager.login;

import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class XSTSToken {
	public final String token;
	public final DisplayClaims displayClaims;
	
	public XSTSToken(String token, DisplayClaims displayClaims) {
		this.token = token;
		this.displayClaims = displayClaims;
	}
	
	public static XSTSToken fromJson(JsonObject json) {
		try {
			String token = json.get("Token").getAsString();
			Map<String, JsonElement> displayClaimsObject = json.getAsJsonObject("DisplayClaims").asMap();
			JsonArray xui = displayClaimsObject.get("xui").getAsJsonArray();
			int count = xui.size();
			DisplayClaims claims = new DisplayClaims(new Claim[count]);
			for(int i = 0; i < count; i++) {
				JsonObject element = xui.get(i).getAsJsonObject();
				Map<String, JsonElement> xuiMap = element.asMap();
				claims.xui[i] = new Claim(xuiMap.get("uhs").getAsString());
			}
				
            return new XSTSToken(token, claims);
        } catch (Throwable t) {
            throw new JsonParseException("Unable to parse Device Auth Code: " + json, t);
        }
	}
}

