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

package net.aoba.altmanager.exceptions;

import java.io.Serial;

public class InvalidResponseException extends Exception {
    @Serial
    private static final long serialVersionUID = -4593254916052579608L;
    private final String response;

    /**
     * Constructor for InvalidResponseException
     *
     * @param response The response from the server.
     */
    public InvalidResponseException(final String response) {
        super(response);
        this.response = response;
    }

    /**
     * Returns the Response held in this exception.
     *
     * @return Response
     */
    public String getResponse() {
        return this.response;
    }
}
