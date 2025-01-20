/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.managers.altmanager.login;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * An Auth Token from Microsoft that contains an Access Token and Refresh Token.
 */
public class AuthToken {

	public final String accessToken;
	public final String refreshToken;

	public AuthToken(String accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}

	/**
	 * Returns an instance of an AuthToken from JSON text.
	 * 
	 * @param json JSON text to parse.
	 * @return AuthToken object with properties filled from JSON.
	 */
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
