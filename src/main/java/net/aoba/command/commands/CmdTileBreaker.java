/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.command.commands;

import net.aoba.Aoba;
import net.aoba.command.Command;
import net.aoba.managers.CommandManager;
import net.aoba.command.InvalidSyntaxException;
import net.aoba.module.modules.world.TileBreaker;

public class CmdTileBreaker extends Command {

	public CmdTileBreaker() {
		super("tilebreaker", "Breaks insta-break blocks within a certain radius", "[radius, toggle] [value]");
	}

	@Override
	public void runCommand(String[] parameters) throws InvalidSyntaxException {
		if (parameters.length != 2)
			throw new InvalidSyntaxException(this);

		TileBreaker module = Aoba.getInstance().moduleManager.tilebreaker;

		switch (parameters[0]) {
		case "radius":
			try {
				int radius = Integer.parseInt(parameters[1]);
				if (radius > 10) {
					radius = 10;
				} else if (radius < 1) {
					radius = 1;
				}
				module.setRadius(radius);
				CommandManager.sendChatMessage("TileBreaker radius set to " + radius);

			} catch (Exception e) {
				CommandManager.sendChatMessage("Invalid value. [1-10]");
			}
			break;
		case "toggle":
			String state = parameters[1].toLowerCase();
			if (state.equals("on")) {
				module.state.setValue(true);
				CommandManager.sendChatMessage("TileBreaker toggled ON");
			} else if (state.equals("off")) {
				module.state.setValue(false);
				CommandManager.sendChatMessage("TileBreaker toggled OFF");
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
		case "radius":
			return new String[] { "1.0", "2.0", "5.0", "10.0" };
		default:
			return new String[] { "toggle", "radius" };
		}
	}
}
