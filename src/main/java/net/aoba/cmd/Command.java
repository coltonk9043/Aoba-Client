/*
* Aoba Hacked Client
* Copyright (C) 2019-2023 coltonk9043
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
 * A class to represent a Command
 */
package net.aoba.cmd;

import net.aoba.AobaClient;
import net.minecraft.client.MinecraftClient;

public abstract class Command {
	protected String description;
	protected MinecraftClient mc = AobaClient.MC;
	
	/**
	 * Runs the intended action of the command.
	 * @param parameters The parameters being passed.
	 */
	public abstract void command(String[] parameters);
	
	/**
	 * Gets the description of the command.
	 * @return The description of the command.
	 */
	public String getDescription() {
		return this.description;
	}
}
