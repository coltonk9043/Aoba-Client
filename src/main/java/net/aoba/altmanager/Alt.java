/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * A class to represent an Alt Account and all of it's information.
 */
package net.aoba.altmanager;

import net.aoba.altmanager.login.AuthToken;
import net.aoba.altmanager.login.MicrosoftAuth;

public class Alt {
    private String email;
    private String username;
    
    private boolean isCracked = false;
    private AuthToken authToken;
    
    /**
     * Constructor for an Alt given it's email, password, and whether it is a Microsoft account.
     *
     * @param email     Email used to log in to the account.
     * @param password  Password used to log in to the account.
     * @param microsoft Whether or not the account is a Microsoft or Mojang Account.
     */
    public Alt(String email, boolean isCracked) {
        this.email = email;
        this.isCracked = isCracked;
    }

    /**
     * Constructor for an Alt given it's email, password, username, and whether it is a Microsoft account.
     *
     * @param email     Email used to log in to the account.
     * @param password  Password used to log in to the account.
     * @param username  Username that the account currently has.
     * @param microsoft Whether or not the account is a Microsoft or Mojang Account.
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
        if (this.username == null) {
            return "";
        }
        return this.username;
    }

    /**
     * Gets the email of the Alt account.
     *
     * @return The email of the Alt account.
     */
    public String getEmail() {
        return this.email;
    }

    public AuthToken getAuthToken() {
    	return authToken;
    }
    
    public void setAuthToken(AuthToken authToken) {
    	this.authToken = authToken;
    }
    
    /**
     * Gets whether the Alt account is cracked.
     *
     * @return Whether the Alt account is cracked.
     */
    public boolean isCracked() {
        return this.isCracked;
    }
    
    public void auth() {
        MicrosoftAuth.requestAuthToken((authToken) -> {
        	this.authToken = authToken;
        });
    }
}
