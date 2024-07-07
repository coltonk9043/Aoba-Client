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
 * A class to represent an Xbox Live Token.
 */
package net.aoba.altmanager;

public class XboxLiveToken {
    private final String token;
    private final String hash;

    /**
     * Constructor for an Xbox Live Token
     *
     * @param token
     * @param uhs
     */
    public XboxLiveToken(String token, String uhs) {
        this.token = token;
        this.hash = uhs;
    }

    /**
     * Returns the token of the Xbox Live Token.
     *
     * @return The token of the Xbox Live Token.
     */
    public String getToken() {
        return token;
    }

    /**
     * Returns the hash of the Xbox Live Token.
     *
     * @return The hash of the Xbox Live Token.
     */
    public String getHash() {
        return hash;
    }
}
