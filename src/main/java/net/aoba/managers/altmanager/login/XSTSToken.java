/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.managers.altmanager.login;

import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * Class representing a 'Xbox Services Security Token'
 */
public class XSTSToken {
	public final String token;
	public final DisplayClaims displayClaims;

	public XSTSToken(String token, DisplayClaims displayClaims) {
		this.token = token;
		this.displayClaims = displayClaims;
	}

	/**
	 * Returns an instance of an XSTSToken from JSON text.
	 * 
	 * @param json JSON text to parse.
	 * @return XSTSToken object with properties filled from JSON.
	 */
	public static XSTSToken fromJson(JsonObject json) {
		try {
			String token = json.get("Token").getAsString();
			Map<String, JsonElement> displayClaimsObject = json.getAsJsonObject("DisplayClaims").asMap();
			JsonArray xui = displayClaimsObject.get("xui").getAsJsonArray();
			int count = xui.size();
			DisplayClaims claims = new DisplayClaims(new Claim[count]);
			for (int i = 0; i < count; i++) {
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
