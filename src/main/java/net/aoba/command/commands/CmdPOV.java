/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.command.commands;

import java.util.List;

import net.aoba.Aoba;
import net.aoba.command.Command;
import net.aoba.managers.CommandManager;
import net.aoba.command.InvalidSyntaxException;
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

		POV module = Aoba.getInstance().moduleManager.pov;

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