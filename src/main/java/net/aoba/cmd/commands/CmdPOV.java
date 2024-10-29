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

package net.aoba.cmd.commands;

import java.util.List;

import net.aoba.Aoba;
import net.aoba.cmd.Command;
import net.aoba.cmd.CommandManager;
import net.aoba.cmd.InvalidSyntaxException;
import net.aoba.module.modules.render.POV;
import net.minecraft.client.network.AbstractClientPlayerEntity;

public class CmdPOV extends Command {

	public CmdPOV() {
		super("pov", "Allows the player to see through someone else's POV.", "[set, toggle] [value]");
	}

	@Override
	public void runCommand(String[] parameters) throws InvalidSyntaxException {
		if (parameters.length != 2)
			throw new InvalidSyntaxException(this);

		POV module = (POV) Aoba.getInstance().moduleManager.pov;

		switch (parameters[0]) {
		case "set":
			try {
				String player = parameters[1];
				CommandManager.sendChatMessage("Setting POV Player Name to " + player);
				module.setEntityPOV(player);
			} catch (Exception e) {
				CommandManager.sendChatMessage("Invalid value.");
			}
			break;
		case "toggle":
			String state = parameters[1].toLowerCase();
			if (state.equals("on")) {
				module.state.setValue(true);
				CommandManager.sendChatMessage("POV toggled ON");
			} else if (state.equals("off")) {
				module.state.setValue(false);
				CommandManager.sendChatMessage("POV toggled OFF");
			} else {
				CommandManager.sendChatMessage("Invalid value. [ON/OFF]");
			}
			break;
		default:
			throw new InvalidSyntaxException(this);
		}
	}

	@Override
	public String[] getAutocorrect(String previousParameter) {
		switch (previousParameter) {
		case "toggle":
			return new String[] { "on", "off" };
		case "set":
			List<AbstractClientPlayerEntity> players = mc.world.getPlayers();
			int numPlayers = players.size();
			String[] suggestions = new String[numPlayers];

			int i = 0;
			for (AbstractClientPlayerEntity x : players)
				suggestions[i++] = x.getName().getString();

			return suggestions;
		default:
			return new String[] { "toggle", "set" };
		}
	}
}