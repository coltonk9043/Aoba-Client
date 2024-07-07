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

public class Alt {
    private String email;
    private String username;
    private String password;
    private boolean microsoft = false;
    private boolean isCracked = false;

    /**
     * Constructor for an Alt given it's email, password, and whether it is a Microsoft account.
     *
     * @param email     Email used to log in to the account.
     * @param password  Password used to log in to the account.
     * @param microsoft Whether or not the account is a Microsoft or Mojang Account.
     */
    public Alt(String email, String password, boolean microsoft) {
        this.email = email;
        this.password = password;
        this.microsoft = microsoft;
        // If no password is entered, assume the account is a cracked account.
        if (this.password.isEmpty()) {
            this.isCracked = true;
        }
    }

    /**
     * Constructor for an Alt given it's email, password, username, and whether it is a Microsoft account.
     *
     * @param email     Email used to log in to the account.
     * @param password  Password used to log in to the account.
     * @param username  Username that the account currently has.
     * @param microsoft Whether or not the account is a Microsoft or Mojang Account.
     */
    public Alt(String email, String password, String username, boolean microsoft) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.microsoft = microsoft;
        // If no password is entered, assume the account is a cracked account.
        if (this.password.isEmpty()) {
            this.isCracked = true;
        }
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
     * Sets the password of the Alt account.
     *
     * @param password The password of the Alt account.
     */
    public void setPassword(String password) {
        this.password = password;
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

    /**
     * Gets the password of the Alt account.
     *
     * @return The password of the Alt account.
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Gets whether the Alt account is cracked.
     *
     * @return Whether the Alt account is cracked.
     */
    public boolean isCracked() {
        return this.isCracked;
    }

    /**
     * Gets whether the Alt account is a Microsoft account.
     *
     * @return Whether the Alt account is a Microsoft account.
     */
    public boolean isMicrosoft() {
        return this.microsoft;
    }
}
