package net.aoba.managers.altmanager.login;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * A token that shows whether the user has Minecraft or not.
 */
public class EntitlementToken {
	private boolean hasProduct = false;
	private boolean hasGame = false;

	public EntitlementToken() {
	}

	/**
	 * Returns whether the Entitlement Token states that the user has access to the
	 * game.
	 * 
	 * @return Whether the user has access to the game.
	 */
	public boolean hasGame() {
		return hasProduct && hasGame;
	}

	/**
	 * Returns an instance of an AuthToken from JSON text.
	 * 
	 * @param object JSON text to parse.
	 * @return AuthToken object with properties filled from JSON.
	 */
	public static EntitlementToken fromJson(JsonObject object) {
		EntitlementToken token = new EntitlementToken();
		JsonArray items = object.getAsJsonArray("items");
		for (JsonElement item : items) {
			JsonObject itemObj = item.getAsJsonObject();
			JsonPrimitive name = itemObj.getAsJsonPrimitive("name");
			String nameStr = name.getAsString();

			if (!token.hasProduct && nameStr.equals("product_minecraft"))
				token.hasProduct = true;

			if (!token.hasGame && nameStr.equals("game_minecraft"))
				token.hasGame = true;

			if (token.hasProduct && token.hasGame)
				break;
		}

		return token;
	}
}
