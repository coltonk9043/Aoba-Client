/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.managers.altmanager;

import net.aoba.managers.altmanager.login.AuthToken;
import net.aoba.managers.altmanager.login.MicrosoftAuth;

public class Alt {
	private String email;
	private String username;
	private boolean isCracked = false;
	private AuthToken authToken;

	/**
	 * Constructor for an Alt given it's email, password, and whether it is a
	 * Microsoft account.
	 * 
	 * @param email      Email used to log in to the account.
	 * @param isCracked  Is the account cracked.
	 */
	public Alt(String email, boolean isCracked) {
		this.email = email;
		this.isCracked = isCracked;
	}

	/**
	 * Constructor for an Alt given it's email, password, username, and whether it
	 * is a Microsoft account.
	 * 
	 * @param email     Email used to log in to the account.
	 * @param username  Username that the account currently has.
	 * @param isCracked  Is the account cracked.
	 */
	public Alt(String email, String username, boolean isCracked) {
		this.email = email;
		this.username = username;
		this.isCracked = isCracked;
	}

	/**
	 * Sets the username of the Alt account.
	 * 
	 * @param username The username of the Alt account.
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Sets the email of the Alt account.
	 * 
	 * @param email The email of the Alt account.
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Gets the username of the Alt account.
	 * 
	 * @return The username of the Alt account.
	 */
	public String getUsername() {
		if (username == null) {
			return "";
		}
		return username;
	}

	/**
	 * Gets the email of the Alt account.
	 * 
	 * @return The email of the Alt account.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Gets the Auth Token associated with the account.
	 * 
	 * @return Auth token of the Alt account.
	 */
	public AuthToken getAuthToken() {
		return authToken;
	}

	/**
	 * Sets the Auth Token associated with the alt.
	 * 
	 * @param authToken Auth to set alt's token to.
	 */
	public void setAuthToken(AuthToken authToken) {
		this.authToken = authToken;
	}

	/**
	 * Gets whether the Alt account is cracked.
	 * 
	 * @return Whether the Alt account is cracked.
	 */
	public boolean isCracked() {
		return isCracked;
	}

	/**
	 * Attempts to authenticate the alt and set the alt Auth Token.
	 */
	public void auth() {
		MicrosoftAuth.requestAuthToken((authToken) -> {
			this.authToken = authToken;
		});
	}
}
