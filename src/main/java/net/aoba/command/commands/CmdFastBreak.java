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
import net.aoba.module.modules.misc.FastBreak;

public class CmdFastBreak extends Command {

	public CmdFastBreak() {
		super("fastbreak", "Decreases the time it takes to break blocks", "[multiplier, toggle] [value]");
	}

	@Override
	public void runCommand(String[] parameters) throws InvalidSyntaxException {
		if (parameters.length != 2)
			throw new InvalidSyntaxException(this);

		FastBreak module = Aoba.getInstance().moduleManager.fastbreak;
		switch (parameters[0]) {
		case "multiplier":
			try {
				float multiplier = Float.parseFloat(parameters[1]);
				module.setMultiplier(multiplier);
				module.toggle();
				CommandManager.sendChatMessage("FastBreak multiplier set to " + multiplier + "x");
				module.toggle();
			} catch (Exception e) {
				CommandManager.sendChatMessage("Invalid value.");
			}
			break;
		case "toggle":
			String state = parameters[1].toLowerCase();
			if (state.equals("on")) {
				module.state.setValue(true);
				CommandManager.sendChatMessage("FastBreak toggled ON");
			} else if (state.equals("off")) {
				module.state.setValue(false);
				CommandManager.sendChatMessage("FastBreak toggled OFF");
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
		case "multiplier":
			return new String[] { "0.5", "1.0", "1.15", "1.25", "1.5", "2.0" };
		default:
			return new String[] { "toggle", "multiplier" };
		}
	}
}
