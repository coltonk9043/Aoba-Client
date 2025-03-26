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
import net.aoba.module.modules.combat.Reach;

public class CmdReach extends Command {

	public CmdReach() {
		super("reach", "Allows the player to reach further.", "[toggle/distance] [value]");
	}

	@Override
	public void runCommand(String[] parameters) throws InvalidSyntaxException {
		if (parameters.length != 2)
			throw new InvalidSyntaxException(this);

		Reach module = Aoba.getInstance().moduleManager.reach;

		switch (parameters[0]) {
		case "distance":
			try {
				float distance = Float.parseFloat(parameters[1]);
				module.setReachLength(distance);
				CommandManager.sendChatMessage("Reach distance set to " + distance);

			} catch (Exception e) {
				CommandManager.sendChatMessage("Invalid value.");
			}
			break;
		case "toggle":
			String state = parameters[1].toLowerCase();
			if (state.equals("on")) {
				module.state.setValue(true);
				CommandManager.sendChatMessage("Reach toggled ON");
			} else if (state.equals("off")) {
				module.state.setValue(false);
				CommandManager.sendChatMessage("Reach toggled OFF");
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
		case "distance:":
			return new String[] { "1.0", "2.0", "3.0", "4.0", "5.0", "6.0", "7.0", "8.0" };
		default:
			return new String[] { "toggle" };
		}
	}
}